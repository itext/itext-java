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
package com.itextpdf.io.font.otf.lookuptype8;

import com.itextpdf.io.font.otf.ChainingContextualTable;
import com.itextpdf.io.font.otf.ContextualPositionRule;
import com.itextpdf.io.font.otf.OpenTypeFontTableReader;
import com.itextpdf.io.font.otf.PosLookupRecord;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Chaining Context Positioning Format 1: Simple Glyph Contexts
 */
public class PosTableLookup8Format1 extends ChainingContextualTable<ContextualPositionRule> {
    private final Map<Integer, List<ContextualPositionRule>> posMap;

    /**
     * Creates a new Chaining Context Positioning Format 1.
     *
     * @param openReader the OpenType font reader
     * @param lookupFlag specifies processing options, e.g. whether to skip base glyphs, marks or
     *                   ligatures during glyph substitution or positioning. See
     *                   <a href="https://learn.microsoft.com/en-us/typography/opentype/spec/chapter2#lookup-table">Lookup table</a>
     * @param posMap the positioning map
     */
    public PosTableLookup8Format1(OpenTypeFontTableReader openReader, int lookupFlag,
                                  Map<Integer, List<ContextualPositionRule>> posMap) {
        super(openReader, lookupFlag);
        this.posMap = posMap;
    }

    @Override
    protected List<ContextualPositionRule> getSetOfRulesForStartGlyph(int startGlyphId) {
        if (posMap.containsKey(startGlyphId) && !openReader.isSkip(startGlyphId, lookupFlag)) {
            return posMap.get(startGlyphId);
        }
        return Collections.<ContextualPositionRule>emptyList();
    }

    /**
     * Represents the positioning rule format1 of an OpenType font.
     */
    public static class PosRuleFormat1 extends ContextualPositionRule {
        // inputGlyphIds array omits the first glyph in the sequence,
        // the first glyph is defined by corresponding coverage glyph
        private final int[] inputGlyphIds;
        private final int[] backtrackGlyphIds;
        private final int[] lookAheadGlyphIds;
        private final PosLookupRecord[] posLookupRecords;

        /**
         * Creates a new positioning rule format1.
         *
         * @param backtrackGlyphIds the backtrack glyph ids
         * @param inputGlyphIds the input glyph ids
         * @param lookAheadGlyphIds the look ahead glyph ids
         * @param posLookupRecords the positioning lookup records
         */
        public PosRuleFormat1(int[] backtrackGlyphIds, int[] inputGlyphIds, int[] lookAheadGlyphIds,
                              PosLookupRecord[] posLookupRecords) {
            this.backtrackGlyphIds = backtrackGlyphIds;
            this.inputGlyphIds = inputGlyphIds;
            this.lookAheadGlyphIds = lookAheadGlyphIds;
            this.posLookupRecords = posLookupRecords;
        }

        @Override
        public int getContextLength() {
            return inputGlyphIds.length + 1;
        }

        @Override
        public int getLookaheadContextLength() {
            return lookAheadGlyphIds.length;
        }

        @Override
        public int getBacktrackContextLength() {
            return backtrackGlyphIds.length;
        }

        @Override
        public PosLookupRecord[] getPosLookupRecords() {
            return posLookupRecords;
        }

        @Override
        public boolean isGlyphMatchesInput(int glyphId, int atIdx) {
            return glyphId == inputGlyphIds[atIdx - 1];
        }

        @Override
        public boolean isGlyphMatchesLookahead(int glyphId, int atIdx) {
            return glyphId == lookAheadGlyphIds[atIdx];
        }

        @Override
        public boolean isGlyphMatchesBacktrack(int glyphId, int atIdx) {
            return glyphId == backtrackGlyphIds[atIdx];
        }
    }
}
