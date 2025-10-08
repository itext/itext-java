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
package com.itextpdf.io.font;

import com.itextpdf.io.exceptions.IOException;
import com.itextpdf.io.exceptions.IoExceptionMessageConstant;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Merges TrueType fonts and subset merged font by leaving only needed glyphs in the font.
 */
class TrueTypeFontMerger extends AbstractTrueTypeFontModifier {

    TrueTypeFontMerger(String fontName, Map<OpenTypeParser, Set<Integer>> fontsToMerge) throws java.io.IOException {
        super(fontName, true);
        horizontalMetricMap = new HashMap<>();
        glyphDataMap = new HashMap<>();
        OpenTypeParser parserExample = null;
        for (Map.Entry<OpenTypeParser, Set<Integer>> entry : fontsToMerge.entrySet()) {
            OpenTypeParser parser = entry.getKey();

            List<Integer> usedGlyphs = parser.getFlatGlyphs(entry.getValue());
            for (Integer glyphObj : usedGlyphs) {
                int glyph = (int) glyphObj;
                byte[] glyphData = parser.getGlyphDataForGid((int)glyph);
                if (glyphDataMap.containsKey((int)glyph) && !Arrays.equals(glyphDataMap.get((int)glyph), glyphData)) {
                    throw new IOException(IoExceptionMessageConstant.INCOMPATIBLE_GLYPH_DATA_DURING_FONT_MERGING)
                            .setMessageParams(fontName);
                }
                glyphDataMap.put(glyph, glyphData);

                byte[] glyphMetric = parser.getHorizontalMetricForGid(glyph);
                if (horizontalMetricMap.containsKey(glyph) && !Arrays.equals(horizontalMetricMap.get(glyph), glyphMetric)) {
                    throw new IOException(IoExceptionMessageConstant.INCOMPATIBLE_GLYPH_DATA_DURING_FONT_MERGING)
                            .setMessageParams(fontName);
                }
                horizontalMetricMap.put(glyph, glyphMetric);
            }
            // hmtx table size is defined by numberOfHMetrics field from hhea table. To avoid hmtx table resizing and
            // updating numberOfHMetrics, as a base font, on which merged font will be built, we choose the font with
            // the biggest numberOfHMetrics value. Biggest numberOfHMetrics guarantee that hmtx table will contain
            // gids from all the fonts.
            if (parserExample == null || parser.hhea.numberOfHMetrics > parserExample.hhea.numberOfHMetrics) {
                parserExample = parser;
            }
        }

        this.raf = parserExample.raf.createView();
        this.directoryOffset = parserExample.directoryOffset;
        this.numberOfHMetrics = parserExample.hhea.numberOfHMetrics;
    }

    @Override
    protected int mergeTables() throws java.io.IOException {
        return super.createModifiedTables();
        // cmap table merging isn't supported yet
    }
}
