package com.itextpdf.core.fonts;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfObjectWrapper;

/**
 * Nothing here...
 * We do not yet know how the font class should look like.
 */
public class PdfFont extends PdfObjectWrapper<PdfDictionary> {

    public PdfFont(PdfDictionary pdfObject, PdfDocument pdfDocument) throws PdfException {
        super(pdfObject, pdfDocument);
    }

    public PdfFont(PdfDocument pdfDocument) throws PdfException {
        this(new PdfDictionary(), pdfDocument);
        pdfObject.put(PdfName.Type, PdfName.Font);
    }

    /**
     * Makes font from the dictionary.
     *
     * @param dictionary  a dictionary to construct font from.
     * @param pdfDocument a document font belongs to.
     * @return constructed font.
     */
    static public PdfFont makeFont(PdfDictionary dictionary, PdfDocument pdfDocument) {
        return null;
    }

    @Override
    public PdfFont copy(PdfDocument document) throws PdfException {
        return new PdfFont((PdfDictionary)getPdfObject().copy(document), document);
    }
}
