/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
package com.itextpdf.kernel.counter;

import com.itextpdf.kernel.counter.event.CoreEvent;
import com.itextpdf.kernel.counter.event.IEvent;
import com.itextpdf.kernel.counter.event.IMetaInfo;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class EventCounterHandlerTest extends ExtendedITextTest {

    @Test
    public void testCoreEvent() {
        final int EVENTS_COUNT = 100;
        IEventCounterFactory counterFactory = new SimpleEventCounterFactory(new ToLogCounter());
        EventCounterHandler.getInstance().register(counterFactory);
        MetaInfoCounter counter = new MetaInfoCounter();
        for (int i = 0; i < EVENTS_COUNT; ++i) {
            EventCounterHandler.getInstance().onEvent(CoreEvent.PROCESS, counter, getClass());
        }
        EventCounterHandler.getInstance().unregister(counterFactory);
        Assert.assertEquals(counter.events_count, EVENTS_COUNT);
    }

    private static class ToLogCounter extends EventCounter {
        @Override
        protected void onEvent(IEvent event, IMetaInfo metaInfo) {
            ((MetaInfoCounter)metaInfo).events_count++;
        }
    }

    @Test
    public void testDefaultCoreEvent() {
        final int EVENTS_COUNT = 10001;
        IEventCounterFactory counterFactory = new SimpleEventCounterFactory(new ToLogDefaultCounter());
        EventCounterHandler.getInstance().register(counterFactory);
        MetaInfoCounter counter = new MetaInfoCounter();
        for (int i = 0; i < EVENTS_COUNT; ++i) {
            EventCounterHandler.getInstance().onEvent(CoreEvent.PROCESS, counter, getClass());
        }
        EventCounterHandler.getInstance().unregister(counterFactory);
        Assert.assertEquals(counter.events_count, EVENTS_COUNT);
    }

    private static class MetaInfoCounter implements IMetaInfo {
        int events_count = 0;
    }

    private static class ToLogDefaultCounter extends DefaultEventCounter {
        @Override
        protected void onEvent(IEvent event, IMetaInfo metaInfo) {
            super.onEvent(event, metaInfo);
            ((MetaInfoCounter)metaInfo).events_count++;
        }
    }
}
