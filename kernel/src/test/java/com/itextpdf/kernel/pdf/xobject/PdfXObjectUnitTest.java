package com.itextpdf.kernel.pdf.xobject;

import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class PdfXObjectUnitTest extends ExtendedITextTest {
    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void noSubTypeProvidedTest() {
        junitExpectedException.expect(UnsupportedOperationException.class);
        junitExpectedException.expectMessage(KernelExceptionMessageConstant.UNSUPPORTED_XOBJECT_TYPE);

        PdfStream pdfStream = new PdfStream();
        PdfXObject pdfXObject = PdfXObject.makeXObject(pdfStream);
    }

    @Test
    public void unsupportedSubTypeIsSet() {
        junitExpectedException.expect(UnsupportedOperationException.class);
        junitExpectedException.expectMessage(KernelExceptionMessageConstant.UNSUPPORTED_XOBJECT_TYPE);

        PdfStream pdfStream = new PdfStream();
        pdfStream.put(PdfName.Subtype, new PdfName("Unsupported SubType"));
        PdfXObject pdfXObject = PdfXObject.makeXObject(pdfStream);
    }
}
