package com.itextpdf.basics.font.otf;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * LookupType 1: Single Substitution Subtable
 * @author psoares
 */
public class GsubLookupFormat1 extends OpenTableLookup {

    private HashMap<Integer,Integer> substMap;

    public GsubLookupFormat1(OpenTypeFontTableReader openReader, int lookupFlag, int[] subTableLocations) throws IOException {
        super(openReader, lookupFlag, subTableLocations);
        substMap = new HashMap<>();
        readSubTables();
    }

    @Override
    public boolean transformOne(GlyphLine line) {
        if (line.idx >= line.end)
            return false;
        Glyph g = line.glyphs.get(line.idx);
        boolean changed = false;
        if (!openReader.IsSkip(g.index, lookupFlag)) {
            Integer substCode = substMap.get(g.index);
            if (substCode != null) {
                Integer c = openReader.getGlyphToCharacter(substCode);
                Glyph glyph = new Glyph(substCode, openReader.getGlyphWidth(substCode), c, c == null ? g.chars : String.valueOf((char) (int) c), g.IsMark);
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
        if (substFormat == 1) {
            int coverage = openReader.rf.readUnsignedShort();
            int deltaGlyphID = openReader.rf.readShort();
            List<Integer> coverageGlyphIds = openReader.readCoverageFormat(subTableLocation + coverage);
            for (int coverageGlyphId : coverageGlyphIds) {
                int substituteGlyphId = coverageGlyphId + deltaGlyphID;
                substMap.put(coverageGlyphId, substituteGlyphId);
            }
        } else if (substFormat == 2) {
            int coverage = openReader.rf.readUnsignedShort();
            int glyphCount = openReader.rf.readUnsignedShort();
            int[] substitute = new int[glyphCount];
            for (int k = 0; k < glyphCount; ++k) {
                substitute[k] = openReader.rf.readUnsignedShort();
            }
            List<Integer> coverageGlyphIds = openReader.readCoverageFormat(subTableLocation + coverage);
            for (int k = 0; k < glyphCount; ++k) {
                substMap.put(coverageGlyphIds.get(k), substitute[k]);
            }
        } else {
            throw new IllegalArgumentException("Bad substFormat: " + substFormat);
        }
    }
}
