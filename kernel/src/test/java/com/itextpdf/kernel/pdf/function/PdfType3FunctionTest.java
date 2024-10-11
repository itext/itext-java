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
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class PdfType3FunctionTest extends ExtendedITextTest {
    private final static double EPSILON = 10e-6;

    @Test
    public void constructorNullFunctionsTest() {
        PdfDictionary type3Func = createMinimalPdfType3FunctionDict();
        type3Func.remove(PdfName.Functions);
        Exception ex = Assertions.assertThrows(PdfException.class, () -> new PdfType3Function(type3Func));
        Assertions.assertEquals(KernelExceptionMessageConstant.INVALID_TYPE_3_FUNCTION_NULL_FUNCTIONS, ex.getMessage());
    }

    @Test
    public void constructorZeroSizeOfFunctionsTest() {
        PdfDictionary type3Func = createMinimalPdfType3FunctionDict();
        type3Func.put(PdfName.Functions, new PdfArray());
        Exception ex = Assertions.assertThrows(PdfException.class, () -> new PdfType3Function(type3Func));
        Assertions.assertEquals(KernelExceptionMessageConstant.INVALID_TYPE_3_FUNCTION_NULL_FUNCTIONS, ex.getMessage());
    }

    @Test
    public void constructorDifferentOutputSizeOfFunctionsTest() {
        PdfDictionary type3Func = createMinimalPdfType3FunctionDict();
        type3Func.getAsArray(PdfName.Functions).getAsDictionary(0).put(PdfName.Range, new PdfArray(new double[] {-100, 100, -100, 100}));
        Exception ex = Assertions.assertThrows(PdfException.class, () -> new PdfType3Function(type3Func));
        Assertions.assertEquals(KernelExceptionMessageConstant.INVALID_TYPE_3_FUNCTION_FUNCTIONS_OUTPUT, ex.getMessage());
    }

    @Test
    public void constructorDifferentOutputSizeFuncWithRangeTest() {
        PdfDictionary type3Func = createMinimalPdfType3FunctionDict();
        type3Func.put(PdfName.Range, new PdfArray(new double[] {-100, 100, -100, 100}));
        Exception ex = Assertions.assertThrows(PdfException.class, () -> new PdfType3Function(type3Func));
        Assertions.assertEquals(KernelExceptionMessageConstant.INVALID_TYPE_3_FUNCTION_FUNCTIONS_OUTPUT, ex.getMessage());
    }

    @Test
    public void constructorInvalidInputSizeOfFuncTest() {
        PdfDictionary type3Func = createMinimalPdfType3FunctionDict();
        IPdfFunctionFactory customFactory = (dict) -> new CustomPdfFunction((PdfDictionary)dict, 2, 1);
        Exception ex = Assertions.assertThrows(PdfException.class, () -> new PdfType3Function(type3Func, customFactory));
        Assertions.assertEquals(KernelExceptionMessageConstant.INVALID_TYPE_3_FUNCTION_FUNCTIONS_INPUT, ex.getMessage());
    }

    @Test
    public void constructorIgnoreNotDictFunctionsTest() {
        PdfDictionary type3FuncDict = createMinimalPdfType3FunctionDict();
        type3FuncDict.getAsArray(PdfName.Functions).add(new PdfNumber(1));
        PdfType3Function type3Function = new PdfType3Function(type3FuncDict);
        Assertions.assertEquals(2, type3Function.getFunctions().size());
    }

    @Test
    public void constructorInvalidFunctionTest() {
        PdfDictionary type3Func = createMinimalPdfType3FunctionDict();
        type3Func.getAsArray(PdfName.Functions).getAsDictionary(0).remove(PdfName.N);
        Exception ex = Assertions.assertThrows(PdfException.class, () -> new PdfType3Function(type3Func));
        Assertions.assertEquals(KernelExceptionMessageConstant.INVALID_TYPE_2_FUNCTION_N, ex.getMessage());
    }

    @Test
    public void constructorNullBoundsTest() {
        PdfDictionary type3Func = createMinimalPdfType3FunctionDict();
        type3Func.remove(PdfName.Bounds);
        Exception ex = Assertions.assertThrows(PdfException.class, () -> new PdfType3Function(type3Func));
        Assertions.assertEquals(KernelExceptionMessageConstant.INVALID_TYPE_3_FUNCTION_NULL_BOUNDS, ex.getMessage());
    }

    @Test
    public void constructorInvalidSizeOfBoundsTest() {
        PdfDictionary type3Func = createMinimalPdfType3FunctionDict();
        type3Func.put(PdfName.Bounds, new PdfArray());
        Exception ex = Assertions.assertThrows(PdfException.class, () -> new PdfType3Function(type3Func));
        Assertions.assertEquals(KernelExceptionMessageConstant.INVALID_TYPE_3_FUNCTION_NULL_BOUNDS, ex.getMessage());
    }

    @Test
    public void constructorInvalidBoundsLessThanDomainTest() {
        PdfDictionary type3Func = createMinimalPdfType3FunctionDict();
        type3Func.getAsArray(PdfName.Bounds).remove(0);
        type3Func.getAsArray(PdfName.Bounds).add(new PdfNumber(-1));
        Exception ex = Assertions.assertThrows(PdfException.class, () -> new PdfType3Function(type3Func));
        Assertions.assertEquals(KernelExceptionMessageConstant.INVALID_TYPE_3_FUNCTION_BOUNDS, ex.getMessage());
    }

    @Test
    public void constructorInvalidBoundsMoreThanDomainTest() {
        PdfDictionary type3Func = createMinimalPdfType3FunctionDict();
        type3Func.getAsArray(PdfName.Bounds).remove(0);
        type3Func.getAsArray(PdfName.Bounds).add(new PdfNumber(3));
        Exception ex = Assertions.assertThrows(PdfException.class, () -> new PdfType3Function(type3Func));
        Assertions.assertEquals(KernelExceptionMessageConstant.INVALID_TYPE_3_FUNCTION_BOUNDS, ex.getMessage());
    }

    @Test
    public void constructorInvalidBoundsLessThanPreviousTest() {
        PdfDictionary type3Func = new PdfDictionary();
        type3Func.put(PdfName.FunctionType, new PdfNumber(3));

        PdfArray domain = new PdfArray(new int[] {0, 1});
        type3Func.put(PdfName.Domain, domain);

        PdfArray functions = new PdfArray(PdfFunctionUtil.createMinimalPdfType2FunctionDict());
        functions.add(PdfFunctionUtil.createMinimalPdfType2FunctionDict());
        functions.add(PdfFunctionUtil.createMinimalPdfType2FunctionDict());
        type3Func.put(PdfName.Functions, functions);

        type3Func.put(PdfName.Bounds, new PdfArray(new double[] {1, 0.5}));

        type3Func.put(PdfName.Encode, new PdfArray(new double[] {0, 1, 0, 1, 0, 1}));

        Exception ex = Assertions.assertThrows(PdfException.class, () -> new PdfType3Function(type3Func));
        Assertions.assertEquals(KernelExceptionMessageConstant.INVALID_TYPE_3_FUNCTION_BOUNDS, ex.getMessage());
    }

    @Test
    public void constructorNullEncodeTest() {
        PdfDictionary type3Func = createMinimalPdfType3FunctionDict();
        type3Func.remove(PdfName.Encode);
        Exception ex = Assertions.assertThrows(PdfException.class, () -> new PdfType3Function(type3Func));
        Assertions.assertEquals(KernelExceptionMessageConstant.INVALID_TYPE_3_FUNCTION_NULL_ENCODE, ex.getMessage());
    }

    @Test
    public void constructorInvalidSizeOfEncodeTest() {
        PdfDictionary type3Func = createMinimalPdfType3FunctionDict();
        type3Func.put(PdfName.Encode, new PdfArray());
        Exception ex = Assertions.assertThrows(PdfException.class, () -> new PdfType3Function(type3Func));
        Assertions.assertEquals(KernelExceptionMessageConstant.INVALID_TYPE_3_FUNCTION_NULL_ENCODE, ex.getMessage());
    }

    @Test
    public void constructorInvalidDomainTest() {
        PdfDictionary type3Func = createMinimalPdfType3FunctionDict();
        type3Func.put(PdfName.Domain, new PdfArray(new double[] {1}));
        Exception ex = Assertions.assertThrows(PdfException.class, () -> new PdfType3Function(type3Func));
        Assertions.assertEquals(KernelExceptionMessageConstant.INVALID_TYPE_3_FUNCTION_DOMAIN, ex.getMessage());
    }

    @Test
    public void getOutputSizeNullRangeTest() {
        PdfDictionary type3FuncDict = createMinimalPdfType3FunctionDict();
        IPdfFunctionFactory customFactory = (dict) -> new CustomPdfFunction((PdfDictionary)dict, 1, 7);
        PdfType3Function type3Function = new PdfType3Function(type3FuncDict, customFactory);
        Assertions.assertEquals(7, type3Function.getOutputSize());
    }

    @Test
    public void getEncodeTest() {
        PdfDictionary type3FuncDict = createMinimalPdfType3FunctionDict();
        type3FuncDict.put(PdfName.Encode, new PdfArray(new double[] {0, 0.37, -1, 0}));
        PdfType3Function type3Function = new PdfType3Function(type3FuncDict);

        Assertions.assertArrayEquals(new double[] {0, 0.37, -1, 0}, type3Function.getEncode(), EPSILON);
    }

    @Test
    public void getBoundsTest() {
        PdfDictionary type3FuncDict = createMinimalPdfType3FunctionDict();
        type3FuncDict.put(PdfName.Bounds, new PdfArray(new double[] {0.789}));
        PdfType3Function type3Function = new PdfType3Function(type3FuncDict);

        Assertions.assertArrayEquals(new double[] {0.789}, type3Function.getBounds(), EPSILON);
    }

    @Test
    public void calculateInvalid2NumberInputTest() {
        PdfType3Function type3Func = new PdfType3Function(createMinimalPdfType3FunctionDict());

        Exception ex = Assertions.assertThrows(PdfException.class, () -> type3Func.calculate(new double[] {0, 1}));
        Assertions.assertEquals(KernelExceptionMessageConstant.INVALID_INPUT_FOR_TYPE_3_FUNCTION, ex.getMessage());
    }

    @Test
    public void calculateInvalidNullInputTest() {
        PdfType3Function type3Func = new PdfType3Function(createMinimalPdfType3FunctionDict());

        Exception ex = Assertions.assertThrows(PdfException.class, () -> type3Func.calculate(null));
        Assertions.assertEquals(KernelExceptionMessageConstant.INVALID_INPUT_FOR_TYPE_3_FUNCTION, ex.getMessage());
    }

    @Test
    public void calculateInputClipTest() {
        PdfDictionary type3FuncDict = createMinimalPdfType3FunctionDict();
        PdfType3Function type3Function = new PdfType3Function(type3FuncDict);

        double[] output = type3Function.calculate(new double[] {-5});
        // input value was clipped to 0 from -5
        Assertions.assertArrayEquals(new double[] {0}, output, EPSILON);
    }

    @Test
    public void calculateDomainOnePointIntervalTest() {
        PdfDictionary type3FuncDict = createMinimalPdfType3FunctionDict();
        type3FuncDict.put(PdfName.Bounds, new PdfArray());
        type3FuncDict.getAsArray(PdfName.Functions).remove(1);
        type3FuncDict.put(PdfName.Domain, new PdfArray(new double[] {0.5, 0.5}));
        PdfType3Function type3Function = new PdfType3Function(type3FuncDict);

        double[] output = type3Function.calculate(new double[] {7});
        Assertions.assertArrayEquals(new double[] {0}, output, EPSILON);
    }

    @Test
    public void calculateInputClipByFuncTest() {
        PdfDictionary type3FuncDict = createMinimalPdfType3FunctionDict();
        type3FuncDict.getAsArray(PdfName.Functions).getAsDictionary(0).put(PdfName.Domain, new PdfArray(new double[] {2, 3}));
        PdfType3Function type3Function = new PdfType3Function(type3FuncDict);

        double[] output = type3Function.calculate(new double[] {0.1});
        // input value 0.1 was passed to first function with domain [2, 3], so value was clipped to 2 from 0.1
        Assertions.assertArrayEquals(new double[] {4}, output, EPSILON);
    }

    @Test
    public void calculateInputValueEqualBoundsTest() {
        PdfDictionary type3FuncDict = createMinimalPdfType3FunctionDict();
        type3FuncDict.getAsArray(PdfName.Functions).getAsDictionary(1).put(PdfName.C0, new PdfArray(new double[] {-3}));
        PdfType3Function type3Function = new PdfType3Function(type3FuncDict);

        double[] output = type3Function.calculate(new double[] {0.5});
        // Input value 0.5 was passed to second function.
        // Subdomain is [0.5, 1], encode is [0, 1], so value 0.5 was encoded to 0.
        Assertions.assertArrayEquals(new double[] {-3}, output, EPSILON);
    }

    @Test
    public void calculateInputValueNotEqualBoundsTest() {
        PdfDictionary type3FuncDict = createMinimalPdfType3FunctionDict();
        PdfType3Function type3Function = new PdfType3Function(type3FuncDict);

        double[] output = type3Function.calculate(new double[] {0.53});
        // Input value 0.53 was passed to second function.
        // Subdomain is [0.5, 1], encode is [0, 1], so value 0.53 was encoded to 0.06.
        Assertions.assertArrayEquals(new double[] {0.06}, output, EPSILON);
    }

    @Test
    public void calculateInputValueEqualDomainTest() {
        PdfDictionary type3FuncDict = createMinimalPdfType3FunctionDict();
        PdfType3Function type3Function = new PdfType3Function(type3FuncDict);

        double[] output = type3Function.calculate(new double[] {1});
        Assertions.assertArrayEquals(new double[] {1}, output, EPSILON);
    }

    @Test
    public void calculateWith3FunctionsTest() {
        PdfDictionary type3FuncDict = createMinimalPdfType3FunctionDict();

        PdfDictionary minimalType2Func = PdfFunctionUtil.createMinimalPdfType2FunctionDict();
        minimalType2Func.put(PdfName.N, new PdfNumber(3));
        type3FuncDict.getAsArray(PdfName.Functions).add(1, minimalType2Func);
        type3FuncDict.put(PdfName.Encode, new PdfArray(new double[] {0, 1, 0, 1, 0, 1}));
        type3FuncDict.put(PdfName.Bounds, new PdfArray(new double[] {0.5, 0.7}));
        PdfType3Function type3Function = new PdfType3Function(type3FuncDict);

        double[] output = type3Function.calculate(new double[] {0.52});
        // Input value 0.52 was passed to second function.
        // Subdomain is [0.5, 0.7], encode is [0, 1], so value 0.52 was encoded to 0.1.
        Assertions.assertArrayEquals(new double[] {0.001}, output, EPSILON);
    }

    @Test
    public void calculateReverseEncodingTest() {
        PdfDictionary type3FuncDict = createMinimalPdfType3FunctionDict();
        type3FuncDict.put(PdfName.Encode, new PdfArray(new double[] {0, 1, 1, 0}));
        PdfType3Function type3Function = new PdfType3Function(type3FuncDict);

        Assertions.assertArrayEquals(new double[] {0, 1, 1, 0}, type3Function.getEncode(), EPSILON);

        double[] output = type3Function.calculate(new double[] {1});
        // Input value 1 was passed to second function.
        // Subdomain is [0.5, 1], encode is [1, 0], so value 1 was encoded to 0.
        Assertions.assertArrayEquals(new double[] {0}, output, EPSILON);
    }

    @Test
    public void calculateOneFunctionTest() {
        PdfDictionary type3FuncDict = createMinimalPdfType3FunctionDict();
        type3FuncDict.put(PdfName.Bounds, new PdfArray());
        type3FuncDict.getAsArray(PdfName.Functions).remove(1);
        PdfType3Function type3Function = new PdfType3Function(type3FuncDict);

        double[] output = type3Function.calculate(new double[] {0.6});
        Assertions.assertArrayEquals(new double[] {0.36}, output, EPSILON);
    }

    @Test
    public void calculateBoundsEqualLeftDomainTest() {
        PdfDictionary type3FuncDict = createMinimalPdfType3FunctionDict();
        type3FuncDict.getAsArray(PdfName.Functions).getAsDictionary(0).put(PdfName.C0, new PdfArray(new double[] {-3}));
        type3FuncDict.getAsArray(PdfName.Functions).getAsDictionary(1).put(PdfName.C1, new PdfArray(new double[] {5}));
        type3FuncDict.put(PdfName.Bounds, new PdfArray(new double[] {0}));
        PdfType3Function type3Function = new PdfType3Function(type3FuncDict);

        double[] output = type3Function.calculate(new double[] {0});
        // first function was used
        Assertions.assertArrayEquals(new double[] {-3}, output, EPSILON);

        output = type3Function.calculate(new double[] {0.1});
        // second function was used
        Assertions.assertArrayEquals(new double[] {0.5}, output, EPSILON);

        output = type3Function.calculate(new double[] {1});
        // second function was used
        Assertions.assertArrayEquals(new double[] {5}, output, EPSILON);
    }

    @Test
    public void calculateBoundsEqualRightDomainTest() {
        PdfDictionary type3FuncDict = createMinimalPdfType3FunctionDict();
        type3FuncDict.getAsArray(PdfName.Functions).getAsDictionary(0).put(PdfName.C1, new PdfArray(new double[] {-3}));
        type3FuncDict.getAsArray(PdfName.Functions).getAsDictionary(1).put(PdfName.C0, new PdfArray(new double[] {5}));
        type3FuncDict.put(PdfName.Bounds, new PdfArray(new double[] {1}));
        PdfType3Function type3Function = new PdfType3Function(type3FuncDict);

        double[] output = type3Function.calculate(new double[] {0});
        // first function was used
        Assertions.assertArrayEquals(new double[] {0}, output, EPSILON);

        output = type3Function.calculate(new double[] {0.1});
        // first function was used
        Assertions.assertArrayEquals(new double[] {-0.03}, output, EPSILON);

        output = type3Function.calculate(new double[] {1});
        // second function was used
        Assertions.assertArrayEquals(new double[] {5}, output, EPSILON);
    }

    @Test
    public void calculateBoundsEqualLeftDomainWith3FuncTest() {
        PdfDictionary type3FuncDict = createMinimalPdfType3FunctionDict();
        type3FuncDict.getAsArray(PdfName.Functions).getAsDictionary(0).put(PdfName.C0, new PdfArray(new double[] {-3}));
        type3FuncDict.getAsArray(PdfName.Functions).getAsDictionary(1).put(PdfName.C1, new PdfArray(new double[] {5}));
        PdfDictionary minimalType2Func = PdfFunctionUtil.createMinimalPdfType2FunctionDict();
        minimalType2Func.put(PdfName.N, new PdfNumber(1));
        minimalType2Func.put(PdfName.C1, new PdfArray(new double[] {-2}));
        type3FuncDict.getAsArray(PdfName.Functions).add(minimalType2Func);
        type3FuncDict.put(PdfName.Bounds, new PdfArray(new double[] {0, 0.5}));
        type3FuncDict.put(PdfName.Encode, new PdfArray(new double[] {0, 1, 0, 1, 0, 1}));
        PdfType3Function type3Function = new PdfType3Function(type3FuncDict);

        double[] output = type3Function.calculate(new double[] {0});
        // first function was used
        Assertions.assertArrayEquals(new double[] {-3}, output, EPSILON);

        output = type3Function.calculate(new double[] {0.1});
        // second function was used
        Assertions.assertArrayEquals(new double[] {1}, output, EPSILON);

        output = type3Function.calculate(new double[] {0.6});
        // third function was used
        Assertions.assertArrayEquals(new double[] {-0.4}, output, EPSILON);

        output = type3Function.calculate(new double[] {1});
        // third function was used
        Assertions.assertArrayEquals(new double[] {-2}, output, EPSILON);
    }

    private static PdfDictionary createMinimalPdfType3FunctionDict() {
        PdfDictionary type3Func = new PdfDictionary();
        type3Func.put(PdfName.FunctionType, new PdfNumber(3));

        PdfArray domain = new PdfArray(new int[] {0, 1});
        type3Func.put(PdfName.Domain, domain);

        PdfArray functions = new PdfArray(PdfFunctionUtil.createMinimalPdfType2FunctionDict());
        PdfDictionary minimalType2Func = PdfFunctionUtil.createMinimalPdfType2FunctionDict();
        minimalType2Func.put(PdfName.N, new PdfNumber(1));
        functions.add(minimalType2Func);
        type3Func.put(PdfName.Functions, functions);

        type3Func.put(PdfName.Bounds, new PdfArray(new double[] {0.5}));

        type3Func.put(PdfName.Encode, new PdfArray(new double[] {0, 1, 0, 1}));

        return type3Func;
    }

    private static class CustomPdfFunction extends AbstractPdfFunction<PdfDictionary> {
        private final int inputSize;
        private final int outputSize;

        protected CustomPdfFunction(PdfDictionary pdfObject, int inputSize, int outputSize) {
            super(pdfObject);
            this.inputSize = inputSize;
            this.outputSize = outputSize;
        }

        @Override
        public int getInputSize() {
            return inputSize;
        }

        @Override
        public int getOutputSize() {
            return outputSize;
        }

        @Override
        public double[] calculate(double[] input) {
            return new double[0];
        }

        @Override
        protected boolean isWrappedObjectMustBeIndirect() {
            return false;
        }
    }
}
