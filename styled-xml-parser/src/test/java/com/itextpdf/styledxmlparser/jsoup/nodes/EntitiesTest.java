/*
    This file is part of jsoup, see NOTICE.txt in the root of the repository.
    It may contain modifications beyond the original version.
*/
package com.itextpdf.styledxmlparser.jsoup.nodes;

import com.itextpdf.styledxmlparser.jsoup.Jsoup;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import static com.itextpdf.styledxmlparser.jsoup.nodes.Document.OutputSettings;
import static com.itextpdf.styledxmlparser.jsoup.nodes.Entities.EscapeMode.xhtml;

@Tag("UnitTest")
public class EntitiesTest extends ExtendedITextTest {
    @Test public void escape() {
        String text = "Hello &<> Å å π 新 there ¾ © »";
        String escapedAscii = Entities.escape(text, new OutputSettings().charset("ascii").escapeMode(Entities.EscapeMode.base));
        String escapedAsciiFull = Entities.escape(text, new OutputSettings().charset("ascii").escapeMode(Entities.EscapeMode.extended));
        String escapedAsciiXhtml = Entities.escape(text, new OutputSettings().charset("ascii").escapeMode(Entities.EscapeMode.xhtml));
        String escapedUtfFull = Entities.escape(text, new OutputSettings().charset("UTF-8").escapeMode(Entities.EscapeMode.extended));
        String escapedUtfMin = Entities.escape(text, new OutputSettings().charset("UTF-8").escapeMode(Entities.EscapeMode.xhtml));

        Assertions.assertEquals("Hello &amp;&lt;&gt; &Aring; &aring; &#x3c0; &#x65b0; there &frac34; &copy; &raquo;", escapedAscii);
        Assertions.assertEquals("Hello &amp;&lt;&gt; &angst; &aring; &pi; &#x65b0; there &frac34; &copy; &raquo;", escapedAsciiFull);
        Assertions.assertEquals("Hello &amp;&lt;&gt; &#xc5; &#xe5; &#x3c0; &#x65b0; there &#xbe; &#xa9; &#xbb;", escapedAsciiXhtml);
        Assertions.assertEquals("Hello &amp;&lt;&gt; Å å π 新 there ¾ © »", escapedUtfFull);
        Assertions.assertEquals("Hello &amp;&lt;&gt; Å å π 新 there ¾ © »", escapedUtfMin);
        // odd that it's defined as aring in base but angst in full

        // round trip
        Assertions.assertEquals(text, Entities.unescape(escapedAscii));
        Assertions.assertEquals(text, Entities.unescape(escapedAsciiFull));
        Assertions.assertEquals(text, Entities.unescape(escapedAsciiXhtml));
        Assertions.assertEquals(text, Entities.unescape(escapedUtfFull));
        Assertions.assertEquals(text, Entities.unescape(escapedUtfMin));
    }

    @Test public void escapedSupplementary() {
        String text = "\uD835\uDD59";
        String escapedAscii = Entities.escape(text, new OutputSettings().charset("ascii").escapeMode(Entities.EscapeMode.base));
        Assertions.assertEquals("&#x1d559;", escapedAscii);
        String escapedAsciiFull = Entities.escape(text, new OutputSettings().charset("ascii").escapeMode(Entities.EscapeMode.extended));
        Assertions.assertEquals("&hopf;", escapedAsciiFull);
        String escapedUtf= Entities.escape(text, new OutputSettings().charset("UTF-8").escapeMode(Entities.EscapeMode.extended));
        Assertions.assertEquals(text, escapedUtf);
    }

    @Test public void unescapeMultiChars() {
        String text = "&NestedGreaterGreater; &nGg; &nGt; &nGtv; &Gt; &gg;"; // gg is not combo, but 8811 could conflict with NestedGreaterGreater or others
        String un = "≫ ⋙̸ ≫⃒ ≫̸ ≫ ≫";
        Assertions.assertEquals(un, Entities.unescape(text));
        String escaped = Entities.escape(un, new OutputSettings().charset("ascii").escapeMode(Entities.EscapeMode.extended));
        Assertions.assertEquals("&Gt; &Gg;&#x338; &Gt;&#x20d2; &Gt;&#x338; &Gt; &Gt;", escaped);
        Assertions.assertEquals(un, Entities.unescape(escaped));
    }

    @Test public void xhtml() {
        Assertions.assertEquals(38, xhtml.codepointForName("amp"));
        Assertions.assertEquals(62, xhtml.codepointForName("gt"));
        Assertions.assertEquals(60, xhtml.codepointForName("lt"));
        Assertions.assertEquals(34, xhtml.codepointForName("quot"));

        Assertions.assertEquals("amp", xhtml.nameForCodepoint(38));
        Assertions.assertEquals("gt", xhtml.nameForCodepoint(62));
        Assertions.assertEquals("lt", xhtml.nameForCodepoint(60));
        Assertions.assertEquals("quot", xhtml.nameForCodepoint(34));
    }

    @Test public void getByName() {
        Assertions.assertEquals("≫⃒", Entities.getByName("nGt"));
        Assertions.assertEquals("fj", Entities.getByName("fjlig"));
        Assertions.assertEquals("≫", Entities.getByName("gg"));
        Assertions.assertEquals("©", Entities.getByName("copy"));
    }

    @Test public void escapeSupplementaryCharacter() {
        String text = new String(Character.toChars(135361));
        String escapedAscii = Entities.escape(text, new OutputSettings().charset("ascii").escapeMode(Entities.EscapeMode.base));
        Assertions.assertEquals("&#x210c1;", escapedAscii);
        String escapedUtf = Entities.escape(text, new OutputSettings().charset("UTF-8").escapeMode(Entities.EscapeMode.base));
        Assertions.assertEquals(text, escapedUtf);
    }

    @Test public void notMissingMultis() {
        String text = "&nparsl;";
        String un = "\u2AFD\u20E5";
        Assertions.assertEquals(un, Entities.unescape(text));
    }

    @Test public void notMissingSupplementals() {
        String text = "&npolint; &qfr;";
        String un = "⨔ \uD835\uDD2E"; // 𝔮
        Assertions.assertEquals(un, Entities.unescape(text));
    }

    @Test public void unescape() {
        String text = "Hello &AElig; &amp;&LT&gt; &reg &angst; &angst &#960; &#960 &#x65B0; there &! &frac34; &copy; &COPY;";
        Assertions.assertEquals("Hello Æ &<> ® Å &angst π π 新 there &! ¾ © ©", Entities.unescape(text));

        Assertions.assertEquals("&0987654321; &unknown", Entities.unescape("&0987654321; &unknown"));
    }

    @Test public void strictUnescape() { // for attributes, enforce strict unescaping (must look like &#xxx; , not just &#xxx)
        String text = "Hello &amp= &amp;";
        Assertions.assertEquals("Hello &amp= &", Entities.unescape(text, true));
        Assertions.assertEquals("Hello &= &", Entities.unescape(text));
        Assertions.assertEquals("Hello &= &", Entities.unescape(text, false));
    }


    @Test public void caseSensitive() {
        String unescaped = "Ü ü & &";
        Assertions.assertEquals("&Uuml; &uuml; &amp; &amp;",
                Entities.escape(unescaped, new OutputSettings().charset("ascii").escapeMode(Entities.EscapeMode.extended)));

        String escaped = "&Uuml; &uuml; &amp; &AMP";
        Assertions.assertEquals("Ü ü & &", Entities.unescape(escaped));
    }

    @Test public void quoteReplacements() {
        String escaped = "&#92; &#36;";
        String unescaped = "\\ $";

        Assertions.assertEquals(unescaped, Entities.unescape(escaped));
    }

    @Test public void letterDigitEntities() {
        String html = "<p>&sup1;&sup2;&sup3;&frac14;&frac12;&frac34;</p>";
        Document doc = Jsoup.parse(html);
        doc.outputSettings().charset("ascii");
        Element p = doc.select("p").first();
        Assertions.assertEquals("&sup1;&sup2;&sup3;&frac14;&frac12;&frac34;", p.html());
        Assertions.assertEquals("¹²³¼½¾", p.text());
        doc.outputSettings().charset("UTF-8");
        Assertions.assertEquals("¹²³¼½¾", p.html());
    }

    @Test public void noSpuriousDecodes() {
        String string = "http://www.foo.com?a=1&num_rooms=1&children=0&int=VA&b=2";
        Assertions.assertEquals(string, Entities.unescape(string));
    }

    @Test public void escapesGtInXmlAttributesButNotInHtml() {
        // https://github.com/jhy/jsoup/issues/528 - < is OK in HTML attribute values, but not in XML


        String docHtml = "<a title='<p>One</p>'>One</a>";
        Document doc = Jsoup.parse(docHtml);
        Element element = doc.select("a").first();

        doc.outputSettings().escapeMode(Entities.EscapeMode.base);
        Assertions.assertEquals("<a title=\"<p>One</p>\">One</a>", element.outerHtml());

        doc.outputSettings().escapeMode(Entities.EscapeMode.xhtml);
        Assertions.assertEquals("<a title=\"&lt;p>One&lt;/p>\">One</a>", element.outerHtml());
    }
}
