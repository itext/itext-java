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
package com.itextpdf.layout;

import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.io.IOException;

@Tag("IntegrationTest")
public class SurrPairTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/SurrPairTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/layout/SurrPairTest/";
    public static final String fontsFolder = "./src/test/resources/com/itextpdf/layout/fonts/";

    @BeforeAll
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void surrogatePairFrom2Chars() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "surrogatePairFrom2Chars.pdf";
        String cmpFileName = sourceFolder + "cmp_" + "surrogatePairFrom2Chars.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
        Document document = new Document(pdfDocument);

        PdfFont font = PdfFontFactory.createFont(fontsFolder + "NotoEmoji-Regular.ttf", PdfEncodings.IDENTITY_H);

        //😉
        String winkinkSmile = "\uD83D\uDE09";

        Paragraph paragraph = new Paragraph(winkinkSmile);

        document.setFont(font);
        document.add(paragraph);
        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder));
    }

    @Test
    public void surrogatePair2Pairs() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "surrogatePair2Pairs.pdf";
        String cmpFileName = sourceFolder + "cmp_" + "surrogatePair2Pairs.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
        Document document = new Document(pdfDocument);

        PdfFont font = PdfFontFactory.createFont(fontsFolder + "NotoEmoji-Regular.ttf", PdfEncodings.IDENTITY_H);

        //🇧🇾
        String belarusAbbr = "\uD83C\uDDE7\uD83C\uDDFE";

        Paragraph paragraph = new Paragraph(belarusAbbr);

        document.setFont(font);
        document.add(paragraph);
        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder));
    }

    @Test
    public void surrogatePairFullCharacter() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "surrogatePairFullCharacter.pdf";
        String cmpFileName = sourceFolder + "cmp_" + "surrogatePairFullCharacter.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
        Document document = new Document(pdfDocument);

        PdfFont font = PdfFontFactory.createFont(fontsFolder + "NotoEmoji-Regular.ttf", PdfEncodings.IDENTITY_H);

        //🛀
        String em = new String(Character.toChars(0x0001F6C0));

        Paragraph paragraph = new Paragraph(em);

        document.setFont(font);
        document.add(paragraph);
        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder));
    }

    @Test
    //TODO DEVSIX-3307
    @LogMessages(messages = @LogMessage(messageTemplate = IoLogMessageConstant.FONT_SUBSET_ISSUE))
    public void surrogatePairCombingFullSurrs() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "surrogatePairCombingFullSurrs.pdf";
        String cmpFileName = sourceFolder + "cmp_" + "surrogatePairCombingFullSurrs.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
        Document document = new Document(pdfDocument);

        PdfFont font = PdfFontFactory.createFont(fontsFolder + "NotoColorEmoji.ttf", PdfEncodings.IDENTITY_H);

        //🏴󠁧󠁢󠁥󠁮󠁧󠁿
        String firstPair = new String(Character.toChars(0x0001F3F4));
        String secondPair = new String(Character.toChars(0x000E0067));
        String thirdPair = new String(Character.toChars(0x000E0062));
        String forthPair = new String(Character.toChars(0x000E0065));
        String fifthPair = new String(Character.toChars(0x000E006E));
        String sixthPair = new String(Character.toChars(0x000E0067));
        String seventhPair = new String(Character.toChars(0x000E007F));
        String blackFlag = firstPair + secondPair + thirdPair + forthPair + fifthPair + sixthPair + seventhPair;

        Paragraph paragraph = new Paragraph(blackFlag);

        document.setFont(font);
        document.add(paragraph);
        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder));
    }

    @Test
    //TODO DEVSIX-3307
    @LogMessages(messages = @LogMessage(messageTemplate = IoLogMessageConstant.FONT_SUBSET_ISSUE))
    public void surrogatePairCombingFullSurrsWithNoSurrs() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "surrogatePairCombingFullSurrsWithNoSurrs.pdf";
        String cmpFileName = sourceFolder + "cmp_" + "surrogatePairCombingFullSurrsWithNoSurrs.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
        Document document = new Document(pdfDocument);

        PdfFont font = PdfFontFactory.createFont(fontsFolder + "NotoColorEmoji.ttf", PdfEncodings.IDENTITY_H);

        //World Map
        String firstPair = new String(Character.toChars(0x0001F5FA));
        String space = "\u0020";

        //🗽
        String secondPair = new String(Character.toChars(0x0001F5FD));

        //Satellite
        String thirdPair = new String(Character.toChars(0x0001F6F0));
        String allPairs = firstPair + space + secondPair + space + thirdPair;

        Paragraph paragraph = new Paragraph(allPairs);

        document.setFont(font);
        document.add(paragraph);
        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder));
    }
}
