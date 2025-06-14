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
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfUAConformance;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.filespec.PdfFileSpec;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.pdfua.UaValidationTestFramework;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.TestUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.List;

@Tag("IntegrationTest")
public class PdfUAEmbeddedFilesCheckTest extends ExtendedITextTest {

    private static final String DESTINATION_FOLDER = TestUtil.getOutputPath() + "/pdfua/PdfUAFormulaTest/";
    private static final String FONT = "./src/test/resources/com/itextpdf/pdfua/font/FreeSans.ttf";

    @BeforeAll
    public static void before() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    public static List<PdfUAConformance> data() {
        return UaValidationTestFramework.getConformanceList();
    }

    @ParameterizedTest
    @MethodSource("data")
    public void pdfuaWithEmbeddedFilesWithoutFTest(PdfUAConformance pdfUAConformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER);
        framework.addBeforeGenerationHook(pdfDocument -> {
            PdfFileSpec fs = PdfFileSpec.createEmbeddedFileSpec(
                    pdfDocument, "file".getBytes(), "description", "file.txt", null, null, null);
            PdfDictionary fsDict = (PdfDictionary) fs.getPdfObject();
            fsDict.remove(PdfName.F);
            pdfDocument.addFileAttachment("file.txt", fs);
        });

        if (pdfUAConformance == PdfUAConformance.PDF_UA_1) {
            framework.assertBothFail("pdfuaWithEmbeddedFilesWithoutF",
                    PdfUAExceptionMessageConstants.FILE_SPECIFICATION_DICTIONARY_SHALL_CONTAIN_F_KEY_AND_UF_KEY, pdfUAConformance);
        } else if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
            framework.assertBothValid("pdfuaWithEmbeddedFilesWithoutF", pdfUAConformance);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void pdfuaWithEmbeddedFilesWithoutUFTest(PdfUAConformance pdfUAConformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER);
        framework.addBeforeGenerationHook(pdfDocument -> {
            pdfDocument.addNewPage();
            PdfFileSpec fs = PdfFileSpec.createEmbeddedFileSpec(
                    pdfDocument, "file".getBytes(), "description", "file.txt", null, null, null);
            PdfDictionary fsDict = (PdfDictionary) fs.getPdfObject();
            fsDict.remove(PdfName.UF);
            pdfDocument.addFileAttachment("file.txt", fs);
        });

        if (pdfUAConformance == PdfUAConformance.PDF_UA_1) {
            framework.assertBothFail("pdfuaWithEmbeddedFilesWithoutUF",
                    PdfUAExceptionMessageConstants.FILE_SPECIFICATION_DICTIONARY_SHALL_CONTAIN_F_KEY_AND_UF_KEY, pdfUAConformance);
        } else if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
            framework.assertBothValid("pdfuaWithEmbeddedFilesWithoutUF", pdfUAConformance);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void pdfuaWithValidEmbeddedFileTest(PdfUAConformance pdfUAConformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER);
        framework.addBeforeGenerationHook((pdfDocument -> {
            addEmbeddedFile(pdfDocument, "some test pdf file");
        }));
        framework.assertBothValid("pdfuaWithValidEmbeddedFile", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void embeddedFilesWithFileSpecWithoutDescTest(PdfUAConformance pdfUAConformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER);
        framework.addBeforeGenerationHook((pdfDocument -> {
            addEmbeddedFile(pdfDocument, null);
        }));
        if (pdfUAConformance == PdfUAConformance.PDF_UA_1) {
            framework.assertBothValid("embeddedFilesWithFileSpecWithoutDesc", pdfUAConformance);
        } else if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
            framework.assertBothFail("embeddedFilesWithFileSpecWithoutDesc", PdfUAExceptionMessageConstants.
                    DESC_IS_REQUIRED_ON_ALL_FILE_SPEC_FROM_THE_EMBEDDED_FILES, pdfUAConformance);
        }
    }

    private static void addEmbeddedFile(PdfDocument pdfDocument, String description) {
        PdfFont font;
        try {
            font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED);
        } catch (IOException e) {
            // Rethrow as unchecked to fail the test.
            throw new RuntimeException();
        }
        PdfPage page = pdfDocument.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);

        TagTreePointer tagPointer = new TagTreePointer(pdfDocument)
                .setPageForTagging(page)
                .addTag(StandardRoles.P);

        canvas.openTag(tagPointer.getTagReference())
                .saveState()
                .beginText()
                .setFontAndSize(font, 12)
                .moveText(100, 100)
                .showText("Test text.")
                .endText()
                .restoreState()
                .closeTag();

        byte[] somePdf = new byte[35];
        pdfDocument.addAssociatedFile("some test pdf file",
                PdfFileSpec.createEmbeddedFileSpec(pdfDocument, somePdf, description, "foo.pdf", PdfName.ApplicationPdf,
                        null, new PdfName("Data")));
    }
}
