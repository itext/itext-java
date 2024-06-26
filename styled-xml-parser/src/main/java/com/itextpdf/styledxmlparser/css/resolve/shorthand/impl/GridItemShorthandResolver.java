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
import java.util.Collections;
import java.util.List;

/**
 * {@link IShorthandResolver} implementation for grid items column/row start and end positions.
 */
public abstract class GridItemShorthandResolver implements IShorthandResolver {
    private static final Logger LOGGER = LoggerFactory.getLogger(GridItemShorthandResolver.class);
    private final String propertyTemplate;

    /**
     * Creates a new shorthand resolver for provided shorthand template
     *
     * @param shorthand shorthand from which template will be created.
     */
    protected GridItemShorthandResolver(String shorthand) {
        this.propertyTemplate = shorthand + "-{0}";
    }

    @Override
    public List<CssDeclaration> resolveShorthand(String shorthandExpression) {
        shorthandExpression = shorthandExpression.trim();
        if (shorthandExpression.isEmpty()) {
            LOGGER.warn(MessageFormatUtil.format(
                    StyledXmlParserLogMessageConstant.SHORTHAND_PROPERTY_CANNOT_BE_EMPTY,
                    propertyTemplate.substring(0, propertyTemplate.length() - 4)
            ));
            return new ArrayList<>();
        }
        if (CssTypesValidationUtils.isInitialOrInheritOrUnset(shorthandExpression)
                || CommonCssConstants.AUTO.equals(shorthandExpression)) {
            return new ArrayList<>();
        }
        final String[] values = shorthandExpression.split("/");
        if (values.length == 1) {
            if (shorthandExpression.startsWith("span")) {
                return Collections.singletonList(
                        new CssDeclaration(MessageFormatUtil.format(propertyTemplate, "start"), values[0]));
            }
            return Arrays.asList(
                    new CssDeclaration(MessageFormatUtil.format(propertyTemplate, "start"), values[0]),
                    new CssDeclaration(MessageFormatUtil.format(propertyTemplate, "end"), values[0])
            );
        }
        return Arrays.asList(
                new CssDeclaration(MessageFormatUtil.format(propertyTemplate, "start"), values[0]),
                new CssDeclaration(MessageFormatUtil.format(propertyTemplate, "end"), values[1])
        );
    }
}
