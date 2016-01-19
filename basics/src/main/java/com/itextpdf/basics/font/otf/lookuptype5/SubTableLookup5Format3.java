package com.itextpdf.basics.font.otf.lookuptype5;

import com.itextpdf.basics.font.otf.ContextualSubstRule;
import com.itextpdf.basics.font.otf.ContextualSubTable;
import com.itextpdf.basics.font.otf.OpenTypeFontTableReader;
import com.itextpdf.basics.font.otf.SubstLookupRecord;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Contextual Substitution Subtable: Coverage-based context glyph substitution
 */
public class SubTableLookup5Format3 extends ContextualSubTable {
    ContextualSubstRule substitutionRule;

    public SubTableLookup5Format3(OpenTypeFontTableReader openReader, int lookupFlag, SubstRuleFormat3 rule) {
        super(openReader, lookupFlag);
        this.substitutionRule = rule;
    }

    @Override
    protected List<ContextualSubstRule> getSetOfRulesForStartGlyph(int startId) {
        SubstRuleFormat3 ruleFormat3 = (SubstRuleFormat3) this.substitutionRule;
        if (ruleFormat3.coverages.get(0).contains(startId) && !openReader.isSkip(startId, lookupFlag)) {
            return Collections.singletonList(this.substitutionRule);
        }
        return Collections.emptyList();
    }

    public static class SubstRuleFormat3 extends ContextualSubstRule {
        List<Set<Integer>> coverages;
        SubstLookupRecord[] substLookupRecords;

        public SubstRuleFormat3(List<Set<Integer>> coverages, SubstLookupRecord[] substLookupRecords) {
            this.coverages = coverages;
            this.substLookupRecords = substLookupRecords;
        }

        @Override
        public int getContextLength() {
            return coverages.size();
        }

        @Override
        public SubstLookupRecord[] getSubstLookupRecords() {
            return substLookupRecords;
        }

        @Override
        public boolean isGlyphMatchesInput(int glyphId, int atIdx) {
            return coverages.get(atIdx).contains(glyphId);
        }
    }

}
