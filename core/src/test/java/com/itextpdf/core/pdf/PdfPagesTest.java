package com.itextpdf.core.pdf;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.font.PdfFont;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PdfPagesTest {
    static final public String destinationFolder = "./target/test/com/itextpdf/core/pdf/PdfPagesTest/";
    static final public String sourceFolder = "./src/test/resources/com/itextpdf/core/pdf/PdfPagesTest/";
    static final PdfName PageNum = new PdfName("PageNum");
    static final com.itextpdf.text.pdf.PdfName PageNum5 = new com.itextpdf.text.pdf.PdfName("PageNum");

    @Before
    public void setup() {
        new File(destinationFolder).mkdirs();
    }

    @Test
    public void simplePagesTest() throws IOException {
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
    public void reversePagesTest() throws IOException {
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
    public void randomObjectPagesTest() throws IOException {
        String filename = "randomObjectPagesTest.pdf";
        int pageCount = 10000;
        int indexes[] = new int[pageCount];
        for (int i = 0; i < indexes.length; i++)
            indexes[i] = i + 1;

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
        Assert.assertTrue(testPage.getPdfObject().getIndirectReference().getObjNumber() < xrefSize);

        for (int i = 0; i < pages.length; i++) {
            Assert.assertEquals("Remove page", true, document.removePage(pages[i]));
            document.addPage(i + 1, pages[i]);
        }
        document.close();

        verifyPagesOrder(destinationFolder + filename, pageCount);
    }

    @Test
    public void randomNumberPagesTest() throws IOException {
        String filename = "randomNumberPagesTest.pdf";
        int pageCount = 3000;
        int indexes[] = new int[pageCount];
        for (int i = 0; i < indexes.length; i++)
            indexes[i] = i + 1;

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
                if (j_page < i_page) {
                    PdfPage page = pdfDoc.removePage(j);
                    pdfDoc.addPage(i + 1, page);
                    page = pdfDoc.removePage(i);
                    pdfDoc.addPage(j, page);
                }
            }
            Assert.assertTrue(verifyIntegrity(pdfDoc.getCatalog().pageTree) == -1);
        }
        pdfDoc.close();

        verifyPagesOrder(destinationFolder + filename, pageCount);
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
            pdfDoc.addPage(1, page);
            pdfDoc.close();
        } catch (PdfException e) {
            if (PdfException.FlushedPageCannotBeAddedOrInserted.equals(e.getMessage()))
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
            if (PdfException.FlushedPageCannotBeAddedOrInserted.equals(e.getMessage()))
                error = true;
        }

        Assert.assertTrue(error);
    }

    @Test
    public void removeFlushedPage() throws IOException {
        String filename = "removeFlushedPage.pdf";
        int pageCount = 10;

        FileOutputStream fos = new FileOutputStream(destinationFolder + filename);
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument pdfDoc = new PdfDocument(writer);

        PdfPage removedPage = pdfDoc.addNewPage();
        int removedPageObjectNumber = removedPage.getPdfObject().getIndirectReference().getObjNumber();
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

    void verifyPagesOrder(String filename, int numOfPages) throws IOException {
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

    int verifyIntegrity(PdfPagesTree pagesTree) {
        ArrayList<PdfPages> parents = pagesTree.getParents();
        int from = 0;
        for (int i = 0; i < parents.size(); i++) {
            if (parents.get(i).getFrom() != from)
                return i;
            from = parents.get(i).getFrom() + parents.get(i).getCount();
        }
        return -1;
    }

    @Test@Ignore
    public void testInheritedResources() throws IOException {
        String inputFileName1 = sourceFolder + "veraPDF-A003-a-pass.pdf";
        PdfReader reader1 = new PdfReader(inputFileName1);
        PdfDocument inputPdfDoc1 = new PdfDocument(reader1);
        PdfPage page = inputPdfDoc1.getPage(1);
        List<PdfFont> list = page.getResources().getFonts(true);
        Assert.assertEquals(1, list.size());
        Assert.assertEquals("ASJKFO+Arial-BoldMT", list.get(0).getFontProgram().getFontNames().getFontName());
    }

    @Test(expected = PdfException.class)
    public void testCircularReferencesInResources() throws IOException {
        String inputFileName1 = sourceFolder + "circularReferencesInResources.pdf";
        PdfReader reader1 = new PdfReader(inputFileName1);
        PdfDocument inputPdfDoc1 = new PdfDocument(reader1);
        PdfPage page = inputPdfDoc1.getPage(1);
        List<PdfFont> list = page.getResources().getFonts(true);
    }

    @Test@Ignore
    public void testInheritedResourcesUpdate() throws IOException {


        String inputFileName1 = sourceFolder + "veraPDF-A003-a-pass.pdf";
        PdfReader reader1 = new PdfReader(inputFileName1);

        FileOutputStream fos = new FileOutputStream(destinationFolder + "veraPDF-A003-a-pass_new.pdf");
        PdfWriter writer = new PdfWriter(fos);
        writer.setCompressionLevel(PdfOutputStream.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(reader1, writer);
        pdfDoc.getPage(1).getResources().getFonts(true);
        PdfFont f = PdfFont.createFont(pdfDoc, (PdfDictionary) pdfDoc.getPdfObject(6));
        pdfDoc.getPage(1).getResources().addFont(f);
        int fontCount = pdfDoc.getPage(1).getResources().getFonts(false).size();
        pdfDoc.getPage(1).flush();
        pdfDoc.close();

        Assert.assertEquals(2, fontCount);
    }


}
