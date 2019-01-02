/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.io.font.otf;

import com.itextpdf.io.font.otf.lookuptype6.SubTableLookup6Format1;
import com.itextpdf.io.font.otf.lookuptype6.SubTableLookup6Format2;
import com.itextpdf.io.font.otf.lookuptype6.SubTableLookup6Format3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * LookupType 6: Chaining Contextual Substitution Subtable
 */
public class GsubLookupType6 extends GsubLookupType5 {
    private static final long serialVersionUID = 6205375104387477124L;

    protected GsubLookupType6(OpenTypeFontTableReader openReader, int lookupFlag, int[] subTableLocations) throws java.io.IOException {
        super(openReader, lookupFlag, subTableLocations);
    }

    @Override
    protected void readSubTableFormat1(int subTableLocation) throws java.io.IOException {
        Map<Integer, List<ContextualSubstRule>> substMap = new HashMap<>();

        int coverageOffset = openReader.rf.readUnsignedShort();
        int chainSubRuleSetCount = openReader.rf.readUnsignedShort();
        int[] chainSubRuleSetOffsets = openReader.readUShortArray(chainSubRuleSetCount, subTableLocation);

        List<Integer> coverageGlyphIds = openReader.readCoverageFormat(subTableLocation + coverageOffset);
        for (int i = 0; i < chainSubRuleSetCount; ++i) {
            openReader.rf.seek(chainSubRuleSetOffsets[i]);
            int chainSubRuleCount = openReader.rf.readUnsignedShort();
            int[] chainSubRuleOffsets = openReader.readUShortArray(chainSubRuleCount, chainSubRuleSetOffsets[i]);

            List<ContextualSubstRule> chainSubRuleSet = new ArrayList<>(chainSubRuleCount);
            for (int j = 0; j < chainSubRuleCount; ++j) {
                openReader.rf.seek(chainSubRuleOffsets[j]);
                int backtrackGlyphCount = openReader.rf.readUnsignedShort();
                int[] backtrackGlyphIds = openReader.readUShortArray(backtrackGlyphCount);
                int inputGlyphCount = openReader.rf.readUnsignedShort();
                int[] inputGlyphIds = openReader.readUShortArray(inputGlyphCount - 1);
                int lookAheadGlyphCount = openReader.rf.readUnsignedShort();
                int[] lookAheadGlyphIds = openReader.readUShortArray(lookAheadGlyphCount);
                int substCount = openReader.rf.readUnsignedShort();
                SubstLookupRecord[] substLookupRecords = openReader.readSubstLookupRecords(substCount);

                chainSubRuleSet.add(new SubTableLookup6Format1.SubstRuleFormat1(backtrackGlyphIds, inputGlyphIds, lookAheadGlyphIds, substLookupRecords));
            }
            substMap.put(coverageGlyphIds.get(i), chainSubRuleSet);
        }

        subTables.add(new SubTableLookup6Format1(openReader, lookupFlag, substMap));
    }

    @Override
    protected void readSubTableFormat2(int subTableLocation) throws java.io.IOException {
        int coverageOffset = openReader.rf.readUnsignedShort();
        int backtrackClassDefOffset = openReader.rf.readUnsignedShort();
        int inputClassDefOffset = openReader.rf.readUnsignedShort();
        int lookaheadClassDefOffset = openReader.rf.readUnsignedShort();
        int chainSubClassSetCount = openReader.rf.readUnsignedShort();
        int[] chainSubClassSetOffsets = openReader.readUShortArray(chainSubClassSetCount, subTableLocation);

        Set<Integer> coverageGlyphIds = new HashSet<>(openReader.readCoverageFormat(subTableLocation + coverageOffset));
        OtfClass backtrackClassDefinition = openReader.readClassDefinition(subTableLocation + backtrackClassDefOffset);
        OtfClass inputClassDefinition = openReader.readClassDefinition(subTableLocation + inputClassDefOffset);
        OtfClass lookaheadClassDefinition = openReader.readClassDefinition(subTableLocation + lookaheadClassDefOffset);

        SubTableLookup6Format2 t = new SubTableLookup6Format2(openReader, lookupFlag, coverageGlyphIds,
                backtrackClassDefinition, inputClassDefinition, lookaheadClassDefinition);

        List<List<ContextualSubstRule>> subClassSets = new ArrayList<>(chainSubClassSetCount);
        for (int i = 0; i < chainSubClassSetCount; ++i) {
            List<ContextualSubstRule> subClassSet = null;
            if (chainSubClassSetOffsets[i] != 0) {
                openReader.rf.seek(chainSubClassSetOffsets[i]);
                int chainSubClassRuleCount = openReader.rf.readUnsignedShort();
                int[] chainSubClassRuleOffsets = openReader.readUShortArray(chainSubClassRuleCount, chainSubClassSetOffsets[i]);

                subClassSet = new ArrayList<>(chainSubClassRuleCount);
                for (int j = 0; j < chainSubClassRuleCount; ++j) {
                    SubTableLookup6Format2.SubstRuleFormat2 rule;
                    openReader.rf.seek(chainSubClassRuleOffsets[j]);

                    int backtrackClassCount = openReader.rf.readUnsignedShort();
                    int[] backtrackClassIds = openReader.readUShortArray(backtrackClassCount);
                    int inputClassCount = openReader.rf.readUnsignedShort();
                    int[] inputClassIds = openReader.readUShortArray(inputClassCount - 1);
                    int lookAheadClassCount = openReader.rf.readUnsignedShort();
                    int[] lookAheadClassIds = openReader.readUShortArray(lookAheadClassCount);
                    int substCount = openReader.rf.readUnsignedShort();
                    SubstLookupRecord[] substLookupRecords = openReader.readSubstLookupRecords(substCount);

                    rule = new SubTableLookup6Format2.SubstRuleFormat2(t, backtrackClassIds, inputClassIds, lookAheadClassIds, substLookupRecords);
                    subClassSet.add(rule);
                }
            }
            subClassSets.add(subClassSet);
        }

        t.setSubClassSets(subClassSets);
        subTables.add(t);
    }

    @Override
    protected void readSubTableFormat3(int subTableLocation) throws java.io.IOException {
        int backtrackGlyphCount = openReader.rf.readUnsignedShort();
        int[] backtrackCoverageOffsets = openReader.readUShortArray(backtrackGlyphCount, subTableLocation);
        int inputGlyphCount = openReader.rf.readUnsignedShort();
        int[] inputCoverageOffsets = openReader.readUShortArray(inputGlyphCount, subTableLocation);
        int lookaheadGlyphCount = openReader.rf.readUnsignedShort();
        int[] lookaheadCoverageOffsets = openReader.readUShortArray(lookaheadGlyphCount, subTableLocation);
        int substCount = openReader.rf.readUnsignedShort();
        SubstLookupRecord[] substLookupRecords = openReader.readSubstLookupRecords(substCount);

        List<Set<Integer>> backtrackCoverages = new ArrayList<>(backtrackGlyphCount);
        openReader.readCoverages(backtrackCoverageOffsets, backtrackCoverages);

        List<Set<Integer>> inputCoverages = new ArrayList<>(inputGlyphCount);
        openReader.readCoverages(inputCoverageOffsets, inputCoverages);

        List<Set<Integer>> lookaheadCoverages = new ArrayList<>(lookaheadGlyphCount);
        openReader.readCoverages(lookaheadCoverageOffsets, lookaheadCoverages);

        SubTableLookup6Format3.SubstRuleFormat3 rule =
                new SubTableLookup6Format3.SubstRuleFormat3(backtrackCoverages, inputCoverages, lookaheadCoverages, substLookupRecords);
        subTables.add(new SubTableLookup6Format3(openReader, lookupFlag, rule));
    }
}
