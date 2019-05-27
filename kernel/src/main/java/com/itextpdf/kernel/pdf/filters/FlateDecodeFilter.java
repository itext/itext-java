/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.kernel.pdf.filters;

import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.pdf.MemoryLimitsAwareException;
import com.itextpdf.kernel.pdf.MemoryLimitsAwareFilter;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.zip.InflaterInputStream;

/**
 * Handles FlateDecode filter.
 */
public class FlateDecodeFilter extends MemoryLimitsAwareFilter {

    /**
     * Creates a FlateDecodeFilter.
     */
    public FlateDecodeFilter() {
        this(false);
    }

    /**
     * Creates a FlateDecodeFilter.
     *
     * @param strictDecoding defines whether the decoder will try to read a corrupted stream
     * @deprecated will be removed in 7.2, use {@link FlateDecodeStrictFilter} instead.
     */
    @Deprecated
    public FlateDecodeFilter(boolean strictDecoding) {
        this.strictDecoding = strictDecoding;
    }

    /**
     * Checks whether the decoder will try to read a corrupted stream (not strict) or not (strict)
     *
     * @return true if the decoder will try to read a corrupted stream otherwise false
     * @deprecated will be removed in 7.2, use {@link FlateDecodeStrictFilter} instead.
     */
    @Deprecated
    public boolean isStrictDecoding() {
        return strictDecoding;
    }

    /**
     * Defines how the corrupted streams should be treated.
     *
     * @param strict true if the decoder should try to read a corrupted stream otherwise false
     * @return the decoder
     * @deprecated will be removed in 7.2, use {@link FlateDecodeStrictFilter} instead.
     */
    @Deprecated
    public FlateDecodeFilter setStrictDecoding(boolean strict) {
        this.strictDecoding = strict;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] decode(byte[] b, PdfName filterName, PdfObject decodeParams, PdfDictionary streamDictionary) {
        ByteArrayOutputStream outputStream = enableMemoryLimitsAwareHandler(streamDictionary);
        byte[] res = flateDecode(b, true, outputStream);
        if (res == null && !strictDecoding) {
            outputStream.reset();
            res = flateDecode(b, false, outputStream);
        }
        b = decodePredictor(res, decodeParams);
        return b;
    }

    /**
     * Defines how the corrupted streams should be treated.
     *
     * @deprecated will be removed in 7.2, use {@link FlateDecodeStrictFilter} instead.
     */
    @Deprecated
    private boolean strictDecoding = false;

    /**
     * A helper to flateDecode.
     *
     * @param in     the input data
     * @param strict {@code true} to read a correct stream. {@code false} to try to read a corrupted stream.
     * @return the decoded data
     */
    public static byte[] flateDecode(byte[] in, boolean strict) {
        return flateDecode(in, strict, new ByteArrayOutputStream());
    }

    /**
     * A helper to flateDecode.
     *
     * @param in     the input data
     * @param strict {@code true} to read a correct stream. {@code false} to try to read a corrupted stream.
     * @param out    the out stream which will be used to write the bytes.
     * @return the decoded data
     */
    private static byte[] flateDecode(byte[] in, boolean strict, ByteArrayOutputStream out) {
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

    /**
     * @param in           Input byte array.
     * @param decodeParams PdfDictionary of decodeParams.
     * @return a byte array
     */
    public static byte[] decodePredictor(byte[] in, PdfObject decodeParams) {
        if (decodeParams == null || decodeParams.getType() != PdfObject.DICTIONARY)
            return in;
        PdfDictionary dic = (PdfDictionary) decodeParams;
        PdfObject obj = dic.get(PdfName.Predictor);
        if (obj == null || obj.getType() != PdfObject.NUMBER)
            return in;
        int predictor = ((PdfNumber) obj).intValue();
        if (predictor < 10 && predictor != 2)
            return in;
        int width = 1;
        obj = dic.get(PdfName.Columns);
        if (obj != null && obj.getType() == PdfObject.NUMBER)
            width = ((PdfNumber) obj).intValue();
        int colors = 1;
        obj = dic.get(PdfName.Colors);
        if (obj != null && obj.getType() == PdfObject.NUMBER)
            colors = ((PdfNumber) obj).intValue();
        int bpc = 8;
        obj = dic.get(PdfName.BitsPerComponent);
        if (obj != null && obj.getType() == PdfObject.NUMBER)
            bpc = ((PdfNumber) obj).intValue();
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
                    throw new PdfException(PdfException.PngFilterUnknown);
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
}
