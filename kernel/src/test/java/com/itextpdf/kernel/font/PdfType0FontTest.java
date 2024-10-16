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
package com.itextpdf.kernel.font;

import com.itextpdf.io.font.CMapEncoding;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.TrueTypeFont;
import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.io.font.otf.GlyphLine;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.test.ExtendedITextTest;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class PdfType0FontTest extends ExtendedITextTest {

    public static final String DESTINATION_FOLDER = "./target/test/resources/com/itextpdf/kernel/font/PdfType0FontTest/";
    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/kernel/font/PdfType0FontTest/";

    @Test
    public void trueTypeFontAndCmapConstructorTest() throws IOException {
        TrueTypeFont ttf = new TrueTypeFont(SOURCE_FOLDER + "NotoSerif-Regular_v1.7.ttf");

        PdfType0Font type0Font = new PdfType0Font(ttf, PdfEncodings.IDENTITY_H);

        CMapEncoding cmap = type0Font.getCmap();

        Assertions.assertNotNull(cmap);
        Assertions.assertTrue(cmap.isDirect());
        Assertions.assertFalse(cmap.hasUniMap());
        Assertions.assertNull(cmap.getUniMapName());
        Assertions.assertEquals("Adobe", cmap.getRegistry());
        Assertions.assertEquals("Identity", cmap.getOrdering());
        Assertions.assertEquals(0, cmap.getSupplement());
        Assertions.assertEquals(PdfEncodings.IDENTITY_H, cmap.getCmapName());
    }

    @Test
    public void unsupportedCmapTest() throws IOException {
        TrueTypeFont ttf = new TrueTypeFont(SOURCE_FOLDER + "NotoSerif-Regular_v1.7.ttf");

        Exception e = Assertions.assertThrows(PdfException.class,
                () -> new PdfType0Font(ttf, PdfEncodings.WINANSI)
        );
        Assertions.assertEquals(KernelExceptionMessageConstant.ONLY_IDENTITY_CMAPS_SUPPORTS_WITH_TRUETYPE, e.getMessage());
    }

    @Test
    public void dictionaryConstructorTest() throws IOException {
        String filePath = SOURCE_FOLDER + "documentWithType0Noto.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfReader(filePath));

        PdfDictionary fontDict = pdfDocument.getPage(1).getResources()
                .getResource(PdfName.Font).getAsDictionary(new PdfName("F1"));

        PdfType0Font type0Font = new PdfType0Font(fontDict);

        CMapEncoding cmap = type0Font.getCmap();

        Assertions.assertNotNull(cmap);
        Assertions.assertTrue(cmap.isDirect());
        Assertions.assertFalse(cmap.hasUniMap());
        Assertions.assertNull(cmap.getUniMapName());
        Assertions.assertEquals("Adobe", cmap.getRegistry());
        Assertions.assertEquals("Identity", cmap.getOrdering());
        Assertions.assertEquals(0, cmap.getSupplement());
        Assertions.assertEquals(PdfEncodings.IDENTITY_H, cmap.getCmapName());
    }

    @Test
    public void appendThreeSurrogatePairsTest() throws IOException {
        // this text contains three successive surrogate pairs, which should result in three glyphs
        String textWithThreeSurrogatePairs = "\uD800\uDF10\uD800\uDF00\uD800\uDF11";
        PdfFont type0Font =
                PdfFontFactory.createFont(SOURCE_FOLDER + "NotoSansOldItalic-Regular.ttf", PdfEncodings.IDENTITY_H);

        List<Glyph> glyphs = new ArrayList<>();
        type0Font.appendGlyphs(textWithThreeSurrogatePairs, 0, textWithThreeSurrogatePairs.length() - 1, glyphs);
        Assertions.assertEquals(3, glyphs.size());
    }

    @Test
    public void getUniMapFromOrderingTest() {
        Assertions.assertEquals("UniCNS-UTF16-H", PdfType0Font.getUniMapFromOrdering("CNS1", true));
        Assertions.assertEquals("UniCNS-UTF16-V", PdfType0Font.getUniMapFromOrdering("CNS1", false));

        Assertions.assertEquals("UniJIS-UTF16-H", PdfType0Font.getUniMapFromOrdering("Japan1", true));
        Assertions.assertEquals("UniJIS-UTF16-V", PdfType0Font.getUniMapFromOrdering("Japan1", false));

        Assertions.assertEquals("UniKS-UTF16-H", PdfType0Font.getUniMapFromOrdering("Korea1", true));
        Assertions.assertEquals("UniKS-UTF16-V", PdfType0Font.getUniMapFromOrdering("Korea1", false));

        Assertions.assertEquals("UniGB-UTF16-H", PdfType0Font.getUniMapFromOrdering("GB1", true));
        Assertions.assertEquals("UniGB-UTF16-V", PdfType0Font.getUniMapFromOrdering("GB1", false));

        Assertions.assertEquals("Identity-H", PdfType0Font.getUniMapFromOrdering("Identity", true));
        Assertions.assertEquals("Identity-V", PdfType0Font.getUniMapFromOrdering("Identity", false));
    }

    @Test
    public void descendantCidFontWithoutOrderingTest() {
        PdfDictionary fontDict = new PdfDictionary();
        PdfArray descendantFonts = new PdfArray();
        PdfDictionary descendantFont = new PdfDictionary();
        descendantFont.put(PdfName.CIDSystemInfo, new PdfDictionary());
        descendantFonts.add(descendantFont);
        fontDict.put(PdfName.DescendantFonts, descendantFonts);


        Exception e = Assertions.assertThrows(PdfException.class, () -> new PdfType0Font(fontDict));
        Assertions.assertEquals(KernelExceptionMessageConstant.ORDERING_SHOULD_BE_DETERMINED, e.getMessage());
    }


    private static List<Glyph> constructGlyphListFromString(String text, PdfFont font) {
        List<Glyph> glyphList = new ArrayList<>(text.length());
        char[] chars = text.toCharArray();
        for (char letter : chars) {
            glyphList.add(font.getGlyph(letter));
        }
        return glyphList;
    }

    @Test
    public void convertToBytesNoEncoderTest() throws IOException {
        byte[] expected = "十锊埋伏".getBytes(StandardCharsets.UTF_16BE);

        PdfFont font = PdfFontFactory.createFont("STSong-Light", "UniGB-UCS2-H", PdfFontFactory.EmbeddingStrategy.PREFER_NOT_EMBEDDED);
        GlyphLine line = new GlyphLine(constructGlyphListFromString("\u5341\u950a\u57cb\u4f0f", font));
        byte[] result = font.convertToBytes(line);
        Assertions.assertArrayEquals(expected, result);
    }

}
