package com.itextpdf.io.source;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

import com.itextpdf.io.util.DecimalFormatUtil;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class WriteNumbersTest {

    public static double round(double value, int places) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    @Test
    public void WriteNumber1Test() {

        Random rnd = new Random();
        for (int i = 0; i < 100000; i++) {
            double d = (double)rnd.nextInt(2120000000)/100000;
            d = round(d, 2);
            if (d < 1.02) {
                i--;
                continue;
            }
            byte[] actuals = ByteUtils.getIsoBytes(d);
            byte[] expecteds = DecimalFormatUtil.formatNumber(d, "0.##").getBytes();
            String message = "Expects: " + new String(expecteds) + ", actual: " + new String(actuals) + " \\\\ "+ d;
            Assert.assertArrayEquals(message, expecteds, actuals);
        }
    }

    @Test
    public void WriteNumber2Test() {
        Random rnd = new Random();
        for (int i = 0; i < 100000; i++) {
            double d = (double)rnd.nextInt(1000000)/1000000;
            d = round(d, 5);
            if (Math.abs(d) < 0.000015) continue;
            byte[] actuals = ByteUtils.getIsoBytes(d);
            byte[] expecteds = DecimalFormatUtil.formatNumber(d, "0.#####").getBytes();
            String message = "Expects: " + new String(expecteds) + ", actual: " + new String(actuals) + " \\\\ " + d;
            Assert.assertArrayEquals(message, expecteds, actuals);
        }
    }

    @Test
    public void WriteNumber3Test() {
        Random rnd = new Random();
        for (int i = 0; i < 100000; i++) {
            double d = rnd.nextDouble(); if (d < 32700) d*= 100000;
            d = round(d, 0);
            byte[] actuals = ByteUtils.getIsoBytes(d);
            byte[] expecteds = DecimalFormatUtil.formatNumber(d, "0").getBytes();
            String message = "Expects: " + new String(expecteds) + ", actual: " + new String(actuals) + " \\\\ "+ d;
            Assert.assertArrayEquals(message, expecteds, actuals);
        }
    }
}
