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
package com.itextpdf.io.font.otf;

import com.itextpdf.io.font.otf.lookuptype5.SubTableLookup5Format1;
import com.itextpdf.io.font.otf.lookuptype5.SubTableLookup5Format2;
import com.itextpdf.io.font.otf.lookuptype5.SubTableLookup5Format3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * LookupType 5: Contextual Substitution Subtable
 */
public class GsubLookupType5 extends OpenTableLookup {

    protected List<ContextualTable<ContextualSubstRule>> subTables;

    protected GsubLookupType5(OpenTypeFontTableReader openReader, int lookupFlag, int[] subTableLocations) throws java.io.IOException {
        super(openReader, lookupFlag, subTableLocations);
        subTables = new ArrayList<>();
        readSubTables();
    }

    @Override
    public boolean transformOne(GlyphLine line) {
        boolean changed = false;
        int oldLineStart = line.getStart();
        int oldLineEnd = line.getEnd();
        int initialLineIndex = line.getIdx();

        for (ContextualTable<ContextualSubstRule> subTable : subTables) {
            ContextualSubstRule contextRule = subTable.getMatchingContextRule(line);
            if (contextRule == null) {
                continue;
            }

            int lineEndBeforeSubstitutions = line.getEnd();
            SubstLookupRecord[] substLookupRecords = contextRule.getSubstLookupRecords();
            GlyphIndexer gidx = new GlyphIndexer();
            gidx.setLine(line);
            for (SubstLookupRecord substRecord : substLookupRecords) {
                // There could be some skipped glyphs inside the context sequence, therefore currently GlyphIndexer and
                // nextGlyph method are used to get to the glyph at "substRecord.sequenceIndex" index
                gidx.setIdx(initialLineIndex);
                for (int i = 0; i < substRecord.sequenceIndex; ++i) {
                    gidx.nextGlyph(openReader, lookupFlag);
                }

                line.setIdx(gidx.getIdx());
                OpenTableLookup lookupTable = openReader.getLookupTable(substRecord.lookupListIndex);
                changed = lookupTable.transformOne(line) || changed;
            }

            line.setIdx(line.getEnd());
            line.setStart(oldLineStart);
            int lenDelta = lineEndBeforeSubstitutions - line.getEnd();
            line.setEnd(oldLineEnd - lenDelta);
            return changed;
        }

        line.setIdx(line.getIdx()+1);
        return changed;
    }

    @Override
    protected void readSubTable(int subTableLocation) throws java.io.IOException {
        openReader.rf.seek(subTableLocation);
        int substFormat = openReader.rf.readShort();
        if (substFormat == 1) {
            readSubTableFormat1(subTableLocation);
        } else if (substFormat == 2){
            readSubTableFormat2(subTableLocation);
        } else if (substFormat == 3) {
            readSubTableFormat3(subTableLocation);
        } else {
            throw new IllegalArgumentException("Bad substFormat: " + substFormat);
        }
    }

    protected void readSubTableFormat1(int subTableLocation) throws java.io.IOException {
        Map<Integer, List<ContextualSubstRule>> substMap = new HashMap<>();

        int coverageOffset = openReader.rf.readUnsignedShort();
        int subRuleSetCount = openReader.rf.readUnsignedShort();
        int[] subRuleSetOffsets = openReader.readUShortArray(subRuleSetCount, subTableLocation);

        List<Integer> coverageGlyphIds = openReader.readCoverageFormat(subTableLocation + coverageOffset);
        for (int i = 0; i < subRuleSetCount; ++i) {
            openReader.rf.seek(subRuleSetOffsets[i]);
            int subRuleCount = openReader.rf.readUnsignedShort();
            int[] subRuleOffsets = openReader.readUShortArray(subRuleCount, subRuleSetOffsets[i]);

            List<ContextualSubstRule> subRuleSet = new ArrayList<>(subRuleCount);
            for (int j = 0; j < subRuleCount; ++j) {
                openReader.rf.seek(subRuleOffsets[j]);
                int glyphCount = openReader.rf.readUnsignedShort();
                int substCount = openReader.rf.readUnsignedShort();
                int[] inputGlyphIds = openReader.readUShortArray(glyphCount-1);
                SubstLookupRecord[] substLookupRecords = openReader.readSubstLookupRecords(substCount);

                subRuleSet.add(new SubTableLookup5Format1.SubstRuleFormat1(inputGlyphIds, substLookupRecords));
            }
            substMap.put(coverageGlyphIds.get(i), subRuleSet);
        }

        subTables.add(new SubTableLookup5Format1(openReader, lookupFlag, substMap));
    }

    protected void readSubTableFormat2(int subTableLocation) throws java.io.IOException {
        int coverageOffset = openReader.rf.readUnsignedShort();
        int classDefOffset = openReader.rf.readUnsignedShort();
        int subClassSetCount = openReader.rf.readUnsignedShort();
        int[] subClassSetOffsets = openReader.readUShortArray(subClassSetCount, subTableLocation);

        Set<Integer> coverageGlyphIds = new HashSet<>(openReader.readCoverageFormat(subTableLocation + coverageOffset));
        OtfClass classDefinition = openReader.readClassDefinition(subTableLocation + classDefOffset);

        SubTableLookup5Format2 t = new SubTableLookup5Format2(openReader, lookupFlag, coverageGlyphIds, classDefinition);

        List<List<ContextualSubstRule>> subClassSets = new ArrayList<>(subClassSetCount);
        for (int i = 0; i < subClassSetCount; ++i) {
            List<ContextualSubstRule> subClassSet = null;
            if (subClassSetOffsets[i] != 0) {
                openReader.rf.seek(subClassSetOffsets[i]);
                int subClassRuleCount = openReader.rf.readUnsignedShort();
                int[] subClassRuleOffsets = openReader.readUShortArray(subClassRuleCount, subClassSetOffsets[i]);

                subClassSet = new ArrayList<>(subClassRuleCount);
                for (int j = 0; j < subClassRuleCount; ++j) {
                    ContextualSubstRule rule;
                    openReader.rf.seek(subClassRuleOffsets[j]);

                    int glyphCount = openReader.rf.readUnsignedShort();
                    int substCount = openReader.rf.readUnsignedShort();
                    int[] inputClassIds = openReader.readUShortArray(glyphCount - 1);
                    SubstLookupRecord[] substLookupRecords = openReader.readSubstLookupRecords(substCount);

                    rule = new SubTableLookup5Format2.SubstRuleFormat2(t, inputClassIds, substLookupRecords);
                    subClassSet.add(rule);
                }
            }
            subClassSets.add(subClassSet);
        }

        t.setSubClassSets(subClassSets);
        subTables.add(t);

    }

    protected void readSubTableFormat3(int subTableLocation) throws java.io.IOException {
        int glyphCount = openReader.rf.readUnsignedShort();
        int substCount = openReader.rf.readUnsignedShort();
        int[] coverageOffsets = openReader.readUShortArray(glyphCount, subTableLocation);
        SubstLookupRecord[] substLookupRecords = openReader.readSubstLookupRecords(substCount);

        List<Set<Integer>> coverages = new ArrayList<>(glyphCount);
        openReader.readCoverages(coverageOffsets, coverages);

        SubTableLookup5Format3.SubstRuleFormat3 rule = new SubTableLookup5Format3.SubstRuleFormat3(coverages, substLookupRecords);
        subTables.add(new SubTableLookup5Format3(openReader, lookupFlag, rule));
    }
}
