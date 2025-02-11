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
package com.itextpdf.kernel.pdf;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class PdfObjectUnitTest extends ExtendedITextTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/kernel/pdf/PdfObjectUnitTest/";

    @Test
    public void noWriterForMakingIndirectTest() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + "noWriterForMakingIndirect.pdf"));
        PdfDictionary pdfDictionary = new PdfDictionary();
        Exception exception = Assertions.assertThrows(PdfException.class,
                () -> pdfDictionary.makeIndirect(pdfDocument));
        Assertions.assertEquals(KernelExceptionMessageConstant.THERE_IS_NO_ASSOCIATE_PDF_WRITER_FOR_MAKING_INDIRECTS,
                exception.getMessage());
    }

    @Test
    public void copyDocInReadingModeTest() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + "copyDocInReadingMode.pdf"));
        PdfDictionary pdfDictionary = new PdfDictionary();
        Exception exception = Assertions.assertThrows(PdfException.class,
                () -> pdfDictionary.processCopying(pdfDocument, true));
        Assertions.assertEquals(KernelExceptionMessageConstant.CANNOT_COPY_TO_DOCUMENT_OPENED_IN_READING_MODE,
                exception.getMessage());
    }

    @Test
    public void copyIndirectObjectTest() {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfObject pdfObject = pdfDocument.getPdfObject(1);
        Exception exception = Assertions.assertThrows(PdfException.class,
                () -> pdfObject.copyTo(pdfDocument, true));
        Assertions.assertEquals(
                KernelExceptionMessageConstant.CANNOT_COPY_INDIRECT_OBJECT_FROM_THE_DOCUMENT_THAT_IS_BEING_WRITTEN,
                exception.getMessage());
    }

    @Test
    public void copyFlushedObjectTest() {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfObject pdfObject = pdfDocument.getPdfObject(1);
        pdfObject.flush();
        Exception exception = Assertions.assertThrows(PdfException.class,
                () -> pdfObject.copyContent(pdfObject, pdfDocument));
        Assertions.assertEquals(KernelExceptionMessageConstant.CANNOT_COPY_FLUSHED_OBJECT, exception.getMessage());
    }
}
