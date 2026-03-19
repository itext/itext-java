package com.itextpdf.kernel.pdf.colorspace;

import com.itextpdf.kernel.pdf.colorspace.PdfDeviceCs.Cmyk;
import com.itextpdf.kernel.pdf.colorspace.PdfDeviceCs.Rgb;
import com.itextpdf.kernel.pdf.colorspace.PdfSpecialCs.Separation;
import com.itextpdf.kernel.pdf.function.PdfType0Function;
import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("UnitTest")
public class SeparationTest extends ExtendedITextTest {

    @Test
    public void getSeparationColorNameTest() {
        double[] domain = {-1, 2};
        int[] size = {2};
        double[] range = {-1, 2, -3, 6, 0, 3};
        int bitsPerSample = 1;
        int order = 1;
        byte[] samples = {0x2d, 0x2d, 0x2d, 0x2d, 0x2d, 0x2d, 0x2d, 0x2d};
        PdfType0Function pdfFunction = new PdfType0Function(domain, size, range, order,
                null, null, bitsPerSample, samples);
        Separation sut = new Separation("test1", new Rgb(), pdfFunction);

        range = new double[] {-1, 2, -3, 6, 0, 3, -2 , 7};
        pdfFunction = new PdfType0Function(domain, size, range, order,
                null, null, bitsPerSample, samples);
        Assertions.assertEquals("test1", sut.getSeparationColorName().getValue());
        sut = new Separation("test2", new Cmyk(), pdfFunction);
        Assertions.assertEquals("test2", sut.getSeparationColorName().getValue());
    }

    @Test
    public void getNameTest() {
        double[] domain = {-1, 2};
        int[] size = {2};
        double[] range = {-1, 2, -3, 6, 0, 3};
        int bitsPerSample = 1;
        int order = 1;
        byte[] samples = {0x2d, 0x2d, 0x2d, 0x2d, 0x2d, 0x2d, 0x2d, 0x2d};
        PdfType0Function pdfFunction = new PdfType0Function(domain, size, range, order,
                null, null, bitsPerSample, samples);
        Separation sut = new Separation("test1", new Rgb(), pdfFunction);

        range = new double[] {-1, 2, -3, 6, 0, 3, -2 , 7};
        pdfFunction = new PdfType0Function(domain, size, range, order,
                null, null, bitsPerSample, samples);
        Assertions.assertEquals("test1", sut.getName().getValue());
        sut = new Separation("test2", new Cmyk(), pdfFunction);
        Assertions.assertEquals("test2", sut.getName().getValue());
    }

    @Test
    public void getBaseCsTest() {
        double[] domain = {-1, 2};
        int[] size = {2};
        double[] range = {-1, 2, -3, 6, 0, 3};
        int bitsPerSample = 1;
        int order = 1;
        byte[] samples = {0x2d, 0x2d, 0x2d, 0x2d, 0x2d, 0x2d, 0x2d, 0x2d};

        PdfType0Function pdfFunction = new PdfType0Function(domain, size, range, order,
                null, null, bitsPerSample, samples);
        Separation sut = new Separation("test1", new Rgb(), pdfFunction);
        Assertions.assertEquals(Rgb.class, sut.getBaseCs().getClass());

        range = new double[] {-1, 2, -3, 6, 0, 3, -2 , 7};
        pdfFunction = new PdfType0Function(domain, size, range, order,
                null, null, bitsPerSample, samples);
        sut = new Separation("test2", new Cmyk(), pdfFunction);
        Assertions.assertEquals(Cmyk.class, sut.getBaseCs().getClass());
    }

    @Test
    public void getNumberOfComponentsTest() {
        double[] domain = {-1, 2};
        int[] size = {2};
        double[] range = {-1, 2, -3, 6, 0, 3};
        int bitsPerSample = 8;
        int order = 1;
        byte[] samples = {0x2d, 0x2d, 0x2d, 0x2d, 0x2d, 0x2d, 0x2d, 0x2d};

        PdfType0Function pdfFunction = new PdfType0Function(domain, size, range, order,
                null, null, bitsPerSample, samples);
        Separation sut = new Separation("test1", new Rgb(), pdfFunction);
        Assertions.assertEquals(1, sut.getNumberOfComponents());

        range = new double[] {-1, 2, -3, 6, 0, 3, -2 , 7};
        pdfFunction = new PdfType0Function(domain, size, range, order,
                null, null, bitsPerSample, samples);
        sut = new Separation("test2", new Cmyk(), pdfFunction);
        Assertions.assertEquals(1, sut.getNumberOfComponents());
    }
}
