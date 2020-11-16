package com.itextpdf.forms.fields;

import com.itextpdf.forms.exceptions.FormsExceptionMessageConstant;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class PdfFormFieldUnitTest extends ExtendedITextTest {
    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void cannotGetRectangleIfKidsIsNullTest() {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException
                .expectMessage(FormsExceptionMessageConstant.WRONG_FORM_FIELD_ADD_ANNOTATION_TO_THE_FIELD);

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfDictionary pdfDictionary = new PdfDictionary();
        PdfFormField pdfFormField = new PdfFormField(pdfDocument);
        pdfFormField.getRect(pdfDictionary);
    }
}
