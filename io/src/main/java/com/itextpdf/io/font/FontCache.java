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

    private static final Map<FontCacheKey, FontProgram> fontCache = new ConcurrentHashMap<>();

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
