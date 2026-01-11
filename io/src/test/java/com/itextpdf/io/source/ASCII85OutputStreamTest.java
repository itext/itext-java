/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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

import com.itextpdf.io.util.CloseableByteArrayOutputStream;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@Tag("UnitTest")
public class ASCII85OutputStreamTest extends ExtendedITextTest {
    public static Iterable<Object[]> encodeTestArguments() {
        // These inputs were generated randomly just in case
        return Arrays.asList(
                new Object[]{
                        new byte[0],
                        "~>",
                        "Empty input",
                },
                new Object[]{
                        new byte[]{0x0C},
                        "$i~>",
                        "Single 1-byte block",
                },
                new Object[]{
                        new byte[]{0x39, 0x14},
                        "3>;~>",
                        "Single 2-byte block",
                },
                new Object[]{
                        new byte[]{0x21, (byte) 0xD8, 0x6C},
                        "+kUT~>",
                        "Single 3-byte block",
                },
                new Object[]{
                        new byte[]{0x7F, 0x1D, (byte) 0xEA, 0x50},
                        "Ii[fN~>",
                        "Single full block",
                },
                new Object[]{
                        new byte[]{0x2D, 0x1E, (byte) 0xB7, 0x7D,
                                   0x1F},
                        "/KVBL*r~>",
                        "Single full block + single 1-byte block",
                },
                new Object[]{
                        new byte[]{0x54, (byte) 0x8E, 0x0A, (byte) 0xAD,
                                   0x20, 0x3F},
                        "</q<f+?o~>",
                        "Single full block + single 2-byte block",
                },
                new Object[]{
                        new byte[]{(byte) 0xA1, 0x72, 0x6A, 0x6C,
                                   (byte) 0xAC, 0x56, 0x09},
                        "TlOmaXB#W~>",
                        "Single full block + single 3-byte block",
                },
                new Object[]{
                        new byte[]{(byte) 0x99, 0x0D, (byte) 0xCA, 0x53,
                                   (byte) 0xFF, (byte) 0x94, (byte) 0xC4, 0x26},
                        "R17;;s-1GK~>",
                        "Two full blocks",
                },
                // Single full block + single partial zero block variations
                new Object[]{
                        new byte[]{(byte) 0xA9, 0x1A, (byte) 0x9D, 0x59,
                                    0x00},
                        "W>_=1!!~>",
                        "Single full block + single zeroed 1-byte block",
                },
                new Object[]{
                        new byte[]{(byte) 0xFC, 0x17, 0x09, (byte) 0x8A,
                                   0x00, 0x00},
                        "r\"fZs!!!~>",
                        "Single full block + single zeroed 2-byte block",
                },
                new Object[]{
                        new byte[]{0x65, (byte) 0x87, 0x11, (byte) 0xFF,
                                   0x00, 0x00, 0x00},
                        "AVUlt!!!!~>",
                        "Single full block + single zeroed 3-byte block",
                },
                new Object[]{
                        new byte[]{0x45, 0x35, (byte) 0xD6, (byte) 0xCE,
                                   0x00, 0x00, 0x00, 0x00},
                        "75`ZAz~>",
                        "Single full block + single zeroed full block",
                }
        );
    }

    @ParameterizedTest(name = "{2}")
    @MethodSource("encodeTestArguments")
    public void encodeTest(byte[] input, String output, String name) throws IOException {
        CloseableByteArrayOutputStream baos = new CloseableByteArrayOutputStream();
        try (ASCII85OutputStream encoder = new ASCII85OutputStream(baos)) {
            encoder.write(input);
        }
        Assertions.assertEquals(output, toString(baos));
    }

    @Test
    public void finishableImplTest() throws IOException {
        CloseableByteArrayOutputStream baos = new CloseableByteArrayOutputStream();
        ASCII85OutputStream encoder = new ASCII85OutputStream(baos);

        encoder.write(new byte[]{(byte) 0xBF, 0x1B, 0x25, 0x03, (byte) 0x94});
        encoder.flush();
        Assertions.assertEquals("^DeI$", toString(baos));

        // Should add encoded partial block and EOD
        encoder.finish();
        Assertions.assertFalse(baos.isClosed());
        Assertions.assertEquals("^DeI$PQ~>", toString(baos));

        // Should be noop, since idempotent
        encoder.finish();
        Assertions.assertFalse(baos.isClosed());
        Assertions.assertEquals("^DeI$PQ~>", toString(baos));

        // Should not append data, since finished
        encoder.close();
        Assertions.assertTrue(baos.isClosed());
        Assertions.assertEquals("^DeI$PQ~>", toString(baos));
    }

    private static String toString(java.io.ByteArrayOutputStream baos) {
        return new String(baos.toByteArray(), StandardCharsets.US_ASCII);
    }
}
