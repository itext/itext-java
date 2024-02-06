/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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

import com.itextpdf.io.font.cmap.CMapByteCid;
import com.itextpdf.io.font.cmap.CMapCidToCodepoint;
import com.itextpdf.io.font.cmap.CMapCidUni;
import com.itextpdf.io.font.cmap.CMapCodepointToCid;
import com.itextpdf.io.font.cmap.CMapUniCid;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class FontCache {

    private static Map<FontCacheKey, FontProgram> fontCache = new ConcurrentHashMap<>();

    /**
     * Checks if the font with the given name and encoding is one
     * of the predefined CID fonts.
     *
     * @param fontName the font name.
     * @return {@code true} if it is CJKFont.
     * @deprecated in favour of {@link CjkResourceLoader}.
     */
    @Deprecated
    protected static boolean isPredefinedCidFont(String fontName) {
        return CjkResourceLoader.isPredefinedCidFont(fontName);
    }

    /**
     * Finds a CJK font family which is compatible to the given CMap.
     *
     * @param cmap a name of the CMap for which compatible font is searched.
     * @return a CJK font name if there's known compatible font for the given cmap name, or null otherwise.
     * @deprecated in favour of {@link CjkResourceLoader}.
     */
    @Deprecated
    public static String getCompatibleCidFont(String cmap) {
        return CjkResourceLoader.getCompatibleCidFont(cmap);
    }

    /**
     * Finds all CMap names that belong to the same registry to which a given
     * font belongs.
     *
     * @param fontName a name of the font for which CMap's are searched.
     * @return a set of CMap names corresponding to the given font.
     * @deprecated in favour of {@link CjkResourceLoader}.
     */
    @Deprecated
    public static Set<String> getCompatibleCmaps(String fontName) {
        return CjkResourceLoader.getCompatibleCmaps(fontName);
    }

    @Deprecated
    public static Map<String, Map<String, Object>> getAllPredefinedCidFonts() {
        return CjkResourceLoader.getAllPredefinedCidFonts();
    }

    @Deprecated
    public static Map<String, Set<String>> getRegistryNames() {
        return CjkResourceLoader.getRegistryNames();
    }

    /**
     * Parses CMap with a given name producing it in a form of cid to unicode mapping.
     *
     * @param uniMap a CMap name. It is expected that CMap identified by this name defines unicode to cid mapping.
     * @return an object for convenient mapping from cid to unicode. If no CMap was found for provided name an exception is thrown.
     * @deprecated in favour of {@link CjkResourceLoader}.
     */
    @Deprecated
    public static CMapCidUni getCid2UniCmap(String uniMap) {
        return CjkResourceLoader.getCid2UniCmap(uniMap);
    }

    @Deprecated
    public static CMapUniCid getUni2CidCmap(String uniMap) {
        return CjkResourceLoader.getUni2CidCmap(uniMap);
    }

    @Deprecated
    public static CMapByteCid getByte2CidCmap(String cmap) {
        return CjkResourceLoader.getByte2CidCmap(cmap);
    }

    @Deprecated
    public static CMapCidToCodepoint getCidToCodepointCmap(String cmap) {
        return CjkResourceLoader.getCidToCodepointCmap(cmap);
    }

    @Deprecated
    public static CMapCodepointToCid getCodepointToCidCmap(String uniMap) {
        return CjkResourceLoader.getCodepointToCidCmap(uniMap);
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
}
