package com.itextpdf.canvas;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.fonts.PdfStandardFont;
import com.itextpdf.core.pdf.*;
import com.itextpdf.testutils.CompareTool;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class PdfCanvasTest {

    static final public String sourceFolder = "./src/test/resources/com/itextpdf/canvas/PdfCanvasTest/";
    static final public String destinationFolder = "./target/test/com/itextpdf/canvas/PdfCanvasTest/";

    @BeforeClass
    static public void beforeClass() {
        new File(destinationFolder).mkdirs();
    }

    @Test
    public void createSimpleCanvas() throws IOException, PdfException {

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
        Assert.assertEquals(author, info.get("Author"));
        Assert.assertEquals(creator, info.get("Creator"));
        Assert.assertEquals(title, info.get("Title"));
        PdfDictionary page = reader.getPageN(1);
        Assert.assertEquals(com.itextpdf.text.pdf.PdfName.PAGE, page.get(com.itextpdf.text.pdf.PdfName.TYPE));
        reader.close();
    }

    @Test
    public void createSimpleCanvasWithDrawing() throws IOException, PdfException {

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
        Assert.assertEquals(author, info.get("Author"));
        Assert.assertEquals(creator, info.get("Creator"));
        Assert.assertEquals(title, info.get("Title"));
        PdfDictionary page = reader.getPageN(1);
        Assert.assertEquals(com.itextpdf.text.pdf.PdfName.PAGE, page.get(com.itextpdf.text.pdf.PdfName.TYPE));
        reader.close();
    }

    @Test
    public void createSimpleCanvasWithText() throws IOException, PdfException {

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
                .setFontAndSize(new PdfStandardFont(pdfDoc, PdfStandardFont.Helvetica), 16)
                .showText("Hello Helvetica!")
                .endText()
                .restoreState();

        canvas
                .saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(new PdfStandardFont(pdfDoc, PdfStandardFont.HelveticaBoldOblique), 16)
                .showText("Hello Helvetica Bold Oblique!")
                .endText()
                .restoreState();

        canvas
                .saveState()
                .beginText()
                .moveText(36, 650)
                .setFontAndSize(new PdfStandardFont(pdfDoc, PdfStandardFont.Courier), 16)
                .showText("Hello Courier!")
                .endText()
                .restoreState();

        canvas
                .saveState()
                .beginText()
                .moveText(36, 600)
                .setFontAndSize(new PdfStandardFont(pdfDoc, PdfStandardFont.TimesItalic), 16)
                .showText("Hello Times Italic!")
                .endText()
                .restoreState();

        canvas
                .saveState()
                .beginText()
                .moveText(36, 550)
                .setFontAndSize(new PdfStandardFont(pdfDoc, PdfStandardFont.Symbol), 16)
                .showText("Hello Ellada!")
                .endText()
                .restoreState();

        canvas
                .saveState()
                .beginText()
                .moveText(36, 500)
                .setFontAndSize(new PdfStandardFont(pdfDoc, PdfStandardFont.ZapfDingbats), 16)
                .showText("Hello ZapfDingbats!")
                .endText()
                .restoreState();
        canvas.release();
        pdfDoc.close();

        com.itextpdf.text.pdf.PdfReader reader = new com.itextpdf.text.pdf.PdfReader(destinationFolder + fileName);
        Assert.assertEquals("Rebuilt", false, reader.isRebuilt());
        HashMap<String, String> info = reader.getInfo();
        Assert.assertEquals(author, info.get("Author"));
        Assert.assertEquals(creator, info.get("Creator"));
        Assert.assertEquals(title, info.get("Title"));
        PdfDictionary page = reader.getPageN(1);
        Assert.assertEquals(com.itextpdf.text.pdf.PdfName.PAGE, page.get(com.itextpdf.text.pdf.PdfName.TYPE));
        reader.close();
    }

    @Test
    public void createSimpleCanvasWithPageFlush() throws IOException, PdfException {

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
        Assert.assertEquals(author, info.get("Author"));
        Assert.assertEquals(creator, info.get("Creator"));
        Assert.assertEquals(title, info.get("Title"));
        PdfDictionary page = reader.getPageN(1);
        Assert.assertEquals(com.itextpdf.text.pdf.PdfName.PAGE, page.get(com.itextpdf.text.pdf.PdfName.TYPE));
        reader.close();
    }

    @Test
    public void createSimpleCanvasWithFullCompression() throws IOException, PdfException {

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
        Assert.assertEquals(author, info.get("Author"));
        Assert.assertEquals(creator, info.get("Creator"));
        Assert.assertEquals(title, info.get("Title"));
        PdfDictionary page = reader.getPageN(1);
        Assert.assertEquals(com.itextpdf.text.pdf.PdfName.PAGE, page.get(com.itextpdf.text.pdf.PdfName.TYPE));
        reader.close();
    }

    @Test
    public void createSimpleCanvasWithPageFlushAndFullCompression() throws IOException, PdfException {

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
        Assert.assertEquals(author, info.get("Author"));
        Assert.assertEquals(creator, info.get("Creator"));
        Assert.assertEquals(title, info.get("Title"));
        PdfDictionary page = reader.getPageN(1);
        Assert.assertEquals(com.itextpdf.text.pdf.PdfName.PAGE, page.get(com.itextpdf.text.pdf.PdfName.TYPE));
        reader.close();
    }

    @Test
    public void create1000PagesDocument() throws IOException, PdfException {
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
                    .setFontAndSize(new PdfStandardFont(pdfDoc, PdfStandardFont.Helvetica), 72)
                    .showText(Integer.toString(i+1))
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
        Assert.assertEquals(author, info.get("Author"));
        Assert.assertEquals(creator, info.get("Creator"));
        Assert.assertEquals(title, info.get("Title"));
        for (int i = 1; i <= pageCount; i++) {
            PdfDictionary page = reader.getPageN(i);
            Assert.assertEquals(com.itextpdf.text.pdf.PdfName.PAGE, page.get(com.itextpdf.text.pdf.PdfName.TYPE));
        }
        reader.close();
    }

    @Test
    public void create100PagesDocument() throws IOException, PdfException {
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
                    .setFontAndSize(new PdfStandardFont(pdfDoc, PdfStandardFont.Helvetica), 72)
                    .showText(Integer.toString(i+1))
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
        Assert.assertEquals(author, info.get("Author"));
        Assert.assertEquals(creator, info.get("Creator"));
        Assert.assertEquals(title, info.get("Title"));
        for (int i = 1; i <= pageCount; i++) {
            PdfDictionary page = reader.getPageN(i);
            Assert.assertEquals(com.itextpdf.text.pdf.PdfName.PAGE, page.get(com.itextpdf.text.pdf.PdfName.TYPE));
        }
        reader.close();
    }

    @Test
    public void create10PagesDocument() throws IOException, PdfException {
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
                    .setFontAndSize(new PdfStandardFont(pdfDoc, PdfStandardFont.Helvetica), 72)
                    .showText(Integer.toString(i+1))
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
        Assert.assertEquals(author, info.get("Author"));
        Assert.assertEquals(creator, info.get("Creator"));
        Assert.assertEquals(title, info.get("Title"));
        for (int i = 1; i <= pageCount; i++) {
            PdfDictionary page = reader.getPageN(i);
            Assert.assertEquals(com.itextpdf.text.pdf.PdfName.PAGE, page.get(com.itextpdf.text.pdf.PdfName.TYPE));
        }
        reader.close();
    }

    @Test
    public void create1000PagesDocumentWithText() throws IOException, PdfException {

        final String file = "1000PagesDocumentWithText.pdf";

        final String author = "Alexander Chingarev";
        final String creator = "iText 6";
        final String title = "Empty iText 6 Document";

        FileOutputStream fos = new FileOutputStream(destinationFolder + file);
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);
        int pageCount = 1000;
        for (int i = 0; i < pageCount; i++) {
            PdfPage page = pdfDoc.addNewPage();
            PdfCanvas canvas = new PdfCanvas(page);
            canvas.saveState()
                    .beginText()
                    .moveText(36, 650)
                    .setFontAndSize(new PdfStandardFont(pdfDoc, PdfStandardFont.Courier), 16)
                    .showText("Page " + (i + 1))
                    .endText();

            canvas.rectangle(100, 100, 100, 100).fill();
            canvas.release();
            page.flush();
        }
        pdfDoc.close();

        com.itextpdf.text.pdf.PdfReader reader = new com.itextpdf.text.pdf.PdfReader(destinationFolder + file);
        Assert.assertEquals("Rebuilt", false, reader.isRebuilt());
        HashMap<String, String> info = reader.getInfo();
        Assert.assertEquals(author, info.get("Author"));
        Assert.assertEquals(creator, info.get("Creator"));
        Assert.assertEquals(title, info.get("Title"));
        for (int i = 1; i <= pageCount; i++) {
            PdfDictionary page = reader.getPageN(i);
            Assert.assertEquals(com.itextpdf.text.pdf.PdfName.PAGE, page.get(com.itextpdf.text.pdf.PdfName.TYPE));
        }
        reader.close();
    }

    @Test
    public void create1000PagesDocumentWithFullCompression() throws IOException, PdfException {

        final String author = "Alexander Chingarev";
        final String creator = "iText 6";
        final String title = "Empty iText 6 Document";

        FileOutputStream fos = new FileOutputStream(destinationFolder + "1000PagesDocumentWithFullCompression.pdf");
        PdfWriter writer = new PdfWriter(fos);
        writer.setFullCompression(true);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);
        for (int i = 0; i < 1000; i++) {
            PdfPage page = pdfDoc.addNewPage();
            PdfCanvas canvas = new PdfCanvas(page);
            canvas
                    .saveState()
                    .beginText()
                    .moveText(36, 700)
                    .setFontAndSize(new PdfStandardFont(pdfDoc, PdfStandardFont.Helvetica), 72)
                    .showText(Integer.toString(i+1))
                    .endText()
                    .restoreState();
            canvas.rectangle(100, 500, 100, 100).fill();
            canvas.release();
            page.flush();
        }
        pdfDoc.close();

        com.itextpdf.text.pdf.PdfReader reader = new com.itextpdf.text.pdf.PdfReader(destinationFolder + "1000PagesDocumentWithFullCompression.pdf");
        Assert.assertEquals("Rebuilt", false, reader.isRebuilt());
        HashMap<String, String> info = reader.getInfo();
        Assert.assertEquals(author, info.get("Author"));
        Assert.assertEquals(creator, info.get("Creator"));
        Assert.assertEquals(title, info.get("Title"));
        for (int i = 1; i <= 10; i++) {
            PdfDictionary page = reader.getPageN(i);
            Assert.assertEquals(com.itextpdf.text.pdf.PdfName.PAGE, page.get(com.itextpdf.text.pdf.PdfName.TYPE));
        }
        reader.close();
    }

    @Test
    public void create100PagesDocumentWithFullCompression() throws IOException, PdfException {
        int count = 100;
        String filename = destinationFolder + count + "PagesDocumentWithFullCompression.pdf";

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
        for (int i = 0; i < count; i++) {
            PdfPage page = pdfDoc.addNewPage();
            PdfCanvas canvas = new PdfCanvas(page);
            canvas
                    .saveState()
                    .beginText()
                    .moveText(36, 700)
                    .setFontAndSize(new PdfStandardFont(pdfDoc, PdfStandardFont.Helvetica), 72)
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
        Assert.assertEquals(author, info.get("Author"));
        Assert.assertEquals(creator, info.get("Creator"));
        Assert.assertEquals(title, info.get("Title"));
        for (int i = 1; i <= count; i++) {
            PdfDictionary page = reader.getPageN(i);
            Assert.assertEquals(com.itextpdf.text.pdf.PdfName.PAGE, page.get(com.itextpdf.text.pdf.PdfName.TYPE));
        }
        reader.close();
    }

    @Test
    public void create197PagesDocumentWithFullCompression() throws IOException, PdfException {
        int count = 197;
        String filename = destinationFolder + count + "PagesDocumentWithFullCompression.pdf";

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
        for (int i = 0; i < count; i++) {
            PdfPage page = pdfDoc.addNewPage();
            PdfCanvas canvas = new PdfCanvas(page);
            canvas
                    .saveState()
                    .beginText()
                    .moveText(36, 700)
                    .setFontAndSize(new PdfStandardFont(pdfDoc, PdfStandardFont.Helvetica), 72)
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
        Assert.assertEquals(author, info.get("Author"));
        Assert.assertEquals(creator, info.get("Creator"));
        Assert.assertEquals(title, info.get("Title"));
        for (int i = 1; i <= count; i++) {
            PdfDictionary page = reader.getPageN(i);
            Assert.assertEquals(com.itextpdf.text.pdf.PdfName.PAGE, page.get(com.itextpdf.text.pdf.PdfName.TYPE));
        }
        reader.close();
    }

    @Test
    public void create10PagesDocumentWithFullCompression() throws IOException, PdfException {
        int count = 10;
        String filename = destinationFolder + count + "PagesDocumentWithFullCompression.pdf";

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
        for (int i = 0; i < count; i++) {
            PdfPage page = pdfDoc.addNewPage();
            PdfCanvas canvas = new PdfCanvas(page);
            canvas
                    .saveState()
                    .beginText()
                    .moveText(36, 700)
                    .setFontAndSize(new PdfStandardFont(pdfDoc, PdfStandardFont.Helvetica), 72)
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
        Assert.assertEquals(author, info.get("Author"));
        Assert.assertEquals(creator, info.get("Creator"));
        Assert.assertEquals(title, info.get("Title"));
        for (int i = 1; i <= count; i++) {
            PdfDictionary page = reader.getPageN(i);
            Assert.assertEquals(com.itextpdf.text.pdf.PdfName.PAGE, page.get(com.itextpdf.text.pdf.PdfName.TYPE));
        }
        reader.close();
    }

    @Test
    public void copyPagesTest1() throws IOException, PdfException, DocumentException, InterruptedException {
        FileOutputStream fos1 = new FileOutputStream(destinationFolder + "copyPages1_1.pdf");
        PdfWriter writer1 = new PdfWriter(fos1);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);

        PdfPage page1 = pdfDoc1.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page1);
        canvas.rectangle(100, 600, 100, 100);
        canvas.fill();
        canvas.beginText();
        canvas.setFontAndSize(new PdfStandardFont(pdfDoc1, PdfStandardFont.Courier), 12);
        canvas.setTextMatrix(1, 0, 0, 1, 100, 500);
        canvas.showText("Hello World!");
        canvas.endText();
        canvas.release();

        FileOutputStream fos2 = new FileOutputStream(destinationFolder + "copyPages1_2.pdf");
        PdfWriter writer2 = new PdfWriter(fos2);
        PdfDocument pdfDoc2 = new PdfDocument(writer2);
        PdfPage page2 = page1.copy(pdfDoc2);
        pdfDoc2.addPage(page2);

        page1.flush();
        page2.flush();

        pdfDoc1.close();
        pdfDoc2.close();

        PdfReader reader = new PdfReader(destinationFolder + "copyPages1_2.pdf");
        Assert.assertEquals("Rebuilt", false, reader.isRebuilt());
        com.itextpdf.text.pdf.PdfDictionary page = reader.getPageN(1);
        Assert.assertNotNull(page.get(PdfName.PARENT));
        reader.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "copyPages1_1.pdf", destinationFolder + "copyPages1_2.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void copyPagesTest2() throws IOException, PdfException, DocumentException, InterruptedException {
        FileOutputStream fos1 = new FileOutputStream(destinationFolder + "copyPages2_1.pdf");
        PdfWriter writer1 = new PdfWriter(fos1);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);

        for (int i = 0; i < 10; i++) {
            PdfPage page1 = pdfDoc1.addNewPage();
            PdfCanvas canvas = new PdfCanvas(page1);
            canvas.rectangle(100, 600, 100, 100);
            canvas.fill();
            canvas.beginText();
            canvas.setFontAndSize(new PdfStandardFont(pdfDoc1, PdfStandardFont.Courier), 12);
            canvas.setTextMatrix(1, 0, 0, 1, 100, 500);
            canvas.showText(String.format("Page_%d", i + 1));
            canvas.endText();
            canvas.release();
        }

        FileOutputStream fos2 = new FileOutputStream(destinationFolder + "copyPages2_2.pdf");
        PdfWriter writer2 = new PdfWriter(fos2);
        PdfDocument pdfDoc2 = new PdfDocument(writer2);
        for (int i = 9; i >= 0; i--) {
            PdfPage page2 = pdfDoc1.getPage(i + 1).copy(pdfDoc2);
            pdfDoc2.addPage(page2);
        }

        pdfDoc1.close();
        pdfDoc2.close();

        PdfReader reader = new PdfReader(destinationFolder + "copyPages2_2.pdf");
        Assert.assertEquals("Rebuilt", false, reader.isRebuilt());
        com.itextpdf.text.pdf.PdfDictionary page = reader.getPageN(1);
        Assert.assertNotNull(page.get(PdfName.PARENT));
        reader.close();

        CompareTool cmpTool = new CompareTool();
        PdfReader reader1 = new PdfReader(destinationFolder + "copyPages2_1.pdf");
        PdfReader reader2 = new PdfReader(destinationFolder + "copyPages2_2.pdf");

        for (int i = 0; i < 10; i++) {
            PdfDictionary page1 = reader1.getPageN(i + 1);
            PdfDictionary page2 = reader2.getPageN(10 - i);
            Assert.assertTrue(cmpTool.compareDictionaries(page1, page2));
        }

        reader1.close();
        reader2.close();
    }

    @Test
    public void copyPagesTest3() throws IOException, PdfException, DocumentException, InterruptedException {
        FileOutputStream fos1 = new FileOutputStream(destinationFolder + "copyPages3_1.pdf");
        PdfWriter writer1 = new PdfWriter(fos1);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);

        PdfPage page1 = pdfDoc1.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page1);
        canvas.rectangle(100, 600, 100, 100);
        canvas.fill();
        canvas.beginText();
        canvas.setFontAndSize(new PdfStandardFont(pdfDoc1, PdfStandardFont.Courier), 12);
        canvas.setTextMatrix(1, 0, 0, 1, 100, 500);
        canvas.showText("Hello World!!!");
        canvas.endText();
        canvas.release();

        FileOutputStream fos2 = new FileOutputStream(destinationFolder + "copyPages3_2.pdf");
        PdfWriter writer2 = new PdfWriter(fos2);
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
        PdfReader reader1 = new PdfReader(destinationFolder + "copyPages3_1.pdf");
        Assert.assertEquals("Rebuilt", false, reader1.isRebuilt());
        PdfDictionary p1 = reader1.getPageN(1);
        PdfReader reader2 = new PdfReader(destinationFolder + "copyPages3_2.pdf");
        Assert.assertEquals("Rebuilt", false, reader2.isRebuilt());
        for (int i = 0; i < 10; i++) {
            PdfDictionary p2 = reader2.getPageN(i + 1);
            Assert.assertTrue(cmpTool.compareDictionaries(p1, p2));
        }
        reader1.close();
        reader2.close();
    }

    @Test
    public void copyPagesTest4() throws IOException, PdfException, DocumentException, InterruptedException {
        FileOutputStream fos1 = new FileOutputStream(destinationFolder + "copyPages4_1.pdf");
        PdfWriter writer1 = new PdfWriter(fos1);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);

        for (int i = 0; i < 5; i++) {
            PdfPage page1 = pdfDoc1.addNewPage();
            PdfCanvas canvas = new PdfCanvas(page1);
            canvas.rectangle(100, 600, 100, 100);
            canvas.fill();
            canvas.beginText();
            canvas.setFontAndSize(new PdfStandardFont(pdfDoc1, PdfStandardFont.Courier), 12);
            canvas.setTextMatrix(1, 0, 0, 1, 100, 500);
            canvas.showText(String.format("Page_%d", i + 1));
            canvas.endText();
            canvas.release();
        }

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
        PdfReader reader1 = new PdfReader(destinationFolder + "copyPages4_1.pdf");
        Assert.assertEquals("Rebuilt", false, reader1.isRebuilt());

        for (int i = 0; i < 5; i++) {
            PdfDictionary page1 = reader1.getPageN(i + 1);
            PdfReader reader2 = new PdfReader(destinationFolder + String.format("copyPages4_%d.pdf", i + 2));
            PdfDictionary page = reader2.getPageN(1);
            Assert.assertTrue(cmpTool.compareDictionaries(page1, page));
            reader2.close();
        }

        reader1.close();
    }


    @Test
    public void copyPagesTest5() throws IOException, PdfException, DocumentException, InterruptedException {

        List<PdfDocument> docs = new ArrayList<PdfDocument>();

        for (int i = 0; i < 3; i++) {
            FileOutputStream fos1 = new FileOutputStream(destinationFolder + String.format("copyPages5_%d.pdf", i + 1));
            PdfWriter writer1 = new PdfWriter(fos1);
            PdfDocument pdfDoc1 = new PdfDocument(writer1);
            docs.add(pdfDoc1);
            PdfPage page1 = pdfDoc1.addNewPage();
            PdfCanvas canvas = new PdfCanvas(page1);
            canvas.rectangle(100, 600, 100, 100);
            canvas.fill();
            canvas.beginText();
            canvas.setFontAndSize(new PdfStandardFont(pdfDoc1, PdfStandardFont.Courier), 12);
            canvas.setTextMatrix(1, 0, 0, 1, 100, 500);
            canvas.showText(String.format("Page_%d", i + 1));
            canvas.endText();
            canvas.release();
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
            Assert.assertEquals("Rebuilt", false, reader1.isRebuilt());
            PdfReader reader2 = new PdfReader(destinationFolder + "copyPages5_4.pdf");
            Assert.assertEquals("Rebuilt", false, reader2.isRebuilt());
            PdfDictionary page1 = reader1.getPageN(1);
            PdfDictionary page2 = reader2.getPageN(i + 1);
            Assert.assertTrue(cmpTool.compareDictionaries(page1, page2));
            reader1.close();
            reader2.close();
        }

    }

    @Test
    public void copyPagesTest6() throws IOException, PdfException, DocumentException, InterruptedException {

        FileOutputStream fos1 = new FileOutputStream(destinationFolder + "copyPages6_1.pdf");
        PdfWriter writer1 = new PdfWriter(fos1);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        PdfPage page1 = pdfDoc1.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page1);
        canvas.rectangle(100, 600, 100, 100);
        canvas.fill();
        canvas.beginText();
        canvas.setFontAndSize(new PdfStandardFont(pdfDoc1, PdfStandardFont.Courier), 12);
        canvas.setTextMatrix(1, 0, 0, 1, 100, 500);
        canvas.showText("Hello World!");
        canvas.endText();
        canvas.release();

        FileOutputStream fos2 = new FileOutputStream(destinationFolder + "copyPages6_2.pdf");
        PdfWriter writer2 = new PdfWriter(fos2);
        PdfDocument pdfDoc2 = new PdfDocument(writer2);
        pdfDoc2.addPage(pdfDoc1.getPage(1).copy(pdfDoc2));

        FileOutputStream fos3 = new FileOutputStream(destinationFolder + "copyPages6_3.pdf");
        PdfWriter writer3 = new PdfWriter(fos3);
        PdfDocument pdfDoc3 = new PdfDocument(writer3);
        pdfDoc3.addPage(pdfDoc2.getPage(1).copy(pdfDoc3));

        pdfDoc1.addPage(pdfDoc3.getPage(1).copy(pdfDoc1));

        pdfDoc1.close();
        pdfDoc2.close();
        pdfDoc3.close();


        CompareTool cmpTool = new CompareTool();
        for (int i = 0; i < 3; i++) {
            PdfReader reader1 = new PdfReader(destinationFolder + "copyPages6_1.pdf");
            Assert.assertEquals("Rebuilt", false, reader1.isRebuilt());
            PdfReader reader2 = new PdfReader(destinationFolder + "copyPages6_2.pdf");
            Assert.assertEquals("Rebuilt", false, reader2.isRebuilt());
            PdfReader reader3 = new PdfReader(destinationFolder + "copyPages6_3.pdf");
            Assert.assertEquals("Rebuilt", false, reader3.isRebuilt());
            Assert.assertTrue(cmpTool.compareDictionaries(reader1.getPageN(1), reader1.getPageN(2)));
            Assert.assertTrue(cmpTool.compareDictionaries(reader1.getPageN(2), reader2.getPageN(1)));
            Assert.assertTrue(cmpTool.compareDictionaries(reader2.getPageN(1), reader3.getPageN(1)));
            reader1.close();
            reader2.close();
            reader3.close();
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

        com.itextpdf.core.pdf.PdfDictionary tag2 = new com.itextpdf.core.pdf.PdfDictionary(new HashMap<com.itextpdf.core.pdf.PdfName, PdfObject>(){{put(new com.itextpdf.core.pdf.PdfName("Tag"), new PdfNumber(2));}});
        com.itextpdf.core.pdf.PdfDictionary tag3 = new com.itextpdf.core.pdf.PdfDictionary(new HashMap<com.itextpdf.core.pdf.PdfName, PdfObject>(){{put(new com.itextpdf.core.pdf.PdfName("Tag"), new PdfNumber(3).makeIndirect(document));}});

        canvas.beginMarkedContent(new com.itextpdf.core.pdf.PdfName("Tag1")).endMarkedContent().
                beginMarkedContent(new com.itextpdf.core.pdf.PdfName("Tag2"), tag2).endMarkedContent().
                beginMarkedContent(new com.itextpdf.core.pdf.PdfName("Tag3"), (com.itextpdf.core.pdf.PdfDictionary) tag3.makeIndirect(document)).endMarkedContent();

        canvas.release();
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "markedContentTest2.pdf", sourceFolder + "cmp_markedContentTest2.pdf", destinationFolder, "diff_"));
    }


}
