/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
    Authors: iText Software.

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

import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.Point;
import com.itextpdf.kernel.geom.Rectangle;

/**
 * The linear gradient builder with automatic coordinates vector evaluation for the target filled
 * area based on configured strategy
 */
public class StrategyBasedLinearGradientBuilder extends AbstractLinearGradientBuilder {

    private double rotateVectorAngle = 0d;
    private GradientStrategy gradientStrategy = GradientStrategy.TO_BOTTOM;
    private boolean isCentralRotationAngleStrategy = false;

    /**
     * Create a new instance of the builder
     */
    public StrategyBasedLinearGradientBuilder() {
    }

    /**
     * Set the strategy to use the minimal coordinates vector that passes through the central point
     * of the target rectangle area, rotated by the specified amount of radians counter clockwise
     * and covers the area to be filled. Zero angle corresponds to the vector from bottom to top.
     *
     * @param radians the radians value to rotate the coordinates vector
     * @return the current builder instance
     */
    public StrategyBasedLinearGradientBuilder setGradientDirectionAsCentralRotationAngle(double radians) {
        this.rotateVectorAngle = radians;
        this.isCentralRotationAngleStrategy = true;
        return this;
    }

    /**
     * Set the strategy to predefined one
     *
     * @param gradientStrategy the strategy to set
     * @return the current builder instance
     */
    public StrategyBasedLinearGradientBuilder setGradientDirectionAsStrategy(GradientStrategy gradientStrategy) {
        this.gradientStrategy = gradientStrategy != null ? gradientStrategy : GradientStrategy.TO_BOTTOM;
        this.isCentralRotationAngleStrategy = false;
        return this;
    }

    /**
     * Get the last set rotate vector angle
     *
     * @return the last set rotate vector angle
     */
    public double getRotateVectorAngle() {
        return rotateVectorAngle;
    }

    /**
     * Get the last set predefined strategy
     *
     * @return the last set predefined strategy
     */
    public GradientStrategy getGradientStrategy() {
        return gradientStrategy;
    }

    /**
     * Is the central rotation angle strategy was set last
     *
     * @return {@code true} if the last strategy that has been set is a custom rotation angle
     */
    public boolean isCentralRotationAngleStrategy() {
        return isCentralRotationAngleStrategy;
    }

    @Override
    protected Point[] getGradientVector(Rectangle targetBoundingBox, AffineTransform contextTransform) {
        if (targetBoundingBox == null) {
            return null;
        }
        return this.isCentralRotationAngleStrategy
                ? buildCentralRotationCoordinates(targetBoundingBox, this.rotateVectorAngle)
                : buildCoordinatesWithGradientStrategy(targetBoundingBox, this.gradientStrategy);
    }

    private static Point[] buildCoordinatesWithGradientStrategy(Rectangle targetBoundingBox,
            GradientStrategy gradientStrategy) {
        double xCenter = targetBoundingBox.getX() + targetBoundingBox.getWidth() / 2;
        double yCenter = targetBoundingBox.getY() + targetBoundingBox.getHeight() / 2;
        switch (gradientStrategy) {
            case TO_TOP:
                return createCoordinates(xCenter, targetBoundingBox.getBottom(), xCenter,
                        targetBoundingBox.getTop());
            case TO_LEFT:
                return createCoordinates(targetBoundingBox.getRight(), yCenter, targetBoundingBox.getLeft(),
                        yCenter);
            case TO_RIGHT:
                return createCoordinates(targetBoundingBox.getLeft(), yCenter, targetBoundingBox.getRight(),
                        yCenter);
            case TO_TOP_LEFT:
                return buildToCornerCoordinates(targetBoundingBox,
                        new Point(targetBoundingBox.getRight(), targetBoundingBox.getTop()));
            case TO_TOP_RIGHT:
                return buildToCornerCoordinates(targetBoundingBox,
                        new Point(targetBoundingBox.getRight(), targetBoundingBox.getBottom()));
            case TO_BOTTOM_RIGHT:
                return buildToCornerCoordinates(targetBoundingBox,
                        new Point(targetBoundingBox.getLeft(), targetBoundingBox.getBottom()));
            case TO_BOTTOM_LEFT:
                return buildToCornerCoordinates(targetBoundingBox,
                        new Point(targetBoundingBox.getLeft(), targetBoundingBox.getTop()));
            // default case is equal to TO_BOTTOM
            case TO_BOTTOM:
            default:
                return createCoordinates(xCenter, targetBoundingBox.getTop(),
                        xCenter, targetBoundingBox.getBottom());
        }
    }

    private static Point[] buildCentralRotationCoordinates(Rectangle targetBoundingBox, double angle) {
        double xCenter = targetBoundingBox.getX() + targetBoundingBox.getWidth() / 2;
        AffineTransform rotateInstance = AffineTransform.getRotateInstance(angle, xCenter,
                targetBoundingBox.getY() + targetBoundingBox.getHeight() / 2);
        return buildCoordinates(targetBoundingBox, rotateInstance);
    }

    private static Point[] buildToCornerCoordinates(Rectangle targetBoundingBox, Point gradientCenterLineRightCorner) {
        AffineTransform transform = buildToCornerTransform(
                new Point(targetBoundingBox.getX() + targetBoundingBox.getWidth() / 2,
                        targetBoundingBox.getY() + targetBoundingBox.getHeight() / 2),
                gradientCenterLineRightCorner);
        return buildCoordinates(targetBoundingBox, transform);
    }

    private static AffineTransform buildToCornerTransform(Point center, Point gradientCenterLineRightCorner) {
        double scale = 1d / (center.distance(gradientCenterLineRightCorner));
        double sin = (gradientCenterLineRightCorner.getY() - center.getY()) * scale;
        double cos = (gradientCenterLineRightCorner.getX() - center.getX()) * scale;
        if (Math.abs(cos) < ZERO_EPSILON) {
            cos = 0d;
            sin = sin > 0d ? 1d : -1d;
        } else if (Math.abs(sin) < ZERO_EPSILON) {
            sin = 0d;
            cos = cos > 0d ? 1d : -1d;
        }
        double m02 = center.getX() * (1d - cos) + center.getY() * sin;
        double m12 = center.getY() * (1d - cos) - center.getX() * sin;
        return new AffineTransform(cos, sin, -sin, cos, m02, m12);
    }

    private static Point[] buildCoordinates(Rectangle targetBoundingBox, AffineTransform transformation) {
        double xCenter = targetBoundingBox.getX() + targetBoundingBox.getWidth() / 2;
        Point start = transformation.transform(new Point(xCenter, targetBoundingBox.getBottom()), null);
        Point end = transformation.transform(new Point(xCenter, targetBoundingBox.getTop()), null);
        Point[] baseVector = new Point[] {start, end};
        double[] targetDomain = evaluateCoveringDomain(baseVector, targetBoundingBox);
        return createCoordinatesForNewDomain(targetDomain, baseVector);
    }

    private static Point[] createCoordinates(double x1, double y1, double x2, double y2) {
        return new Point[] {new Point(x1, y1), new Point(x2, y2)};
    }

    /**
     * Specifies the predefined strategies
     */
    public enum GradientStrategy {
        /**
         * Gradient vector from the middle of the top side to the middle of the bottom side
         */
        TO_BOTTOM,
        /**
         * Evaluates the gradient vector in such way that the first color would be painted
         * at the top right corner, the last one - at the bottom left corner and the middle color
         * line would pass through left corners
         */
        TO_BOTTOM_LEFT,
        /**
         * Evaluates the gradient vector in such way that the first color would be painted
         * at the top left corner, the last one - at the bottom right corner and the middle color
         * line would pass through left corners
         */
        TO_BOTTOM_RIGHT,
        /**
         * Gradient vector from the middle of the right side to the middle of the left side
         */
        TO_LEFT,
        /**
         * Gradient vector from the middle of the left side to the middle of the right side
         */
        TO_RIGHT,
        /**
         * Gradient vector from the middle of the bottom side to the middle of the top side
         */
        TO_TOP,
        /**
         * Evaluates the gradient vector in such way that the first color would be painted
         * at the bottom right corner, the last one - at the top left corner and the middle color
         * line would pass through left corners
         */
        TO_TOP_LEFT,
        /**
         * Evaluates the gradient vector in such way that the first color would be painted
         * at the bottom left corner, the last one - at the top right corner and the middle color
         * line would pass through left corners
         */
        TO_TOP_RIGHT
    }
}
