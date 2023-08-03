package com.itextpdf.forms.util;

import com.itextpdf.forms.fields.merging.MergeFieldsStrategy;
import com.itextpdf.forms.fields.merging.OnDuplicateFormFieldNameStrategy;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(UnitTest.class)
public class RegisterDefaultDiContainerTest extends ExtendedITextTest {

    @Test
    public void testDefaultRegistrationFormsModule() {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        OnDuplicateFormFieldNameStrategy strategy = pdfDocument.getDiContainer()
                .getInstance(OnDuplicateFormFieldNameStrategy.class);
        Assert.assertEquals(MergeFieldsStrategy.class, strategy.getClass());
    }
}