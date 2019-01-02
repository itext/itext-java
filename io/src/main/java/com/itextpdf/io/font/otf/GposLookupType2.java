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
import java.util.HashSet;
import java.util.List;
import java.util.Map;


/**
 * Lookup Type 2:
 * Pair Adjustment Positioning Subtable
 */
public class GposLookupType2 extends OpenTableLookup {

    private static final long serialVersionUID = 4781829862270887603L;

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

        private static final long serialVersionUID = -5556528810086852702L;

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
        protected void readSubTable(int subTableLocation) throws java.io.IOException {
            //never called here
        }
    }

    private static class PairPosAdjustmentFormat2 extends OpenTableLookup {
        private static final long serialVersionUID = 3056620748845862393L;
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
        protected void readSubTable(int subTableLocation) throws java.io.IOException {
            //never called here
        }
    }

    private static class PairValueFormat implements Serializable {
        private static final long serialVersionUID = -6442882035589529495L;
        public GposValueRecord first;
        public GposValueRecord second;
    }
}
