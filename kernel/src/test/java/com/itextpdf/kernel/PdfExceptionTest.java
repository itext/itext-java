package com.itextpdf.kernel;

import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class PdfExceptionTest extends ExtendedITextTest {
    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void whenCreatedPdfExceptionWrapsCauseWithUnknownExceptionMessageTest() {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException.expectMessage(KernelExceptionMessageConstant.UNKNOWN_PDF_EXCEPTION);

        throw new PdfException(new Exception("itext"));
    }
}
