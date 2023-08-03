/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
package com.itextpdf.styledxmlparser.jsoup.nodes;

import com.itextpdf.styledxmlparser.jsoup.Jsoup;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static com.itextpdf.styledxmlparser.jsoup.nodes.Document.OutputSettings;
import static com.itextpdf.styledxmlparser.jsoup.nodes.Entities.EscapeMode.xhtml;

@Category(UnitTest.class)
public class EntitiesTest extends ExtendedITextTest {
    @Test public void escape() {
        String text = "Hello &<> Å å π 新 there ¾ © »";
        String escapedAscii = Entities.escape(text, new OutputSettings().charset("ascii").escapeMode(Entities.EscapeMode.base));
        String escapedAsciiFull = Entities.escape(text, new OutputSettings().charset("ascii").escapeMode(Entities.EscapeMode.extended));
        String escapedAsciiXhtml = Entities.escape(text, new OutputSettings().charset("ascii").escapeMode(Entities.EscapeMode.xhtml));
        String escapedUtfFull = Entities.escape(text, new OutputSettings().charset("UTF-8").escapeMode(Entities.EscapeMode.extended));
        String escapedUtfMin = Entities.escape(text, new OutputSettings().charset("UTF-8").escapeMode(Entities.EscapeMode.xhtml));

        Assert.assertEquals("Hello &amp;&lt;&gt; &Aring; &aring; &#x3c0; &#x65b0; there &frac34; &copy; &raquo;", escapedAscii);
        Assert.assertEquals("Hello &amp;&lt;&gt; &angst; &aring; &pi; &#x65b0; there &frac34; &copy; &raquo;", escapedAsciiFull);
        Assert.assertEquals("Hello &amp;&lt;&gt; &#xc5; &#xe5; &#x3c0; &#x65b0; there &#xbe; &#xa9; &#xbb;", escapedAsciiXhtml);
        Assert.assertEquals("Hello &amp;&lt;&gt; Å å π 新 there ¾ © »", escapedUtfFull);
        Assert.assertEquals("Hello &amp;&lt;&gt; Å å π 新 there ¾ © »", escapedUtfMin);
        // odd that it's defined as aring in base but angst in full

        // round trip
        Assert.assertEquals(text, Entities.unescape(escapedAscii));
        Assert.assertEquals(text, Entities.unescape(escapedAsciiFull));
        Assert.assertEquals(text, Entities.unescape(escapedAsciiXhtml));
        Assert.assertEquals(text, Entities.unescape(escapedUtfFull));
        Assert.assertEquals(text, Entities.unescape(escapedUtfMin));
    }

    @Test public void escapedSupplementary() {
        String text = "\uD835\uDD59";
        String escapedAscii = Entities.escape(text, new OutputSettings().charset("ascii").escapeMode(Entities.EscapeMode.base));
        Assert.assertEquals("&#x1d559;", escapedAscii);
        String escapedAsciiFull = Entities.escape(text, new OutputSettings().charset("ascii").escapeMode(Entities.EscapeMode.extended));
        Assert.assertEquals("&hopf;", escapedAsciiFull);
        String escapedUtf= Entities.escape(text, new OutputSettings().charset("UTF-8").escapeMode(Entities.EscapeMode.extended));
        Assert.assertEquals(text, escapedUtf);
    }

    @Test public void unescapeMultiChars() {
        String text = "&NestedGreaterGreater; &nGg; &nGt; &nGtv; &Gt; &gg;"; // gg is not combo, but 8811 could conflict with NestedGreaterGreater or others
        String un = "≫ ⋙̸ ≫⃒ ≫̸ ≫ ≫";
        Assert.assertEquals(un, Entities.unescape(text));
        String escaped = Entities.escape(un, new OutputSettings().charset("ascii").escapeMode(Entities.EscapeMode.extended));
        Assert.assertEquals("&Gt; &Gg;&#x338; &Gt;&#x20d2; &Gt;&#x338; &Gt; &Gt;", escaped);
        Assert.assertEquals(un, Entities.unescape(escaped));
    }

    @Test public void xhtml() {
        Assert.assertEquals(38, xhtml.codepointForName("amp"));
        Assert.assertEquals(62, xhtml.codepointForName("gt"));
        Assert.assertEquals(60, xhtml.codepointForName("lt"));
        Assert.assertEquals(34, xhtml.codepointForName("quot"));

        Assert.assertEquals("amp", xhtml.nameForCodepoint(38));
        Assert.assertEquals("gt", xhtml.nameForCodepoint(62));
        Assert.assertEquals("lt", xhtml.nameForCodepoint(60));
        Assert.assertEquals("quot", xhtml.nameForCodepoint(34));
    }

    @Test public void getByName() {
        Assert.assertEquals("≫⃒", Entities.getByName("nGt"));
        Assert.assertEquals("fj", Entities.getByName("fjlig"));
        Assert.assertEquals("≫", Entities.getByName("gg"));
        Assert.assertEquals("©", Entities.getByName("copy"));
    }

    @Test public void escapeSupplementaryCharacter() {
        String text = new String(Character.toChars(135361));
        String escapedAscii = Entities.escape(text, new OutputSettings().charset("ascii").escapeMode(Entities.EscapeMode.base));
        Assert.assertEquals("&#x210c1;", escapedAscii);
        String escapedUtf = Entities.escape(text, new OutputSettings().charset("UTF-8").escapeMode(Entities.EscapeMode.base));
        Assert.assertEquals(text, escapedUtf);
    }

    @Test public void notMissingMultis() {
        String text = "&nparsl;";
        String un = "\u2AFD\u20E5";
        Assert.assertEquals(un, Entities.unescape(text));
    }

    @Test public void notMissingSupplementals() {
        String text = "&npolint; &qfr;";
        String un = "⨔ \uD835\uDD2E"; // 𝔮
        Assert.assertEquals(un, Entities.unescape(text));
    }

    @Test public void unescape() {
        String text = "Hello &AElig; &amp;&LT&gt; &reg &angst; &angst &#960; &#960 &#x65B0; there &! &frac34; &copy; &COPY;";
        Assert.assertEquals("Hello Æ &<> ® Å &angst π π 新 there &! ¾ © ©", Entities.unescape(text));

        Assert.assertEquals("&0987654321; &unknown", Entities.unescape("&0987654321; &unknown"));
    }

    @Test public void strictUnescape() { // for attributes, enforce strict unescaping (must look like &#xxx; , not just &#xxx)
        String text = "Hello &amp= &amp;";
        Assert.assertEquals("Hello &amp= &", Entities.unescape(text, true));
        Assert.assertEquals("Hello &= &", Entities.unescape(text));
        Assert.assertEquals("Hello &= &", Entities.unescape(text, false));
    }


    @Test public void caseSensitive() {
        String unescaped = "Ü ü & &";
        Assert.assertEquals("&Uuml; &uuml; &amp; &amp;",
                Entities.escape(unescaped, new OutputSettings().charset("ascii").escapeMode(Entities.EscapeMode.extended)));

        String escaped = "&Uuml; &uuml; &amp; &AMP";
        Assert.assertEquals("Ü ü & &", Entities.unescape(escaped));
    }

    @Test public void quoteReplacements() {
        String escaped = "&#92; &#36;";
        String unescaped = "\\ $";

        Assert.assertEquals(unescaped, Entities.unescape(escaped));
    }

    @Test public void letterDigitEntities() {
        String html = "<p>&sup1;&sup2;&sup3;&frac14;&frac12;&frac34;</p>";
        Document doc = Jsoup.parse(html);
        doc.outputSettings().charset("ascii");
        Element p = doc.select("p").first();
        Assert.assertEquals("&sup1;&sup2;&sup3;&frac14;&frac12;&frac34;", p.html());
        Assert.assertEquals("¹²³¼½¾", p.text());
        doc.outputSettings().charset("UTF-8");
        Assert.assertEquals("¹²³¼½¾", p.html());
    }

    @Test public void noSpuriousDecodes() {
        String string = "http://www.foo.com?a=1&num_rooms=1&children=0&int=VA&b=2";
        Assert.assertEquals(string, Entities.unescape(string));
    }

    @Test public void escapesGtInXmlAttributesButNotInHtml() {
        // https://github.com/jhy/jsoup/issues/528 - < is OK in HTML attribute values, but not in XML


        String docHtml = "<a title='<p>One</p>'>One</a>";
        Document doc = Jsoup.parse(docHtml);
        Element element = doc.select("a").first();

        doc.outputSettings().escapeMode(Entities.EscapeMode.base);
        Assert.assertEquals("<a title=\"<p>One</p>\">One</a>", element.outerHtml());

        doc.outputSettings().escapeMode(Entities.EscapeMode.xhtml);
        Assert.assertEquals("<a title=\"&lt;p>One&lt;/p>\">One</a>", element.outerHtml());
    }
}
