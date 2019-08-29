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

import com.itextpdf.io.IOException;
import com.itextpdf.io.font.cmap.AbstractCMap;
import com.itextpdf.io.font.cmap.CMapByteCid;
import com.itextpdf.io.font.cmap.CMapCidByte;
import com.itextpdf.io.font.cmap.CMapCidUni;
import com.itextpdf.io.font.cmap.CMapLocationResource;
import com.itextpdf.io.font.cmap.CMapParser;
import com.itextpdf.io.font.cmap.CMapUniCid;
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
import java.util.concurrent.ConcurrentHashMap;

public class FontCache {

    private static final Map<String, Map<String, Object>> allCidFonts = new HashMap<>();
    private static final Map<String, Set<String>> registryNames = new HashMap<>();

    private static final String CJK_REGISTRY_FILENAME = "cjk_registry.properties";
    private static final String FONTS_PROP = "fonts";
    private static final String REGISTRY_PROP = "Registry";
    private static final String W_PROP = "W";
    private static final String W2_PROP = "W2";

    private static Map<FontCacheKey, FontProgram> fontCache = new ConcurrentHashMap<>();

    static {
        try {
            loadRegistry();
            for (String font : registryNames.get(FONTS_PROP)) {
                allCidFonts.put(font, readFontProperties(font));
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * Checks if the font with the given name and encoding is one
     * of the predefined CID fonts.
     * @param fontName the font name.
     * @return {@code true} if it is CJKFont.
     */
    protected static boolean isPredefinedCidFont(String fontName) {
        if (!registryNames.containsKey(FONTS_PROP)) {
            return false;
        } else if (!registryNames.get(FONTS_PROP).contains(fontName)) {
            return false;
        }
        return true;
    }

    public static String getCompatibleCidFont(String cmap) {
        for (Map.Entry<String, Set<String>> e : registryNames.entrySet()) {
            if (e.getValue().contains(cmap)) {
                String registry = e.getKey();
                for (Map.Entry<String, Map<String, Object>> e1 : allCidFonts.entrySet()) {
                    if (registry.equals(e1.getValue().get(REGISTRY_PROP)))
                        return e1.getKey();
                }
            }
        }
        return null;
    }

    public static Set<String> getCompatibleCmaps(String fontName) {
        String registry = (String) FontCache.getAllPredefinedCidFonts().get(fontName).get(REGISTRY_PROP);
        return registryNames.get(registry);
    }

    public static Map<String, Map<String, Object>> getAllPredefinedCidFonts() {
        return allCidFonts;
    }

    public static Map<String, Set<String>> getRegistryNames() {
        return registryNames;
    }

    public static CMapCidUni getCid2UniCmap(String uniMap) {
        CMapCidUni cidUni = new CMapCidUni();
        return parseCmap(uniMap, cidUni);
    }

    public static CMapUniCid getUni2CidCmap(String uniMap) {
        CMapUniCid uniCid = new CMapUniCid();
        return parseCmap(uniMap, uniCid);
    }

    public static CMapByteCid getByte2CidCmap(String cmap) {
        CMapByteCid uniCid = new CMapByteCid();
        return parseCmap(cmap, uniCid);
    }

    public static CMapCidByte getCid2Byte(String cmap) {
        CMapCidByte cidByte = new CMapCidByte();
        return parseCmap(cmap, cidByte);
    }

    /**
     * Clears the cache by removing fonts that were added via {@link #saveFont(FontProgram, String)}.
     * <p>
     * Be aware that in multithreading environment this method call will affect the result of {@link #getFont(String)}.
     * This in its turn affects creation of fonts via factories when {@code cached} argument is set to true (which is by default).
     */
    public static void clearSavedFonts() {
        fontCache.clear();
    }

    public static FontProgram getFont(String fontName) {
        return fontCache.get(FontCacheKey.create(fontName));
    }

    static FontProgram getFont(FontCacheKey key) {
        return fontCache.get(key);
    }

    public static FontProgram saveFont(FontProgram font, String fontName) {
        return saveFont(font, FontCacheKey.create(fontName));
    }

    static FontProgram saveFont(FontProgram font, FontCacheKey key) {
        FontProgram fontFound = fontCache.get(key);
        if (fontFound != null) {
            return fontFound;
        }
        fontCache.put(key, font);
        return font;
    }

    private static void loadRegistry() throws java.io.IOException {
        InputStream resource = ResourceUtil.getResourceStream(FontResources.CMAPS + CJK_REGISTRY_FILENAME);
        try {
            Properties p = new Properties();
            p.load(resource);

            for (Map.Entry<Object, Object> entry : p.entrySet()) {
                String value = (String) entry.getValue();
                String[] splitValue = value.split(" ");
                Set<String> set = new HashSet<>();

                for (String s : splitValue) {
                    if (s.length() != 0) {
                        set.add(s);
                    }
                }

                registryNames.put((String) entry.getKey(), set);
            }
        } finally {
            if (resource != null) {
                resource.close();
            }
        }
    }

    private static Map<String, Object> readFontProperties(String name) throws java.io.IOException {
        InputStream resource = ResourceUtil.getResourceStream(FontResources.CMAPS + name + ".properties");

        try {
            Properties p = new Properties();
            p.load(resource);

            Map<String, Object> fontProperties = new HashMap<>();
            for (Map.Entry<Object, Object> entry : p.entrySet()) {
                fontProperties.put((String) entry.getKey(), entry.getValue());
            }
            fontProperties.put(W_PROP, createMetric((String) fontProperties.get(W_PROP)));
            fontProperties.put(W2_PROP, createMetric((String) fontProperties.get(W2_PROP)));

            return fontProperties;
        } finally {
            if (resource != null) {
                resource.close();
            }
        }
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

    private static <T extends AbstractCMap> T parseCmap(String name, T cmap) {
        try {
            CMapParser.parseCid(name, cmap, new CMapLocationResource());
        } catch (java.io.IOException e) {
            throw new IOException(IOException.IoException, e);
        }
        return cmap;
    }
}
