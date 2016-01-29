package com.itextpdf.kernel.pdf.filters;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;

import java.io.ByteArrayOutputStream;

/**
 * Handles RunLengthDecode filter.
 */
public class RunLengthDecodeFilter implements FilterHandler {

    @Override
    public byte[] decode(byte[] b, PdfName filterName, PdfObject decodeParams, PdfDictionary streamDictionary) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte dupCount = -1;
        for(int i = 0; i < b.length; i++){
            dupCount = b[i];
            if (dupCount == -128) { // this is implicit end of data
                break;
            }
            if (dupCount >= 0) {
                int bytesToCopy = dupCount+1;
                baos.write(b, i, bytesToCopy);
                i+=bytesToCopy;
            } else {                // make dupcount copies of the next byte
                i++;
                for(int j = 0; j < 1 - (int)(dupCount); j++){
                    baos.write(b[i]);
                }
            }
        }
        return baos.toByteArray();
    }
}
