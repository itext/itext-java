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
package com.itextpdf.kernel.pdf.event;

import com.itextpdf.commons.actions.EventManager;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;

@Tag("UnitTest")
public class PdfDocumentEventHandlingTest extends ExtendedITextTest {

    @Test
    public void simplePdfDocumentEventTest() {
        try (PdfDocument document = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            InsertPageHandler insertPageHandler = new InsertPageHandler();
            document.addEventHandler(PdfDocumentEvent.INSERT_PAGE, insertPageHandler);
            document.addNewPage();
            document.addNewPage();
            Assertions.assertEquals(2, insertPageHandler.getInsertedPagesCounter());
        }
    }

    @Test
    public void globallyRegisteredAbstractPdfDocumentEventHandlerTest() {
        InsertPageHandler insertPageHandler = new InsertPageHandler();
        insertPageHandler.addType(PdfDocumentEvent.INSERT_PAGE);
        EventManager.getInstance().register(insertPageHandler);
        try (PdfDocument document = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            document.addNewPage();
        }
        // Events with specified PDF document are ignored.
        Assertions.assertEquals(0, insertPageHandler.getInsertedPagesCounter());
        EventManager.getInstance().unregister(insertPageHandler);
    }

    @Test
    public void eventHandlerPerSeveralDocumentsTest() {
        try (PdfDocument document1 = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
             PdfDocument document2 = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
             PdfDocument document3 = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            InsertPageHandler insertPageHandler = new InsertPageHandler();
            document1.addEventHandler(PdfDocumentEvent.INSERT_PAGE, insertPageHandler);
            document2.addEventHandler(PdfDocumentEvent.INSERT_PAGE, insertPageHandler);

            document1.addNewPage();
            document2.addNewPage();
            document3.addNewPage();
            Assertions.assertEquals(2, insertPageHandler.getInsertedPagesCounter());

            document2.removeEventHandler(insertPageHandler);
            document2.addNewPage();
            Assertions.assertEquals(2, insertPageHandler.getInsertedPagesCounter());
        }
    }

    @Test
    public void noDocumentSpecifiedForEventButHandlerIsGloballyRegisteredTest() {
        InsertPageHandler insertPageHandler = new InsertPageHandler();
        insertPageHandler.addType(PdfDocumentEvent.INSERT_PAGE);
        EventManager.getInstance().register(insertPageHandler);
        EventManager.getInstance().onEvent(new PdfDocumentEvent(PdfDocumentEvent.INSERT_PAGE));
        EventManager.getInstance().unregister(insertPageHandler);
        Assertions.assertEquals(1, insertPageHandler.getInsertedPagesCounter());
    }

    private static class InsertPageHandler extends AbstractPdfDocumentEventHandler {
        private int insertedPagesCounter = 0;

        public int getInsertedPagesCounter() {
            return insertedPagesCounter;
        }

        @Override
        protected void onAcceptedEvent(AbstractPdfDocumentEvent event) {
            insertedPagesCounter++;
        }
    }
}
