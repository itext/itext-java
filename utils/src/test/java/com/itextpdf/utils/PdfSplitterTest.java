package com.itextpdf.utils;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfReader;
import com.itextpdf.core.pdf.PdfWriter;
import com.itextpdf.core.testutils.CompareTool;
import com.itextpdf.text.DocumentException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PdfSplitterTest {

    static final public String sourceFolder = "./src/test/resources/com/itextpdf/utils/PdfSplitterTest/";
    static final public String destinationFolder = "./target/test/com/itextpdf/utils/PdfSplitterTest/";

    @BeforeClass
    static public void beforeClass() {
        new File(destinationFolder).mkdirs();
    }

    @Test
    public void splitDocumentTest01() throws IOException, InterruptedException, DocumentException {
        String inputFileName =  sourceFolder + "iphone_user_guide.pdf";
        PdfDocument inputPdfDoc = new PdfDocument(new PdfReader(inputFileName));

        List<Integer> pageNumbers = Arrays.asList(30, 100);

        List<PdfDocument> splitDocuments = new PdfSplitter(inputPdfDoc) {
            int partNumber = 1;

            @Override
            protected PdfWriter getNextPdfWriter(PageRange documentPageRange) {
                try {
                    return new PdfWriter(new FileOutputStream(destinationFolder + "splitDocument1_" + String.valueOf(partNumber++) + ".pdf"));
                } catch (FileNotFoundException e) {
                    throw new RuntimeException();
                }
            }
        }.splitByPageNumbers(pageNumbers);

        for (PdfDocument doc : splitDocuments)
            doc.close();

        for (int i = 1; i <= 3; i++) {
            Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "splitDocument1_" + String.valueOf(i) + ".pdf",
                    sourceFolder + "cmp/" + "splitDocument1_" + String.valueOf(i) + ".pdf", destinationFolder, "diff_"));
        }
    }

    @Test
    public void splitDocumentTest02() throws IOException, InterruptedException, DocumentException {
        String inputFileName =  sourceFolder + "iphone_user_guide.pdf";
        PdfDocument inputPdfDoc = new PdfDocument(new PdfReader(inputFileName));

        new PdfSplitter(inputPdfDoc) {
            int partNumber = 1;

            @Override
            protected PdfWriter getNextPdfWriter(PageRange documentPageRange) {
                try {
                    return new PdfWriter(new FileOutputStream(destinationFolder + "splitDocument2_" + String.valueOf(partNumber++) + ".pdf"));
                } catch (FileNotFoundException e) {
                    throw new RuntimeException();
                }
            }
        }.splitByPageCount(60, new PdfSplitter.IDocumentReadyListener() {
            @Override
            public void documentReady(PdfDocument pdfDocument, PdfSplitter.PageRange pageRange) {
                try {
                    if (new PdfSplitter.PageRange("61-120").equals(pageRange)) {
                        pdfDocument.getInfo().setAuthor("Modified Author");
                    }

                    pdfDocument.close();
                } catch (PdfException e) {
                    e.printStackTrace();
                }
            }
        });

        for (int i = 1; i <= 3; i++) {
            Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "splitDocument2_" + String.valueOf(i) + ".pdf",
                    sourceFolder + "cmp/" + "splitDocument2_" + String.valueOf(i) + ".pdf", destinationFolder, "diff_"));
        }
    }

    @Test
    public void splitDocumentTest03() throws IOException, InterruptedException, DocumentException {
        String inputFileName =  sourceFolder + "iphone_user_guide.pdf";
        PdfDocument inputPdfDoc = new PdfDocument(new PdfReader(inputFileName));

        PdfSplitter.PageRange pageRange1 = new PdfSplitter.PageRange().addPageSequence(4, 15).addSinglePage(18).addPageSequence(1, 2);
        PdfSplitter.PageRange pageRange2 = new PdfSplitter.PageRange().addSinglePage(99).addSinglePage(98).addPageSequence(70, 99);

        List<PdfDocument> splitDocuments = new PdfSplitter(inputPdfDoc) {
            int partNumber = 1;

            @Override
            protected PdfWriter getNextPdfWriter(PageRange documentPageRange) {
                try {
                    return new PdfWriter(new FileOutputStream(destinationFolder + "splitDocument3_" + String.valueOf(partNumber++) + ".pdf"));
                } catch (FileNotFoundException e) {
                    throw new RuntimeException();
                }
            }
        }.extractPageRanges(Arrays.asList(pageRange1, pageRange2));

        for (PdfDocument pdfDocument : splitDocuments) {
            pdfDocument.close();
        }

        for (int i = 1; i <= 2; i++) {
            Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "splitDocument3_" + i + ".pdf",
                    sourceFolder + "cmp/" + "splitDocument3_" + String.valueOf(i) + ".pdf", destinationFolder, "diff_"));
        }
    }

    @Test
    public void splitDocumentByOutlineTest() throws IOException, InterruptedException, DocumentException {

        String inputFileName =  sourceFolder + "iphone_user_guide.pdf";
        PdfDocument inputPdfDoc = new PdfDocument(new PdfReader(inputFileName));
        PdfSplitter splitter = new PdfSplitter(inputPdfDoc);
        List listTitles = new ArrayList();
        listTitles.add("Syncing iPod Content from Your iTunes Library");
        listTitles.add("Restoring or Transferring Your iPhone Settings");
        List<PdfDocument> list = splitter.splitByOutlines(listTitles);
        Assert.assertEquals(1,list.get(0).getNumOfPages());
        Assert.assertEquals(2,list.get(1).getNumOfPages());
        list.get(0).close();
        list.get(1).close();
    }

    @Test
    public void splitDocumentBySize() throws IOException, InterruptedException {
        String inputFileName = sourceFolder + "splitBySize.pdf";
        PdfDocument inputPdfDoc = new PdfDocument(new PdfReader(inputFileName));
        PdfSplitter splitter = new PdfSplitter(inputPdfDoc) {

            int partNumber = 1;

            @Override
            protected PdfWriter getNextPdfWriter(PageRange documentPageRange) {
                try {
                    return new PdfWriter(new FileOutputStream(destinationFolder + "splitBySize_part" + String.valueOf(partNumber++) + ".pdf"));
                } catch (FileNotFoundException e) {
                    throw new RuntimeException();
                }
            }
        };

        List<PdfDocument> documents = splitter.splitBySize(100000);

        for (PdfDocument doc : documents) {
            doc.close();
        }

        for (int i = 1; i <= 4; ++i) {
            Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "splitBySize_part" + i + ".pdf",
                                                                 sourceFolder + "cmp/" + "splitBySize_part" + i + ".pdf", destinationFolder, "diff_"));
        }
    }
}
