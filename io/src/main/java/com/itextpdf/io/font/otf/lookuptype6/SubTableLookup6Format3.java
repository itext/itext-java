package com.itextpdf.io.font.otf.lookuptype6;

import com.itextpdf.io.font.otf.ContextualSubstRule;
import com.itextpdf.io.font.otf.OpenTypeFontTableReader;
import com.itextpdf.io.font.otf.SubstLookupRecord;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Chaining Contextual Substitution Subtable: Coverage-based Chaining Context Glyph Substitution
 */
public class SubTableLookup6Format3 extends SubTableLookup6 {
    ContextualSubstRule substitutionRule;

    public SubTableLookup6Format3(OpenTypeFontTableReader openReader, int lookupFlag, SubstRuleFormat3 rule) {
        super(openReader, lookupFlag);
        this.substitutionRule = rule;
    }

    @Override
    protected List<ContextualSubstRule> getSetOfRulesForStartGlyph(int startId) {
        SubstRuleFormat3 ruleFormat3 = (SubstRuleFormat3) this.substitutionRule;
        if (ruleFormat3.inputCoverages.get(0).contains(startId) && !openReader.isSkip(startId, lookupFlag)) {
            return Collections.singletonList(this.substitutionRule);
        }
        return Collections.emptyList();
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
