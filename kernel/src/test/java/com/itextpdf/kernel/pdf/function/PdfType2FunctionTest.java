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
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class PdfType2FunctionTest extends ExtendedITextTest {
    private final static double EPSILON = 10e-6;

    @Test
    public void constructorInvalidObjWithoutNTest() {
        PdfDictionary type2Func = PdfFunctionUtil.createMinimalPdfType2FunctionDict();
        type2Func.remove(PdfName.N);
        Exception ex = Assertions.assertThrows(PdfException.class, () -> new PdfType2Function(type2Func));
        Assertions.assertEquals(KernelExceptionMessageConstant.INVALID_TYPE_2_FUNCTION_N, ex.getMessage());
    }

    @Test
    public void constructorInvalidObjNNotNumberTest() {
        PdfDictionary type2Func = PdfFunctionUtil.createMinimalPdfType2FunctionDict();
        type2Func.put(PdfName.N, new PdfString("some text"));
        Exception ex = Assertions.assertThrows(PdfException.class, () -> new PdfType2Function(type2Func));
        Assertions.assertEquals(KernelExceptionMessageConstant.INVALID_TYPE_2_FUNCTION_N, ex.getMessage());
    }

    @Test
    public void constructorInvalidObjWithNonIntegerNTest() {
        PdfDictionary type2Func = PdfFunctionUtil.createMinimalPdfType2FunctionDict();
        type2Func.put(PdfName.N, new PdfNumber(2.3));
        final PdfArray domain = type2Func.getAsArray(PdfName.Domain);
        domain.add(0, new PdfNumber(-1));
        domain.remove(2);
        Exception ex = Assertions.assertThrows(PdfException.class, () -> new PdfType2Function(type2Func));
        Assertions.assertEquals(KernelExceptionMessageConstant.INVALID_TYPE_2_FUNCTION_N_NOT_INTEGER, ex.getMessage());
    }

    @Test
    public void constructorInvalidObjWithNegativeNTest() {
        PdfDictionary type2Func = PdfFunctionUtil.createMinimalPdfType2FunctionDict();
        type2Func.put(PdfName.N, new PdfNumber(-2));
        Exception ex = Assertions.assertThrows(PdfException.class, () -> new PdfType2Function(type2Func));
        Assertions.assertEquals(KernelExceptionMessageConstant.INVALID_TYPE_2_FUNCTION_N_NEGATIVE, ex.getMessage());
    }

    @Test
    public void constructorInvalidDomainTest() {
        PdfDictionary type2Func = PdfFunctionUtil.createMinimalPdfType2FunctionDict();
        type2Func.put(PdfName.Domain, new PdfArray(new double[] {1}));
        Exception ex = Assertions.assertThrows(PdfException.class, () -> new PdfType2Function(type2Func));
        Assertions.assertEquals(KernelExceptionMessageConstant.INVALID_TYPE_2_FUNCTION_DOMAIN, ex.getMessage());
    }

    @Test
    public void constructorCorrectDomainTest() {
        PdfDictionary type2Func = PdfFunctionUtil.createMinimalPdfType2FunctionDict();
        type2Func.put(PdfName.Domain, new PdfArray(new double[] {1, 2, 3, 4}));
        PdfType2Function type2Function = new PdfType2Function(type2Func);
        Assertions.assertEquals(2, type2Function.getInputSize());
    }

    @Test
    public void constructorInvalidCDifferentSizeTest() {
        PdfDictionary type2Func = PdfFunctionUtil.createMinimalPdfType2FunctionDict();
        type2Func.put(PdfName.C0, new PdfArray(new int[]{1, 2}));
        type2Func.put(PdfName.C1, new PdfArray(new int[]{3}));
        Exception ex = Assertions.assertThrows(PdfException.class, () -> new PdfType2Function(type2Func));
        Assertions.assertEquals(KernelExceptionMessageConstant.INVALID_TYPE_2_FUNCTION_OUTPUT_SIZE, ex.getMessage());
    }

    @Test
    public void constructorInvalidCAndRangeDifferentSizeTest() {
        PdfDictionary type2Func = PdfFunctionUtil.createMinimalPdfType2FunctionDict();
        type2Func.put(PdfName.C0, new PdfArray(new int[]{1, 2}));
        type2Func.put(PdfName.Range, new PdfArray(new int[] {1, 3}));
        Exception ex = Assertions.assertThrows(PdfException.class, () -> new PdfType2Function(type2Func));
        Assertions.assertEquals(KernelExceptionMessageConstant.INVALID_TYPE_2_FUNCTION_OUTPUT_SIZE, ex.getMessage());
    }

    @Test
    public void constructorWithRangeTest() {
        PdfDictionary type2FuncDict = PdfFunctionUtil.createMinimalPdfType2FunctionDict();
        type2FuncDict.put(PdfName.Range, new PdfArray(new int[] {1, 2, 3, 4, 5, 6}));
        PdfType2Function type2Func = new PdfType2Function(type2FuncDict);
        Assertions.assertArrayEquals(new double[] {0, 0, 0}, type2Func.getC0(), EPSILON);
        Assertions.assertArrayEquals(new double[] {1, 1, 1}, type2Func.getC1(), EPSILON);
        Assertions.assertEquals(2, type2Func.getN(), EPSILON);
    }

    @Test
    public void constructorMinimalTest() {
        PdfDictionary type2FuncDict = PdfFunctionUtil.createMinimalPdfType2FunctionDict();
        PdfType2Function type2Func = new PdfType2Function(type2FuncDict);
        Assertions.assertArrayEquals(new double[] {0}, type2Func.getC0(), EPSILON);
        Assertions.assertArrayEquals(new double[] {1}, type2Func.getC1(), EPSILON);
        Assertions.assertEquals(2, type2Func.getN(), EPSILON);
    }

    @Test
    public void constructorFullTest() {
        PdfDictionary type2FuncDict = PdfFunctionUtil.createMinimalPdfType2FunctionDict();
        type2FuncDict.put(PdfName.C0, new PdfArray(new int[] {0, 1}));
        type2FuncDict.put(PdfName.C1, new PdfArray(new int[] {1, 2}));

        PdfType2Function type2Func = new PdfType2Function(type2FuncDict);
        Assertions.assertEquals(2, type2Func.getN(), EPSILON);
        Assertions.assertArrayEquals(new double[] {0, 1}, type2Func.getC0(), EPSILON);
        Assertions.assertArrayEquals(new double[] {1, 2}, type2Func.getC1(), EPSILON);
    }

    @Test
    public void calculateInvalid2NumberInputTest() {
        PdfType2Function type2Func = new PdfType2Function(PdfFunctionUtil.createMinimalPdfType2FunctionDict());

        Exception ex = Assertions.assertThrows(PdfException.class, () -> type2Func.calculate(new double[] {0, 1}));
        Assertions.assertEquals(KernelExceptionMessageConstant.INVALID_INPUT_FOR_TYPE_2_FUNCTION, ex.getMessage());
    }

    @Test
    public void calculateInvalidNullInputTest() {
        PdfType2Function type2Func = new PdfType2Function(PdfFunctionUtil.createMinimalPdfType2FunctionDict());

        Exception ex = Assertions.assertThrows(PdfException.class, () -> type2Func.calculate(null));
        Assertions.assertEquals(KernelExceptionMessageConstant.INVALID_INPUT_FOR_TYPE_2_FUNCTION, ex.getMessage());
    }

    @Test
    public void calculateInputClipTest() {
        PdfDictionary type2FuncDict = PdfFunctionUtil.createMinimalPdfType2FunctionDict();
        type2FuncDict.put(PdfName.Domain, new PdfArray(new int[] {1, 3}));
        PdfType2Function type2Function = new PdfType2Function(type2FuncDict);

        double[] output = type2Function.calculate(new double[] {8});
        // input value was clipped to 3 from 8
        Assertions.assertArrayEquals(new double[] {9}, output, EPSILON);
    }

    @Test
    public void calculateTest() {
        PdfDictionary type2FuncDict = PdfFunctionUtil.createMinimalPdfType2FunctionDict();
        type2FuncDict.put(PdfName.Domain, new PdfArray(new int[] {1, 3}));
        type2FuncDict.put(PdfName.C0, new PdfArray(new int[] {0, 1}));
        type2FuncDict.put(PdfName.C1, new PdfArray(new int[] {0, -3}));

        PdfType2Function type2Function = new PdfType2Function(type2FuncDict);

        double[] output = type2Function.calculate(new double[] {2});
        Assertions.assertArrayEquals(new double[] {0, -15}, output, EPSILON);
    }

    @Test
    public void calculateWithoutC0Test() {
        PdfDictionary type2FuncDict = PdfFunctionUtil.createMinimalPdfType2FunctionDict();
        type2FuncDict.put(PdfName.Domain, new PdfArray(new int[] {1, 3}));
        type2FuncDict.put(PdfName.C1, new PdfArray(new int[] {0, -3}));

        PdfType2Function type2Function = new PdfType2Function(type2FuncDict);

        double[] output = type2Function.calculate(new double[] {2});
        Assertions.assertArrayEquals(new double[] {0, -12}, output, EPSILON);
    }

    @Test
    public void calculateClipOutputTest() {
        PdfDictionary type2FuncDict = PdfFunctionUtil.createMinimalPdfType2FunctionDict();
        type2FuncDict.put(PdfName.Domain, new PdfArray(new int[] {1, 3}));
        type2FuncDict.put(PdfName.Range, new PdfArray(new int[] {-4, -2}));

        PdfType2Function type2Function = new PdfType2Function(type2FuncDict);

        double[] output = type2Function.calculate(new double[] {2});
        // output value was clipped to -2 from 4
        Assertions.assertArrayEquals(new double[] {-2}, output, EPSILON);
    }
}
