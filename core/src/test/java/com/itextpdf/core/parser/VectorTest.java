package com.itextpdf.core.parser;

import com.itextpdf.core.testutils.annotations.type.UnitTest;
import com.itextpdf.test.ExtendedITextTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class VectorTest extends ExtendedITextTest {

    @Test
    public void testCrossVector() {
        Vector v = new Vector(2, 3, 4);
        Matrix m = new Matrix(5, 6, 7, 8, 9, 10);
        Vector shouldBe = new Vector(67, 76, 4);

        Vector rslt = v.cross(m);
        Assert.assertEquals(shouldBe, rslt);
    }

}
