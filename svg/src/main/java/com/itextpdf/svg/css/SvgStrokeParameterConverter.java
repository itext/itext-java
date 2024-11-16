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
package com.itextpdf.svg.css;

import com.itextpdf.styledxmlparser.css.util.CssDimensionParsingUtils;
import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.svg.utils.SvgCoordinateUtils;
import com.itextpdf.svg.utils.SvgCssUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class converts stroke related SVG parameters and attributes into those from PDF specification.
 */
public final class SvgStrokeParameterConverter {

    private SvgStrokeParameterConverter() {
    }

    /**
     * Convert stroke related SVG parameters and attributes into PDF line dash parameters.
     *
     * @param strokeDashArray 'stroke-dasharray' css property value.
     * @param strokeDashOffset 'stroke-dashoffset' css property value.
     * @param fontSize font size of the current element.
     * @param context the svg draw context.
     * @return PDF line dash parameters represented by {@link PdfLineDashParameters}.
     */
    public static PdfLineDashParameters convertStrokeDashParameters(String strokeDashArray, String strokeDashOffset,
            float fontSize, SvgDrawContext context) {
        if (strokeDashArray != null && !SvgConstants.Values.NONE.equalsIgnoreCase(strokeDashArray)) {
            final float rem = context.getCssContext().getRootFontSize();
            final float percentBaseValue = SvgCoordinateUtils.calculateNormalizedDiagonalLength(context);

            List<String> dashArray = SvgCssUtils.splitValueList(strokeDashArray);
            if (dashArray.size() > 0) {
                if (dashArray.size() % 2 == 1) {
                    // If an odd number of values is provided, then the list of values is repeated to yield an even
                    // number of values. Thus, 5,3,2 is equivalent to 5,3,2,5,3,2.
                    dashArray.addAll(new ArrayList<>(dashArray));
                }
                float[] dashArrayFloat = new float[dashArray.size()];
                for (int i = 0; i < dashArray.size(); i++) {
                    dashArrayFloat[i] = CssDimensionParsingUtils.parseLength(dashArray.get(i), percentBaseValue,
                            1f, fontSize, rem);
                }

                // Parse stroke dash offset
                float dashPhase = 0;
                if (strokeDashOffset != null && !strokeDashOffset.isEmpty() &&
                        !SvgConstants.Values.NONE.equalsIgnoreCase(strokeDashOffset)) {
                    dashPhase = CssDimensionParsingUtils.parseLength(strokeDashOffset, percentBaseValue,
                            1f, fontSize, rem);
                }

                return new PdfLineDashParameters(dashArrayFloat, dashPhase);
            }
        }

        return null;
    }

    /**
     * This class represents PDF dash parameters.
     */
    public static class PdfLineDashParameters {
        private final float[] dashArray;
        private final float dashPhase;

        /**
         * Construct PDF dash parameters.
         *
         * @param dashArray Numbers that specify the lengths of alternating dashes and gaps;
         *                 the numbers shall be nonnegative and not all zero.
         * @param dashPhase A number that specifies the distance into the dash pattern at which to start the dash.
         */
        public PdfLineDashParameters(float[] dashArray, float dashPhase) {
            this.dashArray = dashArray;
            this.dashPhase = dashPhase;
        }

        /**
         * Return dash array.
         *
         * @return dash array.
         */
        public float[] getDashArray() {
            return dashArray;
        }

        /**
         * Return dash phase.
         *
         * @return dash phase.
         */
        public float getDashPhase() {
            return dashPhase;
        }

        /**
         * Check if some object is equal to the given object.
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            PdfLineDashParameters that = (PdfLineDashParameters) o;

            if (Float.compare(that.dashPhase, dashPhase) != 0) {
                return false;
            }
            return Arrays.equals(dashArray, that.dashArray);
        }

        /**
         * Generate a hash code for this object.
         *
         * @return hash code.
         */
        @Override
        public int hashCode() {
            int result = Arrays.hashCode(dashArray);
            result = 31 * result + Float.floatToIntBits(dashPhase);
            return result;
        }
    }
}
