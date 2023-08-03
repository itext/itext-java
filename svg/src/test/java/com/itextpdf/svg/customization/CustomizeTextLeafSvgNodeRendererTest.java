/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
package com.itextpdf.svg.customization;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.styledxmlparser.node.IElementNode;
import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.SvgConstants.Tags;
import com.itextpdf.svg.converter.SvgConverter;
import com.itextpdf.svg.processors.impl.SvgConverterProperties;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.svg.renderers.SvgIntegrationTest;
import com.itextpdf.svg.renderers.factories.DefaultSvgNodeRendererFactory;
import com.itextpdf.svg.renderers.impl.TextLeafSvgNodeRenderer;
import com.itextpdf.test.ITextTest;

import java.io.IOException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class CustomizeTextLeafSvgNodeRendererTest extends SvgIntegrationTest {

    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/svg/customization/CustomizeTextLeafSvgNodeRendererTest/";
    public static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/svg/customization/CustomizeTextLeafSvgNodeRendererTest/";

    @BeforeClass
    public static void beforeClass() {
        ITextTest.createDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void testCustomizeTextLeafSvgNodeRenderer() throws IOException, InterruptedException {
        String pdfFilename = "customizeTextLeafSvgNodeRenderer.pdf";
        PdfDocument doc = new PdfDocument(new PdfWriter(DESTINATION_FOLDER + pdfFilename));
        doc.addNewPage();

        SvgConverterProperties properties = new SvgConverterProperties();
        properties.setRendererFactory(new CustomTextLeafOverridingSvgNodeRendererFactory());

        String svg = "<svg viewBox=\"0 0 240 80\" xmlns=\"http://www.w3.org/2000/svg\">\n"
                + "  <text x=\"20\" y=\"35\" class=\"small\">Hello world</text>\n"
                + "</svg>";

        PdfFormXObject form = SvgConverter.convertToXObject(svg, doc, properties);
        new PdfCanvas(doc.getPage(1)).addXObjectFittedIntoRectangle(form, new Rectangle(100, 100, 240, 80));

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(DESTINATION_FOLDER + pdfFilename, SOURCE_FOLDER + "cmp_" + pdfFilename, DESTINATION_FOLDER, "diff_"));
    }

    private static class CustomTextLeafOverridingSvgNodeRendererFactory extends DefaultSvgNodeRendererFactory {
        @Override
        public ISvgNodeRenderer createSvgNodeRendererForTag(IElementNode tag, ISvgNodeRenderer parent) {
            if (Tags.TEXT_LEAF.equals(tag.name())) {
                return new CustomTextLeafSvgNodeRenderer();
            } else {
                return super.createSvgNodeRendererForTag(tag, parent);
            }
        }
    }

    private static class CustomTextLeafSvgNodeRenderer extends TextLeafSvgNodeRenderer {
        @Override
        public ISvgNodeRenderer createDeepCopy() {
            CustomTextLeafSvgNodeRenderer copy = new CustomTextLeafSvgNodeRenderer();
            deepCopyAttributesAndStyles(copy);
            return copy;
        }

        @Override
        protected void doDraw(SvgDrawContext context) {
            if (this.attributesAndStyles != null && this.attributesAndStyles.containsKey(SvgConstants.Attributes.TEXT_CONTENT)) {
                PdfCanvas currentCanvas = context.getCurrentCanvas();
                currentCanvas.setFillColor(ColorConstants.RED);
                currentCanvas.moveText(context.getTextMove()[0], context.getTextMove()[1]);
                String initialText = this.attributesAndStyles.get(SvgConstants.Attributes.TEXT_CONTENT);
                String amendedText = "_" + initialText + "_";
                currentCanvas.showText(amendedText);
            }
        }
    }
}
