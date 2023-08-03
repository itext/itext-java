/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
import com.itextpdf.test.annotations.type.UnitTest;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
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

    private void runTest(String src, List<String> tokenValues, List<CssDeclarationValueTokenizer.TokenType> tokenTypes) {
        CssDeclarationValueTokenizer tokenizer = new CssDeclarationValueTokenizer(src);
        CssDeclarationValueTokenizer.Token token = null;
        Assert.assertTrue("Value and type arrays size should be equal", tokenValues.size() == tokenTypes.size());
        int index = 0;
        while ((token = tokenizer.getNextValidToken()) != null) {
            Assert.assertEquals(tokenValues.get(index), token.getValue());
            Assert.assertEquals(tokenTypes.get(index), token.getType());
            ++index;
        }
        Assert.assertTrue(index == tokenValues.size());
    }
}
