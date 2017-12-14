package com.itextpdf.layout;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class NewLineTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/NewLineTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/layout/NewLineTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void r() throws IOException, InterruptedException {
        test("\r", "r.pdf");
    }

    @Test
    public void n() throws IOException, InterruptedException {
        test("\n", "n.pdf");
    }

    @Test
    public void rn() throws IOException, InterruptedException {
        test("\r\n", "rn.pdf");
    }

    @Test
    public void rrn() throws IOException, InterruptedException {
        test("\r\r\n", "rrn.pdf");
    }

    @Test
    public void nn() throws IOException, InterruptedException {
        test("\n\n", "nn.pdf");
    }

    @Test
    public void rnn() throws IOException, InterruptedException {
        test("\r\n\n", "rnn.pdf");
    }

    @Test
    public void rnrn() throws IOException, InterruptedException {
        test("\r\n\r\n", "rnrn.pdf");
    }

    private void test(String newlineCharacters, String fileName) throws IOException, InterruptedException {
        String outFileName = destinationFolder + fileName;
        String cmpFileName = sourceFolder + "cmp_" + fileName;
        String diffPrefix = "diff_" + fileName + "_";

        PdfDocument pdf = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName), new WriterProperties().setCompressionLevel(0)));
        Document document = new Document(pdf);

        Paragraph paragraph = new Paragraph().add(
                "This line is before." + newlineCharacters + "This line is after.");
                
        document.add(paragraph);
        document.close();

        Assert.assertNull(new CompareTool().compareVisually(outFileName, cmpFileName, destinationFolder, diffPrefix));
    }
}