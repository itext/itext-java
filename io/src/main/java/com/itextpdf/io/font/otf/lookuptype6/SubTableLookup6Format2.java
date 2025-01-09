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
import com.itextpdf.io.font.otf.OtfClass;
import com.itextpdf.io.font.otf.SubstLookupRecord;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Chaining Contextual Substitution Subtable: Class-based Chaining Context Glyph Substitution
 */
public class SubTableLookup6Format2 extends ChainingContextualTable<ContextualSubstRule> {
    private Set<Integer> substCoverageGlyphIds;
    private List<List<ContextualSubstRule>> subClassSets;
    private OtfClass backtrackClassDefinition;
    private OtfClass inputClassDefinition;
    private OtfClass lookaheadClassDefinition;

    public SubTableLookup6Format2(OpenTypeFontTableReader openReader, int lookupFlag, Set<Integer> substCoverageGlyphIds,
                                  OtfClass backtrackClassDefinition, OtfClass inputClassDefinition, OtfClass lookaheadClassDefinition) {
        super(openReader, lookupFlag);
        this.substCoverageGlyphIds = substCoverageGlyphIds;
        this.backtrackClassDefinition = backtrackClassDefinition;
        this.inputClassDefinition = inputClassDefinition;
        this.lookaheadClassDefinition = lookaheadClassDefinition;
    }

    public void setSubClassSets(List<List<ContextualSubstRule>> subClassSets) {
        this.subClassSets = subClassSets;
    }

    @Override
    protected List<ContextualSubstRule> getSetOfRulesForStartGlyph(int startId) {
        if (substCoverageGlyphIds.contains(startId) && !openReader.isSkip(startId, lookupFlag)) {
            int gClass = inputClassDefinition.getOtfClass(startId);
            return subClassSets.get(gClass);
        }
        return Collections.<ContextualSubstRule>emptyList();
    }

    public static class SubstRuleFormat2 extends ContextualSubstRule {
        // inputClassIds array omits the first class in the sequence,
        // the first class is defined by corresponding index of subClassSet array
        private int[] backtrackClassIds;
        private int[] inputClassIds;
        private int[] lookAheadClassIds;

        private SubstLookupRecord[] substLookupRecords;

        private SubTableLookup6Format2 subTable;

        public SubstRuleFormat2(SubTableLookup6Format2 subTable, int[] backtrackClassIds, int[] inputClassIds,
                                int[] lookAheadClassIds, SubstLookupRecord[] substLookupRecords) {
            this.subTable = subTable;
            this.backtrackClassIds = backtrackClassIds;
            this.inputClassIds = inputClassIds;
            this.lookAheadClassIds = lookAheadClassIds;
            this.substLookupRecords = substLookupRecords;
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
        public SubstLookupRecord[] getSubstLookupRecords() {
            return substLookupRecords;
        }

        @Override
        public boolean isGlyphMatchesInput(int glyphId, int atIdx) {
            return subTable.inputClassDefinition.getOtfClass(glyphId) == inputClassIds[atIdx - 1];
        }
        @Override
        public boolean isGlyphMatchesLookahead(int glyphId, int atIdx) {
            return subTable.lookaheadClassDefinition.getOtfClass(glyphId) == lookAheadClassIds[atIdx];
        }
        @Override
        public boolean isGlyphMatchesBacktrack(int glyphId, int atIdx) {
            return subTable.backtrackClassDefinition.getOtfClass(glyphId) == backtrackClassIds[atIdx];
        }
    }
}
