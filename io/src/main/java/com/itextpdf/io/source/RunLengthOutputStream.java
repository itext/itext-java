/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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

/**
 * An output stream that encodes data according to the {@code RunLengthDecode}
 * filter from the PDF specification.
 */
public class RunLengthOutputStream extends FilterOutputStream implements IFinishable {
    /**
     * Maximum length of a run. Applies to both "unique" and repeating ones.
     */
    private static final int MAX_LENGTH = 128;
    /**
     * End Of Data marker.
     */
    private static final byte EOD = (byte) 128;

    /**
     * Buffer for storing the pending run.
     */
    private final byte[] buffer = new byte[MAX_LENGTH];
    /**
     * Value, that repeats in a repeating run. Set to {@code -1}, when the
     * pending run is a "unique" one.
     */
    private int repeatValue = -1;
    /**
     * Current length of the pending run.
     */
    private int currentLength = 0;

    /**
     * Flag for detecting, whether {@link #finish} has been called.
     */
    private boolean finished = false;

    /**
     * Creates a new {@code RunLengthDecode} encoding stream.
     *
     * @param out the output stream to write encoded data to
     */
    public RunLengthOutputStream(OutputStream out) {
        super(out);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(int b) throws IOException {
        int value = b & 0xFF;
        // Case for continuing a repeating run
        if (value == repeatValue) {
            ++currentLength;
            if (currentLength == MAX_LENGTH) {
                writePending();
            }
            return;
        }
        /*
         * If there was a repeating run, but we got a different value, then we
         * need to write the current repeating run we had and start a new
         * "unique" run.
         */
        if (repeatValue != -1) {
            writePending();
            buffer[currentLength] = (byte) value;
            ++currentLength;
            return;
        }
        /*
         * As soon as we detect a sequence of 3 or more bytes, which are the
         * same, we need to switch to a repeating run. For this we will write
         * the values before the repeated one as a "unique" run and start a
         * new repeating run at length 3.
         *
         * Technically speaking we can switch to a repeating run at 2 bytes,
         * but in the vast majority of cases this will make the compression
         * ratio worse.
         */
        if (currentLength >= 2
                && buffer[currentLength - 1] == (byte) value
                && buffer[currentLength - 2] == (byte) value) {
            currentLength -= 2;
            writePending();
            repeatValue = value;
            currentLength = 3;
            return;
        }
        // Just continuing (or starting) a "unique" run
        buffer[currentLength] = (byte) value;
        ++currentLength;
        if (currentLength == MAX_LENGTH) {
            writePending();
        }
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
        writePending();
        out.write(EOD);
        flush();
    }

    private void writePending() throws IOException {
        if (currentLength <= 0) {
            return;
        }
        if (repeatValue < 0) {
            // Writing "unique" run
            out.write(currentLength - 1);
            out.write(buffer, 0, currentLength);
        } else {
            // Writing repeating run
            out.write(257 - currentLength);
            out.write(repeatValue);
        }
        resetPending();
    }

    private void resetPending() {
        repeatValue = -1;
        currentLength = 0;
    }
}
