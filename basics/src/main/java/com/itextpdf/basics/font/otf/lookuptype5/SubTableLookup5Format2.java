package com.itextpdf.basics.font.otf.lookuptype5;

import com.itextpdf.basics.font.otf.ContextualSubstRule;
import com.itextpdf.basics.font.otf.ContextualSubTable;
import com.itextpdf.basics.font.otf.OpenTypeFontTableReader;
import com.itextpdf.basics.font.otf.OtfClass;
import com.itextpdf.basics.font.otf.SubstLookupRecord;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Contextual Substitution Subtable: Class-based context glyph substitution
 */
public class SubTableLookup5Format2 extends ContextualSubTable {
    private Set<Integer> substCoverageGlyphIds;
    private List<List<ContextualSubstRule>> subClassSets;
    private OtfClass classDefinition;

    public SubTableLookup5Format2(OpenTypeFontTableReader openReader, int lookupFlag, Set<Integer> substCoverageGlyphIds, OtfClass classDefinition) {
        super(openReader, lookupFlag);
        this.substCoverageGlyphIds = substCoverageGlyphIds;

        this.classDefinition = classDefinition;
    }

    public void setSubClassSets(List<List<ContextualSubstRule>> subClassSets) {
        this.subClassSets = subClassSets;
    }

    @Override
    protected List<ContextualSubstRule> getSetOfRulesForStartGlyph(int startId) {
        if (substCoverageGlyphIds.contains(startId) && !openReader.isSkip(startId, lookupFlag)) {
            int gClass = classDefinition.getOtfClass(startId);
            return subClassSets.get(gClass);
        }
        return Collections.emptyList();
    }

    public class SubstRuleFormat2 extends ContextualSubstRule {
        // inputClassIds array omits the first class in the sequence,
        // the first class is defined by corresponding index of subClassSet array
        private int[] inputClassIds;
        private SubstLookupRecord[] substLookupRecords;

        public SubstRuleFormat2(int[] inputClassIds, SubstLookupRecord[] substLookupRecords) {
            this.inputClassIds = inputClassIds;
            this.substLookupRecords = substLookupRecords;
        }

        @Override
        public int getContextLength() {
            return inputClassIds.length + 1;
        }

        @Override
        public SubstLookupRecord[] getSubstLookupRecords() {
            return substLookupRecords;
        }

        @Override
        public boolean isGlyphMatchesInput(int glyphId, int atIdx) {
            return classDefinition.getOtfClass(glyphId) == inputClassIds[atIdx - 1];
        }

    }
}
