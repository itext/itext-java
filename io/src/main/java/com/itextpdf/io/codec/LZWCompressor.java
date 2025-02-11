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

        // set flag for GIF as NOT tiff
        bf_ = new BitFile(outputStream, !TIFF);
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
