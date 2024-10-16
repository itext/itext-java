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
package com.itextpdf.kernel.actions;

import com.itextpdf.commons.actions.AbstractProductProcessITextEvent;
import com.itextpdf.commons.actions.EventManager;
import com.itextpdf.commons.actions.ProductNameConstant;
import com.itextpdf.kernel.actions.ecosystem.ITextTestEvent;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class ProductEventHandlerTest extends ExtendedITextTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/kernel/actions/";

    @Test
    public void documentIdBasedEventTest() throws IOException {
        ProductEventHandlerAccess handler = new ProductEventHandlerAccess();

        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "hello.pdf"))) {

            int alreadyRegisteredEvents = handler.publicGetEvents(document.getDocumentIdWrapper()).size();
            EventManager.getInstance().onEvent(new ITextTestEvent(document.getDocumentIdWrapper(), null, "test-event",
                    ProductNameConstant.ITEXT_CORE));

            Assertions.assertEquals(alreadyRegisteredEvents + 1, handler.publicGetEvents(document.getDocumentIdWrapper()).size());


            AbstractProductProcessITextEvent event = handler.publicGetEvents(document.getDocumentIdWrapper()).get(alreadyRegisteredEvents);
            Assertions.assertEquals(document.getDocumentIdWrapper(), event.getSequenceId());
            Assertions.assertEquals("test-event", event.getEventType());
            Assertions.assertEquals(ProductNameConstant.ITEXT_CORE, event.getProductName());
            Assertions.assertNotNull(event.getProductData());
        }
    }
}
