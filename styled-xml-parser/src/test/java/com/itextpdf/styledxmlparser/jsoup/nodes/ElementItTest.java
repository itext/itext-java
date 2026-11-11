/*
    This file is part of jsoup, see NOTICE.txt in the root of the repository.
    It may contain modifications beyond the original version.
*/

package com.itextpdf.styledxmlparser.jsoup.nodes;

import com.itextpdf.styledxmlparser.jsoup.Jsoup;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.List;

@Category(UnitTest.class)
public class ElementItTest extends ExtendedITextTest {
    @Test
    public void testFastReparent() {
        StringBuilder htmlBuf = new StringBuilder();
        int rows = 300000;
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
        Assert.assertEquals(rows, wrapper.childNodes.size());
        Assert.assertEquals(rows, childNodes.size()); // child nodes is a wrapper, so still there
        Assert.assertEquals(0, doc.body().childNodes().size()); // but on a fresh look, all gone

        ((Element) doc.body().empty()).appendChild(wrapper);
        Element wrapperAcutal = doc.body().children().get(0);
        Assert.assertEquals(wrapper, wrapperAcutal);
        Assert.assertEquals("El-1", wrapperAcutal.children().get(0).text());
        Assert.assertEquals("El-" + rows, wrapperAcutal.children().get(rows - 1).text());
        Assert.assertTrue(runtime <= 10000);
    }

    @Test
    public void testFastReparentExistingContent() {
        StringBuilder htmlBuf = new StringBuilder();
        int rows = 300000;
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
        Assert.assertEquals(2, wrapper.childNodes.size());

        List<Node> childNodes = doc.body().childNodes();
        wrapper.insertChildren(1, childNodes);

        long runtime = (System.nanoTime() - start) / 1000000;
        Assert.assertEquals(rows + 2, wrapper.childNodes.size());
        Assert.assertEquals(rows, childNodes.size()); // child nodes is a wrapper, so still there
        Assert.assertEquals(0, doc.body().childNodes().size()); // but on a fresh look, all gone

        ((Element) doc.body().empty()).appendChild(wrapper);
        Element wrapperAcutal = doc.body().children().get(0);
        Assert.assertEquals(wrapper, wrapperAcutal);
        Assert.assertEquals("Prior Content", wrapperAcutal.children().get(0).text());
        Assert.assertEquals("El-1", wrapperAcutal.children().get(1).text());

        Assert.assertEquals("El-" + rows, wrapperAcutal.children().get(rows).text());
        Assert.assertEquals("End Content", wrapperAcutal.children().get(rows + 1).text());

        Assert.assertTrue(runtime <= 10000);
    }
}
