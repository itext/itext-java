package com.itextpdf.core.pdf.filters;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfObject;

import java.io.ByteArrayOutputStream;

/**
 * Handles LZWDECODE filter
 */
public class LZWDecodeFilter implements FilterHandler{

    @Override
    public byte[] decode(byte[] b, PdfName filterName, PdfObject decodeParams, PdfDictionary streamDictionary) throws PdfException {
        b = LZWDecode(b);
        b = FlateDecodeFilter.decodePredictor(b, decodeParams);
        return b;
    }

    public static byte[] LZWDecode(final byte in[]) throws PdfException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        LZWDecoder lzw = new LZWDecoder();
        lzw.decode(in, out);
        return out.toByteArray();
    }
}
