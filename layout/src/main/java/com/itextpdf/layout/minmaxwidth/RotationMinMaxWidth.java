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
package com.itextpdf.layout.minmaxwidth;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.renderer.AbstractRenderer;

/**
 * Class for min-max-width of rotated elements.
 * Also contains heuristic methods for it calculation based on the assumption that area of element stays the same
 * when we try to layout it with different available width (available width is between min-width and max-width).
 */
public class RotationMinMaxWidth extends MinMaxWidth {

    private double minWidthOrigin;
    private double maxWidthOrigin;
    private double minWidthHeight;
    private double maxWidthHeight;

    /**
     * Create new instance
     *
     * @param minWidth min-width of rotated element
     * @param maxWidth max-width of rotated element
     * @param minWidthOrigin the width of not rotated element, that will have min-width after rotation
     * @param maxWidthOrigin the width of not rotated element, that will have max-width after rotation
     * @param minWidthHeight the height of rotated element, that have min-width as its rotated width
     * @param maxWidthHeight the height of rotated element, that have min-width as its rotated width
     */
    public RotationMinMaxWidth(double minWidth, double maxWidth, double minWidthOrigin, double maxWidthOrigin, double minWidthHeight, double maxWidthHeight) {
        super((float) minWidth, (float) maxWidth, 0);
        this.maxWidthOrigin = maxWidthOrigin;
        this.minWidthOrigin = minWidthOrigin;
        this.minWidthHeight = minWidthHeight;
        this.maxWidthHeight = maxWidthHeight;
    }

    public double getMinWidthOrigin() {
        return minWidthOrigin;
    }

    public double getMaxWidthOrigin() {
        return maxWidthOrigin;
    }

    public double getMinWidthHeight() {
        return minWidthHeight;
    }

    public double getMaxWidthHeight() {
        return maxWidthHeight;
    }

    /**
     * Heuristic method, based on the assumption that area of element stays the same, when we try to
     * layout it with different available width (available width is between min-width and max-width).
     *
     * @param angle rotation angle in radians
     * @param area the constant area
     * @param elementMinMaxWidth NOT rotated element min-max-width
     * @return possible min-max-width of element after rotation
     */
    public static RotationMinMaxWidth calculate(double angle, double area, MinMaxWidth elementMinMaxWidth) {
        WidthFunction function = new WidthFunction(angle, area);
        return calculate(function, elementMinMaxWidth.getMinWidth(), elementMinMaxWidth.getMaxWidth());
    }

    /**
     * Heuristic method, based on the assumption that area of element stays the same, when we try to
     * layout it with different available width (available width is between min-width and max-width).
     *
     * @param angle rotation angle in radians
     * @param area the constant area
     * @param elementMinMaxWidth NOT rotated element min-max-width
     * @param availableWidth the maximum width of area the element will occupy after rotation.
     * @return possible min-max-width of element after rotation
     */
    public static RotationMinMaxWidth calculate(double angle, double area, MinMaxWidth elementMinMaxWidth, double availableWidth) {
        WidthFunction function = new WidthFunction(angle, area);
        WidthFunction.Interval validArguments = function.getValidOriginalWidths(availableWidth);
        if (validArguments == null) {
            return null;
        }
        double xMin = Math.max(elementMinMaxWidth.getMinWidth(), validArguments.getMin());
        double xMax = Math.min(elementMinMaxWidth.getMaxWidth(), validArguments.getMax());

        if (xMax < xMin) {
            //Initially the null was returned in this case, but this result in old layout logic that looks worse in most cases.
            //The difference between min and max is not that big and not critical.
            double rotatedWidth = function.getRotatedWidth(xMin);
            double rotatedHeight = function.getRotatedHeight(xMin);
            return new RotationMinMaxWidth(rotatedWidth, rotatedWidth, xMin, xMin, rotatedHeight, rotatedHeight);
        }
        return calculate(function, xMin, xMax);
    }

    /**
     * Utility method for calculating rotated width of area in a similar way to other calculations in this class.
     *
     * @param area the initial area
     * @param angle the rotation angle in radians
     * @return width of rotated area
     */
    public static double calculateRotatedWidth(Rectangle area, double angle) {
        return  area.getWidth() * cos(angle) + area.getHeight() * sin(angle);
    }

    /**
     * This method use derivative of function defined on interval: [xMin, xMax] to find its local minimum and maximum.
     * It also calculate other handy values needed for the creation of {@link RotationMinMaxWidth}.
     *
     * @param func the {@link WidthFunction#getRotatedWidth(double)} of this instance is used as analysed function
     * @param xMin the smallest possible value of function argument
     * @param xMax the biggest possible value of function argument
     * @return the calculated {@link RotationMinMaxWidth}
     */
    private static RotationMinMaxWidth calculate(WidthFunction func, double xMin, double xMax) {
        double minWidthOrigin;
        double maxWidthOrigin;

        //Derivative sign change point
        double x0 = func.getWidthDerivativeZeroPoint();

        //The point x0 may be in three different positions in relation to function interval.
        if (x0 < xMin) {
            //The function is decreasing in this case on whole interval so the local mim and max are on interval borders
            minWidthOrigin = xMin;
            maxWidthOrigin = xMax;
        }
        else if (x0 > xMax) {
            //The function is increasing in this case on whole interval so the local mim and max are on interval borders
            minWidthOrigin = xMax;
            maxWidthOrigin = xMin;
        }
        else {
            //The function derivative changes its sign from negative to positive on function interval in point x0,
            //so its local min is x0, and its local maximum is on one of interval borders
            minWidthOrigin = x0;
            maxWidthOrigin = func.getRotatedWidth(xMax) > func.getRotatedWidth(xMin) ? xMax : xMin;
        }

        return new RotationMinMaxWidth(func.getRotatedWidth(minWidthOrigin), func.getRotatedWidth(maxWidthOrigin),
                minWidthOrigin, maxWidthOrigin, func.getRotatedHeight(minWidthOrigin), func.getRotatedHeight(maxWidthOrigin));
    }

    private static double sin(double angle) {
        return correctSinCos(Math.abs((Math.sin(angle))));
    }

    private static double cos(double angle) {
        return correctSinCos(Math.abs((Math.cos(angle))));
    }

    private static double correctSinCos(double value) {
        if (MinMaxWidthUtils.isEqual(value, 0)) {
            return 0;
        } else if (MinMaxWidthUtils.isEqual(value, 1)) {
            return 1;
        }
        return value;
    }

    /**
     * Class that represents functions used, for calculation of width of element after rotation
     * based on it's NOT rotated width and assumption, that area of element stays the same when
     * we try to layout it with different available width.
     * Contains handy methods for function analysis.
     */
    private static class WidthFunction {
        private double sin;
        private double cos;
        private double area;

        /**
         * Create new instance
         *
         * @param angle rotation angle in radians
         * @param area the constant area
         */
        public WidthFunction(double angle, double area) {
            this.sin = sin(angle);
            this.cos = cos(angle);
            this.area = area;
        }

        /**
         * Function used for width calculations of rotated element. This function is continuous on interval: (0, Infinity)
         *
         * @param x width value of NOT rotated element
         * @return width of rotated element
         */
        public double getRotatedWidth(double x) {
            return x * cos + area * sin / x;
        }

        /**
         * Function used for height calculations of rotated element. This function is continuous on interval: (0, Infinity)
         *
         * @param x width value of NOT rotated element
         * @return width of rotated element
         */
        public double getRotatedHeight(double x) {
            return x * sin + area * cos / x;
        }

        /**
         * Get's possible values of NOT rotated width of all element that have therer rotated width less that availableWidth
         *
         * @param availableWidth the highest possible width of rotated element.
         * @return interval that specify biggest and smallest possible values of NOT rotated width of such elements.
         */
        public Interval getValidOriginalWidths(double availableWidth) {
            double minWidth;
            double maxWidth;
            if (cos == 0) {
                minWidth = area * sin / availableWidth;
                maxWidth = MinMaxWidthUtils.getInfWidth();
            } else if (sin == 0) {
                minWidth = 0;
                maxWidth = availableWidth / cos;
            } else {
                double D = availableWidth * availableWidth - 4 * area * sin * cos;
                if (D < 0) {
                    return null;
                }
                minWidth = (availableWidth - Math.sqrt(D)) / (2 * cos);
                maxWidth = (availableWidth + Math.sqrt(D)) / (2 * cos);
            }
            return new Interval(minWidth, maxWidth);
        }

        /**
         * Gets the argument of {@link #getRotatedWidth(double)} that results in zero derivative.
         * In case we have {@link #sin}{@code == 0} or {@link #sin}{@code == 0} the function doesn't have
         * zero derivative on defined interval, but value returned by this method fits well in the calculations above.
         *
         * @return the argument of {@link #getRotatedWidth(double)} that results in zero derivative
         */
        public double getWidthDerivativeZeroPoint() {
            return Math.sqrt(area * sin / cos);
        }

        public static class Interval {
            private double min;
            private double max;

            public Interval(double min, double max) {
                this.min = min;
                this.max = max;
            }

            public double getMin() {
                return min;
            }

            public double getMax() {
                return max;
            }
        }
    }
}
