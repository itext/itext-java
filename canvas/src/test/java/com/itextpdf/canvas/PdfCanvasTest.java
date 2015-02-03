package com.itextpdf.canvas;

import com.itextpdf.basics.PdfException;
import com.itextpdf.canvas.color.*;
import com.itextpdf.core.fonts.PdfStandardFont;
import com.itextpdf.core.pdf.*;
import com.itextpdf.core.pdf.colorspace.PdfCieBasedCs;
import com.itextpdf.core.pdf.colorspace.PdfDeviceCs;
import com.itextpdf.core.pdf.colorspace.PdfSpecialCs;
import com.itextpdf.core.pdf.extgstate.PdfExtGState;
import com.itextpdf.core.pdf.tagging.IPdfTag;
import com.itextpdf.core.pdf.tagging.PdfMcrDictionary;
import com.itextpdf.core.pdf.tagging.PdfMcrNumber;
import com.itextpdf.core.pdf.tagging.PdfStructElem;
import com.itextpdf.testutils.CompareTool;
import com.itextpdf.text.DocumentException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.*;
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
        Assert.assertEquals("Author", author, info.get("Author"));
        Assert.assertEquals("Creator", creator, info.get("Creator"));
        Assert.assertEquals("Title", title, info.get("Title"));
        Assert.assertEquals("Page count", 1, reader.getNumberOfPages());
        com.itextpdf.text.pdf.PdfDictionary page = reader.getPageN(1);
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
        Assert.assertEquals("Author", author, info.get("Author"));
        Assert.assertEquals("Creator", creator, info.get("Creator"));
        Assert.assertEquals("Title", title, info.get("Title"));
        Assert.assertEquals("Page count", 1, reader.getNumberOfPages());
        com.itextpdf.text.pdf.PdfDictionary page = reader.getPageN(1);
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
        Assert.assertEquals("Author", author, info.get("Author"));
        Assert.assertEquals("Creator", creator, info.get("Creator"));
        Assert.assertEquals("Title", title, info.get("Title"));
        Assert.assertEquals("Page count", 1, reader.getNumberOfPages());
        com.itextpdf.text.pdf.PdfDictionary page = reader.getPageN(1);
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
        Assert.assertEquals("Author", author, info.get("Author"));
        Assert.assertEquals("Creator", creator, info.get("Creator"));
        Assert.assertEquals("Title", title, info.get("Title"));
        Assert.assertEquals("Page count", 1, reader.getNumberOfPages());
        com.itextpdf.text.pdf.PdfDictionary page = reader.getPageN(1);
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
        Assert.assertEquals("Author", author, info.get("Author"));
        Assert.assertEquals("Creator", creator, info.get("Creator"));
        Assert.assertEquals("Title", title, info.get("Title"));
        Assert.assertEquals("Page count", 1, reader.getNumberOfPages());
        com.itextpdf.text.pdf.PdfDictionary page = reader.getPageN(1);
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
        Assert.assertEquals("Author", author, info.get("Author"));
        Assert.assertEquals("Creator", creator, info.get("Creator"));
        Assert.assertEquals("Title", title, info.get("Title"));
        Assert.assertEquals("Page count", 1, reader.getNumberOfPages());
        com.itextpdf.text.pdf.PdfDictionary page = reader.getPageN(1);
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
    public void create1000PagesDocumentWithText() throws IOException, PdfException {
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
                    .setFontAndSize(new PdfStandardFont(pdfDoc, PdfStandardFont.Courier), 16)
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
    public void create1000PagesDocumentWithFullCompression() throws IOException, PdfException {
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
    public void create100PagesDocumentWithFullCompression() throws IOException, PdfException {
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
    public void create197PagesDocumentWithFullCompression() throws IOException, PdfException {
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
    public void create10PagesDocumentWithFullCompression() throws IOException, PdfException {
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

        com.itextpdf.text.pdf.PdfReader reader = new com.itextpdf.text.pdf.PdfReader(destinationFolder + "copyPages1_2.pdf");
        Assert.assertEquals("Rebuilt", false, reader.isRebuilt());
        com.itextpdf.text.pdf.PdfDictionary page = reader.getPageN(1);
        Assert.assertNotNull(page.get(com.itextpdf.text.pdf.PdfName.PARENT));
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

        com.itextpdf.text.pdf.PdfReader reader = new com.itextpdf.text.pdf.PdfReader(destinationFolder + "copyPages2_2.pdf");
        Assert.assertEquals("Rebuilt", false, reader.isRebuilt());
        com.itextpdf.text.pdf.PdfDictionary page = reader.getPageN(1);
        Assert.assertNotNull(page.get(com.itextpdf.text.pdf.PdfName.PARENT));
        reader.close();

        CompareTool cmpTool = new CompareTool();
        com.itextpdf.text.pdf.PdfReader reader1 = new com.itextpdf.text.pdf.PdfReader(destinationFolder + "copyPages2_1.pdf");
        com.itextpdf.text.pdf.PdfReader reader2 = new com.itextpdf.text.pdf.PdfReader(destinationFolder + "copyPages2_2.pdf");

        for (int i = 0; i < 10; i++) {
            com.itextpdf.text.pdf.PdfDictionary page1 = reader1.getPageN(i + 1);
            com.itextpdf.text.pdf.PdfDictionary page2 = reader2.getPageN(10 - i);
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
        com.itextpdf.text.pdf.PdfReader reader1 = new com.itextpdf.text.pdf.PdfReader(destinationFolder + "copyPages3_1.pdf");
        Assert.assertEquals("Rebuilt", false, reader1.isRebuilt());
        com.itextpdf.text.pdf.PdfDictionary p1 = reader1.getPageN(1);
        com.itextpdf.text.pdf.PdfReader reader2 = new com.itextpdf.text.pdf.PdfReader(destinationFolder + "copyPages3_2.pdf");
        Assert.assertEquals("Rebuilt", false, reader2.isRebuilt());
        for (int i = 0; i < 10; i++) {
            com.itextpdf.text.pdf.PdfDictionary p2 = reader2.getPageN(i + 1);
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
        com.itextpdf.text.pdf.PdfReader reader1 = new com.itextpdf.text.pdf.PdfReader(destinationFolder + "copyPages4_1.pdf");
        Assert.assertEquals("Rebuilt", false, reader1.isRebuilt());

        for (int i = 0; i < 5; i++) {
            com.itextpdf.text.pdf.PdfDictionary page1 = reader1.getPageN(i + 1);
            com.itextpdf.text.pdf.PdfReader reader2 = new com.itextpdf.text.pdf.PdfReader(destinationFolder + String.format("copyPages4_%d.pdf", i + 2));
            com.itextpdf.text.pdf.PdfDictionary page = reader2.getPageN(1);
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
            com.itextpdf.text.pdf.PdfReader reader1 = new com.itextpdf.text.pdf.PdfReader(destinationFolder + String.format("copyPages5_%d.pdf", i + 1));
            Assert.assertEquals("Rebuilt", false, reader1.isRebuilt());
            com.itextpdf.text.pdf.PdfReader reader2 = new com.itextpdf.text.pdf.PdfReader(destinationFolder + "copyPages5_4.pdf");
            Assert.assertEquals("Rebuilt", false, reader2.isRebuilt());
            com.itextpdf.text.pdf.PdfDictionary page1 = reader1.getPageN(1);
            com.itextpdf.text.pdf.PdfDictionary page2 = reader2.getPageN(i + 1);
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
            com.itextpdf.text.pdf.PdfReader reader1 = new com.itextpdf.text.pdf.PdfReader(destinationFolder + "copyPages6_1.pdf");
            Assert.assertEquals("Rebuilt", false, reader1.isRebuilt());
            com.itextpdf.text.pdf.PdfReader reader2 = new com.itextpdf.text.pdf.PdfReader(destinationFolder + "copyPages6_2.pdf");
            Assert.assertEquals("Rebuilt", false, reader2.isRebuilt());
            com.itextpdf.text.pdf.PdfReader reader3 = new com.itextpdf.text.pdf.PdfReader(destinationFolder + "copyPages6_3.pdf");
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
        PdfExtGState egs = new PdfExtGState(document);
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

        canvas.setFillColor(DeviceRgb.Red).rectangle(50, 500, 50, 50).fill();
        canvas.setFillColor(DeviceRgb.Green).rectangle(150, 500, 50, 50).fill();
        canvas.setFillColor(DeviceRgb.Blue).rectangle(250, 500, 50, 50).fill();
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

        PdfDeviceCs.Rgb rgb = new PdfDeviceCs.Rgb(document);
        Color red = new Color(rgb, new float[]{1, 0, 0});
        Color green = new Color(rgb, new float[]{0, 1, 0});
        Color blue = new Color(rgb, new float[]{0, 0, 1});
        PdfDeviceCs.Cmyk cmyk = new PdfDeviceCs.Cmyk(document);
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

        PdfSpecialCs.Indexed indexed = new PdfSpecialCs.Indexed(document, com.itextpdf.core.pdf.PdfName.DeviceRGB, 255, new PdfString(new String(bytes)));
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
    public void taggingTest01() throws Exception {
        FileOutputStream fos = new FileOutputStream(destinationFolder + "taggingTest01.pdf");
        PdfWriter writer = new PdfWriter(fos);
        writer.setCompressionLevel(PdfWriter.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(writer);
        document.setTagged();
        PdfStructElem doc = document.getStructTreeRoot().addKid(new PdfStructElem(document, com.itextpdf.core.pdf.PdfName.Document));

        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.beginText();
        canvas.setFontAndSize(new PdfStandardFont(document, PdfStandardFont.Courier), 24);
        canvas.setTextMatrix(1, 0, 0, 1, 32, 512);
        PdfStructElem paragraph = doc.addKid(new PdfStructElem(document, com.itextpdf.core.pdf.PdfName.P));
        PdfStructElem span1 = paragraph.addKid(new PdfStructElem(document, com.itextpdf.core.pdf.PdfName.Span, page));

        canvas.openTag(span1.addKid(new PdfMcrNumber(page, span1)));
        canvas.showText("Hello ");
        canvas.closeTag();
        canvas.openTag(span1.addKid(new PdfMcrDictionary(page, span1)));
        canvas.showText("World");
        canvas.closeTag();
        canvas.endText();
        canvas.release();
        page.flush();

        page = document.addNewPage();
        canvas = new PdfCanvas(page);
        canvas.beginText();
        canvas.setFontAndSize(new PdfStandardFont(document, PdfStandardFont.Helvetica), 24);
        canvas.setTextMatrix(1, 0, 0, 1, 32, 512);
        paragraph = doc.addKid(new PdfStructElem(document, com.itextpdf.core.pdf.PdfName.P));
        span1 = paragraph.addKid(new PdfStructElem(document, com.itextpdf.core.pdf.PdfName.Span, page));
        canvas.openTag(span1.addKid(new PdfMcrNumber(page, span1)));
        canvas.showText("Hello ");
        canvas.closeTag();
        PdfStructElem span2 = paragraph.addKid(new PdfStructElem(document, com.itextpdf.core.pdf.PdfName.Span, page));
        canvas.openTag(span2.addKid(new PdfMcrNumber(page, span2)));
        canvas.showText("World");
        canvas.closeTag();
        canvas.endText();
        canvas.release();
        page.flush();

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "taggingTest01.pdf", sourceFolder + "cmp_taggingTest01.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void taggingTest02() throws Exception {
        FileOutputStream fos = new FileOutputStream(destinationFolder + "taggingTest02.pdf");
        PdfWriter writer = new PdfWriter(fos);
        writer.setCompressionLevel(PdfWriter.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(writer);
        document.setTagged();
        document.getStructTreeRoot().getRoleMap().put(new com.itextpdf.core.pdf.PdfName("Chunk"), com.itextpdf.core.pdf.PdfName.Span);
        PdfStructElem doc = document.getStructTreeRoot().addKid(new PdfStructElem(document, com.itextpdf.core.pdf.PdfName.Document));

        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.beginText();
        canvas.setFontAndSize(new PdfStandardFont(document, PdfStandardFont.Courier), 24);
        canvas.setTextMatrix(1, 0, 0, 1, 32, 512);
        PdfStructElem paragraph = doc.addKid(new PdfStructElem(document, com.itextpdf.core.pdf.PdfName.P));
        PdfStructElem span1 = paragraph.addKid(new PdfStructElem(document, com.itextpdf.core.pdf.PdfName.Span, page));
        canvas.openTag(span1.addKid(new PdfMcrNumber(page, span1)));
        canvas.showText("Hello ");
        canvas.closeTag();
        PdfStructElem span2 = paragraph.addKid(new PdfStructElem(document, new com.itextpdf.core.pdf.PdfName("Chunk"), page));
        canvas.openTag(span2.addKid(new PdfMcrNumber(page, span2)));
        canvas.showText("World");
        canvas.closeTag();
        canvas.endText();
        canvas.release();
        page.flush();

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "taggingTest02.pdf", sourceFolder + "cmp_taggingTest02.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void taggingTest03() throws Exception {
        FileOutputStream fos = new FileOutputStream(destinationFolder + "taggingTest03.pdf");
        PdfWriter writer = new PdfWriter(fos);
        writer.setCompressionLevel(PdfWriter.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(writer);
        document.setTagged();
        document.getStructTreeRoot().getRoleMap().put(new com.itextpdf.core.pdf.PdfName("Chunk"), com.itextpdf.core.pdf.PdfName.Span);
        PdfStructElem doc = document.getStructTreeRoot().addKid(new PdfStructElem(document, com.itextpdf.core.pdf.PdfName.Document));

        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.beginText();
        canvas.setFontAndSize(new PdfStandardFont(document, PdfStandardFont.Courier), 24);
        canvas.setTextMatrix(1, 0, 0, 1, 32, 512);
        PdfStructElem paragraph = doc.addKid(new PdfStructElem(document, com.itextpdf.core.pdf.PdfName.P));
        PdfStructElem span1 = paragraph.addKid(new PdfStructElem(document, com.itextpdf.core.pdf.PdfName.Span, page));
        canvas.openTag(span1.addKid(new PdfMcrNumber(page, span1)));
        canvas.showText("Hello ");
        canvas.closeTag();
        PdfStructElem span2 = paragraph.addKid(new PdfStructElem(document, new com.itextpdf.core.pdf.PdfName("Chunk"), page));
        canvas.openTag(span2.addKid(new PdfMcrNumber(page, span2)));
        canvas.showText("World");
        canvas.closeTag();
        canvas.endText();
        canvas.release();
        page.flush();

        page = document.addNewPage();
        canvas = new PdfCanvas(page);
        canvas.beginText();
        canvas.setFontAndSize(new PdfStandardFont(document, PdfStandardFont.Helvetica), 24);
        canvas.setTextMatrix(1, 0, 0, 1, 32, 512);
        paragraph = doc.addKid(new PdfStructElem(document, com.itextpdf.core.pdf.PdfName.P));
        span1 = paragraph.addKid(new PdfStructElem(document, com.itextpdf.core.pdf.PdfName.Span, page));
        canvas.openTag(span1.addKid(new PdfMcrNumber(page, span1)));
        canvas.showText("Hello ");
        canvas.closeTag();
        span2 = paragraph.addKid(new PdfStructElem(document, new com.itextpdf.core.pdf.PdfName("Chunk"), page));
        canvas.openTag(span2.addKid(new PdfMcrNumber(page, span2)));
        canvas.showText("World");
        canvas.closeTag();
        canvas.endText();
        canvas.release();
        page.flush();

        document.close();

        com.itextpdf.core.pdf.PdfReader reader = new com.itextpdf.core.pdf.PdfReader(new FileInputStream(destinationFolder + "taggingTest03.pdf"));
        document = new PdfDocument(reader);
        Assert.assertEquals(2, document.getNextStructParentIndex().intValue());
        PdfPage page1 = document.getPage(1);
        Assert.assertEquals(0, page1.getStructParentIndex().intValue());
        Assert.assertEquals(2, page1.getNextMcid());
        document.close();
    }

    @Test
    public void taggingTest04() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        writer.setCompressionLevel(PdfWriter.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(writer);
        document.setTagged();
        document.getStructTreeRoot().getRoleMap().put(new com.itextpdf.core.pdf.PdfName("Chunk"), com.itextpdf.core.pdf.PdfName.Span);
        PdfStructElem doc = document.getStructTreeRoot().addKid(new PdfStructElem(document, com.itextpdf.core.pdf.PdfName.Document));

        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.beginText();
        canvas.setFontAndSize(new PdfStandardFont(document, PdfStandardFont.Courier), 24);
        canvas.setTextMatrix(1, 0, 0, 1, 32, 512);
        PdfStructElem paragraph = doc.addKid(new PdfStructElem(document, com.itextpdf.core.pdf.PdfName.P));
        PdfStructElem span1 = paragraph.addKid(new PdfStructElem(document, com.itextpdf.core.pdf.PdfName.Span, page));
        canvas.openTag(span1.addKid(new PdfMcrNumber(page, span1)));
        canvas.showText("Hello ");
        canvas.closeTag();
        PdfStructElem span2 = paragraph.addKid(new PdfStructElem(document, new com.itextpdf.core.pdf.PdfName("Chunk"), page));
        canvas.openTag(span2.addKid(new PdfMcrNumber(page, span2)));
        canvas.showText("World");
        canvas.closeTag();
        canvas.endText();
        canvas.release();
        page.flush();

        document.close();
        byte[] bytes = baos.toByteArray();

        com.itextpdf.core.pdf.PdfReader reader = new com.itextpdf.core.pdf.PdfReader(new ByteArrayInputStream(bytes));
        writer = new PdfWriter(new FileOutputStream(destinationFolder + "taggingTest04.pdf"));
        writer.setCompressionLevel(PdfWriter.NO_COMPRESSION);
        document = new PdfDocument(reader, writer);

        page = document.getPage(1);
        canvas = new PdfCanvas(page);

        List<IPdfTag> elems = page.getPageTags();

        canvas.beginText();
        canvas.setFontAndSize(new PdfStandardFont(document, PdfStandardFont.Courier), 24);
        canvas.setTextMatrix(1, 0, 0, 1, 32, 490);

        //Inserting span between of 2 existing ones.
        span1 = ((PdfStructElem) elems.get(0).getParent().getParent()).addKid(1, new PdfStructElem(document, com.itextpdf.core.pdf.PdfName.Span, page));
        canvas.openTag(span1.addKid(new PdfMcrNumber(page, span1)));
        canvas.showText("text1");
        canvas.closeTag();

        elems = page.getPageTags();

        //Inserting span at the end.
        IPdfTag elem = elems.get(elems.size() - 1);
        span1 = ((PdfStructElem) elem.getParent().getParent()).addKid(new PdfStructElem(document, com.itextpdf.core.pdf.PdfName.Span, page));
        canvas.openTag(span1.addKid(new PdfMcrNumber(page, span1)));
        canvas.showText("text2");
        canvas.closeTag();

        canvas.endText();

        canvas.release();
        page.flush();

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "taggingTest04.pdf", sourceFolder + "cmp_taggingTest04.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void taggingTest05() throws Exception {
        FileInputStream fis = new FileInputStream(sourceFolder + "iphone_user_guide.pdf");
        PdfReader reader = new PdfReader(fis);
        PdfDocument document = new PdfDocument(reader);

        Assert.assertEquals(2072, document.getNextStructParentIndex().intValue());

        document.close();
    }


}
