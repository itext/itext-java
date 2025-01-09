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

import com.itextpdf.io.source.ByteUtils;
import com.itextpdf.io.source.DeflaterOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Writes a PNG image.
 */
public class PngWriter {

    private static final byte[] PNG_SIGNTURE = {(byte) 137, 80, 78, 71, 13, 10, 26, 10};

    private static final byte[] IHDR = ByteUtils.getIsoBytes("IHDR");
    private static final byte[] PLTE = ByteUtils.getIsoBytes("PLTE");
    private static final byte[] IDAT = ByteUtils.getIsoBytes("IDAT");
    private static final byte[] IEND = ByteUtils.getIsoBytes("IEND");
    private static final byte[] iCCP = ByteUtils.getIsoBytes("iCCP");

    private static int[] crc_table;

    private OutputStream outp;

    public PngWriter(OutputStream outp) throws IOException {
        this.outp = outp;
        outp.write(PNG_SIGNTURE);
    }

    public void writeHeader(int width, int height, int bitDepth, int colorType) throws IOException {
        ByteArrayOutputStream ms = new ByteArrayOutputStream();
        outputInt(width, ms);
        outputInt(height, ms);
        ms.write(bitDepth);
        ms.write(colorType);
        ms.write(0);
        ms.write(0);
        ms.write(0);
        writeChunk(IHDR, ms.toByteArray());
    }

    public void writeEnd() throws IOException {
        writeChunk(IEND, new byte[0]);
    }

    public void writeData(byte[] data, int stride) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DeflaterOutputStream zip = new DeflaterOutputStream(stream);
        int k;
        for (k = 0; k < data.length - stride; k += stride) {
            zip.write(0);
            zip.write(data, k, stride);
        }
        int remaining = data.length - k;
        if (remaining > 0) {
            zip.write(0);
            zip.write(data, k, remaining);
        }
        zip.close();
        writeChunk(IDAT, stream.toByteArray());
    }

    public void writePalette(byte[] data) throws IOException {
        writeChunk(PLTE, data);
    }

    public void writeIccProfile(byte[] data) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        stream.write((byte) 'I');
        stream.write((byte) 'C');
        stream.write((byte) 'C');
        stream.write(0);
        stream.write(0);
        DeflaterOutputStream zip = new DeflaterOutputStream(stream);
        zip.write(data);
        zip.close();
        writeChunk(iCCP, stream.toByteArray());
    }

    private static void make_crc_table() {
        if (crc_table != null)
            return;
        int[] crc2 = new int[256];
        for (int n = 0; n < 256; n++) {
            int c = n;
            for (int k = 0; k < 8; k++) {
                if ((c & 1) != 0)
                    c = (int)(0xedb88320 ^ (c >>> 1));
                else
                    c = c >>> 1;
            }
            crc2[n] = c;
        }
        crc_table = crc2;
    }

    private static int update_crc(int crc, byte[] buf, int offset, int len) {
        int c = crc;

        if (crc_table == null)
            make_crc_table();
        for (int n = 0; n < len; n++) {
            c = crc_table[(c ^ buf[n + offset]) & 0xff] ^ (c >>> 8);
        }
        return c;
    }

    private static int crc(byte[] buf, int offset, int len) {
        return ~update_crc(-1, buf, offset, len);
    }

    private static int crc(byte[] buf) {
        return ~update_crc(-1, buf, 0, buf.length);
    }

    public void outputInt(int n) throws IOException {
        outputInt(n, outp);
    }

    public static void outputInt(int n, OutputStream s) throws IOException {
        s.write((byte) (n >> 24));
        s.write((byte) (n >> 16));
        s.write((byte) (n >> 8));
        s.write((byte) n);
    }

    public void writeChunk(byte[] chunkType, byte[] data) throws IOException {
        outputInt(data.length);
        outp.write(chunkType, 0, 4);
        outp.write(data);
        int c = update_crc(-1, chunkType, 0, chunkType.length);
        c = ~update_crc(c, data, 0, data.length);
        outputInt(c);
    }

}
