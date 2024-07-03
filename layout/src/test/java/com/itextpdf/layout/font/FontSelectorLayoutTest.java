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

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.font.selectorstrategy.BestMatchFontSelectorStrategy.BestMatchFontSelectorStrategyFactory;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class FontSelectorLayoutTest extends ExtendedITextTest {
    private static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/NonBreakingHyphenTest/";
    private static final String destinationFolder = "./target/test/com/itextpdf/layout/NonBreakingHyphenTest/";
    private static final String fontsFolder = "./src/test/resources/com/itextpdf/layout/fonts/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void nonBreakingHyphenDifferentFonts() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "nonBreakingHyphenDifferentFonts.pdf";
        String cmpFileName = sourceFolder + "cmp_nonBreakingHyphenDifferentFonts.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFileName)));

        FontProvider sel = new FontProvider();
        sel.setFontSelectorStrategyFactory(new BestMatchFontSelectorStrategyFactory());
        sel.getFontSet().addFont(StandardFonts.TIMES_ROMAN);
        sel.getFontSet().addFont(StandardFonts.COURIER);
        sel.getFontSet().addFont(fontsFolder + "Puritan2.otf", PdfEncodings.IDENTITY_H, "Puritan2");
        sel.getFontSet().addFont(fontsFolder + "NotoSans-Regular.ttf", PdfEncodings.IDENTITY_H, "NotoSans");
        sel.getFontSet().addFont(fontsFolder + "FreeSans.ttf", PdfEncodings.IDENTITY_H, "FreeSans");
        document.setFontProvider(sel);

        document.add(createParagraph("For Standard font TIMES_ROMAN: ", StandardFonts.TIMES_ROMAN));
        document.add(createParagraph("For Standard font COURIER: ", StandardFonts.COURIER));
        document.add(createParagraph("For FreeSans: ", ("FreeSans")));
        document.add(createParagraph("For NotoSans: ", ("NotoSans")));
        document.add(createParagraph("For Puritan2: ", ("Puritan2")));

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diffPrefix"));
    }

    private static Paragraph createParagraph(String textParagraph, String font) {
        String text = "here is non-breaking hyphen: <\u2011> text after non-breaking hyphen.";
        Paragraph p = new Paragraph(textParagraph + text).setFontFamily(font);

        return p;
    }


    @Test
    public void utfToGlyphToUtfRountripTest() throws IOException {
        // this should not throw a null pointer exception
        try(PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
            Document doc = new Document(pdfDoc)) {
            doc.setFont(PdfFontFactory.createFont("HeiseiMin-W3", "UniJIS-UCS2-H"));
            AssertUtil.doesNotThrow(() -> doc.add(new Paragraph("\u9F9C")));
        }
    }
}
