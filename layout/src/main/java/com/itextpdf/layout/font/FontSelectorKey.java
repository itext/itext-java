package com.itextpdf.layout.font;

final class FontSelectorKey {
    String fontFamily;
    int style;

    public FontSelectorKey(String fontFamily, int style) {
        this.fontFamily = fontFamily;
        this.style = style;
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        FontSelectorKey that = (FontSelectorKey) o;

        return style == that.style
                && (fontFamily != null ? fontFamily.equals(that.fontFamily) : that.fontFamily == null);
    }

    @Override
    public int hashCode() {
        int result = fontFamily != null ? fontFamily.hashCode() : 0;
        result = 31 * result + style;
        return result;
    }
}
