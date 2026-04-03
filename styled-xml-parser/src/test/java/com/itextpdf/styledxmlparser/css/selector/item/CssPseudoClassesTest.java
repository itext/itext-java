package com.itextpdf.styledxmlparser.css.selector.item;

import com.itextpdf.styledxmlparser.IXmlParser;
import com.itextpdf.styledxmlparser.css.selector.CssSelector;
import com.itextpdf.styledxmlparser.node.IDocumentNode;
import com.itextpdf.styledxmlparser.node.IElementNode;
import com.itextpdf.styledxmlparser.node.INode;
import com.itextpdf.styledxmlparser.node.impl.jsoup.JsoupHtmlParser;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("UnitTest")
public class CssPseudoClassesTest extends ExtendedITextTest {

    @Test
    public void isMatchesAnySelectorInListTest() {
        IXmlParser parser = new JsoupHtmlParser();
        IDocumentNode doc = parser.parse("<div id='a' class='c'></div><p id='b'></p>");

        IElementNode a = findElementById(doc, "a");
        IElementNode b = findElementById(doc, "b");

        CssSelector s = new CssSelector(":is(.c, p)");

        Assertions.assertTrue(s.matches(a));
        Assertions.assertTrue(s.matches(b));
    }

    @Test
    public void whereMatchesAnySelectorInListTest() {
        IXmlParser parser = new JsoupHtmlParser();
        IDocumentNode doc = parser.parse("<div id='a' class='c'></div><p id='b'></p>");

        IElementNode a = findElementById(doc, "a");
        IElementNode b = findElementById(doc, "b");

        CssSelector s = new CssSelector(":where(.c, p)");

        Assertions.assertTrue(s.matches(a));
        Assertions.assertTrue(s.matches(b));
    }

    @Test
    public void isSupportsAttributeSelectorWithCommaInQuotedValueTest() {
        IXmlParser parser = new JsoupHtmlParser();
        IDocumentNode doc = parser.parse(
                "<div>"
                        + "<span id='s1' title='a,b'></span>"
                        + "<span id='s2' title='a'></span>"
                        + "</div>"
        );

        IElementNode s1 = findElementById(doc, "s1");
        IElementNode s2 = findElementById(doc, "s2");

        CssSelector selector = new CssSelector(":is([title='a,b'], #doesNotExist)");

        Assertions.assertTrue(selector.matches(s1));
        Assertions.assertFalse(selector.matches(s2));
    }

    @Test
    public void isSupportsNestedFunctionalPseudoClassesTest() {
        IXmlParser parser = new JsoupHtmlParser();
        IDocumentNode doc = parser.parse(
                "<div id='root'>"
                        + "<div id='host' class='c'></div>"
                        + "<p id='p1' class='x'></p>"
                        + "<p id='p2'></p>"
                        + "</div>"
        );

        IElementNode host = findElementById(doc, "host");
        IElementNode p1 = findElementById(doc, "p1");
        IElementNode p2 = findElementById(doc, "p2");

        Assertions.assertNotNull(host);
        Assertions.assertNotNull(p1);
        Assertions.assertNotNull(p2);

        CssSelector selector = new CssSelector(":is(:not(.x), p.x)");

        // host has no .x => :not(.x) matches
        Assertions.assertTrue(selector.matches(host));
        // p1 has .x => :not(.x) doesn't match, but p.x does
        Assertions.assertTrue(selector.matches(p1));
        // p2 has no .x => :not(.x) matches
        Assertions.assertTrue(selector.matches(p2));
    }


    @Test
    public void whereParsesWithExtraWhitespaceAndMatchesCorrectlyTest() {
        IXmlParser parser = new JsoupHtmlParser();
        IDocumentNode doc = parser.parse(
                "<div id='host' class='c'></div>"
                        + "<p id='p1'></p>"
        );

        IElementNode host = findElementById(doc, "host");
        IElementNode p1 = findElementById(doc, "p1");

        CssSelector selector = new CssSelector(":where(  div.c  ,   #p1 )");

        Assertions.assertTrue(selector.matches(host));
        Assertions.assertTrue(selector.matches(p1));
    }

    @Test
    public void isCanBeUsedInsideHasArgumentsTest() {
        IXmlParser parser = new JsoupHtmlParser();
        IDocumentNode doc = parser.parse(
                "<div id='host'>"
                        + "  <span id='s1' title='a,b'></span>"
                        + "</div>"
                        + "<div id='other'>"
                        + "  <em></em>"
                        + "</div>"
        );

        IElementNode host = findElementById(doc, "host");
        IElementNode other = findElementById(doc, "other");

        CssSelector selector = new CssSelector("div:has(:is(span[title='a,b'], p))");

        Assertions.assertTrue(selector.matches(host));
        Assertions.assertFalse(selector.matches(other));
    }

    @Test
    public void whereCanBeUsedInsideHasArgumentsTest() {
        IXmlParser parser = new JsoupHtmlParser();
        IDocumentNode doc = parser.parse(
                "<div id='host'>"
                        + "  <p class='x'></p>"
                        + "</div>"
                        + "<div id='other'>"
                        + "  <span id='s2'></span>"
                        + "</div>"
        );

        IElementNode host = findElementById(doc, "host");
        IElementNode other = findElementById(doc, "other");

        CssSelector selector = new CssSelector("div:has(:where(p.x, span#s2))");

        Assertions.assertTrue(selector.matches(host));
        Assertions.assertTrue(selector.matches(other));
    }

    @Test
    public void isCanNestWhereTest() {
        IXmlParser parser = new JsoupHtmlParser();
        IDocumentNode doc = parser.parse(
                "<div id='a' class='c'></div>"
                        + "<p id='b'></p>"
                        + "<span id='c'></span>"
        );

        IElementNode a = findElementById(doc, "a");
        IElementNode b = findElementById(doc, "b");
        IElementNode c = findElementById(doc, "c");

        CssSelector selector = new CssSelector(":is(:where(.c, p), #doesNotExist)");

        Assertions.assertTrue(selector.matches(a));
        Assertions.assertTrue(selector.matches(b));
        Assertions.assertFalse(selector.matches(c));
    }

    @Test
    public void whereCanNestIsTest() {
        IXmlParser parser = new JsoupHtmlParser();
        IDocumentNode doc = parser.parse(
                "<div id='a' class='c'></div>"
                        + "<p id='b'></p>"
                        + "<span id='c'></span>"
        );

        IElementNode a = findElementById(doc, "a");
        IElementNode b = findElementById(doc, "b");
        IElementNode c = findElementById(doc, "c");

        CssSelector selector = new CssSelector(":where(:is(.c, p), #doesNotExist)");

        Assertions.assertTrue(selector.matches(a));
        Assertions.assertTrue(selector.matches(b));
        Assertions.assertFalse(selector.matches(c));
    }

    @Test
    public void isCanNestWhereAndIsMultipleLevelsTest() {
        IXmlParser parser = new JsoupHtmlParser();
        IDocumentNode doc = parser.parse(
                "<div id='a' class='c'></div>"
                        + "<p id='b' class='x'></p>"
                        + "<p id='c'></p>"
        );

        IElementNode a = findElementById(doc, "a");
        IElementNode b = findElementById(doc, "b");
        IElementNode c = findElementById(doc, "c");

        CssSelector selector = new CssSelector(":is(:where(div.c, :is(p.x, #nope)), #alsoNope)");

        Assertions.assertTrue(selector.matches(a));
        Assertions.assertTrue(selector.matches(b));
        Assertions.assertFalse(selector.matches(c));
    }

    @Test
    public void isIgnoresRelativeSelectorInArgumentsAndStillMatchesValidOnesTest() {
        IXmlParser parser = new JsoupHtmlParser();
        IDocumentNode doc = parser.parse(
                "<div id='a' class='c'></div>"
                        + "<p id='b'></p>"
        );

        IElementNode a = findElementById(doc, "a");
        IElementNode b = findElementById(doc, "b");

        // Per Selectors Level 4 forgiving selector list rules, invalid entries are ignored.
        CssSelector selector = new CssSelector(":is(> p, .c)");

        Assertions.assertTrue(selector.matches(a));
        Assertions.assertFalse(selector.matches(b));
    }

    @Test
    public void whereIgnoresRelativeSelectorInArgumentsAndStillMatchesValidOnesTest() {
        IXmlParser parser = new JsoupHtmlParser();
        IDocumentNode doc = parser.parse(
                "<div id='a' class='c'></div>"
                        + "<p id='b'></p>"
        );

        IElementNode a = findElementById(doc, "a");
        IElementNode b = findElementById(doc, "b");

        CssSelector selector = new CssSelector(":where(+ p, .c)");

        Assertions.assertTrue(selector.matches(a));
        Assertions.assertFalse(selector.matches(b));
    }

    @Test
    public void isIgnoresUnsupportedPseudoClassInArgumentsTest() {
        IXmlParser parser = new JsoupHtmlParser();
        IDocumentNode doc = parser.parse(
                "<div id='a' class='c'></div>"
                        + "<p id='b'></p>"
        );

        IElementNode a = findElementById(doc, "a");
        IElementNode b = findElementById(doc, "b");

        // Unknown pseudo-class should not invalidate the whole :is(), it should be ignored.
        CssSelector selector = new CssSelector(":is(:unknownPseudo(.x), .c)");

        Assertions.assertTrue(selector.matches(a));
        Assertions.assertFalse(selector.matches(b));
    }

    @Test
    public void whereIgnoresUnsupportedPseudoClassInArgumentsTest() {
        IXmlParser parser = new JsoupHtmlParser();
        IDocumentNode doc = parser.parse(
                "<div id='a' class='c'></div>"
                        + "<p id='b'></p>"
        );

        IElementNode a = findElementById(doc, "a");
        IElementNode b = findElementById(doc, "b");

        CssSelector selector = new CssSelector(":where(:unknownPseudo(.x), .c)");

        Assertions.assertTrue(selector.matches(a));
        Assertions.assertFalse(selector.matches(b));
    }

    @Test
    public void isIgnoresSelectorWithPseudoElementInArgumentsTest() {
        IXmlParser parser = new JsoupHtmlParser();
        IDocumentNode doc = parser.parse(
                "<div id='a' class='c'></div>"
                        + "<p id='b'></p>"
        );

        IElementNode a = findElementById(doc, "a");
        IElementNode b = findElementById(doc, "b");

        // Selectors containing pseudo-elements are not allowed in :is(...), but in a forgiving list
        // they should be ignored rather than invalidating :is(...).
        CssSelector selector = new CssSelector(":is(div::before, .c)");

        Assertions.assertTrue(selector.matches(a));
        Assertions.assertFalse(selector.matches(b));
    }

    @Test
    public void whereIgnoresSelectorWithPseudoElementInArgumentsTest() {
        IXmlParser parser = new JsoupHtmlParser();
        IDocumentNode doc = parser.parse(
                "<div id='a' class='c'></div>"
                        + "<p id='b'></p>"
        );

        IElementNode a = findElementById(doc, "a");
        IElementNode b = findElementById(doc, "b");

        CssSelector selector = new CssSelector(":where(p::after, .c)");

        Assertions.assertTrue(selector.matches(a));
        Assertions.assertFalse(selector.matches(b));
    }

    @Test
    public void isAllInvalidSelectorsInArgumentsMatchesNothingTest() {
        IXmlParser parser = new JsoupHtmlParser();
        IDocumentNode doc = parser.parse(
                "<div id='a' class='c'></div>"
                        + "<p id='b'></p>"
        );

        IElementNode a = findElementById(doc, "a");
        IElementNode b = findElementById(doc, "b");

        // All entries invalid => selector-list is effectively empty => matches nothing.
        CssSelector selector = new CssSelector(":is(> p, ::before, :unknownPseudo(.x))");

        Assertions.assertFalse(selector.matches(a));
        Assertions.assertFalse(selector.matches(b));
    }

    @Test
    public void whereAllInvalidSelectorsInArgumentsMatchesNothingTest() {
        IXmlParser parser = new JsoupHtmlParser();
        IDocumentNode doc = parser.parse(
                "<div id='a' class='c'></div>"
                        + "<p id='b'></p>"
        );

        IElementNode a = findElementById(doc, "a");
        IElementNode b = findElementById(doc, "b");

        CssSelector selector = new CssSelector(":where(+ p, ::after, :unknownPseudo(.x))");

        Assertions.assertFalse(selector.matches(a));
        Assertions.assertFalse(selector.matches(b));
    }

    @Test
    public void isEmptyArgumentsIsInvalidTest() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new CssSelector(":is()"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> new CssSelector(":is(   )"));
    }

    @Test
    public void whereEmptyArgumentsIsInvalidTest() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new CssSelector(":where()"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> new CssSelector(":where(\n\t )"));
    }

    @Test
    public void isSpecificityUsesMaxOfArgumentsTest() {
        // :is(#id, .class) should have ID specificity (highest among arguments)
        CssSelector isWithId = new CssSelector(":is(#myId, .myClass)");
        // ID specificity = 1 << 20 = 1048576
        Assertions.assertEquals(1 << 20, isWithId.calculateSpecificity());

        // :is(.a, .b) should have class specificity
        CssSelector isWithClasses = new CssSelector(":is(.a, .b)");
        // Class specificity = 1 << 10 = 1024
        Assertions.assertEquals(1 << 10, isWithClasses.calculateSpecificity());

        // :is(div, p) should have element specificity
        CssSelector isWithElements = new CssSelector(":is(div, p)");
        // Element specificity = 1
        Assertions.assertEquals(1, isWithElements.calculateSpecificity());
    }

    @Test
    public void whereSpecificityIsAlwaysZeroTest() {
        // :where() always contributes 0 specificity regardless of arguments
        CssSelector whereWithId = new CssSelector(":where(#myId, .myClass)");
        Assertions.assertEquals(0, whereWithId.calculateSpecificity());

        CssSelector whereWithClasses = new CssSelector(":where(.a, .b)");
        Assertions.assertEquals(0, whereWithClasses.calculateSpecificity());
    }

    @Test
    public void combinedSelectorSpecificityTest() {
        // div:is(.a, .b) should have element (1) + class (1024) = 1025
        CssSelector combined = new CssSelector("div:is(.a, .b)");
        Assertions.assertEquals(1 + (1 << 10), combined.calculateSpecificity());

        // div:where(.a, #id) should have only element (1) since :where() = 0
        CssSelector combinedWhere = new CssSelector("div:where(.a, #id)");
        Assertions.assertEquals(1, combinedWhere.calculateSpecificity());
    }


    @Test
    public void isExtraCommaCreatesEmptyEntryWhichIsIgnoredTest() {
        IXmlParser parser = new JsoupHtmlParser();
        IDocumentNode doc = parser.parse(
                "<div id='a' class='c'></div>"
                        + "<p id='b'></p>"
        );

        IElementNode a = findElementById(doc, "a");
        IElementNode b = findElementById(doc, "b");

        CssSelector selector = new CssSelector(":is(.c,, #doesNotExist)");

        Assertions.assertTrue(selector.matches(a));
        Assertions.assertFalse(selector.matches(b));
    }

    @Test
    public void whereExtraCommaCreatesEmptyEntryWhichIsIgnoredTest() {
        IXmlParser parser = new JsoupHtmlParser();
        IDocumentNode doc = parser.parse(
                "<div id='a' class='c'></div>"
                        + "<p id='b'></p>"
        );

        IElementNode a = findElementById(doc, "a");
        IElementNode b = findElementById(doc, "b");

        CssSelector selector = new CssSelector(":where(.c,, #doesNotExist)");

        Assertions.assertTrue(selector.matches(a));
        Assertions.assertFalse(selector.matches(b));
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

