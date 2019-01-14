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
package com.itextpdf.layout.font;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.util.FileUtil;
import com.itextpdf.kernel.font.Type3Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Reusable font set for FontProgram related data.
 * Add and search fonts.
 * <p>
 * A FontSet instance could be shared for multiple threads.
 * However FontSet filling is not thread safe operation.
 *
 * @see FontProvider
 */
public final class FontSet {
    // FontSet MUST be final to avoid overriding #add(FontInfo) method or remove functionality.

    private static final AtomicLong lastId = new AtomicLong();

    // Due to new logic HashSet can be used instead of List.
    // But FontInfo with or without alias will be the same FontInfo.
    private final Set<FontInfo> fonts = new LinkedHashSet<>();
    private final Map<FontInfo, FontProgram> fontPrograms = new HashMap<>();
    private final long id;

    /**
     * Creates a new instance of {@link FontSet}.
     */
    public FontSet() {
        this.id = lastId.incrementAndGet();
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
                    if (FileUtil.fileExists(pfb) && addFont(file)) {
                        count++;
                    }
                } else if ((".ttf".equals(suffix) || ".otf".equals(suffix) || ".ttc".equals(suffix))
                        && addFont(file)) {
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
     * <p>
     * Note, {@link FontInfo#getAlias()} do not taken into account in {@link FontInfo#equals}.
     * The same font with different alias will not be replaced.
     * Alias will replace original font family in font selector algorithm.
     *
     * @param fontProgram  {@link FontProgram}
     * @param encoding     FontEncoding for creating {@link com.itextpdf.kernel.font.PdfFont}
     * @param alias        font alias.
     * @param unicodeRange sets the specific range of characters to be used from the font
     * @return true, if font was successfully added, otherwise false.
     */
    public boolean addFont(FontProgram fontProgram, String encoding, String alias, Range unicodeRange) {
        if (fontProgram == null) {
            return false;
        }
        if (fontProgram instanceof Type3Font) {
            Logger logger = LoggerFactory.getLogger(FontSet.class);
            logger.error(LogMessageConstant.TYPE3_FONT_CANNOT_BE_ADDED);
            return false;
        }
        FontInfo fi = FontInfo.create(fontProgram, encoding, alias, unicodeRange);
        if (addFont(fi)) {
            fontPrograms.put(fi, fontProgram);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Add not supported for auto creating FontPrograms.
     * <p>
     * Note, {@link FontInfo#getAlias()} do not taken into account in {@link FontInfo#equals}.
     * The same font with different alias will not be replaced.
     * Alias will replace original font family in font selector algorithm.
     *
     * @param fontProgram {@link FontProgram}
     * @param encoding    FontEncoding for creating {@link com.itextpdf.kernel.font.PdfFont}
     * @param alias       font alias.
     * @return true, if font was successfully added, otherwise false.
     */
    public boolean addFont(FontProgram fontProgram, String encoding, String alias) {
        return addFont(fontProgram, encoding, alias, null);
    }

    /**
     * Add not supported for auto creating FontPrograms.
     *
     * @param fontProgram {@link FontProgram}
     * @param encoding    FontEncoding for creating {@link com.itextpdf.kernel.font.PdfFont}.
     * @return true, if font was successfully added, otherwise false.
     */
    public boolean addFont(FontProgram fontProgram, String encoding) {
        return addFont(fontProgram, encoding, null);
    }

    /**
     * Creates {@link FontInfo}, fetches {@link com.itextpdf.io.font.FontProgramDescriptor}
     * and adds just created {@link FontInfo} to {@link FontSet}.
     * <p>
     * Note, {@link FontInfo#getAlias()} do not taken into account in {@link FontInfo#equals}.
     * The same font with different alias will not be replaced.
     * Alias will replace original font family in font selector algorithm.
     *
     * @param fontPath     path to font data.
     * @param encoding     preferred font encoding.
     * @param alias        font alias, will replace original font family.
     * @param unicodeRange sets the specific range of characters to be used from the font
     * @return true, if font was successfully added, otherwise false.
     * @see com.itextpdf.io.font.PdfEncodings
     */
    public boolean addFont(String fontPath, String encoding, String alias, Range unicodeRange) {
        return addFont(FontInfo.create(fontPath, encoding, alias, unicodeRange));
    }

    /**
     * Creates {@link FontInfo}, fetches {@link com.itextpdf.io.font.FontProgramDescriptor}
     * and adds just created {@link FontInfo} to {@link FontSet}.
     * <p>
     * Note, {@link FontInfo#getAlias()} do not taken into account in {@link FontInfo#equals}.
     * The same font with different alias will not be replaced.
     * Alias will replace original font family in font selector algorithm.
     *
     * @param fontPath path to font data.
     * @param encoding preferred font encoding.
     * @param alias    font alias.
     * @return true, if font was successfully added, otherwise false.
     * @see com.itextpdf.io.font.PdfEncodings
     */
    public boolean addFont(String fontPath, String encoding, String alias) {
        return addFont(fontPath, encoding, alias, null);
    }

    /**
     * Creates {@link FontInfo}, fetches {@link com.itextpdf.io.font.FontProgramDescriptor}
     * and adds just created {@link FontInfo} to {@link FontSet}.
     *
     * @param fontPath path to font data.
     * @param encoding preferred font encoding.
     * @return true, if font was successfully added, otherwise false.
     * @see com.itextpdf.io.font.PdfEncodings
     */
    public boolean addFont(String fontPath, String encoding) {
        return addFont(FontInfo.create(fontPath, encoding, null, null));
    }

    /**
     * Creates {@link FontInfo}, fetches {@link com.itextpdf.io.font.FontProgramDescriptor}
     * and adds just created {@link FontInfo} to {@link FontSet}.
     * <p>
     * Note, {@link FontInfo#getAlias()} do not taken into account in {@link FontInfo#equals}.
     * The same font with different alias will not be replaced.
     * Alias will replace original font family in font selector algorithm.
     *
     * @param fontData     font data.
     * @param encoding     preferred font encoding.
     * @param alias        font alias.
     * @param unicodeRange sets the specific range of characters to be used from the font
     * @return true, if font was successfully added, otherwise false.
     * @see com.itextpdf.io.font.PdfEncodings
     */
    public boolean addFont(byte[] fontData, String encoding, String alias, Range unicodeRange) {
        return addFont(FontInfo.create(fontData, encoding, alias, unicodeRange));
    }

    /**
     * Creates {@link FontInfo}, fetches {@link com.itextpdf.io.font.FontProgramDescriptor}
     * and adds just created {@link FontInfo} to {@link FontSet}.
     * <p>
     * Note, {@link FontInfo#getAlias()} do not taken into account in {@link FontInfo#equals}.
     * The same font with different alias will not be replaced.
     * Alias will replace original font family in font selector algorithm.
     *
     * @param fontData font data.
     * @param encoding preferred font encoding.
     * @param alias    font alias.
     * @return true, if font was successfully added, otherwise false.
     * @see com.itextpdf.io.font.PdfEncodings
     */
    public boolean addFont(byte[] fontData, String encoding, String alias) {
        return addFont(fontData, encoding, alias, null);
    }

    /**
     * Creates {@link FontInfo}, fetches {@link com.itextpdf.io.font.FontProgramDescriptor}
     * and adds just created {@link FontInfo} to {@link FontSet}.
     *
     * @param fontData font data.
     * @param encoding preferred font encoding.
     * @return true, if font was successfully added, otherwise false.
     * @see com.itextpdf.io.font.PdfEncodings
     */
    public boolean addFont(byte[] fontData, String encoding) {
        return addFont(FontInfo.create(fontData, encoding, null, null));
    }

    /**
     * Creates {@link FontInfo}, fetches {@link com.itextpdf.io.font.FontProgramDescriptor}
     * and adds just created {@link FontInfo} to {@link FontSet}.
     * {@link FontProvider#getDefaultEncoding(FontProgram)} will be used to determine encoding.
     *
     * @param fontPath path to font data.
     * @return true, if font was successfully added, otherwise false.
     */
    public boolean addFont(String fontPath) {
        return addFont(fontPath, null, null);
    }

    /**
     * Creates {@link FontInfo}, fetches {@link com.itextpdf.io.font.FontProgramDescriptor}
     * and adds just created {@link FontInfo} to {@link FontSet}.
     * {@link FontProvider#getDefaultEncoding(FontProgram)} will be used to determine encoding.
     *
     * @param fontData font data.
     * @return true, if font was successfully added, otherwise false.
     */
    public boolean addFont(byte[] fontData) {
        return addFont(fontData, null, null);
    }

    /**
     * Adds {@link FontInfo} with alias. Could be used to fill temporary font set.
     * <p>
     * Note, {@link FontInfo#getAlias()} do not taken into account in {@link FontInfo#equals}.
     * The same font with different alias will not be replaced.
     * Alias will replace original font family in font selector algorithm.
     *
     * @param fontInfo     font info.
     * @param alias        font alias.
     * @param unicodeRange sets the specific range of characters to be used from the font
     * @return true, if font was successfully added, otherwise false.
     */
    public boolean addFont(FontInfo fontInfo, String alias, Range unicodeRange) {
        return addFont(FontInfo.create(fontInfo, alias, unicodeRange));
    }

    /**
     * Adds {@link FontInfo} with alias. Could be used to fill temporary font set.
     * <p>
     * Note, {@link FontInfo#getAlias()} do not taken into account in {@link FontInfo#equals}.
     * The same font with different alias will not be replaced.
     * Alias will replace original font family in font selector algorithm.
     *
     * @param fontInfo font info.
     * @param alias    font alias.
     * @return true, if font was successfully added, otherwise false.
     */
    public boolean addFont(FontInfo fontInfo, String alias) {
        return addFont(fontInfo, alias, null);
    }

    /**
     * Adds {@link FontInfo}. Could be used to fill temporary font set.
     * <p>
     * Note, {@link FontInfo#getAlias()} do not taken into account in {@link FontInfo#equals}.
     * The same font with different alias will not be replaced.
     *
     * @param fontInfo font info.
     * @return true, if font was successfully added, otherwise false.
     */
    public final boolean addFont(FontInfo fontInfo) {
        // This method MUST be final, to avoid inconsistency with FontSelectorCache.
        // (Yes, FontSet is final. Double check.)
        if (fontInfo != null && !fonts.contains(fontInfo)) {
            // NOTE! We SHALL NOT replace font, because it will influence on FontSelectorCache.
            // FontSelectorCache reset cache ONLY if number of fonts has been changed,
            // while replacing will modify list of fonts without size change.
            fonts.add(fontInfo);
            return true;
        }
        return false;
    }

    /**
     * Search in existed fonts for PostScript name or full font name.
     * <p>
     * Note, this method has O(n) complexity.
     *
     * @param fontName PostScript or full name.
     * @return true, if {@link FontSet} contains font with given name.
     */
    public boolean contains(String fontName) {
        if (fontName == null || fontName.length() == 0) {
            return false;
        }
        fontName = fontName.toLowerCase();

        for (FontInfo fi : getFonts()) {
            if (fontName.equals(fi.getDescriptor().getFullNameLowerCase())
                    || fontName.equals(fi.getDescriptor().getFontNameLowerCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Search in existed fonts for PostScript name or full font name.
     * <p>
     * Note, this method has O(n) complexity.
     *
     * @param fontName PostScript or full name.
     * @return Collection of {@link FontInfo} from set of fonts with given PostScript or full name.
     */
    public Collection<FontInfo> get(String fontName) {
        if (fontName == null || fontName.length() == 0) {
            return Collections.<FontInfo>emptyList();
        }
        fontName = fontName.toLowerCase();
        List<FontInfo> list = new ArrayList<>();
        for (FontInfo fi : getFonts()) {
            if (fontName.equals(fi.getDescriptor().getFullNameLowerCase())
                    || fontName.equals(fi.getDescriptor().getFontNameLowerCase())) {
                list.add(fi);
            }
        }
        return list;
    }

    /**
     * Gets available fonts.
     * <p>
     * Note, the collection is unmodifiable.
     */
    public Collection<FontInfo> getFonts() {
        return getFonts(null);
    }

    /**
     * Gets union of available and temporary fonts.
     * <p>
     * Note, the collection is unmodifiable.
     */
    public Collection<FontInfo> getFonts(FontSet tempFonts) {
        return new FontSetCollection(fonts, tempFonts != null ? tempFonts.fonts : null);
    }

    /**
     * Returns {@code true} if this set contains no elements.
     *
     * @return {@code true} if this set contains no elements
     */
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Returns the number of elements in this set.
     *
     * @return the number of elements in this set
     */
    public int size() {
        return fonts.size();
    }

    //region Internal members

    long getId() {
        return id;
    }

    FontProgram getFontProgram(FontInfo fontInfo) {
        return fontPrograms.get(fontInfo);
    }

    //endregion
}
