package com.itextpdf.kernel.pdf;

import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class PageFlushingHelperUnitTest extends ExtendedITextTest {
    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void flushingInReadingModeTest01() throws IOException {
        junitExpectedException.expect(IllegalArgumentException.class);
        junitExpectedException.expectMessage(
                KernelExceptionMessageConstant.FLUSHING_HELPER_FLUSHING_MODE_IS_NOT_FOR_DOC_READING_MODE);

        int pageToFlush = 1;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outputStream));
        pdfDocument.addNewPage();
        pdfDocument.close();
        pdfDocument = new PdfDocument(new PdfReader(new ByteArrayInputStream(outputStream.toByteArray())));
        PageFlushingHelper pageFlushingHelper = new PageFlushingHelper(pdfDocument);
        pageFlushingHelper.unsafeFlushDeep(pageToFlush);
    }

    @Test
    public void flushingInReadingModeTest02() throws IOException {
        junitExpectedException.expect(IllegalArgumentException.class);
        junitExpectedException.expectMessage(
                KernelExceptionMessageConstant.FLUSHING_HELPER_FLUSHING_MODE_IS_NOT_FOR_DOC_READING_MODE);

        int pageToFlush = 1;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outputStream));
        pdfDocument.addNewPage();
        pdfDocument.close();
        pdfDocument = new PdfDocument(new PdfReader(new ByteArrayInputStream(outputStream.toByteArray())));
        PageFlushingHelper pageFlushingHelper = new PageFlushingHelper(pdfDocument);
        pageFlushingHelper.appendModeFlush(pageToFlush);
    }
}
