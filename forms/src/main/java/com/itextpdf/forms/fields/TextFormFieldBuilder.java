package com.itextpdf.forms.fields;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfWidgetAnnotation;

/**
 * Builder for text form field.
 */
public class TextFormFieldBuilder extends TerminalFormFieldBuilder<TextFormFieldBuilder> {

    private static final String TEXT_FORM_FIELD_DEFAULT_VALUE = "";

    /**
     * Creates builder for {@link PdfTextFormField} creation.
     *
     * @param document document to be used for form field creation
     * @param formFieldName name of the form field
     */
    public TextFormFieldBuilder(PdfDocument document, String formFieldName) {
        super(document, formFieldName);
    }

    /**
     * Creates text form field based on provided parameters.
     *
     * @return new {@link PdfTextFormField} instance
     */
    public PdfTextFormField createText() {
        return createText(false);
    }

    private PdfTextFormField createText(boolean multiline) {
        PdfTextFormField field;
        if (getWidgetRectangle() == null) {
            field = new PdfTextFormField(getDocument());
        } else {
            PdfWidgetAnnotation annotation = new PdfWidgetAnnotation(getWidgetRectangle());
            if (null != getConformanceLevel()) {
                annotation.setFlag(PdfAnnotation.PRINT);
            }
            field = new PdfTextFormField(annotation, getDocument());
            setPageToField(field);
        }

        field.pdfAConformanceLevel = getConformanceLevel();
        field.updateFontAndFontSize(getDocument().getDefaultFont(), PdfFormField.DEFAULT_FONT_SIZE);
        field.setMultiline(multiline);
        field.setFieldName(getFormFieldName());
        field.setValue(TEXT_FORM_FIELD_DEFAULT_VALUE);

        return field;
    }

    /**
     * Creates multiline text form field based on provided parameters.
     *
     * @return new {@link PdfTextFormField} instance
     */
    public PdfTextFormField createMultilineText() {
        return createText(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected TextFormFieldBuilder getThis() {
        return this;
    }
}
