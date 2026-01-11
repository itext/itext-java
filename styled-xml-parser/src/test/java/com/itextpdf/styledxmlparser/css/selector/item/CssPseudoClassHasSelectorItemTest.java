/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
import com.itextpdf.styledxmlparser.css.parse.CssSelectorParser;
import com.itextpdf.styledxmlparser.css.selector.CssSelector;
import com.itextpdf.styledxmlparser.css.selector.ICssSelector;
import com.itextpdf.styledxmlparser.node.IDocumentNode;
import com.itextpdf.styledxmlparser.node.IElementNode;
import com.itextpdf.styledxmlparser.node.INode;
import com.itextpdf.styledxmlparser.node.impl.jsoup.JsoupHtmlParser;
import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("UnitTest")
public class CssPseudoClassHasSelectorItemTest extends ExtendedITextTest {

    @Test
    public void hasDescendantSelectorItemTest() {
        CssPseudoClassHasSelectorItem item = new CssPseudoClassHasSelectorItem(new CssSelector("p"));

        IXmlParser htmlParser = new JsoupHtmlParser();
        IDocumentNode documentNode = htmlParser.parse("<div id='host'><p id='p1'></p></div>");
        IElementNode host = findElementById(documentNode, "host");

        Assertions.assertFalse(item.matches(documentNode));
        Assertions.assertTrue(item.matches(host));
        Assertions.assertFalse(item.matches(null));
    }

    @Test
    public void hasDirectChildSelectorItemTest() {
        CssPseudoClassHasSelectorItem item = new CssPseudoClassHasSelectorItem(
                CssSelectorParser.parseCommaSeparatedSelectors("> p").get(0));

        IXmlParser htmlParser = new JsoupHtmlParser();
        IDocumentNode docDirect = htmlParser.parse("<div id='host'><p id='p1'></p></div>");
        IElementNode hostDirect = findElementById(docDirect, "host");

        IDocumentNode docNested = htmlParser.parse("<div id='host'><span><p id='p1'></p></span></div>");
        IElementNode hostNested = findElementById(docNested, "host");

        Assertions.assertTrue(item.matches(hostDirect));
        Assertions.assertFalse(item.matches(hostNested));
    }

    @Test
    public void hasNextSiblingSelectorItemTest() {
        CssPseudoClassHasSelectorItem item = new CssPseudoClassHasSelectorItem(
                CssSelectorParser.parseCommaSeparatedSelectors("+ p").get(0));

        IXmlParser htmlParser = new JsoupHtmlParser();
        IDocumentNode doc = htmlParser.parse(
                "<div>"
                        + "<span id='a'></span>"
                        + "<p id='b'></p>"
                        + "<p id='c'></p>"
                        + "</div>");
        IElementNode a = findElementById(doc, "a");

        Assertions.assertTrue(item.matches(a));
    }

    @Test
    public void hasNextSiblingSelectorItemSkipsNonElementNodesTest() {
        CssPseudoClassHasSelectorItem item = new CssPseudoClassHasSelectorItem(
                CssSelectorParser.parseCommaSeparatedSelectors("+ p").get(0));

        IXmlParser htmlParser = new JsoupHtmlParser();
        IDocumentNode doc = htmlParser.parse(
                "<div>"
                        + "<span id='a'></span>"
                        + " some text "
                        + "<p id='b'></p>"
                        + "</div>");
        IElementNode a = findElementById(doc, "a");

        Assertions.assertTrue(item.matches(a));
    }

    @Test
    public void hasFollowingSiblingSelectorItemTest() {
        CssPseudoClassHasSelectorItem item = new CssPseudoClassHasSelectorItem(
                CssSelectorParser.parseCommaSeparatedSelectors("~ p").get(0));

        IXmlParser htmlParser = new JsoupHtmlParser();
        IDocumentNode doc = htmlParser.parse(
                "<div>"
                        + "<span id='a'></span>"
                        + "<span id='x'></span>"
                        + "<p id='b'></p>"
                        + "</div>");
        IElementNode a = findElementById(doc, "a");

        Assertions.assertTrue(item.matches(a));
    }

    @Test
    public void hasNegativeCaseNoMatchingElementsTest() {
        CssPseudoClassHasSelectorItem item = new CssPseudoClassHasSelectorItem(new CssSelector("p.needle"));

        IXmlParser htmlParser = new JsoupHtmlParser();
        IDocumentNode documentNode = htmlParser.parse("<div id='host'><p class='other'></p></div>");
        IElementNode host = findElementById(documentNode, "host");

        Assertions.assertFalse(item.matches(host));
    }

    @Test
    public void hasSelectorWithCommaInAttributeTest() {
        CssPseudoClassHasSelectorItem item = new CssPseudoClassHasSelectorItem(
                CssSelectorParser.parseCommaSeparatedSelectors("span[title='a,b']").get(0));

        IXmlParser htmlParser = new JsoupHtmlParser();
        IDocumentNode doc = htmlParser.parse("<div id='host'><span title='a,b'></span></div>");
        IElementNode host = findElementById(doc, "host");

        Assertions.assertTrue(item.matches(host));
    }


    @Test
    public void hasSelectorIntegrationViaCssSelectorTest() {
        IXmlParser htmlParser = new JsoupHtmlParser();
        IDocumentNode documentNode = htmlParser.parse(
                "<div>"
                        + "<div id='a'><p></p></div>"
                        + "<div id='b'><span></span></div>"
                        + "</div>");

        IElementNode a = findElementById(documentNode, "a");
        IElementNode b = findElementById(documentNode, "b");

        CssSelector selector = new CssSelector("div:has(p)");

        Assertions.assertTrue(selector.matches(a));
        Assertions.assertFalse(selector.matches(b));
    }

    @Test
    public void hasDoesNotMatchBasedOnScopeElementItselfTest() {
        CssPseudoClassHasSelectorItem item = new CssPseudoClassHasSelectorItem(new CssSelector("div"));

        IXmlParser htmlParser = new JsoupHtmlParser();
        IDocumentNode doc = htmlParser.parse("<div id='host'></div>");
        IElementNode host = findElementById(doc, "host");

        // Must be false because the scope element itself should not be considered a "descendant".
        Assertions.assertFalse(item.matches(host));
    }

    @Test
    public void hasDescendantDeepNestedSelectorItemTest() {
        CssPseudoClassHasSelectorItem item = new CssPseudoClassHasSelectorItem(new CssSelector("p"));

        IXmlParser htmlParser = new JsoupHtmlParser();
        IDocumentNode doc = htmlParser.parse("<div id='host'><span><em><p id='p1'></p></em></span></div>");
        IElementNode host = findElementById(doc, "host");

        Assertions.assertTrue(item.matches(host));
    }

    @Test
    public void hasDirectChildDoesNotMatchNestedDescendantTest() {
        CssPseudoClassHasSelectorItem item = new CssPseudoClassHasSelectorItem(
                CssSelectorParser.parseCommaSeparatedSelectors("> p").get(0));

        IXmlParser htmlParser = new JsoupHtmlParser();
        IDocumentNode doc = htmlParser.parse("<div id='host'><span><p id='p1'></p></span></div>");
        IElementNode host = findElementById(doc, "host");

        Assertions.assertFalse(item.matches(host));
    }

    @Test
    public void hasNextSiblingIsOnlyNextElementSiblingTest() {
        CssPseudoClassHasSelectorItem item = new CssPseudoClassHasSelectorItem(
                CssSelectorParser.parseCommaSeparatedSelectors("+ p").get(0));

        IXmlParser htmlParser = new JsoupHtmlParser();
        IDocumentNode doc = htmlParser.parse(
                "<div>"
                        + "<span id='a'></span>"
                        + "<span id='x'></span>"
                        + "<p id='b'></p>"
                        + "</div>");
        IElementNode a = findElementById(doc, "a");

        // Next element sibling is <span id='x'>, so "+ p" must be false.
        Assertions.assertFalse(item.matches(a));
    }

    @Test
    public void hasFollowingSiblingSkipsUntilMatchTest() {
        CssPseudoClassHasSelectorItem item = new CssPseudoClassHasSelectorItem(
                CssSelectorParser.parseCommaSeparatedSelectors("~ p").get(0));

        IXmlParser htmlParser = new JsoupHtmlParser();
        IDocumentNode doc = htmlParser.parse(
                "<div>"
                        + "<span id='a'></span>"
                        + "<span id='x'></span>"
                        + "<p id='b'></p>"
                        + "</div>");
        IElementNode a = findElementById(doc, "a");

        // "~ p" should match any following element sibling, not necessarily the next.
        Assertions.assertTrue(item.matches(a));
    }

    @Test
    public void hasSiblingSelectorsReturnFalseWhenNoParentTest() {
        IXmlParser htmlParser = new JsoupHtmlParser();
        IDocumentNode doc = htmlParser.parse("<div id='root'></div>");
        IElementNode root = findElementById(doc, "root");

        CssPseudoClassHasSelectorItem nextSibling = new CssPseudoClassHasSelectorItem(
                CssSelectorParser.parseCommaSeparatedSelectors("+ p").get(0));
        CssPseudoClassHasSelectorItem followingSibling = new CssPseudoClassHasSelectorItem(
                CssSelectorParser.parseCommaSeparatedSelectors("~ p").get(0));

        Assertions.assertFalse(nextSibling.matches(root));
        Assertions.assertFalse(followingSibling.matches(root));
    }

    @Test
    public void hasSupportsAttributeSelectorInArgumentsTest() {
        CssPseudoClassHasSelectorItem item = new CssPseudoClassHasSelectorItem(new CssSelector("[data-x]"));

        IXmlParser htmlParser = new JsoupHtmlParser();
        IDocumentNode doc = htmlParser.parse("<div id='host'><span data-x='1'></span></div>");
        IElementNode host = findElementById(doc, "host");

        Assertions.assertTrue(item.matches(host));
    }

    @Test
    public void hasSupportsMultiStepSelectorInArgumentsTest() {
        CssPseudoClassHasSelectorItem item = new CssPseudoClassHasSelectorItem(new CssSelector("div > p"));

        IXmlParser htmlParser = new JsoupHtmlParser();
        IDocumentNode doc = htmlParser.parse("<section id='host'><div><p></p></div></section>");
        IElementNode host = findElementById(doc, "host");

        Assertions.assertTrue(item.matches(host));
    }

    @Test
    public void hasIntegrationWithLeadingCombinatorViaCssSelectorTest() {
        IXmlParser htmlParser = new JsoupHtmlParser();
        IDocumentNode doc = htmlParser.parse(
                "<div>"
                        + "<div id='a'><p></p></div>"
                        + "<div id='b'><span><p></p></span></div>"
                        + "</div>");

        IElementNode a = findElementById(doc, "a");
        IElementNode b = findElementById(doc, "b");

        CssSelector selector = new CssSelector("div:has(> p)");

        Assertions.assertTrue(selector.matches(a));
        Assertions.assertFalse(selector.matches(b));
    }

    @Test
    public void hasCombinatorOnlyReturnsFalseTest() {
        CssPseudoClassHasSelectorItem item = new CssPseudoClassHasSelectorItem(
                CssSelectorParser.parseCommaSeparatedSelectors(">").get(0));

        IXmlParser htmlParser = new JsoupHtmlParser();
        IDocumentNode doc = htmlParser.parse("<div id='host'><p></p></div>");
        IElementNode host = findElementById(doc, "host");

        Assertions.assertFalse(item.matches(host));
    }

    @Test
    public void hasSelectorListCommaSeparatedTest() {
        // :has(p, span) should match if element has either p OR span descendant
        CssSelector selector = new CssSelector("div:has(p, span)");

        IXmlParser htmlParser = new JsoupHtmlParser();
        IDocumentNode doc = htmlParser.parse(
                "<div>"
                        + "<div id='a'><p></p></div>"
                        + "<div id='b'><span></span></div>"
                        + "<div id='c'><em></em></div>"
                        + "</div>");

        IElementNode a = findElementById(doc, "a");
        IElementNode b = findElementById(doc, "b");
        IElementNode c = findElementById(doc, "c");

        Assertions.assertTrue(selector.matches(a));  // has <p>
        Assertions.assertTrue(selector.matches(b));  // has <span>
        Assertions.assertFalse(selector.matches(c)); // has neither
    }

    @Test
    public void hasSelectorWithMultipleCombinatorsDoesNotMatchWhenHierarchyDiffersTest() {
        CssSelector selector = new CssSelector("div:has(div > p > span)");

        IXmlParser htmlParser = new JsoupHtmlParser();
        IDocumentNode doc = htmlParser.parse(
                "<div>"
                        + "<div id='a'><p></p><div><span></span></div></div>"
                        + "<div id='b'><span></span></div>"
                        + "<div id='c'><em></em></div>"
                        + "</div>");

        IElementNode a = findElementById(doc, "a");
        Assertions.assertFalse(selector.matches(a));
    }


    @Test
    public void hasSpecificityWithSelectorListTest() {
        // Specificity should be the maximum of all selectors
        CssPseudoClassHasSelectorItem item = new CssPseudoClassHasSelectorItem(
                java.util.Arrays.asList((ICssSelector) new CssSelector("p"), (ICssSelector) new CssSelector("#id")),
                "p, #id"
        );

        // #id has higher specificity (0,1,0,0) than p (0,0,0,1)
        Assertions.assertEquals(new CssSelector("#id").calculateSpecificity(), item.getSpecificity());
    }

    @Test
    public void hasMalformedCombinatorOnlyTest() {
        CssPseudoClassHasSelectorItem item = new CssPseudoClassHasSelectorItem(
                CssSelectorParser.parseCommaSeparatedSelectors(">").get(0));

        IXmlParser htmlParser = new JsoupHtmlParser();
        IDocumentNode doc = htmlParser.parse("<div id='host'><p></p></div>");
        IElementNode host = findElementById(doc, "host");

        Assertions.assertFalse(item.matches(host));
    }

    @Test
    public void hasWithPseudoElementReturnsNullTest() {
        // Pseudo-elements in :has() should not be supported
        CssPseudoClassSelectorItem item = CssPseudoClassSelectorItem.create("has", "p::before");
        Assertions.assertNull(item);
    }

    @Test
    public void hasEmptyArgumentsReturnsFalseTest() {
        CssPseudoClassHasSelectorItem item = new CssPseudoClassHasSelectorItem(new CssSelector(""));

        IXmlParser htmlParser = new JsoupHtmlParser();
        IDocumentNode doc = htmlParser.parse("<div id='host'><p></p></div>");
        IElementNode host = findElementById(doc, "host");

        Assertions.assertFalse(item.matches(host));
    }

    @Test
    public void hasNoSpaceAfterLeadingCombinatorTest() {
        IXmlParser htmlParser = new JsoupHtmlParser();
        IDocumentNode doc = htmlParser.parse(
                "<div>"
                        + "<div id='a'><p></p></div>"
                        + "<div id='b'><span></span><p></p></div>"
                        + "</div>");

        IElementNode a = findElementById(doc, "a");
        IElementNode b = findElementById(doc, "b");

        Assertions.assertTrue(new CssSelector("div:has(>p)").matches(a));
        Assertions.assertTrue(new CssSelector("div:has(>p)").matches(b)); // direct children are <span> and <p> in b? actually b has direct <span> and <p>
        Assertions.assertTrue(new CssSelector("div:has(>p)").matches(b));

        Assertions.assertFalse(new CssSelector("div:has(+p)").matches(a)); // next element sibling is b (div), not p
        Assertions.assertFalse(new CssSelector("div:has(~p)").matches(a)); // following siblings are divs, not p
    }

    @Test
    public void hasSelectorListWithMixedLeadingCombinatorsTest() {
        // div:has(> p, +span, em) should match:
        // - by direct child p
        // - OR by next sibling span
        // - OR by descendant em
        IXmlParser htmlParser = new JsoupHtmlParser();
        IDocumentNode doc = htmlParser.parse(
                "<section>"
                        + "<div id='x'><p></p></div>"
                        + "<div id='y'></div>"
                        + "<div id='z'><em></em></div>"
                        + "<span id='s'></span>"
                        + "</section>");

        IElementNode x = findElementById(doc, "x");
        IElementNode y = findElementById(doc, "y");
        IElementNode z = findElementById(doc, "z");

        CssSelector selector = new CssSelector("div:has(> p, +span, em)");

        Assertions.assertTrue(selector.matches(x)); // has direct child <p>
        Assertions.assertFalse(selector.matches(y)); // no direct <p>, no descendant <em>, next sibling is z (div) not span
        Assertions.assertTrue(selector.matches(z)); // has descendant <em>
    }

    @Test
    public void hasDeepRelativeSelectorTest() {
        // :has(> div > p) requires a direct child div which itself has a direct child p.
        IXmlParser htmlParser = new JsoupHtmlParser();

        IDocumentNode docOk = htmlParser.parse("<section id='host'><div><p></p></div></section>");
        IElementNode hostOk = findElementById(docOk, "host");

        IDocumentNode docBad = htmlParser.parse("<section id='host'><span><p></p></span></section>");
        IElementNode hostBad = findElementById(docBad, "host");

        CssSelector selector = new CssSelector("section:has(> div > p)");

        Assertions.assertTrue(selector.matches(hostOk));
        Assertions.assertFalse(selector.matches(hostBad));
    }

    @Test
    public void hasChainedNextSiblingRelativeSelectorTest() {
        // :has(+ div > p) requires that the next element sibling is a div that has a direct child p.
        IXmlParser htmlParser = new JsoupHtmlParser();

        IDocumentNode docOk = htmlParser.parse(
                "<section>"
                        + "<span id='host'></span>"
                        + "<div><p></p></div>"
                        + "</section>");
        IElementNode hostOk = findElementById(docOk, "host");

        IDocumentNode docBadNextIsNotDiv = htmlParser.parse(
                "<section>"
                        + "<span id='host'></span>"
                        + "<span><p></p></span>"
                        + "</section>");
        IElementNode hostBadNextIsNotDiv = findElementById(docBadNextIsNotDiv, "host");

        IDocumentNode docBadDivWithoutP = htmlParser.parse(
                "<section>"
                        + "<span id='host'></span>"
                        + "<div><span></span></div>"
                        + "</section>");
        IElementNode hostBadDivWithoutP = findElementById(docBadDivWithoutP, "host");

        CssSelector selector = new CssSelector("span:has(+ div > p)");

        Assertions.assertTrue(selector.matches(hostOk));
        Assertions.assertFalse(selector.matches(hostBadNextIsNotDiv));
        Assertions.assertFalse(selector.matches(hostBadDivWithoutP));
    }

    @Test
    public void hasChainedFollowingSiblingRelativeSelectorTest() {
        // :has(~ div > p) requires that some following element sibling is a div that has a direct child p.
        IXmlParser htmlParser = new JsoupHtmlParser();

        IDocumentNode docOk = htmlParser.parse(
                "<section>"
                        + "<span id='host'></span>"
                        + "<span></span>"
                        + "<div><p></p></div>"
                        + "</section>");
        IElementNode hostOk = findElementById(docOk, "host");

        IDocumentNode docBadNoFollowingDiv = htmlParser.parse(
                "<section>"
                        + "<span id='host'></span>"
                        + "<span></span>"
                        + "<span><p></p></span>"
                        + "</section>");
        IElementNode hostBadNoFollowingDiv = findElementById(docBadNoFollowingDiv, "host");

        IDocumentNode docBadDivWithoutP = htmlParser.parse(
                "<section>"
                        + "<span id='host'></span>"
                        + "<div><span></span></div>"
                        + "<div><span></span></div>"
                        + "</section>");
        IElementNode hostBadDivWithoutP = findElementById(docBadDivWithoutP, "host");

        CssSelector selector = new CssSelector("span:has(~ div > p)");

        Assertions.assertTrue(selector.matches(hostOk));
        Assertions.assertFalse(selector.matches(hostBadNoFollowingDiv));
        Assertions.assertFalse(selector.matches(hostBadDivWithoutP));
    }


    @Test
    public void hasNestedHasInFullSelectorShouldThrowTest() {
        // Factory returns null for nested :has(), and parser treats unsupported pseudo selector as an error.
        Assertions.assertThrows(IllegalArgumentException.class, () -> new CssSelector("div:has(p:has(span))"));
    }

    @Test
    public void hasNestedNotSelectorTest() {
        // div:has(p:not(.ignore))
        CssSelector selector = new CssSelector("div:has(p:not(.ignore))");

        IXmlParser htmlParser = new JsoupHtmlParser();
        IDocumentNode doc = htmlParser.parse(
                "<div>"
                        + "<div id='a'><p>Valid</p></div>"
                        + "<div id='b'><p class='ignore'>Ignored</p></div>"
                        + "</div>");

        IElementNode a = findElementById(doc, "a");
        IElementNode b = findElementById(doc, "b");

        Assertions.assertTrue(selector.matches(a));
        Assertions.assertFalse(selector.matches(b));
    }

    @Test
    public void hasWithComplexDescendantSelectorTest() {
        // div:has(section p) means: match a div that contains a section which contains a p
        // The section must be INSIDE the div, not outside
        IXmlParser htmlParser = new JsoupHtmlParser();

        // Case 1: section is INSIDE the div -> should match
        IDocumentNode docMatch = htmlParser.parse(
                "<div id='host'>"
                        + "<section>"
                        + "<p></p>"
                        + "</section>"
                        + "</div>");
        IElementNode hostMatch = findElementById(docMatch, "host");

        // Case 2: section is OUTSIDE the div (ancestor) -> should NOT match
        // because :has() only searches descendants
        IDocumentNode docNoMatch = htmlParser.parse(
                "<section>"
                        + "<div id='host'>"
                        + "<p></p>"
                        + "</div>"
                        + "</section>");
        IElementNode hostNoMatch = findElementById(docNoMatch, "host");

        CssSelector selector = new CssSelector("div:has(section p)");

        // Should match: div contains section > p
        Assertions.assertTrue(selector.matches(hostMatch));

        // Should NOT match: section is ancestor, not descendant
        // This is correct behavior per CSS spec
        Assertions.assertFalse(selector.matches(hostNoMatch));
    }

    @Test
    public void hasWithNestedStructureRequiringBothLevelsTest() {
        // div:has(ul li.active) - match div that has a ul containing li.active
        IXmlParser htmlParser = new JsoupHtmlParser();

        IDocumentNode docMatch = htmlParser.parse(
                "<div id='a'>"
                        + "<ul>"
                        + "<li class='active'>Item</li>"
                        + "</ul>"
                        + "</div>");

        IDocumentNode docNoUl = htmlParser.parse(
                "<div id='b'>"
                        + "<li class='active'>Item without ul parent</li>"
                        + "</div>");

        IDocumentNode docNoActiveClass = htmlParser.parse(
                "<div id='c'>"
                        + "<ul>"
                        + "<li>Item without active class</li>"
                        + "</ul>"
                        + "</div>");

        IElementNode a = findElementById(docMatch, "a");
        IElementNode b = findElementById(docNoUl, "b");
        IElementNode c = findElementById(docNoActiveClass, "c");

        CssSelector selector = new CssSelector("div:has(ul li.active)");

        Assertions.assertTrue(selector.matches(a));   // has ul > li.active
        Assertions.assertFalse(selector.matches(b));  // li.active exists but not inside ul
        Assertions.assertFalse(selector.matches(c));  // ul > li exists but not .active
    }

    @Test
    public void hasWithAdjacentSiblingSelectorInArgumentsTest() {
        // div:has(span + p) should match only when a <p> exists whose immediately preceding sibling is <span>
        IXmlParser htmlParser = new JsoupHtmlParser();

        IDocumentNode docOk = htmlParser.parse(
                "<div id='host'>"
                        + "<span></span>"
                        + "<p></p>"
                        + "</div>");
        IElementNode hostOk = findElementById(docOk, "host");

        IDocumentNode docBadNotAdjacent = htmlParser.parse(
                "<div id='host'>"
                        + "<span></span>"
                        + "<em></em>"
                        + "<p></p>"
                        + "</div>");
        IElementNode hostBadNotAdjacent = findElementById(docBadNotAdjacent, "host");

        CssSelector selector = new CssSelector("div:has(span + p)");

        Assertions.assertTrue(selector.matches(hostOk));
        Assertions.assertFalse(selector.matches(hostBadNotAdjacent));
    }

    @Test
    public void hasWithGeneralSiblingSelectorInArgumentsTest() {
        // div:has(span ~ p) should match when a <p> exists that has any preceding <span> sibling
        IXmlParser htmlParser = new JsoupHtmlParser();

        IDocumentNode docOk = htmlParser.parse(
                "<div id='host'>"
                        + "<span></span>"
                        + "<em></em>"
                        + "<p></p>"
                        + "</div>");
        IElementNode hostOk = findElementById(docOk, "host");

        IDocumentNode docBadSpanAfterP = htmlParser.parse(
                "<div id='host'>"
                        + "<p></p>"
                        + "<span></span>"
                        + "</div>");
        IElementNode hostBadSpanAfterP = findElementById(docBadSpanAfterP, "host");

        CssSelector selector = new CssSelector("div:has(span ~ p)");

        Assertions.assertTrue(selector.matches(hostOk));
        Assertions.assertFalse(selector.matches(hostBadSpanAfterP));
    }

    @Test
    public void hasRelativeSelectorWithDescendantCombinatorStepMatchesDeepDescendantTest() {
        // Covers CssPseudoClassHasSelectorItem#fillNextScopesByCombinator case ' '
        // via a relative selector that includes a descendant combinator step:
        // :has(> div p) => first step '>' then step ' ' (descendant).
        IXmlParser htmlParser = new JsoupHtmlParser();

        IDocumentNode doc = htmlParser.parse(
                "<section id='host'>"
                        + "<div>"
                        + "<span><p class='target'></p></span>"
                        + "</div>"
                        + "</section>");
        IElementNode host = findElementById(doc, "host");

        CssSelector selector = new CssSelector("section:has(> div p.target)");
        Assertions.assertTrue(selector.matches(host));
    }

    @Test
    public void hasRelativeSelectorWithDescendantCombinatorStepDoesNotEscapeChildScopeTest() {
        // Negative case for :has(> div p): p exists, but not as a descendant of the matched "div" scope.
        IXmlParser htmlParser = new JsoupHtmlParser();

        IDocumentNode doc = htmlParser.parse(
                "<section id='host'>"
                        + "<div></div>"
                        + "<p class='target'></p>"
                        + "</section>");
        IElementNode host = findElementById(doc, "host");

        CssSelector selector = new CssSelector("section:has(> div p.target)");
        Assertions.assertFalse(selector.matches(host));
    }

    @Test
    public void hasArgumentSelectorDoesNotEscapeHasScopeWhenMatchingAncestorsTest() {
        // Verifies CssSelector#matchesWithinScope boundary is respected inside :has().
        // The candidate <p> is inside the host <div>, but "body p" requires an ancestor "body".
        // Since ancestor traversal is limited to the :has() scope element, this must be false.
        IXmlParser htmlParser = new JsoupHtmlParser();
        IDocumentNode doc = htmlParser.parse(
                "<body>"
                        + "<div id='host'><p></p></div>"
                        + "</body>");
        IElementNode host = findElementById(doc, "host");

        CssSelector selector = new CssSelector("div:has(body p)");
        Assertions.assertFalse(selector.matches(host));
    }

    @Test
    public void hasRelativeSelectorDescendantStepWorksAcrossMultipleCurrentScopesTest() {
        // Ensures the descendant step (' ') is applied for EACH current scope when there are multiple
        // scopes after a previous step (e.g. after '> div' there may be multiple div scopes).
        IXmlParser htmlParser = new JsoupHtmlParser();
        IDocumentNode doc = htmlParser.parse(
                "<section id='host'>"
                        + "<div id='d1'><span><em><p class='target'></p></em></span></div>"
                        + "<div id='d2'><span><p></p></span></div>"
                        + "</section>");
        IElementNode host = findElementById(doc, "host");

        CssSelector selector = new CssSelector("section:has(> div p.target)");
        Assertions.assertTrue(selector.matches(host));
    }

    @Test
    public void hasRelativeSelectorDescendantStepReturnsFalseWhenNoDescendantMatchesInAnyScopeTest() {
        // Negative counterpart to the test above.
        IXmlParser htmlParser = new JsoupHtmlParser();
        IDocumentNode doc = htmlParser.parse(
                "<section id='host'>"
                        + "<div><span><p></p></span></div>"
                        + "<div><span><p></p></span></div>"
                        + "</section>");
        IElementNode host = findElementById(doc, "host");

        CssSelector selector = new CssSelector("section:has(> div p.target)");
        Assertions.assertFalse(selector.matches(host));
    }

    @Test
    public void emptyHasTest() {
        IXmlParser htmlParser = new JsoupHtmlParser();
        IDocumentNode doc = htmlParser.parse(
                "<section id='host'>"
                        + "<div id='d1'><span><em><p class='target'></p></em></span></div>"
                        + "<div id='d2'><span><p></p></span></div>"
                        + "</section>");
        IElementNode host = findElementById(doc, "host");

        Assertions.assertFalse(new CssSelector("has()").matches(host));
        Assertions.assertFalse(new CssSelector("has(,)").matches(host));
    }

    @Test
    public void trippleHasTest() {
        IXmlParser htmlParser = new JsoupHtmlParser();
        IDocumentNode doc = htmlParser.parse(
                "<section id='host'>"
                        + "<div id='d1' class='c1'><span><em><p class='target'><span class=\"c2\"></span></p></em></span></div>"
                        + "<div id='d2'><span><p></p></span></div>"
                        + "</section>");
        IElementNode host = findElementById(doc, "host");

        CssSelector selector = new CssSelector(":has(.c1, > .target, > .c2)");
        Assertions.assertTrue(selector.matches(host));
    }

    private static IElementNode findElementById(INode root, String id) {
        if (root instanceof IElementNode) {
            IElementNode el = (IElementNode) root;
            String attr = el.getAttribute("id");
            if (id.equals(attr)) {
                return el;
            }
        }

        for (INode child : root.childNodes()) {
            IElementNode found = findElementById(child, id);
            if (found != null) {
                return found;
            }
        }
        return null;
    }
}
