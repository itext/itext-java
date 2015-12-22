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
public class GlyphPositioningTableReader extends OpenTypeFontTableReader {
    public GlyphPositioningTableReader(RandomAccessFileOrArray rf, int gposTableLocation,
                                       OpenTypeGdefTableReader gdef, Map<Integer, Glyph> indexGlyphMap) throws IOException {
        super(rf, gposTableLocation, gdef, indexGlyphMap);
        startReadingTable();
    }

    @Override
    protected OpenTableLookup readLookupTable(int lookupType, int lookupFlag, int[] subTableLocations) throws IOException {
        switch (lookupType) {
            case 4:
                return new GposLookupType4(this, lookupFlag, subTableLocations);
            default:
                return null;
        }
    }
}
