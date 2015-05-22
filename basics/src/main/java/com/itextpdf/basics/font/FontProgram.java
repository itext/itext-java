package com.itextpdf.basics.font;

public abstract class FontProgram {

    public abstract String getFontName();

    public String getStyle() {
        return "";
    }

    public String getPanose() {
        return "";
    }

    public abstract int getFlags();

    public abstract int getLlx();

    public abstract int getLly();

    public abstract int getUrx();

    public abstract int getUry();

    public abstract int getCapHeight();

    public abstract int getAscent();

    public abstract int getDescent();

    public abstract float getItalicAngle();

    public abstract int getStemV();

    /**
     * Gets the name without the modifiers Bold, Italic or BoldItalic.
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
