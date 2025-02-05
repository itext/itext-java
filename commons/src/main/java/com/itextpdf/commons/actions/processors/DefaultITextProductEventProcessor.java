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
package com.itextpdf.commons.actions.processors;

import com.itextpdf.commons.actions.AbstractProductProcessITextEvent;
import com.itextpdf.commons.actions.confirmations.ConfirmEvent;
import com.itextpdf.commons.utils.Base64;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Defines a default strategy of product event processing.
 */
public class DefaultITextProductEventProcessor extends AbstractITextProductEventProcessor {

    static final byte[] MESSAGE_FOR_LOGGING = Base64
            .decode("WW91IGFyZSB1c2luZyBpVGV4dCB1bmRlciB0aGUgQUdQTC4KCklmIHRoaXMgaXMgeW9" +
                       "1ciBpbnRlbnRpb24sIHlvdSBoYXZlIHB1Ymxpc2hlZCB5b3VyIG93biBzb3VyY2UgY2" +
                       "9kZSBhcyBBR1BMIHNvZnR3YXJlIHRvby4KUGxlYXNlIGxldCB1cyBrbm93IHdoZXJlI" +
                       "HRvIGZpbmQgeW91ciBzb3VyY2UgY29kZSBieSBzZW5kaW5nIGEgbWFpbCB0byBhZ3Bs" +
                       "QGFwcnlzZS5jb20KV2UnZCBiZSBob25vcmVkIHRvIGFkZCBpdCB0byBvdXIgbGlzdCB" +
                       "vZiBBR1BMIHByb2plY3RzIGJ1aWx0IG9uIHRvcCBvZiBpVGV4dAphbmQgd2UnbGwgZX" +
                       "hwbGFpbiBob3cgdG8gcmVtb3ZlIHRoaXMgbWVzc2FnZSBmcm9tIHlvdXIgZXJyb3Igb" +
                       "G9ncy4KCklmIHRoaXMgd2Fzbid0IHlvdXIgaW50ZW50aW9uLCB5b3UgYXJlIHByb2Jh" +
                       "Ymx5IHVzaW5nIGlUZXh0IGluIGEgbm9uLWZyZWUgZW52aXJvbm1lbnQuCkluIHRoaXM" +
                       "gY2FzZSwgcGxlYXNlIGNvbnRhY3QgdXMgYnkgZmlsbGluZyBvdXQgdGhpcyBmb3JtOi" +
                       "BodHRwOi8vaXRleHRwZGYuY29tL3NhbGVzCklmIHlvdSBhcmUgYSBjdXN0b21lciwgd" +
                       "2UnbGwgZXhwbGFpbiBob3cgdG8gaW5zdGFsbCB5b3VyIGxpY2Vuc2Uga2V5IHRvIGF2" +
                       "b2lkIHRoaXMgbWVzc2FnZS4KSWYgeW91J3JlIG5vdCBhIGN1c3RvbWVyLCB3ZSdsbCB" +
                       "leHBsYWluIHRoZSBiZW5lZml0cyBvZiBiZWNvbWluZyBhIGN1c3RvbWVyLg==");

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultITextProductEventProcessor.class);

    private static final long[] REPEAT = {10000L, 5000L, 1000L};

    private static final int MAX_LVL = REPEAT.length - 1;

    private final Object lock = new Object();

    private final AtomicLong counter = new AtomicLong(0);

    private final AtomicLong level = new AtomicLong(0);

    private final AtomicLong repeatLevel;

    /**
     * Creates an instance of product event processor.
     *
     * @param productName is a product name
     */
    public DefaultITextProductEventProcessor(String productName) {
        super(productName);
        repeatLevel = new AtomicLong(acquireRepeatLevel((int) level.get()));
    }

    @Override
    public void onEvent(AbstractProductProcessITextEvent event) {
        if (!(event instanceof ConfirmEvent)) {
            return;
        }
        boolean isNeededToLogMessage = false;
        synchronized (lock) {
            if (counter.incrementAndGet() > repeatLevel.get()) {
                counter.set(0);
                if (level.incrementAndGet() > MAX_LVL) {
                    level.set(MAX_LVL);
                }
                repeatLevel.set(acquireRepeatLevel((int) level.get()));
                isNeededToLogMessage = true;
            }
        }

        if (isNeededToLogMessage) {
            String message = new String(MESSAGE_FOR_LOGGING, StandardCharsets.ISO_8859_1);
            LOGGER.info(message);
            // System out added with purpose. This is not a debug code
            System.out.println(message);
        }
    }

    @Override
    public String getUsageType() {
        return "AGPL";
    }

    long acquireRepeatLevel(int lvl) {
        return REPEAT[lvl];
    }
}
