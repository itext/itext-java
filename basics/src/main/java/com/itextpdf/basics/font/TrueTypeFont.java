package com.itextpdf.basics.font;

import com.itextpdf.basics.IntHashtable;
import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.PdfRuntimeException;
import com.itextpdf.basics.Utilities;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

public class TrueTypeFont extends FontProgram {

    private OpenTypeParser fontParser;

    protected String postscriptFontName;
    protected String[][] fullFontName;
    protected String[][] familyFontName;
    protected String[][] allNameEntries;

    /** The content of 'HEAD' table. */
    protected OpenTypeParser.FontHeader head;
    /** The content of 'HHEA' table. */
    protected OpenTypeParser.HorizontalHeader hhea;
    /** The content of 'OS/2' table. */
    protected OpenTypeParser.WindowsMetrics os_2;
    /** The content of 'POST' table. */
    protected OpenTypeParser.PostTable post;
    /** The content of 'CMAP' table. */
    protected OpenTypeParser.Cmaps cmaps;
    /** The width of the glyphs. This is essentially the content of table
     * 'hmtx' normalized to 1000 units. */
    protected int[] glyphWidthsByIndex;
    /** Contains the smallest box enclosing the character contours. */
    private int[][] charBBoxes;
    /** Table of characters widths for this encoding. */
    private int[] widths;

    private boolean isUnicode;

    protected int[][] bBoxes;

    protected int maxGlyphId;

    //TODO doublicated with PdfType0Font.isVertical.
    protected boolean isVertical;

    /** A variable. */
    private int stemV = 80;

    /** The map containing the kerning information. It represents the content of
     * table 'kern'. The key is an <CODE>Integer</CODE> where the top 16 bits
     * are the glyph number for the first character and the lower 16 bits are the
     * glyph number for the second character. The value is the amount of kerning in
     * normalized 1000 units as an <CODE>Integer</CODE>. This value is usually negative. */
    protected IntHashtable kerning = new IntHashtable();

    protected FontEncoding encoding;
    // TODO Duplicated with FontEncoding.baseEncoding.
    protected String baseEncoding;

    private byte[] fontStreamBytes;
    private int[] fontStreamLengths;

    public TrueTypeFont(String name, String baseEncoding, byte[] ttf) throws IOException, PdfException {
        fontParser = new OpenTypeParser(name, ttf);
        postscriptFontName = fontParser.getFontName();
        fullFontName = fontParser.getFullName();
        familyFontName = fontParser.getFamilyName();
        allNameEntries = fontParser.getAllNameEntries();

        head = fontParser.readHeadTable();
        hhea = fontParser.readHheaTable();
        os_2 = fontParser.readOs2Table(head.unitsPerEm);
        post = fontParser.readPostTable();
        if (post == null) {
            post = new OpenTypeParser.PostTable();
            post.italicAngle = -Math.atan2(hhea.caretSlopeRun, hhea.caretSlopeRise) * 180 / Math.PI;
        }
        maxGlyphId = fontParser.readMaxGlyphId();
        glyphWidthsByIndex = fontParser.readGlyphWidths(hhea.numberOfHMetrics, head.unitsPerEm);
        cmaps = fontParser.readCMaps();
        kerning = fontParser.readKerning(head.unitsPerEm);
        bBoxes = fontParser.readBbox(head.unitsPerEm);
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

    public boolean allowEmbedding() {
        return os_2.fsType == 2;
    }

    public FontEncoding getEncoding() {
        return encoding;
    }

    @Override
    public String getFontName() {
        return postscriptFontName;
    }

    @Override
    public String getStyle() {
        return fontParser.getStyle();
    }

    /**
     * Converts a <CODE>String</CODE> to a </CODE>byte</CODE> array according
     * to the font's encoding.
     * Be careful in use. PdfFont should convertToBytes() for any text for PDF.
     * @param text the <CODE>String</CODE> to be converted
     * @return an array of <CODE>byte</CODE> representing the conversion according to the font's encoding
     */
    public byte[] convertToBytes(String text) {
        if (isUnicode) {
            int len = text.length();
            int metrics[] = null;
            char glyph[] = new char[len];
            int i = 0;
            if (cmaps.fontSpecific) {
                byte[] b = PdfEncodings.convertToBytes(text, "symboltt");
                len = b.length;
                for (int k = 0; k < len; ++k) {
                    metrics = getMetrics(b[k] & 0xff);
                    if (metrics == null) {
                        continue;
                    }
                    glyph[i++] = (char)metrics[0];
                }
            } else {
                for (int k = 0; k < len; ++k) {
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
                    glyph[i++] = (char)metrics[0];
                }
            }
            String s = new String(glyph, 0, i);
            try {
                return s.getBytes("UnicodeBigUnmarked");
            } catch (UnsupportedEncodingException e) {
                throw new PdfRuntimeException("TrueTypeFont", e);
            }
        } else {
            return encoding.convertToBytes(text);
        }
    }

    /**
     * Gets the width of a {@code char} in normalized 1000 units.
     * @param ch the unicode {@code char} to get the width of
     * @return the width in normalized 1000 units
     */
    public int getWidth(int ch) {
        if (isUnicode) {
            if (isVertical) {
                return 1000;
            } else if (cmaps.fontSpecific) {
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
            for (byte b : bytes){
                total += widths[b & 0xff];
            }
            return total;
        }
    }

    /**
     * Gets the width of a {@code String} in normalized 1000 units.
     * @param text the {@code String} to get the width of
     * @return the width in normalized 1000 units
     */
    public int getWidth(String text) {
        int total = 0;
        if (isUnicode) {
            if (isVertical) {
                return text.length() * 1000;
            } else if (cmaps.fontSpecific) {
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

    /**
     * Gets the descent of a <CODE>String</CODE> in normalized 1000 units. The descent will always be
     * less than or equal to zero even if all the characters have an higher descent.
     * @param text the <CODE>String</CODE> to get the descent of
     * @return the descent in normalized 1000 units
     */
    public int getDescent(String text) {
        int min = 0;
        char[] chars = text.toCharArray();
        for (char ch : chars) {
            int[] bbox = getCharBBox(ch);
            if (bbox != null && bbox[1] < min) {
                min = bbox[1];
            }
        }
        return min;
    }

    /**
     * Gets the ascent of a <CODE>String</CODE> in normalized 1000 units. The ascent will always be
     * greater than or equal to zero even if all the characters have a lower ascent.
     * @param text the <CODE>String</CODE> to get the ascent of
     * @return the ascent in normalized 1000 units
     */
    public int getAscent(String text) {
        int max = 0;
        char[] chars = text.toCharArray();
        for (char ch : chars) {
            int[] bbox = getCharBBox(ch);
            if (bbox != null && bbox[3] > max) {
                max = bbox[3];
            }
        }
        return max;
    }

    @Override
    public int getStemV() {
        return stemV;
    }

    public int[] getCharBBox(int c) {
        if(isUnicode) {
            if (bBoxes == null)
                return null;
            int[] m = getMetrics(c);
            if (m == null)
                return null;
            return bBoxes[m[0]];
        } else {
            byte[] b = encoding.convertToBytes(c);
            if (b.length == 0) {
                return null;
            } else {
                return charBBoxes[b[0] & 0xff];
            }
        }
    }

    /** Gets the glyph index and metrics for a character.
     * @param c the character
     * @return an {@code int} array with {glyph index, width}
     */
    public int[] getMetrics(int c) {
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

    public HashMap<Integer, int[]> getActiveCmap() {
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

    public byte[] getFontStreamBytes() throws PdfException {
        if (fontStreamBytes != null)
            return fontStreamBytes;
        try {
            if (fontParser.isCff()) {
                fontStreamBytes = fontParser.readCffFont();
            } else {
                fontStreamBytes = fontParser.getFullFont();
            }
            fontStreamLengths = new int[] {fontStreamBytes.length};
        } catch (IOException e) {
            fontStreamBytes = null;
            throw new PdfException(PdfException.IoException, e);
        }
        return fontStreamBytes;
    }

    public int[] getFontStreamLengths() {
        return fontStreamLengths;
    }

    /** Gets the font parameter identified by <CODE>key</CODE>. Valid values
     * for <CODE>key</CODE> are <CODE>ASCENT</CODE>, <CODE>CAPHEIGHT</CODE>, <CODE>DESCENT</CODE>
     * and <CODE>ITALICANGLE</CODE>.
     * @param key the parameter to be extracted
     * @param fontSize the font size in points
     * @return the parameter in points
     */
    public float getFontDescriptor(int key, float fontSize) {
        switch (key) {
            case FontConstants.ASCENT:
                return os_2.sTypoAscender * fontSize / head.unitsPerEm;
            case FontConstants.CAPHEIGHT:
                return os_2.sCapHeight * fontSize / head.unitsPerEm;
            case FontConstants.DESCENT:
                return os_2.sTypoDescender * fontSize / head.unitsPerEm;
            case FontConstants.ITALICANGLE:
                return (float)post.italicAngle;
            case FontConstants.BBOXLLX:
                return fontSize * head.xMin / head.unitsPerEm;
            case FontConstants.BBOXLLY:
                return fontSize * head.yMin / head.unitsPerEm;
            case FontConstants.BBOXURX:
                return fontSize * head.xMax / head.unitsPerEm;
            case FontConstants.BBOXURY:
                return fontSize * head.yMax / head.unitsPerEm;
            case FontConstants.AWT_ASCENT:
                return fontSize * hhea.Ascender / head.unitsPerEm;
            case FontConstants.AWT_DESCENT:
                return fontSize * hhea.Descender / head.unitsPerEm;
            case FontConstants.AWT_LEADING:
                return fontSize * hhea.LineGap / head.unitsPerEm;
            case FontConstants.AWT_MAXADVANCE:
                return fontSize * hhea.advanceWidthMax / head.unitsPerEm;
            case FontConstants.UNDERLINE_POSITION:
                return (post.underlinePosition - post.underlineThickness / 2) * fontSize / head.unitsPerEm;
            case FontConstants.UNDERLINE_THICKNESS:
                return post.underlineThickness * fontSize / head.unitsPerEm;
            case FontConstants.STRIKETHROUGH_POSITION:
                return os_2.yStrikeoutPosition * fontSize / head.unitsPerEm;
            case FontConstants.STRIKETHROUGH_THICKNESS:
                return os_2.yStrikeoutSize * fontSize / head.unitsPerEm;
            case FontConstants.SUBSCRIPT_SIZE:
                return os_2.ySubscriptYSize * fontSize / head.unitsPerEm;
            case FontConstants.SUBSCRIPT_OFFSET:
                return -os_2.ySubscriptYOffset * fontSize / head.unitsPerEm;
            case FontConstants.SUPERSCRIPT_SIZE:
                return os_2.ySuperscriptYSize * fontSize / head.unitsPerEm;
            case FontConstants.SUPERSCRIPT_OFFSET:
                return os_2.ySuperscriptYOffset * fontSize / head.unitsPerEm;
            case FontConstants.WEIGHT_CLASS:
                return os_2.usWeightClass;
            case FontConstants.WIDTH_CLASS:
                return os_2.usWidthClass;
        }
        return 0;
    }

    public int getFlags() {
        int flags = 0;
        if (isFixedPitch()) {
            flags |= 1;
        }
        flags |= isFontSpecific() ? 4 : 32;
        if ((getMacStyle() & 2) != 0) {
            flags |= 64;
        }
        if ((getMacStyle() & 1) != 0) {
            flags |= 262144;
        }
        return flags;
    }

    //TODO
    @Override
    public int getLlx() {
        return 0;
    }

    //TODO
    @Override
    public int getLly() {
        return 0;
    }

    //TODO
    @Override
    public int getUrx() {
        return 0;
    }

    //TODO
    @Override
    public int getUry() {
        return 0;
    }

    //TODO
    @Override
    public int getCapHeight() {
        return 0;
    }

    //TODO
    @Override
    public int getAscent() {
        return 0;
    }

    //TODO
    @Override
    public int getDescent() {
        return 0;
    }

    //TODO
    @Override
    public float getItalicAngle() {
        return 0;
    }

    /** Gets the font parameter identified by <CODE>key</CODE>. Valid values
     * for <CODE>key</CODE> are <CODE>ASCENT</CODE>, <CODE>CAPHEIGHT</CODE>, <CODE>DESCENT</CODE>
     * and <CODE>ITALICANGLE</CODE>.
     * @param key the parameter to be extracted
     * @return the parameter in points
     */
    public float getFontDescriptor(int key) {
        return getFontDescriptor(key, 1000);
    }

    public boolean isFixedPitch(){
        return post.isFixedPitch;
    }

    public boolean isFontSpecific() {
        return cmaps.fontSpecific;
    }

    public int getMacStyle(){
        return head.macStyle;
    }

    public HashMap<Integer, int[]> getCmap10() {
        return cmaps != null ? cmaps.cmap10 : null;
    }

    public HashMap<Integer, int[]> getCmap31() {
        return cmaps != null ? cmaps.cmap31 : null;
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

    public byte[] getSubset(Set<Integer> glyphs, boolean subset) throws PdfException {
        try {
            return fontParser.getSubset(glyphs, subset);
        } catch (IOException e) {
            throw new PdfException(PdfException.IoException, e);
        }
    }

    /**
     * Gets the width from the font according to the unicode char {@code c}.
     * If the {@code name} is null it's a symbolic font.
     * @param c the unicode char
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

    /**
     * Creates the {@code widths} and the {@code differences} arrays.
     */
    protected void createEncoding() throws PdfException {
        if (encoding.isFontSpecific()) {
            for (int k = 0; k < 256; ++k) {
                widths[k] = getRawWidth(k, null);
                charBBoxes[k] = getRawCharBBox(k, null);
            }
        } else {
            String s;
            String name;
            char ch;
            byte[] b = new byte[1];

            for (int k = 0; k < 256; ++k) {
                b[0] = (byte)k;
                s = PdfEncodings.convertToString(b, encoding.getBaseEncoding());
                if (s.length() > 0) {
                    ch = s.charAt(0);
                } else {
                    ch = '?';
                }
                name = AdobeGlyphList.unicodeToName(ch);
                if (name == null) {
                    name = FontConstants.notdef;
                }
                encoding.setDifferences(k, name);
                encoding.setUnicodeDifferences(k, ch);
                widths[k] = getRawWidth(ch, name);
                charBBoxes[k] = getRawCharBBox(ch, name);
            }
        }
    }

    /**
     * Creates the {@code widths} and the {@code differences} arrays in case special user map-encoding.
     * Encoding starts with '# simple …' or '# full …'.
     */
    protected void createSpecialEncoding() {
        StringTokenizer tok = new StringTokenizer(encoding.getBaseEncoding().substring(1), " ,\t\n\r\f");
        if (tok.nextToken().equals("full")) {
            while (tok.hasMoreTokens()) {
                String order = tok.nextToken();
                String name = tok.nextToken();
                char uni = (char)Integer.parseInt(tok.nextToken(), 16);
                int orderK;
                if (order.startsWith("'")) {
                    orderK = order.charAt(1);
                } else {
                    orderK = Integer.parseInt(order);
                }
                orderK %= 256;
                encoding.getSpecialMap().put(uni, orderK);
                encoding.setDifferences(orderK, name);
                encoding.setUnicodeDifferences(orderK, uni);

                widths[orderK] = getRawWidth(uni, name);
                charBBoxes[orderK] = getRawCharBBox(uni, name);
            }
        } else {
            int k = 0;
            if (tok.hasMoreTokens()) {
                k = Integer.parseInt(tok.nextToken());
            }
            while (tok.hasMoreTokens() && k < 256) {
                String hex = tok.nextToken();
                int uni = Integer.parseInt(hex, 16) % 0x10000;
                String name = AdobeGlyphList.unicodeToName(uni);
                if (name != null) {
                    encoding.getSpecialMap().put(uni, k);
                    encoding.setDifferences(k, name);
                    encoding.setUnicodeDifferences(k, (char)uni);

                    widths[k] = getRawWidth(uni, name);
                    charBBoxes[k] = getRawCharBBox(uni, name);
                    ++k;
                }
            }
        }
        encoding.fillEmptyDifferences();
    }
}
