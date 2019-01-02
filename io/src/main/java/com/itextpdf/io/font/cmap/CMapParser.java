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

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.io.source.PdfTokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class CMapParser {

    private static final String def = "def";
    private static final String endcidrange = "endcidrange";
    private static final String endcidchar = "endcidchar";
    private static final String endbfrange = "endbfrange";
    private static final String endbfchar = "endbfchar";
    private static final String endcodespacerange = "endcodespacerange";
    private static final String usecmap = "usecmap";

    private static final String Registry = "Registry";
    private static final String Ordering = "Ordering";
    private static final String Supplement = "Supplement";
    private static final String CMapName = "CMapName";

    private static final int MAX_LEVEL = 10;

    public static void parseCid(String cmapName, AbstractCMap cmap, ICMapLocation location) throws java.io.IOException {
        parseCid(cmapName, cmap, location, 0);
    }

    private static void parseCid(String cmapName, AbstractCMap cmap, ICMapLocation location, int level) throws java.io.IOException {
        if (level >= MAX_LEVEL)
            return;
        PdfTokenizer inp = location.getLocation(cmapName);
        try {
            List<CMapObject> list = new ArrayList<>();
            CMapContentParser cp = new CMapContentParser(inp);
            int maxExc = 50;
            while (true) {
                try {
                    cp.parse(list);
                } catch (Exception ex) {
                    if (--maxExc < 0)
                        break;
                    continue;
                }
                if (list.size() == 0)
                    break;
                String last = list.get(list.size() - 1).toString();
                if (level == 0 && list.size() == 3 && last.equals(def)) {
                    CMapObject cmapObject = list.get(0);
                    if (Registry.equals(cmapObject.toString())) {
                        cmap.setRegistry(list.get(1).toString());
                    } else if (Ordering.equals(cmapObject.toString())) {
                        cmap.setOrdering(list.get(1).toString());
                    } else if (CMapName.equals(cmapObject.toString())) {
                        cmap.setName(list.get(1).toString());
                    } else if (Supplement.equals(cmapObject.toString())) {
                        try {
                            cmap.setSupplement((int) list.get(1).getValue());
                        } catch (Exception ignored) {
                        }
                    }
                } else if ((last.equals(endcidchar) || last.equals(endbfchar)) && list.size() >= 3) {
                    int lMax = list.size() - 2;
                    for (int k = 0; k < lMax; k += 2) {
                        if (list.get(k).isString()) {
                            cmap.addChar(list.get(k).toString(), list.get(k + 1));
                        }
                    }
                } else if ((last.equals(endcidrange) || last.equals(endbfrange)) && list.size() >= 4) {
                    int lMax = list.size() - 3;
                    for (int k = 0; k < lMax; k += 3) {
                        if (list.get(k).isString() && list.get(k + 1).isString()) {
                            cmap.addRange(list.get(k).toString(), list.get(k + 1).toString(), list.get(k + 2));
                        }
                    }
                } else if (last.equals(usecmap) && list.size() == 2 && list.get(0).isName()) {
                    parseCid(list.get(0).toString(), cmap, location, level + 1);
                } else if (last.equals(endcodespacerange)) {
                    for (int i = 0; i < list.size() + 1; i += 2) {
                        if (list.get(i).isHexString() && list.get(i + 1).isHexString()) {
                            byte[] low = list.get(i).toHexByteArray();
                            byte[] high = list.get(i + 1).toHexByteArray();
                            cmap.addCodeSpaceRange(low, high);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            Logger logger = LoggerFactory.getLogger(CMapParser.class);
            logger.error(LogMessageConstant.UNKNOWN_ERROR_WHILE_PROCESSING_CMAP);
        } finally {
            inp.close();
        }
    }

//    private static void encodeSequence(int size, byte[] seqs, char cid, ArrayList<char[]> planes) {
//        --size;
//        int nextPlane = 0;
//        for (int idx = 0; idx < size; ++idx) {
//            char[] plane = planes.get(nextPlane);
//            int one = seqs[idx] & 0xff;
//            char c = plane[one];
//            if (c != 0 && (c & 0x8000) == 0)
//                throw new PdfRuntimeException("inconsistent.mapping");
//            if (c == 0) {
//                planes.add(new char[256]);
//                c = (char) (planes.size() - 1 | 0x8000);
//                plane[one] = c;
//            }
//            nextPlane = c & 0x7fff;
//        }
//        char[] plane = planes.get(nextPlane);
//        int one = seqs[size] & 0xff;
//        char c = plane[one];
//        if ((c & 0x8000) != 0)
//            throw new PdfRuntimeException("inconsistent.mapping");
//        plane[one] = cid;
//    }
}
