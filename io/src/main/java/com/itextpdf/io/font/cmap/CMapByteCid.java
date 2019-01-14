/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.io.font.cmap;

import com.itextpdf.io.IOException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author psoares
 */
public class CMapByteCid extends AbstractCMap {

    private static final long serialVersionUID = 8843696844192313477L;

    protected static class Cursor {

        public int offset;
        public int length;

        public Cursor(int offset, int length) {
            this.offset = offset;
            this.length = length;
        }
    }

    private List<char[]> planes = new ArrayList<>();

    public CMapByteCid() {
        planes.add(new char[256]);
    }

    @Override
    void addChar(String mark, CMapObject code) {
        if (code.isNumber()) {
            encodeSequence(decodeStringToByte(mark), (char)code.getValue());
        }
    }

    /**
     *
     * @param cidBytes
     * @param offset
     * @param length
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
        int end = cursor.offset + cursor.length;
        int currentPlane = 0;
        while (cursor.offset < end) {
            int one = cidBytes[cursor.offset++] & 0xff;
            cursor.length--;
            char[] plane = planes.get(currentPlane);
            int cid = plane[one];
            if ((cid & 0x8000) == 0) {
                return cid;
            } else {
                currentPlane = cid & 0x7fff;
            }
        }
        return -1;
    }

    private void encodeSequence(byte[] seq, char cid) {
        int size = seq.length - 1;
        int nextPlane = 0;
        for (int idx = 0; idx < size; ++idx) {
            char[] plane = planes.get(nextPlane);
            int one = seq[idx] & 0xff;
            char c = plane[one];
            if (c != 0 && (c & 0x8000) == 0)
                throw new IOException("Inconsistent mapping.");
            if (c == 0) {
                planes.add(new char[256]);
                c = (char)(planes.size() - 1 | 0x8000);
                plane[one] = c;
            }
            nextPlane = c & 0x7fff;
        }
        char[] plane = planes.get(nextPlane);
        int one = seq[size] & 0xff;
        char c = plane[one];
        if ((c & 0x8000) != 0)
            throw new IOException("Inconsistent mapping.");
        plane[one] = cid;
    }
}
