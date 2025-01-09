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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * LookupType 3: Alternate Substitution Subtable
 */
public class GsubLookupType3 extends OpenTableLookup {

    private Map<Integer, int[]> substMap;

    public GsubLookupType3(OpenTypeFontTableReader openReader, int lookupFlag, int[] subTableLocations) throws java.io.IOException {
        super(openReader, lookupFlag, subTableLocations);
        substMap = new HashMap<>();
        readSubTables();
    }

    @Override
    public boolean transformOne(GlyphLine line) {
        if (line.getIdx() >= line.getEnd()) {
            return false;
        }
        Glyph g = line.get(line.getIdx());
        boolean changed = false;
        if (!openReader.isSkip(g.getCode(), lookupFlag)) {
            int[] substCode = substMap.get(g.getCode());

            // there is no need to substitute a symbol with itself
            if (substCode != null && substCode[0] != g.getCode()) {
                line.substituteOneToOne(openReader, substCode[0]);
                changed = true;
            }
        }
        line.setIdx(line.getIdx()+1);
        return changed;
    }

    @Override
    protected void readSubTable(int subTableLocation) throws java.io.IOException {
        openReader.rf.seek(subTableLocation);
        int substFormat = openReader.rf.readShort();
        assert substFormat == 1;
        int coverage = openReader.rf.readUnsignedShort();
        int alternateSetCount = openReader.rf.readUnsignedShort();
        int[][] substitute = new int[alternateSetCount][];
        int[] alternateLocations = openReader.readUShortArray(alternateSetCount, subTableLocation);
        for (int k = 0; k < alternateSetCount; k++) {
            openReader.rf.seek(alternateLocations[k]);
            int glyphCount = openReader.rf.readUnsignedShort();
            substitute[k] = openReader.readUShortArray(glyphCount);
        }
        List<Integer> coverageGlyphIds = openReader.readCoverageFormat(subTableLocation + coverage);
        for (int k = 0; k < alternateSetCount; ++k) {
            substMap.put(coverageGlyphIds.get(k), substitute[k]);
        }
    }

    @Override
    public boolean hasSubstitution(int index) {
        return substMap.containsKey(index);
    }
}

