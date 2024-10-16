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
package com.itextpdf.kernel.pdf.function.utils;

import com.itextpdf.test.ExtendedITextTest;

import java.util.Arrays;
import java.util.Collection;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@Tag("IntegrationTest")
public class SampleExtractorTest extends ExtendedITextTest {

    private static final String PARAMETERS_NAME_PATTERN = "{0}bitsPerSample";

    private static final byte[] SAMPLES = {0x01, 0x23, 0x45, 0x67,
            (byte) 0x89, (byte) 0xab, (byte) 0xcd, (byte) 0xef};

    public static Collection<Object[]> samplesInfo() {
        return Arrays.asList(new Object[][] {
                {1, new long[] {
                        0, 0, 0, 0, 0, 0, 0, 1,
                        0, 0, 1, 0, 0, 0, 1, 1,
                        0, 1, 0, 0, 0, 1, 0, 1,
                        0, 1, 1, 0, 0, 1, 1, 1,
                        1, 0, 0, 0, 1, 0, 0, 1,
                        1, 0, 1, 0, 1, 0, 1, 1,
                        1, 1, 0, 0, 1, 1, 0, 1,
                        1, 1, 1, 0, 1, 1, 1, 1
                }},
                {2, new long[] {
                        0, 0, 0, 1, 0, 2, 0, 3,
                        1, 0, 1, 1, 1, 2, 1, 3,
                        2, 0, 2, 1, 2, 2, 2, 3,
                        3, 0, 3, 1, 3, 2, 3, 3
                }},
                {4, new long[] {
                        0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15
                }},
                {8, new long[] {
                        1, (2 << 4) | 3,
                        (4 << 4) | 5, (6 << 4) | 7,
                        (8 << 4) | 9, (10 << 4) | 11,
                        (12 << 4) | 13, (14 << 4) | 15
                }},
                {12, new long[] {
                        (1 << 4) | 2, (3 << 8) | (4 << 4) | 5,
                        (6 << 8) | (7 << 4) | 8, (9 << 8) | 10 << 4 | 11,
                        (12 << 8) | (13 << 4) | 14
                }},
                {16, new long[] {
                        (1 << 8) | (2 << 4) | 3,
                        (4 << 12) | (5 << 8) | (6 << 4) | 7,
                        (8 << 12) | (9 << 8) | (10 << 4) | 11,
                        (12 << 12) | (13 << 8) | (14 << 4) | 15
                }},
                {24, new long[] {
                        (1 << 16) | (2 << 12) | (3 << 8) | (4 << 4) | 5,
                        (6 << 20) | (7 << 16) | (8 << 12) | (9 << 8) | (10 << 4) | 11
                }},
                {32, new long[] {
                        (1 << 24) | (2 << 20) | (3 << 16) | (4 << 12) | (5 << 8) | (6 << 4) | 7,
                        (8L << 28) | (9 << 24) | (10 << 20) | (11 << 16) | (12 << 12) | (13 << 8) | (14 << 4) | 15
                }}
        });
    }

    @ParameterizedTest(name = PARAMETERS_NAME_PATTERN)
    @MethodSource("samplesInfo")
    public void testSamplesExtraction(int bitsPerSample, long[] expected) {
        long[] actual = new long[(SAMPLES.length << 3) / bitsPerSample];
        Assertions.assertEquals(expected.length, actual.length);

        AbstractSampleExtractor extractor = AbstractSampleExtractor.createExtractor(bitsPerSample);
        for (int i = 0; i < actual.length; ++i) {
            actual[i] = extractor.extract(SAMPLES, i);
        }

        Assertions.assertArrayEquals(expected, actual);
    }

}
