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
package com.itextpdf.io.font.otf.lookuptype5;

import com.itextpdf.io.font.otf.ContextualSubstRule;
import com.itextpdf.io.font.otf.ContextualTable;
import com.itextpdf.io.font.otf.OpenTypeFontTableReader;
import com.itextpdf.io.font.otf.SubstLookupRecord;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Contextual Substitution Subtable: Simple context glyph substitution
 */
public class SubTableLookup5Format1 extends ContextualTable<ContextualSubstRule> {
    private final Map<Integer, List<ContextualSubstRule>> substMap;

    /**
     * Creates a new Contextual Substitution Subtable.
     *
     * @param openReader the OpenType font reader
     * @param lookupFlag specifies processing options, e.g. whether to skip base glyphs, marks or
     *                   ligatures during glyph substitution or positioning. See
     *                   <a href="https://learn.microsoft.com/en-us/typography/opentype/spec/chapter2#lookup-table">Lookup table</a>
     * @param substMap the substitution map
     */
    public SubTableLookup5Format1(OpenTypeFontTableReader openReader, int lookupFlag,
                                  Map<Integer, List<ContextualSubstRule>> substMap) {
        super(openReader, lookupFlag);
        this.substMap = substMap;
    }

    @Override
    protected List<ContextualSubstRule> getSetOfRulesForStartGlyph(int startGlyphId) {
        if (substMap.containsKey(startGlyphId) && !openReader.isSkip(startGlyphId, lookupFlag)) {
            return substMap.get(startGlyphId);
        }
        return Collections.<ContextualSubstRule>emptyList();
    }

    /**
     * Represents the substitution rule format1 of an OpenType font.
     */
    public static class SubstRuleFormat1 extends ContextualSubstRule {
        // inputGlyphIds array omits the first glyph in the sequence,
        // the first glyph is defined by corresponding coverage glyph
        private final int[] inputGlyphIds;
        private final SubstLookupRecord[] substLookupRecords;

        /**
         * Creates a new substitution rule format1.
         *
         * @param inputGlyphIds the input glyph ids
         * @param substLookupRecords the substitution lookup records
         */
        public SubstRuleFormat1(int[] inputGlyphIds, SubstLookupRecord[] substLookupRecords) {
            this.inputGlyphIds = inputGlyphIds;
            this.substLookupRecords = substLookupRecords;
        }

        @Override
        public int getContextLength() {
            return inputGlyphIds.length + 1;
        }

        @Override
        public SubstLookupRecord[] getSubstLookupRecords() {
            return substLookupRecords;
        }

        @Override
        public boolean isGlyphMatchesInput(int glyphId, int atIdx) {
            return glyphId == inputGlyphIds[atIdx - 1];
        }
    }
}
