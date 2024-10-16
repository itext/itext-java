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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * LookupType 2: Multiple Substitution Subtable
 */
public class GsubLookupType2 extends OpenTableLookup {

    private Map<Integer, int[]> substMap;

    public GsubLookupType2(OpenTypeFontTableReader openReader, int lookupFlag, int[] subTableLocations) throws java.io.IOException {
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
            int[] substSequence = substMap.get(g.getCode());
            if (substSequence != null) {
                // The use of multiple substitution for deletion of an input glyph is prohibited. GlyphCount should always be greater than 0.
                if (substSequence.length > 0) {
                    line.substituteOneToMany(openReader, substSequence);
                    changed = true;
                }
            }
        }
        line.setIdx(line.getIdx()+1);
        return changed;
    }

    @Override
    protected void readSubTable(int subTableLocation) throws java.io.IOException {
        openReader.rf.seek(subTableLocation);
        int substFormat = openReader.rf.readUnsignedShort();
        if (substFormat == 1) {
            int coverage = openReader.rf.readUnsignedShort();
            int sequenceCount = openReader.rf.readUnsignedShort();
            int[] sequenceLocations = openReader.readUShortArray(sequenceCount, subTableLocation);

            List<Integer> coverageGlyphIds = openReader.readCoverageFormat(subTableLocation + coverage);
            for (int i = 0; i < sequenceCount; ++i) {
                openReader.rf.seek(sequenceLocations[i]);
                int glyphCount = openReader.rf.readUnsignedShort();
                substMap.put(coverageGlyphIds.get(i), openReader.readUShortArray(glyphCount));
            }
        } else {
            throw new IllegalArgumentException("Bad substFormat: " + substFormat);
        }
    }

    @Override
    public boolean hasSubstitution(int index) {
        return substMap.containsKey(index);
    }
}
