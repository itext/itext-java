/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2022 iText Group NV
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
package com.itextpdf.kernel.pdf.statistics;

import com.itextpdf.commons.actions.IEvent;
import com.itextpdf.commons.actions.IEventHandler;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.commons.actions.EventManager;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class NumberOfPagesStatisticsTest extends ExtendedITextTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/kernel/pdf/statistics/NumberOfPagesStatisticsTest/";

    private static NumberOfPagesStatisticsHandler handler = new NumberOfPagesStatisticsHandler();

    @Before
    public void registerHandler() {
        EventManager.getInstance().register(handler);
    }

    @After
    public void unregisterHandler() {
        EventManager.getInstance().unregister(handler);
        handler.clearNumberOfPagesEvents();
    }

    @Test
    public void pdfDocumentWithWriterTest() {
        try (PdfDocument document = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            document.addNewPage();
        }

        List<NumberOfPagesStatisticsEvent> numberOfPagesEvents = handler.getNumberOfPagesEvents();

        Assert.assertEquals(1, numberOfPagesEvents.size());
        Assert.assertEquals(1, numberOfPagesEvents.get(0).getNumberOfPages());
    }

    @Test
    public void pdfDocumentWithWriterAndReaderTest() throws IOException {
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "document.pdf"),
                new PdfWriter(new ByteArrayOutputStream()))) {
            document.addNewPage();
        }

        List<NumberOfPagesStatisticsEvent> numberOfPagesEvents = handler.getNumberOfPagesEvents();

        Assert.assertEquals(1, numberOfPagesEvents.size());
        Assert.assertEquals(2, numberOfPagesEvents.get(0).getNumberOfPages());
    }

    @Test
    public void pdfDocumentWithReaderTest() throws IOException {
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "document.pdf"))) {
            Assert.assertNotNull(document.getPage(1));
        }

        List<NumberOfPagesStatisticsEvent> numberOfPagesEvents = handler.getNumberOfPagesEvents();

        Assert.assertTrue(numberOfPagesEvents.isEmpty());
    }

    @Test
    public void severalPdfDocumentsTest() {
        try (PdfDocument document1 = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            for (int i = 0; i < 100; ++i) {
                document1.addNewPage();
            }
        }
        try (PdfDocument document2 = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            for (int i = 0; i < 10; ++i) {
                document2.addNewPage();
            }
        }
        try (PdfDocument document3 = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            document3.addNewPage();
        }

        List<NumberOfPagesStatisticsEvent> numberOfPagesEvents = handler.getNumberOfPagesEvents();

        Assert.assertEquals(3, numberOfPagesEvents.size());
        Assert.assertEquals(100, numberOfPagesEvents.get(0).getNumberOfPages());
        Assert.assertEquals(10, numberOfPagesEvents.get(1).getNumberOfPages());
        Assert.assertEquals(1, numberOfPagesEvents.get(2).getNumberOfPages());
    }

    private static class NumberOfPagesStatisticsHandler implements IEventHandler {
        private List<NumberOfPagesStatisticsEvent> numberOfPagesEvents = new ArrayList<>();

        @Override
        public void onEvent(IEvent event) {
            if (!(event instanceof NumberOfPagesStatisticsEvent)) {
                return;
            }
            numberOfPagesEvents.add((NumberOfPagesStatisticsEvent) event);
        }

        public List<NumberOfPagesStatisticsEvent> getNumberOfPagesEvents() {
            return numberOfPagesEvents;
        }

        public void clearNumberOfPagesEvents() {
            numberOfPagesEvents.clear();
        }
    }
}
