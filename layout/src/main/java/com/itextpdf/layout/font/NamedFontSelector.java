package com.itextpdf.layout.font;

import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.kernel.font.PdfFont;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NamedFontSelector extends FontSelector {

    private List<PdfFont> fonts;

    public NamedFontSelector(List<PdfFont> allFonts, String fontFamily, int style) {
        this.fonts = allFonts;
        Collections.sort(allFonts, getComparator(fontFamily, style));
    }

    @Override
    public PdfFont bestMatch() {
        return fonts.get(0);
    }

    @Override
    public Iterable<PdfFont> getFonts() {
        return fonts;
    }

    protected Comparator<PdfFont> getComparator(String fontFamily, int style) {
        return new PdfFontComparator(fontFamily, style);
    }

    private static class PdfFontComparator implements Comparator<PdfFont> {
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
        public int compare(PdfFont o1, PdfFont o2) {
            FontProgram fp1 = o1.getFontProgram();
            FontProgram fp2 = o2.getFontProgram();

            int res = 0;
            if ((style & FontConstants.BOLD) == 0) {
                res = (fp2.getFontNames().isBold() ? 1 : 0)
                        - (fp1.getFontNames().isBold() ? 1 : 0);
            }

            if ((style & FontConstants.ITALIC) == 0) {
                res += (fp2.getFontNames().isItalic() ? 1 : 0)
                        - (fp1.getFontNames().isItalic() ? 1 : 0);
            }

            if (res != 0) return res;

            res = (fp2.getFontNames().getFullNameLowerCase().contains(fontFamily) ? 1 : 0)
                    - (fp1.getFontNames().getFullNameLowerCase().contains(fontFamily) ? 1 : 0);
            return res;
        }
    }
}
