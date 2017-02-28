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

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Reusable font set for FontProgram related data.
 *
 * @see FontProvider
 */
public class FontSet {

    private Set<FontInfo> fonts = new LinkedHashSet<>();
    private Map<FontInfo, FontProgram> fontPrograms = new HashMap<>();
    private Map<FontSelectorKey, FontSelector> fontSelectorCache = new HashMap<>();

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
                    if (FileUtil.fileExists(pfb) && addFont(file, null)) {
                        count++;
                    }
                } else if ((".ttf".equals(suffix) || ".otf".equals(suffix) || ".ttc".equals(suffix))
                        && addFont(file, null)) {
                    count++;
                }
            } catch (Exception ignored) {
            }
        }
        return count;
    }

    public int addDirectory(String dir) {
        return addDirectory(dir, false);
    }

    /**
     * Add not supported for auto creating FontPrograms.
     *
     * @param fontProgram {@link FontProgram}
     * @param encoding    FontEncoding for creating {@link com.itextpdf.kernel.font.PdfFont}.
     * @return false, if fontProgram is null, otherwise true.
     */
    public FontInfo add(FontProgram fontProgram, String encoding) {
        if (fontProgram == null) {
            return null;
        }
        FontInfo fontInfo = add(FontInfo.create(fontProgram, encoding));
        fontPrograms.put(fontInfo, fontProgram);
        return fontInfo;
    }

    public FontInfo add(String fontProgram, String encoding) {
        return add(FontInfo.create(fontProgram, encoding));
    }

    public FontInfo add(byte[] fontProgram, String encoding) {
        return add(FontInfo.create(fontProgram, encoding));
    }

    public FontInfo add(String fontProgram) {
        return add(fontProgram, null);
    }

    public FontInfo add(byte[] fontProgram) {
        return add(FontInfo.create(fontProgram, null));
    }


    public boolean remove(FontInfo fontInfo) {
        if (fonts.contains(fontInfo) || fontPrograms.containsKey(fontInfo)) {
            fonts.remove(fontInfo);
            fontPrograms.remove(fontInfo);
            fontSelectorCache.clear();
            return true;
        }
        return false;
    }

    public Set<FontInfo> getFonts() {
        return fonts;
    }

    //region Deprecated addFont methods

    /**
     * Add not supported for auto creating FontPrograms.
     *
     * @param fontProgram
     * @param encoding    FontEncoding for creating {@link com.itextpdf.kernel.font.PdfFont}.
     * @return false, if fontProgram is null, otherwise true.
     * @deprecated use {@link #add(FontProgram, String)} instead.
     */
    @Deprecated
    public boolean addFont(FontProgram fontProgram, String encoding) {
        if (fontProgram == null) {
            return false;
        }
        FontInfo fontInfo = add(FontInfo.create(fontProgram, encoding));
        fontPrograms.put(fontInfo, fontProgram);
        return true;
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
        return addFont(fontProgram, null);
    }

    /**
     * @deprecated use {@link #add(byte[])} instead.
     */
    @Deprecated
    public boolean addFont(byte[] fontProgram) {
        return add(FontInfo.create(fontProgram, null)) != null;
    }

    //endregion

    FontInfo add(FontInfo fontInfo) {
        if (fontInfo != null) {
            fonts.add(fontInfo);
            fontSelectorCache.clear();
        }
        return fontInfo;
    }

    Map<FontInfo, FontProgram> getFontPrograms() {
        return fontPrograms;
    }

    Map<FontSelectorKey, FontSelector> getFontSelectorCache() {
        return fontSelectorCache;
    }
}
