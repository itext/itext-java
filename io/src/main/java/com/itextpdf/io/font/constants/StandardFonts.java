package com.itextpdf.io.font.constants;

import java.util.HashSet;
import java.util.Set;

public final class StandardFonts {

    private StandardFonts() {
    }

    private static final Set<String> BUILTIN_FONTS = new HashSet<>();

    static {
        BUILTIN_FONTS.add(StandardFonts.COURIER);
        BUILTIN_FONTS.add(StandardFonts.COURIER_BOLD);
        BUILTIN_FONTS.add(StandardFonts.COURIER_BOLDOBLIQUE);
        BUILTIN_FONTS.add(StandardFonts.COURIER_OBLIQUE);
        BUILTIN_FONTS.add(StandardFonts.HELVETICA);
        BUILTIN_FONTS.add(StandardFonts.HELVETICA_BOLD);
        BUILTIN_FONTS.add(StandardFonts.HELVETICA_BOLDOBLIQUE);
        BUILTIN_FONTS.add(StandardFonts.HELVETICA_OBLIQUE);
        BUILTIN_FONTS.add(StandardFonts.SYMBOL);
        BUILTIN_FONTS.add(StandardFonts.TIMES_ROMAN);
        BUILTIN_FONTS.add(StandardFonts.TIMES_BOLD);
        BUILTIN_FONTS.add(StandardFonts.TIMES_BOLDITALIC);
        BUILTIN_FONTS.add(StandardFonts.TIMES_ITALIC);
        BUILTIN_FONTS.add(StandardFonts.ZAPFDINGBATS);
    }

    public static boolean contains(String fontName) {
        return BUILTIN_FONTS.contains(fontName);
    }


    /**
     * This is a possible value of a base 14 type 1 font
     */
    public static final String COURIER = "Courier";

    /**
     * This is a possible value of a base 14 type 1 font
     */
    public static final String COURIER_BOLD = "Courier-Bold";

    /**
     * This is a possible value of a base 14 type 1 font
     */
    public static final String COURIER_OBLIQUE = "Courier-Oblique";

    /**
     * This is a possible value of a base 14 type 1 font
     */
    public static final String COURIER_BOLDOBLIQUE = "Courier-BoldOblique";

    /**
     * This is a possible value of a base 14 type 1 font
     */
    public static final String HELVETICA = "Helvetica";

    /**
     * This is a possible value of a base 14 type 1 font
     */
    public static final String HELVETICA_BOLD = "Helvetica-Bold";

    /**
     * This is a possible value of a base 14 type 1 font
     */
    public static final String HELVETICA_OBLIQUE = "Helvetica-Oblique";

    /**
     * This is a possible value of a base 14 type 1 font
     */
    public static final String HELVETICA_BOLDOBLIQUE = "Helvetica-BoldOblique";

    /**
     * This is a possible value of a base 14 type 1 font
     */
    public static final String SYMBOL = "Symbol";

    /**
     * This is a possible value of a base 14 type 1 font
     */
    public static final String TIMES_ROMAN = "Times-Roman";

    /**
     * This is a possible value of a base 14 type 1 font
     */
    public static final String TIMES_BOLD = "Times-Bold";

    /**
     * This is a possible value of a base 14 type 1 font
     */
    public static final String TIMES_ITALIC = "Times-Italic";

    /**
     * This is a possible value of a base 14 type 1 font
     */
    public static final String TIMES_BOLDITALIC = "Times-BoldItalic";

    /**
     * This is a possible value of a base 14 type 1 font
     */
    public static final String ZAPFDINGBATS = "ZapfDingbats";

    public static final String TIMES = "Times";

}
