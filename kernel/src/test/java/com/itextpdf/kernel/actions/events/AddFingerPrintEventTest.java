package com.itextpdf.kernel.actions.events;

import com.itextpdf.commons.actions.data.ProductData;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.logs.KernelLogMessageConstant;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;

@Tag("UnitTest")
public class AddFingerPrintEventTest  extends ExtendedITextTest {

    @Test
    public void nullDocumentTest() {
        AddFingerPrintEvent addFingerPrintEvent = new AddFingerPrintEvent(null);
        AssertUtil.doesNotThrow(() -> addFingerPrintEvent.doAction());
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate =
            KernelLogMessageConstant.FINGERPRINT_DISABLED_BUT_NO_REQUIRED_LICENCE))
    public void disableFingerPrintAGPLTest() throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            try (PdfDocument doc = new PdfDocument(new PdfWriter(outputStream))) {
                doc.getFingerPrint().disableFingerPrint();
                AssertUtil.doesNotThrow(() -> doc.close());
            }
        }
    }

    @Test
    public void enabledFingerPrintAGPLTest() throws  java.io.IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            try (PdfDocument doc = new PdfDocument(new PdfWriter(outputStream))) {
                AssertUtil.doesNotThrow(() -> doc.close());
            }
        }
    }

    @Test
    public void disableFingerPrintNoProcessorForProductTest() throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            try (PdfDocument doc = new PdfDocument(new PdfWriter(outputStream))) {
                ProductData productData = new ProductData("public product name"
                        , "product name", "1", 2000, 2024);
                doc.getFingerPrint().registerProduct(productData);
                AssertUtil.doesNotThrow(() -> doc.close());
            }
        }
    }
}
