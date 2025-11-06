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
package com.itextpdf.styledxmlparser.css.parse;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.exceptions.IOException;
import com.itextpdf.styledxmlparser.css.selector.item.CssAttributeSelectorItem;
import com.itextpdf.styledxmlparser.css.selector.item.CssClassSelectorItem;
import com.itextpdf.styledxmlparser.css.selector.item.CssIdSelectorItem;
import com.itextpdf.styledxmlparser.css.selector.item.CssPseudoClassSelectorItem;
import com.itextpdf.styledxmlparser.css.selector.item.CssPseudoElementSelectorItem;
import com.itextpdf.styledxmlparser.css.selector.item.CssSeparatorSelectorItem;
import com.itextpdf.styledxmlparser.css.selector.item.CssTagSelectorItem;
import com.itextpdf.styledxmlparser.css.selector.item.ICssSelectorItem;
import com.itextpdf.styledxmlparser.exceptions.StyledXmlParserExceptionMessage;
import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.util.List;

@Tag("UnitTest")
public class CssSelectorParserTest extends ExtendedITextTest {

    @Test
    public void selectorBeginsWithSpaceTest() throws IOException {
        String space = " ";
        String selectorWithSpaceAtTheBeginning = space + ".spaceBefore";

        Exception expectedException = Assertions.assertThrows(IllegalArgumentException.class,
                () -> CssSelectorParser.parseSelectorItems(selectorWithSpaceAtTheBeginning));
        Assertions.assertEquals(
                MessageFormatUtil.format(StyledXmlParserExceptionMessage.INVALID_SELECTOR_STRING,
                        space),
                expectedException.getMessage());
    }

    @Test
    public void pseudoClassSelectorTest() throws IOException {
        String selector = "li:first-of-type + li";
        List<ICssSelectorItem> parsedSelector = CssSelectorParser.parseSelectorItems(selector);
        Assertions.assertEquals(4, parsedSelector.size());
        Assertions.assertTrue(parsedSelector.get(0) instanceof CssTagSelectorItem);
        Assertions.assertEquals("li", parsedSelector.get(0).toString());
        Assertions.assertTrue(parsedSelector.get(1) instanceof CssPseudoClassSelectorItem);
        Assertions.assertEquals(":first-of-type", parsedSelector.get(1).toString());
        Assertions.assertTrue(parsedSelector.get(2) instanceof CssSeparatorSelectorItem);
        Assertions.assertEquals(" + ", parsedSelector.get(2).toString());
        Assertions.assertTrue(parsedSelector.get(3) instanceof CssTagSelectorItem);
        Assertions.assertEquals("li", parsedSelector.get(3).toString());
    }

    @Test
    public void pseudoClassSelectorWithParametersTest() throws IOException {
        String selector = "li:nth-child(-n + 3)";
        List<ICssSelectorItem> parsedSelector = CssSelectorParser.parseSelectorItems(selector);
        Assertions.assertEquals(2, parsedSelector.size());
        Assertions.assertTrue(parsedSelector.get(0) instanceof CssTagSelectorItem);
        Assertions.assertEquals("li", parsedSelector.get(0).toString());
        Assertions.assertTrue(parsedSelector.get(1) instanceof CssPseudoClassSelectorItem);
        Assertions.assertEquals(":nth-child(-n + 3)", parsedSelector.get(1).toString());
    }

    @Test
    public void attributeSelectorTest() throws IOException {
        String selector = "a[class~=\"logo\"]";
        List<ICssSelectorItem> parsedSelector = CssSelectorParser.parseSelectorItems(selector);
        Assertions.assertEquals(2, parsedSelector.size());
        Assertions.assertTrue(parsedSelector.get(0) instanceof CssTagSelectorItem);
        Assertions.assertEquals("a", parsedSelector.get(0).toString());
        Assertions.assertTrue(parsedSelector.get(1) instanceof CssAttributeSelectorItem);
        Assertions.assertEquals("[class~=\"logo\"]", parsedSelector.get(1).toString());
    }

    @Test
    public void idAttributeSelectorTest() throws IOException {
        String selector = "[id=\"id_value\"]";
        List<ICssSelectorItem> parsedSelector = CssSelectorParser.parseSelectorItems(selector);
        Assertions.assertEquals(1, parsedSelector.size());
        Assertions.assertTrue(parsedSelector.get(0) instanceof CssAttributeSelectorItem);
        Assertions.assertEquals("[id=\"id_value\"]", parsedSelector.get(0).toString());
    }

    @Test
    public void universalAttributeSelectorTest() throws IOException {
        String selector = "* [id=\"id_value\"]";
        List<ICssSelectorItem> parsedSelector = CssSelectorParser.parseSelectorItems(selector);
        Assertions.assertEquals(3, parsedSelector.size());
        Assertions.assertTrue(parsedSelector.get(0) instanceof CssTagSelectorItem);
        Assertions.assertEquals("*", parsedSelector.get(0).toString());
        Assertions.assertTrue(parsedSelector.get(1) instanceof CssSeparatorSelectorItem);
        Assertions.assertEquals(" ", parsedSelector.get(1).toString());
        Assertions.assertTrue(parsedSelector.get(2) instanceof CssAttributeSelectorItem);
        Assertions.assertEquals("[id=\"id_value\"]", parsedSelector.get(2).toString());
    }

    @Test
    public void idSelectorTest() throws IOException {
        String selector = "#id_value";
        List<ICssSelectorItem> parsedSelector = CssSelectorParser.parseSelectorItems(selector);
        Assertions.assertEquals(1, parsedSelector.size());
        Assertions.assertTrue(parsedSelector.get(0) instanceof CssIdSelectorItem);
        Assertions.assertEquals("#id_value", parsedSelector.get(0).toString());
    }

    @Test
    public void universalIdSelectorTest() throws IOException {
        String selector = "*#maincontent";
        List<ICssSelectorItem> parsedSelector = CssSelectorParser.parseSelectorItems(selector);
        Assertions.assertEquals(2, parsedSelector.size());
        Assertions.assertTrue(parsedSelector.get(0) instanceof CssTagSelectorItem);
        Assertions.assertEquals("*", parsedSelector.get(0).toString());
        Assertions.assertTrue(parsedSelector.get(1) instanceof CssIdSelectorItem);
        Assertions.assertEquals("#maincontent", parsedSelector.get(1).toString());
    }

    @Test
    public void columnSelectorTest() throws IOException {
        String selector = "::column";
        List<ICssSelectorItem> parsedSelector = CssSelectorParser.parseSelectorItems(selector);
        Assertions.assertEquals(1, parsedSelector.size());
        Assertions.assertTrue(parsedSelector.get(0) instanceof CssPseudoElementSelectorItem);
        Assertions.assertEquals("::column", parsedSelector.get(0).toString());
    }

    @Test
    public void childCombinatorSelectorTest() throws IOException {
        String selector = "ul.my-things > li";
        List<ICssSelectorItem> parsedSelector = CssSelectorParser.parseSelectorItems(selector);
        Assertions.assertEquals(4, parsedSelector.size());
        Assertions.assertTrue(parsedSelector.get(0) instanceof CssTagSelectorItem);
        Assertions.assertEquals("ul", parsedSelector.get(0).toString());
        Assertions.assertTrue(parsedSelector.get(1) instanceof CssClassSelectorItem);
        Assertions.assertEquals(".my-things", parsedSelector.get(1).toString());
        Assertions.assertTrue(parsedSelector.get(2) instanceof CssSeparatorSelectorItem);
        Assertions.assertEquals(" > ", parsedSelector.get(2).toString());
        Assertions.assertTrue(parsedSelector.get(3) instanceof CssTagSelectorItem);
        Assertions.assertEquals("li", parsedSelector.get(3).toString());
    }

    @Test
    public void subsequentSiblingCombinatorSelectorTest() throws IOException {
        String selector = "img ~ p";
        List<ICssSelectorItem> parsedSelector = CssSelectorParser.parseSelectorItems(selector);
        Assertions.assertEquals(3, parsedSelector.size());
        Assertions.assertTrue(parsedSelector.get(0) instanceof CssTagSelectorItem);
        Assertions.assertEquals("img", parsedSelector.get(0).toString());
        Assertions.assertTrue(parsedSelector.get(1) instanceof CssSeparatorSelectorItem);
        Assertions.assertEquals(" ~ ", parsedSelector.get(1).toString());
        Assertions.assertTrue(parsedSelector.get(2) instanceof CssTagSelectorItem);
        Assertions.assertEquals("p", parsedSelector.get(2).toString());
    }

    @Test
    public void descendantCombinatorSelectorTest() throws IOException {
        String selector = "ul.my-things li";
        List<ICssSelectorItem> parsedSelector = CssSelectorParser.parseSelectorItems(selector);
        Assertions.assertEquals(4, parsedSelector.size());
        Assertions.assertTrue(parsedSelector.get(0) instanceof CssTagSelectorItem);
        Assertions.assertEquals("ul", parsedSelector.get(0).toString());
        Assertions.assertTrue(parsedSelector.get(1) instanceof CssClassSelectorItem);
        Assertions.assertEquals(".my-things", parsedSelector.get(1).toString());
        Assertions.assertTrue(parsedSelector.get(2) instanceof CssSeparatorSelectorItem);
        Assertions.assertEquals(" ", parsedSelector.get(2).toString());
        Assertions.assertTrue(parsedSelector.get(3) instanceof CssTagSelectorItem);
        Assertions.assertEquals("li", parsedSelector.get(3).toString());
    }

    @Test
    public void blankNamespaceSeparatorSelectorTest() throws IOException {
        //| is unsupported
        String selector = "|h2";
        Assertions.assertThrows(IllegalArgumentException.class, () -> CssSelectorParser.parseSelectorItems(selector));
    }

    @Test
    public void listSelectorTest() throws IOException {
        String selector = "span, div";
        List<ICssSelectorItem> parsedSelector = CssSelectorParser.parseSelectorItems(selector);
        Assertions.assertEquals(3, parsedSelector.size());
        Assertions.assertTrue(parsedSelector.get(0) instanceof CssTagSelectorItem);
        Assertions.assertEquals("span", parsedSelector.get(0).toString());
        Assertions.assertTrue(parsedSelector.get(1) instanceof CssSeparatorSelectorItem);
        Assertions.assertEquals(" , ", parsedSelector.get(1).toString());
        Assertions.assertTrue(parsedSelector.get(2) instanceof CssTagSelectorItem);
        Assertions.assertEquals("div", parsedSelector.get(2).toString());
    }

    @Test
    public void separatorAtStartTest() throws IOException {
        String selector = "+test";
        Assertions.assertThrows(IllegalArgumentException.class, () -> CssSelectorParser.parseSelectorItems(selector));
    }

    @Test
    public void selectorWithEscapedDotsTest() throws IOException {
        String selector = "#ReleaseiTextCore9\\.3\\.0-FAQ\\(latestones\\)";
        List<ICssSelectorItem> parsedSelector = CssSelectorParser.parseSelectorItems(selector);
        Assertions.assertEquals(1, parsedSelector.size());
        Assertions.assertTrue(parsedSelector.get(0) instanceof CssIdSelectorItem);
        Assertions.assertEquals("#ReleaseiTextCore9.3.0-FAQ(latestones)", parsedSelector.get(0).toString());
    }

    @Test
    public void complexPseudoSelectorTest() throws IOException {
        String selector = "b:not(:last-of-type)::after";
        List<ICssSelectorItem> parsedSelector = CssSelectorParser.parseSelectorItems(selector);
        Assertions.assertEquals(3, parsedSelector.size());
        Assertions.assertTrue(parsedSelector.get(0) instanceof CssTagSelectorItem);
        Assertions.assertEquals("b", parsedSelector.get(0).toString());
        Assertions.assertTrue(parsedSelector.get(1) instanceof CssPseudoClassSelectorItem);
        Assertions.assertEquals(":not(:last-of-type)", parsedSelector.get(1).toString());
        Assertions.assertTrue(parsedSelector.get(2) instanceof CssPseudoElementSelectorItem);
        Assertions.assertEquals("::after", parsedSelector.get(2).toString());
    }

    @Test
    public void escapeAtTheEndTest() throws IOException {
        String selector = "abc\\";
        List<ICssSelectorItem> parsedSelector = CssSelectorParser.parseSelectorItems(selector);
        Assertions.assertEquals(1, parsedSelector.size());
        Assertions.assertTrue(parsedSelector.get(0) instanceof CssTagSelectorItem);
        Assertions.assertEquals("abc\uFFFD", parsedSelector.get(0).toString());
    }

    @Test
    public void escapedHexTest() throws IOException {
        String selector = "abc\\000020def";
        List<ICssSelectorItem> parsedSelector = CssSelectorParser.parseSelectorItems(selector);
        Assertions.assertEquals(1, parsedSelector.size());
        Assertions.assertTrue(parsedSelector.get(0) instanceof CssTagSelectorItem);
        Assertions.assertEquals("abc def", parsedSelector.get(0).toString());
    }

    @Test
    public void escapedHexAfterSeparatorTest() throws IOException {
        String selector = "p+\\000020def";
        List<ICssSelectorItem> parsedSelector = CssSelectorParser.parseSelectorItems(selector);
        Assertions.assertEquals(3, parsedSelector.size());
        Assertions.assertTrue(parsedSelector.get(0) instanceof CssTagSelectorItem);
        Assertions.assertEquals("p", parsedSelector.get(0).toString());
        Assertions.assertTrue(parsedSelector.get(1) instanceof CssSeparatorSelectorItem);
        Assertions.assertEquals(" + ", parsedSelector.get(1).toString());
        Assertions.assertTrue(parsedSelector.get(2) instanceof CssTagSelectorItem);
        Assertions.assertEquals(" def", parsedSelector.get(2).toString());
    }

    @Test
    public void emptyClassTest() {
        String selector = ".";
        List<ICssSelectorItem> parsedSelector = CssSelectorParser.parseSelectorItems(selector);
        Assertions.assertEquals(1, parsedSelector.size());
        Assertions.assertTrue(parsedSelector.get(0) instanceof CssClassSelectorItem);
        Assertions.assertEquals(".", parsedSelector.get(0).toString());
    }

    @Test
    public void emptyIdTest() {
        String selector = "#";
        List<ICssSelectorItem> parsedSelector = CssSelectorParser.parseSelectorItems(selector);
        Assertions.assertEquals(1, parsedSelector.size());
        Assertions.assertTrue(parsedSelector.get(0) instanceof CssIdSelectorItem);
        Assertions.assertEquals("#", parsedSelector.get(0).toString());
    }

    @Test
    public void emptyAttributeTest() {
        String selector = "[]";
        List<ICssSelectorItem> parsedSelector = CssSelectorParser.parseSelectorItems(selector);
        Assertions.assertEquals(1, parsedSelector.size());
        Assertions.assertTrue(parsedSelector.get(0) instanceof CssAttributeSelectorItem);
        Assertions.assertEquals("[]", parsedSelector.get(0).toString());
    }

    @Test
    public void slashesInTagTest() {
        String selector = "//";
        List<ICssSelectorItem> parsedSelector = CssSelectorParser.parseSelectorItems(selector);
        Assertions.assertEquals(1, parsedSelector.size());
        Assertions.assertTrue(parsedSelector.get(0) instanceof CssTagSelectorItem);
        Assertions.assertEquals("//", parsedSelector.get(0).toString());
    }

    @Test
    public void utf32EscapeTest() {
        String selector = ".\\1F600";
        List<ICssSelectorItem> parsedSelector = CssSelectorParser.parseSelectorItems(selector);
        Assertions.assertEquals(1, parsedSelector.size());
        Assertions.assertTrue(parsedSelector.get(0) instanceof CssClassSelectorItem);
        //U+1F600 Grinning Face goes beyond UTF-16 and results in a surrogate pair which is handled poorly by the parser
        Assertions.assertEquals(".\uF600", parsedSelector.get(0).toString());
        Assertions.assertNotEquals(".\uD83D\uDE00", parsedSelector.get(0).toString());
    }

    @Test
    public void camelCaseInPseudoStateTest() {
        String selector = "dd:Nth-Last-Of-Type(EvEn)";
        List<ICssSelectorItem> parsedSelector = CssSelectorParser.parseSelectorItems(selector);
        Assertions.assertEquals(2, parsedSelector.size());
        Assertions.assertTrue(parsedSelector.get(0) instanceof CssTagSelectorItem);
        Assertions.assertEquals("dd", parsedSelector.get(0).toString());
        Assertions.assertTrue(parsedSelector.get(1) instanceof CssPseudoClassSelectorItem);
        Assertions.assertEquals(":nth-last-of-type(EvEn)", parsedSelector.get(1).toString());
    }
}