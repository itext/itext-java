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
package com.itextpdf.io.font.otf;

import com.itextpdf.io.util.TextUtil;

import java.util.Iterator;

public class ActualTextIterator implements Iterator<GlyphLine.GlyphLinePart> {

    private GlyphLine glyphLine;

    public ActualTextIterator(GlyphLine glyphLine) {
        this.glyphLine = glyphLine;
        this.pos = glyphLine.getStart();
    }

    public ActualTextIterator(GlyphLine glyphLine, int start, int end) {
        this(new GlyphLine(glyphLine.glyphs, glyphLine.actualText, start, end));
    }

    private int pos;

    @Override
    public boolean hasNext() {
        return pos < glyphLine.getEnd();
    }

    @Override
    public GlyphLine.GlyphLinePart next() {
        if (glyphLine.actualText == null) {
            GlyphLine.GlyphLinePart result = new GlyphLine.GlyphLinePart(pos, glyphLine.getEnd(), null);
            pos = glyphLine.getEnd();
            return result;
        } else {
            GlyphLine.GlyphLinePart currentResult = nextGlyphLinePart(pos);
            if (currentResult == null) {
                return null;
            }
            pos = currentResult.getEnd();

            if (!glyphLinePartNeedsActualText(currentResult)) {
                currentResult.setActualText(null);
                // Try to add more pieces without "actual text"
                while (pos < glyphLine.getEnd()) {
                    GlyphLine.GlyphLinePart nextResult = nextGlyphLinePart(pos);
                    if (nextResult != null && !glyphLinePartNeedsActualText(nextResult)) {
                        currentResult.setEnd(nextResult.getEnd());
                        pos = nextResult.getEnd();
                    } else {
                        break;
                    }
                }
            }
            return currentResult;
        }
    }

    @Override
    public void remove() {
        throw new IllegalStateException("Operation not supported");
    }

    private GlyphLine.GlyphLinePart nextGlyphLinePart(int pos) {
        if (pos >= glyphLine.getEnd()) {
            return null;
        }
        int startPos = pos;
        GlyphLine.ActualText startActualText = glyphLine.actualText.get(pos);
        while (pos < glyphLine.getEnd() && glyphLine.actualText.get(pos) == startActualText) {
            pos++;
        }
        return new GlyphLine.GlyphLinePart(startPos, pos, startActualText != null ? startActualText.getValue() : null);
    }

    private boolean glyphLinePartNeedsActualText(GlyphLine.GlyphLinePart glyphLinePart) {
        if (glyphLinePart.getActualText() == null) {
            return false;
        }
        boolean needsActualText = false;
        StringBuilder toUnicodeMapResult = new StringBuilder();
        for (int i = glyphLinePart.getStart(); i < glyphLinePart.getEnd(); i++) {
            Glyph currentGlyph = glyphLine.glyphs.get(i);
            if (!currentGlyph.hasValidUnicode()) {
                needsActualText = true;
                break;
            }
            toUnicodeMapResult.append(TextUtil.convertFromUtf32(currentGlyph.getUnicode()));
        }

        return needsActualText || !toUnicodeMapResult.toString().equals(glyphLinePart.getActualText());
    }
}
