/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;


/**
 * Lookup Type 2:
 * Pair Adjustment Positioning Subtable
 */
public class GposLookupType2 extends OpenTableLookup {


    private List<OpenTableLookup> listRules = new ArrayList<>();

    public GposLookupType2(OpenTypeFontTableReader openReader, int lookupFlag, int[] subTableLocations) throws java.io.IOException {
        super(openReader, lookupFlag, subTableLocations);
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
        for (OpenTableLookup lookup : listRules) {
            if (lookup.transformOne(line))
                return true;
        }
        ++line.idx;
        return false;
    }

    @Override
    protected void readSubTable(int subTableLocation) throws java.io.IOException {
        openReader.rf.seek(subTableLocation);
        int gposFormat = openReader.rf.readShort();
        switch (gposFormat) {
            case 1: {
                PairPosAdjustmentFormat1 format1 = new PairPosAdjustmentFormat1(openReader, lookupFlag, subTableLocation);
                listRules.add(format1);
                break;
            }
            case 2: {
                PairPosAdjustmentFormat2 format2 = new PairPosAdjustmentFormat2(openReader, lookupFlag, subTableLocation);
                listRules.add(format2);
                break;
            }
        }
    }

    private static class PairPosAdjustmentFormat1 extends OpenTableLookup {


        private Map<Integer,Map<Integer,PairValueFormat>> gposMap = new HashMap<>();

        public PairPosAdjustmentFormat1(OpenTypeFontTableReader openReader, int lookupFlag, int subtableLocation) throws java.io.IOException {
            super(openReader, lookupFlag, null);
            readFormat(subtableLocation);
        }

        public boolean transformOne(GlyphLine line) {
            if (line.idx >= line.end || line.idx < line.start)
                return false;
            boolean changed = false;
            Glyph g1 = line.get(line.idx);
            Map<Integer,PairValueFormat> m = gposMap.get(g1.getCode());
            if (m != null) {
                GlyphIndexer gi = new GlyphIndexer();
                gi.line = line;
                gi.idx = line.idx;
                gi.nextGlyph(openReader, lookupFlag);
                if (gi.glyph != null) {
                    PairValueFormat pv = m.get(gi.glyph.getCode());
                    if (pv != null) {
                        Glyph g2 = gi.glyph;
                        line.set(line.idx, new Glyph(g1, 0, 0, pv.first.XAdvance, pv.first.YAdvance, 0));
                        line.set(gi.idx, new Glyph(g2, 0, 0, pv.second.XAdvance, pv.second.YAdvance, 0));
                        line.idx = gi.idx;
                        changed = true;
                    }
                }
            }
            return changed;
        }

        protected void readFormat(int subTableLocation) throws java.io.IOException {
            int coverage = openReader.rf.readUnsignedShort() + subTableLocation;
            int valueFormat1 = openReader.rf.readUnsignedShort();
            int valueFormat2 = openReader.rf.readUnsignedShort();
            int pairSetCount = openReader.rf.readUnsignedShort();
            int[] locationRule = openReader.readUShortArray(pairSetCount, subTableLocation);
            List<Integer> coverageList = openReader.readCoverageFormat(coverage);
            for (int k = 0; k < pairSetCount; ++k) {
                openReader.rf.seek(locationRule[k]);
                Map<Integer,PairValueFormat> pairs = new HashMap<>();
                gposMap.put(coverageList.get(k), pairs);
                int pairValueCount = openReader.rf.readUnsignedShort();
                for (int j = 0; j < pairValueCount; ++j) {
                    int glyph2 = openReader.rf.readUnsignedShort();
                    PairValueFormat pair = new PairValueFormat();
                    pair.first = OtfReadCommon.readGposValueRecord(openReader, valueFormat1);
                    pair.second = OtfReadCommon.readGposValueRecord(openReader, valueFormat2);
                    pairs.put(glyph2, pair);
                }
            }
        }

        @Override
        protected void readSubTable(int subTableLocation) {
            //never called here
        }
    }

    private static class PairPosAdjustmentFormat2 extends OpenTableLookup {
        private OtfClass classDef1;
        private OtfClass classDef2;
        private HashSet<Integer> coverageSet;
        private Map<Integer,PairValueFormat[]> posSubs = new HashMap<>();

        public PairPosAdjustmentFormat2(OpenTypeFontTableReader openReader, int lookupFlag, int subtableLocation) throws java.io.IOException {
            super(openReader, lookupFlag, null);
            readFormat(subtableLocation);
        }

        public boolean transformOne(GlyphLine line) {
            if (line.idx >= line.end || line.idx < line.start)
                return false;
            Glyph g1 = line.get(line.idx);
            if (!coverageSet.contains(g1.getCode()))
                return false;
            int c1 = classDef1.getOtfClass(g1.getCode());
            PairValueFormat[] pvs = posSubs.get(c1);
            if (pvs == null)
                return false;
            GlyphIndexer gi = new GlyphIndexer();
            gi.line = line;
            gi.idx = line.idx;
            gi.nextGlyph(openReader, lookupFlag);
            if (gi.glyph == null)
                return false;
            Glyph g2 = gi.glyph;
            int c2 = classDef2.getOtfClass(g2.getCode());
            if (c2 >= pvs.length)
                return false;
            PairValueFormat pv = pvs[c2];
            line.set(line.idx, new Glyph(g1, 0, 0, pv.first.XAdvance, pv.first.YAdvance, 0));
            line.set(gi.idx, new Glyph(g2, 0, 0, pv.second.XAdvance, pv.second.YAdvance, 0));
            line.idx = gi.idx;
            return true;
        }

        protected void readFormat(int subTableLocation) throws java.io.IOException {
            int coverage = openReader.rf.readUnsignedShort()+ subTableLocation;
            int valueFormat1 = openReader.rf.readUnsignedShort();
            int valueFormat2 = openReader.rf.readUnsignedShort();
            int locationClass1 = openReader.rf.readUnsignedShort() + subTableLocation;
            int locationClass2 = openReader.rf.readUnsignedShort() + subTableLocation;
            int class1Count = openReader.rf.readUnsignedShort();
            int class2Count = openReader.rf.readUnsignedShort();

            for (int k = 0; k < class1Count; ++k) {
                PairValueFormat[] pairs = new PairValueFormat[class2Count];
                posSubs.put(k, pairs);
                for (int j = 0; j < class2Count; ++j) {
                    PairValueFormat pair = new PairValueFormat();
                    pair.first = OtfReadCommon.readGposValueRecord(openReader, valueFormat1);
                    pair.second = OtfReadCommon.readGposValueRecord(openReader, valueFormat2);
                    pairs[j] = pair;
                }
            }

            coverageSet = new HashSet<>(openReader.readCoverageFormat(coverage));
            classDef1 = openReader.readClassDefinition(locationClass1);
            classDef2 = openReader.readClassDefinition(locationClass2);
        }

        @Override
        protected void readSubTable(int subTableLocation) {
            //never called here
        }
    }

    private static class PairValueFormat {
        public GposValueRecord first;
        public GposValueRecord second;
    }
}
