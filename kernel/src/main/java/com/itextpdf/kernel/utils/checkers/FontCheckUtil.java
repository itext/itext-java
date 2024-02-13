package com.itextpdf.kernel.utils.checkers;

import com.itextpdf.io.util.TextUtil;
import com.itextpdf.kernel.font.PdfFont;

/**
 * Utility class that contains common checks used in both the  PDFA and PDFUA module for fonts.
 */
public final class FontCheckUtil {

    private FontCheckUtil(){
        // Empty constructor
    }

    /**
     * Checks if all characters in the string contain a valid glyph in the font.
     *
     * @param text The string we want to compare.
     * @param font The font we want to check
     *
     * @return {@code true} if all glyphs in the string are available in the font.
     */
    public static boolean doesFontContainAllUsedGlyphs(String text, PdfFont font) {
        for (int i = 0; i < text.length(); ++i) {
            int ch;
            if (TextUtil.isSurrogatePair(text, i)) {
                ch = TextUtil.convertToUtf32(text, i);
                i++;
            } else {
                ch = text.charAt(i);
            }
            if (!font.containsGlyph(ch)) {
                return false;
            }
        }
        return true;
    }
}
