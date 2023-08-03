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

@Category(UnitTest.class)
public class AttributeTest extends ExtendedITextTest {
    @Test
    public void html() {
        Attribute attr = new Attribute("key", "value &");
        Assert.assertEquals("key=\"value &amp;\"", attr.html());
        Assert.assertEquals(attr.html(), attr.toString());
    }

    @Test public void testWithSupplementaryCharacterInAttributeKeyAndValue() {
        String s = new String(Character.toChars(135361));
        Attribute attr = new Attribute(s, "A" + s + "B");
        Assert.assertEquals(s + "=\"A" + s + "B\"", attr.html());
        Assert.assertEquals(attr.html(), attr.toString());
    }

    @Test public void validatesKeysNotEmpty() {
        Assert.assertThrows(IllegalArgumentException.class, () -> new Attribute(" ", "Check"));
    }

    @Test public void validatesKeysNotEmptyViaSet() {
        Assert.assertThrows(IllegalArgumentException.class, () -> {
            Attribute attr = new Attribute("One", "Check");
            attr.setKey(" ");
        });
    }

    @Test public void booleanAttributesAreEmptyStringValues() {
        Document doc = Jsoup.parse("<div hidden>");
        Attributes attributes = doc.body().child(0).attributes();
        Assert.assertEquals("", attributes.get("hidden"));

        Attribute first = attributes.iterator().next();
        Assert.assertEquals("hidden", first.getKey());
        Assert.assertEquals("", first.getValue());
        Assert.assertFalse(first.hasDeclaredValue());
        Assert.assertTrue(Attribute.isBooleanAttribute(first.getKey()));
    }

    @Test public void settersOnOrphanAttribute() {
        Attribute attr = new Attribute("one", "two");
        attr.setKey("three");
        String oldVal = attr.setValue("four");
        Assert.assertEquals("two", oldVal);
        Assert.assertEquals("three", attr.getKey());
        Assert.assertEquals("four", attr.getValue());
        Assert.assertNull(attr.parent);
    }

    @Test public void hasValue() {
        Attribute a1 = new Attribute("one", "");
        Attribute a2 = new Attribute("two", null);
        Attribute a3 = new Attribute("thr", "thr");

        Assert.assertTrue(a1.hasDeclaredValue());
        Assert.assertFalse(a2.hasDeclaredValue());
        Assert.assertTrue(a3.hasDeclaredValue());
    }
}
