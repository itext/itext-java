/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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

import com.itextpdf.commons.actions.confirmations.ConfirmEvent;
import com.itextpdf.commons.actions.data.CommonsProductData;
import com.itextpdf.commons.actions.sequence.SequenceId;
import com.itextpdf.commons.ecosystem.ITextTestEvent;
import com.itextpdf.commons.exceptions.CommonsExceptionMessageConstant;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.LogLevelConstants;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class DefaultITextProductEventProcessorTest extends ExtendedITextTest {

    @Test
    public void constructorWithNullProductNameTest() {
        Exception e =
                Assertions.assertThrows(IllegalArgumentException.class, () -> new DefaultITextProductEventProcessor(null));
        Assertions.assertEquals(CommonsExceptionMessageConstant.PRODUCT_NAME_CAN_NOT_BE_NULL, e.getMessage());
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = "{0} you are probably {1}", logLevel = LogLevelConstants.INFO))
    public void messageIsLoggedTest() {
        TestDefaultITextProductEventProcessor testProcessor = new TestDefaultITextProductEventProcessor();
        ITextTestEvent e = new ITextTestEvent(new SequenceId(), CommonsProductData.getInstance(), null, "test event");
        AssertUtil.doesNotThrow(() -> testProcessor.onEvent(new ConfirmEvent(e)));
    }

    @Test
    @LogMessages(messages =
        @LogMessage(messageTemplate = "{0} you are probably {1}", logLevel = LogLevelConstants.INFO, count = 4)
    )
    public void messageIsLoggedThreeTimesTest() {
        int iterationsNumber = 15;
        // "1" correspond to expected iterations with log messages:
        // 1 0 0 0 0
        // 0 1 0 0 0
        // 1 0 0 0 1
        TestDefaultITextProductEventProcessor testProcessor = new TestDefaultITextProductEventProcessor();
        ITextTestEvent e = new ITextTestEvent(new SequenceId(), CommonsProductData.getInstance(), null, "test event");
        for (int i = 0; i < iterationsNumber; ++i) {
            AssertUtil.doesNotThrow(() -> testProcessor.onEvent(new ConfirmEvent(e)));
        }
    }

    private static class TestDefaultITextProductEventProcessor extends DefaultITextProductEventProcessor {

        public TestDefaultITextProductEventProcessor() {
            super("test product");
        }

        @Override
        long acquireRepeatLevel(int lvl) {
            switch (lvl) {
                case 0:
                    return 0;
                case 1:
                    return 5;
                case 2:
                    return 3;
            }
            return 0;
        }
    }
}
