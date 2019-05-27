/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.kernel.pdf;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Category(IntegrationTest.class)
public class PdfReaderDecodeTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/PdfReaderDecodeTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/PdfReaderDecodeTest/";


    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void noMemoryHandlerTest() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));

        FileInputStream is = new FileInputStream(sourceFolder + "stream");
        byte[] b = new byte[51];
        is.read(b);

        PdfArray array = new PdfArray();

        PdfStream stream = new PdfStream(b);
        stream.put(PdfName.Filter, array);
        stream.makeIndirect(pdfDocument);

        Assert.assertEquals(51, PdfReader.decodeBytes(b, stream).length);

        array.add(PdfName.Fl);
        Assert.assertEquals(40, PdfReader.decodeBytes(b, stream).length);

        array.add(PdfName.Fl);
        Assert.assertEquals(992, PdfReader.decodeBytes(b, stream).length);

        array.add(PdfName.Fl);
        Assert.assertEquals(1000000, PdfReader.decodeBytes(b, stream).length);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.INVALID_INDIRECT_REFERENCE),
            @LogMessage(messageTemplate = LogMessageConstant.XREF_ERROR)
    })
    public void defaultMemoryHandlerTest() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + "timing.pdf"), new PdfWriter(new ByteArrayOutputStream()));

        PdfStream stream = pdfDocument.getFirstPage().getContentStream(0);
        byte[] b = stream.getBytes(false);

        PdfArray array = new PdfArray();
        stream.put(PdfName.Filter, array);

        Assert.assertEquals(51, PdfReader.decodeBytes(b, stream).length);

        array.add(PdfName.Fl);
        Assert.assertEquals(40, PdfReader.decodeBytes(b, stream).length);

        array.add(PdfName.Fl);
        Assert.assertEquals(992, PdfReader.decodeBytes(b, stream).length);

        array.add(PdfName.Fl);
        Assert.assertEquals(1000000, PdfReader.decodeBytes(b, stream).length);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.INVALID_INDIRECT_REFERENCE),
            @LogMessage(messageTemplate = LogMessageConstant.XREF_ERROR)
    })
    public void customMemoryHandlerSingleTest() throws IOException {
        MemoryLimitsAwareHandler handler = new MemoryLimitsAwareHandler();
        handler.setMaxSizeOfSingleDecompressedPdfStream(1000);

        PdfDocument pdfDocument = new PdfDocument(
                new PdfReader(sourceFolder + "timing.pdf",
                        new ReaderProperties().setMemoryLimitsAwareHandler(handler)),
                new PdfWriter(new ByteArrayOutputStream()));

        PdfStream stream = pdfDocument.getFirstPage().getContentStream(0);
        byte[] b = stream.getBytes(false);

        PdfArray array = new PdfArray();
        stream.put(PdfName.Filter, array);

        Assert.assertEquals(51, PdfReader.decodeBytes(b, stream).length);

        array.add(PdfName.Fl);
        Assert.assertEquals(40, PdfReader.decodeBytes(b, stream).length);

        array.add(PdfName.Fl);
        Assert.assertEquals(992, PdfReader.decodeBytes(b, stream).length);

        array.add(PdfName.Fl);
        String expectedExceptionMessage = PdfException.DuringDecompressionSingleStreamOccupiedMoreMemoryThanAllowed;
        String thrownExceptionMessage = null;
        try {
            PdfReader.decodeBytes(b, stream);
        } catch (MemoryLimitsAwareException e) {
            thrownExceptionMessage = e.getMessage();
        }
        Assert.assertEquals(expectedExceptionMessage, thrownExceptionMessage);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.INVALID_INDIRECT_REFERENCE),
            @LogMessage(messageTemplate = LogMessageConstant.XREF_ERROR)
    })
    public void oneFilterCustomMemoryHandlerSingleTest() throws IOException {
        MemoryLimitsAwareHandler handler = new MemoryLimitsAwareHandler();
        handler.setMaxSizeOfSingleDecompressedPdfStream(20);

        PdfDocument pdfDocument = new PdfDocument(
                new PdfReader(sourceFolder + "timing.pdf",
                        new ReaderProperties().setMemoryLimitsAwareHandler(handler)),
                new PdfWriter(new ByteArrayOutputStream()));

        PdfStream stream = pdfDocument.getFirstPage().getContentStream(0);
        byte[] b = stream.getBytes(false);

        PdfArray array = new PdfArray();
        stream.put(PdfName.Filter, array);

        // Limit is reached, but the stream has no filters. Therefore we don't consider ot to be suspicious
        Assert.assertEquals(51, PdfReader.decodeBytes(b, stream).length);

        // Limit is reached, but the stream has only one filter. Therefore we don't consider ot to be suspicious
        array.add(PdfName.Fl);
        Assert.assertEquals(40, PdfReader.decodeBytes(b, stream).length);
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

        Assert.assertEquals(0, PdfReader.decodeBytes(b, stream).length);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.INVALID_INDIRECT_REFERENCE),
            @LogMessage(messageTemplate = LogMessageConstant.XREF_ERROR)
    })
    public void customMemoryHandlerSumTest() throws IOException {
        MemoryLimitsAwareHandler handler = new MemoryLimitsAwareHandler();
        handler.setMaxSizeOfDecompressedPdfStreamsSum(100000);

        PdfDocument pdfDocument = new PdfDocument(
                new PdfReader(sourceFolder + "timing.pdf",
                        new ReaderProperties().setMemoryLimitsAwareHandler(handler)),
                new PdfWriter(new ByteArrayOutputStream()));

        PdfStream stream = pdfDocument.getFirstPage().getContentStream(0);
        byte[] b = stream.getBytes(false);

        String expectedExceptionMessage = PdfException.DuringDecompressionMultipleStreamsInSumOccupiedMoreMemoryThanAllowed;
        String thrownExceptionMessage = null;
        try {
            PdfReader.decodeBytes(b, stream);
        } catch (MemoryLimitsAwareException e) {
            thrownExceptionMessage = e.getMessage();
        }
        Assert.assertEquals(expectedExceptionMessage, thrownExceptionMessage);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.INVALID_INDIRECT_REFERENCE),
            @LogMessage(messageTemplate = LogMessageConstant.XREF_ERROR)
    })
    public void pageSumTest() throws IOException {
        MemoryLimitsAwareHandler handler = new MemoryLimitsAwareHandler();
        handler.setMaxSizeOfDecompressedPdfStreamsSum(1500000);

        PdfDocument pdfDocument = new PdfDocument(
                new PdfReader(sourceFolder + "timing.pdf",
                        new ReaderProperties().setMemoryLimitsAwareHandler(handler)),
                new PdfWriter(new ByteArrayOutputStream()));


        String expectedExceptionMessage = PdfException.DuringDecompressionMultipleStreamsInSumOccupiedMoreMemoryThanAllowed;
        String thrownExceptionMessage = null;
        try {
            pdfDocument.getFirstPage().getContentBytes();
        } catch (MemoryLimitsAwareException e) {
            thrownExceptionMessage = e.getMessage();
        }
        Assert.assertEquals(expectedExceptionMessage, thrownExceptionMessage);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.INVALID_INDIRECT_REFERENCE),
            @LogMessage(messageTemplate = LogMessageConstant.XREF_ERROR)
    })
    public void pageAsSingleStreamTest() throws IOException {
        MemoryLimitsAwareHandler handler = new MemoryLimitsAwareHandler();
        handler.setMaxSizeOfSingleDecompressedPdfStream(1500000);

        PdfDocument pdfDocument = new PdfDocument(
                new PdfReader(sourceFolder + "timing.pdf",
                        new ReaderProperties().setMemoryLimitsAwareHandler(handler)),
                new PdfWriter(new ByteArrayOutputStream()));


        String expectedExceptionMessage = PdfException.DuringDecompressionSingleStreamOccupiedMoreMemoryThanAllowed;
        String thrownExceptionMessage = null;
        try {
            pdfDocument.getFirstPage().getContentBytes();
        } catch (MemoryLimitsAwareException e) {
            thrownExceptionMessage = e.getMessage();
        }
        Assert.assertEquals(expectedExceptionMessage, thrownExceptionMessage);
    }
}
