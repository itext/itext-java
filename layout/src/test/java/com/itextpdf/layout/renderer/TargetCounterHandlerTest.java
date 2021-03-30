/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
    Authors: iText Software.

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

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.splitting.DefaultSplitCharacters;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;

@Category(IntegrationTest.class)
public class TargetCounterHandlerTest extends ExtendedITextTest {

    @Test
    public void BlockRendererAddByIDTest() {
        DocumentRenderer documentRenderer = new DocumentRenderer(null);
        DivRenderer divRenderer = new DivRenderer(new Div());
        divRenderer.setParent(documentRenderer);
        String id = "id5";
        divRenderer.setProperty(Property.ID, id);
        LayoutContext layoutContext = new LayoutContext(new LayoutArea(4, new Rectangle(50, 50)));
        divRenderer.layout(layoutContext);

        documentRenderer.getTargetCounterHandler().prepareHandlerToRelayout();
        Assert.assertEquals((Integer) 4, TargetCounterHandler.getPageByID(divRenderer, id));
    }

    @Test
    public void TextRendererAddByIDTest() throws IOException {
        DocumentRenderer documentRenderer = new DocumentRenderer(null);
        TextRenderer textRenderer = new TextRenderer(new Text("a"));

        textRenderer.setProperty(Property.TEXT_RISE, 20F);
        textRenderer.setProperty(Property.CHARACTER_SPACING, 20F);
        textRenderer.setProperty(Property.WORD_SPACING, 20F);
        textRenderer.setProperty(Property.FONT, PdfFontFactory.createFont(StandardFonts.HELVETICA));
        textRenderer.setProperty(Property.FONT_SIZE, new UnitValue(UnitValue.POINT, 20));
        textRenderer.setProperty(Property.SPLIT_CHARACTERS, new DefaultSplitCharacters());

        textRenderer.setParent(documentRenderer);
        String id = "id7";
        textRenderer.setProperty(Property.ID, id);
        LayoutContext layoutContext = new LayoutContext(new LayoutArea(4, new Rectangle(50, 50)));
        textRenderer.layout(layoutContext);

        documentRenderer.getTargetCounterHandler().prepareHandlerToRelayout();
        Assert.assertEquals((Integer) 4, TargetCounterHandler.getPageByID(textRenderer, id));
    }

    @Test
    public void TableRendererAddByIDTest() {
        DocumentRenderer documentRenderer = new DocumentRenderer(null);
        TableRenderer tableRenderer = new TableRenderer(new Table(5));
        tableRenderer.setParent(documentRenderer);
        String id = "id5";
        tableRenderer.setProperty(Property.ID, id);
        LayoutContext layoutContext = new LayoutContext(new LayoutArea(4, new Rectangle(50, 50)));
        tableRenderer.layout(layoutContext);

        documentRenderer.getTargetCounterHandler().prepareHandlerToRelayout();
        Assert.assertEquals((Integer) 4, TargetCounterHandler.getPageByID(tableRenderer, id));
    }

    @Test
    public void ParagraphRendererAddByIDTest() {
        DocumentRenderer documentRenderer = new DocumentRenderer(null);
        ParagraphRenderer paragraphRenderer = new ParagraphRenderer(new Paragraph());
        paragraphRenderer.setParent(documentRenderer);
        String id = "id5";
        paragraphRenderer.setProperty(Property.ID, id);
        LayoutContext layoutContext = new LayoutContext(new LayoutArea(4, new Rectangle(50, 50)));
        paragraphRenderer.layout(layoutContext);

        documentRenderer.getTargetCounterHandler().prepareHandlerToRelayout();
        Assert.assertEquals((Integer) 4, TargetCounterHandler.getPageByID(paragraphRenderer, id));
    }

    @Test
    public void ImageRendererAddByIDTest() {
        DocumentRenderer documentRenderer = new DocumentRenderer(null);
        ImageRenderer imageRenderer = new ImageRenderer(new Image(ImageDataFactory.createRawImage(new byte[]{50, 21})));
        imageRenderer.setParent(documentRenderer);
        String id = "id6";
        imageRenderer.setProperty(Property.ID, id);
        LayoutContext layoutContext = new LayoutContext(new LayoutArea(4, new Rectangle(50, 50)));
        imageRenderer.layout(layoutContext);

        documentRenderer.getTargetCounterHandler().prepareHandlerToRelayout();
        Assert.assertEquals((Integer) 4, TargetCounterHandler.getPageByID(imageRenderer, id));
    }

    @Test
    public void LineRendererAddByIDTest() {
        DocumentRenderer documentRenderer = new DocumentRenderer(null);
        LineRenderer lineRenderer = new LineRenderer();
        lineRenderer.setParent(documentRenderer);
        String id = "id6";
        lineRenderer.setProperty(Property.ID, id);
        LayoutContext layoutContext = new LayoutContext(new LayoutArea(4, new Rectangle(50, 50)));
        lineRenderer.layout(layoutContext);

        documentRenderer.getTargetCounterHandler().prepareHandlerToRelayout();
        Assert.assertEquals((Integer) 4, TargetCounterHandler.getPageByID(lineRenderer, id));
    }
}
