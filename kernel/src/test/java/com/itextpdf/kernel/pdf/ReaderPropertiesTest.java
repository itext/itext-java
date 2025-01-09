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

import com.itextpdf.test.ExtendedITextTest;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("UnitTest")
public class ReaderPropertiesTest extends ExtendedITextTest {

    @Test
    public void copyConstructorTest() {
        MemoryLimitsAwareHandler handler = new MemoryLimitsAwareHandler();
        handler.setMaxXObjectsSizePerPage(10);
        ReaderProperties properties = new ReaderProperties().setPassword("123".getBytes(StandardCharsets.ISO_8859_1))
                .setMemoryLimitsAwareHandler(handler);

        ReaderProperties copy = new ReaderProperties(properties);

        Assertions.assertArrayEquals(copy.password, properties.password);

        Assertions.assertNotEquals(copy.memoryLimitsAwareHandler, properties.memoryLimitsAwareHandler);
        Assertions.assertEquals(copy.memoryLimitsAwareHandler.getMaxXObjectsSizePerPage(),
                properties.memoryLimitsAwareHandler.getMaxXObjectsSizePerPage());
        Assertions.assertEquals(10, copy.memoryLimitsAwareHandler.getMaxXObjectsSizePerPage());
    }
}
