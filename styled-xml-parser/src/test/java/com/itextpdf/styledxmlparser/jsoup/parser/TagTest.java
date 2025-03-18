/*
    This file is part of jsoup, see NOTICE.txt in the root of the repository.
    It may contain modifications beyond the original version.
*/
package com.itextpdf.styledxmlparser.jsoup.parser;

import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 Tag tests.
*/
@org.junit.jupiter.api.Tag("UnitTest")
public class TagTest extends ExtendedITextTest {
    @Test public void isCaseSensitive() {
        Tag p1 = Tag.valueOf("P");
        Tag p2 = Tag.valueOf("p");
        Assertions.assertNotEquals(p1, p2);
    }

    @Test public void trims() {
        Tag p1 = Tag.valueOf("p");
        Tag p2 = Tag.valueOf(" p ");
        Assertions.assertEquals(p1, p2);
    }

    @Test public void equality() {
        Tag p1 = Tag.valueOf("p");
        Tag p2 = Tag.valueOf("p");
        Assertions.assertEquals(p1, p2);
        Assertions.assertSame(p1, p2);
    }

    @Test public void divSemantics() {
        Tag div = Tag.valueOf("div");

        Assertions.assertTrue(div.isBlock());
        Assertions.assertTrue(div.formatAsBlock());
    }

    @Test public void pSemantics() {
        Tag p = Tag.valueOf("p");

        Assertions.assertTrue(p.isBlock());
        Assertions.assertFalse(p.formatAsBlock());
    }

    @Test public void imgSemantics() {
        Tag img = Tag.valueOf("img");
        Assertions.assertTrue(img.isInline());
        Assertions.assertTrue(img.isSelfClosing());
        Assertions.assertFalse(img.isBlock());
    }

    @Test public void defaultSemantics() {
        Tag foo = Tag.valueOf("FOO"); // not defined
        Tag foo2 = Tag.valueOf("FOO");

        Assertions.assertEquals(foo, foo2);
        Assertions.assertTrue(foo.isInline());
        Assertions.assertTrue(foo.formatAsBlock());
    }

    @Test public void valueOfChecksNotNull() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> Tag.valueOf(null));
    }

    @Test public void valueOfChecksNotEmpty() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> Tag.valueOf(" "));
    }

    @Test public void knownTags() {
        Assertions.assertTrue(Tag.isKnownTag("div"));
        Assertions.assertFalse(Tag.isKnownTag("explain"));
    }
}
