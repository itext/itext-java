package com.itextpdf.kernel.pdf;

import com.itextpdf.io.source.ByteUtils;
import com.itextpdf.kernel.xmp.XMPException;
import com.itextpdf.kernel.xmp.XMPMetaFactory;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.*;

@Category(IntegrationTest.class)
public class PdfStampingTest extends ExtendedITextTest {

    static final public String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/PdfStampingTest/";
    static final public String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/PdfStampingTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void stamping1() throws IOException {
        String filename1 = destinationFolder + "stamping1_1.pdf";
        String filename2 = destinationFolder + "stamping1_2.pdf";

        FileOutputStream fos1 = new FileOutputStream(filename1);
        PdfWriter writer1 = new PdfWriter(fos1);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        pdfDoc1.getDocumentInfo().setAuthor("Alexander Chingarev").
                setCreator("iText 6").
                setTitle("Empty iText 6 Document");
        PdfPage page1 = pdfDoc1.addNewPage();
        page1.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%Hello World\n"));
        page1.flush();
        pdfDoc1.close();

        FileInputStream fis2 = new FileInputStream(filename1);
        PdfReader reader2 = new PdfReader(fis2);
        FileOutputStream fos2 = new FileOutputStream(filename2);
        PdfWriter writer2 = new PdfWriter(fos2);
        PdfDocument pdfDoc2 = new PdfDocument(reader2, writer2);
        pdfDoc2.getDocumentInfo().setCreator("iText 7").setTitle("Empty iText 7 Document");
        pdfDoc2.close();

        PdfReader reader3 = new PdfReader(new FileInputStream(filename2));
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumberOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        assertEquals("Rebuilt", false, reader3.hasRebuiltXref());
        assertEquals("Fixed", false, reader3.hasFixedXref());
        verifyPdfPagesCount(pdfDoc3.getCatalog().pageTree.getRoot().getPdfObject());
        pdfDoc3.close();

        PdfReader reader = new PdfReader(destinationFolder + "stamping1_2.pdf");
        PdfDocument document = new PdfDocument(reader);
        assertEquals("Rebuilt", false, reader.hasRebuiltXref());
        PdfDictionary trailer = document.getTrailer();
        PdfDictionary info = trailer.getAsDictionary(PdfName.Info);
        PdfString creator = info.getAsString(PdfName.Creator);
        assertEquals("iText 7", creator.toString());
        byte[] bytes = document.getPage(1).getContentBytes();
        assertEquals("%Hello World\n", new String(bytes));
        String date = document.getDocumentInfo().getPdfObject().getAsString(PdfName.ModDate).getValue();
        Calendar cl = PdfDate.decode(date);
        long diff = new GregorianCalendar().getTimeInMillis() - cl.getTimeInMillis();
        String message = "Unexpected creation date. Different from now is " + (float) diff / 1000 + "s";
        assertTrue(message, diff < 5000);
        reader.close();
    }

    @Test
    public void stamping2() throws IOException {
        String filename1 = destinationFolder + "stamping2_1.pdf";
        String filename2 = destinationFolder + "stamping2_2.pdf";

        FileOutputStream fos1 = new FileOutputStream(filename1);
        PdfWriter writer1 = new PdfWriter(fos1);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        PdfPage page1 = pdfDoc1.addNewPage();
        page1.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page 1\n"));
        page1.flush();
        pdfDoc1.close();

        FileInputStream fis2 = new FileInputStream(filename1);
        PdfReader reader2 = new PdfReader(fis2);
        FileOutputStream fos2 = new FileOutputStream(filename2);
        PdfWriter writer2 = new PdfWriter(fos2);
        PdfDocument pdfDoc2 = new PdfDocument(reader2, writer2);
        PdfPage page2 = pdfDoc2.addNewPage();
        page2.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page 2\n"));
        page2.flush();
        pdfDoc2.close();

        PdfReader reader3 = new PdfReader(new FileInputStream(filename2));
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumberOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        assertEquals("Rebuilt", false, reader3.hasRebuiltXref());
        assertEquals("Fixed", false, reader3.hasFixedXref());
        verifyPdfPagesCount(pdfDoc3.getCatalog().pageTree.getRoot().getPdfObject());
        pdfDoc3.close();

        PdfReader reader = new PdfReader(destinationFolder + "stamping2_2.pdf");
        PdfDocument pdfDocument = new PdfDocument(reader);
        assertEquals("Rebuilt", false, reader.hasRebuiltXref());
        byte[] bytes = pdfDocument.getPage(1).getContentBytes();
        assertEquals("%page 1\n", new String(bytes));
        bytes = pdfDocument.getPage(2).getContentBytes();
        assertEquals("%page 2\n", new String(bytes));
        reader.close();
    }

    @Test
    public void stamping3() throws IOException {
        String filename1 = destinationFolder + "stamping3_1.pdf";
        String filename2 = destinationFolder + "stamping3_2.pdf";

        FileOutputStream fos1 = new FileOutputStream(filename1);
        PdfWriter writer1 = new PdfWriter(fos1);
        writer1.setFullCompression(true);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        PdfPage page1 = pdfDoc1.addNewPage();
        page1.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page 1\n"));
        page1.flush();
        pdfDoc1.close();

        FileInputStream fis2 = new FileInputStream(filename1);
        PdfReader reader2 = new PdfReader(fis2);
        FileOutputStream fos2 = new FileOutputStream(filename2);
        PdfWriter writer2 = new PdfWriter(fos2);
        writer2.setFullCompression(true);
        PdfDocument pdfDoc2 = new PdfDocument(reader2, writer2);
        PdfPage page2 = pdfDoc2.addNewPage();
        page2.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page 2\n"));
        page2.flush();
        pdfDoc2.close();

        PdfReader reader3 = new PdfReader(new FileInputStream(filename2));
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumberOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        assertEquals("Rebuilt", false, reader3.hasRebuiltXref());
        assertEquals("Fixed", false, reader3.hasFixedXref());
        verifyPdfPagesCount(pdfDoc3.getCatalog().pageTree.getRoot().getPdfObject());
        pdfDoc3.close();

        PdfReader reader = new PdfReader(filename2);
        PdfDocument pdfDocument = new PdfDocument(reader);
        assertEquals("Rebuilt", false, reader.hasRebuiltXref());
        byte[] bytes = pdfDocument.getPage(1).getContentBytes();
        assertEquals("%page 1\n", new String(bytes));
        bytes = pdfDocument.getPage(2).getContentBytes();
        assertEquals("%page 2\n", new String(bytes));
        reader.close();
    }

    @Test
    public void stamping4() throws IOException {
        String filename1 = destinationFolder + "stamping4_1.pdf";
        String filename2 = destinationFolder + "stamping4_2.pdf";

        FileOutputStream fos1 = new FileOutputStream(filename1);
        PdfWriter writer1 = new PdfWriter(fos1);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        PdfPage page1 = pdfDoc1.addNewPage();
        page1.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page 1\n"));
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
            page2.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page " + i + "\n"));
            page2.flush();
        }
        pdfDoc2.close();

        PdfReader reader3 = new PdfReader(new FileInputStream(filename2));
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumberOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        assertEquals("Rebuilt", false, reader3.hasRebuiltXref());
        assertEquals("Fixed", false, reader3.hasFixedXref());
        verifyPdfPagesCount(pdfDoc3.getCatalog().pageTree.getRoot().getPdfObject());
        pdfDoc3.close();

        PdfReader reader = new PdfReader(filename2);
        PdfDocument pdfDocument = new PdfDocument(reader);
        assertEquals("Rebuilt", false, reader.hasRebuiltXref());
        assertEquals("Page count", pageCount, pdfDocument.getNumberOfPages());
        for (int i = 1; i < pdfDocument.getNumberOfPages(); i++) {
            byte[] bytes = pdfDocument.getPage(i).getContentBytes();
            assertEquals("%page " + i + "\n", new String(bytes));
        }
        reader.close();
    }

    @Test
    public void stamping5() throws IOException {
        String filename1 = destinationFolder + "stamping5_1.pdf";
        String filename2 = destinationFolder + "stamping5_2.pdf";

        FileOutputStream fos1 = new FileOutputStream(filename1);
        PdfWriter writer1 = new PdfWriter(fos1);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        PdfPage page1 = pdfDoc1.addNewPage();
        page1.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page 1\n"));
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
            page2.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page " + i + "\n"));
            page2.flush();
        }
        pdfDoc2.close();

        PdfReader reader3 = new PdfReader(new FileInputStream(filename2));
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumberOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        assertEquals("Rebuilt", false, reader3.hasRebuiltXref());
        assertEquals("Fixed", false, reader3.hasFixedXref());
        verifyPdfPagesCount(pdfDoc3.getCatalog().pageTree.getRoot().getPdfObject());
        pdfDoc3.close();

        PdfReader reader = new PdfReader(filename2);
        PdfDocument pdfDocument = new PdfDocument(reader);
        assertEquals("Rebuilt", false, reader.hasRebuiltXref());
        assertEquals("Page count", pageCount, pdfDocument.getNumberOfPages());
        for (int i = 1; i < pdfDocument.getNumberOfPages(); i++) {
            byte[] bytes = pdfDocument.getPage(i).getContentBytes();
            assertEquals("%page " + i + "\n", new String(bytes));
        }
        reader.close();
    }

    @Test
    public void stamping6() throws IOException {
        String filename1 = destinationFolder + "stamping6_1.pdf";
        String filename2 = destinationFolder + "stamping6_2.pdf";

        FileOutputStream fos1 = new FileOutputStream(filename1);
        PdfWriter writer1 = new PdfWriter(fos1);
        writer1.setFullCompression(true);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        PdfPage page1 = pdfDoc1.addNewPage();
        page1.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page 1\n"));
        page1.flush();
        pdfDoc1.close();

        FileInputStream fis2 = new FileInputStream(filename1);
        PdfReader reader2 = new PdfReader(fis2);
        FileOutputStream fos2 = new FileOutputStream(filename2);
        PdfWriter writer2 = new PdfWriter(fos2);
        PdfDocument pdfDoc2 = new PdfDocument(reader2, writer2);
        PdfPage page2 = pdfDoc2.addNewPage();
        page2.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page 2\n"));
        page2.flush();
        pdfDoc2.close();

        PdfReader reader3 = new PdfReader(new FileInputStream(filename2));
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumberOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        assertEquals("Rebuilt", false, reader3.hasRebuiltXref());
        assertEquals("Fixed", false, reader3.hasFixedXref());
        verifyPdfPagesCount(pdfDoc3.getCatalog().pageTree.getRoot().getPdfObject());
        pdfDoc3.close();

        PdfReader reader = new PdfReader(filename2);
        PdfDocument pdfDocument = new PdfDocument(reader);
        assertEquals("Rebuilt", false, reader.hasRebuiltXref());
        byte[] bytes = pdfDocument.getPage(1).getContentBytes();
        assertEquals("%page 1\n", new String(bytes));
        bytes = pdfDocument.getPage(2).getContentBytes();
        assertEquals("%page 2\n", new String(bytes));
        reader.close();
    }

    @Test
    public void stamping7() throws IOException {
        String filename1 = destinationFolder + "stamping7_1.pdf";
        String filename2 = destinationFolder + "stamping7_2.pdf";

        FileOutputStream fos1 = new FileOutputStream(filename1);
        PdfWriter writer1 = new PdfWriter(fos1);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        PdfPage page1 = pdfDoc1.addNewPage();
        page1.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page 1\n"));
        page1.flush();
        pdfDoc1.close();

        FileInputStream fis2 = new FileInputStream(filename1);
        PdfReader reader2 = new PdfReader(fis2);
        FileOutputStream fos2 = new FileOutputStream(filename2);
        PdfWriter writer2 = new PdfWriter(fos2);
        writer2.setFullCompression(true);
        PdfDocument pdfDoc2 = new PdfDocument(reader2, writer2);
        PdfPage page2 = pdfDoc2.addNewPage();
        page2.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page 2\n"));
        page2.flush();
        pdfDoc2.close();

        PdfReader reader3 = new PdfReader(new FileInputStream(filename2));
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumberOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        assertEquals("Rebuilt", false, reader3.hasRebuiltXref());
        assertEquals("Fixed", false, reader3.hasFixedXref());
        verifyPdfPagesCount(pdfDoc3.getCatalog().pageTree.getRoot().getPdfObject());
        pdfDoc3.close();

        PdfReader reader = new PdfReader(filename2);
        PdfDocument pdfDocument = new PdfDocument(reader);
        assertEquals("Rebuilt", false, reader.hasRebuiltXref());
        byte[] bytes = pdfDocument.getPage(1).getContentBytes();
        assertEquals("%page 1\n", new String(bytes));
        bytes = pdfDocument.getPage(2).getContentBytes();
        assertEquals("%page 2\n", new String(bytes));
        reader.close();
    }

    @Test
    public void stamping8() throws IOException {
        String filename1 = destinationFolder + "stamping8_1.pdf";
        String filename2 = destinationFolder + "stamping8_2.pdf";
        int pageCount = 10;

        FileOutputStream fos1 = new FileOutputStream(filename1);
        PdfWriter writer1 = new PdfWriter(fos1);
        writer1.setFullCompression(true);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        for (int i = 1; i <= pageCount; i++) {
            PdfPage page = pdfDoc1.addNewPage();
            page.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page " + i + "\n"));
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
        for (int i = 0; i < pdfDoc3.getNumberOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        assertEquals("Number of pages", pageCount, pdfDoc3.getNumberOfPages());
        assertEquals("Rebuilt", false, reader3.hasRebuiltXref());
        assertEquals("Fixed", false, reader3.hasFixedXref());
        verifyPdfPagesCount(pdfDoc3.getCatalog().pageTree.getRoot().getPdfObject());
        pdfDoc3.close();

        PdfReader reader = new PdfReader(filename2);
        PdfDocument pdfDocument = new PdfDocument(reader);
        assertEquals("Rebuilt", false, reader.hasRebuiltXref());
        for (int i = 1; i <= pageCount; i++) {
            byte[] bytes = pdfDocument.getPage(i).getContentBytes();
            assertEquals("Page content at page " + i, "%page " + i + "\n", new String(bytes));
        }
        reader.close();
    }

    @Test
    public void stamping9() throws IOException {
        String filename1 = destinationFolder + "stamping9_1.pdf";
        String filename2 = destinationFolder + "stamping9_2.pdf";
        int pageCount = 10;

        FileOutputStream fos1 = new FileOutputStream(filename1);
        PdfWriter writer1 = new PdfWriter(fos1);
        writer1.setFullCompression(false);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        for (int i = 1; i <= pageCount; i++) {
            PdfPage page = pdfDoc1.addNewPage();
            page.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page " + i + "\n"));
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
        for (int i = 0; i < pdfDoc3.getNumberOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        assertEquals("Number of pages", pageCount, pdfDoc3.getNumberOfPages());
        assertEquals("Rebuilt", false, reader3.hasRebuiltXref());
        assertEquals("Fixed", false, reader3.hasFixedXref());
        verifyPdfPagesCount(pdfDoc3.getCatalog().pageTree.getRoot().getPdfObject());
        pdfDoc3.close();

        PdfReader reader = new PdfReader(filename2);
        PdfDocument pdfDocument = new PdfDocument(reader);
        assertEquals("Rebuilt", false, reader.hasRebuiltXref());
        for (int i = 1; i <= pageCount; i++) {
            byte[] bytes = pdfDocument.getPage(i).getContentBytes();
            assertEquals("Page content at page " + i, "%page " + i + "\n", new String(bytes));
        }
        reader.close();
    }

    @Test
    public void stamping10() throws IOException {
        String filename1 = destinationFolder + "stamping10_1.pdf";
        String filename2 = destinationFolder + "stamping10_2.pdf";
        int pageCount = 10;

        FileOutputStream fos1 = new FileOutputStream(filename1);
        PdfWriter writer1 = new PdfWriter(fos1);
        writer1.setFullCompression(true);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        for (int i = 1; i <= pageCount; i++) {
            PdfPage page = pdfDoc1.addNewPage();
            page.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page " + i + "\n"));
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
        for (int i = 0; i < pdfDoc3.getNumberOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        assertEquals("Number of pages", pageCount, pdfDoc3.getNumberOfPages());
        assertEquals("Rebuilt", false, reader3.hasRebuiltXref());
        assertEquals("Fixed", false, reader3.hasFixedXref());
        verifyPdfPagesCount(pdfDoc3.getCatalog().pageTree.getRoot().getPdfObject());
        pdfDoc3.close();

        PdfReader reader = new PdfReader(filename2);
        PdfDocument pdfDocument = new PdfDocument(reader);
        assertEquals("Rebuilt", false, reader.hasRebuiltXref());
        for (int i = 1; i <= pageCount; i++) {
            byte[] bytes = pdfDocument.getPage(i).getContentBytes();
            assertEquals("Page content at page " + i, "%page " + i + "\n", new String(bytes));
        }
        reader.close();
    }

    @Test
    public void stamping11() throws IOException {
        String filename1 = destinationFolder + "stamping11_1.pdf";
        String filename2 = destinationFolder + "stamping11_2.pdf";
        int pageCount = 10;

        FileOutputStream fos1 = new FileOutputStream(filename1);
        PdfWriter writer1 = new PdfWriter(fos1);
        writer1.setFullCompression(false);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        for (int i = 1; i <= pageCount; i++) {
            PdfPage page = pdfDoc1.addNewPage();
            page.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page " + i + "\n"));
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
        for (int i = 0; i < pdfDoc3.getNumberOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        assertEquals("Number of pages", pageCount, pdfDoc3.getNumberOfPages());
        assertEquals("Rebuilt", false, reader3.hasRebuiltXref());
        assertEquals("Fixed", false, reader3.hasFixedXref());
        verifyPdfPagesCount(pdfDoc3.getCatalog().pageTree.getRoot().getPdfObject());
        pdfDoc3.close();

        PdfReader reader = new PdfReader(filename2);
        PdfDocument pdfDocument = new PdfDocument(reader);
        assertEquals("Rebuilt", false, reader.hasRebuiltXref());
        for (int i = 1; i <= pageCount; i++) {
            byte[] bytes = pdfDocument.getPage(i).getContentBytes();
            assertEquals("Page content at page " + i, "%page " + i + "\n", new String(bytes));
        }
        reader.close();
    }

    @Test
    public void stamping12() throws IOException {
        String filename1 = destinationFolder + "stamping12_1.pdf";
        String filename2 = destinationFolder + "stamping12_2.pdf";
        int pageCount = 1010;

        FileOutputStream fos1 = new FileOutputStream(filename1);
        PdfWriter writer1 = new PdfWriter(fos1);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        for (int i = 1; i <= pageCount; i++) {
            PdfPage page = pdfDoc1.addNewPage();
            page.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page " + i + "\n"));
            page.flush();
        }
        pdfDoc1.close();

        PdfReader reader2 = new PdfReader(new FileInputStream(filename1));
        PdfWriter writer2 = new PdfWriter(new FileOutputStream(filename2));
        PdfDocument pdfDoc2 = new PdfDocument(reader2, writer2);

        int newPageCount = 10;
        for (int i = pageCount; i > newPageCount; i--) {
            assertNotNull("Remove page " + i, pdfDoc2.removePage(i));
        }
        pdfDoc2.close();

        PdfReader reader3 = new PdfReader(new FileInputStream(filename2));
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 1; i <= pdfDoc3.getNumberOfPages(); i++) {
            pdfDoc3.getPage(i);
        }
        PdfPage pdfPage = pdfDoc3.getPage(1);
        PdfDictionary root = pdfPage.getPdfObject().getAsDictionary(PdfName.Parent);
        assertEquals("PdfPages kids count", newPageCount, root.getAsArray(PdfName.Kids).size());
        assertEquals("Number of pages", newPageCount, pdfDoc3.getNumberOfPages());
        assertEquals("Rebuilt", false, reader3.hasRebuiltXref());
        assertEquals("Fixed", false, reader3.hasFixedXref());
        verifyPdfPagesCount(pdfDoc3.getCatalog().pageTree.getRoot().getPdfObject());
        pdfDoc3.close();

        PdfReader reader = new PdfReader(filename2);
        PdfDocument pdfDocument = new PdfDocument(reader);
        assertEquals("Rebuilt", false, reader.hasRebuiltXref());
        for (int i = 1; i <= pdfDocument.getNumberOfPages(); i++) {
            byte[] bytes = pdfDocument.getPage(i).getContentBytes();
            assertEquals("Page content at page " + i, "%page " + i + "\n", new String(bytes));
        }
        reader.close();
    }

    @Test
    public void stamping13() throws IOException {
        String filename1 = destinationFolder + "stamping13_1.pdf";
        String filename2 = destinationFolder + "stamping13_2.pdf";
        int pageCount = 1010;

        FileOutputStream fos1 = new FileOutputStream(filename1);
        PdfWriter writer1 = new PdfWriter(fos1);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        for (int i = 1; i <= pageCount; i++) {
            PdfPage page = pdfDoc1.addNewPage();
            page.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page " + i + "\n"));
            page.flush();
        }
        pdfDoc1.close();

        PdfReader reader2 = new PdfReader(new FileInputStream(filename1));
        PdfWriter writer2 = new PdfWriter(new FileOutputStream(filename2));
        PdfDocument pdfDoc2 = new PdfDocument(reader2, writer2);

        for (int i = pageCount; i > 1; i--) {
            assertNotNull("Remove page " + i, pdfDoc2.removePage(i));
        }
        pdfDoc2.removePage(1);
        for (int i = 1; i <= pageCount; i++) {
            PdfPage page = pdfDoc2.addNewPage();
            page.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page " + i + "\n"));
            page.flush();
        }
        pdfDoc2.close();

        PdfReader reader3 = new PdfReader(new FileInputStream(filename2));
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 1; i <= pdfDoc3.getNumberOfPages(); i++) {
            pdfDoc3.getPage(i);
        }
        PdfArray rootKids = pdfDoc3.getCatalog().pageTree.getRoot().getPdfObject().getAsArray(PdfName.Kids);
        assertEquals("Page root kids count", 2, rootKids.size());
        assertEquals("Number of pages", pageCount, pdfDoc3.getNumberOfPages());
        assertEquals("Rebuilt", false, reader3.hasRebuiltXref());
        assertEquals("Fixed", false, reader3.hasFixedXref());
        verifyPdfPagesCount(pdfDoc3.getCatalog().pageTree.getRoot().getPdfObject());
        pdfDoc3.close();

        PdfReader reader = new PdfReader(filename2);
        PdfDocument pdfDocument = new PdfDocument(reader);
        assertEquals("Rebuilt", false, reader.hasRebuiltXref());
        for (int i = 1; i <= pageCount; i++) {
            byte[] bytes = pdfDocument.getPage(i).getContentBytes();
            assertEquals("Page content at page " + i, "%page " + i + "\n", new String(bytes));
        }
        reader.close();
    }

    @Test
    @Ignore
    public void stamping14() throws IOException {
        String filename1 = sourceFolder + "20000PagesDocument.pdf";
        String filename2 = destinationFolder + "stamping14.pdf";

        PdfReader reader2 = new PdfReader(new FileInputStream(filename1));
        PdfWriter writer2 = new PdfWriter(new FileOutputStream(filename2));
        PdfDocument pdfDoc2 = new PdfDocument(reader2, writer2);

        for (int i = pdfDoc2.getNumberOfPages(); i > 3; i--) {
            assertNotNull("Remove page " + i, pdfDoc2.removePage(i));
        }

        pdfDoc2.close();

        PdfReader reader3 = new PdfReader(new FileInputStream(filename2));
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 1; i <= pdfDoc3.getNumberOfPages(); i++) {
            pdfDoc3.getPage(i);
        }
        assertTrue("Xref size is " + pdfDoc3.getXref().size(), pdfDoc3.getXref().size() < 20);
        assertEquals("Number of pages", 3, pdfDoc3.getNumberOfPages());
        assertEquals("Rebuilt", false, reader3.hasRebuiltXref());
        assertEquals("Fixed", false, reader3.hasFixedXref());
        verifyPdfPagesCount(pdfDoc3.getCatalog().pageTree.getRoot().getPdfObject());
        pdfDoc3.close();

        PdfReader reader = new PdfReader(filename2);
        PdfDocument pdfDocument = new PdfDocument(reader);
        assertEquals("Rebuilt", false, reader.hasRebuiltXref());
        for (int i = 1; i <= pdfDocument.getNumberOfPages(); i++) {
            byte[] bytes = pdfDocument.getPage(i).getContentBytes();
            assertEquals("Page content at page " + i, "%page " + i + "\n", new String(bytes));
        }
        reader.close();
    }

    @Test
    public void stampingStreamsCompression01() throws IOException {
        // by default, old streams should not be recompressed

        String filenameIn = sourceFolder + "stampingStreamsCompression.pdf";
        String filenameOut = destinationFolder + "stampingStreamsCompression01.pdf";

        PdfReader reader = new PdfReader(filenameIn);
        PdfWriter writer = new PdfWriter(filenameOut);
        writer.setCompressionLevel(PdfOutputStream.BEST_COMPRESSION);
        PdfDocument doc = new PdfDocument(reader, writer);
        PdfStream stream = (PdfStream) doc.getPdfObject(6);
        int lengthBefore = stream.getLength();
        doc.close();

        doc = new PdfDocument(new PdfReader(filenameOut));
        stream = (PdfStream) doc.getPdfObject(6);
        int lengthAfter = stream.getLength();

        assertTrue(lengthBefore == lengthAfter);
        assertEquals(5731884, lengthBefore);
        assertEquals(5731884, lengthAfter);
    }

    @Test
    public void stampingStreamsCompression02() throws IOException {
        // if user specified, stream may be uncompressed

        String filenameIn = sourceFolder + "stampingStreamsCompression.pdf";
        String filenameOut = destinationFolder + "stampingStreamsCompression02.pdf";

        PdfReader reader = new PdfReader(filenameIn);
        PdfWriter writer = new PdfWriter(filenameOut);
        PdfDocument doc = new PdfDocument(reader, writer);
        PdfStream stream = (PdfStream) doc.getPdfObject(6);
        int lengthBefore = stream.getLength();
        stream.setCompressionLevel(PdfOutputStream.NO_COMPRESSION);
        doc.close();

        doc = new PdfDocument(new PdfReader(filenameOut));
        stream = (PdfStream) doc.getPdfObject(6);
        int lengthAfter = stream.getLength();

        assertTrue(lengthBefore < lengthAfter);
        assertEquals(5731884, lengthBefore);
        assertEquals(11321910, lengthAfter);
    }

    @Test
    public void stampingStreamsCompression03() throws IOException {
        // if user specified, stream may be recompressed

        String filenameIn = sourceFolder + "stampingStreamsCompression.pdf";
        String filenameOut = destinationFolder + "stampingStreamsCompression03.pdf";

        PdfReader reader = new PdfReader(filenameIn);
        PdfWriter writer = new PdfWriter(filenameOut);
        PdfDocument doc = new PdfDocument(reader, writer);
        PdfStream stream = (PdfStream) doc.getPdfObject(6);
        int lengthBefore = stream.getLength();
        stream.setCompressionLevel(PdfOutputStream.BEST_COMPRESSION);
        doc.close();

        doc = new PdfDocument(new PdfReader(filenameOut));
        stream = (PdfStream) doc.getPdfObject(6);
        int lengthAfter = stream.getLength();

        assertTrue(lengthBefore > lengthAfter);
        assertEquals(5731884, lengthBefore);
        assertEquals(5729270, lengthAfter);
    }

    @Test
    public void stampingXmp1() throws IOException, XMPException {
        String filename1 = destinationFolder + "stampingXmp1_1.pdf";
        String filename2 = destinationFolder + "stampingXmp1_2.pdf";
        int pageCount = 10;

        FileOutputStream fos1 = new FileOutputStream(filename1);
        PdfWriter writer1 = new PdfWriter(fos1);
        writer1.setFullCompression(true);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        for (int i = 1; i <= pageCount; i++) {
            PdfPage page = pdfDoc1.addNewPage();
            page.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page " + i + "\n"));
            page.flush();
        }
        pdfDoc1.close();

        PdfReader reader2 = new PdfReader(new FileInputStream(filename1));
        PdfWriter writer2 = new PdfWriter(new FileOutputStream(filename2));
        writer2.setFullCompression(false);
        PdfDocument pdfDoc2 = new PdfDocument(reader2, writer2);
        pdfDoc2.getDocumentInfo().setAuthor("Alexander Chingarev");
        pdfDoc2.createXmpMetadata();
        pdfDoc2.close();

        PdfReader reader3 = new PdfReader(new FileInputStream(filename2));
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumberOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        assertNotNull("XmpMetadata not found", XMPMetaFactory.parseFromBuffer(pdfDoc3.getXmpMetadata()));
        assertEquals("Number of pages", pageCount, pdfDoc3.getNumberOfPages());
        assertEquals("Rebuilt", false, reader3.hasRebuiltXref());
        assertEquals("Fixed", false, reader3.hasFixedXref());
        verifyPdfPagesCount(pdfDoc3.getCatalog().pageTree.getRoot().getPdfObject());
        pdfDoc3.close();

        PdfReader reader = new PdfReader(filename2);
        PdfDocument pdfDocument = new PdfDocument(reader);
        assertEquals("Rebuilt", false, reader.hasRebuiltXref());
        for (int i = 1; i <= pageCount; i++) {
            byte[] bytes = pdfDocument.getPage(i).getContentBytes();
            assertEquals("Page content at page " + i, "%page " + i + "\n", new String(bytes));
        }
        reader.close();
    }

    @Test
    public void stampingXmp2() throws IOException, XMPException {
        String filename1 = destinationFolder + "stampingXmp2_1.pdf";
        String filename2 = destinationFolder + "stampingXmp2_2.pdf";
        int pageCount = 10;

        FileOutputStream fos1 = new FileOutputStream(filename1);
        PdfWriter writer1 = new PdfWriter(fos1);
        writer1.setFullCompression(false);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        for (int i = 1; i <= pageCount; i++) {
            PdfPage page = pdfDoc1.addNewPage();
            page.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page " + i + "\n"));
            page.flush();
        }
        pdfDoc1.close();

        PdfReader reader2 = new PdfReader(new FileInputStream(filename1));
        PdfWriter writer2 = new PdfWriter(new FileOutputStream(filename2));
        writer2.setFullCompression(true);
        PdfDocument pdfDoc2 = new PdfDocument(reader2, writer2);
        pdfDoc2.getDocumentInfo().setAuthor("Alexander Chingarev");
        pdfDoc2.getDocumentInfo().setAuthor("Alexander Chingarev");
        pdfDoc2.createXmpMetadata();
        pdfDoc2.close();

        PdfReader reader3 = new PdfReader(new FileInputStream(filename2));
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumberOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        assertNotNull("XmpMetadata not found",  XMPMetaFactory.parseFromBuffer(pdfDoc3.getXmpMetadata()));
        assertEquals("Number of pages", pageCount, pdfDoc3.getNumberOfPages());
        assertEquals("Rebuilt", false, reader3.hasRebuiltXref());
        assertEquals("Fixed", false, reader3.hasFixedXref());
        verifyPdfPagesCount(pdfDoc3.getCatalog().pageTree.getRoot().getPdfObject());
        pdfDoc3.close();

        PdfReader reader = new PdfReader(filename2);
        PdfDocument pdfDocument = new PdfDocument(reader);
        assertEquals("Rebuilt", false, reader.hasRebuiltXref());
        for (int i = 1; i <= pageCount; i++) {
            byte[] bytes = pdfDocument.getPage(i).getContentBytes();
            assertEquals("Page content at page " + i, "%page " + i + "\n", new String(bytes));
        }
        reader.close();
    }

    @Test
    public void stampingAppend1() throws IOException {
        String filename1 = destinationFolder + "stampingAppend1_1.pdf";
        String filename2 = destinationFolder + "stampingAppend1_2.pdf";

        FileOutputStream fos1 = new FileOutputStream(filename1);
        PdfWriter writer1 = new PdfWriter(fos1);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        pdfDoc1.getDocumentInfo().setAuthor("Alexander Chingarev").
                setCreator("iText 6").
                setTitle("Empty iText 6 Document");
        PdfPage page1 = pdfDoc1.addNewPage();
        page1.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%Hello World\n"));
        page1.flush();
        pdfDoc1.close();

        FileInputStream fis2 = new FileInputStream(filename1);
        PdfReader reader2 = new PdfReader(fis2);
        FileOutputStream fos2 = new FileOutputStream(filename2);
        PdfWriter writer2 = new PdfWriter(fos2);
        PdfDocument pdfDoc2 = new PdfDocument(reader2, writer2, true);
        pdfDoc2.getDocumentInfo().setCreator("iText 7").setTitle("Empty iText 7 Document");
        pdfDoc2.getDocumentInfo().setModified();
        pdfDoc2.close();

        PdfReader reader3 = new PdfReader(new FileInputStream(filename2));
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumberOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        assertEquals("Rebuilt", false, reader3.hasRebuiltXref());
        assertEquals("Fixed", false, reader3.hasFixedXref());
        verifyPdfPagesCount(pdfDoc3.getCatalog().pageTree.getRoot().getPdfObject());
        pdfDoc3.close();

        PdfReader reader = new PdfReader(filename2);
        PdfDocument pdfDocument = new PdfDocument(reader);
        assertEquals("Rebuilt", false, reader.hasRebuiltXref());
        PdfDictionary trailer = pdfDocument.getTrailer();
        PdfDictionary info = trailer.getAsDictionary(PdfName.Info);
        PdfString creator = info.getAsString(PdfName.Creator);
        assertEquals("iText 7", creator.toString());
        byte[] bytes = pdfDocument.getPage(1).getContentBytes();
        assertEquals("%Hello World\n", new String(bytes));
        String date = pdfDocument.getDocumentInfo().getPdfObject().getAsString(PdfName.ModDate).getValue();
        Calendar cl = PdfDate.decode(date);
        long diff = new GregorianCalendar().getTimeInMillis() - cl.getTimeInMillis();
        String message = "Unexpected creation date. Different from now is " + (float) diff / 1000 + "s";
        assertTrue(message, diff < 5000);
        reader.close();
    }

    @Test
    public void stampingAppend2() throws IOException {
        String filename1 = destinationFolder + "stampingAppend2_1.pdf";
        String filename2 = destinationFolder + "stampingAppend2_2.pdf";

        FileOutputStream fos1 = new FileOutputStream(filename1);
        PdfWriter writer1 = new PdfWriter(fos1);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        PdfPage page1 = pdfDoc1.addNewPage();
        page1.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page 1\n"));
        page1.flush();
        pdfDoc1.close();

        FileInputStream fis2 = new FileInputStream(filename1);
        PdfReader reader2 = new PdfReader(fis2);
        FileOutputStream fos2 = new FileOutputStream(filename2);
        PdfWriter writer2 = new PdfWriter(fos2);
        PdfDocument pdfDoc2 = new PdfDocument(reader2, writer2, true);
        PdfPage page2 = pdfDoc2.addNewPage();
        page2.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page 2\n"));
        page2.setModified();
        page2.flush();
        pdfDoc2.close();

        PdfReader reader3 = new PdfReader(new FileInputStream(filename2));
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumberOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        assertEquals("Rebuilt", false, reader3.hasRebuiltXref());
        assertEquals("Fixed", false, reader3.hasFixedXref());
        verifyPdfPagesCount(pdfDoc3.getCatalog().pageTree.getRoot().getPdfObject());
        pdfDoc3.close();

        PdfReader reader = new PdfReader(filename2);
        PdfDocument pdfDocument = new PdfDocument(reader);
        assertEquals("Rebuilt", false, reader.hasRebuiltXref());
        byte[] bytes = pdfDocument.getPage(1).getContentBytes();
        assertEquals("%page 1\n", new String(bytes));
        bytes = pdfDocument.getPage(2).getContentBytes();
        assertEquals("%page 2\n", new String(bytes));
        reader.close();
    }

    @Test
    public void stampingAppend3() throws IOException {
        String filename1 = destinationFolder + "stampingAppend3_1.pdf";
        String filename2 = destinationFolder + "stampingAppend3_2.pdf";

        FileOutputStream fos1 = new FileOutputStream(filename1);
        PdfWriter writer1 = new PdfWriter(fos1);
        writer1.setFullCompression(true);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        PdfPage page1 = pdfDoc1.addNewPage();
        page1.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page 1\n"));
        page1.flush();
        pdfDoc1.close();

        FileInputStream fis2 = new FileInputStream(filename1);
        PdfReader reader2 = new PdfReader(fis2);
        FileOutputStream fos2 = new FileOutputStream(filename2);
        PdfWriter writer2 = new PdfWriter(fos2);
        PdfDocument pdfDoc2 = new PdfDocument(reader2, writer2, true);
        PdfPage page2 = pdfDoc2.addNewPage();
        page2.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page 2\n"));

        page2.flush();
        pdfDoc2.close();

        PdfReader reader3 = new PdfReader(new FileInputStream(filename2));
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumberOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        assertEquals("Rebuilt", false, reader3.hasRebuiltXref());
        assertEquals("Fixed", false, reader3.hasFixedXref());
        verifyPdfPagesCount(pdfDoc3.getCatalog().pageTree.getRoot().getPdfObject());
        pdfDoc3.close();

        PdfReader reader = new PdfReader(filename2);
        PdfDocument pdfDocument = new PdfDocument(reader);
        assertEquals("Rebuilt", false, reader.hasRebuiltXref());
        byte[] bytes = pdfDocument.getPage(1).getContentBytes();
        assertEquals("%page 1\n", new String(bytes));
        bytes = pdfDocument.getPage(2).getContentBytes();
        assertEquals("%page 2\n", new String(bytes));
        reader.close();
    }

    @Test
    public void stampingAppend4() throws IOException {
        String filename1 = destinationFolder + "stampingAppend4_1.pdf";
        String filename2 = destinationFolder + "stampingAppend4_2.pdf";

        FileOutputStream fos1 = new FileOutputStream(filename1);
        PdfWriter writer1 = new PdfWriter(fos1);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        PdfPage page1 = pdfDoc1.addNewPage();
        page1.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page 1\n"));
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
            page2.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page " + i + "\n"));
            page2.flush();
        }

        pdfDoc2.close();

        PdfReader reader3 = new PdfReader(new FileInputStream(filename2));
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumberOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        assertEquals("Rebuilt", false, reader3.hasRebuiltXref());
        assertEquals("Fixed", false, reader3.hasFixedXref());
        verifyPdfPagesCount(pdfDoc3.getCatalog().pageTree.getRoot().getPdfObject());
        pdfDoc3.close();

        PdfReader reader = new PdfReader(filename2);
        PdfDocument pdfDocument = new PdfDocument(reader);
        assertEquals("Rebuilt", false, reader.hasRebuiltXref());
        assertEquals("Page count", pageCount, pdfDocument.getNumberOfPages());
        for (int i = 1; i < pdfDocument.getNumberOfPages(); i++) {
            byte[] bytes = pdfDocument.getPage(i).getContentBytes();
            assertEquals("%page " + i + "\n", new String(bytes));
        }
        reader.close();
    }

    @Test
    public void stampingAppend5() throws IOException {
        String filename1 = destinationFolder + "stampingAppend5_1.pdf";
        String filename2 = destinationFolder + "stampingAppend5_2.pdf";

        FileOutputStream fos1 = new FileOutputStream(filename1);
        PdfWriter writer1 = new PdfWriter(fos1);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        PdfPage page1 = pdfDoc1.addNewPage();
        page1.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page 1\n"));
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
            page2.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page " + i + "\n"));
            page2.flush();
        }
        pdfDoc2.close();

        PdfReader reader3 = new PdfReader(new FileInputStream(filename2));
        PdfDocument pdfDoc3 = new PdfDocument(reader3);
        for (int i = 0; i < pdfDoc3.getNumberOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        assertEquals("Rebuilt", false, reader3.hasRebuiltXref());
        assertEquals("Fixed", false, reader3.hasFixedXref());
        verifyPdfPagesCount(pdfDoc3.getCatalog().pageTree.getRoot().getPdfObject());
        pdfDoc3.close();

        PdfReader reader = new PdfReader(filename2);
        PdfDocument pdfDocument = new PdfDocument(reader);
        assertEquals("Rebuilt", false, reader.hasRebuiltXref());
        assertEquals("Page count", pageCount, pdfDocument.getNumberOfPages());
        for (int i = 1; i < pdfDocument.getNumberOfPages(); i++) {
            byte[] bytes = pdfDocument.getPage(i).getContentBytes();
            assertEquals("%page " + i + "\n", new String(bytes));
        }
        reader.close();
    }

    @Test
    public void stampingAppend8() throws IOException {
        String filename1 = destinationFolder + "stampingAppend8_1.pdf";
        String filename2 = destinationFolder + "stampingAppend8_2.pdf";
        int pageCount = 10;

        FileOutputStream fos1 = new FileOutputStream(filename1);
        PdfWriter writer1 = new PdfWriter(fos1);
        writer1.setFullCompression(true);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        for (int i = 1; i <= pageCount; i++) {
            PdfPage page = pdfDoc1.addNewPage();
            page.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page " + i + "\n"));
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
        for (int i = 0; i < pdfDoc3.getNumberOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        assertEquals("Number of pages", pageCount, pdfDoc3.getNumberOfPages());
        assertEquals("Rebuilt", false, reader3.hasRebuiltXref());
        assertEquals("Fixed", false, reader3.hasFixedXref());
        verifyPdfPagesCount(pdfDoc3.getCatalog().pageTree.getRoot().getPdfObject());
        pdfDoc3.close();

        PdfReader reader = new PdfReader(filename2);
        PdfDocument pdfDocument = new PdfDocument(reader);
        assertEquals("Rebuilt", false, reader.hasRebuiltXref());
        for (int i = 1; i <= pageCount; i++) {
            byte[] bytes = pdfDocument.getPage(i).getContentBytes();
            assertEquals("Page content at page " + i, "%page " + i + "\n", new String(bytes));
        }
        reader.close();
    }

    @Test
    public void stampingAppend9() throws IOException {
        String filename1 = destinationFolder + "stampingAppend9_1.pdf";
        String filename2 = destinationFolder + "stampingAppend9_2.pdf";
        int pageCount = 10;

        FileOutputStream fos1 = new FileOutputStream(filename1);
        PdfWriter writer1 = new PdfWriter(fos1);
        writer1.setFullCompression(false);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        for (int i = 1; i <= pageCount; i++) {
            PdfPage page = pdfDoc1.addNewPage();
            page.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page " + i + "\n"));
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
        for (int i = 0; i < pdfDoc3.getNumberOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        assertEquals("Number of pages", pageCount, pdfDoc3.getNumberOfPages());
        assertEquals("Rebuilt", false, reader3.hasRebuiltXref());
        assertEquals("Fixed", false, reader3.hasFixedXref());
        verifyPdfPagesCount(pdfDoc3.getCatalog().pageTree.getRoot().getPdfObject());
        pdfDoc3.close();

        PdfReader reader = new PdfReader(filename2);
        PdfDocument pdfDocument = new PdfDocument(reader);
        assertEquals("Rebuilt", false, reader.hasRebuiltXref());
        for (int i = 1; i <= pageCount; i++) {
            byte[] bytes = pdfDocument.getPage(i).getContentBytes();
            assertEquals("Page content at page " + i, "%page " + i + "\n", new String(bytes));
        }
        reader.close();
    }

    @Test
    public void stampingAppend10() throws IOException {
        String filename1 = destinationFolder + "stampingAppend10_1.pdf";
        String filename2 = destinationFolder + "stampingAppend10_2.pdf";
        int pageCount = 10;

        FileOutputStream fos1 = new FileOutputStream(filename1);
        PdfWriter writer1 = new PdfWriter(fos1);
        writer1.setFullCompression(true);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        for (int i = 1; i <= pageCount; i++) {
            PdfPage page = pdfDoc1.addNewPage();
            page.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page " + i + "\n"));
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
        for (int i = 0; i < pdfDoc3.getNumberOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        assertEquals("Number of pages", pageCount, pdfDoc3.getNumberOfPages());
        assertEquals("Rebuilt", false, reader3.hasRebuiltXref());
        assertEquals("Fixed", false, reader3.hasFixedXref());
        verifyPdfPagesCount(pdfDoc3.getCatalog().pageTree.getRoot().getPdfObject());
        pdfDoc3.close();

        PdfReader reader = new PdfReader(filename2);
        PdfDocument pdfDocument = new PdfDocument(reader);
        assertEquals("Rebuilt", false, reader.hasRebuiltXref());
        for (int i = 1; i <= pageCount; i++) {
            byte[] bytes = pdfDocument.getPage(i).getContentBytes();
            assertEquals("Page content at page " + i, "%page " + i + "\n", new String(bytes));
        }
        reader.close();
    }

    @Test
    public void stampingAppend11() throws IOException {
        String filename1 = destinationFolder + "stampingAppend11_1.pdf";
        String filename2 = destinationFolder + "stampingAppend11_2.pdf";
        int pageCount = 10;

        FileOutputStream fos1 = new FileOutputStream(filename1);
        PdfWriter writer1 = new PdfWriter(fos1);
        writer1.setFullCompression(false);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        for (int i = 1; i <= pageCount; i++) {
            PdfPage page = pdfDoc1.addNewPage();
            page.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%page " + i + "\n"));
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
        for (int i = 0; i < pdfDoc3.getNumberOfPages(); i++) {
            pdfDoc3.getPage(i + 1);
        }
        assertEquals("Number of pages", pageCount, pdfDoc3.getNumberOfPages());
        assertEquals("Rebuilt", false, reader3.hasRebuiltXref());
        assertEquals("Fixed", false, reader3.hasFixedXref());
        verifyPdfPagesCount(pdfDoc3.getCatalog().pageTree.getRoot().getPdfObject());
        pdfDoc3.close();

        PdfReader reader = new PdfReader(filename2);
        PdfDocument pdfDocument = new PdfDocument(reader);
        assertEquals("Rebuilt", false, reader.hasRebuiltXref());
        for (int i = 1; i <= pageCount; i++) {
            byte[] bytes = pdfDocument.getPage(i).getContentBytes();
            assertEquals("Page content at page " + i, "%page " + i + "\n", new String(bytes));
        }
        reader.close();
    }

    @Test
    public void stampingVersionTest01() throws IOException {
        // By default the version of the output file should be the same as the original one
        String in = sourceFolder + "hello.pdf";
        String out = destinationFolder + "hello_stamped01.pdf";

        FileInputStream fis = new FileInputStream(in);
        PdfReader reader = new PdfReader(fis);
        PdfDocument pdfDoc = new PdfDocument(reader, new PdfWriter(out));

        assertEquals(PdfVersion.PDF_1_4, pdfDoc.getPdfVersion());

        pdfDoc.close();

        PdfDocument assertPdfDoc = new PdfDocument(new PdfReader(out));
        assertEquals(PdfVersion.PDF_1_4, assertPdfDoc.getPdfVersion());
        assertPdfDoc.close();
    }

    @Test
    public void stampingVersionTest02() throws IOException {
        // There is a possibility to override version in stamping mode
        String in = sourceFolder + "hello.pdf";
        String out = destinationFolder + "hello_stamped02.pdf";

        FileInputStream fis = new FileInputStream(in);
        PdfReader reader = new PdfReader(fis);
        PdfDocument pdfDoc = new PdfDocument(reader, new PdfWriter(out), PdfVersion.PDF_2_0);

        assertEquals(PdfVersion.PDF_2_0, pdfDoc.getPdfVersion());

        pdfDoc.close();

        PdfDocument assertPdfDoc = new PdfDocument(new PdfReader(out));
        assertEquals(PdfVersion.PDF_2_0, assertPdfDoc.getPdfVersion());
        assertPdfDoc.close();
    }

    @Test
    public void stampingAppendVersionTest01() throws IOException {
        // There is a possibility to override version in stamping mode
        String in = sourceFolder + "hello.pdf";
        String out = destinationFolder + "stampingAppendVersionTest01.pdf";

        FileInputStream fis = new FileInputStream(in);
        PdfReader reader = new PdfReader(fis);
        PdfDocument pdfDoc = new PdfDocument(reader, new PdfWriter(out), true, PdfVersion.PDF_2_0);

        assertEquals(PdfVersion.PDF_2_0, pdfDoc.getPdfVersion());

        pdfDoc.close();

        PdfDocument assertPdfDoc = new PdfDocument(new PdfReader(out));
        assertEquals(PdfVersion.PDF_2_0, assertPdfDoc.getPdfVersion());
        assertPdfDoc.close();
    }

    @Test
    public void stampingTestWithTaggedStructure() throws IOException {
        String filename = sourceFolder + "iphone_user_guide.pdf";

        PdfReader reader = new PdfReader(new FileInputStream(filename));
        FileOutputStream fos = new FileOutputStream(destinationFolder + "stampingDocWithTaggedStructure.pdf");
        PdfWriter writer = new PdfWriter(fos);

        PdfDocument pdfDoc = new PdfDocument(reader, writer);
        pdfDoc.close();
    }

    @Test
    public void stampingTestWithFullCompression01() throws IOException, InterruptedException {
        PdfReader reader = new PdfReader(sourceFolder + "fullCompressedDocument.pdf");
        PdfDocument pdfDoc = new PdfDocument(reader, new PdfWriter(destinationFolder + "stampingTestWithFullCompression01.pdf"));

        pdfDoc.close();
        assertEquals(new File(destinationFolder + "stampingTestWithFullCompression01.pdf").length(), new File(sourceFolder + "cmp_stampingTestWithFullCompression01.pdf").length());
    }

    @Test
    public void stampingTestWithFullCompression02() throws IOException, InterruptedException {
        PdfReader reader = new PdfReader(sourceFolder + "fullCompressedDocument.pdf");
        PdfDocument pdfDoc = new PdfDocument(reader, new PdfWriter(destinationFolder + "stampingTestWithFullCompression02.pdf"));
        pdfDoc.getWriter().setFullCompression(false);

        pdfDoc.close();
        assertEquals(new File(destinationFolder + "stampingTestWithFullCompression02.pdf").length(), new File(sourceFolder + "cmp_stampingTestWithFullCompression02.pdf").length());
    }

    static void verifyPdfPagesCount(PdfObject root) {
        if (root.getType() == PdfObject.INDIRECT_REFERENCE)
            root = ((PdfIndirectReference) root).getRefersTo();
        PdfDictionary pages = (PdfDictionary) root;
        if (!pages.containsKey(PdfName.Kids)) return;
        PdfNumber count = pages.getAsNumber(PdfName.Count);
        if (count != null) {
            assertTrue("PdfPages with zero count", count.intValue() > 0);
        }
        PdfObject kids = pages.get(PdfName.Kids);
        if (kids.getType() == PdfObject.ARRAY) {
            for (PdfObject kid : (PdfArray) kids) {
                verifyPdfPagesCount(kid);
            }
        } else {
            verifyPdfPagesCount(kids);
        }
    }
}
