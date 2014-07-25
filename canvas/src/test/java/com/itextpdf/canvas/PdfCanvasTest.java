package com.itextpdf.canvas;

import com.itextpdf.core.exceptions.PdfException;
import com.itextpdf.core.pdf.*;
import com.itextpdf.text.pdf.PdfDictionary;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;


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
        PdfCanvas canvas = new PdfCanvas(page1.getContentStream());
        canvas.rectangle(100, 100, 100, 100).fill();
        pdfDoc.close();

        com.itextpdf.text.pdf.PdfReader reader = new com.itextpdf.text.pdf.PdfReader(destinationFolder + "simpleCanvas.pdf");
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
        PdfCanvas canvas = new PdfCanvas(page1.getContentStream());
        canvas.rectangle(100, 100, 100, 100).fill();
        page1.flush();
        pdfDoc.close();

        com.itextpdf.text.pdf.PdfReader reader = new com.itextpdf.text.pdf.PdfReader(destinationFolder + "simpleCanvasWithPageFlush.pdf");
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
        PdfCanvas canvas = new PdfCanvas(page1.getContentStream());
        canvas.rectangle(100, 100, 100, 100).fill();
        pdfDoc.close();

        com.itextpdf.text.pdf.PdfReader reader = new com.itextpdf.text.pdf.PdfReader(destinationFolder + "simpleCanvasWithFullCompression.pdf");
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
        PdfCanvas canvas = new PdfCanvas(page1.getContentStream());
        canvas.rectangle(100, 100, 100, 100).fill();
        page1.flush();
        pdfDoc.close();

        com.itextpdf.text.pdf.PdfReader reader = new com.itextpdf.text.pdf.PdfReader(destinationFolder + "simpleCanvasWithPageFlushAndFullCompression.pdf");
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

        final String author = "Alexander Chingarev";
        final String creator = "iText 6";
        final String title = "Empty iText 6 Document";

        FileOutputStream fos = new FileOutputStream(destinationFolder + "1000PagesDocument.pdf");
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);
        for (int i = 0; i < 1000; i ++) {
            PdfPage page = pdfDoc.addNewPage();
            PdfCanvas canvas = new PdfCanvas(page.getContentStream());
            canvas.rectangle(100, 100, 100, 100).fill();
            if (i % 2 == 0)
                page.flush();
        }
        pdfDoc.close();

        com.itextpdf.text.pdf.PdfReader reader = new com.itextpdf.text.pdf.PdfReader(destinationFolder + "1000PagesDocument.pdf");
        HashMap<String, String> info = reader.getInfo();
        Assert.assertEquals(author, info.get("Author"));
        Assert.assertEquals(creator, info.get("Creator"));
        Assert.assertEquals(title, info.get("Title"));
        PdfDictionary page = reader.getPageN(1);
        Assert.assertEquals(com.itextpdf.text.pdf.PdfName.PAGE, page.get(com.itextpdf.text.pdf.PdfName.TYPE));
        reader.close();
    }

    @Test
    public void create1000PagesDocumentWithFullCompression() throws IOException, PdfException {

        final String author = "Alexander Chingarev";
        final String creator = "iText 6";
        final String title = "Empty iText 6 Document";

        FileOutputStream fos = new FileOutputStream(destinationFolder + "1000PagesDocumentWithFullCompression.pdf");
        PdfWriter writer = new PdfWriter(new BufferedOutputStream(fos));
        writer.setFullCompression(true);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);
        for (int i = 0; i < 1000; i ++) {
            PdfPage page = pdfDoc.addNewPage();
            PdfCanvas canvas = new PdfCanvas(page.getContentStream());
            canvas.rectangle(100, 100, 100, 100).fill();
            page.flush();
        }
        pdfDoc.close();

        com.itextpdf.text.pdf.PdfReader reader = new com.itextpdf.text.pdf.PdfReader(destinationFolder + "1000PagesDocumentWithFullCompression.pdf");
        HashMap<String, String> info = reader.getInfo();
        Assert.assertEquals(author, info.get("Author"));
        Assert.assertEquals(creator, info.get("Creator"));
        Assert.assertEquals(title, info.get("Title"));
        PdfDictionary page = reader.getPageN(1);
        Assert.assertEquals(com.itextpdf.text.pdf.PdfName.PAGE, page.get(com.itextpdf.text.pdf.PdfName.TYPE));
        reader.close();
    }

}
