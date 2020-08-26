package com.itextpdf.kernel.numbering;

import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class RomanNumberingTest extends ExtendedITextTest {
    @Test
    public void negativeConvertTest() {
        Assert.assertEquals("-vi", RomanNumbering.convert(-6));
    }

    @Test
    public void zeroConvertTest() {
        Assert.assertEquals("", RomanNumbering.convert(0));
    }

    @Test
    public void convertTest() {
        Assert.assertEquals("mdclxvi", RomanNumbering.convert(1666));
        Assert.assertEquals("mcmlxxxiii", RomanNumbering.convert(1983));
        Assert.assertEquals("mmm", RomanNumbering.convert(3000));
        Assert.assertEquals("|vi|", RomanNumbering.convert(6000));
        Assert.assertEquals("|vi|dccxxxiii", RomanNumbering.convert(6733));
    }

    @Test
    public void toRomanTest() {
        String expected = "dcclvi";
        Assert.assertEquals(expected.toUpperCase(), RomanNumbering.toRoman(756, true));
        Assert.assertEquals(expected.toLowerCase(), RomanNumbering.toRoman(756, false));
    }

    @Test
    public void toRomanUpperCaseTest() {
        Assert.assertEquals("CCCLXXXVI", RomanNumbering.toRomanUpperCase(386));
    }

    @Test
    public void toRomanLowerCaseTest() {
        Assert.assertEquals("xxvi", RomanNumbering.toRomanLowerCase(26));
    }
}
