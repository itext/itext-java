/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.io.font.otf;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Lookup Type 6:
 * MarkToMark Attachment Positioning Subtable
 */
public class GposLookupType6 extends OpenTableLookup {

    private static final long serialVersionUID = -2213669257401436260L;
    private final List<MarkToBaseMark> marksbases;

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
        for (MarkToBaseMark mb : marksbases) {
            OtfMarkRecord omr = mb.marks.get(line.get(line.idx).getCode());
            if (omr == null)
                continue;
            if (gi == null) {
                gi = new GlyphIndexer();
                gi.idx = line.idx;
                gi.line = line;
                while (true) {
                    int prev = gi.idx;
                    // avoid attaching this mark glyph to another very distant mark glyph
                    boolean foundBaseGlyph = false;
                    gi.previousGlyph(openReader, lookupFlag);
                    if (gi.idx != -1) {
                        for (int i = gi.idx; i < prev; i++) {
                            if (openReader.getGlyphClass(line.get(i).getCode()) == OtfClass.GLYPH_BASE) {
                                foundBaseGlyph = true;
                                break;
                            }
                        }
                    }
                    if (foundBaseGlyph) {
                        gi.glyph = null;
                        break;
                    }
                    if (gi.glyph == null)
                        break;
                    if (mb.baseMarks.containsKey(gi.glyph.getCode()))
                        break;
                }
                if (gi.glyph == null)
                    break;
            }
            GposAnchor[] gpas = mb.baseMarks.get(gi.glyph.getCode());
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

    private static class MarkToBaseMark implements Serializable {
        private static final long serialVersionUID = -2097614797893579206L;
        public final Map<Integer, OtfMarkRecord> marks = new HashMap<>();
        public final Map<Integer, GposAnchor[]> baseMarks = new HashMap<>();
    }

}
