/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2018 iText Group NV
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
package com.itextpdf.layout;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.constants.StandardFontFamilies;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
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
import com.itextpdf.layout.property.Property;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
        doc.setProperty(Property.FONT, "Puritan42");
        Text text = new Text(s).setBackgroundColor(ColorConstants.LIGHT_GRAY);
        Paragraph paragraph = new Paragraph(text);
        doc.add(paragraph);
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff" + fileName));
    }

    @Test
    // TODO DEVSIX-2120 The font-family name of Puritan2.otf is 'Puritan 2.0' but this name doesn't match font-family name pattern
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
        doc.setFont("'Puritan', \"FreeSans\""); // TODO DEVSIX-2120 font-family is Puritan 2.0 here, however it doesn't match font-family pattern
        Text text = new Text(s).setBackgroundColor(ColorConstants.LIGHT_GRAY);
        Paragraph paragraph = new Paragraph(text);
        doc.add(paragraph);
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff" + fileName));
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
        doc.setFont("Puritan");
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

        Div div = new Div().setFont(StandardFonts.TIMES_ROMAN);
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

        Div div = new Div().setFont(StandardFontFamilies.TIMES);// TODO DEVSIX-2136 Update of necessary
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

        Div div = new Div().setFont(StandardFontFamilies.TIMES); // TODO DEVSIX-2136 Update of necessary
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
        paragraph.setFont("Courier");
        doc.add(paragraph);
        paragraph = new Paragraph(s);
        paragraph.setProperty(Property.FONT, "Times");
        doc.add(paragraph);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff" + fileName));
    }

    @Test
    public void searchNames() throws Exception {
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
    public void searchNames2() throws Exception {
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
        doc.setProperty(Property.FONT, "FontAlias");
        Text text = new Text(s).setBackgroundColor(ColorConstants.LIGHT_GRAY);
        Paragraph paragraph = new Paragraph(text);
        doc.add(paragraph);
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff" + fileName));
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
        doc.setProperty(Property.FONT, "FontAlias");
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
        doc.setProperty(Property.FONT, "FontAlias");
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
    // TODO DEVSIX-2120 Currently both light and regular fonts have the same score so that light is picked up lexicographically. After the changes are implemented the correct one (regular) font shall be selected and the expected constants should be updated
    // TODO Default font shall be specified.
    public void openSansFontSetIncorrectNameTest01() {
        FontSet set = getOpenSansFontSet();
        addTimesFonts(set);
        checkSelector(set.getFonts(), "OpenSans", "OpenSans-Light", "OpenSans-Bold", "OpenSans-LightItalic", "OpenSans-BoldItalic");
    }

    @Test
    // TODO DEVSIX-2120 Currently both light and regular fonts have the same score so that light is picked up lexicographically. After the changes are implemented the correct one (regular) font shall be selected and the expected constants should be updated
    // TODO Default font shall be specified.
    public void openSansFontSetRegularTest01() {
        FontSet set = getOpenSansFontSet();
        addTimesFonts(set);
        checkSelector(set.getFonts(), "Open Sans", "OpenSans-Light", "OpenSans-Bold", "OpenSans-LightItalic", "OpenSans-BoldItalic");
    }

    @Test
    // TODO DEVSIX-2127 After DEVSIX-2120 the font should be selected correctly, but the text will still need to be bolded via emulation
    // TODO DEVSIX-2120 Light subfamily is not processed
    // TODO Default font shall be specified.
    public void openSansFontSetLightTest01() {
        FontSet set = getOpenSansFontSet();
        addTimesFonts(set);
        checkSelector(set.getFonts(), "Open Sans Light", "OpenSans-Light", "OpenSans-Bold", "OpenSans-LightItalic", "OpenSans-BoldItalic");
    }

    @Test
    // TODO DEVSIX-2120 ExtraBold subfamily is not processed
    // TODO DEVSIX-2135 if FontCharacteristics instance is not modified, font-family is parsed and 'bold' substring is considered as a reason to set bold flag in FontCharacteristics instance. That should be reviewed.
    @Ignore("DEVSIX-2120: we cannot set a wrong expected string for normal font characteristics because in different contexts iText selects different fonts")
    public void openSansFontSetExtraBoldTest01() {
        FontSet set = getOpenSansFontSet();
        addTimesFonts(set);
        checkSelector(set.getFonts(), "Open Sans ExtraBold", "Times-Bold", "Times-Bold", "Times-BoldItalic", "Times-BoldItalic");
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
