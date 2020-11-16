package com.itextpdf.kernel.utils;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class PdfSplitterUnitTest extends ExtendedITextTest {
    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void splitDocumentThatIsBeingWrittenTest() {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException
                .expectMessage(KernelExceptionMessageConstant.CANNOT_SPLIT_DOCUMENT_THAT_IS_BEING_WRITTEN);

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfSplitter pdfSplitter = new PdfSplitter(pdfDocument);
    }
}
