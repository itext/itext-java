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

import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class PdfNumberTest extends ExtendedITextTest {

    private static final double DELTA = 0.0001;

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = IoLogMessageConstant.ATTEMPT_PROCESS_NAN))
    public void testNaN() {
        PdfNumber number = new PdfNumber(Double.NaN);
        // code for "0"
        byte[] expected = {48};
        Assertions.assertArrayEquals(expected, number.getInternalContent());
    }

    @Test
    public void intValueInPdfNumberTest() {
        double valueToSet = 3000000000d;
        final PdfNumber number = new PdfNumber(valueToSet);

        Assertions.assertEquals(valueToSet, number.getValue(), DELTA);
        Assertions.assertEquals(valueToSet, number.doubleValue(), DELTA);
        Assertions.assertEquals(Integer.MAX_VALUE, number.intValue());

        valueToSet = 50d;
        number.setValue(valueToSet + DELTA);
        Assertions.assertEquals(50, number.intValue());
    }
}
