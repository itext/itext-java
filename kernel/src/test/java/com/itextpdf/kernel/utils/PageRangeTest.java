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

        Assert.assertEquals(range.getAllPages(), Arrays.asList(5));
    }

    @Test
    public void addSingles() {
        PageRange range = new PageRange();
        range.addSinglePage(5);
        range.addSinglePage(1);

        Assert.assertEquals(range.getAllPages(), Arrays.asList(5, 1));
    }

    @Test
    public void addSequence() {
        PageRange range = new PageRange();
        range.addPageSequence(11, 19);

        Assert.assertEquals(range.getAllPages(), Arrays.asList(11, 12, 13, 14, 15, 16, 17, 18, 19));
    }

    @Test
    public void addSequenceAndSingle() {
        PageRange range = new PageRange();
        range.addPageSequence(22, 27);
        range.addSinglePage(25);

        Assert.assertEquals(range.getAllPages(), Arrays.asList(22, 23, 24, 25, 26, 27, 25));
    }

    @Test
    public void addSingleAndSequence() {
        PageRange range = new PageRange();
        range.addSinglePage(5);
        range.addPageSequence(3, 8);

        Assert.assertEquals(range.getAllPages(), Arrays.asList(5, 3, 4, 5, 6, 7, 8));
    }

    @Test
    public void addCustomAfter() {
        PageRange range = new PageRange();
        range.addPageRangePart(new PageRange.PageRangePartAfter(3));

        Assert.assertEquals(range.getAllPages(), Arrays.asList(3));
        Assert.assertEquals(range.getAllPages(5), Arrays.asList(3, 4, 5));
    }
    
    @Test
    public void addCustomEven() {
        PageRange range = new PageRange();
        range.addPageRangePart(PageRange.PageRangePartOddEven.EVEN);

        Assert.assertEquals(range.getAllPages(5), Arrays.asList(2, 4));
    }

    @Test
    public void addCustomAnd() {
        PageRange range = new PageRange();
        PageRange.IPageRangePart odd = PageRange.PageRangePartOddEven.ODD;
        PageRange.IPageRangePart seq = new PageRange.PageRangePartSequence(2, 14);
        PageRange.IPageRangePart and = new PageRange.PageRangePartAnd(odd, seq);
        range.addPageRangePart(and);

        Assert.assertEquals(range.getAllPages(15), Arrays.asList(3, 5, 7, 9, 11, 13));
    }
}
