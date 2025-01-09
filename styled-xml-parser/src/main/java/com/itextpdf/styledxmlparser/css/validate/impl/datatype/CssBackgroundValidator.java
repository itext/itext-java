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
package com.itextpdf.styledxmlparser.css.validate.impl.datatype;

import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.styledxmlparser.css.util.CssBackgroundUtils;
import com.itextpdf.styledxmlparser.css.util.CssTypesValidationUtils;
import com.itextpdf.styledxmlparser.css.util.CssUtils;
import com.itextpdf.styledxmlparser.css.validate.ICssDataTypeValidator;

import java.util.List;

/**
 * {@link ICssDataTypeValidator} implementation for background properties.
 * This validator should not be used with non-background properties.
 */
public class CssBackgroundValidator implements ICssDataTypeValidator {

    private static final int MAX_AMOUNT_OF_VALUES = 2;

    private final String backgroundProperty;

    /**
     * Creates a new {@link CssBackgroundValidator} instance.
     *
     * @param backgroundProperty is background property corresponding to current validator
     */
    public CssBackgroundValidator(final String backgroundProperty) {
        this.backgroundProperty = backgroundProperty;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(final String objectString) {
        if (objectString == null) {
            return false;
        }
        if (CssTypesValidationUtils.isInitialOrInheritOrUnset(objectString)) {
            return true;
        }
        // Actually it's not shorthand but extractShorthandProperties method works exactly as needed in this case
        final List<List<String>> extractedProperties = CssUtils.extractShorthandProperties(objectString);
        for (final List<String> propertyValues : extractedProperties) {
            if (propertyValues.isEmpty() || propertyValues.size() > MAX_AMOUNT_OF_VALUES) {
                return false;
            }
            for (int i = 0; i < propertyValues.size(); i++) {
                if (!isValidProperty(propertyValues, i)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isValidProperty(List<String> propertyValues, int index) {
        if (isPropertyValueCorrespondsPropertyType(propertyValues.get(index))) {
            if (propertyValues.size() == MAX_AMOUNT_OF_VALUES) {
                if (isMultiValueAllowedForThisType() && isMultiValueAllowedForThisValue(propertyValues.get(index))) {
                    return checkMultiValuePositionXY(propertyValues, index);
                }
                return false;
            }
            return true;
        }
        return false;
    }

    private boolean checkMultiValuePositionXY(List<String> propertyValues, int index) {
        if (CommonCssConstants.BACKGROUND_POSITION_X.equals(backgroundProperty) ||
                CommonCssConstants.BACKGROUND_POSITION_Y.equals(backgroundProperty)) {
            if (CommonCssConstants.BACKGROUND_POSITION_VALUES.contains(propertyValues.get(index)) && index == 1) {
                return false;
            }
            return CommonCssConstants.BACKGROUND_POSITION_VALUES.contains(propertyValues.get(index)) || index == 1;
        }
        return true;
    }

    private boolean isMultiValueAllowedForThisType() {
        return !CommonCssConstants.BACKGROUND_ORIGIN.equals(backgroundProperty) &&
                !CommonCssConstants.BACKGROUND_CLIP.equals(backgroundProperty) &&
                !CommonCssConstants.BACKGROUND_IMAGE.equals(backgroundProperty) &&
                !CommonCssConstants.BACKGROUND_ATTACHMENT.equals(backgroundProperty);
    }

    private static boolean isMultiValueAllowedForThisValue(final String value) {
        return !CommonCssConstants.REPEAT_X.equals(value) &&
                !CommonCssConstants.REPEAT_Y.equals(value) &&
                !CommonCssConstants.COVER.equals(value) &&
                !CommonCssConstants.CONTAIN.equals(value) &&
                !CommonCssConstants.CENTER.equals(value);
    }

    private boolean isPropertyValueCorrespondsPropertyType(final String value) {
        final CssBackgroundUtils.BackgroundPropertyType propertyType =
                CssBackgroundUtils.resolveBackgroundPropertyType(value);
        if (propertyType == CssBackgroundUtils.BackgroundPropertyType.UNDEFINED) {
            return false;
        }
        if (CssBackgroundUtils.getBackgroundPropertyNameFromType(propertyType).equals(backgroundProperty)) {
            return true;
        }
        if (propertyType == CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_POSITION &&
                (CommonCssConstants.BACKGROUND_POSITION_X.equals(backgroundProperty) ||
                        CommonCssConstants.BACKGROUND_POSITION_Y.equals(backgroundProperty))) {
            return true;
        }
        if (propertyType == CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_ORIGIN_OR_CLIP &&
                (CommonCssConstants.BACKGROUND_CLIP.equals(backgroundProperty) ||
                        CommonCssConstants.BACKGROUND_ORIGIN.equals(backgroundProperty))) {
            return true;
        }
        return propertyType == CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_POSITION_OR_SIZE &&
                (CommonCssConstants.BACKGROUND_POSITION_X.equals(backgroundProperty) ||
                        CommonCssConstants.BACKGROUND_POSITION_Y.equals(backgroundProperty) ||
                        CommonCssConstants.BACKGROUND_SIZE.equals(backgroundProperty));
    }
}
