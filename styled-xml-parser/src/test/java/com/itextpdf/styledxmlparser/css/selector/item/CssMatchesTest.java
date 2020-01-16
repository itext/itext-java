/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2020 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.styledxmlparser.css.selector.item;


import com.itextpdf.styledxmlparser.IXmlParser;
import com.itextpdf.styledxmlparser.node.IDocumentNode;
import com.itextpdf.styledxmlparser.node.INode;
import com.itextpdf.styledxmlparser.node.impl.jsoup.JsoupHtmlParser;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
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

      Assert.assertTrue(item.matches(divNode));
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

        Assert.assertFalse(item.matches(divNode));
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

        Assert.assertFalse(item.matches(divNode));
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

        Assert.assertTrue(item.matches(divNode));
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

        Assert.assertFalse(item.matches(divNode));
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

        Assert.assertTrue(item.matches(divNode));
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

        Assert.assertFalse(item.matches(divNode));
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

        Assert.assertTrue(item.matches(divNode));
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

        Assert.assertFalse(item.matches(divNode));
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

        Assert.assertTrue(item.matches(divNode));
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

        Assert.assertFalse(item.matches(divNode));
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

        Assert.assertFalse(item.matches(divNode));
    }

    @Test
    public void matchesRootSelectorItemTest() {
        CssPseudoClassRootSelectorItem item = CssPseudoClassRootSelectorItem.getInstance();
        IXmlParser htmlParser = new JsoupHtmlParser();
        IDocumentNode documentNode = htmlParser.parse("<div><p>Alexander</p><p>Alexander</p></div>");

        INode headNode = documentNode
                .childNodes().get(0);

        Assert.assertTrue(item.matches(headNode));
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

        Assert.assertFalse(item.matches(divNode));
    }
}
