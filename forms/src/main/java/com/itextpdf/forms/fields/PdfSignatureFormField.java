package com.itextpdf.forms.fields;

import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfObject;
import com.itextpdf.core.pdf.annot.PdfWidgetAnnotation;
import com.itextpdf.forms.PdfSigFieldLockDictionary;


/**
 * An AcroForm field containing signature data.
 */
public class PdfSignatureFormField extends PdfFormField {

    protected PdfSignatureFormField() {
        super();
    }

    protected PdfSignatureFormField(PdfWidgetAnnotation widget) {
        super(widget);
    }

    protected PdfSignatureFormField(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    /**
     * Returns <code>Sig</code>, the form type for signature form fields.
     * 
     * @return the form type, as a {@link PdfName}
     */
    @Override
    public PdfName getFormType() {
        return PdfName.Sig;
    }

    /**
     * Adds the signature to the signature field.
     * 
     * @param <T> guaranteed to be a {@link PdfFormField} type
     * @param value the signature to be contained in the signature field, or an indirect reference to it
     * @return 
     */
    public <T extends PdfFormField> T setValue(PdfObject value) {
        return put(PdfName.V, value);
    }

    /**
     * Gets the {@link PdfSigFieldLockDictionary}, which contains fields that
     * must be locked if the document is signed.
     * 
     * @return a dictionary containing locked fields.
     * @see PdfSigFieldLockDictionary
     */
    public PdfSigFieldLockDictionary getSigFieldLockDictionary() {
        PdfDictionary sigLockDict = (PdfDictionary) getPdfObject().get(PdfName.Lock);
        return sigLockDict == null ? null : new PdfSigFieldLockDictionary(sigLockDict);
    }
}
