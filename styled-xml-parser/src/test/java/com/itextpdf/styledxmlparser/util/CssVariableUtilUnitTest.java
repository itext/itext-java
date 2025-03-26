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
package com.itextpdf.styledxmlparser.util;

import com.itextpdf.styledxmlparser.css.CssDeclaration;
import com.itextpdf.styledxmlparser.css.CssRuleSet;
import com.itextpdf.styledxmlparser.css.CssStyleSheet;
import com.itextpdf.styledxmlparser.css.selector.CssSelector;
import com.itextpdf.styledxmlparser.logs.StyledXmlParserLogMessageConstant;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag("UnitTest")
public class CssVariableUtilUnitTest extends ExtendedITextTest {
    @Test
    public void resolveSimpleVariableTest() {
        Map<String, String> styles = new HashMap<>();
        styles.put("margin", null);
        styles.put("--test", "50px");
        styles.put("padding-bottom", "var(--test)");
        CssVariableUtil.resolveCssVariables(styles);
        Assertions.assertEquals("50px", styles.get("padding-bottom"));
    }

    @Test
    public void resolveSimpleVariableWithSpacesTest() {
        Map<String, String> styles = new HashMap<>();
        styles.put("margin", null);
        styles.put("--test", "50px");
        styles.put("padding-right", "var ( --test )");
        CssVariableUtil.resolveCssVariables(styles);
        Assertions.assertEquals("var ( --test )", styles.get("padding-right"));
    }

    @Test
    public void resolveSimpleVariableWithSpacesTest2() {
        Map<String, String> styles = new HashMap<>();
        styles.put("margin", null);
        styles.put("--test", "50px");
        styles.put("padding-top", "var( --test )");
        CssVariableUtil.resolveCssVariables(styles);
        Assertions.assertEquals("50px", styles.get("padding-top"));
    }

    @Test
    public void resolveSimpleVariableWithDefaultTest() {
        Map<String, String> styles = new HashMap<>();
        styles.put("--test", "50px");
        styles.put("padding", "var(--test, 30px)");
        CssVariableUtil.resolveCssVariables(styles);
        Assertions.assertEquals("50px", styles.get("padding-left"));
        Assertions.assertEquals("50px", styles.get("padding-right"));
        Assertions.assertEquals("50px", styles.get("padding-top"));
        Assertions.assertEquals("50px", styles.get("padding-bottom"));
    }

    @Test
    public void resolveIncorrectVariableWithDefaultTest() {
        Map<String, String> styles = new HashMap<>();
        styles.put("--test", "50px");
        styles.put("padding-left", "var(--incorrect, 30px)");
        CssVariableUtil.resolveCssVariables(styles);
        Assertions.assertEquals("30px", styles.get("padding-left"));
    }

    @Test
    public void resolveIncorrectVariableWithoutDefaultTest() {
        Map<String, String> styles = new HashMap<>();
        styles.put("--test", "50px");
        styles.put("padding", "var(--incorrect)");
        CssVariableUtil.resolveCssVariables(styles);
        Assertions.assertNull(styles.get("padding"));
    }

    @LogMessages(messages = @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION))
    @Test
    public void resolveIncorrectVariableWithValidationTest() {
        Map<String, String> styles = new HashMap<>();
        styles.put("--test", "50px");
        styles.put("word-break", "var(--test, 30px)");
        CssVariableUtil.resolveCssVariables(styles);
        Assertions.assertNull(styles.get("word-break"));
    }

    @Test
    public void resolveVarExpressionInStyleSheetTest() {
        List<CssDeclaration> declarations = new ArrayList<>();
        declarations.add(new CssDeclaration("--test", "normal"));
        declarations.add(new CssDeclaration("word-break", "var(--test, break-all)"));
        List<CssRuleSet> ruleSets = new ArrayList<>();
        ruleSets.add(new CssRuleSet(new CssSelector("a"), declarations));
        Map<String, String> result = CssStyleSheet.extractStylesFromRuleSets(ruleSets);
        Map<String, String> expected = new HashMap<>();
        expected.put("--test", "normal");
        expected.put("word-break", "var(--test,break-all)");
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void resolveInnerVariableTest() {
        Map<String, String> styles = new HashMap<>();
        styles.put("--color-black", "rgb(0, 0, 0)");
        styles.put("border", "1px dotted var(--color-black)");
        CssVariableUtil.resolveCssVariables(styles);
        Assertions.assertEquals(13, styles.size());
        Assertions.assertEquals("dotted", styles.get("border-right-style"));
        Assertions.assertEquals("1px", styles.get("border-right-width"));
        Assertions.assertEquals("rgb(0,0,0)", styles.get("border-right-color"));
    }

    @Test
    public void resolveComplexVariableTest() {
        Map<String, String> styles = new HashMap<>();
        styles.put("--color-black", "black");
        styles.put("--border-dotted", "1px dotted var(--color-black)");
        styles.put("border", "var(--border-dotted)");
        CssVariableUtil.resolveCssVariables(styles);
        Assertions.assertNull(styles.get("border"));
        Assertions.assertEquals(14, styles.size());
        Assertions.assertEquals("dotted", styles.get("border-right-style"));
        Assertions.assertEquals("1px", styles.get("border-right-width"));
        Assertions.assertEquals("black", styles.get("border-right-color"));
    }

    @LogMessages(messages = @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION))
    @Test
    public void resolveVariableWithFallbackOnDefaultTest() {
        Map<String, String> styles = new HashMap<>();
        styles.put("--color-black-60", "bad-color");
        styles.put("background-color", "var(--color-black-60, rgba(1, 255, 0))");
        CssVariableUtil.resolveCssVariables(styles);
        Assertions.assertNull(styles.get("background-color"));
    }

    @LogMessages(messages = @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION))
    @Test
    public void resolveVariableWithFallbackOnDefaultTest2() {
        Map<String, String> styles = new HashMap<>();
        styles.put("--test1", "invalid");
        styles.put("word-break", "var(--test1, var(--invalid, break-all))");
        CssVariableUtil.resolveCssVariables(styles);
        Assertions.assertNull(styles.get("word-break"));
    }

    @Test
    public void resolveVariableWithFallbackOnDefaultTest3() {
        Map<String, String> styles = new HashMap<>();
        styles.put("word-break", "var(--test1, var(--invalid, break-all))");
        CssVariableUtil.resolveCssVariables(styles);
        Assertions.assertEquals("break-all", styles.get("word-break"));
    }

    @LogMessages(messages = @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION))
    @Test
    public void resolveVariableWithFallbackOnInvalidDefaultTest() {
        Map<String, String> styles = new HashMap<>();
        styles.put("--color-black-60", "bad-color");
        styles.put("background-color", "var(--color-black-60, bad-color)");
        CssVariableUtil.resolveCssVariables(styles);
        Assertions.assertNull(styles.get("background-color"));
    }

    @LogMessages(messages = @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.INVALID_CSS_VARIABLE_COUNT))
    @Test
    public void variableCycleRefTest1() {
        Map<String, String> styles = new HashMap<>();
        styles.put("--one", "calc(var(--two) + 20px)");
        styles.put("--two", "calc(var(--one) - 20px)");
        styles.put("margin", "var(--two)");
        CssVariableUtil.resolveCssVariables(styles);
        Assertions.assertNull(styles.get("margin"));
    }

    @Test
    public void varAsPrimaryTest() {
        Map<String, String> styles = new HashMap<>();
        styles.put("--value", "55px");
        styles.put("--default", "33px");
        styles.put("margin", "var(var(--value), --default)");
        CssVariableUtil.resolveCssVariables(styles);
        Assertions.assertEquals("--default", styles.get("margin-top"));
    }

    @Test
    public void varAsPrimaryTest2() {
        Map<String, String> styles = new HashMap<>();
        styles.put("--value", "55px");
        styles.put("--default", "33px");
        styles.put("margin", "var(var(--value), var(--default))");
        CssVariableUtil.resolveCssVariables(styles);
        Assertions.assertEquals("33px", styles.get("margin-top"));
    }

    @LogMessages(messages = @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.INVALID_CSS_VARIABLE_COUNT))
    @Test
    public void variableCycleRefTest2() {
        Map<String, String> styles = new HashMap<>();
        styles.put("--one", "var(--two)");
        styles.put("--two", "var(--one)");
        styles.put("margin", "var(--two)");
        CssVariableUtil.resolveCssVariables(styles);
        Assertions.assertNull(styles.get("margin"));
    }

    @LogMessages(messages = @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.INVALID_CSS_VARIABLE_COUNT))
    @Test
    public void variableCycleRefTest3() {
        Map<String, String> styles = new HashMap<>();
        styles.put("--one", "var(--two)");
        styles.put("--two", "var(--one)");
        styles.put("border", "1px dotted var(--two)");
        CssVariableUtil.resolveCssVariables(styles);
        Assertions.assertNull(styles.get("border"));
    }

    @Test
    public void variableInFontShorthandTest() {
        Map<String, String> styles = new HashMap<>();
        styles.put("--one", "small-caps");
        styles.put("--two", "var(--one, bold)");
        styles.put("font", "italic var(--one) var(--two, thin) 12px/30px \"Fira Sans\", sans-serif");
        CssVariableUtil.resolveCssVariables(styles);
        Assertions.assertEquals("small-caps", styles.get("font-variant"));
        Assertions.assertEquals("initial", styles.get("font-weight"));
        Assertions.assertEquals("12px", styles.get("font-size"));
        Assertions.assertEquals("30px", styles.get("line-height"));
        Assertions.assertEquals("\"Fira Sans\",sans-serif", styles.get("font-family"));
        Assertions.assertEquals("italic", styles.get("font-style"));
    }

    @Test
    public void resolveComplexVariableWithFallbackOnDefaultTest() {
        Map<String, String> styles = new HashMap<>();
        styles.put("--color-black-60", "bad-color");
        styles.put("--color-to-use", "var(--color-black-60, rgb(0, 255, 0))");
        styles.put("--border-dotted", "1px dotted var(--color-to-use)");
        styles.put("border", "var(--border-dotted)");
        CssVariableUtil.resolveCssVariables(styles);
        //it is expected to see rgb(0, 255, 0), but due to variable resolving mechanism we can't resolve defaults properly
        Assertions.assertEquals(15, styles.size());
        Assertions.assertEquals("dotted", styles.get("border-right-style"));
        Assertions.assertEquals("1px", styles.get("border-right-width"));
        Assertions.assertEquals("initial", styles.get("border-right-color"));
    }

    @Test
    public void resolveNestedComplexVariableTest() {
        Map<String, String> styles = new HashMap<>();
        styles.put("--thickness", "1px");
        styles.put("--color-black", "#000000");
        styles.put("--color-black-60", "#00000060");
        styles.put("--border-dotted", "var(--color-black-60, var(--color-black, #0000010)) dotted var(--thickness, 2px)");
        CssVariableUtil.resolveCssVariables(styles);
        //variables themselves are not resolve
        Assertions.assertEquals("var(--color-black-60, var(--color-black, #0000010)) dotted var(--thickness, 2px)", styles.get("--border-dotted"));
    }

    @Test
    public void resolveNestedComplexVariableTest2() {
        Map<String, String> styles = new HashMap<>();
        styles.put("--thickness", "1px");
        styles.put("--color-black", "rgb(255, 255, 0)");
        styles.put("--color-black-60", "rgb(0, 255, 0)");
        styles.put("border", "var(--thickness, 2px) dotted var(--color-black-60, var(--color-black, #0000010))");
        CssVariableUtil.resolveCssVariables(styles);
        Assertions.assertEquals(15, styles.size());
        Assertions.assertEquals("dotted", styles.get("border-right-style"));
        Assertions.assertEquals("1px", styles.get("border-right-width"));
        Assertions.assertEquals("rgb(0,255,0)", styles.get("border-right-color"));
    }

    @Test
    public void resolveVarInFunctionTest() {
        Map<String, String> styles = new HashMap<>();
        styles.put("--thickness", "1px");
        styles.put("border-right-width", "calc(var(--thickness))");
        CssVariableUtil.resolveCssVariables(styles);
        Assertions.assertEquals("calc(1px)", styles.get("border-right-width"));
    }

    @Test
    public void resolveVarInFunctionTest2() {
        Map<String, String> styles = new HashMap<>();
        styles.put("--thickness", "1px");
        styles.put("border-right-width", "calc(var(--thickness) + 20px)");
        CssVariableUtil.resolveCssVariables(styles);
        Assertions.assertEquals("calc(1px + 20px)", styles.get("border-right-width"));
    }

    @Test
    public void resolveVarInFunctionTest3() {
        Map<String, String> styles = new HashMap<>();
        styles.put("--thickness", "1px");
        styles.put("border-right-width", "calc(20px + var(--thickness) + 20px)");
        CssVariableUtil.resolveCssVariables(styles);
        Assertions.assertEquals("calc(20px + 1px + 20px)", styles.get("border-right-width"));
    }

    @Test
    public void resolveVarInFunctionTest4() {
        Map<String, String> styles = new HashMap<>();
        styles.put("--thickness", "1px");
        styles.put("border-right-width", "calc(20px + var(--invalid, var(--thickness, 20px)) + 20px)");
        CssVariableUtil.resolveCssVariables(styles);
        Assertions.assertEquals("calc(20px + 1px + 20px)", styles.get("border-right-width"));
    }

    @Test
    public void resolveNestedComplexVariableWithWhitespacesTest() {
        Map<String, String> styles = new HashMap<>();
        styles.put("--thickness", "1px");
        styles.put("--color-black", "black");
        styles.put("--color-black-60", "gray");
        styles.put("border", "var(  --thickness   , 2px) dotted var(  --color-black-60, var(    --color-black,   #0000010   ))  ");
        CssVariableUtil.resolveCssVariables(styles);
        Assertions.assertEquals(15, styles.size());
        Assertions.assertEquals("dotted", styles.get("border-right-style"));
        Assertions.assertEquals("1px", styles.get("border-right-width"));
        Assertions.assertEquals("gray", styles.get("border-right-color"));
    }
}
