package com.itextpdf.layout.font;

import org.junit.Assert;
import org.junit.Test;

public class FontCharacteristicsUtilsTest {
    @Test
    public void testNormalizingThinFontWeight() {
        Assert.assertEquals(100, FontCharacteristicsUtils.normalizeFontWeight((short) -10000));

        Assert.assertEquals(100, FontCharacteristicsUtils.normalizeFontWeight((short) 0));

        Assert.assertEquals(100, FontCharacteristicsUtils.normalizeFontWeight((short) 50));

        Assert.assertEquals(100, FontCharacteristicsUtils.normalizeFontWeight((short) 100));
    }

    @Test
    public void testNormalizingHeavyFontWeight() {
        Assert.assertEquals(900, FontCharacteristicsUtils.normalizeFontWeight((short) 900));

        Assert.assertEquals(900, FontCharacteristicsUtils.normalizeFontWeight((short) 1600));

        Assert.assertEquals(900, FontCharacteristicsUtils.normalizeFontWeight((short) 23000));
    }

    @Test
    public void testNormalizingNormalFontWeight() {
        Assert.assertEquals(200, FontCharacteristicsUtils.normalizeFontWeight((short) 220));

        Assert.assertEquals(400, FontCharacteristicsUtils.normalizeFontWeight((short) 456));

        Assert.assertEquals(500, FontCharacteristicsUtils.normalizeFontWeight((short) 550));

        Assert.assertEquals(600, FontCharacteristicsUtils.normalizeFontWeight((short) 620));

        Assert.assertEquals(700, FontCharacteristicsUtils.normalizeFontWeight((short) 780));
    }

    @Test
    public void testParsingIncorrectFontWeight() {
        Assert.assertEquals((short) -1, FontCharacteristicsUtils.parseFontWeight(""));

        Assert.assertEquals((short) -1, FontCharacteristicsUtils.parseFontWeight(null));

        Assert.assertEquals((short) -1, FontCharacteristicsUtils.parseFontWeight("dfgdgdfgdfgdf"));

        Assert.assertEquals((short) -1, FontCharacteristicsUtils.parseFontWeight("italic"));
    }

    @Test
    public void testParsingNumberFontWeight() {
        Assert.assertEquals((short) 100, FontCharacteristicsUtils.parseFontWeight("-1"));

        Assert.assertEquals((short) 100, FontCharacteristicsUtils.parseFontWeight("50"));

        Assert.assertEquals((short) 300, FontCharacteristicsUtils.parseFontWeight("360"));

        Assert.assertEquals((short) 900, FontCharacteristicsUtils.parseFontWeight("25000"));
    }


    @Test
    public void testParseAllowedFontWeight() {
        Assert.assertEquals((short) 400, FontCharacteristicsUtils.parseFontWeight("normal"));

        Assert.assertEquals((short) 700, FontCharacteristicsUtils.parseFontWeight("bold"));
    }
}
