/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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


import com.itextpdf.kernel.PdfException;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class MemoryLimitsAwareHandlerTest extends ExtendedITextTest {

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
    public void overridenMemoryHandler() {
        MemoryLimitsAwareHandler defaultHandler = new MemoryLimitsAwareHandler();
        MemoryLimitsAwareHandler customHandler = new MemoryLimitsAwareHandler() {
            @Override
            public boolean isMemoryLimitsAwarenessRequiredOnDecompression(PdfArray filters) {
                return true;
            }
        };

        PdfArray filters = new PdfArray();
        filters.add(PdfName.FlateDecode);

        Assert.assertFalse(defaultHandler.isMemoryLimitsAwarenessRequiredOnDecompression(filters));
        Assert.assertTrue(customHandler.isMemoryLimitsAwarenessRequiredOnDecompression(filters));

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
