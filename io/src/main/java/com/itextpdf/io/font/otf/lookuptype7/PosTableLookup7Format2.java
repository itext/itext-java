/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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

/**
 * Contextual Positioning Subtable: Class-Based Glyph Contexts.
 */
public class PosTableLookup7Format2 extends ContextualTable<ContextualPositionRule> {
    private final Set<Integer> posCoverageGlyphIds;
    private final OtfClass classDefinition;
    private List<List<ContextualPositionRule>> posClassSets;

    /**
     * Creates a new Contextual Positioning Subtable.
     *
     * @param openReader the OpenType font reader
     * @param lookupFlag specifies processing options, e.g. whether to skip base glyphs, marks or
     *                   ligatures during glyph substitution or positioning. See
     *                   <a href="https://learn.microsoft.com/en-us/typography/opentype/spec/chapter2#lookup-table">Lookup table</a>
     * @param posCoverageGlyphIds the positioning coverage glyph ids
     * @param classDefinition the class definition
     */
    public PosTableLookup7Format2(OpenTypeFontTableReader openReader, int lookupFlag, Set<Integer> posCoverageGlyphIds,
                                  OtfClass classDefinition) {
        super(openReader, lookupFlag);
        this.posCoverageGlyphIds = posCoverageGlyphIds;
        this.classDefinition = classDefinition;
    }

    /**
     * Updates the positioning class sets.
     *
     * @param posClassSets the positioning class sets
     */
    public void setPosClassSets(List<List<ContextualPositionRule>> posClassSets) {
        this.posClassSets = posClassSets;
    }

    @Override
    protected List<ContextualPositionRule> getSetOfRulesForStartGlyph(int startId) {
        if (posCoverageGlyphIds.contains(startId) && !openReader.isSkip(startId, lookupFlag)) {
            int gClass = classDefinition.getOtfClass(startId);
            return posClassSets.get(gClass);
        }
        return Collections.<ContextualPositionRule>emptyList();
    }

    /**
     * Represents the positioning rule format2 of an OpenType font.
     */
    public static class PosRuleFormat2 extends ContextualPositionRule {
        // inputClassIds array omits the first class in the sequence,
        // the first class is defined by corresponding index of subClassSet array
        private final int[] inputClassIds;
        private final PosLookupRecord[] posLookupRecords;
        private final OtfClass classDefinition;

        /**
         * Creates a new positioning rule format2.
         *
         * @param subTable the sub table
         * @param inputClassIds the input class ids
         * @param posLookupRecords the positioning lookup records
         */
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
