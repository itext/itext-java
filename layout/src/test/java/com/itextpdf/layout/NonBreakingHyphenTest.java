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
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;

@Category(IntegrationTest.class)
public class NonBreakingHyphenTest extends ExtendedITextTest {
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/NonBreakingHyphenTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/layout/NonBreakingHyphenTest/";
    public static final String fontsFolder = "./src/test/resources/com/itextpdf/layout/fonts/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void nonBreakingHyphenDifferentFonts() throws IOException, InterruptedException {
        //TODO: update after fix of DEVSIX-2034
        String outFileName = destinationFolder + "nonBreakingHyphenDifferentFonts.pdf";
        String cmpFileName = sourceFolder + "cmp_nonBreakingHyphenDifferentFonts.pdf";
        String diffPrefix = "diff01_";

        Document document = new Document(new PdfDocument(new PdfWriter(outFileName)));

        FontProvider sel = new FontProvider();
        sel.getFontSet().addFont(StandardFonts.TIMES_ROMAN);
        sel.getFontSet().addFont(StandardFonts.COURIER);
        sel.getFontSet().addFont(fontsFolder + "Puritan2.otf", PdfEncodings.IDENTITY_H, "Puritan2");
        sel.getFontSet().addFont(fontsFolder + "NotoSans-Regular.ttf", PdfEncodings.IDENTITY_H, "NotoSans");
        document.setFontProvider(sel);
        document.add(new Paragraph("StandardFonts - non-breaking hyphen \\u2011")
                .setUnderline()
                .setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph("for Standard font TIMES_ROMAN: <&#8209;> non-breaking hyphen <\u2011> 2 hyphens<\u2011\u2011>here ")
                .setFontFamily(StandardFonts.TIMES_ROMAN));
        document.add(new Paragraph("for Standard font COURIER: <&#8209;> non-breaking hyphen<\u2011> 2hyphens <\u2011\u2011>here ")
                .setFontFamily(StandardFonts.COURIER));
        document.add(new Paragraph("for Standard font HELVETICA_BOLD: <&#8209;> non-breaking hyphen<\u2011> 2hyphens <\u2011\u2011>here ")
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)));
        document.add(new Paragraph("for Standard font SYMBOL: <&#8209;> non-breaking hyphen<\u2011> 2hyphens <\u2011\u2011>here ")
                .setFont(PdfFontFactory.createFont(StandardFonts.SYMBOL)));

        document.add(new Paragraph("Non-Standard fonts - non-breaking hyphen \\u2011")
                .setUnderline()
                .setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph("for NotoSans: <&#8209;> hyphen<\u2011> 2hyphens <\u2011\u2011>here")
                .setFontFamily("NotoSans"));
        document.add(new Paragraph("for Puritan2: <&#8209;> hyphen<\u2011> 2hyphens <\u2011\u2011>here")
                .setFontFamily("Puritan2"));


        sel.getFontSet().addFont(fontsFolder + "FreeSans.ttf", PdfEncodings.IDENTITY_H, "FreeSans");
        document.add(new Paragraph("AFTER adding of FreeSans font with non-breaking hyphen \\u2011 support")
                .setUnderline()
                .setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph("for Standard font TIMES_ROMAN: <&#8209;> non-breaking hyphen <\u2011> 2 hyphens<\u2011\u2011>here ")
                .setFontFamily(StandardFonts.TIMES_ROMAN));
        document.add(new Paragraph("for Standard font COURIER: <&#8209;> non-breaking hyphen<\u2011> 2hyphens <\u2011\u2011>here ")
                .setFontFamily(StandardFonts.COURIER));
        document.add(new Paragraph("for Standard font HELVETICA_BOLD: <&#8209;> non-breaking hyphen<\u2011> 2hyphens <\u2011\u2011>here ")
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)));
        document.add(new Paragraph("for Standard font SYMBOL: <&#8209;> non-breaking hyphen<\u2011> 2hyphens <\u2011\u2011>here ")
                .setFont(PdfFontFactory.createFont(StandardFonts.SYMBOL)));
        document.add(new Paragraph("for FreeSans: <&#8209;> hyphen<\u2011> 2hyphens <\u2011\u2011>here")
                .setFontFamily("FreeSans"));
        document.add(new Paragraph("for NotoSans: <&#8209;> hyphen<\u2011> 2hyphens <\u2011\u2011>here")
                .setFontFamily("NotoSans"));
        document.add(new Paragraph("for Puritan2: <&#8209;> hyphen<\u2011> 2hyphens <\u2011\u2011>here")
                .setFontFamily("Puritan2"));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, diffPrefix));
    }
}
