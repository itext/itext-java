package com.itextpdf.layout.font;

import org.junit.Assert;
import org.junit.Test;

public class FontCharacteristicsTest {
    @Test
    public void testDefaultFontCharacteristics() {
        FontCharacteristics fontCharacteristics = new FontCharacteristics();
        Assert.assertFalse(fontCharacteristics.isItalic());
        Assert.assertFalse(fontCharacteristics.isBold());
        Assert.assertFalse(fontCharacteristics.isMonospace());
        Assert.assertTrue(fontCharacteristics.isUndefined());
        Assert.assertEquals(400, fontCharacteristics.getFontWeight());
    }

    @Test
    public void testPositiveFontWeight() {
        FontCharacteristics fontCharacteristics = new FontCharacteristics();

        fontCharacteristics.setFontWeight((short) 50);
        Assert.assertEquals(100, fontCharacteristics.getFontWeight());
        Assert.assertFalse(fontCharacteristics.isUndefined());

        fontCharacteristics.setFontWeight((short) 120);
        Assert.assertEquals(100, fontCharacteristics.getFontWeight());
        Assert.assertFalse(fontCharacteristics.isUndefined());

        fontCharacteristics.setFontWeight((short) 340);
        Assert.assertEquals(300, fontCharacteristics.getFontWeight());
        Assert.assertFalse(fontCharacteristics.isUndefined());

        fontCharacteristics.setFontWeight((short) 550);
        Assert.assertEquals(500, fontCharacteristics.getFontWeight());
        Assert.assertFalse(fontCharacteristics.isUndefined());

        fontCharacteristics.setFontWeight((short) 885);
        Assert.assertEquals(800, fontCharacteristics.getFontWeight());
        Assert.assertFalse(fontCharacteristics.isUndefined());

        fontCharacteristics.setFontWeight((short) 20000);
        Assert.assertEquals(900, fontCharacteristics.getFontWeight());
        Assert.assertFalse(fontCharacteristics.isUndefined());
    }

    @Test
    public void testIncorrectFontWeight() {
        FontCharacteristics fontCharacteristics = new FontCharacteristics();

        fontCharacteristics.setFontWeight((short) 0);
        Assert.assertEquals(400, fontCharacteristics.getFontWeight());
        Assert.assertTrue(fontCharacteristics.isUndefined());

        fontCharacteristics.setFontWeight((short) -500);
        Assert.assertEquals(400, fontCharacteristics.getFontWeight());
        Assert.assertTrue(fontCharacteristics.isUndefined());
    }

    @Test
    public void testBoldFlag() {
        FontCharacteristics fontCharacteristics = new FontCharacteristics();

        Assert.assertFalse(fontCharacteristics.isBold());
        Assert.assertTrue(fontCharacteristics.isUndefined());

        fontCharacteristics.setBoldFlag(true);
        Assert.assertTrue(fontCharacteristics.isBold());
        Assert.assertFalse(fontCharacteristics.isUndefined());

        fontCharacteristics = new FontCharacteristics();

        fontCharacteristics.setBoldFlag(false);
        Assert.assertFalse(fontCharacteristics.isBold());
        Assert.assertTrue(fontCharacteristics.isUndefined());
    }

    @Test
    public void testItalicFlag() {
        FontCharacteristics fontCharacteristics = new FontCharacteristics();

        Assert.assertFalse(fontCharacteristics.isItalic());
        Assert.assertTrue(fontCharacteristics.isUndefined());

        fontCharacteristics.setItalicFlag(true);
        Assert.assertTrue(fontCharacteristics.isItalic());
        Assert.assertFalse(fontCharacteristics.isUndefined());

        fontCharacteristics = new FontCharacteristics();

        fontCharacteristics.setItalicFlag(false);
        Assert.assertFalse(fontCharacteristics.isItalic());
        Assert.assertTrue(fontCharacteristics.isUndefined());
    }

    @Test
    public void testMonospaceFlag() {
        FontCharacteristics fontCharacteristics = new FontCharacteristics();

        Assert.assertFalse(fontCharacteristics.isMonospace());
        Assert.assertTrue(fontCharacteristics.isUndefined());

        fontCharacteristics.setMonospaceFlag(true);
        Assert.assertTrue(fontCharacteristics.isMonospace());
        Assert.assertFalse(fontCharacteristics.isUndefined());

        fontCharacteristics = new FontCharacteristics();

        fontCharacteristics.setMonospaceFlag(false);
        Assert.assertFalse(fontCharacteristics.isMonospace());
        Assert.assertTrue(fontCharacteristics.isUndefined());
    }

    @Test
    public void testIncorrectFontStyle() {
        FontCharacteristics fontCharacteristics = new FontCharacteristics();
        fontCharacteristics.setFontStyle(null);
        Assert.assertFalse(fontCharacteristics.isItalic());
        Assert.assertTrue(fontCharacteristics.isUndefined());

        fontCharacteristics.setFontStyle("dsodkodkopsdkod");
        Assert.assertFalse(fontCharacteristics.isItalic());
        Assert.assertTrue(fontCharacteristics.isUndefined());

        fontCharacteristics.setFontStyle("");
        Assert.assertFalse(fontCharacteristics.isItalic());
        Assert.assertTrue(fontCharacteristics.isUndefined());

        fontCharacteristics.setFontStyle("-1");
        Assert.assertFalse(fontCharacteristics.isItalic());
        Assert.assertTrue(fontCharacteristics.isUndefined());

        fontCharacteristics.setFontStyle("bold");
        Assert.assertFalse(fontCharacteristics.isItalic());
        Assert.assertTrue(fontCharacteristics.isUndefined());
    }


    @Test
    public void testAllowedFontStyle() {
        FontCharacteristics fontCharacteristics = new FontCharacteristics();
        Assert.assertFalse(fontCharacteristics.isItalic());
        Assert.assertTrue(fontCharacteristics.isUndefined());

        fontCharacteristics = new FontCharacteristics();
        fontCharacteristics.setFontStyle("normal");
        Assert.assertFalse(fontCharacteristics.isItalic());
        Assert.assertTrue(fontCharacteristics.isUndefined());

        fontCharacteristics = new FontCharacteristics();
        fontCharacteristics.setFontStyle("oblique");
        Assert.assertTrue(fontCharacteristics.isItalic());
        Assert.assertFalse(fontCharacteristics.isUndefined());

        fontCharacteristics = new FontCharacteristics();
        fontCharacteristics.setFontStyle("italic");
        Assert.assertTrue(fontCharacteristics.isItalic());
        Assert.assertFalse(fontCharacteristics.isUndefined());
    }

    @Test
    public void testEquals() {
        FontCharacteristics fontCharacteristics = new FontCharacteristics();
        fontCharacteristics.setFontStyle("italic");
        fontCharacteristics.setFontWeight((short) 300);

        FontCharacteristics sameFontCharacteristics = new FontCharacteristics();
        sameFontCharacteristics.setFontStyle("italic");
        sameFontCharacteristics.setFontWeight((short) 300);
        Assert.assertTrue(fontCharacteristics.equals(sameFontCharacteristics));

        FontCharacteristics copyFontCharacteristics = new FontCharacteristics(fontCharacteristics);
        Assert.assertTrue(fontCharacteristics.equals(copyFontCharacteristics));

        FontCharacteristics diffFontCharacteristics = new FontCharacteristics();
        fontCharacteristics.setBoldFlag(true);
        fontCharacteristics.setFontWeight((short) 800);
        Assert.assertFalse(fontCharacteristics.equals(diffFontCharacteristics));
    }

    @Test
    public void testHashCode() {
        FontCharacteristics fontCharacteristics = new FontCharacteristics();
        fontCharacteristics.setFontStyle("italic");
        fontCharacteristics.setFontWeight((short) 300);

        FontCharacteristics sameFontCharacteristics = new FontCharacteristics();
        sameFontCharacteristics.setFontStyle("italic");
        sameFontCharacteristics.setFontWeight((short) 300);
        Assert.assertEquals(fontCharacteristics.hashCode(), sameFontCharacteristics.hashCode());

        FontCharacteristics copyFontCharacteristics = new FontCharacteristics(fontCharacteristics);
        Assert.assertEquals(fontCharacteristics.hashCode(), copyFontCharacteristics.hashCode());

        FontCharacteristics diffFontCharacteristics = new FontCharacteristics();
        fontCharacteristics.setBoldFlag(true);
        fontCharacteristics.setFontWeight((short) 800);
        Assert.assertNotEquals(fontCharacteristics.hashCode(), diffFontCharacteristics.hashCode());
    }
}
