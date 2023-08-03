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
package com.itextpdf.kernel.pdf.canvas.parser.listener;

import com.itextpdf.kernel.geom.LineSegment;
import com.itextpdf.kernel.geom.Point;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.parser.data.TextRenderInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class represents a single character and its bounding box
 */
public class CharacterRenderInfo extends TextChunk {

    private Rectangle boundingBox;

    /**
     * This method converts a {@link List} of {@link CharacterRenderInfo}.
     * The returned data structure contains both the plaintext
     * and the mapping of indices (from the list to the string).
     * These indices can differ; if there is sufficient spacing between two CharacterRenderInfo
     * objects, this algorithm will decide to insert space. The inserted space will cause
     * the indices to differ by at least 1.
     */
    static StringConversionInfo mapString(List<CharacterRenderInfo> cris) {
        Map<Integer, Integer> indexMap = new HashMap<>();
        StringBuilder sb = new StringBuilder();
        CharacterRenderInfo lastChunk = null;
        for (int i = 0; i < cris.size(); i++) {
            CharacterRenderInfo chunk = cris.get(i);
            if (lastChunk == null) {
                putCharsWithIndex(chunk.getText(), i, indexMap, sb);
            } else {
                if (chunk.sameLine(lastChunk)) {
                    // we only insert a blank space if the trailing character of the previous string wasn't a space, and the leading character of the current string isn't a space
                    if (chunk.getLocation().isAtWordBoundary(lastChunk.getLocation()) && !chunk.getText().startsWith(" ") && !chunk.getText().endsWith(" ")) {
                        sb.append(' ');
                    }
                    putCharsWithIndex(chunk.getText(), i, indexMap, sb);
                } else {
                    // we insert a newline character in the resulting string if the chunks are placed on different lines
                    sb.append('\n');
                    putCharsWithIndex(chunk.getText(), i, indexMap, sb);
                }
            }
            lastChunk = chunk;
        }
        CharacterRenderInfo.StringConversionInfo ret = new StringConversionInfo();
        ret.indexMap = indexMap;
        ret.text = sb.toString();
        return ret;
    }

    private static void putCharsWithIndex(final CharSequence seq, int index, final Map<Integer, Integer> indexMap, StringBuilder sb) {
        int charCount = seq.length();
        for (int i = 0; i < charCount; i++) {
            indexMap.put(sb.length(), index);
            sb.append(seq.charAt(i));
        }
    }

    public CharacterRenderInfo(TextRenderInfo tri) {
        super(tri == null ? "" : tri.getText(), tri == null ? null : getLocation(tri));
        if (tri == null)
            throw new IllegalArgumentException("TextRenderInfo argument is not nullable.");

        // determine bounding box
        List<Point> points = new ArrayList<>();
        points.add(new Point(tri.getDescentLine().getStartPoint().get(0),tri.getDescentLine().getStartPoint().get(1)));
        points.add(new Point(tri.getDescentLine().getEndPoint().get(0),tri.getDescentLine().getEndPoint().get(1)));
        points.add(new Point(tri.getAscentLine().getStartPoint().get(0),tri.getAscentLine().getStartPoint().get(1)));
        points.add(new Point(tri.getAscentLine().getEndPoint().get(0),tri.getAscentLine().getEndPoint().get(1)));

        this.boundingBox = Rectangle.calculateBBox(points);
    }

    public Rectangle getBoundingBox() {
        return boundingBox;
    }

    private static ITextChunkLocation getLocation(TextRenderInfo tri) {
        LineSegment baseline = tri.getBaseline();
        return new TextChunkLocationDefaultImp(baseline.getStartPoint(),
                baseline.getEndPoint(),
                tri.getSingleSpaceWidth());
    }

    static class StringConversionInfo {
        Map<Integer, Integer> indexMap;
        String text;
    }
}
