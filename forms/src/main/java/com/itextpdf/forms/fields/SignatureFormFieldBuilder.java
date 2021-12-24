package com.itextpdf.forms.fields;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfWidgetAnnotation;

/**
 * Builder for signature form field.
 */
public class SignatureFormFieldBuilder extends TerminalFormFieldBuilder<SignatureFormFieldBuilder> {

    /**
     * Creates builder for {@link PdfSignatureFormField} creation.
     *
     * @param document document to be used for form field creation
     * @param formFieldName name of the form field
     */
    public SignatureFormFieldBuilder(PdfDocument document, String formFieldName) {
        super(document, formFieldName);
    }

    /**
     * Creates signature form field based on provided parameters.
     *
     * @return new {@link PdfSignatureFormField} instance.
     */
    public PdfSignatureFormField createSignature() {
        PdfSignatureFormField signatureFormField;
        if (getWidgetRectangle() == null) {
            signatureFormField = new PdfSignatureFormField(getDocument());
        } else {
            PdfWidgetAnnotation annotation = new PdfWidgetAnnotation(getWidgetRectangle());
            if (getConformanceLevel() != null) {
                annotation.setFlag(PdfAnnotation.PRINT);
            }
            signatureFormField = new PdfSignatureFormField(annotation, getDocument());
            setPageToField(signatureFormField);
        }
        signatureFormField.pdfAConformanceLevel = getConformanceLevel();
        signatureFormField.setFieldName(getFormFieldName());
        return signatureFormField;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected SignatureFormFieldBuilder getThis() {
        return this;
    }
}
