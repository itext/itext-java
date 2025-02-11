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
package com.itextpdf.io.font.otf.lookuptype7;

import com.itextpdf.io.font.otf.ContextualPositionRule;
import com.itextpdf.io.font.otf.ContextualTable;
import com.itextpdf.io.font.otf.OpenTypeFontTableReader;
import com.itextpdf.io.font.otf.OtfClass;
import com.itextpdf.io.font.otf.PosLookupRecord;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class PosTableLookup7Format2 extends ContextualTable<ContextualPositionRule> {
    private Set<Integer> posCoverageGlyphIds;
    private List<List<ContextualPositionRule>> subClassSets;
    private OtfClass classDefinition;

    public PosTableLookup7Format2(OpenTypeFontTableReader openReader, int lookupFlag, Set<Integer> posCoverageGlyphIds,
            OtfClass classDefinition) {
        super(openReader, lookupFlag);
        this.posCoverageGlyphIds = posCoverageGlyphIds;

        this.classDefinition = classDefinition;
    }

    public void setPosClassSets(List<List<ContextualPositionRule>> subClassSets) {
        this.subClassSets = subClassSets;
    }

    @Override
    protected List<ContextualPositionRule> getSetOfRulesForStartGlyph(int startId) {
        if (posCoverageGlyphIds.contains(startId) && !openReader.isSkip(startId, lookupFlag)) {
            int gClass = classDefinition.getOtfClass(startId);
            return subClassSets.get(gClass);
        }
        return Collections.<ContextualPositionRule>emptyList();
    }

    public static class PosRuleFormat2 extends ContextualPositionRule {
        // inputClassIds array omits the first class in the sequence,
        // the first class is defined by corresponding index of subClassSet array
        private int[] inputClassIds;
        private PosLookupRecord[] posLookupRecords;
        private OtfClass classDefinition;

        public PosRuleFormat2(PosTableLookup7Format2 subTable, int[] inputClassIds,
                PosLookupRecord[] posLookupRecords) {
            this.inputClassIds = inputClassIds;
            this.posLookupRecords = posLookupRecords;
            this.classDefinition = subTable.classDefinition;
        }

        @Override
        public int getContextLength() {
            return inputClassIds.length + 1;
        }

        @Override
        public PosLookupRecord[] getPosLookupRecords() {
            return posLookupRecords;
        }

        @Override
        public boolean isGlyphMatchesInput(int glyphId, int atIdx) {
            return classDefinition.getOtfClass(glyphId) == inputClassIds[atIdx - 1];
        }
    }
}
