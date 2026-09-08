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
import com.itextpdf.kernel.colors.gradients.StrategyBasedRadialGradientBuilder;
import com.itextpdf.layout.properties.BackgroundPosition;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.styledxmlparser.css.CssDeclaration;
import com.itextpdf.styledxmlparser.css.resolve.shorthand.impl.BackgroundPositionShorthandResolver;
import com.itextpdf.styledxmlparser.exceptions.StyledXMLParserException;

import java.util.List;
import java.util.regex.Pattern;

/**
 * CSS {@code <position>} parsing for gradient functions, e.g. {@code radial-gradient(... at <position>)}.
 */
final class CssGradientPositionUtil {

    private static final Pattern WHITESPACE_SPLIT_REGEX = Pattern.compile("\\s+");

    private CssGradientPositionUtil() {
    }

    /**
     * Parses a CSS {@code <position>} value and sets it as the center of the radial gradient.
     *
     * @param builder the radial gradient builder to set the center strategy on
     * @param positionValue the CSS position value
     * @param emValue the current element's em value
     * @param remValue the current root em value
     * @param fullValue the full CSS value which contains the position, used in the exception message
     * if the position is invalid
     */
    static void parseAndSetRadialGradientCenter(StrategyBasedRadialGradientBuilder builder, String positionValue,
            float emValue, float remValue, String fullValue) {
        List<CssDeclaration> resolved = new BackgroundPositionShorthandResolver().resolveShorthand(positionValue);
        if (resolved.isEmpty()) {
            throw new StyledXMLParserException(MessageFormatUtil.format(
                    StyledXMLParserException.INVALID_GRADIENT_FUNCTION_ARGUMENTS_LIST, fullValue));
        }

        String positionX = null;
        String positionY = null;
        for (CssDeclaration declaration : resolved) {
            if (CommonCssConstants.BACKGROUND_POSITION_X.equals(declaration.getProperty())) {
                positionX = declaration.getExpression();
            } else if (CommonCssConstants.BACKGROUND_POSITION_Y.equals(declaration.getProperty())) {
                positionY = declaration.getExpression();
            }
        }
        if (positionX == null || positionY == null) {
            throw new StyledXMLParserException(MessageFormatUtil.format(
                    StyledXMLParserException.INVALID_GRADIENT_FUNCTION_ARGUMENTS_LIST, fullValue));
        }

        ParsedPositionX parsedX = parsePositionX(positionX, emValue, remValue, fullValue);
        ParsedPositionY parsedY = parsePositionY(positionY, emValue, remValue, fullValue);
        BackgroundPosition position = new BackgroundPosition()
                .setPositionX(parsedX.position)
                .setXShift(parsedX.shift)
                .setPositionY(parsedY.position)
                .setYShift(parsedY.shift);
        builder.setCenterStrategy(
                position.getPositionX() != BackgroundPosition.PositionX.RIGHT,
                getPositionOffset(position.getPositionX(), position.getXShift()),
                position.getPositionX() == BackgroundPosition.PositionX.CENTER
                        || position.getXShift().isPercentValue(),
                position.getPositionY() == BackgroundPosition.PositionY.BOTTOM,
                getPositionOffset(position.getPositionY(), position.getYShift()),
                position.getPositionY() == BackgroundPosition.PositionY.CENTER
                        || position.getYShift().isPercentValue());
    }

    private static ParsedPositionX parsePositionX(String expression, float emValue, float remValue,
            String fullValue) {
        String[] parts = WHITESPACE_SPLIT_REGEX.split(expression.trim());
        if (parts.length == 1) {
            if (CommonCssConstants.LEFT.equals(parts[0])) {
                return new ParsedPositionX(BackgroundPosition.PositionX.LEFT,
                        new UnitValue(UnitValue.PERCENT, 0));
            }
            if (CommonCssConstants.RIGHT.equals(parts[0])) {
                return new ParsedPositionX(BackgroundPosition.PositionX.RIGHT,
                        new UnitValue(UnitValue.PERCENT, 0));
            }
            if (CommonCssConstants.CENTER.equals(parts[0])) {
                return new ParsedPositionX(BackgroundPosition.PositionX.CENTER,
                        new UnitValue(UnitValue.PERCENT, 0));
            }
            UnitValue shift = CssDimensionParsingUtils.parseLengthValueToPt(parts[0], emValue, remValue);
            if (shift == null) {
                throw new StyledXMLParserException(MessageFormatUtil.format(
                        StyledXMLParserException.INVALID_GRADIENT_FUNCTION_ARGUMENTS_LIST, fullValue));
            }
            return new ParsedPositionX(BackgroundPosition.PositionX.LEFT, shift);
        }
        if (parts.length == 2 && (CommonCssConstants.LEFT.equals(parts[0])
                || CommonCssConstants.RIGHT.equals(parts[0]))) {
            UnitValue shift = CssDimensionParsingUtils.parseLengthValueToPt(parts[1], emValue, remValue);
            if (shift == null) {
                throw new StyledXMLParserException(MessageFormatUtil.format(
                        StyledXMLParserException.INVALID_GRADIENT_FUNCTION_ARGUMENTS_LIST, fullValue));
            }
            BackgroundPosition.PositionX position = CommonCssConstants.LEFT.equals(parts[0])
                    ? BackgroundPosition.PositionX.LEFT : BackgroundPosition.PositionX.RIGHT;
            return new ParsedPositionX(position, shift);
        }
        throw new StyledXMLParserException(MessageFormatUtil.format(
                StyledXMLParserException.INVALID_GRADIENT_FUNCTION_ARGUMENTS_LIST, fullValue));
    }

    private static ParsedPositionY parsePositionY(String expression, float emValue, float remValue,
            String fullValue) {
        String[] parts = WHITESPACE_SPLIT_REGEX.split(expression.trim());
        if (parts.length == 1) {
            if (CommonCssConstants.TOP.equals(parts[0])) {
                return new ParsedPositionY(BackgroundPosition.PositionY.TOP,
                        new UnitValue(UnitValue.PERCENT, 0));
            }
            if (CommonCssConstants.BOTTOM.equals(parts[0])) {
                return new ParsedPositionY(BackgroundPosition.PositionY.BOTTOM,
                        new UnitValue(UnitValue.PERCENT, 0));
            }
            if (CommonCssConstants.CENTER.equals(parts[0])) {
                return new ParsedPositionY(BackgroundPosition.PositionY.CENTER,
                        new UnitValue(UnitValue.PERCENT, 0));
            }
            UnitValue shift = CssDimensionParsingUtils.parseLengthValueToPt(parts[0], emValue, remValue);
            if (shift == null) {
                throw new StyledXMLParserException(MessageFormatUtil.format(
                        StyledXMLParserException.INVALID_GRADIENT_FUNCTION_ARGUMENTS_LIST, fullValue));
            }
            return new ParsedPositionY(BackgroundPosition.PositionY.TOP, shift);
        }
        if (parts.length == 2 && (CommonCssConstants.TOP.equals(parts[0])
                || CommonCssConstants.BOTTOM.equals(parts[0]))) {
            UnitValue shift = CssDimensionParsingUtils.parseLengthValueToPt(parts[1], emValue, remValue);
            if (shift == null) {
                throw new StyledXMLParserException(MessageFormatUtil.format(
                        StyledXMLParserException.INVALID_GRADIENT_FUNCTION_ARGUMENTS_LIST, fullValue));
            }
            BackgroundPosition.PositionY position = CommonCssConstants.TOP.equals(parts[0])
                    ? BackgroundPosition.PositionY.TOP : BackgroundPosition.PositionY.BOTTOM;
            return new ParsedPositionY(position, shift);
        }
        throw new StyledXMLParserException(MessageFormatUtil.format(
                StyledXMLParserException.INVALID_GRADIENT_FUNCTION_ARGUMENTS_LIST, fullValue));
    }

    private static double getPositionOffset(BackgroundPosition.PositionX position, UnitValue shift) {
        return position == BackgroundPosition.PositionX.CENTER ? 0.5D : getShiftValue(shift);
    }

    private static double getPositionOffset(BackgroundPosition.PositionY position, UnitValue shift) {
        return position == BackgroundPosition.PositionY.CENTER ? 0.5D : getShiftValue(shift);
    }

    private static double getShiftValue(UnitValue shift) {
        return shift.isPercentValue() ? (shift.getValue() / 100D) : shift.getValue();
    }

    static final class ParsedPositionX {
        final BackgroundPosition.PositionX position;
        final UnitValue shift;

        ParsedPositionX(BackgroundPosition.PositionX position, UnitValue shift) {
            this.position = position;
            this.shift = shift;
        }
    }

    static final class ParsedPositionY {
        final BackgroundPosition.PositionY position;
        final UnitValue shift;

        ParsedPositionY(BackgroundPosition.PositionY position, UnitValue shift) {
            this.position = position;
            this.shift = shift;
        }
    }
}
