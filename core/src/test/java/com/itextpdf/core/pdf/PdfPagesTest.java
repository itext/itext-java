package com.itextpdf.core.pdf;

import com.itextpdf.basics.PdfException;
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
        int pageCount = 111;

        FileOutputStream fos = new FileOutputStream(destinationFolder + filename);
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument pdfDoc = new PdfDocument(writer);

        for (int i = 0; i < pageCount; i++) {
            PdfPage page = pdfDoc.addNewPage();
            page.getPdfObject().put(PageNum, new PdfNumber(i + 1));
            page.flush();
        }
        pdfDoc.close();
        verifyPagesOrder(destinationFolder + filename, pageCount);
    }

    @Test
    public void reversePagesTest() throws IOException, PdfException {
        String filename = "reversePagesTest.pdf";
        int pageCount = 111;

        FileOutputStream fos = new FileOutputStream(destinationFolder + filename);
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument pdfDoc = new PdfDocument(writer);

        for (int i = pageCount; i > 0; i--) {
            PdfPage page = new PdfPage(pdfDoc, pdfDoc.getDefaultPageSize());
            pdfDoc.addPage(1, page);
            page.getPdfObject().put(PageNum, new PdfNumber(i));
            page.flush();
        }
        pdfDoc.close();

        verifyPagesOrder(destinationFolder + filename, pageCount);
    }

    @Test
    public void randomObjectPagesTest() throws IOException, PdfException {
        String filename = "randomObjectPagesTest.pdf";
        int pageCount = 10000;
        int indexes[] = new int[pageCount];
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
        PdfDocument document = new PdfDocument(writer);
        PdfPage pages[] = new PdfPage[pageCount];

        for (int i = 0; i < indexes.length; i++) {
            PdfPage page = document.addNewPage();
            page.getPdfObject().put(PageNum, new PdfNumber(indexes[i]));
            //page.flush();
            pages[indexes[i] - 1] = page;
        }

        int xrefSize = document.getXref().size();
        PdfPage testPage = document.removePage(1000);
        Assert.assertTrue(testPage.getPdfObject().getIndirectReference() == null);
        document.addPage(1000, testPage);
        Assert.assertTrue(testPage.getPdfObject().getIndirectReference().getObjNr() < xrefSize);

        for (int i = 0; i < pages.length; i++) {
            Assert.assertEquals("Remove page", true, document.removePage(pages[i]));
            document.addPage(i + 1, pages[i]);
        }
        document.close();

        verifyPagesOrder(destinationFolder + filename, pageCount);
    }

    @Test
    public void randomNumberPagesTest() throws IOException, PdfException {
        String filename = "randomNumberPagesTest.pdf";
        int pageCount = 3000;
        int indexes[] = new int[pageCount];
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

        for (int i = 0; i < indexes.length; i++) {
            PdfPage page = pdfDoc.addNewPage();
            page.getPdfObject().put(PageNum, new PdfNumber(indexes[i]));
        }

        for (int i = 1; i < pageCount; i++) {
            for (int j = i + 1; j <= pageCount; j++) {
                int j_page = pdfDoc.getPage(j).getPdfObject().getAsNumber(PageNum).getIntValue();
                int i_page = pdfDoc.getPage(i).getPdfObject().getAsNumber(PageNum).getIntValue();
                if (j_page < i_page){
                    PdfPage page = pdfDoc.removePage(j);
                    pdfDoc.addPage(i+1, page);
                    page = pdfDoc.removePage(i);
                    pdfDoc.addPage(j, page);
                }
            }
            Assert.assertTrue(pdfDoc.getCatalog().pageTree.verifyIntegrity() == -1);
        }
        pdfDoc.close();

        verifyPagesOrder(destinationFolder + filename, pageCount);
    }

    @Test
    public void insertFlushedPageTest() throws IOException, PdfException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        PdfDocument pdfDoc = new PdfDocument(writer);
        PdfPage page = pdfDoc.addNewPage();
        boolean error = false;
        try {
            page.flush();
            pdfDoc.removePage(page);
            pdfDoc.addPage(1, page);
            pdfDoc.close();
        } catch (PdfException e) {
            if (PdfException.FlushedPageCannotBeAddedOrInserted.equals(e.getMessage()))
                error = true;
        }

        Assert.assertTrue(error);
    }

    @Test
    public void addFlushedPageTest() throws IOException, PdfException {
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
            if (PdfException.FlushedPageCannotBeAddedOrInserted.equals(e.getMessage()))
                error = true;
        }

        Assert.assertTrue(error);
    }

    @Test
    public void removeFlushedPage() throws IOException, PdfException {
        String filename = "removeFlushedPage.pdf";
        int pageCount = 10;

        FileOutputStream fos = new FileOutputStream(destinationFolder + filename);
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument pdfDoc = new PdfDocument(writer);

        PdfPage removedPage = pdfDoc.addNewPage();
        int removedPageObjectNumber = removedPage.getPdfObject().getIndirectReference().getObjNr();
        removedPage.flush();
        pdfDoc.removePage(removedPage);


        for (int i = 0; i < pageCount; i++) {
            PdfPage page = pdfDoc.addNewPage();
            page.getPdfObject().put(PageNum, new PdfNumber(i + 1));
            page.flush();
        }

        Assert.assertEquals("Remove last page", true, pdfDoc.removePage(pdfDoc.getPage(pageCount)));
        Assert.assertEquals("Free reference", true, pdfDoc.getXref().get(removedPageObjectNumber).checkState(PdfIndirectReference.Free));

        pdfDoc.close();
        verifyPagesOrder(destinationFolder + filename, pageCount - 1);
    }

    public void verifyPagesOrder(String filename, int numOfPages) throws IOException {
        com.itextpdf.text.pdf.PdfReader reader = new com.itextpdf.text.pdf.PdfReader(filename);
        Assert.assertEquals("Rebuilt", false, reader.isRebuilt());

        for (int i = 1; i <= reader.getNumberOfPages(); i++) {
            com.itextpdf.text.pdf.PdfDictionary page = reader.getPageN(i);
            Assert.assertNotNull(page);
            com.itextpdf.text.pdf.PdfNumber number = page.getAsNumber(PageNum5);
            Assert.assertEquals("Page number", i, number.intValue());
        }

        Assert.assertEquals("Number of pages", numOfPages, reader.getNumberOfPages());
        reader.close();
    }
}
