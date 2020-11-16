package com.itextpdf.kernel.pdf.tagutils;

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
public class TagStructureContextUnitTest extends ExtendedITextTest {
    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void noTagStructureInNonTaggedDocumentTest() {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException.expectMessage(KernelExceptionMessageConstant.MUST_BE_A_TAGGED_DOCUMENT);

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        TagStructureContext tagStructureContext = new TagStructureContext(pdfDocument);
    }
}
