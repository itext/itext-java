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
    private PosRuleFormat3 posRule;

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

    public static class PosRuleFormat3 extends ContextualPositionRule {
        private List<Set<Integer>> inputCoverages;
        private List<Set<Integer>> backtrackCoverages;
        private List<Set<Integer>> lookaheadCoverages;
        private PosLookupRecord[] posLookupRecords;

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
