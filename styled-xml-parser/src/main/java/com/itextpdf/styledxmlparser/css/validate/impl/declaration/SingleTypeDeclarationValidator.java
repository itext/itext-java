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
package com.itextpdf.styledxmlparser.css.validate.impl.declaration;


import com.itextpdf.styledxmlparser.css.CssDeclaration;
import com.itextpdf.styledxmlparser.css.validate.ICssDataTypeValidator;
import com.itextpdf.styledxmlparser.css.validate.ICssDeclarationValidator;

/**
 * {@link ICssDeclarationValidator} implementation to validate a single type.
 */
public class SingleTypeDeclarationValidator implements ICssDeclarationValidator {

    /** The data type validator. */
    private ICssDataTypeValidator dataTypeValidator;

    /**
     * Creates a new {@link SingleTypeDeclarationValidator} instance.
     *
     * @param dataTypeValidator the data type validator
     */
    public SingleTypeDeclarationValidator(ICssDataTypeValidator dataTypeValidator) {
        this.dataTypeValidator = dataTypeValidator;
    }

    /* (non-Javadoc)
     * @see com.itextpdf.styledxmlparser.css.validate.ICssDeclarationValidator#isValid(com.itextpdf.styledxmlparser.css.CssDeclaration)
     */
    @Override
    public boolean isValid(CssDeclaration cssDeclaration) {
        return dataTypeValidator.isValid(cssDeclaration.getExpression());
    }
}
