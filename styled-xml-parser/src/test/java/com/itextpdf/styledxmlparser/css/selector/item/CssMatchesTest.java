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
package com.itextpdf.styledxmlparser.css.selector.item;


import com.itextpdf.styledxmlparser.IXmlParser;
import com.itextpdf.styledxmlparser.node.IDocumentNode;
import com.itextpdf.styledxmlparser.node.INode;
import com.itextpdf.styledxmlparser.node.impl.jsoup.JsoupHtmlParser;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class CssMatchesTest extends ExtendedITextTest {

    @Test
    public void matchesEmptySelectorItemTest() {
      CssPseudoClassEmptySelectorItem item = CssPseudoClassEmptySelectorItem.getInstance();
      IXmlParser htmlParser = new JsoupHtmlParser();
      IDocumentNode documentNode = htmlParser.parse("<div><input value=\"Alexander\"></div>");

      INode bodyNode = documentNode
              .childNodes().get(0)
                    .childNodes().get(1);
      INode divNode = bodyNode
              .childNodes().get(0)
                  .childNodes().get(0);

      Assertions.assertTrue(item.matches(divNode));
    }

    @Test
    public void matchesEmptySelectorItemNotTaggedTextTest() {
        CssPseudoClassEmptySelectorItem item = CssPseudoClassEmptySelectorItem.getInstance();
        IXmlParser htmlParser = new JsoupHtmlParser();
        IDocumentNode documentNode = htmlParser.parse("Some text!");

        INode bodyNode = documentNode
                .childNodes().get(0)
                    .childNodes().get(1);
        INode divNode = bodyNode
                .childNodes().get(0);

        Assertions.assertFalse(item.matches(divNode));
    }

    @Test
    public void matchesEmptySelectorItemSpaceTest() {
        CssPseudoClassEmptySelectorItem item = CssPseudoClassEmptySelectorItem.getInstance();
        IXmlParser htmlParser = new JsoupHtmlParser();
        IDocumentNode documentNode = htmlParser.parse("<div> </div>");

        INode bodyNode = documentNode
                .childNodes().get(0)
                    .childNodes().get(1);
        INode divNode = bodyNode
                .childNodes().get(0);

        Assertions.assertFalse(item.matches(divNode));
    }

    @Test
    public void matchesFirstOfTypeSelectorItemTest() {
        CssPseudoClassFirstOfTypeSelectorItem item = CssPseudoClassFirstOfTypeSelectorItem.getInstance();
        IXmlParser htmlParser = new JsoupHtmlParser();
        IDocumentNode documentNode = htmlParser.parse("<div><p>Alexander</p><p>Alexander</p></div>");

        INode bodyNode = documentNode
                .childNodes().get(0)
                    .childNodes().get(1);
        INode divNode = bodyNode
                .childNodes().get(0)
                    .childNodes().get(0);

        Assertions.assertTrue(item.matches(divNode));
    }

    @Test
    public void matchesFirstOfTypeSelectorItemTestNotTaggedText() {
        CssPseudoClassFirstOfTypeSelectorItem item = CssPseudoClassFirstOfTypeSelectorItem.getInstance();
        IXmlParser htmlParser = new JsoupHtmlParser();
        IDocumentNode documentNode = htmlParser.parse("Some text!");

        INode bodyNode = documentNode
                .childNodes().get(0)
                    .childNodes().get(1);
        INode divNode = bodyNode
                .childNodes().get(0);

        Assertions.assertFalse(item.matches(divNode));
    }

    @Test
    public void matchesLastOfTypeSelectorItemTest() {
        CssPseudoClassLastOfTypeSelectorItem item = CssPseudoClassLastOfTypeSelectorItem.getInstance();
        IXmlParser htmlParser = new JsoupHtmlParser();
        IDocumentNode documentNode = htmlParser.parse("<div><p>Alexander</p><p>Alexander</p></div>");

        INode bodyNode = documentNode
                .childNodes().get(0)
                    .childNodes().get(1);
        INode divNode = bodyNode
                .childNodes().get(0)
                    .childNodes().get(1);

        Assertions.assertTrue(item.matches(divNode));
    }

    @Test
    public void matchesLastOfTypeSelectorItemTestNotTaggedText() {
        CssPseudoClassLastOfTypeSelectorItem item = CssPseudoClassLastOfTypeSelectorItem.getInstance();
        IXmlParser htmlParser = new JsoupHtmlParser();
        IDocumentNode documentNode = htmlParser.parse("SomeText!");

        INode bodyNode = documentNode
                .childNodes().get(0)
                    .childNodes().get(1);
        INode divNode = bodyNode
                .childNodes().get(0);

        Assertions.assertFalse(item.matches(divNode));
    }

    @Test
    public void matchesLastChildSelectorItemTest() {
        CssPseudoClassLastChildSelectorItem item = CssPseudoClassLastChildSelectorItem.getInstance();
        IXmlParser htmlParser = new JsoupHtmlParser();
        IDocumentNode documentNode = htmlParser.parse("<div><p>Alexander</p><p>Alexander</p></div>");

        INode bodyNode = documentNode
                .childNodes().get(0)
                    .childNodes().get(1);
        INode divNode = bodyNode
                .childNodes().get(0)
                    .childNodes().get(1);

        Assertions.assertTrue(item.matches(divNode));
    }

    @Test
    public void matchesLastChildSelectorItemTestNotTaggedText() {
        CssPseudoClassLastChildSelectorItem item = CssPseudoClassLastChildSelectorItem.getInstance();
        IXmlParser htmlParser = new JsoupHtmlParser();
        IDocumentNode documentNode = htmlParser.parse("SomeText!");

        INode bodyNode = documentNode
                .childNodes().get(0)
                    .childNodes().get(1);
        INode divNode = bodyNode
                .childNodes().get(0);

        Assertions.assertFalse(item.matches(divNode));
    }

    @Test
    public void matchesNthOfTypeSelectorItemTest() {
        CssPseudoClassNthOfTypeSelectorItem item = new CssPseudoClassNthOfTypeSelectorItem("1n");
        IXmlParser htmlParser = new JsoupHtmlParser();
        IDocumentNode documentNode = htmlParser.parse("<div><p>Alexander</p><p>Alexander</p></div>");

        INode bodyNode = documentNode
                .childNodes().get(0)
                    .childNodes().get(1);
        INode divNode = bodyNode
                .childNodes().get(0)
                    .childNodes().get(0);

        Assertions.assertTrue(item.matches(divNode));
    }

    @Test
    public void matchesNthOfTypeSelectorItemTestNotTaggedText() {
        CssPseudoClassNthOfTypeSelectorItem item = new CssPseudoClassNthOfTypeSelectorItem("1n");
        IXmlParser htmlParser = new JsoupHtmlParser();
        IDocumentNode documentNode = htmlParser.parse("SomeText!");

        INode bodyNode = documentNode
                .childNodes().get(0)
                    .childNodes().get(1);
        INode divNode = bodyNode
                .childNodes().get(0);

        Assertions.assertFalse(item.matches(divNode));
    }

    @Test
    public void matchesNthOfTypeSelectorItemTestBadNodeArgument() {
        CssPseudoClassNthOfTypeSelectorItem item = new CssPseudoClassNthOfTypeSelectorItem("text");
        IXmlParser htmlParser = new JsoupHtmlParser();
        IDocumentNode documentNode = htmlParser.parse("<div><p>Alexander</p><p>Alexander</p></div>");

        INode bodyNode = documentNode
                .childNodes().get(0)
                    .childNodes().get(1);
        INode divNode = bodyNode
                .childNodes().get(0)
                    .childNodes().get(0);

        Assertions.assertFalse(item.matches(divNode));
    }

    @Test
    public void matchesRootSelectorItemTest() {
        CssPseudoClassRootSelectorItem item = CssPseudoClassRootSelectorItem.getInstance();
        IXmlParser htmlParser = new JsoupHtmlParser();
        IDocumentNode documentNode = htmlParser.parse("<div><p>Alexander</p><p>Alexander</p></div>");

        INode headNode = documentNode
                .childNodes().get(0);

        Assertions.assertTrue(item.matches(headNode));
    }

    @Test
    public void matchesRootSelectorItemTestNotTaggedText() {
        CssPseudoClassRootSelectorItem item = CssPseudoClassRootSelectorItem.getInstance();
        IXmlParser htmlParser = new JsoupHtmlParser();
        IDocumentNode documentNode = htmlParser.parse("SomeText!");

        INode bodyNode = documentNode
                .childNodes().get(0)
                    .childNodes().get(1);
        INode divNode = bodyNode
                .childNodes().get(0);

        Assertions.assertFalse(item.matches(divNode));
    }
}
