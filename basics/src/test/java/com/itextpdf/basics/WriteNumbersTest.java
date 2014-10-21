package com.itextpdf.basics;

import com.itextpdf.basics.streams.OutputStream;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Random;

public class WriteNumbersTest {

    private static final DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.US);

    public static double round(double value, int places) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    @Test
    public void WriteNumber1Test() {
        Random rnd = new Random();
        DecimalFormat dn = new DecimalFormat("0.##", dfs);
        for (int i = 0; i < 100000; i++) {
            double d = (double)rnd.nextInt(2120000000)/100000;
            d = round(d, 2);
            if (d < 1.02) {
                i--;
                continue;
            }
            byte[] actuals = OutputStream.getIsoBytes(d);
            byte[] expecteds = dn.format(d).getBytes();
            String message = "Expects: " + new String(expecteds) + ", actual: " + new String(actuals) + " \\\\ "+ d;
            Assert.assertArrayEquals(message, expecteds, actuals);
        }
    }

    @Test
    public void WriteNumber2Test() {
        Random rnd = new Random();
        DecimalFormat dn = new DecimalFormat("0.#####", dfs);
        for (int i = 0; i < 100000; i++) {
            double d = (double)rnd.nextInt(1000000)/1000000;
            d = round(d, 5);
            if (Math.abs(d) < 0.000015) continue;
            byte[] actuals = OutputStream.getIsoBytes(d);
            byte[] expecteds = dn.format(d).getBytes();
            String message = "Expects: " + new String(expecteds) + ", actual: " + new String(actuals) + " \\\\ " + d;
            Assert.assertArrayEquals(message, expecteds, actuals);
        }
    }

    @Test
    public void WriteNumber3Test() {
        Random rnd = new Random();
        DecimalFormat dn = new DecimalFormat("0", dfs);
        for (int i = 0; i < 100000; i++) {
            double d = rnd.nextDouble(); if (d < 32700) d*= 100000;
            d = round(d, 0);
            byte[] actuals = OutputStream.getIsoBytes(d);
            byte[] expecteds = dn.format(d).getBytes();
            String message = "Expects: " + new String(expecteds) + ", actual: " + new String(actuals) + " \\\\ "+ d;
            Assert.assertArrayEquals(message, expecteds, actuals);
        }
    }

    @Test
    public void WriteNumber4Test() {

    }

    public void WriteNumber5Test() {

    }
}
