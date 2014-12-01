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
        pdfDoc3.close();

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
    public void stamping3Mix1() throws IOException, PdfException {
        String filename1 =  destinationFolder + "stamping3Mix1_1.pdf";
        String filename2 =  destinationFolder + "stamping3Mix1_2.pdf";

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
    public void stamping3Mix2() throws IOException, PdfException {
        String filename1 =  destinationFolder + "stamping3Mix2_1.pdf";
        String filename2 =  destinationFolder + "stamping3Mix2_2.pdf";

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
        pdfDoc3.close();


        com.itextpdf.text.pdf.PdfReader reader = new com.itextpdf.text.pdf.PdfReader(filename2);
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
