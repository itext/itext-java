/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
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
package com.itextpdf.svg.converter;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.pdf.xobject.PdfXObject;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.layout.font.FontSet;
import com.itextpdf.svg.dummy.sdk.ExceptionInputStream;
import com.itextpdf.svg.exceptions.SvgLogMessageConstant;
import com.itextpdf.svg.processors.ISvgConverterProperties;
import com.itextpdf.svg.processors.ISvgProcessorResult;
import com.itextpdf.svg.processors.impl.SvgConverterProperties;
import com.itextpdf.svg.processors.impl.SvgProcessorResult;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgIntegrationTest;
import com.itextpdf.svg.renderers.impl.RectangleSvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.SvgTagSvgNodeRenderer;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


@Category(IntegrationTest.class)
public class SvgConverterIntegrationTest extends SvgIntegrationTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/svg/converter/SvgConverterTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/svg/converter/SvgConverterTest/";

    private static final String ECLIPSESVGSTRING = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
            "<svg\n" +
            "   xmlns=\"http://www.w3.org/2000/svg\"\n" +
            "   width=\"200pt\"\n" +
            "   height=\"200pt\"\n" +
            "   viewBox=\"0 0 100 100\"\n" +
            "   version=\"1.1\">\n" +
            "    <circle\n" +
            "       style=\"opacity:1;fill:none;fill-opacity:1;stroke:#ffcc00;stroke-width:4.13364887;stroke-miterlimit:4;stroke-opacity:1\"\n" +
            "       cx=\"35.277779\"\n" +
            "       cy=\"35.277779\"\n" +
            "       r=\"33.210953\" />\n" +
            "    <circle\n" +
            "       style=\"opacity:1;fill:#ffcc00;fill-opacity:1;stroke:#ffcc00;stroke-width:1.42177439;stroke-miterlimit:4;stroke-dashoffset:0;stroke-opacity:1\"\n" +
            "       id=\"path923\"\n" +
            "       cx=\"35.277779\"\n" +
            "       cy=\"35.277779\"\n" +
            "       r=\"16.928001\" />\n" +
            "</svg>\n";

    @BeforeClass
    public static void beforeClass() {
        ITextTest.createDestinationFolder(destinationFolder);
    }

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void unusedXObjectIntegrationTest() throws IOException, InterruptedException {
        // This method tests that making an XObject does not, in itself, influence the document it's for.
        PdfDocument doc1 = new PdfDocument(new PdfWriter(destinationFolder + "unusedXObjectIntegrationTest1.pdf"));
        PdfDocument doc2 = new PdfDocument(new PdfWriter(destinationFolder + "unusedXObjectIntegrationTest2.pdf"));
        doc1.addNewPage();
        doc2.addNewPage();

        SvgConverter.convertToXObject("<svg width='100pt' height='100pt' />", doc1);

        doc1.close();
        doc2.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "unusedXObjectIntegrationTest1.pdf", destinationFolder + "unusedXObjectIntegrationTest2.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void basicIntegrationTest() throws IOException, InterruptedException {
        String filename = "basicIntegrationTest.pdf";
        PdfDocument doc = new PdfDocument(new PdfWriter(destinationFolder + filename));
        doc.addNewPage();

        PdfFormXObject form = SvgConverter.convertToXObject("<svg width='100pt' height='100pt' />", doc);

        new PdfCanvas(doc.getPage(1)).addXObject(form, new Rectangle(100, 100, 100, 100));
        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = SvgLogMessageConstant.UNMAPPEDTAG),
    })
    public void nonExistingTagIntegrationTest() {
        String contents = "<svg width='100pt' height='100pt'> <nonExistingTag/> </svg>";
        PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        doc.addNewPage();

        SvgConverter.convertToXObject(contents, doc);
        doc.close();
    }

    /**
     * Convert a SVG file defining all ignored tags currently defined in the project.
     * @result There will be no <code>Exception</code> during the process and PDF output is generated.
     */
    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = SvgLogMessageConstant.UNMAPPEDTAG, count = 32),
    })
    public void convertFileWithAllIgnoredTags() throws IOException, InterruptedException {
        convertAndCompareSinglePage(sourceFolder, destinationFolder, "ignored_tags");
    }

    /**
     * Convert a SVG file of a chart which contains some currently ignored tags.
     * @result There will be no <code>Exception</code> during the process and PDF output is generated.
     */
    @Test
    public void convertChartWithSomeIgnoredTags() throws IOException, InterruptedException {
         convertAndCompareSinglePage(sourceFolder, destinationFolder, "chart_snippet");
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = SvgLogMessageConstant.UNMAPPEDTAG, count = 14),
    })
    public void caseSensitiveTagTest() {
        String contents = "<svg width='100pt' height='100pt'>" +
                "<altGlyph /><altglyph />" +
                "<feMergeNode /><femergeNode /><feMergenode /><femergenode />"+
                "<foreignObject /><foreignobject />" +
                "<glyphRef /><glyphref />"+
                "<linearGradient /><lineargradient />" +
                "<radialGradient /><radialgradient />" +
                "</svg>";
        PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        doc.addNewPage();

        SvgConverter.convertToXObject(contents, doc);
        doc.close();
    }

    @Test
    public void pdfFromSvgString() throws IOException, InterruptedException {
        PdfWriter writer = new PdfWriter(destinationFolder  + "pdfFromSvgString.pdf");
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.addNewPage();

        String svg = "<?xml version=\"1.0\" standalone=\"no\"?>\n" +
                "<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\"\n" +
                "        \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\n" +
                "<svg width=\"500\" height=\"400\" xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\">\n" +
                "    <rect width=\"500\" height=\"400\" fill=\"none\" stroke=\"black\"/>\n" +
                "    <line x1=\"0\" y1=\"0\" x2=\"20\" y2=\"135\" stroke=\"black\"/>\n" +
                "    <circle cx=\"20\" cy=\"135\" r=\"5\" fill=\"none\" stroke=\"black\"/>\n" +
                "    <text x=\"20\" y=\"135\" font-family=\"Verdana\" font-size=\"35\">\n" +
                "        Hello world\n" +
                "    </text>\n" +
                "</svg>";

        int pagenr = 1;
        SvgConverter.drawOnDocument(svg,pdfDoc,pagenr);
        String output = destinationFolder + "pdfFromSvgString.pdf";
        String cmp_file = sourceFolder + "cmp_pdfFromSvgString.pdf";
        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(output, cmp_file, destinationFolder, "diff_"));
    }

    @Test
    public void fromFile() throws IOException, InterruptedException {
        PdfWriter writer = new PdfWriter(destinationFolder + "pdfFromSvgFile.pdf");
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.addNewPage();

        String svg = "eclipse.svg";
        String output = destinationFolder + "pdfFromSvgFile.pdf";
        String cmp_file = sourceFolder + "cmp_pdfFromSvgFile.pdf";

        int pagenr = 1;
        FileInputStream fis = new FileInputStream(sourceFolder + svg);
        SvgConverter.drawOnDocument(fis,pdfDoc,pagenr);
        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(output, cmp_file, destinationFolder, "diff_"));
    }

    @Test
    public void addToExistingDoc() throws IOException, InterruptedException {
        PdfReader reader = new PdfReader(sourceFolder + "cmp_eclipse.pdf");
        PdfWriter writer = new PdfWriter(destinationFolder + "addToExistingDoc.pdf");
        PdfDocument pdfDoc = new PdfDocument(reader, writer);
        pdfDoc.addNewPage();

        String output = destinationFolder + "addToExistingDoc.pdf";
        String cmp_file = sourceFolder + "cmp_addToExistingDoc.pdf";

        int pagenr = 1;
        FileInputStream fis = new FileInputStream(sourceFolder + "minimal.svg");
        SvgConverter.drawOnDocument(fis,pdfDoc,pagenr);
        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(output, cmp_file, destinationFolder, "diff_"));
    }

    @Test
    public void singlePageHelloWorldTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(sourceFolder, destinationFolder, "hello_world");
    }

    @Test
    public void twoArgTest() throws IOException, InterruptedException {
        String name = "hello_world";
        FileInputStream fis = new FileInputStream(sourceFolder + name + ".svg");
        FileOutputStream fos = new FileOutputStream(destinationFolder + name + ".pdf");
        SvgConverter.createPdf(fis, fos);
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + name + ".pdf", sourceFolder + "cmp_" + name + ".pdf", destinationFolder, "diff_"));
    }

    @Test
    public void drawOnSpecifiedPositionX() throws IOException, InterruptedException {
        String name = "eclipse";
        int x = 50;
        int y = 0;
        String destName = name + "_" + x + "_" + y;
        FileInputStream fis = new FileInputStream(sourceFolder + name + ".svg");
        drawOnSpecifiedPositionDocument(fis, destinationFolder + destName + ".pdf", x, y);

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + destName + ".pdf", sourceFolder + "cmp_" + destName + ".pdf", destinationFolder, "diff_"));
    }

    @Test
    public void drawOnSpecifiedPositionY() throws IOException, InterruptedException {
        String name = "eclipse";
        int x = 0;
        int y = 100;
        String destName = name + "_" + x + "_" + y;
        FileInputStream fis = new FileInputStream(sourceFolder + name + ".svg");
        drawOnSpecifiedPositionDocument(fis, destinationFolder + destName + ".pdf", x, y);

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + destName + ".pdf", sourceFolder + "cmp_" + destName + ".pdf", destinationFolder, "diff_"));
    }

    @Test
    public void drawOnSpecifiedPositionXY() throws IOException, InterruptedException {
        String name = "eclipse";
        int x = 50;
        int y = 100;
        String destName = name + "_" + x + "_" + y;
        FileInputStream fis = new FileInputStream(sourceFolder + name + ".svg");
        drawOnSpecifiedPositionDocument(fis, destinationFolder + destName + ".pdf", x, y);

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + destName + ".pdf", sourceFolder + "cmp_" + destName + ".pdf", destinationFolder, "diff_"));

    }

    @Test
    public void drawOnSpecifiedPositionNegativeX() throws IOException, InterruptedException {
        String name = "eclipse";
        int x = -50;
        int y = 0;
        String destName = name + "_" + x + "_" + y;
        FileInputStream fis = new FileInputStream(sourceFolder + name + ".svg");
        drawOnSpecifiedPositionDocument(fis, destinationFolder + destName + ".pdf", x, y);

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + destName + ".pdf", sourceFolder + "cmp_" + destName + ".pdf", destinationFolder, "diff_"));
    }

    @Test
    public void drawOnSpecifiedPositionNegativeY() throws IOException, InterruptedException {
        String name = "eclipse";
        int x = 0;
        int y = -100;
        String destName = name + "_" + x + "_" + y;
        FileInputStream fis = new FileInputStream(sourceFolder + name + ".svg");
        drawOnSpecifiedPositionDocument(fis, destinationFolder + destName + ".pdf", x, y);

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + destName + ".pdf", sourceFolder + "cmp_" + destName + ".pdf", destinationFolder, "diff_"));

    }

    @Test
    public void drawOnSpecifiedPositionNegativeXY() throws IOException, InterruptedException {
        String name = "eclipse";
        int x = -50;
        int y = -100;
        String destName = name + "_" + x + "_" + y;
        FileInputStream fis = new FileInputStream(sourceFolder + name + ".svg");
        drawOnSpecifiedPositionDocument(fis, destinationFolder + destName + ".pdf", x, y);

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + destName + ".pdf", sourceFolder + "cmp_" + destName + ".pdf", destinationFolder, "diff_"));

    }

    @Test
    public void drawOnSpecifiedPositionPartialOnPage() throws IOException, InterruptedException {
        String name = "eclipse";
        int x = -50;
        int y = -50;
        String destName = name + "_" + x + "_" + y;
        FileInputStream fis = new FileInputStream(sourceFolder + name + ".svg");
        drawOnSpecifiedPositionDocument(fis, destinationFolder + destName + ".pdf", x, y);

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + destName + ".pdf", sourceFolder + "cmp_" + destName + ".pdf", destinationFolder, "diff_"));

    }

    @Test
    public void convertToXObjectStringPdfDocumentConverterProps() throws IOException, InterruptedException {
        String name = "eclipse";
        String destName = "CTXO_" + name + "_StringDocProps";

        PdfDocument doc = new PdfDocument(new PdfWriter(destinationFolder + destName + ".pdf"));
        PdfPage page = doc.addNewPage();

        ISvgConverterProperties props = new SvgConverterProperties();

        PdfXObject xObj = SvgConverter.convertToXObject(ECLIPSESVGSTRING, doc, props);

        PdfCanvas canv = new PdfCanvas(page);
        canv.addXObject(xObj, 0, 0);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + destName + ".pdf", sourceFolder + "cmp_" + destName + ".pdf", destinationFolder, "diff_"));
    }

    @Test
    public void convertToXObjectStreamPdfDocumentConverterProps() throws IOException, InterruptedException {
        String name = "eclipse";
        String destName = "CTXO_" + name + "_StreamDocProps";
        FileInputStream fis = new FileInputStream(sourceFolder + name + ".svg");
        PdfDocument doc = new PdfDocument(new PdfWriter(destinationFolder + destName + ".pdf"));
        PdfPage page = doc.addNewPage();

        ISvgConverterProperties props = new SvgConverterProperties();

        PdfXObject xObj = SvgConverter.convertToXObject(fis, doc, props);

        PdfCanvas canv = new PdfCanvas(page);
        canv.addXObject(xObj, 0, 0);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + destName + ".pdf", sourceFolder + "cmp_" + destName + ".pdf", destinationFolder, "diff_"));
    }

    @Test
    public void convertToImageStreamDocument() throws IOException, InterruptedException {
        String name = "eclipse";
        FileInputStream fis = new FileInputStream(sourceFolder + name + ".svg");
        String destName = "CTI_" + name + "_StreamDocument";
        FileOutputStream fos = new FileOutputStream(destinationFolder + destName + ".pdf");
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(fos, new WriterProperties().setCompressionLevel(0)));
        Image image = SvgConverter.convertToImage(fis, pdfDocument);

        Document doc = new Document(pdfDocument);
        doc.add(image);
        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + destName + ".pdf", sourceFolder + "cmp_" + name + ".pdf", destinationFolder, "diff_"));
    }

    @Test
    public void convertToImageStreamDocumentConverterProperties() throws IOException, InterruptedException {
        String name = "eclipse";
        FileInputStream fis = new FileInputStream(sourceFolder + name + ".svg");
        String destName = "CTI_" + name + "_StreamDocumentProps";
        FileOutputStream fos = new FileOutputStream(destinationFolder + destName + ".pdf");
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(fos, new WriterProperties().setCompressionLevel(0)));

        ISvgConverterProperties props = new SvgConverterProperties();
        Image image = SvgConverter.convertToImage(fis, pdfDocument, props);

        Document doc = new Document(pdfDocument);
        doc.add(image);
        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + destName + ".pdf", sourceFolder + "cmp_" + name + ".pdf", destinationFolder, "diff_"));
    }


    @Test
    public void drawOnPageStringPage() throws IOException, InterruptedException {
        String name = "eclipse";
        String destName = "DOP_" + name + "_StringPdfPage";

        PdfDocument doc = new PdfDocument(new PdfWriter(destinationFolder + destName + ".pdf"));
        PdfPage page = doc.addNewPage();

        SvgConverter.drawOnPage(ECLIPSESVGSTRING, page);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + destName + ".pdf", sourceFolder + "cmp_" + destName + ".pdf", destinationFolder, "diff_"));
    }

    @Test
    public void drawOnPageStringPageConverterProps() throws IOException, InterruptedException {
        String name = "eclipse";
        String destName = "DOP_" + name + "_StringPdfPageConverterProps";

        PdfDocument doc = new PdfDocument(new PdfWriter(destinationFolder + destName + ".pdf"));
        PdfPage page = doc.addNewPage();

        ISvgConverterProperties props = new SvgConverterProperties();

        SvgConverter.drawOnPage(ECLIPSESVGSTRING, page, props);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + destName + ".pdf", sourceFolder + "cmp_" + destName + ".pdf", destinationFolder, "diff_"));
    }

    @Test
    public void drawOnPageStreamPage() throws IOException, InterruptedException {
        String name = "eclipse";
        String destName = "DOP_" + name + "_StreamPdfPage";
        FileInputStream fis = new FileInputStream(sourceFolder + name + ".svg");
        PdfDocument doc = new PdfDocument(new PdfWriter(destinationFolder + destName + ".pdf"));
        PdfPage page = doc.addNewPage();

        SvgConverter.drawOnPage(fis, page);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + destName + ".pdf", sourceFolder + "cmp_" + destName + ".pdf", destinationFolder, "diff_"));
    }

    @Test
    public void drawOnPageStreamPageConverterProperties() throws IOException, InterruptedException {
        String name = "eclipse";
        String destName = "DOP_" + name + "_StreamPdfPageConverterProperties";
        FileInputStream fis = new FileInputStream(sourceFolder + name + ".svg");
        PdfDocument doc = new PdfDocument(new PdfWriter(destinationFolder + destName + ".pdf"));
        PdfPage page = doc.addNewPage();

        ISvgConverterProperties props = new SvgConverterProperties();

        SvgConverter.drawOnPage(fis, page, props);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + destName + ".pdf", sourceFolder + "cmp_" + destName + ".pdf", destinationFolder, "diff_"));
    }

    @Test
    public void drawOnDocumentStringPdfDocumentInt() throws IOException, InterruptedException {
        String name = "eclipse";
        String destName = "DOD_" + name + "_StringPdfDocumentInt";
        PdfDocument doc = new PdfDocument(new PdfWriter(destinationFolder + destName + ".pdf"));
        doc.addNewPage();

        SvgConverter.drawOnDocument(ECLIPSESVGSTRING, doc, 1);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + destName + ".pdf", sourceFolder + "cmp_" + destName + ".pdf", destinationFolder, "diff_"));
    }

    @Test
    public void drawOnDocumentStringPdfDocumentIntConverterProperties() throws IOException, InterruptedException {
        String name = "eclipse";
        String destName = "DOD_" + name + "_StringPdfDocumentIntProps";
        FileInputStream fis = new FileInputStream(sourceFolder + name + ".svg");
        PdfDocument doc = new PdfDocument(new PdfWriter(destinationFolder + destName + ".pdf"));
        doc.addNewPage();

        ISvgConverterProperties props = new SvgConverterProperties();

        SvgConverter.drawOnDocument(fis, doc, 1, props);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + destName + ".pdf", sourceFolder + "cmp_" + destName + ".pdf", destinationFolder, "diff_"));
    }

    @Test
    public void drawOnDocumentStreamPdfDocumentIntConverterProperties() throws IOException, InterruptedException {
        String name = "eclipse";
        String destName = "DOD_" + name + "_StreamPdfDocumentIntProps";
        PdfDocument doc = new PdfDocument(new PdfWriter(destinationFolder + destName + ".pdf"));
        doc.addNewPage();

        ISvgConverterProperties props = new SvgConverterProperties();

        SvgConverter.drawOnDocument(ECLIPSESVGSTRING, doc, 1, props);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + destName + ".pdf", sourceFolder + "cmp_" + destName + ".pdf", destinationFolder, "diff_"));
    }

    @Test
    public void drawOnCanvasStringPdfCanvasConverter() throws IOException, InterruptedException {
        String name = "eclipse";
        String destName = "DOC_" + name + "_StringCanvas";
        PdfDocument doc = new PdfDocument(new PdfWriter(destinationFolder + destName + ".pdf"));
        PdfCanvas canvas = new PdfCanvas(doc.addNewPage());

        SvgConverter.drawOnCanvas(ECLIPSESVGSTRING, canvas);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + destName + ".pdf", sourceFolder + "cmp_" + destName + ".pdf", destinationFolder, "diff_"));

    }

    @Test
    public void drawOnCanvasStringPdfCanvasConverterProps() throws IOException, InterruptedException {
        String name = "eclipse";
        String destName = "DOC_" + name + "_StringCanvasProps";
        PdfDocument doc = new PdfDocument(new PdfWriter(destinationFolder + destName + ".pdf"));
        PdfCanvas canvas = new PdfCanvas(doc.addNewPage());

        ISvgConverterProperties props = new SvgConverterProperties();

        SvgConverter.drawOnCanvas(ECLIPSESVGSTRING, canvas, props);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + destName + ".pdf", sourceFolder + "cmp_" + destName + ".pdf", destinationFolder, "diff_"));
    }

    @Test
    public void drawOnCanvasStreamPdfCanvas() throws IOException, InterruptedException {
        String name = "eclipse";
        String destName = "DOC_" + name + "_StreamCanvas";
        PdfDocument doc = new PdfDocument(new PdfWriter(destinationFolder + destName + ".pdf"));
        FileInputStream fis = new FileInputStream(sourceFolder + name + ".svg");
        PdfCanvas canvas = new PdfCanvas(doc.addNewPage());

        SvgConverter.drawOnCanvas(fis, canvas);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + destName + ".pdf", sourceFolder + "cmp_" + destName + ".pdf", destinationFolder, "diff_"));
    }

    @Test
    public void drawOnCanvasStreamPdfCanvasConverterProps() throws IOException, InterruptedException {
        String name = "eclipse";
        String destName = "DOC_" + name + "_StreamCanvasProps";
        PdfDocument doc = new PdfDocument(new PdfWriter(destinationFolder + destName + ".pdf"));
        FileInputStream fis = new FileInputStream(sourceFolder + name + ".svg");
        PdfCanvas canvas = new PdfCanvas(doc.addNewPage());

        ISvgConverterProperties props = new SvgConverterProperties();

        SvgConverter.drawOnCanvas(fis, canvas, props);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + destName + ".pdf", sourceFolder + "cmp_" + destName + ".pdf", destinationFolder, "diff_"));
    }

    private static void drawOnSpecifiedPositionDocument(InputStream svg, String dest, int x, int y) throws IOException {
        PdfDocument document = new PdfDocument(new PdfWriter(dest, new WriterProperties().setCompressionLevel(0)));
        document.addNewPage();

        SvgConverter.drawOnDocument(svg, document, 1, x, y);

        document.close();
    }

    @Test
    public void parseAndProcessSuccessTest() throws IOException {
        String name = "minimal";
        FileInputStream fis = new FileInputStream(sourceFolder + name + ".svg");
        Map<String, ISvgNodeRenderer> map = new HashMap<>();
        RectangleSvgNodeRenderer rect = new RectangleSvgNodeRenderer();
        rect.setAttribute("fill", "none");
        rect.setAttribute("stroke", "black");
        rect.setAttribute("width", "500");
        rect.setAttribute("height", "400");
        ISvgNodeRenderer root = new SvgTagSvgNodeRenderer();
        root.setAttribute("xmlns", "http://www.w3.org/2000/svg");
        root.setAttribute("version", "1.1");
        root.setAttribute("width", "500");
        root.setAttribute("height", "400");

        ISvgProcessorResult expected = new SvgProcessorResult(map, root, new FontProvider(), new FontSet());

        ISvgProcessorResult actual = SvgConverter.parseAndProcess(fis);
        //TODO(RND-868): remove below checks
        Assert.assertEquals(SvgTagSvgNodeRenderer.class, actual.getRootRenderer().getClass());
        Assert.assertEquals(0, actual.getNamedObjects().size());
        Assert.assertEquals("500", actual.getRootRenderer().getAttribute("width"));

        //TODO(RND-868): Switch test over to this logic
        //Assert.assertEquals(expected,actual);
    }

    @Test
    public void parseAndProcessIOExceptionTest() throws IOException {
        junitExpectedException.expect(IOException.class);
        InputStream fis = new ExceptionInputStream();

        ISvgProcessorResult result = SvgConverter.parseAndProcess(fis);
    }
}
