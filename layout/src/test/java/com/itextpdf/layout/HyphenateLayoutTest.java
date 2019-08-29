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
package com.itextpdf.layout;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.hyphenation.HyphenationConfig;
import com.itextpdf.layout.hyphenation.Hyphenator;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class HyphenateLayoutTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/HyphenateLayoutTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/layout/HyphenateLayoutTest/";
    public static final String fontsFolder = "./src/test/resources/com/itextpdf/layout/fonts/";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    //TODO DEVSIX-3148
    public void parenthesisTest01() throws Exception {
        String outFileName = destinationFolder + "parenthesisTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_parenthesisTest01.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document document = new Document(pdfDoc, new PageSize(300, 500));

        Hyphenator hyphenator = new Hyphenator("de", "de", 3, 3);
        HyphenationConfig hyphenationConfig = new HyphenationConfig(hyphenator);
        document.setHyphenation(hyphenationConfig);

        document.add(new Paragraph("1                             (((\"|Annuitätendarlehen|\")))"));
        document.add(new Paragraph("2                              ((\"|Annuitätendarlehen|\"))"));
        document.add(new Paragraph("3                               (\"|Annuitätendarlehen|\")"));
        document.add(new Paragraph("4                                \"|Annuitätendarlehen|\""));
        document.add(new Paragraph("5                                 \"Annuitätendarlehen\""));
        document.add(new Paragraph("6                                      Annuitätendarlehen"));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void uriTest01() throws Exception {
        String outFileName = destinationFolder + "uriTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_uriTest01.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document document = new Document(pdfDoc, new PageSize(140, 500));

        Hyphenator hyphenator = new Hyphenator("en", "en", 3, 3);
        HyphenationConfig hyphenationConfig = new HyphenationConfig(hyphenator);
        document.setHyphenation(hyphenationConfig);

        Paragraph p = new Paragraph("https://stackoverflow.com/");
        document.add(p);
        p = new Paragraph("http://stackoverflow.com/");
        document.add(p);
        p = new Paragraph("m://iiiiiiii.com/");
        document.add(p);

        document.add(new AreaBreak());

        p = new Paragraph("https://stackoverflow.com/");
        p.setHyphenation(null);
        document.add(p);
        p = new Paragraph("http://stackoverflow.com/");
        p.setHyphenation(null);
        document.add(p);
        p = new Paragraph("m://iiiiiiii.com/");
        p.setHyphenation(null);
        document.add(p);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void widthTest01() throws Exception {
        String outFileName = destinationFolder + "widthTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_widthTest01.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Text text = new Text("Hier ein link https://stackoverflow " + "\n" + " (Sperrvermerk) (Sperrvermerk)" + "\n" + "„Sperrvermerk“ „Sperrvermerk“" + "\n" + "Der Sperrvermerk Sperrvermerk" + "\n" + "correct Sperr|ver|merk");
        Paragraph paragraph = new Paragraph(text);
        paragraph.setWidth(150);
        paragraph.setTextAlignment(TextAlignment.JUSTIFIED);
        paragraph.setHyphenation(new HyphenationConfig("de", "DE", 2, 2));

        doc.add(paragraph);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }


    @Test
    public void widthTest02() throws Exception {
        String outFileName = destinationFolder + "widthTest02.pdf";
        String cmpFileName = sourceFolder + "cmp_widthTest02.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Text text = new Text("Der/Die Depot-/Kontoinhaber muss/m\u00FCssen sich im Klaren dar\u00FCber sein.");
        Paragraph paragraph = new Paragraph(text);
        paragraph.setWidth(210);
        paragraph.setBorder(new SolidBorder(ColorConstants.BLACK, 1));
        paragraph.setHyphenation(new HyphenationConfig("de", "DE", 2, 2));

        doc.add(paragraph);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void widthTest03() throws Exception {
        String outFileName = destinationFolder + "widthTest03.pdf";
        String cmpFileName = sourceFolder + "cmp_widthTest03.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDocument);

        String s = "";
        s = s + "Hier ein Link: https://stackoverflow" + "\n";
        s = s + "(Sperrvermerk) (Sperrvermerk)" + "\n";
        s = s + "„Sperrvermerk“ „Sperrvermerk“" + "\n";
        s = s + "\"Sperrvermerk\" \"Sperrvermerk\"" + "\n";
        s = s + "'Sperrvermerk' 'Sperrvermerk'" + "\n";
        s = s + "Der Sperrvermerk Sperrvermerk" + "\n";
        s = s + "correct Sperr|ver|merk" + "\n";
        s = s + "Leistung Leistungen Leistung leisten" + "\n";
        s = s + "correct Leis|tung" + "\n";
        s = s + "Einmalig Einmalig Einmalig Einmalig" + "\n";
        s = s + "(Einmalig) (Einmalig) (Einmalig)" + "\n";
        s = s + "muss/müssen muss/müssen muss/müssen" + "\n";

        Paragraph p = new Paragraph(s)
                .setWidth(150)
                .setTextAlignment(TextAlignment.JUSTIFIED)
                .setBorderRight(new SolidBorder(1))
                .setHyphenation(new HyphenationConfig("de", "DE", 2, 2));
        doc.add(p);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void nonBreakingHyphenTest01() throws Exception {
        String outFileName = destinationFolder + "nonBreakingHyphenTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_nonBreakingHyphenTest01.pdf";

        PdfWriter writer = new PdfWriter(outFileName);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        Text text = new Text(
                "Dies ist ein Satz in deutscher Sprache. An hm kann man sehen, ob alle Buchstaben da sind. Und der Umbruch? 99\u2011Tage-Kaiser.\n"
                        + "Dies ist ein Satz in deutscher Sprache. An hm kann man sehen, ob alle Buchstaben da sind. Und der Umbruch? 99\u2011Days-Kaiser.\n"
                        + "Dies ist ein Satz in deutscher Sprache. An hm kann man sehen, ob alle Buchstaben da sind. Und der Umbruch? 99\u2011Frage-Kaiser.\n");
        PdfFont font = PdfFontFactory.createFont(fontsFolder + "FreeSans.ttf", PdfEncodings.IDENTITY_H, true);
        text.setFont(font);
        text.setFontSize(10);
        Paragraph paragraph = new Paragraph(text);
        paragraph.setHyphenation(new HyphenationConfig("de", "DE", 2, 2));
        document.add(paragraph);
        document.close();
        pdf.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void nonBreakingHyphenTest02() throws Exception {
        String outFileName = destinationFolder + "nonBreakingHyphenTest02.pdf";
        String cmpFileName = sourceFolder + "cmp_nonBreakingHyphenTest02.pdf";

        PdfWriter writer = new PdfWriter(outFileName);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        Div div = new Div();
        div.setHyphenation(new HyphenationConfig("en", "EN", 2, 2));
        PdfFont font = PdfFontFactory.createFont(fontsFolder + "FreeSans.ttf", PdfEncodings.IDENTITY_H, true);
        div.setFont(font);
        div.setFontSize(12);

        Text text = new Text("Hyphen hyphen hyphen hyphen hyphen hyphen hyphen hyphen hyphen hyphen hyphen ");
        Paragraph paragraph1 = new Paragraph().add(text).add("non\u2011breaking");
        div.add(paragraph1);
        Paragraph paragraph2 = new Paragraph().add(text).add("non\u2010breaking");
        div.add(paragraph2);

        document.add(div);
        document.close();
        pdf.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void hyphenSymbolTest01() throws Exception {
        String outFileName = destinationFolder + "hyphenSymbolTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_hyphenSymbolTest01.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        PdfFont font = PdfFontFactory.createFont(fontsFolder + "FreeSans.ttf", PdfEncodings.IDENTITY_H, true);
        Style style = new Style();
        style.setBorder(new SolidBorder(ColorConstants.BLACK, 1));
        style.setHyphenation(new HyphenationConfig("en", "EN", 2, 2));
        style.setFont(font);

        style.setBackgroundColor(ColorConstants.RED);
        doc.add(new Paragraph("tre\u2011").setWidth(19).addStyle(style));
        doc.add(new Paragraph("tre\u2011\u2011").setWidth(19).addStyle(style));
        doc.add(new Paragraph("r\u2011\u2011m").setWidth(19).addStyle(style));
        doc.add(new Paragraph("r\u2011\u2011\u2011\u2011\u2011\u2011mmma").setWidth(19).addStyle(style));

        style.setBackgroundColor(ColorConstants.BLUE);
        doc.add(new Paragraph("tre\u2011\u2011").setWidth(22).addStyle(style));
        doc.add(new Paragraph("tre\u2011\u2011m").setWidth(22).addStyle(style));
        doc.add(new Paragraph("\n\n\n"));

        style.setBackgroundColor(ColorConstants.GREEN);
        doc.add(new Paragraph("e\u2011\u2011m\u2011ma").setWidth(20).addStyle(style));
        doc.add(new Paragraph("tre\u2011\u2011m\u2011ma").setWidth(20).addStyle(style));
        doc.add(new Paragraph("tre\u2011\u2011m\u2011ma").setWidth(35).addStyle(style));
        doc.add(new Paragraph("tre\u2011\u2011m\u2011ma").setWidth(40).addStyle(style));

        style.setBackgroundColor(ColorConstants.YELLOW);
        doc.add(new Paragraph("ar\u2011ma").setWidth(22).addStyle(style));
        doc.add(new Paragraph("ar\u2011ma").setWidth(15).addStyle(style));
        doc.add(new Paragraph("ar\u2011").setWidth(14).addStyle(style));

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }
}
