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
package com.itextpdf.styledxmlparser.css.resolve.shorthand;

import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.styledxmlparser.css.CssDeclaration;
import com.itextpdf.styledxmlparser.css.resolve.shorthand.impl.ColumnsShorthandResolver;
import com.itextpdf.styledxmlparser.logs.StyledXmlParserLogMessageConstant;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class ColumnsShorthandResolverTest extends ExtendedITextTest {
    @Test
    public void initialOrInheritOrUnsetValuesTest() {
        IShorthandResolver resolver = new ColumnsShorthandResolver();

        String initialShorthand = CommonCssConstants.INITIAL;
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(initialShorthand);
        Assertions.assertEquals(2, resolvedShorthand.size());
        Assertions.assertEquals(CommonCssConstants.COLUMN_COUNT, resolvedShorthand.get(0).getProperty());
        Assertions.assertEquals(CommonCssConstants.INITIAL, resolvedShorthand.get(0).getExpression());
        Assertions.assertEquals(CommonCssConstants.COLUMN_WIDTH, resolvedShorthand.get(1).getProperty());
        Assertions.assertEquals(CommonCssConstants.INITIAL, resolvedShorthand.get(1).getExpression());

        String inheritShorthand = CommonCssConstants.INHERIT;
        resolvedShorthand = resolver.resolveShorthand(inheritShorthand);
        Assertions.assertEquals(2, resolvedShorthand.size());
        Assertions.assertEquals(CommonCssConstants.COLUMN_COUNT, resolvedShorthand.get(0).getProperty());
        Assertions.assertEquals(CommonCssConstants.INHERIT, resolvedShorthand.get(0).getExpression());
        Assertions.assertEquals(CommonCssConstants.COLUMN_WIDTH, resolvedShorthand.get(1).getProperty());
        Assertions.assertEquals(CommonCssConstants.INHERIT, resolvedShorthand.get(1).getExpression());

        String unsetShorthand = CommonCssConstants.UNSET;
        resolvedShorthand = resolver.resolveShorthand(unsetShorthand);
        Assertions.assertEquals(2, resolvedShorthand.size());
        Assertions.assertEquals(CommonCssConstants.COLUMN_COUNT, resolvedShorthand.get(0).getProperty());
        Assertions.assertEquals(CommonCssConstants.UNSET, resolvedShorthand.get(0).getExpression());
        Assertions.assertEquals(CommonCssConstants.COLUMN_WIDTH, resolvedShorthand.get(1).getProperty());
        Assertions.assertEquals(CommonCssConstants.UNSET, resolvedShorthand.get(1).getExpression());
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.SHORTHAND_PROPERTY_CANNOT_BE_EMPTY, count = 2))
    public void emptyShorthandTest() {
        IShorthandResolver resolver = new ColumnsShorthandResolver();
        String emptyShorthand = "";
        Assertions.assertEquals(Collections.<CssDeclaration>emptyList(), resolver.resolveShorthand(emptyShorthand));

        String shorthandWithSpaces = "    ";
        Assertions.assertEquals(Collections.<CssDeclaration>emptyList(), resolver.resolveShorthand(shorthandWithSpaces));
    }

    @Test
    public void columnsWithOneAbsoluteValueTest() {
        IShorthandResolver resolver = new ColumnsShorthandResolver();

        String shorthand = "10px";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assertions.assertEquals(1, resolvedShorthand.size());
        Assertions.assertEquals(CommonCssConstants.COLUMN_WIDTH, resolvedShorthand.get(0).getProperty());
        Assertions.assertEquals("10px", resolvedShorthand.get(0).getExpression());
    }

    @Test
    public void columnWithOneMetricValueTest() {
        IShorthandResolver resolver = new ColumnsShorthandResolver();

        String shorthand = "10px";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assertions.assertEquals(1, resolvedShorthand.size());
        Assertions.assertEquals(CommonCssConstants.COLUMN_WIDTH, resolvedShorthand.get(0).getProperty());
        Assertions.assertEquals("10px", resolvedShorthand.get(0).getExpression());
    }

    @Test
    public void columnWithOneRelativeValueTest() {
        IShorthandResolver resolver = new ColumnsShorthandResolver();

        String shorthand = "10em";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assertions.assertEquals(1, resolvedShorthand.size());
        Assertions.assertEquals(CommonCssConstants.COLUMN_WIDTH, resolvedShorthand.get(0).getProperty());
        Assertions.assertEquals("10em", resolvedShorthand.get(0).getExpression());
    }

    @Test
    public void columnWithColumnCountTest() {
        IShorthandResolver resolver = new ColumnsShorthandResolver();

        String shorthand = "3";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assertions.assertEquals(1, resolvedShorthand.size());
        Assertions.assertEquals(CommonCssConstants.COLUMN_COUNT, resolvedShorthand.get(0).getProperty());
        Assertions.assertEquals("3", resolvedShorthand.get(0).getExpression());
    }

    @Test
    public void columnWithAutoValuesTest() {
        IShorthandResolver resolver = new ColumnsShorthandResolver();

        String shorthand = "auto auto";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assertions.assertTrue(resolvedShorthand.isEmpty());
    }

    @Test
    public void columnWithAutoAndRelativeValueTest() {
        IShorthandResolver resolver = new ColumnsShorthandResolver();

        String shorthand = "3em auto";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assertions.assertEquals(1, resolvedShorthand.size());
        Assertions.assertEquals(CommonCssConstants.COLUMN_WIDTH, resolvedShorthand.get(0).getProperty());
        Assertions.assertEquals("3em", resolvedShorthand.get(0).getExpression());
    }

    @Test
    public void columnWithRelativeAndAutoValueTest() {
        IShorthandResolver resolver = new ColumnsShorthandResolver();

        String shorthand = "auto 3em";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assertions.assertEquals(1, resolvedShorthand.size());
        Assertions.assertEquals(CommonCssConstants.COLUMN_WIDTH, resolvedShorthand.get(0).getProperty());
        Assertions.assertEquals("3em", resolvedShorthand.get(0).getExpression());
    }

    @Test
    public void columnWithRelativeAndCountValueTest() {
        IShorthandResolver resolver = new ColumnsShorthandResolver();

        String shorthand = "12 3em";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assertions.assertEquals(2, resolvedShorthand.size());
        Assertions.assertEquals(CommonCssConstants.COLUMN_COUNT, resolvedShorthand.get(0).getProperty());
        Assertions.assertEquals("12", resolvedShorthand.get(0).getExpression());
        Assertions.assertEquals(CommonCssConstants.COLUMN_WIDTH, resolvedShorthand.get(1).getProperty());
        Assertions.assertEquals("3em", resolvedShorthand.get(1).getExpression());
    }

}
