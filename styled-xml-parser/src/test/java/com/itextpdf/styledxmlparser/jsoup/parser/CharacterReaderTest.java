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
package com.itextpdf.styledxmlparser.jsoup.parser;

import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.io.BufferedReader;
import java.io.StringReader;

/**
 * Test suite for character reader.
 */
@Tag("UnitTest")
public class CharacterReaderTest extends ExtendedITextTest {

    @Test public void consume() {
        CharacterReader r = new CharacterReader("one");
        Assertions.assertEquals(0, r.pos());
        Assertions.assertEquals('o', r.current());
        Assertions.assertEquals('o', r.consume());
        Assertions.assertEquals(1, r.pos());
        Assertions.assertEquals('n', r.current());
        Assertions.assertEquals(1, r.pos());
        Assertions.assertEquals('n', r.consume());
        Assertions.assertEquals('e', r.consume());
        Assertions.assertTrue(r.isEmpty());
        Assertions.assertEquals(CharacterReader.EOF, r.consume());
        Assertions.assertTrue(r.isEmpty());
        Assertions.assertEquals(CharacterReader.EOF, r.consume());
    }

    @Test public void unconsume() {
        CharacterReader r = new CharacterReader("one");
        Assertions.assertEquals('o', r.consume());
        Assertions.assertEquals('n', r.current());
        r.unconsume();
        Assertions.assertEquals('o', r.current());

        Assertions.assertEquals('o', r.consume());
        Assertions.assertEquals('n', r.consume());
        Assertions.assertEquals('e', r.consume());
        Assertions.assertTrue(r.isEmpty());
        r.unconsume();
        Assertions.assertFalse(r.isEmpty());
        Assertions.assertEquals('e', r.current());
        Assertions.assertEquals('e', r.consume());
        Assertions.assertTrue(r.isEmpty());

        Assertions.assertEquals(CharacterReader.EOF, r.consume());
        r.unconsume(); // read past, so have to eat again
        Assertions.assertTrue(r.isEmpty());
        r.unconsume();
        Assertions.assertFalse(r.isEmpty());

        Assertions.assertEquals('e', r.consume());
        Assertions.assertTrue(r.isEmpty());

        Assertions.assertEquals(CharacterReader.EOF, r.consume());
        Assertions.assertTrue(r.isEmpty());
    }

    @Test public void mark() {
        CharacterReader r = new CharacterReader("one");
        r.consume();
        r.mark();
        Assertions.assertEquals(1, r.pos());
        Assertions.assertEquals('n', r.consume());
        Assertions.assertEquals('e', r.consume());
        Assertions.assertTrue(r.isEmpty());
        r.rewindToMark();
        Assertions.assertEquals(1, r.pos());
        Assertions.assertEquals('n', r.consume());
        Assertions.assertFalse(r.isEmpty());
        Assertions.assertEquals(2, r.pos());
    }

    @Test public void consumeToEnd() {
        String in = "one two three";
        CharacterReader r = new CharacterReader(in);
        String toEnd = r.consumeToEnd();
        Assertions.assertEquals(in, toEnd);
        Assertions.assertTrue(r.isEmpty());
    }

    @Test public void nextIndexOfChar() {
        String in = "blah blah";
        CharacterReader r = new CharacterReader(in);

        Assertions.assertEquals(-1, r.nextIndexOf('x'));
        Assertions.assertEquals(3, r.nextIndexOf('h'));
        String pull = r.consumeTo('h');
        Assertions.assertEquals("bla", pull);
        r.consume();
        Assertions.assertEquals(2, r.nextIndexOf('l'));
        Assertions.assertEquals(" blah", r.consumeToEnd());
        Assertions.assertEquals(-1, r.nextIndexOf('x'));
    }

    @Test public void nextIndexOfString() {
        String in = "One Two something Two Three Four";
        CharacterReader r = new CharacterReader(in);

        Assertions.assertEquals(-1, r.nextIndexOf("Foo"));
        Assertions.assertEquals(4, r.nextIndexOf("Two"));
        Assertions.assertEquals("One Two ", r.consumeTo("something"));
        Assertions.assertEquals(10, r.nextIndexOf("Two"));
        Assertions.assertEquals("something Two Three Four", r.consumeToEnd());
        Assertions.assertEquals(-1, r.nextIndexOf("Two"));
    }

    @Test public void nextIndexOfUnmatched() {
        CharacterReader r = new CharacterReader("<[[one]]");
        Assertions.assertEquals(-1, r.nextIndexOf("]]>"));
    }

    @Test public void consumeToChar() {
        CharacterReader r = new CharacterReader("One Two Three");
        Assertions.assertEquals("One ", r.consumeTo('T'));
        Assertions.assertEquals("", r.consumeTo('T')); // on Two
        Assertions.assertEquals('T', r.consume());
        Assertions.assertEquals("wo ", r.consumeTo('T'));
        Assertions.assertEquals('T', r.consume());
        Assertions.assertEquals("hree", r.consumeTo('T')); // consume to end
    }

    @Test public void consumeToString() {
        CharacterReader r = new CharacterReader("One Two Two Four");
        Assertions.assertEquals("One ", r.consumeTo("Two"));
        Assertions.assertEquals('T', r.consume());
        Assertions.assertEquals("wo ", r.consumeTo("Two"));
        Assertions.assertEquals('T', r.consume());
        // To handle strings straddling across buffers, consumeTo() may return the
        // data in multiple pieces near EOF.
        StringBuilder builder = new StringBuilder();
        String part;
        do {
            part = r.consumeTo("Qux");
            builder.append(part);
        } while (!part.isEmpty());
        Assertions.assertEquals("wo Four", builder.toString());
    }

    @Test public void advance() {
        CharacterReader r = new CharacterReader("One Two Three");
        Assertions.assertEquals('O', r.consume());
        r.advance();
        Assertions.assertEquals('e', r.consume());
    }

    @Test public void consumeToAny() {
        CharacterReader r = new CharacterReader("One &bar; qux");
        Assertions.assertEquals("One ", r.consumeToAny('&', ';'));
        Assertions.assertTrue(r.matches('&'));
        Assertions.assertTrue(r.matches("&bar;"));
        Assertions.assertEquals('&', r.consume());
        Assertions.assertEquals("bar", r.consumeToAny('&', ';'));
        Assertions.assertEquals(';', r.consume());
        Assertions.assertEquals(" qux", r.consumeToAny('&', ';'));
    }

    @Test public void consumeLetterSequence() {
        CharacterReader r = new CharacterReader("One &bar; qux");
        Assertions.assertEquals("One", r.consumeLetterSequence());
        Assertions.assertEquals(" &", r.consumeTo("bar;"));
        Assertions.assertEquals("bar", r.consumeLetterSequence());
        Assertions.assertEquals("; qux", r.consumeToEnd());
    }

    @Test public void consumeLetterThenDigitSequence() {
        CharacterReader r = new CharacterReader("One12 Two &bar; qux");
        Assertions.assertEquals("One12", r.consumeLetterThenDigitSequence());
        Assertions.assertEquals(' ', r.consume());
        Assertions.assertEquals("Two", r.consumeLetterThenDigitSequence());
        Assertions.assertEquals(" &bar; qux", r.consumeToEnd());
    }

    @Test public void matches() {
        CharacterReader r = new CharacterReader("One Two Three");
        Assertions.assertTrue(r.matches('O'));
        Assertions.assertTrue(r.matches("One Two Three"));
        Assertions.assertTrue(r.matches("One"));
        Assertions.assertFalse(r.matches("one"));
        Assertions.assertEquals('O', r.consume());
        Assertions.assertFalse(r.matches("One"));
        Assertions.assertTrue(r.matches("ne Two Three"));
        Assertions.assertFalse(r.matches("ne Two Three Four"));
        Assertions.assertEquals("ne Two Three", r.consumeToEnd());
        Assertions.assertFalse(r.matches("ne"));
        Assertions.assertTrue(r.isEmpty());
    }

    @Test
    public void matchesIgnoreCase() {
        CharacterReader r = new CharacterReader("One Two Three");
        Assertions.assertTrue(r.matchesIgnoreCase("O"));
        Assertions.assertTrue(r.matchesIgnoreCase("o"));
        Assertions.assertTrue(r.matches('O'));
        Assertions.assertFalse(r.matches('o'));
        Assertions.assertTrue(r.matchesIgnoreCase("One Two Three"));
        Assertions.assertTrue(r.matchesIgnoreCase("ONE two THREE"));
        Assertions.assertTrue(r.matchesIgnoreCase("One"));
        Assertions.assertTrue(r.matchesIgnoreCase("one"));
        Assertions.assertEquals('O', r.consume());
        Assertions.assertFalse(r.matchesIgnoreCase("One"));
        Assertions.assertTrue(r.matchesIgnoreCase("NE Two Three"));
        Assertions.assertFalse(r.matchesIgnoreCase("ne Two Three Four"));
        Assertions.assertEquals("ne Two Three", r.consumeToEnd());
        Assertions.assertFalse(r.matchesIgnoreCase("ne"));
    }

    @Test public void containsIgnoreCase() {
        CharacterReader r = new CharacterReader("One TWO three");
        Assertions.assertTrue(r.containsIgnoreCase("two"));
        Assertions.assertTrue(r.containsIgnoreCase("three"));
        // weird one: does not find one, because it scans for consistent case only
        Assertions.assertFalse(r.containsIgnoreCase("one"));
    }

    @Test public void matchesAny() {
        char[] scan = {' ', '\n', '\t'};
        CharacterReader r = new CharacterReader("One\nTwo\tThree");
        Assertions.assertFalse(r.matchesAny(scan));
        Assertions.assertEquals("One", r.consumeToAny(scan));
        Assertions.assertTrue(r.matchesAny(scan));
        Assertions.assertEquals('\n', r.consume());
        Assertions.assertFalse(r.matchesAny(scan));
    }

    @Test public void cachesStrings() {
        CharacterReader r = new CharacterReader("Check\tCheck\tCheck\tCHOKE\tA string that is longer than 16 chars");
        String one = r.consumeTo('\t');
        r.consume();
        String two = r.consumeTo('\t');
        r.consume();
        String three = r.consumeTo('\t');
        r.consume();
        String four = r.consumeTo('\t');
        r.consume();
        String five = r.consumeTo('\t');

        Assertions.assertEquals("Check", one);
        Assertions.assertEquals("Check", two);
        Assertions.assertEquals("Check", three);
        Assertions.assertEquals("CHOKE", four);
        Assertions.assertSame(one, two);
        Assertions.assertSame(two, three);
        Assertions.assertNotSame(three, four);
        Assertions.assertNotSame(four, five);
        Assertions.assertEquals(five, "A string that is longer than 16 chars");
    }

    @Test
    public void rangeEquals() {
        CharacterReader r = new CharacterReader("Check\tCheck\tCheck\tCHOKE");
        Assertions.assertTrue(r.rangeEquals(0, 5, "Check"));
        Assertions.assertFalse(r.rangeEquals(0, 5, "CHOKE"));
        Assertions.assertFalse(r.rangeEquals(0, 5, "Chec"));

        Assertions.assertTrue(r.rangeEquals(6, 5, "Check"));
        Assertions.assertFalse(r.rangeEquals(6, 5, "Chuck"));

        Assertions.assertTrue(r.rangeEquals(12, 5, "Check"));
        Assertions.assertFalse(r.rangeEquals(12, 5, "Cheeky"));

        Assertions.assertTrue(r.rangeEquals(18, 5, "CHOKE"));
        Assertions.assertFalse(r.rangeEquals(18, 5, "CHIKE"));
    }

    @Test
    public void empty() {
        CharacterReader r = new CharacterReader("One");
        Assertions.assertTrue(r.matchConsume("One"));
        Assertions.assertTrue(r.isEmpty());

        r = new CharacterReader("Two");
        String two = r.consumeToEnd();
        Assertions.assertEquals("Two", two);
    }

    @Test
    public void consumeToNonexistentEndWhenAtAnd() {
        CharacterReader r = new CharacterReader("<!");
        Assertions.assertTrue(r.matchConsume("<!"));
        Assertions.assertTrue(r.isEmpty());

        String after = r.consumeTo('>');
        Assertions.assertEquals("", after);

        Assertions.assertTrue(r.isEmpty());
    }

    @Test
    public void notEmptyAtBufferSplitPoint() {
        CharacterReader r = new CharacterReader(new StringReader("How about now"), 3);
        Assertions.assertEquals("How", r.consumeTo(' '));
        Assertions.assertFalse(r.isEmpty());

        Assertions.assertEquals(' ', r.consume());
        Assertions.assertFalse(r.isEmpty());
        Assertions.assertEquals(4, r.pos());
        Assertions.assertEquals('a', r.consume());
        Assertions.assertEquals(5, r.pos());
        Assertions.assertEquals('b', r.consume());
        Assertions.assertEquals('o', r.consume());
        Assertions.assertEquals('u', r.consume());
        Assertions.assertEquals('t', r.consume());
        Assertions.assertEquals(' ', r.consume());
        Assertions.assertEquals('n', r.consume());
        Assertions.assertEquals('o', r.consume());
        Assertions.assertEquals('w', r.consume());
        Assertions.assertTrue(r.isEmpty());
    }

    @Test public void bufferUp() {
        String note = "HelloThere"; // + ! = 11 chars
        int loopCount = 64;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < loopCount; i++) {
            sb.append(note);
            sb.append("!");
        }

        String s = sb.toString();
        BufferedReader br = new BufferedReader(new StringReader(s));

        CharacterReader r = new CharacterReader(br);
        for (int i = 0; i < loopCount; i++) {
            String pull = r.consumeTo('!');
            Assertions.assertEquals(note, pull);
            Assertions.assertEquals('!', r.current());
            r.advance();
        }

        Assertions.assertTrue(r.isEmpty());
    }

}
