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
import com.itextpdf.styledxmlparser.css.resolve.shorthand.impl.FlexFlowShorthandResolver;
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
public class FlexFlowShorthandResolverTest extends ExtendedITextTest {
    @Test
    public void initialOrInheritOrUnsetValuesTest() {
        IShorthandResolver resolver = new FlexFlowShorthandResolver();

        String initialShorthand = CommonCssConstants.INITIAL;
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(initialShorthand);
        Assert.assertEquals(2, resolvedShorthand.size());
        Assert.assertEquals(CommonCssConstants.FLEX_DIRECTION, resolvedShorthand.get(0).getProperty());
        Assert.assertEquals(CommonCssConstants.INITIAL, resolvedShorthand.get(0).getExpression());
        Assert.assertEquals(CommonCssConstants.FLEX_WRAP, resolvedShorthand.get(1).getProperty());
        Assert.assertEquals(CommonCssConstants.INITIAL, resolvedShorthand.get(1).getExpression());

        String inheritShorthand = CommonCssConstants.INHERIT;
        resolvedShorthand = resolver.resolveShorthand(inheritShorthand);
        Assert.assertEquals(2, resolvedShorthand.size());
        Assert.assertEquals(CommonCssConstants.FLEX_DIRECTION, resolvedShorthand.get(0).getProperty());
        Assert.assertEquals(CommonCssConstants.INHERIT, resolvedShorthand.get(0).getExpression());
        Assert.assertEquals(CommonCssConstants.FLEX_WRAP, resolvedShorthand.get(1).getProperty());
        Assert.assertEquals(CommonCssConstants.INHERIT, resolvedShorthand.get(1).getExpression());

        String unsetShorthand = CommonCssConstants.UNSET;
        resolvedShorthand = resolver.resolveShorthand(unsetShorthand);
        Assert.assertEquals(2, resolvedShorthand.size());
        Assert.assertEquals(CommonCssConstants.FLEX_DIRECTION, resolvedShorthand.get(0).getProperty());
        Assert.assertEquals(CommonCssConstants.UNSET, resolvedShorthand.get(0).getExpression());
        Assert.assertEquals(CommonCssConstants.FLEX_WRAP, resolvedShorthand.get(1).getProperty());
        Assert.assertEquals(CommonCssConstants.UNSET, resolvedShorthand.get(1).getExpression());
    }

    @Test
    public void initialWithSpacesTest() {
        IShorthandResolver resolver = new FlexFlowShorthandResolver();

        String initialWithSpacesShorthand = "  initial  ";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(initialWithSpacesShorthand);
        Assert.assertEquals(2, resolvedShorthand.size());
        Assert.assertEquals(CommonCssConstants.FLEX_DIRECTION, resolvedShorthand.get(0).getProperty());
        Assert.assertEquals(CommonCssConstants.INITIAL, resolvedShorthand.get(0).getExpression());
        Assert.assertEquals(CommonCssConstants.FLEX_WRAP, resolvedShorthand.get(1).getProperty());
        Assert.assertEquals(CommonCssConstants.INITIAL, resolvedShorthand.get(1).getExpression());
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION, count = 3))
    public void containsInitialOrInheritOrUnsetShorthandTest() {
        IShorthandResolver resolver = new FlexFlowShorthandResolver();

        String containsInitialShorthand = "row initial ";
        Assert.assertEquals(Collections.<CssDeclaration>emptyList(), resolver.resolveShorthand(containsInitialShorthand));

        String containsInheritShorthand = "inherit wrap";
        Assert.assertEquals(Collections.<CssDeclaration>emptyList(), resolver.resolveShorthand(containsInheritShorthand));

        String containsUnsetShorthand = "wrap unset";
        Assert.assertEquals(Collections.<CssDeclaration>emptyList(), resolver.resolveShorthand(containsUnsetShorthand));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.SHORTHAND_PROPERTY_CANNOT_BE_EMPTY, count = 2))
    public void emptyShorthandTest() {
        IShorthandResolver resolver = new FlexFlowShorthandResolver();
        String emptyShorthand = "";
        Assert.assertEquals(Collections.<CssDeclaration>emptyList(), resolver.resolveShorthand(emptyShorthand));

        String shorthandWithSpaces = "    ";
        Assert.assertEquals(Collections.<CssDeclaration>emptyList(), resolver.resolveShorthand(shorthandWithSpaces));
    }

    @Test
    public void shorthandWithOneDirectionValueTest() {
        IShorthandResolver resolver = new FlexFlowShorthandResolver();

        String shorthand = "column";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assert.assertEquals(2, resolvedShorthand.size());
        Assert.assertEquals(CommonCssConstants.FLEX_DIRECTION, resolvedShorthand.get(0).getProperty());
        Assert.assertEquals("column", resolvedShorthand.get(0).getExpression());
        Assert.assertEquals(CommonCssConstants.FLEX_WRAP, resolvedShorthand.get(1).getProperty());
        Assert.assertEquals("nowrap", resolvedShorthand.get(1).getExpression());
    }

    @Test
    public void shorthandWithOneWrapValueTest() {
        IShorthandResolver resolver = new FlexFlowShorthandResolver();

        String shorthand = CommonCssConstants.WRAP_REVERSE;
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assert.assertEquals(2, resolvedShorthand.size());
        Assert.assertEquals(CommonCssConstants.FLEX_WRAP, resolvedShorthand.get(0).getProperty());
        Assert.assertEquals(CommonCssConstants.WRAP_REVERSE, resolvedShorthand.get(0).getExpression());
        Assert.assertEquals(CommonCssConstants.FLEX_DIRECTION, resolvedShorthand.get(1).getProperty());
        Assert.assertEquals(CommonCssConstants.ROW, resolvedShorthand.get(1).getExpression());
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION, count = 1))
    public void shorthandWithOneInvalidValueTest() {
        IShorthandResolver resolver = new FlexFlowShorthandResolver();

        String shorthand = "invalid";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assert.assertEquals(0, resolvedShorthand.size());
    }

    @Test
    public void shorthandWithDirectionAndWrapValuesTest() {
        IShorthandResolver resolver = new FlexFlowShorthandResolver();

        String shorthand = CommonCssConstants.ROW_REVERSE + " " + CommonCssConstants.WRAP;
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assert.assertEquals(CommonCssConstants.FLEX_DIRECTION, resolvedShorthand.get(0).getProperty());
        Assert.assertEquals(CommonCssConstants.ROW_REVERSE, resolvedShorthand.get(0).getExpression());
        Assert.assertEquals(CommonCssConstants.FLEX_WRAP, resolvedShorthand.get(1).getProperty());
        Assert.assertEquals(CommonCssConstants.WRAP, resolvedShorthand.get(1).getExpression());
    }

    @Test
    public void shorthandWithWrapAndDirectionValuesTest() {
        IShorthandResolver resolver = new FlexFlowShorthandResolver();

        String shorthand = "wrap-reverse column";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assert.assertEquals(CommonCssConstants.FLEX_DIRECTION, resolvedShorthand.get(0).getProperty());
        Assert.assertEquals(CommonCssConstants.COLUMN, resolvedShorthand.get(0).getExpression());
        Assert.assertEquals(CommonCssConstants.FLEX_WRAP, resolvedShorthand.get(1).getProperty());
        Assert.assertEquals(CommonCssConstants.WRAP_REVERSE, resolvedShorthand.get(1).getExpression());
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION))
    public void shorthandWithTwoDirectionValuesTest() {
        IShorthandResolver resolver = new FlexFlowShorthandResolver();

        String shorthand = "column-reverse row";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assert.assertEquals(Collections.<CssDeclaration>emptyList(), resolvedShorthand);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION))
    public void shorthandWithTwoWrapValuesTest() {
        IShorthandResolver resolver = new FlexFlowShorthandResolver();

        String shorthand = "nowrap wrap-reverse";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assert.assertEquals(Collections.<CssDeclaration>emptyList(), resolvedShorthand);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION))
    public void shorthandWithTwoValuesAndSecondIsInvalidTest() {
        IShorthandResolver resolver = new FlexFlowShorthandResolver();

        String shorthand = "column-reverse invalid";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assert.assertEquals(Collections.<CssDeclaration>emptyList(), resolvedShorthand);
    }
}
