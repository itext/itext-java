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
 * Lookup Type 5:
 * MarkToLigature Attachment Positioning Subtable
 */
public class GposLookupType5 extends OpenTableLookup {

    private final List<MarkToLigature> marksligatures;

    public GposLookupType5(OpenTypeFontTableReader openReader, int lookupFlag, int[] subTableLocations) throws java.io.IOException {
        super(openReader, lookupFlag, subTableLocations);
        marksligatures = new ArrayList<>();
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
        GlyphIndexer ligatureGlyphIndexer = null;
        for (MarkToLigature mb : marksligatures) {
            OtfMarkRecord omr = mb.marks.get(line.get(line.getIdx()).getCode());
            if (omr == null)
                continue;
            if (ligatureGlyphIndexer == null) {
                ligatureGlyphIndexer = new GlyphIndexer();
                ligatureGlyphIndexer.setIdx(line.getIdx());
                ligatureGlyphIndexer.setLine(line);
                while (true) {
                    ligatureGlyphIndexer.previousGlyph(openReader, lookupFlag);
                    if (ligatureGlyphIndexer.getGlyph() == null) {
                        break;
                    }
                    // not mark => ligature glyph
                    if (!mb.marks.containsKey(ligatureGlyphIndexer.getGlyph().getCode())) {
                        break;
                    }
                }
                if (ligatureGlyphIndexer.getGlyph() == null) {
                    break;
                }
            }
            List<GposAnchor[]> componentAnchors = mb.ligatures.get(ligatureGlyphIndexer.getGlyph().getCode());
            if (componentAnchors == null) {
                continue;
            }
            int markClass = omr.getMarkClass();
            // TODO DEVSIX-3732 For complex cases like (glyph1, glyph2, mark, glyph3) and
            //  (glyph1, mark, glyph2, glyph3) when the base glyphs compose a ligature and the mark
            //  is attached to the ligature afterwards, mark should be placed in the corresponding anchor
            //  of that ligature (by finding the right component's anchor).
            //  Excerpt from Microsoft Docs: "For a given mark assigned to a particular class, the appropriate
            //  base attachment point is determined by which ligature component the mark is associated with.
            //  This is dependent on the original character string and subsequent character- or glyph-sequence
            //  processing, not the font data alone. While a text-layout client is performing any character-based
            //  preprocessing or any glyph-substitution operations using the GSUB table, the text-layout client
            //  must keep track of the associations of marks to particular ligature-glyph components."
            //  For now we do not store all the substitution info and therefore not able to follow that logic.
            //  We place the mark symbol in the last available place for now (seems to be better default than
            //  first available place).
            for (int component = componentAnchors.size() - 1; component >= 0; component--) {
                if (componentAnchors.get(component)[markClass] != null) {
                    GposAnchor baseAnchor = componentAnchors.get(component)[markClass];
                    GposAnchor markAnchor = omr.getAnchor();
                    line.set(line.getIdx(), new Glyph(line.get(line.getIdx()),
                            baseAnchor.getXCoordinate() - markAnchor.getXCoordinate(),
                            baseAnchor.getYCoordinate() - markAnchor.getYCoordinate(),
                            0, 0, ligatureGlyphIndexer.getIdx() - line.getIdx()));
                    changed = true;
                    break;
                }
            }

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
        int ligatureCoverageLocation = openReader.rf.readUnsignedShort() + subTableLocation;
        int classCount = openReader.rf.readUnsignedShort();
        int markArrayLocation = openReader.rf.readUnsignedShort() + subTableLocation;
        int ligatureArrayLocation = openReader.rf.readUnsignedShort() + subTableLocation;
        List<Integer> markCoverage = openReader.readCoverageFormat(markCoverageLocation);
        List<Integer> ligatureCoverage = openReader.readCoverageFormat(ligatureCoverageLocation);
        List<OtfMarkRecord> markRecords = OtfReadCommon.readMarkArray(openReader, markArrayLocation);
        MarkToLigature markToLigature = new MarkToLigature();
        for (int k = 0; k < markCoverage.size(); ++k) {
            markToLigature.marks.put(markCoverage.get(k), markRecords.get(k));
        }
        List<List<GposAnchor[]>> ligatureArray = OtfReadCommon.readLigatureArray(openReader, classCount, ligatureArrayLocation);
        for (int k = 0; k < ligatureCoverage.size(); ++k) {
            markToLigature.ligatures.put(ligatureCoverage.get(k), ligatureArray.get(k));
        }
        marksligatures.add(markToLigature);
    }


    public static class MarkToLigature {
        public final Map<Integer, OtfMarkRecord> marks = new HashMap<>();
        // Glyph id to list of components, each component has a separate list of attachment points
        // defined for different mark classes
        public final Map<Integer, List<GposAnchor[]>> ligatures = new HashMap<>();
    }
}
