/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 iText Group NV
    Authors: iText Software.

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

import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Tests for Attributes.
 *
 * @author Jonathan Hedley
 */
@Category(UnitTest.class)
public class AttributesTest extends ExtendedITextTest {

    @Test
    public void html() {
        Attributes a = new Attributes();
        a.put("Tot", "a&p");
        a.put("Hello", "There");
        a.put("data-name", "Jsoup");

        Assert.assertEquals(3, a.size());
        Assert.assertTrue(a.hasKey("Tot"));
        Assert.assertTrue(a.hasKey("Hello"));
        Assert.assertTrue(a.hasKey("data-name"));
        Assert.assertFalse(a.hasKey("tot"));
        Assert.assertTrue(a.hasKeyIgnoreCase("tot"));
        Assert.assertEquals("There", a.getIgnoreCase("hEllo"));

        Map<String, String> dataset = a.dataset();
        Assert.assertEquals(1, dataset.size());
        Assert.assertEquals("Jsoup", dataset.get("name"));
        Assert.assertEquals("", a.get("tot"));
        Assert.assertEquals("a&p", a.get("Tot"));
        Assert.assertEquals("a&p", a.getIgnoreCase("tot"));

        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", a.html());
        Assert.assertEquals(a.html(), a.toString());
    }

    @Test
    public void testIteratorUpdateable() {
        Attributes a = new Attributes();
        a.put("Tot", "a&p");
        a.put("Hello", "There");

        Assert.assertFalse(a.hasKey("Foo"));
        Iterator<Attribute> iterator = a.iterator();
        Assert.assertTrue(iterator.hasNext());
        Attribute attr = iterator.next();
        attr.setKey("Foo");
        attr = iterator.next();
        attr.setKey("Bar");
        attr.setValue("Qux");

        Assert.assertEquals("a&p", a.get("Foo"));
        Assert.assertEquals("Qux", a.get("Bar"));
        Assert.assertFalse(a.hasKey("Tot"));
        Assert.assertFalse(a.hasKey("Hello"));
    }

    @Test public void testIteratorHasNext() {
        Attributes a = new Attributes();
        a.put("Tot", "1");
        a.put("Hello", "2");
        a.put("data-name", "3");

        int seen = 0;
        for (Attribute attribute : a) {
            seen++;
            Assert.assertEquals(String.valueOf(seen), attribute.getValue());
        }
        Assert.assertEquals(3, seen);
    }

    @Test
    public void testIterator() {
        Attributes a = new Attributes();
        String[][] datas = {{"Tot", "raul"},
            {"Hello", "pismuth"},
            {"data-name", "Jsoup"}};
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }

        int i = 0;
        for (Attribute attribute : a) {
            Assert.assertEquals(datas[i][0], attribute.getKey());
            Assert.assertEquals(datas[i][1], attribute.getValue());
            i++;
        }
        Assert.assertEquals(datas.length, i);
    }

    @Test
    public void testIteratorSkipsInternal() {
        Attributes a = new Attributes();
        a.put("One", "One");
        a.put(Attributes.internalKey("baseUri"), "example.com");
        a.put("Two", "Two");
        a.put(Attributes.internalKey("another"), "example.com");

        Iterator<Attribute> it = a.iterator();
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals("One", it.next().getKey());
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals("Two", it.next().getKey());
        Assert.assertFalse(it.hasNext());

        int seen = 0;
        for (Attribute attribute : a) {
            seen++;
        }
        Assert.assertEquals(2, seen);
    }

    @Test
    public void testListSkipsInternal() {
        Attributes a = new Attributes();
        a.put("One", "One");
        a.put(Attributes.internalKey("baseUri"), "example.com");
        a.put("Two", "Two");
        a.put(Attributes.internalKey("another"), "example.com");

        List<Attribute> attributes = a.asList();
        Assert.assertEquals(2, attributes.size());
        Assert.assertEquals("One", attributes.get(0).getKey());
        Assert.assertEquals("Two", attributes.get(1). getKey());
    }

    @Test public void htmlSkipsInternals() {
        Attributes a = new Attributes();
        a.put("One", "One");
        a.put(Attributes.internalKey("baseUri"), "example.com");
        a.put("Two", "Two");
        a.put(Attributes.internalKey("another"), "example.com");

        Assert.assertEquals(" One=\"One\" Two=\"Two\"", a.html());
    }

    @Test
    public void testIteratorEmpty() {
        Attributes a = new Attributes();

        Iterator<Attribute> iterator = a.iterator();
        Assert.assertFalse(iterator.hasNext());
    }

    @Test
    public void removeCaseSensitive() {
        Attributes a = new Attributes();
        a.put("Tot", "a&p");
        a.put("tot", "one");
        a.put("Hello", "There");
        a.put("hello", "There");
        a.put("data-name", "Jsoup");

        Assert.assertEquals(5, a.size());
        a.remove("Tot");
        a.remove("Hello");
        Assert.assertEquals(3, a.size());
        Assert.assertTrue(a.hasKey("tot"));
        Assert.assertFalse(a.hasKey("Tot"));
    }

    @Test
    public void testSetKeyConsistency() {
        Attributes a = new Attributes();
        a.put("a", "a");
        for(Attribute at : a) {
            at.setKey("b");
        }
        Assert.assertFalse(a.hasKey("a"));
        Assert.assertTrue(a.hasKey("b"));
    }

    @Test
    public void testBoolean() {
        Attributes ats = new Attributes();
        ats.put("a", "a");
        ats.put("B", "b");
        ats.put("c", null);

        Assert.assertTrue(ats.hasDeclaredValueForKey("a"));
        Assert.assertFalse(ats.hasDeclaredValueForKey("A"));
        Assert.assertTrue(ats.hasDeclaredValueForKeyIgnoreCase("A"));

        Assert.assertFalse(ats.hasDeclaredValueForKey("c"));
        Assert.assertFalse(ats.hasDeclaredValueForKey("C"));
        Assert.assertFalse(ats.hasDeclaredValueForKeyIgnoreCase("C"));
    }

    @Test public void testSizeWhenHasInternal() {
        Attributes a = new Attributes();
        a.put("One", "One");
        a.put("Two", "Two");
        Assert.assertEquals(2, a.size());

        a.put(Attributes.internalKey("baseUri"), "example.com");
        a.put(Attributes.internalKey("another"), "example.com");
        Assert.assertEquals(2, a.size());
    }
}
