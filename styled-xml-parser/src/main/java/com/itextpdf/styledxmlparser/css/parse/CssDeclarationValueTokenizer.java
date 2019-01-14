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
package com.itextpdf.styledxmlparser.css.parse;

/**
 * Tokenizer for CSS declaration values.
 */
public class CssDeclarationValueTokenizer {
    
    /** The source string. */
    private String src;
    
    /** The current index. */
    private int index = -1;
    
    /** The quote string, either "'" or "\"". */
    private char stringQuote;
    
    /** Indicates if we're inside a string. */
    private boolean inString;
    
    /** The depth. */
    private int functionDepth = 0;

    /**
     * Creates a new {@link CssDeclarationValueTokenizer} instance.
     *
     * @param propertyValue the property value
     */
    public CssDeclarationValueTokenizer(String propertyValue) {
        this.src = propertyValue;
    }

    /**
     * Gets the next valid token.
     *
     * @return the next valid token
     */
    public Token getNextValidToken() {
        Token token = getNextToken();
        while (token != null && !token.isString() && token.getValue().trim().isEmpty()) {
            token = getNextToken();
        }
        if (token != null && functionDepth > 0) {
            StringBuilder functionBuffer = new StringBuilder();
            while (token != null && functionDepth > 0) {
                processFunctionToken(token, functionBuffer);
                token = getNextToken();
            }
            functionDepth = 0;
            if (functionBuffer.length() != 0) {
                if (token != null) {
                    processFunctionToken(token, functionBuffer);
                }
                return new Token(functionBuffer.toString(), TokenType.FUNCTION);
            }
        }
        return token;
    }

    /**
     * Gets the next token.
     *
     * @return the next token
     */
    private Token getNextToken() {
        StringBuilder buff = new StringBuilder();
        char curChar;
        if (index >= src.length() - 1) {
            return null;
        }
        if (inString) {
            boolean isEscaped = false;
            StringBuilder pendingUnicodeSequence = new StringBuilder();
            while (++index < src.length()) {
                curChar = src.charAt(index);
                if (isEscaped) {
                    if (isHexDigit(curChar) && pendingUnicodeSequence.length() < 6) {
                        pendingUnicodeSequence.append(curChar);
                    } else if (pendingUnicodeSequence.length() != 0) {
                        int codePoint = Integer.parseInt(pendingUnicodeSequence.toString(), 16);
                        if (Character.isValidCodePoint(codePoint)) {
                            buff.appendCodePoint(codePoint);
                        } else {
                            buff.append("\uFFFD");
                        }
                        pendingUnicodeSequence.setLength(0);
                        if (curChar == stringQuote) {
                            inString = false;
                            return new Token(buff.toString(), TokenType.STRING);
                        } else if (!Character.isWhitespace(curChar)) {
                            buff.append(curChar);
                        }
                        isEscaped = false;
                    } else {
                        buff.append(curChar);
                        isEscaped = false;
                    }
                } else if (curChar == stringQuote){
                    inString = false;
                    return new Token(buff.toString(), TokenType.STRING);
                } else if (curChar == '\\') {
                    isEscaped = true;
                } else {
                    buff.append(curChar);
                }
            }
        } else {
            while (++index < src.length()) {
                curChar = src.charAt(index);
                if (curChar == '(') {
                    ++functionDepth;
                    buff.append(curChar);
                } else if (curChar == ')') {
                    --functionDepth;
                    buff.append(curChar);
                    if (functionDepth == 0) {
                        return new Token(buff.toString(), TokenType.FUNCTION);
                    }
                } else if (curChar == '"' || curChar == '\'') {
                    stringQuote = curChar;
                    inString = true;
                    return new Token(buff.toString(), TokenType.FUNCTION);
                } else if (curChar == ',' && !inString && functionDepth == 0) {
                    return new Token(",", TokenType.COMMA);
                } else if (Character.isWhitespace(curChar)) {
                    if (functionDepth > 0) {
                        buff.append(curChar);
                    }
                    return new Token(buff.toString(), functionDepth > 0 ? TokenType.FUNCTION : TokenType.UNKNOWN);
                } else {
                    buff.append(curChar);
                }
            }
        }
        return new Token(buff.toString(), TokenType.FUNCTION);
    }

    /**
     * Checks if a character is a hexadecimal digit.
     *
     * @param c the character
     * @return true, if it's a hexadecimal digit
     */
    private boolean isHexDigit(char c) {
        return (47 < c && c < 58) || (64 < c && c < 71) || (96 < c && c < 103);
    }

    /**
     * Processes a function token.
     *
     * @param token the token
     * @param functionBuffer the function buffer
     */
    private void processFunctionToken(Token token, StringBuilder functionBuffer) {
        if (token.isString()) {
            functionBuffer.append(stringQuote);
            functionBuffer.append(token.getValue());
            functionBuffer.append(stringQuote);
        } else {
            functionBuffer.append(token.getValue());
        }
    }

    /**
     * The Token class.
     */
    public static class Token {
        
        /** The value. */
        private String value;
        
        /** The type. */
        private TokenType type;

        /**
         * Creates a new {@link Token} instance.
         *
         * @param value the value
         * @param type the type
         */
        public Token(String value, TokenType type) {
            this.value = value;
            this.type = type;
        }

        /**
         * Gets the value.
         *
         * @return the value
         */
        public String getValue() {
            return value;
        }

        /**
         * Gets the type.
         *
         * @return the type
         */
        public TokenType getType() {
            return type;
        }

        /**
         * Checks if the token is a string.
         *
         * @return true, if is string
         */
        public boolean isString() {
            return type == TokenType.STRING;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return value;
        }
    }

    /**
     * Enumeration of the different token types.
     */
    public enum TokenType {
        
        /** The string type. */
        STRING,
        
        /** The function type. */
        FUNCTION,
        
        /** The comma type. */
        COMMA,
        
        /** Unknown type. */
        UNKNOWN
    }
}
