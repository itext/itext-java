/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
package com.itextpdf.styledxmlparser.css.resolve;

import com.itextpdf.styledxmlparser.css.parse.CssDeclarationValueTokenizer;
import com.itextpdf.test.ExtendedITextTest;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class CssDeclarationValueTokenizerTest extends ExtendedITextTest {
    @Test
    public void functionTest01() {
        runTest("func(param)", Arrays.asList("func(param)"), Arrays.asList(CssDeclarationValueTokenizer.TokenType.FUNCTION));
    }

    @Test
    public void functionTest02() {
        runTest("func(param1, param2)", Arrays.asList("func(param1, param2)"), Arrays.asList(CssDeclarationValueTokenizer.TokenType.FUNCTION));
    }

    @Test
    public void functionTest03() {
        runTest("func(param,'param)',\"param))\")", Arrays.asList("func(param,'param)',\"param))\")"), Arrays.asList(CssDeclarationValueTokenizer.TokenType.FUNCTION));
    }

    @Test
    public void functionTest04() {
        runTest("func(param, innerFunc())", Arrays.asList("func(param, innerFunc())"), Arrays.asList(CssDeclarationValueTokenizer.TokenType.FUNCTION));
    }

    @Test
    public void functionTest05() {
        runTest(") )) function()", Arrays.asList(")", "))", "function()"), Arrays.asList(CssDeclarationValueTokenizer.TokenType.UNKNOWN, CssDeclarationValueTokenizer.TokenType.UNKNOWN, CssDeclarationValueTokenizer.TokenType.FUNCTION));
    }

    @Test
    public void functionTest06() {
        runTest("a('x'), b('x')", Arrays.asList("a('x')", ",", "b('x')"), Arrays.asList(CssDeclarationValueTokenizer.TokenType.FUNCTION, CssDeclarationValueTokenizer.TokenType.COMMA, CssDeclarationValueTokenizer.TokenType.FUNCTION));
    }

    @Test
    public void stringTest01() {
        runTest("'a b c'", Arrays.asList("a b c"), Arrays.asList(CssDeclarationValueTokenizer.TokenType.STRING));
    }

    @Test
    public void stringTest02() {
        runTest("\"a b c\"", Arrays.asList("a b c"), Arrays.asList(CssDeclarationValueTokenizer.TokenType.STRING));
    }

    @Test
    public void stringTest03() {
        runTest("[ aa  bb  cc ]", Arrays.asList("[ aa  bb  cc ]"),
                Arrays.asList(CssDeclarationValueTokenizer.TokenType.STRING));
    }

    @Test
    public void stringTest04() {
        runTest("[aa bb cc] [dd ee] 'ff ff'", Arrays.asList("[aa bb cc]", "[dd ee]", "ff ff"),
                Arrays.asList(CssDeclarationValueTokenizer.TokenType.STRING,
                        CssDeclarationValueTokenizer.TokenType.STRING,
                        CssDeclarationValueTokenizer.TokenType.STRING));
    }

    @Test
    public void functionWithSquareBracketsTest04() {
        runTest("'prefix' repeat(3, [aa bb cc] 2 [dd ee] 3) 'ff ff'",
                Arrays.asList("prefix", "repeat(3, [aa bb cc] 2 [dd ee] 3)", "ff ff"),
                Arrays.asList(CssDeclarationValueTokenizer.TokenType.STRING,
                        CssDeclarationValueTokenizer.TokenType.FUNCTION,
                        CssDeclarationValueTokenizer.TokenType.STRING));
    }

    private void runTest(String src, List<String> tokenValues, List<CssDeclarationValueTokenizer.TokenType> tokenTypes) {
        CssDeclarationValueTokenizer tokenizer = new CssDeclarationValueTokenizer(src);
        CssDeclarationValueTokenizer.Token token = null;
        Assertions.assertTrue(tokenValues.size() == tokenTypes.size(), "Value and type arrays size should be equal");
        int index = 0;
        while ((token = tokenizer.getNextValidToken()) != null) {
            Assertions.assertEquals(tokenValues.get(index), token.getValue());
            Assertions.assertEquals(tokenTypes.get(index), token.getType());
            ++index;
        }
        Assertions.assertTrue(index == tokenValues.size());
    }
}
