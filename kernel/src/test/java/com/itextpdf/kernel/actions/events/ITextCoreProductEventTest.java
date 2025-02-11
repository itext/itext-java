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
package com.itextpdf.kernel.actions.events;

import com.itextpdf.commons.actions.ProductNameConstant;
import com.itextpdf.commons.actions.confirmations.EventConfirmationType;
import com.itextpdf.commons.actions.sequence.SequenceId;
import com.itextpdf.kernel.actions.data.ITextCoreProductData;
import com.itextpdf.kernel.actions.ecosystem.TestMetaInfo;
import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class ITextCoreProductEventTest extends ExtendedITextTest {
    @Test
    public void openDocumentEventTest() {
        SequenceId sequenceId = new SequenceId();
        ITextCoreProductEvent event = ITextCoreProductEvent.createProcessPdfEvent(sequenceId, new TestMetaInfo("meta data"), EventConfirmationType.ON_CLOSE);

        Assertions.assertEquals(ITextCoreProductEvent.PROCESS_PDF, event.getEventType());
        Assertions.assertEquals(ProductNameConstant.ITEXT_CORE, event.getProductName());
        Assertions.assertEquals(EventConfirmationType.ON_CLOSE, event.getConfirmationType());
        Assertions.assertEquals(sequenceId, event.getSequenceId());

        Assertions.assertEquals(ITextCoreProductData.getInstance().getPublicProductName(), event.getProductData().getPublicProductName());
        Assertions.assertEquals(ITextCoreProductData.getInstance().getProductName(), event.getProductData().getProductName());
        Assertions.assertEquals(ITextCoreProductData.getInstance().getVersion(), event.getProductData().getVersion());
        Assertions.assertEquals(ITextCoreProductData.getInstance().getSinceCopyrightYear(), event.getProductData().getSinceCopyrightYear());
        Assertions.assertEquals(ITextCoreProductData.getInstance().getToCopyrightYear(), event.getProductData().getToCopyrightYear());
    }
}
