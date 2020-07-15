package com.itextpdf.styledxmlparser.css.validate.impl.datatype;

import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.styledxmlparser.css.util.CssUtils;
import com.itextpdf.styledxmlparser.css.validate.ICssDataTypeValidator;

/**
 * {@link ICssDataTypeValidator} implementation for numeric elements.
 */
public class CssNumericValueValidator implements ICssDataTypeValidator {

    private final boolean allowedPercent;
    private final boolean allowedNormal;

    /**
     * Creates a new {@link CssNumericValueValidator} instance.
     *
     * @param allowedPercent is percent value allowed
     * @param allowedNormal is 'normal' value allowed
     */
    public CssNumericValueValidator(final boolean allowedPercent, final boolean allowedNormal) {
        this.allowedPercent = allowedPercent;
        this.allowedNormal = allowedNormal;
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
        if (CommonCssConstants.NORMAL.equals(objectString)) {
            return this.allowedNormal;
        }
        if (!CssUtils.isValidNumericValue(objectString)) {
            return false;
        }
        if (CssUtils.isPercentageValue(objectString)) {
            return this.allowedPercent;
        }
        return true;
    }
}
