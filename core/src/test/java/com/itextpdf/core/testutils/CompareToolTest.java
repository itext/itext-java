package com.itextpdf.core.testutils;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class CompareToolTest {

    static final public String sourceFolder = "./src/test/resources/com/itextpdf/core/testutils/CompareToolTest/";
    static final public String destinationFolder = "./target/test/com/itextpdf/core/testutils/CompareToolTest/";

    public void setUp() {
        new File(destinationFolder).mkdirs();
    }

    @Test
    public void test() throws InterruptedException, IOException {
        CompareTool compareTool = new CompareTool();
        compareTool.setCompareByContentErrorsLimit(10);
        compareTool.setGenerateCompareByContentXmlReport(true);
        String outPdf = sourceFolder + "simple_pdf.pdf";
        String cmpPdf = sourceFolder + "cmp_simple_pdf.pdf";
        String result = compareTool.compareByContent(outPdf, cmpPdf, destinationFolder, "difference");
        System.out.println(result);
        Assert.assertNotNull(result);
    }

    @Test
    public void test2() throws IOException, InterruptedException {
        CompareTool compareTool = new CompareTool();
        compareTool.setCompareByContentErrorsLimit(10);
        compareTool.setGenerateCompareByContentXmlReport(true);
        String outPdf = sourceFolder + "tagged_pdf.pdf";
        String cmpPdf = sourceFolder + "cmp_tagged_pdf.pdf";
        String result = compareTool.compareByContent(outPdf, cmpPdf, destinationFolder, "difference");
        System.out.println(result);
        Assert.assertNotNull(result);
    }

    @Test
    public void test3() throws InterruptedException, IOException {
        CompareTool compareTool = new CompareTool();
        compareTool.setCompareByContentErrorsLimit(10);
        compareTool.setGenerateCompareByContentXmlReport(true);
        String outPdf = sourceFolder + "screenAnnotation.pdf";
        String cmpPdf = sourceFolder + "cmp_screenAnnotation.pdf";
        String result = compareTool.compareByContent(outPdf, cmpPdf, destinationFolder, "difference");
        System.out.println(result);
        Assert.assertNotNull(result);
    }
}
