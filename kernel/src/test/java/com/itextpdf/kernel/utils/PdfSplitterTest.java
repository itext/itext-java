/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.kernel.utils;

import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Tag("IntegrationTest")
public class PdfSplitterTest extends ExtendedITextTest{

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/utils/PdfSplitterTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/utils/PdfSplitterTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @AfterAll
    public static void afterClass() {
        CompareTool.cleanup(destinationFolder);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.SOURCE_DOCUMENT_HAS_ACROFORM_DICTIONARY, count = 3)
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
                    return CompareTool.createTestPdfWriter(destinationFolder + "splitDocument1_" + String.valueOf(partNumber++) + ".pdf");
                } catch (IOException e) {
                    throw new RuntimeException();
                }
            }
        }.splitByPageNumbers(pageNumbers);

        for (PdfDocument doc : splitDocuments)
            doc.close();

        for (int i = 1; i <= 3; i++) {
            Assertions.assertNull(new CompareTool().compareByContent(destinationFolder + "splitDocument1_" + String.valueOf(i) + ".pdf",
                    sourceFolder + "cmp/" + "cmp_splitDocument1_" + String.valueOf(i) + ".pdf", destinationFolder, "diff_"));
        }
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.SOURCE_DOCUMENT_HAS_ACROFORM_DICTIONARY,count = 3)
    })
    public void splitDocumentTest02() throws IOException, InterruptedException {
        String inputFileName =  sourceFolder + "iphone_user_guide.pdf";
        PdfDocument inputPdfDoc = new PdfDocument(new PdfReader(inputFileName));

        PdfSplitter splitter = new PdfSplitter(inputPdfDoc) {
            int partNumber = 1;

            @Override
            protected PdfWriter getNextPdfWriter(PageRange documentPageRange) {
                try {
                    PdfWriter writer = CompareTool.createTestPdfWriter(destinationFolder + "splitDocument2_" + String.valueOf(partNumber++) + ".pdf");
                    return writer;
                } catch (IOException e) {
                    throw new RuntimeException();
                }
            }
        };
        splitter.splitByPageCount(60, new PdfSplitter.IDocumentReadyListener() {
            @Override
            public void documentReady(PdfDocument pdfDocument, PageRange pageRange) {
                if (new PageRange("61-120").equals(pageRange)) {
                    pdfDocument.getDocumentInfo().setAuthor("Modified Author");
                }

                pdfDocument.close();
            }
        });

        for (int i = 1; i <= 3; i++) {
            Assertions.assertNull(new CompareTool().compareByContent(destinationFolder + "splitDocument2_" + String.valueOf(i) + ".pdf",
                    sourceFolder + "cmp/" + "cmp_splitDocument2_" + String.valueOf(i) + ".pdf", destinationFolder, "diff_"));
        }
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.SOURCE_DOCUMENT_HAS_ACROFORM_DICTIONARY, count = 2)
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
                    return CompareTool.createTestPdfWriter(destinationFolder + "splitDocument3_" + String.valueOf(partNumber++) + ".pdf");
                } catch (IOException e) {
                    throw new RuntimeException();
                }
            }
        }.extractPageRanges(Arrays.asList(pageRange1, pageRange2));

        for (PdfDocument pdfDocument : splitDocuments) {
            pdfDocument.close();
        }

        for (int i = 1; i <= 2; i++) {
            Assertions.assertNull(new CompareTool().compareByContent(destinationFolder + "splitDocument3_" + i + ".pdf",
                    sourceFolder + "cmp/" + "cmp_splitDocument3_" + String.valueOf(i) + ".pdf", destinationFolder, "diff_"));
        }
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.SOURCE_DOCUMENT_HAS_ACROFORM_DICTIONARY, count = 2)
    })
    public void splitDocumentTest04() throws IOException, InterruptedException {
        String inputFileName =  sourceFolder + "iphone_user_guide.pdf";
        PdfDocument inputPdfDoc = new PdfDocument(new PdfReader(inputFileName));

        PageRange pageRange1 = new PageRange("even & 80-").addPageSequence(4, 15).addSinglePage(18).addPageSequence(1, 2);
        PageRange pageRange2 = new PageRange("99,98").addPageSequence(70, 99);

        List<PdfDocument> splitDocuments = new PdfSplitter(inputPdfDoc) {
            int partNumber = 1;

            @Override
            protected PdfWriter getNextPdfWriter(PageRange documentPageRange) {
                try {
                    return CompareTool.createTestPdfWriter(destinationFolder + "splitDocument4_" + String.valueOf(partNumber++) + ".pdf");
                } catch (IOException e) {
                    throw new RuntimeException();
                }
            }
        }.extractPageRanges(Arrays.asList(pageRange1, pageRange2));

        for (PdfDocument pdfDocument : splitDocuments) {
            pdfDocument.close();
        }

        for (int i = 1; i <= 2; i++) {
            Assertions.assertNull(new CompareTool().compareByContent(destinationFolder + "splitDocument4_" + i + ".pdf",
                    sourceFolder + "cmp/" + "cmp_splitDocument4_" + String.valueOf(i) + ".pdf", destinationFolder, "diff_"));
        }
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.SOURCE_DOCUMENT_HAS_ACROFORM_DICTIONARY ,count = 2)
    })
    public void splitDocumentByOutlineTest() throws IOException {

        String inputFileName =  sourceFolder + "iphone_user_guide.pdf";
        PdfDocument inputPdfDoc = new PdfDocument(new PdfReader(inputFileName));
        PdfSplitter splitter = new PdfSplitter(inputPdfDoc);
        List<String> listTitles = new ArrayList<>();
        listTitles.add("Syncing iPod Content from Your iTunes Library");
        listTitles.add("Restoring or Transferring Your iPhone Settings");
        List<PdfDocument> list = splitter.splitByOutlines(listTitles);
        Assertions.assertEquals(1,list.get(0).getNumberOfPages());
        Assertions.assertEquals(2,list.get(1).getNumberOfPages());
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
                    return CompareTool.createTestPdfWriter(destinationFolder + "splitBySize_part" + String.valueOf(partNumber++) + ".pdf");
                } catch (IOException e) {
                    throw new RuntimeException();
                }
            }
        };

        List<PdfDocument> documents = splitter.splitBySize(100000);

        for (PdfDocument doc : documents) {
            doc.close();
        }

        for (int i = 1; i <= 4; ++i) {
            Assertions.assertNull(new CompareTool().compareByContent(destinationFolder + "splitBySize_part" + i + ".pdf",
                                                                 sourceFolder + "cmp/" + "cmp_splitBySize_part" + i + ".pdf", destinationFolder, "diff_"));
        }
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.SOURCE_DOCUMENT_HAS_ACROFORM_DICTIONARY , count = 10)
    })
    public void splitByPageCountTest() throws IOException {
        String inputFileName =  sourceFolder + "iphone_user_guide.pdf";
        try (PdfDocument inputPdfDoc = new PdfDocument(new PdfReader(inputFileName))) {
            PdfSplitter splitter = new PdfSplitter(inputPdfDoc);

            int pagesCount = inputPdfDoc.getNumberOfPages();
            int pagesCountInSplitDoc = 13;

            List<PdfDocument> splitDocuments = splitter.splitByPageCount(pagesCountInSplitDoc);

            for (PdfDocument doc : splitDocuments) {
                doc.close();
            }

            Assertions.assertEquals(pagesCount / pagesCountInSplitDoc, splitDocuments.size());
        }
    }
}
