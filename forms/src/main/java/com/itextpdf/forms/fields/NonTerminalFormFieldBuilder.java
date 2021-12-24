package com.itextpdf.forms.fields;

import com.itextpdf.kernel.pdf.PdfDocument;

/**
 * Builder for non-terminal form field.
 */
public class NonTerminalFormFieldBuilder extends FormFieldBuilder<NonTerminalFormFieldBuilder> {

    /**
     * Creates builder for non-terminal {@link PdfFormField} creation.
     *
     * @param document document to be used for form field creation
     * @param formFieldName name of the form field
     */
    public NonTerminalFormFieldBuilder(PdfDocument document, String formFieldName) {
        super(document, formFieldName);
    }

    /**
     * Creates non-terminal form field based on provided parameters.
     *
     * @return new {@link PdfFormField} instance
     */
    public PdfFormField createNonTerminalFormField() {
        PdfFormField field = new PdfFormField(getDocument());
        field.pdfAConformanceLevel = getConformanceLevel();
        field.setFieldName(getFormFieldName());
        return field;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected NonTerminalFormFieldBuilder getThis() {
        return this;
    }
}
