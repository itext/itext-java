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

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.crypto.pdfencryption.PdfEncryptionTestUtils;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.pdf.filespec.PdfFileSpec;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.TestUtil;

import java.io.OutputStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Tag("IntegrationTest")
public class PdfOutputStreamTest extends ExtendedITextTest {

    public static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/kernel/pdf/PdfOutputStreamTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void invalidDecodeParamsTest() {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream(),
                new WriterProperties().setStandardEncryption(PdfEncryptionTestUtils.USER, PdfEncryptionTestUtils.OWNER,
                        0,
                        EncryptionConstants.ENCRYPTION_AES_256 | EncryptionConstants.EMBEDDED_FILES_ONLY));
        PdfDocument document = new CustomPdfDocument1(writer);

        document.addFileAttachment("descripton",
                PdfFileSpec.createEmbeddedFileSpec(document, "TEST".getBytes(StandardCharsets.UTF_8), "descripton",
                        "test.txt", null, null));

        Exception e = Assertions.assertThrows(PdfException.class, () -> document.close());
        Assertions.assertEquals(MessageFormatUtil.format(
                        KernelExceptionMessageConstant.THIS_DECODE_PARAMETER_TYPE_IS_NOT_SUPPORTED,
                        PdfName.class),
                e.getMessage());
    }

    @Test
    public void arrayDecodeParamsTest() throws IOException {
        final String fileName = "arrayDecodeParamsTest.pdf";
        PdfWriter writer = CompareTool.createTestPdfWriter(DESTINATION_FOLDER + fileName,
                new WriterProperties().setStandardEncryption(PdfEncryptionTestUtils.USER, PdfEncryptionTestUtils.OWNER,
                        0,
                        EncryptionConstants.ENCRYPTION_AES_256 | EncryptionConstants.EMBEDDED_FILES_ONLY));
        PdfDocument document = new CustomPdfDocument2(writer);

        document.addFileAttachment("descripton",
                PdfFileSpec.createEmbeddedFileSpec(document, "TEST".getBytes(StandardCharsets.UTF_8), "descripton",
                        "test.txt", null, null));

        AssertUtil.doesNotThrow(() -> document.close());
    }

    @Test
    public void dictDecodeParamsTest() throws IOException {
        final String fileName = "dictDecodeParamsTest.pdf";
        PdfWriter writer = CompareTool.createTestPdfWriter(DESTINATION_FOLDER + fileName,
                new WriterProperties().setStandardEncryption(PdfEncryptionTestUtils.USER, PdfEncryptionTestUtils.OWNER,
                        0,
                        EncryptionConstants.ENCRYPTION_AES_256 | EncryptionConstants.EMBEDDED_FILES_ONLY));
        PdfDocument document = new CustomPdfDocument3(writer);

        document.addFileAttachment("descripton",
                PdfFileSpec.createEmbeddedFileSpec(document, "TEST".getBytes(StandardCharsets.UTF_8), "descripton",
                        "test.txt", null, null));

        AssertUtil.doesNotThrow(() -> document.close());
    }

    @Test
    public void singleFilterNoDecodeChangesNothing() {
        CustomPdfStream stream = new CustomPdfStream(new ByteArrayOutputStream(10));
        PdfStream pdfStream = new PdfStream();

        stream.updateCompressionFilter(pdfStream);
        Assertions.assertEquals(PdfName.FlateDecode, pdfStream.getAsName(PdfName.Filter));
        Assertions.assertNull(pdfStream.get(PdfName.DecodeParms));
    }


    @Test
    public void withoutFilterAndWithDecodeParamsRemovesDecodeParamsAndAddsFilter() {
        CustomPdfStream stream = new CustomPdfStream(new ByteArrayOutputStream(10));
        PdfStream pdfStream = new PdfStream();

        PdfDictionary decodeParms = new PdfDictionary();
        decodeParms.put(PdfName.Predictor, new PdfNumber(12));
        pdfStream.put(PdfName.DecodeParms, decodeParms);
        stream.updateCompressionFilter(pdfStream);
        Assertions.assertEquals(PdfName.FlateDecode, pdfStream.getAsName(PdfName.Filter));
        Assertions.assertNull(pdfStream.get(PdfName.DecodeParms));
    }


    @Test
    public void filterAlreadyExistsAddItConvertsItToArray() {
        CustomPdfStream stream = new CustomPdfStream(new ByteArrayOutputStream(10));
        PdfStream pdfStream = new PdfStream();
        pdfStream.put(PdfName.Filter, PdfName.FlateDecode);

        stream.updateCompressionFilter(pdfStream);

        PdfArray filterArray = pdfStream.getAsArray(PdfName.Filter);
        Assertions.assertEquals(2, filterArray.size());
        Assertions.assertEquals(PdfName.FlateDecode, filterArray.getAsName(0));
        Assertions.assertEquals(PdfName.FlateDecode, filterArray.getAsName(1));

        PdfArray decodeParmsArray = pdfStream.getAsArray(PdfName.DecodeParms);
        Assertions.assertNull(decodeParmsArray);

    }

    @Test
    public void filterArrayExistsAddsNewFilterAtTheEnd() {
        CustomPdfStream stream = new CustomPdfStream(new ByteArrayOutputStream(10));
        PdfStream pdfStream = new PdfStream();
        PdfArray filterArray = new PdfArray();
        filterArray.add(PdfName.LZWDecode);
        pdfStream.put(PdfName.Filter, filterArray);

        PdfArray decodeParmsArray = new PdfArray();
        decodeParmsArray.add(new PdfNumber(20));
        pdfStream.put(PdfName.DecodeParms, decodeParmsArray);

        stream.updateCompressionFilter(pdfStream);

        PdfArray updatedFilterArray = pdfStream.getAsArray(PdfName.Filter);
        Assertions.assertEquals(2, updatedFilterArray.size());
        //new filter should be added at the beginning
        Assertions.assertEquals(PdfName.FlateDecode, updatedFilterArray.getAsName(0));
        Assertions.assertEquals(PdfName.LZWDecode, updatedFilterArray.getAsName(1));

        PdfArray updatedDecodeParmsArray = pdfStream.getAsArray(PdfName.DecodeParms);
        Assertions.assertEquals(2, updatedDecodeParmsArray.size());
        //new decode parms should be added at the beginning
        Assertions.assertTrue(updatedDecodeParmsArray.get(0) instanceof PdfNull);
        Assertions.assertEquals(new PdfNumber(20), updatedDecodeParmsArray.getAsNumber(1));
    }



    @Test
    public void filterWith3AlreadyExistingFiltersButNoDecodeBackFillsDecodeParams() {
        CustomPdfStream stream = new CustomPdfStream(new ByteArrayOutputStream(10));
        PdfStream pdfStream = new PdfStream();
        PdfArray filterArray = new PdfArray();

        filterArray.add(PdfName.LZWDecode);
        filterArray.add(PdfName.FlateDecode);
        filterArray.add(PdfName.ASCII85Decode);
        pdfStream.put(PdfName.Filter, filterArray);

        stream.updateCompressionFilter(pdfStream);
        PdfArray updatedFilterArray = pdfStream.getAsArray(PdfName.Filter);
        Assertions.assertEquals(4, updatedFilterArray.size());
        //new filter should be added at the beginning
        Assertions.assertEquals(PdfName.FlateDecode, updatedFilterArray.getAsName(0));
        Assertions.assertEquals(PdfName.LZWDecode, updatedFilterArray.getAsName(1));
        Assertions.assertEquals(PdfName.FlateDecode, updatedFilterArray.getAsName(2));
        Assertions.assertEquals(PdfName.ASCII85Decode, updatedFilterArray.getAsName(3));


        PdfArray updatedDecodeParmsArray = pdfStream.getAsArray(PdfName.DecodeParms);
        Assertions.assertNull(updatedDecodeParmsArray);
    }


    private static final class CustomPdfStream extends PdfOutputStream {

        public CustomPdfStream(OutputStream outputStream) {
            super(outputStream);
        }
    }

    private static final class CustomPdfDocument1 extends PdfDocument {
        CustomPdfDocument1(PdfWriter writer) {
            super(writer);
        }

        @Override
        public void markStreamAsEmbeddedFile(PdfStream stream) {
            stream.put(PdfName.DecodeParms, PdfName.Crypt);
            stream.setCompressionLevel(CompressionConstants.NO_COMPRESSION);

            super.markStreamAsEmbeddedFile(stream);
        }
    }

    private static final class CustomPdfDocument2 extends PdfDocument {
        CustomPdfDocument2(PdfWriter writer) {
            super(writer);
        }

        @Override
        public void markStreamAsEmbeddedFile(PdfStream stream) {
            PdfArray decodeParmsValue = new PdfArray();
            decodeParmsValue.add(new PdfNull());
            stream.put(PdfName.DecodeParms, decodeParmsValue);
            stream.setCompressionLevel(CompressionConstants.NO_COMPRESSION);

            super.markStreamAsEmbeddedFile(stream);
        }
    }

    private static final class CustomPdfDocument3 extends PdfDocument {
        CustomPdfDocument3(PdfWriter writer) {
            super(writer);
        }

        @Override
        public void markStreamAsEmbeddedFile(PdfStream stream) {
            PdfDictionary decodeParmsValue = new PdfDictionary();
            decodeParmsValue.put(PdfName.Name, new PdfNull());
            stream.put(PdfName.DecodeParms, decodeParmsValue);
            stream.setCompressionLevel(CompressionConstants.NO_COMPRESSION);

            super.markStreamAsEmbeddedFile(stream);
        }
    }
}
