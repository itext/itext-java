/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
package com.itextpdf.io.font.otf.lookuptype6;

import com.itextpdf.io.font.otf.ContextualSubstRule;
import com.itextpdf.io.font.otf.ContextualSubTable;
import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.io.font.otf.GlyphLine;
import com.itextpdf.io.font.otf.OpenTableLookup;
import com.itextpdf.io.font.otf.OpenTypeFontTableReader;

import java.util.List;

public abstract class SubTableLookup6 extends ContextualSubTable {

    private static final long serialVersionUID = -7471613803606544198L;

    protected SubTableLookup6(OpenTypeFontTableReader openReader, int lookupFlag) {
        super(openReader, lookupFlag);
    }

    @Override
    public ContextualSubstRule getMatchingContextRule(GlyphLine line) {
        if (line.idx >= line.end)
            return null;

        Glyph g = line.get(line.idx);
        List<ContextualSubstRule> rules = getSetOfRulesForStartGlyph(g.getCode());
        for (ContextualSubstRule rule : rules) {
            int lastGlyphIndex = checkIfContextMatch(line, rule);

            if (lastGlyphIndex != -1
                    && checkIfLookaheadContextMatch(line, rule, lastGlyphIndex)
                    && checkIfBacktrackContextMatch(line, rule)) {

                line.start = line.idx;
                line.end = lastGlyphIndex + 1;
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
    protected boolean checkIfLookaheadContextMatch(GlyphLine line, ContextualSubstRule rule, int startIdx) {
        int j;
        OpenTableLookup.GlyphIndexer gidx = new OpenTableLookup.GlyphIndexer();
        gidx.line = line;
        gidx.idx = startIdx;
        for (j = 0; j < rule.getLookaheadContextLength(); ++j) {
            gidx.nextGlyph(openReader, lookupFlag);
            if (gidx.glyph == null || !rule.isGlyphMatchesLookahead(gidx.glyph.getCode(), j)) {
                break;
            }
        }
        return j == rule.getLookaheadContextLength();
    }

    /**
     * Checks if given glyph line at the given position matches given rule.
     *
     * @param line glyph line to be checked
     * @param rule rule to be compared with a given line
     * @return true if given glyph line matches given rule
     */
    protected boolean checkIfBacktrackContextMatch(GlyphLine line, ContextualSubstRule rule) {
        int j;
        OpenTableLookup.GlyphIndexer gidx = new OpenTableLookup.GlyphIndexer();
        gidx.line = line;
        gidx.idx = line.idx;
        for (j = 0; j < rule.getBacktrackContextLength(); ++j) {
            gidx.previousGlyph(openReader, lookupFlag);
            if (gidx.glyph == null || !rule.isGlyphMatchesBacktrack(gidx.glyph.getCode(), j)) {
                break;
            }
        }
        return j == rule.getBacktrackContextLength();
    }

}
