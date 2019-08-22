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
package com.itextpdf.kernel.pdf.canvas;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.io.util.UrlUtil;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.CompressionConstants;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.pdf.canvas.wmf.WmfImageData;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;
import java.awt.Toolkit;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class PdfCanvasTest extends ExtendedITextTest {

    /**
     * Paths to images.
     */
    public static final String[] RESOURCES = {
            "Desert.jpg",
            "bulb.gif",
            "0047478.jpg",
            "itext.png"
    };
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/canvas/PdfCanvasTest/";
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/canvas/PdfCanvasTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void createSimpleCanvas() throws IOException, FileNotFoundException {

        final String author = "Alexander Chingarev";
        final String creator = "iText 6";
        final String title = "Empty iText 6 Document";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(destinationFolder + "simpleCanvas.pdf"));
        pdfDoc.getDocumentInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);
        PdfPage page1 = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page1);
        canvas.rectangle(100, 100, 100, 100).fill();
        canvas.release();
        pdfDoc.close();

        PdfReader reader = new PdfReader(destinationFolder + "simpleCanvas.pdf");
        PdfDocument pdfDocument = new PdfDocument(reader);
        Assert.assertEquals("Rebuilt", false, reader.hasRebuiltXref());
        PdfDictionary info = pdfDocument.getTrailer().getAsDictionary(PdfName.Info);
        Assert.assertEquals("Author", author, info.get(PdfName.Author).toString());
        Assert.assertEquals("Creator", creator, info.get(PdfName.Creator).toString());
        Assert.assertEquals("Title", title, info.get(PdfName.Title).toString());
        Assert.assertEquals("Page count", 1, pdfDocument.getNumberOfPages());
        PdfDictionary page = pdfDocument.getPage(1).getPdfObject();
        Assert.assertEquals(PdfName.Page, page.get(PdfName.Type));
        reader.close();
    }

    @Test
    public void createSimpleCanvasWithDrawing() throws IOException {

        final String fileName = "simpleCanvasWithDrawing.pdf";

        final String author = "Alexander Chingarev";
        final String creator = "iText 6";
        final String title = "Empty iText 6 Document";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(destinationFolder + fileName));
        pdfDoc.getDocumentInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);
        PdfPage page1 = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page1);
        canvas
                .saveState()
                .setLineWidth(30)
                .moveTo(36, 700)
                .lineTo(300, 300)
                .stroke()
                .restoreState();
        canvas
                .saveState()
                .rectangle(250, 500, 100, 100)
                .fill()
                .restoreState();

        canvas
                .saveState()
                .circle(100, 400, 25)
                .fill()
                .restoreState();

        canvas
                .saveState()
                .roundRectangle(100, 650, 100, 100, 10)
                .fill()
                .restoreState();

        canvas
                .saveState()
                .setLineWidth(10)
                .roundRectangle(250, 650, 100, 100, 10)
                .stroke()
                .restoreState();

        canvas
                .saveState()
                .setLineWidth(5)
                .arc(400, 650, 550, 750, 0, 180)
                .stroke()
                .restoreState();

        canvas
                .saveState()
                .setLineWidth(5)
                .moveTo(400, 550)
                .curveTo(500, 570, 450, 450, 550, 550)
                .stroke()
                .restoreState();
        canvas.release();
        pdfDoc.close();

        PdfReader reader = new PdfReader(destinationFolder + fileName);
        PdfDocument pdfDocument = new PdfDocument(reader);
        Assert.assertEquals("Rebuilt", false, reader.hasRebuiltXref());
        PdfDictionary info = pdfDocument.getTrailer().getAsDictionary(PdfName.Info);
        Assert.assertEquals("Author", author, info.get(PdfName.Author).toString());
        Assert.assertEquals("Creator", creator, info.get(PdfName.Creator).toString());
        Assert.assertEquals("Title", title, info.get(PdfName.Title).toString());
        Assert.assertEquals("Page count", 1, pdfDocument.getNumberOfPages());
        PdfDictionary page = pdfDocument.getPage(1).getPdfObject();
        Assert.assertEquals(PdfName.Page, page.get(PdfName.Type));
        pdfDocument.close();
    }

    @Test
    public void createSimpleCanvasWithText() throws IOException {

        final String fileName = "simpleCanvasWithText.pdf";

        final String author = "Alexander Chingarev";
        final String creator = "iText 6";
        final String title = "Empty iText 6 Document";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(destinationFolder + fileName));
        pdfDoc.getDocumentInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);
        PdfPage page1 = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page1);
        //Initialize canvas and write text to it
        canvas
                .saveState()
                .beginText()
                .moveText(36, 750)
                .setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 16)
                .showText("Hello Helvetica!")
                .endText()
                .restoreState();

        canvas
                .saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLDOBLIQUE), 16)
                .showText("Hello Helvetica Bold Oblique!")
                .endText()
                .restoreState();

        canvas
                .saveState()
                .beginText()
                .moveText(36, 650)
                .setFontAndSize(PdfFontFactory.createFont(StandardFonts.COURIER), 16)
                .showText("Hello Courier!")
                .endText()
                .restoreState();

        canvas
                .saveState()
                .beginText()
                .moveText(36, 600)
                .setFontAndSize(PdfFontFactory.createFont(StandardFonts.TIMES_ITALIC), 16)
                .showText("Hello Times Italic!")
                .endText()
                .restoreState();

        canvas
                .saveState()
                .beginText()
                .moveText(36, 550)
                .setFontAndSize(PdfFontFactory.createFont(StandardFonts.SYMBOL), 16)
                .showText("Hello Ellada!")
                .endText()
                .restoreState();

        canvas
                .saveState()
                .beginText()
                .moveText(36, 500)
                .setFontAndSize(PdfFontFactory.createFont(StandardFonts.ZAPFDINGBATS), 16)
                .showText("Hello ZapfDingbats!")
                .endText()
                .restoreState();
        canvas.release();
        pdfDoc.close();

        PdfReader reader = new PdfReader(destinationFolder + fileName);
        PdfDocument pdfDocument = new PdfDocument(reader);
        Assert.assertEquals("Rebuilt", false, reader.hasRebuiltXref());
        PdfDictionary info = pdfDocument.getTrailer().getAsDictionary(PdfName.Info);
        Assert.assertEquals("Author", author, info.get(PdfName.Author).toString());
        Assert.assertEquals("Creator", creator, info.get(PdfName.Creator).toString());
        Assert.assertEquals("Title", title, info.get(PdfName.Title).toString());
        Assert.assertEquals("Page count", 1, pdfDocument.getNumberOfPages());
        PdfDictionary page = pdfDocument.getPage(1).getPdfObject();
        Assert.assertEquals(PdfName.Page, page.get(PdfName.Type));
        pdfDocument.close();
    }

    @Test
    public void createSimpleCanvasWithPageFlush() throws IOException {

        final String author = "Alexander Chingarev";
        final String creator = "iText 6";
        final String title = "Empty iText 6 Document";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(destinationFolder + "simpleCanvasWithPageFlush.pdf"));
        pdfDoc.getDocumentInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);
        PdfPage page1 = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page1);
        canvas.rectangle(100, 100, 100, 100).fill();
        canvas.release();
        page1.flush();
        pdfDoc.close();

        PdfReader reader = new PdfReader(destinationFolder + "simpleCanvasWithPageFlush.pdf");
        PdfDocument pdfDocument = new PdfDocument(reader);
        Assert.assertEquals("Rebuilt", false, reader.hasRebuiltXref());
        PdfDictionary info = pdfDocument.getTrailer().getAsDictionary(PdfName.Info);
        Assert.assertEquals("Author", author, info.get(PdfName.Author).toString());
        Assert.assertEquals("Creator", creator, info.get(PdfName.Creator).toString());
        Assert.assertEquals("Title", title, info.get(PdfName.Title).toString());
        Assert.assertEquals("Page count", 1, pdfDocument.getNumberOfPages());
        PdfDictionary page = pdfDocument.getPage(1).getPdfObject();
        Assert.assertEquals(PdfName.Page, page.get(PdfName.Type));
        pdfDocument.close();
    }

    @Test
    public void createSimpleCanvasWithFullCompression() throws IOException {

        final String author = "Alexander Chingarev";
        final String creator = "iText 6";
        final String title = "Empty iText 6 Document";

        PdfWriter writer = new PdfWriter(destinationFolder + "simpleCanvasWithFullCompression.pdf",
                new WriterProperties().setFullCompressionMode(true));
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getDocumentInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);
        PdfPage page1 = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page1);
        canvas.rectangle(100, 100, 100, 100).fill();
        canvas.release();
        pdfDoc.close();

        PdfReader reader = new PdfReader(destinationFolder + "simpleCanvasWithFullCompression.pdf");
        PdfDocument pdfDocument = new PdfDocument(reader);
        Assert.assertEquals("Rebuilt", false, reader.hasRebuiltXref());
        PdfDictionary info = pdfDocument.getTrailer().getAsDictionary(PdfName.Info);
        Assert.assertEquals("Author", author, info.get(PdfName.Author).toString());
        Assert.assertEquals("Creator", creator, info.get(PdfName.Creator).toString());
        Assert.assertEquals("Title", title, info.get(PdfName.Title).toString());
        Assert.assertEquals("Page count", 1, pdfDocument.getNumberOfPages());
        PdfDictionary page = pdfDocument.getPage(1).getPdfObject();
        Assert.assertEquals(PdfName.Page, page.get(PdfName.Type));
        pdfDocument.close();
    }

    @Test
    public void createSimpleCanvasWithPageFlushAndFullCompression() throws IOException {

        final String author = "Alexander Chingarev";
        final String creator = "iText 6";
        final String title = "Empty iText 6 Document";

        PdfWriter writer = new PdfWriter(destinationFolder + "simpleCanvasWithPageFlushAndFullCompression.pdf", new WriterProperties().setFullCompressionMode(true));
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getDocumentInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);
        PdfPage page1 = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page1);
        canvas.rectangle(100, 100, 100, 100).fill();
        canvas.release();
        page1.flush();
        pdfDoc.close();

        PdfReader reader = new PdfReader(destinationFolder + "simpleCanvasWithPageFlushAndFullCompression.pdf");
        PdfDocument pdfDocument = new PdfDocument(reader);
        Assert.assertEquals("Rebuilt", false, reader.hasRebuiltXref());
        PdfDictionary info = pdfDocument.getTrailer().getAsDictionary(PdfName.Info);
        Assert.assertEquals("Author", author, info.get(PdfName.Author).toString());
        Assert.assertEquals("Creator", creator, info.get(PdfName.Creator).toString());
        Assert.assertEquals("Title", title, info.get(PdfName.Title).toString());
        Assert.assertEquals("Page count", 1, pdfDocument.getNumberOfPages());
        PdfDictionary page = pdfDocument.getPage(1).getPdfObject();
        Assert.assertEquals(PdfName.Page, page.get(PdfName.Type));
        pdfDocument.close();
    }

    @Test
    public void create1000PagesDocument() throws IOException {
        int pageCount = 1000;
        String filename = destinationFolder + pageCount + "PagesDocument.pdf";

        final String author = "Alexander Chingarev";
        final String creator = "iText 6";
        final String title = "Empty iText 6 Document";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));
        pdfDoc.getDocumentInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);
        for (int i = 0; i < pageCount; i++) {
            PdfPage page = pdfDoc.addNewPage();
            PdfCanvas canvas = new PdfCanvas(page);
            canvas
                    .saveState()
                    .beginText()
                    .moveText(36, 700)
                    .setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 72)
                    .showText(Integer.toString(i + 1))
                    .endText()
                    .restoreState();
            canvas.rectangle(100, 500, 100, 100).fill();
            canvas.release();
            page.flush();
        }
        pdfDoc.close();

        PdfReader reader = new PdfReader(destinationFolder + "1000PagesDocument.pdf");
        PdfDocument pdfDocument = new PdfDocument(reader);
        Assert.assertEquals("Rebuilt", false, reader.hasRebuiltXref());
        PdfDictionary info = pdfDocument.getTrailer().getAsDictionary(PdfName.Info);
        Assert.assertEquals("Author", author, info.get(PdfName.Author).toString());
        Assert.assertEquals("Creator", creator, info.get(PdfName.Creator).toString());
        Assert.assertEquals("Title", title, info.get(PdfName.Title).toString());
        Assert.assertEquals("Page count", pageCount, pdfDocument.getNumberOfPages());
        for (int i = 1; i <= pageCount; i++) {
            PdfDictionary page = pdfDocument.getPage(i).getPdfObject();
            Assert.assertEquals(PdfName.Page, page.get(PdfName.Type));
        }
        pdfDocument.close();
    }

    @Test
    public void create100PagesDocument() throws IOException {
        int pageCount = 100;
        String filename = destinationFolder + pageCount + "PagesDocument.pdf";

        final String author = "Alexander Chingarev";
        final String creator = "iText 6";
        final String title = "Empty iText 6 Document";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));
        pdfDoc.getDocumentInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);
        for (int i = 0; i < pageCount; i++) {
            PdfPage page = pdfDoc.addNewPage();
            PdfCanvas canvas = new PdfCanvas(page);
            canvas
                    .saveState()
                    .beginText()
                    .moveText(36, 700)
                    .setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 72)
                    .showText(Integer.toString(i + 1))
                    .endText()
                    .restoreState();
            canvas.rectangle(100, 500, 100, 100).fill();
            canvas.release();
            page.flush();
        }
        pdfDoc.close();

        PdfReader reader = new PdfReader(filename);
        PdfDocument pdfDocument = new PdfDocument(reader);
        Assert.assertEquals("Rebuilt", false, reader.hasRebuiltXref());
        PdfDictionary info = pdfDocument.getTrailer().getAsDictionary(PdfName.Info);
        Assert.assertEquals("Author", author, info.get(PdfName.Author).toString());
        Assert.assertEquals("Creator", creator, info.get(PdfName.Creator).toString());
        Assert.assertEquals("Title", title, info.get(PdfName.Title).toString());
        Assert.assertEquals("Page count", pageCount, pdfDocument.getNumberOfPages());
        for (int i = 1; i <= pageCount; i++) {
            PdfDictionary page = pdfDocument.getPage(i).getPdfObject();
            Assert.assertEquals(PdfName.Page, page.get(PdfName.Type));
        }
        pdfDocument.close();
    }

    @Test
    public void create10PagesDocument() throws IOException {
        int pageCount = 10;
        String filename = destinationFolder + pageCount + "PagesDocument.pdf";

        final String author = "Alexander Chingarev";
        final String creator = "iText 6";
        final String title = "Empty iText 6 Document";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));
        pdfDoc.getDocumentInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);
        for (int i = 0; i < pageCount; i++) {
            PdfPage page = pdfDoc.addNewPage();
            PdfCanvas canvas = new PdfCanvas(page);
            canvas
                    .saveState()
                    .beginText()
                    .moveText(36, 700)
                    .setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 72)
                    .showText(Integer.toString(i + 1))
                    .endText()
                    .restoreState();
            canvas.rectangle(100, 500, 100, 100).fill();
            canvas.release();
            page.flush();
        }
        pdfDoc.close();

        PdfReader reader = new PdfReader(filename);
        PdfDocument pdfDocument = new PdfDocument(reader);
        Assert.assertEquals("Rebuilt", false, reader.hasRebuiltXref());
        PdfDictionary info = pdfDocument.getTrailer().getAsDictionary(PdfName.Info);
        Assert.assertEquals("Author", author, info.get(PdfName.Author).toString());
        Assert.assertEquals("Creator", creator, info.get(PdfName.Creator).toString());
        Assert.assertEquals("Title", title, info.get(PdfName.Title).toString());
        Assert.assertEquals("Page count", pageCount, pdfDocument.getNumberOfPages());
        for (int i = 1; i <= pageCount; i++) {
            PdfDictionary page = pdfDocument.getPage(i).getPdfObject();
            Assert.assertEquals(PdfName.Page, page.get(PdfName.Type));
        }
        pdfDocument.close();
    }

    @Test
    public void create1000PagesDocumentWithText() throws IOException {
        int pageCount = 1000;
        final String filename = destinationFolder + "1000PagesDocumentWithText.pdf";

        final String author = "Alexander Chingarev";
        final String creator = "iText 6";
        final String title = "Empty iText 6 Document";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));
        pdfDoc.getDocumentInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);
        for (int i = 0; i < pageCount; i++) {
            PdfPage page = pdfDoc.addNewPage();
            PdfCanvas canvas = new PdfCanvas(page);
            canvas.saveState()
                    .beginText()
                    .moveText(36, 650)
                    .setFontAndSize(PdfFontFactory.createFont(StandardFonts.COURIER), 16)
                    .showText("Page " + (i + 1))
                    .endText();

            canvas.rectangle(100, 100, 100, 100).fill();
            canvas.release();
            page.flush();
        }
        pdfDoc.close();

        PdfReader reader = new PdfReader(filename);
        PdfDocument pdfDocument = new PdfDocument(reader);
        Assert.assertEquals("Rebuilt", false, reader.hasRebuiltXref());
        PdfDictionary info = pdfDocument.getTrailer().getAsDictionary(PdfName.Info);
        Assert.assertEquals("Author", author, info.get(PdfName.Author).toString());
        Assert.assertEquals("Creator", creator, info.get(PdfName.Creator).toString());
        Assert.assertEquals("Title", title, info.get(PdfName.Title).toString());
        Assert.assertEquals("Page count", pageCount, pdfDocument.getNumberOfPages());
        for (int i = 1; i <= pageCount; i++) {
            PdfDictionary page = pdfDocument.getPage(i).getPdfObject();
            Assert.assertEquals(PdfName.Page, page.get(PdfName.Type));
        }
        pdfDocument.close();
    }

    @Test
    public void create1000PagesDocumentWithFullCompression() throws IOException {
        int pageCount = 1000;
        String filename = destinationFolder + "1000PagesDocumentWithFullCompression.pdf";

        final String author = "Alexander Chingarev";
        final String creator = "iText 6";
        final String title = "Empty iText 6 Document";

        PdfWriter writer = new PdfWriter(filename, new WriterProperties().setFullCompressionMode(true));
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getDocumentInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);
        for (int i = 0; i < pageCount; i++) {
            PdfPage page = pdfDoc.addNewPage();
            PdfCanvas canvas = new PdfCanvas(page);
            canvas
                    .saveState()
                    .beginText()
                    .moveText(36, 700)
                    .setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 72)
                    .showText(Integer.toString(i + 1))
                    .endText()
                    .restoreState();
            canvas.rectangle(100, 500, 100, 100).fill();
            canvas.release();
            page.flush();

        }
        pdfDoc.close();

        PdfReader reader = new PdfReader(filename);
        PdfDocument pdfDocument = new PdfDocument(reader);
        Assert.assertEquals("Rebuilt", false, reader.hasRebuiltXref());
        PdfDictionary info = pdfDocument.getTrailer().getAsDictionary(PdfName.Info);
        Assert.assertEquals("Author", author, info.get(PdfName.Author).toString());
        Assert.assertEquals("Creator", creator, info.get(PdfName.Creator).toString());
        Assert.assertEquals("Title", title, info.get(PdfName.Title).toString());
        Assert.assertEquals("Page count", pageCount, pdfDocument.getNumberOfPages());
        for (int i = 1; i <= pageCount; i++) {
            PdfDictionary page = pdfDocument.getPage(i).getPdfObject();
            Assert.assertEquals(PdfName.Page, page.get(PdfName.Type));
        }
        pdfDocument.close();
    }

    @Test(timeout = 0)
    @Ignore("Too big result file. This test is for manual testing. -Xmx6g shall be set.")
    public void hugeDocumentWithFullCompression() throws IOException {
        int pageCount = 800;
        String filename = destinationFolder + "hugeDocumentWithFullCompression.pdf";

        final String author = "Alexander Chingarev";
        final String creator = "iText 6";
        final String title = "Empty iText 6 Document";

        PdfWriter writer = new PdfWriter(filename, new WriterProperties().setFullCompressionMode(true));
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getDocumentInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);
        for (int i = 0; i < pageCount; i++) {
            PdfPage page = pdfDoc.addNewPage();
            PdfCanvas canvas = new PdfCanvas(page);
            canvas
                    .saveState()
                    .beginText()
                    .moveText(36, 700)
                    .setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 72)
                    .showText(Integer.toString(i + 1))
                    .endText()
                    .restoreState();
            PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.create(sourceFolder + "Willaerts_Adam_The_Embarkation_of_the_Elector_Palantine_Oil_Canvas-huge.jpg"));
            canvas.addXObject(xObject, 100, 500, 400);
            canvas.release();
            page.flush();

        }
        pdfDoc.close();

        PdfReader reader = new PdfReader(filename);
        PdfDocument pdfDocument = new PdfDocument(reader);
        Assert.assertEquals("Rebuilt", false, reader.hasRebuiltXref());
        PdfDictionary info = pdfDocument.getTrailer().getAsDictionary(PdfName.Info);
        Assert.assertEquals("Author", author, info.get(PdfName.Author).toString());
        Assert.assertEquals("Creator", creator, info.get(PdfName.Creator).toString());
        Assert.assertEquals("Title", title, info.get(PdfName.Title).toString());
        Assert.assertEquals("Page count", pageCount, pdfDocument.getNumberOfPages());
        for (int i = 1; i <= pageCount; i++) {
            PdfDictionary page = pdfDocument.getPage(i).getPdfObject();
            Assert.assertEquals(PdfName.Page, page.get(PdfName.Type));
        }
        pdfDocument.close();
    }

    @Test
    public void smallDocumentWithFullCompression() throws IOException {
        String filename = destinationFolder + "smallDocumentWithFullCompression.pdf";

        final String author = "Alexander Chingarev";
        final String creator = "iText 6";
        final String title = "Empty iText 6 Document";

        PdfWriter writer = new PdfWriter(filename, new WriterProperties().setFullCompressionMode(true));
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getDocumentInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);

        PdfPage page = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas
                .saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 72)
                .showText("Hi!")
                .endText()
                .restoreState();
        page.flush();

        pdfDoc.close();

        PdfReader reader = new PdfReader(filename);
        PdfDocument pdfDocument = new PdfDocument(reader);
        Assert.assertEquals("Rebuilt", false, reader.hasRebuiltXref());
        PdfDictionary info = pdfDocument.getTrailer().getAsDictionary(PdfName.Info);
        Assert.assertEquals("Author", author, info.get(PdfName.Author).toString());
        Assert.assertEquals("Creator", creator, info.get(PdfName.Creator).toString());
        Assert.assertEquals("Title", title, info.get(PdfName.Title).toString());
        Assert.assertEquals("Page count", 1, pdfDocument.getNumberOfPages());

        page = pdfDocument.getPage(1);
        Assert.assertEquals(PdfName.Page, page.getPdfObject().get(PdfName.Type));

        pdfDocument.close();
    }

    @Test
    public void create100PagesDocumentWithFullCompression() throws IOException {
        int pageCount = 100;
        String filename = destinationFolder + pageCount + "PagesDocumentWithFullCompression.pdf";

        final String author = "Alexander Chingarev";
        final String creator = "iText 6";
        final String title = "Empty iText 6 Document";

        PdfWriter writer = new PdfWriter(filename, new WriterProperties().setFullCompressionMode(true));
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getDocumentInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);
        for (int i = 0; i < pageCount; i++) {
            PdfPage page = pdfDoc.addNewPage();
            PdfCanvas canvas = new PdfCanvas(page);
            canvas
                    .saveState()
                    .beginText()
                    .moveText(36, 700)
                    .setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 72)
                    .showText(Integer.toString(i + 1))
                    .endText()
                    .restoreState();
            canvas.rectangle(100, 500, 100, 100).fill();
            canvas.release();
            page.flush();
        }
        pdfDoc.close();

        PdfReader reader = new PdfReader(filename);
        PdfDocument pdfDocument = new PdfDocument(reader);
        Assert.assertEquals("Rebuilt", false, reader.hasRebuiltXref());
        PdfDictionary info = pdfDocument.getTrailer().getAsDictionary(PdfName.Info);
        Assert.assertEquals("Author", author, info.get(PdfName.Author).toString());
        Assert.assertEquals("Creator", creator, info.get(PdfName.Creator).toString());
        Assert.assertEquals("Title", title, info.get(PdfName.Title).toString());
        Assert.assertEquals("Page count", pageCount, pdfDocument.getNumberOfPages());
        for (int i = 1; i <= pageCount; i++) {
            PdfDictionary page = pdfDocument.getPage(i).getPdfObject();
            Assert.assertEquals(PdfName.Page, page.get(PdfName.Type));
        }
        pdfDocument.close();
    }

    @Test
    public void create197PagesDocumentWithFullCompression() throws IOException {
        int pageCount = 197;
        String filename = destinationFolder + pageCount + "PagesDocumentWithFullCompression.pdf";

        final String author = "Alexander Chingarev";
        final String creator = "iText 6";
        final String title = "Empty iText 6 Document";

        PdfWriter writer = new PdfWriter(filename, new WriterProperties().setFullCompressionMode(true));
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getDocumentInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);
        for (int i = 0; i < pageCount; i++) {
            PdfPage page = pdfDoc.addNewPage();
            PdfCanvas canvas = new PdfCanvas(page);
            canvas
                    .saveState()
                    .beginText()
                    .moveText(36, 700)
                    .setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 72)
                    .showText(Integer.toString(i + 1))
                    .endText()
                    .restoreState();
            canvas.rectangle(100, 500, 100, 100).fill();
            canvas.release();
            page.flush();
        }
        pdfDoc.close();

        PdfReader reader = new PdfReader(filename);
        PdfDocument pdfDocument = new PdfDocument(reader);
        Assert.assertEquals("Rebuilt", false, reader.hasRebuiltXref());
        PdfDictionary info = pdfDocument.getTrailer().getAsDictionary(PdfName.Info);
        Assert.assertEquals("Author", author, info.get(PdfName.Author).toString());
        Assert.assertEquals("Creator", creator, info.get(PdfName.Creator).toString());
        Assert.assertEquals("Title", title, info.get(PdfName.Title).toString());
        Assert.assertEquals("Page count", pageCount, pdfDocument.getNumberOfPages());
        for (int i = 1; i <= pageCount; i++) {
            PdfDictionary page = pdfDocument.getPage(i).getPdfObject();
            Assert.assertEquals(PdfName.Page, page.get(PdfName.Type));
        }
        pdfDocument.close();
    }

    @Test
    public void create10PagesDocumentWithFullCompression() throws IOException {
        int pageCount = 10;
        String filename = destinationFolder + pageCount + "PagesDocumentWithFullCompression.pdf";

        final String author = "Alexander Chingarev";
        final String creator = "iText 6";
        final String title = "Empty iText 6 Document";

        PdfWriter writer = new PdfWriter(filename, new WriterProperties().setFullCompressionMode(true));
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getDocumentInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);
        for (int i = 0; i < pageCount; i++) {
            PdfPage page = pdfDoc.addNewPage();
            PdfCanvas canvas = new PdfCanvas(page);
            canvas
                    .saveState()
                    .beginText()
                    .moveText(36, 700)
                    .setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 72)
                    .showText(Integer.toString(i + 1))
                    .endText()
                    .restoreState();
            canvas.rectangle(100, 500, 100, 100).fill();
            canvas.release();
            page.flush();
        }
        pdfDoc.close();

        PdfReader reader = new PdfReader(filename);
        PdfDocument pdfDocument = new PdfDocument(reader);
        Assert.assertEquals("Rebuilt", false, reader.hasRebuiltXref());
        PdfDictionary info = pdfDocument.getTrailer().getAsDictionary(PdfName.Info);
        Assert.assertEquals("Author", author, info.get(PdfName.Author).toString());
        Assert.assertEquals("Creator", creator, info.get(PdfName.Creator).toString());
        Assert.assertEquals("Title", title, info.get(PdfName.Title).toString());
        Assert.assertEquals("Page count", pageCount, pdfDocument.getNumberOfPages());
        for (int i = 1; i <= pageCount; i++) {
            PdfDictionary page = pdfDocument.getPage(i).getPdfObject();
            Assert.assertEquals(PdfName.Page, page.get(PdfName.Type));
        }
        pdfDocument.close();
    }

    @Test
    public void copyPagesTest1() throws IOException, InterruptedException {
        String file1 = destinationFolder + "copyPages1_1.pdf";
        String file2 = destinationFolder + "copyPages1_2.pdf";

        PdfDocument pdfDoc1 = new PdfDocument(new PdfWriter(file1));

        PdfPage page1 = pdfDoc1.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page1);
        canvas.rectangle(100, 600, 100, 100);
        canvas.fill();
        canvas.beginText();
        canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.COURIER), 12);
        canvas.setTextMatrix(1, 0, 0, 1, 100, 500);
        canvas.showText("Hello World!");
        canvas.endText();
        canvas.release();

        page1.flush();
        pdfDoc1.close();

        pdfDoc1 = new PdfDocument(new PdfReader(file1));
        page1 = pdfDoc1.getPage(1);

        PdfDocument pdfDoc2 = new PdfDocument(new PdfWriter(file2));
        PdfPage page2 = page1.copyTo(pdfDoc2);
        pdfDoc2.addPage(page2);

        page2.flush();
        pdfDoc2.close();

        PdfReader reader = new PdfReader(file2);

        PdfDocument pdfDocument = new PdfDocument(reader);

        for (int i = 1; i <= pdfDocument.getNumberOfPages(); i++) {
            PdfDictionary page = pdfDocument.getPage(i).getPdfObject();
            Assert.assertEquals(PdfName.Page, page.get(PdfName.Type));
        }
        reader.close();
        Assert.assertEquals("Rebuilt", false, reader.hasRebuiltXref());
        PdfDictionary page = pdfDocument.getPage(1).getPdfObject();
        Assert.assertNotNull(page.get(PdfName.Parent));
        pdfDocument.close();
        Assert.assertNull(new CompareTool().compareByContent(file1, file2, destinationFolder, "diff_"));
    }

    @Test
    public void copyPagesTest2() throws IOException {
        String file1 = destinationFolder + "copyPages2_1.pdf";
        String file2 = destinationFolder + "copyPages2_2.pdf";

        PdfDocument pdfDoc1 = new PdfDocument(new PdfWriter(file1));

        for (int i = 0; i < 10; i++) {
            PdfPage page1 = pdfDoc1.addNewPage();
            PdfCanvas canvas = new PdfCanvas(page1);
            canvas.rectangle(100, 600, 100, 100);
            canvas.fill();
            canvas.beginText();
            canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.COURIER), 12);
            canvas.setTextMatrix(1, 0, 0, 1, 100, 500);
            canvas.showText(MessageFormatUtil.format("Page_{0}", i + 1));
            canvas.endText();
            canvas.release();
            page1.flush();
        }
        pdfDoc1.close();

        pdfDoc1 = new PdfDocument(new PdfReader(file1));

        PdfDocument pdfDoc2 = new PdfDocument(new PdfWriter(file2));
        for (int i = 9; i >= 0; i--) {
            PdfPage page2 = pdfDoc1.getPage(i + 1).copyTo(pdfDoc2);
            pdfDoc2.addPage(page2);
        }

        pdfDoc1.close();
        pdfDoc2.close();

        PdfReader reader = new PdfReader(file2);
        PdfDocument pdfDocument = new PdfDocument(reader);
        Assert.assertEquals("Rebuilt", false, reader.hasRebuiltXref());
        PdfDictionary page = pdfDocument.getPage(1).getPdfObject();
        Assert.assertNotNull(page.get(PdfName.Parent));
        pdfDocument.close();

        CompareTool cmpTool = new CompareTool();
        PdfDocument doc1 = new PdfDocument(new PdfReader(file1));
        PdfDocument doc2 = new PdfDocument(new PdfReader(file2));

        for (int i = 0; i < 10; i++) {
            PdfDictionary page1 = doc1.getPage(i + 1).getPdfObject();
            PdfDictionary page2 = doc2.getPage(10 - i).getPdfObject();
            Assert.assertTrue(cmpTool.compareDictionaries(page1, page2));
        }

        doc1.close();
        doc2.close();
    }

    @Test
    public void copyPagesTest3() throws IOException {
        String file1 = destinationFolder + "copyPages3_1.pdf";
        String file2 = destinationFolder + "copyPages3_2.pdf";

        PdfDocument pdfDoc1 = new PdfDocument(new PdfWriter(file1));

        PdfPage page1 = pdfDoc1.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page1);
        canvas.rectangle(100, 600, 100, 100);
        canvas.fill();
        canvas.beginText();
        canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.COURIER), 12);
        canvas.setTextMatrix(1, 0, 0, 1, 100, 500);
        canvas.showText("Hello World!!!");
        canvas.endText();
        canvas.release();

        page1.flush();
        pdfDoc1.close();

        pdfDoc1 = new PdfDocument(new PdfReader(file1));
        page1 = pdfDoc1.getPage(1);

        PdfDocument pdfDoc2 = new PdfDocument(new PdfWriter(file2));
        for (int i = 0; i < 10; i++) {
            PdfPage page2 = page1.copyTo(pdfDoc2);
            pdfDoc2.addPage(page2);
            if (i % 2 == 0)
                page2.flush();
        }

        pdfDoc1.close();
        pdfDoc2.close();


        CompareTool cmpTool = new CompareTool();
        PdfReader reader1 = new PdfReader(file1);
        PdfDocument doc1 = new PdfDocument(reader1);
        Assert.assertEquals("Rebuilt", false, reader1.hasRebuiltXref());
        PdfDictionary p1 = doc1.getPage(1).getPdfObject();
        PdfReader reader2 = new PdfReader(file2);
        PdfDocument doc2 = new PdfDocument(reader2);
        Assert.assertEquals("Rebuilt", false, reader2.hasRebuiltXref());
        for (int i = 0; i < 10; i++) {
            PdfDictionary p2 = doc2.getPage(i + 1).getPdfObject();
            Assert.assertTrue(cmpTool.compareDictionaries(p1, p2));
        }
        doc1.close();
        doc2.close();
    }

    @Test
    public void copyPagesTest4() throws IOException {
        String file1 = destinationFolder + "copyPages4_1.pdf";
        PdfDocument pdfDoc1 = new PdfDocument(new PdfWriter(file1));

        for (int i = 0; i < 5; i++) {
            PdfPage page1 = pdfDoc1.addNewPage();
            PdfCanvas canvas = new PdfCanvas(page1);
            canvas.rectangle(100, 600, 100, 100);
            canvas.fill();
            canvas.beginText();
            canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.COURIER), 12);
            canvas.setTextMatrix(1, 0, 0, 1, 100, 500);
            canvas.showText(MessageFormatUtil.format("Page_{0}", i + 1));
            canvas.endText();
            canvas.release();
        }

        pdfDoc1.close();
        pdfDoc1 = new PdfDocument(new PdfReader(file1));

        for (int i = 0; i < 5; i++) {
            PdfDocument pdfDoc2 = new PdfDocument(new PdfWriter(destinationFolder + MessageFormatUtil.format("copyPages4_{0}.pdf", i + 2)));
            PdfPage page2 = pdfDoc1.getPage(i + 1).copyTo(pdfDoc2);
            pdfDoc2.addPage(page2);
            pdfDoc2.close();
        }

        pdfDoc1.close();


        CompareTool cmpTool = new CompareTool();
        PdfReader reader1 = new PdfReader(file1);
        PdfDocument doc1 = new PdfDocument(reader1);
        Assert.assertEquals("Rebuilt", false, reader1.hasRebuiltXref());

        for (int i = 0; i < 5; i++) {
            PdfDictionary page1 = doc1.getPage(i + 1).getPdfObject();
            PdfDocument doc2 = new PdfDocument(new PdfReader(destinationFolder + MessageFormatUtil.format("copyPages4_{0}.pdf", i + 2)));
            PdfDictionary page = doc2.getPage(1).getPdfObject();
            Assert.assertTrue(cmpTool.compareDictionaries(page1, page));
            doc2.close();
        }

        doc1.close();
    }


    @Test
    public void copyPagesTest5() throws IOException {

        int documentCount = 3;

        for (int i = 0; i < documentCount; i++) {
            PdfDocument pdfDoc1 = new PdfDocument(new PdfWriter(destinationFolder + MessageFormatUtil.format("copyPages5_{0}.pdf", i + 1)));
            PdfPage page1 = pdfDoc1.addNewPage();
            PdfCanvas canvas = new PdfCanvas(page1);
            canvas.rectangle(100, 600, 100, 100);
            canvas.fill();
            canvas.beginText();
            canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.COURIER), 12);
            canvas.setTextMatrix(1, 0, 0, 1, 100, 500);
            canvas.showText(MessageFormatUtil.format("Page_{0}", i + 1));
            canvas.endText();
            canvas.release();
            pdfDoc1.close();
        }

        List<PdfDocument> docs = new ArrayList<PdfDocument>();
        for (int i = 0; i < documentCount; i++) {
            PdfDocument pdfDoc1 = new PdfDocument(new PdfReader(destinationFolder + MessageFormatUtil.format("copyPages5_{0}.pdf", i + 1)));
            docs.add(pdfDoc1);
        }

        PdfDocument pdfDoc2 = new PdfDocument(new PdfWriter(destinationFolder + "copyPages5_4.pdf"));
        for (int i = 0; i < 3; i++) {
            pdfDoc2.addPage(docs.get(i).getPage(1).copyTo(pdfDoc2));
        }

        pdfDoc2.close();
        for (PdfDocument doc : docs)
            doc.close();

        CompareTool cmpTool = new CompareTool();
        for (int i = 0; i < 3; i++) {
            PdfReader reader1 = new PdfReader(destinationFolder + MessageFormatUtil.format("copyPages5_{0}.pdf", i + 1));
            PdfDocument doc1 = new PdfDocument(reader1);
            Assert.assertEquals("Rebuilt", false, reader1.hasRebuiltXref());
            PdfReader reader2 = new PdfReader(destinationFolder + "copyPages5_4.pdf");
            PdfDocument doc2 = new PdfDocument(reader2);
            Assert.assertEquals("Rebuilt", false, reader2.hasRebuiltXref());
            PdfDictionary page1 = doc1.getPage(1).getPdfObject();
            PdfDictionary page2 = doc2.getPage(i + 1).getPdfObject();
            Assert.assertTrue(cmpTool.compareDictionaries(page1, page2));
            doc1.close();
            doc2.close();
        }

    }

    @Test
    public void copyPagesTest6() throws IOException {
        String file1 = destinationFolder + "copyPages6_1.pdf";
        String file2 = destinationFolder + "copyPages6_2.pdf";
        String file3 = destinationFolder + "copyPages6_3.pdf";
        String file1_upd = destinationFolder + "copyPages6_1_upd.pdf";

        PdfDocument pdfDoc1 = new PdfDocument(new PdfWriter(file1));
        PdfPage page1 = pdfDoc1.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page1);
        canvas.rectangle(100, 600, 100, 100);
        canvas.fill();
        canvas.beginText();
        canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.COURIER), 12);
        canvas.setTextMatrix(1, 0, 0, 1, 100, 500);
        canvas.showText("Hello World!");
        canvas.endText();
        canvas.release();

        pdfDoc1.close();
        pdfDoc1 = new PdfDocument(new PdfReader(file1));

        PdfDocument pdfDoc2 = new PdfDocument(new PdfWriter(file2));
        pdfDoc2.addPage(pdfDoc1.getPage(1).copyTo(pdfDoc2));

        pdfDoc2.close();
        pdfDoc2 = new PdfDocument(new PdfReader(file2));

        PdfDocument pdfDoc3 = new PdfDocument(new PdfWriter(file3));
        pdfDoc3.addPage(pdfDoc2.getPage(1).copyTo(pdfDoc3));

        pdfDoc3.close();
        pdfDoc3 = new PdfDocument(new PdfReader(file3));

        pdfDoc1.close();
        pdfDoc1 = new PdfDocument(new PdfReader(file1),
                new PdfWriter(file1_upd));

        pdfDoc1.addPage(pdfDoc3.getPage(1).copyTo(pdfDoc1));

        pdfDoc1.close();
        pdfDoc2.close();
        pdfDoc3.close();


        CompareTool cmpTool = new CompareTool();
        for (int i = 0; i < 3; i++) {
            PdfReader reader1 = new PdfReader(file1);
            PdfDocument doc1 = new PdfDocument(reader1);
            Assert.assertEquals("Rebuilt", false, reader1.hasRebuiltXref());
            PdfReader reader2 = new PdfReader(file2);
            PdfDocument doc2 = new PdfDocument(reader2);
            Assert.assertEquals("Rebuilt", false, reader2.hasRebuiltXref());
            PdfReader reader3 = new PdfReader(file3);
            PdfDocument doc3 = new PdfDocument(reader3);
            Assert.assertEquals("Rebuilt", false, reader3.hasRebuiltXref());
            PdfReader reader4 = new PdfReader(file1_upd);
            PdfDocument doc4 = new PdfDocument(reader4);
            Assert.assertEquals("Rebuilt", false, reader4.hasRebuiltXref());
            Assert.assertTrue(cmpTool.compareDictionaries(doc1.getPage(1).getPdfObject(), doc4.getPage(2).getPdfObject()));
            Assert.assertTrue(cmpTool.compareDictionaries(doc4.getPage(2).getPdfObject(), doc2.getPage(1).getPdfObject()));
            Assert.assertTrue(cmpTool.compareDictionaries(doc2.getPage(1).getPdfObject(), doc4.getPage(1).getPdfObject()));
            doc1.close();
            doc2.close();
            doc3.close();
            doc4.close();
        }

    }

    @Test
    public void markedContentTest1() {
        String message = "";
        PdfDocument document = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.beginMarkedContent(new com.itextpdf.kernel.pdf.PdfName("Tag1"));
        canvas.endMarkedContent();
        try {
            canvas.endMarkedContent();
        } catch (PdfException e) {
            message = e.getMessage();
        }
        canvas.release();
        document.close();
        Assert.assertEquals(PdfException.UnbalancedBeginEndMarkedContentOperators, message);
    }

    @Test
    public void markedContentTest2() throws Exception {
        PdfDocument document = new PdfDocument(new PdfWriter(destinationFolder + "markedContentTest2.pdf"));
        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);

        HashMap<com.itextpdf.kernel.pdf.PdfName, PdfObject> tmpMap = new HashMap<com.itextpdf.kernel.pdf.PdfName, PdfObject>();
        tmpMap.put(new com.itextpdf.kernel.pdf.PdfName("Tag"), new PdfNumber(2));
        com.itextpdf.kernel.pdf.PdfDictionary tag2 = new com.itextpdf.kernel.pdf.PdfDictionary(tmpMap);
        tmpMap = new HashMap<com.itextpdf.kernel.pdf.PdfName, PdfObject>();
        tmpMap.put(new com.itextpdf.kernel.pdf.PdfName("Tag"), new PdfNumber(3).makeIndirect(document));
        com.itextpdf.kernel.pdf.PdfDictionary tag3 = new com.itextpdf.kernel.pdf.PdfDictionary(tmpMap);

        canvas.beginMarkedContent(new com.itextpdf.kernel.pdf.PdfName("Tag1")).endMarkedContent().
                beginMarkedContent(new com.itextpdf.kernel.pdf.PdfName("Tag2"), tag2).endMarkedContent().
                beginMarkedContent(new com.itextpdf.kernel.pdf.PdfName("Tag3"), (com.itextpdf.kernel.pdf.PdfDictionary) tag3.makeIndirect(document)).endMarkedContent();

        canvas.release();
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "markedContentTest2.pdf", sourceFolder + "cmp_markedContentTest2.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void graphicsStateTest1() {
        PdfDocument document = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.setLineWidth(3);
        canvas.saveState();
        canvas.setLineWidth(5);
        Assert.assertEquals(5, canvas.getGraphicsState().getLineWidth(), 0);
        canvas.restoreState();
        Assert.assertEquals(3, canvas.getGraphicsState().getLineWidth(), 0);
        PdfExtGState egs = new PdfExtGState();
        egs.getPdfObject().put(com.itextpdf.kernel.pdf.PdfName.LW, new PdfNumber(2));
        canvas.setExtGState(egs);
        Assert.assertEquals(2, canvas.getGraphicsState().getLineWidth(), 0);
        canvas.release();
        document.close();
    }

    @Test
    public void wmfImageTest01() throws IOException, InterruptedException {
        PdfDocument document = new PdfDocument(new PdfWriter(destinationFolder + "wmfImageTest01.pdf"));
        PdfPage page = document.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page);
        ImageData img = new WmfImageData(sourceFolder + "example.wmf");
        canvas.addImage(img, 0, 0, 0.1f, false);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "wmfImageTest01.pdf", sourceFolder + "cmp_wmfImageTest01.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void wmfImageTest02() throws IOException, InterruptedException {
        PdfDocument document = new PdfDocument(new PdfWriter(destinationFolder + "wmfImageTest02.pdf"));
        PdfPage page = document.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page);
        ImageData img = new WmfImageData(sourceFolder + "butterfly.wmf");
        canvas.addImage(img, 0, 0, 1, false);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "wmfImageTest02.pdf", sourceFolder + "cmp_wmfImageTest02.pdf", destinationFolder, "diff_"));
    }


    @Test
    public void wmfImageTest03() throws IOException, InterruptedException {
        PdfDocument document = new PdfDocument(new PdfWriter(destinationFolder + "wmfImageTest03.pdf"));
        PdfPage page = document.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page);
        ImageData img = new WmfImageData(sourceFolder + "type1.wmf");
        canvas.addImage(img, 0, 0, 1, false);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "wmfImageTest03.pdf", sourceFolder + "cmp_wmfImageTest03.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void wmfImageTest04() throws IOException, InterruptedException {
        PdfDocument document = new PdfDocument(new PdfWriter(destinationFolder + "wmfImageTest04.pdf"));
        PdfPage page = document.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page);
        ImageData img = new WmfImageData(sourceFolder + "type0.wmf");
        canvas.addImage(img, 0, 0, 1, false);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "wmfImageTest04.pdf", sourceFolder + "cmp_wmfImageTest04.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void wmfImageTest05() throws IOException, InterruptedException {
        PdfDocument document = new PdfDocument(new PdfWriter(destinationFolder + "wmfImageTest05.pdf"));
        PdfPage page = document.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page);
        InputStream stream = UrlUtil.openStream(UrlUtil.toURL(sourceFolder + "example2.wmf"));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        StreamUtil.transferBytes(stream, baos);
        ImageData img = new WmfImageData(baos.toByteArray());
        canvas.addImage(img, 0, 0, 1, false);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "wmfImageTest05.pdf", sourceFolder + "cmp_wmfImageTest05.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void gifImageTest01() throws IOException, InterruptedException {
        PdfDocument document = new PdfDocument(new PdfWriter(destinationFolder + "gifImageTest01.pdf"));
        PdfPage page = document.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page);
        ImageData img = ImageDataFactory.create(sourceFolder + "2-frames.gif");
        canvas.addImage(img, 100, 100, 200, false);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "gifImageTest01.pdf", sourceFolder + "cmp_gifImageTest01.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void gifImageTest02() throws IOException, InterruptedException {
        PdfDocument document = new PdfDocument(new PdfWriter(destinationFolder + "gifImageTest02.pdf"));
        PdfPage page = document.addNewPage();

        InputStream is = new FileInputStream(sourceFolder + "2-frames.gif");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int reads = is.read();
        while (reads != -1) {
            baos.write(reads);
            reads = is.read();
        }

        PdfCanvas canvas = new PdfCanvas(page);
        ImageData img = ImageDataFactory.createGifFrame(baos.toByteArray(), 1);
        canvas.addImage(img, 100, 100, 200, false);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "gifImageTest02.pdf", sourceFolder + "cmp_gifImageTest02.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void gifImageTest03() throws IOException, InterruptedException {
        PdfDocument document = new PdfDocument(new PdfWriter(destinationFolder + "gifImageTest03.pdf"));
        PdfPage page = document.addNewPage();

        InputStream is = new FileInputStream(sourceFolder + "2-frames.gif");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int reads = is.read();
        while (reads != -1) {
            baos.write(reads);
            reads = is.read();
        }

        PdfCanvas canvas = new PdfCanvas(page);
        ImageData img = ImageDataFactory.createGifFrame(baos.toByteArray(), 2);
        canvas.addImage(img, 100, 100, 200, false);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "gifImageTest03.pdf", sourceFolder + "cmp_gifImageTest03.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void gifImageTest04() throws IOException {
        PdfDocument document = new PdfDocument(new PdfWriter(destinationFolder + "gifImageTest04.pdf"));
        PdfPage page = document.addNewPage();

        InputStream is = new FileInputStream(sourceFolder + "2-frames.gif");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int reads = is.read();
        while (reads != -1) {
            baos.write(reads);
            reads = is.read();
        }

        PdfCanvas canvas = new PdfCanvas(page);
        try {
            ImageDataFactory.createGifFrame(baos.toByteArray(), 3);
            Assert.fail("IOException expected");
        } catch (com.itextpdf.io.IOException ignored) {

        }
    }

    @Test
    public void gifImageTest05() throws IOException, InterruptedException {
        PdfDocument document = new PdfDocument(new PdfWriter(destinationFolder + "gifImageTest05.pdf"));
        PdfPage page = document.addNewPage();

        InputStream is = new FileInputStream(sourceFolder + "animated_fox_dog.gif");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int reads = is.read();
        while (reads != -1) {
            baos.write(reads);
            reads = is.read();
        }

        PdfCanvas canvas = new PdfCanvas(page);
        List<ImageData> frames = ImageDataFactory.createGifFrames(baos.toByteArray(), new int[]{1, 2, 5});
        float y = 600;
        for (ImageData img : frames) {
            canvas.addImage(img, 100, y, 200, false);
            y -= 200;
        }

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "gifImageTest05.pdf", sourceFolder + "cmp_gifImageTest05.pdf", destinationFolder, "diff_"));
    }

//    @Test
//    public void kernedTextTest01() throws IOException, InterruptedException {
//        FileOutputStream fos = new FileOutputStream(destinationFolder + "kernedTextTest01.pdf");
//        PdfWriter writer = new PdfWriter(fos);
//        PdfDocument document = new PdfDocument(writer);
//        PdfPage page = document.addNewPage();
//
//        PdfCanvas canvas = new PdfCanvas(page);
//        String kernableText = "AVAVAVAVAVAVAVAVAVAVAVAVAVAVAVAVAVAVAVAVAVAVAVAVAVAVAVAVAVAV";
//        PdfFont font = PdfFont.createFont(document, StandardFonts.HELVETICA);
//        canvas.beginText().moveText(50, 600).setFontAndSize(font, 12).showText("Kerning:-" + kernableText).endText();
//        canvas.beginText().moveText(50, 650).setFontAndSize(font, 12).showTextKerned("Kerning:+" + kernableText).endText();
//
//        document.close();
//
//        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "kernedTextTest01.pdf", sourceFolder + "cmp_kernedTextTest01.pdf", destinationFolder, "diff_"));
//    }

    /*@Test
    public void ccittImageTest01() throws IOException, InterruptedException {
        String filename = "ccittImage01.pdf";
        PdfWriter writer = new PdfWriter(destinationFolder + filename);
        PdfDocument document = new PdfDocument(writer);

        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);

        String text = "Call me Ishmael. Some years ago--never mind how long "
                + "precisely --having little or no money in my purse, and nothing "
                + "particular to interest me on shore, I thought I would sail about "
                + "a little and see the watery part of the world.";

        BarcodePDF417 barcode = new BarcodePDF417();
        barcode.setText(text);
        barcode.paintCode();

        byte g4[] = CCITTG4Encoder.compress(barcode.getOutBits(), barcode.getBitColumns(), barcode.getCodeRows());
        RawImage img = (RawImage) ImageDataFactory.create(barcode.getBitColumns(), barcode.getCodeRows(), false, RawImage.CCITTG4, 0, g4, null);
        img.setTypeCcitt(RawImage.CCITTG4);
        canvas.addImage(img, 100, 100, false);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }*/

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.IMAGE_HAS_JBIG2DECODE_FILTER),
            @LogMessage(messageTemplate = LogMessageConstant.IMAGE_HAS_JPXDECODE_FILTER),
            @LogMessage(messageTemplate = LogMessageConstant.IMAGE_HAS_MASK),
            @LogMessage(messageTemplate = LogMessageConstant.IMAGE_SIZE_CANNOT_BE_MORE_4KB)
    })
    public void inlineImagesTest01() throws IOException, InterruptedException {
        String filename = "inlineImages01.pdf";
        PdfDocument document = new PdfDocument(new PdfWriter(destinationFolder + filename));

        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);

        canvas.addImage(ImageDataFactory.create(sourceFolder + "Desert.jpg"), 36, 700, 100, true);
        canvas.addImage(ImageDataFactory.create(sourceFolder + "bulb.gif"), 36, 600, 100, true);
        canvas.addImage(ImageDataFactory.create(sourceFolder + "smpl.bmp"), 36, 500, 100, true);
        canvas.addImage(ImageDataFactory.create(sourceFolder + "itext.png"), 36, 460, 100, true);
        canvas.addImage(ImageDataFactory.create(sourceFolder + "0047478.jpg"), 36, 300, 100, true);
        canvas.addImage(ImageDataFactory.create(sourceFolder + "map.jp2"), 36, 200, 100, true);
        canvas.addImage(ImageDataFactory.create(sourceFolder + "amb.jb2"), 36, 30, 100, true);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.IMAGE_HAS_JBIG2DECODE_FILTER),
            @LogMessage(messageTemplate = LogMessageConstant.IMAGE_HAS_JPXDECODE_FILTER),
            @LogMessage(messageTemplate = LogMessageConstant.IMAGE_HAS_MASK),
            @LogMessage(messageTemplate = LogMessageConstant.IMAGE_SIZE_CANNOT_BE_MORE_4KB)
    })
    public void inlineImagesTest02() throws IOException, InterruptedException {
        String filename = "inlineImages02.pdf";
        PdfDocument document = new PdfDocument(new PdfWriter(destinationFolder + filename));

        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);

        InputStream stream = UrlUtil.openStream(UrlUtil.toURL(sourceFolder + "Desert.jpg"));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        StreamUtil.transferBytes(stream, baos);
        canvas.addImage(ImageDataFactory.create(baos.toByteArray()), 36, 700, 100, true);
        stream = UrlUtil.openStream(UrlUtil.toURL(sourceFolder + "bulb.gif"));
        baos = new ByteArrayOutputStream();
        StreamUtil.transferBytes(stream, baos);
        canvas.addImage(ImageDataFactory.create(baos.toByteArray()), 36, 600, 100, true);
        stream = UrlUtil.openStream(UrlUtil.toURL(sourceFolder + "smpl.bmp"));
        baos = new ByteArrayOutputStream();
        StreamUtil.transferBytes(stream, baos);
        canvas.addImage(ImageDataFactory.create(baos.toByteArray()), 36, 500, 100, true);
        stream = UrlUtil.openStream(UrlUtil.toURL(sourceFolder + "itext.png"));
        baos = new ByteArrayOutputStream();
        StreamUtil.transferBytes(stream, baos);
        canvas.addImage(ImageDataFactory.create(baos.toByteArray()), 36, 460, 100, true);
        stream = UrlUtil.openStream(UrlUtil.toURL(sourceFolder + "0047478.jpg"));
        baos = new ByteArrayOutputStream();
        StreamUtil.transferBytes(stream, baos);
        canvas.addImage(ImageDataFactory.create(baos.toByteArray()), 36, 300, 100, true);
        stream = UrlUtil.openStream(UrlUtil.toURL(sourceFolder + "map.jp2"));
        baos = new ByteArrayOutputStream();
        StreamUtil.transferBytes(stream, baos);
        canvas.addImage(ImageDataFactory.create(baos.toByteArray()), 36, 200, 100, true);
        stream = UrlUtil.openStream(UrlUtil.toURL(sourceFolder + "amb.jb2"));
        baos = new ByteArrayOutputStream();
        StreamUtil.transferBytes(stream, baos);
        canvas.addImage(ImageDataFactory.create(baos.toByteArray()), 36, 30, 100, true);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void inlineImagesTest03() throws IOException, InterruptedException {
        String filename = "inlineImages03.pdf";
        PdfDocument document = new PdfDocument(new PdfWriter(destinationFolder + filename,
                new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0))
                .setCompressionLevel(CompressionConstants.NO_COMPRESSION));

        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);

        canvas.addImage(ImageDataFactory.create(sourceFolder + "bulb.gif"), 36, 600, 100, true);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void awtImagesTest01() throws IOException, InterruptedException {
        String filename = "awtImagesTest01.pdf";
        PdfDocument document = new PdfDocument(new PdfWriter(destinationFolder + filename));

        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);

        int x = 36;
        int y = 700;
        int width = 100;
        for (String image : RESOURCES) {
            java.awt.Image awtImage = Toolkit.getDefaultToolkit().createImage(sourceFolder + image);
            canvas.addImage(ImageDataFactory.create(awtImage, null), x, y, width, false);
            y -= 150;
        }

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void canvasInitializationPageNoContentsKey() throws IOException, InterruptedException {
        String srcFile = sourceFolder + "pageNoContents.pdf";
        String cmpFile = sourceFolder + "cmp_pageNoContentsStamp.pdf";
        String destFile = destinationFolder + "pageNoContentsStamp.pdf";

        PdfDocument document = new PdfDocument(new PdfReader(srcFile), new PdfWriter(destFile));

        PdfCanvas canvas = new PdfCanvas(document.getPage(1));
        canvas.setLineWidth(5).rectangle(50, 680, 300, 50).stroke();
        canvas.release();

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destFile, cmpFile, destinationFolder, "diff_"));
    }

    @Test
    public void canvasInitializationStampingExistingStream() throws IOException, InterruptedException {
        String srcFile = sourceFolder + "pageWithContent.pdf";
        String cmpFile = sourceFolder + "cmp_stampingExistingStream.pdf";
        String destFile = destinationFolder + "stampingExistingStream.pdf";

        PdfDocument document = new PdfDocument(new PdfReader(srcFile), new PdfWriter(destFile));

        PdfPage page = document.getPage(1);
        PdfCanvas canvas = new PdfCanvas(page.getLastContentStream(), page.getResources(), page.getDocument());
        canvas.setLineWidth(5).rectangle(50, 680, 300, 50).stroke();
        canvas.release();

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destFile, cmpFile, destinationFolder, "diff_"));
    }

    @Test
    public void canvasStampingJustCopiedStreamWithCompression() throws IOException, InterruptedException {
        String srcFile = sourceFolder + "pageWithContent.pdf";
        String cmpFile = sourceFolder + "cmp_stampingJustCopiedStreamWithCompression.pdf";
        String destFile = destinationFolder + "stampingJustCopiedStreamWithCompression.pdf";

        PdfDocument srcDocument = new PdfDocument(new PdfReader(srcFile));
        PdfDocument document = new PdfDocument(new PdfWriter(destFile));
        srcDocument.copyPagesTo(1, 1, document);
        srcDocument.close();

        PdfPage page = document.getPage(1);
        PdfCanvas canvas = new PdfCanvas(page.getLastContentStream(), page.getResources(), page.getDocument());
        canvas.setLineWidth(5).rectangle(50, 680, 300, 50).stroke();
        canvas.release();

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destFile, cmpFile, destinationFolder, "diff_"));
    }

    @Test
    public void canvasSmallFontSize01() throws IOException, InterruptedException {
        String cmpFile = sourceFolder + "cmp_canvasSmallFontSize01.pdf";
        String destFile = destinationFolder + "canvasSmallFontSize01.pdf";

        PdfDocument document = new PdfDocument(new PdfWriter(destFile));

        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.saveState()
                .beginText()
                .moveText(50, 750)
                .setFontAndSize(PdfFontFactory.createFont(), 0)
                .showText("simple text")
                .endText()
                .restoreState();

        canvas.saveState()
                .beginText()
                .moveText(50, 700)
                .setFontAndSize(PdfFontFactory.createFont(), -0.00005f)
                .showText("simple text")
                .endText()
                .restoreState();

        canvas.saveState()
                .beginText()
                .moveText(50, 650)
                .setFontAndSize(PdfFontFactory.createFont(), 0.00005f)
                .showText("simple text")
                .endText()
                .restoreState();

        canvas.saveState()
                .beginText()
                .moveText(50, 600)
                .setFontAndSize(PdfFontFactory.createFont(), -12)
                .showText("simple text")
                .endText()
                .restoreState();

        canvas.saveState()
                .beginText()
                .moveText(50, 550)
                .setFontAndSize(PdfFontFactory.createFont(), 12)
                .showText("simple text")
                .endText()
                .restoreState();

        canvas.release();

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destFile, cmpFile, destinationFolder, "diff_"));
    }

    @Test
    public void endPathNewPathTest() {
        ByteArrayOutputStream boasEndPath = new ByteArrayOutputStream();
        PdfDocument pdfDocEndPath = new PdfDocument(new PdfWriter(boasEndPath));
        pdfDocEndPath.addNewPage();

        PdfCanvas endPathCanvas = new PdfCanvas(pdfDocEndPath.getPage(1));
        endPathCanvas.endPath();

        ByteArrayOutputStream boasNewPath = new ByteArrayOutputStream();
        PdfDocument pdfDocNewPath = new PdfDocument(new PdfWriter(boasNewPath));
        pdfDocNewPath.addNewPage();
        PdfCanvas newPathCanvas = new PdfCanvas(pdfDocNewPath.getPage(1));
        newPathCanvas.newPath();
        Assert.assertArrayEquals(boasNewPath.toByteArray(), boasEndPath.toByteArray());
    }
}
