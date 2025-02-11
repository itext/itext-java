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
package com.itextpdf.styledxmlparser.css.validate.impl;

import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.styledxmlparser.css.validate.ICssDeclarationValidator;
import com.itextpdf.styledxmlparser.css.validate.impl.datatype.CssCmykAwareColorValidator;
import com.itextpdf.styledxmlparser.css.validate.impl.datatype.CssEnumValidator;
import com.itextpdf.styledxmlparser.css.validate.impl.declaration.MultiTypeDeclarationValidator;

/**
 * Class that bundles all the CSS declaration validators.
 * It extends the default {@link CssDefaultValidator} to also support device-cmyk color structure.
 */
public class CssDeviceCmykAwareValidator extends CssDefaultValidator {
    public CssDeviceCmykAwareValidator() {
        super();
        ICssDeclarationValidator colorCmykValidator = new MultiTypeDeclarationValidator(
                new CssEnumValidator(CommonCssConstants.TRANSPARENT, CommonCssConstants.INITIAL,
                        CommonCssConstants.INHERIT, CommonCssConstants.CURRENTCOLOR),
                new CssCmykAwareColorValidator());
        defaultValidators.put(CommonCssConstants.BACKGROUND_COLOR, colorCmykValidator);
        defaultValidators.put(CommonCssConstants.COLOR, colorCmykValidator);
        defaultValidators.put(CommonCssConstants.BORDER_COLOR, colorCmykValidator);
        defaultValidators.put(CommonCssConstants.BORDER_BOTTOM_COLOR, colorCmykValidator);
        defaultValidators.put(CommonCssConstants.BORDER_TOP_COLOR, colorCmykValidator);
        defaultValidators.put(CommonCssConstants.BORDER_LEFT_COLOR, colorCmykValidator);
        defaultValidators.put(CommonCssConstants.BORDER_RIGHT_COLOR, colorCmykValidator);
    }
}
