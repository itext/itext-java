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
import com.itextpdf.styledxmlparser.css.CssDeclaration;
import com.itextpdf.styledxmlparser.css.validate.ICssDeclarationValidator;
import com.itextpdf.styledxmlparser.css.validate.impl.datatype.ArrayDataTypeValidator;
import com.itextpdf.styledxmlparser.css.validate.impl.datatype.CssBackgroundValidator;
import com.itextpdf.styledxmlparser.css.validate.impl.datatype.CssBlendModeValidator;
import com.itextpdf.styledxmlparser.css.validate.impl.datatype.CssColorValidator;
import com.itextpdf.styledxmlparser.css.validate.impl.datatype.CssEnumValidator;
import com.itextpdf.styledxmlparser.css.validate.impl.datatype.CssIntegerNumberValueValidator;
import com.itextpdf.styledxmlparser.css.validate.impl.datatype.CssLengthValueValidator;
import com.itextpdf.styledxmlparser.css.validate.impl.datatype.CssNumberValueValidator;
import com.itextpdf.styledxmlparser.css.validate.impl.datatype.CssPercentageValueValidator;
import com.itextpdf.styledxmlparser.css.validate.impl.datatype.CssQuotesValidator;
import com.itextpdf.styledxmlparser.css.validate.impl.datatype.CssTransformValidator;
import com.itextpdf.styledxmlparser.css.validate.impl.declaration.MultiTypeDeclarationValidator;
import com.itextpdf.styledxmlparser.css.validate.impl.declaration.SingleTypeDeclarationValidator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import static com.itextpdf.styledxmlparser.css.CommonCssConstants.BORDER_WIDTH_VALUES;

/**
 * Class that bundles all the CSS declaration validators.
 * It validates CSS declarations against the accepted html/css standard.
 */
public class CssDefaultValidator implements ICssDeclarationValidator {
    /**
     * A map containing all the CSS declaration validators.
     */
    protected final Map<String, ICssDeclarationValidator> defaultValidators;

    private static final ICssDeclarationValidator colorCommonValidator = new MultiTypeDeclarationValidator(
            new CssEnumValidator(CommonCssConstants.TRANSPARENT, CommonCssConstants.INITIAL,
                    CommonCssConstants.INHERIT, CommonCssConstants.CURRENTCOLOR),
            new CssColorValidator());

    public CssDefaultValidator() {
        final CssEnumValidator normalValidator = new CssEnumValidator(CommonCssConstants.NORMAL);
        final CssEnumValidator relativeSizeValidator =
                new CssEnumValidator(CommonCssConstants.LARGER, CommonCssConstants.SMALLER);
        final CssEnumValidator absoluteSizeValidator = new CssEnumValidator();
        absoluteSizeValidator.addAllowedValues(CommonCssConstants.FONT_ABSOLUTE_SIZE_KEYWORDS_VALUES.keySet());
        final CssEnumValidator inheritInitialUnsetValidator = new CssEnumValidator(
                CommonCssConstants.INHERIT, CommonCssConstants.INITIAL, CommonCssConstants.UNSET);

        defaultValidators = new HashMap<>();
        defaultValidators.put(CommonCssConstants.BACKGROUND_COLOR, colorCommonValidator);
        defaultValidators.put(CommonCssConstants.COLOR, colorCommonValidator);
        defaultValidators.put(CommonCssConstants.BORDER_COLOR, colorCommonValidator);
        defaultValidators.put(CommonCssConstants.BORDER_BOTTOM_COLOR, colorCommonValidator);
        defaultValidators.put(CommonCssConstants.BORDER_TOP_COLOR, colorCommonValidator);
        defaultValidators.put(CommonCssConstants.BORDER_LEFT_COLOR, colorCommonValidator);
        defaultValidators.put(CommonCssConstants.BORDER_RIGHT_COLOR, colorCommonValidator);
        defaultValidators.put(CommonCssConstants.FLOAT,
                new SingleTypeDeclarationValidator(
                        new CssEnumValidator(CommonCssConstants.LEFT, CommonCssConstants.RIGHT, CommonCssConstants.NONE,
                                CommonCssConstants.INHERIT, CommonCssConstants.CENTER /*center comes from legacy*/)));
        defaultValidators.put(CommonCssConstants.PAGE_BREAK_BEFORE,
                new SingleTypeDeclarationValidator(
                        new CssEnumValidator(CommonCssConstants.AUTO, CommonCssConstants.ALWAYS,
                                CommonCssConstants.AVOID, CommonCssConstants.LEFT, CommonCssConstants.RIGHT)));
        defaultValidators.put(CommonCssConstants.PAGE_BREAK_AFTER,
                new SingleTypeDeclarationValidator(
                        new CssEnumValidator(CommonCssConstants.AUTO, CommonCssConstants.ALWAYS,
                                CommonCssConstants.AVOID, CommonCssConstants.LEFT, CommonCssConstants.RIGHT)));
        defaultValidators.put(CommonCssConstants.QUOTES,
                new MultiTypeDeclarationValidator(
                        new CssEnumValidator(CommonCssConstants.INITIAL, CommonCssConstants.INHERIT,
                                CommonCssConstants.NONE),
                        new CssQuotesValidator()));
        defaultValidators.put(CommonCssConstants.TRANSFORM,
                new SingleTypeDeclarationValidator(new CssTransformValidator()));

        defaultValidators.put(CommonCssConstants.FONT_SIZE, new MultiTypeDeclarationValidator(
                new CssLengthValueValidator(false), new CssPercentageValueValidator(false), relativeSizeValidator,
                absoluteSizeValidator));
        defaultValidators.put(CommonCssConstants.WORD_SPACING,
                new MultiTypeDeclarationValidator(new CssLengthValueValidator(true), normalValidator));
        defaultValidators.put(CommonCssConstants.LETTER_SPACING,
                new MultiTypeDeclarationValidator(new CssLengthValueValidator(true), normalValidator));
        defaultValidators.put(CommonCssConstants.TEXT_INDENT,
                new MultiTypeDeclarationValidator(new CssLengthValueValidator(true),
                        new CssPercentageValueValidator(true),
                        new CssEnumValidator(CommonCssConstants.EACH_LINE, CommonCssConstants.HANGING,
                                CommonCssConstants.HANGING + " " + CommonCssConstants.EACH_LINE)));
        addColumnRuleValidation(defaultValidators);
        defaultValidators.put(CommonCssConstants.LINE_HEIGHT,
                new MultiTypeDeclarationValidator(new CssNumberValueValidator(false),
                        new CssLengthValueValidator(false), new CssPercentageValueValidator(false), normalValidator,
                        inheritInitialUnsetValidator));
        final MultiTypeDeclarationValidator gapValidator = new MultiTypeDeclarationValidator(
                new CssLengthValueValidator(false), new CssPercentageValueValidator(false), normalValidator,
                inheritInitialUnsetValidator);
        defaultValidators.put(CommonCssConstants.COLUMN_GAP, gapValidator);
        defaultValidators.put(CommonCssConstants.GRID_COLUMN_GAP, gapValidator);
        defaultValidators.put(CommonCssConstants.COLUMN_WIDTH,
                new MultiTypeDeclarationValidator(new CssLengthValueValidator(false),
                        new CssPercentageValueValidator(false), new CssEnumValidator(CommonCssConstants.AUTO)));
        defaultValidators.put(CommonCssConstants.COLUMN_COUNT, new MultiTypeDeclarationValidator(
                new CssIntegerNumberValueValidator(false, false), new CssEnumValidator(CommonCssConstants.AUTO)));
        defaultValidators.put(CommonCssConstants.ROW_GAP, gapValidator);
        defaultValidators.put(CommonCssConstants.GRID_ROW_GAP, gapValidator);
        defaultValidators.put(CommonCssConstants.FLEX_GROW, new MultiTypeDeclarationValidator(
                new CssNumberValueValidator(false), inheritInitialUnsetValidator));
        defaultValidators.put(CommonCssConstants.FLEX_SHRINK, new MultiTypeDeclarationValidator(
                new CssNumberValueValidator(false), inheritInitialUnsetValidator));
        final CssEnumValidator flexBasisEnumValidator = new CssEnumValidator(CommonCssConstants.AUTO,
                CommonCssConstants.CONTENT, CommonCssConstants.MIN_CONTENT, CommonCssConstants.MAX_CONTENT,
                CommonCssConstants.FIT_CONTENT);
        defaultValidators.put(CommonCssConstants.FLEX_BASIS, new MultiTypeDeclarationValidator(
                new CssLengthValueValidator(false), new CssPercentageValueValidator(false), flexBasisEnumValidator));
        defaultValidators.put(CommonCssConstants.BACKGROUND_REPEAT, new SingleTypeDeclarationValidator(
                new CssBackgroundValidator(CommonCssConstants.BACKGROUND_REPEAT)));
        defaultValidators.put(CommonCssConstants.BACKGROUND_IMAGE, new SingleTypeDeclarationValidator(
                new CssBackgroundValidator(CommonCssConstants.BACKGROUND_IMAGE)));
        defaultValidators.put(CommonCssConstants.BACKGROUND_POSITION_X, new SingleTypeDeclarationValidator(
                new CssBackgroundValidator(CommonCssConstants.BACKGROUND_POSITION_X)));
        defaultValidators.put(CommonCssConstants.BACKGROUND_POSITION_Y, new SingleTypeDeclarationValidator(
                new CssBackgroundValidator(CommonCssConstants.BACKGROUND_POSITION_Y)));
        defaultValidators.put(CommonCssConstants.BACKGROUND_SIZE, new SingleTypeDeclarationValidator(
                new CssBackgroundValidator(CommonCssConstants.BACKGROUND_SIZE)));
        defaultValidators.put(CommonCssConstants.BACKGROUND_CLIP, new SingleTypeDeclarationValidator(
                new CssBackgroundValidator(CommonCssConstants.BACKGROUND_CLIP)));
        defaultValidators.put(CommonCssConstants.BACKGROUND_ORIGIN, new SingleTypeDeclarationValidator(
                new CssBackgroundValidator(CommonCssConstants.BACKGROUND_ORIGIN)));
        defaultValidators.put(CommonCssConstants.BACKGROUND_BLEND_MODE, new SingleTypeDeclarationValidator(
                new ArrayDataTypeValidator(new CssBlendModeValidator())));
        defaultValidators.put(CommonCssConstants.OVERFLOW_WRAP, new MultiTypeDeclarationValidator(
                new CssEnumValidator(CommonCssConstants.ANYWHERE, CommonCssConstants.BREAK_WORD),
                normalValidator, inheritInitialUnsetValidator));
        defaultValidators.put(CommonCssConstants.WORD_BREAK, new MultiTypeDeclarationValidator(
                new CssEnumValidator(CommonCssConstants.BREAK_ALL, CommonCssConstants.KEEP_ALL,
                        CommonCssConstants.BREAK_WORD),
                normalValidator,
                inheritInitialUnsetValidator));
        defaultValidators.put(CommonCssConstants.FLEX_DIRECTION, new MultiTypeDeclarationValidator(
                new CssEnumValidator(CommonCssConstants.ROW, CommonCssConstants.ROW_REVERSE,
                        CommonCssConstants.COLUMN, CommonCssConstants.COLUMN_REVERSE),
                inheritInitialUnsetValidator));
        defaultValidators.put(CommonCssConstants.FLEX_WRAP, new MultiTypeDeclarationValidator(
                new CssEnumValidator(CommonCssConstants.NOWRAP, CommonCssConstants.WRAP,
                        CommonCssConstants.WRAP_REVERSE),
                inheritInitialUnsetValidator));
        defaultValidators.put(CommonCssConstants.ALIGN_ITEMS, new MultiTypeDeclarationValidator(
                normalValidator,
                new CssEnumValidator(Arrays.asList(CommonCssConstants.BASELINE),
                        Arrays.asList(CommonCssConstants.FIRST, CommonCssConstants.LAST)),
                new CssEnumValidator(
                        Arrays.asList(CommonCssConstants.STRETCH, CommonCssConstants.CENTER, CommonCssConstants.START,
                                CommonCssConstants.END, CommonCssConstants.FLEX_START, CommonCssConstants.FLEX_END,
                                CommonCssConstants.SELF_START, CommonCssConstants.SELF_END),
                        Arrays.asList(CommonCssConstants.SAFE, CommonCssConstants.UNSAFE)),
                inheritInitialUnsetValidator));
        defaultValidators.put(CommonCssConstants.JUSTIFY_CONTENT, new MultiTypeDeclarationValidator(
                new CssEnumValidator(Arrays.asList(
                        CommonCssConstants.SPACE_AROUND, CommonCssConstants.SPACE_BETWEEN,
                        CommonCssConstants.SPACE_EVENLY, CommonCssConstants.STRETCH, CommonCssConstants.NORMAL,
                        CommonCssConstants.LEFT, CommonCssConstants.RIGHT)),
                new CssEnumValidator(Arrays.asList(
                        CommonCssConstants.CENTER, CommonCssConstants.START, CommonCssConstants.FLEX_START,
                        CommonCssConstants.SELF_START, CommonCssConstants.END, CommonCssConstants.FLEX_END,
                        CommonCssConstants.SELF_END),
                        Arrays.asList(CommonCssConstants.SAFE, CommonCssConstants.UNSAFE)),
                inheritInitialUnsetValidator
        ));
        defaultValidators.put(CommonCssConstants.JUSTIFY_ITEMS, new MultiTypeDeclarationValidator(
                normalValidator,
                new CssEnumValidator(Arrays.asList(CommonCssConstants.BASELINE),
                        Arrays.asList(CommonCssConstants.FIRST, CommonCssConstants.LAST)),
                new CssEnumValidator(
                        Arrays.asList(CommonCssConstants.STRETCH, CommonCssConstants.CENTER, CommonCssConstants.START,
                                CommonCssConstants.END, CommonCssConstants.FLEX_START, CommonCssConstants.FLEX_END,
                                CommonCssConstants.SELF_START, CommonCssConstants.SELF_END, CommonCssConstants.LEFT,
                                CommonCssConstants.RIGHT),
                        Arrays.asList(CommonCssConstants.SAFE, CommonCssConstants.UNSAFE)),
                new CssEnumValidator(CommonCssConstants.LEGACY,
                        CommonCssConstants.LEGACY + " " + CommonCssConstants.LEFT,
                        CommonCssConstants.LEGACY + " " + CommonCssConstants.RIGHT,
                        CommonCssConstants.LEGACY + " " + CommonCssConstants.CENTER),
                inheritInitialUnsetValidator));
    }

    /**
     * Validates a CSS declaration.
     *
     * @param declaration the CSS declaration
     *
     * @return true, if the validation was successful
     */
    @Override
    public boolean isValid(CssDeclaration declaration) {
        ICssDeclarationValidator validator = defaultValidators.get(declaration.getProperty());
        return validator == null || validator.isValid(declaration);
    }

    private static void addColumnRuleValidation(Map<String, ICssDeclarationValidator> container) {
        container.put(CommonCssConstants.COLUMN_RULE_COLOR, colorCommonValidator);
        container.put(CommonCssConstants.COLUMN_RULE_WIDTH,
                new MultiTypeDeclarationValidator(
                        new CssNumberValueValidator(false),
                        new CssLengthValueValidator(false),
                        new CssEnumValidator(BORDER_WIDTH_VALUES),
                        new CssEnumValidator(CommonCssConstants.AUTO)));
        container.put(CommonCssConstants.COLUMN_RULE_STYLE,
                new MultiTypeDeclarationValidator(
                        new CssEnumValidator(CommonCssConstants.BORDER_STYLE_VALUES)
                ));

    }
}
