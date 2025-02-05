/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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
package com.itextpdf.io.source;

import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.io.util.DecimalFormatUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.nio.charset.StandardCharsets;
import java.util.Random;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class WriteNumbersTest extends ExtendedITextTest {

    public static double round(double value, int places) {
        return Math.round(value * Math.pow(10, places)) / Math.pow(10, places);
    }

    @Test
    public void writeNumber1Test() {
        Random rnd = new Random();
        for (int i = 0; i < 100000; i++) {
            double d = (double)rnd.nextInt(2120000000)/100000;
            d = round(d, 2);
            if (d < 1.02) {
                i--;
                continue;
            }
            byte[] actuals = ByteUtils.getIsoBytes(d);
            byte[] expecteds = DecimalFormatUtil.formatNumber(d, "0.##").getBytes(StandardCharsets.ISO_8859_1);
            String message = "Expects: " + new String(expecteds) + ", actual: " + new String(actuals) + " \\\\ "+ d;
            Assertions.assertArrayEquals(expecteds, actuals, message);
        }
    }

    @Test
    public void writeNumber2Test() {
        Random rnd = new Random();
        for (int i = 0; i < 100000; i++) {
            double d = (double)rnd.nextInt(1000000)/1000000;
            d = round(d, 5);
            if (Math.abs(d) < 0.000015) continue;
            byte[] actuals = ByteUtils.getIsoBytes(d);
            byte[] expecteds = DecimalFormatUtil.formatNumber(d, "0.#####").getBytes(StandardCharsets.ISO_8859_1);
            String message = "Expects: " + new String(expecteds) + ", actual: " + new String(actuals) + " \\\\ " + d;
            Assertions.assertArrayEquals(expecteds, actuals, message);
        }
    }

    @Test
    public void writeNumber3Test() {
        Random rnd = new Random();
        for (int i = 0; i < 100000; i++) {
            double d = rnd.nextDouble(); if (d < 32700) d*= 100000;
            d = round(d, 0);
            byte[] actuals = ByteUtils.getIsoBytes(d);
            byte[] expecteds = DecimalFormatUtil.formatNumber(d, "0").getBytes(StandardCharsets.ISO_8859_1);
            String message = "Expects: " + new String(expecteds) + ", actual: " + new String(actuals) + " \\\\ "+ d;
            Assertions.assertArrayEquals(expecteds, actuals, message);
        }
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = IoLogMessageConstant.ATTEMPT_PROCESS_NAN))
    public void writeNanTest() {
        double d = Double.NaN;

        byte[] actuals = ByteUtils.getIsoBytes(d);
        byte[] expecteds = DecimalFormatUtil.formatNumber(0, "0.##").getBytes(StandardCharsets.ISO_8859_1);

        String message = "Expects: " + new String(expecteds) + ", actual: " + new String(actuals) + " \\\\ "+ d;
        Assertions.assertArrayEquals(expecteds, actuals, message);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = IoLogMessageConstant.ATTEMPT_PROCESS_NAN))
    public void writeNanHighPrecisionTest() {
        double d = Double.NaN;

        byte[] actuals = ByteUtils.getIsoBytes(d, null, true);
        byte[] expecteds = DecimalFormatUtil.formatNumber(0, "0.##").getBytes(StandardCharsets.ISO_8859_1);

        String message = "Expects: " + new String(expecteds) + ", actual: " + new String(actuals) + " \\\\ "+ d;
        Assertions.assertArrayEquals(expecteds, actuals, message);
    }
}
