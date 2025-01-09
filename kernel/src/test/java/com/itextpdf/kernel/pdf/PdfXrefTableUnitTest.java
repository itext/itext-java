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
import com.itextpdf.kernel.exceptions.MemoryLimitsAwareException;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExceptionTestUtil;
import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class PdfXrefTableUnitTest extends ExtendedITextTest {
    @Test
    public void checkNumberOfIndirectObjectsTest() {
        PdfXrefTable table = new PdfXrefTable();
        Assertions.assertEquals(0, table.getCountOfIndirectObjects());

        int numberOfReferences = 10;

        for (int i = 0; i < numberOfReferences; i++) {
            table.add(new PdfIndirectReference(null, i + 1));
        }

        Assertions.assertEquals(numberOfReferences, table.getCountOfIndirectObjects());
    }

    @Test
    public void checkNumberOfIndirectObjectsWithFreeReferencesTest() {
        PdfXrefTable table = new PdfXrefTable();

        int numberOfReferences = 10;

        for (int i = 0; i < numberOfReferences; i++) {
            table.add(new PdfIndirectReference(null, i + 1));
        }

        table.initFreeReferencesList(null);

        int freeReferenceNumber = 5;
        table.freeReference(table.get(freeReferenceNumber));

        Assertions.assertEquals(numberOfReferences - 1, table.getCountOfIndirectObjects());
        Assertions.assertTrue(table.get(freeReferenceNumber).isFree());
    }

    @Test
    public void checkNumberOfIndirectObjectsWithRandomNumbersTest() {
        PdfXrefTable table = new PdfXrefTable();

        int numberOfReferences = 10;

        for (int i = 0; i < numberOfReferences; i++) {
            table.add(new PdfIndirectReference(null, i * 25));
        }

        Assertions.assertEquals(numberOfReferences, table.getCountOfIndirectObjects());
        Assertions.assertEquals(226, table.size());
    }

    @Test
    public void checkExceedTheNumberOfElementsInXrefTest() {
        final MemoryLimitsAwareHandler memoryLimitsAwareHandler = new MemoryLimitsAwareHandler();
        memoryLimitsAwareHandler.setMaxNumberOfElementsInXrefStructure(5);

        final PdfXrefTable xrefTable = new PdfXrefTable(5, memoryLimitsAwareHandler);
        final int numberOfReferences = 5;
        for (int i = 1; i < numberOfReferences; i++) {
            xrefTable.add(new PdfIndirectReference(null, i));
        }

        Exception exception = Assertions.assertThrows(MemoryLimitsAwareException.class,
                () -> xrefTable.add(new PdfIndirectReference(null, numberOfReferences)));
        Assertions.assertEquals(KernelExceptionMessageConstant.XREF_STRUCTURE_SIZE_EXCEEDED_THE_LIMIT,
                exception.getMessage());
    }

    @Test
    public void ensureCapacityExceedTheLimitTest() {
        final MemoryLimitsAwareHandler memoryLimitsAwareHandler = new MemoryLimitsAwareHandler();
        final PdfXrefTable xrefTable = new PdfXrefTable(memoryLimitsAwareHandler);
        final int newCapacityExceededTheLimit = memoryLimitsAwareHandler.getMaxNumberOfElementsInXrefStructure() + 2;

        // There we add 2 instead of 1 since xref structures used 1-based indexes, so we decrement the capacity
        // before check.
        Exception ex = Assertions.assertThrows(MemoryLimitsAwareException.class,
                () -> xrefTable.setCapacity(newCapacityExceededTheLimit));
        Assertions.assertEquals(KernelExceptionMessageConstant.XREF_STRUCTURE_SIZE_EXCEEDED_THE_LIMIT, ex.getMessage());
    }

    @Test
    public void passCapacityGreaterThanLimitInConstructorTest() {
        final MemoryLimitsAwareHandler memoryLimitsAwareHandler = new MemoryLimitsAwareHandler();
        memoryLimitsAwareHandler.setMaxNumberOfElementsInXrefStructure(20);

        Exception ex = Assertions.assertThrows(MemoryLimitsAwareException.class,
                () -> new PdfXrefTable(30, memoryLimitsAwareHandler));
        Assertions.assertEquals(KernelExceptionMessageConstant.XREF_STRUCTURE_SIZE_EXCEEDED_THE_LIMIT, ex.getMessage());
    }

    @Test
    public void zeroCapacityInConstructorWithHandlerTest() {
        final MemoryLimitsAwareHandler memoryLimitsAwareHandler = new MemoryLimitsAwareHandler();
        memoryLimitsAwareHandler.setMaxNumberOfElementsInXrefStructure(20);
        final PdfXrefTable xrefTable = new PdfXrefTable(0, memoryLimitsAwareHandler);

        Assertions.assertEquals(20, xrefTable.getCapacity());
    }

    @Test
    public void xRefMaxValueLong() {
        PdfDocument document = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        document.xref.add(new PdfIndirectReferenceProxy(document, 11, Long.MAX_VALUE));

        Exception e = Assertions.assertThrows(PdfException.class, () -> {
            document.close();
        });
        Assertions.assertEquals(KernelExceptionMessageConstant.XREF_HAS_AN_ENTRY_WITH_TOO_BIG_OFFSET, e.getMessage());
    }


    @Test
    public void maxCrossReferenceOffSetReached() {
        long justOver10gbLogical = 10_000_000_001L;
        PdfDocument document = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        document.xref.add(new PdfIndirectReferenceProxy(document, 11, justOver10gbLogical));

        Exception e = Assertions.assertThrows(PdfException.class, () -> {
            document.close();
        });
        Assertions.assertEquals(KernelExceptionMessageConstant.XREF_HAS_AN_ENTRY_WITH_TOO_BIG_OFFSET, e.getMessage());
    }

    @Test
    public void maxCrossReference() {
        long justOver10gbLogical = 10_000_000_000L;
        PdfDocument document = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        document.xref.add(new PdfIndirectReferenceProxy(document, 11, justOver10gbLogical));

        Exception e = Assertions.assertThrows(PdfException.class, () -> {
            document.close();
        });
        Assertions.assertEquals(KernelExceptionMessageConstant.XREF_HAS_AN_ENTRY_WITH_TOO_BIG_OFFSET, e.getMessage());
    }

    @Test
    public void justBelowXrefThreshold() {
        long maxAllowedOffset = 10_000_000_000L - 1L;
        PdfDocument document = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        document.xref.add(new PdfIndirectReferenceProxy(document, 11, maxAllowedOffset));

        AssertUtil.doesNotThrow(() -> document.close());
    }

    @Test
    public void xRefIntMax() {
        PdfDocument document = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        document.xref.add(new PdfIndirectReferenceProxy(document, 11, Integer.MAX_VALUE));
        AssertUtil.doesNotThrow(() -> document.close());
    }




}
 class PdfIndirectReferenceProxy extends PdfIndirectReference {
    private final long offset;

    public PdfIndirectReferenceProxy(PdfDocument document, int objNumber, long offset) {
        super(document, objNumber);
        this.offset = offset;
    }

    @Override
    public long getOffset() {
        return offset;
    }
}
