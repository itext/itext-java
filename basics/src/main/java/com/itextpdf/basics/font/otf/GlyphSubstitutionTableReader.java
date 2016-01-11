package com.itextpdf.basics.font.otf;

import com.itextpdf.basics.io.RandomAccessFileOrArray;

import java.io.IOException;
import java.util.Map;


/**
 * <p>
 * Parses an OpenTypeFont file and reads the Glyph Substitution Table. This table governs how two or more Glyphs should be merged
 * to a single Glyph. This is especially useful for Asian languages like Bangla, Hindi, etc.
 * </p>
 * <p>
 * This has been written according to the OPenTypeFont specifications. This may be found <a href="http://www.microsoft.com/typography/otspec/gsub.htm">here</a>.
 * </p>
 * 
 * @author <a href="mailto:paawak@gmail.com">Palash Ray</a>
 */
public class GlyphSubstitutionTableReader extends OpenTypeFontTableReader {

    public GlyphSubstitutionTableReader(RandomAccessFileOrArray rf, int gsubTableLocation, OpenTypeGdefTableReader gdef,
        Map<Integer, Glyph> indexGlyphMap, int unitsPerEm) throws IOException {
        super(rf, gsubTableLocation, gdef, indexGlyphMap, unitsPerEm);
        startReadingTable();
    }

    @Override
    protected OpenTableLookup readLookupTable(int lookupType, int lookupFlag, int[] subTableLocations) throws IOException {
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
