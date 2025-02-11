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
package com.itextpdf.io.font;

import com.itextpdf.io.font.constants.FontResources;
import com.itextpdf.io.util.IntHashtable;
import com.itextpdf.io.util.ResourceUtil;

import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashSet;
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
            Set<String> hs = new LinkedHashSet<String>();
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
