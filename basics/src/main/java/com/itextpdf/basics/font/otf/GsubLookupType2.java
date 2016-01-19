package com.itextpdf.basics.font.otf;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * LookupType 2: Multiple Substitution Subtable
 */
public class GsubLookupType2 extends OpenTableLookup {

    private Map<Integer, int[]> substMap;

    public GsubLookupType2(OpenTypeFontTableReader openReader, int lookupFlag, int[] subTableLocations) throws IOException {
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
        if (!openReader.isSkip(g.getCode(), lookupFlag)) {
            int[] substSequence = substMap.get(g.getCode());
            if (substSequence != null) {
                line.substituteOneToMany(openReader, substSequence);
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
