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
package com.itextpdf.pdfa;

import com.itextpdf.kernel.pdf.PdfIndirectReference;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.pdfa.logs.PdfAConformanceLogMessageConstant;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.io.IOException;
import java.util.Set;

@Tag("IntegrationTest")
public class PdfAIndirectResourcesTest extends ExtendedITextTest {
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/pdfa/pdfs/";
    public static final String destinationFolder = "./target/test/com/itextpdf/pdfa/PdfAIndirectResourcesTest/";

    @BeforeEach
    public void configure() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = PdfAConformanceLogMessageConstant.CATALOG_SHOULD_CONTAIN_LANG_ENTRY)
    })
    public void indirectResources01Test() throws IOException {
        String fileName = destinationFolder + "indirectResources01Test.pdf";
        PdfADocument pdfDoc = new PdfADocument(new PdfReader(sourceFolder + "indirectResources01.pdf"), new PdfWriter(fileName));
        pdfDoc.close();
    }

    @Test
    public void indirectResources02Test() throws IOException {
        String fileName = destinationFolder + "indirectResources02Test.pdf";

        PdfWriter writer = new CustomPdfWriter(fileName, 19);
        PdfADocument pdfDoc = new PdfADocument(new PdfReader(sourceFolder + "indirectResources02.pdf"), writer);
        pdfDoc.close();
    }

    private static class CustomPdfWriter extends PdfWriter {
        private int objectToFlushNumber;

        public CustomPdfWriter(String filename, int objectToFlushNumber) throws IOException {
            super(filename);
            this.objectToFlushNumber = objectToFlushNumber;
        }

        @Override
        protected void flushWaitingObjects(Set<PdfIndirectReference> forbiddenToFlush) {
            // Because of flushing order in PdfDocument is uncertain, flushWaitingObjects() method is overridden
            // to simulate the issue when the certain PdfObject A, that exists in the Catalog entry and in the resources
            // of another PdfObject B, is flushed before the flushing of the PdfObject B.
            super.document.getPdfObject(objectToFlushNumber).flush();
            super.flushWaitingObjects(forbiddenToFlush);
        }
    }
}
