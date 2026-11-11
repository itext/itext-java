/*
    This file is part of jsoup, see NOTICE.txt in the root of the repository.
    It may contain modifications beyond the original version.
*/
package com.itextpdf.styledxmlparser.jsoup.nodes;

import com.itextpdf.kernel.pdf.annot.da.ExtendedAnnotationFont;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import com.itextpdf.styledxmlparser.jsoup.Jsoup;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Tests for Element (DOM stuff mostly).
 *
 * @author Jonathan Hedley
 */
@Category(UnitTest.class)
public class ElementExceptionTest extends ExtendedITextTest {
    @Test(expected = IllegalArgumentException.class) public void testThrowsOnAddNullText() {
        Document doc = Jsoup.parse("<div id=1><p>Hello</p></div>");
        Element div = doc.getElementById("1");
        div.appendText(null);
    }

    @Test(expected = IllegalArgumentException.class)  public void testThrowsOnPrependNullText() {
        Document doc = Jsoup.parse("<div id=1><p>Hello</p></div>");
        Element div = doc.getElementById("1");
        div.prependText(null);
    }

    @Test public void testChildThrowsIndexOutOfBoundsOnMissing() {
        Document doc = Jsoup.parse("<div><p>One</p><p>Two</p></div>");
        Element div = doc.select("div").first();

        assertEquals(2, div.children().size());
        assertEquals("One", div.child(0).text());

        try {
            div.child(3);
            fail("Should throw index out of bounds");
        } catch (IndexOutOfBoundsException e) {}
    }
}
