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
