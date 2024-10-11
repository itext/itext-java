/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
package com.itextpdf.io.util;

import com.itextpdf.test.ExtendedITextTest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

/**
 * At the moment there is no com.itextpdf.io.util.Matcher class in Java (as we use
 * java.util.regex.Matcher), but there is one in C# that we are testing
 */
@Tag("UnitTest")
public class MatcherTest extends ExtendedITextTest {

    private static final String PATTERN_STRING = "(a+)(b+)?";
    private static final Pattern PATTERN = Pattern.compile(PATTERN_STRING);
    private static final Pattern FULL_MATCH_PATTERN = Pattern.compile("^" + PATTERN_STRING + "$");

    @Test
    public void matchesTest() {
        Matcher matched = FULL_MATCH_PATTERN.matcher("aaabbb");
        Assertions.assertTrue(matched.matches());

        Matcher notMatched = FULL_MATCH_PATTERN.matcher("aaacbbb");
        Assertions.assertFalse(notMatched.matches());
    }

    @Test
    public void twoGroupsFindTest() {
        Matcher matcher = PATTERN.matcher("aabbcaaacc");
        Assertions.assertTrue(matcher.find());
        Assertions.assertEquals(0, matcher.start());
        Assertions.assertEquals(4, matcher.end());
        Assertions.assertEquals("aabb", matcher.group());
        Assertions.assertEquals("aabb", matcher.group(0));
        Assertions.assertEquals("aa", matcher.group(1));
        Assertions.assertEquals("bb", matcher.group(2));
        Assertions.assertTrue(matcher.find());
        Assertions.assertEquals(5, matcher.start());
        Assertions.assertEquals(8, matcher.end());
        Assertions.assertEquals("aaa", matcher.group());
        Assertions.assertEquals("aaa", matcher.group(0));
        Assertions.assertEquals("aaa", matcher.group(1));
        Assertions.assertNull(matcher.group(2));
        Assertions.assertFalse(matcher.find());
    }

    @Test
    public void twoGroupsFindWithIndexTest() {
        Matcher matcher = PATTERN.matcher("aabbcaaacc");
        Assertions.assertTrue(matcher.find(6));
        Assertions.assertEquals(6, matcher.start());
        Assertions.assertEquals(8, matcher.end());
        Assertions.assertEquals("aa", matcher.group());
        Assertions.assertEquals("aa", matcher.group(0));
        Assertions.assertEquals("aa", matcher.group(1));
        Assertions.assertNull(matcher.group(2));
        Assertions.assertFalse(matcher.find());
        Assertions.assertFalse(matcher.find(9));
    }

    @Test
    public void startBeforeSearchTest() {
        Matcher matcher = PATTERN.matcher("aabb");

        Assertions.assertThrows(IllegalStateException.class, () -> matcher.start());
    }

    @Test
    public void startWhenFindFailsTest() {
        Matcher matcher = PATTERN.matcher("aabb");

        while (matcher.find()) {
        }

        Assertions.assertThrows(IllegalStateException.class, () -> matcher.start());
    }

    @Test
    public void endBeforeSearchTest() {
        Matcher matcher = PATTERN.matcher("aabb");

        Assertions.assertThrows(IllegalStateException.class, () -> matcher.end());
    }

    @Test
    public void endWhenFindFailsTest() {
        Matcher matcher = PATTERN.matcher("aabb");

        while (matcher.find()) {
        }

        Assertions.assertThrows(IllegalStateException.class, () -> matcher.end());
    }

    @Test
    public void groupBeforeSearchTest() {
        Matcher matcher = PATTERN.matcher("aabb");

        Assertions.assertThrows(IllegalStateException.class, () -> matcher.group());
    }

    @Test
    public void groupWhenFindFailsTest() {
        Matcher matcher = PATTERN.matcher("aabb");

        while (matcher.find()) {
        }

        Assertions.assertThrows(IllegalStateException.class, () -> matcher.group());
    }

    @Test
    public void groupWithIndexBeforeSearchTest() {
        Matcher matcher = PATTERN.matcher("aabb");

        Assertions.assertThrows(IllegalStateException.class, () -> matcher.group(0));
    }

    @Test
    public void groupWithIndexWhenFindFailsTest() {
        Matcher matcher = PATTERN.matcher("aabb");

        while (matcher.find()) {
        }

        Assertions.assertThrows(IllegalStateException.class, () -> matcher.group(0));
    }

    @Test
    public void groupNegativeIndexTest() {
        Matcher matcher = PATTERN.matcher("aabb");
        Assertions.assertTrue(matcher.find());

        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> matcher.group(-1));
    }

    @Test
    public void groupIndexGraterThanGroupCountTest() {
        Matcher matcher = PATTERN.matcher("aabb");
        Assertions.assertTrue(matcher.find());

        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> matcher.group(3));
    }

    @Test
    public void findNegativeIndexTest() {
        Matcher matcher = PATTERN.matcher("aabb");

        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> matcher.find(-1));
    }

    @Test
    public void findIndexGraterThanInputLengthTest() {
        String input = "aabb";
        Matcher matcher = PATTERN.matcher(input);

        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> matcher.find(input.length() + 1));
    }

    @Test
    public void findIndexEqualInputLengthTest() {
        String input = "aabb";
        Matcher matcher = PATTERN.matcher(input);
        Assertions.assertFalse(matcher.find(input.length()));
    }

    @Test
    public void matchesFullyAndOnceTest() {
        String testPattern = "(\\d+)-(\\d+)?";
        String input = "5-15";
        Matcher matcher = Pattern.compile(testPattern).matcher(input);
        Assertions.assertTrue(matcher.find());
        Assertions.assertEquals("5-15", matcher.group(0));
        Assertions.assertEquals("5", matcher.group(1));
        Assertions.assertEquals("15", matcher.group(2));
        Assertions.assertFalse(matcher.find());
    }

    @Test
    public void matchesOnceTest() {
        String testPattern = "(\\d+)-(\\d+)?";
        String input = "5-15-";
        Matcher matcher = Pattern.compile(testPattern).matcher(input);
        Assertions.assertTrue(matcher.find());
        Assertions.assertEquals("5-15", matcher.group(0));
        Assertions.assertEquals("5", matcher.group(1));
        Assertions.assertEquals("15", matcher.group(2));
        Assertions.assertFalse(matcher.find());
    }

    @Test
    public void matchesTwiceTest() {
        String testPattern = "a*b";
        String input = "abb";
        Matcher matcher = Pattern.compile(testPattern).matcher(input);
        Assertions.assertTrue(matcher.find());
        Assertions.assertEquals("ab", matcher.group(0));
        Assertions.assertTrue(matcher.find());
        Assertions.assertEquals("b", matcher.group(0));
        Assertions.assertFalse(matcher.find());
    }

    @Test
    public void matchesTwiceEmptyMatchTest() {
        String testPattern = "a*b*";
        String input = "abb";
        Matcher matcher = Pattern.compile(testPattern).matcher(input);
        Assertions.assertTrue(matcher.find());
        Assertions.assertEquals("abb", matcher.group(0));
        Assertions.assertTrue(matcher.find());
        Assertions.assertEquals("", matcher.group(0));
        Assertions.assertFalse(matcher.find());
    }

    @Test
    public void groupOutOfBoundsTest() {
        String testPattern = "(\\d+)";
        String input = "123";
        Matcher matcher = Pattern.compile(testPattern).matcher(input);
        Assertions.assertTrue(matcher.find());
        Assertions.assertEquals("123", matcher.group(0));
        Assertions.assertEquals("123", matcher.group(1));

        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> matcher.group(2));
    }

    @Test
    public void groupWhenNoMatchTest() {
        String testPattern = "(\\d+)";
        String input = "abc";
        Matcher matcher = Pattern.compile(testPattern).matcher(input);
        Assertions.assertFalse(matcher.find());

        Assertions.assertThrows(IllegalStateException.class, () -> matcher.group(0));
    }

    @Test
    public void alternativeGroupsTest() {
        String testPattern = "((\\d+)|(ab))cd(a*)e";
        String input = "abcdefg";
        Matcher matcher = Pattern.compile(testPattern).matcher(input);
        Assertions.assertTrue(matcher.find());
        Assertions.assertEquals("abcde", matcher.group(0));
        Assertions.assertEquals("ab", matcher.group(1));
        Assertions.assertNull(matcher.group(2));
        Assertions.assertEquals("ab", matcher.group(3));
        Assertions.assertEquals("", matcher.group(4));
        Assertions.assertFalse(matcher.find());
    }

    @Test
    public void startEndIndicesTest() {
        String testPattern = "cd";
        String input = "abcde";
        Matcher matcher = Pattern.compile(testPattern).matcher(input);
        Assertions.assertTrue(matcher.find());
        Assertions.assertEquals(2, matcher.start());
        Assertions.assertEquals(4, matcher.end());
    }

    @Test
    public void startIndexNotFoundTest() {
        String testPattern = "ef";
        String input = "abcde";
        Matcher matcher = Pattern.compile(testPattern).matcher(input);

        Assertions.assertFalse(matcher.find());
        Assertions.assertThrows(IllegalStateException.class, () -> matcher.start());
    }

    @Test
    public void endIndexNotFoundTest() {
        String testPattern = "ef";
        String input = "abcde";
        Matcher matcher = Pattern.compile(testPattern).matcher(input);

        Assertions.assertFalse(matcher.find());
        Assertions.assertThrows(IllegalStateException.class, () -> matcher.end());
    }

    @Test
    public void findMatchStartingFromIndexTest() {
        String testPattern = "ab|bc";
        String input = "00abcde";
        int startIndex = 3;
        Matcher matcher = Pattern.compile(testPattern).matcher(input);
        Assertions.assertTrue(matcher.find(startIndex));
        Assertions.assertEquals("bc", matcher.group(0));
        Assertions.assertEquals(3, matcher.start());
        Assertions.assertEquals(5, matcher.end());
        Assertions.assertFalse(matcher.find());
    }

    @Test
    public void findNextMatchStartingFromIndexTest() {
        String testPattern = "ab|bc";
        String input = "ab00abcde";
        Matcher matcher = Pattern.compile(testPattern).matcher(input);

        Assertions.assertTrue(matcher.find());
        int startIndex = 5;
        Assertions.assertTrue(matcher.find(startIndex));
        Assertions.assertEquals("bc", matcher.group(0));
        Assertions.assertEquals(5, matcher.start());
        Assertions.assertEquals(7, matcher.end());
        Assertions.assertFalse(matcher.find());
    }

    @Test
    public void findMatchStartingFromAfterInputStringTest() {
        String testPattern = "ab";
        String input = "cab";
        int startIndex = 3;
        Matcher matcher = Pattern.compile(testPattern).matcher(input);
        Assertions.assertFalse(matcher.find(startIndex));
    }

    @Test
    public void findNextMatchStartingFromAfterInputStringTest() {
        String testPattern = "ab";
        String input = "abc";
        Matcher matcher = Pattern.compile(testPattern).matcher(input);
        Assertions.assertTrue(matcher.find());
        int startIndex = 3;
        Assertions.assertFalse(matcher.find(startIndex));
    }

    @Test
    public void findMatchStartingFromIndexOutOfBoundsTest() {
        String testPattern = "ab";
        String input = "cab";
        int startIndex = 4;
        Matcher matcher = Pattern.compile(testPattern).matcher(input);

        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> matcher.find(startIndex));
    }

    @Test
    public void findNextMatchStartingFromIndexOutOfBoundsTest() {
        String testPattern = "ab";
        String input = "cab";

        Matcher matcher = Pattern.compile(testPattern).matcher(input);
        Assertions.assertTrue(matcher.find());

        int startIndex = 4;

        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> matcher.find(startIndex));
    }

    @Test
    public void findMatchStartingFromNegativeIndexTest() {
        String testPattern = "ab";
        String input = "cab";
        int startIndex = -1;
        Matcher matcher = Pattern.compile(testPattern).matcher(input);

        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> matcher.find(startIndex));
    }

    @Test
    public void findNextMatchStartingFromNegativeIndexTest() {
        String testPattern = "ab";
        String input = "cab";

        Matcher matcher = Pattern.compile(testPattern).matcher(input);
        Assertions.assertTrue(matcher.find());

        int startIndex = -1;

        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> matcher.find(startIndex));
    }

    @Test
    public void findNextMatchStartingFromIndexContinuouslyTest() {
        String testPattern = "ab";
        String input = "cabbabcaba";

        int startIndex1 = 2;
        Matcher matcher = Pattern.compile(testPattern).matcher(input);
        Assertions.assertTrue(matcher.find(startIndex1));
        Assertions.assertEquals(4, matcher.start());
        Assertions.assertEquals(6, matcher.end());

        int startIndex2 = 7;
        Assertions.assertTrue(matcher.find(startIndex2));
        Assertions.assertEquals(7, matcher.start());
        Assertions.assertEquals(9, matcher.end());

        int startIndex3 = input.length();
        Assertions.assertFalse(matcher.find(startIndex3));
    }

    @Test
    public void findNextMatchStartingFromIndexMovingBackwardsTest() {
        String testPattern = "ab";
        String input = "cabbabcaba";

        int startIndex1 = 7;
        Matcher matcher = Pattern.compile(testPattern).matcher(input);
        Assertions.assertTrue(matcher.find(startIndex1));
        Assertions.assertEquals(7, matcher.start());
        Assertions.assertEquals(9, matcher.end());

        int startIndex2 = 4;
        Assertions.assertTrue(matcher.find(startIndex2));
        Assertions.assertEquals(4, matcher.start());
        Assertions.assertEquals(6, matcher.end());

        int startIndex3 = 1;
        Assertions.assertTrue(matcher.find(startIndex3));
        Assertions.assertEquals(1, matcher.start());
        Assertions.assertEquals(3, matcher.end());

        Assertions.assertTrue(matcher.find());
        Assertions.assertEquals(4, matcher.start());
        Assertions.assertEquals(6, matcher.end());

        int startIndex4 = input.length();
        Assertions.assertFalse(matcher.find(startIndex4));
    }

    @Test
    public void matchesSuccessAfterFindFinish() {
        Matcher matcher = PATTERN.matcher("aaabbb");
        Assertions.assertTrue(matcher.find());
        Assertions.assertFalse(matcher.find());
        Assertions.assertTrue(matcher.matches());
        Assertions.assertFalse(matcher.find());
    }

    @Test
    public void findAfterMatchesSuccess() {
        Matcher matcher = PATTERN.matcher("aaabbb");
        Assertions.assertTrue(matcher.matches());
        Assertions.assertFalse(matcher.find());
    }

    @Test
    public void regionTest() {
        Matcher matcher = PATTERN.matcher("abbbbbabbbbbbbbbbbbbbb");
        matcher.region(6, 13);

        // abbbbbb [6, 13)
        Assertions.assertTrue(matcher.find());
        Assertions.assertFalse(matcher.find());
        Assertions.assertTrue(matcher.matches());
    }

    @Test
    public void regionSeveralMatchesTest() {
        Matcher matcher = PATTERN.matcher("abbbbbabababbbbbbbbbbb");
        matcher.region(6, 13);
        // ab [6, 8)
        Assertions.assertTrue(matcher.find());
        // ab [8, 10)
        Assertions.assertTrue(matcher.find());
        // abb [10, 13)
        Assertions.assertTrue(matcher.find());
        Assertions.assertFalse(matcher.find());
        Assertions.assertFalse(matcher.matches());
    }

    @Test
    public void stringMatchesButRegionDoesNotMatchTest() {
        Matcher matcher = PATTERN.matcher("abbbbbbbbbbbbbbbbbbbbb");
        Assertions.assertTrue(matcher.matches());
        matcher.region(6, 13);
        Assertions.assertFalse(matcher.matches());
    }

    @Test
    public void negativeStartOfRegionTest() {
        Matcher matcher = PATTERN.matcher("abbbbbbbbbbbbbbbbbbbbb");

        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> matcher.region(-1, 10));
    }

    @Test
    public void tooLargeStartOfRegionTest() {
        Matcher matcher = PATTERN.matcher("abbbbbbbbbbbbbbbbbbbbb");

        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> matcher.region(24, 24));
    }

    @Test
    public void negativeEndOfRegionTest() {
        Matcher matcher = PATTERN.matcher("abbbbbbbbbbbbbbbbbbbbb");

        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> matcher.region(1, -1));
    }

    @Test
    public void tooLargeEndOfRegionTest() {
        Matcher matcher = PATTERN.matcher("abbbbbbbbbbbbbbbbbbbbb");

        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> matcher.region(1, 24));
    }

    @Test
    public void endGreaterThenStartRegionTest() {
        Matcher matcher = PATTERN.matcher("abbbbbbbbbbbbbbbbbbbbb");

        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> matcher.region(10, 9));
    }

    @Test
    public void startAndEndEqualRegionTest() {
        Matcher matcher = PATTERN.matcher("abbbbbbbbbbbbbbbbbbbbb");
        matcher.region(9, 9);
        // *empty string* [9, 9)
        Assertions.assertFalse(matcher.matches());
    }

    @Test
    public void startAndEndEqualRegionMatchTest() {
        Pattern patternAcceptingEmptyString = Pattern.compile("(a+)?");
        Matcher matcher = patternAcceptingEmptyString.matcher("abbbbbbbbbbbbbbbbbbbbb");
        matcher.region(9, 9);
        // *empty string* [9, 9)
        Assertions.assertTrue(matcher.matches());
    }

    @Test
    public void severalRegionCallsTest() {
        Matcher matcher = PATTERN.matcher("abbbbbabababbbbbbbbbbb");
        matcher.region(6, 13);
        // abababb [6, 13)
        Assertions.assertFalse(matcher.matches());
        matcher.region(0, 3);
        // abb [0, 3)
        Assertions.assertTrue(matcher.matches());
        matcher.region(0, 4);
        // abbb [0, 4)
        Assertions.assertTrue(matcher.matches());
        matcher.region(0, 7);
        // abbbbba [0, 7)
        Assertions.assertFalse(matcher.matches());
    }

    @Test
    public void startEndFullRegionMatchesTest() {
        Matcher matcher = PATTERN.matcher("abbbbbabbbbbbbbbbbbbbb");
        matcher.region(6, 13);
        // ab [6, 13)
        Assertions.assertTrue(matcher.find());
        Assertions.assertEquals(6, matcher.start());
        Assertions.assertEquals(13, matcher.end());
    }

    @Test
    public void startEndPartiallyRegionMatchesTest() {
        Matcher matcher = PATTERN.matcher("abbbbbbbbabbabbbbbbbbb");
        matcher.region(6, 13);
        // abb [9, 12)
        Assertions.assertTrue(matcher.find());
        Assertions.assertEquals(9, matcher.start());
        Assertions.assertEquals(12, matcher.end());
    }

    @Test
    public void startRegionDoesNotMatchesTest() {
        Matcher matcher = PATTERN.matcher("abbbbbbbbbbbbbbbbbbbbb");
        matcher.region(6, 13);

        Assertions.assertFalse(matcher.find());
        Assertions.assertThrows(IllegalStateException.class, () -> matcher.start());
    }

    @Test
    public void endRegionDoesNotMatchesTest() {
        Matcher matcher = PATTERN.matcher("abbbbbbbbbbbbbbbbbbbbb");
        matcher.region(6, 13);

        Assertions.assertFalse(matcher.find());
        Assertions.assertThrows(IllegalStateException.class, () -> matcher.end());
    }

    @Test
    public void groupsAndRegionTest() {
        Matcher matcher = PATTERN.matcher("abbbbbabababbbbbbbbbbb");
        matcher.region(6, 8);
        // ab [6, 8)
        Assertions.assertTrue(matcher.find());
        Assertions.assertEquals("ab", matcher.group());
        Assertions.assertEquals("ab", matcher.group(0));
        Assertions.assertEquals("a", matcher.group(1));
        Assertions.assertEquals("b", matcher.group(2));
    }

    @Test
    public void regionResetsSearchTest() {
        Matcher matcher = PATTERN.matcher("bbbbbbabbbbbbbbbabbbbb");
        // abbbbbbbbb [6, 16)
        Assertions.assertTrue(matcher.find());
        Assertions.assertEquals(6, matcher.start());
        Assertions.assertEquals(16, matcher.end());
        // abbbbb [16, 22)
        Assertions.assertTrue(matcher.find());
        Assertions.assertEquals(16, matcher.start());
        Assertions.assertEquals(22, matcher.end());
        matcher.region(6, 13);
        // abbbbbb [6, 16)
        Assertions.assertTrue(matcher.find());
        Assertions.assertEquals(6, matcher.start());
        Assertions.assertEquals(13, matcher.end());
    }

    @Test
    public void findWithParamResetsRegionTest() {
        Matcher matcher = PATTERN.matcher("abbbbbbbbbbbbbbbbbbbbb");
        matcher.region(6, 13);
        // bbbbbbb [6, 13)
        Assertions.assertFalse(matcher.find());
        Assertions.assertTrue(matcher.find(0));
        Assertions.assertEquals("abbbbbbbbbbbbbbbbbbbbb", matcher.group());
        Assertions.assertEquals(0, matcher.start());
        Assertions.assertEquals(22, matcher.end());
    }

    @Test
    public void startAfterRegionThrowsExceptionTest() {
        Matcher matcher = PATTERN.matcher("abbbbbbbbbbbbbbbbbbbbb");
        matcher.find();
        matcher.region(6, 13);

        Assertions.assertThrows(IllegalStateException.class, () -> matcher.start());
    }

    @Test
    public void endAfterRegionThrowsExceptionTest() {
        Matcher matcher = PATTERN.matcher("abbbbbbbbbbbbbbbbbbbbb");
        matcher.find();
        matcher.region(6, 13);

        Assertions.assertThrows(IllegalStateException.class, () -> matcher.end());
    }

    @Test
    public void groupAfterRegionThrowsExceptionTest() {
        Matcher matcher = PATTERN.matcher("abbbbbbbbbbbbbbbbbbbbb");
        matcher.find();
        matcher.region(6, 13);

        Assertions.assertThrows(IllegalStateException.class, () -> matcher.group());
    }

}
