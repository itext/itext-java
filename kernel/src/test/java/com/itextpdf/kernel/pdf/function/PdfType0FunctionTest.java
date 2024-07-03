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
package com.itextpdf.kernel.pdf.function;

import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class PdfType0FunctionTest extends ExtendedITextTest {

    protected static final double DELTA = 1e-12;

    @Test
    public void testEncoding() {
        int[][] encode = {
                {0, 1},
                {0, 10},
                {2, 7},
                {13, 21}
        };
        double[] x = {0, 0.3, 0.5, 0.9, 1};
        double[][] expected = {
                x,
                {0, 3, 5, 9, 10},
                {2, 3.5, 4.5, 6.5, 7},
                {13, 15.4, 17, 20.2, 21}
        };
        for (int i = 0; i < encode.length; ++i) {
            for (int j = 0; j < x.length; ++j) {
                double actual = PdfType0Function.encode(x[j], encode[i][0], encode[i][1]);

                Assertions.assertEquals(expected[i][j], actual, DELTA);
            }
        }
    }

    @Test
    public void testFloor() {
        double[][] normals = {
                {0.0, 0.0, 0.0},
                {0.5, 0.5, 0.6},
                {1.0, 1.0, 1.0}
        };
        int[] encode = {0, 1, 2, 7, 13, 21};
        int[][] expected = {
                {0, 2, 13},
                {0, 4, 17},
                {0, 6, 20}
        };
        for (int i = 0; i < normals.length; ++i) {
            int[] actual = PdfType0Function.getFloor(normals[i], encode);

            Assertions.assertArrayEquals(expected[i], actual);
        }
    }

    @Test
    public void testSamplePositionDim1() {
        for (int size = 2; size < 5; ++size) {
            int[] sizeArr = new int[] {size};
            for (int sample = 0; sample < size; ++sample) {
                int position = PdfType0Function.getSamplePosition(new int[] {sample}, sizeArr);

                Assertions.assertEquals(sample, position);
            }
        }
    }

    @Test
    public void testSamplePositionDim3() {
        int[][] size = {
                {2, 2, 2},
                {5, 5, 5},
                {8, 13, 21}
        };
        int[][][] samples = {
                {{0, 0, 0}, {1, 1, 1}, {0, 1, 0}, {1, 0, 1}},
                {{0, 0, 0}, {4, 4, 4}, {2, 3, 4}, {4, 3, 2}},
                {{0, 0, 0}, {7, 12, 20}, {0, 7, 20}, {6, 7, 8}}
        };
        int[][] expected = {
                {0, 7, 2, 5},
                {0, 124, 117, 69},
                {0, 2183, 2136, 894}
        };
        for (int i = 0; i < size.length; ++i) {
            for (int j = 0; j < samples[i].length; ++j) {
                int actual = PdfType0Function.getSamplePosition(samples[i][j], size[i]);
                Assertions.assertEquals(expected[i][j], actual);
            }
        }
    }

    @Test
    public void testFloorWeights() {
        double[][] normals = {
                {0.0, 0.0, 0.0},
                {0.3, 0.5, 0.7},
                {1.0, 1.0, 1.0}
        };
        int[] encode = {0, 1, 2, 7, 13, 21};
        double[][] expected = {
                {0, 0, 0},
                {0.3, 0.5, 0.6},
                {1, 1, 1}
        };

        for (int i = 0; i < normals.length; ++i) {
            double[] actual = PdfType0Function.getFloorWeights(normals[i], encode);

            Assertions.assertArrayEquals(expected[i], actual, DELTA);
        }
    }

    @Test
    public void testFloorWeight() {
        double[] normals = {0.0, 0.2, 0.4, 0.6, 0.8, 1.0};
        int[] encode = {0, 1, 1, 2, 3, 5, 8, 13, 21, 34, 0, 34};
        double[] expected = {0.0, 0.2, 0.8, 0.0, 0.4, 1.0};

        for (int i = 0; i < normals.length; ++i) {
            double actual = PdfType0Function.getFloorWeight(normals[i], encode[2 * i], encode[2 * i + 1]);

            Assertions.assertEquals(expected[i], actual, DELTA);
        }
    }

    @Test
    public void testSpecialSweepMethod() {
        double[][] rhsVectors = {
                {1},
                {5, 5},
                {5, 6, 5},
                {6, 12, 18, 19}
        };
        double[][] expected = {
                {0, 0.25, 0},
                {0, 1, 1, 0},
                {0, 1, 1, 1, 0},
                {0, 1, 2, 3, 4, 0}
        };

        for (int i = 0; i < rhsVectors.length; ++i) {
            double[] actual = PdfType0Function.specialSweepMethod(rhsVectors[i]);
             Assertions.assertArrayEquals(expected[i], actual, DELTA);
        }
    }

    @Test
    public void testNoInputException() {
        AbstractPdfFunction<PdfStream> function = new PdfType0Function(new PdfStream());

        Exception e = Assertions
                .assertThrows(IllegalArgumentException.class, () -> function.calculate(new double[] {0}));
        Assertions.assertEquals(KernelExceptionMessageConstant.PDF_TYPE0_FUNCTION_NOT_NULL_PARAMETERS, e.getMessage());
    }

    @Test
    public void testSimpleValidPdfFunction() {
        AbstractPdfFunction<PdfStream> function = generateSimplePdfFunction(new byte[] {0}, 1);
        AssertUtil.doesNotThrow(() -> function.calculate(new double[] {0}));
    }



    @Test
    public void testInvalidBitsPerSampleException() {
        PdfType0Function function = generateSimplePdfFunction(new byte[] {0}, 3);
        Exception e = Assertions
                .assertThrows(IllegalArgumentException.class, () -> function.calculate(new double[] {0}));
        Assertions.assertEquals(KernelExceptionMessageConstant.PDF_TYPE0_FUNCTION_BITS_PER_SAMPLE_INVALID_VALUE, e.getMessage());
    }

    @Test
    public void testInvalidOrderException() {
        PdfType0Function function = generateSimplePdfFunction(new byte[] {0}, 1);
        function.setOrder(2);

        Exception e = Assertions
                .assertThrows(IllegalArgumentException.class, () -> function.calculate(new double[] {0}));
        Assertions.assertEquals(KernelExceptionMessageConstant.PDF_TYPE0_FUNCTION_INVALID_ORDER, e.getMessage());
    }

    @Test
    public void testInvalidDomainException() {
        PdfType0Function function = generateSimplePdfFunction(new byte[] {0}, 1);
        function.setDomain(new double[] {0, 1, 1});

        Exception e = Assertions
                .assertThrows(IllegalArgumentException.class, () -> function.calculate(new double[] {0}));
        Assertions.assertEquals(KernelExceptionMessageConstant.PDF_TYPE0_FUNCTION_INVALID_DOMAIN, e.getMessage());
    }

    @Test
    public void testInvalidRangeException() {
        PdfType0Function function = generateSimplePdfFunction(new byte[] {0}, 1);
        function.setRange(new double[] {0, 1, 1});

        Exception e = Assertions
                .assertThrows(IllegalArgumentException.class, () -> function.calculate(new double[] {0}));
        Assertions.assertEquals(KernelExceptionMessageConstant.PDF_TYPE0_FUNCTION_INVALID_RANGE, e.getMessage());
    }

    @Test
    public void testInvalidSizeException() {
        PdfType0Function function = generateSimplePdfFunction(new byte[] {0}, 1);
        function.setSize(new int[] {2, 2});

        Exception e = Assertions
                .assertThrows(IllegalArgumentException.class, () -> function.calculate(new double[] {0}));
        Assertions.assertEquals(KernelExceptionMessageConstant.PDF_TYPE0_FUNCTION_INVALID_SIZE, e.getMessage());
    }

    @Test
    public void testInvalidEncodeException() {
        PdfType0Function function = generateSimplePdfFunction(new byte[] {0}, 1);
        function.setEncode(new int[] {3, 4});

        Exception e = Assertions
                .assertThrows(IllegalArgumentException.class, () -> function.calculate(new double[] {0}));
        Assertions.assertEquals(KernelExceptionMessageConstant.PDF_TYPE0_FUNCTION_INVALID_ENCODE, e.getMessage());
    }

    @Test
    public void testInvalidDecodeException() {
        PdfType0Function function = generateSimplePdfFunction(new byte[] {0}, 1);
        function.setDecode(new double[] {0});

        Exception e = Assertions
                .assertThrows(IllegalArgumentException.class, () -> function.calculate(new double[] {0}));
        Assertions.assertEquals(KernelExceptionMessageConstant.PDF_TYPE0_FUNCTION_INVALID_DECODE, e.getMessage());
    }

    @Test
    public void testInvalidSamplesException() {
        PdfType0Function function = generateSimplePdfFunction(new byte[] {0x0f, 0x0f}, 4);
        function.setSize(new int[] {5});

        Exception e = Assertions
                .assertThrows(IllegalArgumentException.class, () -> function.calculate(new double[] {0}));
        Assertions.assertEquals(KernelExceptionMessageConstant.PDF_TYPE0_FUNCTION_INVALID_SAMPLES, e.getMessage());
    }

    private PdfType0Function generateSimplePdfFunction(byte[] samples, int bitsPerSample) {
        PdfStream stream = new PdfStream(samples);
        stream.put(PdfName.Domain, new PdfArray(new double[] {0, 1}));
        stream.put(PdfName.Range, new PdfArray(new double[] {0, 1}));
        stream.put(PdfName.Size, new PdfArray(new int[] {2}));
        stream.put(PdfName.BitsPerSample, new PdfNumber(bitsPerSample));

        return new PdfType0Function(stream);
    }
}
