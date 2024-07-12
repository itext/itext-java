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
public class GridTemplateShorthandResolverTest extends ExtendedITextTest {
    @Test
    public void initialOrInheritOrUnsetValuesTest() {
        IShorthandResolver resolver = new GridTemplateShorthandResolver();

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
        IShorthandResolver resolver = new GridTemplateShorthandResolver();
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
    public void basicTest() {
        IShorthandResolver resolver = new GridTemplateShorthandResolver();

        String shorthand = "auto 1fr / auto 1fr auto";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assert.assertEquals(2, resolvedShorthand.size());
        Assert.assertEquals(CommonCssConstants.GRID_TEMPLATE_ROWS, resolvedShorthand.get(0).getProperty());
        Assert.assertEquals(CommonCssConstants.GRID_TEMPLATE_COLUMNS, resolvedShorthand.get(1).getProperty());
        Assert.assertEquals("auto 1fr", resolvedShorthand.get(0).getExpression());
        Assert.assertEquals("auto 1fr auto", resolvedShorthand.get(1).getExpression());
    }

    @Test
    public void lineNamesTest() {
        IShorthandResolver resolver = new GridTemplateShorthandResolver();

        String shorthand = "[linename] 100px / [columnname1] 30% [columnname2] 70%";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assert.assertEquals(2, resolvedShorthand.size());
        Assert.assertEquals(CommonCssConstants.GRID_TEMPLATE_ROWS, resolvedShorthand.get(0).getProperty());
        Assert.assertEquals(CommonCssConstants.GRID_TEMPLATE_COLUMNS, resolvedShorthand.get(1).getProperty());
        Assert.assertEquals("[linename] 100px", resolvedShorthand.get(0).getExpression());
        Assert.assertEquals("[columnname1] 30% [columnname2] 70%", resolvedShorthand.get(1).getExpression());
    }

    @Test
    public void areaTest() {
        IShorthandResolver resolver = new GridTemplateShorthandResolver();

        String shorthand = "'a a a'    'b b b'";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assert.assertEquals(1, resolvedShorthand.size());
        Assert.assertEquals(CommonCssConstants.GRID_TEMPLATE_AREAS, resolvedShorthand.get(0).getProperty());
        Assert.assertEquals("'a a a' 'b b b'", resolvedShorthand.get(0).getExpression());
    }

    @Test
    public void areaWithRowsTest() {
        IShorthandResolver resolver = new GridTemplateShorthandResolver();

        String shorthand = "    'a a a' 20%    'b b b' auto";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assert.assertEquals(2, resolvedShorthand.size());
        Assert.assertEquals(CommonCssConstants.GRID_TEMPLATE_ROWS, resolvedShorthand.get(0).getProperty());
        Assert.assertEquals(CommonCssConstants.GRID_TEMPLATE_AREAS, resolvedShorthand.get(1).getProperty());
        Assert.assertEquals("20% auto", resolvedShorthand.get(0).getExpression());
        Assert.assertEquals("'a a a' 'b b b'", resolvedShorthand.get(1).getExpression());
    }

    @Test
    public void areaWithRowsAndColumnsTest() {
        IShorthandResolver resolver = new GridTemplateShorthandResolver();

        String shorthand = "    'a a a' 20%    'b b b' auto / auto 1fr auto";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assert.assertEquals(3, resolvedShorthand.size());
        Assert.assertEquals(CommonCssConstants.GRID_TEMPLATE_ROWS, resolvedShorthand.get(0).getProperty());
        Assert.assertEquals(CommonCssConstants.GRID_TEMPLATE_COLUMNS, resolvedShorthand.get(1).getProperty());
        Assert.assertEquals(CommonCssConstants.GRID_TEMPLATE_AREAS, resolvedShorthand.get(2).getProperty());
        Assert.assertEquals("20% auto", resolvedShorthand.get(0).getExpression());
        Assert.assertEquals("auto 1fr auto", resolvedShorthand.get(1).getExpression());
        Assert.assertEquals("'a a a' 'b b b'", resolvedShorthand.get(2).getExpression());
    }

    @Test
    public void areaWithMissingRowAtTheEndTest() {
        IShorthandResolver resolver = new GridTemplateShorthandResolver();

        String shorthand = "    'a a a' 20% 'b b b' 1fr 'c c c' / auto 1fr auto";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assert.assertEquals(3, resolvedShorthand.size());
        Assert.assertEquals(CommonCssConstants.GRID_TEMPLATE_ROWS, resolvedShorthand.get(0).getProperty());
        Assert.assertEquals(CommonCssConstants.GRID_TEMPLATE_COLUMNS, resolvedShorthand.get(1).getProperty());
        Assert.assertEquals(CommonCssConstants.GRID_TEMPLATE_AREAS, resolvedShorthand.get(2).getProperty());
        Assert.assertEquals("20% 1fr auto", resolvedShorthand.get(0).getExpression());
        Assert.assertEquals("auto 1fr auto", resolvedShorthand.get(1).getExpression());
        Assert.assertEquals("'a a a' 'b b b' 'c c c'", resolvedShorthand.get(2).getExpression());
    }

    @Test
    public void areaWithMissingRowAtTheStartTest() {
        IShorthandResolver resolver = new GridTemplateShorthandResolver();

        String shorthand = "    'a a a' 'b b b' 1fr 'c c c' 80% / auto 1fr auto";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assert.assertEquals(3, resolvedShorthand.size());
        Assert.assertEquals(CommonCssConstants.GRID_TEMPLATE_ROWS, resolvedShorthand.get(0).getProperty());
        Assert.assertEquals(CommonCssConstants.GRID_TEMPLATE_COLUMNS, resolvedShorthand.get(1).getProperty());
        Assert.assertEquals(CommonCssConstants.GRID_TEMPLATE_AREAS, resolvedShorthand.get(2).getProperty());
        Assert.assertEquals("auto 1fr 80%", resolvedShorthand.get(0).getExpression());
        Assert.assertEquals("auto 1fr auto", resolvedShorthand.get(1).getExpression());
        Assert.assertEquals("'a a a' 'b b b' 'c c c'", resolvedShorthand.get(2).getExpression());
    }

    @Test
    public void complexAreaTest() {
        IShorthandResolver resolver = new GridTemplateShorthandResolver();

        String shorthand = "[header-top] 'a a a' [header-bottom] [main-top] 'b b b' 1fr [main-bottom] / auto 1fr auto";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assert.assertEquals(3, resolvedShorthand.size());
        Assert.assertEquals(CommonCssConstants.GRID_TEMPLATE_ROWS, resolvedShorthand.get(0).getProperty());
        Assert.assertEquals(CommonCssConstants.GRID_TEMPLATE_COLUMNS, resolvedShorthand.get(1).getProperty());
        Assert.assertEquals(CommonCssConstants.GRID_TEMPLATE_AREAS, resolvedShorthand.get(2).getProperty());
        Assert.assertEquals("[header-top] [header-bottom] [main-top] 1fr [main-bottom]", resolvedShorthand.get(0).getExpression());
        Assert.assertEquals("auto 1fr auto", resolvedShorthand.get(1).getExpression());
        Assert.assertEquals("'a a a' 'b b b'", resolvedShorthand.get(2).getExpression());
    }

    @Test
    public void complexAreaWithoutLineNamesTest() {
        IShorthandResolver resolver = new GridTemplateShorthandResolver();

        String shorthand = "'head head' 30px 'nav  main' 1fr 'nav  foot' 30px / 120px 1fr";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assert.assertEquals(3, resolvedShorthand.size());
        Assert.assertEquals(CommonCssConstants.GRID_TEMPLATE_ROWS, resolvedShorthand.get(0).getProperty());
        Assert.assertEquals(CommonCssConstants.GRID_TEMPLATE_COLUMNS, resolvedShorthand.get(1).getProperty());
        Assert.assertEquals(CommonCssConstants.GRID_TEMPLATE_AREAS, resolvedShorthand.get(2).getProperty());
        Assert.assertEquals("30px 1fr 30px", resolvedShorthand.get(0).getExpression());
        Assert.assertEquals("120px 1fr", resolvedShorthand.get(1).getExpression());
        Assert.assertEquals("'head head' 'nav  main' 'nav  foot'", resolvedShorthand.get(2).getExpression());
    }
}
