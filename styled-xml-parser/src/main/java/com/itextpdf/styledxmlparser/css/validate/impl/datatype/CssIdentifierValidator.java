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

/**
 * {@link ICssDataTypeValidator} implementation for identifiers.
 * In CSS, identifiers (including element names, classes, and IDs in selectors) can contain only the characters [a-zA-Z0-9]
 * and ISO 10646 characters U+00A0 and higher, plus the hyphen (-) and the underscore (_);
 * they cannot start with a digit, two hyphens, or a hyphen followed by a digit.
 * Identifiers can also contain escaped characters and any ISO 10646 character as a numeric code (see next item).
 * For instance, the identifier "B&amp;W?" may be written as "B\&amp;W\?" or "B\26 W\3F".
 */
public class CssIdentifierValidator implements ICssDataTypeValidator {

    /* (non-Javadoc)
     * @see com.itextpdf.styledxmlparser.css.validate.ICssDataTypeValidator#isValid(java.lang.String)
     */
    @Override
    public boolean isValid(String objectString) {
        // TODO DEVSIX-3969: now the validation is very lenient. Make it more strict
        if (objectString.length() >= 2 && objectString.startsWith("--")) {
            return false;
        } else if (objectString.matches("^[0-9].*")) {
            return false;
        } else {
            return true;
        }
    }

}
