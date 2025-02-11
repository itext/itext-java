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
package com.itextpdf.commons.actions.producer;

import com.itextpdf.commons.actions.confirmations.ConfirmedEventWrapper;
import com.itextpdf.commons.actions.data.ProductData;
import com.itextpdf.commons.ecosystem.ITextTestEvent;
import com.itextpdf.commons.actions.sequence.SequenceId;
import com.itextpdf.test.ExtendedITextTest;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class CopyrightToPlaceholderPopulatorTest extends ExtendedITextTest {
    private CopyrightToPlaceholderPopulator populator = new CopyrightToPlaceholderPopulator();

    @Test
    public void oneEventTest() {
        List<ConfirmedEventWrapper> events = getEvents(2010);
        String result = populator.populate(events, null);
        Assertions.assertEquals("2010", result);
    }

    @Test
    public void severalEventsTest() {
        List<ConfirmedEventWrapper> events = getEvents(2007, 2030, 2020);
        String result = populator.populate(events, null);
        Assertions.assertEquals("2030", result);
    }

    @Test
    public void severalEventsWithSameYearTest() {
        List<ConfirmedEventWrapper> events = getEvents(2009, 1998, 2009, 1998);
        String result = populator.populate(events, null);
        Assertions.assertEquals("2009", result);
    }


    private List<ConfirmedEventWrapper> getEvents(int ... years) {
        List<ConfirmedEventWrapper> events = new ArrayList<>();
        for (int year : years) {
            final ProductData productData = new ProductData("iText Test", "itext-test", "25.3", 1900, year);
            events.add(new ConfirmedEventWrapper(
                    new ITextTestEvent(new SequenceId(), productData, null, "testing"),
                    "AGPL", "iText test product line"));
        }
        return events;
    }
}
