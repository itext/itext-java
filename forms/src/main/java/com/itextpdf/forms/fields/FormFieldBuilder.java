package com.itextpdf.forms.fields;

import com.itextpdf.kernel.pdf.PdfAConformanceLevel;
import com.itextpdf.kernel.pdf.PdfDocument;

/**
 * Builder for form field.
 *
 * @param <T> specific form field builder which extends this class.
 */
public abstract class FormFieldBuilder<T extends FormFieldBuilder<T>> {

    /**
     * Document to be used for form field creation.
     */
    private final PdfDocument document;
    /**
     * Name of the form field.
     */
    private final String formFieldName;
    /**
     * Conformance level of the form field.
     */
    private PdfAConformanceLevel conformanceLevel = null;

    /**
     * Creates builder for {@link PdfFormField} creation.
     *
     * @param document document to be used for form field creation
     * @param formFieldName name of the form field
     */
    protected FormFieldBuilder(PdfDocument document, String formFieldName) {
        this.document = document;
        this.formFieldName = formFieldName;
    }

    /**
     * Gets document to be used for form field creation.
     *
     * @return {@link PdfDocument} instance
     */
    public PdfDocument getDocument() {
        return document;
    }

    /**
     * Gets name of the form field.
     *
     * @return name to be used for form field creation
     */
    public String getFormFieldName() {
        return formFieldName;
    }

    /**
     * Gets conformance level for form field creation.
     *
     * @return instance of {@link PdfAConformanceLevel} to be used for form field creation
     */
    public PdfAConformanceLevel getConformanceLevel() {
        return conformanceLevel;
    }

    /**
     * Sets conformance level for form field creation.
     *
     * @param conformanceLevel instance of {@link PdfAConformanceLevel} to be used for form field creation
     * @return this builder
     */
    public T setConformanceLevel(PdfAConformanceLevel conformanceLevel) {
        this.conformanceLevel = conformanceLevel;
        return getThis();
    }

    /**
     * Returns this builder object. Required for superclass methods.
     *
     * @return this builder
     */
    protected abstract T getThis();
}
