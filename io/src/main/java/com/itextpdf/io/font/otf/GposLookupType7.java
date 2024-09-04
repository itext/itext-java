/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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

import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.io.font.otf.lookuptype7.PosTableLookup7Format2;
import com.itextpdf.commons.utils.MessageFormatUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Lookup Type 7:
 * Contextual Positioning Subtables
 */
public class GposLookupType7 extends OpenTableLookup {

    private static final Logger LOGGER = LoggerFactory.getLogger(GposLookupType7.class);

    protected List<ContextualTable<ContextualPositionRule>> subTables;

    public GposLookupType7(OpenTypeFontTableReader openReader, int lookupFlag, int[] subTableLocations)
            throws java.io.IOException {
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

        for (ContextualTable<ContextualPositionRule> subTable : subTables) {
            ContextualPositionRule contextRule = subTable.getMatchingContextRule(line);
            if (contextRule == null) {
                continue;
            }

            int lineEndBeforeTransformations = line.getEnd();
            PosLookupRecord[] posLookupRecords = contextRule.getPosLookupRecords();
            GlyphIndexer gidx = new GlyphIndexer();
            gidx.setLine(line);
            for (PosLookupRecord posRecord : posLookupRecords) {
                // There could be some skipped glyphs inside the context sequence, therefore currently GlyphIndexer and
                // nextGlyph method are used to get to the glyph at "substRecord.sequenceIndex" index
                gidx.setIdx(initialLineIndex);
                for (int i = 0; i < posRecord.sequenceIndex; ++i) {
                    gidx.nextGlyph(openReader, lookupFlag);
                }

                line.setIdx(gidx.getIdx());
                OpenTableLookup lookupTable = openReader.getLookupTable(posRecord.lookupListIndex);
                changed = lookupTable.transformOne(line) || changed;
            }

            line.setIdx(line.getEnd());
            line.setStart(oldLineStart);
            int lenDelta = lineEndBeforeTransformations - line.getEnd();
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
        switch (substFormat) {
            case 2:
                readSubTableFormat2(subTableLocation);
                break;
            case 1:
            case 3:
                LOGGER.warn(MessageFormatUtil.format(IoLogMessageConstant.GPOS_LOOKUP_SUBTABLE_FORMAT_NOT_SUPPORTED,
                        substFormat, 7));
                break;
            default:
                throw new IllegalArgumentException("Bad subtable format identifier: " + substFormat);
        }
    }

    protected void readSubTableFormat2(int subTableLocation) throws java.io.IOException {
        int coverageOffset = openReader.rf.readUnsignedShort();
        int classDefOffset = openReader.rf.readUnsignedShort();
        int posClassSetCount = openReader.rf.readUnsignedShort();
        int[] posClassSetOffsets = openReader.readUShortArray(posClassSetCount, subTableLocation);

        Set<Integer> coverageGlyphIds = new HashSet<>(openReader.readCoverageFormat(subTableLocation + coverageOffset));
        OtfClass classDefinition = openReader.readClassDefinition(subTableLocation + classDefOffset);

        PosTableLookup7Format2 t = new PosTableLookup7Format2(openReader, lookupFlag,
                coverageGlyphIds, classDefinition);

        List<List<ContextualPositionRule>> subClassSets = new ArrayList<>(posClassSetCount);
        for (int i = 0; i < posClassSetCount; ++i) {
            List<ContextualPositionRule> subClassSet = null;
            if (posClassSetOffsets[i] != 0) {
                openReader.rf.seek(posClassSetOffsets[i]);
                int posClassRuleCount = openReader.rf.readUnsignedShort();
                int[] posClassRuleOffsets = openReader.readUShortArray(posClassRuleCount, posClassSetOffsets[i]);

                subClassSet = new ArrayList<>(posClassRuleCount);
                for (int j = 0; j < posClassRuleCount; ++j) {
                    ContextualPositionRule rule;
                    openReader.rf.seek(posClassRuleOffsets[j]);

                    int glyphCount = openReader.rf.readUnsignedShort();
                    int posCount = openReader.rf.readUnsignedShort();
                    int[] inputClassIds = openReader.readUShortArray(glyphCount - 1);
                    PosLookupRecord[] posLookupRecords = openReader.readPosLookupRecords(posCount);

                    rule = new PosTableLookup7Format2.PosRuleFormat2(t, inputClassIds, posLookupRecords);
                    subClassSet.add(rule);
                }
            }
            subClassSets.add(subClassSet);
        }

        t.setPosClassSets(subClassSets);
        subTables.add(t);
    }

}
