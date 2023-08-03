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
package com.itextpdf.commons.actions;

import com.itextpdf.commons.actions.contexts.IMetaInfo;
import com.itextpdf.commons.actions.data.CommonsProductData;
import com.itextpdf.commons.actions.data.ProductData;
import com.itextpdf.commons.ecosystem.TestMetaInfo;
import com.itextpdf.commons.exceptions.CommonsExceptionMessageConstant;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class AbstractContextBasedITextEventTest extends ExtendedITextTest {

    @Test
    public void setMetaInfoTest() {
        BasicAbstractContextBasedITextEvent e =
                new BasicAbstractContextBasedITextEvent(CommonsProductData.getInstance(), null);

        TestMetaInfo metaInfoAfter = new TestMetaInfo("meta-info-after");
        e.setMetaInfo(metaInfoAfter);
        Assert.assertSame(metaInfoAfter, e.getMetaInfo());
    }

    @Test
    public void resetMetaInfoForbiddenTest() {
        TestMetaInfo metaInfoBefore = new TestMetaInfo("meta-info-before");
        TestMetaInfo metaInfoAfter = new TestMetaInfo("meta-info-after");
        BasicAbstractContextBasedITextEvent e =
                new BasicAbstractContextBasedITextEvent(CommonsProductData.getInstance(), metaInfoBefore);

        Assert.assertSame(metaInfoBefore, e.getMetaInfo());

        Assert.assertFalse(e.setMetaInfo(metaInfoAfter));
    }

    private static class BasicAbstractContextBasedITextEvent extends AbstractContextBasedITextEvent {
        protected BasicAbstractContextBasedITextEvent(ProductData productData,
                IMetaInfo metaInfo) {
            super(productData, metaInfo);
        }
    }
}
