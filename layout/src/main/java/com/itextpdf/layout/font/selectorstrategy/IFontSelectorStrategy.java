/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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
package com.itextpdf.layout.font.selectorstrategy;

import com.itextpdf.commons.datastructures.Tuple2;
import com.itextpdf.io.font.otf.GlyphLine;
import com.itextpdf.kernel.font.PdfFont;

import java.util.List;

/**
 * The font selector strategy is responsible for splitting text into parts with one particular font.
 */
public interface IFontSelectorStrategy {
    /**
     * Converts text into glyphs with the best matching font.
     *
     * @param text the text to split
     * @return the glyphs with the matching font attached
     */
    List<Tuple2<GlyphLine, PdfFont>> getGlyphLines(String text);
}
