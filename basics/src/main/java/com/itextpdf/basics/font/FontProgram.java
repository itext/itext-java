package com.itextpdf.basics.font;

import java.util.StringTokenizer;

public abstract class FontProgram {

    public static final int DEFAULT_WIDTH = 1000;
    public static final int UNITS_NORMALIZATION = 1000;
    /**
     * Font encoding.
     */
    protected FontEncoding encoding;
    /**
     * Contains the smallest box enclosing the character contours.
     */
    protected int[][] charBBoxes;
    /**
     * Table of characters widths for this encoding.
     */
    protected int[] widths;

    FontNames fontNames = new FontNames();

    FontMetrics fontMetrics = new FontMetrics();

    FontIdentification fontIdentification = new FontIdentification();

    /**
     * The font's encoding name. This encoding is 'StandardEncoding' or 'AdobeStandardEncoding' for a font
     * that can be totally encoded according to the characters names. For all other names the font is treated as symbolic.
     */
    protected String encodingScheme = "FontSpecific";

    protected String registry;

    public FontEncoding getEncoding() {
        return encoding;
    }

    public FontNames getFontNames() {
        return fontNames;
    }

    public FontMetrics getFontMetrics() {
        return fontMetrics;
    }

    public FontIdentification getFontIdentification() {
        return fontIdentification;
    }

    public void setWidths(int[] widths) {
        this.widths = widths;
    }

    public int[] getWidths() {
        return widths;
    }

    public String getRegistry() {
        return registry;
    }

    public abstract int getPdfFontFlags();

    protected abstract int getRawWidth(int c, String name);

    protected abstract int[] getRawCharBBox(int c, String name);

    /**
     * Get glyph's width.
     * @param code char code, depends from implementation.
     * @return Gets width in normalized 1000 units.
     */
    public abstract int getWidth(int code);

    /**
     * Get glyph's bbox.
     * @param code char code, depends from implementation.
     * @return Gets bbox in normalized 1000 units.
     */
    public abstract int[] getCharBBox(int code);

    public boolean hasKernPairs() {
        return false;
    }

    public abstract int getKerning(int char1, int char2);

    //TODO change to protected!
    public void setRegistry(String registry) {
        this.registry = registry;
    }

    /**
     * Creates the {@code widths} and the {@code differences} arrays.
     */
    protected void createEncoding() {
        charBBoxes = new int[256][];
        widths = new int[256];

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
        charBBoxes = new int[256][];
        widths = new int[256];

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
        if (name == null) {
            return null;
        }
        if (name.endsWith(",Bold")) {
            return name.substring(0, name.length() - 5);
        } else if (name.endsWith(",Italic")) {
            return name.substring(0, name.length() - 7);
        } else if (name.endsWith(",BoldItalic")) {
            return name.substring(0, name.length() - 11);
        } else {
            return name;
        }
    }
}
