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
package com.itextpdf.barcodes;

import com.itextpdf.test.ExtendedITextTest;

import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Timeout;

@Tag("UnitTest")
public class BarcodeMultiThreadingTest extends ExtendedITextTest {

    private static class DataMatrixThread extends Thread {
        @Override
        public void run() {
            BarcodeDataMatrix bc = new BarcodeDataMatrix();
            bc.setOptions(BarcodeDataMatrix.DM_AUTO);
            bc.setWidth(10);
            bc.setHeight(10);
            int result = bc.setCode("AB01");

            Assertions.assertEquals(BarcodeDataMatrix.DM_NO_ERROR, result);
        }
    }

    @Timeout(unit = TimeUnit.MILLISECONDS, value = 10000)
    @Test
    public void test() throws InterruptedException {
        Thread[] threads = new DataMatrixThread[20];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new DataMatrixThread();
            threads[i].start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
    }
}
