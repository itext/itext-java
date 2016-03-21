package com.itextpdf.signatures;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;
import com.itextpdf.kernel.pdf.PdfString;

/**
 * A dictionary that stores the name of the application that signs the PDF.
 */
public class PdfSignatureAppDictionary extends PdfObjectWrapper<PdfDictionary> {

    /**
     * Creates a new PdfSignatureAppDictionary
     */
    public PdfSignatureAppDictionary() {
        super(new PdfDictionary());
    }

    /**
     * Creates a new PdfSignatureAppDictionary.
     *
     * @param pdfObject PdfDictionary containing initial values
     */
    public PdfSignatureAppDictionary(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    /**
     * Sets the signature created property in the Prop_Build dictionary's App
     * dictionary.
     *
     * @param name String name of the application creating the signature
     */
    public void setSignatureCreator(String name) {
        put(PdfName.Name, new PdfString(name, PdfEncodings.UnicodeBig));
    }

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return false;
    }
}