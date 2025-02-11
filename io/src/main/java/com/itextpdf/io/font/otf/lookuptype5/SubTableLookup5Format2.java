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
package com.itextpdf.io.font.otf.lookuptype5;

import com.itextpdf.io.font.otf.ContextualSubstRule;
import com.itextpdf.io.font.otf.ContextualTable;
import com.itextpdf.io.font.otf.OpenTypeFontTableReader;
import com.itextpdf.io.font.otf.OtfClass;
import com.itextpdf.io.font.otf.SubstLookupRecord;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Contextual Substitution Subtable: Class-based context glyph substitution
 */
public class SubTableLookup5Format2 extends ContextualTable<ContextualSubstRule> {
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
        return Collections.<ContextualSubstRule>emptyList();
    }

    public static class SubstRuleFormat2 extends ContextualSubstRule {
        // inputClassIds array omits the first class in the sequence,
        // the first class is defined by corresponding index of subClassSet array
        private int[] inputClassIds;
        private SubstLookupRecord[] substLookupRecords;
        private OtfClass classDefinition;

        public SubstRuleFormat2(SubTableLookup5Format2 subTable, int[] inputClassIds, SubstLookupRecord[] substLookupRecords) {
            this.inputClassIds = inputClassIds;
            this.substLookupRecords = substLookupRecords;
            this.classDefinition = subTable.classDefinition;
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
