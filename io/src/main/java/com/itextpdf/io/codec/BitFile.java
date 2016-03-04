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
        int numBytes = 255;        // gif block count
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

            if (numbits <= bitsLeft) // bits contents fit in current index byte
            {
                if (blocks) // GIF
                {
                    buffer[index] |= (bits & ((1 << numbits) - 1)) << (8 - bitsLeft);
                    bitsWritten += numbits;
                    bitsLeft -= numbits;
                    numbits = 0;
                } else {
                    buffer[index] |= (bits & ((1 << numbits) - 1)) << (bitsLeft - numbits);
                    bitsWritten += numbits;
                    bitsLeft -= numbits;
                    numbits = 0;

                }
            } else // bits overflow from current byte to next.
            {
                if (blocks)    // GIF
                {
                    // if bits  > space left in current byte then the lowest order bits
                    // of code are taken and put in current byte and rest put in next.
                    buffer[index] |= (bits & ((1 << bitsLeft) - 1)) << (8 - bitsLeft);
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
                    buffer[index] |= topbits;
                    numbits -= bitsLeft;    // ok this many bits gone off the top
                    bitsWritten += bitsLeft;
                    buffer[++index] = 0;    // next index
                    bitsLeft = 8;
                }
            }

        } while (numbits != 0);
    }
}
