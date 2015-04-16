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
    public void splitDocumentTest01() throws IOException, PdfException, InterruptedException, DocumentException {
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
    public void splitDocumentTest02() throws IOException, PdfException, InterruptedException, DocumentException {
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
    public void splitDocumentTest03() throws IOException, PdfException, InterruptedException, DocumentException {
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
            Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "splitDocument3_" + String.valueOf(i) + ".pdf",
                    sourceFolder + "cmp/" + "splitDocument3_" + String.valueOf(i) + ".pdf", destinationFolder, "diff_"));
        }
    }

}
