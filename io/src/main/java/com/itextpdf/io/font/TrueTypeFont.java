/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2020 iText Group NV
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
import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.io.font.constants.TrueTypeCodePages;
import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.io.font.otf.GlyphPositioningTableReader;
import com.itextpdf.io.font.otf.GlyphSubstitutionTableReader;
import com.itextpdf.io.font.otf.OpenTypeGdefTableReader;
import com.itextpdf.io.util.IntHashtable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.io.util.MessageFormatUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;

public class TrueTypeFont extends FontProgram {

    private static final long serialVersionUID = -2232044646577669268L;

	private OpenTypeParser fontParser;

    protected int[][] bBoxes;

    protected boolean isVertical;

    private GlyphSubstitutionTableReader gsubTable;
    private GlyphPositioningTableReader gposTable;
    private OpenTypeGdefTableReader gdefTable;

    /**
     * The map containing the kerning information. It represents the content of
     * table 'kern'. The key is an <CODE>Integer</CODE> where the top 16 bits
     * are the glyph number for the first character and the lower 16 bits are the
     * glyph number for the second character. The value is the amount of kerning in
     * normalized 1000 units as an <CODE>Integer</CODE>. This value is usually negative.
     */
    protected IntHashtable kerning = new IntHashtable();

    private byte[] fontStreamBytes;

    private TrueTypeFont(OpenTypeParser fontParser) throws java.io.IOException {
        this.fontParser = fontParser;
        this.fontParser.loadTables(true);
        initializeFontProperties();
    }

    protected TrueTypeFont() {
        fontNames = new FontNames();
    }

    public TrueTypeFont(String path) throws java.io.IOException {
        this(new OpenTypeParser(path));
    }

    public TrueTypeFont(byte[] ttf) throws java.io.IOException {
        this(new OpenTypeParser(ttf));
    }

    TrueTypeFont(String ttcPath, int ttcIndex) throws java.io.IOException {
        this(new OpenTypeParser(ttcPath, ttcIndex));
    }

    TrueTypeFont(byte[] ttc, int ttcIndex) throws java.io.IOException {
        this(new OpenTypeParser(ttc, ttcIndex));
    }

    @Override
    public boolean hasKernPairs() {
        return kerning.size() > 0;
    }

    /**
     * Gets the kerning between two glyphs.
     *
     * @param first the first glyph
     * @param second the second glyph
     * @return the kerning to be applied
     */
    @Override
    public int getKerning(Glyph first, Glyph second) {
        if (first == null || second == null) {
            return 0;
        }
        return kerning.get((first.getCode() << 16) + second.getCode());
    }

    public boolean isCff() {
        return fontParser.isCff();
    }

    public Map<Integer, int[]> getActiveCmap() {
        OpenTypeParser.CmapTable cmaps = fontParser.getCmapTable();
        if (cmaps.cmapExt != null) {
            return cmaps.cmapExt;
        } else if (!cmaps.fontSpecific && cmaps.cmap31 != null) {
            return cmaps.cmap31;
        } else if (cmaps.fontSpecific && cmaps.cmap10 != null) {
            return cmaps.cmap10;
        } else if (cmaps.cmap31 != null) {
            return cmaps.cmap31;
        } else {
            return cmaps.cmap10;
        }
    }

    public byte[] getFontStreamBytes() {
        if (fontStreamBytes != null)
            return fontStreamBytes;
        try {
            if (fontParser.isCff()) {
                fontStreamBytes = fontParser.readCffFont();
            } else {
                fontStreamBytes = fontParser.getFullFont();
            }
        } catch (java.io.IOException e) {
            fontStreamBytes = null;
            throw new IOException(IOException.IoException, e);
        }
        return fontStreamBytes;
    }

    @Override
    public int getPdfFontFlags() {
        int flags = 0;
        if (fontMetrics.isFixedPitch()) {
            flags |= 1;
        }
        flags |= isFontSpecific() ? 4 : 32;
        if (fontNames.isItalic()) {
            flags |= 64;
        }
        if (fontNames.isBold() || fontNames.getFontWeight() > 500) {
            flags |= 262144;
        }
        return flags;
    }

    /**
     * The offset from the start of the file to the table directory.
     * It is 0 for TTF and may vary for TTC depending on the chosen font.
     */
    public int getDirectoryOffset() {
        return fontParser.directoryOffset;
    }

    public GlyphSubstitutionTableReader getGsubTable() {
        return gsubTable;
    }

    public GlyphPositioningTableReader getGposTable() {
        return gposTable;
    }

    public OpenTypeGdefTableReader getGdefTable() {
        return gdefTable;
    }

    public byte[] getSubset(Set<Integer> glyphs, boolean subset) {
        try {
            return fontParser.getSubset(glyphs, subset);
        } catch (java.io.IOException e) {
            throw new IOException(IOException.IoException, e);
        }
    }

    protected void readGdefTable() throws java.io.IOException {
        int[] gdef = fontParser.tables.get("GDEF");
        if (gdef != null) {
            gdefTable = new OpenTypeGdefTableReader(fontParser.raf, gdef[0]);
        } else {
            gdefTable = new OpenTypeGdefTableReader(fontParser.raf, 0);
        }
        gdefTable.readTable();
    }

    protected void readGsubTable() throws java.io.IOException {
        int[] gsub = fontParser.tables.get("GSUB");
        if (gsub != null) {
            gsubTable = new GlyphSubstitutionTableReader(fontParser.raf, gsub[0], gdefTable, codeToGlyph, fontMetrics.getUnitsPerEm());
        }
    }

    protected void readGposTable() throws java.io.IOException {
        int[] gpos = fontParser.tables.get("GPOS");
        if (gpos != null) {
            gposTable = new GlyphPositioningTableReader(fontParser.raf, gpos[0], gdefTable, codeToGlyph,  fontMetrics.getUnitsPerEm());
        }
    }

    private void initializeFontProperties() throws java.io.IOException {
        // initialize sfnt tables
        OpenTypeParser.HeaderTable head = fontParser.getHeadTable();
        OpenTypeParser.HorizontalHeader hhea = fontParser.getHheaTable();
        OpenTypeParser.WindowsMetrics os_2 = fontParser.getOs_2Table();
        OpenTypeParser.PostTable post = fontParser.getPostTable();
        isFontSpecific = fontParser.getCmapTable().fontSpecific;
        kerning = fontParser.readKerning(head.unitsPerEm);
        bBoxes = fontParser.readBbox(head.unitsPerEm);

        // font names group
        fontNames = fontParser.getFontNames();

        // font metrics group
        fontMetrics.setUnitsPerEm(head.unitsPerEm);
        fontMetrics.updateBbox(head.xMin, head.yMin, head.xMax, head.yMax);
        fontMetrics.setNumberOfGlyphs(fontParser.readNumGlyphs());
        fontMetrics.setGlyphWidths(fontParser.getGlyphWidthsByIndex());
        fontMetrics.setTypoAscender(os_2.sTypoAscender);
        fontMetrics.setTypoDescender(os_2.sTypoDescender);
        fontMetrics.setCapHeight(os_2.sCapHeight);
        fontMetrics.setXHeight(os_2.sxHeight);
        fontMetrics.setItalicAngle(post.italicAngle);
        fontMetrics.setAscender(hhea.Ascender);
        fontMetrics.setDescender(hhea.Descender);
        fontMetrics.setLineGap(hhea.LineGap);
        fontMetrics.setWinAscender(os_2.usWinAscent);
        fontMetrics.setWinDescender(os_2.usWinDescent);
        fontMetrics.setAdvanceWidthMax(hhea.advanceWidthMax);
        fontMetrics.setUnderlinePosition((post.underlinePosition - post.underlineThickness) / 2);
        fontMetrics.setUnderlineThickness(post.underlineThickness);
        fontMetrics.setStrikeoutPosition(os_2.yStrikeoutPosition);
        fontMetrics.setStrikeoutSize(os_2.yStrikeoutSize);
        fontMetrics.setSubscriptOffset(-os_2.ySubscriptYOffset);
        fontMetrics.setSubscriptSize(os_2.ySubscriptYSize);
        fontMetrics.setSuperscriptOffset(os_2.ySuperscriptYOffset);
        fontMetrics.setSuperscriptSize(os_2.ySuperscriptYSize);
        fontMetrics.setIsFixedPitch(post.isFixedPitch);

        // font identification group
        String[][] ttfVersion = fontNames.getNames(5);
        if (ttfVersion != null) {
            fontIdentification.setTtfVersion(ttfVersion[0][3]);
        }
        String[][] ttfUniqueId = fontNames.getNames(3);
        if (ttfUniqueId != null) {
            fontIdentification.setTtfVersion(ttfUniqueId[0][3]);
        }

        byte[] pdfPanose = new byte[12];
        pdfPanose[1] = (byte) (os_2.sFamilyClass);
        pdfPanose[0] = (byte) (os_2.sFamilyClass >> 8);
        System.arraycopy(os_2.panose, 0, pdfPanose, 2, 10);
        fontIdentification.setPanose(pdfPanose);

        Map<Integer, int[]> cmap = getActiveCmap();
        int[] glyphWidths = fontParser.getGlyphWidthsByIndex();
        int numOfGlyphs = fontMetrics.getNumberOfGlyphs();
        unicodeToGlyph = new LinkedHashMap<>(cmap.size());
        codeToGlyph = new LinkedHashMap<>(numOfGlyphs);
        avgWidth = 0;
        for (int charCode : cmap.keySet()) {
            int index = cmap.get(charCode)[0];
            if (index >= numOfGlyphs) {
                Logger LOGGER = LoggerFactory.getLogger(TrueTypeFont.class);
                LOGGER.warn(MessageFormatUtil.format(LogMessageConstant.FONT_HAS_INVALID_GLYPH, getFontNames().getFontName(), index));
                continue;
            }
            Glyph glyph = new Glyph(index, glyphWidths[index], charCode, bBoxes != null ? bBoxes[index] : null);
            unicodeToGlyph.put(charCode, glyph);
            // This is done on purpose to keep the mapping to glyphs with smaller unicode values, in contrast with
            // larger values which often represent different forms of other characters.
            if (!codeToGlyph.containsKey(index)) {
                codeToGlyph.put(index, glyph);
            }
            avgWidth += glyph.getWidth();
        }
        fixSpaceIssue();
        for (int index = 0; index < glyphWidths.length; index++) {
            if (codeToGlyph.containsKey(index)) {
                continue;
            }
            Glyph glyph = new Glyph(index, glyphWidths[index], -1);
            codeToGlyph.put(index, glyph);
            avgWidth += glyph.getWidth();
        }

        if (codeToGlyph.size() != 0) {
            avgWidth /= codeToGlyph.size();
        }

        readGdefTable();
        readGsubTable();
        readGposTable();

        isVertical = false;
    }

    /**
     * Gets the code pages supported by the font.
     *
     * @return the code pages supported by the font
     */
    public String[] getCodePagesSupported() {
        long cp = ((long) fontParser.getOs_2Table().ulCodePageRange2 << 32) + (fontParser.getOs_2Table().ulCodePageRange1 & 0xffffffffL);
        int count = 0;
        long bit = 1;
        for (int k = 0; k < 64; ++k) {
            if ((cp & bit) != 0 && TrueTypeCodePages.get(k) != null)
                ++count;
            bit <<= 1;
        }
        String[] ret = new String[count];
        count = 0;
        bit = 1;
        for (int k = 0; k < 64; ++k) {
            if ((cp & bit) != 0 && TrueTypeCodePages.get(k) != null)
                ret[count++] = TrueTypeCodePages.get(k);
            bit <<= 1;
        }
        return ret;
    }

    @Override
    public boolean isBuiltWith(String fontProgram) {
        return Objects.equals(fontParser.fileName, fontProgram);
    }

    public void close() throws java.io.IOException {
        if (fontParser != null) {
            fontParser.close();
        }
        fontParser = null;
    }

    /**
     * The method will update usedGlyphs with additional range or with all glyphs if there is no subset.
     * This set of used glyphs can be used for building width array and ToUnicode CMAP.
     *
     * @param usedGlyphs a set of integers, which are glyph ids that denote used glyphs.
     *                   This set is updated inside of the method if needed.
     * @param subset subset status
     * @param subsetRanges additional subset ranges
     */
    public void updateUsedGlyphs(SortedSet<Integer> usedGlyphs, boolean subset, List<int[]> subsetRanges) {
        int[] compactRange;
        if (subsetRanges != null) {
            compactRange = toCompactRange(subsetRanges);
        } else if (!subset) {
            compactRange = new int[] {0, 0xFFFF};
        } else {
            compactRange = new int[] {};
        }

        for (int k = 0; k < compactRange.length; k += 2) {
            int from = compactRange[k];
            int to = compactRange[k + 1];
            for (int glyphId = from; glyphId <= to; glyphId++) {
                if (getGlyphByCode(glyphId) != null) {
                    usedGlyphs.add(glyphId);
                }
            }
        }
    }

    /**
     * Normalizes given ranges by making sure that first values in pairs are lower than second values and merges overlapping
     * ranges in one.
     * @param ranges a {@link List} of integer arrays, which are constituted by pairs of ints that denote
     *               each range limits. Each integer array size shall be a multiple of two.
     * @return single merged array consisting of pairs of integers, each of them denoting a range.
     */
    private static int[] toCompactRange(List<int[]> ranges) {
        List<int[]> simp = new ArrayList<>();
        for (int[] range : ranges) {
            for (int j = 0; j < range.length; j += 2) {
                simp.add(new int[]{Math.max(0, Math.min(range[j], range[j + 1])), Math.min(0xffff, Math.max(range[j], range[j + 1]))});
            }
        }
        for (int k1 = 0; k1 < simp.size() - 1; ++k1) {
            for (int k2 = k1 + 1; k2 < simp.size(); ++k2) {
                int[] r1 = simp.get(k1);
                int[] r2 = simp.get(k2);
                if (r1[0] >= r2[0] && r1[0] <= r2[1] || r1[1] >= r2[0] && r1[0] <= r2[1]) {
                    r1[0] = Math.min(r1[0], r2[0]);
                    r1[1] = Math.max(r1[1], r2[1]);
                    simp.remove(k2);
                    --k2;
                }
            }
        }
        int[] s = new int[simp.size() * 2];
        for (int k = 0; k < simp.size(); ++k) {
            int[] r = simp.get(k);
            s[k * 2] = r[0];
            s[k * 2 + 1] = r[1];
        }
        return s;
    }
}
