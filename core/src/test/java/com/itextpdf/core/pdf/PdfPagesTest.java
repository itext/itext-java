package com.itextpdf.core.pdf;

import com.itextpdf.core.exceptions.PdfException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

public class PdfPagesTest {
    static final public String destinationFolder = "./target/test/com/itextpdf/core/pdf/PdfPagesTest/";
    static final PdfName PageNum = new PdfName("PageNum");
    static final com.itextpdf.text.pdf.PdfName PageNum5 = new com.itextpdf.text.pdf.PdfName("PageNum");

    @Before
    public void setup() {
        new File(destinationFolder).mkdirs();
    }

    @Test
    public void simplePagesTest() throws IOException, PdfException {
        String filename = "simplePagesTest.pdf";
        FileOutputStream fos = new FileOutputStream(destinationFolder + filename);
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument pdfDoc = new PdfDocument(writer);

        for (int i = 0; i < 111; i++) {
            PdfPage page = pdfDoc.addNewPage();
            page.put(PageNum, new PdfNumber(i));
            page.flush();
        }
        pdfDoc.close();

        verifyPagesOrder(destinationFolder + filename);
    }

    @Test
    public void reversePagesTest() throws IOException, PdfException {
        String filename = "reversePagesTest.pdf";
        FileOutputStream fos = new FileOutputStream(destinationFolder + filename);
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument pdfDoc = new PdfDocument(writer);

        for (int i = 111; i > 0; i--) {
            PdfPage page = new PdfPage(pdfDoc, pdfDoc.getDefaultPageSize());
            pdfDoc.insertPage(1, page);
            page.put(PageNum, new PdfNumber(i));
            page.flush();
        }
        pdfDoc.close();

        verifyPagesOrder(destinationFolder + filename);
    }

    @Test
    public void randomObjectPagesTest() throws IOException, PdfException {
        String filename = "randomObjectPagesTest.pdf";
        int amount = 10000;
        int indexes[] = new int[amount];
        for (int i = 0; i < indexes.length; i++)
            indexes[i] = i+1;

        Random rnd = new Random();
        for (int i = indexes.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            int a = indexes[index];
            indexes[index] = indexes[i];
            indexes[i] = a;
        }

        FileOutputStream fos = new FileOutputStream(destinationFolder + filename);
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        PdfPage pages[] = new PdfPage[amount];

        for (int i = 0; i < indexes.length; i++) {
            PdfPage page = pdfDoc.addNewPage();
            page.put(PageNum, new PdfNumber(indexes[i]));
            //page.flush();
            pages[indexes[i] - 1] = page;
        }

        for (int i = 0; i < pages.length; i++) {
            pdfDoc.removePage(pages[i]);
            pdfDoc.insertPage( i + 1, pages[i]);
        }
        pdfDoc.close();

        verifyPagesOrder(destinationFolder + filename);
    }

    @Test
    public void randomNumberPagesTest() throws IOException, PdfException {
        String filename = "randomNumberPagesTest.pdf";
        int amount = 10000;
        int indexes[] = new int[amount];
        for (int i = 0; i < indexes.length; i++)
            indexes[i] = i+1;

        Random rnd = new Random();
        for (int i = indexes.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            int a = indexes[index];
            indexes[index] = indexes[i];
            indexes[i] = a;
        }

        FileOutputStream fos = new FileOutputStream(destinationFolder + filename);
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        int pages[] = new int[amount];

        for (int i = 0; i < indexes.length; i++) {
            PdfPage page = pdfDoc.addNewPage();
            page.put(PageNum, new PdfNumber(indexes[i]));
            pages[indexes[i] - 1] = indexes[i];
        }

        for (int i = 0; i < pages.length; i++) {
            PdfPage page = pdfDoc.removePage(pages[i]);
            pdfDoc.insertPage(i + 1, page);
        }
        pdfDoc.close();

        verifyPagesOrder(destinationFolder + filename);
    }

    @Test
    public void insertFlushedPageTest() throws IOException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        PdfDocument pdfDoc = new PdfDocument(writer);
        PdfPage page = pdfDoc.addNewPage();
        boolean error = false;
        try {
            page.flush();
            pdfDoc.removePage(page);
            pdfDoc.insertPage(1, page);
            pdfDoc.close();
        } catch (PdfException e) {
            if (e.getMessage() == PdfException.flushedPageCannotBeAddedOrInserted)
                error = true;
        }

        Assert.assertTrue(error);
    }

    @Test
    public void addFlushedPageTest() throws IOException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        PdfDocument pdfDoc = new PdfDocument(writer);
        PdfPage page = pdfDoc.addNewPage();
        boolean error = false;
        try {
            page.flush();
            pdfDoc.removePage(page);
            pdfDoc.addPage(page);
            pdfDoc.close();
        } catch (PdfException e) {
            if (e.getMessage() == PdfException.flushedPageCannotBeAddedOrInserted)
                error = true;
        }

        Assert.assertTrue(error);
    }

    public void verifyPagesOrder(String filename) throws IOException {
        com.itextpdf.text.pdf.PdfReader reader = new com.itextpdf.text.pdf.PdfReader(filename);

        for (int i = 1; i <= reader.getNumberOfPages(); i++) {
            com.itextpdf.text.pdf.PdfDictionary page = reader.getPageN(i);
            Assert.assertNotNull(page);

            com.itextpdf.text.pdf.PdfNumber number = page.getAsNumber(PageNum5);
            Assert.assertNotNull(number.intValue() == i);
        }
        reader.close();
    }
}
