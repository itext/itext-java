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

public abstract class ContextualTable<T extends ContextualRule> {

    protected OpenTypeFontTableReader openReader;
    protected int lookupFlag;

    protected ContextualTable(OpenTypeFontTableReader openReader, int lookupFlag) {
        this.openReader = openReader;
        this.lookupFlag = lookupFlag;
    }

    /**
     * Gets a most preferable context rule that matches the line at current position. If no matching context rule is
     * found, it returns <code>null</code>.
     * <br><br>
     * NOTE: if matching context rule is found, the <code>GlyphLine.start</code> and <code>GlyphLine.end</code>
     * will be changed in such way that they will point at start and end of the matching context glyph sequence
     * inside the glyph line.
     * @param line a line, which is to be checked if it matches some context.
     * @return matching context rule or null, if none was found.
     */
    public T getMatchingContextRule(GlyphLine line) {
        if (line.getIdx() >= line.getEnd()) {
            return null;
        }

        Glyph g = line.get(line.getIdx());
        List<T> rules = getSetOfRulesForStartGlyph(g.getCode());
        for (T rule : rules) {
            int lastGlyphIndex = checkIfContextMatch(line, rule);
            if (lastGlyphIndex != -1) {
                line.setStart(line.getIdx());
                line.setEnd(lastGlyphIndex + 1);
                return rule;
            }
        }

        return null;
    }

    /**
     * Gets a set of rules, which start with given glyph id.
     *
     * @param startId id of the first glyph in the sequence
     * @return a list of {@link ContextualSubstRule} instances. The list will be empty if there are no rules
     *     that start with a given glyph id
     */
    protected abstract List<T> getSetOfRulesForStartGlyph(int startId);

    /**
     * Checks if given glyph line matches given rule.
     *
     * @param line glyph line to be checked
     * @param rule rule to be compared with a given glyph line
     * @return either index which corresponds to the last glyph of the matching context inside the glyph line
     *     if context matches, or -1 if context doesn't match
     */
    protected int checkIfContextMatch(GlyphLine line, T rule) {
        int j;
        OpenTableLookup.GlyphIndexer gidx = new OpenTableLookup.GlyphIndexer();
        gidx.setLine(line);
        gidx.setIdx(line.getIdx());

        //Note, that starting index shall be 1
        for (j = 1; j < rule.getContextLength(); ++j) {
            gidx.nextGlyph(openReader, lookupFlag);
            if (gidx.getGlyph() == null || !rule.isGlyphMatchesInput(gidx.getGlyph().getCode(), j)) {
                break;
            }
        }

        boolean isMatch = j == rule.getContextLength();
        if (isMatch) {
            return gidx.getIdx();
        } else {
            return -1;
        }
    }
}
