/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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
package com.itextpdf.styledxmlparser.css.util;

import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.styledxmlparser.css.CssDeclaration;
import com.itextpdf.styledxmlparser.css.validate.CssDeclarationValidationMaster;

import java.util.regex.Pattern;

/**
 * Utilities class for CSS types validating operations.
 */
public final class CssTypesValidationUtils {
    // TODO (DEVSIX-3595) The list of the angle measurements is not full. Required to
    //  add 'turn' units to array and move this array to the CommonCssConstants
    private static final String[] ANGLE_MEASUREMENTS_VALUES = new String[] {CommonCssConstants.DEG, CommonCssConstants.GRAD,
            CommonCssConstants.RAD};

    // TODO (DEVSIX-3596) The list of the relative measurements is not full.
    //  Add new relative units to array and move this array to the CommonCssConstants
    private static final String[] RELATIVE_MEASUREMENTS_VALUES = new String[] {CommonCssConstants.PERCENTAGE,
            CommonCssConstants.EM, CommonCssConstants.EX, CommonCssConstants.REM};

    private static final Pattern BASE64_PATTERN = Pattern.compile("^data:[^\\s]+;base64,");
    private static final Pattern DATA_PATTERN = Pattern.compile("^data:[^\\s]+;[^\\s]+,");


    /**
     * Creates a new {@link CssTypesValidationUtils} instance.
     */
    private CssTypesValidationUtils() {
        // Empty constructor
    }

    /**
     * Checks whether a string contains an allowed metric unit in HTML/CSS; rad, deg and grad.
     *
     * @param valueArgument the string that needs to be checked
     * @return boolean true if value contains an allowed angle value
     */
    public static boolean isAngleValue(final String valueArgument) {
        String value = valueArgument;
        if (value == null) {
            return false;
        } else {
            value = value.trim();
        }
        for (String metricPostfix : ANGLE_MEASUREMENTS_VALUES) {
            if (value.endsWith(metricPostfix) && isNumber(
                    value.substring(0, value.length() - metricPostfix.length()))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a data is base 64 encoded.
     *
     * @param data the data
     * @return true, if the data is base 64 encoded
     * @deprecated use {@link #isInlineData(String)} instead.
     */
    @Deprecated
    public static boolean isBase64Data(String data) {
        return BASE64_PATTERN.matcher(data).find();
    }

    /**
     * Checks if the string represent inline data in format `data:{FORMAT};{ENCODING},{DATA}`.
     *
     * @param data the string to check
     *
     * @return true, if the string is inline data
     */
    public static boolean isInlineData(String data) {
        return DATA_PATTERN.matcher(data).find();
    }

    /**
     * Checks if a value is a color property.
     *
     * @param value the value
     * @return true, if the value contains a color property
     */
    public static boolean isColorProperty(String value) {
        return CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.COLOR, value));
    }

    /**
     * Checks whether a string contains an allowed value relative to parent value.
     *
     * @param valueArgument the string that needs to be checked
     * @return boolean true if value contains a em value
     */
    public static boolean isEmValue(final String valueArgument) {
        String value = valueArgument;
        if (value == null) {
            return false;
        } else {
            value = value.trim();
        }
        return value.endsWith(CommonCssConstants.EM) && isNumber(
                value.substring(0, value.length() - CommonCssConstants.EM.length()));
    }

    /**
     * Checks whether a string contains an allowed value relative to element font height.
     *
     * @param valueArgument the string that needs to be checked
     * @return boolean true if value contains a ex value
     */
    public static boolean isExValue(final String valueArgument) {
        String value = valueArgument;
        if (value == null) {
            return false;
        } else {
            value = value.trim();
        }
        return value != null && value.endsWith(CommonCssConstants.EX) && isNumber(
                value.substring(0, value.length() - CommonCssConstants.EX.length()));
    }

    /**
     * Checks whether a string contains an allowed metric unit in HTML/CSS; px, in, cm, mm, pc, Q or pt.
     *
     * @param valueArgument the string that needs to be checked
     * @return boolean true if value contains an allowed metric value
     */
    public static boolean isMetricValue(final String valueArgument) {
        String value = valueArgument;
        if (value == null) {
            return false;
        } else {
            value = value.trim();
        }
        for (String metricPostfix : CommonCssConstants.METRIC_MEASUREMENTS_VALUES) {
            if (value.endsWith(metricPostfix) && isNumber(
                    value.substring(0, value.length() - metricPostfix.length()))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether a string matches a negative value (e.g. -123, -2em, -0.123).
     * All these metric values are allowed in HTML/CSS.
     *
     * @param value the string that needs to be checked
     * @return true if value is negative
     */
    public static boolean isNegativeValue(final String value) {
        if (value == null) {
            return false;
        }
        if (isNumber(value) || isRelativeValue(value) || isMetricValue(value)) {
            return value.startsWith("-");
        }
        return false;
    }

    /**
     * Checks whether a string matches a numeric value (e.g. 123, 1.23, .123). All these metric values are allowed in
     * HTML/CSS.
     *
     * @param value the string that needs to be checked
     * @return boolean true if value contains an allowed metric value
     */
    public static boolean isNumber(final String value) {
        return value != null && (value.matches("^[-+]?\\d\\d*\\.\\d*$")
                || value.matches("^[-+]?\\d\\d*$")
                || value.matches("^[-+]?\\.\\d\\d*$"));
    }

    /**
     * Checks whether a string matches an integer numeric value (e.g. 123, 23). All these metric values are allowed in
     * HTML/CSS.
     *
     * @param value the string that needs to be checked
     * @return boolean true if value contains an allowed metric value
     */
    public static boolean isIntegerNumber(final String value) {
        return value != null && value.matches("^[-+]?\\d\\d*$");
    }

    /**
     * Checks whether a string contains a percentage value
     *
     * @param valueArgument the string that needs to be checked
     * @return boolean true if value contains an allowed percentage value
     */
    public static boolean isPercentageValue(final String valueArgument) {
        String value = valueArgument;
        if (value == null) {
            return false;
        } else {
            value = value.trim();
        }
        return value.endsWith(CommonCssConstants.PERCENTAGE) && isNumber(
                value.substring(0, value.length() - CommonCssConstants.PERCENTAGE.length()));
    }

    /**
     * Checks whether a string contains an allowed value relative to previously set value.
     *
     * @param valueArgument the string that needs to be checked
     * @return boolean true if value contains an allowed metric value
     */
    public static boolean isRelativeValue(final String valueArgument) {
        String value = valueArgument;
        if (value == null) {
            return false;
        } else {
            value = value.trim();
        }
        for (String relativePostfix : RELATIVE_MEASUREMENTS_VALUES) {
            if (value.endsWith(relativePostfix) && isNumber(
                    value.substring(0, value.length() - relativePostfix.length()))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether a string contains an allowed value relative to previously set root value.
     *
     * @param valueArgument the string that needs to be checked
     * @return boolean true if value contains a rem value
     */
    public static boolean isRemValue(final String valueArgument) {
        String value = valueArgument;
        if (value == null) {
            return false;
        } else {
            value = value.trim();
        }
        return value != null && value.endsWith(CommonCssConstants.REM) && isNumber(
                value.substring(0, value.length() - CommonCssConstants.REM.length()));
    }

    /**
     * Checks if a string is in a valid format.
     *
     * @param value the string that needs to be checked
     * @return boolean true if value is in a valid format
     */
    public static boolean isValidNumericValue(final String value) {
        if (value == null || value.contains(" ")) {
            return false;
        }
        return isRelativeValue(value) || isMetricValue(value) || isNumber(value);
    }

    /**
     * Checks if value is initial, inherit or unset.
     *
     * @param value value to check
     * @return true if value is initial, inherit or unset. false otherwise
     */
    public static boolean isInitialOrInheritOrUnset(String value) {
        return CommonCssConstants.INITIAL.equals(value) ||
                CommonCssConstants.INHERIT.equals(value) ||
                CommonCssConstants.UNSET.equals(value);
    }

    /**
     * Checks if value contains initial, inherit or unset.
     *
     * @param value value to check
     * @return true if value contains initial, inherit or unset. False otherwise
     */
    public static boolean containsInitialOrInheritOrUnset(String value) {
        if (value == null) {
            return false;
        }
        return value.contains(CommonCssConstants.INITIAL) ||
                value.contains(CommonCssConstants.INHERIT) ||
                value.contains(CommonCssConstants.UNSET);
    }

    /**
     * Checks whether a string contains a zero.
     *
     * @param value the string that needs to be checked
     * @return boolean true if value contains a zero
     */
    public static boolean isZero(final String value) {
        return isNumericZeroValue(value) || isMetricZeroValue(value) || isRelativeZeroValue(value);
    }

    static boolean isMetricZeroValue(final String valueArgument) {
        String value = valueArgument;
        if (value == null) {
            return false;
        } else {
            value = value.trim();
        }
        for (String metricPostfix : CommonCssConstants.METRIC_MEASUREMENTS_VALUES) {
            if (value.endsWith(metricPostfix) && isNumericZeroValue(
                    value.substring(0, value.length() - metricPostfix.length()))) {
                return true;
            }
        }
        return false;
    }

    static boolean isNumericZeroValue(final String value) {
        return value != null && (value.matches("^[-+]?0$")
                || value.matches("^[-+]?\\.0$"));
    }

    static boolean isRelativeZeroValue(final String valueArgument) {
        String value = valueArgument;
        if (value == null) {
            return false;
        } else {
            value = value.trim();
        }
        for (String relativePostfix : RELATIVE_MEASUREMENTS_VALUES) {
            if (value.endsWith(relativePostfix) && isNumericZeroValue(
                    value.substring(0, value.length() - relativePostfix.length()))) {
                return true;
            }
        }
        return false;
    }
}
