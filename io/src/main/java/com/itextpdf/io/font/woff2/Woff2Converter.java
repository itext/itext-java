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
package com.itextpdf.io.font.woff2;

public class Woff2Converter {

    public static boolean isWoff2Font(byte[] woff2Bytes) {
        if (woff2Bytes.length < 4) {
            return false;
        }
        Buffer file = new Buffer(woff2Bytes, 0,4);
        try {
            return file.readInt() == Woff2Common.kWoff2Signature;
        } catch (Exception any) {
            return false;
        }
    }

    public static byte[] convert(byte[] woff2Bytes) {
        byte[] inner_byte_buffer = new byte[Woff2Dec.computeWoff2FinalSize(woff2Bytes, woff2Bytes.length)];
        Woff2Out out = new Woff2MemoryOut(inner_byte_buffer, inner_byte_buffer.length);
        Woff2Dec.convertWoff2ToTtf(woff2Bytes, woff2Bytes.length, out);
        return inner_byte_buffer;
    }
}
