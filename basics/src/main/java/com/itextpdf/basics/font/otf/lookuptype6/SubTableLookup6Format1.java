package com.itextpdf.basics.font.otf.lookuptype6;

import com.itextpdf.basics.font.otf.ContextualSubstRule;
import com.itextpdf.basics.font.otf.OpenTypeFontTableReader;
import com.itextpdf.basics.font.otf.SubstLookupRecord;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Chaining Contextual Substitution Subtable: Simple Chaining Context Glyph Substitution
 */
public class SubTableLookup6Format1 extends SubTableLookup6 {
    private HashMap<Integer, List<ContextualSubstRule>> substMap;

    public SubTableLookup6Format1(OpenTypeFontTableReader openReader, int lookupFlag, HashMap<Integer, List<ContextualSubstRule>> substMap) {
        super(openReader, lookupFlag);
        this.substMap = substMap;
    }

    @Override
    protected List<ContextualSubstRule> getSetOfRulesForStartGlyph(int startGlyphId) {
        if (substMap.containsKey(startGlyphId) && !openReader.isSkip(startGlyphId, lookupFlag)) {
            return substMap.get(startGlyphId);
        }
        return Collections.emptyList();
    }

    public static class SubstRuleFormat1 extends ContextualSubstRule {
        // inputGlyphIds array omits the first glyph in the sequence,
        // the first glyph is defined by corresponding coverage glyph
        private int[] inputGlyphIds;
        private int[] backtrackGlyphIds;
        private int[] lookAheadGlyphIds;
        private SubstLookupRecord[] substLookupRecords;

        public SubstRuleFormat1(int[] backtrackGlyphIds, int[] inputGlyphIds, int[] lookAheadGlyphIds, SubstLookupRecord[] substLookupRecords) {
            this.backtrackGlyphIds = backtrackGlyphIds;
            this.inputGlyphIds = inputGlyphIds;
            this.lookAheadGlyphIds = lookAheadGlyphIds;
            this.substLookupRecords = substLookupRecords;
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
        public SubstLookupRecord[] getSubstLookupRecords() {
            return substLookupRecords;
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
