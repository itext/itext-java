package com.itextpdf.io.font.otf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Lookup Type 6:
 * MarkToMark Attachment Positioning Subtable
 */
public class GposLookupType6 extends OpenTableLookup {

    private final List<MarkToBase> marksbases;

    public GposLookupType6(OpenTypeFontTableReader openReader, int lookupFlag, int[] subTableLocations) throws java.io.IOException {
        super(openReader, lookupFlag, subTableLocations);
        marksbases = new ArrayList<>();
        readSubTables();
    }

    @Override
    public boolean transformOne(GlyphLine line) {
        if (line.idx >= line.end)
            return false;
        if (openReader.isSkip(line.get(line.idx).getCode(), lookupFlag)) {
            line.idx++;
            return false;
        }

        boolean changed = false;
        GlyphIndexer gi = null;
        for (MarkToBase mb : marksbases) {
            OtfMarkRecord omr = mb.marks.get(line.get(line.idx).getCode());
            if (omr == null)
                continue;
            if (gi == null) {
                gi = new GlyphIndexer();
                gi.idx = line.idx;
                gi.line = line;
                while (true) {
                    gi.previousGlyph(openReader, lookupFlag);
                    if (gi.glyph == null)
                        break;
                    if (mb.marks.containsKey(gi.glyph.getCode()))
                        break;
                }
                if (gi.glyph == null)
                    break;
            }
            GposAnchor[] gpas = mb.bases.get(gi.glyph.getCode());
            if (gpas == null)
                continue;
            int markClass = omr.markClass;
            GposAnchor baseAnchor = gpas[markClass];
            GposAnchor markAnchor = omr.anchor;
            line.set(line.idx, new Glyph(line.get(line.idx),
                    -markAnchor.XCoordinate + baseAnchor.XCoordinate,
                    -markAnchor.YCoordinate + baseAnchor.YCoordinate,
                    0, 0, gi.idx - line.idx));
            changed = true;
            break;
        }
        line.idx++;
        return changed;
    }

    @Override
    protected void readSubTable(int subTableLocation) throws java.io.IOException {
        openReader.rf.seek(subTableLocation);

        openReader.rf.readUnsignedShort(); //skip format, always 1
        int markCoverageLocation = openReader.rf.readUnsignedShort() + subTableLocation;
        int baseCoverageLocation = openReader.rf.readUnsignedShort() + subTableLocation;
        int classCount = openReader.rf.readUnsignedShort();
        int markArrayLocation = openReader.rf.readUnsignedShort() + subTableLocation;
        int baseArrayLocation = openReader.rf.readUnsignedShort() + subTableLocation;
        List<Integer> markCoverage = openReader.readCoverageFormat(markCoverageLocation);
        List<Integer> baseCoverage = openReader.readCoverageFormat(baseCoverageLocation);
        List<OtfMarkRecord> markRecords = OtfReadCommon.readMarkArray(openReader, markArrayLocation);
        MarkToBase markToBase = new MarkToBase();
        for (int k = 0; k < markCoverage.size(); ++k) {
            markToBase.marks.put(markCoverage.get(k), markRecords.get(k));
        }
        List<GposAnchor[]> baseArray = OtfReadCommon.readBaseArray(openReader, classCount, baseArrayLocation);
        for (int k = 0; k < baseCoverage.size(); ++k) {
            markToBase.bases.put(baseCoverage.get(k), baseArray.get(k));
        }
        marksbases.add(markToBase);
    }

    private static class MarkToBase {
        public final Map<Integer, OtfMarkRecord> marks = new HashMap<>();
        public final Map<Integer, GposAnchor[]> bases = new HashMap<>();
    }

}
