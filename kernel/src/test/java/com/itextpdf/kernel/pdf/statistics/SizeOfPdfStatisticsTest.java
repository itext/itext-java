package com.itextpdf.kernel.pdf.statistics;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.actions.EventManager;
import com.itextpdf.kernel.actions.IBaseEvent;
import com.itextpdf.kernel.actions.IBaseEventHandler;
import com.itextpdf.kernel.pdf.CountOutputStream;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Category(IntegrationTest.class)
public class SizeOfPdfStatisticsTest extends ExtendedITextTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/kernel/pdf/statistics/SizeOfPdfStatisticsTest/";

    private static SizeOfPdfStatisticsHandler handler = new SizeOfPdfStatisticsHandler();

    @Before
    public void registerHandler() {
        EventManager.getInstance().register(handler);
    }

    @After
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

        Assert.assertEquals(1, sizeOfPdfEvents.size());
        Assert.assertEquals(outputStream.getAmountOfWrittenBytes(), sizeOfPdfEvents.get(0).getAmountOfBytes());
    }

    @Test
    public void pdfDocumentWithWriterAndReaderTest() throws IOException {
        CountOutputStream outputStream = new CountOutputStream(new ByteArrayOutputStream());
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "document.pdf"),
                new PdfWriter(outputStream))) {
            document.addNewPage();
        }

        List<SizeOfPdfStatisticsEvent> sizeOfPdfEvents = handler.getSizeOfPdfEvents();

        Assert.assertEquals(1, sizeOfPdfEvents.size());
        Assert.assertEquals(outputStream.getAmountOfWrittenBytes(), sizeOfPdfEvents.get(0).getAmountOfBytes());
    }

    @Test
    public void pdfDocumentWithReaderTest() throws IOException {
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "document.pdf"))) {
            Assert.assertNotNull(document.getPage(1));
        }

        List<SizeOfPdfStatisticsEvent> sizeOfPdfEvents = handler.getSizeOfPdfEvents();

        Assert.assertTrue(sizeOfPdfEvents.isEmpty());
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

        Assert.assertEquals(3, sizeOfPdfEvents.size());
        Assert.assertEquals(outputStream1.getAmountOfWrittenBytes(), sizeOfPdfEvents.get(0).getAmountOfBytes());
        Assert.assertEquals(outputStream2.getAmountOfWrittenBytes(), sizeOfPdfEvents.get(1).getAmountOfBytes());
        Assert.assertEquals(outputStream3.getAmountOfWrittenBytes(), sizeOfPdfEvents.get(2).getAmountOfBytes());
    }

    private static class SizeOfPdfStatisticsHandler implements IBaseEventHandler {
        private List<SizeOfPdfStatisticsEvent> sizeOfPdfEvents = new ArrayList<>();

        @Override
        public void onEvent(IBaseEvent event) {
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
