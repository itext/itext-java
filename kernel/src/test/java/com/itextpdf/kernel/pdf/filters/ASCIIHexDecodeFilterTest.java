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
package com.itextpdf.kernel.pdf.filters;

import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.test.ExtendedITextTest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class ASCIIHexDecodeFilterTest extends ExtendedITextTest {
    public static final String SOURCE_FILE =
            "./src/test/resources/com/itextpdf/kernel/pdf/filters/ASCIIHex.bin";

    @Test
    public void decodingTest() throws IOException {
        File file = new File(SOURCE_FILE);
        byte[] bytes = Files.readAllBytes(file.toPath());

        String expectedResult = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. "
                + "Donec ac malesuada tellus. "
                + "Quisque a arcu semper, tristique nibh eu, convallis lacus. "
                + "Donec neque justo, condimentum sed molestie ac, mollis eu nibh. "
                + "Vivamus pellentesque condimentum fringilla. "
                + "Nullam euismod ac risus a semper. "
                + "Etiam hendrerit scelerisque sapien tristique varius.";

        String decoded = new String(ASCIIHexDecodeFilter.ASCIIHexDecode(bytes));
        Assertions.assertEquals(expectedResult, decoded);
    }

    @Test
    public void decodingIllegalaCharacterTest() {
        byte[] bytes = "4c6f72656d20697073756d2eg>".getBytes();

        Exception e = Assertions.assertThrows(PdfException.class,
                () -> ASCIIHexDecodeFilter.ASCIIHexDecode(bytes)
        );
        Assertions.assertEquals(KernelExceptionMessageConstant.ILLEGAL_CHARACTER_IN_ASCIIHEXDECODE, e.getMessage());
    }

    @Test
    public void decodingSkipWhitespacesTest() {
        byte[] bytes = "4c 6f 72 65 6d 20 69 70 73 75 6d 2e>".getBytes();
        String expectedResult = "Lorem ipsum.";

        String decoded = new String(ASCIIHexDecodeFilter.ASCIIHexDecode(bytes));
        Assertions.assertEquals(expectedResult, decoded);
    }
}
