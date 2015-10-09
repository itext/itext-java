package com.itextpdf.canvas;

import com.itextpdf.basics.LogMessageConstant;
import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.Utilities;
import com.itextpdf.basics.codec.CCITTG4Encoder;
import com.itextpdf.basics.font.FontConstants;
import com.itextpdf.basics.image.Image;
import com.itextpdf.basics.image.ImageFactory;
import com.itextpdf.basics.image.RawImage;
import com.itextpdf.basics.io.ByteArrayOutputStream;
import com.itextpdf.canvas.color.CalGray;
import com.itextpdf.canvas.color.CalRgb;
import com.itextpdf.canvas.color.Color;
import com.itextpdf.canvas.color.DeviceCmyk;
import com.itextpdf.canvas.color.DeviceN;
import com.itextpdf.canvas.color.DeviceRgb;
import com.itextpdf.canvas.color.IccBased;
import com.itextpdf.canvas.color.Indexed;
import com.itextpdf.canvas.color.Lab;
import com.itextpdf.canvas.color.Separation;
import com.itextpdf.canvas.image.WmfImage;
import com.itextpdf.core.font.PdfFont;
import com.itextpdf.core.pdf.PdfArray;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfNumber;
import com.itextpdf.core.pdf.PdfObject;
import com.itextpdf.core.pdf.PdfPage;
import com.itextpdf.core.pdf.PdfReader;
import com.itextpdf.core.pdf.PdfResources;
import com.itextpdf.core.pdf.PdfString;
import com.itextpdf.core.pdf.PdfWriter;
import com.itextpdf.core.pdf.colorspace.PdfCieBasedCs;
import com.itextpdf.core.pdf.colorspace.PdfDeviceCs;
import com.itextpdf.core.pdf.colorspace.PdfSpecialCs;
import com.itextpdf.core.pdf.extgstate.PdfExtGState;
import com.itextpdf.core.testutils.CompareTool;
import com.itextpdf.core.testutils.annotations.type.IntegrationTest;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.BarcodePDF417;

import java.awt.Toolkit;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
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

    static final public String sourceFolder = "./src/test/resources/com/itextpdf/canvas/PdfCanvasTest/";
    static final public String destinationFolder = "./target/test/com/itextpdf/canvas/PdfCanvasTest/";

    /**
     * Paths to images.
     */
    public static final String[] RESOURCES = {
            "Desert.jpg",
            "bulb.gif",
            "0047478.jpg",
            "itext.png"

    };

    @BeforeClass
    static public void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void createSimpleCanvas() throws IOException {

        final String author = "Alexander Chingarev";
        final String creator = "iText 6";
        final String title = "Empty iText 6 Document";

        FileOutputStream fos = new FileOutputStream(destinationFolder + "simpleCanvas.pdf");
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);
        PdfPage page1 = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page1);
        canvas.rectangle(100, 100, 100, 100).fill();
        canvas.release();
        pdfDoc.close();

        com.itextpdf.text.pdf.PdfReader reader = new com.itextpdf.text.pdf.PdfReader(destinationFolder + "simpleCanvas.pdf");
        Assert.assertEquals("Rebuilt", false, reader.isRebuilt());
        HashMap<String, String> info = reader.getInfo();
        Assert.assertEquals("Author", author, info.get("Author"));
        Assert.assertEquals("Creator", creator, info.get("Creator"));
        Assert.assertEquals("Title", title, info.get("Title"));
        Assert.assertEquals("Page count", 1, reader.getNumberOfPages());
        com.itextpdf.text.pdf.PdfDictionary page = reader.getPageN(1);
        Assert.assertEquals(com.itextpdf.text.pdf.PdfName.PAGE, page.get(com.itextpdf.text.pdf.PdfName.TYPE));
        reader.close();
    }

    @Test
    public void createSimpleCanvasWithDrawing() throws IOException {

        final String fileName = "simpleCanvasWithDrawing.pdf";

        final String author = "Alexander Chingarev";
        final String creator = "iText 6";
        final String title = "Empty iText 6 Document";

        FileOutputStream fos = new FileOutputStream(destinationFolder + fileName);
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getInfo().setAuthor(author).
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

        com.itextpdf.text.pdf.PdfReader reader = new com.itextpdf.text.pdf.PdfReader(destinationFolder + fileName);
        Assert.assertEquals("Rebuilt", false, reader.isRebuilt());
        HashMap<String, String> info = reader.getInfo();
        Assert.assertEquals("Author", author, info.get("Author"));
        Assert.assertEquals("Creator", creator, info.get("Creator"));
        Assert.assertEquals("Title", title, info.get("Title"));
        Assert.assertEquals("Page count", 1, reader.getNumberOfPages());
        com.itextpdf.text.pdf.PdfDictionary page = reader.getPageN(1);
        Assert.assertEquals(com.itextpdf.text.pdf.PdfName.PAGE, page.get(com.itextpdf.text.pdf.PdfName.TYPE));
        reader.close();
    }

    @Test
    public void createSimpleCanvasWithText() throws IOException {

        final String fileName = "simpleCanvasWithText.pdf";

        final String author = "Alexander Chingarev";
        final String creator = "iText 6";
        final String title = "Empty iText 6 Document";

        FileOutputStream fos = new FileOutputStream(destinationFolder + fileName);
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);
        PdfPage page1 = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page1);
        //Initialize canvas and write text to it
        canvas
                .saveState()
                .beginText()
                .moveText(36, 750)
                .setFontAndSize(PdfFont.createStandardFont(pdfDoc, FontConstants.HELVETICA), 16)
                .showText("Hello Helvetica!")
                .endText()
                .restoreState();

        canvas
                .saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(PdfFont.createStandardFont(pdfDoc, FontConstants.HELVETICA_BOLDOBLIQUE), 16)
                .showText("Hello Helvetica Bold Oblique!")
                .endText()
                .restoreState();

        canvas
                .saveState()
                .beginText()
                .moveText(36, 650)
                .setFontAndSize(PdfFont.createStandardFont(pdfDoc, FontConstants.COURIER), 16)
                .showText("Hello Courier!")
                .endText()
                .restoreState();

        canvas
                .saveState()
                .beginText()
                .moveText(36, 600)
                .setFontAndSize(PdfFont.createStandardFont(pdfDoc, FontConstants.TIMES_ITALIC), 16)
                .showText("Hello Times Italic!")
                .endText()
                .restoreState();

        canvas
                .saveState()
                .beginText()
                .moveText(36, 550)
                .setFontAndSize(PdfFont.createStandardFont(pdfDoc, FontConstants.SYMBOL), 16)
                .showText("Hello Ellada!")
                .endText()
                .restoreState();

        canvas
                .saveState()
                .beginText()
                .moveText(36, 500)
                .setFontAndSize(PdfFont.createStandardFont(pdfDoc, FontConstants.ZAPFDINGBATS), 16)
                .showText("Hello ZapfDingbats!")
                .endText()
                .restoreState();
        canvas.release();
        pdfDoc.close();

        com.itextpdf.text.pdf.PdfReader reader = new com.itextpdf.text.pdf.PdfReader(destinationFolder + fileName);
        Assert.assertEquals("Rebuilt", false, reader.isRebuilt());
        HashMap<String, String> info = reader.getInfo();
        Assert.assertEquals("Author", author, info.get("Author"));
        Assert.assertEquals("Creator", creator, info.get("Creator"));
        Assert.assertEquals("Title", title, info.get("Title"));
        Assert.assertEquals("Page count", 1, reader.getNumberOfPages());
        com.itextpdf.text.pdf.PdfDictionary page = reader.getPageN(1);
        Assert.assertEquals(com.itextpdf.text.pdf.PdfName.PAGE, page.get(com.itextpdf.text.pdf.PdfName.TYPE));
        reader.close();
    }

    @Test
    public void createSimpleCanvasWithPageFlush() throws IOException {

        final String author = "Alexander Chingarev";
        final String creator = "iText 6";
        final String title = "Empty iText 6 Document";

        FileOutputStream fos = new FileOutputStream(destinationFolder + "simpleCanvasWithPageFlush.pdf");
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);
        PdfPage page1 = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page1);
        canvas.rectangle(100, 100, 100, 100).fill();
        canvas.release();
        page1.flush();
        pdfDoc.close();

        com.itextpdf.text.pdf.PdfReader reader = new com.itextpdf.text.pdf.PdfReader(destinationFolder + "simpleCanvasWithPageFlush.pdf");
        Assert.assertEquals("Rebuilt", false, reader.isRebuilt());
        HashMap<String, String> info = reader.getInfo();
        Assert.assertEquals("Author", author, info.get("Author"));
        Assert.assertEquals("Creator", creator, info.get("Creator"));
        Assert.assertEquals("Title", title, info.get("Title"));
        Assert.assertEquals("Page count", 1, reader.getNumberOfPages());
        com.itextpdf.text.pdf.PdfDictionary page = reader.getPageN(1);
        Assert.assertEquals(com.itextpdf.text.pdf.PdfName.PAGE, page.get(com.itextpdf.text.pdf.PdfName.TYPE));
        reader.close();
    }

    @Test
    public void createSimpleCanvasWithFullCompression() throws IOException {

        final String author = "Alexander Chingarev";
        final String creator = "iText 6";
        final String title = "Empty iText 6 Document";

        FileOutputStream fos = new FileOutputStream(destinationFolder + "simpleCanvasWithFullCompression.pdf");
        PdfWriter writer = new PdfWriter(fos);
        writer.setFullCompression(true);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);
        PdfPage page1 = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page1);
        canvas.rectangle(100, 100, 100, 100).fill();
        canvas.release();
        pdfDoc.close();

        com.itextpdf.text.pdf.PdfReader reader = new com.itextpdf.text.pdf.PdfReader(destinationFolder + "simpleCanvasWithFullCompression.pdf");
        Assert.assertEquals("Rebuilt", false, reader.isRebuilt());
        HashMap<String, String> info = reader.getInfo();
        Assert.assertEquals("Author", author, info.get("Author"));
        Assert.assertEquals("Creator", creator, info.get("Creator"));
        Assert.assertEquals("Title", title, info.get("Title"));
        Assert.assertEquals("Page count", 1, reader.getNumberOfPages());
        com.itextpdf.text.pdf.PdfDictionary page = reader.getPageN(1);
        Assert.assertEquals(com.itextpdf.text.pdf.PdfName.PAGE, page.get(com.itextpdf.text.pdf.PdfName.TYPE));
        reader.close();
    }

    @Test
    public void createSimpleCanvasWithPageFlushAndFullCompression() throws IOException {

        final String author = "Alexander Chingarev";
        final String creator = "iText 6";
        final String title = "Empty iText 6 Document";

        FileOutputStream fos = new FileOutputStream(destinationFolder + "simpleCanvasWithPageFlushAndFullCompression.pdf");
        PdfWriter writer = new PdfWriter(fos);
        writer.setFullCompression(true);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);
        PdfPage page1 = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page1);
        canvas.rectangle(100, 100, 100, 100).fill();
        canvas.release();
        page1.flush();
        pdfDoc.close();

        com.itextpdf.text.pdf.PdfReader reader = new com.itextpdf.text.pdf.PdfReader(destinationFolder + "simpleCanvasWithPageFlushAndFullCompression.pdf");
        Assert.assertEquals("Rebuilt", false, reader.isRebuilt());
        HashMap<String, String> info = reader.getInfo();
        Assert.assertEquals("Author", author, info.get("Author"));
        Assert.assertEquals("Creator", creator, info.get("Creator"));
        Assert.assertEquals("Title", title, info.get("Title"));
        Assert.assertEquals("Page count", 1, reader.getNumberOfPages());
        com.itextpdf.text.pdf.PdfDictionary page = reader.getPageN(1);
        Assert.assertEquals(com.itextpdf.text.pdf.PdfName.PAGE, page.get(com.itextpdf.text.pdf.PdfName.TYPE));
        reader.close();
    }

    @Test
    public void create1000PagesDocument() throws IOException {
        int pageCount = 1000;
        String filename = destinationFolder + pageCount + "PagesDocument.pdf";

        final String author = "Alexander Chingarev";
        final String creator = "iText 6";
        final String title = "Empty iText 6 Document";

        FileOutputStream fos = new FileOutputStream(filename);
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);
        for (int i = 0; i < pageCount; i++) {
            PdfPage page = pdfDoc.addNewPage();
            PdfCanvas canvas = new PdfCanvas(page);
            canvas
                    .saveState()
                    .beginText()
                    .moveText(36, 700)
                    .setFontAndSize(PdfFont.createStandardFont(pdfDoc, FontConstants.HELVETICA), 72)
                    .showText(Integer.toString(i + 1))
                    .endText()
                    .restoreState();
            canvas.rectangle(100, 500, 100, 100).fill();
            canvas.release();
            page.flush();
        }
        pdfDoc.close();

        com.itextpdf.text.pdf.PdfReader reader = new com.itextpdf.text.pdf.PdfReader(destinationFolder + "1000PagesDocument.pdf");
        Assert.assertEquals("Rebuilt", false, reader.isRebuilt());
        HashMap<String, String> info = reader.getInfo();
        Assert.assertEquals("Author", author, info.get("Author"));
        Assert.assertEquals("Creator", creator, info.get("Creator"));
        Assert.assertEquals("Title", title, info.get("Title"));
        Assert.assertEquals("Page count", pageCount, reader.getNumberOfPages());
        for (int i = 1; i <= pageCount; i++) {
            com.itextpdf.text.pdf.PdfDictionary page = reader.getPageN(i);
            Assert.assertEquals(com.itextpdf.text.pdf.PdfName.PAGE, page.get(com.itextpdf.text.pdf.PdfName.TYPE));
        }
        reader.close();
    }

    @Test
    public void create100PagesDocument() throws IOException {
        int pageCount = 100;
        String filename = destinationFolder + pageCount + "PagesDocument.pdf";

        final String author = "Alexander Chingarev";
        final String creator = "iText 6";
        final String title = "Empty iText 6 Document";

        FileOutputStream fos = new FileOutputStream(filename);
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);
        for (int i = 0; i < pageCount; i++) {
            PdfPage page = pdfDoc.addNewPage();
            PdfCanvas canvas = new PdfCanvas(page);
            canvas
                    .saveState()
                    .beginText()
                    .moveText(36, 700)
                    .setFontAndSize(PdfFont.createStandardFont(pdfDoc, FontConstants.HELVETICA), 72)
                    .showText(Integer.toString(i + 1))
                    .endText()
                    .restoreState();
            canvas.rectangle(100, 500, 100, 100).fill();
            canvas.release();
            page.flush();
        }
        pdfDoc.close();

        com.itextpdf.text.pdf.PdfReader reader = new com.itextpdf.text.pdf.PdfReader(filename);
        Assert.assertEquals("Rebuilt", false, reader.isRebuilt());
        HashMap<String, String> info = reader.getInfo();
        Assert.assertEquals("Author", author, info.get("Author"));
        Assert.assertEquals("Creator", creator, info.get("Creator"));
        Assert.assertEquals("Title", title, info.get("Title"));
        Assert.assertEquals("Page count", pageCount, reader.getNumberOfPages());
        for (int i = 1; i <= pageCount; i++) {
            com.itextpdf.text.pdf.PdfDictionary page = reader.getPageN(i);
            Assert.assertEquals(com.itextpdf.text.pdf.PdfName.PAGE, page.get(com.itextpdf.text.pdf.PdfName.TYPE));
        }
        reader.close();
    }

    @Test
    public void create10PagesDocument() throws IOException {
        int pageCount = 10;
        String filename = destinationFolder + pageCount + "PagesDocument.pdf";

        final String author = "Alexander Chingarev";
        final String creator = "iText 6";
        final String title = "Empty iText 6 Document";

        FileOutputStream fos = new FileOutputStream(filename);
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);
        for (int i = 0; i < pageCount; i++) {
            PdfPage page = pdfDoc.addNewPage();
            PdfCanvas canvas = new PdfCanvas(page);
            canvas
                    .saveState()
                    .beginText()
                    .moveText(36, 700)
                    .setFontAndSize(PdfFont.createStandardFont(pdfDoc, FontConstants.HELVETICA), 72)
                    .showText(Integer.toString(i + 1))
                    .endText()
                    .restoreState();
            canvas.rectangle(100, 500, 100, 100).fill();
            canvas.release();
            page.flush();
        }
        pdfDoc.close();

        com.itextpdf.text.pdf.PdfReader reader = new com.itextpdf.text.pdf.PdfReader(filename);
        Assert.assertEquals("Rebuilt", false, reader.isRebuilt());
        HashMap<String, String> info = reader.getInfo();
        Assert.assertEquals("Author", author, info.get("Author"));
        Assert.assertEquals("Creator", creator, info.get("Creator"));
        Assert.assertEquals("Title", title, info.get("Title"));
        Assert.assertEquals("Page count", pageCount, reader.getNumberOfPages());
        for (int i = 1; i <= pageCount; i++) {
            com.itextpdf.text.pdf.PdfDictionary page = reader.getPageN(i);
            Assert.assertEquals(com.itextpdf.text.pdf.PdfName.PAGE, page.get(com.itextpdf.text.pdf.PdfName.TYPE));
        }
        reader.close();
    }

    @Test
    public void create1000PagesDocumentWithText() throws IOException {
        int pageCount = 1000;
        final String filename = destinationFolder + "1000PagesDocumentWithText.pdf";

        final String author = "Alexander Chingarev";
        final String creator = "iText 6";
        final String title = "Empty iText 6 Document";

        FileOutputStream fos = new FileOutputStream(filename);
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);
        for (int i = 0; i < pageCount; i++) {
            PdfPage page = pdfDoc.addNewPage();
            PdfCanvas canvas = new PdfCanvas(page);
            canvas.saveState()
                    .beginText()
                    .moveText(36, 650)
                    .setFontAndSize(PdfFont.createStandardFont(pdfDoc, FontConstants.COURIER), 16)
                    .showText("Page " + (i + 1))
                    .endText();

            canvas.rectangle(100, 100, 100, 100).fill();
            canvas.release();
            page.flush();
        }
        pdfDoc.close();

        com.itextpdf.text.pdf.PdfReader reader = new com.itextpdf.text.pdf.PdfReader(filename);
        Assert.assertEquals("Rebuilt", false, reader.isRebuilt());
        HashMap<String, String> info = reader.getInfo();
        Assert.assertEquals("Author", author, info.get("Author"));
        Assert.assertEquals("Creator", creator, info.get("Creator"));
        Assert.assertEquals("Title", title, info.get("Title"));
        Assert.assertEquals("Page count", pageCount, reader.getNumberOfPages());
        for (int i = 1; i <= pageCount; i++) {
            com.itextpdf.text.pdf.PdfDictionary page = reader.getPageN(i);
            Assert.assertEquals(com.itextpdf.text.pdf.PdfName.PAGE, page.get(com.itextpdf.text.pdf.PdfName.TYPE));
        }
        reader.close();
    }

    @Test
    public void create1000PagesDocumentWithFullCompression() throws IOException {
        int pageCount = 1000;
        String filename = destinationFolder + "1000PagesDocumentWithFullCompression.pdf";

        final String author = "Alexander Chingarev";
        final String creator = "iText 6";
        final String title = "Empty iText 6 Document";

        FileOutputStream fos = new FileOutputStream(filename);
        PdfWriter writer = new PdfWriter(fos);
        writer.setFullCompression(true);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);
        for (int i = 0; i < pageCount; i++) {
            PdfPage page = pdfDoc.addNewPage();
            PdfCanvas canvas = new PdfCanvas(page);
            canvas
                    .saveState()
                    .beginText()
                    .moveText(36, 700)
                    .setFontAndSize(PdfFont.createStandardFont(pdfDoc, FontConstants.HELVETICA), 72)
                    .showText(Integer.toString(i + 1))
                    .endText()
                    .restoreState();
            canvas.rectangle(100, 500, 100, 100).fill();
            canvas.release();
            page.flush();
        }
        pdfDoc.close();

        com.itextpdf.text.pdf.PdfReader reader = new com.itextpdf.text.pdf.PdfReader(filename);
        Assert.assertEquals("Rebuilt", false, reader.isRebuilt());
        HashMap<String, String> info = reader.getInfo();
        Assert.assertEquals("Author", author, info.get("Author"));
        Assert.assertEquals("Creator", creator, info.get("Creator"));
        Assert.assertEquals("Title", title, info.get("Title"));
        Assert.assertEquals("Page count", pageCount, reader.getNumberOfPages());
        for (int i = 1; i <= pageCount; i++) {
            com.itextpdf.text.pdf.PdfDictionary page = reader.getPageN(i);
            Assert.assertEquals(com.itextpdf.text.pdf.PdfName.PAGE, page.get(com.itextpdf.text.pdf.PdfName.TYPE));
        }
        reader.close();
    }

    @Test
    public void create100PagesDocumentWithFullCompression() throws IOException {
        int pageCount = 100;
        String filename = destinationFolder + pageCount + "PagesDocumentWithFullCompression.pdf";

        final String author = "Alexander Chingarev";
        final String creator = "iText 6";
        final String title = "Empty iText 6 Document";

        FileOutputStream fos = new FileOutputStream(filename);
        PdfWriter writer = new PdfWriter(fos);
        writer.setFullCompression(true);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);
        for (int i = 0; i < pageCount; i++) {
            PdfPage page = pdfDoc.addNewPage();
            PdfCanvas canvas = new PdfCanvas(page);
            canvas
                    .saveState()
                    .beginText()
                    .moveText(36, 700)
                    .setFontAndSize(PdfFont.createStandardFont(pdfDoc, FontConstants.HELVETICA), 72)
                    .showText(Integer.toString(i + 1))
                    .endText()
                    .restoreState();
            canvas.rectangle(100, 500, 100, 100).fill();
            canvas.release();
            page.flush();
        }
        pdfDoc.close();

        com.itextpdf.text.pdf.PdfReader reader = new com.itextpdf.text.pdf.PdfReader(filename);
        Assert.assertEquals("Rebuilt", false, reader.isRebuilt());
        HashMap<String, String> info = reader.getInfo();
        Assert.assertEquals("Author", author, info.get("Author"));
        Assert.assertEquals("Creator", creator, info.get("Creator"));
        Assert.assertEquals("Title", title, info.get("Title"));
        Assert.assertEquals("Page count", pageCount, reader.getNumberOfPages());
        for (int i = 1; i <= pageCount; i++) {
            com.itextpdf.text.pdf.PdfDictionary page = reader.getPageN(i);
            Assert.assertEquals(com.itextpdf.text.pdf.PdfName.PAGE, page.get(com.itextpdf.text.pdf.PdfName.TYPE));
        }
        reader.close();
    }

    @Test
    public void create197PagesDocumentWithFullCompression() throws IOException {
        int pageCount = 197;
        String filename = destinationFolder + pageCount + "PagesDocumentWithFullCompression.pdf";

        final String author = "Alexander Chingarev";
        final String creator = "iText 6";
        final String title = "Empty iText 6 Document";

        FileOutputStream fos = new FileOutputStream(filename);
        PdfWriter writer = new PdfWriter(fos);
        writer.setFullCompression(true);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);
        for (int i = 0; i < pageCount; i++) {
            PdfPage page = pdfDoc.addNewPage();
            PdfCanvas canvas = new PdfCanvas(page);
            canvas
                    .saveState()
                    .beginText()
                    .moveText(36, 700)
                    .setFontAndSize(PdfFont.createStandardFont(pdfDoc, FontConstants.HELVETICA), 72)
                    .showText(Integer.toString(i + 1))
                    .endText()
                    .restoreState();
            canvas.rectangle(100, 500, 100, 100).fill();
            canvas.release();
            page.flush();
        }
        pdfDoc.close();

        com.itextpdf.text.pdf.PdfReader reader = new com.itextpdf.text.pdf.PdfReader(filename);
        Assert.assertEquals("Rebuilt", false, reader.isRebuilt());
        HashMap<String, String> info = reader.getInfo();
        Assert.assertEquals("Author", author, info.get("Author"));
        Assert.assertEquals("Creator", creator, info.get("Creator"));
        Assert.assertEquals("Title", title, info.get("Title"));
        Assert.assertEquals("Page count", pageCount, reader.getNumberOfPages());
        for (int i = 1; i <= pageCount; i++) {
            com.itextpdf.text.pdf.PdfDictionary page = reader.getPageN(i);
            Assert.assertEquals(com.itextpdf.text.pdf.PdfName.PAGE, page.get(com.itextpdf.text.pdf.PdfName.TYPE));
        }
        reader.close();
    }

    @Test
    public void create10PagesDocumentWithFullCompression() throws IOException {
        int pageCount = 10;
        String filename = destinationFolder + pageCount + "PagesDocumentWithFullCompression.pdf";

        final String author = "Alexander Chingarev";
        final String creator = "iText 6";
        final String title = "Empty iText 6 Document";

        FileOutputStream fos = new FileOutputStream(filename);
        PdfWriter writer = new PdfWriter(fos);
        writer.setFullCompression(true);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);
        for (int i = 0; i < pageCount; i++) {
            PdfPage page = pdfDoc.addNewPage();
            PdfCanvas canvas = new PdfCanvas(page);
            canvas
                    .saveState()
                    .beginText()
                    .moveText(36, 700)
                    .setFontAndSize(PdfFont.createStandardFont(pdfDoc, FontConstants.HELVETICA), 72)
                    .showText(Integer.toString(i + 1))
                    .endText()
                    .restoreState();
            canvas.rectangle(100, 500, 100, 100).fill();
            canvas.release();
            page.flush();
        }
        pdfDoc.close();

        com.itextpdf.text.pdf.PdfReader reader = new com.itextpdf.text.pdf.PdfReader(filename);
        Assert.assertEquals("Rebuilt", false, reader.isRebuilt());
        HashMap<String, String> info = reader.getInfo();
        Assert.assertEquals("Author", author, info.get("Author"));
        Assert.assertEquals("Creator", creator, info.get("Creator"));
        Assert.assertEquals("Title", title, info.get("Title"));
        Assert.assertEquals("Page count", pageCount, reader.getNumberOfPages());
        for (int i = 1; i <= pageCount; i++) {
            com.itextpdf.text.pdf.PdfDictionary page = reader.getPageN(i);
            Assert.assertEquals(com.itextpdf.text.pdf.PdfName.PAGE, page.get(com.itextpdf.text.pdf.PdfName.TYPE));
        }
        reader.close();
    }

    @Test
    public void copyPagesTest1() throws IOException, DocumentException, InterruptedException {
        String file1 = destinationFolder + "copyPages1_1.pdf";
        String file2 = destinationFolder + "copyPages1_2.pdf";

        PdfWriter writer1 = new PdfWriter(new FileOutputStream(file1));
        PdfDocument pdfDoc1 = new PdfDocument(writer1);

        PdfPage page1 = pdfDoc1.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page1);
        canvas.rectangle(100, 600, 100, 100);
        canvas.fill();
        canvas.beginText();
        canvas.setFontAndSize(PdfFont.createStandardFont(pdfDoc1, FontConstants.COURIER), 12);
        canvas.setTextMatrix(1, 0, 0, 1, 100, 500);
        canvas.showText("Hello World!");
        canvas.endText();
        canvas.release();

        page1.flush();
        pdfDoc1.close();

        PdfReader reader1 = new PdfReader(new FileInputStream(file1));
        pdfDoc1 = new PdfDocument(reader1);
        page1 = pdfDoc1.getPage(1);

        FileOutputStream fos2 = new FileOutputStream(file2);
        PdfWriter writer2 = new PdfWriter(fos2);
        PdfDocument pdfDoc2 = new PdfDocument(writer2);
        PdfPage page2 = page1.copy(pdfDoc2);
        pdfDoc2.addPage(page2);

        page2.flush();
        pdfDoc2.close();

        com.itextpdf.text.pdf.PdfReader reader = new com.itextpdf.text.pdf.PdfReader(file2);
        Assert.assertEquals("Rebuilt", false, reader.isRebuilt());
        com.itextpdf.text.pdf.PdfDictionary page = reader.getPageN(1);
        Assert.assertNotNull(page.get(com.itextpdf.text.pdf.PdfName.PARENT));
        reader.close();
        Assert.assertNull(new CompareTool().compareByContent(file1, file2, destinationFolder, "diff_"));
    }

    @Test
    public void copyPagesTest2() throws IOException, DocumentException, InterruptedException {
        String file1 = destinationFolder + "copyPages2_1.pdf";
        String file2 = destinationFolder + "copyPages2_2.pdf";

        PdfWriter writer1 = new PdfWriter(new FileOutputStream(file1));
        PdfDocument pdfDoc1 = new PdfDocument(writer1);

        for (int i = 0; i < 10; i++) {
            PdfPage page1 = pdfDoc1.addNewPage();
            PdfCanvas canvas = new PdfCanvas(page1);
            canvas.rectangle(100, 600, 100, 100);
            canvas.fill();
            canvas.beginText();
            canvas.setFontAndSize(PdfFont.createStandardFont(pdfDoc1, FontConstants.COURIER), 12);
            canvas.setTextMatrix(1, 0, 0, 1, 100, 500);
            canvas.showText(String.format("Page_%d", i + 1));
            canvas.endText();
            canvas.release();
            page1.flush();
        }
        pdfDoc1.close();

        pdfDoc1 = new PdfDocument(new PdfReader(new FileInputStream(file1)));

        PdfWriter writer2 = new PdfWriter(new FileOutputStream(file2));
        PdfDocument pdfDoc2 = new PdfDocument(writer2);
        for (int i = 9; i >= 0; i--) {
            PdfPage page2 = pdfDoc1.getPage(i + 1).copy(pdfDoc2);
            pdfDoc2.addPage(page2);
        }

        pdfDoc1.close();
        pdfDoc2.close();

        com.itextpdf.text.pdf.PdfReader reader = new com.itextpdf.text.pdf.PdfReader(file2);
        Assert.assertEquals("Rebuilt", false, reader.isRebuilt());
        com.itextpdf.text.pdf.PdfDictionary page = reader.getPageN(1);
        Assert.assertNotNull(page.get(com.itextpdf.text.pdf.PdfName.PARENT));
        reader.close();

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
    public void copyPagesTest3() throws IOException, DocumentException, InterruptedException {
        String file1 = destinationFolder + "copyPages3_1.pdf";
        String file2 = destinationFolder + "copyPages3_2.pdf";

        PdfWriter writer1 = new PdfWriter(new FileOutputStream(file1));
        PdfDocument pdfDoc1 = new PdfDocument(writer1);

        PdfPage page1 = pdfDoc1.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page1);
        canvas.rectangle(100, 600, 100, 100);
        canvas.fill();
        canvas.beginText();
        canvas.setFontAndSize(PdfFont.createStandardFont(pdfDoc1, FontConstants.COURIER), 12);
        canvas.setTextMatrix(1, 0, 0, 1, 100, 500);
        canvas.showText("Hello World!!!");
        canvas.endText();
        canvas.release();

        page1.flush();
        pdfDoc1.close();

        pdfDoc1 = new PdfDocument(new PdfReader(new FileInputStream(file1)));
        page1 = pdfDoc1.getPage(1);

        PdfWriter writer2 = new PdfWriter(new FileOutputStream(file2));
        PdfDocument pdfDoc2 = new PdfDocument(writer2);
        for (int i = 0; i < 10; i++) {
            PdfPage page2 = page1.copy(pdfDoc2);
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
    public void copyPagesTest4() throws IOException, DocumentException, InterruptedException {
        String file1 = destinationFolder + "copyPages4_1.pdf";
        FileOutputStream fos1 = new FileOutputStream(file1);
        PdfWriter writer1 = new PdfWriter(fos1);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);

        for (int i = 0; i < 5; i++) {
            PdfPage page1 = pdfDoc1.addNewPage();
            PdfCanvas canvas = new PdfCanvas(page1);
            canvas.rectangle(100, 600, 100, 100);
            canvas.fill();
            canvas.beginText();
            canvas.setFontAndSize(PdfFont.createStandardFont(pdfDoc1, FontConstants.COURIER), 12);
            canvas.setTextMatrix(1, 0, 0, 1, 100, 500);
            canvas.showText(String.format("Page_%d", i + 1));
            canvas.endText();
            canvas.release();
        }

        pdfDoc1.close();
        pdfDoc1 = new PdfDocument(new PdfReader(new FileInputStream(file1)));

        for (int i = 0; i < 5; i++) {
            FileOutputStream fos2 = new FileOutputStream(destinationFolder + String.format("copyPages4_%d.pdf", i + 2));
            PdfWriter writer2 = new PdfWriter(fos2);
            PdfDocument pdfDoc2 = new PdfDocument(writer2);
            PdfPage page2 = pdfDoc1.getPage(i + 1).copy(pdfDoc2);
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
            PdfDocument doc2 = new PdfDocument(new PdfReader(destinationFolder + String.format("copyPages4_%d.pdf", i + 2)));
            PdfDictionary page = doc2.getPage(1).getPdfObject();
            Assert.assertTrue(cmpTool.compareDictionaries(page1, page));
            doc2.close();
        }

        doc1.close();
    }


    @Test
    public void copyPagesTest5() throws IOException, DocumentException, InterruptedException {

        int documentCount = 3;

        for (int i = 0; i < documentCount; i++) {
            FileOutputStream fos1 = new FileOutputStream(destinationFolder + String.format("copyPages5_%d.pdf", i + 1));
            PdfWriter writer1 = new PdfWriter(fos1);
            PdfDocument pdfDoc1 = new PdfDocument(writer1);
            PdfPage page1 = pdfDoc1.addNewPage();
            PdfCanvas canvas = new PdfCanvas(page1);
            canvas.rectangle(100, 600, 100, 100);
            canvas.fill();
            canvas.beginText();
            canvas.setFontAndSize(PdfFont.createStandardFont(pdfDoc1, FontConstants.COURIER), 12);
            canvas.setTextMatrix(1, 0, 0, 1, 100, 500);
            canvas.showText(String.format("Page_%d", i + 1));
            canvas.endText();
            canvas.release();
            pdfDoc1.close();
        }

        List<PdfDocument> docs = new ArrayList<PdfDocument>();
        for (int i = 0; i < documentCount; i++) {
            FileInputStream fos1 = new FileInputStream(destinationFolder + String.format("copyPages5_%d.pdf", i + 1));
            PdfDocument pdfDoc1 = new PdfDocument(new PdfReader(fos1));
            docs.add(pdfDoc1);
        }

        FileOutputStream fos2 = new FileOutputStream(destinationFolder + "copyPages5_4.pdf");
        PdfWriter writer2 = new PdfWriter(fos2);
        PdfDocument pdfDoc2 = new PdfDocument(writer2);
        for (int i = 0; i < 3; i++) {
            pdfDoc2.addPage(docs.get(i).getPage(1).copy(pdfDoc2));
        }

        pdfDoc2.close();
        for (PdfDocument doc : docs)
            doc.close();

        CompareTool cmpTool = new CompareTool();
        for (int i = 0; i < 3; i++) {
            PdfReader reader1 = new PdfReader(destinationFolder + String.format("copyPages5_%d.pdf", i + 1));
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
    public void copyPagesTest6() throws IOException, DocumentException, InterruptedException {
        String file1 = destinationFolder + "copyPages6_1.pdf";
        String file2 = destinationFolder + "copyPages6_2.pdf";
        String file3 = destinationFolder + "copyPages6_3.pdf";
        String file1_upd = destinationFolder + "copyPages6_1_upd.pdf";

        FileOutputStream fos1 = new FileOutputStream(file1);
        PdfWriter writer1 = new PdfWriter(fos1);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        PdfPage page1 = pdfDoc1.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page1);
        canvas.rectangle(100, 600, 100, 100);
        canvas.fill();
        canvas.beginText();
        canvas.setFontAndSize(PdfFont.createStandardFont(pdfDoc1, FontConstants.COURIER), 12);
        canvas.setTextMatrix(1, 0, 0, 1, 100, 500);
        canvas.showText("Hello World!");
        canvas.endText();
        canvas.release();

        pdfDoc1.close();
        pdfDoc1 = new PdfDocument(new PdfReader(new FileInputStream(file1)));

        FileOutputStream fos2 = new FileOutputStream(file2);
        PdfWriter writer2 = new PdfWriter(fos2);
        PdfDocument pdfDoc2 = new PdfDocument(writer2);
        pdfDoc2.addPage(pdfDoc1.getPage(1).copy(pdfDoc2));

        pdfDoc2.close();
        pdfDoc2 = new PdfDocument(new PdfReader(new FileInputStream(file2)));

        FileOutputStream fos3 = new FileOutputStream(file3);
        PdfWriter writer3 = new PdfWriter(fos3);
        PdfDocument pdfDoc3 = new PdfDocument(writer3);
        pdfDoc3.addPage(pdfDoc2.getPage(1).copy(pdfDoc3));

        pdfDoc3.close();
        pdfDoc3 = new PdfDocument(new PdfReader(new FileInputStream(file3)));

        pdfDoc1.close();
        pdfDoc1 = new PdfDocument(new PdfReader(new FileInputStream(file1)),
                new PdfWriter(new FileOutputStream(file1_upd)));

        pdfDoc1.addPage(pdfDoc3.getPage(1).copy(pdfDoc1));

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
    public void markedContentTest1() throws Exception {
        String message = "";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument document = new PdfDocument(writer);
        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.beginMarkedContent(new com.itextpdf.core.pdf.PdfName("Tag1"));
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
        FileOutputStream fos = new FileOutputStream(destinationFolder + "markedContentTest2.pdf");
        PdfWriter writer = new PdfWriter(fos);
        final PdfDocument document = new PdfDocument(writer);
        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);

        com.itextpdf.core.pdf.PdfDictionary tag2 = new com.itextpdf.core.pdf.PdfDictionary(new HashMap<com.itextpdf.core.pdf.PdfName, PdfObject>() {{
            put(new com.itextpdf.core.pdf.PdfName("Tag"), new PdfNumber(2));
        }});
        com.itextpdf.core.pdf.PdfDictionary tag3 = new com.itextpdf.core.pdf.PdfDictionary(new HashMap<com.itextpdf.core.pdf.PdfName, PdfObject>() {{
            put(new com.itextpdf.core.pdf.PdfName("Tag"), new PdfNumber(3).makeIndirect(document));
        }});

        canvas.beginMarkedContent(new com.itextpdf.core.pdf.PdfName("Tag1")).endMarkedContent().
                beginMarkedContent(new com.itextpdf.core.pdf.PdfName("Tag2"), tag2).endMarkedContent().
                beginMarkedContent(new com.itextpdf.core.pdf.PdfName("Tag3"), (com.itextpdf.core.pdf.PdfDictionary) tag3.makeIndirect(document)).endMarkedContent();

        canvas.release();
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "markedContentTest2.pdf", sourceFolder + "cmp_markedContentTest2.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void graphicsStateTest1() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument document = new PdfDocument(writer);
        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.setLineWidth(3);
        canvas.saveState();
        canvas.setLineWidth(5);
        Assert.assertEquals(5, canvas.getGraphicsState().getLineWidth(), 0);
        canvas.restoreState();
        Assert.assertEquals(3, canvas.getGraphicsState().getLineWidth(), 0);
        PdfExtGState egs = new PdfExtGState();
        egs.getPdfObject().put(com.itextpdf.core.pdf.PdfName.LW, new PdfNumber(2));
        canvas.setExtGState(egs);
        Assert.assertEquals(2, canvas.getGraphicsState().getLineWidth(), 0);
        canvas.release();
        document.close();
    }

    @Test
    public void colorTest01() throws Exception {
        FileOutputStream fos = new FileOutputStream(destinationFolder + "colorTest01.pdf");
        PdfWriter writer = new PdfWriter(fos);
        final PdfDocument document = new PdfDocument(writer);
        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);

        canvas.setFillColor(DeviceRgb.RED).rectangle(50, 500, 50, 50).fill();
        canvas.setFillColor(DeviceRgb.GREEN).rectangle(150, 500, 50, 50).fill();
        canvas.setFillColor(DeviceRgb.BLUE).rectangle(250, 500, 50, 50).fill();
        canvas.setLineWidth(5);
        canvas.setStrokeColor(DeviceCmyk.Cyan).rectangle(50, 400, 50, 50).stroke();
        canvas.setStrokeColor(DeviceCmyk.Magenta).rectangle(150, 400, 50, 50).stroke();
        canvas.setStrokeColor(DeviceCmyk.Yellow).rectangle(250, 400, 50, 50).stroke();
        canvas.setStrokeColor(DeviceCmyk.Black).rectangle(350, 400, 50, 50).stroke();

        canvas.release();
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "colorTest01.pdf", sourceFolder + "cmp_colorTest01.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void colorTest02() throws Exception {
        FileOutputStream fos = new FileOutputStream(destinationFolder + "colorTest02.pdf");
        PdfWriter writer = new PdfWriter(fos);
        writer.setCompressionLevel(PdfWriter.NO_COMPRESSION);
        final PdfDocument document = new PdfDocument(writer);
        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);

        PdfDeviceCs.Rgb rgb = new PdfDeviceCs.Rgb().makeIndirect(document);
        Color red = new Color(rgb, new float[]{1, 0, 0});
        Color green = new Color(rgb, new float[]{0, 1, 0});
        Color blue = new Color(rgb, new float[]{0, 0, 1});
        PdfDeviceCs.Cmyk cmyk = new PdfDeviceCs.Cmyk().makeIndirect(document);
        Color cyan = new Color(cmyk, new float[]{1, 0, 0, 0});
        Color magenta = new Color(cmyk, new float[]{0, 1, 0, 0});
        Color yellow = new Color(cmyk, new float[]{0, 0, 1, 0});
        Color black = new Color(cmyk, new float[]{0, 0, 0, 1});

        canvas.setFillColor(red).rectangle(50, 500, 50, 50).fill();
        canvas.setFillColor(green).rectangle(150, 500, 50, 50).fill();
        canvas.setFillColor(blue).rectangle(250, 500, 50, 50).fill();
        canvas.setLineWidth(5);
        canvas.setStrokeColor(cyan).rectangle(50, 400, 50, 50).stroke();
        canvas.setStrokeColor(magenta).rectangle(150, 400, 50, 50).stroke();
        canvas.setStrokeColor(yellow).rectangle(250, 400, 50, 50).stroke();
        canvas.setStrokeColor(black).rectangle(350, 400, 50, 50).stroke();

        canvas.release();
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "colorTest02.pdf", sourceFolder + "cmp_colorTest02.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void colorTest03() throws Exception {
        FileOutputStream fos = new FileOutputStream(destinationFolder + "colorTest03.pdf");
        PdfWriter writer = new PdfWriter(fos);
        writer.setCompressionLevel(PdfWriter.NO_COMPRESSION);
        final PdfDocument document = new PdfDocument(writer);
        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);

        CalGray calGray1 = new CalGray(document, new float[]{0.9505f, 1.0000f, 1.0890f}, 0.5f);
        canvas.setFillColor(calGray1).rectangle(50, 500, 50, 50).fill();
        CalGray calGray2 = new CalGray(document, new float[]{0.9505f, 1.0000f, 1.0890f}, null, 2.222f, 0.5f);
        canvas.setFillColor(calGray2).rectangle(150, 500, 50, 50).fill();

        CalRgb calRgb = new CalRgb(document,
                new float[]{0.9505f, 1.0000f, 1.0890f},
                null,
                new float[]{1.8000f, 1.8000f, 1.8000f},
                new float[]{0.4497f, 0.2446f, 0.0252f, 0.3163f, 0.6720f, 0.1412f, 0.1845f, 0.0833f, 0.9227f},
                new float[]{1f, 0.5f, 0f});
        canvas.setFillColor(calRgb).rectangle(50, 400, 50, 50).fill();

        Lab lab1 = new Lab(document, new float[]{0.9505f, 1.0000f, 1.0890f}, null, new float[]{-128, 127, -128, 127}, new float[]{1f, 0.5f, 0f});
        canvas.setFillColor(lab1).rectangle(50, 300, 50, 50).fill();
        Lab lab2 = new Lab((PdfCieBasedCs.Lab) lab1.getColorSpace(), new float[]{0f, 0.5f, 0f});
        canvas.setFillColor(lab2).rectangle(150, 300, 50, 50).fill();

        canvas.release();
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "colorTest03.pdf", sourceFolder + "cmp_colorTest03.pdf", destinationFolder, "diff_"));

    }

    @Test
    public void colorTest04() throws Exception {
        //Create document with 3 colored rectangles in memory.
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        writer.setCompressionLevel(PdfWriter.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(writer);
        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        FileInputStream streamGray = new FileInputStream(sourceFolder + "BlackWhite.icc");
        FileInputStream streamRgb = new FileInputStream(sourceFolder + "CIERGB.icc");
        FileInputStream streamCmyk = new FileInputStream(sourceFolder + "USWebUncoated.icc");
        IccBased gray = new IccBased(document, streamGray, new float[]{0.5f});
        IccBased rgb = new IccBased(document, streamRgb, new float[]{1.0f, 0.5f, 0f});
        IccBased cmyk = new IccBased(document, streamCmyk, new float[]{1.0f, 0.5f, 0f, 0f});
        canvas.setFillColor(gray).rectangle(50, 500, 50, 50).fill();
        canvas.setFillColor(rgb).rectangle(150, 500, 50, 50).fill();
        canvas.setFillColor(cmyk).rectangle(250, 500, 50, 50).fill();
        canvas.release();
        document.close();

        //Copies page from created document to new document.
        //This is not strictly necessary for ICC-based colors paces test, but this is an additional test for copy functionality.
        byte[] bytes = baos.toByteArray();
        com.itextpdf.core.pdf.PdfReader reader = new com.itextpdf.core.pdf.PdfReader(new ByteArrayInputStream(bytes));
        document = new PdfDocument(reader);
        FileOutputStream fos = new FileOutputStream(destinationFolder + "colorTest04.pdf");
        writer = new PdfWriter(fos);
        PdfDocument newDocument = new PdfDocument(writer);
        newDocument.addPage(document.getPage(1).copy(newDocument));
        newDocument.close();
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "colorTest04.pdf", sourceFolder + "cmp_colorTest04.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void colorTest05() throws Exception {
        FileOutputStream fos = new FileOutputStream(destinationFolder + "colorTest05.pdf");
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument document = new PdfDocument(writer);
        PdfPage page = document.addNewPage();
        FileInputStream streamGray = new FileInputStream(sourceFolder + "BlackWhite.icc");
        FileInputStream streamRgb = new FileInputStream(sourceFolder + "CIERGB.icc");
        FileInputStream streamCmyk = new FileInputStream(sourceFolder + "USWebUncoated.icc");
        PdfCieBasedCs.IccBased gray = (PdfCieBasedCs.IccBased) new IccBased(document, streamGray).getColorSpace();
        PdfCieBasedCs.IccBased rgb = (PdfCieBasedCs.IccBased) new IccBased(document, streamRgb).getColorSpace();
        PdfCieBasedCs.IccBased cmyk = (PdfCieBasedCs.IccBased) new IccBased(document, streamCmyk).getColorSpace();
        PdfResources resources = page.getResources();
        resources.setDefaultGray(gray);
        resources.setDefaultRgb(rgb);
        resources.setDefaultCmyk(cmyk);
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.setFillColorGray(0.5f).rectangle(50, 500, 50, 50).fill();
        canvas.setFillColorRgb(1.0f, 0.5f, 0f).rectangle(150, 500, 50, 50).fill();
        canvas.setFillColorCmyk(1.0f, 0.5f, 0f, 0f).rectangle(250, 500, 50, 50).fill();
        canvas.release();
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "colorTest05.pdf", sourceFolder + "cmp_colorTest05.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void colorTest06() throws Exception {
        byte[] bytes = new byte[256 * 3];
        int k = 0;
        for (int i = 0; i < 256; i++) {
            bytes[k++] = (byte) i;
            bytes[k++] = (byte) i;
            bytes[k++] = (byte) i;
        }

        FileOutputStream fos = new FileOutputStream(destinationFolder + "colorTest06.pdf");
        PdfWriter writer = new PdfWriter(fos);
        writer.setCompressionLevel(PdfWriter.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(writer);
        PdfPage page = document.addNewPage();

        PdfSpecialCs.Indexed indexed = new PdfSpecialCs.Indexed(document, com.itextpdf.core.pdf.PdfName.DeviceRGB, 255, new PdfString(new String(bytes, Charset.forName("UTF-8"))));
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.setFillColor(new Indexed(indexed, 85)).rectangle(50, 500, 50, 50).fill();
        canvas.setFillColor(new Indexed(indexed, 127)).rectangle(150, 500, 50, 50).fill();
        canvas.setFillColor(new Indexed(indexed, 170)).rectangle(250, 500, 50, 50).fill();
        canvas.release();
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "colorTest06.pdf", sourceFolder + "cmp_colorTest06.pdf", destinationFolder, "diff_"));
    }


    @Test
    public void colorTest07() throws Exception {
        FileOutputStream fos = new FileOutputStream(destinationFolder + "colorTest07.pdf");
        PdfWriter writer = new PdfWriter(fos);
        writer.setCompressionLevel(PdfWriter.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(writer);
        PdfPage page = document.addNewPage();

        com.itextpdf.core.pdf.function.PdfFunction.Type4 function = new com.itextpdf.core.pdf.function.PdfFunction.Type4(document, new PdfArray(new float[]{0, 1}), new PdfArray(new float[]{0, 1, 0, 1, 0, 1}), "{0 0}".getBytes());
        PdfSpecialCs.Separation separation = new PdfSpecialCs.Separation(document, "MyRed", new PdfDeviceCs.Rgb(), function);

        PdfCanvas canvas = new PdfCanvas(page);
        canvas.setFillColor(new Separation(separation, 0.25f)).rectangle(50, 500, 50, 50).fill();
        canvas.setFillColor(new Separation(separation, 0.5f)).rectangle(150, 500, 50, 50).fill();
        canvas.setFillColor(new Separation(separation, 0.75f)).rectangle(250, 500, 50, 50).fill();
        canvas.release();
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "colorTest07.pdf", sourceFolder + "cmp_colorTest07.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void colorTest08() throws Exception {
        FileOutputStream fos = new FileOutputStream(destinationFolder + "colorTest08.pdf");
        PdfWriter writer = new PdfWriter(fos);
        writer.setCompressionLevel(PdfWriter.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(writer);
        PdfPage page = document.addNewPage();

        com.itextpdf.core.pdf.function.PdfFunction.Type4 function = new com.itextpdf.core.pdf.function.PdfFunction.Type4(document, new PdfArray(new float[]{0, 1, 0, 1}), new PdfArray(new float[]{0, 1, 0, 1, 0, 1}), "{0}".getBytes());
        PdfSpecialCs.DeviceN deviceN = new PdfSpecialCs.DeviceN(document, new ArrayList<String>() {{
            add("MyRed");
            add("MyGreen");
        }}, new PdfDeviceCs.Rgb(), function);

        PdfCanvas canvas = new PdfCanvas(page);
        canvas.setFillColor(new DeviceN(deviceN, new float[]{0, 0})).rectangle(50, 500, 50, 50).fill();
        canvas.setFillColor(new DeviceN(deviceN, new float[]{0, 1})).rectangle(150, 500, 50, 50).fill();
        canvas.setFillColor(new DeviceN(deviceN, new float[]{1, 0})).rectangle(250, 500, 50, 50).fill();
        canvas.release();
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "colorTest08.pdf", sourceFolder + "cmp_colorTest08.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void wmfImageTest01() throws IOException, InterruptedException {
        FileOutputStream fos = new FileOutputStream(destinationFolder + "wmfImageTest01.pdf");
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument document = new PdfDocument(writer);
        PdfPage page = document.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page);
        Image img = new WmfImage(sourceFolder + "example.wmf");
        canvas.addImage(img, 0, 0, 0.1f, false);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "wmfImageTest01.pdf", sourceFolder + "cmp_wmfImageTest01.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void wmfImageTest02() throws IOException, InterruptedException {
        FileOutputStream fos = new FileOutputStream(destinationFolder + "wmfImageTest02.pdf");
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument document = new PdfDocument(writer);
        PdfPage page = document.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page);
        Image img = new WmfImage(sourceFolder + "butterfly.wmf");
        canvas.addImage(img, 0, 0, 1, false);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "wmfImageTest02.pdf", sourceFolder + "cmp_wmfImageTest02.pdf", destinationFolder, "diff_"));
    }


    @Test
    @Ignore("Failing is caused by commenting of 581 line in MetaDo class. Should be unignored, when issues in MetaDo will be resolved.")
    public void wmfImageTest03() throws IOException, InterruptedException {
        FileOutputStream fos = new FileOutputStream(destinationFolder + "wmfImageTest03.pdf");
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument document = new PdfDocument(writer);
        PdfPage page = document.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page);
        Image img = new WmfImage(sourceFolder + "type1.wmf");
        canvas.addImage(img, 0, 0, 1, false);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "wmfImageTest03.pdf", sourceFolder + "cmp_wmfImageTest03.pdf", destinationFolder, "diff_"));
    }

    @Test
    @Ignore("Failing is caused by commenting of 581 line in MetaDo class. Should be unignored, when issues in MetaDo will be resolved.")
    public void wmfImageTest04() throws IOException, InterruptedException {
        FileOutputStream fos = new FileOutputStream(destinationFolder + "wmfImageTest04.pdf");
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument document = new PdfDocument(writer);
        PdfPage page = document.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page);
        Image img = new WmfImage(sourceFolder + "type0.wmf");
        canvas.addImage(img, 0, 0, 1, false);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "wmfImageTest04.pdf", sourceFolder + "cmp_wmfImageTest04.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void gifImageTest01() throws IOException, InterruptedException {
        FileOutputStream fos = new FileOutputStream(destinationFolder + "gifImageTest01.pdf");
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument document = new PdfDocument(writer);
        PdfPage page = document.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page);
        Image img = ImageFactory.getImage(sourceFolder + "2-frames.gif");
        canvas.addImage(img, 100, 100, 200, false);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "gifImageTest01.pdf", sourceFolder + "cmp_gifImageTest01.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void gifImageTest02() throws IOException, InterruptedException {
        FileOutputStream fos = new FileOutputStream(destinationFolder + "gifImageTest02.pdf");
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument document = new PdfDocument(writer);
        PdfPage page = document.addNewPage();

        InputStream is = new FileInputStream(sourceFolder + "2-frames.gif");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int reads = is.read();
        while (reads != -1) {
            baos.write(reads);
            reads = is.read();
        }

        PdfCanvas canvas = new PdfCanvas(page);
        Image img = ImageFactory.getGifImage(baos.toByteArray(), 1);
        canvas.addImage(img, 100, 100, 200, false);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "gifImageTest02.pdf", sourceFolder + "cmp_gifImageTest02.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void gifImageTest03() throws IOException, InterruptedException {
        FileOutputStream fos = new FileOutputStream(destinationFolder + "gifImageTest03.pdf");
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument document = new PdfDocument(writer);
        PdfPage page = document.addNewPage();

        InputStream is = new FileInputStream(sourceFolder + "2-frames.gif");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int reads = is.read();
        while (reads != -1) {
            baos.write(reads);
            reads = is.read();
        }

        PdfCanvas canvas = new PdfCanvas(page);
        Image img = ImageFactory.getGifImage(baos.toByteArray(), 2);
        canvas.addImage(img, 100, 100, 200, false);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "gifImageTest03.pdf", sourceFolder + "cmp_gifImageTest03.pdf", destinationFolder, "diff_"));
    }

    @Test(expected = PdfException.class)
    public void gifImageTest04() throws IOException, InterruptedException {
        FileOutputStream fos = new FileOutputStream(destinationFolder + "gifImageTest03.pdf");
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument document = new PdfDocument(writer);
        PdfPage page = document.addNewPage();

        InputStream is = new FileInputStream(sourceFolder + "2-frames.gif");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int reads = is.read();
        while (reads != -1) {
            baos.write(reads);
            reads = is.read();
        }

        PdfCanvas canvas = new PdfCanvas(page);
        Image img = ImageFactory.getGifImage(baos.toByteArray(), 3);
        canvas.addImage(img, 100, 100, 200, false);
    }

    @Test
    public void kernedTextTest01() throws IOException, InterruptedException {
        FileOutputStream fos = new FileOutputStream(destinationFolder + "kernedTextTest01.pdf");
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument document = new PdfDocument(writer);
        PdfPage page = document.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page);
        String kernableText = "AVAVAVAVAVAVAVAVAVAVAVAVAVAVAVAVAVAVAVAVAVAVAVAVAVAVAVAVAVAV";
        PdfFont font = PdfFont.createStandardFont(document, FontConstants.HELVETICA);
        canvas.beginText().moveText(50, 600).setFontAndSize(font, 12).showText("Kerning:-" + kernableText).endText();
        canvas.beginText().moveText(50, 650).setFontAndSize(font, 12).showTextKerned("Kerning:+" + kernableText).endText();

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "kernedTextTest01.pdf", sourceFolder + "cmp_kernedTextTest01.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void ccittImageTest01() throws IOException, InterruptedException {
        String filename = "ccittImage01.pdf";
        PdfWriter writer = new PdfWriter(new FileOutputStream(destinationFolder + filename));
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
        RawImage img = (RawImage) ImageFactory.getImage(barcode.getBitColumns(), barcode.getCodeRows(), false, RawImage.CCITTG4, 0, g4, null);
        img.setTypeCcitt(RawImage.CCITTG4);
        canvas.addImage(img, 100, 100, false);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    @LogMessage(messages = {LogMessageConstant.IMAGE_HAS_JBIG2DECODE_FILTER,
            LogMessageConstant.IMAGE_HAS_JPXDECODE_FILTER,
            LogMessageConstant.IMAGE_HAS_MASK,
            LogMessageConstant.IMAGE_SIZE_CANNOT_BE_MORE_4KB})
    public void inlineImagesTest01() throws IOException, InterruptedException {
        String filename = "inlineImages01.pdf";
        PdfWriter writer = new PdfWriter(new FileOutputStream(destinationFolder + filename));
        PdfDocument document = new PdfDocument(writer);

        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);

        canvas.addImage(ImageFactory.getImage(sourceFolder + "Desert.jpg"), 36, 700, 100, true);
        canvas.addImage(ImageFactory.getImage(sourceFolder + "bulb.gif"), 36, 600, 100, true);
        canvas.addImage(ImageFactory.getImage(sourceFolder + "smpl.bmp"), 36, 500, 100, true);
        canvas.addImage(ImageFactory.getImage(sourceFolder + "itext.png"), 36, 460, 100, true);
        canvas.addImage(ImageFactory.getImage(sourceFolder + "0047478.jpg"), 36, 300, 100, true);
        canvas.addImage(ImageFactory.getImage(sourceFolder + "map.jp2"), 36, 200, 100, true);
        canvas.addImage(ImageFactory.getImage(sourceFolder + "amb.jb2"), 36, 30, 100, true);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    @LogMessage(messages = {LogMessageConstant.IMAGE_HAS_JBIG2DECODE_FILTER,
            LogMessageConstant.IMAGE_HAS_JPXDECODE_FILTER,
            LogMessageConstant.IMAGE_HAS_MASK,
            LogMessageConstant.IMAGE_SIZE_CANNOT_BE_MORE_4KB})
    public void inlineImagesTest02() throws IOException, InterruptedException {
        String filename = "inlineImages02.pdf";
        PdfWriter writer = new PdfWriter(new FileOutputStream(destinationFolder + filename));
        PdfDocument document = new PdfDocument(writer);

        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);

        InputStream stream = Utilities.toURL(sourceFolder + "Desert.jpg").openStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Utilities.transferBytes(stream, baos);
        canvas.addImage(ImageFactory.getImage(baos.toByteArray()), 36, 700, 100, true);
        stream = Utilities.toURL(sourceFolder + "bulb.gif").openStream();
        baos = new ByteArrayOutputStream();
        Utilities.transferBytes(stream, baos);
        canvas.addImage(ImageFactory.getImage(baos.toByteArray()), 36, 600, 100, true);
        stream = Utilities.toURL(sourceFolder + "smpl.bmp").openStream();
        baos = new ByteArrayOutputStream();
        Utilities.transferBytes(stream, baos);
        canvas.addImage(ImageFactory.getImage(baos.toByteArray()), 36, 500, 100, true);
        stream = Utilities.toURL(sourceFolder + "itext.png").openStream();
        baos = new ByteArrayOutputStream();
        Utilities.transferBytes(stream, baos);
        canvas.addImage(ImageFactory.getImage(baos.toByteArray()), 36, 460, 100, true);
        stream = Utilities.toURL(sourceFolder + "0047478.jpg").openStream();
        baos = new ByteArrayOutputStream();
        Utilities.transferBytes(stream, baos);
        canvas.addImage(ImageFactory.getImage(baos.toByteArray()), 36, 300, 100, true);
        stream = Utilities.toURL(sourceFolder + "map.jp2").openStream();
        baos = new ByteArrayOutputStream();
        Utilities.transferBytes(stream, baos);
        canvas.addImage(ImageFactory.getImage(baos.toByteArray()), 36, 200, 100, true);
        stream = Utilities.toURL(sourceFolder + "amb.jb2").openStream();
        baos = new ByteArrayOutputStream();
        Utilities.transferBytes(stream, baos);
        canvas.addImage(ImageFactory.getImage(baos.toByteArray()), 36, 30, 100, true);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void awtImagesTest01() throws IOException, InterruptedException {
        String filename = "awtImagesTest01.pdf";
        PdfWriter writer = new PdfWriter(new FileOutputStream(destinationFolder + filename));
        PdfDocument document = new PdfDocument(writer);

        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);

        int x = 36;
        int y = 700;
        int width = 100;
        for (String image : RESOURCES) {
            java.awt.Image awtImage = Toolkit.getDefaultToolkit().createImage(sourceFolder + image);
            canvas.addImage(ImageFactory.getImage(awtImage, null), x, y, width, false);
            y -= 150;
        }

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }
}
