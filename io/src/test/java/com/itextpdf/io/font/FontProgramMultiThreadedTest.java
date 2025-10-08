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
package com.itextpdf.io.font;

import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import java.util.HashSet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("IntegrationTest")
public class FontProgramMultiThreadedTest extends ExtendedITextTest {

    private static final String FONT = "./src/test/resources/com/itextpdf/io/font/otf/FreeSans.ttf";

    @Test
    public void fontSubsetTest() throws InterruptedException, IOException {
        TrueTypeFont fp = (TrueTypeFont) FontProgramFactory.createFont(FONT);

        TestThread[] threads = new TestThread[6];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new TestThread(fp);
            threads[i].start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
        for (TestThread thread : threads) {
            Assertions.assertFalse(thread.exceptionCaught, "Exception during font subsetting");
            Assertions.assertEquals(2956, thread.subsetSize);
        }
    }

    private static class TestThread extends Thread {

        private final TrueTypeFont fp;
        boolean exceptionCaught = false;
        int subsetSize = 0;

        TestThread(TrueTypeFont fp) {
            this.fp = fp;
        }

        @Override
        public void run() {
            for (int i = 0; i < 10; ++i) {
                byte[] bytes = null;
                try {
                     bytes = fp.subset(new HashSet<>(), true).getSecond();
                } catch (Exception e) {
                    exceptionCaught = true;
                }
                subsetSize = bytes == null ? 0 : bytes.length;
            }
        }
    }
}
