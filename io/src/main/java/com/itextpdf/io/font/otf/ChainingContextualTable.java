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
package com.itextpdf.io.font.otf;

import java.util.List;

public abstract class ChainingContextualTable<T extends ContextualRule> extends ContextualTable<T> {

    protected ChainingContextualTable(OpenTypeFontTableReader openReader, int lookupFlag) {
        super(openReader, lookupFlag);
    }

    @Override
    public T getMatchingContextRule(GlyphLine line) {
        if (line.getIdx() >= line.getEnd()) {
            return null;
        }

        Glyph g = line.get(line.getIdx());
        List<T> rules = getSetOfRulesForStartGlyph(g.getCode());
        for (T rule : rules) {
            int lastGlyphIndex = checkIfContextMatch(line, rule);

            if (lastGlyphIndex != -1
                    && checkIfLookaheadContextMatch(line, rule, lastGlyphIndex)
                    && checkIfBacktrackContextMatch(line, rule)) {

                line.setStart(line.getIdx());
                line.setEnd(lastGlyphIndex + 1);
                return rule;
            }
        }

        return null;
    }


    /**
     * Checks if given glyph line at the given position matches given rule.
     *
     * @param line glyph line to be checked
     * @param rule rule to be compared with a given line
     * @param startIdx glyph line position
     * @return true if given glyph line at the given position matches given rule
     */
    protected boolean checkIfLookaheadContextMatch(GlyphLine line, T rule, int startIdx) {
        OpenTableLookup.GlyphIndexer gidx = new OpenTableLookup.GlyphIndexer();
        gidx.setLine(line);
        gidx.setIdx(startIdx);
        for (int j = 0; j < rule.getLookaheadContextLength(); ++j) {
            gidx.nextGlyph(openReader, lookupFlag);
            if (gidx.getGlyph() == null || !rule.isGlyphMatchesLookahead(gidx.getGlyph().getCode(), j)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if given glyph line at the given position matches given rule.
     *
     * @param line glyph line to be checked
     * @param rule rule to be compared with a given line
     * @return true if given glyph line matches given rule
     */
    protected boolean checkIfBacktrackContextMatch(GlyphLine line, T rule) {
        OpenTableLookup.GlyphIndexer gidx = new OpenTableLookup.GlyphIndexer();
        gidx.setLine(line);
        gidx.setIdx(line.getIdx());
        for (int j = 0; j < rule.getBacktrackContextLength(); ++j) {
            gidx.previousGlyph(openReader, lookupFlag);
            if (gidx.getGlyph() == null || !rule.isGlyphMatchesBacktrack(gidx.getGlyph().getCode(), j)) {
                return false;
            }
        }
        return true;
    }

}
