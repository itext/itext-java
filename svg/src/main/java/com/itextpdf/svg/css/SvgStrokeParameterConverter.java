package com.itextpdf.svg.css;

import com.itextpdf.styledxmlparser.css.util.CssDimensionParsingUtils;
import com.itextpdf.styledxmlparser.css.util.CssTypesValidationUtils;
import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.logs.SvgLogMessageConstant;
import com.itextpdf.svg.utils.SvgCssUtils;

import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class converts stroke-related SVG parameters and attributes into those from PDF specification
 */
public class SvgStrokeParameterConverter {

    private SvgStrokeParameterConverter() {
    }

    private static Logger LOGGER = LoggerFactory.getLogger(SvgStrokeParameterConverter.class);

    public static PdfLineDashParameters convertStrokeDashArray(String strokeDashArray) {
        if (!SvgConstants.Values.NONE.equalsIgnoreCase(strokeDashArray)) {
            List<String> dashArray = SvgCssUtils.splitValueList(strokeDashArray);

            for (String dashArrayItem : dashArray) {
                if (CssTypesValidationUtils.isPercentageValue(dashArrayItem)) {
                    LOGGER.error(SvgLogMessageConstant.PERCENTAGE_VALUES_IN_STROKE_DASHARRAY_ARE_NOT_SUPPORTED);
                    return null;
                }
            }

            if (dashArray.size() > 0) {
                if (dashArray.size() % 2 == 1) {
                    // If an odd number of values is provided, then the list of values is repeated to yield an even
                    // number of values. Thus, 5,3,2 is equivalent to 5,3,2,5,3,2.
                    dashArray.addAll(dashArray);
                }
                float[] dashArrayFloat = new float[dashArray.size()];
                for (int i = 0; i < dashArray.size(); i++) {
                    dashArrayFloat[i] = CssDimensionParsingUtils.parseAbsoluteLength(dashArray.get(i));
                }
                return new PdfLineDashParameters(dashArrayFloat, 0);
            }
        }
        return null;
    }

    public static class PdfLineDashParameters {
        private float[] lengths;
        private float phase;

        public PdfLineDashParameters(float[] lengths, float phase) {
            this.lengths = lengths;
            this.phase = phase;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            PdfLineDashParameters that = (PdfLineDashParameters) o;

            if (Float.compare(that.phase, phase) != 0) {
                return false;
            }
            return Arrays.equals(lengths, that.lengths);
        }

        @Override
        public int hashCode() {
            int result = Arrays.hashCode(lengths);
            result = 31 * result + (phase != +0.0f ? Float.floatToIntBits(phase) : 0);
            return result;
        }


        public float[] getLengths() {
            return lengths;
        }

        public float getPhase() {
            return phase;
        }
    }

}
