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
package com.itextpdf.pdfa;

import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.kernel.pdf.PdfAConformanceLevel;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfOutputIntent;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.pdf.filespec.PdfFileSpec;
import com.itextpdf.pdfa.exceptions.PdfAConformanceException;
import com.itextpdf.pdfa.exceptions.PdfaExceptionMessageConstant;
import com.itextpdf.test.ExtendedITextTest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class PdfA4EmbeddedFilesCheckTest extends ExtendedITextTest {
    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/pdfa/";
    public static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/pdfa/PdfA4EmbeddedFilesCheckTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    // Test with successful creation PDF/A-4F (the same for PDF/A-4E and PDF/A-4) in
    // the embedded files meaning can be found in other tests (e.g. PdfA4CatalogCheckTest).

    @Test
    public void pdfA4fWithoutEmbeddedFilesTest() throws IOException {
        String outPdf = DESTINATION_FOLDER + "pdfA4fWithoutEmbeddedFilesTest.pdf";

        PdfWriter writer = new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4F, createOutputIntent());
        doc.addNewPage();

        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assertions.assertEquals(PdfaExceptionMessageConstant.NAME_DICTIONARY_SHALL_CONTAIN_EMBEDDED_FILES_KEY, e.getMessage());
    }

    @Test
    public void pdfA4fWithEmbeddedFilesWithoutFTest() throws IOException {
        String outPdf = DESTINATION_FOLDER + "pdfA4fWithEmbeddedFilesWithoutFTest.pdf";

        PdfWriter writer = new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4F, createOutputIntent());
        doc.addNewPage();

        PdfFileSpec fs = PdfFileSpec.createEmbeddedFileSpec(
                doc, "file".getBytes(), "description", "file.txt", null, null, null);
        PdfDictionary fsDict = (PdfDictionary) fs.getPdfObject();
        fsDict.remove(PdfName.F);
        doc.addFileAttachment("file.txt", fs);

        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assertions.assertEquals(PdfAConformanceException.FILE_SPECIFICATION_DICTIONARY_SHALL_CONTAIN_F_KEY_AND_UF_KEY, e.getMessage());
    }

    @Test
    public void pdfA4fWithEmbeddedFilesWithoutUFTest() throws IOException {
        String outPdf = DESTINATION_FOLDER + "pdfA4fWithEmbeddedFilesWithoutUFTest.pdf";

        PdfWriter writer = new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4F, createOutputIntent());
        doc.addNewPage();

        PdfFileSpec fs = PdfFileSpec.createEmbeddedFileSpec(
                doc, "file".getBytes(), "description", "file.txt", null, null, null);
        PdfDictionary fsDict = (PdfDictionary) fs.getPdfObject();
        fsDict.remove(PdfName.UF);
        doc.addFileAttachment("file.txt", fs);

        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assertions.assertEquals(PdfAConformanceException.FILE_SPECIFICATION_DICTIONARY_SHALL_CONTAIN_F_KEY_AND_UF_KEY, e.getMessage());
    }

    @Test
    public void pdfA4fWithEmbeddedFilesWithoutAFRTest() throws IOException {
        String outPdf = DESTINATION_FOLDER + "pdfA4fWithEmbeddedFilesWithoutAFRTest.pdf";

        PdfWriter writer = new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4F, createOutputIntent());
        doc.addNewPage();

        PdfFileSpec fs = PdfFileSpec.createEmbeddedFileSpec(
                doc, "file".getBytes(), "description", "file.txt", null, null, null);
        PdfDictionary fsDict = (PdfDictionary) fs.getPdfObject();
        fsDict.remove(PdfName.AFRelationship);
        doc.addFileAttachment("file.txt", fs);

        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assertions.assertEquals(PdfaExceptionMessageConstant.FILE_SPECIFICATION_DICTIONARY_SHALL_CONTAIN_AFRELATIONSHIP_KEY, e.getMessage());
    }

    @Test
    public void pdfA4eWithEmbeddedFilesWithoutFTest() throws IOException {
        String outPdf = DESTINATION_FOLDER + "pdfA4eWithEmbeddedFilesWithoutFTest.pdf";

        PdfWriter writer = new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4E, createOutputIntent());
        doc.addNewPage();

        PdfFileSpec fs = PdfFileSpec.createEmbeddedFileSpec(
                doc, "file".getBytes(), "description", "file.txt", null, null, null);
        PdfDictionary fsDict = (PdfDictionary) fs.getPdfObject();
        fsDict.remove(PdfName.F);
        doc.addFileAttachment("file.txt", fs);

        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assertions.assertEquals(PdfAConformanceException.FILE_SPECIFICATION_DICTIONARY_SHALL_CONTAIN_F_KEY_AND_UF_KEY, e.getMessage());
    }

    @Test
    public void pdfA4WithEmbeddedFilesWithoutAFRTest() throws IOException {
        String outPdf = DESTINATION_FOLDER + "pdfA4WithEmbeddedFilesWithoutAFRTest.pdf";

        PdfWriter writer = new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4, createOutputIntent());
        doc.addNewPage();

        PdfFileSpec fs = PdfFileSpec.createEmbeddedFileSpec(
                doc, "file".getBytes(), "description", "file.txt", null, null, null);
        PdfDictionary fsDict = (PdfDictionary) fs.getPdfObject();
        fsDict.remove(PdfName.AFRelationship);
        doc.addFileAttachment("file.txt", fs);

        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assertions.assertEquals(PdfaExceptionMessageConstant.FILE_SPECIFICATION_DICTIONARY_SHALL_CONTAIN_AFRELATIONSHIP_KEY, e.getMessage());
    }

    private PdfOutputIntent createOutputIntent() throws IOException {
        return new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1",
                FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm"));
    }
}
