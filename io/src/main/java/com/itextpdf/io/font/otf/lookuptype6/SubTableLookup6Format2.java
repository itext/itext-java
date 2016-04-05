package com.itextpdf.io.font.otf.lookuptype6;

import com.itextpdf.io.font.otf.ContextualSubstRule;
import com.itextpdf.io.font.otf.OpenTypeFontTableReader;
import com.itextpdf.io.font.otf.OtfClass;
import com.itextpdf.io.font.otf.SubstLookupRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Chaining Contextual Substitution Subtable: Class-based Chaining Context Glyph Substitution
 */
public class SubTableLookup6Format2 extends SubTableLookup6 {
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
        //return Collections.emptyList();
        return new ArrayList<>(0);
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
