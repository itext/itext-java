package com.itextpdf.basics.font;

import java.util.StringTokenizer;

public abstract class FontProgram {

    public static int DEFAULT_WIDTH = 1000;
    /**
     * Font encoding.
     */
    protected FontEncoding encoding;

    private String fontName;
    /**
     * The llx of the FontBox.
     */
    private int llx = -50;
    /**
     * The lly of the FontBox.
     */
    private int lly = -200;
    /**
     * The lurx of the FontBox.
     */
    private int urx = 1000;
    /**
     * The ury of the FontBox.
     */
    private int ury = 900;

    /**
     * The italic angle of the font, usually 0.0 or negative.
     */
    private float italicAngle = 0.0f;

    /**
     * A variable.
     */
    private int capHeight = 700;
    /**
     * A variable.
     */
    private int xHeight = 480;
    /**
     * A variable.
     */
    private int ascender = 800;
    /**
     * A variable.
     */
    private int descender = -200;
    /**
     * A variable.
     */
    private int stdHW;
    /**
     * A variable.
     */
    private int stdVW = 80;

    /**
     * A variable.
     */
    private int stemV = 80;

    private int flags;

    private String panose;

    private String style;

    private String registry;

    /**
     * Contains the smallest box enclosing the character contours.
     */
    protected int[][] charBBoxes = new int[256][];
    /**
     * Table of characters widths for this encoding.
     */
    protected int[] widths = new int[256];

    /**
     * The font's encoding name. This encoding is 'StandardEncoding' or 'AdobeStandardEncoding' for a font
     * that can be totally encoded according to the characters names. For all other names the font is treated as symbolic.
     */
    protected String encodingScheme = "FontSpecific";

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

    public String getFontName() {
        return fontName;
    }

    public void setPanose(String panose) {
        this.panose = panose;
    }

    public String getRegistry() {
        return registry;
    }

    public void setRegistry(String registry) {
        this.registry = registry;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getStyle() {
        return style;
    }

    public String getPanose() {
        return panose;
    }

    public void setWidths(int[] widths) {
        this.widths = widths;
    }

    public int[] getWidths() {
        return widths;
    }

    public int getLlx() {
        return llx;
    }

    public void setLlx(int llx) {
        this.llx = llx;
    }

    public int getLly() {
        return lly;
    }

    public void setLly(int lly) {
        this.lly = lly;
    }

    public int getUrx() {
        return urx;
    }

    public void setUrx(int urx) {
        this.urx = urx;
    }

    public int getUry() {
        return ury;
    }

    public void setUry(int ury) {
        this.ury = ury;
    }

    public float getItalicAngle() {
        return italicAngle;
    }

    public void setItalicAngle(float italicAngle) {
        this.italicAngle = italicAngle;
    }

    public int getCapHeight() {
        return capHeight;
    }

    public void setCapHeight(int capHeight) {
        this.capHeight = capHeight;
    }

    public int getxHeight() {
        return xHeight;
    }

    public void setxHeight(int xHeight) {
        this.xHeight = xHeight;
    }

    public int getAscender() {
        return ascender;
    }

    public void setAscender(int ascender) {
        this.ascender = ascender;
    }

    public int getDescender() {
        return descender;
    }

    public void setDescender(int descender) {
        this.descender = descender;
    }

    public int getStdHW() {
        return stdHW;
    }

    public void setStdHW(int stdHW) {
        this.stdHW = stdHW;
    }

    public int getStdVW() {
        return stdVW;
    }

    public void setStdVW(int stdVW) {
        this.stdVW = stdVW;
    }

    public int getStemV() {
        return stemV;
    }

    public void setStemV(int stemV) {
        this.stemV = stemV;
    }

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    public FontEncoding getEncoding() {
        return encoding;
    }

    /**
     * Gets the width of a <CODE>String</CODE> in points.     *
     *
     * @param text     the <CODE>String</CODE> to get the width of
     * @param fontSize the font size
     * @return the width in points
     */
    public float getWidthPoint(String text, float fontSize) {
        return getWidth(text) * 0.001f * fontSize;
    }

    /**
     * Gets the width of a <CODE>char</CODE> in points.     *
     *
     * @param char1    the <CODE>char</CODE> to get the width of
     * @param fontSize the font size
     * @return the width in points
     */
    public float getWidthPoint(int char1, float fontSize) {
        return getWidth(char1) * 0.001f * fontSize;
    }

    protected abstract int getRawWidth(int c, String name);

    protected abstract int[] getRawCharBBox(int c, String name);

    public abstract int getWidth(int ch);

    public abstract int getWidth(String text);

    /**
     * Gets the descent of a <CODE>String</CODE> in normalized 1000 units. The descent will always be
     * less than or equal to zero even if all the characters have an higher descent.
     *
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
     *
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

    public int[] getCharBBox(char ch) {
        byte[] b = encoding.convertToBytes(ch);
        if (b.length == 0) {
            return null;
        } else {
            return charBBoxes[b[0] & 0xff];
        }
    }

    /**
     * Creates the {@code widths} and the {@code differences} arrays.
     */
    protected void createEncoding() {
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
                b[0] = (byte) k;
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
                char uni = (char) Integer.parseInt(tok.nextToken(), 16);
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
                    encoding.setUnicodeDifferences(k, (char) uni);

                    widths[k] = getRawWidth(uni, name);
                    charBBoxes[k] = getRawCharBBox(uni, name);
                    ++k;
                }
            }
        }
        encoding.fillEmptyDifferences();
    }

    /**
     * Gets the name without the modifiers Bold, Italic or BoldItalic.
     *
     * @param name the full name of the font
     * @return the name without the modifiers Bold, Italic or BoldItalic
     */
    protected static String getBaseName(String name) {
        if (name.endsWith(",Bold"))
            return name.substring(0, name.length() - 5);
        else if (name.endsWith(",Italic"))
            return name.substring(0, name.length() - 7);
        else if (name.endsWith(",BoldItalic"))
            return name.substring(0, name.length() - 11);
        else
            return name;
    }
}
