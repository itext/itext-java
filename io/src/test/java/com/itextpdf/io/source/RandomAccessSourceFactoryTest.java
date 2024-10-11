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
package com.itextpdf.io.source;

import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.io.exceptions.IoExceptionMessageConstant;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class RandomAccessSourceFactoryTest extends ExtendedITextTest {

    private final static String SOURCE_FILE = "./src/test/resources/com/itextpdf/io/source/RAF.txt";

    @Test
    public void readRASInputStreamClosedTest() throws IOException {
        String fileName = SOURCE_FILE;
        try (InputStream pdfStream = FileUtil.getInputStreamForFile(fileName)) {

            IRandomAccessSource randomAccessSource = new RandomAccessSourceFactory()
                    .extractOrCreateSource(pdfStream);
            RASInputStream rasInputStream = new RASInputStream(randomAccessSource);
            IRandomAccessSource extractedRandomAccessSource = new RandomAccessSourceFactory()
                .extractOrCreateSource(rasInputStream);

            extractedRandomAccessSource.close();

            Exception e = Assertions.assertThrows(IllegalStateException.class, () -> rasInputStream.read());
            Assertions.assertEquals(IoExceptionMessageConstant.ALREADY_CLOSED, e.getMessage());

            e = Assertions.assertThrows(IllegalStateException.class,
                    () -> randomAccessSource.get(0));
            Assertions.assertEquals(IoExceptionMessageConstant.ALREADY_CLOSED, e.getMessage());
            e = Assertions.assertThrows(IllegalStateException.class,
                    () -> randomAccessSource.get(0, new byte[10], 0, 10));
            Assertions.assertEquals(IoExceptionMessageConstant.ALREADY_CLOSED, e.getMessage());
            e = Assertions.assertThrows(IllegalStateException.class,
                    () -> randomAccessSource.length());
            Assertions.assertEquals(IoExceptionMessageConstant.ALREADY_CLOSED, e.getMessage());
        }
    }

    @Test
    public void readRASInputStreamTest() throws IOException {
        String fileName = SOURCE_FILE;
        try (InputStream pdfStream = FileUtil.getInputStreamForFile(fileName)) {
            IRandomAccessSource randomAccessSource = new RandomAccessSourceFactory()
                    .extractOrCreateSource(pdfStream);
            RASInputStream rasInputStream = new RASInputStream(randomAccessSource);
            IRandomAccessSource extractedRandomAccessSource = new RandomAccessSourceFactory()
                    .extractOrCreateSource(rasInputStream);

            Assertions.assertEquals(72, rasInputStream.read());
            Assertions.assertEquals(72, extractedRandomAccessSource.get(0));
            Assertions.assertEquals(extractedRandomAccessSource, rasInputStream.getSource());
        }
    }
}
