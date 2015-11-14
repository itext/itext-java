package com.itextpdf.basics.font.otf;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * LookupType 1: Single Substitution Subtable
 * @author psoares
 */
public class GsubLookupFormat3 extends OpenTableLookup {

    private HashMap<Integer, int[]> substMap;

    public GsubLookupFormat3(OpenTypeFontTableReader openReader, int lookupFlag, int[] subTableLocations) throws IOException {
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
        if (!openReader.IsSkip(g.index, lookupFlag)) {
            int[] substCode = substMap.get(g.index);
            if (substCode != null) {
                Integer c = openReader.getGlyphToCharacter(substCode[0]);
                Glyph glyph = new Glyph(substCode[0], openReader.getGlyphWidth(substCode[0]),
                        c, c == null ? g.chars : String.valueOf((char) (int) c), g.IsMark);
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

