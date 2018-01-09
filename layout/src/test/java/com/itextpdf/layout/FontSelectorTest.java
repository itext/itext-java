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
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.font.FontInfo;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.layout.property.Property;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.FileOutputStream;
import java.util.Collection;

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
        String outFileName = destinationFolder + "cyrillicAndLatinGroup.pdf";
        String cmpFileName = sourceFolder + "cmp_cyrillicAndLatinGroup.pdf";

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

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void cyrillicAndLatinGroup2() throws Exception {
        String outFileName = destinationFolder + "cyrillicAndLatinGroup2.pdf";
        String cmpFileName = sourceFolder + "cmp_cyrillicAndLatinGroup2.pdf";

        FontProvider sel = new FontProvider();
        Assert.assertTrue(sel.addFont(fontsFolder + "Puritan2.otf"));
        Assert.assertTrue(sel.addFont(fontsFolder + "NotoSans-Regular.ttf"));
        Assert.assertTrue(sel.addFont(fontsFolder + "FreeSans.ttf"));


        String s = "Hello world! Здравствуй мир! Hello world! Здравствуй мир!";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));
        Document doc = new Document(pdfDoc);

        doc.setFontProvider(sel);
        doc.setFont("'Puritan', \"FreeSans\"");
        Text text = new Text(s).setBackgroundColor(ColorConstants.LIGHT_GRAY);
        Paragraph paragraph = new Paragraph(text);
        doc.add(paragraph);
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void latinAndNotdefGroup() throws Exception {
        String outFileName = destinationFolder + "latinAndNotdefGroup.pdf";
        String cmpFileName = sourceFolder + "cmp_latinAndNotdefGroup.pdf";

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

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void customFontWeight() throws Exception {
        String outFileName = destinationFolder + "customFontWeight.pdf";
        String cmpFileName = sourceFolder + "cmp_customFontWeight.pdf";

        FontProvider sel = new FontProvider();
        sel.getFontSet().addFont(StandardFonts.HELVETICA);
        sel.getFontSet().addFont(StandardFonts.HELVETICA_BOLD);
        sel.getFontSet().addFont(StandardFonts.TIMES_ROMAN);
        sel.getFontSet().addFont(StandardFonts.TIMES_BOLD, null, "Times-Roman Bold");

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));
        Document doc = new Document(pdfDoc);
        doc.setFontProvider(sel);

        Div div = new Div().setFont(StandardFonts.TIMES_ROMAN);
        Paragraph paragraph = new Paragraph("Times Roman Bold text");
        paragraph.setProperty(Property.FONT_WEIGHT, "bold");
        div.add(paragraph);
        doc.add(div);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void customFontWeight2() throws Exception {
        String outFileName = destinationFolder + "customFontWeight2.pdf";
        String cmpFileName = sourceFolder + "cmp_customFontWeight2.pdf";

        FontProvider sel = new FontProvider();
        sel.getFontSet().addFont(StandardFonts.HELVETICA);
        sel.getFontSet().addFont(StandardFonts.HELVETICA_BOLD);
        sel.getFontSet().addFont(StandardFonts.TIMES_ROMAN);
        //sel.getFontSet().addFont(StandardFonts.TIMES_BOLD);

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));
        Document doc = new Document(pdfDoc);
        doc.setFontProvider(sel);

        Div div = new Div().setFont(StandardFonts.TIMES_ROMAN);
        Paragraph paragraph = new Paragraph("Times Roman Bold text");
        paragraph.setProperty(Property.FONT_WEIGHT, "bold");
        div.add(paragraph);
        doc.add(div);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }


    @Test
    public void standardPdfFonts() throws Exception {
        String outFileName = destinationFolder + "standardPdfFonts.pdf";
        String cmpFileName = sourceFolder + "cmp_standardPdfFonts.pdf";

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
        paragraph.setProperty(Property.FONT, "Times-Roman");
        doc.add(paragraph);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
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

    private static FontInfo getFirst(Collection<FontInfo> fonts) {
        if (fonts.size() != 1) {
            return null;
        }
        //noinspection LoopStatementThatDoesntLoop
        for (FontInfo fi: fonts) {
            return fi;
        }
        return null;
    }
}
