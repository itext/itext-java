package com.itextpdf.svg.utils;

import com.itextpdf.svg.exceptions.SvgExceptionMessageConstant;

public class SvgCoordinateUtils {

    /**
     * Converts relative coordinates to absolute ones. Assumes that relative coordinates are represented by
     * an array of coordinates with length proportional to the length of current coordinates array,
     * so that current coordinates array is applied in segments to the relative coordinates array
     */
    public static String[] makeRelativeOperatorCoordinatesAbsolute(String[] relativeCoordinates, double[] currentCoordinates) {
        if (relativeCoordinates.length % currentCoordinates.length != 0) {
            throw new IllegalArgumentException(SvgExceptionMessageConstant.COORDINATE_ARRAY_LENGTH_MUST_BY_DIVISIBLE_BY_CURRENT_COORDINATES_ARRAY_LENGTH);
        }
        String[] absoluteOperators = new String[relativeCoordinates.length];

        for (int i = 0; i < relativeCoordinates.length;) {
            for (int j = 0; j < currentCoordinates.length; j++, i++) {
                double relativeDouble = Double.parseDouble(relativeCoordinates[i]);
                relativeDouble += currentCoordinates[j];
                absoluteOperators[i] = SvgCssUtils.convertDoubleToString(relativeDouble);
            }
        }

        return absoluteOperators;
    }

}
