/*
    This file is part of jsoup, see NOTICE.txt in the root of the repository.
    It may contain modifications beyond the original version.
*/
package com.itextpdf.styledxmlparser.jsoup.nodes;

import com.itextpdf.styledxmlparser.jsoup.Jsoup;
import com.itextpdf.styledxmlparser.jsoup.select.Elements;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.util.List;

@Tag("UnitTest")
public class ElementItTest extends ExtendedITextTest {
    @Test
    public void testFastReparent() {
        StringBuilder htmlBuf = new StringBuilder();
        int rows = 30000;
        for (int i = 1; i <= rows; i++) {
            htmlBuf
                .append("<p>El-")
                .append(i)
                .append("</p>");
        }
        String html = htmlBuf.toString();
        Document doc = Jsoup.parse(html);
        long start = System.nanoTime();

        Element wrapper = new Element("div");
        List<Node> childNodes = doc.body().childNodes();
        wrapper.insertChildren(0, childNodes);

        long runtime = (System.nanoTime() - start) / 1000000;
        Assertions.assertEquals(rows, wrapper.childNodes.size());
        Assertions.assertEquals(rows, childNodes.size()); // child nodes is a wrapper, so still there
        Assertions.assertEquals(0, doc.body().childNodes().size()); // but on a fresh look, all gone

        ((Element) doc.body().empty()).appendChild(wrapper);
        Element wrapperActual = doc.body().children().get(0);
        Assertions.assertEquals(wrapper, wrapperActual);
        Assertions.assertEquals("El-1", wrapperActual.children().get(0).text());
        Assertions.assertEquals("El-" + rows, wrapperActual.children().get(rows - 1).text());
        Assertions.assertTrue(runtime <= 1000);
    }

    @Test
    public void testFastReparentExistingContent() {
        StringBuilder htmlBuf = new StringBuilder();
        int rows = 30000;
        for (int i = 1; i <= rows; i++) {
            htmlBuf
                .append("<p>El-")
                .append(i)
                .append("</p>");
        }
        String html = htmlBuf.toString();
        Document doc = Jsoup.parse(html);
        long start = System.nanoTime();

        Element wrapper = new Element("div");
        wrapper.append("<p>Prior Content</p>");
        wrapper.append("<p>End Content</p>");
        Assertions.assertEquals(2, wrapper.childNodes.size());

        Element docBody = doc.body();
        List<Node> childNodes = docBody.childNodes();
        wrapper.insertChildren(1, childNodes);

        long runtime = (System.nanoTime() - start) / 1000000;
        Assertions.assertEquals(rows + 2, wrapper.childNodes.size());
        Assertions.assertEquals(rows, childNodes.size()); // child nodes is a wrapper, so still there

        Assertions.assertEquals(0, docBody.childNodes().size()); // but on a fresh look, all gone
        ((Element) docBody.empty()).appendChild(wrapper);
        Element wrapperAcutal = doc.body().children().get(0);
        Assertions.assertEquals(wrapper, wrapperAcutal);
        Elements children = wrapperAcutal.children();
        Assertions.assertEquals("Prior Content", children.get(0).text());
        Assertions.assertEquals("El-1", children.get(1).text());

        Assertions.assertEquals("El-" + rows, children.get(rows).text());
        Assertions.assertEquals("End Content", children.get(rows + 1).text());

        Assertions.assertTrue(runtime <= 1000);
    }
}
