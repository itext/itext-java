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
package com.itextpdf.test;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

@Tag("UnitTest")
class AssertUtilTest extends ExtendedITextTest{

    @Test
    void assertPassedWithinTimeoutTestKeepsFailing() throws InterruptedException {
        AtomicInteger callCount = new AtomicInteger();
        Assertions.assertThrows(AssertionFailedError.class, () ->
        AssertUtil.assertPassedWithinTimeout(()->{
            callCount.getAndIncrement();
            Assertions.fail();
        }, Duration.ofMillis(500)));

        Assertions.assertTrue(callCount.get() > 1);
    }


    @Test
    void assertPassedWithinTimeoutTestFailsFirstTime() throws InterruptedException {
        AtomicInteger callCount = new AtomicInteger();
        Assertions.assertDoesNotThrow(() ->
                AssertUtil.assertPassedWithinTimeout(()->{
                    if (callCount.getAndIncrement() < 1) {
                        Assertions.fail();
                    }
                }, Duration.ofMillis(500)));

        Assertions.assertTrue(callCount.get() > 1);
    }

    @Test
    void assertPassedWithinTimeoutTestFailsFirstTimes() throws InterruptedException {
        AtomicInteger callCount = new AtomicInteger();
        Assertions.assertDoesNotThrow(() ->
                AssertUtil.assertPassedWithinTimeout(()->{
                    if (callCount.getAndIncrement() < 2) {
                        Assertions.fail();
                    }
                }, Duration.ofMillis(500)));

        Assertions.assertTrue(callCount.get() > 1);
    }
}