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
package com.itextpdf.styledxmlparser.jsoup.parser;

import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.*;

/**
 Tag tests.
 @author Jonathan Hedley, jonathan@hedley.net */
@Category(UnitTest.class)
public class TagTest extends ExtendedITextTest {

    @Test public void isCaseInsensitive() {
        Tag p1 = Tag.valueOf("P");
        Tag p2 = Tag.valueOf("p");
        assertEquals(p1, p2);
    }

    @Test public void trims() {
        Tag p1 = Tag.valueOf("p");
        Tag p2 = Tag.valueOf(" p ");
        assertEquals(p1, p2);
    }

    @Test public void equality() {
        Tag p1 = Tag.valueOf("p");
        Tag p2 = Tag.valueOf("p");
        assertTrue(p1.equals(p2));
        assertTrue(p1 == p2);
    }

    @Test public void divSemantics() {
        Tag div = Tag.valueOf("div");

        assertTrue(div.isBlock());
        assertTrue(div.formatAsBlock());
    }

    @Test public void pSemantics() {
        Tag p = Tag.valueOf("p");

        assertTrue(p.isBlock());
        assertFalse(p.formatAsBlock());
    }

    @Test public void imgSemantics() {
        Tag img = Tag.valueOf("img");
        assertTrue(img.isInline());
        assertTrue(img.isSelfClosing());
        assertFalse(img.isBlock());
    }

    @Test public void defaultSemantics() {
        Tag foo = Tag.valueOf("foo"); // not defined
        Tag foo2 = Tag.valueOf("FOO");

        assertEquals(foo, foo2);
        assertTrue(foo.isInline());
        assertTrue(foo.formatAsBlock());
    }
}
