package com.itextpdf.kernel.pdf;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class PdfStreamUnitTest extends ExtendedITextTest {
    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void cannotCreatePdfStreamWithoutDocumentTest() {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException.expectMessage(
                KernelExceptionMessageConstant.CANNOT_CREATE_PDFSTREAM_BY_INPUT_STREAM_WITHOUT_PDF_DOCUMENT);

        PdfStream pdfStream = new PdfStream(null, null, 1);
    }

    @Test
    public void setDataToPdfStreamWithInputStreamTest() {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException.expectMessage(
                KernelExceptionMessageConstant.CANNOT_SET_DATA_TO_PDF_STREAM_WHICH_WAS_CREATED_BY_INPUT_STREAM);

        InputStream inputStream = new ByteArrayInputStream(new byte[] {});
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfStream pdfStream = new PdfStream(pdfDocument, inputStream, 1);
        pdfStream.setData(new byte[] {}, true);
    }
}
