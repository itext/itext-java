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
package com.itextpdf.styledxmlparser.jsoup.select;

import com.itextpdf.styledxmlparser.jsoup.Jsoup;
import com.itextpdf.styledxmlparser.jsoup.nodes.Document;
import com.itextpdf.styledxmlparser.jsoup.nodes.Element;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;

@Tag("UnitTest")
public class MultiLocaleTest extends ExtendedITextTest {

    private final Locale defaultLocale = Locale.getDefault();

    public static Collection<Locale> locales() {
        return Arrays.asList(Locale.ENGLISH, new Locale("tr"));
    }

    @AfterEach
    public void setDefaultLocale() {
        Locale.setDefault(defaultLocale);
    }

    @ParameterizedTest
    @MethodSource("locales")
    public void testByAttribute(Locale locale) {
        Locale.setDefault(locale);

        String h = "<div Title=Foo /><div Title=Bar /><div Style=Qux /><div title=Balim /><div title=SLIM />" +
                "<div data-name='with spaces'/>";
        Document doc = Jsoup.parse(h);

        Elements withTitle = doc.select("[title]");
        Assertions.assertEquals(4, withTitle.size());

        Elements foo = doc.select("[TITLE=foo]");
        Assertions.assertEquals(1, foo.size());

        Elements foo2 = doc.select("[title=\"foo\"]");
        Assertions.assertEquals(1, foo2.size());

        Elements foo3 = doc.select("[title=\"Foo\"]");
        Assertions.assertEquals(1, foo3.size());

        Elements dataName = doc.select("[data-name=\"with spaces\"]");
        Assertions.assertEquals(1, dataName.size());
        Assertions.assertEquals("with spaces", dataName.first().attr("data-name"));

        Elements not = doc.select("div[title!=bar]");
        Assertions.assertEquals(5, not.size());
        Assertions.assertEquals("Foo", not.first().attr("title"));

        Elements starts = doc.select("[title^=ba]");
        Assertions.assertEquals(2, starts.size());
        Assertions.assertEquals("Bar", starts.first().attr("title"));
        Assertions.assertEquals("Balim", starts.last().attr("title"));

        Elements ends = doc.select("[title$=im]");
        Assertions.assertEquals(2, ends.size());
        Assertions.assertEquals("Balim", ends.first().attr("title"));
        Assertions.assertEquals("SLIM", ends.last().attr("title"));

        Elements contains = doc.select("[title*=i]");
        Assertions.assertEquals(2, contains.size());
        Assertions.assertEquals("Balim", contains.first().attr("title"));
        Assertions.assertEquals("SLIM", contains.last().attr("title"));
    }

    @ParameterizedTest
    @MethodSource("locales")
    public void testPseudoContains(Locale locale) {
        Locale.setDefault(locale);

        Document doc = Jsoup.parse("<div><p>The Rain.</p> <p class=light>The <i>RAIN</i>.</p> <p>Rain, the.</p></div>");

        Elements ps1 = doc.select("p:contains(Rain)");
        Assertions.assertEquals(3, ps1.size());

        Elements ps2 = doc.select("p:contains(the rain)");
        Assertions.assertEquals(2, ps2.size());
        Assertions.assertEquals("The Rain.", ps2.first().html());
        Assertions.assertEquals("The <i>RAIN</i>.", ps2.last().html());

        Elements ps3 = doc.select("p:contains(the Rain):has(i)");
        Assertions.assertEquals(1, ps3.size());
        Assertions.assertEquals("light", ps3.first().className());

        Elements ps4 = doc.select(".light:contains(rain)");
        Assertions.assertEquals(1, ps4.size());
        Assertions.assertEquals("light", ps3.first().className());

        Elements ps5 = doc.select(":contains(rain)");
        Assertions.assertEquals(8, ps5.size()); // html, body, div,...

        Elements ps6 = doc.select(":contains(RAIN)");
        Assertions.assertEquals(8, ps6.size());
    }

    @ParameterizedTest
    @MethodSource("locales")
    public void containsOwn(Locale locale) {
        Locale.setDefault(locale);

        Document doc = Jsoup.parse("<p id=1>Hello <b>there</b> igor</p>");
        Elements ps = doc.select("p:containsOwn(Hello IGOR)");
        Assertions.assertEquals(1, ps.size());
        Assertions.assertEquals("1", ps.first().id());

        Assertions.assertEquals(0, doc.select("p:containsOwn(there)").size());

        Document doc2 = Jsoup.parse("<p>Hello <b>there</b> IGOR</p>");
        Assertions.assertEquals(1, doc2.select("p:containsOwn(igor)").size());

    }

    @ParameterizedTest
    @MethodSource("locales")
    public void containsData(Locale locale) {
        Locale.setDefault(locale);

        String html = "<p>function</p><script>FUNCTION</script><style>item</style><span><!-- comments --></span>";
        Document doc = Jsoup.parse(html);
        Element body = doc.body();

        Elements dataEls1 = body.select(":containsData(function)");
        Elements dataEls2 = body.select("script:containsData(function)");
        Elements dataEls3 = body.select("span:containsData(comments)");
        Elements dataEls4 = body.select(":containsData(o)");
        Elements dataEls5 = body.select("style:containsData(ITEM)");

        Assertions.assertEquals(2, dataEls1.size()); // body and script
        Assertions.assertEquals(1, dataEls2.size());
        Assertions.assertEquals(dataEls1.last(), dataEls2.first());
        Assertions.assertEquals("<script>FUNCTION</script>", dataEls2.outerHtml());
        Assertions.assertEquals(1, dataEls3.size());
        Assertions.assertEquals("span", dataEls3.first().tagName());
        Assertions.assertEquals(3, dataEls4.size());
        Assertions.assertEquals("body", dataEls4.first().tagName());
        Assertions.assertEquals("script", dataEls4.get(1).tagName());
        Assertions.assertEquals("span", dataEls4.get(2).tagName());
        Assertions.assertEquals(1, dataEls5.size());
    }

    @ParameterizedTest
    @MethodSource("locales")
    public void testByAttributeStarting(Locale locale) {
        Locale.setDefault(locale);

        Document doc = Jsoup.parse("<div id=1 ATTRIBUTE data-name=jsoup>Hello</div><p data-val=5 id=2>There</p><p id=3>No</p>");
        Elements withData = doc.select("[^data-]");
        Assertions.assertEquals(2, withData.size());
        Assertions.assertEquals("1", withData.first().id());
        Assertions.assertEquals("2", withData.last().id());

        withData = doc.select("p[^data-]");
        Assertions.assertEquals(1, withData.size());
        Assertions.assertEquals("2", withData.first().id());

        Assertions.assertEquals(1, doc.select("[^attrib]").size());
    }
}
