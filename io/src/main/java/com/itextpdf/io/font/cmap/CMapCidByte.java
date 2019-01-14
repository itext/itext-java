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

import com.itextpdf.io.util.IntHashtable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author psoares
 */
public class CMapCidByte extends AbstractCMap {

    private static final long serialVersionUID = 4956059671207068672L;
    private Map<Integer, byte[]> map = new HashMap<>();
    private final byte[] EMPTY = {};
    private List<byte[]> codeSpaceRanges = new ArrayList<>();

    @Override
    void addChar(String mark, CMapObject code) {
        if (code.isNumber()) {
            byte[] ser = decodeStringToByte(mark);
            map.put((int)code.getValue(), ser);
        }
    }

    public byte[] lookup(int cid) {
        byte[] ser = map.get(cid);
        if (ser == null) {
            return EMPTY;
        } else {
            return ser;
        }
    }

    public IntHashtable getReversMap() {
        IntHashtable code2cid = new IntHashtable(map.size());
        for (int cid : map.keySet()) {
            byte[] bytes = map.get(cid);
            int byteCode = 0;
            for (byte b: bytes) {
                byteCode <<= 8;
                byteCode += b & 0xff;
            }
            code2cid.put(byteCode, cid);
        }
        return code2cid;
    }

    /**
     * Returns a list containing sequential pairs of code space beginning and endings:
     * (begincodespacerange1, endcodespacerange1, begincodespacerange2, endcodespacerange1, ...)
     */
    public List<byte[]> getCodeSpaceRanges() {
        return codeSpaceRanges;
    }

    @Override
    void addCodeSpaceRange(byte[] low, byte[] high) {
        codeSpaceRanges.add(low);
        codeSpaceRanges.add(high);
    }
}
