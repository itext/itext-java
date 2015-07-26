package com.itextpdf.signatures;

import com.itextpdf.basics.font.PdfEncodings;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfObjectWrapper;
import com.itextpdf.core.pdf.PdfString;

/**
 * A dictionary that stores the name of the application that signs the PDF.
 */
public class PdfSignatureAppDictionary extends PdfObjectWrapper<PdfDictionary> {

    /**
     * Creates new PdfSignatureAppDictionary
     */
    public PdfSignatureAppDictionary() {
        super(new PdfDictionary());
    }

    public PdfSignatureAppDictionary(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    /**
     * Sets the signature created property in the Prop_Build dictionary's App
     * dictionary
     *
     * @param name
     */
    public void setSignatureCreator(String name) {
        put(PdfName.Name, new PdfString(name, PdfEncodings.UnicodeBig));
    }
}