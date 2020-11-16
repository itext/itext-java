package com.itextpdf.kernel.pdf;

import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class PdfArrayUnitTest extends ExtendedITextTest {
    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void cannotConvertArrayOfPdfStringsToArrayOfBooleansTest() {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException.expectMessage(KernelExceptionMessageConstant.CANNOT_CONVERT_PDF_ARRAY_TO_AN_ARRAY_OF_BOOLEANS);

        PdfArray pdfArray = new PdfArray(new PdfString(""));
        pdfArray.toBooleanArray();
    }

    @Test
    public void cannotConvertArrayOfPdfStringsToDoubleArrayTest() {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException.expectMessage(KernelExceptionMessageConstant.CANNOT_CONVERT_PDF_ARRAY_TO_DOUBLE_ARRAY);

        PdfArray pdfArray = new PdfArray(new PdfString(""));
        pdfArray.toDoubleArray();
    }

    @Test
    public void cannotConvertArrayOfPdfStringsToIntArrayTest() {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException.expectMessage(KernelExceptionMessageConstant.CANNOT_CONVERT_PDF_ARRAY_TO_INT_ARRAY);

        PdfArray pdfArray = new PdfArray(new PdfString(""));
        pdfArray.toIntArray();
    }

    @Test
    public void cannotConvertArrayOfPdfStringsToFloatArrayTest() {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException.expectMessage(KernelExceptionMessageConstant.CANNOT_CONVERT_PDF_ARRAY_TO_FLOAT_ARRAY);

        PdfArray pdfArray = new PdfArray(new PdfString(""));
        pdfArray.toFloatArray();
    }

    @Test
    public void cannotConvertArrayOfPdfStringsToLongArrayTest() {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException.expectMessage(KernelExceptionMessageConstant.CANNOT_CONVERT_PDF_ARRAY_TO_LONG_ARRAY);

        PdfArray pdfArray = new PdfArray(new PdfString(""));
        pdfArray.toLongArray();
    }

    @Test
    public void cannotConvertArrayOfPdfStringsToRectangleTest() {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException.expectMessage(KernelExceptionMessageConstant.CANNOT_CONVERT_PDF_ARRAY_TO_RECTANGLE);

        PdfArray pdfArray = new PdfArray(new PdfString(""));
        pdfArray.toRectangle();
    }
}
