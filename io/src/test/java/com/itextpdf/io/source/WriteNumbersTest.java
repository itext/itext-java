/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.io.source;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.io.util.DecimalFormatUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.UnitTest;

import java.nio.charset.StandardCharsets;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
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
            Assert.assertArrayEquals(message, expecteds, actuals);
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
            Assert.assertArrayEquals(message, expecteds, actuals);
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
            Assert.assertArrayEquals(message, expecteds, actuals);
        }
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.ATTEMPT_PROCESS_NAN))
    public void writeNanTest() {
        double d = Double.NaN;

        byte[] actuals = ByteUtils.getIsoBytes(d);
        byte[] expecteds = DecimalFormatUtil.formatNumber(0, "0.##").getBytes(StandardCharsets.ISO_8859_1);

        String message = "Expects: " + new String(expecteds) + ", actual: " + new String(actuals) + " \\\\ "+ d;
        Assert.assertArrayEquals(message, expecteds, actuals);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.ATTEMPT_PROCESS_NAN))
    public void writeNanHighPrecisionTest() {
        double d = Double.NaN;

        byte[] actuals = ByteUtils.getIsoBytes(d, null, true);
        byte[] expecteds = DecimalFormatUtil.formatNumber(0, "0.##").getBytes(StandardCharsets.ISO_8859_1);

        String message = "Expects: " + new String(expecteds) + ", actual: " + new String(actuals) + " \\\\ "+ d;
        Assert.assertArrayEquals(message, expecteds, actuals);
    }
}
