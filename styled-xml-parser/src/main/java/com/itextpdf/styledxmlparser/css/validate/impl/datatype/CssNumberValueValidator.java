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
import com.itextpdf.styledxmlparser.css.util.CssTypesValidationUtils;
import com.itextpdf.styledxmlparser.css.validate.ICssDataTypeValidator;

/**
 * {@link ICssDataTypeValidator} implementation for numeric elements.
 */
public class CssNumberValueValidator implements ICssDataTypeValidator {

    private final boolean allowedNegative;

    /**
     * Creates a new {@link CssNumberValueValidator} instance.
     *
     * @param allowedNegative is negative value allowed
     */
    public CssNumberValueValidator(boolean allowedNegative) {
        this.allowedNegative = allowedNegative;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(final String objectString) {
        if (objectString == null) {
            return false;
        }
        if (CommonCssConstants.INITIAL.equals(objectString) || CommonCssConstants.INHERIT.equals(objectString)
                || CommonCssConstants.UNSET.equals(objectString)) {
            return true;
        }
        if (!CssTypesValidationUtils.isNumber(objectString)) {
            return false;
        }
        if (CssTypesValidationUtils.isNegativeValue(objectString) && !CssTypesValidationUtils.isZero(objectString)) {
            return this.allowedNegative;
        }
        return true;
    }
}
