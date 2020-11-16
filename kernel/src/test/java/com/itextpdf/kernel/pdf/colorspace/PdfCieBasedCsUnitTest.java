package com.itextpdf.kernel.pdf.colorspace;

import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.pdf.colorspace.PdfCieBasedCs.CalGray;
import com.itextpdf.kernel.pdf.colorspace.PdfCieBasedCs.CalRgb;
import com.itextpdf.kernel.pdf.colorspace.PdfCieBasedCs.Lab;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class PdfCieBasedCsUnitTest extends ExtendedITextTest {
    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void whitePointOfCalGrayIsIncorrectEmptyTest() {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException.expectMessage(KernelExceptionMessageConstant.WHITE_POINT_IS_INCORRECTLY_SPECIFIED);

        PdfCieBasedCs basedCs = new CalGray(new float[] {});
    }

    @Test
    public void whitePointOfCalRgbIsIncorrectEmptyTest() {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException.expectMessage(KernelExceptionMessageConstant.WHITE_POINT_IS_INCORRECTLY_SPECIFIED);

        PdfCieBasedCs basedCs = new CalRgb(new float[] {});
    }

    @Test
    public void whitePointOfLabIsIncorrectEmptyTest() {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException.expectMessage(KernelExceptionMessageConstant.WHITE_POINT_IS_INCORRECTLY_SPECIFIED);

        PdfCieBasedCs basedCs = new Lab(new float[] {});
    }

    @Test
    public void whitePointOfCalGrayIsIncorrectTooLittlePointsTest() {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException.expectMessage(KernelExceptionMessageConstant.WHITE_POINT_IS_INCORRECTLY_SPECIFIED);

        PdfCieBasedCs basedCs = new CalGray(new float[] {1, 2});
    }

    @Test
    public void whitePointOfCalRgbIsIncorrectTooLittlePointsTest() {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException.expectMessage(KernelExceptionMessageConstant.WHITE_POINT_IS_INCORRECTLY_SPECIFIED);

        PdfCieBasedCs basedCs = new CalRgb(new float[] {1, 2});
    }

    @Test
    public void whitePointOfLabIsIncorrectTooLittlePointsTest() {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException.expectMessage(KernelExceptionMessageConstant.WHITE_POINT_IS_INCORRECTLY_SPECIFIED);

        PdfCieBasedCs basedCs = new Lab(new float[] {1, 2});
    }

    @Test
    public void whitePointOfCalGrayIsIncorrectTooMuchPointsTest() {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException.expectMessage(KernelExceptionMessageConstant.WHITE_POINT_IS_INCORRECTLY_SPECIFIED);

        PdfCieBasedCs basedCs = new CalGray(new float[] {1, 2, 3, 4});
    }

    @Test
    public void whitePointOfCalRgbIsIncorrectTooMuchPointsTest() {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException.expectMessage(KernelExceptionMessageConstant.WHITE_POINT_IS_INCORRECTLY_SPECIFIED);

        PdfCieBasedCs basedCs = new CalRgb(new float[] {1, 2, 3, 4});
    }

    @Test
    public void whitePointOfLabIsIncorrectTooMuchPointsTest() {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException.expectMessage(KernelExceptionMessageConstant.WHITE_POINT_IS_INCORRECTLY_SPECIFIED);

        PdfCieBasedCs basedCs = new Lab(new float[] {1, 2, 3, 4});
    }
}
