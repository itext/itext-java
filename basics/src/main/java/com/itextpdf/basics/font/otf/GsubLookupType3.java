package com.itextpdf.basics.font.otf;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * LookupType 3: Alternate Substitution Subtable
 * @author psoares
 */
public class GsubLookupType3 extends OpenTableLookup {

    private HashMap<Integer, int[]> substMap;

    public GsubLookupType3(OpenTypeFontTableReader openReader, int lookupFlag, int[] subTableLocations) throws IOException {
        super(openReader, lookupFlag, subTableLocations);
        substMap = new HashMap<>();
        readSubTables();
    }

    @Override
    public boolean transformOne(GlyphLine line) {
        if (line.idx >= line.end) {
            return false;
        }
        Glyph g = line.glyphs.get(line.idx);
        boolean changed = false;
        if (!openReader.isSkip(g.index, lookupFlag)) {
            int[] substCode = substMap.get(g.index);
            if (substCode != null) {
                Glyph glyph = openReader.getGlyph(substCode[0]);
                line.glyphs.set(line.idx, glyph);
                changed = true;
            }
        }
        line.idx++;
        return changed;
    }

    @Override
    protected void readSubTable(int subTableLocation) throws IOException {
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

