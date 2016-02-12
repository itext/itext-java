package com.itextpdf.kernel.geom;

/**
 * Represents a line segment in a particular coordinate system.  This class is immutable.
 */
public class LineSegment {

    /** Start vector of the segment. */
    private final Vector startPoint;
    /** End vector of the segment. */
    private final Vector endPoint;

    /**
     * Creates a new line segment.
     * @param startPoint the start point of a line segment.
     * @param endPoint the end point of a line segment.
     */
    public LineSegment(Vector startPoint, Vector endPoint) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
    }

    /**
     * @return the start point
     */
    public Vector getStartPoint() {
        return startPoint;
    }

    /**
     * @return the end point
     */
    public Vector getEndPoint() {
        return endPoint;
    }

    /**
     * @return the length of this line segment
     * @since 5.0.2
     */
    public float getLength(){
        return endPoint.subtract(startPoint).length();
    }

    /**
     * Computes the bounding rectangle for this line segment.  The rectangle has a rotation 0 degrees
     * with respect to the coordinate system that the line system is in.  For example, if a line segment
     * is 5 unit long and sits at a 37 degree angle from horizontal, the bounding rectangle will have
     * origin of the lower left hand end point of the segment, with width = 4 and height = 3.
     * @return the bounding rectangle
     * @since 5.0.2
     */
    public Rectangle getBoundingRectange(){
        float x1 = getStartPoint().get(Vector.I1);
        float y1 = getStartPoint().get(Vector.I2);
        float x2 = getEndPoint().get(Vector.I1);
        float y2 = getEndPoint().get(Vector.I2);
        return new Rectangle(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2-x1), Math.abs(y2-y1));
    }

    /**
     * Transforms the segment by the specified matrix
     * @param m the matrix for the transformation
     * @return the transformed segment
     */
    public LineSegment transformBy(Matrix m){
        Vector newStart = startPoint.cross(m);
        Vector newEnd = endPoint.cross(m);
        return new LineSegment(newStart, newEnd);
    }

    /**
     * Checks if a segment contains another segment in itself
     * @param other a segment to be checked
     * @return true if this segment contains other one, false otherwise
     */
    public boolean containsSegment(LineSegment other) {
        return other != null && containsPoint(other.startPoint) && containsPoint(other.endPoint);

    }

    /**
     * Checks if a segment contains a given point in itself
     * @param point a point to be checked
     * @return true if this segment contains given point, false otherwise
     */
    public boolean containsPoint(Vector point) {
        if (point == null) {
            return false;
        }

        Vector diff1 = point.subtract(startPoint);
        if (diff1.get(0) < 0 || diff1.get(1) < 0 || diff1.get(2) < 0) {
            return false;
        }

        Vector diff2 = endPoint.subtract(point);
        if (diff2.get(0) < 0 || diff2.get(1) < 0 || diff2.get(2) < 0) {
            return false;
        }

        return true;
    }
}
