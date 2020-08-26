package com.itextpdf.kernel.numbering;

import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class GeorgianNumberingTest extends ExtendedITextTest {
    @Test
    public void negativeToGeorgianTest() {
        Assert.assertEquals("", GeorgianNumbering.toGeorgian(-10));
    }

    @Test
    public void zeroToGeorgianTest() {
        Assert.assertEquals("", GeorgianNumbering.toGeorgian(0));
    }

    @Test
    public void toGeorgianTest() {
        Assert.assertEquals("\u10F5", GeorgianNumbering.toGeorgian(10000));
        Assert.assertEquals("\u10F4\u10E8\u10F2\u10D6", GeorgianNumbering.toGeorgian(7967));
    }

    @Test
    public void numberGreaterThan10000toGeorgianTest() {
        Assert.assertEquals("\u10F5\u10F5\u10F5\u10F5\u10F5\u10F5\u10D2", GeorgianNumbering.toGeorgian(60003));;
    }
}
