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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * LookupType 4: Ligature Substitution Subtable
 */
public class GsubLookupType4 extends OpenTableLookup {
    /**
     * The key is the first character. The first element in the int array is the
     * output ligature
     */
    private Map<Integer,List<int[]>> ligatures;
    
    public GsubLookupType4(OpenTypeFontTableReader openReader, int lookupFlag, int[] subTableLocations) throws java.io.IOException {
        super(openReader, lookupFlag, subTableLocations);
        ligatures = new HashMap<>();
        readSubTables();
    }
    
    @Override
    public boolean transformOne(GlyphLine line) {
        if (line.getIdx() >= line.getEnd())
            return false;
        boolean changed = false;
        Glyph g = line.get(line.getIdx());
        boolean match = false;
        if (ligatures.containsKey(g.getCode()) && !openReader.isSkip(g.getCode(), lookupFlag)) {
            GlyphIndexer gidx = new GlyphIndexer();
            gidx.setLine(line);
            List<int[]> ligs = ligatures.get(g.getCode());
            for (int[] lig : ligs) {
                match = true;
                gidx.setIdx(line.getIdx());
                for (int j = 1; j < lig.length; ++j) {
                    gidx.nextGlyph(openReader, lookupFlag);
                    if (gidx.getGlyph() == null || gidx.getGlyph().getCode() != lig[j]) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    line.substituteManyToOne(openReader, lookupFlag, lig.length - 1, lig[0]);
                    break;
                }
            }
        }
        if (match) {
            changed = true;
        }
        line.setIdx(line.getIdx()+1);
        return changed;
    }

    @Override
    protected void readSubTable(int subTableLocation) throws java.io.IOException {
        openReader.rf.seek(subTableLocation);
        // subformat - always 1
        openReader.rf.readShort();
        int coverage = openReader.rf.readUnsignedShort() + subTableLocation;
        int ligSetCount = openReader.rf.readUnsignedShort();
        int[] ligatureSet = new int[ligSetCount];
        for (int k = 0; k < ligSetCount; ++k) {
            ligatureSet[k] = openReader.rf.readUnsignedShort() + subTableLocation;
        }
        List<Integer> coverageGlyphIds = openReader.readCoverageFormat(coverage);
        for (int k = 0; k < ligSetCount; ++k) {
            openReader.rf.seek(ligatureSet[k]);
            int ligatureCount = openReader.rf.readUnsignedShort();
            int[] ligature = new int[ligatureCount];
            for (int j = 0; j < ligatureCount; ++j) {
                ligature[j] = openReader.rf.readUnsignedShort() + ligatureSet[k];
            }
            List<int[]> components = new ArrayList<>(ligatureCount);
            for (int j = 0; j < ligatureCount; ++j) {
                openReader.rf.seek(ligature[j]);
                int ligGlyph = openReader.rf.readUnsignedShort();
                int compCount = openReader.rf.readUnsignedShort();
                int[] component = new int[compCount];
                component[0] = ligGlyph;
                for (int i = 1; i < compCount; ++i) {
                    component[i] = openReader.rf.readUnsignedShort();
                }
                components.add(component);
            }
            ligatures.put(coverageGlyphIds.get(k), components);
        }
    }    
}
