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
package com.itextpdf.kernel.pdf.statistics;

import com.itextpdf.commons.actions.IEvent;
import com.itextpdf.commons.actions.IEventHandler;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.commons.actions.EventManager;
import com.itextpdf.kernel.pdf.CountOutputStream;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class SizeOfPdfStatisticsTest extends ExtendedITextTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/kernel/pdf/statistics/SizeOfPdfStatisticsTest/";

    private static SizeOfPdfStatisticsHandler handler = new SizeOfPdfStatisticsHandler();

    @BeforeEach
    public void registerHandler() {
        EventManager.getInstance().register(handler);
    }

    @AfterEach
    public void unregisterHandler() {
        EventManager.getInstance().unregister(handler);
        handler.clearSizeOfPdfEvents();
    }

    @Test
    public void pdfDocumentWithWriterTest() {
        CountOutputStream outputStream = new CountOutputStream(new ByteArrayOutputStream());
        try (PdfDocument document = new PdfDocument(new PdfWriter(outputStream))) {
            document.addNewPage();
        }

        List<SizeOfPdfStatisticsEvent> sizeOfPdfEvents = handler.getSizeOfPdfEvents();

        Assertions.assertEquals(1, sizeOfPdfEvents.size());
        Assertions.assertEquals(outputStream.getAmountOfWrittenBytes(), sizeOfPdfEvents.get(0).getAmountOfBytes());
    }

    @Test
    public void pdfDocumentWithWriterAndReaderTest() throws IOException {
        CountOutputStream outputStream = new CountOutputStream(new ByteArrayOutputStream());
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "document.pdf"),
                new PdfWriter(outputStream))) {
            document.addNewPage();
        }

        List<SizeOfPdfStatisticsEvent> sizeOfPdfEvents = handler.getSizeOfPdfEvents();

        Assertions.assertEquals(1, sizeOfPdfEvents.size());
        Assertions.assertEquals(outputStream.getAmountOfWrittenBytes(), sizeOfPdfEvents.get(0).getAmountOfBytes());
    }

    @Test
    public void pdfDocumentWithReaderTest() throws IOException {
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "document.pdf"))) {
            Assertions.assertNotNull(document.getPage(1));
        }

        List<SizeOfPdfStatisticsEvent> sizeOfPdfEvents = handler.getSizeOfPdfEvents();

        Assertions.assertTrue(sizeOfPdfEvents.isEmpty());
    }

    @Test
    public void severalPdfDocumentsTest() {
        CountOutputStream outputStream1 = new CountOutputStream(new ByteArrayOutputStream());
        CountOutputStream outputStream2 = new CountOutputStream(new ByteArrayOutputStream());
        CountOutputStream outputStream3 = new CountOutputStream(new ByteArrayOutputStream());

        try (PdfDocument document1 = new PdfDocument(new PdfWriter(outputStream1))) {
            for (int i = 0; i < 100; ++i) {
                document1.addNewPage();
            }
        }
        try (PdfDocument document2 = new PdfDocument(new PdfWriter(outputStream2))) {
            for (int i = 0; i < 10; ++i) {
                document2.addNewPage();
            }
        }
        try (PdfDocument document3 = new PdfDocument(new PdfWriter(outputStream3))) {
            document3.addNewPage();
        }

        List<SizeOfPdfStatisticsEvent> sizeOfPdfEvents = handler.getSizeOfPdfEvents();

        Assertions.assertEquals(3, sizeOfPdfEvents.size());
        Assertions.assertEquals(outputStream1.getAmountOfWrittenBytes(), sizeOfPdfEvents.get(0).getAmountOfBytes());
        Assertions.assertEquals(outputStream2.getAmountOfWrittenBytes(), sizeOfPdfEvents.get(1).getAmountOfBytes());
        Assertions.assertEquals(outputStream3.getAmountOfWrittenBytes(), sizeOfPdfEvents.get(2).getAmountOfBytes());
    }

    private static class SizeOfPdfStatisticsHandler implements IEventHandler {
        private List<SizeOfPdfStatisticsEvent> sizeOfPdfEvents = new ArrayList<>();

        @Override
        public void onEvent(IEvent event) {
            if (!(event instanceof SizeOfPdfStatisticsEvent)) {
                return;
            }
            sizeOfPdfEvents.add((SizeOfPdfStatisticsEvent) event);
        }

        public List<SizeOfPdfStatisticsEvent> getSizeOfPdfEvents() {
            return sizeOfPdfEvents;
        }

        public void clearSizeOfPdfEvents() {
            sizeOfPdfEvents.clear();
        }
    }
}
