package com.itextpdf.barcodes;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class BarcodeUnitTest extends ExtendedITextTest {

    private static final double EPS = 0.0001;

    @Test
    public void BarcodeMSIGetBarcodeSizeWithChecksumTest() {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        PdfDocument document = new PdfDocument(writer);
        document.addNewPage();
        Barcode1D barcode = new BarcodeMSI(document);
        document.close();
        barcode.setCode("123456789");
        barcode.setGenerateChecksum(true);
        Rectangle barcodeSize = barcode.getBarcodeSize();
        Assert.assertEquals(33.656, barcodeSize.getHeight(), EPS);
        Assert.assertEquals(101.6, barcodeSize.getWidth(), EPS);
    }

    @Test
    public void BarcodeMSIGetBarcodeSizeWithoutChecksumTest() {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        PdfDocument document = new PdfDocument(writer);
        document.addNewPage();
        Barcode1D barcode = new BarcodeMSI(document);
        document.close();
        barcode.setCode("123456789");
        barcode.setGenerateChecksum(false);
        Rectangle barcodeSize = barcode.getBarcodeSize();
        Assert.assertEquals(33.656, barcodeSize.getHeight(), EPS);
        Assert.assertEquals(92.0, barcodeSize.getWidth(), EPS);
    }
}
