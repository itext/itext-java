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

import com.itextpdf.layout.properties.BackgroundRepeat.BackgroundRepeatValue;
import com.itextpdf.styledxmlparser.css.CommonCssConstants;

/**
 * Utilities class for CSS background parsing.
 */
public final class CssBackgroundUtils {

    /**
     * Creates a new {@link CssBackgroundUtils} instance.
     */
    private CssBackgroundUtils() {
    }

    /**
     * Parses the background repeat string value.
     *
     * @param value the string which stores the background repeat value
     * @return the background repeat as a {@link BackgroundRepeatValue} instance
     */
    public static BackgroundRepeatValue parseBackgroundRepeat(String value) {
        switch (value) {
            case CommonCssConstants.NO_REPEAT:
                return BackgroundRepeatValue.NO_REPEAT;
            case CommonCssConstants.ROUND:
                return BackgroundRepeatValue.ROUND;
            case CommonCssConstants.SPACE:
                return BackgroundRepeatValue.SPACE;
            case CommonCssConstants.REPEAT:
            default:
                return BackgroundRepeatValue.REPEAT;
        }
    }

    /**
     * Gets background property name corresponding to its type.
     *
     * @param propertyType background property type
     * @return background property name
     */
    public static String getBackgroundPropertyNameFromType(BackgroundPropertyType propertyType) {
        switch (propertyType) {
            case BACKGROUND_COLOR:
                return CommonCssConstants.BACKGROUND_COLOR;
            case BACKGROUND_IMAGE:
                return CommonCssConstants.BACKGROUND_IMAGE;
            case BACKGROUND_POSITION:
                return CommonCssConstants.BACKGROUND_POSITION;
            case BACKGROUND_POSITION_X:
                return CommonCssConstants.BACKGROUND_POSITION_X;
            case BACKGROUND_POSITION_Y:
                return CommonCssConstants.BACKGROUND_POSITION_Y;
            case BACKGROUND_SIZE:
                return CommonCssConstants.BACKGROUND_SIZE;
            case BACKGROUND_REPEAT:
                return CommonCssConstants.BACKGROUND_REPEAT;
            case BACKGROUND_ORIGIN:
                return CommonCssConstants.BACKGROUND_ORIGIN;
            case BACKGROUND_CLIP:
                return CommonCssConstants.BACKGROUND_CLIP;
            case BACKGROUND_ATTACHMENT:
                return CommonCssConstants.BACKGROUND_ATTACHMENT;
            default:
                return CommonCssConstants.UNDEFINED_NAME;
        }
    }

    /**
     * Resolves the background property type using it's value.
     *
     * @param value the value
     * @return the background property type value
     */
    public static BackgroundPropertyType resolveBackgroundPropertyType(final String value) {
        final String url = "url(";
        if (value.startsWith(url) && value.indexOf('(', url.length()) == -1
                && value.indexOf(')') == value.length() - 1) {
            return BackgroundPropertyType.BACKGROUND_IMAGE;
        }
        if (CssGradientUtil.isCssLinearGradientValue(value) || CommonCssConstants.NONE.equals(value)) {
            return BackgroundPropertyType.BACKGROUND_IMAGE;
        }
        if (CommonCssConstants.BACKGROUND_REPEAT_VALUES.contains(value)) {
            return BackgroundPropertyType.BACKGROUND_REPEAT;
        }
        if (CommonCssConstants.BACKGROUND_ATTACHMENT_VALUES.contains(value)) {
            return BackgroundPropertyType.BACKGROUND_ATTACHMENT;
        }
        if (CommonCssConstants.BACKGROUND_POSITION_X_VALUES.contains(value)
                && !CommonCssConstants.CENTER.equals(value)) {
            return BackgroundPropertyType.BACKGROUND_POSITION_X;
        }
        if (CommonCssConstants.BACKGROUND_POSITION_Y_VALUES.contains(value)
                && !CommonCssConstants.CENTER.equals(value)) {
            return BackgroundPropertyType.BACKGROUND_POSITION_Y;
        }
        if (CommonCssConstants.CENTER.equals(value)) {
            return BackgroundPropertyType.BACKGROUND_POSITION;
        }
        if (((Integer) 0).equals(CssDimensionParsingUtils.parseInteger(value))
                || CssTypesValidationUtils.isMetricValue(value) || CssTypesValidationUtils.isRelativeValue(value)) {
            return BackgroundPropertyType.BACKGROUND_POSITION_OR_SIZE;
        }
        if (CommonCssConstants.BACKGROUND_SIZE_VALUES.contains(value)) {
            return BackgroundPropertyType.BACKGROUND_SIZE;
        }
        if (CssTypesValidationUtils.isColorProperty(value)) {
            return BackgroundPropertyType.BACKGROUND_COLOR;
        }
        if (CommonCssConstants.BACKGROUND_ORIGIN_OR_CLIP_VALUES.contains(value)) {
            return BackgroundPropertyType.BACKGROUND_ORIGIN_OR_CLIP;
        }
        return BackgroundPropertyType.UNDEFINED;
    }

    public enum BackgroundPropertyType {
        BACKGROUND_COLOR,
        BACKGROUND_IMAGE,
        BACKGROUND_POSITION,
        BACKGROUND_POSITION_X,
        BACKGROUND_POSITION_Y,
        BACKGROUND_SIZE,
        BACKGROUND_REPEAT,
        BACKGROUND_ORIGIN,
        BACKGROUND_CLIP,
        BACKGROUND_ATTACHMENT,
        BACKGROUND_POSITION_OR_SIZE,
        BACKGROUND_ORIGIN_OR_CLIP,
        UNDEFINED
    }
}
