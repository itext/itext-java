/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2018 iText Group NV
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

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.svg.converter.SvgConverter;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import java.io.IOException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class RectangleSvgNodeRendererIntegrationTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/svg/renderers/impl/RectangleSvgNodeRendererTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/svg/renderers/impl/RectangleSvgNodeRendererTest/";

    @BeforeClass
    public static void beforeClass() {
        ITextTest.createDestinationFolder(destinationFolder);
    }

    @Test
    public void basicRectangleTest() throws IOException, InterruptedException {
        String filename = "basicRectangleTest.pdf";
        PdfDocument doc = new PdfDocument(new PdfWriter(destinationFolder + filename));
        doc.addNewPage();

        String contents = "<svg width='800' height='800'> <rect x='100' y='100' width='80' height='80' stroke='green' fill ='cyan'/></svg>";
        SvgConverter.drawOnDocument(contents, doc, 1);

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void basicRectangleRxRyZeroTest() throws IOException, InterruptedException {
        String filename = "basicRectangleRxRyZeroTest.pdf";
        PdfDocument doc = new PdfDocument(new PdfWriter(destinationFolder + filename));
        doc.addNewPage();

        String contents = "<svg width='800' height='800'> <rect x='100' y='100' width='80' height='80' rx='0' ry ='0' stroke='green' fill ='cyan'/></svg>";
        SvgConverter.drawOnDocument(contents, doc, 1);

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void basicCircularRoundedRectangleRyZeroTest() throws IOException, InterruptedException {
        String filename = "basicCircularRoundedRectangleRyZeroTest.pdf";
        PdfDocument doc = new PdfDocument(new PdfWriter(destinationFolder + filename));
        doc.addNewPage();

        String contents = "<svg width='800' height='800'> <rect x='100' y='100' width='80' height='80' rx='10' ry='0' stroke='green' fill ='cyan'/></svg>";
        SvgConverter.drawOnDocument(contents, doc, 1);

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void basicCircularRoundedRectangleRxZeroTest() throws IOException, InterruptedException {
        String filename = "basicCircularRoundedRectangleRxZeroTest.pdf";
        PdfDocument doc = new PdfDocument(new PdfWriter(destinationFolder + filename));
        doc.addNewPage();

        String contents = "<svg width='800' height='800'> <rect x='100' y='100' width='80' height='80' rx='0' ry='10' stroke='green' fill ='cyan'/></svg>";
        SvgConverter.drawOnDocument(contents, doc, 1);

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void basicCircularRoundedRxRectangleTest() throws IOException, InterruptedException {
        String filename = "basicCircularRoundedRxRectangleTest.pdf";
        PdfDocument doc = new PdfDocument(new PdfWriter(destinationFolder + filename));
        doc.addNewPage();

        String contents = "<svg width='800' height='800'> <rect x='100' y='100' width='80' height='80' rx='10' stroke='green' fill ='cyan'/></svg>";
        SvgConverter.drawOnDocument(contents, doc, 1);

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void basicCircularRoundedRyRectangleTest() throws IOException, InterruptedException {
        String filename = "basicCircularRoundedRyRectangleTest.pdf";
        PdfDocument doc = new PdfDocument(new PdfWriter(destinationFolder + filename));
        doc.addNewPage();

        String contents = "<svg width='800' height='800'> <rect x='100' y='100' width='80' height='80' ry='15' stroke='green' fill ='cyan'/></svg>";
        SvgConverter.drawOnDocument(contents, doc, 1);

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void basicEllipticalRoundedRectangleXTest() throws IOException, InterruptedException {
        String filename = "basicEllipticalRoundedRectangleXTest.pdf";
        PdfDocument doc = new PdfDocument(new PdfWriter(destinationFolder + filename));
        doc.addNewPage();

        String contents = "<svg width='800' height='800'> <rect x='100' y='100' width='80' height='80' rx='20' ry='5' stroke='green' fill ='cyan' /></svg>";
        SvgConverter.drawOnDocument(contents, doc, 1);

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void basicEllipticalRoundedRectangleYTest() throws IOException, InterruptedException {
        String filename = "basicEllipticalRoundedRectangleYTest.pdf";
        PdfDocument doc = new PdfDocument(new PdfWriter(destinationFolder + filename));
        doc.addNewPage();

        String contents = "<svg width='800' height='800'> <rect x='100' y='100' width='80' height='80' rx='5' ry='10' stroke='green' fill ='cyan'/></svg>";
        SvgConverter.drawOnDocument(contents, doc, 1);

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void basicEllipticalWidthCappedRoundedRectangleTest() throws IOException, InterruptedException {
        String filename = "basicEllipticalWidthCappedRoundedRectangleTest.pdf";
        PdfDocument doc = new PdfDocument(new PdfWriter(destinationFolder + filename));
        doc.addNewPage();

        String contents = "<svg width='800' height='800'> <rect x='100' y='100' width='80' height='80' rx='50' ry='10' stroke='green' fill ='cyan'/></svg>";
        SvgConverter.drawOnDocument(contents, doc, 1);

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void basicEllipticalHeightCappedRoundedRectangleTest() throws IOException, InterruptedException {
        String filename = "basicEllipticalHeightCappedRoundedRectangleTest.pdf";
        PdfDocument doc = new PdfDocument(new PdfWriter(destinationFolder + filename));
        doc.addNewPage();

        String contents = "<svg width='800' height='800'> <rect x='100' y='100' width='80' height='80' rx='10' ry='45' stroke='green' fill ='cyan'/></svg>";
        SvgConverter.drawOnDocument(contents, doc, 1);

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void basicEllipticalNegativeWidthRoundedRectangleTest() throws IOException, InterruptedException {
        String filename = "basicEllipticalNegativeWidthRoundedRectangleTest.pdf";
        PdfDocument doc = new PdfDocument(new PdfWriter(destinationFolder + filename));
        doc.addNewPage();

        String contents = "<svg width='800' height='800'> <rect x='100' y='100' width='80' height='80' rx='-10' ry='15' stroke='green' fill ='cyan'/></svg>";
        SvgConverter.drawOnDocument(contents, doc, 1);

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void basicEllipticalNegativeHeightRoundedRectangleTest() throws IOException, InterruptedException {
        String filename = "basicEllipticalNegativeHeightRoundedRectangleTest.pdf";
        PdfDocument doc = new PdfDocument(new PdfWriter(destinationFolder + filename));
        doc.addNewPage();

        String contents = "<svg width='800' height='800'> <rect x='100' y='100' width='80' height='80' rx='10' ry='-15' stroke='green' fill ='cyan'/></svg>";
        SvgConverter.drawOnDocument(contents, doc, 1);

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void complexRectangleTest() throws IOException, InterruptedException {
        String filename = "complexRectangleTest.pdf";
        PdfDocument doc = new PdfDocument(new PdfWriter(destinationFolder + filename,new WriterProperties().setCompressionLevel(0)));
        doc.addNewPage();

        String contents = "<?xml version=\"1.0\" standalone=\"no\"?>\n" +
                "<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" width='800' height='800'>\n" +
                "\t<rect x='0' y='0' width='800' height='800' stroke= 'red' fill='white'/>\n" +
                "\t<line x1='0' y1='0' x2='100' y2='100' stroke-width='5' stroke='black' />\n" +
                "\t<line x1='100' y1='100' x2='200' y2='100' stroke-width='5' stroke='black' />\n" +
                "\t<line x1='100' y1='100' x2='100' y2='200' stroke-width='5' stroke='black' />\n" +
                "    <rect x='100' y='100' width='80' height='80' rx ='25' ry='15' \n" +
                "\tstroke='green' fill ='cyan'/>\n" +
                "</svg>";
        SvgConverter.drawOnDocument(contents, doc, 1);

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

}
