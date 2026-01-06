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
package com.itextpdf.styledxmlparser.css.parse;

import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("UnitTest")
public class CssDeclarationVarParserTest extends ExtendedITextTest {

    @Test
    public void simpleVarTest() {
        String resolved = parseVar("var(--simple)");
        Assertions.assertEquals("var(--simple)", resolved);
    }

    @Test
    public void varWithFallbackOnFunctionTest() {
        String resolved = parseVar("1px      var(    --test  , calc('test str ' + var(--default, var(--test, 2px)))   )");
        Assertions.assertEquals("var(    --test  , calc('test str ' + var(--default, var(--test, 2px)))   )", resolved);
    }

    @Test
    public void varInsideFunctionTest() {
        String resolved = parseVar("1px      calc('test str ' + var(--default, var(--test, 2px)))");
        Assertions.assertEquals("var(--default, var(--test, 2px))", resolved);
    }

    @Test
    public void varInsideFunctionInShorthandWithVarTest() {
        String resolved = parseVar("1px      calc('test str ' + var(--default, var(--test, 2px)))   var(--dot, dotted)");
        Assertions.assertEquals("var(--default, var(--test, 2px))", resolved);
    }

    @Test
    public void severalVarInsideFunctionTest() {
        String resolved = parseVar("calc(var(--two) + var(--one) + 20px)");
        Assertions.assertEquals("var(--two)", resolved);
    }

    @Test
    public void severalVarInsideFunctionWithFallbackTest() {
        String resolved = parseVar("calc(var(--two, var(--one, 55px)) + var(--one) + 20px)");
        Assertions.assertEquals("var(--two, var(--one, 55px))", resolved);
    }

    @Test
    public void varInCalculationsTest() {
        String resolved = parseVar("calc(20px + var(--one) + 20px)");
        Assertions.assertEquals("var(--one)", resolved);
    }

    @Test
    public void varInCalcSimpleTest() {
        String resolved = parseVar("calc(var(--one))");
        Assertions.assertEquals("var(--one)", resolved);
    }

    @Test
    public void nestedVarInCalcTest() {
        String resolved = parseVar("calc(var(--one, var(--two, 20px)))");
        Assertions.assertEquals("var(--one, var(--two, 20px))", resolved);
    }

    @Test
    public void nestedVarInCalcWithSpacesTest() {
        String resolved = parseVar("calc(    var(--one,       var(--two, 20px   )   )   )");
        Assertions.assertEquals("var(--one,       var(--two, 20px   )   )", resolved);
    }

    @Test
    public void nestedVarsTest() {
        String resolved = parseVar("calc('test' + 'test') dotted 1px var(var(--value), calc(var(--default) + \"test\")) 1px");
        Assertions.assertEquals("var(var(--value), calc(var(--default) + \"test\"))", resolved);
    }

    @Test
    public void varInSingleQuotesTest() {
        String resolved = parseVar("calc('test' + 'var(--value)') calc(var(--default))");
        Assertions.assertEquals("var(--default)", resolved);
    }

    @Test
    public void varInDoubleQuotesTest() {
        String resolved = parseVar("calc('test' + \"var(--value)\") calc(var(--default))");
        Assertions.assertEquals("var(--default)", resolved);
    }

    private static String parseVar(String expression) {
        return new CssDeclarationVarParser(expression).getFirstValidVarToken().getValue();
    }
}
