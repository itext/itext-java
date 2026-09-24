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
import com.itextpdf.io.font.otf.OtfClass;
import com.itextpdf.io.font.otf.PosLookupRecord;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Chained Contexts Positioning Format 2: Class-based Glyph Contexts
 */
public class PosTableLookup8Format2 extends ChainingContextualTable<ContextualPositionRule> {
    private final Set<Integer> posCoverageGlyphIds;
    private final List<List<ContextualPositionRule>> posClassSets;
    private final OtfClass backtrackClassDefinition;
    private final OtfClass inputClassDefinition;
    private final OtfClass lookaheadClassDefinition;

    /**
     * Creates a new Chained Contexts Positioning Format 2.
     *
     * @param openReader the OpenType font reader
     * @param lookupFlag specifies processing options, e.g. whether to skip base glyphs, marks or
     *                   ligatures during glyph substitution or positioning. See
     *                   <a href="https://learn.microsoft.com/en-us/typography/opentype/spec/chapter2#lookup-table">Lookup table</a>
     * @param posCoverageGlyphIds the positioning coverage glyph ids
     * @param backtrackClassDefinition the backtrack class definition
     * @param inputClassDefinition the input class definition
     * @param lookaheadClassDefinition the lookahead class definition
     */
    public PosTableLookup8Format2(OpenTypeFontTableReader openReader, int lookupFlag, Set<Integer> posCoverageGlyphIds,
                                  OtfClass backtrackClassDefinition, OtfClass inputClassDefinition,
                                  OtfClass lookaheadClassDefinition) {
        super(openReader, lookupFlag);
        this.posCoverageGlyphIds = posCoverageGlyphIds;
        this.backtrackClassDefinition = backtrackClassDefinition;
        this.inputClassDefinition = inputClassDefinition;
        this.lookaheadClassDefinition = lookaheadClassDefinition;
        this.posClassSets = new ArrayList<>();
    }

    /**
     * Adds the positioning class set.
     *
     * @param posClassSet the positioning class set
     */
    public void addPosClassSet(List<ContextualPositionRule> posClassSet) {
        for (ContextualPositionRule rule : posClassSet) {
            if (((PosRuleFormat2) rule).getPosTable() != this) {
                throw new IllegalArgumentException("Position class set is invalid. Position rule refers to another table");
            }
        }
        this.posClassSets.add(posClassSet);
    }

    @Override
    protected List<ContextualPositionRule> getSetOfRulesForStartGlyph(int startId) {
        if (posCoverageGlyphIds.contains(startId) && !openReader.isSkip(startId, lookupFlag)) {
            int gClass = inputClassDefinition.getOtfClass(startId);
            return posClassSets.get(gClass);
        }
        return Collections.<ContextualPositionRule>emptyList();
    }

    /**
     * Represents the positioning rule format2 of an OpenType font.
     */
    public static class PosRuleFormat2 extends ContextualPositionRule {
        // inputClassIds array omits the first class in the sequence,
        // the first class is defined by corresponding index of subClassSet array
        private final int[] backtrackClassIds;
        private final int[] inputClassIds;
        private final int[] lookAheadClassIds;

        private final PosLookupRecord[] posLookupRecords;

        private final PosTableLookup8Format2 posTable;

        /**
         * Creates a new ositioning rule format2.
         *
         * @param posTable the positioning table
         * @param backtrackClassIds the backtrack class ids
         * @param inputClassIds the input class ids
         * @param lookAheadClassIds the look ahead class ids
         * @param posLookupRecords the positioning lookup records
         */
        public PosRuleFormat2(PosTableLookup8Format2 posTable, int[] backtrackClassIds, int[] inputClassIds,
                              int[] lookAheadClassIds, PosLookupRecord[] posLookupRecords) {
            this.posTable = posTable;
            this.backtrackClassIds = backtrackClassIds;
            this.inputClassIds = inputClassIds;
            this.lookAheadClassIds = lookAheadClassIds;
            this.posLookupRecords = posLookupRecords;
        }

        @Override
        public int getContextLength() {
            return inputClassIds.length + 1;
        }

        @Override
        public int getLookaheadContextLength() {
            return lookAheadClassIds.length;
        }

        @Override
        public int getBacktrackContextLength() {
            return backtrackClassIds.length;
        }

        @Override
        public PosLookupRecord[] getPosLookupRecords() {
            return posLookupRecords;
        }

        @Override
        public boolean isGlyphMatchesInput(int glyphId, int atIdx) {
            return posTable.inputClassDefinition.getOtfClass(glyphId) == inputClassIds[atIdx - 1];
        }

        @Override
        public boolean isGlyphMatchesLookahead(int glyphId, int atIdx) {
            return posTable.lookaheadClassDefinition.getOtfClass(glyphId) == lookAheadClassIds[atIdx];
        }

        @Override
        public boolean isGlyphMatchesBacktrack(int glyphId, int atIdx) {
            return posTable.backtrackClassDefinition.getOtfClass(glyphId) == backtrackClassIds[atIdx];
        }

        /**
         * Returns the positioning table.
         *
         * @return the requested result
         */
        public PosTableLookup8Format2 getPosTable() {
            return posTable;
        }
    }
}
