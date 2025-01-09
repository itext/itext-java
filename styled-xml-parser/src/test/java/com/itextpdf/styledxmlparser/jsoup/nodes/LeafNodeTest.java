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
package com.itextpdf.styledxmlparser.jsoup.nodes;

import com.itextpdf.styledxmlparser.jsoup.Jsoup;
import com.itextpdf.styledxmlparser.jsoup.select.Elements;
import com.itextpdf.styledxmlparser.jsoup.select.NodeFilter;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class LeafNodeTest extends ExtendedITextTest {

    @Test
    public void doesNotGetAttributesTooEasily() {
        // test to make sure we're not setting attributes on all nodes right away
        String body = "<p>One <!-- Two --> Three<![CDATA[Four]]></p>";
        Document doc = Jsoup.parse(body);
        Assertions.assertTrue(hasAnyAttributes(doc)); // should have one - the base uri on the doc

        Element html = doc.child(0);
        Assertions.assertFalse(hasAnyAttributes(html));

        String s = doc.outerHtml();
        Assertions.assertFalse(hasAnyAttributes(html));

        Elements els = doc.select("p");
        Element p = els.first();
        Assertions.assertEquals(1, els.size());
        Assertions.assertFalse(hasAnyAttributes(html));

        els = doc.select("p.none");
        Assertions.assertFalse(hasAnyAttributes(html));

        String id = p.id();
        Assertions.assertEquals("", id);
        Assertions.assertFalse(p.hasClass("Foobs"));
        Assertions.assertFalse(hasAnyAttributes(html));

        p.addClass("Foobs");
        Assertions.assertTrue(p.hasClass("Foobs"));
        Assertions.assertTrue(hasAnyAttributes(html));
        Assertions.assertTrue(hasAnyAttributes(p));

        Attributes attributes = p.attributes();
        Assertions.assertTrue(attributes.hasKey("class"));
        p.clearAttributes();
        Assertions.assertFalse(hasAnyAttributes(p));
        Assertions.assertFalse(hasAnyAttributes(html));
        Assertions.assertFalse(attributes.hasKey("class"));
    }

    private boolean hasAnyAttributes(Node node) {
        final boolean[] found = new boolean[1];
        node.filter(new NodeFilter() {
            @Override
            public FilterResult head(Node node, int depth) {
                if (node.hasAttributes()) {
                    found[0] = true;
                    return FilterResult.STOP;
                } else {
                    return FilterResult.CONTINUE;
                }
            }

            @Override
            public FilterResult tail(Node node, int depth) {
                return FilterResult.CONTINUE;
            }
        });
        return found[0];
    }
}
