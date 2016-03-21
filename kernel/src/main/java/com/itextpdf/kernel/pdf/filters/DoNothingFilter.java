package com.itextpdf.kernel.pdf.filters;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;

/**
 * A filter that doesn't modify the stream at all
 */
public class DoNothingFilter implements FilterHandler {
    private PdfName lastFilterName;
    @Override
    public byte[] decode(byte[] b, PdfName filterName, PdfObject decodeParams, PdfDictionary streamDictionary) {
        lastFilterName = filterName;
        return b;
    }

    public PdfName getLastFilterName() {
        return lastFilterName;
    }
}
