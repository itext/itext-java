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
