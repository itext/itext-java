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
package com.itextpdf.styledxmlparser.jsoup.safety;

import com.itextpdf.styledxmlparser.jsoup.Jsoup;
import com.itextpdf.styledxmlparser.jsoup.TextUtil;
import com.itextpdf.styledxmlparser.jsoup.nodes.Document;
import com.itextpdf.styledxmlparser.jsoup.nodes.Entities;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

/**
 Tests for the cleaner.
*/
@Tag("UnitTest")
public class CleanerTest extends ExtendedITextTest {
    @Test public void simpleBehaviourTest() {
        String h = "<div><p class=foo><a href='http://evil.com'>Hello <b id=bar>there</b>!</a></div>";
        String cleanHtml = Jsoup.clean(h, Safelist.simpleText());

        Assertions.assertEquals("Hello <b>there</b>!", TextUtil.stripNewlines(cleanHtml));
    }

    @Test public void simpleBehaviourTest2() {
        String h = "Hello <b>there</b>!";
        String cleanHtml = Jsoup.clean(h, Safelist.simpleText());

        Assertions.assertEquals("Hello <b>there</b>!", TextUtil.stripNewlines(cleanHtml));
    }

    @Test public void basicBehaviourTest() {
        String h = "<div><p><a href='javascript:sendAllMoney()'>Dodgy</a> <A HREF='HTTP://nice.com/'>Nice</a></p><blockquote>Hello</blockquote>";
        String cleanHtml = Jsoup.clean(h, Safelist.basic());

        Assertions.assertEquals("<p><a rel=\"nofollow\">Dodgy</a> <a href=\"http://nice.com/\" rel=\"nofollow\">Nice</a></p><blockquote>Hello</blockquote>",
                TextUtil.stripNewlines(cleanHtml));
    }

    @Test public void basicWithImagesTest() {
        String h = "<div><p><img src='http://example.com/' alt=Image></p><p><img src='ftp://ftp.example.com'></p></div>";
        String cleanHtml = Jsoup.clean(h, Safelist.basicWithImages());
        Assertions.assertEquals("<p><img src=\"http://example.com/\" alt=\"Image\"></p><p><img></p>", TextUtil.stripNewlines(cleanHtml));
    }

    @Test public void testRelaxed() {
        String h = "<h1>Head</h1><table><tr><td>One<td>Two</td></tr></table>";
        String cleanHtml = Jsoup.clean(h, Safelist.relaxed());
        Assertions.assertEquals("<h1>Head</h1><table><tbody><tr><td>One</td><td>Two</td></tr></tbody></table>", TextUtil.stripNewlines(cleanHtml));
    }

    @Test public void testRemoveTags() {
        String h = "<div><p><A HREF='HTTP://nice.com'>Nice</a></p><blockquote>Hello</blockquote>";
        String cleanHtml = Jsoup.clean(h, Safelist.basic().removeTags("a"));

        Assertions.assertEquals("<p>Nice</p><blockquote>Hello</blockquote>", TextUtil.stripNewlines(cleanHtml));
    }

    @Test public void testRemoveAttributes() {
        String h = "<div><p>Nice</p><blockquote cite='http://example.com/quotations'>Hello</blockquote>";
        String cleanHtml = Jsoup.clean(h, Safelist.basic().removeAttributes("blockquote", "cite"));

        Assertions.assertEquals("<p>Nice</p><blockquote>Hello</blockquote>", TextUtil.stripNewlines(cleanHtml));
    }

    @Test public void testRemoveEnforcedAttributes() {
        String h = "<div><p><A HREF='HTTP://nice.com/'>Nice</a></p><blockquote>Hello</blockquote>";
        String cleanHtml = Jsoup.clean(h, Safelist.basic().removeEnforcedAttribute("a", "rel"));

        Assertions.assertEquals("<p><a href=\"http://nice.com/\">Nice</a></p><blockquote>Hello</blockquote>",
                TextUtil.stripNewlines(cleanHtml));
    }

    @Test public void testRemoveProtocols() {
        String h = "<p>Contact me <a href='mailto:info@example.com'>here</a></p>";
        String cleanHtml = Jsoup.clean(h, Safelist.basic().removeProtocols("a", "href", "ftp", "mailto"));

        Assertions.assertEquals("<p>Contact me <a rel=\"nofollow\">here</a></p>",
                TextUtil.stripNewlines(cleanHtml));
    }

    @Test public void testDropComments() {
        String h = "<p>Hello<!-- no --></p>";
        String cleanHtml = Jsoup.clean(h, Safelist.relaxed());
        Assertions.assertEquals("<p>Hello</p>", cleanHtml);
    }

    @Test public void testDropXmlProc() {
        String h = "<?import namespace=\"xss\"><p>Hello</p>";
        String cleanHtml = Jsoup.clean(h, Safelist.relaxed());
        Assertions.assertEquals("<p>Hello</p>", cleanHtml);
    }

    @Test public void testDropScript() {
        String h = "<SCRIPT SRC=//ha.ckers.org/.j><SCRIPT>alert(/XSS/.source)</SCRIPT>";
        String cleanHtml = Jsoup.clean(h, Safelist.relaxed());
        Assertions.assertEquals("", cleanHtml);
    }

    @Test public void testDropImageScript() {
        String h = "<IMG SRC=\"javascript:alert('XSS')\">";
        String cleanHtml = Jsoup.clean(h, Safelist.relaxed());
        Assertions.assertEquals("<img>", cleanHtml);
    }

    @Test public void testCleanJavascriptHref() {
        String h = "<A HREF=\"javascript:document.location='http://www.google.com/'\">XSS</A>";
        String cleanHtml = Jsoup.clean(h, Safelist.relaxed());
        Assertions.assertEquals("<a>XSS</a>", cleanHtml);
    }

    @Test public void testCleanAnchorProtocol() {
        String validAnchor = "<a href=\"#valid\">Valid anchor</a>";
        String invalidAnchor = "<a href=\"#anchor with spaces\">Invalid anchor</a>";

        // A Safelist that does not allow anchors will strip them out.
        String cleanHtml = Jsoup.clean(validAnchor, Safelist.relaxed());
        Assertions.assertEquals("<a>Valid anchor</a>", cleanHtml);

        cleanHtml = Jsoup.clean(invalidAnchor, Safelist.relaxed());
        Assertions.assertEquals("<a>Invalid anchor</a>", cleanHtml);

        // A Safelist that allows them will keep them.
        Safelist relaxedWithAnchor = Safelist.relaxed().addProtocols("a", "href", "#");

        cleanHtml = Jsoup.clean(validAnchor, relaxedWithAnchor);
        Assertions.assertEquals(validAnchor, cleanHtml);

        // An invalid anchor is never valid.
        cleanHtml = Jsoup.clean(invalidAnchor, relaxedWithAnchor);
        Assertions.assertEquals("<a>Invalid anchor</a>", cleanHtml);
    }

    @Test public void testDropsUnknownTags() {
        String h = "<p><custom foo=true>Test</custom></p>";
        String cleanHtml = Jsoup.clean(h, Safelist.relaxed());
        Assertions.assertEquals("<p>Test</p>", cleanHtml);
    }

    @Test public void testHandlesEmptyAttributes() {
        String h = "<img alt=\"\" src= unknown=''>";
        String cleanHtml = Jsoup.clean(h, Safelist.basicWithImages());
        Assertions.assertEquals("<img alt=\"\">", cleanHtml);
    }

    @Test public void testIsValidBodyHtml() {
        String ok = "<p>Test <b><a href='http://example.com/' rel='nofollow'>OK</a></b></p>";
        String ok1 = "<p>Test <b><a href='http://example.com/'>OK</a></b></p>"; // missing enforced is OK because still needs run thru cleaner
        String nok1 = "<p><script></script>Not <b>OK</b></p>";
        String nok2 = "<p align=right>Test Not <b>OK</b></p>";
        String nok3 = "<!-- comment --><p>Not OK</p>"; // comments and the like will be cleaned
        String nok4 = "<html><head>Foo</head><body><b>OK</b></body></html>"; // not body html
        String nok5 = "<p>Test <b><a href='http://example.com/' rel='nofollowme'>OK</a></b></p>";
        String nok6 = "<p>Test <b><a href='http://example.com/'>OK</b></p>"; // missing close tag
        String nok7 = "</div>What";
        Assertions.assertTrue(Jsoup.isValid(ok, Safelist.basic()));
        Assertions.assertTrue(Jsoup.isValid(ok1, Safelist.basic()));
        Assertions.assertFalse(Jsoup.isValid(nok1, Safelist.basic()));
        Assertions.assertFalse(Jsoup.isValid(nok2, Safelist.basic()));
        Assertions.assertFalse(Jsoup.isValid(nok3, Safelist.basic()));
        Assertions.assertFalse(Jsoup.isValid(nok4, Safelist.basic()));
        Assertions.assertFalse(Jsoup.isValid(nok5, Safelist.basic()));
        Assertions.assertFalse(Jsoup.isValid(nok6, Safelist.basic()));
        Assertions.assertFalse(Jsoup.isValid(ok, Safelist.none()));
        Assertions.assertFalse(Jsoup.isValid(nok7, Safelist.basic()));
    }

    @Test public void testIsValidDocument() {
        String ok = "<html><head></head><body><p>Hello</p></body><html>";
        String nok = "<html><head><script>woops</script><title>Hello</title></head><body><p>Hello</p></body><html>";

        Safelist relaxed = Safelist.relaxed();
        Cleaner cleaner = new Cleaner(relaxed);
        Document okDoc = Jsoup.parse(ok);
        Assertions.assertTrue(cleaner.isValid(okDoc));
        Assertions.assertFalse(cleaner.isValid(Jsoup.parse(nok)));
        Assertions.assertFalse(new Cleaner(Safelist.none()).isValid(okDoc));
    }

    @Test public void resolvesRelativeLinks() {
        String html = "<a href='/foo'>Link</a><img src='/bar'>";
        String clean = Jsoup.clean(html, "http://example.com/", Safelist.basicWithImages());
        Assertions.assertEquals("<a href=\"http://example.com/foo\" rel=\"nofollow\">Link</a>\n<img src=\"http://example.com/bar\">", clean);
    }

    @Test public void preservesRelativeLinksIfConfigured() {
        String html = "<a href='/foo'>Link</a><img src='/bar'> <img src='javascript:alert()'>";
        String clean = Jsoup.clean(html, "http://example.com/", Safelist.basicWithImages().preserveRelativeLinks(true));
        Assertions.assertEquals("<a href=\"/foo\" rel=\"nofollow\">Link</a>\n<img src=\"/bar\"> \n<img>", clean);
    }

    @Test public void dropsUnresolvableRelativeLinks() {
        String html = "<a href='/foo'>Link</a>";
        String clean = Jsoup.clean(html, Safelist.basic());
        Assertions.assertEquals("<a rel=\"nofollow\">Link</a>", clean);
    }

    @Test public void handlesCustomProtocols() {
        String html = "<img src='cid:12345' /> <img src='data:gzzt' />";
        String dropped = Jsoup.clean(html, Safelist.basicWithImages());
        Assertions.assertEquals("<img> \n<img>", dropped);

        String preserved = Jsoup.clean(html, Safelist.basicWithImages().addProtocols("img", "src", "cid", "data"));
        Assertions.assertEquals("<img src=\"cid:12345\"> \n<img src=\"data:gzzt\">", preserved);
    }

    @Test public void handlesAllPseudoTag() {
        String html = "<p class='foo' src='bar'><a class='qux'>link</a></p>";
        Safelist safelist = new Safelist()
                .addAttributes(":all", "class")
                .addAttributes("p", "style")
                .addTags("p", "a");

        String clean = Jsoup.clean(html, safelist);
        Assertions.assertEquals("<p class=\"foo\"><a class=\"qux\">link</a></p>", clean);
    }

    @Test public void addsTagOnAttributesIfNotSet() {
        String html = "<p class='foo' src='bar'>One</p>";
        Safelist safelist = new Safelist()
            .addAttributes("p", "class");
        // ^^ safelist does not have explicit tag add for p, inferred from add attributes.
        String clean = Jsoup.clean(html, safelist);
        Assertions.assertEquals("<p class=\"foo\">One</p>", clean);
    }

    @Test public void supplyOutputSettings() {
        // test that one can override the default document output settings
        Document.OutputSettings os = new Document.OutputSettings();
        os.prettyPrint(false);
        os.escapeMode(Entities.EscapeMode.extended);
        os.charset("ascii");

        String html = "<div><p>&bernou;</p></div>";
        String customOut = Jsoup.clean(html, "http://foo.com/", Safelist.relaxed(), os);
        String defaultOut = Jsoup.clean(html, "http://foo.com/", Safelist.relaxed());
        Assertions.assertNotSame(defaultOut, customOut);

        Assertions.assertEquals("<div><p>&Bscr;</p></div>", customOut); // entities now prefers shorted names if aliased
        Assertions.assertEquals("<div>\n" +
            " <p>ℬ</p>\n" +
            "</div>", defaultOut);

        os.charset("ASCII");
        os.escapeMode(Entities.EscapeMode.base);
        String customOut2 = Jsoup.clean(html, "http://foo.com/", Safelist.relaxed(), os);
        Assertions.assertEquals("<div><p>&#x212c;</p></div>", customOut2);
    }

    @Test public void handlesFramesets() {
        String dirty = "<html><head><script></script><noscript></noscript></head><frameset><frame src=\"foo\" /><frame src=\"foo\" /></frameset></html>";
        String clean = Jsoup.clean(dirty, Safelist.basic());
        Assertions.assertEquals("", clean); // nothing good can come out of that

        Document dirtyDoc = Jsoup.parse(dirty);
        Document cleanDoc = new Cleaner(Safelist.basic()).clean(dirtyDoc);
        Assertions.assertNotNull(cleanDoc);
        Assertions.assertEquals(0, cleanDoc.body().childNodeSize());
    }

    @Test public void cleansInternationalText() {
        Assertions.assertEquals("привет", Jsoup.clean("привет", Safelist.none()));
    }

    @Test
    public void testScriptTagInSafeList() {
        Safelist safelist = Safelist.relaxed();
        safelist.addTags( "script" );
        Assertions.assertTrue( Jsoup.isValid("Hello<script>alert('Doh')</script>World !", safelist) );
    }

    @Test
    public void bailsIfRemovingProtocolThatsNotSet() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            // a case that came up on the email list
            Safelist w = Safelist.none();

            // note no add tag, and removing protocol without adding first
            w.addAttributes("a", "href");
            w.removeProtocols("a", "href", "javascript"); // with no protocols enforced, this was a noop. Now validates.
        });
    }

    @Test public void handlesControlCharactersAfterTagName() {
        String html = "<a/\06>";
        String clean = Jsoup.clean(html, Safelist.basic());
        Assertions.assertEquals("<a rel=\"nofollow\"></a>", clean);
    }

    @Test public void handlesAttributesWithNoValue() {
        // https://github.com/jhy/jsoup/issues/973
        String clean = Jsoup.clean("<a href>Clean</a>", Safelist.basic());

        Assertions.assertEquals("<a rel=\"nofollow\">Clean</a>", clean);
    }

    @Test public void handlesNoHrefAttribute() {
        String dirty = "<a>One</a> <a href>Two</a>";
        Safelist relaxedWithAnchor = Safelist.relaxed().addProtocols("a", "href", "#");
        String clean = Jsoup.clean(dirty, relaxedWithAnchor);
        Assertions.assertEquals("<a>One</a> <a>Two</a>", clean);
    }

    @Test public void handlesNestedQuotesInAttribute() {
        // https://github.com/jhy/jsoup/issues/1243 - no repro
        String orig = "<div style=\"font-family: 'Calibri'\">Will (not) fail</div>";
        Safelist allow = Safelist.relaxed()
            .addAttributes("div", "style");

        String clean = Jsoup.clean(orig, allow);
        boolean isValid = Jsoup.isValid(orig, allow);

        Assertions.assertEquals(orig, TextUtil.stripNewlines(clean)); // only difference is pretty print wrap & indent
        Assertions.assertTrue(isValid);
    }

    @Test public void copiesOutputSettings() {
        Document orig = Jsoup.parse("<p>test<br></p>");
        orig.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
        orig.outputSettings().escapeMode(Entities.EscapeMode.xhtml);
        Safelist whitelist = Safelist.none().addTags("p", "br");

        Document result = new Cleaner(whitelist).clean(orig);
        Assertions.assertEquals(Document.OutputSettings.Syntax.xml, result.outputSettings().syntax());
        Assertions.assertEquals("<p>test<br /></p>", result.body().html());
    }
}
