/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.kernel.pdf.filters;

import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.MemoryLimitsAwareException;
import com.itextpdf.kernel.pdf.MemoryLimitsAwareFilter;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.zip.InflaterInputStream;

/**
 * Handles FlateDecode filter.
 */
public class FlateDecodeFilter extends MemoryLimitsAwareFilter {

    /**
     * A helper to flateDecode.
     *
     * @param in     the input data
     * @param strict {@code true} to read a correct stream. {@code false} to try to read a corrupted stream.
     * @return the decoded data
     */
    public static byte[] flateDecode(byte[] in, boolean strict) {
        return flateDecodeInternal(in, strict, new ByteArrayOutputStream());
    }

    /**
     * @param in           Input byte array.
     * @param decodeParams PdfDictionary of decodeParams.
     * @return a byte array
     */
    public static byte[] decodePredictor(byte[] in, PdfObject decodeParams) {
        if (decodeParams == null || decodeParams.getType() != PdfObject.DICTIONARY) {
            return in;
        }
        PdfDictionary dic = (PdfDictionary) decodeParams;
        PdfObject obj = dic.get(PdfName.Predictor);
        if (obj == null || obj.getType() != PdfObject.NUMBER) {
            return in;
        }
        int predictor = ((PdfNumber) obj).intValue();
        if (predictor < 10 && predictor != 2) {
            return in;
        }
        final int width = getNumberOrDefault(dic, PdfName.Columns, 1);
        final int colors = getNumberOrDefault(dic, PdfName.Colors, 1);
        final int bpc = getNumberOrDefault(dic, PdfName.BitsPerComponent, 8);
        DataInputStream dataStream = new DataInputStream(new ByteArrayInputStream(in));
        ByteArrayOutputStream fout = new ByteArrayOutputStream(in.length);
        int bytesPerPixel = colors * bpc / 8;
        int bytesPerRow = (colors * width * bpc + 7) / 8;
        byte[] curr = new byte[bytesPerRow];
        byte[] prior = new byte[bytesPerRow];
        if (predictor == 2) {
            if (bpc == 8) {
                int numRows = in.length / bytesPerRow;
                for (int row = 0; row < numRows; row++) {
                    int rowStart = row * bytesPerRow;
                    for (int col = bytesPerPixel; col < bytesPerRow; col++) {
                        in[rowStart + col] = (byte) (in[rowStart + col] + in[rowStart + col - bytesPerPixel]);
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
                final int bytesRead = dataStream.read(curr, 0, bytesPerRow);
                if (bytesRead < bytesPerRow) {
                    Arrays.fill(curr, bytesRead, bytesPerRow, (byte) 0);
                }
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
                        curr[i] += (byte) (prior[i] / 2);
                    }
                    for (int i = bytesPerPixel; i < bytesPerRow; i++) {
                        curr[i] += (byte) (((curr[i - bytesPerPixel] & 0xff) + (prior[i] & 0xff)) / 2);
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
                        curr[i] += (byte) ret;
                    }
                    break;
                default:
                    // Error -- unknown filter type
                    throw new PdfException(KernelExceptionMessageConstant.PNG_FILTER_UNKNOWN);
            }
            try {
                fout.write(curr);
            } catch (IOException ioe) {
                // Never happens
                assert true : "Happens!";
            }

            // Swap curr and prior
            byte[] tmp = prior;
            prior = curr;
            curr = tmp;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] decode(byte[] b, PdfName filterName, PdfObject decodeParams, PdfDictionary streamDictionary) {
        ByteArrayOutputStream outputStream = enableMemoryLimitsAwareHandler(streamDictionary);
        byte[] res = flateDecodeInternal(b, true, outputStream);
        if (res == null) {
            outputStream.reset();
            res = flateDecodeInternal(b, false, outputStream);
        }
        b = decodePredictor(res, decodeParams);
        return b;
    }

    /**
     * A helper to flateDecode.
     *
     * @param in     the input data
     * @param strict {@code true} to read a correct stream. {@code false} to try to read a corrupted stream.
     * @param out    the out stream which will be used to write the bytes.
     * @return the decoded data
     */
    protected static byte[] flateDecodeInternal(byte[] in, boolean strict, ByteArrayOutputStream out) {
        ByteArrayInputStream stream = new ByteArrayInputStream(in);
        InflaterInputStream zip = new InflaterInputStream(stream);
        byte[] b = new byte[strict ? 4092 : 1];
        try {
            int n;
            while ((n = zip.read(b)) >= 0) {
                out.write(b, 0, n);
            }
            zip.close();
            out.close();
            return out.toByteArray();
        } catch (MemoryLimitsAwareException e) {
            throw e;
        } catch (Exception e) {
            if (strict) {
                return null;
            }
            return out.toByteArray();
        }
    }

    private static int getNumberOrDefault(PdfDictionary dict, PdfName key, int defaultInt) {
        int result = defaultInt;
        final PdfObject obj = dict.get(key);

        if (obj != null && obj.getType() == PdfObject.NUMBER) {
            result = ((PdfNumber) obj).intValue();
        }
        return result;
    }
}
