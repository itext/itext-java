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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Lookup Type 1: Single Adjustment Positioning Subtable
 */
public class GposLookupType1 extends OpenTableLookup {

    private Map<Integer, GposValueRecord> valueRecordMap = new HashMap<>();

    public GposLookupType1(OpenTypeFontTableReader openReader, int lookupFlag, int[] subTableLocations)
            throws java.io.IOException {
        super(openReader, lookupFlag, subTableLocations);
        readSubTables();
    }

    @Override
    public boolean transformOne(GlyphLine line) {
        if (line.getIdx() >= line.getEnd()) {
            return false;
        }
        if (openReader.isSkip(line.get(line.getIdx()).getCode(), lookupFlag)) {
            line.setIdx(line.getIdx()+1);
            return false;
        }
        int glyphCode = line.get(line.getIdx()).getCode();
        boolean positionApplied = false;
        GposValueRecord valueRecord = valueRecordMap.get(glyphCode);
        if (valueRecord != null) {
            Glyph newGlyph = new Glyph(line.get(line.getIdx()));
            newGlyph.setXAdvance((short)(newGlyph.getXAdvance() + valueRecord.getXAdvance()));
            newGlyph.setYAdvance((short)(newGlyph.getYAdvance() + valueRecord.getYAdvance()));
            line.set(line.getIdx(), newGlyph);
            positionApplied = true;
        }
        line.setIdx(line.getIdx()+1);
        return positionApplied;
    }

    @Override
    protected void readSubTable(int subTableLocation) throws java.io.IOException {
        openReader.rf.seek(subTableLocation);
        int subTableFormat = openReader.rf.readShort();
        int coverageOffset = openReader.rf.readUnsignedShort();
        int valueFormat = openReader.rf.readUnsignedShort();
        if (subTableFormat == 1) {
            GposValueRecord valueRecord = OtfReadCommon.readGposValueRecord(openReader, valueFormat);
            List<Integer> coverageGlyphIds = openReader.readCoverageFormat(subTableLocation + coverageOffset);
            for (Integer glyphId : coverageGlyphIds) {
                valueRecordMap.put((int) glyphId, valueRecord);
            }
        } else if (subTableFormat == 2) {
            int valueCount = openReader.rf.readUnsignedShort();
            List<GposValueRecord> valueRecords = new ArrayList<>();
            for (int i = 0; i < valueCount; i++) {
                GposValueRecord valueRecord = OtfReadCommon.readGposValueRecord(openReader, valueFormat);
                valueRecords.add(valueRecord);
            }
            List<Integer> coverageGlyphIds = openReader.readCoverageFormat(subTableLocation + coverageOffset);
            for (int i = 0; i < coverageGlyphIds.size(); i++) {
                valueRecordMap.put((int) coverageGlyphIds.get(i), valueRecords.get(i));
            }
        } else {
            throw new IllegalArgumentException("Bad subtable format identifier: " + subTableFormat);
        }
    }
}
