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
 * Came from GIFEncoder initially.
 * Modified - to allow for output compressed data without the block counts
 * which breakup the compressed data stream for GIF.
 */
class BitFile {

    OutputStream output;

    byte buffer[];

    int index;

    // bits left at current index that are avail.
    int bitsLeft;

    /**
     * note this also indicates gif format BITFile.
     **/
    boolean blocks = false;

    /**
     * @param output destination for output data
     * @param blocks GIF LZW requires block counts for output data
     **/
    public BitFile(OutputStream output, boolean blocks) {
        this.output = output;
        this.blocks = blocks;
        buffer = new byte[256];
        index = 0;
        bitsLeft = 8;
    }

    public void flush() throws IOException {
        int numBytes = index + (bitsLeft == 8 ? 0 : 1);
        if (numBytes > 0) {
            if (blocks)
                output.write(numBytes);
            output.write(buffer, 0, numBytes);
            buffer[0] = 0;
            index = 0;
            bitsLeft = 8;
        }
    }

    public void writeBits(int bits, int numbits) throws IOException {
        int bitsWritten = 0;
        // gif block count
        int numBytes = 255;
        do {
            // This handles the GIF block count stuff
            if ((index == 254 && bitsLeft == 0) || index > 254) {
                if (blocks)
                    output.write(numBytes);

                output.write(buffer, 0, numBytes);

                buffer[0] = 0;
                index = 0;
                bitsLeft = 8;
            }

            // bits contents fit in current index byte
            if (numbits <= bitsLeft)
            {
                // GIF
                if (blocks)
                {
                    buffer[index] |= (byte) ((bits & ((1 << numbits) - 1)) << (8 - bitsLeft));
                    bitsWritten += numbits;
                    bitsLeft -= numbits;
                    numbits = 0;
                } else {
                    buffer[index] |= (byte) ((bits & ((1 << numbits) - 1)) << (bitsLeft - numbits));
                    bitsWritten += numbits;
                    bitsLeft -= numbits;
                    numbits = 0;

                }
            } else {
                // bits overflow from current byte to next.

                // GIF
                if (blocks)
                {
                    // if bits  > space left in current byte then the lowest order bits
                    // of code are taken and put in current byte and rest put in next.
                    buffer[index] |= (byte) ((bits & ((1 << bitsLeft) - 1)) << (8 - bitsLeft));
                    bitsWritten += bitsLeft;
                    bits >>= bitsLeft;
                    numbits -= bitsLeft;
                    buffer[++index] = 0;
                    bitsLeft = 8;
                } else {
                    // if bits  > space left in current byte then the highest order bits
                    // of code are taken and put in current byte and rest put in next.
                    // at highest order bit location !!
                    int topbits = (bits >>> (numbits - bitsLeft)) & ((1 << bitsLeft) - 1);
                    buffer[index] |= (byte) topbits;
                    // ok this many bits gone off the top
                    numbits -= bitsLeft;
                    bitsWritten += bitsLeft;
                    // next index
                    buffer[++index] = 0;
                    bitsLeft = 8;
                }
            }

        } while (numbits != 0);
    }
}
