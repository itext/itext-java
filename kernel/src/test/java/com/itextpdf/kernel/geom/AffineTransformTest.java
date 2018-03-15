package com.itextpdf.kernel.geom;

import org.junit.Assert;
import org.junit.Test;

public class AffineTransformTest {

    @Test
    public void selfTest() {
        AffineTransform affineTransform = new AffineTransform();

        Assert.assertTrue(affineTransform.equals(affineTransform));
    }

    @Test
    public void nullTest() {
        AffineTransform affineTransform = new AffineTransform();

        Assert.assertFalse(affineTransform.equals(null));
    }

    @Test
    public void otherClassTest() {
        AffineTransform affineTransform = new AffineTransform();
        String string = "Test";

        Assert.assertFalse(affineTransform.equals(string));
    }

    @Test
    public void sameValuesTest() {
        AffineTransform affineTransform1 = new AffineTransform(0d, 1d, 2d, 3d, 4d, 5d);
        AffineTransform affineTransform2 = new AffineTransform(0d, 1d, 2d, 3d, 4d, 5d);
        int hash1 = affineTransform1.hashCode();
        int hash2 = affineTransform2.hashCode();

        Assert.assertFalse(affineTransform1 == affineTransform2);
        Assert.assertEquals(hash1, hash2);
        Assert.assertTrue(affineTransform1.equals(affineTransform2));
    }

    @Test
    public void differentValuesTest() {
        AffineTransform affineTransform1 = new AffineTransform(0d, 1d, 2d, 3d, 4d, 5d);
        AffineTransform affineTransform2 = new AffineTransform(5d, 4d, 3d, 2d, 1d, 1d);
        int hash1 = affineTransform1.hashCode();
        int hash2 = affineTransform2.hashCode();

        Assert.assertFalse(affineTransform1 == affineTransform2);
        Assert.assertNotEquals(hash1, hash2);
        Assert.assertFalse(affineTransform1.equals(affineTransform2));
    }
}