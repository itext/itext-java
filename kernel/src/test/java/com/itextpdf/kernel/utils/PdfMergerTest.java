/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.xml.sax.SAXException;

@Category(IntegrationTest.class)
public class PdfMergerTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/utils/PdfMergerTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/utils/PdfMergerTest/";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
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

        FileOutputStream fos1 = new FileOutputStream(resultFile);
        PdfWriter writer1 = new PdfWriter(fos1);
        PdfDocument pdfDoc = new PdfDocument(reader);
        PdfDocument pdfDoc1 = new PdfDocument(reader1);
        PdfDocument pdfDoc2 = new PdfDocument(reader2);
        PdfDocument pdfDoc3 = new PdfDocument(writer1);

        PdfMerger merger = new PdfMerger(pdfDoc3).setCloseSourceDocuments(true);
        merger.merge(pdfDoc, 1, 1);
        merger.merge(pdfDoc1, 1, 1);

        merger.merge(pdfDoc2, 1, 1);

        pdfDoc3.close();

        Assert.assertNull(new CompareTool().compareByContent(resultFile, sourceFolder + "cmp_mergedResult01.pdf", destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.SOURCE_DOCUMENT_HAS_ACROFORM_DICTIONARY)
    })
    public void mergeDocumentOutlinesWithNullDestinationTest01() throws IOException, InterruptedException {
        String resultFile = destinationFolder + "mergeDocumentOutlinesWithNullDestinationTest01.pdf";
        String filename = sourceFolder + "null_dest_outline.pdf";
        PdfDocument sourceDocument = new PdfDocument(new PdfReader(filename));

        PdfMerger resultDocument = new PdfMerger(new PdfDocument(new PdfWriter(resultFile)));
        resultDocument.merge(sourceDocument, 1, 1);
        resultDocument.close();
        sourceDocument.close();

        Assert.assertNull(new CompareTool().compareByContent(resultFile, sourceFolder + "cmp_mergeDocumentOutlinesWithNullDestinationTest01.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void mergeDocumentWithCycleRefInAcroFormTest() throws IOException, InterruptedException {
        String filename1 = sourceFolder + "doc1.pdf";
        String filename2 = sourceFolder + "pdfWithCycleRefInAnnotationParent.pdf";
        String resultFile = destinationFolder + "resultFileWithoutStackOverflow.pdf";
        try (PdfDocument pdfDocument1 = new PdfDocument(new PdfReader(filename2));
             PdfDocument pdfDocument2 = new PdfDocument(new PdfReader(filename1),
                        new PdfWriter(resultFile).setSmartMode(true));) {
            PdfMerger merger = new PdfMerger(pdfDocument2);
            merger.merge(pdfDocument1, 1, pdfDocument1.getNumberOfPages());
        }
        Assert.assertNull(
                new CompareTool().compareByContent(resultFile, sourceFolder + "cmp_resultFileWithoutStackOverflow.pdf",
                        destinationFolder, "diff_"));
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

        FileOutputStream fos1 = new FileOutputStream(resultFile);
        PdfWriter writer1 = new PdfWriter(fos1);
        PdfDocument pdfDoc = new PdfDocument(reader);
        PdfDocument pdfDoc1 = new PdfDocument(reader1);
        PdfDocument pdfDoc2 = new PdfDocument(reader2);
        PdfDocument pdfDoc3 = new PdfDocument(writer1);
        PdfMerger merger = new PdfMerger(pdfDoc3).setCloseSourceDocuments(true);

        merger.merge(pdfDoc, 1, 1).merge(pdfDoc1, 1, 1).merge(pdfDoc2, 1, 1).close();

        Assert.assertNull(new CompareTool().compareByContent(resultFile, sourceFolder + "cmp_mergedResult02.pdf", destinationFolder, "diff_"));
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

        FileOutputStream fos1 = new FileOutputStream(resultFile);
        PdfWriter writer1 = new PdfWriter(fos1);
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
            Assert.fail(errorMessage);
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

        FileOutputStream fos1 = new FileOutputStream(resultFile);
        PdfWriter writer1 = new PdfWriter(fos1);
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
            Assert.fail(errorMessage);
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
    // TODO DEVSIX-5974 Empty tr isn't copied.
    public void emptyTrTableTest() throws ParserConfigurationException, SAXException, IOException, InterruptedException {
        mergeAndCompareTagStructures("emptyTrTable.pdf", 1, 1);
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = IoLogMessageConstant.NAME_ALREADY_EXISTS_IN_THE_NAME_TREE, count = 2)})
    public void mergeOutlinesNamedDestinations() throws IOException, InterruptedException {
        String filename = sourceFolder + "outlinesNamedDestinations.pdf";
        String resultFile = destinationFolder + "mergeOutlinesNamedDestinations.pdf";

        PdfReader reader = new PdfReader(filename);

        PdfDocument sourceDoc = new PdfDocument(reader);
        PdfDocument output = new PdfDocument(new PdfWriter(resultFile));
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
            Assert.fail(errorMessage);
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

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder));
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

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder));
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

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder));
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

        PdfDocument mergedDoc = new PdfDocument(new PdfWriter(outPdf));
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

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder));
    }

    @Test
    public void mergePdfWithComplexOCGTwiceTest() throws IOException, InterruptedException {
        String pdfWithOCG = sourceFolder  + "pdfWithComplexOCG.pdf";
        String outPdf = destinationFolder + "mergePdfWithComplexOCGTwiceTest.pdf";
        String cmpPdf = sourceFolder + "cmp_mergePdfWithComplexOCGTwiceTest.pdf";

        PdfDocument mergedDoc = new PdfDocument(new PdfWriter(outPdf));
        PdfMerger merger = new PdfMerger(mergedDoc);
        PdfDocument sourcePdf = new PdfDocument(new PdfReader(new File(pdfWithOCG)));
        // The test verifies that identical layers from the same document are not copied
        merger.merge(sourcePdf, 1, sourcePdf.getNumberOfPages());
        merger.merge(sourcePdf, 1, sourcePdf.getNumberOfPages());
        sourcePdf.close();
        merger.close();
        mergedDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder));
    }

    @Test
    public void stackOverflowErrorCycleReferenceOcgMergeTest() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "cycleReferenceMerged.pdf";
        String cmpPdf = sourceFolder + "cmp_stackOverflowErrorCycleReferenceOcrMerge.pdf";
        
        PdfDocument pdfWithOCG = new PdfDocument(new PdfReader(sourceFolder + "sourceOCG1.pdf"),
                new PdfWriter(outPdf));
        PdfDocument pdfWithOCGToMerge = new PdfDocument
                (new PdfReader( sourceFolder + "stackOverflowErrorCycleReferenceOcgMerge.pdf")); // problem file
        PdfMerger merger = new PdfMerger(pdfWithOCG);
        merger.merge(pdfWithOCGToMerge, 1, pdfWithOCGToMerge.getNumberOfPages());
        pdfWithOCGToMerge.close();
        pdfWithOCG.close();
        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.SOURCE_DOCUMENT_HAS_ACROFORM_DICTIONARY)
    })
    public void mergeOutlinesWithWrongStructureTest() throws IOException, InterruptedException {
        PdfDocument inputDoc = new PdfDocument(new PdfReader(
                sourceFolder + "infiniteLoopInOutlineStructure.pdf"));

        PdfDocument outputDoc = new PdfDocument(new PdfWriter(
                destinationFolder + "infiniteLoopInOutlineStructure.pdf"));

        PdfMerger merger = new PdfMerger(outputDoc, false, true);
        System.out.println("Doing merge");
        merger.merge(inputDoc, 1, 2);
        merger.close();
        System.out.println("Merge done");

        Assert.assertNull(new CompareTool().compareByContent(
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
        PdfDocument output = new PdfDocument(new PdfWriter(dest));
        output.setTagged();
        PdfMerger merger = new PdfMerger(output).setCloseSourceDocuments(true);
        merger.merge(sourceDoc, fromPage, toPage);
        sourceDoc.close();
        reader.close();
        merger.close();
        output.close();

        Assert.assertNull(new CompareTool().compareTagStructures(dest, cmp));
    }

    @Test
    public void mergeDocumentWithColorPropertyInOutlineTest() throws IOException, InterruptedException {
        String firstDocument = sourceFolder + "firstDocumentWithColorPropertyInOutline.pdf";
        String secondDocument = sourceFolder + "SecondDocumentWithColorPropertyInOutline.pdf";
        String cmpDocument = sourceFolder + "cmp_mergeOutlinesWithColorProperty.pdf";
        String mergedPdf = destinationFolder + "mergeOutlinesWithColorProperty.pdf";
        try (PdfDocument merged = new PdfDocument(new PdfWriter(mergedPdf));
                PdfDocument fileA = new PdfDocument(new PdfReader(firstDocument));
                PdfDocument fileB = new PdfDocument(new PdfReader(secondDocument))) {
            PdfMerger merger = new PdfMerger(merged, false, true);

            merger.merge(fileA, 1, fileA.getNumberOfPages());
            merger.merge(fileB, 1, fileB.getNumberOfPages());

            merger.close();
        }

        Assert.assertNull(new CompareTool().compareByContent(mergedPdf, cmpDocument, destinationFolder));
    }

    @Test
    public void mergeDocumentWithStylePropertyInOutlineTest() throws IOException, InterruptedException {
        String firstDocument = sourceFolder + "firstDocumentWithStylePropertyInOutline.pdf";
        String secondDocument = sourceFolder + "secondDocumentWithStylePropertyInOutline.pdf";
        String cmpPdf = sourceFolder + "cmp_mergeOutlineWithStyleProperty.pdf";
        String mergedPdf = destinationFolder + "mergeOutlineWithStyleProperty.pdf";

        try (PdfDocument documentA = new PdfDocument(new PdfReader(firstDocument));
                PdfDocument documentB = new PdfDocument(new PdfReader(secondDocument));
                PdfDocument merged = new PdfDocument(new PdfWriter(mergedPdf))) {
            PdfMerger merger = new PdfMerger(merged, false, true);

            merger.merge(documentA, 1, documentA.getNumberOfPages());
            merger.merge(documentB, 1, documentB.getNumberOfPages());
            merger.close();
        }

        Assert.assertNull(new CompareTool().compareByContent(mergedPdf, cmpPdf, destinationFolder));
    }

    @Test
    public void mergePdfDocumentsWithCopingOutlinesTest() throws IOException, InterruptedException {
        String firstPdfDocument = sourceFolder + "firstDocumentWithOutlines.pdf";
        String secondPdfDocument = sourceFolder + "secondDocumentWithOutlines.pdf";
        String cmpDocument = sourceFolder + "cmp_mergeDocumentsWithOutlines.pdf";
        String mergedDocument = destinationFolder + "mergeDocumentsWithOutlines.pdf";

        try (PdfDocument documentA = new PdfDocument(new PdfReader(firstPdfDocument));
                PdfDocument documentB = new PdfDocument(new PdfReader(secondPdfDocument));
                PdfDocument mergedPdf = new PdfDocument(new PdfWriter(mergedDocument))) {
            PdfMerger merger = new PdfMerger(mergedPdf, false, true);
            merger.merge(documentA, 1, documentA.getNumberOfPages());
            merger.merge(documentB, 1, documentB.getNumberOfPages());

            merger.close();
        }

        Assert.assertNull(new CompareTool().compareByContent(mergedDocument, cmpDocument, destinationFolder));
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

        Assert.assertNull(new CompareTool().compareByContent(mergedDocument, cmpDocument, destinationFolder));
        // We have to compare visually also because compareByContent doesn't catch the differences in OCGs with the same names
        Assert.assertNull(new CompareTool().compareVisually(mergedDocument, cmpDocument, destinationFolder, "diff_"));
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

        Assert.assertNull(new CompareTool().compareByContent(mergedDocument, cmpDocument, destinationFolder));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.TAG_STRUCTURE_INIT_FAILED)
    })
    public void mergePdfWithMissingStructElemBeginningOfTreeTest() throws IOException {
        //TODO change assertion after DEVSIX-7478 is fixed
        Assert.assertNull(mergeSinglePdfAndGetResultingStructTreeRoot("structParentMissingFirstElement.pdf"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.TAG_STRUCTURE_INIT_FAILED),
            @LogMessage(messageTemplate = IoLogMessageConstant.SOURCE_DOCUMENT_HAS_ACROFORM_DICTIONARY)
    })
    public void mergePdfWithMissingStructElemEndOfTreeTest() throws IOException {
        //TODO change assertion after DEVSIX-7478 is fixed
        Assert.assertNull(
                mergeSinglePdfAndGetResultingStructTreeRoot("structParentMissingLastElement.pdf"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.TAG_STRUCTURE_INIT_FAILED),
            @LogMessage(messageTemplate = IoLogMessageConstant.SOURCE_DOCUMENT_HAS_ACROFORM_DICTIONARY)
    })
    public void mergePdfAllObjectsMissingStructParentTest() throws IOException {
        //TODO change assertion after DEVSIX-7478 is fixed
        Assert.assertNull(mergeSinglePdfAndGetResultingStructTreeRoot(
                "allObjectsHaveStructParent.pdf"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.TAG_STRUCTURE_INIT_FAILED)
    })
    public void mergePdfChildObjectsOfSameStructElemMissingStructParentTest() throws IOException {
        //TODO change assertion after DEVSIX-7478 is fixed
        Assert.assertNull(mergeSinglePdfAndGetResultingStructTreeRoot(
                "SameStructElemNoParent.pdf"));
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
}
