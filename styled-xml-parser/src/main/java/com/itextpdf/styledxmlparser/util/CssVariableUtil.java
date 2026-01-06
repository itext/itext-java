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
package com.itextpdf.styledxmlparser.util;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.styledxmlparser.css.CssDeclaration;
import com.itextpdf.styledxmlparser.css.parse.CssDeclarationVarParser;
import com.itextpdf.styledxmlparser.css.resolve.shorthand.IShorthandResolver;
import com.itextpdf.styledxmlparser.css.resolve.shorthand.ShorthandResolverFactory;
import com.itextpdf.styledxmlparser.css.validate.CssDeclarationValidationMaster;
import com.itextpdf.styledxmlparser.exceptions.StyledXMLParserException;
import com.itextpdf.styledxmlparser.logs.StyledXmlParserLogMessageConstant;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for resolving css variables in declarations.
 */
public class CssVariableUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(CssVariableUtil.class);

    /**
     * Max count of css var expressions in single declaration.
     */
    private static final int MAX_CSS_VAR_COUNT = 30;

    private CssVariableUtil() {
        //private constructor for util class
    }

    /**
     * Resolve all css variables in style map
     *
     * @param styles css styles map
     */
    public static void resolveCssVariables(Map<String, String> styles) {
        List<CssDeclaration> varExpressions = new ArrayList<>();

        for (Map.Entry<String, String> entry : styles.entrySet()) {
            if (!containsVarExpression(entry.getValue()) || isCssVariable(entry.getKey())) {
                continue;
            }
            CssDeclaration result = new CssDeclaration(entry.getKey(), null);
            try {
                result = resolveSingleVar(entry.getKey(), entry.getValue(), styles);
            } catch (StyledXMLParserException exception) {
                LOGGER.warn(MessageFormatUtil.format(exception.getMessage(),
                        new CssDeclaration(entry.getKey(), entry.getValue())));
            }
            varExpressions.add(result);
        }
        for (CssDeclaration expression : varExpressions) {
            styles.remove(expression.getProperty());
            if (expression.getExpression() != null) {
                List<CssDeclaration> resolvedShorthandProperties = expandShorthand(expression);
                for (CssDeclaration resolved : resolvedShorthandProperties) {
                    styles.put(resolved.getProperty(), resolved.getExpression());
                }
            }
        }
    }

    /**
     * Checks for var expression.
     *
     * @param expression css expression to check
     *
     * @return true if there is a var expression, false otherwise
     */
    public static boolean containsVarExpression(String expression) {
        return expression != null && expression.contains("var(");
    }

    /**
     * Checks property for css variable.
     *
     * @param property css property to check
     *
     * @return true if it is a css variable, false otherwise
     */
    public static boolean isCssVariable(String property) {
        return property != null && property.startsWith("--");
    }

    private static List<CssDeclaration> expandShorthand(CssDeclaration declaration) {
        List<CssDeclaration> result = new ArrayList<>();
        IShorthandResolver shorthandResolver = ShorthandResolverFactory.getShorthandResolver(declaration.getProperty());
        if (shorthandResolver == null) {
            result.add(declaration);
            return result;
        } else {
            List<CssDeclaration> resolvedShorthandProps = shorthandResolver.resolveShorthand(declaration.getExpression());
            for (CssDeclaration resolved : resolvedShorthandProps) {
                result.addAll(expandShorthand(resolved));
            }
        }
        return result;
    }

    /**
     * Resolve single css var expression recursively
     *
     * @param key css style property
     * @param expression css expression
     * @param styles css styles map
     *
     * @return resolved var expression if present or null if none found
     */
    private static CssDeclaration resolveSingleVar(String key, String expression, Map<String, String> styles) {
        if (!containsVarExpression(expression)) {
            return new CssDeclaration(key, expression);
        }

        String result = resolveVarRecursively(expression, styles, 0);
        CssDeclaration declaration = new CssDeclaration(key, result);
        if (CssDeclarationValidationMaster.checkDeclaration(declaration)) {
            return declaration;
        } else {
            // Throw exception to be able to log the whole css declaration
            throw new StyledXMLParserException(StyledXmlParserLogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION);
        }
    }

    /**
     * Resolves variables without taking into account default values
     *
     * @param expression var value
     * @param styles element styles
     * @param level current var expression nesting level
     *
     * @return resolved var expression
     */
    private static String resolveVarRecursively(String expression, Map<String, String> styles, int level) {
        if (level > MAX_CSS_VAR_COUNT) {
            throw new StyledXMLParserException(StyledXmlParserLogMessageConstant.INVALID_CSS_VARIABLE_COUNT);
        }

        StringBuilder expandedExpressionBuilder = new StringBuilder();
        CssDeclarationVarParser tokenizer = new CssDeclarationVarParser(expression);
        CssDeclarationVarParser.VarToken currentToken = tokenizer.getFirstValidVarToken();
        if (currentToken != null) {

            String resolvedVar = resolveVarExpression(currentToken.getValue(), styles);
            expandedExpressionBuilder.append(expression, 0, currentToken.getStart())
                    .append(resolvedVar)
                    .append(expression, currentToken.getEnd(), expression.length());
        } else {
            throw new StyledXMLParserException(StyledXmlParserLogMessageConstant.ERROR_DURING_CSS_VARIABLE_RESOLVING);
        }
        String expandedExpression = expandedExpressionBuilder.toString();
        if (containsVarExpression(expandedExpression)) {
            level++;
            expandedExpression = resolveVarRecursively(expandedExpression, styles, level);
        }
        return expandedExpression;
    }

    /**
     * Resolve css variable expression, if there is a fallback value and primary value is null,
     * default value will be returned.
     *
     * @param varExpression expression as the following: var(.+?(?:,.*?)?)
     * @param styles map of styles containing resolved variables
     *
     * @return resolved var expression
     */
    private static String resolveVarExpression(String varExpression, Map<String, String> styles) {
        int variableStartIndex = varExpression.indexOf("--");
        int separatorIndex = varExpression.indexOf(',');
        int variableEndIndex = separatorIndex == -1 ? varExpression.indexOf(')') : separatorIndex;
        String name = varExpression.substring(variableStartIndex, variableEndIndex).trim();
        String value = styles.get(name);
        if (value != null) {
            return value;
        } else if (separatorIndex != -1) {
            return varExpression.substring(separatorIndex + 1, varExpression.lastIndexOf(')'));
        }
        return "";
    }
}
