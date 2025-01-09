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
package com.itextpdf.styledxmlparser.css.util;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.colors.gradients.AbstractLinearGradientBuilder;
import com.itextpdf.kernel.colors.gradients.GradientSpreadMethod;
import com.itextpdf.kernel.colors.gradients.GradientColorStop;
import com.itextpdf.kernel.colors.gradients.GradientColorStop.HintOffsetType;
import com.itextpdf.kernel.colors.gradients.GradientColorStop.OffsetType;
import com.itextpdf.kernel.colors.gradients.StrategyBasedLinearGradientBuilder;
import com.itextpdf.kernel.colors.gradients.StrategyBasedLinearGradientBuilder.GradientStrategy;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.styledxmlparser.css.parse.CssDeclarationValueTokenizer;
import com.itextpdf.styledxmlparser.css.parse.CssDeclarationValueTokenizer.Token;
import com.itextpdf.styledxmlparser.css.parse.CssDeclarationValueTokenizer.TokenType;
import com.itextpdf.styledxmlparser.exceptions.StyledXMLParserException;

import java.util.ArrayList;
import java.util.List;

/**
 * Utilities class for CSS gradient functions parsing.
 */
public final class CssGradientUtil {

    private static final String LINEAR_GRADIENT_FUNCTION_SUFFIX = "linear-gradient(";
    private static final String REPEATING_LINEAR_GRADIENT_FUNCTION_SUFFIX =
            "repeating-" + LINEAR_GRADIENT_FUNCTION_SUFFIX;

    private CssGradientUtil() {
    }

    /**
     * Checks whether the provided value is a linear gradient or repeating linear gradient function.
     * This method does not check the validity of arguments list.
     *
     * @param cssValue the value to check
     * @return {@code true} if the provided argument is the linear gradient
     * or repeating linear gradient function (even if the arguments list is invalid)
     */
    public static boolean isCssLinearGradientValue(String cssValue) {
        if (cssValue == null) {
            return false;
        }
        String normalizedValue = cssValue.toLowerCase().trim();
        return normalizedValue.endsWith(")") && (
                normalizedValue.startsWith(LINEAR_GRADIENT_FUNCTION_SUFFIX)
                        || normalizedValue.startsWith(REPEATING_LINEAR_GRADIENT_FUNCTION_SUFFIX)
        );
    }

    /**
     * Parses the provided linear gradient or repeating linear gradient function
     * @param cssGradientValue the value to parse
     * @param emValue the current element's em value
     * @param remValue the current element's rem value
     * @return the {@link StrategyBasedLinearGradientBuilder} constructed from the parsed linear gradient
     * or {@code null} if the argument value is not a linear gradient or repeating linear gradient
     * function
     * @throws StyledXMLParserException if the provided argument is a linear gradient
     * or repeating linear gradient function, but the arguments list is invalid
     */
    public static StrategyBasedLinearGradientBuilder parseCssLinearGradient(String cssGradientValue,
            float emValue, float remValue) {
        if (isCssLinearGradientValue(cssGradientValue)) {
            cssGradientValue = cssGradientValue.toLowerCase().trim();
            boolean isRepeating = false;
            String argumentsPart = null;
            if (cssGradientValue.startsWith(LINEAR_GRADIENT_FUNCTION_SUFFIX)) {
                argumentsPart = cssGradientValue.substring(
                        LINEAR_GRADIENT_FUNCTION_SUFFIX.length(), cssGradientValue.length() - 1);
                isRepeating = false;
            } else if (cssGradientValue.startsWith(REPEATING_LINEAR_GRADIENT_FUNCTION_SUFFIX)) {
                argumentsPart = cssGradientValue.substring(
                        REPEATING_LINEAR_GRADIENT_FUNCTION_SUFFIX.length(), cssGradientValue.length() - 1);
                isRepeating = true;
            }

            if (argumentsPart != null) {
                List<String> argumentsList = new ArrayList<>();
                StringBuilder buff = new StringBuilder();
                CssDeclarationValueTokenizer tokenizer = new CssDeclarationValueTokenizer(argumentsPart);
                Token nextToken;
                while ((nextToken = tokenizer.getNextValidToken()) != null) {
                    if (nextToken.getType() == TokenType.COMMA) {
                        if (buff.length() != 0) {
                            argumentsList.add(buff.toString().trim());
                            buff = new StringBuilder();
                        }
                    } else {
                        buff.append(" ").append(nextToken.getValue());
                    }
                }
                if (buff.length() != 0) {
                    argumentsList.add(buff.toString().trim());
                }
                if (argumentsList.isEmpty()) {
                    throw new StyledXMLParserException(MessageFormatUtil.format(
                            StyledXMLParserException.INVALID_GRADIENT_FUNCTION_ARGUMENTS_LIST, cssGradientValue));
                }
                return parseCssLinearGradient(argumentsList, isRepeating, emValue, remValue);
            }
        }
        return null;
    }

    private static StrategyBasedLinearGradientBuilder parseCssLinearGradient(List<String> argumentsList,
            boolean isRepeating, float emValue, float remValue) {
        StrategyBasedLinearGradientBuilder builder = new StrategyBasedLinearGradientBuilder();

        GradientSpreadMethod gradientSpreadMethod = isRepeating ? GradientSpreadMethod.REPEAT : GradientSpreadMethod.PAD;
        builder.setSpreadMethod(gradientSpreadMethod);

        int colorStopListStartIndex;
        String firstArgument = argumentsList.get(0);
        if (CssTypesValidationUtils.isAngleValue(firstArgument)) {
            double radAngle = CssDimensionParsingUtils.parseAngle(firstArgument);
            // we need to negate the angle as css specifies the clockwise rotation angle
            builder.setGradientDirectionAsCentralRotationAngle(-radAngle);
            colorStopListStartIndex = 1;
        } else if (firstArgument.startsWith("to ")) {
            GradientStrategy gradientStrategy = parseDirection(firstArgument);
            builder.setGradientDirectionAsStrategy(gradientStrategy);
            colorStopListStartIndex = 1;
        } else {
            // default angle = `to bottom`
            builder.setGradientDirectionAsStrategy(GradientStrategy.TO_BOTTOM);
            colorStopListStartIndex = 0;
        }

        addStopColors(builder, argumentsList, colorStopListStartIndex, emValue, remValue);

        return builder;
    }

    private static void addStopColors(AbstractLinearGradientBuilder builder, List<String> argumentsList,
            int stopsStartIndex, float emValue, float remValue) {
        GradientColorStop lastCreatedStopColor = null;
        int lastStopIndex = argumentsList.size() - 1;
        for (int i = stopsStartIndex; i <= lastStopIndex; ++i) {
            String argument = argumentsList.get(i);
            List<String> elementsList = new ArrayList<>();
            CssDeclarationValueTokenizer tokenizer = new CssDeclarationValueTokenizer(argument);
            Token nextToken;
            while ((nextToken = tokenizer.getNextValidToken()) != null) {
                elementsList.add(nextToken.getValue());
            }
            // cases: color, color + offset, color + offset + offset, offset (hint)
            if (elementsList.isEmpty() || elementsList.size() > 3) {
                throw new StyledXMLParserException(
                        MessageFormatUtil.format(StyledXMLParserException.INVALID_GRADIENT_COLOR_STOP_VALUE, argument));
            }
            if (CssTypesValidationUtils.isColorProperty(elementsList.get(0))) {
                float[] rgba = CssDimensionParsingUtils.parseRgbaColor(elementsList.get(0));
                if (elementsList.size() == 1) {
                    UnitValue offset = i == stopsStartIndex
                            ? new UnitValue(UnitValue.PERCENT, 0f)
                            : i == lastStopIndex
                                    ? new UnitValue(UnitValue.PERCENT, 100f)
                                    : null;
                    lastCreatedStopColor = createStopColor(rgba, offset);
                    builder.addColorStop(lastCreatedStopColor);
                } else {
                    for (int j = 1; j < elementsList.size(); ++j) {
                        if (CssTypesValidationUtils.isNumber(elementsList.get(j))) {
                            // the numeric value is invalid in linear gradient function.
                            // So check it here as parsing method will use the default pt metric
                            throw new StyledXMLParserException(MessageFormatUtil
                                    .format(StyledXMLParserException.INVALID_GRADIENT_COLOR_STOP_VALUE, argument));
                        }
                        UnitValue offset = CssDimensionParsingUtils
                                .parseLengthValueToPt(elementsList.get(j), emValue, remValue);
                        if (offset == null) {
                            throw new StyledXMLParserException(MessageFormatUtil
                                    .format(StyledXMLParserException.INVALID_GRADIENT_COLOR_STOP_VALUE, argument));
                        }
                        lastCreatedStopColor = createStopColor(rgba, offset);
                        builder.addColorStop(lastCreatedStopColor);
                    }
                }
            } else {
                // it should be a color hint case
                if (elementsList.size() != 1
                        || lastCreatedStopColor == null
                        || lastCreatedStopColor.getHintOffsetType() != HintOffsetType.NONE
                        || i == lastStopIndex) {
                    // hint is not a single value, or no color at the beginning,
                    // or two hints in a row, or hint as a last value
                    throw new StyledXMLParserException(MessageFormatUtil
                            .format(StyledXMLParserException.INVALID_GRADIENT_COLOR_STOP_VALUE, argument));
                }
                UnitValue hint = CssDimensionParsingUtils.parseLengthValueToPt(elementsList.get(0), emValue, remValue);
                if (hint == null) {
                    throw new StyledXMLParserException(MessageFormatUtil
                            .format(StyledXMLParserException.INVALID_GRADIENT_COLOR_STOP_VALUE, argument));
                }
                if (hint.getUnitType() == UnitValue.PERCENT) {
                    lastCreatedStopColor.setHint(hint.getValue() / 100, HintOffsetType.RELATIVE_ON_GRADIENT);
                } else {
                    lastCreatedStopColor.setHint(hint.getValue(), HintOffsetType.ABSOLUTE_ON_GRADIENT);
                }
            }
        }
    }

    private static GradientStrategy parseDirection(String argument) {
        String[] elementsList = argument.split("\\s+");
        if (elementsList.length < 2) {
            throw new StyledXMLParserException(MessageFormatUtil
                    .format(StyledXMLParserException.INVALID_GRADIENT_TO_SIDE_OR_CORNER_STRING, argument));
        }
        int topCount = 0;
        int bottomCount = 0;
        int leftCount = 0;
        int rightCount = 0;
        for (int i = 1; i < elementsList.length; ++i) {
            if (CommonCssConstants.TOP.equals(elementsList[i])) {
                ++topCount;
            } else if (CommonCssConstants.BOTTOM.equals(elementsList[i])) {
                ++bottomCount;
            } else if (CommonCssConstants.LEFT.equals(elementsList[i])) {
                ++leftCount;
            } else if (CommonCssConstants.RIGHT.equals(elementsList[i])) {
                ++rightCount;
            } else {
                throw new StyledXMLParserException(MessageFormatUtil
                        .format(StyledXMLParserException.INVALID_GRADIENT_TO_SIDE_OR_CORNER_STRING, argument));
            }
        }

        if (topCount == 1 && bottomCount == 0) {
            if (leftCount == 1 && rightCount == 0) {
                return GradientStrategy.TO_TOP_LEFT;
            } else if (leftCount == 0 && rightCount == 1) {
                return GradientStrategy.TO_TOP_RIGHT;
            } else if (leftCount == 0 && rightCount == 0) {
                return GradientStrategy.TO_TOP;
            }
        } else if (topCount == 0 && bottomCount == 1) {
            if (leftCount == 1 && rightCount == 0) {
                return GradientStrategy.TO_BOTTOM_LEFT;
            } else if (leftCount == 0 && rightCount == 1) {
                return GradientStrategy.TO_BOTTOM_RIGHT;
            } else if (leftCount == 0 && rightCount == 0) {
                return GradientStrategy.TO_BOTTOM;
            }
        } else if (topCount == 0 && bottomCount == 0) {
            if (leftCount == 1 && rightCount == 0) {
                return GradientStrategy.TO_LEFT;
            } else if (leftCount == 0 && rightCount == 1) {
                return GradientStrategy.TO_RIGHT;
            }
        }
        throw new StyledXMLParserException(
                MessageFormatUtil.format(StyledXMLParserException.INVALID_GRADIENT_TO_SIDE_OR_CORNER_STRING, argument));
    }

    private static GradientColorStop createStopColor(float[] rgba, UnitValue offset) {
        OffsetType offsetType;
        double offsetValue;
        if (offset == null) {
            offsetType = OffsetType.AUTO;
            offsetValue = 0;
        } else if (offset.getUnitType() == UnitValue.POINT) {
            offsetType = OffsetType.ABSOLUTE;
            offsetValue = offset.getValue();
        } else {
            offsetType = OffsetType.RELATIVE;
            offsetValue = offset.getValue() / 100;
        }
        // TODO: DEVSIX-4136 when opacity would be implemented - check the 4th element of
        //  the rgba array and use it as the opacity
        return new GradientColorStop(rgba, offsetValue, offsetType);
    }
}
