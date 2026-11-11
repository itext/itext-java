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
