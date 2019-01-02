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
import com.itextpdf.io.util.TextUtil;

/**
 * @author psoares
 */
public class CMapUniCid extends AbstractCMap {

    private static final long serialVersionUID = -6111821751136011584L;
    private IntHashtable map = new IntHashtable(65537);

    @Override
    void addChar(String mark, CMapObject code) {
        if (code.isNumber()) {
            int codePoint;
            String s = toUnicodeString(mark, true);
            if (TextUtil.isSurrogatePair(s, 0)) {
                codePoint = TextUtil.convertToUtf32(s, 0);
            } else {
                codePoint = (int) s.charAt(0);
            }
            map.put(codePoint, (int)code.getValue());
        }
    }

    public int lookup(int character) {
        return map.get(character);
    }

    public CMapToUnicode exportToUnicode() {
        CMapToUnicode uni = new CMapToUnicode();
        int[] keys = map.toOrderedKeys();
        for (int key : keys) {
            uni.addChar(map.get(key), TextUtil.convertFromUtf32(key));
        }
        int spaceCid = lookup(32);
        if (spaceCid != 0) {
            uni.addChar(spaceCid, TextUtil.convertFromUtf32(32));
        }
        return uni;
    }
}
