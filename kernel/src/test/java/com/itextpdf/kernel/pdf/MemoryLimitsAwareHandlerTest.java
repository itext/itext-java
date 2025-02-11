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


import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.MemoryLimitsAwareException;
import com.itextpdf.kernel.logs.KernelLogMessageConstant;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Tag("UnitTest")
public class MemoryLimitsAwareHandlerTest extends ExtendedITextTest {

    @Test
    public void defaultMemoryHandler() {
        MemoryLimitsAwareHandler handler = new MemoryLimitsAwareHandler();

        Assertions.assertEquals(Integer.MAX_VALUE / 100, handler.getMaxSizeOfSingleDecompressedPdfStream());
        Assertions.assertEquals(Integer.MAX_VALUE / 20, handler.getMaxSizeOfDecompressedPdfStreamsSum());
        Assertions.assertEquals(50000000, handler.getMaxNumberOfElementsInXrefStructure());
        Assertions.assertEquals(1024L*1024L*1024L*3L, handler.getMaxXObjectsSizePerPage());
    }

    @Test
    public void customMemoryHandler() {
        MemoryLimitsAwareHandler handler = new MemoryLimitsAwareHandler(1000000);

        Assertions.assertEquals(100000000, handler.getMaxSizeOfSingleDecompressedPdfStream());
        Assertions.assertEquals(500000000, handler.getMaxSizeOfDecompressedPdfStreamsSum());
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

        Assertions.assertFalse(defaultHandler.isMemoryLimitsAwarenessRequiredOnDecompression(filters));
        Assertions.assertTrue(customHandler.isMemoryLimitsAwarenessRequiredOnDecompression(filters));
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

        Assertions.assertEquals(state1, state2);

        handler.beginDecompressedPdfStreamProcessing();
        handler.considerBytesOccupiedByDecompressedPdfStream(100);
        long state3 = handler.getAllMemoryUsedForDecompression();
        Assertions.assertEquals(state1, state3);

        handler.considerBytesOccupiedByDecompressedPdfStream(80);
        long state4 = handler.getAllMemoryUsedForDecompression();
        Assertions.assertEquals(state1, state4);

        handler.endDecompressedPdfStreamProcessing();
        long state5 = handler.getAllMemoryUsedForDecompression();
        Assertions.assertEquals(state1 + 100, state5);
    }

    @Test
    public void customXrefCapacityHandlerTest() {
        final MemoryLimitsAwareHandler memoryLimitsAwareHandler = new MemoryLimitsAwareHandler();

        Assertions.assertEquals(50000000, memoryLimitsAwareHandler.getMaxNumberOfElementsInXrefStructure());
        memoryLimitsAwareHandler.setMaxNumberOfElementsInXrefStructure(20);
        Assertions.assertEquals(20, memoryLimitsAwareHandler.getMaxNumberOfElementsInXrefStructure());
    }

    @Test
    public void customMaxXObjectSizePerPageHandlerTest() {
        final MemoryLimitsAwareHandler memoryLimitsAwareHandler = new MemoryLimitsAwareHandler();

        Assertions.assertEquals(1024L*1024L*1024L*3L, memoryLimitsAwareHandler.getMaxXObjectsSizePerPage());
        memoryLimitsAwareHandler.setMaxXObjectsSizePerPage(1024L);
        Assertions.assertEquals(1024L, memoryLimitsAwareHandler.getMaxXObjectsSizePerPage());
    }

    @Test
    public void minSizeBasedXrefCapacityHandlerTest() {
        final MemoryLimitsAwareHandler memoryLimitsAwareHandler = new MemoryLimitsAwareHandler(1024*1024);

        Assertions.assertEquals(500000, memoryLimitsAwareHandler.getMaxNumberOfElementsInXrefStructure());
    }

    @Test
    public void sizeBasedXrefCapacityHandlerTest() {
        final MemoryLimitsAwareHandler memoryLimitsAwareHandler = new MemoryLimitsAwareHandler(1024*1024*80);

        Assertions.assertEquals(40000000, memoryLimitsAwareHandler.getMaxNumberOfElementsInXrefStructure());
    }

    @Test
    public void checkCapacityExceedsLimitTest() {
        final MemoryLimitsAwareHandler memoryLimitsAwareHandler = new MemoryLimitsAwareHandler();
        // There we add 2 instead of 1 since xref structures used 1-based indexes, so we decrement the capacity
        // before check.
        final int capacityExceededTheLimit = memoryLimitsAwareHandler.getMaxNumberOfElementsInXrefStructure() + 2;

        Exception ex = Assertions.assertThrows(MemoryLimitsAwareException.class,
                () -> memoryLimitsAwareHandler.checkIfXrefStructureExceedsTheLimit(capacityExceededTheLimit));
        Assertions.assertEquals(KernelExceptionMessageConstant.XREF_STRUCTURE_SIZE_EXCEEDED_THE_LIMIT, ex.getMessage());
    }

    @Test
    public void checkCapacityTest() {
        final MemoryLimitsAwareHandler memoryLimitsAwareHandler = new MemoryLimitsAwareHandler();
        final int capacityToSet = 2;

        AssertUtil.doesNotThrow(() -> memoryLimitsAwareHandler.checkIfXrefStructureExceedsTheLimit(capacityToSet));
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate =
            KernelLogMessageConstant.MEMORYLIMITAWAREHANDLER_OVERRIDE_CREATENEWINSTANCE_METHOD)})
    public void createCopyMemoryHandlerWarningTest() {
        MemoryLimitsAwareHandler customHandler = new MemoryLimitsAwareHandler() {};

        customHandler.setMaxNumberOfElementsInXrefStructure(1);
        customHandler.setMaxXObjectsSizePerPage(2);
        customHandler.setMaxSizeOfDecompressedPdfStreamsSum(3);
        customHandler.setMaxSizeOfSingleDecompressedPdfStream(4);

        MemoryLimitsAwareHandler copy = customHandler.createNewInstance();

        Assertions.assertEquals(1, copy.getMaxNumberOfElementsInXrefStructure());
        Assertions.assertEquals(2, copy.getMaxXObjectsSizePerPage());
        Assertions.assertEquals(3, copy.getMaxSizeOfDecompressedPdfStreamsSum());
        Assertions.assertEquals(4, copy.getMaxSizeOfSingleDecompressedPdfStream());
    }

    @Test
    public void createCopyMemoryHandlerNoWarningTest() {
        MemoryLimitsAwareHandler customHandler = new MemoryLimitsAwareHandler() {
            @Override
            public MemoryLimitsAwareHandler createNewInstance() {
                MemoryLimitsAwareHandler to = new MemoryLimitsAwareHandler();
                to.setMaxSizeOfSingleDecompressedPdfStream(this.getMaxSizeOfSingleDecompressedPdfStream());
                to.setMaxSizeOfDecompressedPdfStreamsSum(this.getMaxSizeOfDecompressedPdfStreamsSum());
                to.setMaxNumberOfElementsInXrefStructure(this.getMaxNumberOfElementsInXrefStructure());
                to.setMaxXObjectsSizePerPage(this.getMaxXObjectsSizePerPage());

                return to;
            }
        };

        customHandler.setMaxNumberOfElementsInXrefStructure(1);
        customHandler.setMaxXObjectsSizePerPage(2);
        customHandler.setMaxSizeOfDecompressedPdfStreamsSum(3);
        customHandler.setMaxSizeOfSingleDecompressedPdfStream(4);

        MemoryLimitsAwareHandler copy = customHandler.createNewInstance();

        Assertions.assertEquals(1, copy.getMaxNumberOfElementsInXrefStructure());
        Assertions.assertEquals(2, copy.getMaxXObjectsSizePerPage());
        Assertions.assertEquals(3, copy.getMaxSizeOfDecompressedPdfStreamsSum());
        Assertions.assertEquals(4, copy.getMaxSizeOfSingleDecompressedPdfStream());
    }

    private static void testSingleStream(MemoryLimitsAwareHandler handler) {
        String expectedExceptionMessage = KernelExceptionMessageConstant.DURING_DECOMPRESSION_SINGLE_STREAM_OCCUPIED_MORE_MEMORY_THAN_ALLOWED;
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
        Assertions.assertEquals(expectedFailureIndex, i);
        Assertions.assertEquals(expectedExceptionMessage, occuredExceptionMessage);
    }

    private static void testMultipleStreams(MemoryLimitsAwareHandler handler) {
        String expectedExceptionMessage = KernelExceptionMessageConstant.DURING_DECOMPRESSION_MULTIPLE_STREAMS_IN_SUM_OCCUPIED_MORE_MEMORY_THAN_ALLOWED;
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
        Assertions.assertEquals(expectedFailureIndex, i);
        Assertions.assertEquals(expectedExceptionMessage, occuredExceptionMessage);
    }

}
