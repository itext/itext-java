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

import com.itextpdf.io.exceptions.IOException;
import com.itextpdf.io.font.otf.lookuptype8.PosTableLookup8Format1;
import com.itextpdf.io.font.otf.lookuptype8.PosTableLookup8Format1.PosRuleFormat1;
import com.itextpdf.io.font.otf.lookuptype8.PosTableLookup8Format2;
import com.itextpdf.io.font.otf.lookuptype8.PosTableLookup8Format2.PosRuleFormat2;
import com.itextpdf.io.font.otf.lookuptype8.PosTableLookup8Format3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Lookup Type 8:
 * Chaining Contextual Positioning Subtable
 */
public class GposLookupType8 extends GposLookupType7 {

    protected GposLookupType8(OpenTypeFontTableReader openReader, int lookupFlag, int[] subTableLocations)
            throws IOException, java.io.IOException {
        super(openReader, lookupFlag, subTableLocations);
        subTables = new ArrayList<>();
        readSubTables();
    }

    @Override
    protected void readSubTable(int subTableLocation) throws java.io.IOException {
        openReader.rf.seek(subTableLocation);
        int substFormat = openReader.rf.readShort();
        switch (substFormat) {
            case 1:
                readSubTableFormat1(subTableLocation);
                break;
            case 2:
                readSubTableFormat2(subTableLocation);
                break;
            case 3:
                readSubTableFormat3(subTableLocation);
                break;
            default:
                throw new IllegalArgumentException("Bad subtable format identifier: " + substFormat);
        }
    }

    @Override
    protected void readSubTableFormat2(int subTableLocation) throws java.io.IOException {
        int coverageOffset = openReader.rf.readUnsignedShort();
        int backtrackClassDefOffset = openReader.rf.readUnsignedShort();
        int inputClassDefOffset = openReader.rf.readUnsignedShort();
        int lookaheadClassDefOffset = openReader.rf.readUnsignedShort();
        int chainPosClassSetCount = openReader.rf.readUnsignedShort();
        int[] chainPosClassSetOffsets = openReader.readUShortArray(chainPosClassSetCount, subTableLocation);

        Set<Integer> coverageGlyphIds = new HashSet<>(openReader.readCoverageFormat(subTableLocation + coverageOffset));
        OtfClass backtrackClassDefinition = openReader.readClassDefinition(subTableLocation + backtrackClassDefOffset);
        OtfClass inputClassDefinition = openReader.readClassDefinition(subTableLocation + inputClassDefOffset);
        OtfClass lookaheadClassDefinition = openReader.readClassDefinition(subTableLocation + lookaheadClassDefOffset);

        PosTableLookup8Format2 t = new PosTableLookup8Format2(openReader, lookupFlag, coverageGlyphIds,
                backtrackClassDefinition, inputClassDefinition, lookaheadClassDefinition);

        for (int i = 0; i < chainPosClassSetCount; ++i) {
                List<ContextualPositionRule> posClassSet = Collections.<ContextualPositionRule>emptyList();
            if (chainPosClassSetOffsets[i] != 0) {
                openReader.rf.seek(chainPosClassSetOffsets[i]);
                int chainPosClassRuleCount = openReader.rf.readUnsignedShort();
                int[] chainPosClassRuleOffsets =
                        openReader.readUShortArray(chainPosClassRuleCount, chainPosClassSetOffsets[i]);

                posClassSet = new ArrayList<>(chainPosClassRuleCount);
                for (int j = 0; j < chainPosClassRuleCount; ++j) {
                    openReader.rf.seek(chainPosClassRuleOffsets[j]);

                    int backtrackClassCount = openReader.rf.readUnsignedShort();
                    int[] backtrackClassIds = openReader.readUShortArray(backtrackClassCount);
                    int inputClassCount = openReader.rf.readUnsignedShort();
                    int[] inputClassIds = openReader.readUShortArray(inputClassCount - 1);
                    int lookAheadClassCount = openReader.rf.readUnsignedShort();
                    int[] lookAheadClassIds = openReader.readUShortArray(lookAheadClassCount);
                    int substCount = openReader.rf.readUnsignedShort();
                    PosLookupRecord[] posLookupRecords = openReader.readPosLookupRecords(substCount);

                    PosRuleFormat2 rule = new PosRuleFormat2(t, backtrackClassIds, inputClassIds, lookAheadClassIds, posLookupRecords);
                    posClassSet.add(rule);
                }
            }
            t.addPosClassSet(posClassSet);
        }

        subTables.add(t);
    }

    private void readSubTableFormat1(int subTableLocation) throws java.io.IOException {
        Map<Integer, List<ContextualPositionRule>> posMap = new HashMap<>();

        int coverageOffset = openReader.rf.readUnsignedShort();
        int chainPosRuleSetCount = openReader.rf.readUnsignedShort();
        int[] chainPosRuleSetOffsets = openReader.readUShortArray(chainPosRuleSetCount, subTableLocation);

        List<Integer> coverageGlyphIds = openReader.readCoverageFormat(subTableLocation + coverageOffset);
        for (int i = 0; i < chainPosRuleSetCount; ++i) {
            openReader.rf.seek(chainPosRuleSetOffsets[i]);
            int chainPosRuleCount = openReader.rf.readUnsignedShort();
            int[] chainPosRuleOffsets = openReader.readUShortArray(chainPosRuleCount, chainPosRuleSetOffsets[i]);

            List<ContextualPositionRule> chainPosRuleSet = new ArrayList<>(chainPosRuleCount);
            for (int j = 0; j < chainPosRuleCount; ++j) {
                openReader.rf.seek(chainPosRuleOffsets[j]);
                int backtrackGlyphCount = openReader.rf.readUnsignedShort();
                int[] backtrackGlyphIds = openReader.readUShortArray(backtrackGlyphCount);
                int inputGlyphCount = openReader.rf.readUnsignedShort();
                int[] inputGlyphIds = openReader.readUShortArray(inputGlyphCount - 1);
                int lookAheadGlyphCount = openReader.rf.readUnsignedShort();
                int[] lookAheadGlyphIds = openReader.readUShortArray(lookAheadGlyphCount);
                int posCount = openReader.rf.readUnsignedShort();
                PosLookupRecord[] posLookupRecords = openReader.readPosLookupRecords(posCount);

                chainPosRuleSet.add(new PosRuleFormat1(backtrackGlyphIds, inputGlyphIds, lookAheadGlyphIds,
                        posLookupRecords));
            }
            posMap.put(coverageGlyphIds.get(i), chainPosRuleSet);
        }

        subTables.add(new PosTableLookup8Format1(openReader, lookupFlag, posMap));
    }

    private void readSubTableFormat3(int subTableLocation) throws java.io.IOException {
        int backtrackGlyphCount = openReader.rf.readUnsignedShort();
        int[] backtrackCoverageOffsets = openReader.readUShortArray(backtrackGlyphCount, subTableLocation);
        int inputGlyphCount = openReader.rf.readUnsignedShort();
        int[] inputCoverageOffsets = openReader.readUShortArray(inputGlyphCount, subTableLocation);
        int lookaheadGlyphCount = openReader.rf.readUnsignedShort();
        int[] lookaheadCoverageOffsets = openReader.readUShortArray(lookaheadGlyphCount, subTableLocation);
        int posCount = openReader.rf.readUnsignedShort();
        PosLookupRecord[] posLookupRecords = openReader.readPosLookupRecords(posCount);

        List<Set<Integer>> backtrackCoverages = new ArrayList<>(backtrackGlyphCount);
        openReader.readCoverages(backtrackCoverageOffsets, backtrackCoverages);

        List<Set<Integer>> inputCoverages = new ArrayList<>(inputGlyphCount);
        openReader.readCoverages(inputCoverageOffsets, inputCoverages);

        List<Set<Integer>> lookaheadCoverages = new ArrayList<>(lookaheadGlyphCount);
        openReader.readCoverages(lookaheadCoverageOffsets, lookaheadCoverages);

        PosTableLookup8Format3.PosRuleFormat3 rule = new PosTableLookup8Format3.PosRuleFormat3(backtrackCoverages,
                inputCoverages, lookaheadCoverages, posLookupRecords);
        subTables.add(new PosTableLookup8Format3(openReader, lookupFlag, rule));
    }

}
