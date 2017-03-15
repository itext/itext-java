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
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Reusable font set for FontProgram related data.
 * Add, remove and search fonts.
 *
 * @see FontProvider
 */
public class FontSet {

    private final List<FontInfo> fonts = new LinkedList<>();
    private final Map<FontInfo, FontProgram> fontPrograms = new HashMap<>();
    private final Map<FontSelectorKey, FontSelector> fontSelectorCache = new HashMap<>();
    private final FontNameSet fontNames = new FontNameSet();

    /**
     * Add all the fonts in a directory and possibly its subdirectories.
     *
     * @param dir path to directory.
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
                    if (FileUtil.fileExists(pfb) && add(file, null) != null) {
                        count++;
                    }
                } else if ((".ttf".equals(suffix) || ".otf".equals(suffix) || ".ttc".equals(suffix))
                        && add(file, null) != null) {
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
     * Clone existing fontInfo with alias and add to the {@link FontSet}.
     * Note, font selector will match either original font names and alias.
     *
     * @param fontInfo already created {@link FontInfo}.
     * @param alias    font alias, shall not be null.
     * @return just created {@link FontInfo} on success, otherwise null.
     */
    public FontInfo add(FontInfo fontInfo, String alias) {
        if (alias == null) {
            return null;
        }
        FontInfo newFontInfo = FontInfo.create(fontInfo, alias);

        return add(newFontInfo);
    }

    /**
     * Add not supported for auto creating FontPrograms.
     *
     * @param fontProgram {@link FontProgram}
     * @param encoding    FontEncoding for creating {@link com.itextpdf.kernel.font.PdfFont}.
     * @return just created {@link FontInfo} on success, otherwise null.
     */
    public FontInfo add(FontProgram fontProgram, String encoding) {
        if (fontProgram == null) {
            return null;
        }
        FontInfo fontInfo = add(FontInfo.create(fontProgram, encoding, null));
        fontPrograms.put(fontInfo, fontProgram);
        return fontInfo;
    }

    /**
     * Add not supported for auto creating FontPrograms.
     *
     * @param fontProgram {@link FontProgram}
     * @param encoding    FontEncoding for creating {@link com.itextpdf.kernel.font.PdfFont}.
     * @param alias    font alias.
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
     * Creates {@link FontInfo}, fetches {@link com.itextpdf.io.font.FontProgramDescriptor}
     * and adds just created {@link FontInfo} to {@link FontSet}.
     *
     * @param fontProgram path to font data.
     * @param encoding preferred font encoding.
     * @return just created {@link FontInfo} on success, otherwise null.
     * @see com.itextpdf.io.font.PdfEncodings
     */
    public FontInfo add(String fontProgram, String encoding) {
        return add(FontInfo.create(fontProgram, encoding));
    }

    /**
     * Creates {@link FontInfo}, fetches {@link com.itextpdf.io.font.FontProgramDescriptor}
     * and adds just created {@link FontInfo} to {@link FontSet}.
     *
     * @param fontProgram font data.
     * @param encoding preferred font encoding.
     * @return just created {@link FontInfo} on success, otherwise null.
     * @see com.itextpdf.io.font.PdfEncodings
     */
    public FontInfo add(byte[] fontProgram, String encoding) {
        return add(FontInfo.create(fontProgram, encoding));
    }

    /**
     * Creates {@link FontInfo}, fetches {@link com.itextpdf.io.font.FontProgramDescriptor}
     * and adds just created {@link FontInfo} to {@link FontSet}.
     * {@link FontProvider#getDefaultEncoding(FontProgram)} will be used to determine encoding.
     *
     * @param fontProgram path to font data.
     * @return just created {@link FontInfo} on success, otherwise null.
     */
    public FontInfo add(String fontProgram) {
        return add(fontProgram, null);
    }

    /**
     * Creates {@link FontInfo}, fetches {@link com.itextpdf.io.font.FontProgramDescriptor}
     * and adds just created {@link FontInfo} to {@link FontSet}.
     * {@link FontProvider#getDefaultEncoding(FontProgram)} will be used to determine encoding.
     *
     * @param fontProgram font data.
     * @return just created {@link FontInfo} on success, otherwise null.
     */
    public FontInfo add(byte[] fontProgram) {
        return add(FontInfo.create(fontProgram, null));
    }

    /**
     * Removes pre saved {@link FontInfo}.
     *
     * @param fontInfo {@link FontInfo} from group of {@code #add()} methods.
     * @return true, if font was found and successfully removed.
     */
    public boolean remove(FontInfo fontInfo) {
        if (fonts.contains(fontInfo) || fontPrograms.containsKey(fontInfo)) {
            fonts.remove(fontInfo);
            fontPrograms.remove(fontInfo);
            fontNames.remove(fontInfo);

            fontSelectorCache.clear();
            return true;
        }
        return false;
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
     * Set of available fonts.
     * Note, the set is unmodifiable.
     */
    public Collection<FontInfo> getFonts() {
        return Collections.<FontInfo>unmodifiableCollection(fonts);
    }

    //region Deprecated addFont methods

    /**
     * Add not supported for auto creating FontPrograms.
     *
     * @param fontProgram instance of {@link FontProgram}.
     * @param encoding FontEncoding for creating {@link com.itextpdf.kernel.font.PdfFont}.
     * @return false, if fontProgram is null, otherwise true.
     * @deprecated use {@link #add(FontProgram, String)} instead.
     */
    @Deprecated
    public boolean addFont(FontProgram fontProgram, String encoding) {
        return add(fontProgram, encoding) != null;
    }

    /**
     * @deprecated use {@link #add(String, String)} instead.
     */
    @Deprecated
    public boolean addFont(String fontProgram, String encoding) {
        return add(FontInfo.create(fontProgram, encoding)) != null;
    }

    /**
     * @deprecated use {@link #add(byte[], String)} instead.
     */
    @Deprecated
    public boolean addFont(byte[] fontProgram, String encoding) {
        return add(FontInfo.create(fontProgram, encoding)) != null;
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

    Map<FontInfo, FontProgram> getFontPrograms() {
        return fontPrograms;
    }

    Map<FontSelectorKey, FontSelector> getFontSelectorCache() {
        return fontSelectorCache;
    }

    private FontInfo add(FontInfo fontInfo) {
        if (fontInfo != null) {
            fonts.add(fontInfo);
            fontSelectorCache.clear();
            fontNames.add(fontInfo);
        }
        return fontInfo;
    }

    //endregion

    //region Set for quick search of font names

    /**
     * FontNameSet used for quick search of lowercased fontName or fullName,
     * supports remove FontInfo at FontSet level.
     *
     * FontInfoName has tricky implementation. Hashcode builds by fontName String,
     * but equals() works in different ways, depends whether FontInfoName used for search (no FontInfo)
     * or for adding/removing (contains FontInfo).
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

        void remove(FontInfo fontInfo) {
            fontInfoNames.remove(new FontInfoName(fontInfo.getDescriptor().getFontNameLowerCase(), fontInfo));
            fontInfoNames.remove(new FontInfoName(fontInfo.getDescriptor().getFullNameLowerCase(), fontInfo));
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
