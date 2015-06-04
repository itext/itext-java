package com.itextpdf.core.pdf.filters;

import com.itextpdf.basics.PdfRuntimeException;
import com.itextpdf.basics.codec.TIFFConstants;
import com.itextpdf.basics.codec.TIFFFaxDecoder;
import com.itextpdf.basics.codec.TIFFFaxDecompressor;
import com.itextpdf.core.pdf.PdfBoolean;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfNumber;
import com.itextpdf.core.pdf.PdfObject;

/**
 * Handles CCITTFaxDecode filter
 */
public class CCITTFaxDecodeFilter  implements FilterHandler {

    @Override
    public byte[] decode(byte[] b, PdfName filterName, PdfObject decodeParams, PdfDictionary streamDictionary) {
        PdfNumber wn = streamDictionary.getAsNumber(PdfName.Width);
        PdfNumber hn = streamDictionary.getAsNumber(PdfName.Height);
        if (wn == null || hn == null)
            throw new PdfRuntimeException(PdfRuntimeException.FilterCcittfaxdecodeIsOnlySupportedForImages);
        int width = wn.getIntValue();
        int height = hn.getIntValue();

        PdfDictionary param = decodeParams instanceof PdfDictionary ? (PdfDictionary)decodeParams : null;
        int k = 0;
        boolean blackIs1 = false;
        boolean byteAlign = false;
        if (param != null) {
            PdfNumber kn = param.getAsNumber(PdfName.K);
            if (kn != null)
                k = kn.getIntValue();
            PdfBoolean bo = param.getAsBoolean(PdfName.BlackIs1);
            if (bo != null)
                blackIs1 = bo.getValue();
            bo = param.getAsBoolean(PdfName.EncodedByteAlign);
            if (bo != null)
                byteAlign = bo.getValue();
        }
        byte[] outBuf = new byte[(width + 7) / 8 * height];
        TIFFFaxDecompressor decoder = new TIFFFaxDecompressor();
        if (k == 0 || k > 0) {
            int tiffT4Options = k > 0 ? TIFFConstants.GROUP3OPT_2DENCODING : 0;
            tiffT4Options |= byteAlign ? TIFFConstants.GROUP3OPT_FILLBITS : 0;
            decoder.SetOptions(1, TIFFConstants.COMPRESSION_CCITTFAX3, tiffT4Options, 0);
            decoder.decodeRaw(outBuf, b, width, height);
            if (decoder.fails > 0) {
                byte[] outBuf2 = new byte[(width + 7) / 8 * height];
                int oldFails = decoder.fails;
                decoder.SetOptions(1, TIFFConstants.COMPRESSION_CCITTRLE, tiffT4Options, 0);
                decoder.decodeRaw(outBuf2, b, width, height);
                if (decoder.fails < oldFails) {
                    outBuf = outBuf2;
                }
            }
        }
        else {
            TIFFFaxDecoder deca = new TIFFFaxDecoder(1, width, height);
            deca.decodeT6(outBuf, b, 0, height, 0);
        }
        if (!blackIs1) {
            int len = outBuf.length;
            for (int t = 0; t < len; ++t) {
                outBuf[t] ^= 0xff;
            }
        }
        b = outBuf;
        return b;
    }
}
