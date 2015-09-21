package com.itextpdf.utils;

import com.itextpdf.basics.LogMessageConstant;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfReader;
import com.itextpdf.core.pdf.PdfWriter;
import com.itextpdf.core.testutils.CompareTool;
import com.itextpdf.core.testutils.annotations.type.IntegrationTest;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.text.DocumentException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.*;
import java.util.ArrayList;

@Category(IntegrationTest.class)
public class PdfMergerTest extends ExtendedITextTest{

    static final public String sourceFolder = "./src/test/resources/com/itextpdf/utils/PdfMergerTest/";
    static final public String destinationFolder = "./target/test/com/itextpdf/utils/PdfMergerTest/";

    @BeforeClass
    static public void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void mergeDocumentTest01() throws IOException, InterruptedException, DocumentException {
        String filename = sourceFolder + "courierTest.pdf";
        String filename1 = sourceFolder + "helveticaTest.pdf";
        String filename2 = sourceFolder + "timesRomanTest.pdf";
        String resultFile = destinationFolder+"mergedResult01.pdf";

        PdfReader reader = new PdfReader(new FileInputStream(filename));
        PdfReader reader1 = new PdfReader(new FileInputStream(filename1));
        PdfReader reader2 = new PdfReader(new FileInputStream(filename2));

        FileOutputStream fos1 = new FileOutputStream(resultFile);
        PdfWriter writer1 = new PdfWriter(fos1);
        PdfDocument pdfDoc = new PdfDocument(reader);
        PdfDocument pdfDoc1 = new PdfDocument(reader1);
        PdfDocument pdfDoc2 = new PdfDocument(reader2);
        PdfDocument pdfDoc3 = new PdfDocument(writer1);

        PdfMerger merger = new PdfMerger(pdfDoc3);
        merger.addPages(pdfDoc, 1, 1);
        pdfDoc.close();
        merger.addPages(pdfDoc1, 1, 1);
        pdfDoc1.close();
        merger.addPages(pdfDoc2, 1, 1);
        pdfDoc2.close();

        merger.merge();
        pdfDoc3.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(resultFile, sourceFolder + "cmp_mergedResult01.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
     public void mergeDocumentTest02() throws IOException, InterruptedException, DocumentException {
        String filename = sourceFolder + "doc1.pdf";
        String filename1 = sourceFolder + "doc2.pdf";
        String filename2 = sourceFolder + "doc3.pdf";
        String resultFile = destinationFolder+"mergedResult02.pdf";

        PdfReader reader = new PdfReader(new FileInputStream(filename));
        PdfReader reader1 = new PdfReader(new FileInputStream(filename1));
        PdfReader reader2 = new PdfReader(new FileInputStream(filename2));

        FileOutputStream fos1 = new FileOutputStream(resultFile);
        PdfWriter writer1 = new PdfWriter(fos1);
        PdfDocument pdfDoc = new PdfDocument(reader);
        PdfDocument pdfDoc1 = new PdfDocument(reader1);
        PdfDocument pdfDoc2 = new PdfDocument(reader2);
        PdfDocument pdfDoc3 = new PdfDocument(writer1);
        PdfMerger merger = new PdfMerger(pdfDoc3);

        merger.addPages(pdfDoc, 1, 1);
        pdfDoc.close();
        merger.addPages(pdfDoc1, 1, 1);
        pdfDoc1.close();
        merger.addPages(pdfDoc2, 1, 1);
        pdfDoc2.close();

        merger.merge();
        pdfDoc3.close();
        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(resultFile, sourceFolder + "cmp_mergedResult02.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    @LogMessage(messages = {LogMessageConstant.SOURCE_DOCUMENT_HAS_ACROFORM_DICTIONARY})
    public void mergeDocumentTest03() throws IOException, InterruptedException, DocumentException {
        String filename = sourceFolder + "pdf_open_parameters.pdf";
        String filename1 = sourceFolder + "iphone_user_guide.pdf";
        String resultFile = destinationFolder+"mergedResult03.pdf";

        PdfReader reader = new PdfReader(new FileInputStream(filename));
        PdfReader reader1 = new PdfReader(new FileInputStream(filename1));

        FileOutputStream fos1 = new FileOutputStream(resultFile);
        PdfWriter writer1 = new PdfWriter(fos1);
        PdfDocument pdfDoc = new PdfDocument(reader);
        PdfDocument pdfDoc1 = new PdfDocument(reader1);
        PdfDocument pdfDoc3 = new PdfDocument(writer1);
        pdfDoc3.setTagged();

        PdfMerger merger = new PdfMerger(pdfDoc3);
        merger.addPages(pdfDoc, 2, 2);
        pdfDoc.close();
        merger.addPages(pdfDoc1, 7, 8);
        pdfDoc1.close();

        merger.merge();
        pdfDoc3.close();
        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(resultFile, sourceFolder + "cmp_mergedResult03.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    @LogMessage(messages = {LogMessageConstant.SOURCE_DOCUMENT_HAS_ACROFORM_DICTIONARY})
    public void mergeDocumentTest04() throws IOException, InterruptedException, DocumentException {
        String filename = sourceFolder + "pdf_open_parameters.pdf";
        String filename1 = sourceFolder + "iphone_user_guide.pdf";
        String resultFile = destinationFolder+"mergedResult04.pdf";

        PdfReader reader = new PdfReader(new FileInputStream(filename));
        PdfReader reader1 = new PdfReader(new FileInputStream(filename1));

        FileOutputStream fos1 = new FileOutputStream(resultFile);
        PdfWriter writer1 = new PdfWriter(fos1);
        PdfDocument pdfDoc = new PdfDocument(reader);
        PdfDocument pdfDoc1 = new PdfDocument(reader1);
        PdfDocument pdfDoc3 = new PdfDocument(writer1);
        pdfDoc3.setTagged();

        PdfMerger merger = new PdfMerger(pdfDoc3);
        ArrayList<Integer> pages = new ArrayList<Integer>();
        pages.add(3);
        pages.add(2);
        pages.add(1);
        merger.addPages(pdfDoc, pages);
        pdfDoc.close();

        ArrayList<Integer> pages1 = new ArrayList<Integer>();
        pages1.add(5);
        pages1.add(9);
        pages1.add(4);
        pages1.add(3);
        merger.addPages(pdfDoc1, pages1);
        pdfDoc1.close();

        merger.merge();
        pdfDoc3.close();
        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(resultFile, sourceFolder + "cmp_mergedResult04.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }
}
