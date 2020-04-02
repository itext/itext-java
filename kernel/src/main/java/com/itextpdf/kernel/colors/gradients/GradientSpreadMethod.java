package com.itextpdf.kernel.colors.gradients;

/**
 * Represents possible spreading methods for gradient color outside of the coordinates vector
 */
public enum GradientSpreadMethod {
    /**
     * Pad the corner colors to fill the necessary area
     */
    PAD,
    /**
     * Reflect the coloring to fill the necessary area
     */
    REFLECT,
    /**
     * Repeat the coloring to fill the necessary area
     */
    REPEAT,
    /**
     * No coloring outside of the coordinates vector
     */
    NONE
}
