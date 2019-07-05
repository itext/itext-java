/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
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

import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

@Category(IntegrationTest.class)
public class CompareToolTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/utils/CompareToolTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/utils/CompareToolTest/";

    @BeforeClass
    public static void setUp() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void compareToolErrorReportTest01() throws InterruptedException, IOException, ParserConfigurationException, SAXException {
        CompareTool compareTool = new CompareTool();
        compareTool.setCompareByContentErrorsLimit(10);
        compareTool.setGenerateCompareByContentXmlReport(true);
        String outPdf = sourceFolder + "simple_pdf.pdf";
        String cmpPdf = sourceFolder + "cmp_simple_pdf.pdf";
        String result = compareTool.compareByContent(outPdf, cmpPdf, destinationFolder);
        System.out.println(result);
        Assert.assertNotNull("CompareTool must return differences found between the files", result);
        // Comparing the report to the reference one.
        Assert.assertTrue("CompareTool report differs from the reference one", compareTool.compareXmls(destinationFolder + "simple_pdf.report.xml", sourceFolder + "cmp_report01.xml"));
    }

    @Test
    public void compareToolErrorReportTest02() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        CompareTool compareTool = new CompareTool();
        compareTool.setCompareByContentErrorsLimit(10);
        compareTool.setGenerateCompareByContentXmlReport(true);
        String outPdf = sourceFolder + "tagged_pdf.pdf";
        String cmpPdf = sourceFolder + "cmp_tagged_pdf.pdf";
        String result = compareTool.compareByContent(outPdf, cmpPdf, destinationFolder);
        System.out.println(result);
        Assert.assertNotNull("CompareTool must return differences found between the files", result);
        // Comparing the report to the reference one.
        Assert.assertTrue("CompareTool report differs from the reference one", compareTool.compareXmls(destinationFolder + "tagged_pdf.report.xml", sourceFolder + "cmp_report02.xml"));
    }

    @Test
    public void compareToolErrorReportTest03() throws InterruptedException, IOException, ParserConfigurationException, SAXException {
        CompareTool compareTool = new CompareTool();
        compareTool.setCompareByContentErrorsLimit(10);
        compareTool.setGenerateCompareByContentXmlReport(true);
        String outPdf = sourceFolder + "screenAnnotation.pdf";
        String cmpPdf = sourceFolder + "cmp_screenAnnotation.pdf";
        String result = compareTool.compareByContent(outPdf, cmpPdf, destinationFolder);
        System.out.println(result);
        Assert.assertNotNull("CompareTool must return differences found between the files", result);
        // Comparing the report to the reference one.
        Assert.assertTrue("CompareTool report differs from the reference one", compareTool.compareXmls(destinationFolder + "screenAnnotation.report.xml", sourceFolder + "cmp_report03.xml"));
    }


    @Test
    // Test space in name
    public void compareToolErrorReportTest04() throws InterruptedException, IOException, ParserConfigurationException, SAXException {
        CompareTool compareTool = new CompareTool();
        compareTool.setCompareByContentErrorsLimit(10);
        compareTool.setGenerateCompareByContentXmlReport(true);
        String outPdf = sourceFolder + "simple_pdf.pdf";
        String cmpPdf = sourceFolder + "cmp_simple_pdf_with_space .pdf";
        String result = compareTool.compareByContent(outPdf, cmpPdf, destinationFolder);
        System.out.println(result);
        Assert.assertNotNull("CompareTool must return differences found between the files", result);
        // Comparing the report to the reference one.
        Assert.assertTrue("CompareTool report differs from the reference one", compareTool.compareXmls(destinationFolder + "simple_pdf.report.xml", sourceFolder + "cmp_report01.xml"));

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

}
