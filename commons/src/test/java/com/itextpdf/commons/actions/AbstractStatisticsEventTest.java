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
package com.itextpdf.commons.actions;

import com.itextpdf.commons.actions.data.ProductData;
import com.itextpdf.commons.logs.CommonsLogMessageConstant;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.util.Collections;
import java.util.List;

@Tag("UnitTest")
public class AbstractStatisticsEventTest extends ExtendedITextTest {
    @Test
    public void constructorTest() {
        DummyStatisticsEvent dummyEvent =
                new DummyStatisticsEvent(new ProductData("public name", "product name", "version", 15, 3000));

        ProductData data = dummyEvent.getProductData();
        Assertions.assertEquals("public name", data.getPublicProductName());
        Assertions.assertEquals("product name", data.getProductName());
        Assertions.assertEquals("version", data.getVersion());
        Assertions.assertEquals(15, data.getSinceCopyrightYear());
        Assertions.assertEquals(3000, data.getToCopyrightYear());
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = CommonsLogMessageConstant.INVALID_STATISTICS_NAME))
    public void createStatisticsAggregatorFromNameTest() {
        DummyStatisticsEvent dummyEvent =
                new DummyStatisticsEvent(new ProductData("public name", "product name", "version", 15, 3000));

        Assertions.assertNull(dummyEvent.createStatisticsAggregatorFromName("statisticsName"));
    }

    static class DummyStatisticsEvent extends AbstractStatisticsEvent {

        DummyStatisticsEvent(ProductData data) {
            super(data);
        }

        @Override
        public List<String> getStatisticsNames() {
            return null;
        }
    }
}
