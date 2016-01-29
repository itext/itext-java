package com.itextpdf.core.pdf.filters;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.source.ByteBuffer;
import com.itextpdf.basics.source.PdfTokenizer;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfObject;

import java.io.ByteArrayOutputStream;

/**
 * Handles ASCIIHexDecode filter
 */
public class ASCIIHexDecodeFilter implements FilterHandler {

    @Override
    public byte[] decode(byte[] b, PdfName filterName, PdfObject decodeParams, PdfDictionary streamDictionary) {
        b = ASCIIHexDecode(b);
        return b;
    }

    /**
     * Decodes a byte[] according to ASCII Hex encoding.
     *
     * @param in byte[] to be decoded
     * @return decoded byte[]
     */
    public static byte[] ASCIIHexDecode(final byte in[]) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        boolean first = true;
        int n1 = 0;
        for (int k = 0; k < in.length; ++k) {
            int ch = in[k] & 0xff;
            if (ch == '>')
                break;
            if (PdfTokenizer.isWhitespace(ch))
                continue;
            int n = ByteBuffer.getHex(ch);
            if (n == -1)
                throw new PdfException(PdfException.IllegalCharacterInAsciihexdecode);
            if (first)
                n1 = n;
            else
                out.write((byte)((n1 << 4) + n));
            first = !first;
        }
        if (!first)
            out.write((byte)(n1 << 4));
        return out.toByteArray();
    }
}
