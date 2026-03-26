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
package com.itextpdf.kernel.contrast;

import com.itextpdf.kernel.colors.DeviceRgb;

/**
 * Utility class for calculating color contrast ratios according to the Web Content Accessibility Guidelines (WCAG) 2.1.
 * <p>
 * The contrast ratio ranges from 1:1 (no contrast) to 21:1 (maximum contrast between black and white).
 */
public final class ColorContrastCalculator {

    private static final double LUMINANCE_OFFSET = 0.05;
    private static final double SRGB_LINEARIZATION_THRESHOLD = 0.04045;
    private static final double SRGB_LINEARIZATION_DIVISOR = 12.92;
    private static final double SRGB_LINEARIZATION_COEFFICIENT = 1.055;
    private static final double SRGB_LINEARIZATION_OFFSET = 0.055;
    private static final double SRGB_LINEARIZATION_EXPONENT = 2.4;

    // ITU-R BT.709 coefficients for relative luminance calculation
    private static final double RED_LUMINANCE_COEFFICIENT = 0.2126;
    private static final double GREEN_LUMINANCE_COEFFICIENT = 0.7152;
    private static final double BLUE_LUMINANCE_COEFFICIENT = 0.0722;

    private static final int RED_COMPONENT_INDEX = 0;
    private static final int GREEN_COMPONENT_INDEX = 1;
    private static final int BLUE_COMPONENT_INDEX = 2;

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private ColorContrastCalculator() {
        // Utility class
    }

    /**
     * Calculates the contrast ratio between two colors according to WCAG 2.1 guidelines.
     * <p>
     * The contrast ratio is calculated as (L1 + 0.05) / (L2 + 0.05), where L1 is the relative
     * luminance of the lighter color and L2 is the relative luminance of the darker color.
     * <p>
     * The resulting value ranges from 1:1 (identical colors) to 21:1 (black and white).
     *
     * @param color1 the first color to compare, must not be {@code null}
     * @param color2 the second color to compare, must not be {@code null}
     *
     * @return the contrast ratio between the two colors, ranging from 1.0 to 21.0
     *
     * @throws IllegalArgumentException if either color is {@code null}
     */
    public static double contrastRatio(DeviceRgb color1, DeviceRgb color2) {
        if (color1 == null || color2 == null) {
            throw new IllegalArgumentException("Colors must not be null");
        }

        double[] components1 = extractRgbComponents(color1);
        double[] components2 = extractRgbComponents(color2);

        return contrastRatio(
                components1[RED_COMPONENT_INDEX], components1[GREEN_COMPONENT_INDEX], components1[BLUE_COMPONENT_INDEX],
                components2[RED_COMPONENT_INDEX], components2[GREEN_COMPONENT_INDEX],
                components2[BLUE_COMPONENT_INDEX]);
    }

    /**
     * Calculates the contrast ratio between two RGB colors according to WCAG 2.1 guidelines.
     * <p>
     * The contrast ratio is calculated as (L1 + 0.05) / (L2 + 0.05), where L1 is the relative
     * luminance of the lighter color and L2 is the relative luminance of the darker color.
     *
     * @param r1 red component of the first color (0-1)
     * @param g1 green component of the first color (0-1)
     * @param b1 blue component of the first color (0-1)
     * @param r2 red component of the second color (0-1)
     * @param g2 green component of the second color (0-1)
     * @param b2 blue component of the second color (0-1)
     *
     * @return the contrast ratio between the two colors, ranging from 1.0 to 21.0
     */
    public static double contrastRatio(
            double r1, double g1, double b1,
            double r2, double g2, double b2) {
        double l1 = luminance(r1, g1, b1);
        double l2 = luminance(r2, g2, b2);
        double lighter = Math.max(l1, l2);
        double darker = Math.min(l1, l2);
        return (lighter + LUMINANCE_OFFSET) / (darker + LUMINANCE_OFFSET);
    }

    /**
     * Extracts the RGB components from a DeviceRgb color.
     *
     * @param color the color to extract components from
     *
     * @return an array containing the red, green, and blue components (0-1)
     */
    private static double[] extractRgbComponents(DeviceRgb color) {
        float[] colorValues = color.getColorValue();
        return new double[] {
                clampToRgbRange(colorValues[RED_COMPONENT_INDEX]),
                clampToRgbRange(colorValues[GREEN_COMPONENT_INDEX]),
                clampToRgbRange(colorValues[BLUE_COMPONENT_INDEX])
        };
    }

    /**
     * Clamps an integer value to the valid RGB range of 0-255.
     *
     * @param value the value to clamp
     *
     * @return the clamped value, guaranteed to be between 0 and 1 inclusive
     */
    private static double clampToRgbRange(float value) {
        if (value < 0) {
            return 0;
        }
        if (value > 1) {
            return 1;
        }
        return value;
    }

    /**
     * Converts an sRGB color channel value to linear RGB.
     * <p>
     * This implements the inverse of the sRGB gamma correction according to the sRGB specification.
     * Values below the threshold use a linear transformation, while values above use a power function.
     *
     * @param channel the color channel value in the range 0-1
     *
     * @return the linearized channel value in the range 0.0-1.0
     */
    private static double linearize(double channel) {
        return (channel <= SRGB_LINEARIZATION_THRESHOLD)
                ? (channel / SRGB_LINEARIZATION_DIVISOR)
                : Math.pow((channel + SRGB_LINEARIZATION_OFFSET) / SRGB_LINEARIZATION_COEFFICIENT,
                        SRGB_LINEARIZATION_EXPONENT);
    }

    /**
     * Calculates the relative luminance of an RGB color according to WCAG 2.1.
     * <p>
     * The relative luminance is calculated using the ITU-R BT.709 coefficients:
     * L = 0.2126 * R + 0.7152 * G + 0.0722 * B, where R, G, and B are the linearized color components.
     *
     * @param r red component (0-1)
     * @param g green component (0-1)
     * @param b blue component (0-1)
     *
     * @return the relative luminance in the range 0.0 (black) to 1.0 (white)
     */
    private static double luminance(double r, double g, double b) {
        double rLin = linearize(r);
        double gLin = linearize(g);
        double bLin = linearize(b);
        return RED_LUMINANCE_COEFFICIENT * rLin
                + GREEN_LUMINANCE_COEFFICIENT * gLin
                + BLUE_LUMINANCE_COEFFICIENT * bLin;
    }

}
