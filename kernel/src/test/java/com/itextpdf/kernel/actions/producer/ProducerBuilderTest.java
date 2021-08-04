/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
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
package com.itextpdf.kernel.actions.producer;

import com.itextpdf.events.data.ProductData;
import com.itextpdf.io.util.DateTimeUtil;
import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.kernel.actions.ecosystem.ITextTestEvent;
import com.itextpdf.kernel.actions.events.ConfirmedEventWrapper;
import com.itextpdf.kernel.actions.sequence.SequenceId;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.logs.KernelLogMessageConstant;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.LogLevelConstants;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.UnitTest;

import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class ProducerBuilderTest extends ExtendedITextTest {

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void emptyEventsProducerLineTest() {
        junitExpectedException.expect(IllegalArgumentException.class);
        junitExpectedException.expectMessage(KernelExceptionMessageConstant.NO_EVENTS_WERE_REGISTERED_FOR_THE_DOCUMENT);

        ProducerBuilder.modifyProducer(null, null);
    }

    @Test
    public void plainTextNewProducerLineTest() {
        List<ConfirmedEventWrapper> events = getEvents("Plain Text", 1, 2, 3);
        String newProducerLine = ProducerBuilder.modifyProducer(events, null);

        Assert.assertEquals("Plain Text", newProducerLine);
    }

    @Test
    public void plainTextEmptyOldProducerLineTest() {
        List<ConfirmedEventWrapper> events = getEvents("Plain Text", 1, 2, 3);
        String newProducerLine = ProducerBuilder.modifyProducer(events, "");

        Assert.assertEquals("Plain Text", newProducerLine);
    }

    @Test
    public void plainTextExistingOldProducerLineTest() {
        List<ConfirmedEventWrapper> events = getEvents("Plain Text", 1, 2, 3);
        String newProducerLine = ProducerBuilder.modifyProducer(events, "Old producer");

        Assert.assertEquals("Old producer; modified using Plain Text", newProducerLine);
    }

    @Test
    public void plainTextExistingOldProducerWithModifiedPartLineTest() {
        List<ConfirmedEventWrapper> events = getEvents("New Author", 1, 2, 3);
        String newProducerLine = ProducerBuilder.modifyProducer(events, "Old producer; modified using Plain Text");

        Assert.assertEquals("Old producer; modified using Plain Text; modified using New Author", newProducerLine);
    }


    @Test
    public void copyrightSinceProducerLineTest() {
        List<ConfirmedEventWrapper> events = getEvents("Prod. since ${copyrightSince}", 1, 2, 3);
        String newProducerLine = ProducerBuilder.modifyProducer(events, null);

        Assert.assertEquals("Prod. since 1901", newProducerLine);
    }

    @Test
    public void copyrightToProducerLineTest() {
        List<ConfirmedEventWrapper> events = getEvents("All rights reserved, ${copyrightTo}", 1, 2, 3);
        String newProducerLine = ProducerBuilder.modifyProducer(events, null);

        Assert.assertEquals("All rights reserved, 2103", newProducerLine);
    }

    @Test
    public void currentDateProducerLineTest() {
        List<ConfirmedEventWrapper> events = getEvents("Created at ${currentDate:yyyy}", 1, 2, 3);
        String newProducerLine = ProducerBuilder.modifyProducer(events, null);

        Assert.assertEquals("Created at " + DateTimeUtil.formatDate(DateTimeUtil.getCurrentTimeDate(), "yyyy"), newProducerLine);
    }

    @Test
    public void currentDateComplexFormatProducerLineTest() {
        List<ConfirmedEventWrapper> events = getEvents("Created at ${currentDate:yyyy, '{\\'yes::yes\\'', yyyy}", 1, 2, 3);
        String newProducerLine = ProducerBuilder.modifyProducer(events, null);
        String currentYear = DateTimeUtil.formatDate(DateTimeUtil.getCurrentTimeDate(), "yyyy");

        Assert.assertEquals("Created at " + currentYear + ", {'yes::yes', " + currentYear, newProducerLine);
    }

    @Test
    public void currentDatePlaceholderFormatProducerLineTest() {
        List<ConfirmedEventWrapper> events = getEvents("Created at ${currentDate:'${currentDate'}", 1, 2, 3);
        String newProducerLine = ProducerBuilder.modifyProducer(events, null);

        Assert.assertEquals("Created at ${currentDate", newProducerLine);
    }

    @Test
    public void currentDateNoFormatProducerLineTest() {
        List<ConfirmedEventWrapper> events = getEvents("Created at ${currentDate}", 1, 2, 3);

        junitExpectedException.expect(IllegalArgumentException.class);
        junitExpectedException.expectMessage(MessageFormatUtil.format(KernelExceptionMessageConstant.INVALID_USAGE_FORMAT_REQUIRED, "currentDate"));

        ProducerBuilder.modifyProducer(events, null);
    }

    @Test
    public void currentDateEmptyFormatProducerLineTest() {
        List<ConfirmedEventWrapper> events = getEvents("Created at ${currentDate:}", 1, 2, 3);
        String newProducerLine = ProducerBuilder.modifyProducer(events, null);

        Assert.assertEquals("Created at ", newProducerLine);
    }

    @Test
    public void usedProductsProducerLineTest() {
        List<ConfirmedEventWrapper> events = getEvents("Used products: ${usedProducts:P #V (T 'version')}", 1, 2, 3);
        String newProducerLine = ProducerBuilder.modifyProducer(events, null);

        Assert.assertEquals(
                "Used products: product1 #1.0 (type1 version), product2 #2.0 (type2 version), product3 #3.0 (type3 version)",
                newProducerLine);
    }

    @Test
    public void usedProductsEmptyFormatProducerLineTest() {
        List<ConfirmedEventWrapper> events = getEvents("Used products: ${usedProducts}", 1, 2, 3);

        junitExpectedException.expect(IllegalArgumentException.class);
        junitExpectedException.expectMessage(MessageFormatUtil.format(KernelExceptionMessageConstant.INVALID_USAGE_FORMAT_REQUIRED, "usedProducts"));

        ProducerBuilder.modifyProducer(events, null);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = KernelLogMessageConstant.UNKNOWN_PLACEHOLDER_WAS_IGNORED, count = 3, logLevel = LogLevelConstants.INFO)
    })
    public void unknownPlaceHoldersTest() {
        List<ConfirmedEventWrapper> events =
                getEvents("${plchldr}|${plchldrWithParam:param}|${plchldrWithWeirdParam::$$:'''\\''}", 1, 2, 3);
        String newProducerLine = ProducerBuilder.modifyProducer(events, null);

        Assert.assertEquals("||", newProducerLine);
    }

    private List<ConfirmedEventWrapper> getEvents(String initialProducerLine, int ... indexes) {
        List<ConfirmedEventWrapper> events = new ArrayList<>();

        for (int ind = 0; ind < indexes.length; ind++) {
            int i = indexes[ind];
            final ProductData productData = new ProductData("product" + i, "module" + i, i + ".0", 1900 + i, 2100 + i);
            events.add(new ConfirmedEventWrapper(
                    new ITextTestEvent(new SequenceId(), productData, null, "testing" + i),
                    "type" + i, ind == 0 ? initialProducerLine : "iText product " + i));
        }

        return events;
    }
}
