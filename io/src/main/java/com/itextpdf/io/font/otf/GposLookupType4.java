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
 * Lookup Type 4:
 * MarkToBase Attachment Positioning Subtable
 */
public class GposLookupType4 extends OpenTableLookup {

    private final List<MarkToBase> marksbases;

    public GposLookupType4(OpenTypeFontTableReader openReader, int lookupFlag, int[] subTableLocations) throws java.io.IOException {
        super(openReader, lookupFlag, subTableLocations);
        marksbases = new ArrayList<>();
        readSubTables();
    }

    @Override
    public boolean transformOne(GlyphLine line) {
        if (line.getIdx() >= line.getEnd()) {
            return false;
        }
        if (openReader.isSkip(line.get(line.getIdx()).getCode(), lookupFlag)) {
            line.setIdx(line.getIdx()+1);
            return false;
        }

        boolean changed = false;
        GlyphIndexer gi = null;
        for (MarkToBase mb : marksbases) {
            OtfMarkRecord omr = mb.marks.get(line.get(line.getIdx()).getCode());
            if (omr == null)
                continue;
            if (gi == null) {
                gi = new GlyphIndexer();
                gi.setIdx(line.getIdx());
                gi.setLine(line);
                while (true) {
                    gi.previousGlyph(openReader, lookupFlag);
                    if (gi.getGlyph() == null) {
                        break;
                    }
                    // not mark => base glyph
                    if (openReader.getGlyphClass(gi.getGlyph().getCode()) != OtfClass.GLYPH_MARK) {
                        break;
                    }
                }
                if (gi.getGlyph() == null) {
                    break;
                }
            }
            GposAnchor[] gpas = mb.bases.get(gi.getGlyph().getCode());
            if (gpas == null) {
                continue;
            }
            int markClass = omr.getMarkClass();
            int xPlacement = 0;
            int yPlacement = 0;
            GposAnchor baseAnchor = gpas[markClass];
            if (baseAnchor != null) {
                xPlacement = baseAnchor.getXCoordinate();
                yPlacement = baseAnchor.getYCoordinate();
            }
            GposAnchor markAnchor = omr.getAnchor();
            if (markAnchor != null) {
                xPlacement -= markAnchor.getXCoordinate();
                yPlacement -= markAnchor.getYCoordinate();
            }
            line.set(line.getIdx(), new Glyph(line.get(line.getIdx()),
                    xPlacement, yPlacement,
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

    public static class MarkToBase {
        public final Map<Integer, OtfMarkRecord> marks = new HashMap<>();
        public final Map<Integer, GposAnchor[]> bases = new HashMap<>();
    }
}
