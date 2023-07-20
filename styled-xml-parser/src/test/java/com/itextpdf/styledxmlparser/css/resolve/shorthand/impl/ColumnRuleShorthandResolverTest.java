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
package com.itextpdf.styledxmlparser.css.resolve.shorthand.impl;

import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.styledxmlparser.css.CssDeclaration;
import com.itextpdf.styledxmlparser.css.resolve.shorthand.IShorthandResolver;
import com.itextpdf.styledxmlparser.logs.StyledXmlParserLogMessageConstant;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.UnitTest;

import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class ColumnRuleShorthandResolverTest extends ExtendedITextTest {


    @Test
    public void initialOrInheritOrUnsetValuesTest() {
        IShorthandResolver resolver = new ColumnRuleShortHandResolver();

        String initialShorthand = CommonCssConstants.INITIAL;
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(initialShorthand);
        Assert.assertEquals(3, resolvedShorthand.size());

        Assert.assertEquals(CommonCssConstants.COLUMN_RULE_COLOR, resolvedShorthand.get(0).getProperty());
        Assert.assertEquals(CommonCssConstants.INITIAL, resolvedShorthand.get(0).getExpression());
        Assert.assertEquals(CommonCssConstants.COLUMN_RULE_WIDTH, resolvedShorthand.get(1).getProperty());
        Assert.assertEquals(CommonCssConstants.INITIAL, resolvedShorthand.get(1).getExpression());
        Assert.assertEquals(CommonCssConstants.COLUMN_RULE_STYLE, resolvedShorthand.get(2).getProperty());
        Assert.assertEquals(CommonCssConstants.INITIAL, resolvedShorthand.get(2).getExpression());

        String inheritShorthand = CommonCssConstants.INHERIT;
        resolvedShorthand = resolver.resolveShorthand(inheritShorthand);
        Assert.assertEquals(3, resolvedShorthand.size());
        Assert.assertEquals(CommonCssConstants.COLUMN_RULE_COLOR, resolvedShorthand.get(0).getProperty());
        Assert.assertEquals(CommonCssConstants.INHERIT, resolvedShorthand.get(0).getExpression());
        Assert.assertEquals(CommonCssConstants.COLUMN_RULE_WIDTH, resolvedShorthand.get(1).getProperty());
        Assert.assertEquals(CommonCssConstants.INHERIT, resolvedShorthand.get(1).getExpression());
        Assert.assertEquals(CommonCssConstants.COLUMN_RULE_STYLE, resolvedShorthand.get(2).getProperty());
        Assert.assertEquals(CommonCssConstants.INHERIT, resolvedShorthand.get(2).getExpression());

        String unsetShorthand = CommonCssConstants.UNSET;
        resolvedShorthand = resolver.resolveShorthand(unsetShorthand);
        Assert.assertEquals(3, resolvedShorthand.size());
        Assert.assertEquals(CommonCssConstants.COLUMN_RULE_COLOR, resolvedShorthand.get(0).getProperty());
        Assert.assertEquals(CommonCssConstants.UNSET, resolvedShorthand.get(0).getExpression());
        Assert.assertEquals(CommonCssConstants.COLUMN_RULE_WIDTH, resolvedShorthand.get(1).getProperty());
        Assert.assertEquals(CommonCssConstants.UNSET, resolvedShorthand.get(1).getExpression());
        Assert.assertEquals(CommonCssConstants.COLUMN_RULE_STYLE, resolvedShorthand.get(2).getProperty());
        Assert.assertEquals(CommonCssConstants.UNSET, resolvedShorthand.get(2).getExpression());

    }


    @Test
    @LogMessages(messages = @LogMessage(messageTemplate =
            StyledXmlParserLogMessageConstant.SHORTHAND_PROPERTY_CANNOT_BE_EMPTY, count = 4))
    public void emptyShorthandTest() {
        IShorthandResolver resolver = new ColumnRuleShortHandResolver();
        String emptyShorthand = "";
        Assert.assertEquals(Collections.<CssDeclaration>emptyList(), resolver.resolveShorthand(emptyShorthand));

        String shorthandWithSpaces = "    ";
        Assert.assertEquals(Collections.<CssDeclaration>emptyList(), resolver.resolveShorthand(shorthandWithSpaces));

        String shorthandWithTabs = "\t";
        Assert.assertEquals(Collections.<CssDeclaration>emptyList(), resolver.resolveShorthand(shorthandWithTabs));

        String shorthandWithNewLines = "\n";
        Assert.assertEquals(Collections.<CssDeclaration>emptyList(), resolver.resolveShorthand(shorthandWithNewLines));
    }

    @Test
    public void columnsWidthSingleTest01() {
        IShorthandResolver resolver = new ColumnRuleShortHandResolver();

        String shorthand = "10px";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assert.assertEquals(1, resolvedShorthand.size());
        Assert.assertEquals(CommonCssConstants.COLUMN_RULE_WIDTH, resolvedShorthand.get(0).getProperty());
        Assert.assertEquals("10px", resolvedShorthand.get(0).getExpression());
    }


    @Test
    public void columnsWidthSingleTest02() {
        IShorthandResolver resolver = new ColumnRuleShortHandResolver();

        String shorthand = "10em";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assert.assertEquals(1, resolvedShorthand.size());
        Assert.assertEquals(CommonCssConstants.COLUMN_RULE_WIDTH, resolvedShorthand.get(0).getProperty());
        Assert.assertEquals("10em", resolvedShorthand.get(0).getExpression());
    }

    @Test
    public void columnsWidthSingleTest03() {
        IShorthandResolver resolver = new ColumnRuleShortHandResolver();

        String shorthand = "thin";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assert.assertEquals(1, resolvedShorthand.size());
        Assert.assertEquals(CommonCssConstants.COLUMN_RULE_WIDTH, resolvedShorthand.get(0).getProperty());
        Assert.assertEquals("thin", resolvedShorthand.get(0).getExpression());
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate =
            StyledXmlParserLogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION, count = 1))
    public void columnsWidthSingleInvalidTest01() {
        IShorthandResolver resolver = new ColumnRuleShortHandResolver();

        String shorthand = "10dfx";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);
        Assert.assertEquals(0, resolvedShorthand.size());
    }


    @Test
    @LogMessages(messages = @LogMessage(messageTemplate =
            StyledXmlParserLogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION, count = 1))
    public void columnsWidthSingleInvalidTest02() {
        IShorthandResolver resolver = new ColumnRuleShortHandResolver();
        String shorthand = "big";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);
        Assert.assertEquals(0, resolvedShorthand.size());
    }

    @Test
    public void columnsStyleSingleTest01() {
        IShorthandResolver resolver = new ColumnRuleShortHandResolver();
        for (String borderStyleValue : CommonCssConstants.BORDER_STYLE_VALUES) {
            List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(borderStyleValue);
            Assert.assertEquals(1, resolvedShorthand.size());
            Assert.assertEquals(CommonCssConstants.COLUMN_RULE_STYLE, resolvedShorthand.get(0).getProperty());
            Assert.assertEquals(borderStyleValue, resolvedShorthand.get(0).getExpression());
        }
    }


    @Test
    @LogMessages(messages = @LogMessage(messageTemplate =
            StyledXmlParserLogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION, count = 1))
    public void columnsWidthStyleInvalidTest01() {
        IShorthandResolver resolver = new ColumnRuleShortHandResolver();

        String shorthand = "curly";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);
        Assert.assertEquals(0, resolvedShorthand.size());
    }


    @Test
    public void columnsColorSingleTest01() {
        IShorthandResolver resolver = new ColumnRuleShortHandResolver();
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand("red");
        Assert.assertEquals(1, resolvedShorthand.size());
        Assert.assertEquals(CommonCssConstants.COLUMN_RULE_COLOR, resolvedShorthand.get(0).getProperty());
        Assert.assertEquals("red", resolvedShorthand.get(0).getExpression());
    }


    @Test
    public void columnsColorSingleTest02() {
        IShorthandResolver resolver = new ColumnRuleShortHandResolver();
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand("rgb(10,20,30)");
        Assert.assertEquals(1, resolvedShorthand.size());
        Assert.assertEquals(CommonCssConstants.COLUMN_RULE_COLOR, resolvedShorthand.get(0).getProperty());
        Assert.assertEquals("rgb(10,20,30)", resolvedShorthand.get(0).getExpression());
    }

    @Test
    public void columnsColorSingleTest03() {
        IShorthandResolver resolver = new ColumnRuleShortHandResolver();
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand("rgb(10 ,20 ,30)");
        Assert.assertEquals(1, resolvedShorthand.size());
        Assert.assertEquals(CommonCssConstants.COLUMN_RULE_COLOR, resolvedShorthand.get(0).getProperty());
        Assert.assertEquals("rgb(10,20,30)", resolvedShorthand.get(0).getExpression());
    }

    @Test
    public void columnsColorSingleTest04() {
        IShorthandResolver resolver = new ColumnRuleShortHandResolver();
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand("#aabbcc");
        Assert.assertEquals(1, resolvedShorthand.size());
        Assert.assertEquals(CommonCssConstants.COLUMN_RULE_COLOR, resolvedShorthand.get(0).getProperty());
        Assert.assertEquals("#aabbcc", resolvedShorthand.get(0).getExpression());
    }

    @Test
    public void multipleTogether01() {
        IShorthandResolver resolver = new ColumnRuleShortHandResolver();
        String shorthand = "10px solid red";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);
        Assert.assertEquals(3, resolvedShorthand.size());
        Assert.assertEquals(CommonCssConstants.COLUMN_RULE_WIDTH, resolvedShorthand.get(0).getProperty());
        Assert.assertEquals("10px", resolvedShorthand.get(0).getExpression());
        Assert.assertEquals(CommonCssConstants.COLUMN_RULE_STYLE, resolvedShorthand.get(1).getProperty());
        Assert.assertEquals("solid", resolvedShorthand.get(1).getExpression());
        Assert.assertEquals(CommonCssConstants.COLUMN_RULE_COLOR, resolvedShorthand.get(2).getProperty());
        Assert.assertEquals("red", resolvedShorthand.get(2).getExpression());
    }

    @Test
    public void multipleTogether02() {
        IShorthandResolver resolver = new ColumnRuleShortHandResolver();
        String shorthand = "10px solid";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);
        Assert.assertEquals(2, resolvedShorthand.size());
        Assert.assertEquals(CommonCssConstants.COLUMN_RULE_WIDTH, resolvedShorthand.get(0).getProperty());
        Assert.assertEquals("10px", resolvedShorthand.get(0).getExpression());
        Assert.assertEquals(CommonCssConstants.COLUMN_RULE_STYLE, resolvedShorthand.get(1).getProperty());
        Assert.assertEquals("solid", resolvedShorthand.get(1).getExpression());
    }

    @Test
    public void multipleTogether03() {
        IShorthandResolver resolver = new ColumnRuleShortHandResolver();
        String shorthand = "solid blue";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);
        Assert.assertEquals(2, resolvedShorthand.size());
        Assert.assertEquals(CommonCssConstants.COLUMN_RULE_STYLE, resolvedShorthand.get(0).getProperty());
        Assert.assertEquals("solid", resolvedShorthand.get(0).getExpression());
        Assert.assertEquals(CommonCssConstants.COLUMN_RULE_COLOR, resolvedShorthand.get(1).getProperty());
        Assert.assertEquals("blue", resolvedShorthand.get(1).getExpression());
    }

    @Test
    public void multipleTogether04() {
        IShorthandResolver resolver = new ColumnRuleShortHandResolver();
        String shorthand = "thick inset blue";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);
        Assert.assertEquals(3, resolvedShorthand.size());
        Assert.assertEquals(CommonCssConstants.COLUMN_RULE_WIDTH, resolvedShorthand.get(0).getProperty());
        Assert.assertEquals("thick", resolvedShorthand.get(0).getExpression());
        Assert.assertEquals(CommonCssConstants.COLUMN_RULE_STYLE, resolvedShorthand.get(1).getProperty());
        Assert.assertEquals("inset", resolvedShorthand.get(1).getExpression());
        Assert.assertEquals(CommonCssConstants.COLUMN_RULE_COLOR, resolvedShorthand.get(2).getProperty());
        Assert.assertEquals("blue", resolvedShorthand.get(2).getExpression());
    }


}