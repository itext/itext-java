/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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
import com.itextpdf.kernel.logs.KernelLogMessageConstant;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import org.xml.sax.SAXException;

@Tag("IntegrationTest")
public class PdfMergerTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/utils/PdfMergerTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/utils/PdfMergerTest/";

    @BeforeAll
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @AfterAll
    public static void afterClass() {
        CompareTool.cleanup(destinationFolder);
    }

    @Test
    public void mergeDocumentTest01() throws IOException, InterruptedException {
        String filename = sourceFolder + "courierTest.pdf";
        String filename1 = sourceFolder + "helveticaTest.pdf";
        String filename2 = sourceFolder + "timesRomanTest.pdf";
        String resultFile = destinationFolder + "mergedResult01.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfReader reader1 = new PdfReader(filename1);
        PdfReader reader2 = new PdfReader(filename2);

        PdfWriter writer1 = CompareTool.createTestPdfWriter(resultFile);
        PdfDocument pdfDoc = new PdfDocument(reader);
        PdfDocument pdfDoc1 = new PdfDocument(reader1);
        PdfDocument pdfDoc2 = new PdfDocument(reader2);
        PdfDocument pdfDoc3 = new PdfDocument(writer1);

        PdfMerger merger = new PdfMerger(pdfDoc3).setCloseSourceDocuments(true);
        merger.merge(pdfDoc, 1, 1);
        merger.merge(pdfDoc1, 1, 1);

        merger.merge(pdfDoc2, 1, 1);

        pdfDoc3.close();

        Assertions.assertNull(new CompareTool().compareByContent(resultFile, sourceFolder + "cmp_mergedResult01.pdf", destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.SOURCE_DOCUMENT_HAS_ACROFORM_DICTIONARY)
    })
    public void mergeDocumentOutlinesWithNullDestinationTest01() throws IOException, InterruptedException {
        String resultFile = destinationFolder + "mergeDocumentOutlinesWithNullDestinationTest01.pdf";
        String filename = sourceFolder + "null_dest_outline.pdf";
        PdfDocument sourceDocument = new PdfDocument(new PdfReader(filename));

        PdfMerger resultDocument = new PdfMerger(new PdfDocument(CompareTool.createTestPdfWriter(resultFile)));
        resultDocument.merge(sourceDocument, 1, 1);
        resultDocument.close();
        sourceDocument.close();

        Assertions.assertNull(new CompareTool().compareByContent(resultFile, sourceFolder + "cmp_mergeDocumentOutlinesWithNullDestinationTest01.pdf", destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.SOURCE_DOCUMENT_HAS_ACROFORM_DICTIONARY)
    })
    public void mergeDocumentOutlinesWithExplicitRemoteDestinationTest() throws IOException, InterruptedException {
        String resultFile = destinationFolder + "mergeDocumentWithRemoteGoToTest.pdf";
        String filename1 = sourceFolder + "docWithRemoteGoTo.pdf";
        String filename2 = sourceFolder + "doc1.pdf";
        PdfDocument sourceDocument1 = new PdfDocument(new PdfReader(filename1));
        PdfDocument sourceDocument2 = new PdfDocument(new PdfReader(filename2));

        PdfMerger resultDocument = new PdfMerger(new PdfDocument(CompareTool.createTestPdfWriter(resultFile)));
        resultDocument.merge(sourceDocument1, 1, 1);
        resultDocument.merge(sourceDocument2, 1, 1);
        resultDocument.close();
        sourceDocument1.close();
        sourceDocument2.close();

        Assertions.assertNull(new CompareTool().compareByContent(resultFile, sourceFolder + "cmp_mergeDocumentWithRemoteGoToTest.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void mergeDocumentWithCycleRefInAcroFormTest() throws IOException, InterruptedException {
        String filename1 = sourceFolder + "doc1.pdf";
        String filename2 = sourceFolder + "pdfWithCycleRefInAnnotationParent.pdf";
        String resultFile = destinationFolder + "resultFileWithoutStackOverflow.pdf";
        try (PdfDocument pdfDocument1 = new PdfDocument(new PdfReader(filename2));
             PdfDocument pdfDocument2 = new PdfDocument(new PdfReader(filename1),
                        CompareTool.createTestPdfWriter(resultFile).setSmartMode(true));) {
            PdfMerger merger = new PdfMerger(pdfDocument2);
            merger.merge(pdfDocument1, 1, pdfDocument1.getNumberOfPages());
        }
        Assertions.assertNull(
                new CompareTool().compareByContent(resultFile, sourceFolder + "cmp_resultFileWithoutStackOverflow.pdf",
                        destinationFolder, "diff_"));
    }

    @Test
    public void mergeDocumentWithLinkAnnotationTest() throws IOException, InterruptedException {
        String filename = sourceFolder + "documentWithLinkAnnotation.pdf";
        String resultFile = destinationFolder + "mergedDocumentWithLinkAnnotation.pdf";

        PdfReader reader = new PdfReader(filename);

        PdfWriter writer1 = CompareTool.createTestPdfWriter(resultFile);
        PdfDocument pdfDoc = new PdfDocument(reader);
        PdfDocument result = new PdfDocument(writer1);
        PdfMerger merger = new PdfMerger(result).setCloseSourceDocuments(true);

        merger.merge(pdfDoc, 1, 1).close();

        Assertions.assertNull(new CompareTool().compareByContent(resultFile, sourceFolder + "cmp_mergedDocumentWithLinkAnnotation.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void mergeDocumentTest02() throws IOException, InterruptedException {
        String filename = sourceFolder + "doc1.pdf";
        String filename1 = sourceFolder + "doc2.pdf";
        String filename2 = sourceFolder + "doc3.pdf";
        String resultFile = destinationFolder + "mergedResult02.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfReader reader1 = new PdfReader(filename1);
        PdfReader reader2 = new PdfReader(filename2);

        PdfWriter writer1 = CompareTool.createTestPdfWriter(resultFile);
        PdfDocument pdfDoc = new PdfDocument(reader);
        PdfDocument pdfDoc1 = new PdfDocument(reader1);
        PdfDocument pdfDoc2 = new PdfDocument(reader2);
        PdfDocument pdfDoc3 = new PdfDocument(writer1);
        PdfMerger merger = new PdfMerger(pdfDoc3).setCloseSourceDocuments(true);

        merger.merge(pdfDoc, 1, 1).merge(pdfDoc1, 1, 1).merge(pdfDoc2, 1, 1).close();

        Assertions.assertNull(new CompareTool().compareByContent(resultFile, sourceFolder + "cmp_mergedResult02.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void mergeDocumentWithCycleTagReferenceTest() throws IOException, InterruptedException {
        String filename1 = sourceFolder + "doc1.pdf";
        String filename2 = sourceFolder + "pdfWithCycleRefInParentTag.pdf";
        String resultFile = destinationFolder + "pdfWithCycleRefInParentTag.pdf";
        try (PdfDocument pdfDocument1 = new PdfDocument(new PdfReader(filename2));
             PdfDocument pdfDocument2 = new PdfDocument(new PdfReader(filename1),
                     CompareTool.createTestPdfWriter(resultFile).setSmartMode(true));) {
            PdfMerger merger = new PdfMerger(pdfDocument2);
            merger.merge(pdfDocument1, 1, pdfDocument1.getNumberOfPages());
        }
        Assertions.assertNull(
                new CompareTool().compareByContent(resultFile, sourceFolder + "cmp_pdfWithCycleRefInParentTag.pdf",
                        destinationFolder, "diff_"));
    }

    @Test
    public void mergeDocumentWithCycleReferenceInFormFieldTest() throws IOException, InterruptedException {
        String filename1 = sourceFolder + "doc1.pdf";
        String filename2 = sourceFolder + "pdfWithCycleRefInFormField.pdf";
        String resultFile = destinationFolder + "pdfWithCycleRefInFormField.pdf";
        try (PdfDocument pdfDocument1 = new PdfDocument(new PdfReader(filename2));
             PdfDocument pdfDocument2 = new PdfDocument(new PdfReader(filename1),
                     CompareTool.createTestPdfWriter(resultFile).setSmartMode(true));) {
            PdfMerger merger = new PdfMerger(pdfDocument2);
            merger.merge(pdfDocument1, 1, pdfDocument1.getNumberOfPages());
        }
        Assertions.assertNull(
                new CompareTool().compareByContent(resultFile, sourceFolder + "cmp_pdfWithCycleRefInFormField.pdf",
                        destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.SOURCE_DOCUMENT_HAS_ACROFORM_DICTIONARY)
    })
    public void mergeDocumentTest03() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        String filename = sourceFolder + "pdf_open_parameters.pdf";
        String filename1 = sourceFolder + "iphone_user_guide.pdf";
        String resultFile = destinationFolder + "mergedResult03.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfReader reader1 = new PdfReader(filename1);

        PdfWriter writer1 = CompareTool.createTestPdfWriter(resultFile);
        PdfDocument pdfDoc = new PdfDocument(reader);
        PdfDocument pdfDoc1 = new PdfDocument(reader1);
        PdfDocument pdfDoc3 = new PdfDocument(writer1);
        pdfDoc3.setTagged();

        new PdfMerger(pdfDoc3)
                .merge(pdfDoc, 2, 2)
                .merge(pdfDoc1, 7, 8)
                .close();

        pdfDoc.close();
        pdfDoc1.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = "";
        String contentErrorMessage = compareTool.compareByContent(resultFile, sourceFolder + "cmp_mergedResult03.pdf", destinationFolder, "diff_");
        String tagStructErrorMessage = compareTool.compareTagStructures(resultFile, sourceFolder + "cmp_mergedResult03.pdf");

        errorMessage += tagStructErrorMessage == null ? "" : tagStructErrorMessage + "\n";
        errorMessage += contentErrorMessage == null ? "" : contentErrorMessage;
        if (!errorMessage.isEmpty()) {
            Assertions.fail(errorMessage);
        }
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.SOURCE_DOCUMENT_HAS_ACROFORM_DICTIONARY),
            @LogMessage(messageTemplate = IoLogMessageConstant.CREATED_ROOT_TAG_HAS_MAPPING, count = 2)
    })
    public void mergeDocumentTest04() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        String filename = sourceFolder + "pdf_open_parameters.pdf";
        String filename1 = sourceFolder + "iphone_user_guide.pdf";
        String resultFile = destinationFolder + "mergedResult04.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfReader reader1 = new PdfReader(filename1);

        PdfWriter writer1 = CompareTool.createTestPdfWriter(resultFile);
        PdfDocument pdfDoc = new PdfDocument(reader);
        PdfDocument pdfDoc1 = new PdfDocument(reader1);
        PdfDocument pdfDoc3 = new PdfDocument(writer1);
        pdfDoc3.setTagged();

        PdfMerger merger = new PdfMerger(pdfDoc3).setCloseSourceDocuments(true);
        List<Integer> pages = new ArrayList<>();
        pages.add(3);
        pages.add(2);
        pages.add(1);
        merger.merge(pdfDoc, pages);

        List<Integer> pages1 = new ArrayList<>();
        pages1.add(5);
        pages1.add(9);
        pages1.add(4);
        pages1.add(3);
        merger.merge(pdfDoc1, pages1);

        merger.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = "";
        String contentErrorMessage = compareTool.compareByContent(resultFile, sourceFolder + "cmp_mergedResult04.pdf", destinationFolder, "diff_");
        String tagStructErrorMessage = compareTool.compareTagStructures(resultFile, sourceFolder + "cmp_mergedResult04.pdf");

        errorMessage += tagStructErrorMessage == null ? "" : tagStructErrorMessage + "\n";
        errorMessage += contentErrorMessage == null ? "" : contentErrorMessage;
        if (!errorMessage.isEmpty()) {
            Assertions.fail(errorMessage);
        }
    }

    @Test
    public void mergeTableWithEmptyTdTest() throws IOException, ParserConfigurationException, SAXException, InterruptedException {
        mergeAndCompareTagStructures("tableWithEmptyTd.pdf", 1, 1);
    }

    @Test
    public void mergeSplitTableWithEmptyTdTest() throws IOException, ParserConfigurationException, SAXException, InterruptedException {
        mergeAndCompareTagStructures("splitTableWithEmptyTd.pdf", 2, 2);
    }

    @Test
    public void mergeEmptyRowWithTagsTest() throws IOException, ParserConfigurationException, SAXException, InterruptedException {
        mergeAndCompareTagStructures("emptyRowWithTags.pdf", 1, 1);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = IoLogMessageConstant.SOURCE_DOCUMENT_HAS_ACROFORM_DICTIONARY))
    public void trInsideTdTableTest() throws ParserConfigurationException, SAXException, IOException, InterruptedException {
        mergeAndCompareTagStructures("trInsideTdTable.pdf", 1, 1);
    }

    @Test
    public void tdInsideTdTableTest() throws ParserConfigurationException, SAXException, IOException, InterruptedException {
        mergeAndCompareTagStructures("tdInsideTdTable.pdf", 1, 1);
    }

    @Test
    public void emptyTrTableTest() throws ParserConfigurationException, SAXException, IOException, InterruptedException {
        mergeAndCompareTagStructures("emptyTrTable.pdf", 1, 1);
    }

    @Test
    public void splitEmptyTrTableFirstPageTest() throws ParserConfigurationException, SAXException, IOException, InterruptedException {
        mergeAndCompareTagStructures("splitTableWithEmptyTrFirstPage.pdf", 1, 1);
    }

    @Test
    public void splitEmptyTrTableSecondPageTest() throws ParserConfigurationException, SAXException, IOException, InterruptedException {
        mergeAndCompareTagStructures("splitTableWithEmptyTrSecondPage.pdf", 2, 2);
    }

    @Test
    public void splitEmptyTrTableFullTest() throws ParserConfigurationException, SAXException, IOException, InterruptedException {
        mergeAndCompareTagStructures("splitTableWithEmptyTrFull.pdf", 1, 2);
    }

    @Test
    public void emptyFirstTrTableTest() throws ParserConfigurationException, SAXException, IOException, InterruptedException {
        mergeAndCompareTagStructures("emptyFirstTrTable.pdf", 1, 1);
    }

    @Test
    public void emptyLastTrTableTest() throws ParserConfigurationException, SAXException, IOException, InterruptedException {
        mergeAndCompareTagStructures("emptyLastTrTable.pdf", 1, 1);
    }

    @Test
    public void emptyTwoAdjacentTrTableTest() throws ParserConfigurationException, SAXException, IOException, InterruptedException {
        mergeAndCompareTagStructures("emptyTwoAdjacentTrTable.pdf", 1, 1);
    }

    @Test
    public void emptyAllTrTableTest() throws ParserConfigurationException, SAXException, IOException, InterruptedException {
        mergeAndCompareTagStructures("emptyAllTrTable.pdf", 1, 1);
    }

    @Test
    public void emptySingleTrTableTest() throws ParserConfigurationException, SAXException, IOException, InterruptedException {
        mergeAndCompareTagStructures("emptySingleTrTable.pdf", 1, 1);
    }

    @Test
    public void splitAndMergeEmptyTrTableTest() throws ParserConfigurationException, SAXException, IOException, InterruptedException {
        String sourceFilename = sourceFolder + "splitTableWithEmptyTrFull.pdf";
        String firstPageFilename = destinationFolder + "firstPageDoc.pdf";
        String secondPageFilename = destinationFolder + "secondPageDoc.pdf";
        String resultFilename = destinationFolder + "splitAndMergeEmptyTrTable.pdf";
        String cmpFilename = sourceFolder + "cmp_splitAndMergeEmptyTrTable.pdf";

        PdfDocument sourceDoc = new PdfDocument(new PdfReader(sourceFilename));

        PdfDocument firstPageDoc = new PdfDocument(new PdfWriter(firstPageFilename));
        PdfMerger mergerFirstPage =  new PdfMerger(firstPageDoc);
        mergerFirstPage.merge(sourceDoc, 1, 1);
        mergerFirstPage.close();

        PdfDocument secondPageDoc = new PdfDocument(new PdfWriter(secondPageFilename));
        PdfMerger mergerSecondPage = new PdfMerger(secondPageDoc);
        mergerSecondPage.merge(sourceDoc, 2, 2);
        mergerSecondPage.close();

        List<File> sources = new ArrayList<File>();
        sources.add(new File(firstPageFilename));
        sources.add(new File(secondPageFilename));
        mergePdfs(sources, resultFilename, new PdfMergerProperties(), false);

        Assertions.assertNull(new CompareTool().compareTagStructures(resultFilename, cmpFilename));
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = IoLogMessageConstant.NAME_ALREADY_EXISTS_IN_THE_NAME_TREE, count = 2)})
    public void mergeOutlinesNamedDestinations() throws IOException, InterruptedException {
        String filename = sourceFolder + "outlinesNamedDestinations.pdf";
        String resultFile = destinationFolder + "mergeOutlinesNamedDestinations.pdf";

        PdfReader reader = new PdfReader(filename);

        PdfDocument sourceDoc = new PdfDocument(reader);
        PdfDocument output = new PdfDocument(CompareTool.createTestPdfWriter(resultFile));
        PdfMerger merger = new PdfMerger(output).setCloseSourceDocuments(false);
        merger.merge(sourceDoc, 2, 3);
        merger.merge(sourceDoc, 2, 3);
        sourceDoc.close();
        reader.close();
        merger.close();
        output.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(resultFile, sourceFolder + "cmp_mergeOutlinesNamedDestinations.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assertions.fail(errorMessage);
        }
    }

    @Test
    // TODO DEVSIX-1743. Update cmp file after fix
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.SOURCE_DOCUMENT_HAS_ACROFORM_DICTIONARY)
    })
    public void mergeWithAcroFormsTest() throws IOException, InterruptedException {
        String pdfAcro1 = sourceFolder + "pdfSource1.pdf";
        String pdfAcro2 = sourceFolder + "pdfSource2.pdf";
        String outFileName = destinationFolder + "mergeWithAcroFormsTest.pdf";
        String cmpFileName= sourceFolder + "cmp_mergeWithAcroFormsTest.pdf";

        List<File> sources = new ArrayList<File>();
        sources.add(new File(pdfAcro1));
        sources.add(new File(pdfAcro2));
        mergePdfs(sources, outFileName, false);

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.DOCUMENT_HAS_CONFLICTING_OCG_NAMES, count = 3)
    })
    public void mergePdfWithOCGTest() throws IOException, InterruptedException {
        String pdfWithOCG1 = sourceFolder  + "sourceOCG1.pdf";
        String pdfWithOCG2 = sourceFolder  + "sourceOCG2.pdf";
        String outPdf = destinationFolder + "mergePdfWithOCGTest.pdf";
        String cmpPdf = sourceFolder + "cmp_mergePdfWithOCGTest.pdf";

        List<File> sources = new ArrayList<File>();
        sources.add(new File(pdfWithOCG1));
        sources.add(new File(pdfWithOCG2));
        sources.add(new File(pdfWithOCG2));
        sources.add(new File(pdfWithOCG2));
        mergePdfs(sources, outPdf, false);

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.DOCUMENT_HAS_CONFLICTING_OCG_NAMES)
    })
    public void mergePdfWithComplexOCGTest() throws IOException, InterruptedException {
        String pdfWithOCG1 = sourceFolder  + "sourceOCG1.pdf";
        String pdfWithOCG2 = sourceFolder  + "pdfWithComplexOCG.pdf";
        String outPdf = destinationFolder + "mergePdfWithComplexOCGTest.pdf";
        String cmpPdf = sourceFolder + "cmp_mergePdfWithComplexOCGTest.pdf";

        List<File> sources = new ArrayList<File>();
        sources.add(new File(pdfWithOCG1));
        sources.add(new File(pdfWithOCG2));
        mergePdfs(sources, outPdf, false);

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.DOCUMENT_HAS_CONFLICTING_OCG_NAMES)
    })
    public void mergeTwoPagePdfWithComplexOCGTest() throws IOException, InterruptedException {
        String pdfWithOCG1 = sourceFolder  + "sourceOCG1.pdf";
        String pdfWithOCG2 = sourceFolder  + "twoPagePdfWithComplexOCGTest.pdf";
        String outPdf = destinationFolder + "mergeTwoPagePdfWithComplexOCGTest.pdf";
        String cmpPdf = sourceFolder + "cmp_mergeTwoPagePdfWithComplexOCGTest.pdf";

        PdfDocument mergedDoc = new PdfDocument(CompareTool.createTestPdfWriter(outPdf));
        PdfMerger merger = new PdfMerger(mergedDoc);
        List<File> sources = new ArrayList<File>();
        sources.add(new File(pdfWithOCG1));
        sources.add(new File(pdfWithOCG2));

        // The test verifies that are copying only those OCGs and properties that are used on the copied pages
        for(File source : sources){
            PdfDocument sourcePdf = new PdfDocument(new PdfReader(source));
            merger.merge(sourcePdf, 1, 1).setCloseSourceDocuments(true);
            sourcePdf.close();
        }
        merger.close();
        mergedDoc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder));
    }

    @Test
    public void mergePdfWithComplexOCGTwiceTest() throws IOException, InterruptedException {
        String pdfWithOCG = sourceFolder  + "pdfWithComplexOCG.pdf";
        String outPdf = destinationFolder + "mergePdfWithComplexOCGTwiceTest.pdf";
        String cmpPdf = sourceFolder + "cmp_mergePdfWithComplexOCGTwiceTest.pdf";

        PdfDocument mergedDoc = new PdfDocument(CompareTool.createTestPdfWriter(outPdf));
        PdfMerger merger = new PdfMerger(mergedDoc);
        PdfDocument sourcePdf = new PdfDocument(new PdfReader(new File(pdfWithOCG)));
        // The test verifies that identical layers from the same document are not copied
        merger.merge(sourcePdf, 1, sourcePdf.getNumberOfPages());
        merger.merge(sourcePdf, 1, sourcePdf.getNumberOfPages());
        sourcePdf.close();
        merger.close();
        mergedDoc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder));
    }

    @Test
    public void stackOverflowErrorCycleReferenceOcgMergeTest() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "cycleReferenceMerged.pdf";
        String cmpPdf = sourceFolder + "cmp_stackOverflowErrorCycleReferenceOcrMerge.pdf";
        
        PdfDocument pdfWithOCG = new PdfDocument(new PdfReader(sourceFolder + "sourceOCG1.pdf"),
                CompareTool.createTestPdfWriter(outPdf));
        PdfDocument pdfWithOCGToMerge = new PdfDocument
                (new PdfReader( sourceFolder + "stackOverflowErrorCycleReferenceOcgMerge.pdf")); // problem file
        PdfMerger merger = new PdfMerger(pdfWithOCG);
        merger.merge(pdfWithOCGToMerge, 1, pdfWithOCGToMerge.getNumberOfPages());
        pdfWithOCGToMerge.close();
        pdfWithOCG.close();
        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.SOURCE_DOCUMENT_HAS_ACROFORM_DICTIONARY)
    })
    public void mergeOutlinesWithWrongStructureTest() throws IOException, InterruptedException {
        PdfDocument inputDoc = new PdfDocument(new PdfReader(
                sourceFolder + "infiniteLoopInOutlineStructure.pdf"));

        PdfDocument outputDoc = new PdfDocument(CompareTool.createTestPdfWriter(
                destinationFolder + "infiniteLoopInOutlineStructure.pdf"));

        PdfMerger merger = new PdfMerger(outputDoc, new PdfMergerProperties().setMergeTags(false).setMergeOutlines(true));
        System.out.println("Doing merge");
        merger.merge(inputDoc, 1, 2);
        merger.close();
        System.out.println("Merge done");

        Assertions.assertNull(new CompareTool().compareByContent(
                destinationFolder + "infiniteLoopInOutlineStructure.pdf",
                sourceFolder + "cmp_infiniteLoopInOutlineStructure.pdf", destinationFolder));
    }

    private static void mergeAndCompareTagStructures(String testName, int fromPage, int toPage)
            throws IOException, ParserConfigurationException, SAXException, InterruptedException {
        String src = sourceFolder + testName;
        String dest = destinationFolder + testName;
        String cmp = sourceFolder + "cmp_" + testName;

        PdfReader reader = new PdfReader(src);

        PdfDocument sourceDoc = new PdfDocument(reader);
        PdfDocument output = new PdfDocument(CompareTool.createTestPdfWriter(dest));
        output.setTagged();
        PdfMerger merger = new PdfMerger(output).setCloseSourceDocuments(true);
        merger.merge(sourceDoc, fromPage, toPage);
        sourceDoc.close();
        reader.close();
        merger.close();
        output.close();

        Assertions.assertNull(new CompareTool().compareTagStructures(dest, cmp));
    }

    @Test
    public void mergeDocumentWithColorPropertyInOutlineTest() throws IOException, InterruptedException {
        String firstDocument = sourceFolder + "firstDocumentWithColorPropertyInOutline.pdf";
        String secondDocument = sourceFolder + "SecondDocumentWithColorPropertyInOutline.pdf";
        String cmpDocument = sourceFolder + "cmp_mergeOutlinesWithColorProperty.pdf";
        String mergedPdf = destinationFolder + "mergeOutlinesWithColorProperty.pdf";
        try (PdfDocument merged = new PdfDocument(CompareTool.createTestPdfWriter(mergedPdf));
                PdfDocument fileA = new PdfDocument(new PdfReader(firstDocument));
                PdfDocument fileB = new PdfDocument(new PdfReader(secondDocument))) {
            PdfMerger merger = new PdfMerger(merged, new PdfMergerProperties().setMergeTags(false).setMergeOutlines(true));

            merger.merge(fileA, 1, fileA.getNumberOfPages());
            merger.merge(fileB, 1, fileB.getNumberOfPages());

            merger.close();
        }

        Assertions.assertNull(new CompareTool().compareByContent(mergedPdf, cmpDocument, destinationFolder));
    }

    @Test
    public void mergeDocumentWithStylePropertyInOutlineTest() throws IOException, InterruptedException {
        String firstDocument = sourceFolder + "firstDocumentWithStylePropertyInOutline.pdf";
        String secondDocument = sourceFolder + "secondDocumentWithStylePropertyInOutline.pdf";
        String cmpPdf = sourceFolder + "cmp_mergeOutlineWithStyleProperty.pdf";
        String mergedPdf = destinationFolder + "mergeOutlineWithStyleProperty.pdf";

        try (PdfDocument documentA = new PdfDocument(new PdfReader(firstDocument));
                PdfDocument documentB = new PdfDocument(new PdfReader(secondDocument));
                PdfDocument merged = new PdfDocument(CompareTool.createTestPdfWriter(mergedPdf))) {
            PdfMerger merger = new PdfMerger(merged, new PdfMergerProperties().setMergeTags(false).setMergeOutlines(true));

            merger.merge(documentA, 1, documentA.getNumberOfPages());
            merger.merge(documentB, 1, documentB.getNumberOfPages());
            merger.close();
        }

        Assertions.assertNull(new CompareTool().compareByContent(mergedPdf, cmpPdf, destinationFolder));
    }

    @Test
    public void mergePdfDocumentsWithCopingOutlinesTest() throws IOException, InterruptedException {
        String firstPdfDocument = sourceFolder + "firstDocumentWithOutlines.pdf";
        String secondPdfDocument = sourceFolder + "secondDocumentWithOutlines.pdf";
        String cmpDocument = sourceFolder + "cmp_mergeDocumentsWithOutlines.pdf";
        String mergedDocument = destinationFolder + "mergeDocumentsWithOutlines.pdf";

        try (PdfDocument documentA = new PdfDocument(new PdfReader(firstPdfDocument));
                PdfDocument documentB = new PdfDocument(new PdfReader(secondPdfDocument));
                PdfDocument mergedPdf = new PdfDocument(CompareTool.createTestPdfWriter(mergedDocument))) {
            PdfMerger merger = new PdfMerger(mergedPdf, new PdfMergerProperties().setMergeTags(false).setMergeOutlines(true));
            merger.merge(documentA, 1, documentA.getNumberOfPages());
            merger.merge(documentB, 1, documentB.getNumberOfPages());

            merger.close();
        }

        Assertions.assertNull(new CompareTool().compareByContent(mergedDocument, cmpDocument, destinationFolder));
    }

    @Test
    public void MergeWithSameNamedOcgTest() throws IOException, InterruptedException {
        String firstPdfDocument = sourceFolder + "sameNamdOCGSource.pdf";
        String secondPdfDocument = sourceFolder + "doc2.pdf";
        String cmpDocument = sourceFolder + "cmp_MergeWithSameNamedOCG.pdf";
        String mergedDocument = destinationFolder + "mergeWithSameNamedOCG.pdf";

        List<File> sources = new ArrayList<File>();
        sources.add(new File(firstPdfDocument));
        sources.add(new File(secondPdfDocument));
        mergePdfs(sources, mergedDocument, true);

        Assertions.assertNull(new CompareTool().compareByContent(mergedDocument, cmpDocument, destinationFolder));
        // We have to compare visually also because compareByContent doesn't catch the differences in OCGs with the same names
        Assertions.assertNull(new CompareTool().compareVisually(mergedDocument, cmpDocument, destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.SOURCE_DOCUMENT_HAS_ACROFORM_DICTIONARY),
            @LogMessage(messageTemplate = IoLogMessageConstant.DOCUMENT_HAS_CONFLICTING_OCG_NAMES)
    })
    public void MergeWithSameNamedOcgOcmdDTest() throws IOException, InterruptedException {
        String firstPdfDocument = sourceFolder + "Layer doc1.pdf";
        String secondPdfDocument = sourceFolder + "Layer doc2.pdf";
        String cmpDocument = sourceFolder + "cmp_mergeWithSameNamedOCMD.pdf";
        String mergedDocument = destinationFolder + "mergeWithSameNamedOCMD.pdf";

        List<File> sources = new ArrayList<File>();
        sources.add(new File(firstPdfDocument));
        sources.add(new File(secondPdfDocument));
        mergePdfs(sources, mergedDocument, true);

        Assertions.assertNull(new CompareTool().compareByContent(mergedDocument, cmpDocument, destinationFolder));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = KernelLogMessageConstant.STRUCT_PARENT_INDEX_MISSED_AND_RECREATED)
    })
    public void mergePdfWithMissingStructElemBeginningOfTreeTest() throws IOException, InterruptedException {
        String name = "structParentMissingFirstElement.pdf";
        Assertions.assertNotNull(mergeSinglePdfAndGetResultingStructTreeRoot(name));
        Assertions.assertNull(new CompareTool().compareByContent(
                destinationFolder + name,
                sourceFolder + "cmp_" + name, destinationFolder));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.SOURCE_DOCUMENT_HAS_ACROFORM_DICTIONARY),
            @LogMessage(messageTemplate = KernelLogMessageConstant.STRUCT_PARENT_INDEX_MISSED_AND_RECREATED)
    })
    public void mergePdfWithMissingStructElemEndOfTreeTest() throws IOException, InterruptedException {
        String name = "structParentMissingLastElement.pdf";
        Assertions.assertNotNull(mergeSinglePdfAndGetResultingStructTreeRoot(name));
        Assertions.assertNull(new CompareTool().compareByContent(
                destinationFolder + name,
                sourceFolder + "cmp_" + name, destinationFolder));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.SOURCE_DOCUMENT_HAS_ACROFORM_DICTIONARY),
            @LogMessage(messageTemplate = KernelLogMessageConstant.STRUCT_PARENT_INDEX_MISSED_AND_RECREATED, count = 4)
    })
    public void mergePdfAllObjectsMissingStructParentTest() throws IOException, InterruptedException {
        String name = "allObjectsHaveStructParent.pdf";
        Assertions.assertNotNull(mergeSinglePdfAndGetResultingStructTreeRoot(name));
        Assertions.assertNull(new CompareTool().compareByContent(
                destinationFolder + name,
                sourceFolder + "cmp_" + name, destinationFolder));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = KernelLogMessageConstant.STRUCT_PARENT_INDEX_MISSED_AND_RECREATED, count = 2)
    })
    public void mergePdfChildObjectsOfSameStructElemMissingStructParentTest() throws IOException, InterruptedException {
        String name = "SameStructElemNoParent.pdf";
        Assertions.assertNotNull(mergeSinglePdfAndGetResultingStructTreeRoot(name));
        Assertions.assertNull(new CompareTool().compareByContent(
                destinationFolder + name,
                sourceFolder + "cmp_" + name, destinationFolder));
    }

    @Test
    public void mergeDocumentsWithStringAdditionalActions() throws IOException, InterruptedException {
        String firstPdfDocument = sourceFolder + "doc1.pdf";
        String secondPdfDocument = sourceFolder + "docAAString.pdf";
        String cmpDocument = sourceFolder + "cmp_mergeAAString.pdf";
        String mergedDocument = destinationFolder + "mergedAAString.pdf";

        List<File> sources = new ArrayList<File>();
        sources.add(new File(firstPdfDocument));
        sources.add(new File(secondPdfDocument));
        mergePdfs(sources, mergedDocument, new PdfMergerProperties().setMergeScripts(true), true);

        Assertions.assertNull(new CompareTool().compareByContent(mergedDocument, cmpDocument, destinationFolder));
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = KernelLogMessageConstant.CANNOT_MERGE_ENTRY)})
    public void mergeDocumentsWithAdditionalActionsInDestination() throws IOException, InterruptedException {
        String firstPdfDocument = sourceFolder + "docAAStream.pdf";
        String secondPdfDocument = sourceFolder + "docAAString.pdf";
        String cmpDocument = sourceFolder + "cmp_mergeAA2.pdf";
        String mergedDocument = destinationFolder + "mergedAAInDest.pdf";

        List<File> sources = new ArrayList<File>();
        sources.add(new File(firstPdfDocument));
        sources.add(new File(secondPdfDocument));
        mergePdfs(sources, mergedDocument, new PdfMergerProperties().setMergeScripts(true), true);
        Assertions.assertNull(new CompareTool().compareByContent(mergedDocument, cmpDocument, destinationFolder));
    }

    @Test
    public void mergeDocumentsWithUnexpectedKeyAdditionalActions() throws IOException, InterruptedException {
        String firstPdfDocument = sourceFolder + "doc1.pdf";
        String secondPdfDocument = sourceFolder + "docAAStringWithUnexpectedKey.pdf";
        String cmpDocument = sourceFolder + "cmp_mergeAAUnexpectedKey.pdf";
        String mergedDocument = destinationFolder + "mergedAAUnexpectedKey.pdf";

        List<File> sources = new ArrayList<File>();
        sources.add(new File(firstPdfDocument));
        sources.add(new File(secondPdfDocument));
        mergePdfs(sources, mergedDocument, new PdfMergerProperties().setMergeScripts(true), true);

        Assertions.assertNull(new CompareTool().compareByContent(mergedDocument, cmpDocument, destinationFolder));
    }

    @Test
    public void mergeDocumentsWithStreamAdditionalActions() throws IOException, InterruptedException {
        String firstPdfDocument = sourceFolder + "doc1.pdf";
        String secondPdfDocument = sourceFolder + "docAAStream.pdf";
        String cmpDocument = sourceFolder + "cmp_mergeAAStream.pdf";
        String mergedDocument = destinationFolder + "mergedAAStream.pdf";

        List<File> sources = new ArrayList<File>();
        sources.add(new File(firstPdfDocument));
        sources.add(new File(secondPdfDocument));
        mergePdfs(sources, mergedDocument, new PdfMergerProperties().setMergeScripts(true), true);

        Assertions.assertNull(new CompareTool().compareByContent(mergedDocument, cmpDocument, destinationFolder));
    }

    @Test
    public void mergeDocumentsWithStringOpenActions() throws IOException, InterruptedException {
        String firstPdfDocument = sourceFolder + "doc1.pdf";
        String secondPdfDocument = sourceFolder + "docOAString.pdf";
        String cmpDocument = sourceFolder + "cmp_mergeOAString.pdf";
        String mergedDocument = destinationFolder + "mergedOAString.pdf";

        List<File> sources = new ArrayList<File>();
        sources.add(new File(firstPdfDocument));
        sources.add(new File(secondPdfDocument));
        mergePdfs(sources, mergedDocument, new PdfMergerProperties().setMergeScripts(true), true);

        Assertions.assertNull(new CompareTool().compareByContent(mergedDocument, cmpDocument, destinationFolder));
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = KernelLogMessageConstant.CANNOT_MERGE_ENTRY)})
    public void mergeDocumentsWithOpenActionInDestination() throws IOException, InterruptedException {
        String firstPdfDocument = sourceFolder + "docOAStream.pdf";
        String secondPdfDocument = sourceFolder + "docOAString.pdf";
        String cmpDocument = sourceFolder + "cmp_mergeOA2.pdf";
        String mergedDocument = destinationFolder + "mergedOAInDest.pdf";

        List<File> sources = new ArrayList<File>();
        sources.add(new File(firstPdfDocument));
        sources.add(new File(secondPdfDocument));
        mergePdfs(sources, mergedDocument, new PdfMergerProperties().setMergeScripts(true), true);
        Assertions.assertNull(new CompareTool().compareByContent(mergedDocument, cmpDocument, destinationFolder));
    }

    @Test
    public void mergeDocumentsWithStreamOpenActions() throws IOException, InterruptedException {
        String firstPdfDocument = sourceFolder + "doc1.pdf";
        String secondPdfDocument = sourceFolder + "docOAStream.pdf";
        String cmpDocument = sourceFolder + "cmp_mergeOAStream.pdf";
        String mergedDocument = destinationFolder + "mergedOAStream.pdf";

        List<File> sources = new ArrayList<File>();
        sources.add(new File(firstPdfDocument));
        sources.add(new File(secondPdfDocument));
        mergePdfs(sources, mergedDocument, new PdfMergerProperties().setMergeScripts(true), true);

        Assertions.assertNull(new CompareTool().compareByContent(mergedDocument, cmpDocument, destinationFolder));
    }


    @Test
    public void mergeDocumentsWithJSInTree() throws IOException, InterruptedException {
        String firstPdfDocument = sourceFolder + "doc1.pdf";
        String secondPdfDocument = sourceFolder + "docJS.pdf";
        String cmpDocument = sourceFolder + "cmp_mergeJS.pdf";
        String mergedDocument = destinationFolder + "mergedJS.pdf";

        List<File> sources = new ArrayList<File>();
        sources.add(new File(firstPdfDocument));
        sources.add(new File(secondPdfDocument));
        mergePdfs(sources, mergedDocument, new PdfMergerProperties().setMergeScripts(true), true);

        Assertions.assertNull(new CompareTool().compareByContent(mergedDocument, cmpDocument, destinationFolder));
    }

    @Test
    public void mergeDocumentsWithNullDestination() throws IOException, InterruptedException {
        String firstPdfDocument = sourceFolder + "doc1.pdf";
        String secondPdfDocument = sourceFolder + "linkAnnotationWithNullDestinationTest.pdf";
        String cmpDocument = sourceFolder + "cmp_linkAnnotationWithNullDestinationTest.pdf";
        String mergedDocument = destinationFolder + "mergedLinkAnnotationWithNullDestinationTest.pdf";

        List<File> sources = new ArrayList<File>();
        sources.add(new File(firstPdfDocument));
        sources.add(new File(secondPdfDocument));
        mergePdfs(sources, mergedDocument, new PdfMergerProperties().setMergeScripts(true), true);

        Assertions.assertNull(new CompareTool().compareByContent(mergedDocument, cmpDocument, destinationFolder));
    }

    @Test
    public void mergeDocumentsWithNullDestinationInGoTo() throws IOException, InterruptedException {
        String firstPdfDocument = sourceFolder + "doc1.pdf";
        String secondPdfDocument = sourceFolder + "linkAnnotationWithNullDestinationInGoToTest.pdf";
        String cmpDocument = sourceFolder + "cmp_linkAnnotationWithNullDestinationInGoToTest.pdf";
        String mergedDocument = destinationFolder + "mergedLinkAnnotationWithNullDestinationInGoToTest.pdf";

        List<File> sources = new ArrayList<File>();
        sources.add(new File(firstPdfDocument));
        sources.add(new File(secondPdfDocument));
        mergePdfs(sources, mergedDocument, new PdfMergerProperties().setMergeScripts(true), true);

        Assertions.assertNull(new CompareTool().compareByContent(mergedDocument, cmpDocument, destinationFolder));
    }

    @Test
    public void mergeDocumentsWithPdfNullDestinationInGoTo() throws IOException, InterruptedException {
        String firstPdfDocument = sourceFolder + "doc1.pdf";
        String secondPdfDocument = sourceFolder + "linkAnnotationWithPdfNullDestinationInGoToTest.pdf";
        String cmpDocument = sourceFolder + "cmp_linkAnnotationWithPdfNullDestinationInGoToTest.pdf";
        String mergedDocument = destinationFolder + "mergedLinkAnnotationWithPdfNullDestinationInGoToTest.pdf";

        List<File> sources = new ArrayList<File>();
        sources.add(new File(firstPdfDocument));
        sources.add(new File(secondPdfDocument));
        mergePdfs(sources, mergedDocument, new PdfMergerProperties().setMergeScripts(true), true);

        Assertions.assertNull(new CompareTool().compareByContent(mergedDocument, cmpDocument, destinationFolder));
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = KernelLogMessageConstant.CANNOT_MERGE_ENTRY)})
    public void mergeDocumentsWithNamesJSInDestination() throws IOException, InterruptedException {
        String firstPdfDocument = sourceFolder + "cmp_mergeJS.pdf";
        String secondPdfDocument = sourceFolder + "docJS.pdf";
        String cmpDocument = sourceFolder + "cmp_mergeJS2.pdf";
        String mergedDocument = destinationFolder + "mergedJSInDest.pdf";

        List<File> sources = new ArrayList<File>();
        sources.add(new File(firstPdfDocument));
        sources.add(new File(secondPdfDocument));
        mergePdfs(sources, mergedDocument, new PdfMergerProperties().setMergeScripts(true), true);
        Assertions.assertNull(new CompareTool().compareByContent(mergedDocument, cmpDocument, destinationFolder));
    }

    @Test
    public void copyEmptyOcPropertiesTest() throws IOException, InterruptedException {
        String filename = sourceFolder + "emptyOcPropertiesDoc.pdf";
        String resultFile = destinationFolder + "mergedEmptyOcPropertiesDoc.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(filename));
        PdfDocument result = new PdfDocument(CompareTool.createTestPdfWriter(resultFile));

        PdfMerger merger = new PdfMerger(result).setCloseSourceDocuments(true);

        merger.merge(pdfDoc, 1, 1).close();

        Assertions.assertNull(new CompareTool().compareByContent(resultFile, sourceFolder + "cmp_mergedEmptyOcPropertiesDoc.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void copyOnlyEmptyOcPropertiesTest() throws IOException, InterruptedException {
        String filename = sourceFolder + "ocPropertiesDoc.pdf";
        String resultFile = destinationFolder + "mergedOcPropertiesDoc.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(filename));
        PdfDocument result = new PdfDocument(CompareTool.createTestPdfWriter(resultFile));

        PdfMerger merger = new PdfMerger(result).setCloseSourceDocuments(true);

        merger.merge(pdfDoc, 1, 1).close();

        Assertions.assertNull(new CompareTool().compareByContent(resultFile, sourceFolder + "cmp_mergedOcPropertiesDoc.pdf", destinationFolder, "diff_"));
    }

    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.SOURCE_DOCUMENT_HAS_ACROFORM_DICTIONARY)
    })
    @Test
    public void combineTagRootKidsTest() throws IOException, InterruptedException {
        String filename1 = sourceFolder + "tagRootKidsDoc1.pdf";
        String filename2 = sourceFolder + "tagRootKidsDoc2.pdf";
        String resultFile = destinationFolder + "mergedTags.pdf";

        PdfDocument result = new PdfDocument(CompareTool.createTestPdfWriter(resultFile));

        PdfMerger merger = new PdfMerger(result, new PdfMergerProperties().setMergeTags(true).setMergeOutlines(true))
                .setCloseSourceDocuments(true);

        PdfDocument input1 = new PdfDocument(new PdfReader(filename1));
        merger.merge(input1, 1, 1);
        input1.close();

        PdfDocument input2 = new PdfDocument(new PdfReader(filename2));
        merger.merge(input2, 1, 1);
        input2.close();

        merger.close();

        Assertions.assertNull(new CompareTool()
                .compareByContent(resultFile, sourceFolder + "cmp_mergedTags.pdf", destinationFolder, "diff_"));
    }

    private PdfDictionary mergeSinglePdfAndGetResultingStructTreeRoot(String pathToMerge)
            throws IOException {
        List<File> sources = new ArrayList<File>();
        sources.add(new File(sourceFolder + pathToMerge));
        String mergedDoc = destinationFolder + pathToMerge;
        mergePdfs(sources, mergedDoc, true);
        return getStructTreeRootOfDocument(mergedDoc);
    }

    private PdfDictionary getStructTreeRootOfDocument(String pathToFile) throws IOException {
        PdfDocument mergedDocument = new PdfDocument(new PdfReader(pathToFile));
        return mergedDocument.getCatalog().getPdfObject()
                .getAsDictionary(PdfName.StructTreeRoot);
    }

    private void mergePdfs(List<File> sources, String destination, boolean smartMode) throws IOException {
        PdfDocument mergedDoc = new PdfDocument(new PdfWriter(destination));
        mergedDoc.getWriter().setSmartMode(smartMode);
        PdfMerger merger = new PdfMerger(mergedDoc);
        for (File source: sources) {
            PdfDocument sourcePdf = new PdfDocument(new PdfReader(source));
            merger.merge(sourcePdf, 1, sourcePdf.getNumberOfPages()).setCloseSourceDocuments(true);
            sourcePdf.close();
        }

        merger.close();
        mergedDoc.close();
    }

    private void mergePdfs(List<File> sources, String destination, PdfMergerProperties properties, boolean smartMode) throws IOException {
        PdfDocument mergedDoc = new PdfDocument(CompareTool.createTestPdfWriter(destination));
        mergedDoc.getWriter().setSmartMode(smartMode);
        PdfMerger merger = new PdfMerger(mergedDoc, properties);
        for (File source: sources) {
            PdfDocument sourcePdf = new PdfDocument(new PdfReader(source));
            merger.merge(sourcePdf, 1, sourcePdf.getNumberOfPages()).setCloseSourceDocuments(true);
            sourcePdf.close();
        }

        merger.close();
        mergedDoc.close();
    }
}
