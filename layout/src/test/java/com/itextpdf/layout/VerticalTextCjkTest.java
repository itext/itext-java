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
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.AreaBreakType;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.Underline;
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
import java.util.Collections;
import java.util.Map;

@Tag("IntegrationTest")
public class VerticalTextCjkTest extends ExtendedITextTest {
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/layout/VerticalTextCjkTest/";
    private static final String FONTS_FOLDER = "./src/test/resources/com/itextpdf/layout/fonts/";
    private static final String DESTINATION_FOLDER = TestUtil.getOutputPath() + "/layout/VerticalTextCjkTest/";

    private static final String NOTO_SANS_SC = FONTS_FOLDER + "NotoSansCJKsc-Regular.otf";
    private static final String NOTO_SANS_SC_BOLD = FONTS_FOLDER + "NotoSansCJKsc-Bold.otf";
    private static final String NOTO_SERIF_SC = FONTS_FOLDER + "NotoSerifCJKsc-Regular.otf";
    private static final String NOTO_SANS_JP = FONTS_FOLDER + "NotoSansCJKjp-Regular.otf";
    private static final String NOTO_SANS_KR = FONTS_FOLDER + "NotoSansCJKkr-Regular.otf";
    private static final String NOTO_SANS_MONGOLIAN = FONTS_FOLDER + "NotoSansMongolian-Regular.ttf";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void verticalTextSimplifiedChineseTest() throws IOException, InterruptedException {
        String fileName = "verticalTextSimplifiedChinese";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        CjkTextSpec spec = new CjkTextSpec("你好，这是一段竖排中文文本。汉字应保持直立。", loadCjkFont(NOTO_SANS_SC), 24);
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            document.add(buildParagraph(true, spec));
            document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
            document.add(buildParagraph(false, spec));
        }

        Map<Character, Integer> extractedCounts = VerticalTextTestUtil.extractPageCharacterCounts(outFileName);
        Assertions.assertTrue(VerticalTextTestUtil.containsAllCharacters(extractedCounts, "你好"));
        Assertions.assertTrue(VerticalTextTestUtil.containsAllCharacters(extractedCounts, "竖排中文文本"));
        Assertions.assertTrue(VerticalTextTestUtil.containsAllCharacters(extractedCounts, "汉字应保持直立"));
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    public void verticalTextJapaneseWithSmallKanaTest() throws IOException, InterruptedException {
        String fileName = "verticalTextJapaneseWithSmallKana";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        CjkTextSpec spec = new CjkTextSpec(
                "こんにちは、これは縦書きの日本語のテキストです。ちょっと難しいです。ラーメン。", loadCjkFont(NOTO_SANS_JP), 24);
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            document.add(buildParagraph(true, spec));
            document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
            document.add(buildParagraph(false, spec));
        }

        Map<Character, Integer> extractedCounts = VerticalTextTestUtil.extractPageCharacterCounts(outFileName);
        Assertions.assertTrue(VerticalTextTestUtil.containsAllCharacters(extractedCounts, "こんにちは"));
        Assertions.assertTrue(VerticalTextTestUtil.containsAllCharacters(extractedCounts, "縦書きの日本語のテキストです"));
        Assertions.assertTrue(VerticalTextTestUtil.containsAllCharacters(extractedCounts, "ちょっと難しいです"));
        Assertions.assertTrue(VerticalTextTestUtil.containsAllCharacters(extractedCounts, "ラーメン"));
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    public void verticalTextKoreanHangulTest() throws IOException, InterruptedException {
        String fileName = "verticalTextKoreanHangul";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        CjkTextSpec spec = new CjkTextSpec("안녕하세요, 이것은 세로쓰기 한국어 텍스트입니다.", loadCjkFont(NOTO_SANS_KR), 24);
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            document.add(buildParagraph(true, spec));
            document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
            document.add(buildParagraph(false, spec));
        }

        Map<Character, Integer> extractedCounts = VerticalTextTestUtil.extractPageCharacterCounts(outFileName);
        Assertions.assertTrue(VerticalTextTestUtil.containsAllCharacters(extractedCounts, "안녕하세요"));
        Assertions.assertTrue(VerticalTextTestUtil.containsAllCharacters(extractedCounts, "세로쓰기 한국어 텍스트입니다"));
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    public void verticalTextMongolianTest() throws IOException, InterruptedException {
        String fileName = "verticalTextMongolian";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        CjkTextSpec spec = new CjkTextSpec("ᠮᠣᠩᠭᠣᠯ ᠪᠢᠴᠢᠭ ᠣᠷᠴᠢᠨ ᠴᠠᠭ", loadCjkFont(NOTO_SANS_MONGOLIAN), 24);
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            document.add(buildParagraph(true, spec));
            document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
            document.add(buildParagraph(false, spec));
        }

        Map<Character, Integer> extractedCounts = VerticalTextTestUtil.extractPageCharacterCounts(outFileName);
        Assertions.assertTrue(VerticalTextTestUtil.containsAllCharacters(extractedCounts, "ᠮᠣᠩᠭᠣᠯ"));
        Assertions.assertTrue(VerticalTextTestUtil.containsAllCharacters(extractedCounts, "ᠪᠢᠴᠢᠭ"));
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    public void verticalTextCjkPunctuationTest() throws IOException, InterruptedException {
        String fileName = "verticalTextCjkPunctuation";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        CjkTextSpec spec = new CjkTextSpec("彼は「こんにちは」と言った。それから、『さようなら』も言った。", loadCjkFont(NOTO_SANS_JP), 24);
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            document.add(buildParagraph(true, spec));
            document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
            document.add(buildParagraph(false, spec));
        }

        Map<Character, Integer> extractedCounts = VerticalTextTestUtil.extractPageCharacterCounts(outFileName);
        Assertions.assertTrue(VerticalTextTestUtil.containsAllCharacters(extractedCounts, "彼は「こんにちは」と言った"));
        Assertions.assertTrue(VerticalTextTestUtil.containsAllCharacters(extractedCounts, "それから、『さようなら』も言った"));
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    public void verticalTextCjkWithEmbeddedLatinAndDigitsTest() throws IOException, InterruptedException {
        String fileName = "verticalTextCjkWithEmbeddedLatinAndDigits";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        CjkTextSpec spec = new CjkTextSpec(
                "今日は2026年8月19日、iTextのバージョンは8です。ABC123もテストします。", loadCjkFont(NOTO_SANS_JP), 20);
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            document.add(buildParagraph(true, spec));
            document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
            document.add(buildParagraph(false, spec));
        }

        Map<Character, Integer> extractedCounts = VerticalTextTestUtil.extractPageCharacterCounts(outFileName);
        Assertions.assertTrue(VerticalTextTestUtil.containsAllCharacters(extractedCounts, "2026"));
        Assertions.assertTrue(VerticalTextTestUtil.containsAllCharacters(extractedCounts, "8"));
        Assertions.assertTrue(VerticalTextTestUtil.containsAllCharacters(extractedCounts, "19"));
        Assertions.assertTrue(VerticalTextTestUtil.containsAllCharacters(extractedCounts, "iText"));
        Assertions.assertTrue(VerticalTextTestUtil.containsAllCharacters(extractedCounts, "ABC123"));
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    public void verticalTextVerticalMetricsFontComparisonTest() throws IOException, InterruptedException {
        String fileName = "verticalTextVerticalMetricsFontComparison";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        String sentence = "竖排文字的高度取决于字体的垂直度量。";
        CjkTextSpec sansSpec = new CjkTextSpec(sentence, loadCjkFont(NOTO_SANS_SC), 24)
                .backgroundColor(ColorConstants.LIGHT_GRAY);
        CjkTextSpec serifSpec = new CjkTextSpec(sentence, loadCjkFont(NOTO_SERIF_SC), 24)
                .backgroundColor(ColorConstants.CYAN);
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            document.add(buildParagraph(true, sansSpec));
            document.add(buildParagraph(true, serifSpec));
            document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
            document.add(buildParagraph(false, sansSpec));
            document.add(buildParagraph(false, serifSpec));
        }

        Map<Character, Integer> extractedCounts = VerticalTextTestUtil.extractPageCharacterCounts(outFileName);
        Assertions.assertTrue(VerticalTextTestUtil.containsAllCharacters(extractedCounts, sentence, 2));
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    public void verticalTextCjkLineBreakingWithoutWordBoundariesTest() throws IOException, InterruptedException {
        String fileName = "verticalTextCjkLineBreakingWithoutWordBoundaries";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        String text = "这是一段没有任何标点或空格的连续中文文本用来测试竖排换行是否可以在任意汉字之间发生而不需要像西文那样等待单词边界";
        CjkTextSpec spec = new CjkTextSpec(text, loadCjkFont(NOTO_SANS_SC), 20).backgroundColor(ColorConstants.LIGHT_GRAY);
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            Paragraph verticalParagraph = buildParagraph(true, spec);
            verticalParagraph.setHeight(150);
            document.add(verticalParagraph);

            document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
            Paragraph horizontalParagraph = buildParagraph(false, spec);
            horizontalParagraph.setWidth(150);
            document.add(horizontalParagraph);
        }

        Map<Character, Integer> extractedCounts = VerticalTextTestUtil.extractPageCharacterCounts(outFileName);
        Assertions.assertTrue(VerticalTextTestUtil.containsAllCharacters(extractedCounts, text));
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    public void verticalTextCjkUnderlineAndStrikethroughTest() throws IOException, InterruptedException {
        String fileName = "verticalTextCjkUnderlineAndStrikethrough";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        CjkTextSpec underlinedSpec = new CjkTextSpec("下划线文本", loadCjkFont(NOTO_SANS_SC), 24)
                .backgroundColor(ColorConstants.LIGHT_GRAY)
                .underline(new Underline(ColorConstants.RED, 1, .75F, 0, 0, 1 / 4F, PdfCanvasConstants.LineCapStyle.BUTT));
        CjkTextSpec strikethroughSpec = new CjkTextSpec("删除线文本", loadCjkFont(NOTO_SANS_SC), 24)
                .backgroundColor(ColorConstants.CYAN)
                .underline(new Underline(ColorConstants.BLUE, 1, .75F, 0, 0, 1 / 2F, PdfCanvasConstants.LineCapStyle.BUTT));
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            document.add(buildParagraph(true, underlinedSpec, strikethroughSpec));
            document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
            document.add(buildParagraph(false, underlinedSpec, strikethroughSpec));
        }

        Map<Character, Integer> extractedCounts = VerticalTextTestUtil.extractPageCharacterCounts(outFileName);
        Assertions.assertTrue(VerticalTextTestUtil.containsAllCharacters(extractedCounts, "下划线文本"));
        Assertions.assertTrue(VerticalTextTestUtil.containsAllCharacters(extractedCounts, "删除线文本"));
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    public void verticalTextCjkBoldItalicSimulationTest() throws IOException, InterruptedException {
        String fileName = "verticalTextCjkBoldItalicSimulation";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        CjkTextSpec regularSpec = new CjkTextSpec("常规字体", loadCjkFont(NOTO_SANS_SC), 24)
                .backgroundColor(ColorConstants.LIGHT_GRAY);
        CjkTextSpec simulatedBoldItalicSpec = new CjkTextSpec("模拟粗斜体", loadCjkFont(NOTO_SANS_SC), 24)
                .backgroundColor(ColorConstants.CYAN)
                .boldSimulation()
                .italicSimulation();
        CjkTextSpec realBoldSpec = new CjkTextSpec("真实粗体字体", loadCjkFont(NOTO_SANS_SC_BOLD), 24)
                .backgroundColor(ColorConstants.LIGHT_GRAY);
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            document.add(buildParagraph(true, regularSpec, simulatedBoldItalicSpec, realBoldSpec));
            document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
            document.add(buildParagraph(false, regularSpec, simulatedBoldItalicSpec, realBoldSpec));
        }

        Map<Character, Integer> extractedCounts = VerticalTextTestUtil.extractPageCharacterCounts(outFileName);
        Assertions.assertTrue(VerticalTextTestUtil.containsAllCharacters(extractedCounts, "常规字体"));
        Assertions.assertTrue(VerticalTextTestUtil.containsAllCharacters(extractedCounts, "模拟粗斜体"));
        Assertions.assertTrue(VerticalTextTestUtil.containsAllCharacters(extractedCounts, "真实粗体字体"));
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    //TODO DEVSIX-10167: Update test after fix
    @Test
    public void verticalTextCjkIdeographicSpaceVsRegularSpaceTest() throws IOException, InterruptedException {
        String fileName = "verticalTextCjkIdeographicSpaceVsRegularSpace";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        CjkTextSpec ideographicSpec = new CjkTextSpec("文字\u3000文字", loadCjkFont(NOTO_SANS_SC), 24)
                .backgroundColor(ColorConstants.LIGHT_GRAY);
        CjkTextSpec regularSpaceSpec = new CjkTextSpec("文字 文字", loadCjkFont(NOTO_SANS_SC), 24)
                .backgroundColor(ColorConstants.CYAN);
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            document.add(buildParagraph(true, ideographicSpec));
            document.add(buildParagraph(true, regularSpaceSpec));
            document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
            document.add(buildParagraph(false, ideographicSpec));
            document.add(buildParagraph(false, regularSpaceSpec));
        }

        Map<Character, Integer> extractedCounts = VerticalTextTestUtil.extractPageCharacterCounts(outFileName);
        Assertions.assertTrue(VerticalTextTestUtil.containsAllCharacters(extractedCounts, "文字", 4));
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    public void verticalTextCjkCustomLeadingTest() throws IOException, InterruptedException {
        String fileName = "verticalTextCjkCustomLeading";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        CjkTextSpec defaultLeadingSpec = new CjkTextSpec("默认行距\n默认行距", loadCjkFont(NOTO_SANS_SC), 20)
                .backgroundColor(ColorConstants.LIGHT_GRAY);
        CjkTextSpec customLeadingSpec = new CjkTextSpec("自定义行距\n自定义行距", loadCjkFont(NOTO_SANS_SC), 20)
                .backgroundColor(ColorConstants.CYAN);
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            document.add(buildParagraph(true, defaultLeadingSpec));
            Paragraph customLeadingVertical = buildParagraph(true, customLeadingSpec);
            customLeadingVertical.setMultipliedLeading(2.5F);
            document.add(customLeadingVertical);

            document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
            document.add(buildParagraph(false, defaultLeadingSpec));
            Paragraph customLeadingHorizontal = buildParagraph(false, customLeadingSpec);
            customLeadingHorizontal.setMultipliedLeading(2.5F);
            document.add(customLeadingHorizontal);
        }

        Map<Character, Integer> extractedCounts = VerticalTextTestUtil.extractPageCharacterCounts(outFileName);
        Assertions.assertTrue(VerticalTextTestUtil.containsAllCharacters(extractedCounts, "默认行距", 2));
        Assertions.assertTrue(VerticalTextTestUtil.containsAllCharacters(extractedCounts, "自定义行距", 2));
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    public void verticalTextCjkFullWidthVsHalfWidthFormsTest() throws IOException, InterruptedException {
        String fileName = "verticalTextCjkFullWidthVsHalfWidthForms";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        CjkTextSpec spec = new CjkTextSpec("全角：１２３ＡＢＣ 半角：123ABC", loadCjkFont(NOTO_SANS_SC), 20);
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            document.add(buildParagraph(true, spec));
            document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
            document.add(buildParagraph(false, spec));
        }

        Map<Character, Integer> extractedCounts = VerticalTextTestUtil.extractPageCharacterCounts(outFileName);
        Assertions.assertTrue(VerticalTextTestUtil.containsAllCharacters(extractedCounts, "全角"));
        Assertions.assertTrue(VerticalTextTestUtil.containsAllCharacters(extractedCounts, "１２３ＡＢＣ"));
        Assertions.assertTrue(VerticalTextTestUtil.containsAllCharacters(extractedCounts, "半角"));
        Assertions.assertTrue(VerticalTextTestUtil.containsAllCharacters(extractedCounts, "123ABC"));
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
            if (spec.underline != null) {
                text.setProperty(Property.UNDERLINE, Collections.singletonList(spec.underline));
            }
            if (spec.boldSimulation) {
                text.setProperty(Property.BOLD_SIMULATION, Boolean.TRUE);
            }
            if (spec.italicSimulation) {
                text.setProperty(Property.ITALIC_SIMULATION, Boolean.TRUE);
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
        protected Underline underline;
        protected boolean boldSimulation;
        protected boolean italicSimulation;

        protected CjkTextSpec(String content, PdfFont font, float fontSize) {
            this.content = content;
            this.font = font;
            this.fontSize = fontSize;
        }

        protected CjkTextSpec backgroundColor(Color color) {
            this.backgroundColor = color;
            return this;
        }

        protected CjkTextSpec underline(Underline underline) {
            this.underline = underline;
            return this;
        }

        protected CjkTextSpec boldSimulation() {
            this.boldSimulation = true;
            return this;
        }

        protected CjkTextSpec italicSimulation() {
            this.italicSimulation = true;
            return this;
        }
    }
}