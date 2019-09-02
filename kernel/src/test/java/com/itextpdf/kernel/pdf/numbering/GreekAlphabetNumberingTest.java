package com.itextpdf.kernel.pdf.numbering;

import com.itextpdf.kernel.numbering.GreekAlphabetNumbering;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class GreekAlphabetNumberingTest {

    @Test
    public void testUpperCase() {
        StringBuilder builder = new StringBuilder();
        for (int i = 1; i <= 25; i++) {
            builder.append(GreekAlphabetNumbering.toGreekAlphabetNumber(i, true));
        }
        // 25th symbol is `AA`, i.e. alphabet has 24 letters.
        Assert.assertEquals("ΑΒΓΔΕΖΗΘΙΚΛΜΝΞΟΠΡΣΤΥΦΧΨΩΑΑ", builder.toString());
    }

    @Test
    public void testLowerCase() {
        StringBuilder builder = new StringBuilder();
        for (int i = 1; i <= 25; i++) {
            builder.append(GreekAlphabetNumbering.toGreekAlphabetNumber(i, false));
        }
        // 25th symbol is `αα`, i.e. alphabet has 24 letters.
        Assert.assertEquals("αβγδεζηθικλμνξοπρστυφχψωαα", builder.toString());
    }

    @Test
    public void testUpperCaseSymbol() {
        StringBuilder builder = new StringBuilder();
        for (int i = 1; i <= 25; i++) {
            builder.append(GreekAlphabetNumbering.toGreekAlphabetNumber(i, true, true));
        }
        // Symbol font use regular WinAnsi codes for greek letters.
        Assert.assertEquals("ABGDEZHQIKLMNXOPRSTUFCYWAA", builder.toString());
    }

    @Test
    public void testLowerCaseSymbol() {
        StringBuilder builder = new StringBuilder();
        for (int i = 1; i <= 25; i++) {
            builder.append(GreekAlphabetNumbering.toGreekAlphabetNumber(i, false, true));
        }
        // Symbol font use regular WinAnsi codes for greek letters.
        Assert.assertEquals("abgdezhqiklmnxoprstufcywaa", builder.toString());
    }
}
