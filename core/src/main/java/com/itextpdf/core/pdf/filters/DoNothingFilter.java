package com.itextpdf.core.pdf.filters;

import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfObject;

/**
 * A filter that doesn't modify the stream at all
 */
public class DoNothingFilter implements FilterHandler {

    @Override
    public byte[] decode(byte[] b, PdfName filterName, PdfObject decodeParams, PdfDictionary streamDictionary) {
        return b;
    }
}
