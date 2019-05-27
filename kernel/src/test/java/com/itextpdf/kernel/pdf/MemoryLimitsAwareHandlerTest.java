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


import com.itextpdf.kernel.PdfException;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class MemoryLimitsAwareHandlerTest {

    @Test
    public void defaultMemoryHandler() {
        MemoryLimitsAwareHandler handler = new MemoryLimitsAwareHandler();

        Assert.assertEquals(Integer.MAX_VALUE / 100, handler.getMaxSizeOfSingleDecompressedPdfStream());
        Assert.assertEquals(Integer.MAX_VALUE / 20, handler.getMaxSizeOfDecompressedPdfStreamsSum());
    }

    @Test
    public void customMemoryHandler() {
        MemoryLimitsAwareHandler handler = new MemoryLimitsAwareHandler(1000000);

        Assert.assertEquals(100000000, handler.getMaxSizeOfSingleDecompressedPdfStream());
        Assert.assertEquals(500000000, handler.getMaxSizeOfDecompressedPdfStreamsSum());
    }

    @Test
    public void defaultSingleMemoryHandler() {
        MemoryLimitsAwareHandler handler = new MemoryLimitsAwareHandler();

        testSingleStream(handler);
    }

    @Test
    public void defaultMultipleMemoryHandler() {
        MemoryLimitsAwareHandler handler = new MemoryLimitsAwareHandler();

        testMultipleStreams(handler);
    }

    @Test
    public void considerBytesTest() {
        MemoryLimitsAwareHandler handler = new MemoryLimitsAwareHandler();

        long state1 = handler.getAllMemoryUsedForDecompression();

        handler.considerBytesOccupiedByDecompressedPdfStream(100);
        long state2 = handler.getAllMemoryUsedForDecompression();

        Assert.assertEquals(state1, state2);

        handler.beginDecompressedPdfStreamProcessing();
        handler.considerBytesOccupiedByDecompressedPdfStream(100);
        long state3 = handler.getAllMemoryUsedForDecompression();
        Assert.assertEquals(state1, state3);

        handler.considerBytesOccupiedByDecompressedPdfStream(80);
        long state4 = handler.getAllMemoryUsedForDecompression();
        Assert.assertEquals(state1, state4);

        handler.endDecompressedPdfStreamProcessing();
        long state5 = handler.getAllMemoryUsedForDecompression();
        Assert.assertEquals(state1 + 100, state5);
    }

    private static void testSingleStream(MemoryLimitsAwareHandler handler) {
        String expectedExceptionMessage = PdfException.DuringDecompressionSingleStreamOccupiedMoreMemoryThanAllowed;
        int expectedFailureIndex = 10;
        String occuredExceptionMessage = null;

        int limit = handler.getMaxSizeOfSingleDecompressedPdfStream();

        long step = limit / 10;

        int i = 0;
        try {
            handler.beginDecompressedPdfStreamProcessing();
            for (i = 0; i < 11; i++) {
                handler.considerBytesOccupiedByDecompressedPdfStream(step * (1 + i));
            }
            handler.endDecompressedPdfStreamProcessing();
        } catch (MemoryLimitsAwareException e) {
            occuredExceptionMessage = e.getMessage();
        }
        Assert.assertEquals(expectedFailureIndex, i);
        Assert.assertEquals(expectedExceptionMessage, occuredExceptionMessage);
    }

    private static void testMultipleStreams(MemoryLimitsAwareHandler handler) {
        String expectedExceptionMessage = PdfException.DuringDecompressionMultipleStreamsInSumOccupiedMoreMemoryThanAllowed;
        int expectedFailureIndex = 10;
        String occuredExceptionMessage = null;

        int i = 0;
        try {
            long limit = handler.getMaxSizeOfDecompressedPdfStreamsSum();
            long step = limit / 10;

            for (i = 0; i < 11; i++) {
                handler.beginDecompressedPdfStreamProcessing();
                handler.considerBytesOccupiedByDecompressedPdfStream(step);
                handler.endDecompressedPdfStreamProcessing();
            }
        } catch (MemoryLimitsAwareException e) {
            occuredExceptionMessage = e.getMessage();
        }
        Assert.assertEquals(expectedFailureIndex, i);
        Assert.assertEquals(expectedExceptionMessage, occuredExceptionMessage);
    }

}
