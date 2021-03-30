/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
    Authors: iText Software.

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
package com.itextpdf.styledxmlparser.css.resolve.shorthand;

import com.itextpdf.styledxmlparser.LogMessageConstant;
import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.styledxmlparser.css.CssDeclaration;
import com.itextpdf.styledxmlparser.css.resolve.shorthand.impl.FlexShorthandResolver;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.Collections;
import java.util.List;

@Category(UnitTest.class)
public class FlexShorthandResolverTest extends ExtendedITextTest {
    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.SHORTHAND_PROPERTY_CANNOT_BE_EMPTY, count = 2))
    public void emptyShorthandTest() {
        String emptyShorthand = "";
        IShorthandResolver resolver = new FlexShorthandResolver();
        Assert.assertEquals(Collections.<CssDeclaration>emptyList(), resolver.resolveShorthand(emptyShorthand));

        String shorthandWithSpaces = "    ";
        Assert.assertEquals(Collections.<CssDeclaration>emptyList(), resolver.resolveShorthand(shorthandWithSpaces));
    }

    @Test
    public void initialOrInheritOrUnsetShorthandTest() {
        String initialShorthand = CommonCssConstants.INITIAL;
        IShorthandResolver resolver = new FlexShorthandResolver();
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(initialShorthand);
        Assert.assertEquals(3, resolvedShorthand.size());
        Assert.assertEquals(CommonCssConstants.FLEX_GROW, resolvedShorthand.get(0).getProperty());
        Assert.assertEquals(CommonCssConstants.INITIAL, resolvedShorthand.get(0).getExpression());
        Assert.assertEquals(CommonCssConstants.FLEX_SHRINK, resolvedShorthand.get(1).getProperty());
        Assert.assertEquals(CommonCssConstants.INITIAL, resolvedShorthand.get(1).getExpression());
        Assert.assertEquals(CommonCssConstants.FLEX_BASIS, resolvedShorthand.get(2).getProperty());
        Assert.assertEquals(CommonCssConstants.INITIAL, resolvedShorthand.get(2).getExpression());

        String inheritShorthand = CommonCssConstants.INHERIT;
        resolvedShorthand = resolver.resolveShorthand(inheritShorthand);
        Assert.assertEquals(3, resolvedShorthand.size());
        Assert.assertEquals(CommonCssConstants.FLEX_GROW, resolvedShorthand.get(0).getProperty());
        Assert.assertEquals(CommonCssConstants.INHERIT, resolvedShorthand.get(0).getExpression());
        Assert.assertEquals(CommonCssConstants.FLEX_SHRINK, resolvedShorthand.get(1).getProperty());
        Assert.assertEquals(CommonCssConstants.INHERIT, resolvedShorthand.get(1).getExpression());
        Assert.assertEquals(CommonCssConstants.FLEX_BASIS, resolvedShorthand.get(2).getProperty());
        Assert.assertEquals(CommonCssConstants.INHERIT, resolvedShorthand.get(2).getExpression());

        String unsetShorthand = CommonCssConstants.UNSET;
        resolvedShorthand = resolver.resolveShorthand(unsetShorthand);
        Assert.assertEquals(3, resolvedShorthand.size());
        Assert.assertEquals(CommonCssConstants.FLEX_GROW, resolvedShorthand.get(0).getProperty());
        Assert.assertEquals(CommonCssConstants.UNSET, resolvedShorthand.get(0).getExpression());
        Assert.assertEquals(CommonCssConstants.FLEX_SHRINK, resolvedShorthand.get(1).getProperty());
        Assert.assertEquals(CommonCssConstants.UNSET, resolvedShorthand.get(1).getExpression());
        Assert.assertEquals(CommonCssConstants.FLEX_BASIS, resolvedShorthand.get(2).getProperty());
        Assert.assertEquals(CommonCssConstants.UNSET, resolvedShorthand.get(2).getExpression());
    }

    @Test
    public void initialWithSpacesTest() {
        IShorthandResolver resolver = new FlexShorthandResolver();

        String initialWithSpacesShorthand = "  initial  ";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(initialWithSpacesShorthand);
        Assert.assertEquals(3, resolvedShorthand.size());
        Assert.assertEquals(CommonCssConstants.FLEX_GROW, resolvedShorthand.get(0).getProperty());
        Assert.assertEquals(CommonCssConstants.INITIAL, resolvedShorthand.get(0).getExpression());
        Assert.assertEquals(CommonCssConstants.FLEX_SHRINK, resolvedShorthand.get(1).getProperty());
        Assert.assertEquals(CommonCssConstants.INITIAL, resolvedShorthand.get(1).getExpression());
        Assert.assertEquals(CommonCssConstants.FLEX_BASIS, resolvedShorthand.get(2).getProperty());
        Assert.assertEquals(CommonCssConstants.INITIAL, resolvedShorthand.get(2).getExpression());
    }

    @Test
    public void autoShorthandTest() {
        String initialShorthand = CommonCssConstants.AUTO;
        IShorthandResolver resolver = new FlexShorthandResolver();
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(initialShorthand);
        Assert.assertEquals(3, resolvedShorthand.size());
        Assert.assertEquals(CommonCssConstants.FLEX_GROW, resolvedShorthand.get(0).getProperty());
        Assert.assertEquals("1", resolvedShorthand.get(0).getExpression());
        Assert.assertEquals(CommonCssConstants.FLEX_SHRINK, resolvedShorthand.get(1).getProperty());
        Assert.assertEquals("1", resolvedShorthand.get(1).getExpression());
        Assert.assertEquals(CommonCssConstants.FLEX_BASIS, resolvedShorthand.get(2).getProperty());
        Assert.assertEquals(CommonCssConstants.AUTO, resolvedShorthand.get(2).getExpression());
    }

    @Test
    public void noneShorthandTest() {
        String initialShorthand = CommonCssConstants.NONE;
        IShorthandResolver resolver = new FlexShorthandResolver();
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(initialShorthand);
        Assert.assertEquals(3, resolvedShorthand.size());
        Assert.assertEquals(CommonCssConstants.FLEX_GROW, resolvedShorthand.get(0).getProperty());
        Assert.assertEquals("0", resolvedShorthand.get(0).getExpression());
        Assert.assertEquals(CommonCssConstants.FLEX_SHRINK, resolvedShorthand.get(1).getProperty());
        Assert.assertEquals("0", resolvedShorthand.get(1).getExpression());
        Assert.assertEquals(CommonCssConstants.FLEX_BASIS, resolvedShorthand.get(2).getProperty());
        Assert.assertEquals(CommonCssConstants.AUTO, resolvedShorthand.get(2).getExpression());
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION, count = 3))
    public void containsInitialOrInheritOrUnsetShorthandTest() {
        IShorthandResolver resolver = new FlexShorthandResolver();

        String containsInitialShorthand = "1 initial 50px";
        Assert.assertEquals(Collections.<CssDeclaration>emptyList(), resolver.resolveShorthand(containsInitialShorthand));

        String containsInheritShorthand = "inherit 2 50px";
        Assert.assertEquals(Collections.<CssDeclaration>emptyList(), resolver.resolveShorthand(containsInheritShorthand));

        String containsUnsetShorthand = "0 2 unset";
        Assert.assertEquals(Collections.<CssDeclaration>emptyList(), resolver.resolveShorthand(containsUnsetShorthand));
    }

    @Test
    public void shorthandWithOneUnitlessNumberValueTest() {
        IShorthandResolver resolver = new FlexShorthandResolver();

        String shorthand = "5";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assert.assertEquals(3, resolvedShorthand.size());
        Assert.assertEquals(CommonCssConstants.FLEX_GROW, resolvedShorthand.get(0).getProperty());
        Assert.assertEquals("5", resolvedShorthand.get(0).getExpression());
        Assert.assertEquals(CommonCssConstants.FLEX_SHRINK, resolvedShorthand.get(1).getProperty());
        Assert.assertEquals("1", resolvedShorthand.get(1).getExpression());
        Assert.assertEquals(CommonCssConstants.FLEX_BASIS, resolvedShorthand.get(2).getProperty());
        Assert.assertEquals("0", resolvedShorthand.get(2).getExpression());
    }

    @Test
    public void shorthandWithOneUnitNumberValueTest() {
        IShorthandResolver resolver = new FlexShorthandResolver();

        String shorthand = "5px";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assert.assertEquals(3, resolvedShorthand.size());
        Assert.assertEquals(CommonCssConstants.FLEX_BASIS, resolvedShorthand.get(0).getProperty());
        Assert.assertEquals("5px", resolvedShorthand.get(0).getExpression());
        Assert.assertEquals(CommonCssConstants.FLEX_GROW, resolvedShorthand.get(1).getProperty());
        Assert.assertEquals("0", resolvedShorthand.get(1).getExpression());
        Assert.assertEquals(CommonCssConstants.FLEX_SHRINK, resolvedShorthand.get(2).getProperty());
        Assert.assertEquals("1", resolvedShorthand.get(2).getExpression());
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION))
    public void shorthandWithOneInvalidValueTest() {
        IShorthandResolver resolver = new FlexShorthandResolver();

        String shorthand = "5pixels";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assert.assertEquals(0, resolvedShorthand.size());
    }

    @Test
    public void shorthandWithTwoUnitlessNumberValuesTest() {
        IShorthandResolver resolver = new FlexShorthandResolver();

        String shorthand = "5 7";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assert.assertEquals(3, resolvedShorthand.size());
        Assert.assertEquals(CommonCssConstants.FLEX_GROW, resolvedShorthand.get(0).getProperty());
        Assert.assertEquals("5", resolvedShorthand.get(0).getExpression());
        Assert.assertEquals(CommonCssConstants.FLEX_SHRINK, resolvedShorthand.get(1).getProperty());
        Assert.assertEquals("7", resolvedShorthand.get(1).getExpression());
        Assert.assertEquals(CommonCssConstants.FLEX_BASIS, resolvedShorthand.get(2).getProperty());
        Assert.assertEquals("0", resolvedShorthand.get(2).getExpression());
    }

    @Test
    public void shorthandWithUnitlessAndUnitNumberValuesTest() {
        IShorthandResolver resolver = new FlexShorthandResolver();

        String shorthand = "5 7px";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assert.assertEquals(3, resolvedShorthand.size());
        Assert.assertEquals(CommonCssConstants.FLEX_GROW, resolvedShorthand.get(0).getProperty());
        Assert.assertEquals("5", resolvedShorthand.get(0).getExpression());
        Assert.assertEquals(CommonCssConstants.FLEX_BASIS, resolvedShorthand.get(1).getProperty());
        Assert.assertEquals("7px", resolvedShorthand.get(1).getExpression());
        Assert.assertEquals(CommonCssConstants.FLEX_SHRINK, resolvedShorthand.get(2).getProperty());
        Assert.assertEquals("1", resolvedShorthand.get(2).getExpression());
    }

    @Test
    public void shorthandWithUnitAndUnitlessNumberValuesTest() {
        IShorthandResolver resolver = new FlexShorthandResolver();

        String shorthand = "5px 7";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assert.assertEquals(3, resolvedShorthand.size());
        Assert.assertEquals(CommonCssConstants.FLEX_BASIS, resolvedShorthand.get(0).getProperty());
        Assert.assertEquals("5px", resolvedShorthand.get(0).getExpression());
        Assert.assertEquals(CommonCssConstants.FLEX_GROW, resolvedShorthand.get(1).getProperty());
        Assert.assertEquals("7", resolvedShorthand.get(1).getExpression());
        Assert.assertEquals(CommonCssConstants.FLEX_SHRINK, resolvedShorthand.get(2).getProperty());
        Assert.assertEquals("1", resolvedShorthand.get(2).getExpression());
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION))
    public void shorthandWithTwoUnitValuesTest() {
        IShorthandResolver resolver = new FlexShorthandResolver();

        String shorthand = "5px 7px";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assert.assertEquals(0, resolvedShorthand.size());
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION))
    public void shorthandWithOneUnitlessAndOneInvalidValuesTest() {
        IShorthandResolver resolver = new FlexShorthandResolver();

        String shorthand = "5 invalid";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assert.assertEquals(0, resolvedShorthand.size());
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION))
    public void shorthandWithTwoValuesAndFirstIsInvalidTest() {
        IShorthandResolver resolver = new FlexShorthandResolver();

        String shorthand = "invalid 5px";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assert.assertEquals(0, resolvedShorthand.size());
    }

    @Test
    public void shorthandWithTwoUnitlessAndOneUnitValuesTest() {
        IShorthandResolver resolver = new FlexShorthandResolver();

        String shorthand = "5 7 10px";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assert.assertEquals(3, resolvedShorthand.size());
        Assert.assertEquals(CommonCssConstants.FLEX_GROW, resolvedShorthand.get(0).getProperty());
        Assert.assertEquals("5", resolvedShorthand.get(0).getExpression());
        Assert.assertEquals(CommonCssConstants.FLEX_SHRINK, resolvedShorthand.get(1).getProperty());
        Assert.assertEquals("7", resolvedShorthand.get(1).getExpression());
        Assert.assertEquals(CommonCssConstants.FLEX_BASIS, resolvedShorthand.get(2).getProperty());
        Assert.assertEquals("10px", resolvedShorthand.get(2).getExpression());
    }

    @Test
    public void shorthandWithOneUnitAndTwoUnitlessValuesTest() {
        IShorthandResolver resolver = new FlexShorthandResolver();

        String shorthand = "5px 7 10";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assert.assertEquals(3, resolvedShorthand.size());
        Assert.assertEquals(CommonCssConstants.FLEX_GROW, resolvedShorthand.get(0).getProperty());
        Assert.assertEquals("7", resolvedShorthand.get(0).getExpression());
        Assert.assertEquals(CommonCssConstants.FLEX_SHRINK, resolvedShorthand.get(1).getProperty());
        Assert.assertEquals("10", resolvedShorthand.get(1).getExpression());
        Assert.assertEquals(CommonCssConstants.FLEX_BASIS, resolvedShorthand.get(2).getProperty());
        Assert.assertEquals("5px", resolvedShorthand.get(2).getExpression());
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION))
    public void shorthandWithThreeUnitlessValuesTest() {
        IShorthandResolver resolver = new FlexShorthandResolver();

        String shorthand = "5 7 10";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assert.assertEquals(0, resolvedShorthand.size());
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION))
    public void shorthandWithOneUnitlessOneUnitAndOneUnitlessValuesTest() {
        IShorthandResolver resolver = new FlexShorthandResolver();

        String shorthand = "5 7px 10";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assert.assertEquals(0, resolvedShorthand.size());
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION))
    public void shorthandWithThreeUnitValuesTest() {
        IShorthandResolver resolver = new FlexShorthandResolver();

        String shorthand = "5px 7px 10px";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assert.assertEquals(0, resolvedShorthand.size());
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION))
    public void shorthandWithOneUnitOneUnitlessAndOneUnitValuesTest() {
        IShorthandResolver resolver = new FlexShorthandResolver();

        String shorthand = "5px 7 10px";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assert.assertEquals(0, resolvedShorthand.size());
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION))
    public void shorthandWithThreeValuesAndFirstIsInvalidTest() {
        IShorthandResolver resolver = new FlexShorthandResolver();

        String shorthand = "invalid 7 10";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assert.assertEquals(0, resolvedShorthand.size());
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION))
    public void shorthandWithFourValuesTest() {
        IShorthandResolver resolver = new FlexShorthandResolver();

        String shorthand = "5 7 10 13";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assert.assertEquals(Collections.<CssDeclaration>emptyList(), resolvedShorthand);
    }
}
