/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV
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
import com.itextpdf.io.util.ArrayUtil;

public final class FontNamesFactory {
    @SuppressWarnings("FieldCanBeLocal")
    private static boolean FETCH_CACHED_FIRST = true;

    public static FontNames fetchFontNames(String fontName) {
        if (fontName == null || fontName.length() == 0) {
            return null;
        }

        String baseName = FontProgram.getBaseName(fontName);
        //yes, we trying to find built-in standard font with original name, not baseName.
        boolean isBuiltinFonts14 = FontConstants.BUILTIN_FONTS_14.contains(fontName);
        boolean isCidFont = !isBuiltinFonts14 && FontCache.isPredefinedCidFont(baseName);

        FontNames fontNames = null;
        if (FETCH_CACHED_FIRST) {
            fontNames = fetchCachedFontNames(fontName, null);
            if (fontNames != null) {
                return fontNames;
            }
        }

        try {
            if (isBuiltinFonts14 || fontName.toLowerCase().endsWith(".afm") || fontName.toLowerCase().endsWith(".pfm")) {
                fontNames = fetchType1Names(fontName, null);
            } else if (isCidFont) {
                fontNames = fetchCidFontNames(fontName);
            } else if (baseName.toLowerCase().endsWith(".ttf") || baseName.toLowerCase().endsWith(".otf")) {
                fontNames = fetchTrueTypeNames(fontName);
            } else {
                fontNames = fetchTTCNames(baseName);
            }
        } catch (Exception ignored) {
            fontNames = null;
        }

        return fontNames;
    }

    public static FontNames fetchFontNames(byte[] fontProgram) {
        if (fontProgram == null || fontProgram.length == 0) {
            return null;
        }

        FontNames fontNames = null;
        if (FETCH_CACHED_FIRST) {
            fontNames = fetchCachedFontNames(null, fontProgram);
            if (fontNames != null) {
                return fontNames;
            }
        }

        try {
            fontNames = fetchTrueTypeNames(fontProgram);
        } catch (Exception ignored) {
        }
        if (fontNames == null) {
            try {
                fontNames = fetchType1Names(null, fontProgram);
            } catch (Exception ignored) {
            }
        }
        return fontNames;
    }

    private static FontNames fetchCachedFontNames(String fontName, byte[] fontProgram)  {
        FontProgram fontFound;
        FontCacheKey key; 
        if (fontName != null) {
            key = FontCacheKey.create(fontName);
        } else {
            key = FontCacheKey.create(fontProgram);
        }
        fontFound = FontCache.getFont(key);
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

    private static FontNames fetchTrueTypeNames(String fontName) throws java.io.IOException {
        try(OpenTypeParser parser = new OpenTypeParser(fontName)) {
            return fetchOpenTypeNames(parser);
        }
    }

    private static FontNames fetchTrueTypeNames(byte[] fontProgram) throws java.io.IOException {
        try(OpenTypeParser parser = new OpenTypeParser(fontProgram)) {
            return fetchOpenTypeNames(parser);
        }
    }

    private static FontNames fetchOpenTypeNames(OpenTypeParser fontParser) throws java.io.IOException {
        fontParser.loadTables(false);
        return fontParser.getFontNames();
    }

    private static FontNames fetchType1Names(String fontName, byte[] afm) throws java.io.IOException {
        //TODO close original stream, may be separate static method should introduced
        Type1Font fp = new Type1Font(fontName, null, afm, null);
        return fp.getFontNames();
    }

    private static FontNames fetchCidFontNames(String fontName) {
        CidFont font = new CidFont(fontName, null);
        return font.getFontNames();
    }
}
