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

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Category(IntegrationTest.class)
public class ProducerBuilderIntegrationTest extends ExtendedITextTest {

    private static String ITEXT_PRODUCER;

    private static final String MODIFIED_USING = "; modified using ";

    @BeforeClass
    public static void beforeClass() throws IOException {
        byte[] docBytes;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            try (PdfDocument doc = new PdfDocument(new PdfWriter(outputStream))) {
                doc.addNewPage();
            }
            docBytes = outputStream.toByteArray();
        }

        try (PdfDocument docReopen = new PdfDocument(new PdfReader(new ByteArrayInputStream(docBytes)))) {
            ITEXT_PRODUCER = docReopen.getDocumentInfo().getProducer();
        }
    }

    @Test
    public void modifiedByItextTest() throws IOException {
        byte[] docBytes;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            try (PdfDocument doc = new PdfDocument(new PdfWriter(outputStream))) {
                doc.getDocumentInfo().setProducer("someProducer");
            }
            docBytes = outputStream.toByteArray();
        }

        try (PdfDocument docReopen = new PdfDocument(new PdfReader(new ByteArrayInputStream(docBytes)))) {
            Assert.assertEquals("someProducer" + MODIFIED_USING + ITEXT_PRODUCER
                    , docReopen.getDocumentInfo().getProducer());
        }
    }

    @Test
    public void modifiedSecondTimeModifiedByItextTest() throws IOException {
        byte[] docBytes;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            try (PdfDocument doc = new PdfDocument(new PdfWriter(outputStream))) {
                doc.getDocumentInfo().setProducer("someProducer; modified using anotherProducer");
            }
            docBytes = outputStream.toByteArray();
        }

        try (PdfDocument docReopen = new PdfDocument(new PdfReader(new ByteArrayInputStream(docBytes)))) {
            Assert.assertEquals("someProducer; modified using anotherProducer" + MODIFIED_USING + ITEXT_PRODUCER
                    , docReopen.getDocumentInfo().getProducer());
        }
    }

    @Test
    public void createdByItextModifiedByItextTest() throws IOException {
        byte[] docBytes;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            try (PdfDocument doc = new PdfDocument(new PdfWriter(outputStream))) {
                doc.getDocumentInfo().setProducer(ITEXT_PRODUCER);
            }
            docBytes = outputStream.toByteArray();
        }

        try (PdfDocument docReopen = new PdfDocument(new PdfReader(new ByteArrayInputStream(docBytes)))) {
            Assert.assertEquals(ITEXT_PRODUCER, docReopen.getDocumentInfo().getProducer());
        }
    }

    @Test
    public void modifiedByItextSecondTimeModifiedByItextTest() throws IOException {
        byte[] docBytes;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            try (PdfDocument doc = new PdfDocument(new PdfWriter(outputStream))) {
                doc.getDocumentInfo().setProducer("someProducer" + MODIFIED_USING +ITEXT_PRODUCER);
            }
            docBytes = outputStream.toByteArray();
        }

        try (PdfDocument docReopen = new PdfDocument(new PdfReader(new ByteArrayInputStream(docBytes)))) {
            Assert.assertEquals("someProducer" + MODIFIED_USING + ITEXT_PRODUCER
                    , docReopen.getDocumentInfo().getProducer());
        }
    }

    @Test
    public void modifiedByItextSecondTimeModifiedThirdTimeModifiedByItextTest() throws IOException {
        byte[] docBytes;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            try (PdfDocument doc = new PdfDocument(new PdfWriter(outputStream))) {
                doc.getDocumentInfo().setProducer("someProducer" + MODIFIED_USING + ITEXT_PRODUCER + MODIFIED_USING
                        + "thirdProducer");
            }
            docBytes = outputStream.toByteArray();
        }

        try (PdfDocument docReopen = new PdfDocument(new PdfReader(new ByteArrayInputStream(docBytes)))) {
            Assert.assertEquals("someProducer" + MODIFIED_USING + ITEXT_PRODUCER + MODIFIED_USING
                    + "thirdProducer" + MODIFIED_USING + ITEXT_PRODUCER, docReopen.getDocumentInfo().getProducer());
        }
    }
}
