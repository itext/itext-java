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

import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Subsets a True Type font by removing the unneeded glyphs from the font.
 */
class TrueTypeFontSubsetter extends AbstractTrueTypeFontModifier {

    TrueTypeFontSubsetter(String fontName, OpenTypeParser parser, Set<Integer> glyphs, boolean subsetTables)
            throws java.io.IOException {
        super(fontName, subsetTables);
        horizontalMetricMap = new HashMap<>(glyphs.size());
        glyphDataMap = new HashMap<>(glyphs.size());
        List<Integer> usedGlyphs = parser.getFlatGlyphs(glyphs);
        for (Integer glyph : usedGlyphs) {
            byte[] glyphData = parser.getGlyphDataForGid((int) glyph);
            glyphDataMap.put((int) glyph, glyphData);

            byte[] glyphMetric = parser.getHorizontalMetricForGid((int) glyph);
            horizontalMetricMap.put((int) glyph, glyphMetric);
        }

        this.raf = parser.raf.createView();
        this.directoryOffset = parser.directoryOffset;
        this.numberOfHMetrics = parser.hhea.numberOfHMetrics;
    }

    @Override
    protected int mergeTables() throws java.io.IOException {
        return super.createModifiedTables();
        // cmap table subsetting isn't supported yet
    }
}
