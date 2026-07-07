/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
package com.itextpdf.layout;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.DocumentProperties;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.logs.LayoutLogMessageConstant;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.renderer.ParagraphRenderer;
import com.itextpdf.layout.utils.LayoutInfiniteLoopResolver;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.TestUtil;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.IOException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("IntegrationTest")
public class InfiniteLoopTest extends ExtendedITextTest {
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/layout/InfiniteLoopTest/";
    private static final String DESTINATION_FOLDER = TestUtil.getOutputPath() + "/layout/InfiniteLoopTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @AfterAll
    public static void afterClass() {
        // We need to clean this because it generates a very big pdf file which is not used
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void infiniteLoopWithPartialResultTest() throws IOException {
        String outFileName = DESTINATION_FOLDER + "infiniteLoopWithPartialResult.pdf";
        DocumentProperties documentProperties = new DocumentProperties();
        documentProperties.registerDependency(LayoutInfiniteLoopResolver.class, () -> new LayoutInfiniteLoopResolver(100));
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName), documentProperties)) {
            try (Document document = new Document(pdfDocument)) {
                Paragraph paragraph = new Paragraph() {
                    @Override
                    protected IRenderer makeNewRenderer() {
                        return new ReturningPartialRenderer(this);
                    }
                };
                Assertions.assertThrows(PdfException.class, () -> document.add(paragraph));
            }
        }
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, count = 15))
    public void veryBigLayoutThrowsTest() throws IOException {
        String outFileName = DESTINATION_FOLDER + "veryBigLayoutThrows.pdf";
        DocumentProperties documentProperties = new DocumentProperties();
        documentProperties.registerDependency(LayoutInfiniteLoopResolver.class, () -> new LayoutInfiniteLoopResolver(10));
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName), documentProperties)) {
            pdfDocument.setDefaultPageSize(PageSize.A10);
            try (Document document = new Document(pdfDocument)) {
                Div container = new Div();
                for (int i = 0; i < 16; ++i) {
                    PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.create(SOURCE_FOLDER + "Desert.jpg"));
                    Image image = new Image(xObject, 50);
                    container.add(image);
                }
                Assertions.assertThrows(PdfException.class, () -> document.add(container));
            }
        }
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, count = 15))
    public void notBigEnoughLayoutDoesntThrowTest() throws IOException {
        String outFileName = DESTINATION_FOLDER + "notBigEnoughLayoutDoesntThrow.pdf";
        DocumentProperties documentProperties = new DocumentProperties();
        documentProperties.registerDependency(LayoutInfiniteLoopResolver.class, () -> new LayoutInfiniteLoopResolver(10));
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName), documentProperties)) {
            pdfDocument.setDefaultPageSize(PageSize.A10);
            try (Document document = new Document(pdfDocument)) {
                Div container = new Div();
                for (int i = 0; i < 15; ++i) {
                    PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.create(SOURCE_FOLDER + "Desert.jpg"));
                    Image image = new Image(xObject, 50);
                    container.add(image);
                }
                Assertions.assertDoesNotThrow(() -> document.add(container));
            }
        }
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, count = 20))
    public void limitWithKeepTogetherEvenSmallerTest() throws IOException {
        String outFileName = DESTINATION_FOLDER + "limitWithKeepTogetherEvenSmaller.pdf";
        DocumentProperties documentProperties = new DocumentProperties();
        documentProperties.registerDependency(LayoutInfiniteLoopResolver.class, () -> new LayoutInfiniteLoopResolver(10));
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName), documentProperties)) {
            pdfDocument.setDefaultPageSize(PageSize.A10);
            try (Document document = new Document(pdfDocument)) {
                Div container = new Div();
                for (int i = 0; i < 11; ++i) {
                    PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.create(SOURCE_FOLDER + "Desert.jpg"));
                    Image image = new Image(xObject, 50);
                    image.setProperty(Property.KEEP_TOGETHER, Boolean.TRUE);
                    container.add(image);
                }
                Assertions.assertThrows(PdfException.class, () -> document.add(container));
            }
        }
    }

    private static class ReturningPartialRenderer extends ParagraphRenderer {
        public ReturningPartialRenderer(Paragraph modelElement) {
            super(modelElement);
        }

        @Override
        public LayoutResult layout(LayoutContext layoutContext) {
            LayoutResult layoutResult = super.layout(layoutContext);
            layoutResult.setStatus(LayoutResult.PARTIAL);
            layoutResult.setOverflowRenderer(this);
            layoutResult.setSplitRenderer(this);
            return layoutResult;
        }
    }
}
