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

import com.itextpdf.kernel.actions.data.ProductData;
import com.itextpdf.kernel.actions.ecosystem.ITextTestEvent;
import com.itextpdf.kernel.actions.events.ITextProductEventWrapper;
import com.itextpdf.kernel.actions.sequence.SequenceId;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class CopyrightToPlaceholderPopulatorTest extends ExtendedITextTest {
    private CopyrightToPlaceholderPopulator populator = new CopyrightToPlaceholderPopulator();

    @Test
    public void oneEventTest() {
        List<ITextProductEventWrapper> events = getEvents(2010);
        String result = populator.populate(events, null);
        Assert.assertEquals("2010", result);
    }

    @Test
    public void severalEventsTest() {
        List<ITextProductEventWrapper> events = getEvents(2007, 2030, 2020);
        String result = populator.populate(events, null);
        Assert.assertEquals("2030", result);
    }

    @Test
    public void severalEventsWithSameYearTest() {
        List<ITextProductEventWrapper> events = getEvents(2009, 1998, 2009, 1998);
        String result = populator.populate(events, null);
        Assert.assertEquals("2009", result);
    }


    private List<ITextProductEventWrapper> getEvents(int ... years) {
        List<ITextProductEventWrapper> events = new ArrayList<>();
        for (int year : years) {
            final ProductData productData = new ProductData("iText Test", "itext-test", "25.3", 1900, year);
            events.add(new ITextProductEventWrapper(
                    new ITextTestEvent(new SequenceId(), productData, null, "testing"),
                    "AGPL", "iText test product line"));
        }
        return events;
    }
}
