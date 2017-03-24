/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2017 iText Group NV
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

import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.util.FileUtil;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Reusable font set for FontProgram related data.
 * Add and search fonts.
 * <p/>
 * A FontSet instance could be shared for multiple threads.
 * However FontSet filling is not thread safe operation.
 *
 * @see FontProvider
 */
public final class FontSet {
    // FontSet MUST be final to avoid overriding #add(FontInfo) method or remove functionality.

    private static AtomicLong lastId = new AtomicLong();

    // Due to new logic HashSet can be used instead of List.
    // But FontInfo with or without alias will be the same FontInfo.
    private final Set<FontInfo> fonts = new LinkedHashSet<>();
    private final Map<FontInfo, FontProgram> fontPrograms = new HashMap<>();
    private final FontNameSet fontNames = new FontNameSet();
    private long id;

    public FontSet() {
        this.id = incrementId();
    }

    /**
     * Add all the fonts in a directory and possibly its subdirectories.
     *
     * @param dir                path to directory.
     * @param scanSubdirectories recursively scan subdirectories if {@code true}.
     * @return number of added fonts.
     */
    public int addDirectory(String dir, boolean scanSubdirectories) {
        int count = 0;
        String[] files = FileUtil.listFilesInDirectory(dir, scanSubdirectories);
        if (files == null)
            return 0;
        for (String file : files) {
            try {
                String suffix = file.length() < 4 ? null : file.substring(file.length() - 4).toLowerCase();
                if (".afm".equals(suffix) || ".pfm".equals(suffix)) {
                    // Add only Type 1 fonts with matching .pfb files.
                    String pfb = file.substring(0, file.length() - 4) + ".pfb";
                    if (FileUtil.fileExists(pfb) && add(file, null, null) != null) {
                        count++;
                    }
                } else if ((".ttf".equals(suffix) || ".otf".equals(suffix) || ".ttc".equals(suffix))
                        && add(file, null, null) != null) {
                    count++;
                }
            } catch (Exception ignored) {
            }
        }
        return count;
    }

    /**
     * Add all the fonts in a directory.
     *
     * @param dir path to directory.
     * @return number of added fonts.
     */
    public int addDirectory(String dir) {
        return addDirectory(dir, false);
    }

    /**
     * Add not supported for auto creating FontPrograms.
     * <p/>
     * Note, {@link FontInfo#getAlias()} do not taken into account in {@link FontInfo#equals}.
     * The same font with different alias will not be replaced.
     *
     * @param fontProgram {@link FontProgram}
     * @param encoding    FontEncoding for creating {@link com.itextpdf.kernel.font.PdfFont}.
     * @param alias       font alias.
     * @return just created {@link FontInfo} on success, otherwise null.
     */
    public FontInfo add(FontProgram fontProgram, String encoding, String alias) {
        if (fontProgram == null) {
            return null;
        }
        FontInfo fontInfo = add(FontInfo.create(fontProgram, encoding, alias));
        fontPrograms.put(fontInfo, fontProgram);
        return fontInfo;
    }

    /**
     * Add not supported for auto creating FontPrograms.
     * <p/>
     * Note, {@link FontInfo#getAlias()} do not taken into account in {@link FontInfo#equals}.
     * The same font with different alias will not be replaced.
     *
     * @param fontProgram {@link FontProgram}
     * @param encoding    FontEncoding for creating {@link com.itextpdf.kernel.font.PdfFont}.
     * @return just created {@link FontInfo} on success, otherwise null.
     */
    public FontInfo add(FontProgram fontProgram, String encoding) {
        return add(fontProgram, encoding, null);
    }

    /**
     * Creates {@link FontInfo}, fetches {@link com.itextpdf.io.font.FontProgramDescriptor}
     * and adds just created {@link FontInfo} to {@link FontSet}.
     * <p/>
     * Note, {@link FontInfo#getAlias()} do not taken into account in {@link FontInfo#equals}.
     * The same font with different alias will not be replaced.
     *
     * @param fontPath path to font data.
     * @param encoding preferred font encoding.
     * @return just created {@link FontInfo} on success, otherwise null.
     * @see com.itextpdf.io.font.PdfEncodings
     */
    public FontInfo add(String fontPath, String encoding, String alias) {
        return add(FontInfo.create(fontPath, encoding, alias));
    }

    /**
     * Creates {@link FontInfo}, fetches {@link com.itextpdf.io.font.FontProgramDescriptor}
     * and adds just created {@link FontInfo} to {@link FontSet}.
     * <p/>
     * Note, {@link FontInfo#getAlias()} do not taken into account in {@link FontInfo#equals}.
     * The same font with different alias will not be replaced.
     *
     * @param fontData font data.
     * @param encoding preferred font encoding.
     * @return just created {@link FontInfo} on success, otherwise null.
     * @see com.itextpdf.io.font.PdfEncodings
     */
    public FontInfo add(byte[] fontData, String encoding, String alias) {
        return add(FontInfo.create(fontData, encoding, alias));
    }

    /**
     * Creates {@link FontInfo}, fetches {@link com.itextpdf.io.font.FontProgramDescriptor}
     * and adds just created {@link FontInfo} to {@link FontSet}.
     * {@link FontProvider#getDefaultEncoding(FontProgram)} will be used to determine encoding.
     * <p/>
     * Note, {@link FontInfo#getAlias()} do not taken into account in {@link FontInfo#equals}.
     * The same font with different alias will not be replaced.
     *
     * @param fontPath path to font data.
     * @return just created {@link FontInfo} on success, otherwise null.
     */
    public FontInfo add(String fontPath) {
        return add(fontPath, null, null);
    }

    /**
     * Creates {@link FontInfo}, fetches {@link com.itextpdf.io.font.FontProgramDescriptor}
     * and adds just created {@link FontInfo} to {@link FontSet}.
     * {@link FontProvider#getDefaultEncoding(FontProgram)} will be used to determine encoding.
     * <p/>
     * Note, {@link FontInfo#getAlias()} do not taken into account in {@link FontInfo#equals}.
     * The same font with different alias will not be replaced.
     *
     * @param fontData font data.
     * @return just created {@link FontInfo} on success, otherwise null.
     */
    public FontInfo add(byte[] fontData) {
        return add(fontData, null, null);
    }

    /**
     * Adds {@link FontInfo} with alias. Could be used to fill temporary font set.
     * <p/>
     * Note, {@link FontInfo#getAlias()} do not taken into account in {@link FontInfo#equals}.
     * The same font with different alias will not be replaced.
     *
     * @param fontInfo font info.
     * @param alias    font alias.
     * @return just created object on success or null, if equal FontInfo already exist.
     */
    public FontInfo add(FontInfo fontInfo, String alias) {
        return add(FontInfo.create(fontInfo, alias));
    }

    /**
     * Adds {@link FontInfo}. Could be used to fill temporary font set.
     * <p/>
     * Note, {@link FontInfo#getAlias()} do not taken into account in {@link FontInfo#equals}.
     * The same font with different alias will not be replaced.
     *
     * @param fontInfo font info.
     * @return the same object on success or null, if equal FontInfo already exist.
     */
    public final FontInfo add(FontInfo fontInfo) {
        // This method MUST be final, to avoid inconsistency with FontSelectorCache.
        // (Yes, FontSet is final. Double check.)
        if (fontInfo != null) {
            if (fonts.contains(fontInfo)) {
                // NOTE! We SHALL NOT replace font, because it will influence on FontSelectorCache.
                // FontSelectorCache reset cache ONLY if number of fonts has been changed,
                // while replacing will modify list of fonts without size change.
                return null;
            }
            fonts.add(fontInfo);
            fontNames.add(fontInfo);
        }
        return fontInfo;
    }

    /**
     * Search in existed fonts for PostScript name or full font name.
     *
     * @param fontName PostScript or full name.
     * @return true, if {@link FontSet} contains font with given name.
     */
    public boolean contains(String fontName) {
        return fontNames.contains(fontName);
    }

    /**
     * Search in existed fonts for PostScript name or full font name.
     *
     * @param fontName PostScript or full name.
     * @return {@link FontInfo} if {@link FontSet} contains font with given name, otherwise {@code null}.
     */
    public FontInfo get(String fontName) {
        return fontNames.get(fontName);
    }

    /**
     * Gets available fonts.
     * Note, the collection is unmodifiable.
     */
    public Collection<FontInfo> getFonts() {
        return getFonts(null);
    }

    /**
     * Gets union of available and temporary fonts.
     * Note, the collection is unmodifiable.
     */
    public Collection<FontInfo> getFonts(FontSet tempFonts) {
        return new FontSetCollection(fonts, tempFonts != null ? tempFonts.fonts : null);
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public int size() {
        return fonts.size();
    }

    //region Deprecated addFont methods

    /**
     * @deprecated use {@link #add(FontProgram, String)} instead.
     */
    @Deprecated
    public boolean addFont(FontProgram fontProgram, String encoding) {
        return add(fontProgram, encoding) != null;
    }

    /**
     * @deprecated use {@link #add(String, String, String)} instead.
     */
    @Deprecated
    public boolean addFont(String fontProgram, String encoding) {
        return add(FontInfo.create(fontProgram, encoding, null)) != null;
    }

    /**
     * @deprecated use {@link #add(byte[], String, String)} instead.
     */
    @Deprecated
    public boolean addFont(byte[] fontProgram, String encoding) {
        return add(FontInfo.create(fontProgram, encoding, null)) != null;
    }

    /**
     * @deprecated use {@link #add(String)} instead.
     */
    @Deprecated
    public boolean addFont(String fontProgram) {
        return add(fontProgram) != null;
    }

    /**
     * @deprecated use {@link #add(byte[])} instead.
     */
    @Deprecated
    public boolean addFont(byte[] fontProgram) {
        return add(fontProgram) != null;
    }

    //endregion

    //region Internal members

    long getId() {
        return id;
    }

    FontProgram getFontProgram(FontInfo fontInfo) {
        return fontPrograms.get(fontInfo);
    }

    private long incrementId() {
        return lastId.incrementAndGet();
    }

    //endregion

    //region Set for font names quick search

    /**
     * FontNameSet used for quick search of lowercased fontName or fullName,
     * supports remove FontInfo at FontSet level.
     * <p>
     * FontInfoName has tricky implementation. Hashcode builds by fontName String,
     * but equals() works in different ways, depends whether FontInfoName used to search (no FontInfo)
     * or to add (contains FontInfo).
     */
    private static class FontNameSet {

        private final Map<FontInfoName, FontInfo> fontInfoNames = new HashMap<>();

        boolean contains(String fontName) {
            return fontInfoNames.containsKey(new FontInfoName(fontName.toLowerCase()));
        }

        FontInfo get(String fontName) {
            return fontInfoNames.get(new FontInfoName(fontName.toLowerCase()));
        }

        void add(FontInfo fontInfo) {
            fontInfoNames.put(new FontInfoName(fontInfo.getDescriptor().getFontNameLowerCase(), fontInfo), fontInfo);
            fontInfoNames.put(new FontInfoName(fontInfo.getDescriptor().getFullNameLowerCase(), fontInfo), fontInfo);
        }
    }

    private static class FontInfoName {
        private final FontInfo fontInfo;
        private final String fontName;

        FontInfoName(String fontName, FontInfo fontInfo) {
            this.fontInfo = fontInfo;
            this.fontName = fontName;
        }

        FontInfoName(String fontName) {
            this.fontInfo = null;
            this.fontName = fontName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            FontInfoName that = (FontInfoName) o;
            boolean equalFontInfo = true;
            if (fontInfo != null && that.fontInfo != null) {
                equalFontInfo = fontInfo.equals(that.fontInfo);
            }

            return fontName.equals(that.fontName) && equalFontInfo;
        }

        @Override
        public int hashCode() {
            return fontName.hashCode();
        }
    }

    //endregion
}
