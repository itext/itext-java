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

import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.test.ExtendedITextTest;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public abstract class AbstractPdfType0FunctionTest extends ExtendedITextTest {

    protected static final double DELTA = 1e-12;

    private final int order;

    protected AbstractPdfType0FunctionTest(int order) {
        this.order = order;
    }

    @Test
    public void testConstantFunctions() {
        // f(x, y, z) = (1, 2, 3)
        double[] expected = {-1, 0, 1};

        PdfStream stream = new PdfStream(new byte[] {0, 0, 0});
        stream.put(PdfName.Domain, new PdfArray(new double[] {0, 0, 0, 0, 0, 0}));
        stream.put(PdfName.Size, new PdfArray(new int[] {2, 1, 3}));
        stream.put(PdfName.Range, new PdfArray(
                new double[] {expected[0], expected[0], expected[1], expected[1], expected[2], expected[2]}));
        stream.put(PdfName.BitsPerSample, new PdfNumber(1));

        PdfType0Function pdfFunction = new PdfType0Function(stream);

        double[] actual = pdfFunction.calculate(new double[] {0, 10, -99});

        Assertions.assertArrayEquals(expected, actual, DELTA);
    }

    @Test
    public void testLinearFunctions() {
        // f(x) = (x, 3x, 2-x, 2x-1) : [-1, 2] -> [-1,2]x[-3,6]x[0,3]x[-3,3]
        Function<Double, List<Double>> function = x -> Arrays.asList(x, 3 * x, 2 - x, 2 * x - 1);

        double[] domain = {-1, 2};
        int[] size = {2};
        double[] range = {-1, 2, -3, 6, 0, 3, -3, 3};
        int bitsPerSample = 1;
        byte[] samples = {0x2d};

        PdfType0Function pdfFunction = new PdfType0Function(domain, size, range, order,
                null, null, bitsPerSample, samples);

        double[] arguments = {-1.0, -0.67, -0.33, 0.0, 0.33, 0.67, 1.0, 1.33, 1.67, 2.0};
        for (double argument : arguments) {
            List<Double> fRes = function.apply(argument);
            Stream<Double> stream = fRes.stream();
            double[] expected = stream.mapToDouble(x -> x).toArray();
            double[] actual = pdfFunction.calculate(new double[] {argument});

            Assertions.assertArrayEquals(expected, actual, DELTA);
        }
    }

    @Test
    public void testLinearFunctionsWithEncoding() {
        // f(x) = (x, 3x, 2-x, 2x-1) : [-1, 2] -> [-1,2]x[-3,6]x[0,3]x[-3,3]
        Function<Double, List<Double>> function = x -> Arrays.asList(x, 3 * x, 2 - x, 2 * x - 1);

        double[] domain = {-1, 2};
        int[] size = {4};
        double[] range = {-1, 2, -3, 6, 0, 3, -3, 3};
        int[] encode = {1, 2};
        int bitsPerSample = 1;
        byte[] samples = {(byte) 0xf2, (byte) 0xd0};

        PdfType0Function pdfFunction = new PdfType0Function(domain, size, range, order,
                encode, null, bitsPerSample, samples);

        double[] arguments = {-1.0, -0.67, -0.33, 0.0, 0.33, 0.67, 1.0, 1.33, 1.67, 2.0};
        for (double argument : arguments) {
            double[] expected = function.apply(argument).stream().mapToDouble(x -> x).toArray();
            double[] actual = pdfFunction.calculate(new double[] {argument});

            Assertions.assertArrayEquals(expected, actual, DELTA);
        }
    }

    @Test
    public void testLinearFunctionsDim3() {
        // f(x, y, z) = (x+y, x-y, x+y+z) : [0,1]x[0,1]x[0,1] -> [0,2]x[-1,1]x[0,3]
        Function<List<Double>, List<Double>> function = x -> Arrays
                .asList(x.get(0) + x.get(1), x.get(0) - x.get(1), x.get(0) + x.get(1) + x.get(2));

        double[] domain = {0, 1, 0, 1, 0, 1};
        int[] size = {2, 2, 2};
        double[] range = {0, 2, -1, 1, 0, 3};
        double[] decode = {0, 3, -1, 2, 0, 3};
        int bitsPerSample = 2;
        byte[] samples = {0x11, (byte) 0x94, 0x66, 0x15, (byte) 0xa4, (byte) 0xa7};

        PdfType0Function pdfFunction = new PdfType0Function(domain, size, range, order,
                null, decode, bitsPerSample, samples);

        List<List<Double>> arguments = Arrays.asList(
                Arrays.asList(0.0, 0.0, 0.0),
                Arrays.asList(1.0, 1.0, 1.0),
                Arrays.asList(0.05, 0.95, 0.35),
                Arrays.asList(0.15, 0.01, 0.88),
                Arrays.asList(0.99, 0.99, 0.5),
                Arrays.asList(0.98, 0.1, 0.01)
        );
        for (List<Double> argument : arguments) {
            double[] expected = function.apply(argument).stream().mapToDouble(d -> d).toArray();
            double[] actual = pdfFunction.calculate(argument.stream().mapToDouble(d -> d).toArray());

            Assertions.assertArrayEquals(expected, actual, DELTA);
        }
    }

    protected void testPolynomials(double[] expectedDelta) {
        // f(x) = (x^4, 1 - x + x^3, 1 - x^2) : [-1,1] -> [0,1]x[0.5,1.5]x[0,1]
        Function<Double, List<Double>> function = x -> {
            double x2 = x * x;
            return Arrays.asList(x2 * x2, 1 - x + x * x2, 1 - x2);
        };

        double[] domain = {-1, 1};
        int[] size = {5};
        double[] range = {0, 1, 0.5, 1.5, 0, 1};
        double[] decode = {0, 15.9375, 0, 31.875, 0, 63.75};
        int bitsPerSample = 8;
        byte[] samples = {16, 8, 0, 1, 11, 3, 0, 8, 4, 1, 5, 3, 16, 8, 0};

        PdfType0Function pdfFunction = new PdfType0Function(domain, size, range, order,
                null, decode, bitsPerSample, samples);

        double[] arguments = {-1.0, -0.67, -0.33, 0.0, 0.33, 0.67, 1.0};
        for (double argument : arguments) {
            double[] expected = function.apply(argument).stream().mapToDouble(x -> x).toArray();
            double[] actual = pdfFunction.calculate(new double[] {argument});

            for (int i = 0; i < expectedDelta.length; ++i) {
                Assertions.assertEquals(expected[i], actual[i], expectedDelta[i]);
            }
        }
    }

    protected void testPolynomialsWithEncoding(double[] expectedDelta) {
        // f(x) = (x^4, 1 - x + x^3, 1 - x^2) : [-1,1] -> [0,1]x[0.5,1.5]x[0,1]
        Function<Double, List<Double>> function = x -> {
            double x2 = x * x;
            return Arrays.asList(x2 * x2, 1 - x + x * x2, 1 - x2);
        };

        double[] domain = {-1, 1};
        int[] size = {10};
        double[] range = {0, 1, 0.5, 1.5, 0, 1};
        int[] encode = {3, 7};
        double[] decode = {0, 15.9375, 0, 31.875, 0, 63.75};
        int bitsPerSample = 8;
        byte[] samples = {
                (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
                (byte) 0xff,
                16, 8, 0, 1, 11, 3, 0, 8, 4, 1, 5, 3, 16, 8, 0,
                (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
                (byte) 0xff
        };

        PdfType0Function pdfFunction = new PdfType0Function(domain, size, range, order,
                encode, decode, bitsPerSample, samples);

        double[] arguments = {-1.0, -0.67, -0.33, 0.0, 0.33, 0.67, 1.0};
        for (double argument : arguments) {
            double[] expected = function.apply(argument).stream().mapToDouble(x -> x).toArray();
            double[] actual = pdfFunction.calculate(new double[] {argument});

            for (int i = 0; i < expectedDelta.length; ++i) {
                Assertions.assertEquals(expected[i], actual[i], expectedDelta[i]);
            }
        }
    }


    protected void testPolynomialsDim2(double[] expectedDelta) {
        // f(x, y) = (x^2+y, x+xy); [0, 1]x[0, 1] -> [0, 2]x[0,2]
        Function<List<Double>, List<Double>> function = x -> Arrays
                .asList(x.get(0) * x.get(0) + x.get(1), x.get(0) + x.get(0) * x.get(1));

        double[] domain = {0, 1, 0, 1};
        int[] size = {6, 2};
        double[] range = {0, 2, 0, 2};
        double[] decode = {0, 10.2, 0, 51};
        int bitsPerSample = 8;
        byte[] samples = {0, 0, 1, 1, 4, 2, 9, 3, 16, 4, 25, 5, 25, 0, 26, 2, 29, 4, 34, 6, 41, 8, 50, 10};

        PdfType0Function pdfFunction = new PdfType0Function(domain, size, range, order,
                null, decode, bitsPerSample, samples);

        List<List<Double>> arguments = Arrays.asList(
                Arrays.asList(0.0, 0.0),
                Arrays.asList(1.0, 1.0),
                Arrays.asList(0.05, 0.99),
                Arrays.asList(0.9, 0.55),
                Arrays.asList(0.35, 0.11),
                Arrays.asList(0.5, 0.99)
        );
        for (List<Double> argument : arguments) {
            double[] expected = function.apply(argument).stream().mapToDouble(d -> d).toArray();
            double[] actual = pdfFunction.calculate(argument.stream().mapToDouble(d -> d).toArray());

            for (int i = 0; i < expectedDelta.length; ++i) {
                Assertions.assertEquals(expected[i], actual[i], expectedDelta[i]);
            }
        }
    }

    protected void testPolynomialsDim2WithEncoding(double[] expectedDelta) {
        // f(x, y) = (x^2+y, x+xy); [0, 1]x[0, 1] -> [0, 2]x[0,2]
        Function<List<Double>, List<Double>> function = x -> Arrays
                .asList(x.get(0) * x.get(0) + x.get(1), x.get(0) + x.get(0) * x.get(1));

        double[] domain = {0, 1, 0, 1};
        int[] size = {10, 5};
        double[] range = {0, 2, 0, 2};
        int[] encode = {2, 7, 3, 4};
        double[] decode = {0, 10.2, 0, 51};
        int bitsPerSample = 8;
        byte[] samples = {
                0, 0, 1, 0, 2, 0, 3, 0, 4, 0, 5, 0, 6, 0, 7, 0, 8, 0, 9, 0,
                0, 1, 1, 1, 2, 1, 3, 1, 4, 1, 5, 1, 6, 1, 7, 1, 8, 1, 9, 1,
                0, 2, 1, 2, 2, 2, 3, 2, 4, 2, 5, 2, 6, 2, 7, 2, 8, 2, 9, 2,
                0, 3, 1, 3, 0, 0, 1, 1, 4, 2, 9, 3, 16, 4, 25, 5, 8, 3, 9, 3,
                0, 4, 1, 4, 25, 0, 26, 2, 29, 4, 34, 6, 41, 8, 50, 10, 8, 4, 9, 4
        };

        PdfType0Function pdfFunction = new PdfType0Function(domain, size, range, order,
                encode, decode, bitsPerSample, samples);

        List<List<Double>> arguments = Arrays.asList(
                Arrays.asList(0.0, 0.0),
                Arrays.asList(1.0, 1.0),
                Arrays.asList(0.05, 0.99),
                Arrays.asList(0.9, 0.55),
                Arrays.asList(0.35, 0.11),
                Arrays.asList(0.5, 0.99)
        );
        for (List<Double> argument : arguments) {
            double[] expected = function.apply(argument).stream().mapToDouble(d -> d).toArray();
            double[] actual = pdfFunction.calculate(argument.stream().mapToDouble(d -> d).toArray());

            for (int i = 0; i < expectedDelta.length; ++i) {
                Assertions.assertEquals(expected[i], actual[i], expectedDelta[i]);
            }
        }
    }

    protected void testSinus(double delta) {
        // f(x) = sin(x) : [0, 180] -> [0, 1]
        Function<Double, Double> function = x -> Math.sin(Math.toRadians(x));

        double[] domain = {0, 180};
        int[] size = {21};
        double[] range = {0, 1};
        int bitsPerSample = 32;
        byte[] samples = generate1Dim32BitSamples(function, size[0], domain, range);

        PdfType0Function pdfFunction = new PdfType0Function(domain, size, range, order,
                null, null, bitsPerSample, samples);

        for (int i = 0; i <= 180; ++i) {
            double expected = function.apply((double) i);
            double actual = pdfFunction.calculate(new double[] {i})[0];

            Assertions.assertEquals(expected, actual, delta);
        }
    }

    protected void testExponent(double delta) {
        // f(x) = e^x : [0, 2] -> [1, e^2]
        Function<Double, Double> function = (Double val) -> Math.exp(val);

        double[] domain = {0, 2};
        int[] size = {9};
        double[] range = {1, function.apply(domain[1])};
        int bitsPerSample = 32;
        byte[] samples = generate1Dim32BitSamples(function, size[0], domain, range);

        PdfType0Function pdfFunction = new PdfType0Function(domain, size, range, order,
                null, null, bitsPerSample, samples);

        double[] arguments = new double[21];
        for (int i = 0; i < 21; i++) {
            arguments[i] = i / 10;
        }
        //double[] arguments = DoubleStream.iterate(0, x -> x + 0.1).limit(21).toArray();
        for (double argument : arguments) {
            double expected = function.apply(argument);
            double actual = pdfFunction.calculate(new double[] {argument})[0];
            Assertions.assertEquals(expected, actual, delta);
        }
    }

    protected void testLogarithm(double delta) {
        // f(x) = ln(x) : [1,10] -> [0,ln(10)]
        Function<Double, Double> function = (Double val) -> Math.log(val);

        double[] domain = {1, 10};
        int[] size = {10};
        double[] range = {0, function.apply(domain[1])};
        int bitsPerSample = 32;
        byte[] samples = generate1Dim32BitSamples(function, size[0], domain, range);

        PdfType0Function pdfFunction = new PdfType0Function(domain, size, range, order,
                null, null, bitsPerSample, samples);

        double[] arguments = new double[37];
        for (int i = 0; i < 37; i++) {
            arguments[i] = 1 + i / 4;
        }
        //double[] arguments = DoubleStream.iterate(1, x -> x + 0.25).limit(37).toArray();
        for (double argument : arguments) {
            double expected = function.apply(argument);
            double actual = pdfFunction.calculate(new double[] {argument})[0];
            Assertions.assertEquals(expected, actual, delta);
        }
    }

    protected void testGeneralInterpolation(double delta) {
        // f(x, y) = exp(xy) / (1 + y^2) : [0,1]x[0,1] - > [0.5, e]
        Function<List<Double>, Double> function = x ->
                Math.exp(x.get(0) * x.get(1)) / (1 + x.get(1) * x.get(1));

        double[] domain = {0, 1, 0, 1};
        int[] size = {5, 5};
        double[] range = {0.5, Math.E};
        int bitsPerSample = 32;

        byte[] samples = generate2Dim32BitSamples(function, size, domain, range);

        PdfType0Function pdfFunction = new PdfType0Function(domain, size, range, order,
                null, null, bitsPerSample, samples);

        for (double x = 0; x < 1.01; x += 0.03) {
            for (double y = 0; y < 1.01; y += 0.03) {
                double expected = function.apply(Arrays.asList(x, y));
                double actual = pdfFunction.calculate(new double[] {x, y})[0];

                Assertions.assertEquals(expected, actual, delta);
            }
        }
    }

    private byte[] generate1Dim32BitSamples(Function<Double, Double> function, int size, double[] domain,
            double[] range) {
        byte[] samples = new byte[size << 2];
        int pos = 0;
        for (int i = 0; i < size; ++i) {
            double value = function.apply(domain[0] + i * (domain[1] - domain[0]) / (size - 1));
            long sampleValue = (long) Math.round(0xffffffffL * ((value - range[0]) / (range[1] - range[0])));
            for (int k = 24; k >= 0; k -= 8) {
                samples[pos++] = (byte) (((sampleValue) >> k) & 0xff);
            }
        }
        return samples;
    }

    private byte[] generate2Dim32BitSamples(Function<List<Double>, Double> function, int[] size, double[] domain,
            double[] range) {
        byte[] samples = new byte[(size[0] * size[1]) << 2];
        int pos = 0;
        for (int i = 0; i < size[1]; ++i) {
            for (int j = 0; j < size[0]; ++j) {
                double value = function.apply(Arrays.asList(
                        domain[0] + j * (domain[1] - domain[0]) / (size[0] - 1),
                        domain[0] + i * (domain[1] - domain[0]) / (size[1] - 1)
                ));
                long sampleValue = (long) Math.round(0xffffffffL * ((value - range[0]) / (range[1] - range[0])));
                for (int k = 24; k >= 0; k -= 8) {
                    samples[pos++] = (byte) (((sampleValue) >> k) & 0xff);
                }
            }
        }
        return samples;
    }
}
