package com.itextpdf.basics.font;

import com.itextpdf.basics.IntHashtable;
import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.Utilities;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;


public class TrueTypeFont extends FontProgram {

    private OpenTypeParser fontParser;

    /**
     * Contains the smallest box enclosing the character contours.
     */
    private int[][] charBBoxes;

    private boolean isUnicode;

    protected int[][] bBoxes;

    protected HashMap<Integer, List<String[]>> allNames;

    protected int maxGlyphId;

    //TODO doublicated with PdfType0Font.isVertical.
    protected boolean isVertical;

    /**
     * The map containing the kerning information. It represents the content of
     * table 'kern'. The key is an <CODE>Integer</CODE> where the top 16 bits
     * are the glyph number for the first character and the lower 16 bits are the
     * glyph number for the second character. The value is the amount of kerning in
     * normalized 1000 units as an <CODE>Integer</CODE>. This value is usually negative.
     */
    protected IntHashtable kerning = new IntHashtable();

    // TODO Duplicated with FontEncoding.baseEncoding.
    protected String baseEncoding;

    private byte[] fontStreamBytes;
    private int[] fontStreamLengths;

    // TODO remove 'name' parameter
    public TrueTypeFont(String name, String baseEncoding, byte[] ttf) throws IOException {
        fontParser = new OpenTypeParser(name, ttf);

        // initialize sfnt tables
        OpenTypeParser.HeaderTable head = fontParser.getHeadTable();
        OpenTypeParser.HorizontalHeader hhea = fontParser.getHheaTable();
        OpenTypeParser.WindowsMetrics os_2 = fontParser.getOs_2Table();
        OpenTypeParser.PostTable post = fontParser.getPostTable();
        OpenTypeParser.CmapTable cmaps = fontParser.getCmapTable();
        kerning = fontParser.readKerning(head.unitsPerEm);
        bBoxes = fontParser.readBbox(head.unitsPerEm);
        allNames = fontParser.getAllNameEntries();

        // font names group
        fontNames.setFontName(fontParser.getPsFontName());
        fontNames.setFullName(getNames(4));
        String[][] otfFamilyName = getNames(16);
        if (otfFamilyName != null) {
            fontNames.setFamilyName(otfFamilyName);
        } else {
            fontNames.setFamilyName(getNames(1));
        }
        String[][] subfamily = getNames(2);
        if (subfamily != null) {
            fontNames.setStyle(subfamily[0][3]);
        }
        String[][] otfSubFamily = getNames(17);
        if (otfFamilyName != null) {
            fontNames.setSubfamily(otfSubFamily);
        } else {
            fontNames.setSubfamily(subfamily);
        }
        String[][] cidName = getNames(20);
        if (cidName != null) {
            fontNames.setCidFontName(cidName[0][3]);
        }
        fontNames.setWeight(os_2.usWeightClass);
        fontNames.setWidth(os_2.usWidthClass);
        fontNames.setMacStyle(head.macStyle);
        fontNames.setAllowEmbedding(os_2.fsType != 2);

        // font metrics group
        fontMetrics.setUnitsPerEm(head.unitsPerEm);
        fontMetrics.updateBbox(head.xMin, head.yMin, head.xMax, head.yMax);
        fontMetrics.setMaxGlyphId(fontParser.readMaxGlyphId());
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
        String[][] ttfVersion = getNames(5);
        if (ttfVersion != null) {
            fontIdentification.setTtfVersion(ttfVersion[0][3]);
        }
        String[][] ttfUniqueId = getNames(3);
        if (ttfUniqueId != null) {
            fontIdentification.setTtfVersion(ttfVersion[0][3]);
        }
        fontIdentification.setPanose(os_2.panose);


        this.baseEncoding = baseEncoding;
        if (this.baseEncoding.equals(PdfEncodings.IDENTITY_H) || this.baseEncoding.equals(PdfEncodings.IDENTITY_V)) {
            isUnicode = true;
            isVertical = this.baseEncoding.endsWith("V");

        } else {
            isUnicode = false;
            encoding = new FontEncoding(this.baseEncoding, cmaps.fontSpecific);
            charBBoxes = new int[256][];
            widths = new int[256];
            if (encoding.hasSpecialEncoding()) {
                createSpecialEncoding();
            } else {
                createEncoding();
            }
        }
    }

    public TrueTypeFont(String encoding) {
        this.baseEncoding = encoding;
        if (this.baseEncoding.equals(PdfEncodings.IDENTITY_H) || this.baseEncoding.equals(PdfEncodings.IDENTITY_V)) {
            isUnicode = true;
            isVertical = this.baseEncoding.endsWith("V");
        } else {
            this.encoding = new FontEncoding(encoding, true);
        }
    }

    /**
     * Converts a <CODE>String</CODE> to a </CODE>byte</CODE> array according
     * to the font's encoding.
     *
     * @param text the <CODE>String</CODE> to be converted
     * @return an array of <CODE>byte</CODE> representing the conversion according to the font's encoding
     */
    public byte[] convertToBytes(String text) {
        if (isUnicode) {
            char[] glyph;
            int[] metrics;
            int i = 0;
            if (fontParser.getCmapTable().fontSpecific) {
                byte[] b = PdfEncodings.convertToBytes(text, "symboltt");
                glyph = new char[b.length];
                for (int k = 0; k < b.length; ++k) {
                    metrics = getMetrics(b[k] & 0xff);
                    if (metrics == null) {
                        continue;
                    }
                    glyph[i++] = (char) metrics[0];
                }
            } else {
                glyph = new char[text.length()];
                for (int k = 0; k < text.length(); ++k) {
                    int val;
                    if (Utilities.isSurrogatePair(text, k)) {
                        val = Utilities.convertToUtf32(text, k);
                        k++;
                    } else {
                        val = text.charAt(k);
                    }
                    metrics = getMetrics(val);
                    if (metrics == null) {
                        continue;
                    }
                    glyph[i++] = (char) metrics[0];
                }
            }
            String s = new String(glyph, 0, i);
            try {
                return s.getBytes("UnicodeBigUnmarked");
            } catch (UnsupportedEncodingException e) {
                throw new PdfException("TrueTypeFont", e);
            }
        } else {
            return encoding.convertToBytes(text);
        }
    }

    /**
     * Gets the width of a {@code char} in normalized 1000 units.
     *
     * @param ch the unicode {@code char} to get the width of
     * @return the width in normalized 1000 units
     */
    public int getWidth(int ch) {
        if (isUnicode) {
            if (isVertical) {
                return 1000;
            } else if (fontParser.getCmapTable().fontSpecific) {
                if ((ch & 0xff00) == 0 || (ch & 0xff00) == 0xf000) {
                    return getRawWidth(ch & 0xff, null);
                } else {
                    return 0;
                }
            } else {
                return getRawWidth(ch, baseEncoding);
            }
        } else if (encoding.isFastWinansi()) {
            if (ch < 128 || ch >= 160 && ch <= 255) {
                return widths[ch];
            } else {
                return widths[PdfEncodings.winansi.get(ch)];
            }
        } else {
            int total = 0;
            byte[] bytes = encoding.convertToBytes(ch);
            for (byte b : bytes) {
                total += widths[b & 0xff];
            }
            return total;
        }
    }

    /**
     * Gets the width of a {@code String} in normalized 1000 units.
     *
     * @param text the {@code String} to get the width of
     * @return the width in normalized 1000 units
     */
    public int getWidth(String text) {
        int total = 0;
        if (isUnicode) {
            if (isVertical) {
                return text.length() * 1000;
            } else if (fontParser.getCmapTable().fontSpecific) {
                char[] chars = text.toCharArray();
                for (char ch : chars) {
                    if ((ch & 0xff00) == 0 || (ch & 0xff00) == 0xf000) {
                        total += getRawWidth(ch & 0xff, null);
                    }
                }
            } else {
                int len = text.length();
                for (int k = 0; k < len; ++k) {
                    if (Utilities.isSurrogatePair(text, k)) {
                        total += getRawWidth(Utilities.convertToUtf32(text, k), baseEncoding);
                        ++k;
                    } else {
                        total += getRawWidth(text.charAt(k), baseEncoding);
                    }
                }
            }
            return total;
        } else if (encoding.isFastWinansi()) {
            for (int k = 0; k < text.length(); ++k) {
                char ch = text.charAt(k);
                if (ch < 128 || ch >= 160 && ch <= 255) {
                    total += widths[ch];
                } else {
                    total += widths[PdfEncodings.winansi.get(ch)];
                }
            }
            return total;
        } else {
            byte[] bytes = encoding.convertToBytes(text);
            for (byte b : bytes) {
                total += widths[b & 0xff];
            }
        }
        return total;
    }

    @Override
    public boolean hasKernPairs() {
        return kerning.size() > 0;
    }

    /** Gets the kerning between two Unicode chars.
     * @param char1 the first char
     * @param char2 the second char
     * @return the kerning to be applied
     */
    @Override
    public int getKerning(int char1, int char2) {
        int metrics[] = getMetrics(char1);
        if (metrics == null)
            return 0;
        int c1 = metrics[0];
        metrics = getMetrics(char2);
        if (metrics == null)
            return 0;
        int c2 = metrics[0];
        return kerning.get((c1 << 16) + c2);
    }

    /**
     * Gets the glyph index and metrics for a character.
     *
     * @param c the character
     * @return an {@code int} array with {glyph index, width}
     */
    public int[] getMetrics(int c) {
        OpenTypeParser.CmapTable cmaps = fontParser.getCmapTable();
        if (isUnicode) {
            if (cmaps.cmapExt != null) {
                return cmaps.cmapExt.get(Integer.valueOf(c));
            }
            HashMap<Integer, int[]> map;
            if (cmaps.fontSpecific) {
                map = cmaps.cmap10;
            } else {
                map = cmaps.cmap31;
            }
            if (map == null) {
                return null;
            } else if (cmaps.fontSpecific) {
                if ((c & 0xffffff00) == 0 || (c & 0xffffff00) == 0xf000) {
                    return map.get(Integer.valueOf(c & 0xff));
                } else {
                    return null;
                }
            } else {
                return map.get(Integer.valueOf(c));
            }
        } else {
            if (cmaps.cmapExt != null) {
                return cmaps.cmapExt.get(Integer.valueOf(c));
            } else if (!cmaps.fontSpecific && cmaps.cmap31 != null) {
                return cmaps.cmap31.get(Integer.valueOf(c));
            } else if (cmaps.fontSpecific && cmaps.cmap10 != null) {
                return cmaps.cmap10.get(Integer.valueOf(c));
            } else if (cmaps.cmap31 != null) {
                return cmaps.cmap31.get(Integer.valueOf(c));
            } else if (cmaps.cmap10 != null) {
                return cmaps.cmap10.get(Integer.valueOf(c));
            }
            return null;
        }
    }

    public boolean isCff() {
        return fontParser.isCff();
    }

    public HashMap<Integer, List<String[]>> getAllNames() {
        return allNames;
    }

    public HashMap<Integer, int[]> getActiveCmap() {
        OpenTypeParser.CmapTable cmaps = fontParser.getCmapTable();
        if (!cmaps.fontSpecific && cmaps.cmap31 != null) {
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
            fontStreamLengths = new int[]{fontStreamBytes.length};
        } catch (IOException e) {
            fontStreamBytes = null;
            throw new PdfException(PdfException.IoException, e);
        }
        return fontStreamBytes;
    }

    public int[] getFontStreamLengths() {
        return fontStreamLengths;
    }

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

    public boolean isFontSpecific() {
        return fontParser.getCmapTable().fontSpecific;
    }

    public HashMap<Integer, int[]> getCmap10() {
        return fontParser.getCmapTable() != null
                ? fontParser.getCmapTable().cmap10
                : null;
    }

    public HashMap<Integer, int[]> getCmap31() {
        return fontParser.getCmapTable() != null
                ? fontParser.getCmapTable().cmap31
                : null;
    }

    /**
     * Gets table of characters widths for this simple font encoding.
     */
    public int[] getRawWidths() {
        return widths;
    }

    /**
     * The offset from the start of the file to the table directory.
     * It is 0 for TTF and may vary for TTC depending on the chosen font.
     */
    public int getDirectoryOffset() {
        return fontParser.directoryOffset;
    }

    public byte[] getSubset(Set<Integer> glyphs, boolean subset) {
        try {
            return fontParser.getSubset(glyphs, subset);
        } catch (IOException e) {
            throw new PdfException(PdfException.IoException, e);
        }
    }

    /**
     * Gets the width from the font according to the unicode char {@code c}.
     * If the {@code name} is null it's a symbolic font.
     *
     * @param c    the unicode char
     * @param name not used in {@code TrueTypeFont}.
     * @return the width of the char
     */
    protected int getRawWidth(int c, String name) {
        int[] metric = getMetrics(c);
        if (metric == null) {
            return 0;
        }
        return metric[1];
    }

    protected int[] getRawCharBBox(int c, String name) {
        OpenTypeParser.CmapTable cmaps = fontParser.getCmapTable();
        HashMap<Integer, int[]> map;
        if (name == null || cmaps.cmap31 == null) {
            map = cmaps.cmap10;
        } else {
            map = cmaps.cmap31;
        }
        if (map == null) {
            return null;
        }
        int[] metric = map.get(Integer.valueOf(c));
        if (metric == null || bBoxes == null) {
            return null;
        }
        return bBoxes[metric[0]];
    }

    /** Extracts the names of the font in all the languages available.
     * @param id the name id to retrieve
     * @return not empty {@code String[][]} if any names exists, otherwise {@code null}.
     */
    protected String[][] getNames(int id) throws IOException {
        List<String[]> names = allNames.get(id);
        return names != null && names.size() > 0 ? listToArray(names) : null;
    }

    private String[][] listToArray(List<String[]> list) {
        String[][] array = new String[list.size()][];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
    }
}
