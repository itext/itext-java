/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
package com.itextpdf.layout.font;

import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class FontCharacteristicsTest extends ExtendedITextTest {
    @Test
    public void testDefaultFontCharacteristics() {
        FontCharacteristics fontCharacteristics = new FontCharacteristics();
        Assertions.assertFalse(fontCharacteristics.isItalic());
        Assertions.assertFalse(fontCharacteristics.isBold());
        Assertions.assertFalse(fontCharacteristics.isMonospace());
        Assertions.assertTrue(fontCharacteristics.isUndefined());
        Assertions.assertEquals(400, fontCharacteristics.getFontWeight());
    }

    @Test
    public void testPositiveFontWeight() {
        FontCharacteristics fontCharacteristics = new FontCharacteristics();

        fontCharacteristics.setFontWeight((short) 50);
        Assertions.assertEquals(100, fontCharacteristics.getFontWeight());
        Assertions.assertFalse(fontCharacteristics.isUndefined());

        fontCharacteristics.setFontWeight((short) 120);
        Assertions.assertEquals(100, fontCharacteristics.getFontWeight());
        Assertions.assertFalse(fontCharacteristics.isUndefined());

        fontCharacteristics.setFontWeight((short) 340);
        Assertions.assertEquals(300, fontCharacteristics.getFontWeight());
        Assertions.assertFalse(fontCharacteristics.isUndefined());

        fontCharacteristics.setFontWeight((short) 550);
        Assertions.assertEquals(500, fontCharacteristics.getFontWeight());
        Assertions.assertFalse(fontCharacteristics.isUndefined());

        fontCharacteristics.setFontWeight((short) 885);
        Assertions.assertEquals(800, fontCharacteristics.getFontWeight());
        Assertions.assertFalse(fontCharacteristics.isUndefined());

        fontCharacteristics.setFontWeight((short) 20000);
        Assertions.assertEquals(900, fontCharacteristics.getFontWeight());
        Assertions.assertFalse(fontCharacteristics.isUndefined());
    }

    @Test
    public void testIncorrectFontWeight() {
        FontCharacteristics fontCharacteristics = new FontCharacteristics();

        fontCharacteristics.setFontWeight((short) 0);
        Assertions.assertEquals(400, fontCharacteristics.getFontWeight());
        Assertions.assertTrue(fontCharacteristics.isUndefined());

        fontCharacteristics.setFontWeight((short) -500);
        Assertions.assertEquals(400, fontCharacteristics.getFontWeight());
        Assertions.assertTrue(fontCharacteristics.isUndefined());
    }

    @Test
    public void testBoldFlag() {
        FontCharacteristics fontCharacteristics = new FontCharacteristics();

        Assertions.assertFalse(fontCharacteristics.isBold());
        Assertions.assertTrue(fontCharacteristics.isUndefined());

        fontCharacteristics.setBoldFlag(true);
        Assertions.assertTrue(fontCharacteristics.isBold());
        Assertions.assertFalse(fontCharacteristics.isUndefined());

        fontCharacteristics = new FontCharacteristics();

        fontCharacteristics.setBoldFlag(false);
        Assertions.assertFalse(fontCharacteristics.isBold());
        Assertions.assertTrue(fontCharacteristics.isUndefined());
    }

    @Test
    public void testItalicFlag() {
        FontCharacteristics fontCharacteristics = new FontCharacteristics();

        Assertions.assertFalse(fontCharacteristics.isItalic());
        Assertions.assertTrue(fontCharacteristics.isUndefined());

        fontCharacteristics.setItalicFlag(true);
        Assertions.assertTrue(fontCharacteristics.isItalic());
        Assertions.assertFalse(fontCharacteristics.isUndefined());

        fontCharacteristics = new FontCharacteristics();

        fontCharacteristics.setItalicFlag(false);
        Assertions.assertFalse(fontCharacteristics.isItalic());
        Assertions.assertTrue(fontCharacteristics.isUndefined());
    }

    @Test
    public void testMonospaceFlag() {
        FontCharacteristics fontCharacteristics = new FontCharacteristics();

        Assertions.assertFalse(fontCharacteristics.isMonospace());
        Assertions.assertTrue(fontCharacteristics.isUndefined());

        fontCharacteristics.setMonospaceFlag(true);
        Assertions.assertTrue(fontCharacteristics.isMonospace());
        Assertions.assertFalse(fontCharacteristics.isUndefined());

        fontCharacteristics = new FontCharacteristics();

        fontCharacteristics.setMonospaceFlag(false);
        Assertions.assertFalse(fontCharacteristics.isMonospace());
        Assertions.assertTrue(fontCharacteristics.isUndefined());
    }

    @Test
    public void testIncorrectFontStyle() {
        FontCharacteristics fontCharacteristics = new FontCharacteristics();
        fontCharacteristics.setFontStyle(null);
        Assertions.assertFalse(fontCharacteristics.isItalic());
        Assertions.assertTrue(fontCharacteristics.isUndefined());

        fontCharacteristics.setFontStyle("dsodkodkopsdkod");
        Assertions.assertFalse(fontCharacteristics.isItalic());
        Assertions.assertTrue(fontCharacteristics.isUndefined());

        fontCharacteristics.setFontStyle("");
        Assertions.assertFalse(fontCharacteristics.isItalic());
        Assertions.assertTrue(fontCharacteristics.isUndefined());

        fontCharacteristics.setFontStyle("-1");
        Assertions.assertFalse(fontCharacteristics.isItalic());
        Assertions.assertTrue(fontCharacteristics.isUndefined());

        fontCharacteristics.setFontStyle("bold");
        Assertions.assertFalse(fontCharacteristics.isItalic());
        Assertions.assertTrue(fontCharacteristics.isUndefined());
    }


    @Test
    public void testAllowedFontStyle() {
        FontCharacteristics fontCharacteristics = new FontCharacteristics();
        Assertions.assertFalse(fontCharacteristics.isItalic());
        Assertions.assertTrue(fontCharacteristics.isUndefined());

        fontCharacteristics = new FontCharacteristics();
        fontCharacteristics.setFontStyle("normal");
        Assertions.assertFalse(fontCharacteristics.isItalic());
        Assertions.assertTrue(fontCharacteristics.isUndefined());

        fontCharacteristics = new FontCharacteristics();
        fontCharacteristics.setFontStyle("oblique");
        Assertions.assertTrue(fontCharacteristics.isItalic());
        Assertions.assertFalse(fontCharacteristics.isUndefined());

        fontCharacteristics = new FontCharacteristics();
        fontCharacteristics.setFontStyle("italic");
        Assertions.assertTrue(fontCharacteristics.isItalic());
        Assertions.assertFalse(fontCharacteristics.isUndefined());
    }

    @Test
    public void testEquals() {
        FontCharacteristics fontCharacteristics = new FontCharacteristics();
        fontCharacteristics.setFontStyle("italic");
        fontCharacteristics.setFontWeight((short) 300);

        FontCharacteristics sameFontCharacteristics = new FontCharacteristics();
        sameFontCharacteristics.setFontStyle("italic");
        sameFontCharacteristics.setFontWeight((short) 300);
        Assertions.assertTrue(fontCharacteristics.equals(sameFontCharacteristics));

        FontCharacteristics copyFontCharacteristics = new FontCharacteristics(fontCharacteristics);
        Assertions.assertTrue(fontCharacteristics.equals(copyFontCharacteristics));

        FontCharacteristics diffFontCharacteristics = new FontCharacteristics();
        fontCharacteristics.setBoldFlag(true);
        fontCharacteristics.setFontWeight((short) 800);
        Assertions.assertFalse(fontCharacteristics.equals(diffFontCharacteristics));
    }

    @Test
    public void testHashCode() {
        FontCharacteristics fontCharacteristics = new FontCharacteristics();
        fontCharacteristics.setFontStyle("italic");
        fontCharacteristics.setFontWeight((short) 300);

        FontCharacteristics sameFontCharacteristics = new FontCharacteristics();
        sameFontCharacteristics.setFontStyle("italic");
        sameFontCharacteristics.setFontWeight((short) 300);
        Assertions.assertEquals(fontCharacteristics.hashCode(), sameFontCharacteristics.hashCode());

        FontCharacteristics copyFontCharacteristics = new FontCharacteristics(fontCharacteristics);
        Assertions.assertEquals(fontCharacteristics.hashCode(), copyFontCharacteristics.hashCode());

        FontCharacteristics diffFontCharacteristics = new FontCharacteristics();
        fontCharacteristics.setBoldFlag(true);
        fontCharacteristics.setFontWeight((short) 800);
        Assertions.assertNotEquals(fontCharacteristics.hashCode(), diffFontCharacteristics.hashCode());
    }
}
