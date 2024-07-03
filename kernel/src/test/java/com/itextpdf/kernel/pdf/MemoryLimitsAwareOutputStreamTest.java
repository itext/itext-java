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

import com.itextpdf.kernel.exceptions.MemoryLimitsAwareException;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class MemoryLimitsAwareOutputStreamTest extends ExtendedITextTest {

    @Test
    public void testMaxSize() {
        byte[] bigArray = new byte[70];
        byte[] smallArray = new byte[31];

        MemoryLimitsAwareOutputStream stream = new MemoryLimitsAwareOutputStream();

        stream.setMaxStreamSize(100);
        Assertions.assertEquals(100, stream.getMaxStreamSize());

        stream.write(bigArray, 0, bigArray.length);

        Assertions.assertEquals(bigArray.length, stream.size());
        Assertions.assertThrows(MemoryLimitsAwareException.class, () -> stream.write(smallArray, 0, smallArray.length));
    }

    @Test
    public void testNegativeSize() {
        byte[] zeroArray = new byte[0];

        MemoryLimitsAwareOutputStream stream = new MemoryLimitsAwareOutputStream();

        stream.setMaxStreamSize(-100);

        Assertions.assertEquals(-100, stream.getMaxStreamSize());
        Assertions.assertThrows(MemoryLimitsAwareException.class, () -> stream.write(zeroArray, 0, zeroArray.length));
    }

    @Test
    public void testIncorrectLength() {
        MemoryLimitsAwareOutputStream stream = new MemoryLimitsAwareOutputStream();

        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> stream.write(new byte[1],0,  -1));
    }
}
