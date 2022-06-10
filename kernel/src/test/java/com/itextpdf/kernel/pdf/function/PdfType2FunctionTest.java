package com.itextpdf.kernel.pdf.function;

import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class PdfType2FunctionTest extends ExtendedITextTest {
    private final static double EPSILON = 10e-6;

    @Test
    public void constructorInvalidObjWithoutNTest() {
        PdfDictionary type2Func = PdfFunctionUtil.createMinimalPdfType2FunctionDict();
        type2Func.remove(PdfName.N);
        Exception ex = Assert.assertThrows(PdfException.class, () -> new PdfType2Function(type2Func));
        Assert.assertEquals(KernelExceptionMessageConstant.INVALID_TYPE_2_FUNCTION_N, ex.getMessage());
    }

    @Test
    public void constructorInvalidObjNNotNumberTest() {
        PdfDictionary type2Func = PdfFunctionUtil.createMinimalPdfType2FunctionDict();
        type2Func.put(PdfName.N, new PdfString("some text"));
        Exception ex = Assert.assertThrows(PdfException.class, () -> new PdfType2Function(type2Func));
        Assert.assertEquals(KernelExceptionMessageConstant.INVALID_TYPE_2_FUNCTION_N, ex.getMessage());
    }

    @Test
    public void constructorInvalidObjWithNonIntegerNTest() {
        PdfDictionary type2Func = PdfFunctionUtil.createMinimalPdfType2FunctionDict();
        type2Func.put(PdfName.N, new PdfNumber(2.3));
        final PdfArray domain = type2Func.getAsArray(PdfName.Domain);
        domain.add(0, new PdfNumber(-1));
        domain.remove(2);
        Exception ex = Assert.assertThrows(PdfException.class, () -> new PdfType2Function(type2Func));
        Assert.assertEquals(KernelExceptionMessageConstant.INVALID_TYPE_2_FUNCTION_N_NOT_INTEGER, ex.getMessage());
    }

    @Test
    public void constructorInvalidObjWithNegativeNTest() {
        PdfDictionary type2Func = PdfFunctionUtil.createMinimalPdfType2FunctionDict();
        type2Func.put(PdfName.N, new PdfNumber(-2));
        Exception ex = Assert.assertThrows(PdfException.class, () -> new PdfType2Function(type2Func));
        Assert.assertEquals(KernelExceptionMessageConstant.INVALID_TYPE_2_FUNCTION_N_NEGATIVE, ex.getMessage());
    }

    @Test
    public void constructorInvalidDomainTest() {
        PdfDictionary type2Func = PdfFunctionUtil.createMinimalPdfType2FunctionDict();
        type2Func.put(PdfName.Domain, new PdfArray(new double[] {1}));
        Exception ex = Assert.assertThrows(PdfException.class, () -> new PdfType2Function(type2Func));
        Assert.assertEquals(KernelExceptionMessageConstant.INVALID_TYPE_2_FUNCTION_DOMAIN, ex.getMessage());
    }

    @Test
    public void constructorCorrectDomainTest() {
        PdfDictionary type2Func = PdfFunctionUtil.createMinimalPdfType2FunctionDict();
        type2Func.put(PdfName.Domain, new PdfArray(new double[] {1, 2, 3, 4}));
        PdfType2Function type2Function = new PdfType2Function(type2Func);
        Assert.assertEquals(2, type2Function.getInputSize());
    }

    @Test
    public void constructorInvalidCDifferentSizeTest() {
        PdfDictionary type2Func = PdfFunctionUtil.createMinimalPdfType2FunctionDict();
        type2Func.put(PdfName.C0, new PdfArray(new int[]{1, 2}));
        type2Func.put(PdfName.C1, new PdfArray(new int[]{3}));
        Exception ex = Assert.assertThrows(PdfException.class, () -> new PdfType2Function(type2Func));
        Assert.assertEquals(KernelExceptionMessageConstant.INVALID_TYPE_2_FUNCTION_OUTPUT_SIZE, ex.getMessage());
    }

    @Test
    public void constructorInvalidCAndRangeDifferentSizeTest() {
        PdfDictionary type2Func = PdfFunctionUtil.createMinimalPdfType2FunctionDict();
        type2Func.put(PdfName.C0, new PdfArray(new int[]{1, 2}));
        type2Func.put(PdfName.Range, new PdfArray(new int[] {1, 3}));
        Exception ex = Assert.assertThrows(PdfException.class, () -> new PdfType2Function(type2Func));
        Assert.assertEquals(KernelExceptionMessageConstant.INVALID_TYPE_2_FUNCTION_OUTPUT_SIZE, ex.getMessage());
    }

    @Test
    public void constructorWithRangeTest() {
        PdfDictionary type2FuncDict = PdfFunctionUtil.createMinimalPdfType2FunctionDict();
        type2FuncDict.put(PdfName.Range, new PdfArray(new int[] {1, 2, 3, 4, 5, 6}));
        PdfType2Function type2Func = new PdfType2Function(type2FuncDict);
        Assert.assertArrayEquals(new double[] {0, 0, 0}, type2Func.getC0(), EPSILON);
        Assert.assertArrayEquals(new double[] {1, 1, 1}, type2Func.getC1(), EPSILON);
        Assert.assertEquals(2, type2Func.getN(), EPSILON);
    }

    @Test
    public void constructorMinimalTest() {
        PdfDictionary type2FuncDict = PdfFunctionUtil.createMinimalPdfType2FunctionDict();
        PdfType2Function type2Func = new PdfType2Function(type2FuncDict);
        Assert.assertArrayEquals(new double[] {0}, type2Func.getC0(), EPSILON);
        Assert.assertArrayEquals(new double[] {1}, type2Func.getC1(), EPSILON);
        Assert.assertEquals(2, type2Func.getN(), EPSILON);
    }

    @Test
    public void constructorFullTest() {
        PdfDictionary type2FuncDict = PdfFunctionUtil.createMinimalPdfType2FunctionDict();
        type2FuncDict.put(PdfName.C0, new PdfArray(new int[] {0, 1}));
        type2FuncDict.put(PdfName.C1, new PdfArray(new int[] {1, 2}));

        PdfType2Function type2Func = new PdfType2Function(type2FuncDict);
        Assert.assertEquals(2, type2Func.getN(), EPSILON);
        Assert.assertArrayEquals(new double[] {0, 1}, type2Func.getC0(), EPSILON);
        Assert.assertArrayEquals(new double[] {1, 2}, type2Func.getC1(), EPSILON);
    }

    @Test
    public void calculateInvalid2NumberInputTest() {
        PdfType2Function type2Func = new PdfType2Function(PdfFunctionUtil.createMinimalPdfType2FunctionDict());

        Exception ex = Assert.assertThrows(PdfException.class, () -> type2Func.calculate(new double[] {0, 1}));
        Assert.assertEquals(KernelExceptionMessageConstant.INVALID_INPUT_FOR_TYPE_2_FUNCTION, ex.getMessage());
    }

    @Test
    public void calculateInvalidNullInputTest() {
        PdfType2Function type2Func = new PdfType2Function(PdfFunctionUtil.createMinimalPdfType2FunctionDict());

        Exception ex = Assert.assertThrows(PdfException.class, () -> type2Func.calculate(null));
        Assert.assertEquals(KernelExceptionMessageConstant.INVALID_INPUT_FOR_TYPE_2_FUNCTION, ex.getMessage());
    }

    @Test
    public void calculateInputClipTest() {
        PdfDictionary type2FuncDict = PdfFunctionUtil.createMinimalPdfType2FunctionDict();
        type2FuncDict.put(PdfName.Domain, new PdfArray(new int[] {1, 3}));
        PdfType2Function type2Function = new PdfType2Function(type2FuncDict);

        double[] output = type2Function.calculate(new double[] {8});
        // input value was clipped to 3 from 8
        Assert.assertArrayEquals(new double[] {9}, output, EPSILON);
    }

    @Test
    public void calculateTest() {
        PdfDictionary type2FuncDict = PdfFunctionUtil.createMinimalPdfType2FunctionDict();
        type2FuncDict.put(PdfName.Domain, new PdfArray(new int[] {1, 3}));
        type2FuncDict.put(PdfName.C0, new PdfArray(new int[] {0, 1}));
        type2FuncDict.put(PdfName.C1, new PdfArray(new int[] {0, -3}));

        PdfType2Function type2Function = new PdfType2Function(type2FuncDict);

        double[] output = type2Function.calculate(new double[] {2});
        Assert.assertArrayEquals(new double[] {0, -15}, output, EPSILON);
    }

    @Test
    public void calculateWithoutC0Test() {
        PdfDictionary type2FuncDict = PdfFunctionUtil.createMinimalPdfType2FunctionDict();
        type2FuncDict.put(PdfName.Domain, new PdfArray(new int[] {1, 3}));
        type2FuncDict.put(PdfName.C1, new PdfArray(new int[] {0, -3}));

        PdfType2Function type2Function = new PdfType2Function(type2FuncDict);

        double[] output = type2Function.calculate(new double[] {2});
        Assert.assertArrayEquals(new double[] {0, -12}, output, EPSILON);
    }

    @Test
    public void calculateClipOutputTest() {
        PdfDictionary type2FuncDict = PdfFunctionUtil.createMinimalPdfType2FunctionDict();
        type2FuncDict.put(PdfName.Domain, new PdfArray(new int[] {1, 3}));
        type2FuncDict.put(PdfName.Range, new PdfArray(new int[] {-4, -2}));

        PdfType2Function type2Function = new PdfType2Function(type2FuncDict);

        double[] output = type2Function.calculate(new double[] {2});
        // output value was clipped to -2 from 4
        Assert.assertArrayEquals(new double[] {-2}, output, EPSILON);
    }
}
