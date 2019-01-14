/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.styledxmlparser.css.validate;


import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.styledxmlparser.css.CssDeclaration;
import com.itextpdf.styledxmlparser.css.validate.impl.datatype.CssColorValidator;
import com.itextpdf.styledxmlparser.css.validate.impl.datatype.CssEnumValidator;
import com.itextpdf.styledxmlparser.css.validate.impl.datatype.CssQuotesValidator;
import com.itextpdf.styledxmlparser.css.validate.impl.datatype.CssTransformValidator;
import com.itextpdf.styledxmlparser.css.validate.impl.declaration.MultiTypeDeclarationValidator;
import com.itextpdf.styledxmlparser.css.validate.impl.declaration.SingleTypeDeclarationValidator;

import java.util.HashMap;
import java.util.Map;

/**
 * Class that bundles all the CSS declaration validators.
 */
public class CssDeclarationValidationMaster {

    /**
     * A map containing all the CSS declaration validators.
     */
    private static final Map<String, ICssDeclarationValidator> DEFAULT_VALIDATORS;

    static {
        // TODO lazy initialization?
        ICssDeclarationValidator colorCommonValidator = new MultiTypeDeclarationValidator(
                new CssEnumValidator(CommonCssConstants.TRANSPARENT, CommonCssConstants.INITIAL, CommonCssConstants.INHERIT, CommonCssConstants.CURRENTCOLOR),
                new CssColorValidator());

        DEFAULT_VALIDATORS = new HashMap<>();
        DEFAULT_VALIDATORS.put(CommonCssConstants.BACKGROUND_COLOR, colorCommonValidator);
        DEFAULT_VALIDATORS.put(CommonCssConstants.COLOR, colorCommonValidator);
        DEFAULT_VALIDATORS.put(CommonCssConstants.BORDER_COLOR, colorCommonValidator);
        DEFAULT_VALIDATORS.put(CommonCssConstants.BORDER_BOTTOM_COLOR, colorCommonValidator);
        DEFAULT_VALIDATORS.put(CommonCssConstants.BORDER_TOP_COLOR, colorCommonValidator);
        DEFAULT_VALIDATORS.put(CommonCssConstants.BORDER_LEFT_COLOR, colorCommonValidator);
        DEFAULT_VALIDATORS.put(CommonCssConstants.BORDER_RIGHT_COLOR, colorCommonValidator);
        DEFAULT_VALIDATORS.put(CommonCssConstants.FLOAT,
                new SingleTypeDeclarationValidator(
                        new CssEnumValidator(CommonCssConstants.LEFT, CommonCssConstants.RIGHT, CommonCssConstants.NONE, CommonCssConstants.INHERIT, CommonCssConstants.CENTER /*center comes from legacy*/)));
        DEFAULT_VALIDATORS.put(CommonCssConstants.PAGE_BREAK_BEFORE,
                new SingleTypeDeclarationValidator(
                        new CssEnumValidator(CommonCssConstants.AUTO, CommonCssConstants.ALWAYS, CommonCssConstants.AVOID, CommonCssConstants.LEFT, CommonCssConstants.RIGHT)));
        DEFAULT_VALIDATORS.put(CommonCssConstants.PAGE_BREAK_AFTER,
                new SingleTypeDeclarationValidator(
                        new CssEnumValidator(CommonCssConstants.AUTO, CommonCssConstants.ALWAYS, CommonCssConstants.AVOID, CommonCssConstants.LEFT, CommonCssConstants.RIGHT)));
        DEFAULT_VALIDATORS.put(CommonCssConstants.QUOTES,
                new MultiTypeDeclarationValidator(
                        new CssEnumValidator(CommonCssConstants.INITIAL, CommonCssConstants.INHERIT, CommonCssConstants.NONE),
                        new CssQuotesValidator()));
        DEFAULT_VALIDATORS.put(CommonCssConstants.TRANSFORM,
                new SingleTypeDeclarationValidator(new CssTransformValidator()));
    }

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
        ICssDeclarationValidator validator = DEFAULT_VALIDATORS.get(declaration.getProperty());
        return validator == null || validator.isValid(declaration);
    }

}
