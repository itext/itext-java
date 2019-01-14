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
package com.itextpdf.io.font;

import com.itextpdf.io.font.constants.FontResources;
import com.itextpdf.io.util.ResourceUtil;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class AdobeGlyphList {

    private static Map<Integer, String> unicode2names = new HashMap<>();
    private static Map<String, Integer> names2unicode = new HashMap<>();

    static {
        InputStream resource = null;
        try {
            resource = ResourceUtil.getResourceStream(FontResources.ADOBE_GLYPH_LIST);
            if (resource == null) {
                throw new Exception(FontResources.ADOBE_GLYPH_LIST + " not found as resource.");
            }
            byte[] buf = new byte[1024];
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            while (true) {
                int size = resource.read(buf);
                if (size < 0) {
                    break;
                }
                stream.write(buf, 0, size);
            }
            resource.close();
            resource = null;
            String s = PdfEncodings.convertToString(stream.toByteArray(), null);
            StringTokenizer tk = new StringTokenizer(s, "\r\n");
            while (tk.hasMoreTokens()) {
                String line = tk.nextToken();
                if (line.startsWith("#")) {
                    continue;
                }
                StringTokenizer t2 = new StringTokenizer(line, " ;\r\n\t\f");
                if (!t2.hasMoreTokens()) {
                    continue;
                }
                String name = t2.nextToken();
                if (!t2.hasMoreTokens()) {
                    continue;
                }
                String hex = t2.nextToken();
                // AdobeGlyphList could contains symbols with marks, e.g.:
                // resh;05E8
                // reshhatafpatah;05E8 05B2
                // So in this case we will just skip this nam
                if (t2.hasMoreTokens()) {
                    continue;
                }
                int num = Integer.parseInt(hex, 16);
                unicode2names.put(num, name);
                names2unicode.put(name, num);
            }
        } catch (Exception e) {
            System.err.println("AdobeGlyphList.txt loading error: " + e.getMessage());
        } finally {
            if (resource != null) {
                try {
                    resource.close();
                } catch (Exception e) {
                    // empty on purpose
                }
            }
        }
    }

    public static int nameToUnicode(String name) {
        int v = -1;
        if (names2unicode.containsKey(name)) {
            v = (int) names2unicode.get(name);
        }
        if (v == -1 && name.length() == 7 && name.toLowerCase().startsWith("uni")) {
            try {
                return Integer.parseInt(name.substring(3), 16);
            } catch (Exception ignored) {
            }
        }
        return v;
    }

    public static String unicodeToName(int num) {
        return unicode2names.get(num);
    }

    public static int getNameToUnicodeLength() {
        return names2unicode.size();
    }

    public static int getUnicodeToNameLength() {
        return unicode2names.size();
    }
}
