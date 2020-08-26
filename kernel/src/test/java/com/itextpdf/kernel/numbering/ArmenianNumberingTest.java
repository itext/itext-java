package com.itextpdf.kernel.numbering;

import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class ArmenianNumberingTest extends ExtendedITextTest {
    @Test
    public void negativeToArmenianTest() {
        Assert.assertEquals("", ArmenianNumbering.toArmenian(-10));
    }

    @Test
    public void zeroToArmenianTest() {
        Assert.assertEquals("", ArmenianNumbering.toArmenian(0));
    }

    @Test
    public void toArmenianTest() {
        Assert.assertEquals("\u0554\u054B\u0542\u0539", ArmenianNumbering.toArmenian(9999));
        Assert.assertEquals("\u0552\u054A\u0540\u0534", ArmenianNumbering.toArmenian(7874));
    }

    @Test
    public void numberGreaterThan9999toArmenianTest() {
        Assert.assertEquals("\u0554\u0554\u0554\u0554\u0554\u0554\u0554\u0532", ArmenianNumbering.toArmenian(63002));
    }
}
