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
package com.itextpdf.commons.actions;

import com.itextpdf.commons.actions.confirmations.ConfirmEvent;
import com.itextpdf.commons.actions.confirmations.ConfirmedEventWrapper;
import com.itextpdf.commons.actions.sequence.SequenceId;
import com.itextpdf.commons.ecosystem.ITextTestEvent;
import com.itextpdf.test.ExtendedITextTest;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class ProductEventHandlerIntegrationTest extends ExtendedITextTest {
    private PrintStream outBackup;

    @BeforeEach
    public void initTest() {
        outBackup = System.out;
        ProductEventHandler.INSTANCE.clearProcessors();
    }

    @AfterEach
    public void afterEach() {
        System.setOut(outBackup);
        ProductProcessorFactoryKeeper.restoreDefaultProductProcessorFactory();
        ProductEventHandler.INSTANCE.clearProcessors();
    }

    @Test
    public void removeAGPLLoggingTest() {
        ByteArrayOutputStream testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));

        EventManager.acknowledgeAgplUsageDisableWarningMessage();
        for (int i = 0; i < 10001; i++) {

            ProductEventHandler handler = ProductEventHandler.INSTANCE;

            SequenceId sequenceId = new SequenceId();

            Assertions.assertTrue(handler.getEvents(sequenceId).isEmpty());
            ITextTestEvent event = new ITextTestEvent(sequenceId, null, "test-event",
                    ProductNameConstant.ITEXT_CORE);
            EventManager.getInstance().onEvent(event);

            ConfirmEvent confirmEvent = new ConfirmEvent(sequenceId, event);
            EventManager.getInstance().onEvent(confirmEvent);

            Assertions.assertEquals(1, handler.getEvents(sequenceId).size());
            Assertions.assertTrue(handler.getEvents(sequenceId).get(0) instanceof ConfirmedEventWrapper);
            Assertions.assertEquals(event, ((ConfirmedEventWrapper) handler.getEvents(sequenceId).get(0)).getEvent());
        }
        Assertions.assertEquals("", testOut.toString());
    }
}
