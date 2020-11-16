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
public class PdfViewerPreferencesUnitTest extends ExtendedITextTest {
    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void printScalingIsNullTest() {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException.expectMessage(KernelExceptionMessageConstant.PRINT_SCALING_ENFORCE_ENTRY_INVALID);

        PdfViewerPreferences preferences = new PdfViewerPreferences();
        PdfName pdfName = PdfName.PrintScaling;
        PdfArray pdfArray = new PdfArray(pdfName);
        preferences.setEnforce(pdfArray);
    }
}
