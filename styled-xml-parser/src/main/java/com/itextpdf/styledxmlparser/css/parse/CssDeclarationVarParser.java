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
            String resultTokenValue = removeUnclosedBrackets(tokenValue.substring(tokenValue.indexOf("var")));
            return new VarToken(resultTokenValue, start, index + 1);
        }

        // handle the following tokens: 'calc(var(--one', 'calc(20px + var(--one)', ...
        Token func = parseFunctionToken(token, functionDepth - 1);
        // func == null condition is not expected and shouldn't be invoked since all cases which can produce null func
        // are handled above

        String resultTokenValue = removeUnclosedBrackets(func.getValue().substring(func.getValue().indexOf("var")));
        return new VarToken(resultTokenValue, start, index + 1);
    }

    private String removeUnclosedBrackets(String expression) {
        StringBuilder resultBuilder = new StringBuilder();
        int openBrackets = 0;
        int closeBrackets = 0;
        for (int i = 0; i < expression.length(); ++i) {
            if (expression.charAt(i) == '(') {
                ++openBrackets;
            } else if (expression.charAt(i) == ')') {
                ++closeBrackets;
                if (closeBrackets > openBrackets) {
                    --index;
                    continue;
                }
            }
            resultBuilder.append(expression.charAt(i));
        }

        String resultTrimmed = resultBuilder.toString().trim();
        index -= resultBuilder.length() - resultTrimmed.length();
        return resultTrimmed;
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
