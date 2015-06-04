package com.itextpdf.core.pdf.filters;

import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfObject;

/**
 * The main interface for creating a new {@code FilterHandler}
 */
public interface FilterHandler {

    public byte[] decode(byte[] b, PdfName filterName, PdfObject decodeParams, final PdfDictionary streamDictionary);
}
