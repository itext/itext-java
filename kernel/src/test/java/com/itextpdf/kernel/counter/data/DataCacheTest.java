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
package com.itextpdf.kernel.counter.data;

import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.Arrays;
import java.util.List;

@Category(UnitTest.class)
public class DataCacheTest extends ExtendedITextTest {

    @Test
    public void queueTest() {
        testCache(new EventDataCacheQueueBased<String, SimpleData>(),
                Arrays.asList(new SimpleData("type1", 10), new SimpleData("type2", 4), new SimpleData("type3", 5), new SimpleData("type2", 12)),
                Arrays.asList(new SimpleData("type1", 10), new SimpleData("type2", 16), new SimpleData("type3", 5)));
    }

    @Test
    public void biggestCountTest() {
        testCache(new EventDataCacheComparatorBased<String, SimpleData>(new EventDataHandlerUtil.BiggerCountComparator<String, SimpleData>()),
                Arrays.asList(new SimpleData("type1", 10), new SimpleData("type2", 4), new SimpleData("type3", 5), new SimpleData("type2", 8)),
                Arrays.asList(new SimpleData("type2", 12), new SimpleData("type1", 10), new SimpleData("type3", 5)));
    }

    private static void testCache(IEventDataCache<String, SimpleData> cache, List<SimpleData> input, List<SimpleData> expectedOutput) {
        for (SimpleData event : input) {
            cache.put(event);
        }
        for (SimpleData expected : expectedOutput) {
            SimpleData actual = cache.retrieveNext();
            Assert.assertEquals(expected.getSignature(), actual.getSignature());
            Assert.assertEquals(expected.getCount(), actual.getCount());
        }
    }
    
    private static class SimpleData extends EventData<String> {

        public SimpleData(String signature, long count) {
            super(signature, count);
        }
    }
}
