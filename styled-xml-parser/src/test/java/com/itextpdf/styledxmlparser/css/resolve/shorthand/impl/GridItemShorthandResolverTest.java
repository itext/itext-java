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
package com.itextpdf.styledxmlparser.css.resolve.shorthand.impl;

import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.styledxmlparser.css.CssDeclaration;
import com.itextpdf.styledxmlparser.css.resolve.shorthand.IShorthandResolver;
import com.itextpdf.styledxmlparser.logs.StyledXmlParserLogMessageConstant;
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
public class GridItemShorthandResolverTest extends ExtendedITextTest {
    @Test
    public void initialOrInheritOrUnsetValuesTest() {
        IShorthandResolver resolver = new GridRowShorthandResolver();

        String initialShorthand = CommonCssConstants.INITIAL;
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(initialShorthand);
        Assert.assertEquals(0, resolvedShorthand.size());

        String inheritShorthand = CommonCssConstants.INHERIT;
        resolvedShorthand = resolver.resolveShorthand(inheritShorthand);
        Assert.assertEquals(0, resolvedShorthand.size());

        String unsetShorthand = CommonCssConstants.UNSET;
        resolvedShorthand = resolver.resolveShorthand(unsetShorthand);
        Assert.assertEquals(0, resolvedShorthand.size());
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate =
            StyledXmlParserLogMessageConstant.SHORTHAND_PROPERTY_CANNOT_BE_EMPTY, count = 4))
    public void emptyShorthandTest() {
        IShorthandResolver resolver = new GridColumnShorthandResolver();
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
    public void basicRowValuesTest() {
        IShorthandResolver resolver = new GridRowShorthandResolver();

        String shorthand = "span 2 / 4";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assert.assertEquals(2, resolvedShorthand.size());
        Assert.assertEquals(CommonCssConstants.GRID_ROW_START, resolvedShorthand.get(0).getProperty());
        Assert.assertEquals(CommonCssConstants.GRID_ROW_END, resolvedShorthand.get(1).getProperty());
        Assert.assertEquals("span 2", resolvedShorthand.get(0).getExpression());
        Assert.assertEquals("4", resolvedShorthand.get(1).getExpression());
    }

    @Test
    public void basicColumnValuesTest() {
        IShorthandResolver resolver = new GridColumnShorthandResolver();

        String shorthand = "3 / span 6";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assert.assertEquals(2, resolvedShorthand.size());
        Assert.assertEquals(CommonCssConstants.GRID_COLUMN_START, resolvedShorthand.get(0).getProperty());
        Assert.assertEquals(CommonCssConstants.GRID_COLUMN_END, resolvedShorthand.get(1).getProperty());
        Assert.assertEquals("3", resolvedShorthand.get(0).getExpression());
        Assert.assertEquals("span 6", resolvedShorthand.get(1).getExpression());
    }

    @Test
    public void singleValueTest() {
        IShorthandResolver resolver = new GridColumnShorthandResolver();

        String shorthand = "3";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assert.assertEquals(2, resolvedShorthand.size());
        Assert.assertEquals(CommonCssConstants.GRID_COLUMN_START, resolvedShorthand.get(0).getProperty());
        Assert.assertEquals(CommonCssConstants.GRID_COLUMN_END, resolvedShorthand.get(1).getProperty());
        Assert.assertEquals("3", resolvedShorthand.get(0).getExpression());
        Assert.assertEquals("3", resolvedShorthand.get(1).getExpression());
    }

    @Test
    public void singleValueSpanTest() {
        IShorthandResolver resolver = new GridColumnShorthandResolver();

        String shorthand = "span 3";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assert.assertEquals(1, resolvedShorthand.size());
        Assert.assertEquals(CommonCssConstants.GRID_COLUMN_START, resolvedShorthand.get(0).getProperty());
        Assert.assertEquals("span 3", resolvedShorthand.get(0).getExpression());
    }
}
