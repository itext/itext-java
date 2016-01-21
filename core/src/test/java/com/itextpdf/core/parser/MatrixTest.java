package com.itextpdf.core.parser;

import com.itextpdf.core.testutils.annotations.type.UnitTest;
import com.itextpdf.test.ExtendedITextTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class MatrixTest extends ExtendedITextTest {

    @Test
    public void testMultiply() throws Exception{
        Matrix m1 = new Matrix(2, 3, 4, 5, 6, 7);
        Matrix m2 = new Matrix(8, 9, 10, 11, 12, 13);
        Matrix shouldBe = new Matrix(46, 51, 82, 91, 130, 144);

        Matrix rslt = m1.multiply(m2);
        Assert.assertEquals(shouldBe, rslt);
    }

    @Test
    public void testDeterminant(){
        Matrix m = new Matrix(2, 3, 4, 5, 6, 7);
        Assert.assertEquals(-2f, m.getDeterminant(), .001f);
    }

}
