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
import com.itextpdf.test.ExtendedITextTest;

import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Tag("UnitTest")
public class CssShorthandResolverTest extends ExtendedITextTest {

    @Test
    public void borderBottomTest01() {
        String shorthandExpression = "15px dotted blue";
        Set<String> expectedResolvedProperties = new HashSet<>(Arrays.asList(
                "border-bottom-width: 15px",
                "border-bottom-style: dotted",
                "border-bottom-color: blue"
        ));

        IShorthandResolver resolver = ShorthandResolverFactory.getShorthandResolver(CommonCssConstants.BORDER_BOTTOM);
        assertNotNull(resolver);
        List<CssDeclaration> resolvedShorthandProps = resolver.resolveShorthand(shorthandExpression);
        compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    public void borderLeftTest01() {
        String shorthandExpression = "10px solid #ff0000";
        Set<String> expectedResolvedProperties = new HashSet<>(Arrays.asList(
                "border-left-width: 10px",
                "border-left-style: solid",
                "border-left-color: #ff0000"
        ));

        IShorthandResolver resolver = ShorthandResolverFactory.getShorthandResolver(CommonCssConstants.BORDER_LEFT);
        assertNotNull(resolver);
        List<CssDeclaration> resolvedShorthandProps = resolver.resolveShorthand(shorthandExpression);
        compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    public void borderRightTest01() {
        String shorthandExpression = "10px double rgb(12,220,100)";
        Set<String> expectedResolvedProperties = new HashSet<>(Arrays.asList(
                "border-right-width: 10px",
                "border-right-style: double",
                "border-right-color: rgb(12,220,100)"
        ));

        IShorthandResolver resolver = ShorthandResolverFactory.getShorthandResolver(CommonCssConstants.BORDER_RIGHT);
        assertNotNull(resolver);
        List<CssDeclaration> resolvedShorthandProps = resolver.resolveShorthand(shorthandExpression);
        compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    public void borderTopTest01() {
        String shorthandExpression = "10px hidden rgba(12,225,100,0.7)";
        Set<String> expectedResolvedProperties = new HashSet<>(Arrays.asList(
                "border-top-width: 10px",
                "border-top-style: hidden",
                "border-top-color: rgba(12,225,100,0.7)"
        ));

        IShorthandResolver resolver = ShorthandResolverFactory.getShorthandResolver(CommonCssConstants.BORDER_TOP);
        assertNotNull(resolver);
        List<CssDeclaration> resolvedShorthandProps = resolver.resolveShorthand(shorthandExpression);
        compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    public void borderTest01() {
        String shorthandExpression = "thick groove black";
        Set<String> expectedResolvedProperties = new HashSet<>(Arrays.asList(
                "border-top-width: thick",
                "border-right-width: thick",
                "border-bottom-width: thick",
                "border-left-width: thick",
                "border-top-style: groove",
                "border-right-style: groove",
                "border-bottom-style: groove",
                "border-left-style: groove",
                "border-top-color: black",
                "border-right-color: black",
                "border-bottom-color: black",
                "border-left-color: black"
        ));

        IShorthandResolver resolver = ShorthandResolverFactory.getShorthandResolver(CommonCssConstants.BORDER);
        assertNotNull(resolver);
        List<CssDeclaration> resolvedShorthandProps = resolver.resolveShorthand(shorthandExpression);
        compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    public void borderTest02() {
        String shorthandExpression = "groove";
        Set<String> expectedResolvedProperties = new HashSet<>(Arrays.asList(
                "border-top-width: initial",
                "border-right-width: initial",
                "border-bottom-width: initial",
                "border-left-width: initial",
                "border-top-style: groove",
                "border-right-style: groove",
                "border-bottom-style: groove",
                "border-left-style: groove",
                "border-bottom-color: initial",
                "border-left-color: initial",
                "border-right-color: initial",
                "border-top-color: initial"
        ));

        IShorthandResolver resolver = ShorthandResolverFactory.getShorthandResolver(CommonCssConstants.BORDER);
        assertNotNull(resolver);
        List<CssDeclaration> resolvedShorthandProps = resolver.resolveShorthand(shorthandExpression);
        compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    public void borderTest03() {
        String shorthandExpression = "inherit";
        Set<String> expectedResolvedProperties = new HashSet<>(Arrays.asList(
                "border-top-width: inherit",
                "border-right-width: inherit",
                "border-bottom-width: inherit",
                "border-left-width: inherit",
                "border-top-style: inherit",
                "border-right-style: inherit",
                "border-bottom-style: inherit",
                "border-left-style: inherit",
                "border-top-color: inherit",
                "border-right-color: inherit",
                "border-bottom-color: inherit",
                "border-left-color: inherit"
        ));

        IShorthandResolver resolver = ShorthandResolverFactory.getShorthandResolver(CommonCssConstants.BORDER);
        assertNotNull(resolver);
        List<CssDeclaration> resolvedShorthandProps = resolver.resolveShorthand(shorthandExpression);
        compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    public void borderTest04() {
        String shorthandExpression = "dashed";
        Set<String> expectedResolvedProperties = new HashSet<>(Arrays.asList(
                "border-top-width: initial",
                "border-right-width: initial",
                "border-bottom-width: initial",
                "border-left-width: initial",
                "border-top-style: dashed",
                "border-right-style: dashed",
                "border-bottom-style: dashed",
                "border-left-style: dashed",
                "border-bottom-color: initial",
                "border-left-color: initial",
                "border-right-color: initial",
                "border-top-color: initial"
        ));

        IShorthandResolver resolver = ShorthandResolverFactory.getShorthandResolver(CommonCssConstants.BORDER);
        assertNotNull(resolver);
        List<CssDeclaration> resolvedShorthandProps = resolver.resolveShorthand(shorthandExpression);
        compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    public void borderTest05() {
        String shorthandExpression = "dashed green";
        Set<String> expectedResolvedProperties = new HashSet<>(Arrays.asList(
                "border-top-width: initial",
                "border-right-width: initial",
                "border-bottom-width: initial",
                "border-left-width: initial",
                "border-top-style: dashed",
                "border-right-style: dashed",
                "border-bottom-style: dashed",
                "border-left-style: dashed",
                "border-top-color: green",
                "border-right-color: green",
                "border-bottom-color: green",
                "border-left-color: green"
        ));

        IShorthandResolver resolver = ShorthandResolverFactory.getShorthandResolver(CommonCssConstants.BORDER);
        assertNotNull(resolver);
        List<CssDeclaration> resolvedShorthandProps = resolver.resolveShorthand(shorthandExpression);
        compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    public void borderTest06() {
        String shorthandExpression = "1px dashed";
        Set<String> expectedResolvedProperties = new HashSet<>(Arrays.asList(
                "border-top-width: 1px",
                "border-right-width: 1px",
                "border-bottom-width: 1px",
                "border-left-width: 1px",
                "border-top-style: dashed",
                "border-right-style: dashed",
                "border-bottom-style: dashed",
                "border-left-style: dashed",
                "border-bottom-color: initial",
                "border-left-color: initial",
                "border-right-color: initial",
                "border-top-color: initial"
        ));

        IShorthandResolver resolver = ShorthandResolverFactory.getShorthandResolver(CommonCssConstants.BORDER);
        assertNotNull(resolver);
        List<CssDeclaration> resolvedShorthandProps = resolver.resolveShorthand(shorthandExpression);
        compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    public void borderTest07() {
        String shorthandExpression = "1px dashed green";
        Set<String> expectedResolvedProperties = new HashSet<>(Arrays.asList(
                "border-top-width: 1px",
                "border-right-width: 1px",
                "border-bottom-width: 1px",
                "border-left-width: 1px",
                "border-top-style: dashed",
                "border-right-style: dashed",
                "border-bottom-style: dashed",
                "border-left-style: dashed",
                "border-top-color: green",
                "border-right-color: green",
                "border-bottom-color: green",
                "border-left-color: green"
        ));

        IShorthandResolver resolver = ShorthandResolverFactory.getShorthandResolver(CommonCssConstants.BORDER);
        assertNotNull(resolver);
        List<CssDeclaration> resolvedShorthandProps = resolver.resolveShorthand(shorthandExpression);
        compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    public void borderWidthTest01() {
        String shorthandExpression = "thin medium thick 10px";
        Set<String> expectedResolvedProperties = new HashSet<>(Arrays.asList(
                "border-top-width: thin",
                "border-right-width: medium",
                "border-bottom-width: thick",
                "border-left-width: 10px"
        ));

        IShorthandResolver resolver = ShorthandResolverFactory.getShorthandResolver(CommonCssConstants.BORDER_WIDTH);
        assertNotNull(resolver);
        List<CssDeclaration> resolvedShorthandProps = resolver.resolveShorthand(shorthandExpression);
        compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    public void borderWidthTest02() {
        String shorthandExpression = "thin 20% thick";
        Set<String> expectedResolvedProperties = new HashSet<>(Arrays.asList(
                "border-top-width: thin",
                "border-right-width: 20%",
                "border-bottom-width: thick",
                "border-left-width: 20%"
        ));

        IShorthandResolver resolver = ShorthandResolverFactory.getShorthandResolver(CommonCssConstants.BORDER_WIDTH);
        assertNotNull(resolver);
        List<CssDeclaration> resolvedShorthandProps = resolver.resolveShorthand(shorthandExpression);
        compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    public void borderWidthTest03() {
        String shorthandExpression = "inherit";
        Set<String> expectedResolvedProperties = new HashSet<>(Arrays.asList(
                "border-top-width: inherit",
                "border-right-width: inherit",
                "border-bottom-width: inherit",
                "border-left-width: inherit"
        ));

        IShorthandResolver resolver = ShorthandResolverFactory.getShorthandResolver(CommonCssConstants.BORDER_WIDTH);
        assertNotNull(resolver);
        List<CssDeclaration> resolvedShorthandProps = resolver.resolveShorthand(shorthandExpression);
        compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    public void borderStyleTest01() {
        String shorthandExpression = "dotted solid double dashed";
        Set<String> expectedResolvedProperties = new HashSet<>(Arrays.asList(
                "border-top-style: dotted",
                "border-right-style: solid",
                "border-bottom-style: double",
                "border-left-style: dashed"
        ));

        IShorthandResolver resolver = ShorthandResolverFactory.getShorthandResolver(CommonCssConstants.BORDER_STYLE);
        assertNotNull(resolver);
        List<CssDeclaration> resolvedShorthandProps = resolver.resolveShorthand(shorthandExpression);
        compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    public void borderStyleTest02() {
        String shorthandExpression = "dotted solid";
        Set<String> expectedResolvedProperties = new HashSet<>(Arrays.asList(
                "border-top-style: dotted",
                "border-right-style: solid",
                "border-bottom-style: dotted",
                "border-left-style: solid"
        ));

        IShorthandResolver resolver = ShorthandResolverFactory.getShorthandResolver(CommonCssConstants.BORDER_STYLE);
        assertNotNull(resolver);
        List<CssDeclaration> resolvedShorthandProps = resolver.resolveShorthand(shorthandExpression);
        compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    public void borderColorTest01() {
        String shorthandExpression = "red rgba(125,0,50,0.4) rgb(12,255,0) #0000ff";
        Set<String> expectedResolvedProperties = new HashSet<>(Arrays.asList(
                "border-top-color: red",
                "border-right-color: rgba(125,0,50,0.4)",
                "border-bottom-color: rgb(12,255,0)",
                "border-left-color: #0000ff"
        ));

        IShorthandResolver resolver = ShorthandResolverFactory.getShorthandResolver(CommonCssConstants.BORDER_COLOR);
        assertNotNull(resolver);
        List<CssDeclaration> resolvedShorthandProps = resolver.resolveShorthand(shorthandExpression);
        compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    public void borderColorTest02() {
        String shorthandExpression = "red";
        Set<String> expectedResolvedProperties = new HashSet<>(Arrays.asList(
                "border-top-color: red",
                "border-right-color: red",
                "border-bottom-color: red",
                "border-left-color: red"
        ));

        IShorthandResolver resolver = ShorthandResolverFactory.getShorthandResolver(CommonCssConstants.BORDER_COLOR);
        assertNotNull(resolver);
        List<CssDeclaration> resolvedShorthandProps = resolver.resolveShorthand(shorthandExpression);
        compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    public void listStyleTest01() {
        String shorthandExpression = "square inside url('sqpurple.gif')";
        Set<String> expectedResolvedProperties = new HashSet<>(Arrays.asList(
                "list-style-type: square",
                "list-style-position: inside",
                "list-style-image: url('sqpurple.gif')"
        ));

        IShorthandResolver resolver = ShorthandResolverFactory.getShorthandResolver(CommonCssConstants.LIST_STYLE);
        assertNotNull(resolver);
        List<CssDeclaration> resolvedShorthandProps = resolver.resolveShorthand(shorthandExpression);
        compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    public void listStyleTest02() {
        String shorthandExpression = "inside url('sqpurple.gif')";
        Set<String> expectedResolvedProperties = new HashSet<>(Arrays.asList(
                "list-style-type: initial",
                "list-style-position: inside",
                "list-style-image: url('sqpurple.gif')"
        ));

        IShorthandResolver resolver = ShorthandResolverFactory.getShorthandResolver(CommonCssConstants.LIST_STYLE);
        assertNotNull(resolver);
        List<CssDeclaration> resolvedShorthandProps = resolver.resolveShorthand(shorthandExpression);
        compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    public void listStyleTest03() {
        String shorthandExpression = "inherit";
        Set<String> expectedResolvedProperties = new HashSet<>(Arrays.asList(
                "list-style-type: inherit",
                "list-style-position: inherit",
                "list-style-image: inherit"
        ));

        IShorthandResolver resolver = ShorthandResolverFactory.getShorthandResolver(CommonCssConstants.LIST_STYLE);
        assertNotNull(resolver);
        List<CssDeclaration> resolvedShorthandProps = resolver.resolveShorthand(shorthandExpression);
        compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    public void marginTest01() {
        String shorthandExpression = "2cm -4cm 3cm 4cm";
        Set<String> expectedResolvedProperties = new HashSet<>(Arrays.asList(
                "margin-top: 2cm",
                "margin-right: -4cm",
                "margin-bottom: 3cm",
                "margin-left: 4cm"
        ));

        IShorthandResolver resolver = ShorthandResolverFactory.getShorthandResolver(CommonCssConstants.MARGIN);
        assertNotNull(resolver);
        List<CssDeclaration> resolvedShorthandProps = resolver.resolveShorthand(shorthandExpression);
        compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    public void marginTest02() {
        String shorthandExpression = "2cm auto 4cm";
        Set<String> expectedResolvedProperties = new HashSet<>(Arrays.asList(
                "margin-top: 2cm",
                "margin-right: auto",
                "margin-bottom: 4cm",
                "margin-left: auto"
        ));

        IShorthandResolver resolver = ShorthandResolverFactory.getShorthandResolver(CommonCssConstants.MARGIN);
        assertNotNull(resolver);
        List<CssDeclaration> resolvedShorthandProps = resolver.resolveShorthand(shorthandExpression);
        compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    public void outlineTest01() {
        String shorthandExpression = "#00ff00 dashed medium";
        Set<String> expectedResolvedProperties = new HashSet<>(Arrays.asList(
                "outline-color: #00ff00",
                "outline-style: dashed",
                "outline-width: medium"
        ));

        IShorthandResolver resolver = ShorthandResolverFactory.getShorthandResolver(CommonCssConstants.OUTLINE);
        assertNotNull(resolver);
        List<CssDeclaration> resolvedShorthandProps = resolver.resolveShorthand(shorthandExpression);
        compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    public void paddingTest01() {
        String shorthandExpression = "10px 5px 15px 20px";
        Set<String> expectedResolvedProperties = new HashSet<>(Arrays.asList(
                "padding-top: 10px",
                "padding-right: 5px",
                "padding-bottom: 15px",
                "padding-left: 20px"
        ));

        IShorthandResolver resolver = ShorthandResolverFactory.getShorthandResolver(CommonCssConstants.PADDING);
        assertNotNull(resolver);
        List<CssDeclaration> resolvedShorthandProps = resolver.resolveShorthand(shorthandExpression);
        compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    public void paddingTest02() {
        String shorthandExpression = "10px 5px";
        Set<String> expectedResolvedProperties = new HashSet<>(Arrays.asList(
                "padding-top: 10px",
                "padding-right: 5px",
                "padding-bottom: 10px",
                "padding-left: 5px"
        ));

        IShorthandResolver resolver = ShorthandResolverFactory.getShorthandResolver(CommonCssConstants.PADDING);
        assertNotNull(resolver);
        List<CssDeclaration> resolvedShorthandProps = resolver.resolveShorthand(shorthandExpression);
        compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    public void paddingTest03() {
        String shorthandExpression = "inherit";
        Set<String> expectedResolvedProperties = new HashSet<>(Arrays.asList(
                "padding-top: inherit",
                "padding-right: inherit",
                "padding-bottom: inherit",
                "padding-left: inherit"
        ));

        IShorthandResolver resolver = ShorthandResolverFactory.getShorthandResolver(CommonCssConstants.PADDING);
        assertNotNull(resolver);
        List<CssDeclaration> resolvedShorthandProps = resolver.resolveShorthand(shorthandExpression);
        compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    public void linearGradientInlistStyleImageTest() {
        String shorthandExpression = "inside linear-gradient(red, green, blue)";
        Set<String> expectedResolvedProperties = new HashSet<>(Arrays.asList(
                "list-style-type: initial",
                "list-style-position: inside",
                "list-style-image: linear-gradient(red,green,blue)"
        ));

        IShorthandResolver resolver = ShorthandResolverFactory.getShorthandResolver(com.itextpdf.styledxmlparser.css.CommonCssConstants.LIST_STYLE);
        Assertions.assertNotNull(resolver);
        List<CssDeclaration> resolvedShorthandProps = resolver.resolveShorthand(shorthandExpression);
        compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    public void repeatingLinearGradientInlistStyleImageTest() {
        String shorthandExpression = "square inside repeating-linear-gradient(45deg, blue 7%, red 10%)";
        Set<String> expectedResolvedProperties = new HashSet<>(Arrays.asList(
                "list-style-type: square",
                "list-style-position: inside",
                "list-style-image: repeating-linear-gradient(45deg,blue 7%,red 10%)"
        ));

        IShorthandResolver resolver = ShorthandResolverFactory.getShorthandResolver(com.itextpdf.styledxmlparser.css.CommonCssConstants.LIST_STYLE);
        Assertions.assertNotNull(resolver);
        List<CssDeclaration> resolvedShorthandProps = resolver.resolveShorthand(shorthandExpression);
        compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    public void noneInlistStyleImageTest() {
        String shorthandExpression = "circle none inside";
        Set<String> expectedResolvedProperties = new HashSet<>(Arrays.asList(
                "list-style-type: circle",
                "list-style-position: inside",
                "list-style-image: none"
        ));

        IShorthandResolver resolver = ShorthandResolverFactory.getShorthandResolver(com.itextpdf.styledxmlparser.css.CommonCssConstants.LIST_STYLE);
        Assertions.assertNotNull(resolver);
        List<CssDeclaration> resolvedShorthandProps = resolver.resolveShorthand(shorthandExpression);
        compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    public void fontTest01() {
        String shorthandExpression = "italic normal bold 12px/30px Georgia, serif";
        Set<String> expectedResolvedProperties = new HashSet<>(Arrays.asList(
                "font-style: italic",
                "font-variant: initial",
                "font-weight: bold",
                "font-size: 12px",
                "line-height: 30px",
                "font-family: georgia,serif"
        ));

        IShorthandResolver resolver = ShorthandResolverFactory.getShorthandResolver(com.itextpdf.styledxmlparser.css.CommonCssConstants.FONT);
        Assertions.assertNotNull(resolver);
        List<CssDeclaration> resolvedShorthandProps = resolver.resolveShorthand(shorthandExpression);
        compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    public void fontTest02() {
        String shorthandExpression = "bold Georgia, serif";
        Set<String> expectedResolvedProperties = new HashSet<>(Arrays.asList(
                "font-style: initial",
                "font-variant: initial",
                "font-weight: bold",
                "font-size: initial",
                "line-height: initial",
                "font-family: georgia,serif"
        ));

        IShorthandResolver resolver = ShorthandResolverFactory.getShorthandResolver(com.itextpdf.styledxmlparser.css.CommonCssConstants.FONT);
        Assertions.assertNotNull(resolver);
        List<CssDeclaration> resolvedShorthandProps = resolver.resolveShorthand(shorthandExpression);
        compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    public void fontTest03() {
        String shorthandExpression = "inherit";
        Set<String> expectedResolvedProperties = new HashSet<>(Arrays.asList(
                "font-style: inherit",
                "font-variant: inherit",
                "font-weight: inherit",
                "font-size: inherit",
                "line-height: inherit",
                "font-family: inherit"
        ));

        IShorthandResolver resolver = ShorthandResolverFactory.getShorthandResolver(com.itextpdf.styledxmlparser.css.CommonCssConstants.FONT);
        Assertions.assertNotNull(resolver);
        List<CssDeclaration> resolvedShorthandProps = resolver.resolveShorthand(shorthandExpression);
        compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    public void fontTest04() {
        String shorthandExpression = "bold Georgia, serif, \"Times New Roman\"";
        Set<String> expectedResolvedProperties = new HashSet<>(Arrays.asList(
                "font-style: initial",
                "font-variant: initial",
                "font-weight: bold",
                "font-size: initial",
                "line-height: initial",
                "font-family: georgia,serif,\"Times New Roman\""
        ));

        IShorthandResolver resolver = ShorthandResolverFactory.getShorthandResolver(com.itextpdf.styledxmlparser.css.CommonCssConstants.FONT);
        Assertions.assertNotNull(resolver);
        List<CssDeclaration> resolvedShorthandProps = resolver.resolveShorthand(shorthandExpression);
        compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    public void fontTest05() {
        String shorthandExpression = "italic normal bold 12px/30px Georgia, \"Times New Roman\", serif";
        Set<String> expectedResolvedProperties = new HashSet<>(Arrays.asList(
                "font-style: italic",
                "font-variant: initial",
                "font-weight: bold",
                "font-size: 12px",
                "line-height: 30px",
                "font-family: georgia,\"Times New Roman\",serif"
        ));

        IShorthandResolver resolver = ShorthandResolverFactory.getShorthandResolver(com.itextpdf.styledxmlparser.css.CommonCssConstants.FONT);
        Assertions.assertNotNull(resolver);
        List<CssDeclaration> resolvedShorthandProps = resolver.resolveShorthand(shorthandExpression);
        compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    public void fontTest06() {
        String shorthandExpression = "italic normal bold 12px/30px Georgia    ,   \"Times New Roman\"   ,    serif";
        Set<String> expectedResolvedProperties = new HashSet<>(Arrays.asList(
                "font-style: italic",
                "font-variant: initial",
                "font-weight: bold",
                "font-size: 12px",
                "line-height: 30px",
                "font-family: georgia,\"Times New Roman\",serif"
        ));

        IShorthandResolver resolver = ShorthandResolverFactory.getShorthandResolver(com.itextpdf.styledxmlparser.css.CommonCssConstants.FONT);
        Assertions.assertNotNull(resolver);
        List<CssDeclaration> resolvedShorthandProps = resolver.resolveShorthand(shorthandExpression);
        compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    public void fontTest07() {
        String shorthandExpression = "italic normal bold 12px/30px Georgia    ,   \"Times New Roman\"   ";
        Set<String> expectedResolvedProperties = new HashSet<>(Arrays.asList(
                "font-style: italic",
                "font-variant: initial",
                "font-weight: bold",
                "font-size: 12px",
                "line-height: 30px",
                "font-family: georgia,\"Times New Roman\""
        ));

        IShorthandResolver resolver = ShorthandResolverFactory.getShorthandResolver(com.itextpdf.styledxmlparser.css.CommonCssConstants.FONT);
        Assertions.assertNotNull(resolver);
        List<CssDeclaration> resolvedShorthandProps = resolver.resolveShorthand(shorthandExpression);
        compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    public void fontTest08() {
        String shorthandExpression = "Georgia,'Times New Roman'";
        Set<String> expectedResolvedProperties = new HashSet<>(Arrays.asList(
                "font-style: initial",
                "font-variant: initial",
                "font-weight: initial",
                "font-size: initial",
                "line-height: initial",
                "font-family: georgia,'Times New Roman'"
        ));

        IShorthandResolver resolver = ShorthandResolverFactory.getShorthandResolver(com.itextpdf.styledxmlparser.css.CommonCssConstants.FONT);
        Assertions.assertNotNull(resolver);
        List<CssDeclaration> resolvedShorthandProps = resolver.resolveShorthand(shorthandExpression);
        compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    public void fontTest09() {
        String shorthandExpression = "Georgia  ,   'Times New Roman', serif";
        Set<String> expectedResolvedProperties = new HashSet<>(Arrays.asList(
                "font-style: initial",
                "font-variant: initial",
                "font-weight: initial",
                "font-size: initial",
                "line-height: initial",
                "font-family: georgia,'Times New Roman',serif"
        ));

        IShorthandResolver resolver = ShorthandResolverFactory.getShorthandResolver(com.itextpdf.styledxmlparser.css.CommonCssConstants.FONT);
        Assertions.assertNotNull(resolver);
        List<CssDeclaration> resolvedShorthandProps = resolver.resolveShorthand(shorthandExpression);
        compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    static void compareResolvedProps(List<CssDeclaration> actual, Set<String> expected) {
        Set<String> actualSet = new HashSet<>();
        for (CssDeclaration cssDecl : actual) {
            actualSet.add(cssDecl.toString());
        }

        boolean areDifferent = false;

        StringBuilder sb = new StringBuilder("Resolved styles are different from expected!");
        Set<String> expCopy = new TreeSet<>(expected);
        Set<String> actCopy = new TreeSet<>(actualSet);
        expCopy.removeAll(actualSet);
        actCopy.removeAll(expected);
        if (!expCopy.isEmpty()) {
            areDifferent = true;
            sb.append("\nExpected but not found properties:\n");
            for (String expProp : expCopy) {
                sb.append(expProp).append('\n');
            }
        }
        if (!actCopy.isEmpty()) {
            areDifferent = true;
            sb.append("\nNot expected but found properties:\n");
            for (String actProp : actCopy) {
                sb.append(actProp).append('\n');
            }
        }

        if (areDifferent) {
            Assertions.fail(sb.toString());
        }
    }
}
