/*
    This file is part of jsoup, see NOTICE.txt in the root of the repository.
    It may contain modifications beyond the original version.
*/
package com.itextpdf.styledxmlparser.jsoup.nodes;

import com.itextpdf.styledxmlparser.jsoup.Jsoup;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class AttributeTest extends ExtendedITextTest {
    @Test
    public void html() {
        Attribute attr = new Attribute("key", "value &");
        Assertions.assertEquals("key=\"value &amp;\"", attr.html());
        Assertions.assertEquals(attr.html(), attr.toString());
    }

    @Test public void testWithSupplementaryCharacterInAttributeKeyAndValue() {
        String s = new String(Character.toChars(135361));
        Attribute attr = new Attribute(s, "A" + s + "B");
        Assertions.assertEquals(s + "=\"A" + s + "B\"", attr.html());
        Assertions.assertEquals(attr.html(), attr.toString());
    }

    @Test public void validatesKeysNotEmpty() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Attribute(" ", "Check"));
    }

    @Test public void validatesKeysNotEmptyViaSet() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Attribute attr = new Attribute("One", "Check");
            attr.setKey(" ");
        });
    }

    @Test public void booleanAttributesAreEmptyStringValues() {
        Document doc = Jsoup.parse("<div hidden>");
        Attributes attributes = doc.body().child(0).attributes();
        Assertions.assertEquals("", attributes.get("hidden"));

        Attribute first = attributes.iterator().next();
        Assertions.assertEquals("hidden", first.getKey());
        Assertions.assertEquals("", first.getValue());
        Assertions.assertFalse(first.hasDeclaredValue());
        Assertions.assertTrue(Attribute.isBooleanAttribute(first.getKey()));
    }

    @Test public void settersOnOrphanAttribute() {
        Attribute attr = new Attribute("one", "two");
        attr.setKey("three");
        String oldVal = attr.setValue("four");
        Assertions.assertEquals("two", oldVal);
        Assertions.assertEquals("three", attr.getKey());
        Assertions.assertEquals("four", attr.getValue());
        Assertions.assertNull(attr.parent);
    }

    @Test public void hasValue() {
        Attribute a1 = new Attribute("one", "");
        Attribute a2 = new Attribute("two", null);
        Attribute a3 = new Attribute("thr", "thr");

        Assertions.assertTrue(a1.hasDeclaredValue());
        Assertions.assertFalse(a2.hasDeclaredValue());
        Assertions.assertTrue(a3.hasDeclaredValue());
    }
}
