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
import java.util.Set;

/**
 * Chained Contexts Positioning Format 3: Coverage-based Glyph Contexts
 */
public class PosTableLookup8Format3 extends ChainingContextualTable<ContextualPositionRule> {
    private final PosRuleFormat3 posRule;

    /**
     * Creates a new Chained Contexts Positioning Format 3.
     *
     * @param openReader the OpenType font reader
     * @param lookupFlag specifies processing options, e.g. whether to skip base glyphs, marks or
     *                   ligatures during glyph substitution or positioning. See
     *                   <a href="https://learn.microsoft.com/en-us/typography/opentype/spec/chapter2#lookup-table">Lookup table</a>
     * @param rule the rule
     */
    public PosTableLookup8Format3(OpenTypeFontTableReader openReader, int lookupFlag, PosRuleFormat3 rule) {
        super(openReader, lookupFlag);
        this.posRule = rule;
    }

    @Override
    protected List<ContextualPositionRule> getSetOfRulesForStartGlyph(int startId) {
        PosRuleFormat3 ruleFormat3 = (PosRuleFormat3) this.posRule;
        if (ruleFormat3.getInputCoverage(0).contains(startId) && !openReader.isSkip(startId, lookupFlag)) {
            return Collections.<ContextualPositionRule>singletonList(this.posRule);
        }
        return Collections.<ContextualPositionRule>emptyList();
    }

    /**
     * Represents the positioning rule format3 of an OpenType font.
     */
    public static class PosRuleFormat3 extends ContextualPositionRule {
        private final List<Set<Integer>> inputCoverages;
        private final List<Set<Integer>> backtrackCoverages;
        private final List<Set<Integer>> lookaheadCoverages;
        private final PosLookupRecord[] posLookupRecords;

        /**
         * Creates a new positioning rule format3.
         *
         * @param backtrackCoverages the backtrack coverages
         * @param inputCoverages the input coverages
         * @param lookaheadCoverages the lookahead coverages
         * @param posLookupRecords the positioning lookup records
         */
        public PosRuleFormat3(List<Set<Integer>> backtrackCoverages, List<Set<Integer>> inputCoverages,
                              List<Set<Integer>> lookaheadCoverages, PosLookupRecord[] posLookupRecords) {
            this.backtrackCoverages = backtrackCoverages;
            this.inputCoverages = inputCoverages;
            this.lookaheadCoverages = lookaheadCoverages;
            this.posLookupRecords = posLookupRecords;
        }

        @Override
        public PosLookupRecord[] getPosLookupRecords() {
            return posLookupRecords;
        }

        @Override
        public int getContextLength() {
            return inputCoverages.size();
        }

        /**
         * Returns the input coverage.
         *
         * @param idx the idx
         *
         * @return the requested result
         */
        public Set<Integer> getInputCoverage(int idx) {
            return inputCoverages.get(idx);
        }

        @Override
        public boolean isGlyphMatchesInput(int glyphId, int atIdx) {
            return getInputCoverage(atIdx).contains(glyphId);
        }

        @Override
        public int getLookaheadContextLength() {
            return lookaheadCoverages.size();
        }

        @Override
        public boolean isGlyphMatchesLookahead(int glyphId, int atIdx) {
            return lookaheadCoverages.get(atIdx).contains(glyphId);
        }

        @Override
        public int getBacktrackContextLength() {
            return backtrackCoverages.size();
        }

        @Override
        public boolean isGlyphMatchesBacktrack(int glyphId, int atIdx) {
            return backtrackCoverages.get(atIdx).contains(glyphId);
        }
    }
}
