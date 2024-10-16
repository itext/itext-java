/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
package com.itextpdf.io.font.cmap;

import com.itextpdf.io.exceptions.IOException;

import java.util.ArrayList;
import java.util.List;

public class CMapByteCid extends AbstractCMap {


    protected static class Cursor {

        private int offset;
        private int length;

        public Cursor(int offset, int length) {
            this.offset = offset;
            this.length = length;
        }

        /**
         * Retrieves the offset of the object.
         *
         * @return offset value
         */
        public int getOffset() {
            return offset;
        }

        /**
         * Sets the offset of the object.
         *
         * @param offset offset value
         */
        public void setOffset(int offset) {
            this.offset = offset;
        }

        /**
         * Retrieves the length of the object.
         *
         * @return length value
         */
        public int getLength() {
            return length;
        }

        /**
         * Sets the length value of the object.
         *
         * @param length length value
         */
        public void setLength(int length) {
            this.length = length;
        }
    }

    private final List<int[]> planes = new ArrayList<>();

    public CMapByteCid() {
        planes.add(new int[256]);
    }

    @Override
    void addChar(String mark, CMapObject code) {
        if (code.isNumber()) {
            encodeSequence(decodeStringToByte(mark), (int) code.getValue());
        }
    }

    /**
     * Decode byte sequence.
     *
     * @param cidBytes byteCodeBytes
     * @param offset   number of bytes to skip before starting to return chars from the sequence
     * @param length   number of bytes to process
     * @return string that contains decoded representation of the given sequence
     */
    public String decodeSequence(byte[] cidBytes, int offset, int length) {
        StringBuilder sb = new StringBuilder();
        Cursor cursor = new Cursor(offset, length);
        int cid;
        while ((cid = decodeSingle(cidBytes, cursor)) >= 0) {
            sb.append((char)cid);
        }
        return sb.toString();
    }

    protected int decodeSingle(byte[] cidBytes, Cursor cursor) {
        int end = cursor.getOffset() + cursor.getLength();
        int currentPlane = 0;
        while (cursor.getOffset() < end) {
            int one = cidBytes[cursor.getOffset()] & 0xff;
            cursor.setOffset(cursor.getOffset() + 1);
            cursor.setLength(cursor.getLength() - 1);
            int[] plane = planes.get(currentPlane);
            int cid = plane[one];
            if ((cid & 0x8000) == 0) {
                return cid;
            } else {
                currentPlane = cid & 0x7fff;
            }
        }
        return -1;
    }

    private void encodeSequence(byte[] seq, int cid) {
        int size = seq.length - 1;
        int nextPlane = 0;
        for (int idx = 0; idx < size; ++idx) {
            int[] plane = planes.get(nextPlane);
            int one = seq[idx] & 0xff;
            int c = plane[one];
            if (c != 0 && (c & 0x8000) == 0)
                throw new IOException("Inconsistent mapping.");
            if (c == 0) {
                planes.add(new int[256]);
                c = (planes.size() - 1 | 0x8000);
                plane[one] = c;
            }
            nextPlane = c & 0x7fff;
        }
        int[] plane = planes.get(nextPlane);
        int one = seq[size] & 0xff;
        int c = plane[one];
        if ((c & 0x8000) != 0)
            throw new IOException("Inconsistent mapping.");
        plane[one] = cid;
    }
}
