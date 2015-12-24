package com.itextpdf.basics.font.otf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Lookup Type 5:
 * MarkToLigature Attachment Positioning Subtable
 */
public class GposLookupType5 extends OpenTableLookup {

    private final List<MarkToLigature> marksligatures;

    public GposLookupType5(OpenTypeFontTableReader openReader, int lookupFlag, int[] subTableLocations) throws IOException {
        super(openReader, lookupFlag, subTableLocations);
        marksligatures = new ArrayList<>();
        readSubTables();
    }

    @Override
    public boolean transformOne(GlyphLine line) {
        // TODO it seems that for complex cases (symbol1, symbol2, mark, symbol3) and (symbol1, symbol2, symbol3) compose a ligature,
        // mark should be placed in the corresponding anchor of that ligature (second component's anchor).
        // But for now we do not store all the substitution info and therefore not able to follow that logic.
        // Place the mark symbol in the first available place for now.
        if (line.idx >= line.end)
            return false;
        if (openReader.isSkip(line.glyphs.get(line.idx).index, lookupFlag)) {
            line.idx++;
            return false;
        }

        boolean changed = false;
        GlyphIndexer gi = null;
        for (MarkToLigature mb : marksligatures) {
            OtfMarkRecord omr = mb.marks.get(line.glyphs.get(line.idx).index);
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
                    // not mark => ligature glyph
                    if (!mb.marks.containsKey(gi.glyph.index))
                        break;
                }
                if (gi.glyph == null)
                    break;
            }
            List<GposAnchor[]> gpas = mb.ligatures.get(gi.glyph.index);
            if (gpas == null)
                continue;
            int markClass = omr.markClass;
            for (int component = 0; component < gpas.size(); component++) {
                if (gpas.get(component)[markClass] != null) {
                    GposAnchor baseAnchor = gpas.get(component)[markClass];
                    GposAnchor markAnchor = omr.anchor;
                    // TODO
//            line.glyphs.add(line.idx, new Glyph(line.glyphs.get(line.idx),
//                    markAnchor.XCoordinate - baseAnchor.XCoordinate,
//                    markAnchor.YCoordinate - baseAnchor.YCoordinate,
//                    0, 0, gi.idx - line.idx));
                    changed = true;
                    break;
                }
            }

            break;
        }

        line.idx++;
        return changed;
    }

    @Override
    protected void readSubTable(int subTableLocation) throws IOException {
        openReader.rf.seek(subTableLocation);

        openReader.rf.readUnsignedShort(); //skip format, always 1
        int markCoverageLocation = openReader.rf.readUnsignedShort() + subTableLocation;
        int ligatureCoverageLocation = openReader.rf.readUnsignedShort() + subTableLocation;
        int classCount = openReader.rf.readUnsignedShort();
        int markArrayLocation = openReader.rf.readUnsignedShort() + subTableLocation;
        int ligatureArrayLocation = openReader.rf.readUnsignedShort() + subTableLocation;
        List<Integer> markCoverage = openReader.readCoverageFormat(markCoverageLocation);
        List<Integer> ligatureCoverage = openReader.readCoverageFormat(ligatureCoverageLocation);
        List<OtfMarkRecord> markRecords = OtfReadCommon.readMarkArray(openReader.rf, markArrayLocation);
        MarkToLigature markToLigature = new MarkToLigature();
        for (int k = 0; k < markCoverage.size(); ++k) {
            markToLigature.marks.put(markCoverage.get(k), markRecords.get(k));
        }
        List<List<GposAnchor[]>> ligatureArray = OtfReadCommon.readLigatureArray(openReader.rf, classCount, ligatureArrayLocation);
        for (int k = 0; k < ligatureCoverage.size(); ++k) {
            markToLigature.ligatures.put(ligatureCoverage.get(k), ligatureArray.get(k));
        }
        marksligatures.add(markToLigature);
    }


    public static class MarkToLigature {
        public final Map<Integer,OtfMarkRecord> marks = new HashMap<>();
        public final Map<Integer,List<GposAnchor[]>> ligatures = new HashMap<>();
    }

}
