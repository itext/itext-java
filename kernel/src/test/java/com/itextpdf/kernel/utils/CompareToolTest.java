/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.kernel.utils;

import com.itextpdf.io.IoExceptionMessage;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.util.GhostscriptHelper;
import com.itextpdf.io.util.SystemUtil;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.navigation.PdfExplicitDestination;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.xml.sax.SAXException;

@Category(IntegrationTest.class)
public class CompareToolTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/utils/CompareToolTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/utils/CompareToolTest/";

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @BeforeClass
    public static void setUp() {
        createOrClearDestinationFolder(destinationFolder);
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
        Assert.assertNotNull("CompareTool must return differences found between the files", result);
        Assert.assertTrue(result.contains("differs on page [1, 2]."));
        // Comparing the report to the reference one.
        Assert.assertTrue("CompareTool report differs from the reference one", compareTool
                .compareXmls(destinationFolder + "simple_pdf.report.xml", sourceFolder + "cmp_report01.xml"));
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
        Assert.assertNotNull("CompareTool must return differences found between the files", result);
        Assert.assertTrue(result.contains("Compare by content fails. No visual differences"));
        // Comparing the report to the reference one.
        Assert.assertTrue("CompareTool report differs from the reference one", compareTool
                .compareXmls(destinationFolder + "tagged_pdf.report.xml", sourceFolder + "cmp_report02.xml"));
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
        Assert.assertNotNull("CompareTool must return differences found between the files", result);
        Assert.assertTrue(result.contains("Compare by content fails. No visual differences"));
        // Comparing the report to the reference one.
        Assert.assertTrue("CompareTool report differs from the reference one", compareTool
                .compareXmls(destinationFolder + "screenAnnotation.report.xml", sourceFolder + "cmp_report03.xml"));
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
        Assert.assertNotNull("CompareTool must return differences found between the files", result);
        Assert.assertTrue(result.contains("differs on page [1, 2]."));
        // Comparing the report to the reference one.
        Assert.assertTrue("CompareTool report differs from the reference one", compareTool
                .compareXmls(destinationFolder + "simple_pdf.report.xml", sourceFolder + "cmp_report01.xml"));

    }

    @Test
    public void differentProducerTest() throws IOException {
        String expectedMessage = "Document info fail. Expected: \"iText\u00ae <version> \u00a9<copyright years> iText Group NV (iText Software; licensed version)\", actual: \"iText\u00ae <version> \u00a9<copyright years> iText Group NV (AGPL-version)\"";
        String licensed = sourceFolder + "producerLicensed.pdf";
        String agpl = sourceFolder + "producerAGPL.pdf";
        Assert.assertEquals(expectedMessage, new CompareTool().compareDocumentInfo(agpl, licensed));
    }

    @Test
    public void versionReplaceTest() {
        String initial = "iText® 1.10.10-SNAPSHOT (licensed to iText) ©2000-2018 iText Group NV";
        String replacedExpected = "iText® <version> (licensed to iText) ©<copyright years> iText Group NV";
        Assert.assertEquals(replacedExpected, new CompareTool().convertProducerLine(initial));
    }

    @Test
    public void gsEnvironmentVariableIsNotSpecifiedExceptionTest() throws IOException, InterruptedException {
        String outPdf = sourceFolder + "simple_pdf.pdf";
        String cmpPdf = sourceFolder + "cmp_simple_pdf.pdf";
        new CompareTool(null, null).compareVisually(outPdf, cmpPdf, destinationFolder, "diff_");
        Assert.assertTrue(new File(destinationFolder + "diff_1.png").exists());
    }

    @Test
    public void gsEnvironmentVariableSpecifiedIncorrectlyTest() throws IOException, InterruptedException {
        junitExpectedException.expect(CompareTool.CompareToolExecutionException.class);
        junitExpectedException.expectMessage(IoExceptionMessage.GS_ENVIRONMENT_VARIABLE_IS_NOT_SPECIFIED);
        String outPdf = sourceFolder + "simple_pdf.pdf";
        String cmpPdf = sourceFolder + "cmp_simple_pdf.pdf";
        new CompareTool("unspecified", null).compareVisually(outPdf, cmpPdf, destinationFolder, "diff_");
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
        Assert.assertFalse(result.contains(IoExceptionMessage.COMPARE_COMMAND_IS_NOT_SPECIFIED));
        Assert.assertTrue(new File(destinationFolder + "diff_1.png").exists());
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoExceptionMessage.COMPARE_COMMAND_SPECIFIED_INCORRECTLY)})
    public void compareCommandSpecifiedIncorrectlyTest() throws IOException, InterruptedException {
        String outPdf = sourceFolder + "simple_pdf.pdf";
        String cmpPdf = sourceFolder + "cmp_simple_pdf.pdf";
        String gsExec = SystemUtil.getPropertyOrEnvironmentVariable(GhostscriptHelper.GHOSTSCRIPT_ENVIRONMENT_VARIABLE);
        if (gsExec == null) {
            gsExec = SystemUtil.getPropertyOrEnvironmentVariable("gsExec");
        }
        String result = new CompareTool(gsExec, "unspecified")
                .compareVisually(outPdf, cmpPdf, destinationFolder, "diff_");
        Assert.assertTrue(result.contains(IoExceptionMessage.COMPARE_COMMAND_SPECIFIED_INCORRECTLY));
    }

    @Test
    public void compareVisuallyDiffTestTest() throws IOException, InterruptedException {
        String outPdf = sourceFolder + "compareVisuallyDiffTestTest1.pdf";
        String cmpPdf = sourceFolder + "compareVisuallyDiffTestTest2.pdf";
        String result = new CompareTool().compareVisually(outPdf, cmpPdf, destinationFolder, "diff_");
        System.out.println("\nRESULT:\n" + result);
        Assert.assertTrue(result.contains("differs on page [1, 2]."));
        Assert.assertTrue(new File(destinationFolder + "diff_1.png").exists());
        Assert.assertTrue(new File(destinationFolder + "diff_2.png").exists());
    }

    @Test
    public void compareDiffFilesWithSameLinkAnnotationTest() throws IOException {
        String firstPdf = destinationFolder + "firstPdf.pdf";
        String secondPdf = destinationFolder + "secondPdf.pdf";
        PdfDocument firstDocument = new PdfDocument(new PdfWriter(firstPdf));

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

        PdfDocument secondDocument = new PdfDocument(new PdfWriter(secondPdf));
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

        Assert.assertNull(new CompareTool().compareLinkAnnotations(firstPdf, secondPdf));
    }

    @Test
    public void compareFilesWithDiffLinkAnnotationTest() throws IOException {
        String firstPdf = destinationFolder + "outPdf.pdf";
        String secondPdf = destinationFolder + "secondPdf.pdf";
        PdfDocument firstDocument = new PdfDocument(new PdfWriter(firstPdf));
        PdfDocument secondDocument = new PdfDocument(new PdfWriter(secondPdf));

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

        Assert.assertNotNull(new CompareTool().compareLinkAnnotations(firstPdf, secondPdf));
    }
}
