/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
    Authors: iText Software.

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
package com.itextpdf.kernel.actions.exceptions;

import com.itextpdf.io.IOException;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class AggregatedExceptionTest extends ExtendedITextTest {

    @Test
    public void aggregatedMessageWithGeneralMessageTest() {
        List<Exception> exceptions = new ArrayList<>();
        exceptions.add(new RuntimeException("Message 1"));
        exceptions.add(new RuntimeException("Message 2"));
        exceptions.add(new IOException("Message 3"));

        AggregatedException exception = new AggregatedException("General message", exceptions);
        Assert.assertEquals(exceptions, exception.getAggregatedExceptions());
        Assert.assertEquals("General message:\n"
                + "0) Message 1\n"
                + "1) Message 2\n"
                + "2) Message 3\n", exception.getMessage());
    }

    @Test
    public void aggregatedMessageWithoutGeneralMessageTest() {
        List<Exception> exceptions = new ArrayList<>();
        exceptions.add(new RuntimeException("Message 1"));
        exceptions.add(new RuntimeException("Message 2"));
        exceptions.add(new IOException("Message 3"));

        AggregatedException exception = new AggregatedException(exceptions);
        Assert.assertEquals("Aggregated message:\n"
                + "0) Message 1\n"
                + "1) Message 2\n"
                + "2) Message 3\n", exception.getMessage());
    }
}
