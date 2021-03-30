/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.kernel.utils;

import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class PageRangeTest extends ExtendedITextTest {

    @Test
    public void addSingle() {
        PageRange range = new PageRange();
        range.addSinglePage(5);

        Assert.assertEquals(Arrays.asList(5), range.getQualifyingPageNums(10));
    }

    @Test
    public void addSingles() {
        PageRange range = new PageRange();
        range.addSinglePage(5);
        range.addSinglePage(1);

        Assert.assertEquals(Arrays.asList(5, 1), range.getQualifyingPageNums(7));
    }

    @Test
    public void addSequence() {
        PageRange range = new PageRange();
        range.addPageSequence(11, 19);

        Assert.assertEquals(Arrays.asList(11, 12, 13, 14, 15, 16), range.getQualifyingPageNums(16));
    }

    @Test
    public void addSequenceAndSingle() {
        PageRange range = new PageRange();
        range.addPageSequence(22, 27);
        range.addSinglePage(25);

        Assert.assertEquals(Arrays.asList(22, 23, 24, 25, 26, 27, 25), range.getQualifyingPageNums(30));
    }

    @Test
    public void addSingleAndSequence() {
        PageRange range = new PageRange();
        range.addSinglePage(5);
        range.addPageSequence(3, 8);

        Assert.assertEquals(Arrays.asList(5, 3, 4, 5, 6, 7, 8), range.getQualifyingPageNums(10));
    }

    @Test
    public void addCustomAfter() {
        PageRange range = new PageRange();
        range.addPageRangePart(new PageRange.PageRangePartAfter(3));

        Assert.assertEquals(Arrays.asList(3, 4, 5), range.getQualifyingPageNums(5));
    }

    @Test
    public void addCustomEven() {
        PageRange range = new PageRange();
        range.addPageRangePart(PageRange.PageRangePartOddEven.EVEN);

        Assert.assertEquals(Arrays.asList(2, 4), range.getQualifyingPageNums(5));
    }

    @Test
    public void addCustomAnd() {
        PageRange range = new PageRange();
        PageRange.IPageRangePart odd = PageRange.PageRangePartOddEven.ODD;
        PageRange.IPageRangePart seq = new PageRange.PageRangePartSequence(2, 14);
        PageRange.IPageRangePart and = new PageRange.PageRangePartAnd(odd, seq);
        range.addPageRangePart(and);

        Assert.assertEquals(Arrays.asList(3, 5, 7, 9, 11, 13), range.getQualifyingPageNums(15));
    }

    @Test
    public void addSingleConstructor() {
        PageRange range = new PageRange("5");

        Assert.assertEquals(Arrays.asList(5), range.getQualifyingPageNums(7));
    }

    @Test
    public void addSinglesConstructor() {
        PageRange range = new PageRange("5, 1");

        Assert.assertEquals(Arrays.asList(5, 1), range.getQualifyingPageNums(10));
    }

    @Test
    public void addSequenceConstructor() {
        PageRange range = new PageRange("11-19");

        Assert.assertEquals(Arrays.asList(11, 12, 13, 14, 15, 16), range.getQualifyingPageNums(16));
    }

    @Test
    public void addSequenceAndSingleConstructor() {
        PageRange range = new PageRange("22-27,25");

        Assert.assertEquals(Arrays.asList(22, 23, 24, 25, 26, 27, 25), range.getQualifyingPageNums(30));
    }

    @Test
    public void addSingleAndSequenceConstructor() {
        PageRange range = new PageRange("5, 3-8");

        Assert.assertEquals(Arrays.asList(5, 3, 4, 5, 6, 7, 8), range.getQualifyingPageNums(10));
    }

    @Test
    public void addCustomAfterConstructor() {
        PageRange range = new PageRange("3-");

        Assert.assertEquals(Arrays.asList(3, 4, 5), range.getQualifyingPageNums(5));
    }

    @Test
    public void addCustomEvenConstructor() {
        PageRange range = new PageRange("even");

        Assert.assertEquals(Arrays.asList(2, 4), range.getQualifyingPageNums(5));
    }

    @Test
    public void addCustomAndConstructor() {
        PageRange range = new PageRange("odd & 2-14");

        Assert.assertEquals(Arrays.asList(3, 5, 7, 9, 11, 13), range.getQualifyingPageNums(15));
    }

    @Test
    public void addIncorrectCustomAndConstructor() {
        PageRange range = new PageRange("&");

        Assert.assertEquals(new ArrayList<>(), range.getQualifyingPageNums(0));
    }

    @Test
    public void addIncorrectConstructor() {
        PageRange range = new PageRange("");

        Assert.assertEquals(new ArrayList<>(), range.getQualifyingPageNums(0));
    }

    @Test
    public void isPageInRangeTrueTest() {
        PageRange range = new PageRange("3-8");

        Assert.assertTrue(range.isPageInRange(6));
    }

    @Test
    public void isPageInRangeFalseTest() {
        PageRange range = new PageRange("3-8");

        Assert.assertFalse(range.isPageInRange(2));
    }

    @Test
    public void pageRangeEqualsNullTest() {
        PageRange range1 = new PageRange("3-8");

        Assert.assertFalse(range1.equals(null));
    }

    @Test
    public void pageRangeEqualsAndHashCodeTest() {
        PageRange range1 = new PageRange("3-8");
        PageRange range2 = new PageRange("3-8");

        boolean result = range1.equals(range2);
        Assert.assertTrue(result);
        Assert.assertEquals(range1.hashCode(), range2.hashCode());
    }

    @Test
    public void pageRangeNotEqualsAndHashCodeTest() {
        PageRange range1 = new PageRange("3-8");
        PageRange range2 = new PageRange("1-2");

        boolean result = range1.equals(range2);
        Assert.assertFalse(result);
        Assert.assertNotEquals(range1.hashCode(), range2.hashCode());
    }

    @Test
    public void getAllPagesInRangeEmptyTest() {
        PageRange.PageRangePartSingle pageRangePartSingle = new PageRange.PageRangePartSingle(10);

        Assert.assertEquals(new ArrayList<>(), pageRangePartSingle.getAllPagesInRange(4));
    }

    @Test
    public void isRangePartSingleInRangeTrueTest() {
        PageRange.PageRangePartSingle pageRangePartSingle = new PageRange.PageRangePartSingle(10);

        Assert.assertTrue(pageRangePartSingle.isPageInRange(10));
    }

    @Test
    public void isRangePartSingleInRangeFalseTest() {
        PageRange.PageRangePartSingle pageRangePartSingle = new PageRange.PageRangePartSingle(10);

        Assert.assertFalse(pageRangePartSingle.isPageInRange(1));
    }

    @Test
    public void rangePartSingleEqualsNullTest() {
        PageRange.PageRangePartSingle pageRangePartSingle = new PageRange.PageRangePartSingle(10);

        Assert.assertFalse(pageRangePartSingle.equals(null));
    }

    @Test
    public void rangePartSingleEqualsAndHashCodeTest() {
        PageRange.PageRangePartSingle pageRangePartSingle1 = new PageRange.PageRangePartSingle(10);
        PageRange.PageRangePartSingle pageRangePartSingle2 = new PageRange.PageRangePartSingle(10);

        boolean result = pageRangePartSingle1.equals(pageRangePartSingle2);
        Assert.assertTrue(result);
        Assert.assertEquals(pageRangePartSingle1.hashCode(), pageRangePartSingle2.hashCode());
    }

    @Test
    public void rangePartSingleNotEqualsAndHashCodeTest() {
        PageRange.PageRangePartSingle pageRangePartSingle1 = new PageRange.PageRangePartSingle(10);
        PageRange.PageRangePartSingle pageRangePartSingle2 = new PageRange.PageRangePartSingle(1);

        boolean result = pageRangePartSingle1.equals(pageRangePartSingle2);
        Assert.assertFalse(result);
        Assert.assertNotEquals(pageRangePartSingle1.hashCode(), pageRangePartSingle2.hashCode());
    }

    @Test
    public void rangePartSequenceEqualsNullTest() {
        PageRange.PageRangePartSequence pageRangePartSequence = new PageRange.PageRangePartSequence(1, 2);

        Assert.assertFalse(pageRangePartSequence.equals(null));
    }

    @Test
    public void rangePartSequenceEqualsAndHashCodeTest() {
        PageRange.PageRangePartSequence pageRangePartSequence = new PageRange.PageRangePartSequence(1, 2);
        PageRange.PageRangePartSequence pageRangePartSequence2 = new PageRange.PageRangePartSequence(1, 2);

        boolean result = pageRangePartSequence.equals(pageRangePartSequence2);
        Assert.assertTrue(result);
        Assert.assertEquals(pageRangePartSequence.hashCode(), pageRangePartSequence2.hashCode());
    }

    @Test
    public void rangePartSequenceNotEqualsAndHashCodeTest() {
        PageRange.PageRangePartSequence pageRangePartSequence = new PageRange.PageRangePartSequence(1, 2);
        PageRange.PageRangePartSequence pageRangePartSequence2 = new PageRange.PageRangePartSequence(3, 4);

        boolean result = pageRangePartSequence.equals(pageRangePartSequence2);
        Assert.assertFalse(result);
        Assert.assertNotEquals(pageRangePartSequence.hashCode(), pageRangePartSequence2.hashCode());
    }

    @Test
    public void isRangePartAfterInRangeTrueTest() {
        PageRange.PageRangePartAfter pageRangePartAfter = new PageRange.PageRangePartAfter(10);

        Assert.assertTrue(pageRangePartAfter.isPageInRange(11));
    }

    @Test
    public void isRangePartAfterInRangeFalseTest() {
        PageRange.PageRangePartAfter pageRangePartAfter = new PageRange.PageRangePartAfter(10);

        Assert.assertFalse(pageRangePartAfter.isPageInRange(1));
    }

    @Test
    public void rangePartAfterEqualsNullTest() {
        PageRange.PageRangePartAfter pageRangePartAfter = new PageRange.PageRangePartAfter(10);

        Assert.assertFalse(pageRangePartAfter.equals(null));
    }

    @Test
    public void rangePartAfterEqualsAndHashCodeTest() {
        PageRange.PageRangePartAfter pageRangePartAfter = new PageRange.PageRangePartAfter(10);
        PageRange.PageRangePartAfter pageRangePartAfter2 = new PageRange.PageRangePartAfter(10);

        boolean result = pageRangePartAfter.equals(pageRangePartAfter2);
        Assert.assertTrue(result);
        Assert.assertEquals(pageRangePartAfter.hashCode(), pageRangePartAfter2.hashCode());
    }

    @Test
    public void rangePartAfterNotEqualsAndHashCodeTest() {
        PageRange.PageRangePartAfter pageRangePartAfter = new PageRange.PageRangePartAfter(10);
        PageRange.PageRangePartAfter pageRangePartAfter2 = new PageRange.PageRangePartAfter(1);

        boolean result = pageRangePartAfter.equals(pageRangePartAfter2);
        Assert.assertFalse(result);
        Assert.assertNotEquals(pageRangePartAfter.hashCode(), pageRangePartAfter2.hashCode());
    }

    @Test
    public void isRangePartOddEvenInRangeTrueTest() {
        Assert.assertTrue(PageRange.PageRangePartOddEven.ODD.isPageInRange(11));
        Assert.assertTrue(PageRange.PageRangePartOddEven.EVEN.isPageInRange(10));
    }

    @Test
    public void isRangePartOddEvenInRangeFalseTest() {
        Assert.assertFalse(PageRange.PageRangePartOddEven.ODD.isPageInRange(10));
        Assert.assertFalse(PageRange.PageRangePartOddEven.EVEN.isPageInRange(11));
    }

    @Test
    public void rangePartOddEvenEqualsNullTest() {
        Assert.assertFalse(PageRange.PageRangePartOddEven.EVEN.equals(null));
        Assert.assertFalse(PageRange.PageRangePartOddEven.ODD.equals(null));
    }

    @Test
    public void rangePartOddEvenEqualsAndHashCodeTest() {
        Assert.assertTrue(PageRange.PageRangePartOddEven.EVEN.equals(PageRange.PageRangePartOddEven.EVEN));
        Assert.assertTrue(PageRange.PageRangePartOddEven.ODD.equals(PageRange.PageRangePartOddEven.ODD));

        Assert.assertEquals(PageRange.PageRangePartOddEven.EVEN.hashCode(),
                PageRange.PageRangePartOddEven.EVEN.hashCode());
        Assert.assertEquals(PageRange.PageRangePartOddEven.ODD.hashCode(),
                PageRange.PageRangePartOddEven.ODD.hashCode());
    }

    @Test
    public void rangePartOddEvenNotEqualsAndHashCodeTest() {
        Assert.assertFalse(PageRange.PageRangePartOddEven.EVEN.equals(PageRange.PageRangePartOddEven.ODD));

        Assert.assertNotEquals(PageRange.PageRangePartOddEven.EVEN.hashCode(),
                PageRange.PageRangePartOddEven.ODD.hashCode());
    }

    @Test
    public void isRangePartAndInRangeTrueTest() {
        PageRange.IPageRangePart odd = PageRange.PageRangePartOddEven.ODD;
        PageRange.IPageRangePart seq = new PageRange.PageRangePartSequence(2, 14);
        PageRange.PageRangePartAnd pageRangePartAnd = new PageRange.PageRangePartAnd(odd, seq);

        Assert.assertTrue(pageRangePartAnd.isPageInRange(5));
    }

    @Test
    public void isRangePartAndInRangeFalseTest() {
        PageRange.IPageRangePart odd = PageRange.PageRangePartOddEven.ODD;
        PageRange.IPageRangePart seq = new PageRange.PageRangePartSequence(2, 14);
        PageRange.PageRangePartAnd pageRangePartAnd = new PageRange.PageRangePartAnd(odd, seq);

        Assert.assertFalse(pageRangePartAnd.isPageInRange(1));
    }

    @Test
    public void rangePartAndEqualsNullTest() {
        PageRange.IPageRangePart odd = PageRange.PageRangePartOddEven.ODD;
        PageRange.IPageRangePart seq = new PageRange.PageRangePartSequence(2, 14);
        PageRange.PageRangePartAnd pageRangePartAnd = new PageRange.PageRangePartAnd(odd, seq);

        Assert.assertFalse(pageRangePartAnd.equals(null));
    }

    @Test
    public void rangePartAndEqualsAndHashCodeTest() {
        PageRange.IPageRangePart odd = PageRange.PageRangePartOddEven.ODD;
        PageRange.IPageRangePart seq = new PageRange.PageRangePartSequence(2, 14);
        PageRange.PageRangePartAnd pageRangePartAnd = new PageRange.PageRangePartAnd(odd, seq);
        PageRange.PageRangePartAnd pageRangePartAnd2 = new PageRange.PageRangePartAnd(odd, seq);

        boolean result = pageRangePartAnd.equals(pageRangePartAnd2);
        Assert.assertTrue(result);
        Assert.assertEquals(pageRangePartAnd.hashCode(), pageRangePartAnd2.hashCode());
    }

    @Test
    public void rangePartAndNotEqualsAndHashCodeTest() {
        PageRange.IPageRangePart odd = PageRange.PageRangePartOddEven.ODD;
        PageRange.IPageRangePart seq = new PageRange.PageRangePartSequence(2, 14);
        PageRange.PageRangePartAnd pageRangePartAnd = new PageRange.PageRangePartAnd(odd, seq);
        PageRange.PageRangePartAnd pageRangePartAnd2 = new PageRange.PageRangePartAnd();

        boolean result = pageRangePartAnd.equals(pageRangePartAnd2);
        Assert.assertFalse(result);
        Assert.assertNotEquals(pageRangePartAnd.hashCode(), pageRangePartAnd2.hashCode());
    }
}
