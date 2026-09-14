/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.TrueTypeFont;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.font.otf.GlyphLine;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.font.PdfType3Font;
import com.itextpdf.kernel.font.Type3Glyph;
import com.itextpdf.kernel.pdf.PdfConformance;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.TestUtil;

import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@Tag("IntegrationTest")
public class PdfUAFontsTest extends ExtendedITextTest {
    private static final String DESTINATION_FOLDER = TestUtil.getOutputPath() + "/pdfua/PdfUAFontsTest/";
    private static final String FONTS_FOLDER = "./src/test/resources/com/itextpdf/pdfua/font/";
    private static final String FONT = FONTS_FOLDER + "FreeSans.ttf";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    public static List<PdfConformance> data() {
        return UaValidationTestFramework.getConformanceList();
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tryToUseType0Cid0FontTest(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addBeforeGenerationHook(pdfDoc -> {
            Document document = new Document(pdfDoc);
            PdfFont font;
            try {
                font = PdfFontFactory.createFont("KozMinPro-Regular", "UniJIS-UCS2-H", EmbeddingStrategy.PREFER_EMBEDDED);
            } catch (IOException e) {
                throw new PdfException(e);
            }
            document.setFont(font);

            Paragraph paragraph = new Paragraph("Simple paragraph");
            document.add(paragraph);
        });

        framework.assertBothFail("tryToUseType0Cid0FontTest",
                MessageFormatUtil.format(PdfUAExceptionMessageConstants.FONT_SHOULD_BE_EMBEDDED, "KozMinPro-Regular"),
                false);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void type0Cid2FontTest(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addBeforeGenerationHook(pdfDoc -> {
            Document document = new Document(pdfDoc);
            PdfFont font;
            try {
                font = PdfFontFactory.createFont(FONT);
            } catch (IOException e) {
                throw new PdfException(e);
            }
            document.setFont(font);

            Paragraph paragraph = new Paragraph("Simple paragraph");
            document.add(paragraph);
        });
        framework.assertBothValid("type0Cid2FontTest");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void trueTypeFontTest(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addBeforeGenerationHook(pdfDoc -> {
            Document document = new Document(pdfDoc);
            PdfFont font;
            try {
                font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, EmbeddingStrategy.FORCE_EMBEDDED);
            } catch (IOException e) {
                throw new PdfException(e);
            }
            document.setFont(font);

            Paragraph paragraph = new Paragraph("Simple paragraph");
            document.add(paragraph);
        });
        framework.assertBothValid("trueTypeFontTest");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void trueTypeFontGlyphNotPresentTest(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfFont font;
            try {
                font = PdfFontFactory.createFont(FONT, "# simple 32 0020 00C5 1987", EmbeddingStrategy.PREFER_EMBEDDED);
            } catch (IOException e) {
                throw new PdfException(e);
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
                MessageFormatUtil.format(PdfUAExceptionMessageConstants.GLYPH_IS_NOT_DEFINED_OR_WITHOUT_UNICODE, "w"),
                false);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void trueTypeFontWithDifferencesTest(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfFont font;
            try {
                font = PdfFontFactory.createFont(FONT, "# simple 32 0077 006f 0072 006c 0064", EmbeddingStrategy.PREFER_EMBEDDED);
            } catch (IOException e) {
                throw new PdfException(e);
            }
            PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
            TagTreePointer tagPointer = new TagTreePointer(pdfDoc)
                    .setPageForTagging(pdfDoc.getFirstPage())
                    .addTag(StandardRoles.H1);
            canvas.
                    saveState().openTag(tagPointer.getTagReference()).
                    beginText().
                    moveText(36, 786).
                    setFontAndSize(font, 36).
                    showText("world").
                    endText().
                    restoreState().closeTag();
        });

        framework.assertBothFail("trueTypeFontWithDifferencesTest", PdfUAExceptionMessageConstants.
                NON_SYMBOLIC_TTF_SHALL_SPECIFY_MAC_ROMAN_OR_WIN_ANSI_ENCODING, false);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tryToUseStandardFontsTest(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addBeforeGenerationHook(pdfDoc -> {
            Document document = new Document(pdfDoc);
            PdfFont font;
            try {
                font = PdfFontFactory.createFont(StandardFonts.COURIER, "", EmbeddingStrategy.PREFER_EMBEDDED);
            } catch (IOException e) {
                throw new PdfException(e);
            }
            document.setFont(font);

            Paragraph paragraph = new Paragraph("Helloworld");
            document.add(paragraph);
        });

        framework.assertBothFail("tryToUseStandardFontsTest",
                MessageFormatUtil.format(PdfUAExceptionMessageConstants.FONT_SHOULD_BE_EMBEDDED, "Courier"), false);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void type1EmbeddedFontTest(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addBeforeGenerationHook(pdfDoc -> {
            Document document = new Document(pdfDoc);
            PdfFont font;
            try {
                font = PdfFontFactory.createFont(
                        FontProgramFactory.createType1Font(FONTS_FOLDER + "cmr10.afm", FONTS_FOLDER + "cmr10.pfb"),
                        FontEncoding.FONT_SPECIFIC, EmbeddingStrategy.FORCE_EMBEDDED);
            } catch (IOException e) {
                throw new PdfException(e);
            }
            document.setFont(font);

            Paragraph paragraph = new Paragraph("Helloworld");
            document.add(paragraph);
        });
        framework.assertBothValid("type1EmbeddedFontTest");
    }

    @Test
    // TODO DEVSIX-9076 NPE when cmap of True Type Font doesn't contain Microsoft Unicode or Macintosh Roman encodings
    public void nonSymbolicTtfWithChangedCmapTest() {
        Assertions.assertThrows(NullPointerException.class,
                () -> PdfFontFactory.createFont(FONTS_FOLDER + "FreeSans_changed_cmap.ttf", PdfEncodings.MACROMAN,
                        EmbeddingStrategy.FORCE_EMBEDDED));
    }

    @ParameterizedTest
    @MethodSource("data")
    public void nonSymbolicTtfWithValidEncodingTest(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addBeforeGenerationHook(pdfDoc -> {
            Document document = new Document(pdfDoc);
            PdfFont font;
            try {
                font = PdfFontFactory.createFont(FONT, PdfEncodings.MACROMAN, EmbeddingStrategy.FORCE_EMBEDDED);
            } catch (IOException e) {
                throw new PdfException(e);
            }
            document.setFont(font);

            Paragraph paragraph = new Paragraph("ABC");
            document.add(paragraph);
        });
        framework.assertBothValid("nonSymbolicTtfWithValidEncodingTest");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void nonSymbolicTtfWithIncompatibleEncodingTest(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addBeforeGenerationHook(pdfDoc -> {
            Document document = new Document(pdfDoc);
            PdfFont font;
            try {
                font = PdfFontFactory.createFont(FONT, PdfEncodings.UTF8, EmbeddingStrategy.FORCE_EMBEDDED);
            } catch (IOException e) {
                throw new PdfException(e);
            }
            document.setFont(font);

            Paragraph paragraph = new Paragraph("ABC");
            document.add(paragraph);
        });
        framework.assertBothFail("nonSymbolicTtfWithIncompatibleEncoding", PdfUAExceptionMessageConstants.
                NON_SYMBOLIC_TTF_SHALL_SPECIFY_MAC_ROMAN_OR_WIN_ANSI_ENCODING, false);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void symbolicTtfTest(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addBeforeGenerationHook(pdfDoc -> {
            Document document = new Document(pdfDoc);
            PdfFont font;
            try {
                font = PdfFontFactory.createFont(FONTS_FOLDER + "iTextSymbolicFont.ttf", PdfEncodings.MACROMAN,
                        EmbeddingStrategy.FORCE_EMBEDDED);
            } catch (IOException e) {
                throw new PdfException(e);
            }
            document.setFont(font);

            Paragraph paragraph = new Paragraph("ABC");
            document.add(paragraph);
        });
        framework.assertBothValid("symbolicTtf");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void symbolicTtfWithEncodingTest(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addBeforeGenerationHook(pdfDoc -> {
            Document document = new Document(pdfDoc);
            PdfFont font;
            try {
                font = PdfFontFactory.createFont(FONTS_FOLDER + "iTextSymbolicFont.ttf", PdfEncodings.MACROMAN,
                        EmbeddingStrategy.FORCE_EMBEDDED);
            } catch (IOException e) {
                throw new PdfException(e);
            }
            font.getPdfObject().put(PdfName.Encoding, PdfName.MacRomanEncoding);
            document.setFont(font);

            Paragraph paragraph = new Paragraph("ABC");
            document.add(paragraph);
        });
        // VeraPDF is valid since iText fixes symbolic flag to non-symbolic on closing.
        framework.assertITextFailVeraPdfValid("symbolicTtfWithEncoding",
                PdfUAExceptionMessageConstants.SYMBOLIC_TTF_SHALL_NOT_CONTAIN_ENCODING);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void symbolicTtfWithInvalidCmapTest(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addBeforeGenerationHook(pdfDoc -> {
            Document document = new Document(pdfDoc);
            PdfFont font;
            try {
                TrueTypeFont fontProgram = new CustomSymbolicTrueTypeFont(FONT);
                font = PdfFontFactory.createFont(fontProgram, PdfEncodings.MACROMAN, EmbeddingStrategy.FORCE_EMBEDDED);
            } catch (IOException e) {
                throw new PdfException(e);
            }
            document.setFont(font);

            Paragraph paragraph = new Paragraph("ABC");
            document.add(paragraph);
        });
        // VeraPDF is valid since iText fixes symbolic flag to non-symbolic on closing.
        if (PdfConformance.PDF_UA_1.equals(conformance)) {
            framework.assertITextFailVeraPdfValid("symbolicTtfWithInvalidCmapTest", PdfUAExceptionMessageConstants.
                    SYMBOLIC_TTF_SHALL_CONTAIN_EXACTLY_ONE_OR_AT_LEAST_MICROSOFT_SYMBOL_CMAP);
        } else {
            framework.assertITextFailVeraPdfValid("symbolicTtfWithInvalidCmapTest", PdfUAExceptionMessageConstants.
                    SYMBOLIC_TTF_SHALL_CONTAIN_MAC_ROMAN_OR_MICROSOFT_SYMBOL_CMAP);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void nonSymbolicTtfWithInvalidCmapTest(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addBeforeGenerationHook(pdfDoc -> {
            Document document = new Document(pdfDoc);
            PdfFont font;
            try {
                TrueTypeFont fontProgram = new CustomNonSymbolicTrueTypeFont(FONT);
                font = PdfFontFactory.createFont(fontProgram, PdfEncodings.MACROMAN, EmbeddingStrategy.FORCE_EMBEDDED);
            } catch (IOException e) {
                throw new PdfException(e);
            }
            document.setFont(font);

            Paragraph paragraph = new Paragraph("ABC");
            document.add(paragraph);
        });
        // VeraPDF is valid since the file itself is valid, but itext code is modified for testing.
        if (PdfConformance.PDF_UA_1.equals(conformance) ) {
            framework.assertITextFailVeraPdfValid("nonSymbolicTtfWithInvalidCmapTest", PdfUAExceptionMessageConstants.
                    NON_SYMBOLIC_TTF_SHALL_CONTAIN_NON_SYMBOLIC_CMAP);
        } else {
            framework.assertITextFailVeraPdfValid("nonSymbolicTtfWithInvalidCmapTest", PdfUAExceptionMessageConstants.
                    NON_SYMBOLIC_TTF_SHALL_CONTAIN_MAC_ROMAN_OR_MICROSOFT_UNI_CMAP);
        }
    }

    @Test
    // TODO DEVSIX-9076 NPE when cmap of True Type Font doesn't contain Microsoft Unicode or Macintosh Roman encodings
    public void symbolicTtfWithChangedCmapTest() {
        Assertions.assertThrows(NullPointerException.class,
                () -> PdfFontFactory.createFont(FONTS_FOLDER + "iTextSymbolicFontChangedCmap.ttf",
                        EmbeddingStrategy.FORCE_EMBEDDED));
    }

    @Test
    public void notdefGlyphTest() throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, false, PdfConformance.PDF_UA_2);
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfFont font = null;
            try {
                font = PdfFontFactory.createFont(FONTS_FOLDER + "NotoNaskhArabic-Regular.ttf");
            } catch (IOException e) {
                // ignore
            }
            GlyphLine glyphLine = new GlyphLine();
            final FontProgram fontProgram = font.getFontProgram();
            // zero glyph in the font is .notdef glyph without Unicode
            glyphLine.add(fontProgram.getGlyphByCode(0));
            glyphLine.setEnd(glyphLine.size());

            PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
            TagTreePointer tagPointer = new TagTreePointer(pdfDoc)
                    .setPageForTagging(pdfDoc.getFirstPage())
                    .addTag(StandardRoles.H1);
            canvas.
                    saveState().
                    openTag(tagPointer.getTagReference()).
                    beginText().
                    moveText(36, 786).
                    setFontAndSize(font, 10).
                    showText(glyphLine).
                    endText().
                    restoreState().
                    closeTag();
        });
        framework.assertBothFail("notdefGlyph", MessageFormatUtil.format(
                PdfUAExceptionMessageConstants.GLYPH_IS_NOT_DEFINED_OR_WITHOUT_UNICODE, "�"));
    }

    @Test
    public void zeroUnicodeGlyphTest() throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, false, PdfConformance.PDF_UA_2);
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfFont font = null;
            try {
                font = PdfFontFactory.createFont(FONTS_FOLDER + "NotoNaskhArabic-Regular.ttf");
            } catch (IOException e) {
                // ignore
            }
            GlyphLine glyphLine = new GlyphLine();
            final FontProgram fontProgram = font.getFontProgram();
            // 1 index glyph in the font is .null glyph with Unicode U+0000
            glyphLine.add(fontProgram.getGlyphByCode(1));
            glyphLine.setEnd(glyphLine.size());

            PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
            TagTreePointer tagPointer = new TagTreePointer(pdfDoc)
                    .setPageForTagging(pdfDoc.getFirstPage())
                    .addTag(StandardRoles.H1);
            canvas.
                    saveState().
                    openTag(tagPointer.getTagReference()).
                    beginText().
                    moveText(36, 786).
                    setFontAndSize(font, 10).
                    showText(glyphLine).
                    endText().
                    restoreState().
                    closeTag();
        });
        // TODO DEVSIX-10160 missing check on iText side for ToUnicode mapping to 0, fffe and feff
        framework.assertVeraPdfFailITextValid("zeroUnicodeGlyph");
    }

    @Test
    public void glyphsWithoutUnicodeTest() throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, false,
                PdfConformance.PDF_UA_2);
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfFont font = null;
            try {
                font = PdfFontFactory.createFont(FONTS_FOLDER + "NotoNaskhArabic-Regular.ttf");
            } catch (IOException e) {
                // ignore
            }
            GlyphLine glyphLine = new GlyphLine();
            final FontProgram fontProgram = font.getFontProgram();
            // 0 index glyph is .notdef without Unicode
            // 1 index glyph is .null with Unicode U+0000
            for (int i = 2; i < fontProgram.countOfGlyphs(); i++) {
                glyphLine.add(fontProgram.getGlyphByCode(i));
            }
            glyphLine.setEnd(glyphLine.size());

            PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
            TagTreePointer tagPointer = new TagTreePointer(pdfDoc)
                    .setPageForTagging(pdfDoc.getFirstPage())
                    .addTag(StandardRoles.H1);
            canvas.
                    saveState().
                    openTag(tagPointer.getTagReference()).
                    beginText().
                    moveText(36, 786).
                    setFontAndSize(font, 10).
                    showText(glyphLine).
                    endText().
                    restoreState().
                    closeTag();
        });
        framework.assertBothFail("glyphsWithoutUnicode", MessageFormatUtil.format(
                PdfUAExceptionMessageConstants.GLYPH_IS_NOT_DEFINED_OR_WITHOUT_UNICODE, "�"));
    }

    @Test
    public void fontWithReplacementCharTest() throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, false,
                PdfConformance.PDF_UA_2);
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfFont font = null;
            try {
                font = PdfFontFactory.createFont(FONTS_FOLDER + "NotoSans-Regular.ttf");
            } catch (IOException e) {
                // ignore
            }
            GlyphLine glyphLine = new GlyphLine();
            final FontProgram fontProgram = font.getFontProgram();
            // font contain replacement char U+FFFD
            for (int i = 0; i < fontProgram.countOfGlyphs(); i++) {
                glyphLine.add(fontProgram.getGlyphByCode(i));
            }
            glyphLine.setEnd(glyphLine.size());

            PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
            TagTreePointer tagPointer = new TagTreePointer(pdfDoc)
                    .setPageForTagging(pdfDoc.getFirstPage())
                    .addTag(StandardRoles.H1);
            canvas.
                    saveState().
                    openTag(tagPointer.getTagReference()).
                    beginText().
                    moveText(36, 786).
                    setFontAndSize(font, 10).
                    showText(glyphLine).
                    endText().
                    restoreState().
                    closeTag();
        });
        // TODO DEVSIX-10160 missing check on iText side for ToUnicode mapping to 0, fffe and feff
        framework.assertBothFail("fontWithReplacementChar", MessageFormatUtil.format(
                PdfUAExceptionMessageConstants.GLYPH_IS_NOT_DEFINED_OR_WITHOUT_UNICODE, "�"));
    }

    @Test
    public void notdefGlyphType3FontTest() throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, false,
                PdfConformance.PDF_UA_2);
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfType3Font font = PdfFontFactory.createType3Font(pdfDoc, "itextFont", "itextFont", false);
            Type3Glyph a = font.addGlyph('A', 600, 0, 0, 600, 700);
            a.setLineWidth(100);
            a.moveTo(5, 5);
            a.lineTo(300, 695);
            a.lineTo(595, 5);
            a.closePathFillStroke();

            // Need to populate CharProcs, because it's done only on font flushing,
            // but iText check that field before document closing
            PdfDictionary charProcs = new PdfDictionary();
            charProcs.put(new PdfName("A"), a.getContentStream());
            font.getPdfObject().put(PdfName.CharProcs, charProcs);

            Document doc = new Document(pdfDoc);
            doc.setFont(font);
            // In simple fonts (which is Type3) we just ignore not defined glyphs, see PdfSimpleFont.createGlyphLine
            Paragraph p = new Paragraph("AB");
            doc.add(p);
        });
        framework.assertBothValid("notdefGlyphType3Font");
    }


    private static class CustomSymbolicTrueTypeFont extends TrueTypeFont {
        public CustomSymbolicTrueTypeFont(String path) throws IOException {
            super(path);
        }

        @Override
        public int getPdfFontFlags() {
            return 4;
        }

        @Override
        public boolean isCmapPresent(int platformID, int encodingID) {
            if (platformID == 1) {
                return false;
            }
            return super.isCmapPresent(platformID, encodingID);
        }
    }

    private static class CustomNonSymbolicTrueTypeFont extends TrueTypeFont {
        public CustomNonSymbolicTrueTypeFont(String path) throws IOException {
            super(path);
        }

        @Override
        public int getPdfFontFlags() {
            return 32;
        }

        @Override
        public boolean isCmapPresent(int platformID, int encodingID) {
            if (platformID == 1 || encodingID == 1) {
                return false;
            }
            return super.isCmapPresent(platformID, encodingID);
        }

        @Override
        public int getNumberOfCmaps() {
            return 0;
        }
    }
}
