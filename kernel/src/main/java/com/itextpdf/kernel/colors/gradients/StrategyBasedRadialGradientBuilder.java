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
package com.itextpdf.kernel.colors.gradients;

import com.itextpdf.commons.datastructures.Tuple2;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.NoninvertibleTransformException;
import com.itextpdf.kernel.geom.Point;
import com.itextpdf.kernel.geom.Rectangle;

/**
 * The radial gradient builder with automatic end circle (or ellipse) evaluation for the target filled
 * area based on configured strategy. Start center would be equal to end center, radius would be 0.
 */
public class StrategyBasedRadialGradientBuilder extends AbstractRadialGradientBuilder {

    // center point
    private boolean leftToRight = true;
    private double xOffset = 0.5d;
    private boolean xOffsetRelative = true;
    private boolean bottomToTop = false;
    private double yOffset = 0.5d;
    private boolean yOffsetRelative = true;

    // radius
    // manual radius definition based on target bbox
    private double xRadius = 0d;
    private boolean xRadiusRelative = false;
    private double yRadius = 0d;
    private boolean yRadiusRelative = false;
    // from center based radius definition
    private GradientStrategy gradientStrategy = GradientStrategy.FARTHEST_CORNER;
    private boolean isCircular = false;
    // whether from center based strategy to use or manual
    private boolean isFromCenterStrategy = true;


    /**
     * Constructs the builder instance
     */
    public StrategyBasedRadialGradientBuilder() {
        // empty constructor
    }

    /**
     * Specifies the strategy to determine center point.
     *
     * @param leftToRight     if {@code true} then X offset would be added to the left side X value,
     *                        if {@code false} then X offset would be subtracted from the right side X value
     * @param xOffset         the offset value to add/subtract for X
     * @param xOffsetRelative if {@code true} then X offset treated as relative to bbox width,
     *                        if {@code false} then X offset treated as absolute
     * @param bottomToTop     if {@code true} then Y offset would be subtracted from the top side Y value,
     *                        if {@code false} then Y offset would be added to the bottom side Y value
     * @param yOffset         the offset value to add/subtract for Y
     * @param yOffsetRelative if {@code true} then Y offset treated as relative to bbox height,
     *                        if {@code false} then Y offset treated as absolute
     *
     * @return the current builder instance
     */
    public StrategyBasedRadialGradientBuilder setCenterStrategy(
            boolean leftToRight, double xOffset, boolean xOffsetRelative,
            boolean bottomToTop, double yOffset, boolean yOffsetRelative) {
        this.leftToRight = leftToRight;
        this.xOffset = xOffset;
        this.xOffsetRelative = xOffsetRelative;
        this.bottomToTop = bottomToTop;
        this.yOffset = yOffset;
        this.yOffsetRelative = yOffsetRelative;
        return this;
    }

    /**
     * Set the strategy to calculate radius based on bounding box dimensions.
     *
     * @param xRadius         X radius value
     * @param xRadiusRelative if {@code true} then X radius treated as relative to bbox width,
     *                        if {@code false} then X radius treated as absolute
     * @param yRadius         Y radius value
     * @param yRadiusRelative if {@code true} then Y radius treated as relative to bbox height,
     *                        if {@code false} then Y radius treated as absolute
     *
     * @return the current builder instance
     */
    public StrategyBasedRadialGradientBuilder setRadiusRelativeToBoundingBoxSize(
            double xRadius, boolean xRadiusRelative,
            double yRadius, boolean yRadiusRelative) {
        this.xRadius = xRadius;
        this.xRadiusRelative = xRadiusRelative;
        this.yRadius = yRadius;
        this.yRadiusRelative = yRadiusRelative;

        this.isFromCenterStrategy = false;
        return this;
    }

    /**
     * Set the strategy to predefined one from gradient center.
     *
     * @param isCircular       whether the gradient strategy to be applied for circular or elliptical gradient spreading
     * @param gradientStrategy strategy to be used for calculating target radius from gradient center
     *
     * @return the current builder instance
     */
    public StrategyBasedRadialGradientBuilder setRadiusFromCenterStrategy(boolean isCircular,
            GradientStrategy gradientStrategy) {
        this.isCircular = isCircular;
        this.gradientStrategy = gradientStrategy != null ? gradientStrategy : GradientStrategy.FARTHEST_CORNER;

        this.isFromCenterStrategy = true;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Tuple2<RadialGradientPoint[], AffineTransform> getGradientVectorWithTransform(
            Rectangle targetBoundingBox, AffineTransform contextTransform) {
        Tuple2<RadialGradientPoint, AffineTransform> coordsWithTransform = evaluateCoordAndTransform(targetBoundingBox);
        RadialGradientPoint target = coordsWithTransform.getFirst();

        RadialGradientPoint[] vector = target == null
                ? null
                : new RadialGradientPoint[] {new RadialGradientPoint(target.getCenter(), 0), target};
        return new Tuple2<RadialGradientPoint[], AffineTransform>(vector, coordsWithTransform.getSecond());
    }

    private Tuple2<RadialGradientPoint, AffineTransform> evaluateCoordAndTransform(Rectangle targetBoundingBox) {
        if (targetBoundingBox == null) {
            return new Tuple2<RadialGradientPoint, AffineTransform>(null, null);
        }
        double cX = evaluateValueOnSegment(targetBoundingBox.getLeft(), targetBoundingBox.getRight(),
                leftToRight, xOffset, xOffsetRelative);
        double cY = evaluateValueOnSegment(targetBoundingBox.getBottom(), targetBoundingBox.getTop(),
                bottomToTop, yOffset, yOffsetRelative);
        Point center = new Point(cX, cY);

        double[] radius = isFromCenterStrategy
                ? getRadiusForCenterBasedStrategy(targetBoundingBox, center)
                : getRadiusForManualStrategy(targetBoundingBox);

        double rX = radius[0];
        double rY = radius[1];
        // if any of radii is 0 or negative, then normalize both to 0
        if (isZero(rX) || isZero(rY)) {
            rX = 0d;
            rY = 0d;
        }

        AffineTransform transform = null;
        if (!isZero(rY - rX)) {
            // ellipse case
            transform = new AffineTransform();
            transform.scale(1.0d, rY / rX);

            try {
                center = transform.inverseTransform(center, null);
            } catch (NoninvertibleTransformException e) {
                throw new PdfException(e.getMessage(), e);
            }
        }

        return new Tuple2<RadialGradientPoint, AffineTransform>(new RadialGradientPoint(center, rX), transform);
    }

    private double[] getRadiusForManualStrategy(Rectangle targetBoundingBox) {
        double rX = evaluateValueOnSegment(0, targetBoundingBox.getWidth(), true, xRadius, xRadiusRelative);
        double rY = evaluateValueOnSegment(0, targetBoundingBox.getHeight(), true, yRadius, yRadiusRelative);

        return new double[] {rX, rY};
    }

    private double[] getRadiusForCenterBasedStrategy(Rectangle targetBoundingBox, Point center) {
        switch (gradientStrategy) {
            case CLOSEST_SIDE:
                return evaluateClosestSideRadius(targetBoundingBox, center);
            case CLOSEST_CORNER:
                return evaluateClosestCornerRadius(targetBoundingBox, center);
            case FARTHEST_SIDE:
                return evaluateFarthestSideRadius(targetBoundingBox, center);
            // default case is equal to FARTHEST_CORNER
            case FARTHEST_CORNER:
            default:
                return evaluateFarthestCornerRadius(targetBoundingBox, center);
        }
    }

    private double[] evaluateClosestSideRadius(Rectangle targetBoundingBox, Point center) {
        double leftDist = Math.abs(center.getX() - targetBoundingBox.getLeft());
        double rightDist = Math.abs(center.getX() - targetBoundingBox.getRight());
        double bottomDist = Math.abs(center.getY() - targetBoundingBox.getBottom());
        double topDist = Math.abs(center.getY() - targetBoundingBox.getTop());

        double horizontalClosest = Math.min(leftDist, rightDist);
        double verticalClosest = Math.min(bottomDist, topDist);

        if (isCircular) {
            double closestDist = Math.min(horizontalClosest, verticalClosest);
            return new double[] {closestDist, closestDist};
        } else {
            return new double[] {horizontalClosest, verticalClosest};
        }
    }

    private double[] evaluateFarthestSideRadius(Rectangle targetBoundingBox, Point center) {
        double leftDist = Math.abs(center.getX() - targetBoundingBox.getLeft());
        double rightDist = Math.abs(center.getX() - targetBoundingBox.getRight());
        double bottomDist = Math.abs(center.getY() - targetBoundingBox.getBottom());
        double topDist = Math.abs(center.getY() - targetBoundingBox.getTop());

        double horizontalFarthest = Math.max(leftDist, rightDist);
        double verticalFarthest = Math.max(bottomDist, topDist);

        if (isCircular) {
            double farthestDist = Math.max(horizontalFarthest, verticalFarthest);
            return new double[] {farthestDist, farthestDist};
        } else {
            return new double[] {horizontalFarthest, verticalFarthest};
        }
    }

    private double[] evaluateClosestCornerRadius(Rectangle targetBoundingBox, Point center) {
        Point[] vertices = targetBoundingBox.toPointsArray();
        Point closestCorner = vertices[0];
        for (int i = 1; i < vertices.length; ++i) {
            if (center.distance(closestCorner) > center.distance(vertices[i])) {
                closestCorner = vertices[i];
            }
        }

        return evaluateRadiusForCorner(center, closestCorner);
    }

    private double[] evaluateFarthestCornerRadius(Rectangle targetBoundingBox, Point center) {
        Point[] vertices = targetBoundingBox.toPointsArray();
        Point farthestCorner = vertices[0];
        for (int i = 1; i < vertices.length; ++i) {
            if (center.distance(farthestCorner) < center.distance(vertices[i])) {
                farthestCorner = vertices[i];
            }
        }

        return evaluateRadiusForCorner(center, farthestCorner);
    }

    private double[] evaluateRadiusForCorner(Point center, Point corner) {
        if (isCircular) {
            double distance = center.distance(corner);
            return new double[] {distance, distance};
        } else {
            double xDiff = Math.abs(corner.getX() - center.getX());
            double yDiff = Math.abs(corner.getY() - center.getY());
            double aspectRatio = yDiff / xDiff;
            double xR = Math.sqrt(xDiff * xDiff + (yDiff / aspectRatio) * (yDiff / aspectRatio));
            double yR = aspectRatio * xR;
            return new double[] {xR, yR};
        }
    }

    private static double evaluateValueOnSegment(double segmentStart, double segmentEnd,
            boolean isFromStart, double offset, boolean offsetRelative) {
        double absoluteOffset = offsetRelative
                ? (segmentEnd - segmentStart) * offset
                : offset;
        return isFromStart
                ? segmentStart + absoluteOffset
                : segmentEnd - absoluteOffset;
    }

    /**
     * Specifies the predefined strategies
     */
    public enum GradientStrategy {
        /**
         * Circle radius equal to the closest side distance from center,
         * ellipse radii equal to the closest horizontal and vertical sides from center.
         */
        CLOSEST_SIDE,
        /**
         * Circle radius equal to the closest corner distance from center,
         * ellipse passes through the closest corner from center
         * while radii aspect ratio is the same as for CLOSEST_SIDE strategy for ellipse.
         */
        CLOSEST_CORNER,
        /**
         * Circle radius equal to the farthest side distance from center,
         * ellipse radii equal to the farthest horizontal and vertical sides from center.
         */
        FARTHEST_SIDE,
        /**
         * Circle radius equal to the farthest corner distance from center,
         * ellipse passes through the farthest corner from center
         * while radii aspect ratio is the same as for FARTHEST_SIDE strategy for ellipse.
         */
        FARTHEST_CORNER
    }
}
