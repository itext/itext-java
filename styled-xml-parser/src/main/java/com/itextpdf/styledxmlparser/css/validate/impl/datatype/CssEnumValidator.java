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


import com.itextpdf.styledxmlparser.css.validate.ICssDataTypeValidator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * {@link ICssDataTypeValidator} implementation for elements in an enumeration.
 */
public class CssEnumValidator implements ICssDataTypeValidator {

    /** The allowed values. */
    private Set<String> allowedValues;

    /**
     * Creates a new {@link CssEnumValidator} instance.
     *
     * @param allowedValues the allowed values
     */
    public CssEnumValidator(String... allowedValues) {
        this.allowedValues = new HashSet<>(Arrays.asList(allowedValues));
    }

    /**
     * Creates a new {@link CssEnumValidator} instance.
     *
     * @param allowedValues the allowed values
     */
    public CssEnumValidator(Collection<String> allowedValues) {
        this(allowedValues, null);
    }

    /**
     * Creates a new {@link CssEnumValidator} instance.
     * <p>
     * Each allowed value will be added with all the modificators.
     * Each allowed value will be added as well.
     *
     * @param allowedValues the allowed values
     * @param allowedModificators the allowed prefixes
     */
    public CssEnumValidator(Collection<String> allowedValues, Collection<String> allowedModificators) {
        this.allowedValues = new HashSet<>();
        this.allowedValues.addAll(allowedValues);
        if (null != allowedModificators) {
            for (String prefix : allowedModificators) {
                for (String value : allowedValues) {
                    this.allowedValues.add(prefix + " " + value);
                }
            }
        }
    }

    /**
     * Adds new allowed values to the allowedValues.
     *
     * @param allowedValues the allowed values
     */
    public void addAllowedValues(final Collection<String> allowedValues) {
        this.allowedValues.addAll(allowedValues);
    }

    /* (non-Javadoc)
     * @see com.itextpdf.styledxmlparser.css.validate.ICssDataTypeValidator#isValid(java.lang.String)
     */
    @Override
    public boolean isValid(String objectString) {
        return allowedValues.contains(objectString);
    }
}
