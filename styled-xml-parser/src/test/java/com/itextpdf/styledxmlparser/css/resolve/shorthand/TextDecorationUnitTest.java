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

import com.itextpdf.styledxmlparser.css.CssDeclaration;
import com.itextpdf.styledxmlparser.css.resolve.shorthand.impl.TextDecorationShorthandResolver;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class TextDecorationUnitTest extends ExtendedITextTest {

    @Test
    public void resolveShorthandLineEmptyTest() {
        TextDecorationShorthandResolver resolver = new TextDecorationShorthandResolver();
        List<CssDeclaration> result = resolver.resolveShorthand("");
        Map<String, String> resultMap = convertCssDeclarationsToMap(result);
        Assert.assertEquals(3, resultMap.size());
        Assert.assertEquals("initial", resultMap.get("text-decoration-line"));
        Assert.assertEquals("initial", resultMap.get("text-decoration-style"));
        Assert.assertEquals("initial", resultMap.get("text-decoration-color"));
    }

    @Test
    public void resolveShorthandLineNoneTest() {
        TextDecorationShorthandResolver resolver = new TextDecorationShorthandResolver();
        List<CssDeclaration> result = resolver.resolveShorthand("none");
        Map<String, String> resultMap = convertCssDeclarationsToMap(result);
        Assert.assertEquals(3, resultMap.size());
        Assert.assertEquals("none", resultMap.get("text-decoration-line"));
        Assert.assertEquals("initial", resultMap.get("text-decoration-style"));
        Assert.assertEquals("initial", resultMap.get("text-decoration-color"));
    }

    @Test
    public void resolveShorthandLineNoneAndUnderlineTogetherTest() {
        TextDecorationShorthandResolver resolver = new TextDecorationShorthandResolver();
        List<CssDeclaration> result = resolver.resolveShorthand("none underline");
        Map<String, String> resultMap = convertCssDeclarationsToMap(result);
        Assert.assertEquals(3, resultMap.size());
        String line = resultMap.get("text-decoration-line");
        Assert.assertTrue(line != null && line.contains("underline") && line.contains("none"));
        Assert.assertEquals("initial", resultMap.get("text-decoration-style"));
        Assert.assertEquals("initial", resultMap.get("text-decoration-color"));
    }

    @Test
    public void resolveShorthandLineOnePropertyTest() {
        TextDecorationShorthandResolver resolver = new TextDecorationShorthandResolver();
        List<CssDeclaration> result = resolver.resolveShorthand("underline");
        Map<String, String> resultMap = convertCssDeclarationsToMap(result);
        Assert.assertEquals(3, resultMap.size());
        Assert.assertEquals("underline", resultMap.get("text-decoration-line"));
        Assert.assertEquals("initial", resultMap.get("text-decoration-style"));
        Assert.assertEquals("initial", resultMap.get("text-decoration-color"));
    }

    @Test
    public void resolveShorthandLineTwoPropertiesTest() {
        TextDecorationShorthandResolver resolver = new TextDecorationShorthandResolver();
        List<CssDeclaration> result = resolver.resolveShorthand("underline overline");
        Map<String, String> resultMap = convertCssDeclarationsToMap(result);
        Assert.assertEquals(3, resultMap.size());
        String line = resultMap.get("text-decoration-line");
        Assert.assertTrue(line != null && line.contains("underline") && line.contains("overline"));
        Assert.assertEquals("initial", resultMap.get("text-decoration-style"));
        Assert.assertEquals("initial", resultMap.get("text-decoration-color"));
    }

    @Test
    public void resolveShorthandColorNamedTest() {
        TextDecorationShorthandResolver resolver = new TextDecorationShorthandResolver();
        List<CssDeclaration> result = resolver.resolveShorthand("underline red");
        Map<String, String> resultMap = convertCssDeclarationsToMap(result);
        Assert.assertEquals(3, resultMap.size());
        Assert.assertEquals("underline", resultMap.get("text-decoration-line"));
        Assert.assertEquals("initial", resultMap.get("text-decoration-style"));
        Assert.assertEquals("red", resultMap.get("text-decoration-color"));
    }

    @Test
    public void resolveShorthandColorRgbTest() {
        TextDecorationShorthandResolver resolver = new TextDecorationShorthandResolver();
        List<CssDeclaration> result = resolver.resolveShorthand("underline rgb(255, 255, 0)");
        Map<String, String> resultMap = convertCssDeclarationsToMap(result);
        Assert.assertEquals(3, resultMap.size());
        Assert.assertEquals("underline", resultMap.get("text-decoration-line"));
        Assert.assertEquals("initial", resultMap.get("text-decoration-style"));
        Assert.assertEquals("rgb(255,255,0)", resultMap.get("text-decoration-color"));
    }

    @Test
    public void resolveShorthandColorRgbWithOpacityTest() {
        TextDecorationShorthandResolver resolver = new TextDecorationShorthandResolver();
        List<CssDeclaration> result = resolver.resolveShorthand("underline rgb(255, 255, 0, 0.5)");
        Map<String, String> resultMap = convertCssDeclarationsToMap(result);
        Assert.assertEquals(3, resultMap.size());
        Assert.assertEquals("underline", resultMap.get("text-decoration-line"));
        Assert.assertEquals("initial", resultMap.get("text-decoration-style"));
        Assert.assertEquals("rgb(255,255,0,0.5)", resultMap.get("text-decoration-color"));
    }

        @Test
    public void resolveShorthandColorHslTest() {
        TextDecorationShorthandResolver resolver = new TextDecorationShorthandResolver();
        List<CssDeclaration> result = resolver.resolveShorthand("underline hsl(300, 76%, 72%)");
        Map<String, String> resultMap = convertCssDeclarationsToMap(result);
        Assert.assertEquals(3, resultMap.size());
        Assert.assertEquals("underline", resultMap.get("text-decoration-line"));
        Assert.assertEquals("initial", resultMap.get("text-decoration-style"));
        Assert.assertEquals("hsl(300,76%,72%)", resultMap.get("text-decoration-color"));
    }

    @Test
    public void resolveShorthandColorHexTest() {
        TextDecorationShorthandResolver resolver = new TextDecorationShorthandResolver();
        List<CssDeclaration> result = resolver.resolveShorthand("underline #DDAA55");
        Map<String, String> resultMap = convertCssDeclarationsToMap(result);
        Assert.assertEquals(3, resultMap.size());
        Assert.assertEquals("underline", resultMap.get("text-decoration-line"));
        Assert.assertEquals("initial", resultMap.get("text-decoration-style"));
        Assert.assertEquals("#ddaa55", resultMap.get("text-decoration-color"));
    }

    @Test
    public void resolveShorthandStyleOnePropertyTest() {
        TextDecorationShorthandResolver resolver = new TextDecorationShorthandResolver();
        List<CssDeclaration> result = resolver.resolveShorthand("underline wavy");
        Map<String, String> resultMap = convertCssDeclarationsToMap(result);
        Assert.assertEquals(3, resultMap.size());
        Assert.assertEquals("underline", resultMap.get("text-decoration-line"));
        Assert.assertEquals("wavy", resultMap.get("text-decoration-style"));
        Assert.assertEquals("initial", resultMap.get("text-decoration-color"));
    }

    @Test
    public void resolveShorthandStyleTwoPropertiesTest() {
        TextDecorationShorthandResolver resolver = new TextDecorationShorthandResolver();
        List<CssDeclaration> result = resolver.resolveShorthand("underline wavy dotted");
        Map<String, String> resultMap = convertCssDeclarationsToMap(result);
        Assert.assertEquals(3, resultMap.size());
        Assert.assertEquals("underline", resultMap.get("text-decoration-line"));
        Assert.assertEquals("dotted", resultMap.get("text-decoration-style"));
        Assert.assertEquals("initial", resultMap.get("text-decoration-color"));
    }

    private Map<String, String> convertCssDeclarationsToMap(List<CssDeclaration> declarations) {
        Map<String , String> result = new HashMap<>();
        for (CssDeclaration decl : declarations) {
            result.put(decl.getProperty(), decl.getExpression());
        }
        return result;
    }
}
