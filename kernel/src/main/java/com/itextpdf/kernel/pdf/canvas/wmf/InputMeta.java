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
package com.itextpdf.kernel.pdf.canvas.wmf;

import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;

import java.io.IOException;
import java.io.InputStream;

/**
 * Helper class to read nt, short, words, etc. from an InputStream.
 */
public class InputMeta {

    InputStream in;
    int length;

    /**
     * Creates an InputMeta object.
     *
     * @param in InputStream containing the WMF data
     */
    public InputMeta(InputStream in) {
        this.in = in;
    }

    /**
     * Read the next word from the InputStream.
     *
     * @return the next word or 0 if the end of the stream has been reached
     * @throws IOException an {@link IOException}
     */
    public int readWord() throws IOException {
        length += 2;
        int k1 = in.read();
        if (k1 < 0)
            return 0;
        return (k1 + (in.read() << 8)) & 0xffff;
    }

    /**
     * Read the next short from the InputStream.
     *
     * @return the next short value
     * @throws IOException an {@link IOException}
     */
    public int readShort() throws IOException{
        int k = readWord();
        if (k > 0x7fff)
            k -= 0x10000;
        return k;
    }

    /**
     * Read the next int from the InputStream.
     *
     * @return the next int
     * @throws IOException an {@link IOException}
     */
    public int readInt() throws IOException{
        length += 4;
        int k1 = in.read();
        if (k1 < 0)
            return 0;
        int k2 = in.read() << 8;
        int k3 = in.read() << 16;
        return k1 + k2 + k3 + (in.read() << 24);
    }

    /**
     * Read the next byte from the InputStream.
     *
     * @return the next byte
     * @throws IOException an {@link IOException}
     */
    public int readByte() throws IOException{
        ++length;
        return in.read() & 0xff;
    }

    /**
     * Skips "len" amount of bytes from the InputStream. If len is &lt; 0, nothing is skipped.
     *
     * @param len amount of bytes needed to skip
     * @throws IOException an {@link IOException}
     */
    public void skip(int len) throws IOException{
        length += len;
        StreamUtil.skip(in, len);
    }

    /**
     * Get the amount of bytes read and/or skipped from the InputStream.
     *
     * @return number of bytes read
     */
    public int getLength() {
        return length;
    }

    /**
     * Read the next {@link Color} from the InputStream. This reads 4 bytes.
     *
     * @return the next Color
     * @throws IOException an {@link IOException}
     */
    public Color readColor() throws IOException{
        int red = readByte();
        int green = readByte();
        int blue = readByte();
        readByte();
        return new DeviceRgb(red, green, blue);
    }
}
