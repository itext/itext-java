/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
import com.itextpdf.io.font.OpenTypeParser.CmapTable;
import com.itextpdf.io.source.RandomAccessFileOrArray;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Merges TrueType fonts and subset merged font by leaving only needed glyphs in the font.
 */
class TrueTypeFontMerger extends AbstractTrueTypeFontModifier {
    private OpenTypeParser cmapSourceParser = null;

    TrueTypeFontMerger(String fontName, Map<OpenTypeParser, Set<Integer>> fontsToMerge,
            boolean isCmapCheckRequired) throws java.io.IOException {

        super(fontName, true);
        horizontalMetricMap = new HashMap<>();
        glyphDataMap = new HashMap<>();
        OpenTypeParser parserExample = null;
        Set<Integer> allGids = new HashSet<>();
        Map<OpenTypeParser, List<Integer>> fontsToMergeWithFlatGlyphs = new LinkedHashMap<>();
        for (Map.Entry<OpenTypeParser, Set<Integer>> entry : fontsToMerge.entrySet()) {
            OpenTypeParser parser = entry.getKey();
            List<Integer> usedFlatGlyphs = parser.getFlatGlyphs(entry.getValue());
            fontsToMergeWithFlatGlyphs.put(parser, usedFlatGlyphs);
            allGids.addAll(usedFlatGlyphs);
        }
        // 'cmap' table shouldn't contain mapping for .notdef glyph
        allGids.remove(0);
        for (Map.Entry<OpenTypeParser, List<Integer>> entry : fontsToMergeWithFlatGlyphs.entrySet()) {
            OpenTypeParser parser = entry.getKey();

            List<Integer> usedFlatGlyphs = entry.getValue();
            for (Integer glyphObj : usedFlatGlyphs) {
                int glyph = (int) glyphObj;
                byte[] glyphData = parser.getGlyphDataForGid((int)glyph);
                if (glyphDataMap.containsKey((int)glyph) && !Arrays.equals(glyphDataMap.get((int)glyph), glyphData)) {
                    throw new IOException(IoExceptionMessageConstant.INCOMPATIBLE_GLYPH_DATA_DURING_FONT_MERGING);
                }
                glyphDataMap.put(glyph, glyphData);

                byte[] glyphMetric = parser.getHorizontalMetricForGid(glyph);
                if (horizontalMetricMap.containsKey(glyph) && !Arrays.equals(horizontalMetricMap.get(glyph), glyphMetric)) {
                    throw new IOException(IoExceptionMessageConstant.INCOMPATIBLE_GLYPH_DATA_DURING_FONT_MERGING);
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

            if (isCmapCheckRequired && cmapSourceParser == null && isCmapContainsGids(parser, allGids)) {
                cmapSourceParser = parser;
            }
        }

        if (isCmapCheckRequired && cmapSourceParser == null) {
            throw new IOException(IoExceptionMessageConstant.CMAP_TABLE_MERGING_IS_NOT_SUPPORTED);
        }

        this.raf = parserExample.raf.createView();
        this.directoryOffset = parserExample.directoryOffset;
        this.numberOfHMetrics = parserExample.hhea.numberOfHMetrics;
    }

    @Override
    protected int mergeTables() throws java.io.IOException {
        final int numOfGlyphs = super.createModifiedTables();
        // cmap table merging isn't supported yet, so cmap which
        // contains all CID will be taken (if there is such)
        if (cmapSourceParser != null) {
            final RandomAccessFileOrArray cmapSourceRaf = cmapSourceParser.raf.createView();
            final int[] tableLocation = cmapSourceParser.tables.get("cmap");
            byte[] cmap = new byte[tableLocation[1]];
            cmapSourceRaf.seek(tableLocation[0]);
            cmapSourceRaf.read(cmap);
            modifiedTables.put("cmap", cmap);
        }

        return numOfGlyphs;
    }

    private static boolean isCmapContainsGids(OpenTypeParser parser, Set<Integer> gids) {
        final CmapTable cmapTable = parser.getCmapTable();
        return isEncodingContainsGids(cmapTable.cmap03, gids)
                && isEncodingContainsGids(cmapTable.cmap10, gids)
                && isEncodingContainsGids(cmapTable.cmap30, gids)
                && isEncodingContainsGids(cmapTable.cmap31, gids)
                && isEncodingContainsGids(cmapTable.cmap310, gids);
    }

    private static boolean isEncodingContainsGids(Map<Integer, int[]> encoding, Set<Integer> gids) {
        if (encoding == null) {
            return true;
        }
        Set<Integer> encodingGids = new HashSet<>();
        for (int[] mapping : encoding.values()) {
            encodingGids.add(mapping[0]);
        }
        return encodingGids.containsAll(gids);
    }
}
