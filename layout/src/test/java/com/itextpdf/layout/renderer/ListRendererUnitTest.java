/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.layout.renderer;

import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.List;
import com.itextpdf.layout.element.ListItem;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.properties.ListSymbolPosition;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Tag("UnitTest")
public class ListRendererUnitTest extends RendererUnitTest {

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.GET_NEXT_RENDERER_SHOULD_BE_OVERRIDDEN)
    })
    public void getNextRendererShouldBeOverriddenTest() {
        ListRenderer listRenderer = new ListRenderer(new List()) {
            // Nothing is overridden
        };

        Assertions.assertEquals(ListRenderer.class, listRenderer.getNextRenderer().getClass());
    }

    @Test
    public void symbolPositioningInsideDrawnOnceTest() {
        InvocationsCounter invocationsCounter = new InvocationsCounter();

        List modelElement = new List();
        modelElement.setNextRenderer(new ListRendererCreatingNotifyingListSymbols(modelElement, invocationsCounter));
        modelElement.add((ListItem) new ListItem().add(new Paragraph("ListItem1")).add(new Paragraph("ListItem2")));
        modelElement.setProperty(Property.LIST_SYMBOL_POSITION, ListSymbolPosition.INSIDE);
        modelElement.setFontSize(30);

        // A hack for a test in order to layout the list at the very left border of the parent area.
        // List symbols are not drawn outside the parent area, so we want to make sure that symbol renderer
        // won't be drawn twice even if there's enough space around the list.
        modelElement.setMarginLeft(500);
        IRenderer listRenderer = modelElement.createRendererSubTree();

        Document document = createDummyDocument();
        listRenderer.setParent(document.getRenderer());
        PdfPage pdfPage = document.getPdfDocument().addNewPage();

        // should be enough to fit a single list-item, but not both
        int height = 80;

        // we don't want to impose any width restrictions
        int width = 1000;
        LayoutResult result = listRenderer.layout(createLayoutContext(width, height));
        assert result.getStatus() == LayoutResult.PARTIAL;
        result.getSplitRenderer().draw(new DrawContext(document.getPdfDocument(), new PdfCanvas(pdfPage)));

        // only split part is drawn, list symbol is expected to be drawn only once.
        Assertions.assertEquals(1, invocationsCounter.getInvocationsCount());
    }

    @Test
    public void symbolPositioningInsideAfterPageBreakTest() {
        List modelElement = new List();
        modelElement.setNextRenderer(new ListRenderer(modelElement));

        for (int i = 0; i < 25; i++) {
            String s = "listitem " + i;
            ListItem listItem = (ListItem) new ListItem().add(new Paragraph(s));
            modelElement.add(listItem);
        }

        modelElement.setProperty(Property.LIST_SYMBOL_POSITION, ListSymbolPosition.INSIDE);
        modelElement.setFontSize(30);

        IRenderer listRenderer = modelElement.createRendererSubTree();

        Document document = createDummyDocument();
        listRenderer.setParent(document.getRenderer());

        LayoutContext layoutContext = createLayoutContext(595, 842);
        LayoutResult result = listRenderer.layout(layoutContext);
        result.getOverflowRenderer().layout(layoutContext);

        Pattern regex = Pattern.compile("^.-.*?-.*$");
        java.util.List<IRenderer> childRenderers = listRenderer.getChildRenderers();

        Assertions.assertEquals(0, childRenderers.stream()
                .filter(listitem -> regex.matcher(listitem.toString()).matches()).count());
    }

    private static class ListRendererCreatingNotifyingListSymbols extends ListRenderer {
        private InvocationsCounter counter;

        public ListRendererCreatingNotifyingListSymbols(List modelElement, InvocationsCounter counter) {
            super(modelElement);
            this.counter = counter;
        }

        @Override
        protected IRenderer makeListSymbolRenderer(int index, IRenderer renderer) {
            return new NotifyingListSymbolRenderer(new Text("-"), counter);
        }

        @Override
        public IRenderer getNextRenderer() {
            return new ListRendererCreatingNotifyingListSymbols((List) getModelElement(), counter);
        }
    }

    private static class NotifyingListSymbolRenderer extends TextRenderer {
        private InvocationsCounter counter;

        public NotifyingListSymbolRenderer(Text textElement, InvocationsCounter counter) {
            super(textElement);
            this.counter = counter;
        }

        @Override
        public void draw(DrawContext drawContext) {
            counter.registerInvocation();
            super.draw(drawContext);
        }
    }

    private static class InvocationsCounter {
        private int counter = 0;
        void registerInvocation() {
            ++counter;
        }
        int getInvocationsCount() {
            return counter;
        }
    }
}
