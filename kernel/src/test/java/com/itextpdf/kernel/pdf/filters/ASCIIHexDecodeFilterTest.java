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
package com.itextpdf.kernel.pdf.filters;

import com.itextpdf.kernel.PdfException;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class ASCIIHexDecodeFilterTest extends ExtendedITextTest {
    public static final String SOURCE_FILE =
            "./src/test/resources/com/itextpdf/kernel/pdf/filters/ASCIIHex.bin";

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

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
        Assert.assertEquals(expectedResult, decoded);
    }

    @Test
    public void decodingIllegalaCharacterTest() {
        byte[] bytes = "4c6f72656d20697073756d2eg>".getBytes();
        junitExpectedException.expect(PdfException.class);
        junitExpectedException.expectMessage(PdfException.IllegalCharacterInAsciihexdecode);
        ASCIIHexDecodeFilter.ASCIIHexDecode(bytes);
    }

    @Test
    public void decodingSkipWhitespacesTest() {
        byte[] bytes = "4c 6f 72 65 6d 20 69 70 73 75 6d 2e>".getBytes();
        String expectedResult = "Lorem ipsum.";

        String decoded = new String(ASCIIHexDecodeFilter.ASCIIHexDecode(bytes));
        Assert.assertEquals(expectedResult, decoded);
    }
}
