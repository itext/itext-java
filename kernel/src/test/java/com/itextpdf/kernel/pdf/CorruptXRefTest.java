package com.itextpdf.kernel.pdf;

import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;

import static com.itextpdf.test.ITextTest.createDestinationFolder;

/**
 * This test checks correct handling of pdf documents with (slightly) corrupt XREF table
 * xref
 * 0 30
 * 0000000000 65535 f
 * 0000000000 65535 f
 */
@Category(IntegrationTest.class)
public class CorruptXRefTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/CorruptXRefTest/";
    public static final String outputFolder = "./target/test/com/itextpdf/kernel/pdf/CorruptXRefTest/";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(outputFolder);
    }

    @Test
    public void readPdfWithCorruptXRef() throws IOException, InterruptedException {
        String cmpFile = sourceFolder + "cmp_docOut.pdf";
        String outputFile = outputFolder + "docOut.pdf";
        String inputFile = sourceFolder + "docIn.pdf";
        PdfWriter writer = new PdfWriter(outputFile);

        PdfReader reader = new PdfReader(inputFile);

        PdfDocument inputPdfDocument = new PdfDocument(reader);
        PdfDocument outputPdfDocument = new PdfDocument(writer);

        int lastPage = inputPdfDocument.getNumberOfPages();
        inputPdfDocument.copyPagesTo(lastPage, lastPage, outputPdfDocument);

        inputPdfDocument.close();
        outputPdfDocument.close();

        Assert.assertNull(new CompareTool().compareByContent(outputFolder + "docOut.pdf", cmpFile, outputFolder, "diff_"));

    }
}
