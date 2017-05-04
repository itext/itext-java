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
public class CharacterRenderInfo extends LocationTextExtractionStrategy.TextChunk {

    private Rectangle boundingBox;

    /**
     * This method converts a List<CharacterRenderInfo>
     * The datastructure that gets returned contains both the plaintext,
     * as well as the mapping of indices (from the list to the string).
     * These indices can differ; if there is sufficient spacing between two CharacterRenderInfo
     * objects, this algorithm will decide to insert space. The inserted space will cause
     * the indices to differ by at least 1.
     *
     * @param cris
     * @return
     */
    static StringConversionInfo mapString(List<CharacterRenderInfo> cris) {
        Map<Integer, Integer> indexMap = new HashMap<>();
        StringBuilder sb = new StringBuilder();
        CharacterRenderInfo lastChunk = null;
        for (int i = 0; i < cris.size(); i++) {
            CharacterRenderInfo chunk = cris.get(i);
            if (lastChunk == null) {
                indexMap.put(sb.length(), i);
                sb.append(chunk.getText());
            } else {
                if (chunk.sameLine(lastChunk)) {
                    // we only insert a blank space if the trailing character of the previous string wasn't a space, and the leading character of the current string isn't a space
                    if (chunk.getLocation().isAtWordBoundary(lastChunk.getLocation()) && !chunk.getText().startsWith(" ") && !chunk.getText().endsWith(" ")) {
                        sb.append(' ');
                    }
                    indexMap.put(sb.length(), i);
                    sb.append(chunk.getText());
                } else {
                    indexMap.put(sb.length(), i);
                    sb.append(chunk.getText());
                }
            }
            lastChunk = chunk;
        }
        CharacterRenderInfo.StringConversionInfo ret = new StringConversionInfo();
        ret.indexMap = indexMap;
        ret.text = sb.toString();
        return ret;
    }

    public CharacterRenderInfo(TextRenderInfo tri) {
        super(tri == null ? "" : tri.getText(), tri == null ? null : getLocation(tri));
        if (tri == null)
            throw new IllegalArgumentException("TextRenderInfo argument is not nullable.");
        if (tri.getText().length() != 1)
            throw new IllegalArgumentException("CharacterRenderInfo objects represent a single character. They should not be made from TextRenderInfo objects containing more than a single character of text.");

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

    private static LocationTextExtractionStrategy.ITextChunkLocation getLocation(TextRenderInfo tri) {
        LineSegment baseline = tri.getBaseline();
        return new LocationTextExtractionStrategy.TextChunkLocationDefaultImp(baseline.getStartPoint(),
                baseline.getEndPoint(),
                tri.getSingleSpaceWidth());
    }

    static class StringConversionInfo {
        Map<Integer, Integer> indexMap;
        String text;
    }
}
