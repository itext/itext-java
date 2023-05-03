package com.itextpdf.forms.fields;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.annot.PdfWidgetAnnotation;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class PdfFormCreatorTest extends ExtendedITextTest {
    @Test
    public void getAcroFormTest() {
        PdfFormFactory customFactory = new PdfFormFactory() {
            @Override
            public PdfAcroForm getAcroForm(PdfDocument document, boolean createIfNotExist) {
                // Never create new acroform.
                return super.getAcroForm(document, false);
            }
        };
        
        PdfFormCreator.setFactory(customFactory);
        
        try (PdfDocument document = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            PdfAcroForm acroForm = PdfFormCreator.getAcroForm(document, true);
            Assert.assertNull(acroForm);
        } finally {
            PdfFormCreator.setFactory(new PdfFormFactory());
        }
    }

    @Test
    public void createTextFormFieldTest() {
        PdfFormFactory customFactory = new PdfFormFactory() {
            @Override
            public PdfTextFormField createTextFormField(PdfWidgetAnnotation widgetAnnotation, PdfDocument document) {
                PdfTextFormField formField = super.createTextFormField(widgetAnnotation, document);
                // All text is read by default.
                formField.setColor(ColorConstants.RED);
                return formField;
            }
        };

        PdfFormCreator.setFactory(customFactory);

        try (PdfDocument document = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            PdfFormField text =
                    new TextFormFieldBuilder(document, "name").setWidgetRectangle(new Rectangle(100, 100)).createText();
            Assert.assertEquals(ColorConstants.RED, text.getColor());
        } finally {
            PdfFormCreator.setFactory(new PdfFormFactory());
        }
    }
}
