/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
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
