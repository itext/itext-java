/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.layout.font;

import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.layout.font.selectorstrategy.IFontSelectorStrategy;

import java.util.List;

/**
 * {@link FontSelectorStrategy} is responsible for splitting text into sub texts with one particular font.
 * {@link #nextGlyphs()} will create next sub text and set current font.
 * @deprecated replaced by {@link IFontSelectorStrategy}.
 */
@Deprecated
public abstract class FontSelectorStrategy {

    protected String text;
    protected int index;
    protected final FontProvider provider;
    protected final FontSet additionalFonts;

    protected FontSelectorStrategy(String text, FontProvider provider, FontSet additionalFonts) {
        this.text = text;
        this.index = 0;
        this.provider = provider;
        this.additionalFonts = additionalFonts;
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
        return provider.getPdfFont(fontInfo, additionalFonts);
    }
}
