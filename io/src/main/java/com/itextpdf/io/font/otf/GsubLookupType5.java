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

    private static final long serialVersionUID = 1499367592878919320L;
    protected List<ContextualSubTable> subTables;

    protected GsubLookupType5(OpenTypeFontTableReader openReader, int lookupFlag, int[] subTableLocations) throws java.io.IOException {
        super(openReader, lookupFlag, subTableLocations);
        subTables = new ArrayList<>();
        readSubTables();
    }

    @Override
    public boolean transformOne(GlyphLine line) {
        boolean changed = false;
        int oldLineStart = line.start;
        int oldLineEnd = line.end;
        int initialLineIndex = line.idx;

        for (ContextualSubTable subTable : subTables) {
            ContextualSubstRule contextRule = subTable.getMatchingContextRule(line);
            if (contextRule == null) {
                continue;
            }

            int lineEndBeforeSubstitutions = line.end;
            SubstLookupRecord[] substLookupRecords = contextRule.getSubstLookupRecords();
            GlyphIndexer gidx = new GlyphIndexer();
            gidx.line = line;
            for (SubstLookupRecord substRecord : substLookupRecords) {
                // There could be some skipped glyphs inside the context sequence, therefore currently GlyphIndexer and
                // nextGlyph method are used to get to the glyph at "substRecord.sequenceIndex" index
                gidx.idx = initialLineIndex;
                for (int i = 0; i < substRecord.sequenceIndex; ++i) {
                    gidx.nextGlyph(openReader, lookupFlag);
                }

                line.idx = gidx.idx;
                OpenTableLookup lookupTable = openReader.getLookupTable(substRecord.lookupListIndex);
                changed = lookupTable.transformOne(line) || changed;
            }

            line.idx = line.end;
            line.start = oldLineStart;
            int lenDelta = lineEndBeforeSubstitutions - line.end;
            line.end = oldLineEnd - lenDelta;
            return changed;
        }

        ++line.idx;
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
