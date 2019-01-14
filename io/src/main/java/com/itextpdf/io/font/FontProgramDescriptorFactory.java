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
        boolean isCidFont = !isBuiltinFonts14 && FontCache.isPredefinedCidFont(baseName);

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
                ttcName = baseName.substring(0, ttcSplit + 4); // count(.ttc) = 4
                ttcIndex = Integer.parseInt(baseName.substring(ttcSplit + 5)); // count(.ttc,) = 5)
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
        //TODO close original stream, may be separate static method should introduced
        Type1Font fp = new Type1Font(fontName, null, afm, null);
        return new FontProgramDescriptor(fp.getFontNames(), fp.getFontMetrics());
    }

    private static FontProgramDescriptor fetchCidFontDescriptor(String fontName) {
        CidFont font = new CidFont(fontName, null);
        return new FontProgramDescriptor(font.getFontNames(), font.getFontMetrics());
    }

    private static FontProgramDescriptor fetchDescriptorFromFontProgram(FontProgram fontProgram) {
        return new FontProgramDescriptor(fontProgram.getFontNames(), fontProgram.getFontMetrics());
    }
}
