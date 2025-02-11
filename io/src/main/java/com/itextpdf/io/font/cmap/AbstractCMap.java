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
package com.itextpdf.io.font.cmap;

import com.itextpdf.io.font.PdfEncodings;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCMap {


    private String cmapName;
    private String registry;
    private String ordering;
    private int supplement;
    
    public String getName() {
        return cmapName;
    }

    void setName(String cmapName) {
        this.cmapName = cmapName;
    }
    
    public String getOrdering() {
        return ordering;
    }

    void setOrdering(String ordering) {
        this.ordering = ordering;
    }
    
    public String getRegistry() {
        return registry;
    }

    void setRegistry(String registry) {
        this.registry = registry;
    }
    
    public int getSupplement() {
        return supplement;
    }
    
    void setSupplement(int supplement) {
        this.supplement = supplement;
    }

    abstract void addChar(String mark, CMapObject code);

    void addCodeSpaceRange(byte[] low, byte[] high) {
    }
    
    void addRange(String from, String to, CMapObject code) {
        byte[] a1 = decodeStringToByte(from);
        byte[] a2 = decodeStringToByte(to);
        if (a1.length != a2.length || a1.length == 0) {
            throw new IllegalArgumentException("Invalid map.");
        }
        byte[] sout = null;
        if (code.isString()) {
            sout = decodeStringToByte(code.toString());
        }
        int start = byteArrayToInt(a1);
        int end = byteArrayToInt(a2);
        for (int k = start; k <= end; ++k) {
            intToByteArray(k, a1);
            String mark = PdfEncodings.convertToString(a1, null);
            if (code.isArray()) {
                List<CMapObject> codes = (ArrayList<CMapObject>) code.getValue();
                addChar(mark, codes.get(k - start));
            } else if (code.isNumber()) {
                int nn = (int)code.getValue() + k - start;
                addChar(mark, new CMapObject(CMapObject.NUMBER, nn));
            } else if (code.isString()) {
                CMapObject s1 = new CMapObject(CMapObject.HEX_STRING, sout);
                addChar(mark, s1);
                assert sout != null;
                intToByteArray(byteArrayToInt(sout) + 1, sout);
            }
        }
    }
    
//    protected static byte[] toByteArray(String value) {
//        if (PdfEncodings.isPdfDocEncoding(value)) {
//            return PdfEncodings.convertToBytes(value, PdfEncodings.PDF_DOC_ENCODING);
//        } else {
//            return PdfEncodings.convertToBytes(value, null);
//        }
//    }

    public static byte[] decodeStringToByte(String range) {
        byte[] bytes = new byte[range.length()];
        for (int i = 0; i < range.length(); i++) {
            bytes[i] = (byte)range.charAt(i);
        }
        return bytes;
    }

    protected String toUnicodeString(String value, boolean isHexWriting) {
        byte[] bytes = decodeStringToByte(value);
        if (isHexWriting) {
            return PdfEncodings.convertToString(bytes, PdfEncodings.UNICODE_BIG_UNMARKED);
        } else {
            if (bytes.length >= 2 && bytes[0] == (byte)0xfe && bytes[1] == (byte)0xff) {
                return PdfEncodings.convertToString(bytes, PdfEncodings.UNICODE_BIG);
            } else {
                return PdfEncodings.convertToString(bytes, PdfEncodings.PDF_DOC_ENCODING);
            }
        }
    }

    private static void intToByteArray(int n, byte[] b) {
        for (int k = b.length - 1; k >= 0; --k) {
            b[k] = (byte)n;
            n = n >>> 8;
        }
    }

    private static int byteArrayToInt(byte[] b) {
        int n = 0;
        for (int k = 0; k < b.length; ++k) {
            n = n << 8;
            n |= b[k] & 0xff;
        }
        return n;
    }
}
