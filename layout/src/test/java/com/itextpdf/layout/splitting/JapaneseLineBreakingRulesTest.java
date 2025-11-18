/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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
package com.itextpdf.layout.splitting;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.TestUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("IntegrationTest")
public class JapaneseLineBreakingRulesTest extends ExtendedITextTest {
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/layout/splitting/JapaneseLineBreakingRulesTest/";
    private static final String DESTINATION_FOLDER = TestUtil.getOutputPath() + "/layout/splitting/JapaneseLineBreakingRulesTest/";
    private static final String FONTS_FOLDER = "./src/test/resources/com/itextpdf/layout/fonts/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    // ---------------- Tests for https://www.w3.org/TR/jlreq/?lang=en#characters_not_starting_a_line section
    @Test
    public void closingBracketsNotStartingLineTest() throws IOException, InterruptedException {
        createPdfAndCompare("closingBracketsNotStartingLine",
                Arrays.asList("’", "”", ")", "〕", "]", "}", "〉", "》", "」", "』", "】", "⦆", "〙", "〗", "»", "〟"),
                null);
    }

    @Test
    public void hyphensNotStartingLineTest() throws IOException, InterruptedException {
        createPdfAndCompare("hyphensNotStartingLine",
                Arrays.asList("‐", "〜", "゠", "–"),
                null);
    }

    @Test
    public void dividingPunctuationMarksNotStartingLineTest() throws IOException, InterruptedException {
        createPdfAndCompare("dividingPunctuationMarksNotStartingLine",
                Arrays.asList("!", "?", "‼", "⁇", "⁈", "⁉"),
                null);
    }

    @Test
    public void middleDotsNotStartingLineTest() throws IOException, InterruptedException {
        createPdfAndCompare("middleDotsNotStartingLine",
                Arrays.asList("・", ":", ";"),
                null);
    }

    @Test
    public void fullStopsNotStartingLineTest() throws IOException, InterruptedException {
        createPdfAndCompare("fullStopsNotStartingLine",
                Arrays.asList("。", "."),
                null);
    }

    @Test
    public void commasNotStartingLineTest() throws IOException, InterruptedException {
        createPdfAndCompare("commasNotStartingLine",
                Arrays.asList("、", ","),
                null);
    }

    @Test
    public void iterationMarksNotStartingLineTest() throws IOException, InterruptedException {
        createPdfAndCompare("iterationMarksNotStartingLine",
                Arrays.asList("ヽ", "ヾ", "ゝ", "ゞ", "々", "〻"),
                null);
    }

    @Test
    public void prolongedSoundMarkNotStartingLineTest() throws IOException, InterruptedException {
        createPdfAndCompare("prolongedSoundMarkNotStartingLine",
                Arrays.asList("ー"),
                null);
    }

    @Test
    public void smallKanaNotStartingLineTest() throws IOException, InterruptedException {
        createPdfAndCompare("smallKanaNotStartingLine",
                // ㇷ゚	<31F7, 309A> is missing, because 31F7 is already in the list
                Arrays.asList(
                        "ぁ", "ぃ", "ぅ", "ぇ", "ぉ", "ァ", "ィ", "ゥ", "ェ", "ォ", "っ", "ゃ", "ゅ", "ょ",
                        "ゎ", "ゕ", "ゖ", "ッ", "ャ", "ュ", "ョ", "ヮ", "ヵ", "ヶ", "ㇰ", "ㇱ", "ㇲ", "ㇳ",
                        "ㇴ", "ㇵ", "ㇶ", "ㇷ", "ㇸ", "ㇹ", "ㇺ", "ㇻ", "ㇼ", "ㇽ", "ㇾ", "ㇿ"
                ),
                null);
    }

    @Test
    public void warichuClosingBracketsNotStartingLineTest() throws IOException, InterruptedException {
        createPdfAndCompare("warichuClosingBracketsNotStartingLine",
                Arrays.asList(")", "〕", "]"),
                null);
    }

    // ---------------- Tests for https://www.w3.org/TR/jlreq/?lang=en#characters_not_ending_a_line section
    @Test
    public void openingBracketsNotEndingLineTest() throws IOException, InterruptedException {
        createPdfAndCompare("openingBracketsNotEndingLine",
                null,
                Arrays.asList("‘", "“", "(", "〔", "[", "{", "〈", "《", "「", "『", "【", "⦅", "〘", "〖", "«", "〝"));
    }

    @Test
    public void warichuOpeningBracketsNotEndingLineTest() throws IOException, InterruptedException {
        createPdfAndCompare("warichuOpeningBracketsNotEndingLine",
                null,
                Arrays.asList("(", "〔", "[")
        );
    }

    // ---------------- Tests for https://www.w3.org/TR/jlreq/?lang=en#unbreakable_character_sequences
    @Test
    public void inseparableCharsSequenceTest() throws IOException, InterruptedException {
        // 5th point from https://www.w3.org/TR/jlreq/?lang=en#notes_a3
        createPdfAndCompare("inseparableCharsSequence",
                Arrays.asList("—", "…", "‥", "〵", "〵"),
                Arrays.asList("—", "…", "‥", "〳", "〴"));
    }

    @Test
    // TODO DEVSIX-4863 Layout splitting logic handles negative values incorrectly if they are not in the very beginning of Text element
    public void europeanNumeralsSequenceTest() throws IOException, InterruptedException {
        createPdfAndCompare("europeanNumeralsSequence",
                Arrays.asList("2", "9", "9"),
                Arrays.asList("1", "-", "+"));
    }

    @Test
    public void prefixedAbbreviationsSequenceTest() throws IOException, InterruptedException {
        createPdfAndCompare("prefixedAbbreviationsSequence",
                Arrays.asList("9", "9", "9", "9", "9", "9"),
                Arrays.asList("¥", "$", "£", "#", "€", "№"));
    }

    @Test
    public void postfixedAbbreviationsSequenceTest() throws IOException, InterruptedException {
        createPdfAndCompare("postfixedAbbreviationsSequence",
                Arrays.asList(
                        "°", "′", "″", "℃", "¢", "%", "‰", "㏋", "ℓ", "㌃", "㌍", "㌔", "㌘", "㌢", "㌣",
                        "㌦", "㌧", "㌫", "㌶", "㌻", "㍉", "㍊", "㍍", "㍑", "㍗", "㎎", "㎏", "㎜", "㎝", "㎞", "㎡", "㏄"
                )
                ,
                Arrays.asList(
                        "9", "9", "9", "9", "9", "9", "9", "9", "9", "9", "9", "9", "9", "9", "9",
                        "9", "9", "9", "9", "9", "9", "9", "9", "9", "9", "9", "9", "9", "9", "9", "9", "9"
                ));
    }

    @Test
    public void unbreakableCharsWithoutSequenceEndedLineTest() throws IOException, InterruptedException {
        createPdfAndCompare("unbreakableCharsWithoutSequenceEndedLine",
                null,
                Arrays.asList("—", "…", "‥", "〳", "〴",
                        "¥", "$", "£", "#", "€", "№",
                        "°", "′", "″", "℃", "¢", "%", "‰", "㏋", "ℓ", "㌃", "㌍", "㌔", "㌘", "㌢", "㌣",
                        "㌦", "㌧", "㌫", "㌶", "㌻", "㍉", "㍊", "㍍", "㍑", "㍗", "㎎", "㎏", "㎜", "㎝", "㎞", "㎡", "㏄"));
    }

    @Test
    public void unbreakableCharsWithoutSequenceStartedLineTest() throws IOException, InterruptedException {
        createPdfAndCompare("unbreakableCharsWithoutSequenceStartedLine",
                Arrays.asList("—", "…", "‥", "〵", "〵",
                        "¥", "$", "£", "#", "€", "№",
                        "°", "′", "″", "℃", "¢", "%", "‰", "㏋", "ℓ", "㌃", "㌍", "㌔", "㌘", "㌢", "㌣",
                        "㌦", "㌧", "㌫", "㌶", "㌻", "㍉", "㍊", "㍍", "㍑", "㍗", "㎎", "㎏", "㎜", "㎝", "㎞", "㎡", "㏄"),
                null);
    }

    private static void createPdfAndCompare(String pdfName, List<String> startLineChars, List<String> endLineChars)
            throws IOException, InterruptedException {

        String outFileName = DESTINATION_FOLDER + pdfName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + pdfName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
                Document document = new Document(pdfDocument)) {

            document.setFont(PdfFontFactory.createFont(FONTS_FOLDER + "NotoSansJP-Regular.ttf"));
            if (startLineChars != null && endLineChars != null) {
                if (startLineChars.size() != endLineChars.size()) {
                    Assertions.fail("startLineChars size not equal endLineChars size");
                }
                for (int i = 0; i < startLineChars.size(); i++) {
                    Div div = createDiv(startLineChars.get(i), endLineChars.get(i));
                    document.add(div);
                }

            } else if (startLineChars != null) {
                for (String startLineChar : startLineChars) {
                    Div div = createDiv(startLineChar, null);
                    document.add(div);
                }
            } else if (endLineChars != null) {
                for (String endLineChar : endLineChars) {
                    Div div = createDiv(null, endLineChar);
                    document.add(div);
                }
            }
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    private static Div createDiv(String startLineChar, String endLineChar) {
        Div parentDiv = new Div();
        Div div = new Div();
        div.setBorder(new SolidBorder(ColorConstants.RED, 1));
        div.setWidth(159);
        Paragraph p = null;
        if (startLineChar != null && endLineChar != null) {
            parentDiv.add(new Paragraph("End line char is '" + endLineChar + "', start line char is '" + startLineChar + "'"));
            p = new Paragraph("に関連する主要なステーク" + endLineChar + startLineChar + "以下の項目");
        } else if (endLineChar != null && endLineChar.length() == 1) {
            parentDiv.add(new Paragraph("End line character for check is '" + endLineChar + "'"));
            p = new Paragraph("に関連する主要なステーク" + endLineChar + "以下の項目");
        } else if (startLineChar != null && startLineChar.length() == 1) {
            parentDiv.add(new Paragraph("Start line character for check is '" + startLineChar + "'"));
            p = new Paragraph("に関連する主要なステークホ" + startLineChar + "以下の項目");
        } else {
            Assertions.fail("Wrong start and/or end line");
        }

        div.add(p);
        parentDiv.add(div);
        parentDiv.setKeepTogether(true);
        return parentDiv;
    }
}
