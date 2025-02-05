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
package com.itextpdf.kernel.utils;

import com.itextpdf.test.ExtendedITextTest;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class PageRangeTest extends ExtendedITextTest {

    @Test
    public void addSingleTest() {
        PageRange range = new PageRange();
        range.addSinglePage(5);

        Assertions.assertEquals(Arrays.asList(5), range.getQualifyingPageNums(10));
    }

    @Test
    public void addSinglesTest() {
        PageRange range = new PageRange();
        range.addSinglePage(5);
        range.addSinglePage(1);

        Assertions.assertEquals(Arrays.asList(5, 1), range.getQualifyingPageNums(7));
    }

    @Test
    public void addSequenceTest() {
        PageRange range = new PageRange();
        range.addPageSequence(11, 19);

        Assertions.assertEquals(Arrays.asList(11, 12, 13, 14, 15, 16), range.getQualifyingPageNums(16));
    }

    @Test
    public void addSequenceAndSingleTest() {
        PageRange range = new PageRange();
        range.addPageSequence(22, 27);
        range.addSinglePage(25);

        Assertions.assertEquals(Arrays.asList(22, 23, 24, 25, 26, 27, 25), range.getQualifyingPageNums(30));
    }

    @Test
    public void addSingleAndSequenceTest() {
        PageRange range = new PageRange();
        range.addSinglePage(5);
        range.addPageSequence(3, 8);

        Assertions.assertEquals(Arrays.asList(5, 3, 4, 5, 6, 7, 8), range.getQualifyingPageNums(10));
    }

    @Test
    public void addCustomAfterTest() {
        PageRange range = new PageRange();
        range.addPageRangePart(new PageRange.PageRangePartAfter(3));

        Assertions.assertEquals(Arrays.asList(3, 4, 5), range.getQualifyingPageNums(5));
    }

    @Test
    public void addCustomEvenTest() {
        PageRange range = new PageRange();
        range.addPageRangePart(PageRange.PageRangePartOddEven.EVEN);

        Assertions.assertEquals(Arrays.asList(2, 4), range.getQualifyingPageNums(5));
    }

    @Test
    public void addCustomAndTest() {
        PageRange range = new PageRange();
        PageRange.IPageRangePart odd = PageRange.PageRangePartOddEven.ODD;
        PageRange.IPageRangePart seq = new PageRange.PageRangePartSequence(2, 14);
        PageRange.IPageRangePart and = new PageRange.PageRangePartAnd(odd, seq);
        range.addPageRangePart(and);

        Assertions.assertEquals(Arrays.asList(3, 5, 7, 9, 11, 13), range.getQualifyingPageNums(15));
    }

    @Test
    public void addSingleConstructorTest() {
        PageRange range = new PageRange("5");

        Assertions.assertEquals(Arrays.asList(5), range.getQualifyingPageNums(7));
    }

    @Test
    public void addSinglesConstructorTest() {
        PageRange range = new PageRange("5, 1");

        Assertions.assertEquals(Arrays.asList(5, 1), range.getQualifyingPageNums(10));
    }

    @Test
    public void addSinglesConstructorWithNegativeNumbersTest() {
        PageRange range = new PageRange("-5, -1");

        Assertions.assertNotEquals(Arrays.asList(5, 1), range.getQualifyingPageNums(10));
    }

    @Test
    public void addSinglesConstructorWithWhitespacesTest() {
        PageRange range = new PageRange(" 5 , 1  ");

        Assertions.assertEquals(Arrays.asList(5, 1), range.getQualifyingPageNums(10));
    }

    @Test
    public void addSinglesConstructorWithLetterTest() {
        PageRange range = new PageRange("5, A, 1");

        Assertions.assertEquals(Arrays.asList(5, 1), range.getQualifyingPageNums(10));
    }

    @Test
    public void addSequenceConstructorTest() {
        PageRange range = new PageRange("11-19");

        Assertions.assertEquals(Arrays.asList(11, 12, 13, 14, 15, 16), range.getQualifyingPageNums(16));
    }

    @Test
    public void addSequenceConstructorWithWhitespacesTest() {
        PageRange range1 = new PageRange(" 11- 19");
        PageRange range2 = new PageRange(" 11 -19");
        PageRange range3 = new PageRange(" 11 - 19");

        Assertions.assertEquals(Arrays.asList(11, 12, 13, 14, 15, 16), range1.getQualifyingPageNums(16));
        Assertions.assertEquals(Arrays.asList(11, 12, 13, 14, 15, 16), range2.getQualifyingPageNums(16));
        Assertions.assertEquals(Arrays.asList(11, 12, 13, 14, 15, 16), range3.getQualifyingPageNums(16));
    }

    @Test
    public void addSequenceAndSingleConstructorTest() {
        PageRange range = new PageRange("22-27,25");

        Assertions.assertEquals(Arrays.asList(22, 23, 24, 25, 26, 27, 25), range.getQualifyingPageNums(30));
    }

    @Test
    public void addSingleAndSequenceConstructorTest() {
        PageRange range = new PageRange("5, 3-8");

        Assertions.assertEquals(Arrays.asList(5, 3, 4, 5, 6, 7, 8), range.getQualifyingPageNums(10));
    }

    @Test
    public void addCustomAfterConstructorTest() {
        PageRange range = new PageRange("3-");

        Assertions.assertEquals(Arrays.asList(3, 4, 5), range.getQualifyingPageNums(5));
    }

    @Test
    public void addCustomEvenConstructorTest() {
        PageRange range = new PageRange("even");

        Assertions.assertEquals(Arrays.asList(2, 4), range.getQualifyingPageNums(5));
    }

    @Test
    public void addCustomAndConstructorTest() {
        PageRange range = new PageRange("odd & 2-14");

        Assertions.assertEquals(Arrays.asList(3, 5, 7, 9, 11, 13), range.getQualifyingPageNums(15));
    }

    @Test
    public void addIncorrectCustomAndConstructorTest() {
        PageRange range = new PageRange("&");

        Assertions.assertEquals(new ArrayList<>(), range.getQualifyingPageNums(0));
    }

    @Test
    public void addIncorrectConstructorTest() {
        PageRange range = new PageRange("");

        Assertions.assertEquals(new ArrayList<>(), range.getQualifyingPageNums(0));
    }

    @Test
    public void isPageInRangeTrueTest() {
        PageRange range = new PageRange("3-8");

        Assertions.assertTrue(range.isPageInRange(6));
    }

    @Test
    public void isPageInRangeFalseTest() {
        PageRange range = new PageRange("3-8");

        Assertions.assertFalse(range.isPageInRange(2));
    }

    @Test
    public void addSequenceConstructorWithNegativeNumberTest() {
        PageRange range = new PageRange("-3-8");

        Assertions.assertEquals(new ArrayList<>(), range.getQualifyingPageNums(3));
    }

    @Test
    public void addSequenceConstructorWithLetterTest() {
        PageRange range1 = new PageRange("3-F");
        PageRange range2 = new PageRange("3-8F");

        Assertions.assertEquals(new ArrayList<>(), range1.getQualifyingPageNums(3));
        Assertions.assertEquals(new ArrayList<>(), range2.getQualifyingPageNums(3));
    }

    @Test
    public void checkPageRangeEqualsNullTest() {
        PageRange range1 = new PageRange("3-8");

        Assertions.assertFalse(range1.equals(null));
    }

    @Test
    public void checkPageRangeEqualsAndHashCodeTest() {
        PageRange range1 = new PageRange("3-8");
        PageRange range2 = new PageRange("3-8");

        boolean result = range1.equals(range2);
        Assertions.assertTrue(result);
        Assertions.assertEquals(range1.hashCode(), range2.hashCode());
    }

    @Test
    public void checkPageRangeNotEqualsAndHashCodeTest() {
        PageRange range1 = new PageRange("3-8");
        PageRange range2 = new PageRange("1-2");

        boolean result = range1.equals(range2);
        Assertions.assertFalse(result);
        Assertions.assertNotEquals(range1.hashCode(), range2.hashCode());
    }

    @Test
    public void getAllPagesInRangeEmptyTest() {
        PageRange.PageRangePartSingle pageRangePartSingle = new PageRange.PageRangePartSingle(10);

        Assertions.assertEquals(new ArrayList<>(), pageRangePartSingle.getAllPagesInRange(4));
    }

    @Test
    public void isRangePartSingleInRangeTrueTest() {
        PageRange.PageRangePartSingle pageRangePartSingle = new PageRange.PageRangePartSingle(10);

        Assertions.assertTrue(pageRangePartSingle.isPageInRange(10));
    }

    @Test
    public void isRangePartSingleInRangeFalseTest() {
        PageRange.PageRangePartSingle pageRangePartSingle = new PageRange.PageRangePartSingle(10);

        Assertions.assertFalse(pageRangePartSingle.isPageInRange(1));
    }

    @Test
    public void checkRangePartSingleEqualsNullTest() {
        PageRange.PageRangePartSingle pageRangePartSingle = new PageRange.PageRangePartSingle(10);

        Assertions.assertFalse(pageRangePartSingle.equals(null));
    }

    @Test
    public void checkRangePartSingleEqualsAndHashCodeTest() {
        PageRange.PageRangePartSingle pageRangePartSingle1 = new PageRange.PageRangePartSingle(10);
        PageRange.PageRangePartSingle pageRangePartSingle2 = new PageRange.PageRangePartSingle(10);

        boolean result = pageRangePartSingle1.equals(pageRangePartSingle2);
        Assertions.assertTrue(result);
        Assertions.assertEquals(pageRangePartSingle1.hashCode(), pageRangePartSingle2.hashCode());
    }

    @Test
    public void checkRangePartSingleNotEqualsAndHashCodeTest() {
        PageRange.PageRangePartSingle pageRangePartSingle1 = new PageRange.PageRangePartSingle(10);
        PageRange.PageRangePartSingle pageRangePartSingle2 = new PageRange.PageRangePartSingle(1);

        boolean result = pageRangePartSingle1.equals(pageRangePartSingle2);
        Assertions.assertFalse(result);
        Assertions.assertNotEquals(pageRangePartSingle1.hashCode(), pageRangePartSingle2.hashCode());
    }

    @Test
    public void checkRangePartSequenceEqualsNullTest() {
        PageRange.PageRangePartSequence pageRangePartSequence = new PageRange.PageRangePartSequence(1, 2);

        Assertions.assertFalse(pageRangePartSequence.equals(null));
    }

    @Test
    public void checkRangePartSequenceEqualsAndHashCodeTest() {
        PageRange.PageRangePartSequence pageRangePartSequence = new PageRange.PageRangePartSequence(1, 2);
        PageRange.PageRangePartSequence pageRangePartSequence2 = new PageRange.PageRangePartSequence(1, 2);

        boolean result = pageRangePartSequence.equals(pageRangePartSequence2);
        Assertions.assertTrue(result);
        Assertions.assertEquals(pageRangePartSequence.hashCode(), pageRangePartSequence2.hashCode());
    }

    @Test
    public void checkRangePartSequenceNotEqualsAndHashCodeTest() {
        PageRange.PageRangePartSequence pageRangePartSequence = new PageRange.PageRangePartSequence(1, 2);
        PageRange.PageRangePartSequence pageRangePartSequence2 = new PageRange.PageRangePartSequence(3, 4);

        boolean result = pageRangePartSequence.equals(pageRangePartSequence2);
        Assertions.assertFalse(result);
        Assertions.assertNotEquals(pageRangePartSequence.hashCode(), pageRangePartSequence2.hashCode());
    }

    @Test
    public void isRangePartAfterInRangeTrueTest() {
        PageRange.PageRangePartAfter pageRangePartAfter = new PageRange.PageRangePartAfter(10);

        Assertions.assertTrue(pageRangePartAfter.isPageInRange(11));
    }

    @Test
    public void isRangePartAfterInRangeFalseTest() {
        PageRange.PageRangePartAfter pageRangePartAfter = new PageRange.PageRangePartAfter(10);

        Assertions.assertFalse(pageRangePartAfter.isPageInRange(1));
    }

    @Test
    public void checkRangePartAfterEqualsNullTest() {
        PageRange.PageRangePartAfter pageRangePartAfter = new PageRange.PageRangePartAfter(10);

        Assertions.assertFalse(pageRangePartAfter.equals(null));
    }

    @Test
    public void checkRangePartAfterEqualsAndHashCodeTest() {
        PageRange.PageRangePartAfter pageRangePartAfter = new PageRange.PageRangePartAfter(10);
        PageRange.PageRangePartAfter pageRangePartAfter2 = new PageRange.PageRangePartAfter(10);

        boolean result = pageRangePartAfter.equals(pageRangePartAfter2);
        Assertions.assertTrue(result);
        Assertions.assertEquals(pageRangePartAfter.hashCode(), pageRangePartAfter2.hashCode());
    }

    @Test
    public void checkRangePartAfterNotEqualsAndHashCodeTest() {
        PageRange.PageRangePartAfter pageRangePartAfter = new PageRange.PageRangePartAfter(10);
        PageRange.PageRangePartAfter pageRangePartAfter2 = new PageRange.PageRangePartAfter(1);

        boolean result = pageRangePartAfter.equals(pageRangePartAfter2);
        Assertions.assertFalse(result);
        Assertions.assertNotEquals(pageRangePartAfter.hashCode(), pageRangePartAfter2.hashCode());
    }

    @Test
    public void isRangePartOddEvenInRangeTrueTest() {
        Assertions.assertTrue(PageRange.PageRangePartOddEven.ODD.isPageInRange(11));
        Assertions.assertTrue(PageRange.PageRangePartOddEven.EVEN.isPageInRange(10));
    }

    @Test
    public void isRangePartOddEvenInRangeFalseTest() {
        Assertions.assertFalse(PageRange.PageRangePartOddEven.ODD.isPageInRange(10));
        Assertions.assertFalse(PageRange.PageRangePartOddEven.EVEN.isPageInRange(11));
    }

    @Test
    public void checkRangePartOddEvenEqualsNullTest() {
        Assertions.assertFalse(PageRange.PageRangePartOddEven.EVEN.equals(null));
        Assertions.assertFalse(PageRange.PageRangePartOddEven.ODD.equals(null));
    }

    @Test
    public void checkRangePartOddEvenEqualsAndHashCodeTest() {
        Assertions.assertTrue(PageRange.PageRangePartOddEven.EVEN.equals(PageRange.PageRangePartOddEven.EVEN));
        Assertions.assertTrue(PageRange.PageRangePartOddEven.ODD.equals(PageRange.PageRangePartOddEven.ODD));

        Assertions.assertEquals(PageRange.PageRangePartOddEven.EVEN.hashCode(),
                PageRange.PageRangePartOddEven.EVEN.hashCode());
        Assertions.assertEquals(PageRange.PageRangePartOddEven.ODD.hashCode(),
                PageRange.PageRangePartOddEven.ODD.hashCode());
    }

    @Test
    public void checkRangePartOddEvenNotEqualsAndHashCodeTest() {
        Assertions.assertFalse(PageRange.PageRangePartOddEven.EVEN.equals(PageRange.PageRangePartOddEven.ODD));

        Assertions.assertNotEquals(PageRange.PageRangePartOddEven.EVEN.hashCode(),
                PageRange.PageRangePartOddEven.ODD.hashCode());
    }

    @Test
    public void isRangePartAndInRangeTrueTest() {
        PageRange.IPageRangePart odd = PageRange.PageRangePartOddEven.ODD;
        PageRange.IPageRangePart seq = new PageRange.PageRangePartSequence(2, 14);
        PageRange.PageRangePartAnd pageRangePartAnd = new PageRange.PageRangePartAnd(odd, seq);

        Assertions.assertTrue(pageRangePartAnd.isPageInRange(5));
    }

    @Test
    public void isRangePartAndInRangeFalseTest() {
        PageRange.IPageRangePart odd = PageRange.PageRangePartOddEven.ODD;
        PageRange.IPageRangePart seq = new PageRange.PageRangePartSequence(2, 14);
        PageRange.PageRangePartAnd pageRangePartAnd = new PageRange.PageRangePartAnd(odd, seq);

        Assertions.assertFalse(pageRangePartAnd.isPageInRange(1));
    }

    @Test
    public void checkRangePartAndEqualsNullTest() {
        PageRange.IPageRangePart odd = PageRange.PageRangePartOddEven.ODD;
        PageRange.IPageRangePart seq = new PageRange.PageRangePartSequence(2, 14);
        PageRange.PageRangePartAnd pageRangePartAnd = new PageRange.PageRangePartAnd(odd, seq);

        Assertions.assertFalse(pageRangePartAnd.equals(null));
    }

    @Test
    public void checkRangePartAndEqualsAndHashCodeTest() {
        PageRange.IPageRangePart odd = PageRange.PageRangePartOddEven.ODD;
        PageRange.IPageRangePart seq = new PageRange.PageRangePartSequence(2, 14);
        PageRange.PageRangePartAnd pageRangePartAnd = new PageRange.PageRangePartAnd(odd, seq);
        PageRange.PageRangePartAnd pageRangePartAnd2 = new PageRange.PageRangePartAnd(odd, seq);

        boolean result = pageRangePartAnd.equals(pageRangePartAnd2);
        Assertions.assertTrue(result);
        Assertions.assertEquals(pageRangePartAnd.hashCode(), pageRangePartAnd2.hashCode());
    }

    @Test
    public void checkRangePartAndNotEqualsAndHashCodeTest() {
        PageRange.IPageRangePart odd = PageRange.PageRangePartOddEven.ODD;
        PageRange.IPageRangePart seq = new PageRange.PageRangePartSequence(2, 14);
        PageRange.PageRangePartAnd pageRangePartAnd = new PageRange.PageRangePartAnd(odd, seq);
        PageRange.PageRangePartAnd pageRangePartAnd2 = new PageRange.PageRangePartAnd();

        boolean result = pageRangePartAnd.equals(pageRangePartAnd2);
        Assertions.assertFalse(result);
        Assertions.assertNotEquals(pageRangePartAnd.hashCode(), pageRangePartAnd2.hashCode());
    }
}
