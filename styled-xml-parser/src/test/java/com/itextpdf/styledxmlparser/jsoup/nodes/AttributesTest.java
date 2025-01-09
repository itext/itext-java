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

import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Tests for Attributes.
 */
@Tag("UnitTest")
public class AttributesTest extends ExtendedITextTest {

    @Test
    public void html() {
        Attributes a = new Attributes();
        a.put("Tot", "a&p");
        a.put("Hello", "There");
        a.put("data-name", "Jsoup");

        Assertions.assertEquals(3, a.size());
        Assertions.assertTrue(a.hasKey("Tot"));
        Assertions.assertTrue(a.hasKey("Hello"));
        Assertions.assertTrue(a.hasKey("data-name"));
        Assertions.assertFalse(a.hasKey("tot"));
        Assertions.assertTrue(a.hasKeyIgnoreCase("tot"));
        Assertions.assertEquals("There", a.getIgnoreCase("hEllo"));

        Map<String, String> dataset = a.dataset();
        Assertions.assertEquals(1, dataset.size());
        Assertions.assertEquals("Jsoup", dataset.get("name"));
        Assertions.assertEquals("", a.get("tot"));
        Assertions.assertEquals("a&p", a.get("Tot"));
        Assertions.assertEquals("a&p", a.getIgnoreCase("tot"));

        Assertions.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", a.html());
        Assertions.assertEquals(a.html(), a.toString());
    }

    @Test
    public void testIteratorUpdateable() {
        Attributes a = new Attributes();
        a.put("Tot", "a&p");
        a.put("Hello", "There");

        Assertions.assertFalse(a.hasKey("Foo"));
        Iterator<Attribute> iterator = a.iterator();
        Assertions.assertTrue(iterator.hasNext());
        Attribute attr = iterator.next();
        attr.setKey("Foo");
        attr = iterator.next();
        attr.setKey("Bar");
        attr.setValue("Qux");

        Assertions.assertEquals("a&p", a.get("Foo"));
        Assertions.assertEquals("Qux", a.get("Bar"));
        Assertions.assertFalse(a.hasKey("Tot"));
        Assertions.assertFalse(a.hasKey("Hello"));
    }

    @Test public void testIteratorHasNext() {
        Attributes a = new Attributes();
        a.put("Tot", "1");
        a.put("Hello", "2");
        a.put("data-name", "3");

        int seen = 0;
        for (Attribute attribute : a) {
            seen++;
            Assertions.assertEquals(String.valueOf(seen), attribute.getValue());
        }
        Assertions.assertEquals(3, seen);
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
            Assertions.assertEquals(datas[i][0], attribute.getKey());
            Assertions.assertEquals(datas[i][1], attribute.getValue());
            i++;
        }
        Assertions.assertEquals(datas.length, i);
    }

    @Test
    public void testIteratorSkipsInternal() {
        Attributes a = new Attributes();
        a.put("One", "One");
        a.put(Attributes.internalKey("baseUri"), "example.com");
        a.put("Two", "Two");
        a.put(Attributes.internalKey("another"), "example.com");

        Iterator<Attribute> it = a.iterator();
        Assertions.assertTrue(it.hasNext());
        Assertions.assertEquals("One", it.next().getKey());
        Assertions.assertTrue(it.hasNext());
        Assertions.assertEquals("Two", it.next().getKey());
        Assertions.assertFalse(it.hasNext());

        int seen = 0;
        for (Attribute attribute : a) {
            seen++;
        }
        Assertions.assertEquals(2, seen);
    }

    @Test
    public void testListSkipsInternal() {
        Attributes a = new Attributes();
        a.put("One", "One");
        a.put(Attributes.internalKey("baseUri"), "example.com");
        a.put("Two", "Two");
        a.put(Attributes.internalKey("another"), "example.com");

        List<Attribute> attributes = a.asList();
        Assertions.assertEquals(2, attributes.size());
        Assertions.assertEquals("One", attributes.get(0).getKey());
        Assertions.assertEquals("Two", attributes.get(1). getKey());
    }

    @Test public void htmlSkipsInternals() {
        Attributes a = new Attributes();
        a.put("One", "One");
        a.put(Attributes.internalKey("baseUri"), "example.com");
        a.put("Two", "Two");
        a.put(Attributes.internalKey("another"), "example.com");

        Assertions.assertEquals(" One=\"One\" Two=\"Two\"", a.html());
    }

    @Test
    public void testIteratorEmpty() {
        Attributes a = new Attributes();

        Iterator<Attribute> iterator = a.iterator();
        Assertions.assertFalse(iterator.hasNext());
    }

    @Test
    public void removeCaseSensitive() {
        Attributes a = new Attributes();
        a.put("Tot", "a&p");
        a.put("tot", "one");
        a.put("Hello", "There");
        a.put("hello", "There");
        a.put("data-name", "Jsoup");

        Assertions.assertEquals(5, a.size());
        a.remove("Tot");
        a.remove("Hello");
        Assertions.assertEquals(3, a.size());
        Assertions.assertTrue(a.hasKey("tot"));
        Assertions.assertFalse(a.hasKey("Tot"));
    }

    @Test
    public void testSetKeyConsistency() {
        Attributes a = new Attributes();
        a.put("a", "a");
        for(Attribute at : a) {
            at.setKey("b");
        }
        Assertions.assertFalse(a.hasKey("a"));
        Assertions.assertTrue(a.hasKey("b"));
    }

    @Test
    public void testBoolean() {
        Attributes ats = new Attributes();
        ats.put("a", "a");
        ats.put("B", "b");
        ats.put("c", null);

        Assertions.assertTrue(ats.hasDeclaredValueForKey("a"));
        Assertions.assertFalse(ats.hasDeclaredValueForKey("A"));
        Assertions.assertTrue(ats.hasDeclaredValueForKeyIgnoreCase("A"));

        Assertions.assertFalse(ats.hasDeclaredValueForKey("c"));
        Assertions.assertFalse(ats.hasDeclaredValueForKey("C"));
        Assertions.assertFalse(ats.hasDeclaredValueForKeyIgnoreCase("C"));
    }

    @Test public void testSizeWhenHasInternal() {
        Attributes a = new Attributes();
        a.put("One", "One");
        a.put("Two", "Two");
        Assertions.assertEquals(2, a.size());

        a.put(Attributes.internalKey("baseUri"), "example.com");
        a.put(Attributes.internalKey("another"), "example.com");
        Assertions.assertEquals(2, a.size());
    }
}
