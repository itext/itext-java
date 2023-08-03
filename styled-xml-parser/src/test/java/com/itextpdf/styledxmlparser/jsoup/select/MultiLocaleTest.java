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
package com.itextpdf.styledxmlparser.jsoup.select;

import com.itextpdf.styledxmlparser.jsoup.Jsoup;
import com.itextpdf.styledxmlparser.jsoup.nodes.Document;
import com.itextpdf.styledxmlparser.jsoup.nodes.Element;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;

@RunWith(Parameterized.class)
@Category(UnitTest.class)
public class MultiLocaleTest extends ExtendedITextTest {

    private final Locale defaultLocale = Locale.getDefault();

    @Parameterized.Parameters
    public static Collection<Locale> locales() {
        return Arrays.asList(Locale.ENGLISH, new Locale("tr"));
    }

    @After
    public void setDefaultLocale() {
        Locale.setDefault(defaultLocale);
    }

    private Locale locale;

    public MultiLocaleTest(Locale locale) {
        this.locale = locale;
    }

    @Test
    public void testByAttribute() {
        Locale.setDefault(locale);

        String h = "<div Title=Foo /><div Title=Bar /><div Style=Qux /><div title=Balim /><div title=SLIM />" +
                "<div data-name='with spaces'/>";
        Document doc = Jsoup.parse(h);

        Elements withTitle = doc.select("[title]");
        Assert.assertEquals(4, withTitle.size());

        Elements foo = doc.select("[TITLE=foo]");
        Assert.assertEquals(1, foo.size());

        Elements foo2 = doc.select("[title=\"foo\"]");
        Assert.assertEquals(1, foo2.size());

        Elements foo3 = doc.select("[title=\"Foo\"]");
        Assert.assertEquals(1, foo3.size());

        Elements dataName = doc.select("[data-name=\"with spaces\"]");
        Assert.assertEquals(1, dataName.size());
        Assert.assertEquals("with spaces", dataName.first().attr("data-name"));

        Elements not = doc.select("div[title!=bar]");
        Assert.assertEquals(5, not.size());
        Assert.assertEquals("Foo", not.first().attr("title"));

        Elements starts = doc.select("[title^=ba]");
        Assert.assertEquals(2, starts.size());
        Assert.assertEquals("Bar", starts.first().attr("title"));
        Assert.assertEquals("Balim", starts.last().attr("title"));

        Elements ends = doc.select("[title$=im]");
        Assert.assertEquals(2, ends.size());
        Assert.assertEquals("Balim", ends.first().attr("title"));
        Assert.assertEquals("SLIM", ends.last().attr("title"));

        Elements contains = doc.select("[title*=i]");
        Assert.assertEquals(2, contains.size());
        Assert.assertEquals("Balim", contains.first().attr("title"));
        Assert.assertEquals("SLIM", contains.last().attr("title"));
    }

    @Test
    public void testPseudoContains() {
        Locale.setDefault(locale);

        Document doc = Jsoup.parse("<div><p>The Rain.</p> <p class=light>The <i>RAIN</i>.</p> <p>Rain, the.</p></div>");

        Elements ps1 = doc.select("p:contains(Rain)");
        Assert.assertEquals(3, ps1.size());

        Elements ps2 = doc.select("p:contains(the rain)");
        Assert.assertEquals(2, ps2.size());
        Assert.assertEquals("The Rain.", ps2.first().html());
        Assert.assertEquals("The <i>RAIN</i>.", ps2.last().html());

        Elements ps3 = doc.select("p:contains(the Rain):has(i)");
        Assert.assertEquals(1, ps3.size());
        Assert.assertEquals("light", ps3.first().className());

        Elements ps4 = doc.select(".light:contains(rain)");
        Assert.assertEquals(1, ps4.size());
        Assert.assertEquals("light", ps3.first().className());

        Elements ps5 = doc.select(":contains(rain)");
        Assert.assertEquals(8, ps5.size()); // html, body, div,...

        Elements ps6 = doc.select(":contains(RAIN)");
        Assert.assertEquals(8, ps6.size());
    }

    @Test
    public void containsOwn() {
        Locale.setDefault(locale);

        Document doc = Jsoup.parse("<p id=1>Hello <b>there</b> igor</p>");
        Elements ps = doc.select("p:containsOwn(Hello IGOR)");
        Assert.assertEquals(1, ps.size());
        Assert.assertEquals("1", ps.first().id());

        Assert.assertEquals(0, doc.select("p:containsOwn(there)").size());

        Document doc2 = Jsoup.parse("<p>Hello <b>there</b> IGOR</p>");
        Assert.assertEquals(1, doc2.select("p:containsOwn(igor)").size());

    }

    @Test
    public void containsData() {
        Locale.setDefault(locale);

        String html = "<p>function</p><script>FUNCTION</script><style>item</style><span><!-- comments --></span>";
        Document doc = Jsoup.parse(html);
        Element body = doc.body();

        Elements dataEls1 = body.select(":containsData(function)");
        Elements dataEls2 = body.select("script:containsData(function)");
        Elements dataEls3 = body.select("span:containsData(comments)");
        Elements dataEls4 = body.select(":containsData(o)");
        Elements dataEls5 = body.select("style:containsData(ITEM)");

        Assert.assertEquals(2, dataEls1.size()); // body and script
        Assert.assertEquals(1, dataEls2.size());
        Assert.assertEquals(dataEls1.last(), dataEls2.first());
        Assert.assertEquals("<script>FUNCTION</script>", dataEls2.outerHtml());
        Assert.assertEquals(1, dataEls3.size());
        Assert.assertEquals("span", dataEls3.first().tagName());
        Assert.assertEquals(3, dataEls4.size());
        Assert.assertEquals("body", dataEls4.first().tagName());
        Assert.assertEquals("script", dataEls4.get(1).tagName());
        Assert.assertEquals("span", dataEls4.get(2).tagName());
        Assert.assertEquals(1, dataEls5.size());
    }

    @Test
    public void testByAttributeStarting() {
        Locale.setDefault(locale);

        Document doc = Jsoup.parse("<div id=1 ATTRIBUTE data-name=jsoup>Hello</div><p data-val=5 id=2>There</p><p id=3>No</p>");
        Elements withData = doc.select("[^data-]");
        Assert.assertEquals(2, withData.size());
        Assert.assertEquals("1", withData.first().id());
        Assert.assertEquals("2", withData.last().id());

        withData = doc.select("p[^data-]");
        Assert.assertEquals(1, withData.size());
        Assert.assertEquals("2", withData.first().id());

        Assert.assertEquals(1, doc.select("[^attrib]").size());
    }
}
