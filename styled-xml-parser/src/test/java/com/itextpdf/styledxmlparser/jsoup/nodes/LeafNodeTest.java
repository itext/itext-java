/*
    This file is part of jsoup, see NOTICE.txt in the root of the repository.
    It may contain modifications beyond the original version.
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
