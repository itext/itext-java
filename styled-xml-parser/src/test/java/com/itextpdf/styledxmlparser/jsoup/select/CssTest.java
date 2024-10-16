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
package com.itextpdf.styledxmlparser.jsoup.select;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.styledxmlparser.jsoup.Jsoup;
import com.itextpdf.styledxmlparser.jsoup.nodes.Document;
import com.itextpdf.styledxmlparser.jsoup.parser.Tag;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


@org.junit.jupiter.api.Tag("UnitTest")
public class CssTest extends ExtendedITextTest {

	private Document html = null;
	private static String htmlString;

	@BeforeAll
	public static void initClass() {
		StringBuilder sb = new StringBuilder("<html><head></head><body>");

		sb.append("<div id='pseudo'>");
		for (int i = 1; i <= 10; i++) {
			sb.append(MessageFormatUtil.format("<p>{0}</p>",i));
		}
		sb.append("</div>");

		sb.append("<div id='type'>");
		for (int i = 1; i <= 10; i++) {
			sb.append(MessageFormatUtil.format("<p>{0}</p>",i));
			sb.append(MessageFormatUtil.format("<span>{0}</span>",i));
			sb.append(MessageFormatUtil.format("<em>{0}</em>",i));
            sb.append(MessageFormatUtil.format("<svg>{0}</svg>",i));
		}
		sb.append("</div>");

		sb.append("<span id='onlySpan'><br /></span>");
		sb.append("<p class='empty'><!-- Comment only is still empty! --></p>");

		sb.append("<div id='only'>");
		sb.append("Some text before the <em>only</em> child in this div");
		sb.append("</div>");

		sb.append("</body></html>");
		htmlString = sb.toString();
	}

	@BeforeEach
	public void init() {
		html  = Jsoup.parse(htmlString);
	}

	@Test
	public void firstChild() {
		check(html.select("#pseudo :first-child"), "1");
		check(html.select("html:first-child"));
	}

	@Test
	public void lastChild() {
		check(html.select("#pseudo :last-child"), "10");
		check(html.select("html:last-child"));
	}

	@Test
	public void nthChild_simple() {
		for(int i = 1; i <=10; i++) {
			check(html.select(MessageFormatUtil.format("#pseudo :nth-child({0})", i)), String.valueOf(i));
		}
	}

    @Test
    public void nthOfType_unknownTag() {
        for(int i = 1; i <=10; i++) {
            check(html.select(MessageFormatUtil.format("#type svg:nth-of-type({0})", i)), String.valueOf(i));
        }
    }

	@Test
	public void nthLastChild_simple() {
		for(int i = 1; i <=10; i++) {
			check(html.select(MessageFormatUtil.format("#pseudo :nth-last-child({0})", i)), String.valueOf(11-i));
		}
	}

	@Test
	public void nthOfType_simple() {
		for(int i = 1; i <=10; i++) {
			check(html.select(MessageFormatUtil.format("#type p:nth-of-type({0})", i)), String.valueOf(i));
		}
	}

	@Test
	public void nthLastOfType_simple() {
		for(int i = 1; i <=10; i++) {
			check(html.select(MessageFormatUtil.format("#type :nth-last-of-type({0})", i)), String.valueOf(11-i),String.valueOf(11-i),String.valueOf(11-i),String.valueOf(11-i));
		}
	}

	@Test
	public void nthChild_advanced() {
		check(html.select("#pseudo :nth-child(-5)"));
		check(html.select("#pseudo :nth-child(odd)"), "1", "3", "5", "7", "9");
		check(html.select("#pseudo :nth-child(2n-1)"), "1", "3", "5", "7", "9");
		check(html.select("#pseudo :nth-child(2n+1)"), "1", "3", "5", "7", "9");
		check(html.select("#pseudo :nth-child(2n+3)"), "3", "5", "7", "9");
		check(html.select("#pseudo :nth-child(even)"), "2", "4", "6", "8", "10");
		check(html.select("#pseudo :nth-child(2n)"), "2", "4", "6", "8", "10");
		check(html.select("#pseudo :nth-child(3n-1)"), "2", "5", "8");
		check(html.select("#pseudo :nth-child(-2n+5)"), "1", "3", "5");
		check(html.select("#pseudo :nth-child(+5)"), "5");
	}

	@Test
	public void nthOfType_advanced() {
		check(html.select("#type :nth-of-type(-5)"));
		check(html.select("#type p:nth-of-type(odd)"), "1", "3", "5", "7", "9");
		check(html.select("#type em:nth-of-type(2n-1)"), "1", "3", "5", "7", "9");
		check(html.select("#type p:nth-of-type(2n+1)"), "1", "3", "5", "7", "9");
		check(html.select("#type span:nth-of-type(2n+3)"), "3", "5", "7", "9");
		check(html.select("#type p:nth-of-type(even)"), "2", "4", "6", "8", "10");
		check(html.select("#type p:nth-of-type(2n)"), "2", "4", "6", "8", "10");
		check(html.select("#type p:nth-of-type(3n-1)"), "2", "5", "8");
		check(html.select("#type p:nth-of-type(-2n+5)"), "1", "3", "5");
		check(html.select("#type :nth-of-type(+5)"), "5", "5", "5", "5");
	}


	@Test
	public void nthLastChild_advanced() {
		check(html.select("#pseudo :nth-last-child(-5)"));
		check(html.select("#pseudo :nth-last-child(odd)"), "2", "4", "6", "8", "10");
		check(html.select("#pseudo :nth-last-child(2n-1)"), "2", "4", "6", "8", "10");
		check(html.select("#pseudo :nth-last-child(2n+1)"), "2", "4", "6", "8", "10");
		check(html.select("#pseudo :nth-last-child(2n+3)"), "2", "4", "6", "8");
		check(html.select("#pseudo :nth-last-child(even)"), "1", "3", "5", "7", "9");
		check(html.select("#pseudo :nth-last-child(2n)"), "1", "3", "5", "7", "9");
		check(html.select("#pseudo :nth-last-child(3n-1)"), "3", "6", "9");

		check(html.select("#pseudo :nth-last-child(-2n+5)"), "6", "8", "10");
		check(html.select("#pseudo :nth-last-child(+5)"), "6");
	}

	@Test
	public void nthLastOfType_advanced() {
		check(html.select("#type :nth-last-of-type(-5)"));
		check(html.select("#type p:nth-last-of-type(odd)"), "2", "4", "6", "8", "10");
		check(html.select("#type em:nth-last-of-type(2n-1)"), "2", "4", "6", "8", "10");
		check(html.select("#type p:nth-last-of-type(2n+1)"), "2", "4", "6", "8", "10");
		check(html.select("#type span:nth-last-of-type(2n+3)"), "2", "4", "6", "8");
		check(html.select("#type p:nth-last-of-type(even)"), "1", "3", "5", "7", "9");
		check(html.select("#type p:nth-last-of-type(2n)"), "1", "3", "5", "7", "9");
		check(html.select("#type p:nth-last-of-type(3n-1)"), "3", "6", "9");

		check(html.select("#type span:nth-last-of-type(-2n+5)"), "6", "8", "10");
		check(html.select("#type :nth-last-of-type(+5)"), "6", "6", "6", "6");
	}

	@Test
	public void firstOfType() {
		check(html.select("div:not(#only) :first-of-type"), "1", "1", "1", "1", "1");
	}

	@Test
	public void lastOfType() {
		check(html.select("div:not(#only) :last-of-type"), "10", "10", "10", "10", "10");
	}

	@Test
	public void empty() {
		final Elements sel = html.select(":empty");
		Assertions.assertEquals(3, sel.size());
	    Assertions.assertEquals("head", sel.get(0).tagName());
		Assertions.assertEquals("br", sel.get(1).tagName());
		Assertions.assertEquals("p", sel.get(2).tagName());
	}

	@Test
	public void onlyChild() {
		final Elements sel = html.select("span :only-child");
		Assertions.assertEquals(1, sel.size());
		Assertions.assertEquals("br", sel.get(0).tagName());

		check(html.select("#only :only-child"), "only");
	}

	@Test
	public void onlyOfType() {
		final Elements sel = html.select(":only-of-type");
		Assertions.assertEquals(6, sel.size());
		Assertions.assertEquals("head", sel.get(0).tagName());
		Assertions.assertEquals("body", sel.get(1).tagName());
		Assertions.assertEquals("span", sel.get(2).tagName());
		Assertions.assertEquals("br", sel.get(3).tagName());
		Assertions.assertEquals("p", sel.get(4).tagName());
		Assertions.assertTrue(sel.get(4).hasClass("empty"));
		Assertions.assertEquals("em", sel.get(5).tagName());
	}

	protected void check(Elements result, String...expectedContent ) {
		Assertions.assertEquals(expectedContent.length, result.size());
		for (int i = 0; i < expectedContent.length; i++) {
			Assertions.assertNotNull(result.get(i));
			Assertions.assertEquals(expectedContent[i], result.get(i).ownText());
		}
	}

	@Test
	public void root() {
		Elements sel = html.select(":root");
		Assertions.assertEquals(1, sel.size());
		Assertions.assertNotNull(sel.get(0));
		Assertions.assertEquals(Tag.valueOf("html"), sel.get(0).tag());

		Elements sel2 = html.select("body").select(":root");
		Assertions.assertEquals(1, sel2.size());
		Assertions.assertNotNull(sel2.get(0));
		Assertions.assertEquals(Tag.valueOf("body"), sel2.get(0).tag());
	}

}
