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

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.logs.KernelLogMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.LogLevelConstants;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class PdfStreamDecodeTest extends ExtendedITextTest {

    private static final byte[] BYTES = new byte[] {
            (byte) 0x78, (byte) 0xda, (byte) 0x01, (byte) 0x28, (byte) 0x00, (byte) 0xd7,
            (byte) 0xff, (byte) 0x78, (byte) 0xda, (byte) 0xab, (byte) 0xb8, (byte) 0xf5,
            (byte) 0xf6, (byte) 0x60, (byte) 0x23, (byte) 0x03, (byte) 0x10, (byte) 0x1c,
            (byte) 0x56, (byte) 0x58, (byte) 0xf1, (byte) 0x73, (byte) 0xb7, (byte) 0xec,
            (byte) 0x93, (byte) 0x50, (byte) 0x46, (byte) 0x86, (byte) 0x51, (byte) 0x30,
            (byte) 0x0a, (byte) 0x46, (byte) 0xc1, (byte) 0x90, (byte) 0x07, (byte) 0xeb,
            (byte) 0xd9, (byte) 0x96, (byte) 0x87, (byte) 0x26, (byte) 0x84, (byte) 0x03,
            (byte) 0x00, (byte) 0x27, (byte) 0xef, (byte) 0x0a, (byte) 0x80, (byte) 0x91,
            (byte) 0x9d, (byte) 0x12, (byte) 0x0e
    };

    private static final byte[] FLATE_DECODED_BYTES = new byte[] {
            (byte) 0x78, (byte) 0x9c, (byte) 0x01, (byte) 0x33, (byte) 0x00, (byte) 0xcc,
            (byte) 0xff, (byte) 0x78, (byte) 0xda, (byte) 0x01, (byte) 0x28, (byte) 0x00,
            (byte) 0xd7, (byte) 0xff, (byte) 0x78, (byte) 0xda, (byte) 0xab, (byte) 0xb8,
            (byte) 0xf5, (byte) 0xf6, (byte) 0x60, (byte) 0x23, (byte) 0x03, (byte) 0x10,
            (byte) 0x1c, (byte) 0x56, (byte) 0x58, (byte) 0xf1, (byte) 0x73, (byte) 0xb7,
            (byte) 0xec, (byte) 0x93, (byte) 0x50, (byte) 0x46, (byte) 0x86, (byte) 0x51,
            (byte) 0x30, (byte) 0x0a, (byte) 0x46, (byte) 0xc1, (byte) 0x90, (byte) 0x07,
            (byte) 0xeb, (byte) 0xd9, (byte) 0x96, (byte) 0x87, (byte) 0x26, (byte) 0x84,
            (byte) 0x03, (byte) 0x00, (byte) 0x27, (byte) 0xef, (byte) 0x0a, (byte) 0x80,
            (byte) 0x91, (byte) 0x9d, (byte) 0x12, (byte) 0x0e, (byte) 0x7b, (byte) 0xda,
            (byte) 0x16, (byte) 0xad
    };

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = KernelLogMessageConstant.DCTDECODE_FILTER_DECODING, logLevel = LogLevelConstants.INFO)
    })
    public void testDCTDecodeFilter() {
        PdfStream pdfStream = new PdfStream(FLATE_DECODED_BYTES);
        pdfStream.put(PdfName.Filter, new PdfArray(Arrays.asList((PdfObject) PdfName.FlateDecode, (PdfObject) PdfName.DCTDecode)));

        Assertions.assertArrayEquals(BYTES, pdfStream.getBytes());
    }

    @Test
    public void testJBIG2DecodeFilter() {
        PdfStream pdfStream = new PdfStream(FLATE_DECODED_BYTES);
        pdfStream.put(PdfName.Filter, new PdfArray(Arrays.asList((PdfObject) PdfName.FlateDecode, (PdfObject) PdfName.JBIG2Decode)));

        Exception e = Assertions.assertThrows(PdfException.class,
                () -> pdfStream.getBytes(true)
        );
        Assertions.assertEquals(MessageFormatUtil.format(KernelExceptionMessageConstant.THIS_FILTER_IS_NOT_SUPPORTED, PdfName.JBIG2Decode), e.getMessage());
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = KernelLogMessageConstant.JPXDECODE_FILTER_DECODING, logLevel = LogLevelConstants.INFO)
    })
    public void testJPXDecodeFilter() {
        PdfStream pdfStream = new PdfStream(FLATE_DECODED_BYTES);
        pdfStream.put(PdfName.Filter, new PdfArray(Arrays.asList((PdfObject) PdfName.FlateDecode, (PdfObject) PdfName.JPXDecode)));

        Assertions.assertArrayEquals(BYTES, pdfStream.getBytes());
    }
}
