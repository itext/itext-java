package com.itextpdf.layout.properties;

/**
 * A specialized enum containing potential property values for
 * {@link com.itextpdf.layout.properties.Property#GRID_FLOW}.
 */
public enum GridFlow {
    /**
     * Defines row flow from left to right of a grid.
     */
    ROW,
    /**
     * Defines column flow from top to bottom of a grid.
     */
    COLUMN,
    /**
     * Same as {@code ROW} but uses dense algorithm for cell placement.
     */
    ROW_DENSE,
    /**
     * Same as {@code COLUMN} but uses dense algorithm for cell placement.
     */
    COLUMN_DENSE
}
