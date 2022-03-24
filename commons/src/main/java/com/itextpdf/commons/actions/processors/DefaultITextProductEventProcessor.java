/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2022 iText Group NV
    Authors: iText Software.

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
            .decode("WW91IGFyZSB1c2luZyBpVGV4dCB1bmRlciB0aGUgQUdQTC4KCklmIHRoaXMgaXMgeW9"
                    + "1ciBpbnRlbnRpb24sIHlvdSBoYXZlIHB1Ymxpc2hlZCB5b3VyIG93biBzb3VyY2UgY2"
                    + "9kZSBhcyBBR1BMIHNvZnR3YXJlIHRvby4KUGxlYXNlIGxldCB1cyBrbm93IHdoZXJlI"
                    + "HRvIGZpbmQgeW91ciBzb3VyY2UgY29kZSBieSBzZW5kaW5nIGEgbWFpbCB0byBhZ3Bs"
                    + "QGl0ZXh0cGRmLmNvbQpXZSdkIGJlIGhvbm9yZWQgdG8gYWRkIGl0IHRvIG91ciBsaXN"
                    + "0IG9mIEFHUEwgcHJvamVjdHMgYnVpbHQgb24gdG9wIG9mIGlUZXh0IDcKYW5kIHdlJ2"
                    + "xsIGV4cGxhaW4gaG93IHRvIHJlbW92ZSB0aGlzIG1lc3NhZ2UgZnJvbSB5b3VyIGVyc"
                    + "m9yIGxvZ3MuCgpJZiB0aGlzIHdhc24ndCB5b3VyIGludGVudGlvbiwgeW91IGFyZSBw"
                    + "cm9iYWJseSB1c2luZyBpVGV4dCBpbiBhIG5vbi1mcmVlIGVudmlyb25tZW50LgpJbiB"
                    + "0aGlzIGNhc2UsIHBsZWFzZSBjb250YWN0IHVzIGJ5IGZpbGxpbmcgb3V0IHRoaXMgZm"
                    + "9ybTogaHR0cDovL2l0ZXh0cGRmLmNvbS9zYWxlcwpJZiB5b3UgYXJlIGEgY3VzdG9tZ"
                    + "XIsIHdlJ2xsIGV4cGxhaW4gaG93IHRvIGluc3RhbGwgeW91ciBsaWNlbnNlIGtleSB0"
                    + "byBhdm9pZCB0aGlzIG1lc3NhZ2UuCklmIHlvdSdyZSBub3QgYSBjdXN0b21lciwgd2U"
                    + "nbGwgZXhwbGFpbiB0aGUgYmVuZWZpdHMgb2YgYmVjb21pbmcgYSBjdXN0b21lci4=");

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
