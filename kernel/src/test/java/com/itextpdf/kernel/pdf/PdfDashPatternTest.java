package com.itextpdf.kernel.pdf;

import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class PdfDashPatternTest extends ExtendedITextTest {

    @Test
    public void constructorNoParamTest() {
        PdfDashPattern dashPattern = new PdfDashPattern();
        Assert.assertEquals(-1, dashPattern.getDash(), 0.0001);
        Assert.assertEquals(-1, dashPattern.getGap(), 0.0001);
        Assert.assertEquals(-1, dashPattern.getPhase(), 0.0001);
    }

    @Test
    public void constructorOneParamTest() {
        PdfDashPattern dashPattern = new PdfDashPattern(10);
        Assert.assertEquals(10, dashPattern.getDash(), 0.0001);
        Assert.assertEquals(-1, dashPattern.getGap(), 0.0001);
        Assert.assertEquals(-1, dashPattern.getPhase(), 0.0001);
    }

    @Test
    public void constructorTwoParamsTest() {
        PdfDashPattern dashPattern = new PdfDashPattern(10, 20);
        Assert.assertEquals(10, dashPattern.getDash(), 0.0001);
        Assert.assertEquals(20, dashPattern.getGap(), 0.0001);
        Assert.assertEquals(-1, dashPattern.getPhase(), 0.0001);
    }

    @Test
    public void constructorThreeParamsTest() {
        PdfDashPattern dashPattern = new PdfDashPattern(10, 20, 30);
        Assert.assertEquals(10, dashPattern.getDash(), 0.0001);
        Assert.assertEquals(20, dashPattern.getGap(), 0.0001);
        Assert.assertEquals(30, dashPattern.getPhase(), 0.0001);
    }
}
