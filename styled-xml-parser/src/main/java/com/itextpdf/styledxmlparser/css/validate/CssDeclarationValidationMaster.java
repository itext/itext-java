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
package com.itextpdf.styledxmlparser.css.validate;


import com.itextpdf.styledxmlparser.css.CssDeclaration;
import com.itextpdf.styledxmlparser.css.validate.impl.CssDefaultValidator;

/**
 * Class that holds CSS declaration validator.
 */
public class CssDeclarationValidationMaster {

    /**
     * A validator containing all the CSS declaration validators.
     */
    private static ICssDeclarationValidator VALIDATOR = new CssDefaultValidator();

    /**
     * Creates a new {@code CssDeclarationValidationMaster} instance.
     */
    private CssDeclarationValidationMaster() {
    }

    /**
     * Checks a CSS declaration.
     *
     * @param declaration the CSS declaration
     * @return true, if the validation was successful
     */
    public static boolean checkDeclaration(CssDeclaration declaration) {
        return VALIDATOR.isValid(declaration);
    }

    /**
     * Sets new validator for CSS declarations.
     *
     * @param validator validator for CSS declarations:
     *                  use {@link com.itextpdf.styledxmlparser.css.validate.impl.CssDefaultValidator} instance to
     *                  use default validation,
     *                  use {@link com.itextpdf.styledxmlparser.css.validate.impl.CssDeviceCmykAwareValidator}
     *                  instance to support device-cmyk feature
     */
    public static void setValidator(ICssDeclarationValidator validator) {
        VALIDATOR = validator;
    }
}
