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
package com.itextpdf.commons.actions.producer;

import com.itextpdf.commons.actions.AbstractProductProcessITextEvent;
import com.itextpdf.commons.actions.confirmations.ConfirmedEventWrapper;
import com.itextpdf.commons.actions.data.ProductData;
import com.itextpdf.commons.actions.sequence.SequenceId;
import com.itextpdf.commons.ecosystem.ITextTestEvent;
import com.itextpdf.commons.exceptions.CommonsExceptionMessageConstant;
import com.itextpdf.commons.logs.CommonsLogMessageConstant;
import com.itextpdf.commons.utils.DateTimeUtil;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.LogLevelConstants;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.UnitTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class ProducerBuilderTest extends ExtendedITextTest {

    @Test
    public void emptyEventsProducerLineTest() {
        Exception exception = Assert.assertThrows(IllegalArgumentException.class,
                () -> ProducerBuilder.modifyProducer(Collections.<AbstractProductProcessITextEvent>emptyList(), null));
        Assert.assertEquals(CommonsExceptionMessageConstant.NO_EVENTS_WERE_REGISTERED_FOR_THE_DOCUMENT,
                exception.getMessage());
    }

    @Test
    public void nullEventsProducerLineTest() {
        Exception exception = Assert.assertThrows(IllegalArgumentException.class,
                () -> ProducerBuilder.modifyProducer((List<AbstractProductProcessITextEvent>)null, null));
        Assert.assertEquals(CommonsExceptionMessageConstant.NO_EVENTS_WERE_REGISTERED_FOR_THE_DOCUMENT,
                exception.getMessage());
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

        Assert.assertEquals("Created at " + DateTimeUtil.format(DateTimeUtil.getCurrentTimeDate(), "yyyy"), newProducerLine);
    }

    @Test
    public void currentDateComplexFormatProducerLineTest() {
        List<ConfirmedEventWrapper> events = getEvents("Created at ${currentDate:yyyy, '{\\'yes::yes\\'', yyyy}", 1, 2, 3);
        String newProducerLine = ProducerBuilder.modifyProducer(events, null);
        String currentYear = DateTimeUtil.format(DateTimeUtil.getCurrentTimeDate(), "yyyy");

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
        Exception exception = Assert.assertThrows(IllegalArgumentException.class,
                () -> ProducerBuilder.modifyProducer(events, null));
        Assert.assertEquals(MessageFormatUtil.format(CommonsExceptionMessageConstant.INVALID_USAGE_FORMAT_REQUIRED, "currentDate"),
                exception.getMessage());
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
        Exception exception = Assert.assertThrows(IllegalArgumentException.class,
                () -> ProducerBuilder.modifyProducer(events, null));
        Assert.assertEquals(MessageFormatUtil.format(CommonsExceptionMessageConstant.INVALID_USAGE_FORMAT_REQUIRED, "usedProducts"),
                exception.getMessage());
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = CommonsLogMessageConstant.UNKNOWN_PLACEHOLDER_WAS_IGNORED, count = 3, logLevel = LogLevelConstants.INFO)
    })
    public void unknownPlaceHoldersTest() {
        List<ConfirmedEventWrapper> events =
                getEvents("${plchldr}|${plchldrWithParam:param}|${plchldrWithWeirdParam::$$:'''\\''}", 1, 2, 3);
        String newProducerLine = ProducerBuilder.modifyProducer(events, null);

        Assert.assertEquals("||", newProducerLine);
    }

    @Test
    public void modifiedUsingEqualsCurrentProducerTest() {
        List<ConfirmedEventWrapper> events = getEvents("some Author", 1, 2, 3);
        String newProducerLine = ProducerBuilder.modifyProducer(events
                , "Old producer; modified using some Author");

        Assert.assertEquals("Old producer; modified using some Author", newProducerLine);
    }

    @Test
    public void prevModifiedUsingEqualsCurrentProducerTest() {
        List<ConfirmedEventWrapper> events = getEvents("some Author", 1, 2, 3);
        String newProducerLine = ProducerBuilder.modifyProducer(events
                , "Old producer; modified using some Author; modified using another tool");

        Assert.assertEquals("Old producer; modified using some Author; modified using another tool; " +
                "modified using some Author", newProducerLine);
    }

    @Test
    public void severalModifiedUsingEqualsCurrentProducerTest() {
        List<ConfirmedEventWrapper> events = getEvents("some Author", 1, 2, 3);
        String newProducerLine = ProducerBuilder.modifyProducer(events
                , "Old producer; modified using some Author; modified using some Author");

        Assert.assertEquals("Old producer; modified using some Author; modified using some Author", newProducerLine);
    }

    @Test
    public void oldProducerEqualsCurrentProducerTest() {
        List<ConfirmedEventWrapper> events = getEvents("some Author", 1, 2, 3);
        String newProducerLine = ProducerBuilder.modifyProducer(events, "some Author");

        Assert.assertEquals("some Author", newProducerLine);
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
