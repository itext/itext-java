package com.itextpdf.core.pdf;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.pdf.navigation.PdfStringDestination;
import com.itextpdf.core.xmp.XMPException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.*;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class PdfDocumentTest {

    static final public String sourceFolder = "./src/test/resources/com/itextpdf/core/pdf/PdfDocumentTest/";
    static final public String destinationFolder = "./target/test/com/itextpdf/core/pdf/PdfDocumentTest/";

    @BeforeClass
    static public void beforeClass() {
        new File(destinationFolder).mkdirs();
    }

    @Test
    public void stamping1() throws IOException, PdfException {
        String filename1 =  destinationFolder + "stamping1_1.pdf";
        String filename2 =  destinationFolder + "stamping1_2.pdf";

        FileOutputStream fos1 = new FileOutputStream(filename1);
        PdfWriter writer1 = new PdfWriter(fos1);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        pdfDoc1.getInfo().setAuthor("Alexander Chingarev").
                setCreator("iText 6").
                setTitle("Empty iText 6 Document");
        PdfPage page1 = pdfDoc1.addNewPage();
        page1.getContentStream(0).getOutputStream().write(PdfWriter.getIsoBytes("%Hello World\n"));
        page1.flush();
        pdfDoc1.close();

        FileInputStream fis2 = new FileInputStream(filename1);
        PdfReader reader2 = new PdfReader(fis2);
        FileOutputStream fos2 = new FileOutputStream(filename2);
        PdfWriter writer2 = new PdfWriter(fos2);
        PdfDocument pdfDoc2 = new PdfDocument(reader2, writer2);
        pdfDoc2.getInfo().setCreator("iText 7").setTitle("Empty iText 7 Document");
        pdfDoc2.close();

        PdfReader reader3 = new PdfReader(new FileInputStream(filename2));
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        Assert.assertEquals("Rebuilt", false, reader3.hasRebuiltXref());
        Assert.assertEquals("Fixed", false, reader3.hasFixedXref());
        verifyPdfPagesCount(pdfDoc3.getCatalog().pageTree.getRoot().getPdfObject());
        pdfDoc3.close();

        com.itextpdf.text.pdf.PdfReader reader = new com.itextpdf.text.pdf.PdfReader(destinationFolder + "stamping1_2.pdf");
        Assert.assertEquals("Rebuilt", false, reader.isRebuilt());
        com.itextpdf.text.pdf.PdfDictionary trailer = reader.getTrailer();
        com.itextpdf.text.pdf.PdfDictionary info = trailer.getAsDict(com.itextpdf.text.pdf.PdfName.INFO);
        com.itextpdf.text.pdf.PdfString creator = info.getAsString(com.itextpdf.text.pdf.PdfName.CREATOR);
        Assert.assertEquals("iText 7", creator.toString());
        byte[] bytes = reader.getPageContent(1);
        Assert.assertEquals("%Hello World\n", new String(bytes));
        String date = reader.getInfo().get("ModDate");
        Calendar cl = com.itextpdf.text.pdf.PdfDate.decode(date);
        long diff = new GregorianCalendar().getTimeInMillis() - cl.getTimeInMillis();
        String message = "Unexpected creation date. Different from now is " + (float)diff/1000 + "s";
        Assert.assertTrue(message, diff < 5000);
        reader.close();
    }

    @Test
    public void stamping2() throws IOException, PdfException {
        String filename1 =  destinationFolder + "stamping2_1.pdf";
        String filename2 =  destinationFolder + "stamping2_2.pdf";

        FileOutputStream fos1 = new FileOutputStream(filename1);
        PdfWriter writer1 = new PdfWriter(fos1);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        PdfPage page1 = pdfDoc1.addNewPage();
        page1.getContentStream(0).getOutputStream().write(PdfWriter.getIsoBytes("%page 1\n"));
        page1.flush();
        pdfDoc1.close();

        FileInputStream fis2 = new FileInputStream(filename1);
        PdfReader reader2 = new PdfReader(fis2);
        FileOutputStream fos2 = new FileOutputStream(filename2);
        PdfWriter writer2 = new PdfWriter(fos2);
        PdfDocument pdfDoc2 = new PdfDocument(reader2, writer2);
        PdfPage page2 = pdfDoc2.addNewPage();
        page2.getContentStream(0).getOutputStream().write(PdfWriter.getIsoBytes("%page 2\n"));
        page2.flush();
        pdfDoc2.close();

        PdfReader reader3 = new PdfReader(new FileInputStream(filename2));
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        Assert.assertEquals("Rebuilt", false, reader3.hasRebuiltXref());
        Assert.assertEquals("Fixed", false, reader3.hasFixedXref());
        verifyPdfPagesCount(pdfDoc3.getCatalog().pageTree.getRoot().getPdfObject());
        pdfDoc3.close();

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
        String filename1 =  destinationFolder + "stamping3_1.pdf";
        String filename2 =  destinationFolder + "stamping3_2.pdf";

        FileOutputStream fos1 = new FileOutputStream(filename1);
        PdfWriter writer1 = new PdfWriter(fos1);
        writer1.setFullCompression(true);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        PdfPage page1 = pdfDoc1.addNewPage();
        page1.getContentStream(0).getOutputStream().write(PdfWriter.getIsoBytes("%page 1\n"));
        page1.flush();
        pdfDoc1.close();

        FileInputStream fis2 = new FileInputStream(filename1);
        PdfReader reader2 = new PdfReader(fis2);
        FileOutputStream fos2 = new FileOutputStream(filename2);
        PdfWriter writer2 = new PdfWriter(fos2);
        writer2.setFullCompression(true);
        PdfDocument pdfDoc2 = new PdfDocument(reader2, writer2);
        PdfPage page2 = pdfDoc2.addNewPage();
        page2.getContentStream(0).getOutputStream().write(PdfWriter.getIsoBytes("%page 2\n"));
        page2.flush();
        pdfDoc2.close();

        PdfReader reader3 = new PdfReader(new FileInputStream(filename2));
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        Assert.assertEquals("Rebuilt", false, reader3.hasRebuiltXref());
        Assert.assertEquals("Fixed", false, reader3.hasFixedXref());
        verifyPdfPagesCount(pdfDoc3.getCatalog().pageTree.getRoot().getPdfObject());
        pdfDoc3.close();

        com.itextpdf.text.pdf.PdfReader reader = new com.itextpdf.text.pdf.PdfReader(filename2);
        Assert.assertEquals("Rebuilt", false, reader.isRebuilt());
        byte[] bytes = reader.getPageContent(1);
        Assert.assertEquals("%page 1\n", new String(bytes));
        bytes = reader.getPageContent(2);
        Assert.assertEquals("%page 2\n", new String(bytes));
        reader.close();
    }

    @Test
    public void stamping4() throws IOException, PdfException {
        String filename1 =  destinationFolder + "stamping4_1.pdf";
        String filename2 =  destinationFolder + "stamping4_2.pdf";

        FileOutputStream fos1 = new FileOutputStream(filename1);
        PdfWriter writer1 = new PdfWriter(fos1);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        PdfPage page1 = pdfDoc1.addNewPage();
        page1.getContentStream(0).getOutputStream().write(PdfWriter.getIsoBytes("%page 1\n"));
        page1.flush();
        pdfDoc1.close();

        int pageCount = 15;
        FileInputStream fis2 = new FileInputStream(filename1);
        PdfReader reader2 = new PdfReader(fis2);
        FileOutputStream fos2 = new FileOutputStream(filename2);
        PdfWriter writer2 = new PdfWriter(fos2);
        PdfDocument pdfDoc2 = new PdfDocument(reader2, writer2);
        for (int i = 2; i <= pageCount; i++) {
            PdfPage page2 = pdfDoc2.addNewPage();
            page2.getContentStream(0).getOutputStream().write(PdfWriter.getIsoBytes("%page "+i+"\n"));
            page2.flush();
        }
        pdfDoc2.close();

        PdfReader reader3 = new PdfReader(new FileInputStream(filename2));
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        Assert.assertEquals("Rebuilt", false, reader3.hasRebuiltXref());
        Assert.assertEquals("Fixed", false, reader3.hasFixedXref());
        verifyPdfPagesCount(pdfDoc3.getCatalog().pageTree.getRoot().getPdfObject());
        pdfDoc3.close();

        com.itextpdf.text.pdf.PdfReader reader = new com.itextpdf.text.pdf.PdfReader(filename2);
        Assert.assertEquals("Rebuilt", false, reader.isRebuilt());
        Assert.assertEquals("Page count", pageCount, reader.getNumberOfPages());
        for (int i = 1; i < reader.getNumberOfPages(); i++) {
            byte[] bytes = reader.getPageContent(i);
            Assert.assertEquals("%page " + i + "\n", new String(bytes));
        }
        reader.close();
    }

    @Test
    public void stamping5() throws IOException, PdfException {
        String filename1 =  destinationFolder + "stamping5_1.pdf";
        String filename2 =  destinationFolder + "stamping5_2.pdf";

        FileOutputStream fos1 = new FileOutputStream(filename1);
        PdfWriter writer1 = new PdfWriter(fos1);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        PdfPage page1 = pdfDoc1.addNewPage();
        page1.getContentStream(0).getOutputStream().write(PdfWriter.getIsoBytes("%page 1\n"));
        page1.flush();
        pdfDoc1.close();

        int pageCount = 15;
        FileInputStream fis2 = new FileInputStream(filename1);
        PdfReader reader2 = new PdfReader(fis2);
        FileOutputStream fos2 = new FileOutputStream(filename2);
        PdfWriter writer2 = new PdfWriter(fos2);
        writer2.setFullCompression(true);
        PdfDocument pdfDoc2 = new PdfDocument(reader2, writer2);
        for (int i = 2; i <= pageCount; i++) {
            PdfPage page2 = pdfDoc2.addNewPage();
            page2.getContentStream(0).getOutputStream().write(PdfWriter.getIsoBytes("%page "+i+"\n"));
            page2.flush();
        }
        pdfDoc2.close();

        PdfReader reader3 = new PdfReader(new FileInputStream(filename2));
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        Assert.assertEquals("Rebuilt", false, reader3.hasRebuiltXref());
        Assert.assertEquals("Fixed", false, reader3.hasFixedXref());
        verifyPdfPagesCount(pdfDoc3.getCatalog().pageTree.getRoot().getPdfObject());
        pdfDoc3.close();

        com.itextpdf.text.pdf.PdfReader reader = new com.itextpdf.text.pdf.PdfReader(filename2);
        Assert.assertEquals("Rebuilt", false, reader.isRebuilt());
        Assert.assertEquals("Page count", pageCount, reader.getNumberOfPages());
        for (int i = 1; i < reader.getNumberOfPages(); i++) {
            byte[] bytes = reader.getPageContent(i);
            Assert.assertEquals("%page " + i + "\n", new String(bytes));
        }
        reader.close();
    }

    @Test
    public void stamping6() throws IOException, PdfException {
        String filename1 =  destinationFolder + "stamping6_1.pdf";
        String filename2 =  destinationFolder + "stamping6_2.pdf";

        FileOutputStream fos1 = new FileOutputStream(filename1);
        PdfWriter writer1 = new PdfWriter(fos1);
        writer1.setFullCompression(true);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        PdfPage page1 = pdfDoc1.addNewPage();
        page1.getContentStream(0).getOutputStream().write(PdfWriter.getIsoBytes("%page 1\n"));
        page1.flush();
        pdfDoc1.close();

        FileInputStream fis2 = new FileInputStream(filename1);
        PdfReader reader2 = new PdfReader(fis2);
        FileOutputStream fos2 = new FileOutputStream(filename2);
        PdfWriter writer2 = new PdfWriter(fos2);
        PdfDocument pdfDoc2 = new PdfDocument(reader2, writer2);
        PdfPage page2 = pdfDoc2.addNewPage();
        page2.getContentStream(0).getOutputStream().write(PdfWriter.getIsoBytes("%page 2\n"));
        page2.flush();
        pdfDoc2.close();

        PdfReader reader3 = new PdfReader(new FileInputStream(filename2));
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        Assert.assertEquals("Rebuilt", false, reader3.hasRebuiltXref());
        Assert.assertEquals("Fixed", false, reader3.hasFixedXref());
        verifyPdfPagesCount(pdfDoc3.getCatalog().pageTree.getRoot().getPdfObject());
        pdfDoc3.close();

        com.itextpdf.text.pdf.PdfReader reader = new com.itextpdf.text.pdf.PdfReader(filename2);
        Assert.assertEquals("Rebuilt", false, reader.isRebuilt());
        byte[] bytes = reader.getPageContent(1);
        Assert.assertEquals("%page 1\n", new String(bytes));
        bytes = reader.getPageContent(2);
        Assert.assertEquals("%page 2\n", new String(bytes));
        reader.close();
    }

    @Test
    public void stamping7() throws IOException, PdfException {
        String filename1 =  destinationFolder + "stamping7_1.pdf";
        String filename2 =  destinationFolder + "stamping7_2.pdf";

        FileOutputStream fos1 = new FileOutputStream(filename1);
        PdfWriter writer1 = new PdfWriter(fos1);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        PdfPage page1 = pdfDoc1.addNewPage();
        page1.getContentStream(0).getOutputStream().write(PdfWriter.getIsoBytes("%page 1\n"));
        page1.flush();
        pdfDoc1.close();

        FileInputStream fis2 = new FileInputStream(filename1);
        PdfReader reader2 = new PdfReader(fis2);
        FileOutputStream fos2 = new FileOutputStream(filename2);
        PdfWriter writer2 = new PdfWriter(fos2);
        writer2.setFullCompression(true);
        PdfDocument pdfDoc2 = new PdfDocument(reader2, writer2);
        PdfPage page2 = pdfDoc2.addNewPage();
        page2.getContentStream(0).getOutputStream().write(PdfWriter.getIsoBytes("%page 2\n"));
        page2.flush();
        pdfDoc2.close();

        PdfReader reader3 = new PdfReader(new FileInputStream(filename2));
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        Assert.assertEquals("Rebuilt", false, reader3.hasRebuiltXref());
        Assert.assertEquals("Fixed", false, reader3.hasFixedXref());
        verifyPdfPagesCount(pdfDoc3.getCatalog().pageTree.getRoot().getPdfObject());
        pdfDoc3.close();

        com.itextpdf.text.pdf.PdfReader reader = new com.itextpdf.text.pdf.PdfReader(filename2);
        Assert.assertEquals("Rebuilt", false, reader.isRebuilt());
        byte[] bytes = reader.getPageContent(1);
        Assert.assertEquals("%page 1\n", new String(bytes));
        bytes = reader.getPageContent(2);
        Assert.assertEquals("%page 2\n", new String(bytes));
        reader.close();
    }

    @Test
    public void stamping8() throws IOException, PdfException {
        String filename1 =  destinationFolder + "stamping8_1.pdf";
        String filename2 =  destinationFolder + "stamping8_2.pdf";
        int pageCount = 10;

        FileOutputStream fos1 = new FileOutputStream(filename1);
        PdfWriter writer1 = new PdfWriter(fos1);
        writer1.setFullCompression(true);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        for (int i = 1; i <= pageCount; i++ ) {
            PdfPage page = pdfDoc1.addNewPage();
            page.getContentStream(0).getOutputStream().write(PdfWriter.getIsoBytes("%page " + i +"\n"));
            page.flush();
        }
        pdfDoc1.close();

        PdfReader reader2 = new PdfReader(new FileInputStream(filename1));
        PdfWriter writer2 = new PdfWriter(new FileOutputStream(filename2));
        writer2.setFullCompression(true);
        PdfDocument pdfDoc2 = new PdfDocument(reader2, writer2);
        pdfDoc2.close();

        PdfReader reader3 = new PdfReader(new FileInputStream(filename2));
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        Assert.assertEquals("Number of pages", pageCount, pdfDoc3.getNumOfPages());
        Assert.assertEquals("Rebuilt", false, reader3.hasRebuiltXref());
        Assert.assertEquals("Fixed", false, reader3.hasFixedXref());
        verifyPdfPagesCount(pdfDoc3.getCatalog().pageTree.getRoot().getPdfObject());
        pdfDoc3.close();

        com.itextpdf.text.pdf.PdfReader reader = new com.itextpdf.text.pdf.PdfReader(filename2);
        Assert.assertEquals("Rebuilt", false, reader.isRebuilt());
        for (int i = 1; i <= pageCount; i++) {
            byte[] bytes = reader.getPageContent(i);
            Assert.assertEquals("Page content at page " + i, "%page " + i + "\n", new String(bytes));
        }
        reader.close();
    }

    @Test
    public void stamping9() throws IOException, PdfException {
        String filename1 =  destinationFolder + "stamping9_1.pdf";
        String filename2 =  destinationFolder + "stamping9_2.pdf";
        int pageCount = 10;

        FileOutputStream fos1 = new FileOutputStream(filename1);
        PdfWriter writer1 = new PdfWriter(fos1);
        writer1.setFullCompression(false);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        for (int i = 1; i <= pageCount; i++ ) {
            PdfPage page = pdfDoc1.addNewPage();
            page.getContentStream(0).getOutputStream().write(PdfWriter.getIsoBytes("%page " + i +"\n"));
            page.flush();
        }
        pdfDoc1.close();

        PdfReader reader2 = new PdfReader(new FileInputStream(filename1));
        PdfWriter writer2 = new PdfWriter(new FileOutputStream(filename2));
        writer2.setFullCompression(true);
        PdfDocument pdfDoc2 = new PdfDocument(reader2, writer2);
        pdfDoc2.close();

        PdfReader reader3 = new PdfReader(new FileInputStream(filename2));
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        Assert.assertEquals("Number of pages", pageCount, pdfDoc3.getNumOfPages());
        Assert.assertEquals("Rebuilt", false, reader3.hasRebuiltXref());
        Assert.assertEquals("Fixed", false, reader3.hasFixedXref());
        verifyPdfPagesCount(pdfDoc3.getCatalog().pageTree.getRoot().getPdfObject());
        pdfDoc3.close();

        com.itextpdf.text.pdf.PdfReader reader = new com.itextpdf.text.pdf.PdfReader(filename2);
        Assert.assertEquals("Rebuilt", false, reader.isRebuilt());
        for (int i = 1; i <= pageCount; i++) {
            byte[] bytes = reader.getPageContent(i);
            Assert.assertEquals("Page content at page " + i, "%page " + i + "\n", new String(bytes));
        }
        reader.close();
    }

    @Test
    public void stamping10() throws IOException, PdfException {
        String filename1 =  destinationFolder + "stamping10_1.pdf";
        String filename2 =  destinationFolder + "stamping10_2.pdf";
        int pageCount = 10;

        FileOutputStream fos1 = new FileOutputStream(filename1);
        PdfWriter writer1 = new PdfWriter(fos1);
        writer1.setFullCompression(true);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        for (int i = 1; i <= pageCount; i++ ) {
            PdfPage page = pdfDoc1.addNewPage();
            page.getContentStream(0).getOutputStream().write(PdfWriter.getIsoBytes("%page " + i +"\n"));
            page.flush();
        }
        pdfDoc1.close();

        PdfReader reader2 = new PdfReader(new FileInputStream(filename1));
        PdfWriter writer2 = new PdfWriter(new FileOutputStream(filename2));
        writer2.setFullCompression(false);
        PdfDocument pdfDoc2 = new PdfDocument(reader2, writer2);
        pdfDoc2.close();

        PdfReader reader3 = new PdfReader(new FileInputStream(filename2));
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        Assert.assertEquals("Number of pages", pageCount, pdfDoc3.getNumOfPages());
        Assert.assertEquals("Rebuilt", false, reader3.hasRebuiltXref());
        Assert.assertEquals("Fixed", false, reader3.hasFixedXref());
        verifyPdfPagesCount(pdfDoc3.getCatalog().pageTree.getRoot().getPdfObject());
        pdfDoc3.close();

        com.itextpdf.text.pdf.PdfReader reader = new com.itextpdf.text.pdf.PdfReader(filename2);
        Assert.assertEquals("Rebuilt", false, reader.isRebuilt());
        for (int i = 1; i <= pageCount; i++) {
            byte[] bytes = reader.getPageContent(i);
            Assert.assertEquals("Page content at page " + i, "%page " + i + "\n", new String(bytes));
        }
        reader.close();
    }

    @Test
    public void stamping11() throws IOException, PdfException {
        String filename1 =  destinationFolder + "stamping11_1.pdf";
        String filename2 =  destinationFolder + "stamping11_2.pdf";
        int pageCount = 10;

        FileOutputStream fos1 = new FileOutputStream(filename1);
        PdfWriter writer1 = new PdfWriter(fos1);
        writer1.setFullCompression(false);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        for (int i = 1; i <= pageCount; i++ ) {
            PdfPage page = pdfDoc1.addNewPage();
            page.getContentStream(0).getOutputStream().write(PdfWriter.getIsoBytes("%page " + i +"\n"));
            page.flush();
        }
        pdfDoc1.close();

        PdfReader reader2 = new PdfReader(new FileInputStream(filename1));
        PdfWriter writer2 = new PdfWriter(new FileOutputStream(filename2));
        writer2.setFullCompression(false);
        PdfDocument pdfDoc2 = new PdfDocument(reader2, writer2);
        pdfDoc2.close();

        PdfReader reader3 = new PdfReader(new FileInputStream(filename2));
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        Assert.assertEquals("Number of pages", pageCount, pdfDoc3.getNumOfPages());
        Assert.assertEquals("Rebuilt", false, reader3.hasRebuiltXref());
        Assert.assertEquals("Fixed", false, reader3.hasFixedXref());
        verifyPdfPagesCount(pdfDoc3.getCatalog().pageTree.getRoot().getPdfObject());
        pdfDoc3.close();

        com.itextpdf.text.pdf.PdfReader reader = new com.itextpdf.text.pdf.PdfReader(filename2);
        Assert.assertEquals("Rebuilt", false, reader.isRebuilt());
        for (int i = 1; i <= pageCount; i++) {
            byte[] bytes = reader.getPageContent(i);
            Assert.assertEquals("Page content at page " + i, "%page " + i + "\n", new String(bytes));
        }
        reader.close();
    }

    @Test
    public void stamping12() throws IOException, PdfException {
        String filename1 =  destinationFolder + "stamping12_1.pdf";
        String filename2 =  destinationFolder + "stamping12_2.pdf";
        int pageCount = 1010;

        FileOutputStream fos1 = new FileOutputStream(filename1);
        PdfWriter writer1 = new PdfWriter(fos1);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        for (int i = 1; i <= pageCount; i++ ) {
            PdfPage page = pdfDoc1.addNewPage();
            page.getContentStream(0).getOutputStream().write(PdfWriter.getIsoBytes("%page " + i +"\n"));
            page.flush();
        }
        pdfDoc1.close();

        PdfReader reader2 = new PdfReader(new FileInputStream(filename1));
        PdfWriter writer2 = new PdfWriter(new FileOutputStream(filename2));
        PdfDocument pdfDoc2 = new PdfDocument(reader2, writer2);

        int newPageCount = 10;
        for (int i = pageCount; i > newPageCount; i--) {
            Assert.assertNotNull("Remove page " + i, pdfDoc2.removePage(i));
        }
        pdfDoc2.close();

        PdfReader reader3 = new PdfReader(new FileInputStream(filename2));
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 1; i <= pdfDoc3.getNumOfPages(); i++) {
            pdfDoc3.getPage(i);
        }
        PdfPage pdfPage = pdfDoc3.getPage(1);
        PdfDictionary root = pdfPage.getPdfObject().getAsDictionary(PdfName.Parent);
        Assert.assertEquals("PdfPages kids count", newPageCount, root.getAsArray(PdfName.Kids).size());
        Assert.assertEquals("Number of pages", newPageCount, pdfDoc3.getNumOfPages());
        Assert.assertEquals("Rebuilt", false, reader3.hasRebuiltXref());
        Assert.assertEquals("Fixed", false, reader3.hasFixedXref());
        verifyPdfPagesCount(pdfDoc3.getCatalog().pageTree.getRoot().getPdfObject());
        pdfDoc3.close();

        com.itextpdf.text.pdf.PdfReader reader = new com.itextpdf.text.pdf.PdfReader(filename2);
        Assert.assertEquals("Rebuilt", false, reader.isRebuilt());
        for (int i = 1; i <= reader.getNumberOfPages(); i++) {
            byte[] bytes = reader.getPageContent(i);
            Assert.assertEquals("Page content at page " + i, "%page " + i + "\n", new String(bytes));
        }
        reader.close();
    }

    @Test
    public void stamping13() throws IOException, PdfException {
        String filename1 =  destinationFolder + "stamping13_1.pdf";
        String filename2 =  destinationFolder + "stamping13_2.pdf";
        int pageCount = 1010;

        FileOutputStream fos1 = new FileOutputStream(filename1);
        PdfWriter writer1 = new PdfWriter(fos1);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        for (int i = 1; i <= pageCount; i++ ) {
            PdfPage page = pdfDoc1.addNewPage();
            page.getContentStream(0).getOutputStream().write(PdfWriter.getIsoBytes("%page " + i +"\n"));
            page.flush();
        }
        pdfDoc1.close();

        PdfReader reader2 = new PdfReader(new FileInputStream(filename1));
        PdfWriter writer2 = new PdfWriter(new FileOutputStream(filename2));
        PdfDocument pdfDoc2 = new PdfDocument(reader2, writer2);

        for (int i = pageCount; i > 1; i--) {
            Assert.assertNotNull("Remove page " + i, pdfDoc2.removePage(i));
        }
        pdfDoc2.removePage(1);
        for (int i = 1; i <= pageCount; i++ ) {
            PdfPage page = pdfDoc2.addNewPage();
            page.getContentStream(0).getOutputStream().write(PdfWriter.getIsoBytes("%page " + i + "\n"));
            page.flush();
        }
        pdfDoc2.close();

        PdfReader reader3 = new PdfReader(new FileInputStream(filename2));
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 1; i <= pdfDoc3.getNumOfPages(); i++) {
            pdfDoc3.getPage(i);
        }
        PdfArray rootKids = pdfDoc3.getCatalog().pageTree.getRoot().getPdfObject().getAsArray(PdfName.Kids);
        Assert.assertEquals("Page root kids count", 2, rootKids.size());
        Assert.assertEquals("Number of pages", pageCount, pdfDoc3.getNumOfPages());
        Assert.assertEquals("Rebuilt", false, reader3.hasRebuiltXref());
        Assert.assertEquals("Fixed", false, reader3.hasFixedXref());
        verifyPdfPagesCount(pdfDoc3.getCatalog().pageTree.getRoot().getPdfObject());
        pdfDoc3.close();

        com.itextpdf.text.pdf.PdfReader reader = new com.itextpdf.text.pdf.PdfReader(filename2);
        Assert.assertEquals("Rebuilt", false, reader.isRebuilt());
        for (int i = 1; i <= pageCount; i++) {
            byte[] bytes = reader.getPageContent(i);
            Assert.assertEquals("Page content at page " + i, "%page " + i + "\n", new String(bytes));
        }
        reader.close();
    }

    @Test @Ignore
    public void stamping14() throws IOException, PdfException {
        String filename1 =  sourceFolder + "20000PagesDocument.pdf";
        String filename2 =  destinationFolder + "stamping14.pdf";

        PdfReader reader2 = new PdfReader(new FileInputStream(filename1));
        PdfWriter writer2 = new PdfWriter(new FileOutputStream(filename2));
        PdfDocument pdfDoc2 = new PdfDocument(reader2, writer2);

        for (int i = pdfDoc2.getNumOfPages(); i > 3; i--) {
            Assert.assertNotNull("Remove page " + i, pdfDoc2.removePage(i));
        }

        pdfDoc2.close();

        PdfReader reader3 = new PdfReader(new FileInputStream(filename2));
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 1; i <= pdfDoc3.getNumOfPages(); i++) {
            pdfDoc3.getPage(i);
        }
        Assert.assertTrue("Xref size is " + pdfDoc3.getXref().size(), pdfDoc3.getXref().size() < 20);
        Assert.assertEquals("Number of pages", 3, pdfDoc3.getNumOfPages());
        Assert.assertEquals("Rebuilt", false, reader3.hasRebuiltXref());
        Assert.assertEquals("Fixed", false, reader3.hasFixedXref());
        verifyPdfPagesCount(pdfDoc3.getCatalog().pageTree.getRoot().getPdfObject());
        pdfDoc3.close();

        com.itextpdf.text.pdf.PdfReader reader = new com.itextpdf.text.pdf.PdfReader(filename2);
        Assert.assertEquals("Rebuilt", false, reader.isRebuilt());
        for (int i = 1; i <= reader.getNumberOfPages(); i++) {
            byte[] bytes = reader.getPageContent(i);
            Assert.assertEquals("Page content at page " + i, "%page " + i + "\n", new String(bytes));
        }
        reader.close();
    }

    @Test
    public void stampingXmp1() throws IOException, PdfException, XMPException {
        String filename1 =  destinationFolder + "stampingXmp1_1.pdf";
        String filename2 =  destinationFolder + "stampingXmp1_2.pdf";
        int pageCount = 10;

        FileOutputStream fos1 = new FileOutputStream(filename1);
        PdfWriter writer1 = new PdfWriter(fos1);
        writer1.setFullCompression(true);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        for (int i = 1; i <= pageCount; i++ ) {
            PdfPage page = pdfDoc1.addNewPage();
            page.getContentStream(0).getOutputStream().write(PdfWriter.getIsoBytes("%page " + i +"\n"));
            page.flush();
        }
        pdfDoc1.close();

        PdfReader reader2 = new PdfReader(new FileInputStream(filename1));
        PdfWriter writer2 = new PdfWriter(new FileOutputStream(filename2));
        writer2.setFullCompression(false);
        PdfDocument pdfDoc2 = new PdfDocument(reader2, writer2);
        pdfDoc2.getDocumentInfo().setAuthor("Alexander Chingarev");
        pdfDoc2.setXmpMetadata();
        pdfDoc2.close();

        PdfReader reader3 = new PdfReader(new FileInputStream(filename2));
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        Assert.assertNotNull("XmpMetadata not found", pdfDoc3.getXmpMetadata());
        Assert.assertEquals("Number of pages", pageCount, pdfDoc3.getNumOfPages());
        Assert.assertEquals("Rebuilt", false, reader3.hasRebuiltXref());
        Assert.assertEquals("Fixed", false, reader3.hasFixedXref());
        verifyPdfPagesCount(pdfDoc3.getCatalog().pageTree.getRoot().getPdfObject());
        pdfDoc3.close();

        com.itextpdf.text.pdf.PdfReader reader = new com.itextpdf.text.pdf.PdfReader(filename2);
        Assert.assertEquals("Rebuilt", false, reader.isRebuilt());
        for (int i = 1; i <= pageCount; i++) {
            byte[] bytes = reader.getPageContent(i);
            Assert.assertEquals("Page content at page " + i, "%page " + i + "\n", new String(bytes));
        }
        reader.close();
    }

    @Test
    public void stampingXmp2() throws IOException, PdfException, XMPException {
        String filename1 =  destinationFolder + "stampingXmp2_1.pdf";
        String filename2 =  destinationFolder + "stampingXmp2_2.pdf";
        int pageCount = 10;

        FileOutputStream fos1 = new FileOutputStream(filename1);
        PdfWriter writer1 = new PdfWriter(fos1);
        writer1.setFullCompression(false);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        for (int i = 1; i <= pageCount; i++ ) {
            PdfPage page = pdfDoc1.addNewPage();
            page.getContentStream(0).getOutputStream().write(PdfWriter.getIsoBytes("%page " + i +"\n"));
            page.flush();
        }
        pdfDoc1.close();

        PdfReader reader2 = new PdfReader(new FileInputStream(filename1));
        PdfWriter writer2 = new PdfWriter(new FileOutputStream(filename2));
        writer2.setFullCompression(true);
        PdfDocument pdfDoc2 = new PdfDocument(reader2, writer2);
        pdfDoc2.getDocumentInfo().setAuthor("Alexander Chingarev");
        pdfDoc2.getDocumentInfo().setAuthor("Alexander Chingarev");
        pdfDoc2.setXmpMetadata();
        pdfDoc2.close();

        PdfReader reader3 = new PdfReader(new FileInputStream(filename2));
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        Assert.assertNotNull("XmpMetadata not found", pdfDoc3.getXmpMetadata());
        Assert.assertEquals("Number of pages", pageCount, pdfDoc3.getNumOfPages());
        Assert.assertEquals("Rebuilt", false, reader3.hasRebuiltXref());
        Assert.assertEquals("Fixed", false, reader3.hasFixedXref());
        verifyPdfPagesCount(pdfDoc3.getCatalog().pageTree.getRoot().getPdfObject());
        pdfDoc3.close();

        com.itextpdf.text.pdf.PdfReader reader = new com.itextpdf.text.pdf.PdfReader(filename2);
        Assert.assertEquals("Rebuilt", false, reader.isRebuilt());
        for (int i = 1; i <= pageCount; i++) {
            byte[] bytes = reader.getPageContent(i);
            Assert.assertEquals("Page content at page " + i, "%page " + i + "\n", new String(bytes));
        }
        reader.close();
    }

    @Test
    public void stampingAppend1() throws IOException, PdfException {
        String filename1 =  destinationFolder + "stampingAppend1_1.pdf";
        String filename2 =  destinationFolder + "stampingAppend1_2.pdf";

        FileOutputStream fos1 = new FileOutputStream(filename1);
        PdfWriter writer1 = new PdfWriter(fos1);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        pdfDoc1.getInfo().setAuthor("Alexander Chingarev").
                setCreator("iText 6").
                setTitle("Empty iText 6 Document");
        PdfPage page1 = pdfDoc1.addNewPage();
        page1.getContentStream(0).getOutputStream().write(PdfWriter.getIsoBytes("%Hello World\n"));
        page1.flush();
        pdfDoc1.close();

        FileInputStream fis2 = new FileInputStream(filename1);
        PdfReader reader2 = new PdfReader(fis2);
        FileOutputStream fos2 = new FileOutputStream(filename2);
        PdfWriter writer2 = new PdfWriter(fos2);
        PdfDocument pdfDoc2 = new PdfDocument(reader2, writer2, true);
        pdfDoc2.getInfo().setCreator("iText 7").setTitle("Empty iText 7 Document");
        pdfDoc2.getInfo().setModified();
        pdfDoc2.close();

        PdfReader reader3 = new PdfReader(new FileInputStream(filename2));
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        Assert.assertEquals("Rebuilt", false, reader3.hasRebuiltXref());
        Assert.assertEquals("Fixed", false, reader3.hasFixedXref());
        verifyPdfPagesCount(pdfDoc3.getCatalog().pageTree.getRoot().getPdfObject());
        pdfDoc3.close();

        com.itextpdf.text.pdf.PdfReader reader = new com.itextpdf.text.pdf.PdfReader(filename2);
        Assert.assertEquals("Rebuilt", false, reader.isRebuilt());
        com.itextpdf.text.pdf.PdfDictionary trailer = reader.getTrailer();
        com.itextpdf.text.pdf.PdfDictionary info = trailer.getAsDict(com.itextpdf.text.pdf.PdfName.INFO);
        com.itextpdf.text.pdf.PdfString creator = info.getAsString(com.itextpdf.text.pdf.PdfName.CREATOR);
        Assert.assertEquals("iText 7", creator.toString());
        byte[] bytes = reader.getPageContent(1);
        Assert.assertEquals("%Hello World\n", new String(bytes));
        String date = reader.getInfo().get("ModDate");
        Calendar cl = com.itextpdf.text.pdf.PdfDate.decode(date);
        long diff = new GregorianCalendar().getTimeInMillis() - cl.getTimeInMillis();
        String message = "Unexpected creation date. Different from now is " + (float)diff/1000 + "s";
        Assert.assertTrue(message, diff < 5000);
        reader.close();
    }

    @Test
    public void stampingAppend2() throws IOException, PdfException {
        String filename1 =  destinationFolder + "stampingAppend2_1.pdf";
        String filename2 =  destinationFolder + "stampingAppend2_2.pdf";

        FileOutputStream fos1 = new FileOutputStream(filename1);
        PdfWriter writer1 = new PdfWriter(fos1);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        PdfPage page1 = pdfDoc1.addNewPage();
        page1.getContentStream(0).getOutputStream().write(PdfWriter.getIsoBytes("%page 1\n"));
        page1.flush();
        pdfDoc1.close();

        FileInputStream fis2 = new FileInputStream(filename1);
        PdfReader reader2 = new PdfReader(fis2);
        FileOutputStream fos2 = new FileOutputStream(filename2);
        PdfWriter writer2 = new PdfWriter(fos2);
        PdfDocument pdfDoc2 = new PdfDocument(reader2, writer2, true);
        PdfPage page2 = pdfDoc2.addNewPage();
        page2.getContentStream(0).getOutputStream().write(PdfWriter.getIsoBytes("%page 2\n"));
        page2.setModified();
        page2.flush();
        pdfDoc2.close();

        PdfReader reader3 = new PdfReader(new FileInputStream(filename2));
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        Assert.assertEquals("Rebuilt", false, reader3.hasRebuiltXref());
        Assert.assertEquals("Fixed", false, reader3.hasFixedXref());
        verifyPdfPagesCount(pdfDoc3.getCatalog().pageTree.getRoot().getPdfObject());
        pdfDoc3.close();

        com.itextpdf.text.pdf.PdfReader reader = new com.itextpdf.text.pdf.PdfReader(filename2);
        Assert.assertEquals("Rebuilt", false, reader.isRebuilt());
        byte[] bytes = reader.getPageContent(1);
        Assert.assertEquals("%page 1\n", new String(bytes));
        bytes = reader.getPageContent(2);
        Assert.assertEquals("%page 2\n", new String(bytes));
        reader.close();
    }

    @Test
    public void stampingAppend3() throws IOException, PdfException {
        String filename1 =  destinationFolder + "stampingAppend3_1.pdf";
        String filename2 =  destinationFolder + "stampingAppend3_2.pdf";

        FileOutputStream fos1 = new FileOutputStream(filename1);
        PdfWriter writer1 = new PdfWriter(fos1);
        writer1.setFullCompression(true);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        PdfPage page1 = pdfDoc1.addNewPage();
        page1.getContentStream(0).getOutputStream().write(PdfWriter.getIsoBytes("%page 1\n"));
        page1.flush();
        pdfDoc1.close();

        FileInputStream fis2 = new FileInputStream(filename1);
        PdfReader reader2 = new PdfReader(fis2);
        FileOutputStream fos2 = new FileOutputStream(filename2);
        PdfWriter writer2 = new PdfWriter(fos2);
        PdfDocument pdfDoc2 = new PdfDocument(reader2, writer2, true);
        PdfPage page2 = pdfDoc2.addNewPage();
        page2.getContentStream(0).getOutputStream().write(PdfWriter.getIsoBytes("%page 2\n"));

        page2.flush();
        pdfDoc2.close();

        PdfReader reader3 = new PdfReader(new FileInputStream(filename2));
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        Assert.assertEquals("Rebuilt", false, reader3.hasRebuiltXref());
        Assert.assertEquals("Fixed", false, reader3.hasFixedXref());
        verifyPdfPagesCount(pdfDoc3.getCatalog().pageTree.getRoot().getPdfObject());
        pdfDoc3.close();

        com.itextpdf.text.pdf.PdfReader reader = new com.itextpdf.text.pdf.PdfReader(filename2);
        Assert.assertEquals("Rebuilt", false, reader.isRebuilt());
        byte[] bytes = reader.getPageContent(1);
        Assert.assertEquals("%page 1\n", new String(bytes));
        bytes = reader.getPageContent(2);
        Assert.assertEquals("%page 2\n", new String(bytes));
        reader.close();
    }

    @Test
    public void stampingAppend4() throws IOException, PdfException {
        String filename1 =  destinationFolder + "stampingAppend4_1.pdf";
        String filename2 =  destinationFolder + "stampingAppend4_2.pdf";

        FileOutputStream fos1 = new FileOutputStream(filename1);
        PdfWriter writer1 = new PdfWriter(fos1);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        PdfPage page1 = pdfDoc1.addNewPage();
        page1.getContentStream(0).getOutputStream().write(PdfWriter.getIsoBytes("%page 1\n"));
        page1.flush();
        pdfDoc1.close();

        int pageCount = 15;
        FileInputStream fis2 = new FileInputStream(filename1);
        PdfReader reader2 = new PdfReader(fis2);
        FileOutputStream fos2 = new FileOutputStream(filename2);
        PdfWriter writer2 = new PdfWriter(fos2);
        PdfDocument pdfDoc2 = new PdfDocument(reader2, writer2, true);
        for (int i = 2; i <= pageCount; i++) {
            PdfPage page2 = pdfDoc2.addNewPage();
            page2.getContentStream(0).getOutputStream().write(PdfWriter.getIsoBytes("%page "+i+"\n"));
            page2.flush();
        }

        pdfDoc2.close();

        PdfReader reader3 = new PdfReader(new FileInputStream(filename2));
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        Assert.assertEquals("Rebuilt", false, reader3.hasRebuiltXref());
        Assert.assertEquals("Fixed", false, reader3.hasFixedXref());
        verifyPdfPagesCount(pdfDoc3.getCatalog().pageTree.getRoot().getPdfObject());
        pdfDoc3.close();

        com.itextpdf.text.pdf.PdfReader reader = new com.itextpdf.text.pdf.PdfReader(filename2);
        Assert.assertEquals("Rebuilt", false, reader.isRebuilt());
        Assert.assertEquals("Page count", pageCount, reader.getNumberOfPages());
        for (int i = 1; i < reader.getNumberOfPages(); i++) {
            byte[] bytes = reader.getPageContent(i);
            Assert.assertEquals("%page " + i + "\n", new String(bytes));
        }
        reader.close();
    }

    @Test
    public void stampingAppend5() throws IOException, PdfException {
        String filename1 =  destinationFolder + "stampingAppend5_1.pdf";
        String filename2 =  destinationFolder + "stampingAppend5_2.pdf";

        FileOutputStream fos1 = new FileOutputStream(filename1);
        PdfWriter writer1 = new PdfWriter(fos1);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        PdfPage page1 = pdfDoc1.addNewPage();
        page1.getContentStream(0).getOutputStream().write(PdfWriter.getIsoBytes("%page 1\n"));
        page1.flush();
        pdfDoc1.close();

        int pageCount = 15;
        FileInputStream fis2 = new FileInputStream(filename1);
        PdfReader reader2 = new PdfReader(fis2);
        FileOutputStream fos2 = new FileOutputStream(filename2);
        PdfWriter writer2 = new PdfWriter(fos2);
        writer2.setFullCompression(true);
        PdfDocument pdfDoc2 = new PdfDocument(reader2, writer2, true);
        for (int i = 2; i <= pageCount; i++) {
            PdfPage page2 = pdfDoc2.addNewPage();
            page2.getContentStream(0).getOutputStream().write(PdfWriter.getIsoBytes("%page "+i+"\n"));
            page2.flush();
        }
        pdfDoc2.close();

        PdfReader reader3 = new PdfReader(new FileInputStream(filename2));
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        Assert.assertEquals("Rebuilt", false, reader3.hasRebuiltXref());
        Assert.assertEquals("Fixed", false, reader3.hasFixedXref());
        verifyPdfPagesCount(pdfDoc3.getCatalog().pageTree.getRoot().getPdfObject());
        pdfDoc3.close();

        com.itextpdf.text.pdf.PdfReader reader = new com.itextpdf.text.pdf.PdfReader(filename2);
        Assert.assertEquals("Rebuilt", false, reader.isRebuilt());
        Assert.assertEquals("Page count", pageCount, reader.getNumberOfPages());
        for (int i = 1; i < reader.getNumberOfPages(); i++) {
            byte[] bytes = reader.getPageContent(i);
            Assert.assertEquals("%page " + i + "\n", new String(bytes));
        }
        reader.close();
    }

    @Test
    public void stampingAppend8() throws IOException, PdfException {
        String filename1 =  destinationFolder + "stampingAppend8_1.pdf";
        String filename2 =  destinationFolder + "stampingAppend8_2.pdf";
        int pageCount = 10;

        FileOutputStream fos1 = new FileOutputStream(filename1);
        PdfWriter writer1 = new PdfWriter(fos1);
        writer1.setFullCompression(true);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        for (int i = 1; i <= pageCount; i++ ) {
            PdfPage page = pdfDoc1.addNewPage();
            page.getContentStream(0).getOutputStream().write(PdfWriter.getIsoBytes("%page " + i +"\n"));
            page.flush();
        }
        pdfDoc1.close();

        PdfReader reader2 = new PdfReader(new FileInputStream(filename1));
        PdfWriter writer2 = new PdfWriter(new FileOutputStream(filename2));
        writer2.setFullCompression(true);
        PdfDocument pdfDoc2 = new PdfDocument(reader2, writer2, true);
        pdfDoc2.close();

        PdfReader reader3 = new PdfReader(new FileInputStream(filename2));
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        Assert.assertEquals("Number of pages", pageCount, pdfDoc3.getNumOfPages());
        Assert.assertEquals("Rebuilt", false, reader3.hasRebuiltXref());
        Assert.assertEquals("Fixed", false, reader3.hasFixedXref());
        verifyPdfPagesCount(pdfDoc3.getCatalog().pageTree.getRoot().getPdfObject());
        pdfDoc3.close();

        com.itextpdf.text.pdf.PdfReader reader = new com.itextpdf.text.pdf.PdfReader(filename2);
        Assert.assertEquals("Rebuilt", false, reader.isRebuilt());
        for (int i = 1; i <= pageCount; i++) {
            byte[] bytes = reader.getPageContent(i);
            Assert.assertEquals("Page content at page " + i, "%page " + i + "\n", new String(bytes));
        }
        reader.close();
    }

    @Test
    public void stampingAppend9() throws IOException, PdfException {
        String filename1 =  destinationFolder + "stampingAppend9_1.pdf";
        String filename2 =  destinationFolder + "stampingAppend9_2.pdf";
        int pageCount = 10;

        FileOutputStream fos1 = new FileOutputStream(filename1);
        PdfWriter writer1 = new PdfWriter(fos1);
        writer1.setFullCompression(false);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        for (int i = 1; i <= pageCount; i++ ) {
            PdfPage page = pdfDoc1.addNewPage();
            page.getContentStream(0).getOutputStream().write(PdfWriter.getIsoBytes("%page " + i +"\n"));
            page.flush();
        }
        pdfDoc1.close();

        PdfReader reader2 = new PdfReader(new FileInputStream(filename1));
        PdfWriter writer2 = new PdfWriter(new FileOutputStream(filename2));
        writer2.setFullCompression(true);
        PdfDocument pdfDoc2 = new PdfDocument(reader2, writer2, true);
        pdfDoc2.close();

        PdfReader reader3 = new PdfReader(new FileInputStream(filename2));
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        Assert.assertEquals("Number of pages", pageCount, pdfDoc3.getNumOfPages());
        Assert.assertEquals("Rebuilt", false, reader3.hasRebuiltXref());
        Assert.assertEquals("Fixed", false, reader3.hasFixedXref());
        verifyPdfPagesCount(pdfDoc3.getCatalog().pageTree.getRoot().getPdfObject());
        pdfDoc3.close();

        com.itextpdf.text.pdf.PdfReader reader = new com.itextpdf.text.pdf.PdfReader(filename2);
        Assert.assertEquals("Rebuilt", false, reader.isRebuilt());
        for (int i = 1; i <= pageCount; i++) {
            byte[] bytes = reader.getPageContent(i);
            Assert.assertEquals("Page content at page " + i, "%page " + i + "\n", new String(bytes));
        }
        reader.close();
    }

    @Test
    public void stampingAppend10() throws IOException, PdfException {
        String filename1 =  destinationFolder + "stampingAppend10_1.pdf";
        String filename2 =  destinationFolder + "stampingAppend10_2.pdf";
        int pageCount = 10;

        FileOutputStream fos1 = new FileOutputStream(filename1);
        PdfWriter writer1 = new PdfWriter(fos1);
        writer1.setFullCompression(true);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        for (int i = 1; i <= pageCount; i++ ) {
            PdfPage page = pdfDoc1.addNewPage();
            page.getContentStream(0).getOutputStream().write(PdfWriter.getIsoBytes("%page " + i +"\n"));
            page.flush();
        }
        pdfDoc1.close();

        PdfReader reader2 = new PdfReader(new FileInputStream(filename1));
        PdfWriter writer2 = new PdfWriter(new FileOutputStream(filename2));
        writer2.setFullCompression(false);
        PdfDocument pdfDoc2 = new PdfDocument(reader2, writer2, true);
        pdfDoc2.close();

        PdfReader reader3 = new PdfReader(new FileInputStream(filename2));
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        Assert.assertEquals("Number of pages", pageCount, pdfDoc3.getNumOfPages());
        Assert.assertEquals("Rebuilt", false, reader3.hasRebuiltXref());
        Assert.assertEquals("Fixed", false, reader3.hasFixedXref());
        verifyPdfPagesCount(pdfDoc3.getCatalog().pageTree.getRoot().getPdfObject());
        pdfDoc3.close();

        com.itextpdf.text.pdf.PdfReader reader = new com.itextpdf.text.pdf.PdfReader(filename2);
        Assert.assertEquals("Rebuilt", false, reader.isRebuilt());
        for (int i = 1; i <= pageCount; i++) {
            byte[] bytes = reader.getPageContent(i);
            Assert.assertEquals("Page content at page " + i, "%page " + i + "\n", new String(bytes));
        }
        reader.close();
    }

    @Test
    public void stampingAppend11() throws IOException, PdfException {
        String filename1 =  destinationFolder + "stampingAppend11_1.pdf";
        String filename2 =  destinationFolder + "stampingAppend11_2.pdf";
        int pageCount = 10;

        FileOutputStream fos1 = new FileOutputStream(filename1);
        PdfWriter writer1 = new PdfWriter(fos1);
        writer1.setFullCompression(false);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        for (int i = 1; i <= pageCount; i++ ) {
            PdfPage page = pdfDoc1.addNewPage();
            page.getContentStream(0).getOutputStream().write(PdfWriter.getIsoBytes("%page " + i +"\n"));
            page.flush();
        }
        pdfDoc1.close();

        PdfReader reader2 = new PdfReader(new FileInputStream(filename1));
        PdfWriter writer2 = new PdfWriter(new FileOutputStream(filename2));
        writer2.setFullCompression(false);
        PdfDocument pdfDoc2 = new PdfDocument(reader2, writer2, true);
        pdfDoc2.close();

        PdfReader reader3 = new PdfReader(new FileInputStream(filename2));
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        Assert.assertEquals("Number of pages", pageCount, pdfDoc3.getNumOfPages());
        Assert.assertEquals("Rebuilt", false, reader3.hasRebuiltXref());
        Assert.assertEquals("Fixed", false, reader3.hasFixedXref());
        verifyPdfPagesCount(pdfDoc3.getCatalog().pageTree.getRoot().getPdfObject());
        pdfDoc3.close();

        com.itextpdf.text.pdf.PdfReader reader = new com.itextpdf.text.pdf.PdfReader(filename2);
        Assert.assertEquals("Rebuilt", false, reader.isRebuilt());
        for (int i = 1; i <= pageCount; i++) {
            byte[] bytes = reader.getPageContent(i);
            Assert.assertEquals("Page content at page " + i, "%page " + i + "\n", new String(bytes));
        }
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

    @Test
    public void outlinesTest() throws IOException, PdfException {
        PdfReader reader = new PdfReader(new FileInputStream(sourceFolder+"iphone_user_guide.pdf"));

        PdfDocument pdfDoc = new PdfDocument(reader);
        PdfOutline outlines = pdfDoc.getCatalog().getOutlines();
        List<PdfOutline> children = outlines.getAllChildren().get(0).getAllChildren();

        Assert.assertEquals(outlines.getTitle(), "Outlines");
        Assert.assertEquals(children.size(), 13);
        Assert.assertTrue(children.get(0).getDestination() instanceof PdfStringDestination);
    }

    static void verifyPdfPagesCount(PdfObject root) throws PdfException {
        if (root.getType() == PdfObject.IndirectReference)
            root = ((PdfIndirectReference)root).getRefersTo();
        PdfDictionary pages = (PdfDictionary)root;
        if (!pages.containsKey(PdfName.Kids)) return;
        PdfNumber count = pages.getAsNumber(PdfName.Count);
        if (count != null) {
            Assert.assertTrue("PdfPages with zero count", count.getIntValue() > 0);
        }
        PdfObject kids = pages.get(PdfName.Kids);
        if (kids.getType() == PdfObject.Array) {
            for (PdfObject kid : (PdfArray)kids){
                verifyPdfPagesCount(kid);
            }
        } else {
            verifyPdfPagesCount(kids);
        }
    }
}
