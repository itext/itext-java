/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
    Authors: iText Software.

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

import com.itextpdf.kernel.KernelLogMessageConstant;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class PdfDocumentAppendModeTest extends ExtendedITextTest {

    public static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/kernel/pdf/PdfDocumentAppendModeTest/";
    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/kernel/pdf/PdfDocumentAppendModeTest/";

    @BeforeClass
    public static void setUp() {
        createDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = KernelLogMessageConstant.FULL_COMPRESSION_APPEND_MODE_XREF_TABLE_INCONSISTENCY)})
    public void testAppendModeWithFullCompressionRequestedWhenOriginalDocumentHasXrefTable()
            throws IOException, InterruptedException {
        String inFile = SOURCE_FOLDER + "documentWithXrefTable.pdf";
        String outFile = DESTINATION_FOLDER + "documentWithXrefTableAfterAppending.pdf";
        String cmpFile = SOURCE_FOLDER + "cmp_documentWithXrefTableAfterAppending.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inFile),
                new PdfWriter(outFile, new WriterProperties().setFullCompressionMode(true)),
                new StampingProperties().useAppendMode());
        pdfDocument.addNewPage();
        pdfDocument.close();
        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFile, DESTINATION_FOLDER));
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = KernelLogMessageConstant.FULL_COMPRESSION_APPEND_MODE_XREF_STREAM_INCONSISTENCY)})
    public void testAppendModeWithFullCompressionSetToFalseWhenOriginalDocumentHasXrefStream()
            throws IOException, InterruptedException {
        String inFile = SOURCE_FOLDER + "documentWithXrefStream.pdf";
        String outFile = DESTINATION_FOLDER + "documentWithXrefStreamAfterAppending.pdf";
        String cmpFile = SOURCE_FOLDER + "cmp_documentWithXrefStreamAfterAppending.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inFile),
                new PdfWriter(outFile, new WriterProperties().setFullCompressionMode(false)),
                new StampingProperties().useAppendMode());
        pdfDocument.addNewPage();
        pdfDocument.close();
        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFile, DESTINATION_FOLDER));
    }

}
