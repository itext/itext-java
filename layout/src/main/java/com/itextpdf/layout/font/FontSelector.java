package com.itextpdf.layout.font;

import com.itextpdf.io.font.FontConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * Sort given font set according to font name and font style.
 */
public class FontSelector {

    protected List<FontProgramInfo> fonts;

    public FontSelector(Set<FontProgramInfo> allFonts, String fontFamily, int style) {
        this.fonts = new ArrayList<>(allFonts);
        //Possible issue in .NET, virtual member in constructor.
        Collections.sort(this.fonts, getComparator(fontFamily != null ? fontFamily : "", style));
    }

    /**
     * The best font match.
     * If any font from {@link #getFonts()} doesn't contain requested glyphs, this font will be used.
     */
    public final FontProgramInfo bestMatch() {
        return fonts.get(0);
    }

    /**
     * Sorted set of fonts.
     */
    public final Iterable<FontProgramInfo> getFonts() {
        return fonts;
    }

    protected Comparator<FontProgramInfo> getComparator(String fontFamily, int style) {
        return new PdfFontComparator(fontFamily, style);
    }

    private static class PdfFontComparator implements Comparator<FontProgramInfo> {
        String fontFamily;
        int style;

        PdfFontComparator(String fontFamily, int style) {
            this.fontFamily = fontFamily.toLowerCase();
            if (style == FontConstants.UNDEFINED) {
                style = FontConstants.NORMAL;
                if (fontFamily.contains("bold")) {
                    style |= FontConstants.BOLD;
                }
                if (fontFamily.contains("italic") || fontFamily.contains("oblique")) {
                    style |= FontConstants.ITALIC;
                }
            }
            this.style = style;
        }

        @Override
        public int compare(FontProgramInfo o1, FontProgramInfo o2) {
            int res = 0;
            if ((style & FontConstants.BOLD) == 0) {
                res = (o2.getNames().isBold() ? 1 : 0)
                        - (o1.getNames().isBold() ? 1 : 0);
            }

            if ((style & FontConstants.ITALIC) == 0) {
                res += (o2.getNames().isItalic() ? 1 : 0)
                        - (o1.getNames().isItalic() ? 1 : 0);
            }

            if (res != 0) return res;

            res = (o2.getNames().getFullNameLowerCase().contains(fontFamily) ? 1 : 0)
                    - (o1.getNames().getFullNameLowerCase().contains(fontFamily) ? 1 : 0);
            return res;
        }
    }
}
