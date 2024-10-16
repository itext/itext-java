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
package com.itextpdf.commons.exceptions;

import com.itextpdf.test.ExtendedITextTest;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class AggregatedExceptionTest extends ExtendedITextTest {

    @Test
    public void aggregatedMessageWithGeneralMessageTest() {
        List<RuntimeException> exceptions = new ArrayList<>();
        exceptions.add(new RuntimeException("Message 1"));
        exceptions.add(new RuntimeException("Message 2"));
        exceptions.add(new CustomException("Message 3"));

        AggregatedException exception = new AggregatedException("General message", exceptions);
        Assertions.assertEquals(exceptions, exception.getAggregatedExceptions());
        Assertions.assertEquals("General message:\n"
                + "0) Message 1\n"
                + "1) Message 2\n"
                + "2) Message 3\n", exception.getMessage());
    }

    @Test
    public void aggregatedMessageWithoutGeneralMessageTest() {
        List<RuntimeException> exceptions = new ArrayList<>();
        exceptions.add(new RuntimeException("Message 1"));
        exceptions.add(new RuntimeException("Message 2"));
        exceptions.add(new CustomException("Message 3"));

        AggregatedException exception = new AggregatedException(exceptions);
        Assertions.assertEquals("Aggregated message:\n"
                + "0) Message 1\n"
                + "1) Message 2\n"
                + "2) Message 3\n", exception.getMessage());
    }

    private static final class CustomException extends RuntimeException {
        public CustomException(String message) {
            super(message);
        }
    }
}
