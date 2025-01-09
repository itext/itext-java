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

import com.itextpdf.io.exceptions.IOException;
import com.itextpdf.io.exceptions.IoExceptionMessageConstant;
import com.itextpdf.io.font.cmap.AbstractCMap;
import com.itextpdf.io.font.cmap.CMapByteCid;
import com.itextpdf.io.font.cmap.CMapCidToCodepoint;
import com.itextpdf.io.font.cmap.CMapCidUni;
import com.itextpdf.io.font.cmap.CMapCodepointToCid;
import com.itextpdf.io.font.cmap.CMapLocationResource;
import com.itextpdf.io.font.cmap.CMapParser;
import com.itextpdf.io.font.cmap.CMapUniCid;
import com.itextpdf.io.util.IntHashtable;
import com.itextpdf.io.util.ResourceUtil;

import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * This class is responsible for loading and handling CJK fonts and CMaps from font-asian package.
 */
public final class CjkResourceLoader {

    private static final Map<String, Map<String, Object>> allCidFonts = new LinkedHashMap<>();
    private static final Map<String, Set<String>> registryNames = new HashMap<>();

    private static final String CJK_REGISTRY_FILENAME = "cjk_registry.properties";
    private static final String FONTS_PROP = "fonts";
    private static final String REGISTRY_PROP = "Registry";
    private static final String W_PROP = "W";
    private static final String W2_PROP = "W2";

    private static CMapLocationResource cmapLocation;

    private CjkResourceLoader() {
    }

    static {
        CjkResourceLoader.setCmapLocation(new CMapLocationResource());
    }

    /**
     * Checks if the font with the given name and encoding is one
     * of the predefined CID fonts.
     *
     * @param fontName the font name.
     * @return {@code true} if it is CJKFont.
     */
    public static boolean isPredefinedCidFont(String fontName) {
        if (!registryNames.containsKey(FONTS_PROP)) {
            return false;
        } else if (!registryNames.get(FONTS_PROP).contains(fontName)) {
            return false;
        }
        return true;
    }

    /**
     * Finds a CJK font family which is compatible to the given CMap.
     *
     * @param cmap a name of the CMap for which compatible font is searched.
     * @return a CJK font name if there's known compatible font for the given cmap name, or null otherwise.
     */
    public static String getCompatibleCidFont(String cmap) {
        for (Map.Entry<String, Set<String>> e : registryNames.entrySet()) {
            if (e.getValue().contains(cmap)) {
                String registry = e.getKey();
                for (Map.Entry<String, Map<String, Object>> e1 : allCidFonts.entrySet()) {
                    if (registry.equals(e1.getValue().get(REGISTRY_PROP))) {
                        return e1.getKey();
                    }
                }
            }
        }
        return null;
    }

    /**
     * Finds all CMap names that belong to the same registry to which a given
     * font belongs.
     *
     * @param fontName a name of the font for which CMap's are searched.
     * @return a set of CMap names corresponding to the given font.
     */
    public static Set<String> getCompatibleCmaps(String fontName) {
        Map<String, Object> cidFonts = CjkResourceLoader.getAllPredefinedCidFonts().get(fontName);
        if (cidFonts == null) {
            return null;
        }
        String registry = (String) cidFonts.get(REGISTRY_PROP);
        return registryNames.get(registry);
    }

    /**
     * Get all loaded predefined CID fonts.
     *
     * @return predefined CID fonts.
     */
    public static Map<String, Map<String, Object>> getAllPredefinedCidFonts() {
        return allCidFonts;
    }

    /**
     * Get all loaded CJK registry names mapped to a set of compatible cmaps.
     *
     * @return CJK registry names mapped to a set of compatible cmaps.
     */
    public static Map<String, Set<String>> getRegistryNames() {
        return registryNames;
    }

    /**
     * Parses CMap with a given name producing it in a form of cid to unicode mapping.
     *
     * @param uniMap a CMap name. It is expected that CMap identified by this name defines unicode to cid mapping.
     * @return an object for convenient mapping from cid to unicode. If no CMap was found for provided name
     * an exception is thrown.
     */
    public static CMapCidUni getCid2UniCmap(String uniMap) {
        CMapCidUni cidUni = new CMapCidUni();
        return parseCmap(uniMap, cidUni);
    }

    /**
     * Parses CMap with a given name producing it in a form of unicode to cid mapping.
     *
     * @param uniMap a CMap name. It is expected that CMap identified by this name defines unicode to cid mapping.
     * @return an object for convenient mapping from unicode to cid. If no CMap was found for provided name
     * an exception is thrown.
     */
    public static CMapUniCid getUni2CidCmap(String uniMap) {
        return parseCmap(uniMap, new CMapUniCid());
    }

    /**
     * Parses CMap with a given name producing it in a form of byte to cid mapping.
     *
     * @param cmap a CMap name. It is expected that CMap identified by this name defines byte to cid mapping.
     * @return an object for convenient mapping from byte to cid. If no CMap was found for provided name
     * an exception is thrown.
     */
    public static CMapByteCid getByte2CidCmap(String cmap) {
        CMapByteCid uniCid = new CMapByteCid();
        return parseCmap(cmap, uniCid);
    }

    /**
     * Parses CMap with a given name producing it in a form of cid to code point mapping.
     *
     * @param cmap a CMap name. It is expected that CMap identified by this name defines code point to cid mapping.
     * @return an object for convenient mapping from cid to code point. If no CMap was found for provided name
     * an exception is thrown.
     */
    public static CMapCidToCodepoint getCidToCodepointCmap(String cmap) {
        CMapCidToCodepoint cidByte = new CMapCidToCodepoint();
        return parseCmap(cmap, cidByte);
    }

    /**
     * Parses CMap with a given name producing it in a form of code point to cid mapping.
     *
     * @param uniMap a CMap name. It is expected that CMap identified by this name defines code point to cid mapping.
     * @return an object for convenient mapping from code point to cid. If no CMap was found for provided name
     * an exception is thrown.
     */
    public static CMapCodepointToCid getCodepointToCidCmap(String uniMap) {
        return parseCmap(uniMap, new CMapCodepointToCid());
    }

    static void setCmapLocation(CMapLocationResource cmapLocation) {
        CjkResourceLoader.cmapLocation = cmapLocation;
        try {
            loadRegistry();
        } catch (Exception ignored) {
        }
    }

    private static void loadRegistry() throws java.io.IOException {
        registryNames.clear();
        allCidFonts.clear();

        InputStream resource = ResourceUtil.getResourceStream(cmapLocation.getLocationPath() + CJK_REGISTRY_FILENAME);
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

        for (String font : registryNames.get(FONTS_PROP)) {
            allCidFonts.put(font, readFontProperties(font));
        }
    }

    private static Map<String, Object> readFontProperties(String name) throws java.io.IOException {
        InputStream resource = ResourceUtil.getResourceStream(cmapLocation.getLocationPath() + name + ".properties");

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
            CMapParser.parseCid(name, cmap, cmapLocation);
        } catch (java.io.IOException e) {
            throw new IOException(IoExceptionMessageConstant.IO_EXCEPTION, e);
        }
        return cmap;
    }
}
