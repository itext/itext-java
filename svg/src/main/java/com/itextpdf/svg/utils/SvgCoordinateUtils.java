/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
package com.itextpdf.svg.utils;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.geom.Vector;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.styledxmlparser.css.util.CssDimensionParsingUtils;
import com.itextpdf.styledxmlparser.css.util.CssTypesValidationUtils;
import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.SvgConstants.Values;
import com.itextpdf.svg.exceptions.SvgExceptionMessageConstant;
import com.itextpdf.svg.exceptions.SvgProcessingException;
import com.itextpdf.svg.renderers.SvgDrawContext;

/**
 * Utility class that facilitates various methods for calculating/transforming coordinates.
 */
public class SvgCoordinateUtils {

    /**
     * Converts relative coordinates to absolute ones. Assumes that relative coordinates are represented by
     * an array of coordinates with length proportional to the length of current coordinates array,
     * so that current coordinates array is applied in segments to the relative coordinates array
     *
     * @param relativeCoordinates the initial set of coordinates
     * @param currentCoordinates  an array representing the point relative to which the relativeCoordinates are defined
     * @return a String array of absolute coordinates, with the same length as the input array
     */
    public static String[] makeRelativeOperatorCoordinatesAbsolute(String[] relativeCoordinates,
            double[] currentCoordinates) {
        if (relativeCoordinates.length % currentCoordinates.length != 0) {
            throw new IllegalArgumentException(
                    SvgExceptionMessageConstant.COORDINATE_ARRAY_LENGTH_MUST_BY_DIVISIBLE_BY_CURRENT_COORDINATES_ARRAY_LENGTH);
        }
        String[] absoluteOperators = new String[relativeCoordinates.length];

        for (int i = 0; i < relativeCoordinates.length; ) {
            for (int j = 0; j < currentCoordinates.length; j++, i++) {
                double relativeDouble = Double.parseDouble(relativeCoordinates[i]);
                relativeDouble += currentCoordinates[j];
                absoluteOperators[i] = SvgCssUtils.convertDoubleToString(relativeDouble);
            }
        }

        return absoluteOperators;
    }

    /**
     * Calculate the angle between two vectors
     *
     * @param vectorA first vector
     * @param vectorB second vector
     * @return angle between vectors in radians units
     */
    public static double calculateAngleBetweenTwoVectors(Vector vectorA, Vector vectorB) {
        return Math.acos((double) vectorA.dot(vectorB) / ((double) vectorA.length() * (double) vectorB.length()));
    }

    /**
     * Returns absolute value for attribute in userSpaceOnUse coordinate system.
     *
     * @param attributeValue value of attribute.
     * @param defaultValue   default value.
     * @param start          start border for calculating percent value.
     * @param length         length for calculating percent value.
     * @param em             em value.
     * @param rem            rem value.
     * @return absolute value in the userSpaceOnUse coordinate system.
     */
    public static double getCoordinateForUserSpaceOnUse(String attributeValue, double defaultValue,
            double start, double length, float em, float rem) {
        double absoluteValue;
        final UnitValue unitValue = CssDimensionParsingUtils.parseLengthValueToPt(attributeValue, em, rem);
        if (unitValue == null) {
            absoluteValue = defaultValue;
        } else if (unitValue.getUnitType() == UnitValue.PERCENT) {
            absoluteValue = start + (length * unitValue.getValue() / 100);
        } else {
            absoluteValue = unitValue.getValue();
        }
        return absoluteValue;
    }

    /**
     * Returns a value relative to the object bounding box.
     * We should only call this method for attributes with coordinates relative to the object bounding rectangle.
     *
     * @param attributeValue attribute value to parse
     * @param defaultValue   this value will be returned if an error occurs while parsing the attribute value
     * @return if {@code attributeValue} is a percentage value, the given percentage of 1 will be returned.
     * And if it's a valid value with a number, the number will be extracted from that value.
     */
    public static double getCoordinateForObjectBoundingBox(String attributeValue, double defaultValue) {
        if (CssTypesValidationUtils.isPercentageValue(attributeValue)) {
            return CssDimensionParsingUtils.parseRelativeValue(attributeValue, 1);
        }
        if (CssTypesValidationUtils.isNumber(attributeValue)
                || CssTypesValidationUtils.isMetricValue(attributeValue)
                || CssTypesValidationUtils.isRelativeValue(attributeValue)) {
            // if there is incorrect value metric, then we do not need to parse the value
            int unitsPosition = CssDimensionParsingUtils.determinePositionBetweenValueAndUnit(attributeValue);
            if (unitsPosition > 0) {
                // We want to ignore the unit type how this is done in the "Google Chrome" approach
                // which treats the "abstract coordinate system" in the coordinate metric measure,
                // i.e. for value '0.5cm' the top/left of the object bounding box would be (1cm, 1cm),
                // for value '0.5em' the top/left of the object bounding box would be (1em, 1em) and etc.
                // no null pointer should be thrown as determine
                return CssDimensionParsingUtils.parseDouble(attributeValue.substring(0, unitsPosition))
                        .doubleValue();
            }
        }
        return defaultValue;
    }

    /**
     * Calculate normalized diagonal length.
     *
     * @param context svg draw context.
     * @return diagonal length in px.
     */
    public static float calculateNormalizedDiagonalLength(SvgDrawContext context) {
        final float viewPortHeight = context.getCurrentViewPort().getHeight();
        final float viewPortWidth = context.getCurrentViewPort().getWidth();
        return (float) (Math.sqrt(viewPortHeight * viewPortHeight +
                viewPortWidth * viewPortWidth) / Math.sqrt(2));
    }

    /**
     * Calculate percent base value if provided length is percent value.
     *
     * @param context svg draw context.
     * @param length length to check
     * @param isXAxis if {@code true} viewport's width will be used (x-axis), otherwise viewport's height will be
     *             used (y-axis)
     * @return percent base value if provided length is percent value, 0.0F otherwise
     */
    public static float calculatePercentBaseValueIfNeeded(SvgDrawContext context, String length, boolean isXAxis) {
        float percentBaseValue = 0.0F;
        if (CssTypesValidationUtils.isPercentageValue(length)) {
            if (context.getCurrentViewPort() == null) {
                throw new SvgProcessingException(SvgExceptionMessageConstant.ILLEGAL_RELATIVE_VALUE_NO_VIEWPORT_IS_SET);
            }
            percentBaseValue = isXAxis
                    ? context.getCurrentViewPort().getWidth() : context.getCurrentViewPort().getHeight();
        }
        return percentBaseValue;
    }

    /**
     * Returns the viewBox received after scaling and displacement given preserveAspectRatio.
     *
     * @param viewBox         parsed viewBox rectangle. It should be a valid {@link Rectangle}
     * @param currentViewPort current element view port. It should be a valid {@link Rectangle}
     * @param align           the alignment value that indicates whether to force uniform scaling
     *                        and, if so, the alignment method to use in case the aspect ratio of
     *                        the viewBox doesn't match the aspect ratio of the viewport. If align
     *                        is {@code null} or align is invalid (i.e. not in the predefined list),
     *                        then the default logic with align = "xMidYMid", and meetOrSlice = "meet" would be used
     * @param meetOrSlice     the way to scale the viewBox. If meetOrSlice is not {@code null} and invalid,
     *                        then the default logic with align = "xMidYMid"
     *                        and meetOrSlice = "meet" would be used, if meetOrSlice is {@code null}
     *                        then default "meet" value would be used with the specified align
     * @return the applied viewBox {@link Rectangle}
     */
    public static Rectangle applyViewBox(Rectangle viewBox, Rectangle currentViewPort, String align,
            String meetOrSlice) {
        if (currentViewPort == null) {
            throw new IllegalArgumentException(SvgExceptionMessageConstant.CURRENT_VIEWPORT_IS_NULL);
        }

        if (viewBox == null || viewBox.getWidth() <= 0 || viewBox.getHeight() <= 0) {
            throw new IllegalArgumentException(SvgExceptionMessageConstant.VIEWBOX_IS_INCORRECT);
        }

        if (align == null || (
                meetOrSlice != null && !Values.MEET.equals(meetOrSlice) && !Values.SLICE.equals(meetOrSlice)
        )) {
            return applyViewBox(viewBox, currentViewPort, Values.XMID_YMID, Values.MEET);
        }

        double scaleWidth;
        double scaleHeight;
        if (Values.NONE.equalsIgnoreCase(align)) {
            scaleWidth = (double) currentViewPort.getWidth() / (double) viewBox.getWidth();
            scaleHeight = (double) currentViewPort.getHeight() / (double) viewBox.getHeight();
        } else {
            double scale = getScaleWidthHeight(viewBox, currentViewPort, meetOrSlice);
            scaleWidth = scale;
            scaleHeight = scale;
        }

        // Apply scale for width and height.
        Rectangle appliedViewBox = new Rectangle(viewBox.getX(), viewBox.getY(),
                (float) ((double) viewBox.getWidth() * scaleWidth),
                (float) ((double) viewBox.getHeight() * scaleHeight));

        // Calculate offset.
        double minXOffset = (double) currentViewPort.getX() - ((double) appliedViewBox.getX() * scaleWidth);
        double minYOffset = (double) currentViewPort.getY() - ((double) appliedViewBox.getY() * scaleHeight);

        double midXOffset = (double) currentViewPort.getX() + ((double) currentViewPort.getWidth() / 2)
                - (((double) appliedViewBox.getX() * scaleWidth) + ((double) appliedViewBox.getWidth() / 2));
        double midYOffset = (double) currentViewPort.getY() + ((double) currentViewPort.getHeight() / 2)
                - (((double) appliedViewBox.getY() * scaleHeight) + ((double) appliedViewBox.getHeight() / 2));

        double maxXOffset = (double) currentViewPort.getX() + (double) currentViewPort.getWidth()
                - (((double) appliedViewBox.getX() * scaleWidth) + (double) appliedViewBox.getWidth());
        double maxYOffset = (double) currentViewPort.getY() + (double) currentViewPort.getHeight()
                - (((double) appliedViewBox.getY() * scaleHeight) + (double) appliedViewBox.getHeight());

        double xOffset;
        double yOffset;

        switch (align.toLowerCase()) {
            case SvgConstants.Values.NONE:
            case SvgConstants.Values.XMIN_YMIN:
                xOffset = minXOffset;
                yOffset = minYOffset;
                break;
            case SvgConstants.Values.XMIN_YMID:
                xOffset = minXOffset;
                yOffset = midYOffset;
                break;
            case SvgConstants.Values.XMIN_YMAX:
                xOffset = minXOffset;
                yOffset = maxYOffset;
                break;
            case SvgConstants.Values.XMID_YMIN:
                xOffset = midXOffset;
                yOffset = minYOffset;
                break;
            case SvgConstants.Values.XMID_YMAX:
                xOffset = midXOffset;
                yOffset = maxYOffset;
                break;
            case SvgConstants.Values.XMAX_YMIN:
                xOffset = maxXOffset;
                yOffset = minYOffset;
                break;
            case SvgConstants.Values.XMAX_YMID:
                xOffset = maxXOffset;
                yOffset = midYOffset;
                break;
            case SvgConstants.Values.XMAX_YMAX:
                xOffset = maxXOffset;
                yOffset = maxYOffset;
                break;
            case SvgConstants.Values.XMID_YMID:
                xOffset = midXOffset;
                yOffset = midYOffset;
                break;
            default:
                return applyViewBox(viewBox, currentViewPort, Values.XMID_YMID, Values.MEET);
        }

        // Apply offset.
        appliedViewBox.moveRight((float) xOffset);
        appliedViewBox.moveUp((float) yOffset);

        // Apply scale for coordinates.
        appliedViewBox.setX((float) ((double) appliedViewBox.getX() * scaleWidth));
        appliedViewBox.setY((float) ((double) appliedViewBox.getY() * scaleHeight));

        return appliedViewBox;
    }

    private static double getScaleWidthHeight(Rectangle viewBox, Rectangle currentViewPort,
            String meetOrSlice) {
        double scaleWidth = (double) currentViewPort.getWidth() / (double) viewBox.getWidth();
        double scaleHeight = (double) currentViewPort.getHeight() / (double) viewBox.getHeight();
        if (Values.SLICE.equalsIgnoreCase(meetOrSlice)) {
            return Math.max(scaleWidth, scaleHeight);
        } else if (Values.MEET.equalsIgnoreCase(meetOrSlice) || meetOrSlice == null) {
            return Math.min(scaleWidth, scaleHeight);
        } else {
            // This code should be unreachable. We check for incorrect cases
            // in the applyViewBox method and instead use the default implementation (xMidYMid meet).
            throw new IllegalStateException(
                    SvgExceptionMessageConstant.MEET_OR_SLICE_ARGUMENT_IS_INCORRECT);
        }
    }
}
