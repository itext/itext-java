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

import com.itextpdf.io.source.RandomAccessFileOrArray;

import java.util.Map;

/**
 * Parses an OpenTypeFont file and reads the Glyph Substitution Table. This table governs how two or more Glyphs should be merged
 * to a single Glyph. This is especially useful for Asian languages like Bangla, Hindi, etc.
 * <p>
 * This has been written according to the OPenTypeFont specifications. This may be found <a href="http://www.microsoft.com/typography/otspec/gsub.htm">here</a>.
 */
public class GlyphSubstitutionTableReader extends OpenTypeFontTableReader {


    public GlyphSubstitutionTableReader(RandomAccessFileOrArray rf, int gsubTableLocation, OpenTypeGdefTableReader gdef,
                                        Map<Integer, Glyph> indexGlyphMap, int unitsPerEm) throws java.io.IOException {
        super(rf, gsubTableLocation, gdef, indexGlyphMap, unitsPerEm);
        startReadingTable();
    }

    @Override
    protected OpenTableLookup readLookupTable(int lookupType, int lookupFlag, int[] subTableLocations) throws java.io.IOException {
        if (lookupType == 7) {
            for (int k = 0; k < subTableLocations.length; ++k) {
                int location = subTableLocations[k];
                rf.seek(location);
                rf.readUnsignedShort();
                lookupType = rf.readUnsignedShort();
                location += rf.readInt();
                subTableLocations[k] = location;
            }
        }
        switch (lookupType) {
            case 1:
                return new GsubLookupType1(this, lookupFlag, subTableLocations);
            case 2:
                return new GsubLookupType2(this, lookupFlag, subTableLocations);
            case 3:
                return new GsubLookupType3(this, lookupFlag, subTableLocations);
            case 4:
                return new GsubLookupType4(this, lookupFlag, subTableLocations);
            case 5:
                return new GsubLookupType5(this, lookupFlag, subTableLocations);
            case 6:
                return new GsubLookupType6(this, lookupFlag, subTableLocations);
            default:
                return null;
        }
    }
}
