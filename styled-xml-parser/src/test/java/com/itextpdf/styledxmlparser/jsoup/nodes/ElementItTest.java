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
