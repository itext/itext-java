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
package com.itextpdf.io.codec;


import java.io.IOException;
import java.io.OutputStream;

/**
 * Modified from original LZWCompressor to change interface to passing a
 * buffer of data to be compressed.
 */
public class LZWCompressor {
    /**
     * base underlying code size of data being compressed 8 for TIFF, 1 to 8 for GIF
     **/
    int codeSize_;

    /**
     * reserved clear code based on code size
     **/
    int clearCode_;

    /**
     * reserved end of data code based on code size
     **/
    int endOfInfo_;

    /**
     * current number bits output for each code
     **/
    int numBits_;

    /**
     * limit at which current number of bits code size has to be increased
     **/
    int limit_;

    /**
     * the prefix code which represents the predecessor string to current input point
     **/
    short prefix_;

    /**
     * output destination for bit codes
     **/
    BitFile bf_;

    /**
     * general purpose LZW string table
     **/
    LZWStringTable lzss_;

    /**
     * modify the limits of the code values in LZW encoding due to TIFF bug / feature
     **/
    boolean tiffFudge_;

    /**
     * @param outputStream      destination for compressed data
     * @param codeSize the initial code size for the LZW compressor
     * @param TIFF     flag indicating that TIFF lzw fudge needs to be applied
     * @throws IOException if underlying output stream error
     **/
    public LZWCompressor(OutputStream outputStream, int codeSize, boolean TIFF) throws IOException {
        bf_ = new BitFile(outputStream, !TIFF);    // set flag for GIF as NOT tiff
        codeSize_ = codeSize;
        tiffFudge_ = TIFF;
        clearCode_ = 1 << codeSize_;
        endOfInfo_ = clearCode_ + 1;
        numBits_ = codeSize_ + 1;

        limit_ = (1 << numBits_) - 1;
        if (tiffFudge_)
            --limit_;

        //0xFFFF
        prefix_ = -1;
        lzss_ = new LZWStringTable();
        lzss_.ClearTable(codeSize_);
        bf_.writeBits(clearCode_, numBits_);
    }

    /**
     * @param buf       The data to be compressed to output stream
     * @param offset    The offset at which the data starts
     * @param length    The length of the data being compressed
     * @throws IOException if underlying output stream error
     **/
    public void compress(byte[] buf, int offset, int length)
            throws IOException {
        int idx;
        byte c;
        short index;

        int maxOffset = offset + length;
        for (idx = offset; idx < maxOffset; ++idx) {
            c = buf[idx];
            if ((index = lzss_.FindCharString(prefix_, c)) != -1)
                prefix_ = index;
            else {
                bf_.writeBits(prefix_, numBits_);
                if (lzss_.AddCharString(prefix_, c) > limit_) {
                    if (numBits_ == 12) {
                        bf_.writeBits(clearCode_, numBits_);
                        lzss_.ClearTable(codeSize_);
                        numBits_ = codeSize_ + 1;
                    } else
                        ++numBits_;

                    limit_ = (1 << numBits_) - 1;
                    if (tiffFudge_)
                        --limit_;
                }
                prefix_ = (short) ((short) c & 0xFF);
            }
        }
    }

    /**
     * Indicate to compressor that no more data to go so write out
     * any remaining buffered data.
     *
     * @throws IOException if underlying output stream error
     **/
    public void flush() throws IOException {
        if (prefix_ != -1)
            bf_.writeBits(prefix_, numBits_);

        bf_.writeBits(endOfInfo_, numBits_);
        bf_.flush();
    }
}
