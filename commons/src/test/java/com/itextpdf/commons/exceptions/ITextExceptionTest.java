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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class ITextExceptionTest extends ExtendedITextTest {
    @Test
    public void noParametersConstructorTest() {
        Exception exception = Assertions.assertThrows(ITextException.class, () -> {
            throw new ITextException();
        });
        Assertions.assertEquals(CommonsExceptionMessageConstant.UNKNOWN_ITEXT_EXCEPTION, exception.getMessage());
    }

    @Test
    public void stringConstructorTest() {
        Exception exception = Assertions.assertThrows(ITextException.class, () -> {
            throw new ITextException("message");
        });
        Assertions.assertEquals("message", exception.getMessage());
    }

    @Test
    public void throwableConstructorTest() {
        RuntimeException cause = new RuntimeException("cause");
        Exception exception = Assertions.assertThrows(ITextException.class, () -> {
            throw new ITextException(cause);
        });

        Assertions.assertEquals(CommonsExceptionMessageConstant.UNKNOWN_ITEXT_EXCEPTION, exception.getMessage());
        Assertions.assertEquals(cause, exception.getCause());
    }
}
