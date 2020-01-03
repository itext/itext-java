/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2020 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.svg.renderers.impl;

import com.itextpdf.io.IOException;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.svg.renderers.SvgIntegrationTest;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.nio.charset.StandardCharsets;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class EllipseSvgNodeRendererIntegrationTest extends SvgIntegrationTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/svg/renderers/impl/EllipseSvgNodeRendererIntegrationTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/svg/renderers/impl/EllipseSvgNodeRendererIntegrationTest/";

    @BeforeClass
    public static void beforeClass() {
        ITextTest.createDestinationFolder(destinationFolder);
    }

    @Test
    public void basicEllipseTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "basicEllipse");
    }

    @Test
    public void ellipseCxCyAbsentTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "ellipseCxCyAbsent");
    }

    @Test
    public void ellipseCxAbsentTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "ellipseCxAbsent");
    }

    @Test
    public void ellipseCxNegativeTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "ellipseCxNegative");}

    @Test
    public void ellipseCyNegativeTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "ellipseCyNegative");
    }

    @Test
    public void ellipseCyAbsentTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "ellipseCyAbsent");
    }

    @Test
    //TODO: update cmp_ when DEVSIX-3119
    public void ellipseRxAbsentTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "ellipseRxAbsent");
    }

    @Test
    //TODO: update cmp_ when DEVSIX-3119
    public void ellipseRyAbsentTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "ellipseRyAbsent");
    }

    @Test
    public void ellipseRxNegativeTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "ellipseRxNegative");
    }

    @Test
    public void ellipseRyNegativeTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "ellipseRyNegative");
    }

    @Test
    public void ellipseTranslatedTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "ellipseTranslated");
    }

    @Test
    public void ellipseRotatedTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "ellipseRotated");
    }

    @Test
    public void ellipseScaledUpTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "ellipseScaledUp");
    }

    @Test
    public void ellipseScaledDownTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "ellipseScaledDown");
    }

    @Test
    public void ellipseScaledXYTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "ellipseScaledXY");
    }

    @Test
    public void ellipseSkewXTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "ellipseSkewX");
    }

    @Test
    public void ellipseSkewYTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "ellipseSkewY");
    }

    @Test
    public void parseParametersAndCalculateCoordinatesWithBetterPrecisionEllipseTest() throws java.io.IOException, InterruptedException {
        String filename = "parseParametersAndCalculateCoordinatesWithBetterPrecisionEllipseTest.pdf";
        PdfDocument doc = new PdfDocument(new PdfWriter(destinationFolder + filename));
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
        Assert.assertTrue(pageContentBytes.contains(expectedResult));
    }
}
