/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
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

import com.itextpdf.test.annotations.type.UnitTest;

import java.util.Arrays;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import junit.framework.Assert;

@Category(UnitTest.class)
public class PageRangeTest {

    @Test
    public void addSingle() {
        PageRange range = new PageRange();
        range.addSinglePage(5);

        Assert.assertEquals(range.getQualifyingPageNums(10), Arrays.asList(5));
    }

    @Test
    public void addSingles() {
        PageRange range = new PageRange();
        range.addSinglePage(5);
        range.addSinglePage(1);

        Assert.assertEquals(range.getQualifyingPageNums(7), Arrays.asList(5, 1));
    }

    @Test
    public void addSequence() {
        PageRange range = new PageRange();
        range.addPageSequence(11, 19);

        Assert.assertEquals(range.getQualifyingPageNums(16), Arrays.asList(11, 12, 13, 14, 15, 16));
    }

    @Test
    public void addSequenceAndSingle() {
        PageRange range = new PageRange();
        range.addPageSequence(22, 27);
        range.addSinglePage(25);

        Assert.assertEquals(range.getQualifyingPageNums(30), Arrays.asList(22, 23, 24, 25, 26, 27, 25));
    }

    @Test
    public void addSingleAndSequence() {
        PageRange range = new PageRange();
        range.addSinglePage(5);
        range.addPageSequence(3, 8);

        Assert.assertEquals(range.getQualifyingPageNums(10), Arrays.asList(5, 3, 4, 5, 6, 7, 8));
    }

    @Test
    public void addCustomAfter() {
        PageRange range = new PageRange();
        range.addPageRangePart(new PageRange.PageRangePartAfter(3));

        Assert.assertEquals(range.getQualifyingPageNums(5), Arrays.asList(3, 4, 5));
    }

    @Test
    public void addCustomEven() {
        PageRange range = new PageRange();
        range.addPageRangePart(PageRange.PageRangePartOddEven.EVEN);

        Assert.assertEquals(range.getQualifyingPageNums(5), Arrays.asList(2, 4));
    }

    @Test
    public void addCustomAnd() {
        PageRange range = new PageRange();
        PageRange.IPageRangePart odd = PageRange.PageRangePartOddEven.ODD;
        PageRange.IPageRangePart seq = new PageRange.PageRangePartSequence(2, 14);
        PageRange.IPageRangePart and = new PageRange.PageRangePartAnd(odd, seq);
        range.addPageRangePart(and);

        Assert.assertEquals(range.getQualifyingPageNums(15), Arrays.asList(3, 5, 7, 9, 11, 13));
    }

    @Test
    public void addSingleConstructor() {
        PageRange range = new PageRange("5");

        Assert.assertEquals(range.getQualifyingPageNums(7), Arrays.asList(5));
    }

    @Test
    public void addSinglesConstructor() {
        PageRange range = new PageRange("5, 1");

        Assert.assertEquals(range.getQualifyingPageNums(10), Arrays.asList(5, 1));
    }

    @Test
    public void addSequenceConstructor() {
        PageRange range = new PageRange("11-19");

        Assert.assertEquals(range.getQualifyingPageNums(16), Arrays.asList(11, 12, 13, 14, 15, 16));
    }

    @Test
    public void addSequenceAndSingleConstructor() {
        PageRange range = new PageRange("22-27,25");

        Assert.assertEquals(range.getQualifyingPageNums(30), Arrays.asList(22, 23, 24, 25, 26, 27, 25));
    }

    @Test
    public void addSingleAndSequenceConstructor() {
        PageRange range = new PageRange("5, 3-8");

        Assert.assertEquals(range.getQualifyingPageNums(10), Arrays.asList(5, 3, 4, 5, 6, 7, 8));
    }

    @Test
    public void addCustomAfterConstructor() {
        PageRange range = new PageRange("3-");

        Assert.assertEquals(range.getQualifyingPageNums(5), Arrays.asList(3, 4, 5));
    }

    @Test
    public void addCustomEvenConstructor() {
        PageRange range = new PageRange("even");

        Assert.assertEquals(range.getQualifyingPageNums(5), Arrays.asList(2, 4));
    }

    @Test
    public void addCustomAndConstructor() {
        PageRange range = new PageRange("odd & 2-14");

        Assert.assertEquals(range.getQualifyingPageNums(15), Arrays.asList(3, 5, 7, 9, 11, 13));
    }
}
