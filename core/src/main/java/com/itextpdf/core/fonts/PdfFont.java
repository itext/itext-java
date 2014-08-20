package com.itextpdf.core.fonts;

import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfName;

/**
 * Nothing here...
 * We do not yet know how the font class should look like.
 */
public class PdfFont extends PdfDictionary {
    public PdfFont(PdfDocument doc) {
        super(doc);
        put(PdfName.Type, PdfName.Font);
    }
}
