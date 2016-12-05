/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV
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
package com.itextpdf.kernel.pdf.canvas.parser.listener;

import com.itextpdf.kernel.geom.LineSegment;
import com.itextpdf.kernel.geom.Matrix;
import com.itextpdf.kernel.geom.Vector;
import com.itextpdf.kernel.pdf.canvas.CanvasTag;
import com.itextpdf.kernel.pdf.canvas.parser.EventType;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.itextpdf.kernel.pdf.canvas.parser.data.TextRenderInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LocationTextExtractionStrategy implements ITextExtractionStrategy {

    /**
     * set to true for debugging
     */
    private static boolean DUMP_STATE = false;

    private static final float DIACRITICAL_MARKS_ALLOWED_VERTICAL_DEVIATION = 2;

    /**
     * a summary of all found text
     */
    private final List<TextChunk> locationalResult = new ArrayList<>();

    private final ITextChunkLocationStrategy tclStrat;

    private boolean useActualText = false;

    private boolean rightToLeftRunDirection = false;

    private TextRenderInfo lastTextRenderInfo;

    /**
     * Creates a new text extraction renderer.
     */
    public LocationTextExtractionStrategy() {
        this(new ITextChunkLocationStrategy() {
            public ITextChunkLocation createLocation(TextRenderInfo renderInfo, LineSegment baseline) {
                return new TextChunkLocationDefaultImp(baseline.getStartPoint(), baseline.getEndPoint(), renderInfo.getSingleSpaceWidth());
            }
        });
    }

    /**
     * Creates a new text extraction renderer, with a custom strategy for
     * creating new TextChunkLocation objects based on the input of the
     * TextRenderInfo.
     *
     * @param strat the custom strategy
     */
    public LocationTextExtractionStrategy(ITextChunkLocationStrategy strat) {
        tclStrat = strat;
    }

    /**
     * Changes the behavior of text extraction so that if the parameter is set to {@code true},
     * /ActualText marked content property will be used instead of raw decoded bytes.
     * Beware: the logic is not stable yet.
     *
     * @param useActualText true to use /ActualText, false otherwise
     * @return this object
     */
    public LocationTextExtractionStrategy setUseActualText(boolean useActualText) {
        this.useActualText = useActualText;
        return this;
    }

    /**
     * Sets if text flows from left to right or from right to left.
     * Call this method with <code>true</code> argument for extracting Arabic, Hebrew or other
     * text with right-to-left writing direction.
     *
     * @param rightToLeftRunDirection value specifying whether the direction should be right to left
     * @return this object
     */
    public LocationTextExtractionStrategy setRightToLeftRunDirection(boolean rightToLeftRunDirection) {
        this.rightToLeftRunDirection = rightToLeftRunDirection;
        return this;
    }

    /**
     * Gets the value of the property which determines if /ActualText will be used when extracting
     * the text
     *
     * @return true if /ActualText value is used, false otherwise
     */
    public boolean isUseActualText() {
        return useActualText;
    }

    @Override
    public void eventOccurred(IEventData data, EventType type) {
        if (type.equals(EventType.RENDER_TEXT)) {
            TextRenderInfo renderInfo = (TextRenderInfo) data;
            LineSegment segment = renderInfo.getBaseline();
            if (renderInfo.getRise() != 0) {
                // remove the rise from the baseline - we do this because the text from a super/subscript render operations should probably be considered as part of the baseline of the text the super/sub is relative to
                Matrix riseOffsetTransform = new Matrix(0, -renderInfo.getRise());
                segment = segment.transformBy(riseOffsetTransform);
            }

            if (useActualText) {
                CanvasTag lastTagWithActualText = lastTextRenderInfo != null
                        ? findLastTagWithActualText(lastTextRenderInfo.getCanvasTagHierarchy())
                        : null;
                if (lastTagWithActualText != null && lastTagWithActualText == findLastTagWithActualText(renderInfo.getCanvasTagHierarchy())) {
                    // Merge two text pieces, assume they will be in the same line
                    TextChunk lastTextChunk = locationalResult.get(locationalResult.size() - 1);
                    Vector mergedStart = new Vector(Math.min(lastTextChunk.getLocation().getStartLocation().get(0), segment.getStartPoint().get(0)),
                            Math.min(lastTextChunk.getLocation().getStartLocation().get(1), segment.getStartPoint().get(1)),
                            Math.min(lastTextChunk.getLocation().getStartLocation().get(2), segment.getStartPoint().get(2)));
                    Vector mergedEnd = new Vector(Math.max(lastTextChunk.getLocation().getEndLocation().get(0), segment.getEndPoint().get(0)),
                            Math.max(lastTextChunk.getLocation().getEndLocation().get(1), segment.getEndPoint().get(1)),
                            Math.max(lastTextChunk.getLocation().getEndLocation().get(2), segment.getEndPoint().get(2)));
                    TextChunk merged = new TextChunk(lastTextChunk.getText(), tclStrat.createLocation(renderInfo,
                            new LineSegment(mergedStart, mergedEnd)));
                    locationalResult.set(locationalResult.size() - 1, merged);
                } else {
                    String actualText = renderInfo.getActualText();
                    TextChunk tc = new TextChunk(actualText != null ? actualText : renderInfo.getText(),
                            tclStrat.createLocation(renderInfo, segment));
                    locationalResult.add(tc);
                }
            } else {
                TextChunk tc = new TextChunk(renderInfo.getText(), tclStrat.createLocation(renderInfo, segment));
                locationalResult.add(tc);
            }

            lastTextRenderInfo = renderInfo;
        }
    }

    @Override
    public Set<EventType> getSupportedEvents() {
        return null;
    }

    @Override
    public String getResultantText() {
        if (DUMP_STATE) dumpState();

        List<TextChunk> textChunks = new ArrayList<>(locationalResult);
        sortWithMarks(textChunks);

        StringBuilder sb = new StringBuilder();
        TextChunk lastChunk = null;
        for (TextChunk chunk : textChunks) {
            if (lastChunk == null) {
                sb.append(chunk.text);
            } else {
                if (chunk.sameLine(lastChunk)) {
                    // we only insert a blank space if the trailing character of the previous string wasn't a space, and the leading character of the current string isn't a space
                    if (isChunkAtWordBoundary(chunk, lastChunk) && !startsWithSpace(chunk.text) && !endsWithSpace(lastChunk.text)) {
                        sb.append(' ');
                    }

                    sb.append(chunk.text);
                } else {
                    sb.append('\n');
                    sb.append(chunk.text);
                }
            }
            lastChunk = chunk;
        }

        return sb.toString();
    }

    /**
     * Determines if a space character should be inserted between a previous chunk and the current chunk.
     * This method is exposed as a callback so subclasses can fine time the algorithm for determining whether a space should be inserted or not.
     * By default, this method will insert a space if the there is a gap of more than half the font space character width between the end of the
     * previous chunk and the beginning of the current chunk.  It will also indicate that a space is needed if the starting point of the new chunk
     * appears *before* the end of the previous chunk (i.e. overlapping text).
     *
     * @param chunk         the new chunk being evaluated
     * @param previousChunk the chunk that appeared immediately before the current chunk
     * @return true if the two chunks represent different words (i.e. should have a space between them).  False otherwise.
     */
    protected boolean isChunkAtWordBoundary(TextChunk chunk, TextChunk previousChunk) {
        return chunk.getLocation().isAtWordBoundary(previousChunk.getLocation());
    }

    /**
     * Checks if the string starts with a space character, false if the string is empty or starts with a non-space character.
     *
     * @param str the string to be checked
     * @return true if the string starts with a space character, false if the string is empty or starts with a non-space character
     */
    private boolean startsWithSpace(String str) {
        return str.length() != 0 && str.charAt(0) == ' ';
    }

    /**
     * Checks if the string ends with a space character, false if the string is empty or ends with a non-space character
     *
     * @param str the string to be checked
     * @return true if the string ends with a space character, false if the string is empty or ends with a non-space character
     */
    private boolean endsWithSpace(String str) {
        return str.length() != 0 && str.charAt(str.length() - 1) == ' ';
    }

    /**
     * Used for debugging only
     */
    private void dumpState() {
        for (TextChunk location : locationalResult) {
            location.printDiagnostics();
            System.out.println();
        }
    }

    private CanvasTag findLastTagWithActualText(List<CanvasTag> canvasTagHierarchy) {
        CanvasTag lastActualText = null;
        for (CanvasTag tag : canvasTagHierarchy) {
            if (tag.getActualText() != null) {
                lastActualText = tag;
                break;
            }
        }
        return lastActualText;
    }

    private void sortWithMarks(List<TextChunk> textChunks) {
        Map<TextChunk, TextChunkMarks> marks = new HashMap<>();
        List<TextChunk> toSort = new ArrayList<>();

        for (int markInd = 0; markInd < textChunks.size(); markInd++) {
            ITextChunkLocation location = textChunks.get(markInd).getLocation();
            if (location.getStartLocation().equals(location.getEndLocation())) {
                boolean foundBaseToAttachTo = false;
                for (int baseInd = 0; baseInd < textChunks.size(); baseInd++) {
                    if (markInd != baseInd) {
                        ITextChunkLocation baseLocation = textChunks.get(baseInd).getLocation();
                        if (!baseLocation.getStartLocation().equals(baseLocation.getEndLocation()) && containsMark(baseLocation, location)) {
                            TextChunkMarks currentMarks = marks.get(textChunks.get(baseInd));
                            if (currentMarks == null) {
                                currentMarks = new TextChunkMarks();
                                marks.put(textChunks.get(baseInd), currentMarks);
                            }

                            if (markInd < baseInd) {
                                currentMarks.preceding.add(textChunks.get(markInd));
                            } else {
                                currentMarks.succeeding.add(textChunks.get(markInd));
                            }

                            foundBaseToAttachTo = true;
                            break;
                        }
                    }
                }

                if (!foundBaseToAttachTo) {
                    toSort.add(textChunks.get(markInd));
                }
            } else {
                toSort.add(textChunks.get(markInd));
            }
        }

        if (rightToLeftRunDirection) {
            Collections.sort(toSort, new TextChunkComparator(new TextChunkLocationComparator(false)));
        } else {
            Collections.sort(toSort);
        }

        textChunks.clear();

        for (TextChunk current : toSort) {
            TextChunkMarks currentMarks = marks.get(current);
            if (currentMarks != null) {
                if (!rightToLeftRunDirection) {
                    for (int j = 0; j < currentMarks.preceding.size(); j++) {
                        textChunks.add(currentMarks.preceding.get(j));
                    }
                } else {
                    for (int j = currentMarks.succeeding.size() - 1; j >= 0; j--) {
                        textChunks.add(currentMarks.succeeding.get(j));
                    }
                }
            }
            textChunks.add(current);
            if (currentMarks != null) {
                if (!rightToLeftRunDirection) {
                    for (int j = 0; j < currentMarks.succeeding.size(); j++) {
                        textChunks.add(currentMarks.succeeding.get(j));
                    }
                } else {
                    for (int j = currentMarks.preceding.size() - 1; j >= 0; j--) {
                        textChunks.add(currentMarks.preceding.get(j));
                    }
                }
            }
        }
    }

    private boolean containsMark(ITextChunkLocation baseLocation, ITextChunkLocation markLocation) {
        return baseLocation.getStartLocation().get(Vector.I1) <= markLocation.getStartLocation().get(Vector.I1) && baseLocation.getEndLocation().get(Vector.I1) >= markLocation.getEndLocation().get(Vector.I1) &&
                Math.abs(baseLocation.distPerpendicular() - markLocation.distPerpendicular()) <= DIACRITICAL_MARKS_ALLOWED_VERTICAL_DEVIATION;
    }

    public interface ITextChunkLocationStrategy {
        ITextChunkLocation createLocation(TextRenderInfo renderInfo, LineSegment baseline);
    }

    public interface ITextChunkLocation extends Comparable<ITextChunkLocation> {
        float distParallelEnd();

        float distParallelStart();

        int distPerpendicular();

        float getCharSpaceWidth();

        Vector getEndLocation();

        Vector getStartLocation();

        int orientationMagnitude();

        boolean sameLine(ITextChunkLocation as);

        float distanceFromEndOf(ITextChunkLocation other);

        boolean isAtWordBoundary(ITextChunkLocation previous);
    }

    /**
     * Represents a chunk of text, it's orientation, and location relative to the orientation vector
     */
    public static class TextChunk implements Comparable<TextChunk> {
        /**
         * the text of the chunk
         */
        protected final String text;
        protected final ITextChunkLocation location;

        public TextChunk(String string, ITextChunkLocation loc) {
            this.text = string;
            this.location = loc;
        }

        /**
         * @return the text captured by this chunk
         */
        public String getText() {
            return text;
        }

        public ITextChunkLocation getLocation() {
            return location;
        }

        /**
         * Compares based on orientation, perpendicular distance, then parallel distance
         *
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        @Override
        public int compareTo(TextChunk rhs) {
            return location.compareTo(rhs.location);
        }

        void printDiagnostics() {
            System.out.println("Text (@" + location.getStartLocation() + " -> " + location.getEndLocation() + "): " + text);
            System.out.println("orientationMagnitude: " + location.orientationMagnitude());
            System.out.println("distPerpendicular: " + location.distPerpendicular());
            System.out.println("distParallel: " + location.distParallelStart());
        }

        boolean sameLine(TextChunk lastChunk) {
            return getLocation().sameLine(lastChunk.getLocation());
        }
    }

    public static class TextChunkLocationDefaultImp implements ITextChunkLocation {
        private static final TextChunkLocationComparator defaultComparator = new TextChunkLocationComparator();

        /**
         * the starting location of the chunk
         */
        private final Vector startLocation;
        /**
         * the ending location of the chunk
         */
        private final Vector endLocation;
        /**
         * unit vector in the orientation of the chunk
         */
        private final Vector orientationVector;
        /**
         * the orientation as a scalar for quick sorting
         */
        private final int orientationMagnitude;
        /**
         * perpendicular distance to the orientation unit vector (i.e. the Y position in an unrotated coordinate system)
         * we round to the nearest integer to handle the fuzziness of comparing floats
         */
        private final int distPerpendicular;
        /**
         * distance of the start of the chunk parallel to the orientation unit vector (i.e. the X position in an unrotated coordinate system)
         */
        private final float distParallelStart;
        /**
         * distance of the end of the chunk parallel to the orientation unit vector (i.e. the X position in an unrotated coordinate system)
         */
        private final float distParallelEnd;
        /**
         * the width of a single space character in the font of the chunk
         */
        private final float charSpaceWidth;

        public TextChunkLocationDefaultImp(Vector startLocation, Vector endLocation, float charSpaceWidth) {
            this.startLocation = startLocation;
            this.endLocation = endLocation;
            this.charSpaceWidth = charSpaceWidth;

            Vector oVector = endLocation.subtract(startLocation);
            if (oVector.length() == 0) {
                oVector = new Vector(1, 0, 0);
            }
            orientationVector = oVector.normalize();
            orientationMagnitude = (int) (Math.atan2(orientationVector.get(Vector.I2), orientationVector.get(Vector.I1)) * 1000);

            // see http://mathworld.wolfram.com/Point-LineDistance2-Dimensional.html
            // the two vectors we are crossing are in the same plane, so the result will be purely
            // in the z-axis (out of plane) direction, so we just take the I3 component of the result
            Vector origin = new Vector(0, 0, 1);
            distPerpendicular = (int) (startLocation.subtract(origin)).cross(orientationVector).get(Vector.I3);

            distParallelStart = orientationVector.dot(startLocation);
            distParallelEnd = orientationVector.dot(endLocation);
        }


        public int orientationMagnitude() {
            return orientationMagnitude;
        }

        public int distPerpendicular() {
            return distPerpendicular;
        }

        public float distParallelStart() {
            return distParallelStart;
        }

        public float distParallelEnd() {
            return distParallelEnd;
        }


        /**
         * @return the start location of the text
         */
        public Vector getStartLocation() {
            return startLocation;
        }

        /**
         * @return the end location of the text
         */
        public Vector getEndLocation() {
            return endLocation;
        }

        /**
         * @return the width of a single space character as rendered by this chunk
         */
        public float getCharSpaceWidth() {
            return charSpaceWidth;
        }


        /**
         * @param as the location to compare to
         * @return true is this location is on the the same line as the other
         */
        public boolean sameLine(ITextChunkLocation as) {
            if (orientationMagnitude() != as.orientationMagnitude()) {
                return false;
            }
            float distPerpendicularDiff = distPerpendicular() - as.distPerpendicular();
            if (distPerpendicularDiff == 0) {
                return true;
            }
            LineSegment mySegment = new LineSegment(startLocation, endLocation);
            LineSegment otherSegment = new LineSegment(as.getStartLocation(), as.getEndLocation());
            return Math.abs(distPerpendicularDiff) <= DIACRITICAL_MARKS_ALLOWED_VERTICAL_DEVIATION && (mySegment.getLength() == 0 || otherSegment.getLength() == 0);
        }

        /**
         * Computes the distance between the end of 'other' and the beginning of this chunk
         * in the direction of this chunk's orientation vector.  Note that it's a bad idea
         * to call this for chunks that aren't on the same line and orientation, but we don't
         * explicitly check for that condition for performance reasons.
         *
         * @param other
         * @return the number of spaces between the end of 'other' and the beginning of this chunk
         */
        public float distanceFromEndOf(ITextChunkLocation other) {
            return distParallelStart() - other.distParallelEnd();
        }

        public boolean isAtWordBoundary(ITextChunkLocation previous) {
            /*
             * Here we handle a very specific case which in PDF may look like:
             * -.232 Tc [( P)-226.2(r)-231.8(e)-230.8(f)-238(a)-238.9(c)-228.9(e)]TJ
             * The font's charSpace width is 0.232 and it's compensated with charSpacing of 0.232.
             * And a resultant TextChunk.charSpaceWidth comes to TextChunk constructor as 0.
             * In this case every chunk is considered as a word boundary and space is added.
             * We should consider charSpaceWidth equal (or close) to zero as a no-space.
             */
            if (getCharSpaceWidth() < 0.1f) {
                return false;
            }

            // In case a text chunk is of zero length, this probably means this is a mark character,
            // and we do not actually want to insert a space in such case
            if (startLocation.equals(endLocation) || previous.getEndLocation().equals(previous.getStartLocation())) {
                return false;
            }

            float dist = distanceFromEndOf(previous);

            return dist < -getCharSpaceWidth() || dist > getCharSpaceWidth() / 2.0f;
        }

        @Override
        public int compareTo(ITextChunkLocation other) {
            return defaultComparator.compare(this, other);
        }
    }

    private static class TextChunkComparator implements Comparator<TextChunk> {
        private Comparator<ITextChunkLocation> locationComparator;

        public TextChunkComparator(Comparator<ITextChunkLocation> locationComparator) {
            this.locationComparator = locationComparator;
        }

        @Override
        public int compare(TextChunk o1, TextChunk o2) {
            return locationComparator.compare(o1.location, o2.location);
        }
    }

    private static class TextChunkLocationComparator implements Comparator<ITextChunkLocation> {
        private boolean leftToRight = true;

        public TextChunkLocationComparator() {
        }

        public TextChunkLocationComparator(boolean leftToRight) {
            this.leftToRight = leftToRight;
        }

        @Override
        public int compare(ITextChunkLocation first, ITextChunkLocation second) {
            if (first == second) return 0; // not really needed, but just in case

            int result;
            result = Integer.compare(first.orientationMagnitude(), second.orientationMagnitude());
            if (result != 0) {
                return result;
            }

            int distPerpendicularDiff = first.distPerpendicular() - second.distPerpendicular();
            if (distPerpendicularDiff != 0) {
                return distPerpendicularDiff;
            }

            return leftToRight ? Float.compare(first.distParallelStart(), second.distParallelStart()) :
                    -Float.compare(first.distParallelEnd(), second.distParallelEnd());
        }
    }

    private static class TextChunkMarks {
        List<TextChunk> preceding = new ArrayList<>();
        List<TextChunk> succeeding = new ArrayList<>();
    }

}
