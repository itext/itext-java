/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
import com.itextpdf.commons.utils.StringNormalizer;
import com.itextpdf.kernel.colors.gradients.AbstractGradientBuilder;
import com.itextpdf.kernel.colors.gradients.GradientSpreadMethod;
import com.itextpdf.kernel.colors.gradients.GradientColorStop;
import com.itextpdf.kernel.colors.gradients.GradientColorStop.HintOffsetType;
import com.itextpdf.kernel.colors.gradients.GradientColorStop.OffsetType;
import com.itextpdf.kernel.colors.gradients.IGradientBuilder;
import com.itextpdf.kernel.colors.gradients.StrategyBasedLinearGradientBuilder;
import com.itextpdf.kernel.colors.gradients.StrategyBasedLinearGradientBuilder.GradientStrategy;
import com.itextpdf.kernel.colors.gradients.StrategyBasedRadialGradientBuilder;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.styledxmlparser.css.parse.CssDeclarationValueTokenizer;
import com.itextpdf.styledxmlparser.css.parse.CssDeclarationValueTokenizer.Token;
import com.itextpdf.styledxmlparser.css.parse.CssDeclarationValueTokenizer.TokenType;
import com.itextpdf.styledxmlparser.exceptions.StyledXMLParserException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utilities class for CSS gradient functions parsing.
 */
public final class CssGradientUtil {

    private static final String LINEAR_GRADIENT_FUNCTION_SUFFIX = "linear-gradient(";
    private static final String REPEATING_LINEAR_GRADIENT_FUNCTION_SUFFIX =
            "repeating-" + LINEAR_GRADIENT_FUNCTION_SUFFIX;
    private static final String RADIAL_GRADIENT_FUNCTION_SUFFIX = "radial-gradient(";
    private static final String REPEATING_RADIAL_GRADIENT_FUNCTION_SUFFIX =
            "repeating-" + RADIAL_GRADIENT_FUNCTION_SUFFIX;
    private static final List<String> RADIAL_EXTENT_KEYWORDS = Collections.unmodifiableList(Arrays.asList(
            CommonCssConstants.CLOSEST_CORNER, CommonCssConstants.CLOSEST_SIDE,
            CommonCssConstants.FARTHEST_CORNER, CommonCssConstants.FARTHEST_SIDE));
    private static final Map<String, StrategyBasedRadialGradientBuilder.GradientStrategy> RADIAL_EXTENT_STRATEGIES =
            createRadialExtentStrategies();

    private CssGradientUtil() {
    }

    /**
     * Checks whether the provided value is a linear gradient or repeating linear gradient function.
     * This method does not check the validity of arguments list.
     *
     * @param cssValue the value to check
     *
     * @return {@code true} if the provided argument is the linear gradient
     * or repeating linear gradient function (even if the arguments list is invalid)
     */
    public static boolean isCssLinearGradientValue(String cssValue) {
        if (cssValue == null) {
            return false;
        }
        String normalizedValue = StringNormalizer.normalize(cssValue);
        return normalizedValue.endsWith(")") && (
                normalizedValue.startsWith(LINEAR_GRADIENT_FUNCTION_SUFFIX)
                        || normalizedValue.startsWith(REPEATING_LINEAR_GRADIENT_FUNCTION_SUFFIX)
        );
    }

    /**
     * Checks whether the provided value is a radial gradient or repeating radial gradient function.
     * This method does not check the validity of arguments list.
     *
     * @param cssValue the value to check
     *
     * @return {@code true} if the provided argument is the radial gradient
     * or repeating radial gradient function (even if the arguments list is invalid)
     */
    public static boolean isCssRadialGradientValue(String cssValue) {
        if (cssValue == null) {
            return false;
        }
        String normalizedValue = StringNormalizer.normalize(cssValue);
        return normalizedValue.endsWith(")") && (
                normalizedValue.startsWith(RADIAL_GRADIENT_FUNCTION_SUFFIX)
                        || normalizedValue.startsWith(REPEATING_RADIAL_GRADIENT_FUNCTION_SUFFIX)
        );
    }

    /**
     * Checks whether the provided value is a gradient function (linear or radial).
     * This method does not check the validity of arguments list.
     *
     * @param cssValue the value to check
     *
     * @return {@code true} if the provided argument is a gradient function
     */
    public static boolean isCssGradientValue(String cssValue) {
        return isCssLinearGradientValue(cssValue) || isCssRadialGradientValue(cssValue);
    }

    /**
     * Parses the provided gradient function (linear or radial).
     *
     * @param cssGradientValue the value to parse
     * @param emValue the current element's em value
     * @param remValue the current element's rem value
     *
     * @return the {@link IGradientBuilder} constructed from the parsed gradient
     * or {@code null} if the argument value is not a gradient function
     *
     * @throws StyledXMLParserException if the provided argument is a gradient function,
     * but the arguments list is invalid
     */
    public static IGradientBuilder parseCssGradient(String cssGradientValue,
            float emValue, float remValue) {
        if (isCssLinearGradientValue(cssGradientValue)) {
            return parseCssLinearGradient(cssGradientValue, emValue, remValue);
        } else if (isCssRadialGradientValue(cssGradientValue)) {
            return parseCssRadialGradient(cssGradientValue, emValue, remValue);
        }
        return null;
    }

    /**
     * Parses the provided linear gradient or repeating linear gradient function.
     *
     * @param cssGradientValue the value to parse
     * @param emValue the current element's em value
     * @param remValue the current element's rem value
     *
     * @return the {@link StrategyBasedLinearGradientBuilder} constructed from the parsed linear gradient
     * or {@code null} if the argument value is not a linear gradient or repeating linear gradient
     * function
     *
     * @throws StyledXMLParserException if the provided argument is a linear gradient
     * or repeating linear gradient function, but the arguments list is invalid
     */
    public static StrategyBasedLinearGradientBuilder parseCssLinearGradient(String cssGradientValue,
            float emValue, float remValue) {
        if (!isCssLinearGradientValue(cssGradientValue)) {
            return null;
        }

        cssGradientValue = StringNormalizer.normalize(cssGradientValue);
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
            List<String> argumentsList = extractGradientArguments(argumentsPart, cssGradientValue);
            return parseCssLinearGradient(argumentsList, isRepeating, emValue, remValue);
        }

        return null;
    }

    /**
     * Parses the provided radial gradient or repeating radial gradient function.
     *
     * @param cssGradientValue the value to parse
     * @param emValue the current element's em value
     * @param remValue the current element's rem value
     *
     * @return the {@link StrategyBasedRadialGradientBuilder} constructed from the parsed radial gradient
     * or {@code null} if the argument value is not a radial gradient or repeating radial gradient function
     *
     * @throws StyledXMLParserException if the provided argument is a radial gradient
     * or repeating radial gradient function, but the arguments list is invalid
     */
    public static StrategyBasedRadialGradientBuilder parseCssRadialGradient(String cssGradientValue,
            float emValue, float remValue) {
        if (!isCssRadialGradientValue(cssGradientValue)) {
            return null;
        }

        cssGradientValue = StringNormalizer.normalize(cssGradientValue);
        boolean isRepeating;
        String argumentsPart;
        if (cssGradientValue.startsWith(RADIAL_GRADIENT_FUNCTION_SUFFIX)) {
            argumentsPart = cssGradientValue.substring(
                    RADIAL_GRADIENT_FUNCTION_SUFFIX.length(), cssGradientValue.length() - 1);
            isRepeating = false;
        } else if (cssGradientValue.startsWith(REPEATING_RADIAL_GRADIENT_FUNCTION_SUFFIX)) {
            argumentsPart = cssGradientValue.substring(
                    REPEATING_RADIAL_GRADIENT_FUNCTION_SUFFIX.length(), cssGradientValue.length() - 1);
            isRepeating = true;
        } else {
            return null;
        }

        List<String> argumentsList = extractGradientArguments(argumentsPart, cssGradientValue);
        return parseCssRadialGradient(argumentsList, isRepeating, emValue, remValue, cssGradientValue);
    }

    private static StrategyBasedRadialGradientBuilder parseCssRadialGradient(List<String> argumentsList,
            boolean isRepeating, float emValue, float remValue, String originalCssValue) {
        StrategyBasedRadialGradientBuilder builder = new StrategyBasedRadialGradientBuilder();
        builder.setSpread(isRepeating ? GradientSpreadMethod.REPEAT : GradientSpreadMethod.PAD);

        String firstArgument = argumentsList.get(0);
        List<String> firstArgumentTokens = tokenize(firstArgument);
        int colorStopListStartIndex = 0;
        if (firstArgumentTokens.isEmpty()) {
            if (argumentsList.size() <= 1) {
                throw new StyledXMLParserException(MessageFormatUtil.format(
                        StyledXMLParserException.INVALID_GRADIENT_FUNCTION_ARGUMENTS_LIST, originalCssValue));
            }
        } else if (!CssTypesValidationUtils.isColorProperty(firstArgumentTokens.get(0))) {
            if (argumentsList.size() <= 1) {
                throw new StyledXMLParserException(MessageFormatUtil.format(
                        StyledXMLParserException.INVALID_GRADIENT_FUNCTION_ARGUMENTS_LIST, originalCssValue));
            }
            parseRadialShapeSizeAndPosition(builder, firstArgumentTokens, firstArgument, emValue, remValue);
            colorStopListStartIndex = 1;
        }

        addStopColors(builder, argumentsList, colorStopListStartIndex, emValue, remValue);
        return builder;
    }

    private static List<String> extractGradientArguments(String argumentsPart, String cssGradientValue) {
        List<String> argumentsList = new ArrayList<>();
        StringBuilder buff = new StringBuilder();
        CssDeclarationValueTokenizer tokenizer = new CssDeclarationValueTokenizer(argumentsPart);
        Token nextToken;
        while ((nextToken = tokenizer.getNextValidToken()) != null) {
            if (nextToken.getType() == TokenType.COMMA) {
                String argument = buff.toString().trim();
                if (argument.isEmpty()) {
                    throw new StyledXMLParserException(MessageFormatUtil.format(
                            StyledXMLParserException.INVALID_GRADIENT_FUNCTION_ARGUMENTS_LIST, cssGradientValue));
                }
                argumentsList.add(argument);
                buff.setLength(0);
            } else {
                buff.append(" ").append(nextToken.getValue());
            }
        }
        String lastArgument = buff.toString().trim();
        if (lastArgument.isEmpty()) {
            throw new StyledXMLParserException(MessageFormatUtil.format(
                    StyledXMLParserException.INVALID_GRADIENT_FUNCTION_ARGUMENTS_LIST, cssGradientValue));
        }
        argumentsList.add(lastArgument);
        return argumentsList;
    }

    private static StrategyBasedLinearGradientBuilder parseCssLinearGradient(List<String> argumentsList,
            boolean isRepeating, float emValue, float remValue) {
        StrategyBasedLinearGradientBuilder builder = new StrategyBasedLinearGradientBuilder();

        GradientSpreadMethod gradientSpreadMethod = isRepeating ? GradientSpreadMethod.REPEAT : GradientSpreadMethod.PAD;
        builder.setSpread(gradientSpreadMethod);

        int colorStopListStartIndex;
        String firstArgument = argumentsList.get(0);
        List<String> firstArgumentTokens = tokenize(firstArgument);
        if (firstArgumentTokens.size() == 1 && CssTypesValidationUtils.isAngleValue(firstArgumentTokens.get(0))) {
            double radAngle = CssDimensionParsingUtils.parseAngle(firstArgumentTokens.get(0));
            // we need to negate the angle as css specifies the clockwise rotation angle
            builder.setGradientDirectionAsCentralRotationAngle(-radAngle);
            colorStopListStartIndex = 1;
        } else if (firstArgumentTokens.size() > 1 && "to".equals(firstArgumentTokens.get(0))) {
            GradientStrategy gradientStrategy = parseDirection(firstArgumentTokens, firstArgument);
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

    private static List<String> tokenize(String value) {
        List<String> tokens = new ArrayList<>();
        CssDeclarationValueTokenizer tokenizer = new CssDeclarationValueTokenizer(value);
        Token nextToken;
        while ((nextToken = tokenizer.getNextValidToken()) != null) {
            tokens.add(nextToken.getValue());
        }
        return tokens;
    }

    private static String joinTokens(List<String> tokens) {
        StringBuilder result = new StringBuilder();
        for (String token : tokens) {
            if (result.length() != 0) {
                result.append(' ');
            }
            result.append(token);
        }
        return result.toString();
    }

    private static void parseRadialShapeSizeAndPosition(StrategyBasedRadialGradientBuilder builder,
            List<String> tokens, String firstArgument, float emValue, float remValue) {
        int atIndex = -1;
        for (int i = 0; i < tokens.size(); ++i) {
            if (CommonCssConstants.AT.equals(tokens.get(i))) {
                if (atIndex != -1) {
                    throw new StyledXMLParserException(MessageFormatUtil.format(
                            StyledXMLParserException.INVALID_GRADIENT_FUNCTION_ARGUMENTS_LIST, firstArgument));
                }
                atIndex = i;
            }
        }

        List<String> shapeSizeTokens;
        if (atIndex == -1) {
            shapeSizeTokens = tokens;
        } else {
            if (atIndex == tokens.size() - 1) {
                throw new StyledXMLParserException(MessageFormatUtil.format(
                        StyledXMLParserException.INVALID_GRADIENT_FUNCTION_ARGUMENTS_LIST, firstArgument));
            }
            String position = joinTokens(tokens.subList(atIndex + 1, tokens.size()));
            CssGradientPositionUtil.parseAndSetRadialGradientCenter(builder,
                    position, emValue, remValue,firstArgument);
            shapeSizeTokens = tokens.subList(0, atIndex);
        }
        RadialSize radialSize = parseRadialSize(shapeSizeTokens, firstArgument, emValue, remValue);
        String radialShape = parseRadialShape(shapeSizeTokens, radialSize, firstArgument);
        combineRadialShapeAndSize(builder, radialShape, radialSize);
    }

    private static RadialSize parseRadialSize(List<String> shapeSizeTokens, String fullValue,
            float emValue, float remValue) {
        String extentKeyword = null;
        List<UnitValue> radii = new ArrayList<>();
        for (String token : shapeSizeTokens) {
            if (CommonCssConstants.CIRCLE.equals(token) || CommonCssConstants.ELLIPSE.equals(token)) {
                continue;
            }
            if (RADIAL_EXTENT_KEYWORDS.contains(token)) {
                if (extentKeyword != null || !radii.isEmpty()) {
                    throw new StyledXMLParserException(MessageFormatUtil.format(
                            StyledXMLParserException.INVALID_GRADIENT_FUNCTION_ARGUMENTS_LIST, fullValue));
                }
                extentKeyword = token;
            } else {
                if (extentKeyword != null || radii.size() == 2) {
                    throw new StyledXMLParserException(MessageFormatUtil.format(
                            StyledXMLParserException.INVALID_GRADIENT_FUNCTION_ARGUMENTS_LIST, fullValue));
                }
                radii.add(parseRadialRadius(token, emValue, remValue, fullValue));
            }
        }
        return new RadialSize(extentKeyword, radii);
    }

    private static String parseRadialShape(List<String> shapeSizeTokens, RadialSize radialSize,
            String fullValue) {
        String shape = null;
        for (String token : shapeSizeTokens) {
            if (CommonCssConstants.CIRCLE.equals(token) || CommonCssConstants.ELLIPSE.equals(token)) {
                if (shape != null) {
                    throw new StyledXMLParserException(MessageFormatUtil.format(
                            StyledXMLParserException.INVALID_GRADIENT_FUNCTION_ARGUMENTS_LIST, fullValue));
                }
                shape = token;
            }
        }

        int radiusCount = radialSize.radii.size();
        if (shape == null) {
            shape = radiusCount == 1 ? CommonCssConstants.CIRCLE : CommonCssConstants.ELLIPSE;
        }
        // Radius count can be 0, e.g. in "radial-gradient(circle, red, blue)".
        if ((CommonCssConstants.CIRCLE.equals(shape) && radiusCount == 2)
                || (CommonCssConstants.ELLIPSE.equals(shape) && radiusCount == 1)) {
            throw new StyledXMLParserException(MessageFormatUtil.format(
                    StyledXMLParserException.INVALID_GRADIENT_FUNCTION_ARGUMENTS_LIST, fullValue));
        }
        return shape;
    }

    private static void combineRadialShapeAndSize(StrategyBasedRadialGradientBuilder builder,
            String shape, RadialSize radialSize) {
        boolean isCircular = CommonCssConstants.CIRCLE.equals(shape);
        if (radialSize.extentKeyword != null) {
            // FromCenterStrategy is used because extent keywords
            // measure the radius from the gradient center to a side or corner.
            builder.setRadiusFromCenterStrategy(isCircular, RADIAL_EXTENT_STRATEGIES.get(radialSize.extentKeyword));
        } else if (radialSize.radii.isEmpty()) {
            builder.setRadiusFromCenterStrategy(isCircular,
                    StrategyBasedRadialGradientBuilder.GradientStrategy.FARTHEST_CORNER);
        } else if (radialSize.radii.size() == 1) {
            UnitValue radius = radialSize.radii.get(0);
            if (radius.getUnitType() == UnitValue.PERCENT) {
                builder.setRadiusRelativeToScaledDiagonal(radius.getValue() / 100D);
            } else {
                builder.setRadiusRelativeToBoundingBoxSize(radius.getValue(), false,
                        radius.getValue(), false);
            }
        } else {
            UnitValue xRadius = radialSize.radii.get(0);
            UnitValue yRadius = radialSize.radii.get(1);
            boolean xRelative = xRadius.getUnitType() == UnitValue.PERCENT;
            boolean yRelative = yRadius.getUnitType() == UnitValue.PERCENT;
            builder.setRadiusRelativeToBoundingBoxSize(
                    xRelative ? (xRadius.getValue() / 100D) : xRadius.getValue(), xRelative,
                    yRelative ? (yRadius.getValue() / 100D) : yRadius.getValue(), yRelative);
        }
    }

    private static UnitValue parseRadialRadius(String token, float emValue, float remValue, String fullValue) {
        if (CssTypesValidationUtils.isNumber(token)) {
            try {
                if (Double.parseDouble(token) == 0D) {
                    return new UnitValue(UnitValue.POINT, 0);
                }
            } catch (NumberFormatException ignored) {
                // Fall through to the common invalid-value path.
            }
            throw new StyledXMLParserException(MessageFormatUtil.format(
                    StyledXMLParserException.INVALID_GRADIENT_FUNCTION_ARGUMENTS_LIST, fullValue));
        }
        UnitValue value = CssDimensionParsingUtils.parseLengthValueToPt(token, emValue, remValue);
        if (value == null || value.getValue() < 0) {
            throw new StyledXMLParserException(MessageFormatUtil.format(
                    StyledXMLParserException.INVALID_GRADIENT_FUNCTION_ARGUMENTS_LIST, fullValue));
        }
        return value;
    }


    private static void addStopColors(AbstractGradientBuilder<?> builder, List<String> argumentsList,
            int stopsStartIndex, float emValue, float remValue) {
        GradientColorStop lastCreatedStopColor = null;
        int lastStopIndex = argumentsList.size() - 1;
        for (int i = stopsStartIndex; i <= lastStopIndex; ++i) {
            String argument = argumentsList.get(i);
            List<String> elementsList = tokenize(argument);
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
                    builder.addStopColor(lastCreatedStopColor);
                } else {
                    for (int j = 1; j < elementsList.size(); ++j) {
                        UnitValue offset = parseColorStopOffset(elementsList.get(j), emValue, remValue, argument);
                        lastCreatedStopColor = createStopColor(rgba, offset);
                        builder.addStopColor(lastCreatedStopColor);
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
                UnitValue hint = parseColorStopOffset(elementsList.get(0), emValue, remValue, argument);
                if (hint.getUnitType() == UnitValue.PERCENT) {
                    lastCreatedStopColor.setHint(hint.getValue() / 100, HintOffsetType.RELATIVE_ON_GRADIENT);
                } else {
                    lastCreatedStopColor.setHint(hint.getValue(), HintOffsetType.ABSOLUTE_ON_GRADIENT);
                }
            }
        }
    }

    private static UnitValue parseColorStopOffset(String value, float emValue, float remValue,
            String fullValue) {
        if (CssTypesValidationUtils.isNumber(value)) {
            try {
                if (Double.parseDouble(value) == 0D) {
                    return new UnitValue(UnitValue.POINT, 0);
                }
            } catch (NumberFormatException ignored) {
                // Fall through to the invalid color-stop path.
            }
            throw new StyledXMLParserException(MessageFormatUtil.format(
                    StyledXMLParserException.INVALID_GRADIENT_COLOR_STOP_VALUE, fullValue));
        }
        UnitValue offset = CssDimensionParsingUtils.parseLengthValueToPt(value, emValue, remValue);
        if (offset == null) {
            throw new StyledXMLParserException(MessageFormatUtil.format(
                    StyledXMLParserException.INVALID_GRADIENT_COLOR_STOP_VALUE, fullValue));
        }
        return offset;
    }

    private static GradientStrategy parseDirection(List<String> tokens, String argument) {
        if (tokens.size() < 2) {
            throw new StyledXMLParserException(MessageFormatUtil
                    .format(StyledXMLParserException.INVALID_GRADIENT_TO_SIDE_OR_CORNER_STRING, argument));
        }
        int topCount = 0;
        int bottomCount = 0;
        int leftCount = 0;
        int rightCount = 0;
        for (int i = 1; i < tokens.size(); ++i) {
            String token = tokens.get(i);
            if (CommonCssConstants.TOP.equals(token)) {
                ++topCount;
            } else if (CommonCssConstants.BOTTOM.equals(token)) {
                ++bottomCount;
            } else if (CommonCssConstants.LEFT.equals(token)) {
                ++leftCount;
            } else if (CommonCssConstants.RIGHT.equals(token)) {
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
        return new GradientColorStop(rgba, offsetValue, offsetType);
    }

    private static Map<String, StrategyBasedRadialGradientBuilder.GradientStrategy> createRadialExtentStrategies() {
        Map<String, StrategyBasedRadialGradientBuilder.GradientStrategy> strategies = new HashMap<>();
        strategies.put(CommonCssConstants.CLOSEST_CORNER,
                StrategyBasedRadialGradientBuilder.GradientStrategy.CLOSEST_CORNER);
        strategies.put(CommonCssConstants.CLOSEST_SIDE,
                StrategyBasedRadialGradientBuilder.GradientStrategy.CLOSEST_SIDE);
        strategies.put(CommonCssConstants.FARTHEST_CORNER,
                StrategyBasedRadialGradientBuilder.GradientStrategy.FARTHEST_CORNER);
        strategies.put(CommonCssConstants.FARTHEST_SIDE,
                StrategyBasedRadialGradientBuilder.GradientStrategy.FARTHEST_SIDE);
        return Collections.unmodifiableMap(strategies);
    }

    static final class RadialSize {
        final String extentKeyword;
        final List<UnitValue> radii;

        RadialSize(String extentKeyword, List<UnitValue> radii) {
            this.extentKeyword = extentKeyword;
            this.radii = new ArrayList<>(radii);
        }
    }

}
