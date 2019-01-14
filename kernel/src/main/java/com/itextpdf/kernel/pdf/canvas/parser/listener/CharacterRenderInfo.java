/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

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
package com.itextpdf.kernel.pdf.canvas.parser.listener;

import com.itextpdf.kernel.geom.LineSegment;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.parser.data.TextRenderInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class represents a single character and its bounding box
 */
public class CharacterRenderInfo extends TextChunk {

    private Rectangle boundingBox;

    /**
     * This method converts a List<CharacterRenderInfo>
     * The data structure that gets returned contains both the plaintext,
     * as well as the mapping of indices (from the list to the string).
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
        float x0 = tri.getDescentLine().getStartPoint().get(0);
        float y0 = tri.getDescentLine().getStartPoint().get(1);
        float h = tri.getAscentLine().getStartPoint().get(1) - tri.getDescentLine().getStartPoint().get(1);
        float w = Math.abs(tri.getBaseline().getStartPoint().get(0) - tri.getBaseline().getEndPoint().get(0));
        this.boundingBox = new Rectangle(x0, y0, w, h);
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
