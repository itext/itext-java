package com.itextpdf.kernel.colors.gradients;

import com.itextpdf.kernel.colors.gradients.GradientColorStop.HintOffsetType;
import com.itextpdf.kernel.colors.gradients.GradientColorStop.OffsetType;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class GradientColorStopTest extends ExtendedITextTest {

    @Test
    public void normalizationTest() {
        GradientColorStop stopToTest = new GradientColorStop(new float[]{-0.5f, 1.5f, 0.5f, 0.5f}, 1.5, OffsetType.AUTO).setHint(1.5, HintOffsetType.NONE);
        Assert.assertArrayEquals(new float[]{0f, 1f, 0.5f}, stopToTest.getRgbArray(), 1e-10f);
        Assert.assertEquals(0, stopToTest.getOffset(), 1e-10);
        Assert.assertEquals(OffsetType.AUTO, stopToTest.getOffsetType());
        Assert.assertEquals(0, stopToTest.getHintOffset(), 1e-10);
        Assert.assertEquals(HintOffsetType.NONE, stopToTest.getHintOffsetType());
    }

    @Test
    public void cornerCasesTest() {
        GradientColorStop stopToTest = new GradientColorStop((float[]) null, 1.5, OffsetType.AUTO).setHint(1.5, HintOffsetType.NONE);
        Assert.assertArrayEquals(new float[]{0f, 0f, 0f}, stopToTest.getRgbArray(), 1e-10f);
        Assert.assertEquals(0, stopToTest.getOffset(), 1e-10);
        Assert.assertEquals(OffsetType.AUTO, stopToTest.getOffsetType());
        Assert.assertEquals(0, stopToTest.getHintOffset(), 1e-10);
        Assert.assertEquals(HintOffsetType.NONE, stopToTest.getHintOffsetType());
    }
}
