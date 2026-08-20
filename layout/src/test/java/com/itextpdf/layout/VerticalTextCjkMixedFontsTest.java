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
package com.itextpdf.layout;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.AreaBreakType;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.VerticalTextOrientation;
import com.itextpdf.layout.properties.WritingMode;
import com.itextpdf.layout.testutil.VerticalTextTestUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

@Tag("IntegrationTest")
public class VerticalTextCjkMixedFontsTest extends ExtendedITextTest {
    private static final String SOURCE_FOLDER =
            "./src/test/resources/com/itextpdf/layout/VerticalTextCjkMixedFontsTest/";
    private static final String FONTS_FOLDER = "./src/test/resources/com/itextpdf/layout/fonts/";
    private static final String DESTINATION_FOLDER =
            TestUtil.getOutputPath() + "/layout/VerticalTextCjkMixedFontsTest/";

    private static final String NOTO_SANS_SC = FONTS_FOLDER + "NotoSansCJKsc-Regular.otf";
    private static final String NOTO_SANS_TC = FONTS_FOLDER + "NotoSansCJKtc-Regular.otf";
    private static final String NOTO_SANS_JP = FONTS_FOLDER + "NotoSansCJKjp-Regular.otf";
    private static final String NOTO_SANS_KR = FONTS_FOLDER + "NotoSansCJKkr-Regular.otf";
    private static final String NOTO_SANS_MONGOLIAN = FONTS_FOLDER + "NotoSansMongolian-Regular.ttf";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void verticalTextChineseJapaneseKoreanInSameParagraphTest() throws IOException, InterruptedException {
        String fileName = "verticalTextChineseJapaneseKoreanInSameParagraph";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        CjkTextSpec chineseSpec = new CjkTextSpec("中文：你好世界。\n", loadCjkFont(NOTO_SANS_SC), 20)
                .backgroundColor(ColorConstants.LIGHT_GRAY);
        CjkTextSpec japaneseSpec = new CjkTextSpec("日本語：こんにちは世界。\n", loadCjkFont(NOTO_SANS_JP), 20)
                .backgroundColor(ColorConstants.CYAN);
        CjkTextSpec koreanSpec = new CjkTextSpec("한국어: 안녕하세요 세계.", loadCjkFont(NOTO_SANS_KR), 20)
                .backgroundColor(ColorConstants.LIGHT_GRAY);
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            document.add(buildParagraph(true, chineseSpec, japaneseSpec, koreanSpec));
            document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
            document.add(buildParagraph(false, chineseSpec, japaneseSpec, koreanSpec));
        }

        Map<Character, Integer> extractedCounts = VerticalTextTestUtil.extractPageCharacterCounts(outFileName);
        Assertions.assertTrue(VerticalTextTestUtil.containsAllCharacters(extractedCounts, "中文：你好世界。"));
        Assertions.assertTrue(VerticalTextTestUtil.containsAllCharacters(extractedCounts, "日本語：こんにちは世界。"));
        Assertions.assertTrue(VerticalTextTestUtil.containsAllCharacters(extractedCounts, "한국어: 안녕하세요 세계."));
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    public void verticalTextChineseAndLatinSameLineDifferentFontsTest() throws IOException, InterruptedException {
        String fileName = "verticalTextChineseAndLatinSameLineDifferentFonts";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        CjkTextSpec chineseSpec = new CjkTextSpec("产品名称：", loadCjkFont(NOTO_SANS_SC), 20)
                .backgroundColor(ColorConstants.LIGHT_GRAY);
        CjkTextSpec latinSpec = new CjkTextSpec("iText Core", PdfFontFactory.createFont(StandardFonts.HELVETICA), 20)
                .backgroundColor(ColorConstants.CYAN);
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            document.add(buildParagraph(true, chineseSpec, latinSpec));
            document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
            document.add(buildParagraph(false, chineseSpec, latinSpec));
        }

        Map<Character, Integer> extractedCounts = VerticalTextTestUtil.extractPageCharacterCounts(outFileName);
        Assertions.assertTrue(VerticalTextTestUtil.containsAllCharacters(extractedCounts, "产品名称："));
        Assertions.assertTrue(VerticalTextTestUtil.containsAllCharacters(extractedCounts, "iText Core"));
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    public void verticalTextMongolianAndChineseSameParagraphTest() throws IOException, InterruptedException {
        String fileName = "verticalTextMongolianAndChineseSameParagraph";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        CjkTextSpec mongolianSpec = new CjkTextSpec("ᠮᠣᠩᠭᠣᠯ ᠬᠡᠯᠡ\n", loadCjkFont(NOTO_SANS_MONGOLIAN), 22)
                .backgroundColor(ColorConstants.LIGHT_GRAY);
        CjkTextSpec chineseSpec = new CjkTextSpec("蒙古语与中文并排书写。", loadCjkFont(NOTO_SANS_SC), 22)
                .backgroundColor(ColorConstants.CYAN);
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            document.add(buildParagraph(true, mongolianSpec, chineseSpec));
            document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
            document.add(buildParagraph(false, mongolianSpec, chineseSpec));
        }

        Map<Character, Integer> extractedCounts = VerticalTextTestUtil.extractPageCharacterCounts(outFileName);
        Assertions.assertTrue(VerticalTextTestUtil.containsAllCharacters(extractedCounts, "ᠮᠣᠩᠭᠣᠯ"));
        Assertions.assertTrue(VerticalTextTestUtil.containsAllCharacters(extractedCounts, "ᠬᠡᠯᠡ"));
        Assertions.assertTrue(VerticalTextTestUtil.containsAllCharacters(extractedCounts, "蒙古语与中文并排书写。"));
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    public void verticalTextFourScriptsMultipleFontSizesTest() throws IOException, InterruptedException {
        String fileName = "verticalTextFourScriptsMultipleFontSizes";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        CjkTextSpec chineseSpec = new CjkTextSpec("中文 12pt。\n", loadCjkFont(NOTO_SANS_SC), 12);
        CjkTextSpec japaneseSpec = new CjkTextSpec("日本語 18pt。\n", loadCjkFont(NOTO_SANS_JP), 18);
        CjkTextSpec koreanSpec = new CjkTextSpec("한국어 24pt.\n", loadCjkFont(NOTO_SANS_KR), 24);
        CjkTextSpec mongolianSpec = new CjkTextSpec("ᠮᠣᠩᠭᠣᠯ 30pt", loadCjkFont(NOTO_SANS_MONGOLIAN), 30);
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            document.add(buildParagraph(true, chineseSpec, japaneseSpec, koreanSpec, mongolianSpec));
            document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
            document.add(buildParagraph(false, chineseSpec, japaneseSpec, koreanSpec, mongolianSpec));
        }

        Map<Character, Integer> extractedCounts = VerticalTextTestUtil.extractPageCharacterCounts(outFileName);
        Assertions.assertTrue(VerticalTextTestUtil.containsAllCharacters(extractedCounts, "中文 12pt。"));
        Assertions.assertTrue(VerticalTextTestUtil.containsAllCharacters(extractedCounts, "日本語 18pt。"));
        Assertions.assertTrue(VerticalTextTestUtil.containsAllCharacters(extractedCounts, "한국어 24pt."));
        Assertions.assertTrue(VerticalTextTestUtil.containsAllCharacters(extractedCounts, "ᠮᠣᠩᠭᠣᠯ 30pt"));
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    public void verticalTextCjkMultipleFontSizesSameLineTest() throws IOException, InterruptedException {
        String fileName = "verticalTextCjkMultipleFontSizesSameLine";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        CjkTextSpec smallSpec = new CjkTextSpec("小字", loadCjkFont(NOTO_SANS_SC), 12)
                .backgroundColor(ColorConstants.LIGHT_GRAY);
        CjkTextSpec mediumSpec = new CjkTextSpec("中字", loadCjkFont(NOTO_SANS_SC), 24)
                .backgroundColor(ColorConstants.CYAN);
        CjkTextSpec largeSpec = new CjkTextSpec("大字", loadCjkFont(NOTO_SANS_SC), 36)
                .backgroundColor(ColorConstants.LIGHT_GRAY);
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            document.add(buildParagraph(true, smallSpec, mediumSpec, largeSpec));
            document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
            document.add(buildParagraph(false, smallSpec, mediumSpec, largeSpec));
        }

        Map<Character, Integer> extractedCounts = VerticalTextTestUtil.extractPageCharacterCounts(outFileName);
        Assertions.assertTrue(VerticalTextTestUtil.containsAllCharacters(extractedCounts, "小字"));
        Assertions.assertTrue(VerticalTextTestUtil.containsAllCharacters(extractedCounts, "中字"));
        Assertions.assertTrue(VerticalTextTestUtil.containsAllCharacters(extractedCounts, "大字"));
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    public void verticalTextSimplifiedAndTraditionalChineseSameParagraphTest() throws IOException, InterruptedException {
        String fileName = "verticalTextSimplifiedAndTraditionalChineseSameParagraph";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        CjkTextSpec simplifiedSpec = new CjkTextSpec("简体：汉字 国\n", loadCjkFont(NOTO_SANS_SC), 20)
                .backgroundColor(ColorConstants.LIGHT_GRAY);
        CjkTextSpec traditionalSpec = new CjkTextSpec("繁體：漢字 國", loadCjkFont(NOTO_SANS_TC), 20)
                .backgroundColor(ColorConstants.CYAN);
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            document.add(buildParagraph(true, simplifiedSpec, traditionalSpec));
            document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
            document.add(buildParagraph(false, simplifiedSpec, traditionalSpec));
        }

        Map<Character, Integer> extractedCounts = VerticalTextTestUtil.extractPageCharacterCounts(outFileName);
        Assertions.assertTrue(VerticalTextTestUtil.containsAllCharacters(extractedCounts, "简体：汉字 国"));
        Assertions.assertTrue(VerticalTextTestUtil.containsAllCharacters(extractedCounts, "繁體：漢字 國"));
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    private static PdfFont loadCjkFont(String path) throws IOException {
        return PdfFontFactory.createFont(path, PdfEncodings.IDENTITY_H,
                PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED);
    }

    private static Paragraph verticalParagraph() {
        Paragraph paragraph = new Paragraph();
        paragraph.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
        paragraph.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
        paragraph.setBorder(new SolidBorder(1));
        return paragraph;
    }

    private static Paragraph horizontalParagraph() {
        Paragraph paragraph = new Paragraph();
        paragraph.setBorder(new SolidBorder(1));
        return paragraph;
    }

    private static Paragraph buildParagraph(boolean vertical, CjkTextSpec... specs) {
        Paragraph paragraph = vertical ? verticalParagraph() : horizontalParagraph();
        for (CjkTextSpec spec : specs) {
            Text text = new Text(spec.content);
            text.setFont(spec.font);
            text.setFontSize(spec.fontSize);
            if (spec.backgroundColor != null) {
                text.setBackgroundColor(spec.backgroundColor);
            }
            paragraph.add(text);
        }
        return paragraph;
    }

    private static final class CjkTextSpec {
        protected final String content;
        protected final PdfFont font;
        protected final float fontSize;
        protected Color backgroundColor;

        protected CjkTextSpec(String content, PdfFont font, float fontSize) {
            this.content = content;
            this.font = font;
            this.fontSize = fontSize;
        }

        protected CjkTextSpec backgroundColor(Color color) {
            this.backgroundColor = color;
            return this;
        }
    }
}