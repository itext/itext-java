package com.itextpdf.kernel.utils;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class PdfSplitterTest extends ExtendedITextTest{

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/utils/PdfSplitterTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/utils/PdfSplitterTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.SOURCE_DOCUMENT_HAS_ACROFORM_DICTIONARY, count = 3)
    })
    public void splitDocumentTest01() throws IOException, InterruptedException {
        String inputFileName =  sourceFolder + "iphone_user_guide.pdf";
        PdfDocument inputPdfDoc = new PdfDocument(new PdfReader(inputFileName));

        List<Integer> pageNumbers = Arrays.asList(30, 100);

        List<PdfDocument> splitDocuments = new PdfSplitter(inputPdfDoc) {
            int partNumber = 1;

            @Override
            protected PdfWriter getNextPdfWriter(PageRange documentPageRange) {
                try {
                    return new PdfWriter(destinationFolder + "splitDocument1_" + String.valueOf(partNumber++) + ".pdf");
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
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.SOURCE_DOCUMENT_HAS_ACROFORM_DICTIONARY,count = 3)
    })
    public void splitDocumentTest02() throws IOException, InterruptedException {
        String inputFileName =  sourceFolder + "iphone_user_guide.pdf";
        PdfDocument inputPdfDoc = new PdfDocument(new PdfReader(inputFileName));

        new PdfSplitter(inputPdfDoc) {
            int partNumber = 1;

            @Override
            protected PdfWriter getNextPdfWriter(PageRange documentPageRange) {
                try {
                    return new PdfWriter(destinationFolder + "splitDocument2_" + String.valueOf(partNumber++) + ".pdf");
                } catch (FileNotFoundException e) {
                    throw new RuntimeException();
                }
            }
        }.splitByPageCount(60, new PdfSplitter.IDocumentReadyListener() {
            @Override
            public void documentReady(PdfDocument pdfDocument, PageRange pageRange) {
                if (new PageRange("61-120").equals(pageRange)) {
                    pdfDocument.getDocumentInfo().setAuthor("Modified Author");
                }

                pdfDocument.close();
            }
        });

        for (int i = 1; i <= 3; i++) {
            Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "splitDocument2_" + String.valueOf(i) + ".pdf",
                    sourceFolder + "cmp/" + "splitDocument2_" + String.valueOf(i) + ".pdf", destinationFolder, "diff_"));
        }
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.SOURCE_DOCUMENT_HAS_ACROFORM_DICTIONARY, count = 2)
    })
    public void splitDocumentTest03() throws IOException, InterruptedException {
        String inputFileName =  sourceFolder + "iphone_user_guide.pdf";
        PdfDocument inputPdfDoc = new PdfDocument(new PdfReader(inputFileName));

        PageRange pageRange1 = new PageRange().addPageSequence(4, 15).addSinglePage(18).addPageSequence(1, 2);
        PageRange pageRange2 = new PageRange().addSinglePage(99).addSinglePage(98).addPageSequence(70, 99);

        List<PdfDocument> splitDocuments = new PdfSplitter(inputPdfDoc) {
            int partNumber = 1;

            @Override
            protected PdfWriter getNextPdfWriter(PageRange documentPageRange) {
                try {
                    return new PdfWriter(destinationFolder + "splitDocument3_" + String.valueOf(partNumber++) + ".pdf");
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
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.SOURCE_DOCUMENT_HAS_ACROFORM_DICTIONARY ,count = 2)
    })
    public void splitDocumentByOutlineTest() throws IOException, InterruptedException {

        String inputFileName =  sourceFolder + "iphone_user_guide.pdf";
        PdfDocument inputPdfDoc = new PdfDocument(new PdfReader(inputFileName));
        PdfSplitter splitter = new PdfSplitter(inputPdfDoc);
        List<String> listTitles = new ArrayList<>();
        listTitles.add("Syncing iPod Content from Your iTunes Library");
        listTitles.add("Restoring or Transferring Your iPhone Settings");
        List<PdfDocument> list = splitter.splitByOutlines(listTitles);
        Assert.assertEquals(1,list.get(0).getNumberOfPages());
        Assert.assertEquals(2,list.get(1).getNumberOfPages());
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
                    return new PdfWriter(destinationFolder + "splitBySize_part" + String.valueOf(partNumber++) + ".pdf");
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
