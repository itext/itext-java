package com.itextpdf.core.pdf.filters;

import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfObject;

/**
 * The main interface for creating a new {@code FilterHandler}
 */
public interface FilterHandler {

    /**
     * Decode the byte[] using the provided filterName.
     *
     * @param b the bytes that need to be decoded
     * @param filterName PdfName of the filter
     * @param decodeParams decode parameters
     * @param streamDictionary the dictionary of the stream. Can contain additional information needed to decode the byte[].
     * @return
     */
    public byte[] decode(byte[] b, PdfName filterName, PdfObject decodeParams, final PdfDictionary streamDictionary);
}
