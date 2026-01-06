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
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.TrueTypeFont;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfUAConformance;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.List;

@Tag("IntegrationTest")
public class PdfUAFontsTest extends ExtendedITextTest {
    private static final String DESTINATION_FOLDER = TestUtil.getOutputPath() + "/pdfua/PdfUAFontsTest/";
    private static final String FONT = "./src/test/resources/com/itextpdf/pdfua/font/FreeSans.ttf";
    private static final String FONT_FOLDER = "./src/test/resources/com/itextpdf/pdfua/font/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    public static List<PdfUAConformance> data() {
        return UaValidationTestFramework.getConformanceList();
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tryToUseType0Cid0FontTest(PdfUAConformance pdfUAConformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER);
        framework.addBeforeGenerationHook(pdfDoc -> {
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
                MessageFormatUtil.format(PdfUAExceptionMessageConstants.FONT_SHOULD_BE_EMBEDDED, "KozMinPro-Regular"),
                false, pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void type0Cid2FontTest(PdfUAConformance pdfUAConformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER);
        framework.addBeforeGenerationHook(pdfDoc -> {
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
        framework.assertBothValid("type0Cid2FontTest", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void trueTypeFontTest(PdfUAConformance pdfUAConformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER);
        framework.addBeforeGenerationHook(pdfDoc -> {
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
        framework.assertBothValid("trueTypeFontTest", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void trueTypeFontGlyphNotPresentTest(PdfUAConformance pdfUAConformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER);
        framework.addBeforeGenerationHook(pdfDoc -> {
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
                MessageFormatUtil.format(PdfUAExceptionMessageConstants.GLYPH_IS_NOT_DEFINED_OR_WITHOUT_UNICODE, "w"),
                false, pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void trueTypeFontWithDifferencesTest(PdfUAConformance pdfUAConformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER);
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfFont font;
            try {
                font = PdfFontFactory.createFont(FONT, "# simple 32 0077 006f 0072 006c 0064", EmbeddingStrategy.PREFER_EMBEDDED);
            } catch (IOException e) {
                throw new RuntimeException();
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
                NON_SYMBOLIC_TTF_SHALL_SPECIFY_MAC_ROMAN_OR_WIN_ANSI_ENCODING, false, pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tryToUseStandardFontsTest(PdfUAConformance pdfUAConformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER);
        framework.addBeforeGenerationHook(pdfDoc -> {
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
        });

        framework.assertBothFail("tryToUseStandardFontsTest",
                MessageFormatUtil.format(PdfUAExceptionMessageConstants.FONT_SHOULD_BE_EMBEDDED, "Courier"), false,
                pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void type1EmbeddedFontTest(PdfUAConformance pdfUAConformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER);
        framework.addBeforeGenerationHook(pdfDoc -> {
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
        framework.assertBothValid("type1EmbeddedFontTest", pdfUAConformance);
    }

    @Test
    // TODO DEVSIX-9076 NPE when cmap of True Type Font doesn't contain Microsoft Unicode or Macintosh Roman encodings
    public void nonSymbolicTtfWithChangedCmapTest() {
        Assertions.assertThrows(NullPointerException.class,
                () -> PdfFontFactory.createFont(FONT_FOLDER + "FreeSans_changed_cmap.ttf", PdfEncodings.MACROMAN,
                        EmbeddingStrategy.FORCE_EMBEDDED));
    }

    @ParameterizedTest
    @MethodSource("data")
    public void nonSymbolicTtfWithValidEncodingTest(PdfUAConformance pdfUAConformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER);
        framework.addBeforeGenerationHook(pdfDoc -> {
            Document document = new Document(pdfDoc);
            PdfFont font;
            try {
                font = PdfFontFactory.createFont(FONT, PdfEncodings.MACROMAN, EmbeddingStrategy.FORCE_EMBEDDED);
            } catch (IOException e) {
                throw new RuntimeException();
            }
            document.setFont(font);

            Paragraph paragraph = new Paragraph("ABC");
            document.add(paragraph);
        });
        framework.assertBothValid("nonSymbolicTtfWithValidEncodingTest", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void nonSymbolicTtfWithIncompatibleEncodingTest(PdfUAConformance pdfUAConformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER);
        framework.addBeforeGenerationHook(pdfDoc -> {
            Document document = new Document(pdfDoc);
            PdfFont font;
            try {
                font = PdfFontFactory.createFont(FONT, PdfEncodings.UTF8, EmbeddingStrategy.FORCE_EMBEDDED);
            } catch (IOException e) {
                throw new RuntimeException();
            }
            document.setFont(font);

            Paragraph paragraph = new Paragraph("ABC");
            document.add(paragraph);
        });
        framework.assertBothFail("nonSymbolicTtfWithIncompatibleEncoding", PdfUAExceptionMessageConstants.
                NON_SYMBOLIC_TTF_SHALL_SPECIFY_MAC_ROMAN_OR_WIN_ANSI_ENCODING, false, pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void symbolicTtfTest(PdfUAConformance pdfUAConformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER);
        framework.addBeforeGenerationHook(pdfDoc -> {
            Document document = new Document(pdfDoc);
            PdfFont font;
            try {
                // TODO DEVSIX-9589 Create symbol font with cmap 3,0 for testing
                font = PdfFontFactory.createFont(FONT_FOLDER + "Symbols1.ttf", PdfEncodings.MACROMAN,
                        EmbeddingStrategy.FORCE_EMBEDDED);
            } catch (IOException e) {
                throw new RuntimeException();
            }
            document.setFont(font);

            Paragraph paragraph = new Paragraph("ABC");
            document.add(paragraph);
        });
        framework.assertBothValid("symbolicTtf", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void symbolicTtfWithEncodingTest(PdfUAConformance pdfUAConformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER);
        framework.addBeforeGenerationHook(pdfDoc -> {
            Document document = new Document(pdfDoc);
            PdfFont font;
            try {
                // TODO DEVSIX-9589 Create symbol font with cmap 3,0 for testing
                font = PdfFontFactory.createFont(FONT_FOLDER + "Symbols1.ttf", PdfEncodings.MACROMAN,
                        EmbeddingStrategy.FORCE_EMBEDDED);
            } catch (IOException e) {
                throw new RuntimeException();
            }
            font.getPdfObject().put(PdfName.Encoding, PdfName.MacRomanEncoding);
            document.setFont(font);

            Paragraph paragraph = new Paragraph("ABC");
            document.add(paragraph);
        });
        // VeraPDF is valid since iText fixes symbolic flag to non-symbolic on closing.
        framework.assertOnlyITextFail("symbolicTtfWithEncoding",
                PdfUAExceptionMessageConstants.SYMBOLIC_TTF_SHALL_NOT_CONTAIN_ENCODING, pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void symbolicTtfWithInvalidCmapTest(PdfUAConformance pdfUAConformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER);
        framework.addBeforeGenerationHook(pdfDoc -> {
            Document document = new Document(pdfDoc);
            PdfFont font;
            try {
                TrueTypeFont fontProgram = new CustomSymbolicTrueTypeFont(FONT);
                font = PdfFontFactory.createFont(fontProgram, PdfEncodings.MACROMAN, EmbeddingStrategy.FORCE_EMBEDDED);
            } catch (IOException e) {
                throw new RuntimeException();
            }
            document.setFont(font);

            Paragraph paragraph = new Paragraph("ABC");
            document.add(paragraph);
        });
        // VeraPDF is valid since iText fixes symbolic flag to non-symbolic on closing.
        if (PdfUAConformance.PDF_UA_1 == pdfUAConformance) {
            framework.assertOnlyITextFail("symbolicTtfWithInvalidCmapTest", PdfUAExceptionMessageConstants.
                    SYMBOLIC_TTF_SHALL_CONTAIN_EXACTLY_ONE_OR_AT_LEAST_MICROSOFT_SYMBOL_CMAP, pdfUAConformance);
        } else if (PdfUAConformance.PDF_UA_2 == pdfUAConformance) {
            framework.assertOnlyITextFail("symbolicTtfWithInvalidCmapTest", PdfUAExceptionMessageConstants.
                    SYMBOLIC_TTF_SHALL_CONTAIN_MAC_ROMAN_OR_MICROSOFT_SYMBOL_CMAP, pdfUAConformance);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void nonSymbolicTtfWithInvalidCmapTest(PdfUAConformance pdfUAConformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER);
        framework.addBeforeGenerationHook(pdfDoc -> {
            Document document = new Document(pdfDoc);
            PdfFont font;
            try {
                TrueTypeFont fontProgram = new CustomNonSymbolicTrueTypeFont(FONT);
                font = PdfFontFactory.createFont(fontProgram, PdfEncodings.MACROMAN, EmbeddingStrategy.FORCE_EMBEDDED);
            } catch (IOException e) {
                throw new RuntimeException();
            }
            document.setFont(font);

            Paragraph paragraph = new Paragraph("ABC");
            document.add(paragraph);
        });
        // VeraPDF is valid since the file itself is valid, but itext code is modified for testing.
        if (PdfUAConformance.PDF_UA_1 == pdfUAConformance) {
            framework.assertOnlyITextFail("nonSymbolicTtfWithInvalidCmapTest", PdfUAExceptionMessageConstants.
                    NON_SYMBOLIC_TTF_SHALL_CONTAIN_NON_SYMBOLIC_CMAP, pdfUAConformance);
        } else if (PdfUAConformance.PDF_UA_2 == pdfUAConformance) {
            framework.assertOnlyITextFail("nonSymbolicTtfWithInvalidCmapTest", PdfUAExceptionMessageConstants.
                    NON_SYMBOLIC_TTF_SHALL_CONTAIN_MAC_ROMAN_OR_MICROSOFT_UNI_CMAP, pdfUAConformance);
        }
    }

    @Test
    // TODO DEVSIX-9076 NPE when cmap of True Type Font doesn't contain Microsoft Unicode or Macintosh Roman encodings
    // TODO DEVSIX-9589 Create symbol font with cmap 3,0 for testing
    public void symbolicTtfWithChangedCmapTest() {
        Assertions.assertThrows(NullPointerException.class,
                () -> PdfFontFactory.createFont(FONT_FOLDER + "Symbols1_changed_cmap.ttf",
                        EmbeddingStrategy.FORCE_EMBEDDED));
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
