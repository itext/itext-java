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
package com.itextpdf.pdfua;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.font.FontEncoding;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.pdfa.VeraPdfValidator; // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)

import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class PdfUAFontsTest extends ExtendedITextTest {
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/pdfua/PdfUAFontsTest/";
    private static final String FONT = "./src/test/resources/com/itextpdf/pdfua/font/FreeSans.ttf";
    private static final String FONT_FOLDER = "./src/test/resources/com/itextpdf/pdfua/font/";

    private UaValidationTestFramework framework;

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @BeforeEach
    public void initializeFramework() {
        framework = new UaValidationTestFramework(DESTINATION_FOLDER);
    }

    @Test
    public void tryToUseType0Cid0FontTest() throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            Document document = new Document(pdfDoc);
            PdfFont font;
            try {
                font = PdfFontFactory.createFont("KozMinPro-Regular", "UniJIS-UCS2-H", EmbeddingStrategy.PREFER_EMBEDDED);
            } catch (IOException e) {
                throw new RuntimeException();
            }
            document.setFont(font);

            Paragraph paragraph = new Paragraph("Simple paragraph");
            document.add(paragraph);
        });
        framework.assertBothFail("tryToUseType0Cid0FontTest",
                MessageFormatUtil.format(PdfUAExceptionMessageConstants.FONT_SHOULD_BE_EMBEDDED, "KozMinPro-Regular"), false);
    }

    @Test
    public void type0Cid2FontTest() throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            Document document = new Document(pdfDoc);
            PdfFont font;
            try {
                font = PdfFontFactory.createFont(FONT);
            } catch (IOException e) {
                throw new RuntimeException();
            }
            document.setFont(font);

            Paragraph paragraph = new Paragraph("Simple paragraph");
            document.add(paragraph);
        });
        framework.assertBothValid("type0Cid2FontTest");
    }

    @Test
    public void trueTypeFontTest() throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            Document document = new Document(pdfDoc);
            PdfFont font;
            try {
                font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, EmbeddingStrategy.FORCE_EMBEDDED);
            } catch (IOException e) {
                throw new RuntimeException();
            }
            document.setFont(font);

            Paragraph paragraph = new Paragraph("Simple paragraph");
            document.add(paragraph);
        });
        framework.assertBothValid("trueTypeFontTest");
    }

    @Test
    public void trueTypeFontGlyphNotPresentTest() throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            PdfFont font;
            try {
                font = PdfFontFactory.createFont(FONT, "# simple 32 0020 00C5 1987", EmbeddingStrategy.PREFER_EMBEDDED);
            } catch (IOException e) {
                throw new RuntimeException();
            }
            PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
            TagTreePointer tagPointer = new TagTreePointer(pdfDoc)
                    .setPageForTagging(pdfDoc.getFirstPage())
                    .addTag(StandardRoles.H);
            canvas.
                    saveState().openTag(tagPointer.getTagReference()).
                    beginText().
                    moveText(36, 786).
                    setFontAndSize(font, 36).
                    showText("world").
                    endText().
                    restoreState().closeTag();
        });
        framework.assertBothFail("trueTypeFontGlyphNotPresentTest",
                MessageFormatUtil.format(PdfUAExceptionMessageConstants.GLYPH_IS_NOT_DEFINED_OR_WITHOUT_UNICODE, "w"), false);
    }

    @Test
    public void trueTypeFontWithDifferencesTest() throws IOException {
        String outPdf = DESTINATION_FOLDER + "trueTypeFontWithDifferencesTest.pdf";
        try (PdfDocument pdfDoc = new PdfUATestPdfDocument(new PdfWriter(outPdf))) {
            PdfFont font;
            try {
                font = PdfFontFactory.createFont(FONT, "# simple 32 0077 006f 0072 006c 0064", EmbeddingStrategy.PREFER_EMBEDDED);
            } catch (IOException e) {
                throw new RuntimeException();
            }
            PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
            TagTreePointer tagPointer = new TagTreePointer(pdfDoc)
                    .setPageForTagging(pdfDoc.getFirstPage())
                    .addTag(StandardRoles.H);
            canvas.
                    saveState().openTag(tagPointer.getTagReference()).
                    beginText().
                    moveText(36, 786).
                    setFontAndSize(font, 36).
                    showText("world").
                    endText().
                    restoreState().closeTag();
        }
        Assertions.assertNotNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)
    }

    @Test
    public void tryToUseStandardFontsTest() throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            Document document = new Document(pdfDoc);
            PdfFont font;
            try {
                font = PdfFontFactory.createFont(StandardFonts.COURIER, "", EmbeddingStrategy.PREFER_EMBEDDED);
            } catch (IOException e) {
                throw new RuntimeException();
            }
            document.setFont(font);

            Paragraph paragraph = new Paragraph("Helloworld");
            document.add(paragraph);
            document.close();
        });
        framework.assertBothFail("tryToUseStandardFontsTest",
                MessageFormatUtil.format(PdfUAExceptionMessageConstants.FONT_SHOULD_BE_EMBEDDED, "Courier"), false);
    }

    @Test
    public void type1EmbeddedFontTest() throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            Document document = new Document(pdfDoc);
            PdfFont font;
            try {
                font = PdfFontFactory.createFont(
                        FontProgramFactory.createType1Font(FONT_FOLDER + "cmr10.afm", FONT_FOLDER + "cmr10.pfb"),
                        FontEncoding.FONT_SPECIFIC, EmbeddingStrategy.FORCE_EMBEDDED);
            } catch (IOException e) {
                throw new RuntimeException();
            }
            document.setFont(font);

            Paragraph paragraph = new Paragraph("Helloworld");
            document.add(paragraph);
        });
        framework.assertBothValid("type1EmbeddedFontTest");
    }
}
