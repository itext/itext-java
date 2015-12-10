package com.itextpdf.basics.font.otf;

import com.itextpdf.basics.font.otf.lookuptype5.SubTableLookup5Format1;
import com.itextpdf.basics.font.otf.lookuptype5.SubTableLookup5Format2;
import com.itextpdf.basics.font.otf.lookuptype5.SubTableLookup5Format3;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * LookupType 5: Contextual Substitution Subtable
 */
public class GsubLookupType5 extends OpenTableLookup {

    protected List<ContextualSubTable> subTables;

    protected GsubLookupType5(OpenTypeFontTableReader openReader, int lookupFlag, int[] subTableLocations) throws IOException {
        super(openReader, lookupFlag, subTableLocations);
        subTables = new ArrayList<>();
        readSubTables();
    }

    @Override
    public boolean transformOne(GlyphLine line) {
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
                lookupTable.transformOne(line);
            }

            line.idx = line.end;
            line.start = oldLineStart;
            int lenDelta = lineEndBeforeSubstitutions - line.end;
            line.end = oldLineEnd - lenDelta;
            return true;
        }

        ++line.idx;
        return false;
    }

    @Override
    protected void readSubTable(int subTableLocation) throws IOException {
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

    protected void readSubTableFormat1(int subTableLocation) throws IOException {
        HashMap<Integer, List<ContextualSubstRule>> substMap = new HashMap<>();

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

    protected void readSubTableFormat2(int subTableLocation) throws IOException {
        int coverageOffset = openReader.rf.readUnsignedShort();
        int classDefOffset = openReader.rf.readUnsignedShort();
        int subClassSetCount = openReader.rf.readUnsignedShort();
        int[] subClassSetOffsets = openReader.readUShortArray(subClassSetCount, subTableLocation);

        HashSet<Integer> coverageGlyphIds = new HashSet<>(openReader.readCoverageFormat(subTableLocation + coverageOffset));
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

                    rule = t.new SubstRuleFormat2(inputClassIds, substLookupRecords);
                    subClassSet.add(rule);
                }
            }
            subClassSets.add(subClassSet);
        }

        t.setSubClassSets(subClassSets);
        subTables.add(t);

    }

    protected void readSubTableFormat3(int subTableLocation) throws IOException {
        int glyphCount = openReader.rf.readUnsignedShort();
        int substCount = openReader.rf.readUnsignedShort();
        int[] coverageOffsets = openReader.readUShortArray(glyphCount, subTableLocation);
        SubstLookupRecord[] substLookupRecords = openReader.readSubstLookupRecords(substCount);

        List<HashSet<Integer>> coverages = new ArrayList<>(glyphCount);
        openReader.readCoverages(coverageOffsets, coverages);

        SubTableLookup5Format3.SubstRuleFormat3 rule = new SubTableLookup5Format3.SubstRuleFormat3(coverages, substLookupRecords);
        subTables.add(new SubTableLookup5Format3(openReader, lookupFlag, rule));
    }

}
