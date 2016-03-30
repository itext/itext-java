package com.itextpdf.io.font.otf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * LookupType 4: Ligature Substitution Subtable
 * @author psoares
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
        //TODO >
        if (line.idx >= line.end)
            return false;
        boolean changed = false;
        Glyph g = line.get(line.idx);
        boolean match = false;
        if (ligatures.containsKey(g.getCode()) && !openReader.isSkip(g.getCode(), lookupFlag)) {
            GlyphIndexer gidx = new GlyphIndexer();
            gidx.line = line;
            List<int[]> ligs = ligatures.get(g.getCode());
            for (int[] lig : ligs) {
                match = true;
                gidx.idx = line.idx;
                for (int j = 1; j < lig.length; ++j) {
                    gidx.nextGlyph(openReader, lookupFlag);
                    if (gidx.glyph == null || gidx.glyph.getCode() != lig[j]) {
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
        line.idx++;
        return changed;
    }

    @Override
    protected void readSubTable(int subTableLocation) throws java.io.IOException {
        openReader.rf.seek(subTableLocation);
        openReader.rf.readShort(); //subformat - always 1
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
