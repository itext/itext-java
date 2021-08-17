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
package com.itextpdf.events.producer;

import com.itextpdf.events.confirmations.ConfirmedEventWrapper;
import com.itextpdf.events.data.ProductData;
import com.itextpdf.events.ecosystem.ITextTestEvent;
import com.itextpdf.events.exceptions.EventsExceptionMessageConstant;
import com.itextpdf.events.sequence.SequenceId;
import com.itextpdf.events.utils.MessageFormatUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class UsedProductsPlaceholderPopulatorTest extends ExtendedITextTest {
    private final UsedProductsPlaceholderPopulator populator = new UsedProductsPlaceholderPopulator();
    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void nullTest() {
        junitExpectedException.expect(IllegalArgumentException.class);
        junitExpectedException.expectMessage(MessageFormatUtil.format(EventsExceptionMessageConstant.INVALID_USAGE_FORMAT_REQUIRED, "usedProducts"));

        populator.populate(getEvents(1), null);
    }

    @Test
    public void plainTextTest() {
        String result = populator.populate(getEvents(0), "'plain text'");
        Assert.assertEquals("plain text", result);
    }

    @Test
    public void plainTextMultipleEventsMergedTest() {
        String result = populator.populate(getEvents(1, 2, 3, 4), "'plain text'");
        Assert.assertEquals("plain text", result);
    }

    @Test
    public void productNameOneEventTest() {
        String result = populator.populate(getEvents(0), "P");
        Assert.assertEquals("product0", result);
    }

    @Test
    public void productNameSeveralEventsTest() {
        String result = populator.populate(getEvents(0, 1, 2), "P");
        Assert.assertEquals("product0, product1, product2", result);
    }

    @Test
    public void sameProductsMergedTest() {
        String result = populator.populate(getEvents(0, 1, 0, 1, 2), "P");
        Assert.assertEquals("product0, product1, product2", result);
    }

    @Test
    public void versionOneEventTest() {
        String result = populator.populate(getEvents(1), "V");
        Assert.assertEquals("1.0", result);
    }

    @Test
    public void versionSeveralEventsTest() {
        String result = populator.populate(getEvents(1, 2, 3), "V");
        Assert.assertEquals("1.0, 2.0, 3.0", result);
    }

    @Test
    public void sameVersionsMergedTest() {
        String result = populator.populate(getEvents(1, 2, 1, 2, 3), "V");
        Assert.assertEquals("1.0, 2.0, 3.0", result);
    }

    @Test
    public void typeOneEventTest() {
        String result = populator.populate(getEvents(1), "T");
        Assert.assertEquals("type1", result);
    }

    @Test
    public void typeSeveralEventsTest() {
        String result = populator.populate(getEvents(1, 2, 3), "T");
        Assert.assertEquals("type1, type2, type3", result);
    }

    @Test
    public void sameTypesMergedTest() {
        String result = populator.populate(getEvents(1, 2, 1, 2, 3), "T");
        Assert.assertEquals("type1, type2, type3", result);
    }

    @Test
    public void complexFormatTest() {
        String result = populator.populate(getEvents(1, 2, 1, 2, 3), "'module:'P #V (T)");
        Assert.assertEquals("module:product1 #1.0 (type1), module:product2 #2.0 (type2), module:product3 #3.0 (type3)", result);
    }

    @Test
    public void invalidLetterFormatTest() {
        junitExpectedException.expect(IllegalArgumentException.class);
        junitExpectedException.expectMessage(MessageFormatUtil.format(EventsExceptionMessageConstant.PATTERN_CONTAINS_UNEXPECTED_CHARACTER, "X"));

        populator.populate(getEvents(1), "PVTX");
    }

    private List<ConfirmedEventWrapper> getEvents(int ... indexes) {
        List<ConfirmedEventWrapper> events = new ArrayList<>();

        for (int i : indexes) {
            final ProductData productData = new ProductData("product" + i, "module" + i, i + ".0", 1900, 2100);
            events.add(new ConfirmedEventWrapper(
                    new ITextTestEvent(new SequenceId(), productData, null, "testing" + i),
                    "type" + i, "iText product " + i));
        }

        return events;
    }
}
