package com.itextpdf.kernel.pdf.canvas.wmf;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class MetaDoUnitTest extends ExtendedITextTest {
    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void inputStreamShallBeStartFromSpecificValueTest() throws IOException {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException.expectMessage(KernelExceptionMessageConstant.NOT_A_PLACEABLE_WINDOWS_METAFILE);

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfPage pdfPage = pdfDocument.addNewPage(1);
        PdfCanvas pdfCanvas = new PdfCanvas(pdfPage);
        // InputStream value shall be start with 0x9AC6CDD7
        InputStream inputStream = new ByteArrayInputStream(new byte[] {});
        MetaDo metaDo = new MetaDo(inputStream, pdfCanvas);
        metaDo.readAll();
    }
}
