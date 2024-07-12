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
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.List;

@Category(UnitTest.class)
public class GridShorthandResolverTest extends ExtendedITextTest {
    /**
     * Creates grid shorthand resolver.
     */
    public GridShorthandResolverTest() {
    }

    @Test
    public void templateAreasTest() {
        IShorthandResolver resolver = new GridShorthandResolver();

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
    public void columnFlowTest() {
        IShorthandResolver resolver = new GridShorthandResolver();

        String shorthand = "20% 100px 1fr / auto-flow dense 50px";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assert.assertEquals(3, resolvedShorthand.size());
        Assert.assertEquals(CommonCssConstants.GRID_TEMPLATE_ROWS, resolvedShorthand.get(0).getProperty());
        Assert.assertEquals(CommonCssConstants.GRID_AUTO_FLOW, resolvedShorthand.get(1).getProperty());
        Assert.assertEquals(CommonCssConstants.GRID_AUTO_COLUMNS, resolvedShorthand.get(2).getProperty());
        Assert.assertEquals("20% 100px 1fr", resolvedShorthand.get(0).getExpression());
        Assert.assertEquals("column dense", resolvedShorthand.get(1).getExpression());
        Assert.assertEquals("50px", resolvedShorthand.get(2).getExpression());
    }

    @Test
    public void rowFlowTest() {
        IShorthandResolver resolver = new GridShorthandResolver();

        String shorthand = "auto-flow dense auto / 1fr auto minmax(100px, 1fr)";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assert.assertEquals(3, resolvedShorthand.size());
        Assert.assertEquals(CommonCssConstants.GRID_AUTO_FLOW, resolvedShorthand.get(0).getProperty());
        Assert.assertEquals(CommonCssConstants.GRID_AUTO_ROWS, resolvedShorthand.get(1).getProperty());
        Assert.assertEquals(CommonCssConstants.GRID_TEMPLATE_COLUMNS, resolvedShorthand.get(2).getProperty());
        Assert.assertEquals("dense", resolvedShorthand.get(0).getExpression());
        Assert.assertEquals("auto", resolvedShorthand.get(1).getExpression());
        Assert.assertEquals("1fr auto minmax(100px,1fr)", resolvedShorthand.get(2).getExpression());
    }

    @Test
    public void noRowTemplateTest() {
        IShorthandResolver resolver = new GridShorthandResolver();

        String shorthand = "auto-flow dense / 1fr auto minmax(100px, 1fr)";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assert.assertEquals(2, resolvedShorthand.size());
        Assert.assertEquals(CommonCssConstants.GRID_AUTO_FLOW, resolvedShorthand.get(0).getProperty());
        Assert.assertEquals(CommonCssConstants.GRID_TEMPLATE_COLUMNS, resolvedShorthand.get(1).getProperty());
        Assert.assertEquals("dense", resolvedShorthand.get(0).getExpression());
        Assert.assertEquals("1fr auto minmax(100px,1fr)", resolvedShorthand.get(1).getExpression());
    }

    @Test
    public void noColumnTemplateTest() {
        IShorthandResolver resolver = new GridShorthandResolver();

        String shorthand = "1fr auto minmax(100px, 1fr) / auto-flow dense";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assert.assertEquals(2, resolvedShorthand.size());
        Assert.assertEquals(CommonCssConstants.GRID_TEMPLATE_ROWS, resolvedShorthand.get(0).getProperty());
        Assert.assertEquals(CommonCssConstants.GRID_AUTO_FLOW, resolvedShorthand.get(1).getProperty());
        Assert.assertEquals("1fr auto minmax(100px,1fr)", resolvedShorthand.get(0).getExpression());
        Assert.assertEquals("column dense", resolvedShorthand.get(1).getExpression());
    }
}
