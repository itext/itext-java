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
package com.itextpdf.pdfua.checkers;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.filespec.PdfFileSpec;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.pdfua.UaValidationTestFramework;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.io.FileNotFoundException;
import java.io.IOException;

@Tag("IntegrationTest")
public class PdfUAEmbeddedFilesCheckTest  extends ExtendedITextTest {

    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/pdfua/PdfUAFormulaTest/";
    private static final String FONT = "./src/test/resources/com/itextpdf/pdfua/font/FreeSans.ttf";

    private UaValidationTestFramework framework;


    @BeforeAll
    public static void before() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @BeforeEach
    public void initializeFramework() {
        framework = new UaValidationTestFramework(DESTINATION_FOLDER);
    }

    @Test
    public void pdfuaWithEmbeddedFilesWithoutFTest() throws FileNotFoundException {
        framework.addBeforeGenerationHook((pdfDocument) -> {
            PdfFileSpec fs = PdfFileSpec.createEmbeddedFileSpec(
                    pdfDocument, "file".getBytes(), "description", "file.txt", null, null, null);
            PdfDictionary fsDict = (PdfDictionary) fs.getPdfObject();
            fsDict.remove(PdfName.F);
            pdfDocument.addFileAttachment("file.txt", fs);
        });
        framework.assertBothFail("pdfuaWithEmbeddedFilesWithoutF",
                PdfUAExceptionMessageConstants.FILE_SPECIFICATION_DICTIONARY_SHALL_CONTAIN_F_KEY_AND_UF_KEY);
    }

    @Test
    public void pdfuaWithEmbeddedFilesWithoutUFTest() throws FileNotFoundException {
        framework.addBeforeGenerationHook((pdfDocument) -> {
            pdfDocument.addNewPage();
            PdfFileSpec fs = PdfFileSpec.createEmbeddedFileSpec(
                    pdfDocument, "file".getBytes(), "description", "file.txt", null, null, null);
            PdfDictionary fsDict = (PdfDictionary) fs.getPdfObject();
            fsDict.remove(PdfName.UF);
            pdfDocument.addFileAttachment("file.txt", fs);
        });
        framework.assertBothFail("pdfuaWithEmbeddedFilesWithoutUF",
                PdfUAExceptionMessageConstants.FILE_SPECIFICATION_DICTIONARY_SHALL_CONTAIN_F_KEY_AND_UF_KEY);
    }

    @Test
    public void pdfuaWithValidEmbeddedFileTest() throws FileNotFoundException {
        framework.addBeforeGenerationHook((pdfDocument -> {
            PdfFont font;
            try {
                font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED);
            } catch (IOException e) {
                //rethrow as unchecked to fail the test
                throw new RuntimeException();
            }
            PdfPage page1 = pdfDocument.addNewPage();
            PdfCanvas canvas = new PdfCanvas(page1);

            TagTreePointer tagPointer = new TagTreePointer(pdfDocument)
                    .setPageForTagging(page1)
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
                    PdfFileSpec.createEmbeddedFileSpec(pdfDocument, somePdf, "some test pdf file", "foo.pdf",
                            PdfName.ApplicationPdf, null, new PdfName("Data")));
        }));
        framework.assertBothValid("pdfuaWithValidEmbeddedFile");
    }

}
