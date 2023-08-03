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
package com.itextpdf.kernel.actions.events;

import com.itextpdf.commons.actions.ProductNameConstant;
import com.itextpdf.commons.actions.confirmations.EventConfirmationType;
import com.itextpdf.commons.actions.sequence.SequenceId;
import com.itextpdf.kernel.actions.data.ITextCoreProductData;
import com.itextpdf.kernel.actions.ecosystem.TestMetaInfo;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class ITextCoreProductEventTest extends ExtendedITextTest {
    @Test
    public void openDocumentEventTest() {
        SequenceId sequenceId = new SequenceId();
        ITextCoreProductEvent event = ITextCoreProductEvent.createProcessPdfEvent(sequenceId, new TestMetaInfo("meta data"), EventConfirmationType.ON_CLOSE);

        Assert.assertEquals(ITextCoreProductEvent.PROCESS_PDF, event.getEventType());
        Assert.assertEquals(ProductNameConstant.ITEXT_CORE, event.getProductName());
        Assert.assertEquals(EventConfirmationType.ON_CLOSE, event.getConfirmationType());
        Assert.assertEquals(sequenceId, event.getSequenceId());

        Assert.assertEquals(ITextCoreProductData.getInstance().getPublicProductName(), event.getProductData().getPublicProductName());
        Assert.assertEquals(ITextCoreProductData.getInstance().getProductName(), event.getProductData().getProductName());
        Assert.assertEquals(ITextCoreProductData.getInstance().getVersion(), event.getProductData().getVersion());
        Assert.assertEquals(ITextCoreProductData.getInstance().getSinceCopyrightYear(), event.getProductData().getSinceCopyrightYear());
        Assert.assertEquals(ITextCoreProductData.getInstance().getToCopyrightYear(), event.getProductData().getToCopyrightYear());
    }
}
