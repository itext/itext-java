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
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfOutputIntent;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.filespec.PdfFileSpec;
import com.itextpdf.pdfa.exceptions.PdfAConformanceException;
import com.itextpdf.pdfa.exceptions.PdfaExceptionMessageConstant;
import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Tag("IntegrationTest")
public class PdfA1EmbeddedFilesCheckTest extends ExtendedITextTest {
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/pdfa/";

    @Test
    public void fileSpecCheckTest01() throws IOException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument pdfDocument = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, outputIntent);

        PdfDictionary fileNames = new PdfDictionary();
        pdfDocument.getCatalog().put(PdfName.Names, fileNames);

        PdfDictionary embeddedFiles = new PdfDictionary();
        fileNames.put(PdfName.EmbeddedFiles, embeddedFiles);

        PdfArray names = new PdfArray();
        fileNames.put(PdfName.Names, names);

        names.add(new PdfString("some/file/path"));
        PdfFileSpec spec = PdfFileSpec.createEmbeddedFileSpec(pdfDocument, sourceFolder + "sample.wav", "sample.wav", "sample", null, null);
        names.add(spec.getPdfObject());

        pdfDocument.addNewPage();

        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> pdfDocument.close());
        Assertions.assertEquals(PdfaExceptionMessageConstant.A_NAME_DICTIONARY_SHALL_NOT_CONTAIN_THE_EMBEDDED_FILES_KEY, e.getMessage());
    }

    @Test
    public void fileSpecCheckTest02() throws IOException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument pdfDocument = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, outputIntent);

        PdfStream stream = new PdfStream();
        pdfDocument.getCatalog().put(new PdfName("testStream"), stream);

        PdfFileSpec spec = PdfFileSpec.createEmbeddedFileSpec(pdfDocument, sourceFolder + "sample.wav", "sample.wav", "sample", null, null);
        stream.put(PdfName.F, spec.getPdfObject());

        pdfDocument.addNewPage();

        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> pdfDocument.close());
        Assertions.assertEquals(PdfaExceptionMessageConstant.STREAM_OBJECT_DICTIONARY_SHALL_NOT_CONTAIN_THE_F_FFILTER_OR_FDECODEPARAMS_KEYS,
                e.getMessage());
    }

    @Test
    public void fileSpecCheckTest03() throws IOException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument pdfDocument = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, outputIntent);

        PdfStream stream = new PdfStream();
        pdfDocument.getCatalog().put(new PdfName("testStream"), stream);

        PdfFileSpec spec = PdfFileSpec.createEmbeddedFileSpec(pdfDocument, sourceFolder + "sample.wav", "sample.wav", "sample", null, null);
        stream.put(new PdfName("fileData"), spec.getPdfObject());

        pdfDocument.addNewPage();

        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> pdfDocument.close());
        Assertions.assertEquals(PdfaExceptionMessageConstant.FILE_SPECIFICATION_DICTIONARY_SHALL_NOT_CONTAIN_THE_EF_KEY, e.getMessage());
    }
}
