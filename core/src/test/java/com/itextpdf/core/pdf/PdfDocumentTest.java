package com.itextpdf.core.pdf;

import com.itextpdf.basics.PdfException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class PdfDocumentTest {

    static final public String sourceFolder = "./src/test/resources/com/itextpdf/core/pdf/PdfDocumentTest/";
    static final public String destinationFolder = "./target/test/com/itextpdf/core/pdf/PdfDocumentTest/";

    @BeforeClass
    static public void beforeClass() {
        new File(destinationFolder).mkdirs();
    }

    @Test
    public void stamping1() throws IOException, PdfException {
        FileOutputStream fos1 = new FileOutputStream(destinationFolder + "stamping1_1.pdf");
        PdfWriter writer1 = new PdfWriter(fos1);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        pdfDoc1.getInfo().setAuthor("Alexander Chingarev").
                setCreator("iText 6").
                setTitle("Empty iText 6 Document");
        PdfPage page1 = pdfDoc1.addNewPage();
        page1.getContentStream(0).getOutputStream().write(PdfWriter.getIsoBytes("%Hello World\n"));
        page1.flush();
        pdfDoc1.close();

        FileInputStream fis2 = new FileInputStream(destinationFolder + "stamping1_1.pdf");
        PdfReader reader2 = new PdfReader(fis2);
        FileOutputStream fos2 = new FileOutputStream(destinationFolder + "stamping1_2.pdf");
        PdfWriter writer2 = new PdfWriter(fos2);
        PdfDocument pdfDoc2 = new PdfDocument(reader2, writer2);
        pdfDoc2.getInfo().setCreator("iText 7").setTitle("Empty iText 7 Document");
        pdfDoc2.close();

        com.itextpdf.text.pdf.PdfReader reader = new com.itextpdf.text.pdf.PdfReader(destinationFolder + "stamping1_2.pdf");
        Assert.assertEquals("Rebuilt", false, reader.isRebuilt());
        com.itextpdf.text.pdf.PdfDictionary trailer = reader.getTrailer();
        com.itextpdf.text.pdf.PdfDictionary info = trailer.getAsDict(com.itextpdf.text.pdf.PdfName.INFO);
        com.itextpdf.text.pdf.PdfString creator = info.getAsString(com.itextpdf.text.pdf.PdfName.CREATOR);
        Assert.assertEquals("iText 7", creator.toString());
        byte[] bytes = reader.getPageContent(1);
        Assert.assertEquals("%Hello World\n", new String(bytes));
        reader.close();
    }

    @Test
    public void stamping2() throws IOException, PdfException {
        FileOutputStream fos1 = new FileOutputStream(destinationFolder + "stamping2_1.pdf");
        PdfWriter writer1 = new PdfWriter(fos1);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        PdfPage page1 = pdfDoc1.addNewPage();
        page1.getContentStream(0).getOutputStream().write(PdfWriter.getIsoBytes("%page 1\n"));
        page1.flush();
        pdfDoc1.close();

        FileInputStream fis2 = new FileInputStream(destinationFolder + "stamping2_1.pdf");
        PdfReader reader2 = new PdfReader(fis2);
        FileOutputStream fos2 = new FileOutputStream(destinationFolder + "stamping2_2.pdf");
        PdfWriter writer2 = new PdfWriter(fos2);
        PdfDocument pdfDoc2 = new PdfDocument(reader2, writer2);
        PdfPage page2 = pdfDoc2.addNewPage();
        page2.getContentStream(0).getOutputStream().write(PdfWriter.getIsoBytes("%page 2\n"));
        page2.flush();
        pdfDoc2.close();

        com.itextpdf.text.pdf.PdfReader reader = new com.itextpdf.text.pdf.PdfReader(destinationFolder + "stamping2_2.pdf");
        Assert.assertEquals("Rebuilt", false, reader.isRebuilt());
        byte[] bytes = reader.getPageContent(1);
        Assert.assertEquals("%page 1\n", new String(bytes));
        bytes = reader.getPageContent(2);
        Assert.assertEquals("%page 2\n", new String(bytes));
        reader.close();
    }

    @Test
    public void stamping3() throws IOException, PdfException {
        FileOutputStream fos1 = new FileOutputStream(destinationFolder + "stamping3_1.pdf");
        PdfWriter writer1 = new PdfWriter(fos1);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        PdfPage page1 = pdfDoc1.addNewPage();
        page1.getContentStream(0).getOutputStream().write(PdfWriter.getIsoBytes("%page 1\n"));
        page1.flush();
        pdfDoc1.close();

        FileInputStream fis2 = new FileInputStream(destinationFolder + "stamping3_1.pdf");
        PdfReader reader2 = new PdfReader(fis2);
        FileOutputStream fos2 = new FileOutputStream(destinationFolder + "stamping3_2.pdf");
        PdfWriter writer2 = new PdfWriter(fos2);
        writer2.setFullCompression(true);
        PdfDocument pdfDoc2 = new PdfDocument(reader2, writer2);
        PdfPage page2 = pdfDoc2.addNewPage();
        page2.getContentStream(0).getOutputStream().write(PdfWriter.getIsoBytes("%page 2\n"));
        page2.flush();
        pdfDoc2.close();

        com.itextpdf.text.pdf.PdfReader reader = new com.itextpdf.text.pdf.PdfReader(destinationFolder + "stamping3_2.pdf");
        Assert.assertEquals("Rebuilt", false, reader.isRebuilt());
        byte[] bytes = reader.getPageContent(1);
        Assert.assertEquals("%page 1\n", new String(bytes));
        bytes = reader.getPageContent(2);
        Assert.assertEquals("%page 2\n", new String(bytes));
        reader.close();
    }

    @Test
    public void copying1() throws IOException, PdfException {
        FileOutputStream fos1 = new FileOutputStream(destinationFolder + "copying1_1.pdf");
        PdfWriter writer1 = new PdfWriter(fos1);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        pdfDoc1.getInfo().setAuthor("Alexander Chingarev").
                setCreator("iText 6").
                setTitle("Empty iText 6 Document");
        pdfDoc1.getCatalog().getPdfObject().put(new PdfName("a"), new PdfName("b").makeIndirect(pdfDoc1));
        PdfPage page1 = pdfDoc1.addNewPage();
        page1.flush();
        pdfDoc1.close();

        FileInputStream fis1 = new FileInputStream(destinationFolder + "copying1_1.pdf");
        PdfReader reader1 = new PdfReader(fis1);
        pdfDoc1 = new PdfDocument(reader1);

        FileOutputStream fos2 = new FileOutputStream(destinationFolder + "copying1_2.pdf");
        PdfWriter writer2 = new PdfWriter(fos2);
        PdfDocument pdfDoc2 = new PdfDocument(writer2);
        pdfDoc2.addNewPage();
        pdfDoc2.getInfo().getPdfObject().put(new PdfName("a"), pdfDoc1.getCatalog().getPdfObject().get(new PdfName("a")).copy(pdfDoc2));
        pdfDoc2.close();
        pdfDoc1.close();

        com.itextpdf.text.pdf.PdfReader reader = new com.itextpdf.text.pdf.PdfReader(destinationFolder + "copying1_2.pdf");
        Assert.assertEquals("Rebuilt", false, reader.isRebuilt());
        com.itextpdf.text.pdf.PdfDictionary trailer = reader.getTrailer();
        com.itextpdf.text.pdf.PdfDictionary info = trailer.getAsDict(com.itextpdf.text.pdf.PdfName.INFO);
        com.itextpdf.text.pdf.PdfName b = info.getAsName(new com.itextpdf.text.pdf.PdfName("a"));
        Assert.assertEquals("/b", b.toString());
        reader.close();

    }


    @Test
    public void copying2() throws IOException, PdfException {
        FileOutputStream fos1 = new FileOutputStream(destinationFolder + "copying2_1.pdf");
        PdfWriter writer1 = new PdfWriter(fos1);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        for (int i = 0; i < 10; i++) {
            PdfPage page1 = pdfDoc1.addNewPage();
            page1.getContentStream(0).getOutputStream().write(PdfWriter.getIsoBytes("%page " + String.valueOf(i + 1) + "\n"));
            page1.flush();
        }
        pdfDoc1.close();

        FileInputStream fis1 = new FileInputStream(destinationFolder + "copying2_1.pdf");
        PdfReader reader1 = new PdfReader(fis1);
        pdfDoc1 = new PdfDocument(reader1);

        FileOutputStream fos2 = new FileOutputStream(destinationFolder + "copying2_2.pdf");
        PdfWriter writer2 = new PdfWriter(fos2);
        PdfDocument pdfDoc2 = new PdfDocument(writer2);
        for (int i = 0; i < 10; i++) {
            if (i % 2 == 0) {
                pdfDoc2.addPage(pdfDoc1.getPage(i + 1).copy(pdfDoc2));
            }
        }
        pdfDoc2.close();
        pdfDoc1.close();

        com.itextpdf.text.pdf.PdfReader reader = new com.itextpdf.text.pdf.PdfReader(destinationFolder + "copying2_2.pdf");
        Assert.assertEquals("Rebuilt", false, reader.isRebuilt());
        for (int i = 0; i < 5; i++) {
            byte[] bytes = reader.getPageContent(i+1);
            Assert.assertEquals("%page " + String.valueOf(i * 2 + 1) + "\n", new String(bytes));
        }
        reader.close();

    }


}
