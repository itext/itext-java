package com.itextpdf.core.pdf.filters;

import com.itextpdf.core.PdfException;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfNumber;
import com.itextpdf.core.pdf.PdfObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.zip.InflaterInputStream;

/**
 * Handles FlateDecode filter.
 */
public class FlateDecodeFilter implements FilterHandler{

    @Override
    public byte[] decode(byte[] b, PdfName filterName, PdfObject decodeParams, PdfDictionary streamDictionary) {
        byte[] res = flateDecode(b, true);
        if (res == null)
            res = flateDecode(b, false);
        b = decodePredictor(res, decodeParams);
        return b;
    }

    /**
     * A helper to flateDecode.
     *
     * @param in the input data
     * @param strict {@code true} to read a correct stream. {@code false} to try to read a corrupted stream.
     * @return the decoded data
     */
    public static byte[] flateDecode(final byte in[], final boolean strict) {
        ByteArrayInputStream stream = new ByteArrayInputStream(in);
        InflaterInputStream zip = new InflaterInputStream(stream);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte b[] = new byte[strict ? 4092 : 1];
        try {
            int n;
            while ((n = zip.read(b)) >= 0) {
                out.write(b, 0, n);
            }
            zip.close();
            out.close();
            return out.toByteArray();
        }
        catch (Exception e) {
            if (strict)
                return null;
            return out.toByteArray();
        }
    }

    /**
     * @param in Input byte array.
     * @param decodeParams PdfDictionary of decodeParams.
     * @return a byte array
     */
    public static byte[] decodePredictor(final byte[] in, final PdfObject decodeParams) {
        if (decodeParams == null || decodeParams.getType() != PdfObject.Dictionary)
            return in;
        PdfDictionary dic = (PdfDictionary)decodeParams;
        PdfObject obj = dic.get(PdfName.Predictor);
        if (obj == null || obj.getType() != PdfObject.Number)
            return in;
        int predictor = ((PdfNumber)obj).getIntValue();
        if (predictor < 10 && predictor != 2)
            return in;
        int width = 1;
        obj = dic.get(PdfName.Columns);
        if (obj != null && obj.getType() == PdfObject.Number)
            width = ((PdfNumber)obj).getIntValue();
        int colors = 1;
        obj = dic.get(PdfName.Colors);
        if (obj != null && obj.getType() == PdfObject.Number)
            colors = ((PdfNumber)obj).getIntValue();
        int bpc = 8;
        obj = dic.get(PdfName.BitsPerComponent);
        if (obj != null && obj.getType() == PdfObject.Number)
            bpc = ((PdfNumber)obj).getIntValue();
        DataInputStream dataStream = new DataInputStream(new ByteArrayInputStream(in));
        ByteArrayOutputStream fout = new ByteArrayOutputStream(in.length);
        int bytesPerPixel = colors * bpc / 8;
        int bytesPerRow = (colors * width * bpc + 7)/8;
        byte[] curr = new byte[bytesPerRow];
        byte[] prior = new byte[bytesPerRow];
        if (predictor == 2) {
            if (bpc == 8) {
                int numRows = in.length / bytesPerRow;
                for (int row = 0; row < numRows; row++) {
                    int rowStart = row * bytesPerRow;
                    for (int col = bytesPerPixel; col < bytesPerRow; col++) {
                        in[rowStart + col] = (byte)(in[rowStart + col] + in[rowStart + col - bytesPerPixel]);
                    }
                }
            }
            return in;
        }
        // Decode the (sub)image row-by-row
        while (true) {
            // Read the filter type byte and a row of data
            int filter;
            try {
                filter = dataStream.read();
                if (filter < 0) {
                    return fout.toByteArray();
                }
                dataStream.readFully(curr, 0, bytesPerRow);
            } catch (Exception e) {
                return fout.toByteArray();
            }

            switch (filter) {
                case 0: //PNG_FILTER_NONE
                    break;
                case 1: //PNG_FILTER_SUB
                    for (int i = bytesPerPixel; i < bytesPerRow; i++) {
                        curr[i] += curr[i - bytesPerPixel];
                    }
                    break;
                case 2: //PNG_FILTER_UP
                    for (int i = 0; i < bytesPerRow; i++) {
                        curr[i] += prior[i];
                    }
                    break;
                case 3: //PNG_FILTER_AVERAGE
                    for (int i = 0; i < bytesPerPixel; i++) {
                        curr[i] += prior[i] / 2;
                    }
                    for (int i = bytesPerPixel; i < bytesPerRow; i++) {
                        curr[i] += ((curr[i - bytesPerPixel] & 0xff) + (prior[i] & 0xff))/2;
                    }
                    break;
                case 4: //PNG_FILTER_PAETH
                    for (int i = 0; i < bytesPerPixel; i++) {
                        curr[i] += prior[i];
                    }

                    for (int i = bytesPerPixel; i < bytesPerRow; i++) {
                        int a = curr[i - bytesPerPixel] & 0xff;
                        int b = prior[i] & 0xff;
                        int c = prior[i - bytesPerPixel] & 0xff;

                        int p = a + b - c;
                        int pa = Math.abs(p - a);
                        int pb = Math.abs(p - b);
                        int pc = Math.abs(p - c);

                        int ret;

                        if (pa <= pb && pa <= pc) {
                            ret = a;
                        } else if (pb <= pc) {
                            ret = b;
                        } else {
                            ret = c;
                        }
                        curr[i] += (byte)ret;
                    }
                    break;
                default:
                    // Error -- unknown filter type
                    throw new PdfException(PdfException.PngFilterUnknown);
            }
            try {
                fout.write(curr);
            }
            catch (IOException ioe) {
                // Never happens
                assert true: "Happens!";
            }

            // Swap curr and prior
            byte[] tmp = prior;
            prior = curr;
            curr = tmp;
        }
    }
}
