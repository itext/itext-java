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
package com.itextpdf.io.source;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * An output stream that encodes data according to the {@code ASCII85Decode}
 * filter from the PDF specification.
 */
public class ASCII85OutputStream extends FilterOutputStream implements IFinishable {
    private static final int BASE = 85;
    /**
     * Offset to the first base-85 output char.
     */
    private static final int OFFSET = 33;
    /**
     * Size of the encoding block. After this amount of bytes data is converted
     * and flush to the backing stream.
     */
    private static final int INPUT_LENGTH = 4;
    /**
     * Amount of bytes produced from a block of input bytes.
     */
    private static final int OUTPUT_LENGTH = 5;
    /**
     * Marker written, when all input bytes are zero. Not used for partial
     * blocks.
     */
    private static final byte ALL_ZEROS_MARKER = (byte) 'z';
    /**
     * End Of Data marker.
     */
    private static final byte[] EOD = new byte[]{(byte) '~', (byte) '>'};

    /**
     * Encoding block buffer. Reused for encoding output, when flushing.
     */
    private final byte[] buffer = new byte[OUTPUT_LENGTH];
    /**
     * Bitwise OR of all bytes within the encoding block. Used to quickly
     * check, whether the encoding block contains only zeros.
     */
    private int inputOr = 0;
    /**
     * Input bytes cursor within the buffer.
     */
    private int inputCursor = 0;

    /**
     * Flag for detecting, whether {@link #finish} has been called.
     */
    private boolean finished = false;

    /**
     * Creates a new {@code ASCIIHexDecode} encoding stream.
     *
     * @param out the output stream to write encoded data to
     */
    public ASCII85OutputStream(OutputStream out) {
        super(out);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(int b) throws IOException {
        int value = b & 0xFF;
        buffer[inputCursor] = (byte) value;
        inputOr |= value;
        ++inputCursor;
        writeBufferIfFull();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException {
        finish();
        super.close();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void finish() throws IOException {
        if (finished) {
            return;
        }

        finished = true;
        // Writing the remainder
        if (inputCursor > 0) {
            if (inputOr == 0) {
                // If all zeros, output is just n + 1 exclamation points
                Arrays.fill(buffer, 0, inputCursor + 1, (byte) '!');
            } else {
                Arrays.fill(buffer, inputCursor, INPUT_LENGTH, (byte) 0);
                convertBuffer();
            }
            out.write(buffer, 0, inputCursor + 1);
            resetBuffer();
        }
        out.write(EOD);
        flush();
    }

    private void writeBufferIfFull() throws IOException {
        if (inputCursor < INPUT_LENGTH) {
            return;
        }
        if (inputOr == 0) {
            // Special case, if all zeros
            out.write(ALL_ZEROS_MARKER);
        } else {
            convertBuffer();
            out.write(buffer);
        }
        resetBuffer();
    }

    private void resetBuffer() {
        inputOr = 0;
        inputCursor = 0;
    }

    private void convertBuffer() {
        long num = ((buffer[0] & 0xFFL) << 24)
                | ((buffer[1] & 0xFFL) << 16)
                | ((buffer[2] & 0xFFL) << 8)
                | (buffer[3] & 0xFFL);
        for (int i = OUTPUT_LENGTH - 1; i >= 0; --i) {
            buffer[i] = (byte) (OFFSET + (num % BASE));
            num /= BASE;
        }
    }
}
