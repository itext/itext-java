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
package com.itextpdf.io.font.otf.lookuptype6;

import com.itextpdf.io.font.otf.ChainingContextualTable;
import com.itextpdf.io.font.otf.ContextualSubstRule;
import com.itextpdf.io.font.otf.OpenTypeFontTableReader;
import com.itextpdf.io.font.otf.SubstLookupRecord;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Chaining Contextual Substitution Subtable: Coverage-based Chaining Context Glyph Substitution
 */
public class SubTableLookup6Format3 extends ChainingContextualTable<ContextualSubstRule> {
    ContextualSubstRule substitutionRule;

    public SubTableLookup6Format3(OpenTypeFontTableReader openReader, int lookupFlag, SubstRuleFormat3 rule) {
        super(openReader, lookupFlag);
        this.substitutionRule = rule;
    }

    @Override
    protected List<ContextualSubstRule> getSetOfRulesForStartGlyph(int startId) {
        SubstRuleFormat3 ruleFormat3 = (SubstRuleFormat3) this.substitutionRule;
        if (ruleFormat3.inputCoverages.get(0).contains(startId) && !openReader.isSkip(startId, lookupFlag)) {
            return Collections.<ContextualSubstRule>singletonList(this.substitutionRule);
        }
        return Collections.<ContextualSubstRule>emptyList();
    }

    public static class SubstRuleFormat3 extends ContextualSubstRule {
        List<Set<Integer>> backtrackCoverages;
        List<Set<Integer>> inputCoverages;
        List<Set<Integer>> lookaheadCoverages;
        SubstLookupRecord[] substLookupRecords;

        public SubstRuleFormat3(List<Set<Integer>> backtrackCoverages, List<Set<Integer>> inputCoverages,
                                List<Set<Integer>> lookaheadCoverages, SubstLookupRecord[] substLookupRecords) {
            this.backtrackCoverages = backtrackCoverages;
            this.inputCoverages = inputCoverages;
            this.lookaheadCoverages = lookaheadCoverages;
            this.substLookupRecords = substLookupRecords;
        }

        @Override
        public int getContextLength() {
            return inputCoverages.size();
        }
        @Override
        public int getLookaheadContextLength() {
            return lookaheadCoverages.size();
        }
        @Override
        public int getBacktrackContextLength() {
            return backtrackCoverages.size();
        }

        @Override
        public SubstLookupRecord[] getSubstLookupRecords() {
            return substLookupRecords;
        }

        @Override
        public boolean isGlyphMatchesInput(int glyphId, int atIdx) {
            return inputCoverages.get(atIdx).contains(glyphId);
        }
        @Override
        public boolean isGlyphMatchesLookahead(int glyphId, int atIdx) {
            return lookaheadCoverages.get(atIdx).contains(glyphId);
        }
        @Override
        public boolean isGlyphMatchesBacktrack(int glyphId, int atIdx) {
            return backtrackCoverages.get(atIdx).contains(glyphId);
        }
    }
}
