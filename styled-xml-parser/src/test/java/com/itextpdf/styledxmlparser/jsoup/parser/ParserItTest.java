/*
    This file is part of jsoup, see NOTICE.txt in the root of the repository.
    It may contain modifications beyond the original version.
*/
package com.itextpdf.styledxmlparser.jsoup.parser;

import com.itextpdf.styledxmlparser.jsoup.nodes.Document;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

/**
 * Longer running Parser tests.
 */

@Tag("UnitTest")
public class ParserItTest extends ExtendedITextTest {

    @Test
    public void testIssue1251() {
        // https://github.com/jhy/jsoup/issues/1251
        String testString = "<a href=\"\"ca";

        StringBuilder str = new StringBuilder();
        // initial max length of the buffer is 2**15 * 0.75 = 24576
        int spacesToReproduceIssue = 24577 - testString.length();
        for (int i = 0; i < spacesToReproduceIssue; i++) {
            str.append(" ");
        }
        str.append(testString);

        try {
            Parser.htmlParser().setTrackErrors(1).parseInput(str.toString(), "");
        } catch (Exception e) {
            throw new AssertionError("failed at length " + str.length(), e);
        }
    }

    @Test
    public void handlesDeepStack() {
        // inspired by http://sv.stargate.wikia.com/wiki/M2J and https://github.com/jhy/jsoup/issues/955
        // I didn't put it in the integration tests, because explorer and intellij kept dieing trying to preview/index it

        // Arrange
        StringBuilder longBody = new StringBuilder(500000);
        for (int i = 0; i < 25000; i++) {
            longBody.append(i).append("<dl><dd>");
        }
        for (int i = 0; i < 25000; i++) {
            longBody.append(i).append("</dd></dl>");
        }

        // Act
        long start = System.nanoTime();
        Document doc = Parser.parseBodyFragment(longBody.toString(), "");

        // Assert
        Assertions.assertEquals(2, doc.body().childNodeSize());
        Assertions.assertEquals(25000, doc.select("dd").size());
        Assertions.assertTrue((System.nanoTime() - start) / 1000000 < 20000); // I get ~ 1.5 seconds, but others have reported slower
        // was originally much longer, or stack overflow.
    }
}
