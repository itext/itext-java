package com.itextpdf.kernel.pdf;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class PdfObjectStreamUnitTest extends ExtendedITextTest {
    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void cannotAddMoreObjectsThanMaxStreamSizeTest() {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException.expectMessage(KernelExceptionMessageConstant.PDF_OBJECT_STREAM_REACH_MAX_SIZE);

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfObjectStream pdfObjectStream = new PdfObjectStream(pdfDocument);
        PdfNumber number = new PdfNumber(1);
        number.makeIndirect(pdfDocument);
        for (int i = 0; i <= PdfObjectStream.MAX_OBJ_STREAM_SIZE; i++) {
            pdfObjectStream.addObject(number);
        }
    }

    @Test
    public void objectCanBeAddedToObjectStreamWithSizeLessThenMaxStreamSizeTest() {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfObjectStream pdfObjectStream = new PdfObjectStream(pdfDocument);
        PdfNumber number = new PdfNumber(1);
        number.makeIndirect(pdfDocument);
        for (int i = 0; i <= PdfObjectStream.MAX_OBJ_STREAM_SIZE - 1; i++) {
            pdfObjectStream.addObject(number);
        }
        Assert.assertTrue("We don't expect to reach this line, since no exception should have been thrown", true);
    }
}
