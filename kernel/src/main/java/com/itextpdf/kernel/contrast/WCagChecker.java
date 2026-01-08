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

/**
 * Utility class for checking WCAG (Web Content Accessibility Guidelines) compliance for text contrast.
 *
 * @see <a href="https://www.w3.org/WAI/WCAG21/Understanding/contrast-minimum.html">WCAG 2.1 Contrast (Minimum)</a>
 * @see <a href="https://www.w3.org/WAI/WCAG21/Understanding/contrast-enhanced.html">WCAG 2.1 Contrast (Enhanced)</a>
 */
public final class WCagChecker {

    // 14pt in pixels (1pt = 1.333px)
    private static final double LARGE_TEXT_MIN_FONT_SIZE_PX = 18.66;

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private WCagChecker() {
        // Utility class
    }

    /**
     * Checks for WCAG AA compliance.
     *
     * @param fontSize      The font size in pixels
     * @param contrastRatio The contrast ratio between text and background
     *
     * @return {@code true} if the text meets WCAG AA compliance, {@code false} otherwise
     */
    public static boolean isTextWcagAACompliant(double fontSize, double contrastRatio) {
        boolean isLargeText = fontSize >= LARGE_TEXT_MIN_FONT_SIZE_PX;

        if (isLargeText) {
            return contrastRatio >= 3.0;
        } else {
            return contrastRatio >= 4.5;
        }
    }

    /**
     * Checks for WCAG AAA compliance.
     *
     * @param fontSize      The font size in pixels
     * @param contrastRatio The contrast ratio between text and background
     *
     * @return {@code true} if the text meets WCAG AAA compliance, {@code false} otherwise
     */
    public static boolean isTextWcagAAACompliant(double fontSize, double contrastRatio) {
        boolean isLargeText = fontSize >= LARGE_TEXT_MIN_FONT_SIZE_PX;

        if (isLargeText) {
            return contrastRatio >= 4.5;
        } else {
            return contrastRatio >= 7.0;
        }
    }
}
