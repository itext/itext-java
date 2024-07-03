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
package com.itextpdf.kernel.pdf;

import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.MemoryLimitsAwareException;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class PdfReaderDecodeTest extends ExtendedITextTest {

    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/kernel/pdf/PdfReaderDecodeTest/";

    @Test
    public void noMemoryHandlerTest() throws IOException {
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
                InputStream is = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "stream")) {
            byte[] b = new byte[51];
            is.read(b);

            PdfArray array = new PdfArray();

            PdfStream stream = new PdfStream(b);
            stream.put(PdfName.Filter, array);
            stream.makeIndirect(pdfDocument);

            Assertions.assertEquals(51, PdfReader.decodeBytes(b, stream).length);

            array.add(PdfName.Fl);
            Assertions.assertEquals(40, PdfReader.decodeBytes(b, stream).length);

            array.add(PdfName.Fl);
            Assertions.assertEquals(992, PdfReader.decodeBytes(b, stream).length);

            array.add(PdfName.Fl);
            Assertions.assertEquals(1000000, PdfReader.decodeBytes(b, stream).length);

            // needed to close the document
            pdfDocument.addNewPage();
        }
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.INVALID_INDIRECT_REFERENCE),
            @LogMessage(messageTemplate = IoLogMessageConstant.XREF_ERROR_WHILE_READING_TABLE_WILL_BE_REBUILT_WITH_CAUSE)
    })
    public void defaultMemoryHandlerTest() throws IOException {
        try (PdfDocument pdfDocument = new PdfDocument(
                new PdfReader(SOURCE_FOLDER + "timing.pdf"),
                new PdfWriter(new ByteArrayOutputStream()))) {
            PdfStream stream = pdfDocument.getFirstPage().getContentStream(0);
            byte[] b = stream.getBytes(false);

            PdfArray array = new PdfArray();
            stream.put(PdfName.Filter, array);

            Assertions.assertEquals(51, PdfReader.decodeBytes(b, stream).length);

            array.add(PdfName.Fl);
            Assertions.assertEquals(40, PdfReader.decodeBytes(b, stream).length);

            array.add(PdfName.Fl);
            Assertions.assertEquals(992, PdfReader.decodeBytes(b, stream).length);

            array.add(PdfName.Fl);
            Assertions.assertEquals(1000000, PdfReader.decodeBytes(b, stream).length);
        }
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.INVALID_INDIRECT_REFERENCE),
            @LogMessage(messageTemplate = IoLogMessageConstant.XREF_ERROR_WHILE_READING_TABLE_WILL_BE_REBUILT_WITH_CAUSE)
    })
    public void customMemoryHandlerSingleTest() throws IOException {
        MemoryLimitsAwareHandler handler = new MemoryLimitsAwareHandler();
        handler.setMaxSizeOfSingleDecompressedPdfStream(1000);

        try (PdfDocument pdfDocument = new PdfDocument(
                new PdfReader(SOURCE_FOLDER + "timing.pdf",
                        new ReaderProperties().setMemoryLimitsAwareHandler(handler)),
                new PdfWriter(new ByteArrayOutputStream()))) {

            PdfStream stream = pdfDocument.getFirstPage().getContentStream(0);
            byte[] b = stream.getBytes(false);

            PdfArray array = new PdfArray();
            stream.put(PdfName.Filter, array);

            Assertions.assertEquals(51, PdfReader.decodeBytes(b, stream).length);

            array.add(PdfName.Fl);
            Assertions.assertEquals(40, PdfReader.decodeBytes(b, stream).length);

            array.add(PdfName.Fl);
            Assertions.assertEquals(992, PdfReader.decodeBytes(b, stream).length);

            array.add(PdfName.Fl);

            Exception e = Assertions.assertThrows(MemoryLimitsAwareException.class,
                    () -> PdfReader.decodeBytes(b, stream)
            );
            Assertions.assertEquals(KernelExceptionMessageConstant.DURING_DECOMPRESSION_SINGLE_STREAM_OCCUPIED_MORE_MEMORY_THAN_ALLOWED, e.getMessage());
        }
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.INVALID_INDIRECT_REFERENCE),
            @LogMessage(messageTemplate = IoLogMessageConstant.XREF_ERROR_WHILE_READING_TABLE_WILL_BE_REBUILT_WITH_CAUSE)
    })
    public void oneFilterCustomMemoryHandlerSingleTest() throws IOException {
        MemoryLimitsAwareHandler handler = new MemoryLimitsAwareHandler();
        handler.setMaxSizeOfSingleDecompressedPdfStream(20);

        try (PdfDocument pdfDocument = new PdfDocument(
                new PdfReader(SOURCE_FOLDER + "timing.pdf",
                        new ReaderProperties().setMemoryLimitsAwareHandler(handler)),
                new PdfWriter(new ByteArrayOutputStream()))) {

            PdfStream stream = pdfDocument.getFirstPage().getContentStream(0);
            byte[] b = stream.getBytes(false);

            PdfArray array = new PdfArray();
            stream.put(PdfName.Filter, array);

            // Limit is reached, but the stream has no filters. Therefore, we don't consider it to be suspicious.
            Assertions.assertEquals(51, PdfReader.decodeBytes(b, stream).length);

            // Limit is reached, but the stream has only one filter. Therefore, we don't consider it to be suspicious.
            array.add(PdfName.Fl);
            Assertions.assertEquals(40, PdfReader.decodeBytes(b, stream).length);
        }
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.INVALID_INDIRECT_REFERENCE),
            @LogMessage(messageTemplate = IoLogMessageConstant.XREF_ERROR_WHILE_READING_TABLE_WILL_BE_REBUILT_WITH_CAUSE)
    })
    public void overriddenMemoryHandlerAllStreamsAreSuspiciousTest() throws IOException {
        MemoryLimitsAwareHandler handler = new MemoryLimitsAwareHandler() {
            @Override
            public boolean isMemoryLimitsAwarenessRequiredOnDecompression(PdfArray filters) {
                return true;
            }
        };
        handler.setMaxSizeOfSingleDecompressedPdfStream(20);

        try (PdfDocument pdfDocument = new PdfDocument(
                new PdfReader(SOURCE_FOLDER + "timing.pdf",
                        new ReaderProperties().setMemoryLimitsAwareHandler(handler)),
                new PdfWriter(new ByteArrayOutputStream()))) {

            PdfStream stream = pdfDocument.getFirstPage().getContentStream(0);
            byte[] b = stream.getBytes(false);

            PdfArray array = new PdfArray();
            stream.put(PdfName.Filter, array);
            array.add(PdfName.Fl);

            // Limit is reached, and the stream with one filter is considered to be suspicious.
            Exception e = Assertions.assertThrows(MemoryLimitsAwareException.class,
                    () -> PdfReader.decodeBytes(b, stream)
            );
            Assertions.assertEquals(KernelExceptionMessageConstant.DURING_DECOMPRESSION_SINGLE_STREAM_OCCUPIED_MORE_MEMORY_THAN_ALLOWED, e.getMessage());
        }
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.INVALID_INDIRECT_REFERENCE),
            @LogMessage(messageTemplate = IoLogMessageConstant.XREF_ERROR_WHILE_READING_TABLE_WILL_BE_REBUILT_WITH_CAUSE)
    })
    public void overriddenMemoryHandlerNoStreamsAreSuspiciousTest() throws IOException {

        MemoryLimitsAwareHandler handler = new MemoryLimitsAwareHandler() {
            @Override
            public boolean isMemoryLimitsAwarenessRequiredOnDecompression(PdfArray filters) {
                return false;
            }
        };
        handler.setMaxSizeOfSingleDecompressedPdfStream(20);

        try (PdfDocument pdfDocument = new PdfDocument(
                new PdfReader(SOURCE_FOLDER + "timing.pdf",
                        new ReaderProperties().setMemoryLimitsAwareHandler(handler)),
                new PdfWriter(new ByteArrayOutputStream()))) {

            PdfStream stream = pdfDocument.getFirstPage().getContentStream(0);
            byte[] b = stream.getBytes(false);

            PdfArray array = new PdfArray();
            stream.put(PdfName.Filter, array);
            array.add(PdfName.Fl);
            array.add(PdfName.Fl);

            // Limit is reached but the stream with several copies of the filter is not considered to be suspicious.
            PdfReader.decodeBytes(b, stream);
        }
    }

    @Test
    public void differentFiltersEmptyTest() {
        byte[] b = new byte[1000];

        PdfArray array = new PdfArray();
        array.add(PdfName.Fl);
        array.add(PdfName.AHx);
        array.add(PdfName.A85);
        array.add(PdfName.RunLengthDecode);

        PdfStream stream = new PdfStream(b);
        stream.put(PdfName.Filter, array);

        Assertions.assertEquals(0, PdfReader.decodeBytes(b, stream).length);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.INVALID_INDIRECT_REFERENCE),
            @LogMessage(messageTemplate = IoLogMessageConstant.XREF_ERROR_WHILE_READING_TABLE_WILL_BE_REBUILT_WITH_CAUSE)
    })
    public void customMemoryHandlerSumTest() throws IOException {
        MemoryLimitsAwareHandler handler = new MemoryLimitsAwareHandler();
        handler.setMaxSizeOfDecompressedPdfStreamsSum(100000);

        try (PdfDocument pdfDocument = new PdfDocument(
                new PdfReader(SOURCE_FOLDER + "timing.pdf",
                        new ReaderProperties().setMemoryLimitsAwareHandler(handler)),
                new PdfWriter(new ByteArrayOutputStream()))) {

            PdfStream stream = pdfDocument.getFirstPage().getContentStream(0);
            byte[] b = stream.getBytes(false);

            Exception e = Assertions.assertThrows(MemoryLimitsAwareException.class,
                    () -> PdfReader.decodeBytes(b, stream)
            );
            Assertions.assertEquals(KernelExceptionMessageConstant.DURING_DECOMPRESSION_MULTIPLE_STREAMS_IN_SUM_OCCUPIED_MORE_MEMORY_THAN_ALLOWED, e.getMessage());
        }
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.INVALID_INDIRECT_REFERENCE),
            @LogMessage(messageTemplate = IoLogMessageConstant.XREF_ERROR_WHILE_READING_TABLE_WILL_BE_REBUILT_WITH_CAUSE)
    })
    public void pageSumTest() throws IOException {
        MemoryLimitsAwareHandler handler = new MemoryLimitsAwareHandler();
        handler.setMaxSizeOfDecompressedPdfStreamsSum(1500000);

        try (PdfDocument pdfDocument = new PdfDocument(
                new PdfReader(SOURCE_FOLDER + "timing.pdf",
                        new ReaderProperties().setMemoryLimitsAwareHandler(handler)),
                new PdfWriter(new ByteArrayOutputStream()))) {

            Exception e = Assertions.assertThrows(MemoryLimitsAwareException.class,
                    () -> pdfDocument.getFirstPage().getContentBytes()
            );
            Assertions.assertEquals(KernelExceptionMessageConstant.DURING_DECOMPRESSION_MULTIPLE_STREAMS_IN_SUM_OCCUPIED_MORE_MEMORY_THAN_ALLOWED, e.getMessage());
        }
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.INVALID_INDIRECT_REFERENCE),
            @LogMessage(messageTemplate = IoLogMessageConstant.XREF_ERROR_WHILE_READING_TABLE_WILL_BE_REBUILT_WITH_CAUSE)
    })
    public void pageAsSingleStreamTest() throws IOException {
        MemoryLimitsAwareHandler handler = new MemoryLimitsAwareHandler();
        handler.setMaxSizeOfSingleDecompressedPdfStream(1500000);

        try (PdfDocument pdfDocument = new PdfDocument(
                new PdfReader(SOURCE_FOLDER + "timing.pdf",
                        new ReaderProperties().setMemoryLimitsAwareHandler(handler)),
                new PdfWriter(new ByteArrayOutputStream()))) {

            Exception e = Assertions.assertThrows(MemoryLimitsAwareException.class,
                    () -> pdfDocument.getFirstPage().getContentBytes()
            );
            Assertions.assertEquals(KernelExceptionMessageConstant.DURING_DECOMPRESSION_SINGLE_STREAM_OCCUPIED_MORE_MEMORY_THAN_ALLOWED, e.getMessage());
        }
    }
}
