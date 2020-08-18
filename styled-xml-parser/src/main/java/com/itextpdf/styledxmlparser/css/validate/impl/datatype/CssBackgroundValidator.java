/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2020 iText Group NV
    Authors: iText Software.

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
import com.itextpdf.styledxmlparser.css.util.CssUtils;
import com.itextpdf.styledxmlparser.css.validate.ICssDataTypeValidator;

import java.util.List;

/**
 * {@link ICssDataTypeValidator} implementation for background properties.
 * This validator should not be used with non-background properties.
 */
public class CssBackgroundValidator implements ICssDataTypeValidator {

    private final String backgroundProperty;

    /**
     * Creates a new {@link CssNumericValueValidator} instance.
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
        if (CommonCssConstants.INITIAL.equals(objectString) || CommonCssConstants.INHERIT.equals(objectString) ||
                CommonCssConstants.UNSET.equals(objectString)) {
            return true;
        }
        // Actually it's not shorthand but extractShorthandProperties method works exactly as needed in this case
        final List<List<String>> extractedProperties = CssUtils.extractShorthandProperties(objectString);
        for (final List<String> propertyValues : extractedProperties) {
            if (propertyValues.isEmpty()) {
                return false;
            }
            for (final String propertyValue : propertyValues) {
                if (!isValidProperty(propertyValue, propertyValues)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isValidProperty(final String propertyValue, final List<String> propertyValues) {
        if (isPropertyValueCorrespondsPropertyType(propertyValue)) {
            if (propertyValues.size() > 1) {
                if (isMultiValueAllowedForThisType() && isMultiValueAllowedForThisValue(propertyValue)) {
                    // TODO DEVSIX-2106 Some extra validations for currently not supported properties.
                    return true;
                } else {
                    return false;
                }
            }
            return true;
        }
        return false;
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
                !CommonCssConstants.CONTAIN.equals(value);
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
        if (propertyType == CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_ORIGIN_OR_CLIP &&
                (CommonCssConstants.BACKGROUND_CLIP.equals(backgroundProperty) ||
                        CommonCssConstants.BACKGROUND_ORIGIN.equals(backgroundProperty))) {
            return true;
        }
        return propertyType == CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_POSITION_OR_SIZE &&
                (CommonCssConstants.BACKGROUND_POSITION.equals(backgroundProperty) ||
                        CommonCssConstants.BACKGROUND_SIZE.equals(backgroundProperty));
    }
}
