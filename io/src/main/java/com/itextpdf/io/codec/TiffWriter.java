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
import java.nio.charset.StandardCharsets;
import java.util.TreeMap;

/**
 * Exports images as TIFF.
 */
public class TiffWriter {
    private TreeMap<Integer, FieldBase> ifd = new TreeMap<Integer, FieldBase>();

    public void addField(FieldBase field) {
        ifd.put(Integer.valueOf(field.getTag()), field);
    }

    public int getIfdSize() {
        return 6 + ifd.size() * 12;
    }

    public void writeFile(OutputStream stream) throws IOException {
        stream.write(0x4d);
        stream.write(0x4d);
        stream.write(0);
        stream.write(42);
        writeLong(8, stream);
        writeShort(ifd.size(), stream);
        int offset = 8 + getIfdSize();
        for (FieldBase field : ifd.values()) {
            int size = field.getValueSize();
            if (size > 4) {
                field.setOffset(offset);
                offset += size;
            }
            field.writeField(stream);
        }
        writeLong(0, stream);
        for (FieldBase field : ifd.values()) {
            field.writeValue(stream);
        }
    }

    /**
     * Inner class class containing information about a field.
     */
    public abstract static class FieldBase {
        private int tag;
        private int fieldType;
        private int count;
        protected byte[] data;
        private int offset;

        protected FieldBase(int tag, int fieldType, int count) {
            this.tag = tag;
            this.fieldType = fieldType;
            this.count = count;
        }

        public int getValueSize() {
            return (int) ((data.length + 1) & 0xfffffffe);
        }

        public int getTag() {
            return tag;
        }

        public void setOffset(int offset) {
            this.offset = offset;
        }

        public void writeField(OutputStream stream) throws IOException {
            writeShort(tag, stream);
            writeShort(fieldType, stream);
            writeLong(count, stream);
            if (data.length <= 4) {
                stream.write(data);
                for (int k = data.length; k < 4; ++k) {
                    stream.write(0);
                }
            } else {
                writeLong(offset, stream);
            }
        }

        public void writeValue(OutputStream stream) throws IOException {
            if (data.length <= 4)
                return;
            stream.write(data);
            if ((data.length & 1) == 1)
                stream.write(0);
        }
    }

    /**
     * Inner class containing info about a field.
     */
    public static class FieldShort extends FieldBase {
        public FieldShort(int tag, int value) {
            super(tag, 3, 1);
            data = new byte[2];
            data[0] = (byte) (value >> 8);
            data[1] = (byte) value;
        }

        public FieldShort(int tag, int[] values) {
            super(tag, 3, values.length);
            data = new byte[values.length * 2];
            int ptr = 0;
            for (int value : values) {
                data[ptr++] = (byte) (value >> 8);
                data[ptr++] = (byte) value;
            }
        }
    }

    /**
     * Inner class containing info about a field.
     */
    public static class FieldLong extends FieldBase {
        public FieldLong(int tag, int value) {
            super(tag, 4, 1);
            data = new byte[4];
            data[0] = (byte) (value >> 24);
            data[1] = (byte) (value >> 16);
            data[2] = (byte) (value >> 8);
            data[3] = (byte) value;
        }

        public FieldLong(int tag, int[] values) {
            super(tag, 4, values.length);
            data = new byte[values.length * 4];
            int ptr = 0;
            for (int value : values) {
                data[ptr++] = (byte) (value >> 24);
                data[ptr++] = (byte) (value >> 16);
                data[ptr++] = (byte) (value >> 8);
                data[ptr++] = (byte) value;
            }
        }
    }

    /**
     * Inner class containing info about a field.
     */
    public static class FieldRational extends FieldBase {
        public FieldRational(int tag, int[] value) {
            this(tag, new int[][]{value});
        }

        public FieldRational(int tag, int[][] values) {
            super(tag, 5, values.length);
            data = new byte[values.length * 8];
            int ptr = 0;
            for (int value[] : values) {
                data[ptr++] = (byte) (value[0] >> 24);
                data[ptr++] = (byte) (value[0] >> 16);
                data[ptr++] = (byte) (value[0] >> 8);
                data[ptr++] = (byte) value[0];
                data[ptr++] = (byte) (value[1] >> 24);
                data[ptr++] = (byte) (value[1] >> 16);
                data[ptr++] = (byte) (value[1] >> 8);
                data[ptr++] = (byte) value[1];
            }
        }
    }

    /**
     * Inner class containing info about a field.
     */
    public static class FieldByte extends FieldBase {
        public FieldByte(int tag, byte[] values) {
            super(tag, 1, values.length);
            data = values;
        }
    }

    /**
     * Inner class containing info about a field.
     */
    public static class FieldUndefined extends FieldBase {
        public FieldUndefined(int tag, byte[] values) {
            super(tag, 7, values.length);
            data = values;
        }
    }

    /**
     * Inner class containing info about a field.
     */
    public static class FieldImage extends FieldBase {
        public FieldImage(byte[] values) {
            super(TIFFConstants.TIFFTAG_STRIPOFFSETS, 4, 1);
            data = values;
        }
    }

    /**
     * Inner class containing info about an ASCII field.
     */
    public static class FieldAscii extends FieldBase {
        public FieldAscii(int tag, String values) {
            super(tag, 2, values.getBytes(StandardCharsets.US_ASCII).length + 1);
            byte[] b = values.getBytes(StandardCharsets.US_ASCII);
            data = new byte[b.length + 1];
            System.arraycopy(b, 0, data, 0, b.length);
        }
    }

    public static void writeShort(int v, OutputStream stream) throws IOException {
        stream.write((v >> 8) & 0xff);
        stream.write(v & 0xff);
    }

    public static void writeLong(int v, OutputStream stream) throws IOException {
        stream.write((v >> 24) & 0xff);
        stream.write((v >> 16) & 0xff);
        stream.write((v >> 8) & 0xff);
        stream.write(v & 0xff);
    }

    public static void compressLZW(OutputStream stream, int predictor, byte[] b, int height, int samplesPerPixel, int stride) throws IOException {

        LZWCompressor lzwCompressor = new LZWCompressor(stream, 8, true);
        boolean usePredictor = predictor == TIFFConstants.PREDICTOR_HORIZONTAL_DIFFERENCING;

        if (!usePredictor) {
            lzwCompressor.compress(b, 0, b.length);
        } else {
            int off = 0;
            byte[] rowBuf = new byte[stride];
            for (int i = 0; i < height; i++) {
                System.arraycopy(b, off, rowBuf, 0, stride);
                for (int j = stride - 1; j >= samplesPerPixel; j--) {
                    rowBuf[j] -= rowBuf[j - samplesPerPixel];
                }
                lzwCompressor.compress(rowBuf, 0, stride);
                off += stride;
            }
        }

        lzwCompressor.flush();
    }
}
