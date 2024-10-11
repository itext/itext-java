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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Lookup Type 6:
 * MarkToMark Attachment Positioning Subtable
 */
public class GposLookupType6 extends OpenTableLookup {

    private final List<MarkToBaseMark> marksbases;

    public GposLookupType6(OpenTypeFontTableReader openReader, int lookupFlag, int[] subTableLocations) throws java.io.IOException {
        super(openReader, lookupFlag, subTableLocations);
        marksbases = new ArrayList<>();
        readSubTables();
    }

    @Override
    public boolean transformOne(GlyphLine line) {
        if (line.getIdx() >= line.getEnd())
            return false;
        if (openReader.isSkip(line.get(line.getIdx()).getCode(), lookupFlag)) {
            line.setIdx(line.getIdx()+1);
            return false;
        }

        boolean changed = false;
        GlyphIndexer gi = null;
        for (MarkToBaseMark mb : marksbases) {
            OtfMarkRecord omr = mb.marks.get(line.get(line.getIdx()).getCode());
            if (omr == null)
                continue;
            if (gi == null) {
                gi = new GlyphIndexer();
                gi.setIdx(line.getIdx());
                gi.setLine(line);
                while (true) {
                    int prev = gi.getIdx();
                    // avoid attaching this mark glyph to another very distant mark glyph
                    boolean foundBaseGlyph = false;
                    gi.previousGlyph(openReader, lookupFlag);
                    if (gi.getIdx() != -1) {
                        for (int i = gi.getIdx(); i < prev; i++) {
                            if (openReader.getGlyphClass(line.get(i).getCode()) == OtfClass.GLYPH_BASE) {
                                foundBaseGlyph = true;
                                break;
                            }
                        }
                    }
                    if (foundBaseGlyph) {
                        gi.setGlyph(null);
                        break;
                    }
                    if (gi.getGlyph() == null)
                        break;
                    if (mb.baseMarks.containsKey(gi.getGlyph().getCode()))
                        break;
                }
                if (gi.getGlyph() == null)
                    break;
            }
            GposAnchor[] gpas = mb.baseMarks.get(gi.getGlyph().getCode());
            if (gpas == null)
                continue;
            int markClass = omr.getMarkClass();
            GposAnchor baseAnchor = gpas[markClass];
            GposAnchor markAnchor = omr.getAnchor();
            line.set(line.getIdx(), new Glyph(line.get(line.getIdx()),
                    -markAnchor.getXCoordinate() + baseAnchor.getXCoordinate(),
                    -markAnchor.getYCoordinate() + baseAnchor.getYCoordinate(),
                    0, 0, gi.getIdx() - line.getIdx()));
            changed = true;
            break;
        }
        line.setIdx(line.getIdx()+1);
        return changed;
    }

    @Override
    protected void readSubTable(int subTableLocation) throws java.io.IOException {
        openReader.rf.seek(subTableLocation);

        // skip format, always 1
        openReader.rf.readUnsignedShort();
        int markCoverageLocation = openReader.rf.readUnsignedShort() + subTableLocation;
        int baseCoverageLocation = openReader.rf.readUnsignedShort() + subTableLocation;
        int classCount = openReader.rf.readUnsignedShort();
        int markArrayLocation = openReader.rf.readUnsignedShort() + subTableLocation;
        int baseArrayLocation = openReader.rf.readUnsignedShort() + subTableLocation;
        List<Integer> markCoverage = openReader.readCoverageFormat(markCoverageLocation);
        List<Integer> baseCoverage = openReader.readCoverageFormat(baseCoverageLocation);
        List<OtfMarkRecord> markRecords = OtfReadCommon.readMarkArray(openReader, markArrayLocation);
        MarkToBaseMark markToBaseMark = new MarkToBaseMark();
        for (int k = 0; k < markCoverage.size(); ++k) {
            markToBaseMark.marks.put(markCoverage.get(k), markRecords.get(k));
        }
        List<GposAnchor[]> baseArray = OtfReadCommon.readBaseArray(openReader, classCount, baseArrayLocation);
        for (int k = 0; k < baseCoverage.size(); ++k) {
            markToBaseMark.baseMarks.put(baseCoverage.get(k), baseArray.get(k));
        }
        marksbases.add(markToBaseMark);
    }

    private static class MarkToBaseMark {
        public final Map<Integer, OtfMarkRecord> marks = new HashMap<>();
        public final Map<Integer, GposAnchor[]> baseMarks = new HashMap<>();
    }

}
