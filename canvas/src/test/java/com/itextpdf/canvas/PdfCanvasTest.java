package com.itextpdf.canvas;

import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfPage;
import com.itextpdf.core.pdf.PdfReader;
import com.itextpdf.core.pdf.PdfWriter;
import com.itextpdf.text.pdf.PdfDictionary;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

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
        PdfCanvas canvas = new PdfCanvas(page1.getContentStream());
        canvas.rectangle(100, 100, 100, 100).fill();
        page1.flush();
        pdfDoc.close();

        com.itextpdf.text.pdf.PdfReader reader = new com.itextpdf.text.pdf.PdfReader(destinationFolder + "simpleCanvas.pdf");
        HashMap<String,String> info = reader.getInfo();
        Assert.assertEquals(author, info.get("Author"));
        Assert.assertEquals(creator, info.get("Creator"));
        Assert.assertEquals(title, info.get("Title"));
        PdfDictionary page = reader.getPageN(1);
        reader.close();
    }


}
