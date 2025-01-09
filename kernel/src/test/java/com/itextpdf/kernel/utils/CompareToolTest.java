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

import com.itextpdf.io.exceptions.IoExceptionMessageConstant;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.util.GhostscriptHelper;
import com.itextpdf.commons.utils.SystemUtil;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfDocumentInfo;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.navigation.PdfExplicitDestination;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import org.xml.sax.SAXException;

@Tag("IntegrationTest")
// Android-Conversion-Skip-File (during Android conversion the class will be replaced by DeferredCompareTool)
public class CompareToolTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/utils/CompareToolTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/utils/CompareToolTest/";

    @BeforeAll
    public static void setUp() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @AfterAll
    public static void afterClass() {
        CompareTool.cleanup(destinationFolder);
    }

    @Test
    public void compareToolErrorReportTest01()
            throws InterruptedException, IOException, ParserConfigurationException, SAXException {
        CompareTool compareTool = new CompareTool();
        compareTool.setCompareByContentErrorsLimit(10);
        compareTool.setGenerateCompareByContentXmlReport(true);
        String outPdf = sourceFolder + "simple_pdf.pdf";
        String cmpPdf = sourceFolder + "cmp_simple_pdf.pdf";
        String result = compareTool.compareByContent(outPdf, cmpPdf, destinationFolder);
        System.out.println("\nRESULT:\n" + result);
        Assertions.assertNotNull("CompareTool must return differences found between the files", result);
        Assertions.assertTrue(result.contains("differs on page [1, 2]."));
        // Comparing the report to the reference one.
        Assertions.assertTrue(
                compareTool.compareXmls(destinationFolder + "simple_pdf.report.xml", sourceFolder + "cmp_report01.xml"),
                "CompareTool report differs from the reference one");
    }

    @Test
    public void compareToolErrorReportTest02()
            throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        CompareTool compareTool = new CompareTool();
        compareTool.setCompareByContentErrorsLimit(10);
        compareTool.setGenerateCompareByContentXmlReport(true);
        String outPdf = sourceFolder + "tagged_pdf.pdf";
        String cmpPdf = sourceFolder + "cmp_tagged_pdf.pdf";
        String result = compareTool.compareByContent(outPdf, cmpPdf, destinationFolder);
        System.out.println("\nRESULT:\n" + result);
        Assertions.assertNotNull("CompareTool must return differences found between the files", result);
        Assertions.assertTrue(result.contains("Compare by content fails. No visual differences"));
        // Comparing the report to the reference one.
        Assertions.assertTrue(
                compareTool.compareXmls(destinationFolder + "tagged_pdf.report.xml", sourceFolder + "cmp_report02.xml"),
                "CompareTool report differs from the reference one");
    }

    @Test
    public void compareToolErrorReportTest03()
            throws InterruptedException, IOException, ParserConfigurationException, SAXException {
        CompareTool compareTool = new CompareTool();
        compareTool.setCompareByContentErrorsLimit(10);
        compareTool.setGenerateCompareByContentXmlReport(true);
        String outPdf = sourceFolder + "screenAnnotation.pdf";
        String cmpPdf = sourceFolder + "cmp_screenAnnotation.pdf";
        String result = compareTool.compareByContent(outPdf, cmpPdf, destinationFolder);
        System.out.println("\nRESULT:\n" + result);
        Assertions.assertNotNull("CompareTool must return differences found between the files", result);
        Assertions.assertTrue(result.contains("Compare by content fails. No visual differences"));
        // Comparing the report to the reference one.
        Assertions.assertTrue(compareTool.compareXmls(destinationFolder + "screenAnnotation.report.xml",
                sourceFolder + "cmp_report03.xml"), "CompareTool report differs from the reference one");
    }


    @Test
    // Test space in name
    public void compareToolErrorReportTest04()
            throws InterruptedException, IOException, ParserConfigurationException, SAXException {
        CompareTool compareTool = new CompareTool();
        compareTool.setCompareByContentErrorsLimit(10);
        compareTool.setGenerateCompareByContentXmlReport(true);
        String outPdf = sourceFolder + "simple_pdf.pdf";
        String cmpPdf = sourceFolder + "cmp_simple_pdf_with_space .pdf";
        String result = compareTool.compareByContent(outPdf, cmpPdf, destinationFolder);
        System.out.println("\nRESULT:\n" + result);
        Assertions.assertNotNull("CompareTool must return differences found between the files", result);
        Assertions.assertTrue(result.contains("differs on page [1, 2]."));
        // Comparing the report to the reference one.
        Assertions.assertTrue(
                compareTool.compareXmls(destinationFolder + "simple_pdf.report.xml", sourceFolder + "cmp_report01.xml"),
                "CompareTool report differs from the reference one");

    }

    @Test
    public void differentProducerTest() throws IOException {
        String expectedMessage = "Document info fail. Expected: \"iText\u00ae <version> \u00a9<copyright years> Apryse Group NV (iText Software; licensed version)\", actual: \"iText\u00ae <version> \u00a9<copyright years> Apryse Group NV (AGPL-version)\"";
        String licensed = sourceFolder + "producerLicensed.pdf";
        String agpl = sourceFolder + "producerAGPL.pdf";
        Assertions.assertEquals(expectedMessage, new CompareTool().compareDocumentInfo(agpl, licensed));
    }

    @Test
    public void versionReplaceTest() {
        String initial = "iText® 1.10.10-SNAPSHOT (licensed to iText) ©2000-2018 Apryse Group NV";
        String replacedExpected = "iText® <version> (licensed to iText) ©<copyright years> Apryse Group NV";
        Assertions.assertEquals(replacedExpected, new CompareTool().convertProducerLine(initial));
    }

    @Test
    public void gsEnvironmentVariableIsNotSpecifiedExceptionTest() throws IOException, InterruptedException {
        String outPdf = sourceFolder + "simple_pdf.pdf";
        String cmpPdf = sourceFolder + "cmp_simple_pdf.pdf";
        new CompareTool(null, null).compareVisually(outPdf, cmpPdf, destinationFolder, "diff_");
        Assertions.assertTrue(new File(destinationFolder + "diff_1.png").exists());
    }

    @Test
    public void compareXmpThrows(){
        CompareTool compareTool = new CompareTool();
        String outPdf = sourceFolder + "simple_pdf.pdf";
        String cmpPdf = sourceFolder + "cmp_simple_pdf.pdf";
        Assertions.assertEquals("XMP parsing failure!", compareTool.compareXmp(outPdf, cmpPdf));
    }

    @Test
    public void gsEnvironmentVariableSpecifiedIncorrectlyTest() throws IOException, InterruptedException {
        String outPdf = sourceFolder + "simple_pdf.pdf";
        String cmpPdf = sourceFolder + "cmp_simple_pdf.pdf";

        Exception e = Assertions.assertThrows(CompareTool.CompareToolExecutionException.class,
                () -> new CompareTool("unspecified", null).compareVisually(outPdf, cmpPdf, destinationFolder, "diff_")
        );
        Assertions.assertEquals(IoExceptionMessageConstant.GS_ENVIRONMENT_VARIABLE_IS_NOT_SPECIFIED, e.getMessage());
    }

    @Test
    public void compareCommandIsNotSpecifiedTest() throws IOException, InterruptedException {
        String outPdf = sourceFolder + "simple_pdf.pdf";
        String cmpPdf = sourceFolder + "cmp_simple_pdf.pdf";
        String gsExec = SystemUtil.getPropertyOrEnvironmentVariable(GhostscriptHelper.GHOSTSCRIPT_ENVIRONMENT_VARIABLE);
        if (gsExec == null) {
            gsExec = SystemUtil.getPropertyOrEnvironmentVariable("gsExec");
        }
        String result = new CompareTool(gsExec, null)
                .compareVisually(outPdf, cmpPdf, destinationFolder, "diff_");
        Assertions.assertFalse(result.contains(IoExceptionMessageConstant.COMPARE_COMMAND_IS_NOT_SPECIFIED));
        Assertions.assertTrue(new File(destinationFolder + "diff_1.png").exists());
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoExceptionMessageConstant.COMPARE_COMMAND_SPECIFIED_INCORRECTLY)})
    public void compareCommandSpecifiedIncorrectlyTest() throws IOException, InterruptedException {
        String outPdf = sourceFolder + "simple_pdf.pdf";
        String cmpPdf = sourceFolder + "cmp_simple_pdf.pdf";
        String gsExec = SystemUtil.getPropertyOrEnvironmentVariable(GhostscriptHelper.GHOSTSCRIPT_ENVIRONMENT_VARIABLE);
        if (gsExec == null) {
            gsExec = SystemUtil.getPropertyOrEnvironmentVariable("gsExec");
        }
        String result = new CompareTool(gsExec, "unspecified")
                .compareVisually(outPdf, cmpPdf, destinationFolder, "diff_");
        Assertions.assertTrue(result.contains(IoExceptionMessageConstant.COMPARE_COMMAND_SPECIFIED_INCORRECTLY));
    }

    @Test
    public void compareVisuallyDiffTestTest() throws IOException, InterruptedException {
        String outPdf = sourceFolder + "compareVisuallyDiffTestTest1.pdf";
        String cmpPdf = sourceFolder + "compareVisuallyDiffTestTest2.pdf";
        String result = new CompareTool().compareVisually(outPdf, cmpPdf, destinationFolder, "diff_");
        System.out.println("\nRESULT:\n" + result);
        Assertions.assertTrue(result.contains("differs on page [1, 2]."));
        Assertions.assertTrue(new File(destinationFolder + "diff_1.png").exists());
        Assertions.assertTrue(new File(destinationFolder + "diff_2.png").exists());
    }

    @Test
    public void compareDiffFilesWithSameLinkAnnotationTest() throws IOException {
        String firstPdf = destinationFolder + "firstPdf.pdf";
        String secondPdf = destinationFolder + "secondPdf.pdf";
        PdfDocument firstDocument = new PdfDocument(CompareTool.createTestPdfWriter(firstPdf));

        PdfPage page1FirstDocument = firstDocument.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page1FirstDocument);
        canvas.beginText();
        canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.COURIER_BOLD), 14);
        canvas.moveText(100, 600);
        canvas.showText("Page 1");
        canvas.moveText(0, -30);
        canvas.showText("Link to page 1. Click here!");
        canvas.endText();
        canvas.release();
        page1FirstDocument.addAnnotation(new PdfLinkAnnotation(new Rectangle(100, 560, 260, 25)).setDestination(
                PdfExplicitDestination.createFit(page1FirstDocument)).setBorder(new PdfArray(new float[] {0, 0, 1})));
        page1FirstDocument.flush();
        firstDocument.close();

        PdfDocument secondDocument = new PdfDocument(CompareTool.createTestPdfWriter(secondPdf));
        PdfPage page1secondDocument = secondDocument.addNewPage();
        canvas = new PdfCanvas(page1secondDocument);
        canvas.beginText();
        canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.COURIER_BOLD), 14);
        canvas.moveText(100, 600);
        canvas.showText("Page 1 wit different Text");
        canvas.moveText(0, -30);
        canvas.showText("Link to page 1. Click here!");
        canvas.endText();
        canvas.release();
        page1secondDocument.addAnnotation(new PdfLinkAnnotation(new Rectangle(100, 560, 260, 25)).setDestination(
                PdfExplicitDestination.createFit(page1secondDocument)).setBorder(new PdfArray(new float[] {0, 0, 1})));
        page1secondDocument.flush();
        secondDocument.close();

        Assertions.assertNull(new CompareTool().compareLinkAnnotations(firstPdf, secondPdf));
    }

    @Test
    public void compareFilesWithDiffLinkAnnotationTest() throws IOException {
        String firstPdf = destinationFolder + "outPdf.pdf";
        String secondPdf = destinationFolder + "secondPdf.pdf";
        PdfDocument firstDocument = new PdfDocument(CompareTool.createTestPdfWriter(firstPdf));
        PdfDocument secondDocument = new PdfDocument(CompareTool.createTestPdfWriter(secondPdf));

        PdfPage page1FirstDocument = firstDocument.addNewPage();
        page1FirstDocument.addAnnotation(new PdfLinkAnnotation(new Rectangle(100, 560, 400, 50)).setDestination(
                PdfExplicitDestination.createFit(page1FirstDocument)).setBorder(new PdfArray(new float[] {0, 0, 1})));
        page1FirstDocument.flush();
        firstDocument.close();

        PdfPage page1SecondDocument = secondDocument.addNewPage();
        page1SecondDocument.addAnnotation(new PdfLinkAnnotation(new Rectangle(100, 560, 260, 25)).setDestination(
                PdfExplicitDestination.createFit(page1SecondDocument)).setBorder(new PdfArray(new float[] {0, 0, 1})));
        page1SecondDocument.flush();
        secondDocument.close();

        Assertions.assertNotNull(new CompareTool().compareLinkAnnotations(firstPdf, secondPdf));
    }

    @Test
    public void convertDocInfoToStringsTest() throws IOException {
        String inPdf = sourceFolder + "test.pdf";

        class TestCompareTool extends CompareTool {
            @Override
            protected String[] convertDocInfoToStrings(PdfDocumentInfo info) {
                return super.convertDocInfoToStrings(info);
            }
        }

        CompareTool compareTool = new TestCompareTool();
        try (PdfReader reader = new PdfReader(inPdf, compareTool.getOutReaderProperties());
                PdfDocument doc = new PdfDocument(reader)) {
            String[] docInfo = compareTool.convertDocInfoToStrings(doc.getDocumentInfo());
            Assertions.assertEquals("very long title to compare later on", docInfo[0]);
            Assertions.assertEquals("itextcore", docInfo[1]);
            Assertions.assertEquals("test file", docInfo[2]);
            Assertions.assertEquals("new job", docInfo[3]);
            Assertions.assertEquals("Adobe Acrobat Pro DC (64-bit) <version>", docInfo[4]);
                }
    }

    @Test
    public void memoryFirstWriterNoFileTest() throws InterruptedException, IOException {
        String firstPdf = destinationFolder + "memoryFirstWriterNoFileTest.pdf";
        String secondPdf = destinationFolder + "memoryFirstWriterNoFileTest2.pdf";
        PdfDocument firstDocument = new PdfDocument(CompareTool.createTestPdfWriter(firstPdf));
        PdfDocument secondDocument = new PdfDocument(CompareTool.createTestPdfWriter(secondPdf));

        PdfPage page1FirstDocument = firstDocument.addNewPage();
        page1FirstDocument.addAnnotation(new PdfLinkAnnotation(new Rectangle(100, 560, 400, 50)).setDestination(
                PdfExplicitDestination.createFit(page1FirstDocument)).setBorder(new PdfArray(new float[] {0, 0, 1})));
        page1FirstDocument.flush();
        firstDocument.close();

        PdfPage page1SecondDocument = secondDocument.addNewPage();
        page1SecondDocument.addAnnotation(new PdfLinkAnnotation(new Rectangle(100, 560, 400, 50)).setDestination(
                PdfExplicitDestination.createFit(page1SecondDocument)).setBorder(new PdfArray(new float[] {0, 0, 1})));
        page1SecondDocument.flush();
        secondDocument.close();

        Assertions.assertNull(new CompareTool().compareByContent(firstPdf, secondPdf, destinationFolder));
        Assertions.assertFalse(new File(firstPdf).exists());
        Assertions.assertFalse(new File(secondPdf).exists());
    }

    @Test
    public void memoryFirstWriterCmpMissingTest() throws IOException {
        String firstPdf = destinationFolder + "memoryFirstWriterCmpMissingTest.pdf";
        String secondPdf = destinationFolder + "cmp_memoryFirstWriterCmpMissingTest.pdf";
        PdfDocument firstDocument = new PdfDocument(CompareTool.createTestPdfWriter(firstPdf));

        PdfPage page1FirstDocument = firstDocument.addNewPage();
        page1FirstDocument.addAnnotation(new PdfLinkAnnotation(new Rectangle(100, 560, 400, 50)).setDestination(
                PdfExplicitDestination.createFit(page1FirstDocument)).setBorder(new PdfArray(new float[] {0, 0, 1})));
        page1FirstDocument.flush();
        firstDocument.close();

        Assertions.assertThrows(IOException.class,
                () -> new CompareTool().compareByContent(firstPdf, secondPdf, destinationFolder));
        Assertions.assertTrue(new File(firstPdf).exists());
    }

    @Test
    public void dumpMemoryFirstWriterOnDiskTest() throws InterruptedException, IOException {
        String firstPdf = destinationFolder + "dumpMemoryFirstWriterOnDiskTest.pdf";
        String secondPdf = destinationFolder + "dumpMemoryFirstWriterOnDiskTest2.pdf";
        PdfDocument firstDocument = new PdfDocument(CompareTool.createTestPdfWriter(firstPdf));
        PdfDocument secondDocument = new PdfDocument(CompareTool.createTestPdfWriter(secondPdf));

        PdfPage page1FirstDocument = firstDocument.addNewPage();
        page1FirstDocument.addAnnotation(new PdfLinkAnnotation(new Rectangle(100, 560, 400, 50)).setDestination(
                PdfExplicitDestination.createFit(page1FirstDocument)).setBorder(new PdfArray(new float[] {0, 0, 1})));
        page1FirstDocument.flush();
        firstDocument.close();

        PdfPage page1SecondDocument = secondDocument.addNewPage();
        page1SecondDocument.addAnnotation(new PdfLinkAnnotation(new Rectangle(100, 560, 260, 25)).setDestination(
                PdfExplicitDestination.createFit(page1SecondDocument)).setBorder(new PdfArray(new float[] {0, 0, 1})));
        page1SecondDocument.flush();
        secondDocument.close();

        Assertions.assertNotNull(new CompareTool().compareByContent(firstPdf, secondPdf, destinationFolder));
        Assertions.assertTrue(new File(firstPdf).exists());
        Assertions.assertTrue(new File(secondPdf).exists());
    }

    @Test
    public void cleanupTest() throws IOException {
        CompareTool.createTestPdfWriter(destinationFolder + "cleanupTest/cleanupTest.pdf");
        Assertions.assertNotNull(MemoryFirstPdfWriter.get(destinationFolder + "cleanupTest/cleanupTest.pdf"));

        Assertions.assertThrows(IllegalArgumentException.class, () -> CompareTool.cleanup(null));

        CompareTool.cleanup(destinationFolder + "cleanupTest");
        Assertions.assertNull(MemoryFirstPdfWriter.get(destinationFolder + "cleanupTest/cleanupTest.pdf"));
    }
}
