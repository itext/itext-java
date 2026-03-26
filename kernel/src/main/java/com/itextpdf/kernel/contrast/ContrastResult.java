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


import java.util.ArrayList;
import java.util.List;

/**
 * Represents the complete contrast analysis result for a single text element.
 * <p>
 * This class encapsulates all the contrast information for a text element, including
 * the text itself and a list of all background elements that it overlaps with, along
 * with their respective contrast ratios.
 * <p>
 * Each text element may have multiple background elements behind it, especially in
 * complex PDF layouts. This class collects all such relationships to provide a
 * comprehensive view of the text's contrast characteristics for accessibility analysis.
 */
public class ContrastResult {
    private final TextColorInfo textRenderInfo;
    private final List<OverlappingArea> overlappingAreas;
    private final int pageNumber;

    /**
     * Constructs a new {@link ContrastResult} for the specified text element.
     * <p>
     * The result is initialized with an empty list of background entries, which should
     * be populated using {@link #addContrastResult(OverlappingArea)}.
     *
     * @param textRenderInfo the text element for which contrast is being analyzed
     * @param pageNumber     the page number where the text element is located
     *
     */
    public ContrastResult(TextColorInfo textRenderInfo, int pageNumber) {
        this.textRenderInfo = textRenderInfo;
        this.pageNumber = pageNumber;
        this.overlappingAreas = new ArrayList<>();
    }

    /**
     * Gets the page number where the text element is located.
     *
     * @return the page number
     */
    public int getPageNumber() {
        return pageNumber;
    }


    /**
     * Gets the text render information for this contrast result.
     * <p>
     * The text information includes the character, parent text, color, geometric path,
     * and font size of the text element being analyzed.
     *
     * @return the text render information
     */
    public TextColorInfo getTextRenderInfo() {
        return textRenderInfo;
    }

    /**
     * Adds a background contrast entry to this result.
     * <p>
     * Each entry represents a background element that the text overlaps with, along
     * with the calculated contrast ratio between the text color and background color.
     * Multiple entries indicate that the text appears over multiple backgrounds.
     *
     * @param overlappingArea the contrast result entry containing background information and contrast ratio
     */
    public void addContrastResult(OverlappingArea overlappingArea) {
        this.overlappingAreas.add(overlappingArea);
    }

    /**
     * Gets all the background contrast entries for this text element.
     * <p>
     * Each entry in the list represents a background element that the text overlaps with,
     * containing the background's color, path, and the calculated contrast ratio.
     * The list may be empty if no backgrounds were detected, or may contain multiple
     * entries if the text overlaps multiple background elements.
     *
     * @return an unmodifiable view of the list of contrast result entries
     */
    public List<OverlappingArea> getOverlappingAreas() {
        return new ArrayList<>(overlappingAreas);
    }

    /**
     * Represents a single contrast analysis result entry between text and a background element.
     * <p>
     * This class encapsulates the information about a specific background element that intersects
     * with a text element, along with the calculated contrast ratio between them. It is used as
     * part of a {@link ContrastResult} to provide detailed information about all backgrounds
     * that contribute to the overall contrast of a text element.
     * <p>
     * The contrast ratio is calculated according to WCAG 2.1 guidelines and ranges from 1:1
     * (no contrast) to 21:1 (maximum contrast between black and white).
     */
    public static class OverlappingArea {

        private final BackgroundColorInfo backgroundRenderInfo;
        private final double contrastRatio;
        private double overlapRatio;

        /**
         * Constructs a new ContrastResultEntry with the specified background information and contrast ratio.
         *
         * @param backgroundRenderInfo the background element that was analyzed for contrast
         * @param contrastRatio        the calculated contrast ratio between the text and this background,
         *                             according to WCAG 2.1 guidelines (ranges from 1.0 to 21.0)
         */
        public OverlappingArea(BackgroundColorInfo backgroundRenderInfo, double contrastRatio) {
            this.backgroundRenderInfo = backgroundRenderInfo;
            this.contrastRatio = contrastRatio;
        }

        /**
         * Gets the background render information for this contrast entry.
         * <p>
         * The background information includes the color and geometric path of the background
         * element that was compared against the text.
         *
         * @return the background render information
         */
        public BackgroundColorInfo getBackgroundRenderInfo() {
            return backgroundRenderInfo;
        }

        /**
         * Gets the contrast ratio between the text and this background element.
         * <p>
         * The contrast ratio is calculated according to WCAG 2.1 guidelines:
         * <p>
         * *1.0 indicates no contrast (identical colors)
         * *21.0 is the maximum contrast (black and white)
         *
         * @return the contrast ratio value, ranging from 1.0 to 21.0
         */
        public double getContrastRatio() {
            return contrastRatio;
        }

        /**
         * Gets the percentage of the text area that overlaps with this background element.
         *
         * @return the overlapping area in percentage.
         */
        public double getOverlapRatio() {
            return overlapRatio;
        }

        /**
         * Sets the percentage of the text area that overlaps with this background element.
         * Should be a value between 0 and 1 representing 0% to 100%.
         *
         * @param overlappingAreaInPercentage the overlapping area in percentage.
         */
        public void setOverlapRatio(double overlappingAreaInPercentage) {
            if (overlappingAreaInPercentage < 0 || overlappingAreaInPercentage > 1) {
                throw new IllegalArgumentException("Overlap ratio must be between 0 and 1.");
            }
            this.overlapRatio = overlappingAreaInPercentage;
        }
    }
}
