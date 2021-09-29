/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
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
package com.itextpdf.styledxmlparser.jsoup.parser;

import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 Tag tests.
 @author Jonathan Hedley, jonathan@hedley.net */
@Category(UnitTest.class)
public class TagTest extends ExtendedITextTest {
    @Test public void isCaseSensitive() {
        Tag p1 = Tag.valueOf("P");
        Tag p2 = Tag.valueOf("p");
        Assert.assertNotEquals(p1, p2);
    }

    @Test public void trims() {
        Tag p1 = Tag.valueOf("p");
        Tag p2 = Tag.valueOf(" p ");
        Assert.assertEquals(p1, p2);
    }

    @Test public void equality() {
        Tag p1 = Tag.valueOf("p");
        Tag p2 = Tag.valueOf("p");
        Assert.assertEquals(p1, p2);
        Assert.assertSame(p1, p2);
    }

    @Test public void divSemantics() {
        Tag div = Tag.valueOf("div");

        Assert.assertTrue(div.isBlock());
        Assert.assertTrue(div.formatAsBlock());
    }

    @Test public void pSemantics() {
        Tag p = Tag.valueOf("p");

        Assert.assertTrue(p.isBlock());
        Assert.assertFalse(p.formatAsBlock());
    }

    @Test public void imgSemantics() {
        Tag img = Tag.valueOf("img");
        Assert.assertTrue(img.isInline());
        Assert.assertTrue(img.isSelfClosing());
        Assert.assertFalse(img.isBlock());
    }

    @Test public void defaultSemantics() {
        Tag foo = Tag.valueOf("FOO"); // not defined
        Tag foo2 = Tag.valueOf("FOO");

        Assert.assertEquals(foo, foo2);
        Assert.assertTrue(foo.isInline());
        Assert.assertTrue(foo.formatAsBlock());
    }

    @Test public void valueOfChecksNotNull() {
        Assert.assertThrows(IllegalArgumentException.class, () -> Tag.valueOf(null));
    }

    @Test public void valueOfChecksNotEmpty() {
        Assert.assertThrows(IllegalArgumentException.class, () -> Tag.valueOf(" "));
    }

    @Test public void knownTags() {
        Assert.assertTrue(Tag.isKnownTag("div"));
        Assert.assertFalse(Tag.isKnownTag("explain"));
    }
}
