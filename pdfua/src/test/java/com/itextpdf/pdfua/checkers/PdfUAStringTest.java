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
package com.itextpdf.pdfua.checkers;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfUAConformance;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfTextAnnotation;
import com.itextpdf.pdfua.UaValidationTestFramework;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.TestUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@Tag("IntegrationTest")
public class PdfUAStringTest extends ExtendedITextTest {
    private static final String DESTINATION_FOLDER = TestUtil.getOutputPath() + "/pdfua/PdfUAStringTest/";
    private static final Rectangle RECTANGLE = new Rectangle(100, 100, 100, 100);

    @BeforeAll
    public static void before() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    public static List<Integer> privateUseAreaSymbols() {
        return Arrays.asList(0xE004, 0xF0009, 0x10FFFA);
    }

    @Test
    public void validValueWithDocEncodingTest() throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER);
        framework.addBeforeGenerationHook(document -> {
            document.addNewPage();
            PdfString pdfString = new PdfString("value", PdfEncodings.PDF_DOC_ENCODING);
            document.getCatalog().put(PdfName.Lang, pdfString);
        });
        framework.assertBothValid("validValueWithDocEncoding", PdfUAConformance.PDF_UA_2);
    }

    @ParameterizedTest
    @MethodSource("privateUseAreaSymbols")
    public void puaValueWithDocEncodingTest(Integer puaSymbol) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER);
        String filename = "puaValueWithDocEncoding_" + getPuaValueName(puaSymbol);
        framework.addBeforeGenerationHook(document -> {
            PdfString pdfString = new PdfString("hello_" + new String(Character.toChars((int) puaSymbol)), PdfEncodings.PDF_DOC_ENCODING);
            PdfPage page = document.addNewPage();
            PdfAnnotation textAnnotation = new PdfTextAnnotation(RECTANGLE).setContents(pdfString);
            page.addAnnotation(textAnnotation);
        });
        // In this particular case validators which reopen the document cannot identify the problem, and strictly speaking PDF document is valid.
        // Since PDFDocEncoding doesn't have enough space to allocate this Unicode PUA symbol, it is simply not present in the resulting file.
        // Even though the file is valid, there was clearly an attempt to create human-readable PdfString with Unicode PUA, that's why we fail.
        framework.assertOnlyITextFail(filename, PdfUAExceptionMessageConstants.TEXT_STRING_USES_UNICODE_PUA, PdfUAConformance.PDF_UA_2);
    }

    @ParameterizedTest
    @MethodSource("privateUseAreaSymbols")
    public void puaValueWithUTF8Test(Integer puaSymbol) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER);
        String filename = "puaValueWithUTF8_" + getPuaValueName(puaSymbol);
        framework.addBeforeGenerationHook(document -> {
            PdfString pdfString = new PdfString("hello_" + new String(Character.toChars((int) puaSymbol)), PdfEncodings.UTF8);
            PdfPage page = document.addNewPage();
            PdfAnnotation textAnnotation = new PdfTextAnnotation(RECTANGLE).setSubject(pdfString);
            page.addAnnotation(textAnnotation);
        });
        // VeraPdf doesn't fail because they mistakenly don't check all the PdfString entries in the document.
        framework.assertOnlyITextFail(filename, PdfUAExceptionMessageConstants.TEXT_STRING_USES_UNICODE_PUA, PdfUAConformance.PDF_UA_2);
    }

    @ParameterizedTest
    @MethodSource("privateUseAreaSymbols")
    public void puaValueWithUTF16Test(Integer puaSymbol) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER);
        String filename = "puaValueWithUTF16_" + getPuaValueName(puaSymbol);
        framework.addBeforeGenerationHook(document -> {
            PdfString pdfString = new PdfString("hello_" + new String(Character.toChars((int) puaSymbol)), PdfEncodings.UNICODE_BIG);
            PdfPage page = document.addNewPage();
            PdfAnnotation textAnnotation = new PdfTextAnnotation(RECTANGLE).setSubject(pdfString);
            page.addAnnotation(textAnnotation);
        });
        // VeraPdf doesn't fail because they mistakenly don't check all the PdfString entries in the document.
        framework.assertOnlyITextFail(filename, PdfUAExceptionMessageConstants.TEXT_STRING_USES_UNICODE_PUA, PdfUAConformance.PDF_UA_2);
    }

    @ParameterizedTest
    @MethodSource("privateUseAreaSymbols")
    public void puaValueWithUTF16UnmarkedTest(Integer puaSymbol) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER);
        String filename = "puaValueWithUTF16Unmarked_" + getPuaValueName(puaSymbol);
        framework.addBeforeGenerationHook(document -> {
            PdfString pdfString = new PdfString("hello_" + new String(Character.toChars((int) puaSymbol)), PdfEncodings.UNICODE_BIG_UNMARKED);
            PdfPage page = document.addNewPage();
            PdfAnnotation textAnnotation = new PdfTextAnnotation(RECTANGLE).setSubject(pdfString);
            page.addAnnotation(textAnnotation);
        });
        framework.assertBothValid(filename, PdfUAConformance.PDF_UA_2);
    }

    @ParameterizedTest
    @MethodSource("privateUseAreaSymbols")
    public void puaValueInLangTest(Integer puaSymbol) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, false);
        String filename = "puaValueInLang_" + getPuaValueName(puaSymbol);
        framework.addBeforeGenerationHook(document -> {
            PdfString pdfString = new PdfString("hello_" + new String(Character.toChars((int) puaSymbol)), PdfEncodings.UTF8);
            document.addNewPage();
            document.getCatalog().setLang(pdfString);
        });
        // This test is only needed to reproduce veraPdf failure.
        // For now, we only were able to reproduce it when lang entry in catalog dictionary contains PUA.
        // However, iText logic fails earlier, because Lang entry must contain valid language identifier.
        framework.assertBothFail(filename, PdfUAExceptionMessageConstants.DOCUMENT_SHALL_CONTAIN_VALID_LANG_ENTRY, PdfUAConformance.PDF_UA_2);
    }

    @Test
    public void puaValueWithTest() throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, false);
        framework.addBeforeGenerationHook(document -> {
            document.addNewPage();
            PdfString pdfString = new PdfString(new String(Character.toChars(0xE005)), PdfEncodings.WINANSI);
            document.getCatalog().put(PdfName.Lang, pdfString);
        });
        framework.assertBothFail("puaValueWithUTF16", PdfUAConformance.PDF_UA_2);
    }

    private static String getPuaValueName(Integer puaSymbol) {
        switch (puaSymbol) {
            case 0xE004:
                return "PrivateArea";
            case 0xF0009:
                return "SupplementaryPrivateAreaA";
            case 0x10FFFA:
                return "SupplementaryPrivateAreaB";
        }
        return null;
    }
}
