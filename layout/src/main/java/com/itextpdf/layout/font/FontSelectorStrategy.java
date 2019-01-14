/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
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

import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.kernel.font.PdfFont;

import java.util.List;

/**
 * {@link FontSelectorStrategy} is responsible for splitting text into sub texts with one particular font.
 * {@link #nextGlyphs()} will create next sub text and set current font.
 */
public abstract class FontSelectorStrategy {

    protected String text;
    protected int index;
    protected final FontProvider provider;
    protected final FontSet tempFonts;

    protected FontSelectorStrategy(String text, FontProvider provider, FontSet tempFonts) {
        this.text = text;
        this.index = 0;
        this.provider = provider;
        this.tempFonts = tempFonts;
    }

    public boolean endOfText() {
        return text == null || index >= text.length();
    }

    public abstract PdfFont getCurrentFont();

    public abstract List<Glyph> nextGlyphs();

    /**
     * Utility method to create PdfFont.
     *
     * @param fontInfo instance of FontInfo.
     * @return cached or just created PdfFont on success, otherwise null.
     * @see FontProvider#getPdfFont(FontInfo, FontSet)
     */
    protected PdfFont getPdfFont(FontInfo fontInfo) {
        return provider.getPdfFont(fontInfo, tempFonts);
    }
}
