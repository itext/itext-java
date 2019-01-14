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
package com.itextpdf.io.font.otf;

import com.itextpdf.io.font.TrueTypeFont;
import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Category(UnitTest.class)
public class GlyphLineTest {

    private static List<Glyph> constructGlyphListFromString(String text, TrueTypeFont font) {
        List<Glyph> glyphList = new ArrayList<>();
        char[] chars = text.toCharArray();
        for (char letter : chars) {
            glyphList.add(font.getGlyph(letter));
        }
        return glyphList;
    }

    @Test
    public void testEquals() {
        Glyph glyph = new Glyph(200, 200, 200);
        GlyphLine.ActualText actualText = new GlyphLine.ActualText("-");

        GlyphLine one = new GlyphLine(new ArrayList<Glyph>(Arrays.asList(glyph)), new ArrayList<GlyphLine.ActualText>(Arrays.asList(actualText)), 0, 1);
        GlyphLine two = new GlyphLine(new ArrayList<Glyph>(Arrays.asList(glyph)), new ArrayList<GlyphLine.ActualText>(Arrays.asList(actualText)), 0, 1);

        one.add(glyph);
        two.add(glyph);

        one.end++;
        two.end++;

        Assert.assertTrue(one.equals(two));
    }

    @Test
    public void testOtherLinesAddition() throws IOException {
        byte[] ttf = StreamUtil.inputStreamToArray(new FileInputStream("./src/test/resources/com/itextpdf/io/font/otf/FreeSans.ttf"));
        TrueTypeFont font = new TrueTypeFont(ttf);

        GlyphLine containerLine = new GlyphLine(constructGlyphListFromString("Viva France!", font));

        GlyphLine childLine1 = new GlyphLine(constructGlyphListFromString(" Liberte", font));
        containerLine.add(childLine1);
        Assert.assertEquals(containerLine.end, 12);
        containerLine.end = 20;
        GlyphLine childLine2 = new GlyphLine(constructGlyphListFromString(" Egalite", font));
        containerLine.add(childLine2);
        Assert.assertEquals(containerLine.end, 20);
        containerLine.start = 10;
        GlyphLine childLine3 = new GlyphLine(constructGlyphListFromString(" Fraternite", font));
        containerLine.add(childLine3);
        Assert.assertEquals(containerLine.start, 10);
        containerLine.start = 0;
        containerLine.add(constructGlyphListFromString("!", font).get(0));
        containerLine.end = 40;
        Assert.assertEquals(containerLine.glyphs.size(), 40);
    }

    @Test
    public void testOtherLinesWithActualTextAddition() throws IOException {
        byte[] ttf = StreamUtil.inputStreamToArray(new FileInputStream("./src/test/resources/com/itextpdf/io/font/otf/FreeSans.ttf"));
        TrueTypeFont font = new TrueTypeFont(ttf);

        GlyphLine containerLine = new GlyphLine(constructGlyphListFromString("France", font));

        GlyphLine childLine = new GlyphLine(constructGlyphListFromString("---Liberte", font));
        childLine.setActualText(3, 10, "Viva");

        containerLine.add(childLine);
        containerLine.end = 16;
        for (int i = 0; i < 9; i++) {
            Assert.assertNull(containerLine.actualText.get(i));
        }
        for (int i = 9; i < 16; i++) {
            Assert.assertEquals("Viva", containerLine.actualText.get(i).value);
        }
        Assert.assertEquals("France---Viva", containerLine.toString());
    }

    @Test
    public void testOtherLinesWithActualTextAddition02() throws IOException {
        byte[] ttf = StreamUtil.inputStreamToArray(new FileInputStream("./src/test/resources/com/itextpdf/io/font/otf/FreeSans.ttf"));
        TrueTypeFont font = new TrueTypeFont(ttf);

        GlyphLine containerLine = new GlyphLine(constructGlyphListFromString("France", font));
        containerLine.setActualText(1, 5, "id");

        GlyphLine childLine = new GlyphLine(constructGlyphListFromString("---Liberte", font));
        childLine.setActualText(3, 10, "Viva");

        containerLine.add(childLine);
        containerLine.end = 16;
        Assert.assertNull(containerLine.actualText.get(0));
        for (int i = 1; i < 5; i++) {
            Assert.assertEquals("id", containerLine.actualText.get(i).value);
        }
        for (int i = 5; i < 9; i++) {
            Assert.assertNull(containerLine.actualText.get(i));
        }
        for (int i = 9; i < 16; i++) {
            Assert.assertEquals("Viva", containerLine.actualText.get(i).value);
        }
        Assert.assertEquals("Fide---Viva", containerLine.toString());
    }

    @Test
    public void testContentReplacingWithNullActualText() throws IOException {
        byte[] ttf = StreamUtil.inputStreamToArray(new FileInputStream("./src/test/resources/com/itextpdf/io/font/otf/FreeSans.ttf"));
        TrueTypeFont font = new TrueTypeFont(ttf);

        GlyphLine lineToBeReplaced = new GlyphLine(constructGlyphListFromString("Byelorussia", font));
        lineToBeReplaced.setActualText(1, 2, "e");

        GlyphLine lineToBeCopied = new GlyphLine(constructGlyphListFromString("Belarus", font));
        lineToBeReplaced.replaceContent(lineToBeCopied);

        // Test that no exception has been thrown. Also check the content.
        Assert.assertEquals("Belarus", lineToBeReplaced.toString());
    }
}
