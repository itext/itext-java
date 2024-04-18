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
package com.itextpdf.layout;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.constants.StandardFontFamilies;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.font.FontCharacteristics;
import com.itextpdf.layout.font.FontInfo;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.layout.font.FontSelector;
import com.itextpdf.layout.font.FontSet;
import com.itextpdf.layout.font.RangeBuilder;
import com.itextpdf.layout.font.selectorstrategy.BestMatchFontSelectorStrategy.BestMatchFontSelectorStrategyFactory;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class FontSelectorTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/FontSelectorTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/layout/FontSelectorTest/";
    public static final String fontsFolder = "./src/test/resources/com/itextpdf/layout/fonts/";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void cyrillicAndLatinGroup() throws Exception {
        String fileName = "cyrillicAndLatinGroup";
        String outFileName = destinationFolder + "cyrillicAndLatinGroup.pdf";
        String cmpFileName = sourceFolder + "cmp_" + fileName + ".pdf";

        FontProvider sel = new FontProvider();
        Assert.assertTrue(sel.addFont(fontsFolder + "NotoSans-Regular.ttf"));
        Assert.assertTrue(sel.addFont(fontsFolder + "FreeSans.ttf"));
        Assert.assertTrue(sel.getFontSet().addFont(fontsFolder + "Puritan2.otf", PdfEncodings.IDENTITY_H, "Puritan42"));


        String s = "Hello world! Здравствуй мир! Hello world! Здравствуй мир!";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));
        Document doc = new Document(pdfDoc);

        doc.setFontProvider(sel);
        doc.setProperty(Property.FONT, new String[] {"Puritan42"});
        Text text = new Text(s).setBackgroundColor(ColorConstants.LIGHT_GRAY);
        Paragraph paragraph = new Paragraph(text);
        doc.add(paragraph);
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff" + fileName));
    }

    @Test
    public void cyrillicAndLatinGroup2() throws Exception {
        String fileName = "cyrillicAndLatinGroup2";
        String outFileName = destinationFolder + fileName + ".pdf";
        String cmpFileName = sourceFolder + "cmp_" + fileName + ".pdf";

        FontProvider sel = new FontProvider();
        Assert.assertTrue(sel.addFont(fontsFolder + "Puritan2.otf"));
        Assert.assertTrue(sel.addFont(fontsFolder + "NotoSans-Regular.ttf"));
        Assert.assertTrue(sel.addFont(fontsFolder + "FreeSans.ttf"));


        String s = "Hello world! Здравствуй мир! Hello world! Здравствуй мир!";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));
        Document doc = new Document(pdfDoc);

        doc.setFontProvider(sel);
        doc.setFontFamily("Puritan 2.0", "FreeSans");
        Text text = new Text(s).setBackgroundColor(ColorConstants.LIGHT_GRAY);
        Paragraph paragraph = new Paragraph(text);
        doc.add(paragraph);
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff" + fileName));
    }

    @Test
    public void cyrillicAndLatinGroup3() throws Exception {
        String fileName = "cyrillicAndLatinGroup3";
        String outFileName = destinationFolder + fileName + ".pdf";
        String cmpFileName = sourceFolder + "cmp_" + fileName + ".pdf";

        FontProvider sel = new FontProvider();

        Assert.assertTrue(sel.addFont(fontsFolder + "FreeSans.ttf"));
        Assert.assertTrue(sel.addFont(fontsFolder + "NotoSans-Regular.ttf"));
        Assert.assertTrue(sel.addFont(fontsFolder + "Puritan2.otf"));


        String s = "Hello world! Здравствуй мир! Hello world! Здравствуй мир!";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));
        Document doc = new Document(pdfDoc);

        doc.setFontProvider(sel);
        doc.setFontFamily(Arrays.asList("Puritan 2.0", "Noto Sans"));
        Text text = new Text(s).setBackgroundColor(ColorConstants.LIGHT_GRAY);
        Paragraph paragraph = new Paragraph(text);
        doc.add(paragraph);
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff" + fileName));
    }

    @Test
    public void cyrillicAndLatinGroupFontAsStringValue() throws Exception {
        String fileName = "cyrillicAndLatinGroupDeprecatedFontAsStringValue";
        String outFileName = destinationFolder + fileName + ".pdf";
        String cmpFileName = sourceFolder + "cmp_" + fileName + ".pdf";

        FontProvider sel = new FontProvider();

        Assert.assertTrue(sel.addFont(fontsFolder + "FreeSans.ttf"));
        Assert.assertTrue(sel.addFont(fontsFolder + "NotoSans-Regular.ttf"));
        Assert.assertTrue(sel.addFont(fontsFolder + "Puritan2.otf"));


        String s = "Hello world! Здравствуй мир! Hello world! Здравствуй мир!";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));
        Document doc = new Document(pdfDoc);

        doc.setFontProvider(sel);
        doc.setProperty(Property.FONT, "'Puritan', \"FreeSans\"");
        Text text = new Text(s).setBackgroundColor(ColorConstants.LIGHT_GRAY);
        Paragraph paragraph = new Paragraph(text);
        Exception exception = Assert.assertThrows(IllegalStateException.class, () -> {
            doc.add(paragraph);
            doc.close();
            Assert.assertNull(
                    new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff" + fileName));
        });
        Assert.assertEquals("Invalid FONT property value type.", exception.getMessage());
    }

    @Test
    public void latinAndNotdefGroup() throws Exception {
        String fileName = "latinAndNotdefGroup";
        String outFileName = destinationFolder + fileName + ".pdf";
        String cmpFileName = sourceFolder + "cmp_" + fileName + ".pdf";

        FontProvider sel = new FontProvider();
        Assert.assertTrue(sel.addFont(fontsFolder + "Puritan2.otf"));

        String s = "Hello мир!";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));
        Document doc = new Document(pdfDoc);

        doc.setFontProvider(sel);
        doc.setFontFamily("Puritan 2.0");
        Text text = new Text(s).setBackgroundColor(ColorConstants.LIGHT_GRAY);
        Paragraph paragraph = new Paragraph(text);
        doc.add(paragraph);
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff" + fileName));
    }

    @Test
    public void customFontWeight() throws Exception {
        String fileName = "customFontWeight";
        String outFileName = destinationFolder + fileName + ".pdf";
        String cmpFileName = sourceFolder + "cmp_" + fileName + ".pdf";

        FontProvider sel = new FontProvider();
        sel.getFontSet().addFont(StandardFonts.HELVETICA);
        sel.getFontSet().addFont(StandardFonts.HELVETICA_BOLD);
        sel.getFontSet().addFont(StandardFonts.TIMES_ROMAN);
        // The provided alias is incorrect. It'll be used as a font's family, but since the name is invalid, the font shouldn't be selected
        sel.getFontSet().addFont(StandardFonts.TIMES_BOLD, null, "Times-Roman Bold");

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));
        Document doc = new Document(pdfDoc);
        doc.setFontProvider(sel);

        Div div = new Div().setFontFamily(StandardFonts.TIMES_ROMAN);
        Paragraph paragraph = new Paragraph("Times Roman Bold text");
        paragraph.setProperty(Property.FONT_WEIGHT, "bold");
        div.add(paragraph);
        doc.add(div);

        doc.add(new Paragraph("UPD: The paragraph above should be written in Helvetica-Bold. The provided alias for Times-Bold was incorrect. It was used as a font's family, but since the name is invalid, the font wasn't selected."));

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff" + fileName));
    }

    @Test
    public void customFontWeight2() throws Exception {
        String fileName = "customFontWeight2";
        String outFileName = destinationFolder + fileName + ".pdf";
        String cmpFileName = sourceFolder + "cmp_" + fileName + ".pdf";

        FontProvider sel = new FontProvider();
        sel.getFontSet().addFont(StandardFonts.HELVETICA);
        sel.getFontSet().addFont(StandardFonts.HELVETICA_BOLD);
        sel.getFontSet().addFont(StandardFonts.TIMES_ROMAN);
        sel.getFontSet().addFont(StandardFonts.TIMES_BOLD);

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));
        Document doc = new Document(pdfDoc);
        doc.setFontProvider(sel);

        Div div = new Div().setFontFamily(StandardFontFamilies.TIMES);
        Paragraph paragraph = new Paragraph("Times Roman Bold text");
        paragraph.setProperty(Property.FONT_WEIGHT, "bold");
        div.add(paragraph);
        doc.add(div);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff" + fileName));
    }

    @Test
    public void customFontWeight3() throws Exception {
        String fileName = "customFontWeight3";
        String outFileName = destinationFolder + fileName + ".pdf";
        String cmpFileName = sourceFolder + "cmp_" + fileName + ".pdf";

        FontProvider sel = new FontProvider();
        sel.getFontSet().addFont(StandardFonts.HELVETICA);
        sel.getFontSet().addFont(StandardFonts.HELVETICA_BOLD);
        sel.getFontSet().addFont(StandardFonts.TIMES_ROMAN);
        // correct alias
        sel.getFontSet().addFont(StandardFonts.TIMES_BOLD);

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));
        Document doc = new Document(pdfDoc);
        doc.setFontProvider(sel);

        Div div = new Div().setFontFamily(StandardFontFamilies.TIMES);
        Paragraph paragraph = new Paragraph("Times Roman Bold text");
        paragraph.setProperty(Property.FONT_WEIGHT, "bold");
        div.add(paragraph);
        doc.add(div);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff" + fileName));
    }

    @Test
    public void standardPdfFonts() throws Exception {
        String fileName = "standardPdfFonts";
        String outFileName = destinationFolder + fileName + ".pdf";
        String cmpFileName = sourceFolder + "cmp_" + fileName + ".pdf";

        FontProvider sel = new FontProvider();
        sel.addStandardPdfFonts();

        String s = "Hello world!";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));
        Document doc = new Document(pdfDoc);
        doc.setFontProvider(sel);

        Paragraph paragraph = new Paragraph(s);
        paragraph.setFontFamily("Courier");
        doc.add(paragraph);
        paragraph = new Paragraph(s);
        paragraph.setProperty(Property.FONT, new String[] {"Times"});
        doc.add(paragraph);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff" + fileName));
    }

    @Test
    public void searchNames() {
        FontProvider sel = new FontProvider();
        Assert.assertTrue(sel.addFont(fontsFolder + "NotoSans-Regular.ttf"));
        Assert.assertTrue(sel.addFont(fontsFolder + "FreeSans.ttf"));
        Assert.assertTrue(sel.getFontSet().addFont(fontsFolder + "Puritan2.otf", PdfEncodings.IDENTITY_H, "Puritan42"));
        Collection<FontInfo> fonts = sel.getFontSet().get("puritan2");
        Assert.assertTrue("Puritan not found!", fonts.size() != 0);

        FontInfo puritan = getFirst(fonts);

        Assert.assertFalse("Replace existed font", sel.getFontSet().addFont(puritan, "Puritan42"));
        Assert.assertFalse("Replace existed font", sel.getFontSet().addFont(puritan));

        Assert.assertTrue("NotoSans not found!", sel.getFontSet().contains("NotoSans"));
        Assert.assertTrue("NotoSans not found!", sel.getFontSet().contains("Noto Sans"));
        Assert.assertTrue("FreeSans not found!", sel.getFontSet().contains("FreeSans"));
        Assert.assertTrue("FreeSans not found!", sel.getFontSet().contains("Free Sans"));
        Assert.assertTrue("Puritan 2.0 not found!", sel.getFontSet().contains("puritan 2.0 regular"));
        Assert.assertTrue("Puritan 2.0 not found!", sel.getFontSet().contains("puritan2"));
        Assert.assertFalse("Puritan42 found!", sel.getFontSet().contains("puritan42"));

        Assert.assertEquals("Puritan 2.0 not found!", puritan, getFirst(sel.getFontSet().get("puritan 2.0 regular")));
        Assert.assertEquals("Puritan 2.0 not found!", puritan, getFirst(sel.getFontSet().get("puritan2")));
        Assert.assertTrue("Puritan42 found!", getFirst(sel.getFontSet().get("puritan42")) == null);
    }

    @Test
    public void searchNames2() {
        FontProvider sel = new FontProvider();
        Assert.assertTrue(sel.getFontSet().addFont(fontsFolder + "NotoSans-Regular.ttf"));
        Assert.assertTrue(sel.getFontSet().addFont(fontsFolder + "FreeSans.ttf"));
        Assert.assertTrue(sel.getFontSet().addFont(fontsFolder + "Puritan2.otf", PdfEncodings.IDENTITY_H, "Puritan42"));


        Collection<FontInfo> fonts = sel.getFontSet().get("puritan2");
        Assert.assertTrue("Puritan not found!", fonts.size() != 0);
        FontInfo puritan = getFirst(fonts);

        fonts = sel.getFontSet().get("NotoSans");
        Assert.assertTrue("NotoSans not found!", fonts.size() != 0);
        FontInfo notoSans = getFirst(fonts);

        fonts = sel.getFontSet().get("FreeSans");
        Assert.assertTrue("FreeSans not found!", fonts.size() != 0);
        FontInfo freeSans = getFirst(fonts);

        Assert.assertTrue("NotoSans not found!", sel.getFontSet().contains("NotoSans"));
        Assert.assertTrue("NotoSans not found!", sel.getFontSet().contains("Noto Sans"));
        Assert.assertTrue("FreeSans not found!", sel.getFontSet().contains("FreeSans"));
        Assert.assertTrue("FreeSans not found!", sel.getFontSet().contains("Free Sans"));
        Assert.assertTrue("Puritan 2.0 not found!", sel.getFontSet().contains("puritan 2.0 regular"));
        Assert.assertTrue("Puritan 2.0 not found!", sel.getFontSet().contains("puritan2"));
        Assert.assertFalse("Puritan42 found!", sel.getFontSet().contains("puritan42"));

        Assert.assertEquals("NotoSans not found!", notoSans, getFirst(sel.getFontSet().get("NotoSans")));
        Assert.assertEquals("NotoSans not found!", notoSans, getFirst(sel.getFontSet().get("Noto Sans")));
        Assert.assertEquals("FreeSans not found!", freeSans, getFirst(sel.getFontSet().get("FreeSans")));
        Assert.assertEquals("FreeSans not found!", freeSans, getFirst(sel.getFontSet().get("Free Sans")));
        Assert.assertEquals("Puritan 2.0 not found!", puritan, getFirst(sel.getFontSet().get("puritan 2.0 regular")));
        Assert.assertEquals("Puritan 2.0 not found!", puritan, getFirst(sel.getFontSet().get("puritan2")));
        Assert.assertTrue("Puritan42 found!", getFirst(sel.getFontSet().get("puritan42")) == null);
    }

    @Test
    public void searchFontAliasWithUnicodeChars() {

        // фонт1
        String cyrillicAlias = "\u0444\u043E\u043D\u04421";

        // γραμματοσειρά2
        String greekAlias = "\u03B3\u03C1\u03B1\u03BC\u03BC\u03B1\u03C4\u03BF\u03C3\u03B5\u03B9\u03C1\u03AC2";

        // フォント3
        String japaneseAlias = "\u30D5\u30A9\u30F3\u30C83";
        Map<String, String> aliasToFontName = new LinkedHashMap<>();
        aliasToFontName.put(cyrillicAlias, "NotoSans-Regular.ttf");
        aliasToFontName.put(greekAlias, "FreeSans.ttf");
        aliasToFontName.put(japaneseAlias, "Puritan2.otf");


        FontProvider provider = new FontProvider();
        for (Map.Entry<String, String> e : aliasToFontName.entrySet()) {
            provider.getFontSet().addFont(fontsFolder + e.getValue(), PdfEncodings.IDENTITY_H, e.getKey());
        }

        Set<String> actualAliases = new HashSet<>();
        for (FontInfo fontInfo : provider.getFontSet().getFonts()) {
            actualAliases.add(fontInfo.getAlias());
        }
        Set<String> expectedAliases = aliasToFontName.keySet();
        Assert.assertTrue(actualAliases.containsAll(expectedAliases) && expectedAliases.containsAll(actualAliases));

        for (String fontAlias : expectedAliases) {
            PdfFont pdfFont = provider.getPdfFont(provider.getFontSelector(Collections.singletonList(fontAlias), new FontCharacteristics()).bestMatch());
            String fontName = pdfFont.getFontProgram().getFontNames().getFontName();
            Assert.assertTrue(aliasToFontName.get(fontAlias).contains(fontName));
        }
    }

    @Test
    public void writeTextInFontWhichAliasWithUnicodeChars() throws IOException, InterruptedException {
        String fileName = "writeTextInFontWhichAliasWithUnicodeChars";
        String outFileName = destinationFolder + fileName + ".pdf";
        String cmpFileName = sourceFolder + "cmp_" + fileName + ".pdf";

        // フォント3
        String japaneseAlias = "\u30D5\u30A9\u30F3\u30C83";
        FontProvider provider = new FontProvider();
        provider.addFont(fontsFolder + "NotoSans-Regular.ttf");
        provider.getFontSet().addFont(fontsFolder + "Puritan2.otf", PdfEncodings.IDENTITY_H, japaneseAlias);
        provider.addFont(fontsFolder + "FreeSans.ttf");

        String s = "Hello world!";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));
        Document doc = new Document(pdfDoc);

        doc.setFontProvider(provider);
        Paragraph paragraph = new Paragraph(new Text(s).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        paragraph.setFontFamily(japaneseAlias);
        doc.add(paragraph);
        doc.close();

        // Text shall be written in Puritan 2.0
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder));
    }


    @Test
    public void cyrillicAndLatinWithUnicodeRange() throws Exception {
        String fileName = "cyrillicAndLatinWithUnicodeRange";
        String outFileName = destinationFolder + fileName + ".pdf";
        String cmpFileName = sourceFolder + "cmp_" + fileName + ".pdf";

        FontProvider sel = new FontProvider();
        Assert.assertTrue(sel.getFontSet().addFont(fontsFolder + "NotoSans-Regular.ttf", null, "FontAlias", new RangeBuilder(0, 255).create()));
        Assert.assertTrue(sel.getFontSet().addFont(fontsFolder + "FreeSans.ttf", null, "FontAlias", new RangeBuilder(1024, 1279).create()));
        Assert.assertTrue(sel.getFontSet().size() == 2);

        String s = "Hello world! Здравствуй мир! Hello world! Здравствуй мир!";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));
        Document doc = new Document(pdfDoc);

        doc.setFontProvider(sel);
        doc.setProperty(Property.FONT, new String[] {"FontAlias"});
        Text text = new Text(s).setBackgroundColor(ColorConstants.LIGHT_GRAY);
        Paragraph paragraph = new Paragraph(text);
        doc.add(paragraph);
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff" + fileName));
    }

    @Test
    public void notSignificantCharacterOfTheFontWithUnicodeRange() throws Exception {
        String outFileName = destinationFolder + "notSignificantCharacterOfTheFontWithUnicodeRange.pdf";
        String cmpFileName = sourceFolder + "cmp_notSignificantCharacterOfTheFontWithUnicodeRange.pdf";

        FontProvider sel = new FontProvider();
        sel.setFontSelectorStrategyFactory(new BestMatchFontSelectorStrategyFactory());
        Assert.assertTrue(sel.getFontSet().addFont(fontsFolder + "NotoSansCJKjp-Bold.otf", null, "FontAlias", new RangeBuilder(117, 117).create())); // just 'u' letter
        Assert.assertTrue(sel.getFontSet().addFont(fontsFolder + "FreeSans.ttf", null, "FontAlias", new RangeBuilder(106, 113).create()));// 'j', 'm' and 'p' are in that interval

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        doc.setFontProvider(sel);
        doc.setProperty(Property.FONT, new String[] {"FontAlias"});

        doc.add(new Paragraph("jump"));

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void checkThreeFontsInOneLineWithUnicodeRange() throws Exception {
        String outFileName = destinationFolder + "checkThreeFontsInOneLineWithUnicodeRange.pdf";
        String cmpFileName = sourceFolder + "cmp_checkThreeFontsInOneLineWithUnicodeRange.pdf";

        FontProvider sel = new FontProvider();
        sel.setFontSelectorStrategyFactory(new BestMatchFontSelectorStrategyFactory());
        // 'a', 'b' and 'c' are in that interval
        Assert.assertTrue(sel.getFontSet().addFont(fontsFolder + "NotoSansCJKjp-Bold.otf", null, "FontAlias", new RangeBuilder(97, 99).create()));
        // 'd', 'e' and 'f' are in that interval
        Assert.assertTrue(sel.getFontSet().addFont(fontsFolder + "FreeSans.ttf", null, "FontAlias", new RangeBuilder(100, 102).create()));
        // 'x', 'y' and 'z' are in that interval
        Assert.assertTrue(sel.getFontSet().addFont(fontsFolder + "Puritan2.otf", null, "FontAlias", new RangeBuilder(120, 122).create()));

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        doc.setFontProvider(sel);
        doc.setProperty(Property.FONT, new String[] {"FontAlias"});

        doc.add(new Paragraph("abc def xyz"));

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void duplicateFontWithUnicodeRange() throws Exception {
        String fileName = "duplicateFontWithUnicodeRange";
        //In the result pdf will be two equal fonts but with different subsets
        String outFileName = destinationFolder + fileName + ".pdf";
        String cmpFileName = sourceFolder + "cmp_" + fileName + ".pdf";

        FontProvider sel = new FontProvider();
        Assert.assertTrue(sel.getFontSet().addFont(fontsFolder + "NotoSans-Regular.ttf", null, "FontAlias", new RangeBuilder(0, 255).create()));
        Assert.assertTrue(sel.getFontSet().addFont(fontsFolder + "NotoSans-Regular.ttf", null, "FontAlias", new RangeBuilder(1024, 1279).create()));
        Assert.assertTrue(sel.getFontSet().size() == 2);

        String s = "Hello world! Здравствуй мир! Hello world! Здравствуй мир!";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));
        Document doc = new Document(pdfDoc);

        doc.setFontProvider(sel);
        doc.setProperty(Property.FONT, new String[] {"FontAlias"});
        Text text = new Text(s).setBackgroundColor(ColorConstants.LIGHT_GRAY);
        Paragraph paragraph = new Paragraph(text);
        doc.add(paragraph);
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff" + fileName));
    }

    @Test
    public void singleFontWithUnicodeRange() throws Exception {
        String fileName = "singleFontWithUnicodeRange";
        //In the result pdf will be two equal fonts but with different subsets
        String outFileName = destinationFolder + fileName + ".pdf";
        String cmpFileName = sourceFolder + "cmp_" + fileName + ".pdf";

        FontProvider sel = new FontProvider();
        Assert.assertTrue(sel.getFontSet().addFont(fontsFolder + "NotoSans-Regular.ttf", null, "FontAlias"));
        Assert.assertFalse(sel.getFontSet().addFont(fontsFolder + "NotoSans-Regular.ttf", null, "FontAlias"));
        Assert.assertTrue(sel.getFontSet().size() == 1);

        String s = "Hello world! Здравствуй мир! Hello world! Здравствуй мир!";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));
        Document doc = new Document(pdfDoc);

        doc.setFontProvider(sel);
        doc.setProperty(Property.FONT, new String[] {"FontAlias"});
        Text text = new Text(s).setBackgroundColor(ColorConstants.LIGHT_GRAY);
        Paragraph paragraph = new Paragraph(text);
        doc.add(paragraph);
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff" + fileName));
    }

    @Test
    public void standardFontSetTimesTest01() {
        checkSelector(getStandardFontSet().getFonts(), "Times", "Times-Roman", "Times-Bold", "Times-Italic", "Times-BoldItalic");
    }

    @Test
    public void standardFontSetHelveticaTest01() {
        checkSelector(getStandardFontSet().getFonts(), "Helvetica", "Helvetica", "Helvetica-Bold", "Helvetica-Oblique", "Helvetica-BoldOblique");
    }

    @Test
    public void standardFontSetCourierTest01() {
        checkSelector(getStandardFontSet().getFonts(), "Courier", "Courier", "Courier-Bold", "Courier-Oblique", "Courier-BoldOblique");
    }

    @Test
    public void openSansFontSetIncorrectNameTest01() {
        FontSet set = getOpenSansFontSet();
        addTimesFonts(set);
        Collection<FontInfo> fontInfoCollection = set.getFonts();
        List<String> fontFamilies = new ArrayList<>();
        fontFamilies.add("Open Sans");

        // Normal

        FontCharacteristics fc = new FontCharacteristics();
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, "OpenSans-Regular");

        fc = new FontCharacteristics();
        fc.setFontWeight((short) 300);
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, "OpenSans-Light");

        fc = new FontCharacteristics();
        fc.setFontWeight((short) 100);
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, "OpenSans-Light");

        fc = new FontCharacteristics();
        fc.setFontWeight("normal");
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, "OpenSans-Regular");

        fc = new FontCharacteristics();
        fc.setFontStyle("normal");
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, "OpenSans-Regular");

        // Bold

        fc = new FontCharacteristics();
        fc.setBoldFlag(true);
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, "OpenSans-SemiBold");

        fc = new FontCharacteristics();
        fc.setFontWeight("bold");
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, "OpenSans-Bold");

        fc = new FontCharacteristics();
        fc.setFontWeight((short) 700);
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, "OpenSans-Bold");

        fc = new FontCharacteristics();
        fc.setFontWeight((short) 800);
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, "OpenSans-ExtraBold");

        // Italic

        fc = new FontCharacteristics();
        fc.setFontStyle("italic");
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, "OpenSans-Italic");

        fc = new FontCharacteristics();
        fc.setFontStyle("italic");
        fc.setFontWeight("normal");
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, "OpenSans-Italic");

        fc = new FontCharacteristics();
        fc.setFontStyle("italic");
        fc.setFontWeight((short) 300);
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, "OpenSans-LightItalic");

        fc = new FontCharacteristics();
        fc.setFontStyle("italic");
        fc.setFontWeight((short) 500);
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, "OpenSans-Italic");

        fc = new FontCharacteristics();
        fc.setFontStyle("oblique");
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, "OpenSans-Italic");

        // BoldItalic

        fc = new FontCharacteristics();
        fc.setFontStyle("italic");
        fc.setFontWeight("bold");
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, "OpenSans-BoldItalic");

        fc = new FontCharacteristics();
        fc.setFontStyle("oblique");
        fc.setFontWeight("bold");
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, "OpenSans-BoldItalic");

        fc = new FontCharacteristics();
        fc.setFontStyle("italic");
        fc.setFontWeight((short) 700);
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, "OpenSans-BoldItalic");

        fc = new FontCharacteristics();
        fc.setFontStyle("italic");
        fc.setFontWeight((short) 800);
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, "OpenSans-ExtraBoldItalic");
    }

    @Test
    public void openSansFontSetRegularTest01() {
        FontSet set = getOpenSansFontSet();
        addTimesFonts(set);
        Collection<FontInfo> fontInfoCollection = set.getFonts();
        List<String> fontFamilies = new ArrayList<>();
        fontFamilies.add("Open Sans");

        // Normal

        FontCharacteristics fc = new FontCharacteristics();
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, "OpenSans-Regular");

        fc = new FontCharacteristics();
        fc.setFontWeight((short) 300);
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, "OpenSans-Light");

        fc = new FontCharacteristics();
        fc.setFontWeight((short) 100);
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, "OpenSans-Light");

        fc = new FontCharacteristics();
        fc.setFontWeight("normal");
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, "OpenSans-Regular");

        fc = new FontCharacteristics();
        fc.setFontStyle("normal");
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, "OpenSans-Regular");

        // Bold

        fc = new FontCharacteristics();
        fc.setBoldFlag(true);
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, "OpenSans-SemiBold");

        fc = new FontCharacteristics();
        fc.setFontWeight("bold");
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, "OpenSans-Bold");

        fc = new FontCharacteristics();
        fc.setFontWeight((short) 700);
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, "OpenSans-Bold");

        fc = new FontCharacteristics();
        fc.setFontWeight((short) 800);
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, "OpenSans-ExtraBold");

        // Italic

        fc = new FontCharacteristics();
        fc.setFontStyle("italic");
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, "OpenSans-Italic");

        fc = new FontCharacteristics();
        fc.setFontStyle("italic");
        fc.setFontWeight("normal");
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, "OpenSans-Italic");

        fc = new FontCharacteristics();
        fc.setFontStyle("italic");
        fc.setFontWeight((short) 300);
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, "OpenSans-LightItalic");

        fc = new FontCharacteristics();
        fc.setFontStyle("italic");
        fc.setFontWeight((short) 500);
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, "OpenSans-Italic");

        fc = new FontCharacteristics();
        fc.setFontStyle("oblique");
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, "OpenSans-Italic");

        // BoldItalic

        fc = new FontCharacteristics();
        fc.setFontStyle("italic");
        fc.setFontWeight("bold");
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, "OpenSans-BoldItalic");

        fc = new FontCharacteristics();
        fc.setFontStyle("oblique");
        fc.setFontWeight("bold");
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, "OpenSans-BoldItalic");

        fc = new FontCharacteristics();
        fc.setFontStyle("italic");
        fc.setFontWeight((short) 700);
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, "OpenSans-BoldItalic");

        fc = new FontCharacteristics();
        fc.setFontStyle("italic");
        fc.setFontWeight((short) 800);
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, "OpenSans-ExtraBoldItalic");
    }

    @Test
    // TODO DEVSIX-2127 After DEVSIX-2120 the font should be selected correctly, but the text will still need to be bolded via emulation
    public void openSansFontSetLightTest01() {
        FontSet set = getOpenSansFontSet();
        addTimesFonts(set);
        Collection<FontInfo> fontInfoCollection = set.getFonts();
        List<String> fontFamilies = new ArrayList<>();
        fontFamilies.add("Open Sans");

        // Normal

        FontCharacteristics fc = new FontCharacteristics();
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, "OpenSans-Regular");

        fc = new FontCharacteristics();
        fc.setFontWeight((short) 300);
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, "OpenSans-Light");

        fc = new FontCharacteristics();
        fc.setFontWeight((short) 100);
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, "OpenSans-Light");

        fc = new FontCharacteristics();
        fc.setFontWeight("normal");
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, "OpenSans-Regular");

        fc = new FontCharacteristics();
        fc.setFontStyle("normal");
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, "OpenSans-Regular");

        // Bold

        fc = new FontCharacteristics();
        fc.setBoldFlag(true);
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, "OpenSans-SemiBold");

        fc = new FontCharacteristics();
        fc.setFontWeight("bold");
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, "OpenSans-Bold");

        fc = new FontCharacteristics();
        fc.setFontWeight((short) 700);
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, "OpenSans-Bold");

        fc = new FontCharacteristics();
        fc.setFontWeight((short) 800);
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, "OpenSans-ExtraBold");

        // Italic

        fc = new FontCharacteristics();
        fc.setFontStyle("italic");
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, "OpenSans-Italic");

        fc = new FontCharacteristics();
        fc.setFontStyle("italic");
        fc.setFontWeight("normal");
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, "OpenSans-Italic");

        fc = new FontCharacteristics();
        fc.setFontStyle("italic");
        fc.setFontWeight((short) 300);
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, "OpenSans-LightItalic");

        fc = new FontCharacteristics();
        fc.setFontStyle("italic");
        fc.setFontWeight((short) 500);
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, "OpenSans-Italic");

        fc = new FontCharacteristics();
        fc.setFontStyle("oblique");
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, "OpenSans-Italic");

        // BoldItalic

        fc = new FontCharacteristics();
        fc.setFontStyle("italic");
        fc.setFontWeight("bold");
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, "OpenSans-BoldItalic");

        fc = new FontCharacteristics();
        fc.setFontStyle("oblique");
        fc.setFontWeight("bold");
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, "OpenSans-BoldItalic");

        fc = new FontCharacteristics();
        fc.setFontStyle("italic");
        fc.setFontWeight((short) 700);
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, "OpenSans-BoldItalic");

        fc = new FontCharacteristics();
        fc.setFontStyle("italic");
        fc.setFontWeight((short) 800);
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, "OpenSans-ExtraBoldItalic");
    }

    @Test
    // TODO DEVSIX-2135 if FontCharacteristics instance is not modified, font-family is parsed and 'bold' substring is considered as a reason to set bold flag in FontCharacteristics instance. That should be reviewed.
    public void openSansFontSetExtraBoldTest01() {
        FontSet set = getOpenSansFontSet();
        addTimesFonts(set);
        Collection<FontInfo> fontInfoCollection = set.getFonts();
        List<String> fontFamilies = new ArrayList<>();
        fontFamilies.add("Open Sans");

        // Normal

        FontCharacteristics fc = new FontCharacteristics();
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, "OpenSans-Regular");

        fc = new FontCharacteristics();
        fc.setFontWeight((short) 300);
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, "OpenSans-Light");

        fc = new FontCharacteristics();
        fc.setFontWeight((short) 100);
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, "OpenSans-Light");

        fc = new FontCharacteristics();
        fc.setFontWeight("normal");
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, "OpenSans-Regular");

        fc = new FontCharacteristics();
        fc.setFontStyle("normal");
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, "OpenSans-Regular");

        // Bold

        fc = new FontCharacteristics();
        fc.setBoldFlag(true);
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, "OpenSans-SemiBold");

        fc = new FontCharacteristics();
        fc.setFontWeight("bold");
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, "OpenSans-Bold");

        fc = new FontCharacteristics();
        fc.setFontWeight((short) 700);
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, "OpenSans-Bold");

        fc = new FontCharacteristics();
        fc.setFontWeight((short) 800);
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, "OpenSans-ExtraBold");

        // Italic

        fc = new FontCharacteristics();
        fc.setFontStyle("italic");
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, "OpenSans-Italic");

        fc = new FontCharacteristics();
        fc.setFontStyle("italic");
        fc.setFontWeight("normal");
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, "OpenSans-Italic");

        fc = new FontCharacteristics();
        fc.setFontStyle("italic");
        fc.setFontWeight((short) 300);
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, "OpenSans-LightItalic");

        fc = new FontCharacteristics();
        fc.setFontStyle("italic");
        fc.setFontWeight((short) 500);
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, "OpenSans-Italic");

        fc = new FontCharacteristics();
        fc.setFontStyle("oblique");
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, "OpenSans-Italic");

        // BoldItalic

        fc = new FontCharacteristics();
        fc.setFontStyle("italic");
        fc.setFontWeight("bold");
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, "OpenSans-BoldItalic");

        fc = new FontCharacteristics();
        fc.setFontStyle("oblique");
        fc.setFontWeight("bold");
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, "OpenSans-BoldItalic");

        fc = new FontCharacteristics();
        fc.setFontStyle("italic");
        fc.setFontWeight((short) 700);
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, "OpenSans-BoldItalic");

        fc = new FontCharacteristics();
        fc.setFontStyle("italic");
        fc.setFontWeight((short) 800);
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, "OpenSans-ExtraBoldItalic");
    }

    @Test
    public void openSansLightTest() {
        FontSet set = getOpenSansFontSet();
        addTimesFonts(set);
        Collection<FontInfo> fontInfoCollection = set.getFonts();
        List<String> fontFamilies = new ArrayList<>();
        fontFamilies.add("Open Sans Light");

        FontCharacteristics fc = new FontCharacteristics();
        fc.setFontWeight((short) 500);
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, "OpenSans-Light");
    }

    @Test
    public void openSansFontWeightBoldRenderingTest() throws Exception {
        String outFileName = destinationFolder + "openSansFontWeightBoldRendering.pdf";
        String cmpFileName = sourceFolder + "cmp_openSansFontWeightBoldRendering.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        FontProvider sel = new FontProvider();
        sel.getFontSet().addFont(fontsFolder + "Open_Sans/" + "OpenSans-Bold.ttf");
        sel.getFontSet().addFont(fontsFolder + "Open_Sans/" + "OpenSans-ExtraBold.ttf");
        sel.getFontSet().addFont(fontsFolder + "Open_Sans/" + "OpenSans-SemiBold.ttf");
        doc.setFontProvider(sel);

        Div div = new Div().setFontFamily("OpenSans");

        Paragraph paragraph1 = new Paragraph("Hello, OpenSansExtraBold! ");
        paragraph1.setProperty(Property.FONT_WEIGHT, "800");

        Paragraph paragraph2 = new Paragraph(new Text("Hello, OpenSansBold! "));
        paragraph2.setProperty(Property.FONT_WEIGHT, "700");

        Paragraph paragraph3 = new Paragraph(new Text("Hello, OpenSansSemiBold!"));
        paragraph3.setProperty(Property.FONT_WEIGHT, "600");

        div
                .add(paragraph1)
                .add(paragraph2)
                .add(paragraph3);
        doc.add(div);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder));
    }

    @Test
    public void openSansFontWeightNotBoldRenderingTest() throws Exception {
        String outFileName = destinationFolder + "openSansFontWeightNotBoldRendering.pdf";
        String cmpFileName = sourceFolder + "cmp_openSansFontWeightNotBoldRendering.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        FontProvider sel = new FontProvider();
        sel.getFontSet().addFont(fontsFolder + "Open_Sans/" + "OpenSans-Regular.ttf");
        sel.getFontSet().addFont(fontsFolder + "Open_Sans/" + "OpenSans-Light.ttf");
        doc.setFontProvider(sel);

        Div div = new Div().setFontFamily("OpenSans");

        Paragraph paragraph1 = new Paragraph("Hello, OpenSansRegular! ");
        paragraph1.setProperty(Property.FONT_WEIGHT, "400");

        Paragraph paragraph2 = new Paragraph(new Text("Hello, OpenSansLight! "));
        paragraph2.setProperty(Property.FONT_WEIGHT, "300");

        div
                .add(paragraph1)
                .add(paragraph2);
        doc.add(div);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder));
    }

    @Test
    public void openSansOutOfBoldFontWeightTest() {
        String openSansFolder = "Open_Sans/";

        FontSet set = new FontSet();
        set.addFont(fontsFolder + openSansFolder + "OpenSans-Bold.ttf");
        set.addFont(fontsFolder + openSansFolder + "OpenSans-ExtraBold.ttf");

        List<String> fontFamilies = new ArrayList<>();
        fontFamilies.add("OpenSans");

        FontCharacteristics fc = new FontCharacteristics();
        fc.setFontWeight((short) 400);

        Assert.assertEquals("OpenSans-Bold", new FontSelector(set.getFonts(), fontFamilies, fc).bestMatch().getDescriptor().getFontName());
    }

    @Test
    public void openSansOutOfMixedFontWeightTest() {
        String openSansFolder = "Open_Sans/";

        FontSet set = new FontSet();
        set.addFont(fontsFolder + openSansFolder + "OpenSans-Light.ttf");
        set.addFont(fontsFolder + openSansFolder + "OpenSans-SemiBold.ttf");

        List<String> fontFamilies = new ArrayList<>();
        fontFamilies.add("OpenSans");

        FontCharacteristics fc = new FontCharacteristics();
        fc.setFontWeight((short) 100);

        Assert.assertEquals("OpenSans-Light",
                new FontSelector(set.getFonts(), fontFamilies, fc).bestMatch().getDescriptor().getFontName());

        fc = new FontCharacteristics();
        fc.setFontWeight((short) 600);

        Assert.assertEquals("OpenSans-SemiBold",
                new FontSelector(set.getFonts(), fontFamilies, fc).bestMatch().getDescriptor().getFontName());
    }

    @Test
    // TODO: DEVSIX-2120 Currently light and regular fonts have the same score. When fixed update assertion to "OpenSans-Regular"
    public void openSansOutOfNotBoldFontWeightTest() {
        String openSansFolder = "Open_Sans/";

        FontSet set = new FontSet();
        set.addFont(fontsFolder + openSansFolder + "OpenSans-Light.ttf");
        set.addFont(fontsFolder + openSansFolder + "OpenSans-Regular.ttf");

        List<String> fontFamilies = new ArrayList<>();
        fontFamilies.add("OpenSans");

        FontCharacteristics fc = new FontCharacteristics();
        fc.setFontWeight((short) 700);

        Assert.assertEquals("OpenSans-Light",
                new FontSelector(set.getFonts(), fontFamilies, fc).bestMatch().getDescriptor().getFontName());
    }

    @Test
    //TODO DEVSIX-6077 FontSelector: iText checks monospaceness before looking at font-family
    public void monospaceFontIsNotSelectedInPreferenceToTestFamilyTest() {
        FontSet set = new FontSet();
        set.addFont(StandardFonts.COURIER);
        set.addFont(StandardFonts.HELVETICA);

        List<String> fontFamilies = new ArrayList<>();
        fontFamilies.add("test");
        fontFamilies.add("monospace");

        FontCharacteristics fc = new FontCharacteristics();

        //Expected font is Courier
        Assert.assertEquals("Helvetica",
                new FontSelector(set.getFonts(), fontFamilies, fc).bestMatch().getDescriptor().getFontName());
    }

    @Test
    public void family2UsedToSortFontsTest() {
        FontSet set = new FontSet();
        set.addFont(fontsFolder + "Lato/Lato-Black.ttf");
        set.addFont(fontsFolder + "Lato/Lato-Regular.ttf");
        set.addFont(fontsFolder + "Lato/Lato-Italic.ttf");
        set.addFont(fontsFolder + "Lato/Lato-Hairline.ttf");

        List<String> fontFamilies = new ArrayList<>();
        fontFamilies.add("Lato Hairline");

        FontCharacteristics fc = new FontCharacteristics();
        fc.setFontWeight((short) 300); // Between hairline (200) and regular (400)

        Assert.assertEquals("Lato-Hairline",
                new FontSelector(set.getFonts(), fontFamilies, fc).bestMatch().getDescriptor().getFontName());
    }

    private void checkSelector(Collection<FontInfo> fontInfoCollection, String fontFamily,
                               String expectedNormal, String expectedBold, String expectedItalic, String expectedBoldItalic) {
        List<String> fontFamilies = new ArrayList<>();
        fontFamilies.add(fontFamily);

        // Normal

        FontCharacteristics fc = new FontCharacteristics();
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, expectedNormal);

        fc = new FontCharacteristics();
        fc.setFontWeight((short) 300);
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, expectedNormal);

        fc = new FontCharacteristics();
        fc.setFontWeight((short) 100);
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, expectedNormal);

        fc = new FontCharacteristics();
        fc.setFontWeight("normal");
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, expectedNormal);

        fc = new FontCharacteristics();
        fc.setFontStyle("normal");
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, expectedNormal);


        // Bold

        fc = new FontCharacteristics();
        fc.setBoldFlag(true);
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, expectedBold);

        fc = new FontCharacteristics();
        fc.setFontWeight("bold");
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, expectedBold);

        fc = new FontCharacteristics();
        fc.setFontWeight((short) 700);
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, expectedBold);

        fc = new FontCharacteristics();
        fc.setFontWeight((short) 800);
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, expectedBold);


        // Italic

        fc = new FontCharacteristics();
        fc.setFontStyle("italic");
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, expectedItalic);

        fc = new FontCharacteristics();
        fc.setFontStyle("italic");
        fc.setFontWeight("normal");
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, expectedItalic);

        fc = new FontCharacteristics();
        fc.setFontStyle("italic");
        fc.setFontWeight((short) 300);
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, expectedItalic);

        fc = new FontCharacteristics();
        fc.setFontStyle("italic");
        fc.setFontWeight((short) 500);
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, expectedItalic);

        fc = new FontCharacteristics();
        fc.setFontStyle("oblique");
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, expectedItalic);


        // BoldItalic

        fc = new FontCharacteristics();
        fc.setFontStyle("italic");
        fc.setFontWeight("bold");
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, expectedBoldItalic);

        fc = new FontCharacteristics();
        fc.setFontStyle("oblique");
        fc.setFontWeight("bold");
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, expectedBoldItalic);

        fc = new FontCharacteristics();
        fc.setFontStyle("italic");
        fc.setFontWeight((short) 700);
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, expectedBoldItalic);

        fc = new FontCharacteristics();
        fc.setFontStyle("italic");
        fc.setFontWeight((short) 800);
        assertSelectedFont(fontInfoCollection, fontFamilies, fc, expectedBoldItalic);
    }


    private void assertSelectedFont(Collection<FontInfo> fontInfoCollection, List<String> fontFamilies, FontCharacteristics fc, String expectedFontName) {
        Assert.assertEquals(expectedFontName, new FontSelector(fontInfoCollection, fontFamilies, fc).bestMatch().getDescriptor().getFontName());
    }

    private static FontSet getStandardFontSet() {
        FontSet set = new FontSet();
        set.addFont(StandardFonts.COURIER);
        set.addFont(StandardFonts.COURIER_BOLD);
        set.addFont(StandardFonts.COURIER_BOLDOBLIQUE);
        set.addFont(StandardFonts.COURIER_OBLIQUE);
        set.addFont(StandardFonts.HELVETICA);
        set.addFont(StandardFonts.HELVETICA_BOLD);
        set.addFont(StandardFonts.HELVETICA_BOLDOBLIQUE);
        set.addFont(StandardFonts.HELVETICA_OBLIQUE);
        set.addFont(StandardFonts.SYMBOL);
        set.addFont(StandardFonts.ZAPFDINGBATS);
        addTimesFonts(set);
        return set;
    }

    private static FontSet getOpenSansFontSet() {
        String openSansFolder = "Open_Sans/";
        FontSet set = new FontSet();
        set.addFont(fontsFolder + openSansFolder + "OpenSans-Bold.ttf");
        set.addFont(fontsFolder + openSansFolder + "OpenSans-BoldItalic.ttf");
        set.addFont(fontsFolder + openSansFolder + "OpenSans-ExtraBold.ttf");
        set.addFont(fontsFolder + openSansFolder + "OpenSans-ExtraBoldItalic.ttf");
        set.addFont(fontsFolder + openSansFolder + "OpenSans-Light.ttf");
        set.addFont(fontsFolder + openSansFolder + "OpenSans-LightItalic.ttf");
        set.addFont(fontsFolder + openSansFolder + "OpenSans-Regular.ttf");
        set.addFont(fontsFolder + openSansFolder + "OpenSans-Italic.ttf");
        set.addFont(fontsFolder + openSansFolder + "OpenSans-SemiBold.ttf");
        set.addFont(fontsFolder + openSansFolder + "OpenSans-SemiBoldItalic.ttf");
        return set;
    }

    private static FontSet addTimesFonts(FontSet set) {
        set.addFont(StandardFonts.TIMES_ROMAN);
        set.addFont(StandardFonts.TIMES_BOLD);
        set.addFont(StandardFonts.TIMES_BOLDITALIC);
        set.addFont(StandardFonts.TIMES_ITALIC);
        return set;
    }

    private static FontInfo getFirst(Collection<FontInfo> fonts) {
        if (fonts.size() != 1) {
            return null;
        }
        //noinspection LoopStatementThatDoesntLoop
        for (FontInfo fi : fonts) {
            return fi;
        }
        return null;
    }
}
