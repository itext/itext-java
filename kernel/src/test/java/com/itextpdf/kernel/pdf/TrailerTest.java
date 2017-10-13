package com.itextpdf.kernel.pdf;

import com.itextpdf.kernel.ProductInfo;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.test.ExtendedITextTest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Michael Demey
 */
public class TrailerTest extends ExtendedITextTest {

    private ProductInfo productInfo;
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/TrailerTest/";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Before
    public void beforeTest() {
        this.productInfo = new ProductInfo("pdfProduct", 1, 0, 0, true);
    }

    @Test
    public void trailerFingerprintTest() throws IOException {
        FileOutputStream fos = new FileOutputStream(destinationFolder + "output.pdf");
        PdfDocument pdf = new PdfDocument(new PdfWriter(fos));
        pdf.registerProduct(this.productInfo);
        PdfPage page = pdf.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.beginText()
                .setFontAndSize(PdfFontFactory.createFont(), 12f)
                .showText("Hello World")
                .endText();

        pdf.close();

        Assert.assertTrue(doesTrailerContainFingerprint(new File(destinationFolder + "output.pdf"), productInfo.toString()));
    }

    private boolean doesTrailerContainFingerprint(File file, String fingerPrint) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(file, "r");

        // put the pointer at the end of the file
        raf.seek(raf.length());

        // look for startxref
        String startxref = "startxref";
        String templine = "";

        while ( ! templine.contains(startxref) ) {
            templine = (char) raf.read() + templine;
            raf.seek(raf.getFilePointer() - 2);
        }

        // look for fingerprint
        char read = ' ';
        templine = "";

        while ( read != '%' ) {
            read = (char) raf.read();
            templine = read + templine;
            raf.seek(raf.getFilePointer() - 2);
        }

        boolean output = templine.contains(fingerPrint);
        raf.close();
        return output;
    }

}