/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
package com.itextpdf.layout.renderer;

import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.io.font.otf.GlyphLine;
import com.itextpdf.io.util.TextUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Layout-only view of combined text. Each run is one atomic, one-em glyph; forced newlines remain separate glyphs.
 * The source glyphs (including shaping and ActualText) are never modified or replaced for drawing.
 */
final class TextCombineUprightGlyphLine extends GlyphLine {
    private final GlyphLine source;
    private final List<Integer> sourcePositions = new ArrayList<>();
    private final float ascender;
    private final float descender;

    TextCombineUprightGlyphLine(GlyphLine source, float ascender, float descender) {
        this.source = source;
        this.ascender = ascender;
        this.descender = descender;
        int pos = source.getStart();
        while (pos < source.getEnd()) {
            sourcePositions.add(pos);
            add(new PlaceholderGlyph(Math.max(FontProgram.UNITS_NORMALIZATION, ascender - descender)));
            while (pos < source.getEnd() && !TextUtil.isNewLine(source.get(pos))) {
                ++pos;
            }
            if (pos < source.getEnd()) {
                boolean crlf = TextUtil.isCarriageReturnFollowedByLineFeed(source, pos);
                sourcePositions.add(pos);
                add(source.get(pos++));
                if (crlf) {
                    sourcePositions.add(pos);
                    add(source.get(pos++));
                }
            }
        }
        sourcePositions.add(source.getEnd());
        setEnd(size());
    }

    float getAscender() {
        return ascender;
    }

    float getDescender() {
        return descender;
    }

    int getSourcePosition(int position) {
        return sourcePositions.get(Math.max(0, position));
    }

    GlyphLine restore(GlyphLine laidOutLine) {
        GlyphLine restored = new GlyphLine(source);
        restored.setStart(getSourcePosition(laidOutLine.getStart()));
        restored.setEnd(getSourcePosition(laidOutLine.getEnd()));
        return restored;
    }

    static final class PlaceholderGlyph extends Glyph {
        private final float layoutWidth;

        PlaceholderGlyph(float layoutWidth) {
            // U+FFFC OBJECT REPLACEMENT CHARACTER is only used for layout, never written to the PDF.
            super(-1, (int) Math.ceil(layoutWidth), 0xFFFC);
            this.layoutWidth = layoutWidth;
        }

        float getLayoutWidth() {
            return layoutWidth;
        }
    }
}
