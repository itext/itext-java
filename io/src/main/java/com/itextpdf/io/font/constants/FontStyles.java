package com.itextpdf.io.font.constants;

public final class FontStyles {

    private FontStyles() {
    }

    /**
     * Undefined font style.
     */
    public static final int UNDEFINED = -1;
    /**
     * Normal font style.
     */
    public static final int NORMAL = 0;
    /**
     * Bold font style.
     */
    public static final int BOLD = 1;
    /**
     * Italic font style.
     */
    public static final int ITALIC = 2;
    /**
     * Bold-Italic font style.
     */
    public static final int BOLDITALIC = BOLD | ITALIC;
}
