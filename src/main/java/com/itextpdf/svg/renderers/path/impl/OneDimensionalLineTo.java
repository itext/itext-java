package com.itextpdf.svg.renderers.path.impl;


import java.util.Arrays;
import java.util.HashMap;

/***
 * Implements the abstract functionality of a line pathing operation that only changes the path's coordinate
 * in one dimension, i.e a vertical (V/v) or horizontal (H/h) line.
 *
 * */
public abstract class OneDimensionalLineTo extends AbstractPathShape {

    /**
     * The current x or y coordinate that will remain unchanged when drawing the line.
     * For vertical lines the x value will not change,
     * for horizontal lines y value will not change.
     */
    protected final String CURRENT_NONCHANGING_DIMENSION_VALUE = "CURRENT_NONCHANGING_DIMENSION_VALUE";

    /**
     * The minimum x or y value in the dimension that will change.
     * For vertical lines this will be the y value of the bottom-most point.
     * For horizontal lines this will be the x value of the left-most point.
     */
    protected final String MINIMUM_CHANGING_DIMENSION_VALUE = "MINIMUM_CHANGING_DIMENSION_VALUE";

    /**
     * The maximum x or y value in the dimension that will change.
     * For vertical lines this will be the y value of the top-most point.
     * For horizontal lines this will be the x value of the righ-most point.
     */

    protected final String MAXIMUM_CHANGING_DIMENSION_VALUE = "MAXIMUM_CHANGING_DIMENSION_VALUE";

    /**
     * The final x or y value in the dimension that will change.
     * For vertical lines this will be the y value of the last point.
     * For horizontal lines this will be the x value of the last point.
     */
    protected final String ENDING_CHANGING_DIMENSION_VALUE = "ENDING_CHANGING_DIMENSION_VALUE";


    /**
     * Sets the minimum and maximum value of the coordinates in the dimension that changes for this line segment.
     */
    private void setSegmentPoints(String[] coordinates) {
        float min = Float.MAX_VALUE;
        float max = Float.MIN_VALUE;

        float current;
        for (int x = 0; x < coordinates.length; x++) {
            current = Float.parseFloat(coordinates[x]);
            if (current > max) {
                max = current;
            }
            if (current < min) {
                min = current;
            }
        }

        if (properties == null) {
            properties = new HashMap<>();
        }
        properties.put(MAXIMUM_CHANGING_DIMENSION_VALUE, String.valueOf(max));
        properties.put(MINIMUM_CHANGING_DIMENSION_VALUE, String.valueOf(min));


    }

    /**
     * The coordinates for a one dimensional line. The first argument is the x or y value of the dimension that does not
     * change. The rest of the coordinates represent the operators to the (V/v) pathing operator and the current
     * coordinate in the dimension that does change.
     *
     * @param staticDimensionValue The value of the non-changing dimension.
     * @param coordinates          an array containing point values for path coordinates
     */
    private void setCoordinates(String staticDimensionValue, String[] coordinates) {
        properties = new HashMap<String, String>();
        setSegmentPoints(coordinates);
        properties.put(CURRENT_NONCHANGING_DIMENSION_VALUE, staticDimensionValue);
        properties.put(ENDING_CHANGING_DIMENSION_VALUE, coordinates[coordinates.length - 1]);
    }

    @Override
    public void setCoordinates(String[] coordinates) {
        String staticDimensionValue = coordinates[0];
        String[] operatorArgs = Arrays.copyOfRange(coordinates, 1, coordinates.length);
        setCoordinates(staticDimensionValue, operatorArgs);
    }
}
