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
                        , "product name", "1", 2000, 2025);
                doc.getFingerPrint().registerProduct(productData);
                AssertUtil.doesNotThrow(() -> doc.close());
            }
        }
    }
}
