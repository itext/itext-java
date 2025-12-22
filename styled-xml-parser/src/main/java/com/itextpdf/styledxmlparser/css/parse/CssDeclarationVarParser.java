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
package com.itextpdf.styledxmlparser.css.parse;

import com.itextpdf.styledxmlparser.util.CssVariableUtil;

/**
 * Tokenizer for searching var expressions in css declarations.
 */
public class CssDeclarationVarParser extends CssDeclarationValueTokenizer {
    /**
     * Creates a new {@link CssDeclarationVarParser} instance.
     *
     * @param propertyValue the property value
     */
    public CssDeclarationVarParser(String propertyValue) {
        super(propertyValue);
    }

    /**
     * Gets the first valid var expression token. This method can't be called in chain to find all
     * var expressions in declaration since it invalidates internal parser state.
     *
     * @return the first valid var expression token
     */
    public VarToken getFirstValidVarToken() {
        // This class and method is needed for only one purpose - parse var() expressions inside other functions
        // Since original tokenizer is not suited for 'sensible' token parsing and only parses top level properties,
        // in this method we perform some hacks to be able to extract var expression inside top level tokens (functions).
        // E.g. extract `var(--value)` expression from `calc(var(--value) + 50px)`
        Token token;
        int start = -1;
        do {
            token = getNextToken();
            if (token != null && TokenType.FUNCTION == token.getType() && CssVariableUtil.containsVarExpression(token.getValue())) {
                // example of expected tokens: 'var(--one)', 'var(--one, ', 'calc(var(--one', ...
                start = index - (token.getValue().length() - token.getValue().indexOf("var")) + 1;
                break;
            }
        } while (token != null);

        if (token == null) {
            return null;
        }

        String tokenValue = token.getValue();

        // handle the following tokens: var(--one), calc(var(--one)), ...
        if (isEndingWithBracket(tokenValue)) {
            String resultTokenValue = extractSingleVar(tokenValue.substring(tokenValue.indexOf("var")));
            return new VarToken(resultTokenValue, start, index + 1);
        }

        // handle the following tokens: 'calc(var(--one', 'calc(20px + var(--one)', ...
        Token func = parseFunctionToken(token, functionDepth - 1);
        // func == null condition is not expected and shouldn't be invoked since all cases which can produce null func
        // are handled above

        String resultTokenValue = extractSingleVar(func.getValue().substring(func.getValue().indexOf("var")));
        return new VarToken(resultTokenValue, start, index + 1);
    }

    /**
     * Cut symbols not related to first variable.
     *
     * @param expression expression to process
     * @return expression with single variable
     */
    private String extractSingleVar(String expression) {
        //Starting from index 3 as we expect string to start like "var(..."
        int currentIndex = 3;
        int depth = 0;
        do {
            char ch = expression.charAt(currentIndex);
            if (ch == '(') {
                depth++;
            } else if (ch == ')') {
                depth--;
            }

            currentIndex++;
        } while (currentIndex <= expression.length() - 1 && depth != 0);
        String result = expression.substring(0, currentIndex);
        this.index -= expression.length() - result.length();
        return result;
    }

    private static boolean isEndingWithBracket(String expression) {
        for (int i = expression.length() - 1; i >= 0; --i) {
            if (!isSpaceOrWhitespace(expression.charAt(i))) {
                return ')' == expression.charAt(i);
            }
        }
        return false;
    }

    private static boolean isSpaceOrWhitespace(char character) {
        return Character.isSpaceChar(character) || Character.isWhitespace(character);
    }

    /**
     * The Token class which contains CSS var expression.
     */
    public static class VarToken {
        private final int start;
        private final int end;
        private final String value;

        VarToken(String value, int start, int end) {
            this.value = value;
            this.start = start;
            this.end = end;
        }

        /**
         * Gets the var expression value.
         *
         * @return the value
         */
        public String getValue() {
            return value;
        }

        /**
         * Gets start position of var expression in original css declaration
         *
         * @return start position in original css declaration
         */
        public int getStart() {
            return start;
        }

        /**
         * Gets end position of var expression in original css declaration
         *
         * @return end position in original css declaration
         */
        public int getEnd() {
            return end;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return value;
        }
    }
}
