package com.itextpdf.kernel.utils;

import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runners.MethodSorters;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

@FixMethodOrder(MethodSorters.DEFAULT)
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
        String result = compareTool.compareByContent(outPdf, cmpPdf, destinationFolder, "difference");
        System.out.println(result);
        Assert.assertNotNull("CompareTool must return differences found between the files", result);
        // Comparing the report to the reference one.
        Assert.assertTrue("CompareTool report differs from the reference one", compareTool.compareXmls(sourceFolder + "cmp_report01.xml", destinationFolder + "simple_pdf.report.xml"));
    }

    @Test
    public void compareToolErrorReportTest02() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        CompareTool compareTool = new CompareTool();
        compareTool.setCompareByContentErrorsLimit(10);
        compareTool.setGenerateCompareByContentXmlReport(true);
        String outPdf = sourceFolder + "tagged_pdf.pdf";
        String cmpPdf = sourceFolder + "cmp_tagged_pdf.pdf";
        String result = compareTool.compareByContent(outPdf, cmpPdf, destinationFolder, "difference");
        System.out.println(result);
        Assert.assertNotNull("CompareTool must return differences found between the files", result);
        // Comparing the report to the reference one.
        Assert.assertTrue("CompareTool report differs from the reference one", compareTool.compareXmls(sourceFolder + "cmp_report02.xml", destinationFolder + "tagged_pdf.report.xml"));
    }

    @Test
    public void compareToolErrorReportTest03() throws InterruptedException, IOException, ParserConfigurationException, SAXException {
        CompareTool compareTool = new CompareTool();
        compareTool.setCompareByContentErrorsLimit(10);
        compareTool.setGenerateCompareByContentXmlReport(true);
        String outPdf = sourceFolder + "screenAnnotation.pdf";
        String cmpPdf = sourceFolder + "cmp_screenAnnotation.pdf";
        String result = compareTool.compareByContent(outPdf, cmpPdf, destinationFolder, "difference");
        System.out.println(result);
        Assert.assertNotNull("CompareTool must return differences found between the files", result);
        // Comparing the report to the reference one.
        Assert.assertTrue("CompareTool report differs from the reference one", compareTool.compareXmls(sourceFolder + "cmp_report03.xml", destinationFolder + "screenAnnotation.report.xml"));
    }
}
