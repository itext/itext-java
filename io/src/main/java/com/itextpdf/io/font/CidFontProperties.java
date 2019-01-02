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
import com.itextpdf.io.util.IntHashtable;
import com.itextpdf.io.util.ResourceUtil;

import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

public class CidFontProperties {

    private static final Map<String, Map<String, Object>> allFonts = new HashMap<>();
    private static final Map<String, Set<String>> registryNames = new HashMap<>();

    static {
        try {
            loadRegistry();
            for (String font : registryNames.get("fonts")) {
                allFonts.put(font, readFontProperties(font));
            }
        } catch (Exception ignored) { }
    }

    /** Checks if its a valid CJKFont font.
     * @param fontName the font name.
     * @param enc the encoding.
     * @return {@code true} if it is CJKFont.
     */
    public static boolean isCidFont(String fontName, String enc) {
        if (!registryNames.containsKey("fonts"))
            return false;
        if (!registryNames.get("fonts").contains(fontName))
            return false;
        if (enc.equals(PdfEncodings.IDENTITY_H) || enc.equals(PdfEncodings.IDENTITY_V))
            return true;
        String registry = (String)allFonts.get(fontName).get("Registry");
        Set<String> encodings = registryNames.get(registry);
        return encodings != null && encodings.contains(enc);
    }

    public static String getCompatibleFont(String enc) {
        for (Map.Entry<String,Set<String>> e : registryNames.entrySet()) {
            if (e.getValue().contains(enc)) {
                String registry = e.getKey();
                for (Map.Entry<String, Map<String, Object>> e1 : allFonts.entrySet()) {
                    if (registry.equals(e1.getValue().get("Registry")))
                        return e1.getKey();
                }
            }
        }
        return null;
    }

    public static Map<String, Map<String, Object>> getAllFonts() {
        return allFonts;
    }

    public static Map<String, Set<String>> getRegistryNames() {
        return registryNames;
    }

    private static void loadRegistry() throws java.io.IOException {
        InputStream resource = ResourceUtil.getResourceStream(FontResources.CMAPS + "cjk_registry.properties");
        Properties p = new Properties();
        p.load(resource);
        resource.close();
        for (Object key : p.keySet()) {
            String value = p.getProperty((String)key);
            String[] sp = value.split(" ");
            Set<String> hs = new HashSet<String>();
            for (String s : sp) {
                if (s.length() > 0)
                    hs.add(s);
            }
            registryNames.put((String)key, hs);
        }
    }

    private static Map<String, Object> readFontProperties(String name) throws java.io.IOException {
        name += ".properties";
        InputStream resource = ResourceUtil.getResourceStream(FontResources.CMAPS + name);
        Properties p = new Properties();
        p.load(resource);
        resource.close();
        IntHashtable W = createMetric(p.getProperty("W"));
        p.remove("W");
        IntHashtable W2 = createMetric(p.getProperty("W2"));
        p.remove("W2");
        Map<String, Object> map = new HashMap<String, Object>();
        for (Object obj : p.keySet()) {
            map.put((String)obj, p.getProperty((String)obj));
        }
        map.put("W", W);
        map.put("W2", W2);
        return map;
    }

    private static IntHashtable createMetric(String s) {
        IntHashtable h = new IntHashtable();
        StringTokenizer tk = new StringTokenizer(s);
        while (tk.hasMoreTokens()) {
            int n1 = Integer.parseInt(tk.nextToken());
            h.put(n1, Integer.parseInt(tk.nextToken()));
        }
        return h;
    }
}
