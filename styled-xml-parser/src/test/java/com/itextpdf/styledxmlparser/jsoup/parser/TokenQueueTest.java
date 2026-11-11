/*
    This file is part of jsoup, see NOTICE.txt in the root of the repository.
    It may contain modifications beyond the original version.
*/
package com.itextpdf.styledxmlparser.jsoup.parser;

import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.*;

/**
 * Token queue tests.
 */
@Category(UnitTest.class)
public class TokenQueueTest extends ExtendedITextTest {
    @Test public void chompBalanced() {
        TokenQueue tq = new TokenQueue(":contains(one (two) three) four");
        String pre = tq.consumeTo("(");
        String guts = tq.chompBalanced('(', ')');
        String remainder = tq.remainder();

        assertEquals(":contains", pre);
        assertEquals("one (two) three", guts);
        assertEquals(" four", remainder);
    }
    
    @Test public void chompEscapedBalanced() {
        TokenQueue tq = new TokenQueue(":contains(one (two) \\( \\) \\) three) four");
        String pre = tq.consumeTo("(");
        String guts = tq.chompBalanced('(', ')');
        String remainder = tq.remainder();

        assertEquals(":contains", pre);
        assertEquals("one (two) \\( \\) \\) three", guts);
        assertEquals("one (two) ( ) ) three", TokenQueue.unescape(guts));
        assertEquals(" four", remainder);
    }

    @Test public void chompBalancedMatchesAsMuchAsPossible() {
        TokenQueue tq = new TokenQueue("unbalanced(something(or another");
        tq.consumeTo("(");
        String match = tq.chompBalanced('(', ')');
        assertEquals("something(or another", match);
    }
    
    @Test public void unescape() {
        assertEquals("one ( ) \\", TokenQueue.unescape("one \\( \\) \\\\"));
    }
    
    @Test public void chompToIgnoreCase() {
        String t = "<textarea>one < two </TEXTarea>";
        TokenQueue tq = new TokenQueue(t);
        String data = tq.chompToIgnoreCase("</textarea");
        assertEquals("<textarea>one < two ", data);
        
        tq = new TokenQueue("<textarea> one two < three </oops>");
        data = tq.chompToIgnoreCase("</textarea");
        assertEquals("<textarea> one two < three </oops>", data);
    }

    @Test public void addFirst() {
        TokenQueue tq = new TokenQueue("One Two");
        tq.consumeWord();
        tq.addFirst("Three");
        assertEquals("Three Two", tq.remainder());
    }
}
