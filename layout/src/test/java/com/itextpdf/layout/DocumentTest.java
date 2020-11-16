package com.itextpdf.layout;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.exceptions.LayoutExceptionMessageConstant;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(IntegrationTest.class)
public class DocumentTest extends ExtendedITextTest {
    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void executeActionInClosedDocTest() {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException
                .expectMessage(LayoutExceptionMessageConstant.DOCUMENT_CLOSED_IT_IS_IMPOSSIBLE_TO_EXECUTE_ACTION);

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        Document document = new Document(pdfDoc);
        Paragraph paragraph = new Paragraph("test");
        document.add(paragraph);
        document.close();
        document.checkClosingStatus();
    }
}
