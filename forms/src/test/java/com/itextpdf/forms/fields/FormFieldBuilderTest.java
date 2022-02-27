package com.itextpdf.forms.fields;

import com.itextpdf.kernel.pdf.PdfAConformanceLevel;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.ByteArrayOutputStream;

@Category(UnitTest.class)
public class FormFieldBuilderTest extends ExtendedITextTest {
    private static final PdfDocument DUMMY_DOCUMENT = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
    private static final String DUMMY_NAME = "dummy name";

    @Test
    public void constructorTest() {
        TestBuilder builder = new TestBuilder(DUMMY_DOCUMENT, DUMMY_NAME);

        Assert.assertSame(DUMMY_DOCUMENT, builder.getDocument());
        Assert.assertSame(DUMMY_NAME, builder.getFormFieldName());
    }

    @Test
    public void getSetConformanceLevelTest() {
        TestBuilder builder = new TestBuilder(DUMMY_DOCUMENT, DUMMY_NAME);
        builder.setConformanceLevel(PdfAConformanceLevel.PDF_A_1A);

        Assert.assertSame(PdfAConformanceLevel.PDF_A_1A, builder.getConformanceLevel());
    }

    private static class TestBuilder extends FormFieldBuilder<TestBuilder> {

        protected TestBuilder(PdfDocument document, String formFieldName) {
            super(document, formFieldName);
        }

        @Override
        protected TestBuilder getThis() {
            return this;
        }
    }
}