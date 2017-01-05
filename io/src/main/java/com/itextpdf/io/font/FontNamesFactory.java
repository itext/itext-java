package com.itextpdf.io.font;

import com.itextpdf.io.IOException;
import com.itextpdf.io.util.ArrayUtil;

public final class FontNamesFactory {
    @SuppressWarnings("FieldCanBeLocal")
    private static boolean FETCH_CACHED_FIRST = true;

    public static FontNames fetchFontNames(String fontName, byte[] fontProgram) {
        String baseName = FontProgram.getBaseName(fontName);

        //yes, we trying to find built-in standard font with original name, not baseName.
        boolean isBuiltinFonts14 = FontConstants.BUILTIN_FONTS_14.contains(fontName);
        boolean isCidFont = !isBuiltinFonts14 && FontCache.isPredefinedCidFont(baseName);

        FontNames fontNames = null;
        if (FETCH_CACHED_FIRST) {
            fontNames = fetchCachedFontNames(fontName, fontProgram);
            if (fontNames != null) {
                return fontNames;
            }
        }

        if (fontName == null) {
            if (fontProgram != null) {
                try {
                    fontNames = fetchTrueTypeNames(null, fontProgram);
                } catch (Exception ignored) {
                }
                if (fontNames == null) {
                    try {
                        fontNames = fetchType1Names(null, fontProgram);
                    } catch (Exception ignored) {
                    }
                }
            }
        } else {
            try {
                if (isBuiltinFonts14 || fontName.toLowerCase().endsWith(".afm") || fontName.toLowerCase().endsWith(".pfm")) {
                    fontNames = fetchType1Names(fontName, null);
                } else if (isCidFont) {
                    fontNames = fetchCidFontNames(fontName);
                } else if (baseName.toLowerCase().endsWith(".ttf") || baseName.toLowerCase().endsWith(".otf")) {
                    fontNames = fetchTrueTypeNames(fontName, fontProgram);
                } else {
                    fontNames = fetchTTCNames(baseName);
                }
            } catch (Exception ignored) {
                fontNames = null;
            }
        }
        return fontNames;
    }

    private static FontNames fetchCachedFontNames(String fontName, byte[] fontProgram)  {
        String fontKey;
        if (fontName != null) {
            fontKey = fontName;
        } else {
            fontKey = Integer.toString(ArrayUtil.hashCode(fontProgram));
        }
        FontProgram fontFound = FontCache.getFont(fontKey);
        return fontFound != null ? fontFound.getFontNames() : null;
    }

    private static FontNames fetchTTCNames(String baseName) throws java.io.IOException {
        int ttcSplit = baseName.toLowerCase().indexOf(".ttc,");
        if (ttcSplit > 0) {
            String ttcName;
            int ttcIndex;
            try {
                ttcName = baseName.substring(0, ttcSplit + 4);//count(.ttc) = 4
                ttcIndex = Integer.parseInt(baseName.substring(ttcSplit + 5));//count(.ttc,) = 5)
            } catch (NumberFormatException nfe) {
                throw new IOException(nfe.getMessage(), nfe);
            }
            OpenTypeParser parser = new OpenTypeParser(ttcName, ttcIndex);
            FontNames names = fetchOpenTypeNames(parser);
            parser.close();
            return names;
        } else {
            return null;
        }
    }

    private static FontNames fetchTrueTypeNames(String fontName, byte[] fontProgram) throws java.io.IOException {
        OpenTypeParser parser;
        if (fontName != null) {
            parser = new OpenTypeParser(fontName);
        } else {
            parser = new OpenTypeParser(fontProgram);
        }
        FontNames names = fetchOpenTypeNames(parser);
        parser.close();
        return names;
    }

    private static FontNames fetchOpenTypeNames(OpenTypeParser fontParser) throws java.io.IOException {
        fontParser.loadTables(false);
        return fontParser.getFontNames();
    }

    private static FontNames fetchType1Names(String fontName, byte[] afm) throws java.io.IOException {
        Type1Font fp = new Type1Font(fontName, null, afm, null);
        return fp.getFontNames();
    }

    private static FontNames fetchCidFontNames(String fontName) {
        CidFont font = new CidFont(fontName, null);
        return font.getFontNames();
    }
}
