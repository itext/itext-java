package com.itextpdf.kernel.parser;

import com.itextpdf.kernel.geom.LineSegment;
import com.itextpdf.kernel.geom.Matrix;
import com.itextpdf.kernel.geom.Vector;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class LocationTextExtractionStrategy implements TextExtractionStrategy {

    /**
     * set to true for debugging
     */
    private static boolean DUMP_STATE = false;

    /**
     * a summary of all found text
     */
    private final List<TextChunk> locationalResult = new ArrayList<>();

    private final TextChunkLocationStrategy tclStrat;

    private boolean useActualText = false;

    /**
     * Creates a new text extraction renderer.
     */
    public LocationTextExtractionStrategy() {
        this(new TextChunkLocationStrategy() {
            public TextChunkLocation createLocation(TextRenderInfo renderInfo, LineSegment baseline) {
                return new TextChunkLocationDefaultImp(baseline.getStartPoint(), baseline.getEndPoint(), renderInfo.getSingleSpaceWidth());
            }
        });
    }

    /**
     * Creates a new text extraction renderer, with a custom strategy for
     * creating new TextChunkLocation objects based on the input of the
     * TextRenderInfo.
     * @param strat the custom strategy
     */
    public LocationTextExtractionStrategy(TextChunkLocationStrategy strat) {
        tclStrat = strat;
    }

    /**
     * Changes the behavior of text extraction so that if the parameter is set to {@code true},
     * /ActualText marked content property will be used instead of raw decoded bytes.
     * Beware: the logic is not stable yet.
     * @param useActualText true to use /ActualText, false otherwise
     * @return this object
     */
    public LocationTextExtractionStrategy setUseActualText(boolean useActualText) {
        this.useActualText = useActualText;
        return this;
    }

    /**
     * Gets the value of the property which determines if /ActualText will be used when extracting
     * the text
     * @return true if /ActualText value is used, false otherwise
     */
    public boolean isUseActualText() {
        return useActualText;
    }

    @Override
    public void eventOccurred(EventData data, EventType type) {
        if (type.equals(EventType.RENDER_TEXT)) {
            TextRenderInfo renderInfo = (TextRenderInfo) data;
            LineSegment segment = renderInfo.getBaseline();
            if (renderInfo.getRise() != 0) {
                // remove the rise from the baseline - we do this because the text from a super/subscript render operations should probably be considered as part of the baseline of the text the super/sub is relative to
                Matrix riseOffsetTransform = new Matrix(0, -renderInfo.getRise());
                segment = segment.transformBy(riseOffsetTransform);
            }
            TextChunk tc = new TextChunk(useActualText && renderInfo.getActualText() != null ? renderInfo.getActualText() : renderInfo.getText(),
                    tclStrat.createLocation(renderInfo, segment));
            locationalResult.add(tc);
        }
    }

    @Override
    public Set<EventType> getSupportedEvents() {
        return null;
    }

    @Override
    public String getResultantText() {
        if (DUMP_STATE) dumpState();

        List<TextChunk> filteredTextChunks = new ArrayList<>(locationalResult);
        Collections.sort(filteredTextChunks);

        StringBuilder sb = new StringBuilder();
        TextChunk lastChunk = null;
        for (TextChunk chunk : filteredTextChunks) {

            if (lastChunk == null) {
                sb.append(chunk.text);
            } else {
                if (chunk.sameLine(lastChunk)) {
                    // we only insert a blank space if the trailing character of the previous string wasn't a space, and the leading character of the current string isn't a space
                    if (isChunkAtWordBoundary(chunk, lastChunk) && !startsWithSpace(chunk.text) && !endsWithSpace(lastChunk.text))
                        sb.append(' ');

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

    public interface TextChunkLocationStrategy {
        TextChunkLocation createLocation(TextRenderInfo renderInfo, LineSegment baseline);
    }

    public interface TextChunkLocation extends Comparable<TextChunkLocation> {
        float distParallelEnd();

        float distParallelStart();

        int distPerpendicular();

        float getCharSpaceWidth();

        Vector getEndLocation();

        Vector getStartLocation();

        int orientationMagnitude();

        boolean sameLine(TextChunkLocation as);

        float distanceFromEndOf(TextChunkLocation other);

        boolean isAtWordBoundary(TextChunkLocation previous);
    }

    /**
     * Represents a chunk of text, it's orientation, and location relative to the orientation vector
     */
    public static class TextChunk implements Comparable<TextChunk> {
        /** the text of the chunk */
        private final String text;
        private final TextChunkLocation location;

        public TextChunk(String string, TextChunkLocation loc) {
            this.text = string;
            this.location = loc;
        }

        /**
         * @return the text captured by this chunk
         */
        public String getText(){
            return text;
        }

        public TextChunkLocation getLocation() {
            return location;
        }

        private void printDiagnostics(){
            System.out.println("Text (@" + location.getStartLocation() + " -> " + location.getEndLocation() + "): " + text);
            System.out.println("orientationMagnitude: " + location.orientationMagnitude());
            System.out.println("distPerpendicular: " + location.distPerpendicular());
            System.out.println("distParallel: " + location.distParallelStart());
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

        private boolean sameLine(TextChunk lastChunk) {
            return getLocation().sameLine(lastChunk.getLocation());
        }
    }

    private static class TextChunkLocationDefaultImp implements TextChunkLocation {

        /** the starting location of the chunk */
        private final Vector startLocation;
        /** the ending location of the chunk */
        private final Vector endLocation;
        /** unit vector in the orientation of the chunk */
        private final Vector orientationVector;
        /** the orientation as a scalar for quick sorting */
        private final int orientationMagnitude;
        /** perpendicular distance to the orientation unit vector (i.e. the Y position in an unrotated coordinate system)
         * we round to the nearest integer to handle the fuzziness of comparing floats */
        private final int distPerpendicular;
        /** distance of the start of the chunk parallel to the orientation unit vector (i.e. the X position in an unrotated coordinate system) */
        private final float distParallelStart;
        /** distance of the end of the chunk parallel to the orientation unit vector (i.e. the X position in an unrotated coordinate system) */
        private final float distParallelEnd;
        /** the width of a single space character in the font of the chunk */
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
            orientationMagnitude = (int)(Math.atan2(orientationVector.get(Vector.I2), orientationVector.get(Vector.I1))*1000);

            // see http://mathworld.wolfram.com/Point-LineDistance2-Dimensional.html
            // the two vectors we are crossing are in the same plane, so the result will be purely
            // in the z-axis (out of plane) direction, so we just take the I3 component of the result
            Vector origin = new Vector(0,0,1);
            distPerpendicular = (int)(startLocation.subtract(origin)).cross(orientationVector).get(Vector.I3);

            distParallelStart = orientationVector.dot(startLocation);
            distParallelEnd = orientationVector.dot(endLocation);
        }


        public int orientationMagnitude() {return orientationMagnitude;}
        public int distPerpendicular() {return distPerpendicular;}
        public float distParallelStart() {return distParallelStart; }
        public float distParallelEnd() { return distParallelEnd;}


        /**
         * @return the start location of the text
         */
        public Vector getStartLocation(){
            return startLocation;
        }

        /**
         * @return the end location of the text
         */
        public Vector getEndLocation(){
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
        public boolean sameLine(TextChunkLocation as){
            return orientationMagnitude() == as.orientationMagnitude() && distPerpendicular() == as.distPerpendicular();
        }

        /**
         * Computes the distance between the end of 'other' and the beginning of this chunk
         * in the direction of this chunk's orientation vector.  Note that it's a bad idea
         * to call this for chunks that aren't on the same line and orientation, but we don't
         * explicitly check for that condition for performance reasons.
         * @param other
         * @return the number of spaces between the end of 'other' and the beginning of this chunk
         */
        public float distanceFromEndOf(TextChunkLocation other){
            float distance = distParallelStart() - other.distParallelEnd();
            return distance;
        }

        public boolean isAtWordBoundary(TextChunkLocation previous){
            /**
             * Here we handle a very specific case which in PDF may look like:
             * -.232 Tc [( P)-226.2(r)-231.8(e)-230.8(f)-238(a)-238.9(c)-228.9(e)]TJ
             * The font's charSpace width is 0.232 and it's compensated with charSpacing of 0.232.
             * And a resultant TextChunk.charSpaceWidth comes to TextChunk constructor as 0.
             * In this case every chunk is considered as a word boundary and space is added.
             * We should consider charSpaceWidth equal (or close) to zero as a no-space.
             */
            if (getCharSpaceWidth() < 0.1f)
                return false;

            float dist = distanceFromEndOf(previous);

            return dist < -getCharSpaceWidth() || dist > getCharSpaceWidth()/2.0f;
        }

        @Override
        public int compareTo(TextChunkLocation other) {
            if (this == other) return 0; // not really needed, but just in case

            int rslt;
            rslt = Integer.compare(orientationMagnitude(), other.orientationMagnitude());
            if (rslt != 0) return rslt;

            rslt = Integer.compare(distPerpendicular(), other.distPerpendicular());
            if (rslt != 0) return rslt;

            return Float.compare(distParallelStart(), other.distParallelStart());
        }
    }

}
