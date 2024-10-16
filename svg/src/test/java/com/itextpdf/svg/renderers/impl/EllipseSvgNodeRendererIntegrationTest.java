/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
package com.itextpdf.svg.renderers.impl;

import com.itextpdf.io.exceptions.IOException;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.svg.renderers.SvgIntegrationTest;
import com.itextpdf.test.ITextTest;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class EllipseSvgNodeRendererIntegrationTest extends SvgIntegrationTest {

    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/svg/renderers/impl/EllipseSvgNodeRendererIntegrationTest/";
    public static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/svg/renderers/impl/EllipseSvgNodeRendererIntegrationTest/";

    @BeforeAll
    public static void beforeClass() {
        ITextTest.createDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void basicEllipseTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "basicEllipse");
    }

    @Test
    public void ellipseCxCyAbsentTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "ellipseCxCyAbsent");
    }

    @Test
    public void ellipseCxAbsentTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "ellipseCxAbsent");
    }

    @Test
    public void ellipseCxNegativeTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "ellipseCxNegative");}

    @Test
    public void ellipseCyNegativeTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "ellipseCyNegative");
    }

    @Test
    public void ellipseCyAbsentTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "ellipseCyAbsent");
    }

    @Test
    //TODO: update cmp_ when DEVSIX-3119
    public void ellipseRxAbsentTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "ellipseRxAbsent");
    }

    @Test
    //TODO: update cmp_ when DEVSIX-3119
    public void ellipseRyAbsentTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "ellipseRyAbsent");
    }

    @Test
    public void ellipseRxNegativeTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "ellipseRxNegative");
    }

    @Test
    public void ellipseRyNegativeTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "ellipseRyNegative");
    }

    @Test
    public void ellipseTranslatedTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "ellipseTranslated");
    }

    @Test
    public void ellipseRotatedTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "ellipseRotated");
    }

    @Test
    public void ellipseScaledUpTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "ellipseScaledUp");
    }

    @Test
    public void ellipseScaledDownTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "ellipseScaledDown");
    }

    @Test
    public void ellipseScaledXYTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "ellipseScaledXY");
    }

    @Test
    public void ellipseSkewXTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "ellipseSkewX");
    }

    @Test
    public void ellipseSkewYTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "ellipseSkewY");
    }

    @Test
    public void parseParametersAndCalculateCoordinatesWithBetterPrecisionEllipseTest() throws java.io.IOException {
        String filename = "calculateCoordinatesWithBetterPrecision.pdf";
        PdfDocument doc = new PdfDocument(new PdfWriter(DESTINATION_FOLDER + filename));
        doc.addNewPage();

        EllipseSvgNodeRenderer ellipseRenderer = new EllipseSvgNodeRenderer();
        ellipseRenderer.setAttribute(SvgConstants.Attributes.CX, "170.3");
        ellipseRenderer.setAttribute(SvgConstants.Attributes.CY, "339.5");
        ellipseRenderer.setAttribute(SvgConstants.Attributes.RX, "6");
        ellipseRenderer.setAttribute(SvgConstants.Attributes.RY, "6");

        // Parse parameters with better precision (in double type) in the method CssUtils#parseAbsoluteLength
        ellipseRenderer.setParameters();

        SvgDrawContext context = new SvgDrawContext(null, null);
        PdfCanvas cv = new PdfCanvas(doc, 1);
        context.pushCanvas(cv);

        // Calculate coordinates with better precision (in double type) in the method EllipseSvgNodeRenderer#doDraw
        ellipseRenderer.draw(context);

        String pageContentBytes = new String(doc.getPage(1).getContentBytes(), StandardCharsets.UTF_8);
        doc.close();

        String expectedResult = "132.22 254.63 m\n"
                + "132.22 257.11 130.21 259.13 127.72 259.13 c\n"
                + "125.24 259.13 123.22 257.11 123.22 254.63 c\n"
                + "123.22 252.14 125.24 250.13 127.72 250.13 c\n"
                + "130.21 250.13 132.22 252.14 132.22 254.63 c";
        Assertions.assertTrue(pageContentBytes.contains(expectedResult));
    }

    @Test
    // TODO: DEVSIX-3932 update cmp_ after fix
    public void ellipseWithBigStrokeWidthTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "ellipseWithBigStrokeWidth");
    }
}
