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
import com.itextpdf.styledxmlparser.css.resolve.shorthand.impl.PlaceItemsShorthandResolver;
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
public class PlaceItemsShorthandResolverTest extends ExtendedITextTest {
    @Test
    public void initialOrInheritOrUnsetValuesTest() {
        IShorthandResolver resolver = new PlaceItemsShorthandResolver();

        String initialShorthand = CommonCssConstants.INITIAL;
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(initialShorthand);
        Assert.assertEquals(2, resolvedShorthand.size());
        Assert.assertEquals(CommonCssConstants.ALIGN_ITEMS, resolvedShorthand.get(0).getProperty());
        Assert.assertEquals(CommonCssConstants.INITIAL, resolvedShorthand.get(0).getExpression());
        Assert.assertEquals(CommonCssConstants.JUSTIFY_ITEMS, resolvedShorthand.get(1).getProperty());
        Assert.assertEquals(CommonCssConstants.INITIAL, resolvedShorthand.get(1).getExpression());

        String inheritShorthand = CommonCssConstants.INHERIT;
        resolvedShorthand = resolver.resolveShorthand(inheritShorthand);
        Assert.assertEquals(2, resolvedShorthand.size());
        Assert.assertEquals(CommonCssConstants.ALIGN_ITEMS, resolvedShorthand.get(0).getProperty());
        Assert.assertEquals(CommonCssConstants.INHERIT, resolvedShorthand.get(0).getExpression());
        Assert.assertEquals(CommonCssConstants.JUSTIFY_ITEMS, resolvedShorthand.get(1).getProperty());
        Assert.assertEquals(CommonCssConstants.INHERIT, resolvedShorthand.get(1).getExpression());

        String unsetShorthand = CommonCssConstants.UNSET;
        resolvedShorthand = resolver.resolveShorthand(unsetShorthand);
        Assert.assertEquals(2, resolvedShorthand.size());
        Assert.assertEquals(CommonCssConstants.ALIGN_ITEMS, resolvedShorthand.get(0).getProperty());
        Assert.assertEquals(CommonCssConstants.UNSET, resolvedShorthand.get(0).getExpression());
        Assert.assertEquals(CommonCssConstants.JUSTIFY_ITEMS, resolvedShorthand.get(1).getProperty());
        Assert.assertEquals(CommonCssConstants.UNSET, resolvedShorthand.get(1).getExpression());
    }

    @Test
    public void initialWithSpacesTest() {
        IShorthandResolver resolver = new PlaceItemsShorthandResolver();

        String initialWithSpacesShorthand = "  initial  ";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(initialWithSpacesShorthand);
        Assert.assertEquals(2, resolvedShorthand.size());
        Assert.assertEquals(CommonCssConstants.ALIGN_ITEMS, resolvedShorthand.get(0).getProperty());
        Assert.assertEquals(CommonCssConstants.INITIAL, resolvedShorthand.get(0).getExpression());
        Assert.assertEquals(CommonCssConstants.JUSTIFY_ITEMS, resolvedShorthand.get(1).getProperty());
        Assert.assertEquals(CommonCssConstants.INITIAL, resolvedShorthand.get(1).getExpression());
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION, count = 3))
    public void containsInitialOrInheritOrUnsetShorthandTest() {
        IShorthandResolver resolver = new PlaceItemsShorthandResolver();

        String containsInitialShorthand = "start initial ";
        Assert.assertEquals(Collections.<CssDeclaration>emptyList(), resolver.resolveShorthand(containsInitialShorthand));

        String containsInheritShorthand = "inherit safe end";
        Assert.assertEquals(Collections.<CssDeclaration>emptyList(), resolver.resolveShorthand(containsInheritShorthand));

        String containsUnsetShorthand = "baseline unset";
        Assert.assertEquals(Collections.<CssDeclaration>emptyList(), resolver.resolveShorthand(containsUnsetShorthand));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.SHORTHAND_PROPERTY_CANNOT_BE_EMPTY, count = 2))
    public void emptyShorthandTest() {
        IShorthandResolver resolver = new PlaceItemsShorthandResolver();
        String emptyShorthand = "";
        Assert.assertEquals(Collections.<CssDeclaration>emptyList(), resolver.resolveShorthand(emptyShorthand));

        String shorthandWithSpaces = "    ";
        Assert.assertEquals(Collections.<CssDeclaration>emptyList(), resolver.resolveShorthand(shorthandWithSpaces));
    }

    @Test
    public void shorthandWithOneValidWordTest() {
        IShorthandResolver resolver = new PlaceItemsShorthandResolver();

        String shorthand = "baseline";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assert.assertEquals(2, resolvedShorthand.size());
        Assert.assertEquals(CommonCssConstants.ALIGN_ITEMS, resolvedShorthand.get(0).getProperty());
        Assert.assertEquals("baseline", resolvedShorthand.get(0).getExpression());
        Assert.assertEquals(CommonCssConstants.JUSTIFY_ITEMS, resolvedShorthand.get(1).getProperty());
        Assert.assertEquals("baseline", resolvedShorthand.get(1).getExpression());
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION))
    public void shorthandWithOneInvalidAlignItemsWordTest() {
        IShorthandResolver resolver = new PlaceItemsShorthandResolver();

        String shorthand = "legacy";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assert.assertEquals(0, resolvedShorthand.size());
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION))
    public void shorthandWithOneInvalidWordTest() {
        IShorthandResolver resolver = new PlaceItemsShorthandResolver();

        String shorthand = "invalid";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assert.assertEquals(0, resolvedShorthand.size());
    }

    @Test
    public void shorthandWithTwoWordsAlignItemsTest() {
        IShorthandResolver resolver = new PlaceItemsShorthandResolver();

        String shorthand = "unsafe start";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assert.assertEquals(2, resolvedShorthand.size());
        Assert.assertEquals(CommonCssConstants.ALIGN_ITEMS, resolvedShorthand.get(0).getProperty());
        Assert.assertEquals("unsafe start", resolvedShorthand.get(0).getExpression());
        Assert.assertEquals(CommonCssConstants.JUSTIFY_ITEMS, resolvedShorthand.get(1).getProperty());
        Assert.assertEquals("unsafe start", resolvedShorthand.get(1).getExpression());
    }

    @Test
    public void shorthandWithOneWordAlignItemsAndOneWordJustifyItemsTest() {
        IShorthandResolver resolver = new PlaceItemsShorthandResolver();

        String shorthand = CommonCssConstants.CENTER + " " + CommonCssConstants.LEGACY + " " + CommonCssConstants.RIGHT;
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assert.assertEquals(2, resolvedShorthand.size());
        Assert.assertEquals(CommonCssConstants.ALIGN_ITEMS, resolvedShorthand.get(0).getProperty());
        Assert.assertEquals(CommonCssConstants.CENTER, resolvedShorthand.get(0).getExpression());
        Assert.assertEquals(CommonCssConstants.JUSTIFY_ITEMS, resolvedShorthand.get(1).getProperty());
        Assert.assertEquals(CommonCssConstants.LEGACY + " " + CommonCssConstants.RIGHT, resolvedShorthand.get(1).getExpression());
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION))
    public void shorthandWithTwoWordsAndFirstWordIsInvalidTest() {
        IShorthandResolver resolver = new PlaceItemsShorthandResolver();

        String shorthand = "invalid self-end";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assert.assertEquals(0, resolvedShorthand.size());
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION))
    public void shorthandWithTwoWordsAndSecondWordIsInvalidTest() {
        IShorthandResolver resolver = new PlaceItemsShorthandResolver();

        String shorthand = "flex-start invalid";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assert.assertEquals(0, resolvedShorthand.size());
    }

    @Test
    public void shorthandWithOneWordAlignItemsAndTwoWordsJustifyItemsTest() {
        IShorthandResolver resolver = new PlaceItemsShorthandResolver();

        String shorthand = "flex-start legacy right";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assert.assertEquals(2, resolvedShorthand.size());
        Assert.assertEquals(CommonCssConstants.ALIGN_ITEMS, resolvedShorthand.get(0).getProperty());
        Assert.assertEquals("flex-start", resolvedShorthand.get(0).getExpression());
        Assert.assertEquals(CommonCssConstants.JUSTIFY_ITEMS, resolvedShorthand.get(1).getProperty());
        Assert.assertEquals("legacy right", resolvedShorthand.get(1).getExpression());
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION))
    public void shorthandWithOneWordAlignItemsAndInvalidTwoWordsJustifyItemsTest() {
        IShorthandResolver resolver = new PlaceItemsShorthandResolver();

        String shorthand = "flex-start legacy invalid";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assert.assertEquals(0, resolvedShorthand.size());
    }

    @Test
    public void shorthandWithTwoWordsAlignItemsAndOneWordJustifyItemsTest() {
        IShorthandResolver resolver = new PlaceItemsShorthandResolver();

        String shorthand = "unsafe flex-start normal";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assert.assertEquals(2, resolvedShorthand.size());
        Assert.assertEquals(CommonCssConstants.ALIGN_ITEMS, resolvedShorthand.get(0).getProperty());
        Assert.assertEquals("unsafe flex-start", resolvedShorthand.get(0).getExpression());
        Assert.assertEquals(CommonCssConstants.JUSTIFY_ITEMS, resolvedShorthand.get(1).getProperty());
        Assert.assertEquals("normal", resolvedShorthand.get(1).getExpression());
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION))
    public void shorthandWithTwoWordsAlignItemsAndInvalidOneWordJustifyItemsTest() {
        IShorthandResolver resolver = new PlaceItemsShorthandResolver();

        String shorthand = "unsafe flex-start invalid";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assert.assertEquals(0, resolvedShorthand.size());
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION))
    public void shorthandWithThreeWordsAndInvalidAlignItemsTest() {
        IShorthandResolver resolver = new PlaceItemsShorthandResolver();

        String shorthand = "invalid safe self-end";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assert.assertEquals(0, resolvedShorthand.size());
    }

    @Test
    public void shorthandWithTwoWordsAlignItemsAndTwoWordsJustifyItemsTest() {
        IShorthandResolver resolver = new PlaceItemsShorthandResolver();

        String shorthand = "first baseline legacy center";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assert.assertEquals(2, resolvedShorthand.size());
        Assert.assertEquals(CommonCssConstants.ALIGN_ITEMS, resolvedShorthand.get(0).getProperty());
        Assert.assertEquals("first baseline", resolvedShorthand.get(0).getExpression());
        Assert.assertEquals(CommonCssConstants.JUSTIFY_ITEMS, resolvedShorthand.get(1).getProperty());
        Assert.assertEquals("legacy center", resolvedShorthand.get(1).getExpression());
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION))
    public void shorthandWithTwoWordsAlignItemsAndInvalidTwoWordsJustifyItemsTest() {
        IShorthandResolver resolver = new PlaceItemsShorthandResolver();

        String shorthand = "first baseline invalid center";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assert.assertEquals(0, resolvedShorthand.size());
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION))
    public void shorthandWithInvalidTwoWordsAlignItemsAndTwoWordsJustifyItemsTest() {
        IShorthandResolver resolver = new PlaceItemsShorthandResolver();

        String shorthand = "invalid baseline legacy left";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assert.assertEquals(0, resolvedShorthand.size());
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION))
    public void shorthandWithFiveWordsTest() {
        IShorthandResolver resolver = new PlaceItemsShorthandResolver();

        String shorthand = "last baseline unsafe safe center";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assert.assertEquals(Collections.<CssDeclaration>emptyList(), resolvedShorthand);
    }
}
