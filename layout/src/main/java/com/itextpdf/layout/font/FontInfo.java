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
package com.itextpdf.layout.font;

import com.itextpdf.io.font.FontCacheKey;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramDescriptor;
import com.itextpdf.io.font.FontProgramDescriptorFactory;
import com.itextpdf.io.util.ArrayUtil;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.font.PdfFont;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Contains all font related data to create {@link FontProgram} and {@link PdfFont}.
 * {@link FontProgramDescriptor} fetches with {@link FontProgramDescriptorFactory}.
 *
 * @see FontProvider#getPdfFont(FontInfo)
 * @see FontProvider#getPdfFont(FontInfo, FontSet)
 *
 * Note, {@link #getAlias()} and {@link #getDescriptor()} are not taken into account in {@link #equals},
 * the same font with different aliases will have equal FontInfo's,
 * and therefore the same {@link PdfFont} in the end document.
 */
public final class FontInfo {

    private static final Map<FontCacheKey, FontProgramDescriptor> fontNamesCache = new ConcurrentHashMap<>();

    private final String fontName;
    private final byte[] fontData;
    private final FontProgramDescriptor descriptor;
    private final Range range;
    private final int hash;
    private final String encoding;
    private final String alias;

    private FontInfo(String fontName, byte[] fontData, String encoding, FontProgramDescriptor descriptor,
                     Range unicodeRange, String alias) {
        this.fontName = fontName;
        this.fontData = fontData;
        this.encoding = encoding;
        this.descriptor = descriptor;
        this.range = unicodeRange != null ? unicodeRange : RangeBuilder.getFullRange();
        this.alias = alias != null ? alias.toLowerCase() : null;
        this.hash = calculateHashCode(this.fontName, this.fontData, this.encoding, this.range);
    }

    public static FontInfo create(FontInfo fontInfo, String alias, Range range) {
        return new FontInfo(fontInfo.fontName, fontInfo.fontData, fontInfo.encoding,
                fontInfo.descriptor, range, alias);
    }

    public static FontInfo create(FontInfo fontInfo, String alias) {
        return create(fontInfo, alias, null);
    }

    public static FontInfo create(FontProgram fontProgram, String encoding, String alias, Range range) {
        FontProgramDescriptor descriptor = FontProgramDescriptorFactory.fetchDescriptor(fontProgram);
        return new FontInfo(descriptor.getFontName(), null, encoding, descriptor, range, alias);
    }

    public static FontInfo create(FontProgram fontProgram, String encoding, String alias) {
        return create(fontProgram, encoding, alias, null);
    }

    static FontInfo create(String fontName, String encoding, String alias, Range range) {
        FontCacheKey cacheKey = FontCacheKey.create(fontName);
        FontProgramDescriptor descriptor = getFontNamesFromCache(cacheKey);
        if (descriptor == null) {
            descriptor = FontProgramDescriptorFactory.fetchDescriptor(fontName);
            putFontNamesToCache(cacheKey, descriptor);
        }
        return descriptor != null ? new FontInfo(fontName, null, encoding, descriptor, range, alias) : null;
    }

    static FontInfo create(byte[] fontProgram, String encoding, String alias, Range range) {
        FontCacheKey cacheKey = FontCacheKey.create(fontProgram);
        FontProgramDescriptor descriptor = getFontNamesFromCache(cacheKey);
        if (descriptor == null) {
            descriptor = FontProgramDescriptorFactory.fetchDescriptor(fontProgram);
            putFontNamesToCache(cacheKey, descriptor);
        }
        return descriptor != null ? new FontInfo(null, fontProgram, encoding, descriptor, range, alias) : null;
    }

    public FontProgramDescriptor getDescriptor() {
        return descriptor;
    }

    //shall not be null
    public Range getFontUnicodeRange() {
        return range;
    }

    /**
     * Gets path to font, if {@link FontInfo} was created by String.
     * Note, to get PostScript or full name, use {@link #getDescriptor()}.
     * @return the font name
     */
    public String getFontName() {
        return fontName;
    }

    /**
     * Gets font data, if {@link FontInfo} was created with {@code byte[]}.
     *
     * @return font data
     */
    public byte[] getFontData() {
        return fontData;
    }

    public String getEncoding() {
        return encoding;
    }

    /**
     * Gets font alias.
     *
     * @return alias if exist, otherwise null.
     */
    public String getAlias() {
        return alias;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FontInfo)) return false;

        FontInfo that = (FontInfo) o;
        return (fontName != null ? fontName.equals(that.fontName) : that.fontName == null)
                && range.equals(that.range)
                && Arrays.equals(fontData, that.fontData)
                && (encoding != null ? encoding.equals(that.encoding) : that.encoding == null);
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public String toString() {
        String name = descriptor.getFontName();
        if (name.length() > 0) {
            if (encoding != null) {
                return MessageFormatUtil.format("{0}+{1}", name, encoding);
            } else {
                return name;
            }
        }
        return super.toString();
    }

    private static int calculateHashCode(String fontName, byte[] bytes, String encoding,
                                         Range range) {
        int result = fontName != null ? fontName.hashCode() : 0;
        result = 31 * result + ArrayUtil.hashCode(bytes);
        result = 31 * result + (encoding != null ? encoding.hashCode() : 0);
        result = 31 * result + range.hashCode();
        return result;
    }

    private static FontProgramDescriptor getFontNamesFromCache(FontCacheKey key) {
        return fontNamesCache.get(key);
    }

    private static void putFontNamesToCache(FontCacheKey key, FontProgramDescriptor descriptor) {
        if (descriptor != null) {
            fontNamesCache.put(key, descriptor);
        }
    }
}
