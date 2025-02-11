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
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.font.woff2.Woff2Converter;

public final class FontProgramDescriptorFactory {
    private static boolean FETCH_CACHED_FIRST = true;

    public static FontProgramDescriptor fetchDescriptor(String fontName) {
        if (fontName == null || fontName.length() == 0) {
            return null;
        }

        String baseName = FontProgram.trimFontStyle(fontName);
        //yes, we trying to find built-in standard font with original name, not baseName.
        boolean isBuiltinFonts14 = StandardFonts.isStandardFont(fontName);
        boolean isCidFont = !isBuiltinFonts14 && CjkResourceLoader.isPredefinedCidFont(baseName);

        FontProgramDescriptor fontDescriptor = null;
        if (FETCH_CACHED_FIRST) {
            fontDescriptor = fetchCachedDescriptor(fontName, null);
            if (fontDescriptor != null) {
                return fontDescriptor;
            }
        }

        try {
            String fontNameLowerCase = baseName.toLowerCase();
            if (isBuiltinFonts14 || fontNameLowerCase.endsWith(".afm") || fontNameLowerCase.endsWith(".pfm")) {
                fontDescriptor = fetchType1FontDescriptor(fontName, null);
            } else if (isCidFont) {
                fontDescriptor = fetchCidFontDescriptor(fontName);
            } else if (fontNameLowerCase.endsWith(".ttf") || fontNameLowerCase.endsWith(".otf")) {
                fontDescriptor = fetchTrueTypeFontDescriptor(fontName);
            } else if (fontNameLowerCase.endsWith(".woff") || fontNameLowerCase.endsWith(".woff2")) {
                byte[] fontProgram;
                if (fontNameLowerCase.endsWith(".woff")) {
                    fontProgram = WoffConverter.convert(FontProgramFactory.readFontBytesFromPath(baseName));
                } else {
                    fontProgram = Woff2Converter.convert(FontProgramFactory.readFontBytesFromPath(baseName));
                }
                fontDescriptor = fetchTrueTypeFontDescriptor(fontProgram);
            } else {
                fontDescriptor = fetchTTCDescriptor(baseName);
            }
        } catch (Exception ignored) {
            fontDescriptor = null;
        }

        return fontDescriptor;
    }

    public static FontProgramDescriptor fetchDescriptor(byte[] fontProgram) {
        if (fontProgram == null || fontProgram.length == 0) {
            return null;
        }

        FontProgramDescriptor fontDescriptor = null;
        if (FETCH_CACHED_FIRST) {
            fontDescriptor = fetchCachedDescriptor(null, fontProgram);
            if (fontDescriptor != null) {
                return fontDescriptor;
            }
        }

        try {
            fontDescriptor = fetchTrueTypeFontDescriptor(fontProgram);
        } catch (Exception ignored) {
        }
        if (fontDescriptor == null) {
            try {
                fontDescriptor = fetchType1FontDescriptor(null, fontProgram);
            } catch (Exception ignored) {
            }
        }
        return fontDescriptor;
    }

    public static FontProgramDescriptor fetchDescriptor(FontProgram fontProgram) {
        return fetchDescriptorFromFontProgram(fontProgram);
    }

    private static FontProgramDescriptor fetchCachedDescriptor(String fontName, byte[] fontProgram) {
        FontProgram fontFound;
        FontCacheKey key;
        if (fontName != null) {
            key = FontCacheKey.create(fontName);
        } else {
            key = FontCacheKey.create(fontProgram);
        }
        fontFound = FontCache.getFont(key);
        return fontFound != null ? fetchDescriptorFromFontProgram(fontFound) : null;
    }

    private static FontProgramDescriptor fetchTTCDescriptor(String baseName) throws java.io.IOException {
        int ttcSplit = baseName.toLowerCase().indexOf(".ttc,");
        if (ttcSplit > 0) {
            String ttcName;
            int ttcIndex;
            try {
                // count(.ttc) = 4
                ttcName = baseName.substring(0, ttcSplit + 4);
                // count(.ttc,) = 5)
                ttcIndex = Integer.parseInt(baseName.substring(ttcSplit + 5));
            } catch (NumberFormatException nfe) {
                throw new IOException(nfe.getMessage(), nfe);
            }
            OpenTypeParser parser = new OpenTypeParser(ttcName, ttcIndex);
            FontProgramDescriptor descriptor = fetchOpenTypeFontDescriptor(parser);
            parser.close();
            return descriptor;
        } else {
            return null;
        }
    }

    private static FontProgramDescriptor fetchTrueTypeFontDescriptor(String fontName) throws java.io.IOException {
        try (OpenTypeParser parser = new OpenTypeParser(fontName)) {
            return fetchOpenTypeFontDescriptor(parser);
        }
    }

    private static FontProgramDescriptor fetchTrueTypeFontDescriptor(byte[] fontProgram) throws java.io.IOException {
        try (OpenTypeParser parser = new OpenTypeParser(fontProgram)) {
            return fetchOpenTypeFontDescriptor(parser);
        }
    }

    private static FontProgramDescriptor fetchOpenTypeFontDescriptor(OpenTypeParser fontParser) throws java.io.IOException {
        fontParser.loadTables(false);
        return new FontProgramDescriptor(fontParser.getFontNames(), fontParser.getPostTable().italicAngle,
                fontParser.getPostTable().isFixedPitch);
    }

    private static FontProgramDescriptor fetchType1FontDescriptor(String fontName, byte[] afm) throws java.io.IOException {
        Type1Font fp = new Type1Font(fontName, null, afm, null);
        return new FontProgramDescriptor(fp.getFontNames(), fp.getFontMetrics());
    }

    private static FontProgramDescriptor fetchCidFontDescriptor(String fontName) {
        CidFont font = new CidFont(fontName, null, null);
        return new FontProgramDescriptor(font.getFontNames(), font.getFontMetrics());
    }

    private static FontProgramDescriptor fetchDescriptorFromFontProgram(FontProgram fontProgram) {
        return new FontProgramDescriptor(fontProgram.getFontNames(), fontProgram.getFontMetrics());
    }
}
