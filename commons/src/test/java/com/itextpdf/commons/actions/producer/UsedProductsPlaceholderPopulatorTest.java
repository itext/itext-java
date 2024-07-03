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

import com.itextpdf.commons.actions.confirmations.ConfirmedEventWrapper;
import com.itextpdf.commons.actions.data.ProductData;
import com.itextpdf.commons.actions.sequence.SequenceId;
import com.itextpdf.commons.ecosystem.ITextTestEvent;
import com.itextpdf.commons.exceptions.CommonsExceptionMessageConstant;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.test.ExtendedITextTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class UsedProductsPlaceholderPopulatorTest extends ExtendedITextTest {

    private final UsedProductsPlaceholderPopulator populator = new UsedProductsPlaceholderPopulator();

    @Test
    public void nullTest() {
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> populator.populate(getEvents(1), null));
        Assertions.assertEquals(MessageFormatUtil.format(CommonsExceptionMessageConstant.INVALID_USAGE_FORMAT_REQUIRED, "usedProducts"),
                exception.getMessage());
    }

    @Test
    public void plainTextTest() {
        String result = populator.populate(getEvents(0), "'plain text'");
        Assertions.assertEquals("plain text", result);
    }

    @Test
    public void plainTextMultipleEventsMergedTest() {
        String result = populator.populate(getEvents(1, 2, 3, 4), "'plain text'");
        Assertions.assertEquals("plain text", result);
    }

    @Test
    public void productNameOneEventTest() {
        String result = populator.populate(getEvents(0), "P");
        Assertions.assertEquals("product0", result);
    }

    @Test
    public void productNameSeveralEventsTest() {
        String result = populator.populate(getEvents(0, 1, 2), "P");
        Assertions.assertEquals("product0, product1, product2", result);
    }

    @Test
    public void sameProductsMergedTest() {
        String result = populator.populate(getEvents(0, 1, 0, 1, 2), "P");
        Assertions.assertEquals("product0, product1, product2", result);
    }

    @Test
    public void versionOneEventTest() {
        String result = populator.populate(getEvents(1), "V");
        Assertions.assertEquals("1.0", result);
    }

    @Test
    public void versionSeveralEventsTest() {
        String result = populator.populate(getEvents(1, 2, 3), "V");
        Assertions.assertEquals("1.0, 2.0, 3.0", result);
    }

    @Test
    public void sameVersionsMergedTest() {
        String result = populator.populate(getEvents(1, 2, 1, 2, 3), "V");
        Assertions.assertEquals("1.0, 2.0, 3.0", result);
    }

    @Test
    public void typeOneEventTest() {
        String result = populator.populate(getEvents(1), "T");
        Assertions.assertEquals("type1", result);
    }

    @Test
    public void typeSeveralEventsTest() {
        String result = populator.populate(getEvents(1, 2, 3), "T");
        Assertions.assertEquals("type1, type2, type3", result);
    }

    @Test
    public void sameTypesMergedTest() {
        String result = populator.populate(getEvents(1, 2, 1, 2, 3), "T");
        Assertions.assertEquals("type1, type2, type3", result);
    }

    @Test
    public void complexFormatTest() {
        String result = populator.populate(getEvents(1, 2, 1, 2, 3), "'module:'P #V (T)");
        Assertions.assertEquals("module:product1 #1.0 (type1), module:product2 #2.0 (type2), module:product3 #3.0 (type3)", result);
    }

    @Test
    public void humanReadableNormalizationTest() {
        ProductData productData = new ProductData("public-name", "name", "1.0.0", 2020, 2021);
        ConfirmedEventWrapper event = new ConfirmedEventWrapper(
                new ITextTestEvent(new SequenceId(), productData, null, "testing"),
                "nonproduction", "iText product");
        String result = populator.populate(Arrays.asList(event), "'module:'P #V (T)");
        Assertions.assertEquals("module:public-name #1.0.0 (non-production)", result);
    }

    @Test
    public void invalidLetterFormatTest() {
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> populator.populate(getEvents(1), "PVTX"));
        Assertions.assertEquals(
                MessageFormatUtil.format(CommonsExceptionMessageConstant.PATTERN_CONTAINS_UNEXPECTED_CHARACTER, "X"),
                exception.getMessage());
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
