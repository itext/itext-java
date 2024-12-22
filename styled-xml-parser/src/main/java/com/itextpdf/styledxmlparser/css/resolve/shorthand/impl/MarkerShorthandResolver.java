package com.itextpdf.styledxmlparser.css.resolve.shorthand.impl;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.styledxmlparser.css.CssDeclaration;
import com.itextpdf.styledxmlparser.css.resolve.shorthand.IShorthandResolver;
import com.itextpdf.styledxmlparser.css.util.CssTypesValidationUtils;
import com.itextpdf.styledxmlparser.logs.StyledXmlParserLogMessageConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MarkerShorthandResolver implements IShorthandResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(MarkerShorthandResolver.class);

    /**
     * Creates a new {@link MarkerShorthandResolver} instance.
     */
    public MarkerShorthandResolver() {
        //empty constructor
    }

    @Override
    public List<CssDeclaration> resolveShorthand(String shorthandExpression) {
        if (CssTypesValidationUtils.isInitialOrInheritOrUnset(shorthandExpression)) {
            return Arrays.asList(new CssDeclaration(CommonCssConstants.MARKER_START, shorthandExpression),
                                 new CssDeclaration(CommonCssConstants.MARKER_MID, shorthandExpression),
                                 new CssDeclaration(CommonCssConstants.MARKER_END, shorthandExpression));
        }
        String expression = shorthandExpression.trim();
        if (expression.isEmpty()) {
            LOGGER.warn(MessageFormatUtil.format(StyledXmlParserLogMessageConstant.SHORTHAND_PROPERTY_CANNOT_BE_EMPTY,
                    CommonCssConstants.MARKER));
            return new ArrayList<>();
        }
        if (!expression.startsWith(CommonCssConstants.URL + "(") || !expression.endsWith(")")) {
            LOGGER.warn(MessageFormatUtil.format(
                    StyledXmlParserLogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION, shorthandExpression));
            return new ArrayList<>();
        }
        return Arrays.asList(new CssDeclaration(CommonCssConstants.MARKER_START, shorthandExpression),
                new CssDeclaration(CommonCssConstants.MARKER_MID, shorthandExpression),
                new CssDeclaration(CommonCssConstants.MARKER_END, shorthandExpression));
    }
}
