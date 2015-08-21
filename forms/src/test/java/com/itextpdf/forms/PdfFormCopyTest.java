package com.itextpdf.forms;

import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfReader;
import com.itextpdf.core.pdf.PdfWriter;
import com.itextpdf.core.testutils.CompareTool;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class PdfFormCopyTest {

    static final public String sourceFolder = "./src/test/resources/com/itextpdf/forms/PdfFormFieldsCopyTest/";
    static final public String destinationFolder = "./target/test/com/itextpdf/forms/PdfFormFieldsCopyTest/";

    @BeforeClass
    static public void beforeClass() {
        new File(destinationFolder).mkdirs();
    }

    @Test
    public void copyFieldsTest01() throws IOException, InterruptedException {
        String srcFilename1 = sourceFolder + "appearances1.pdf";
        String srcFilename2 = sourceFolder + "fieldsOn2-sPage.pdf";
        String srcFilename3 = sourceFolder + "fieldsOn3-sPage.pdf";

        String filename = destinationFolder + "copyFields01.pdf";

        PdfDocument doc1 = new PdfDocument(new PdfReader(new FileInputStream(srcFilename1)));
        PdfDocument doc2 = new PdfDocument(new PdfReader(new FileInputStream(srcFilename2)));
        PdfDocument doc3 = new PdfDocument(new PdfReader(new FileInputStream(srcFilename3)));
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new FileOutputStream(filename)));

        doc3.copyPages(1, doc3.getNumOfPages(), pdfDoc, new PdfPageFormCopier());
        doc2.copyPages(1, doc2.getNumOfPages(), pdfDoc, new PdfPageFormCopier());
        doc1.copyPages(1, doc1.getNumOfPages(), pdfDoc, new PdfPageFormCopier());

        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(filename, sourceFolder + "cmp_copyFields01.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void copyFieldsTest02() throws IOException, InterruptedException {
        String srcFilename = sourceFolder + "hello_with_comments.pdf";

        String filename = destinationFolder + "copyFields02.pdf";

        PdfDocument doc1 = new PdfDocument(new PdfReader(new FileInputStream(srcFilename)));
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new FileOutputStream(filename)));

        doc1.copyPages(1, doc1.getNumOfPages(), pdfDoc, new PdfPageFormCopier());

        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(filename, sourceFolder + "cmp_copyFields02.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void copyFieldsTest03() throws IOException, InterruptedException {
        String srcFilename = sourceFolder + "hello2_with_comments.pdf";

        String filename = destinationFolder + "copyFields03.pdf";

        PdfDocument doc1 = new PdfDocument(new PdfReader(new FileInputStream(srcFilename)));
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new FileOutputStream(filename)));

        doc1.copyPages(1, doc1.getNumOfPages(), pdfDoc, new PdfPageFormCopier());

        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(filename, sourceFolder + "cmp_copyFields03.pdf", destinationFolder, "diff_"));
    }

    @Test(timeout = 60000)
    public void largeFilePerformanceTest() throws IOException, InterruptedException {
        String srcFilename1 = sourceFolder + "frontpage.pdf";
        String srcFilename2 = sourceFolder + "largeFile.pdf";

        String filename = destinationFolder + "copyLargeFile.pdf";

        long timeStart = System.nanoTime();

        PdfDocument doc1 = new PdfDocument(new PdfReader(new FileInputStream(srcFilename1)));
        PdfDocument doc2 = new PdfDocument(new PdfReader(new FileInputStream(srcFilename2)));
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new FileOutputStream(filename)));

        doc1.copyPages(1, doc1.getNumOfPages(), pdfDoc, new PdfPageFormCopier());
        doc2.copyPages(1, doc2.getNumOfPages(), pdfDoc, new PdfPageFormCopier());

        pdfDoc.close();

        System.out.println(((System.nanoTime() - timeStart)/1000/1000));

        Assert.assertNull(new CompareTool().compareByContent(filename, sourceFolder + "cmp_copyLargeFile.pdf", destinationFolder, "diff_"));
    }
}
