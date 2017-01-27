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
package com.itextpdf.layout.font;

import com.itextpdf.io.font.FontCacheKey;
import com.itextpdf.io.font.FontNames;
import com.itextpdf.io.font.FontNamesFactory;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.util.ArrayUtil;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.font.PdfFont;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Contains all font related data to create {@link FontProgram} and {@link PdfFont}.
 * {@link FontNames} fetches with {@link FontNamesFactory}.
 */
public final class FontProgramInfo {

    private static final Map<FontCacheKey, FontNames> fontNamesCache = new ConcurrentHashMap<>();

    private final String fontName;
    private final byte[] fontProgram;
    private final String encoding;
    private final FontNames names;
    private final int hash;

    private FontProgramInfo(String fontName, byte[] fontProgram, String encoding, FontNames names) {
        this.fontName = fontName;
        this.fontProgram = fontProgram;
        this.encoding = encoding;
        this.names = names;
        this.hash = calculateHashCode(fontName, fontProgram, encoding);
    }

    static FontProgramInfo create(FontProgram fontProgram, String encoding) {
        return new FontProgramInfo(fontProgram.getFontNames().getFontName(), null, encoding, fontProgram.getFontNames());
    }

    static FontProgramInfo create(String fontName, String encoding) {
        FontCacheKey cacheKey = FontCacheKey.create(fontName);
        FontNames names = getFontNamesFromCache(cacheKey);
        if (names == null) {
            names = FontNamesFactory.fetchFontNames(fontName);
            putFontNamesToCache(cacheKey, names);
        }
        return names != null ? new FontProgramInfo(fontName, null, encoding, names) : null;
    }

    static FontProgramInfo create(byte[] fontProgram, String encoding) {
        FontCacheKey cacheKey = FontCacheKey.create(fontProgram);
        FontNames names = getFontNamesFromCache(cacheKey);
        if (names == null) {
            names = FontNamesFactory.fetchFontNames(fontProgram);
            putFontNamesToCache(cacheKey, names);
        }
        return names != null ? new FontProgramInfo(null, fontProgram, encoding, names) : null;
    }

    public PdfFont getPdfFont(FontProvider fontProvider) {
        try {
            return fontProvider.getPdfFont(this);
        } catch (IOException e) {
            throw new PdfException(PdfException.IoExceptionWhileCreatingFont, e);
        }
    }

    public FontNames getNames() {
        return names;
    }

    public String getFontName() {
        return fontName;
    }

    public byte[] getFontProgram() {
        return fontProgram;
    }

    public String getEncoding() {
        return encoding;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FontProgramInfo)) return false;

        FontProgramInfo that = (FontProgramInfo) o;
        return (fontName != null ? fontName.equals(that.fontName) : that.fontName == null)
                && Arrays.equals(fontProgram, that.fontProgram)
                && (encoding != null ? encoding.equals(that.encoding) : that.encoding == null);
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public String toString() {
        String name = names.getFontName();
        if (name.length() > 0) {
            if (encoding != null) {
                return String.format("%s+%s", name, encoding);
            } else {
                return name;
            }
        }
        return super.toString();
    }

    private static int calculateHashCode(String fontName, byte[] bytes, String encoding) {
        int result = fontName != null ? fontName.hashCode() : 0;
        result = 31 * result + ArrayUtil.hashCode(bytes);
        result = 31 * result + (encoding != null ? encoding.hashCode() : 0);
        return result;
    }

    private static FontNames getFontNamesFromCache(FontCacheKey key) {
        return fontNamesCache.get(key);
    }

    private static void putFontNamesToCache(FontCacheKey key, FontNames names) {
        if (names != null) {
            fontNamesCache.put(key, names);
        }
    }
}
