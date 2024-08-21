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
package com.itextpdf.kernel.pdf.canvas;

import com.itextpdf.commons.datastructures.Tuple2;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfResources;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.kernel.validation.IValidationChecker;
import com.itextpdf.kernel.validation.ValidationContainer;
import com.itextpdf.kernel.validation.ValidationType;
import com.itextpdf.kernel.validation.context.CanvasBmcValidationContext;
import com.itextpdf.kernel.validation.context.ExtendedGStateValidationContext;
import com.itextpdf.kernel.validation.context.FontGlyphsGStateValidationContext;
import com.itextpdf.kernel.validation.IValidationContext;
import com.itextpdf.kernel.validation.context.RenderingIntentValidationContext;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import java.util.Stack;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("UnitTest")
public class PdfCanvasUnitTest extends ExtendedITextTest {

    @Test
    public void unbalancedSaveRestoreStateOperatorsUnexpectedRestoreTest() {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfStream pdfStream = new PdfStream();
        PdfResources pdfResources = new PdfResources();
        PdfCanvas pdfCanvas = new PdfCanvas(pdfStream, pdfResources, pdfDocument);
        Assertions.assertTrue(pdfCanvas.gsStack.isEmpty());
        Exception exception = Assertions.assertThrows(PdfException.class,
                () -> pdfCanvas.restoreState());
        Assertions.assertEquals(KernelExceptionMessageConstant.UNBALANCED_SAVE_RESTORE_STATE_OPERATORS,
                exception.getMessage());
    }

    @Test
    public void unbalancedLayerOperatorUnexpectedEndTest() {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfStream pdfStream = new PdfStream();
        PdfResources pdfResources = new PdfResources();
        PdfCanvas pdfCanvas = new PdfCanvas(pdfStream, pdfResources, pdfDocument);
        Exception exception = Assertions.assertThrows(PdfException.class,
                () -> pdfCanvas.endLayer());
        Assertions.assertEquals(KernelExceptionMessageConstant.UNBALANCED_LAYER_OPERATORS, exception.getMessage());
    }

    @Test
    public void unbalancedBeginAndMarkedOperatorsUnexpectedEndTest() {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfStream pdfStream = new PdfStream();
        PdfResources pdfResources = new PdfResources();
        PdfCanvas pdfCanvas = new PdfCanvas(pdfStream, pdfResources, pdfDocument);
        Exception exception = Assertions.assertThrows(PdfException.class,
                () -> pdfCanvas.endMarkedContent());
        Assertions.assertEquals(KernelExceptionMessageConstant.UNBALANCED_BEGIN_END_MARKED_CONTENT_OPERATORS,
                exception.getMessage());
    }

    @Test
    public void fontAndSizeShouldBeSetBeforeShowTextTest01() {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfPage pdfPage = pdfDocument.addNewPage();
        PdfCanvas pdfCanvas = new PdfCanvas(pdfPage);
        Exception exception = Assertions.assertThrows(PdfException.class,
                () -> pdfCanvas.showText("text"));
        Assertions.assertEquals(KernelExceptionMessageConstant.FONT_AND_SIZE_MUST_BE_SET_BEFORE_WRITING_ANY_TEXT,
                exception.getMessage());
    }

    @Test
    public void fontAndSizeShouldBeSetBeforeShowTextTest02() {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfPage pdfPage = pdfDocument.addNewPage();
        PdfCanvas pdfCanvas = new PdfCanvas(pdfPage);
        PdfArray pdfArray = new PdfArray();
        Exception exception = Assertions.assertThrows(PdfException.class,
                () -> pdfCanvas.showText(pdfArray));
        Assertions.assertEquals(KernelExceptionMessageConstant.FONT_AND_SIZE_MUST_BE_SET_BEFORE_WRITING_ANY_TEXT,
                exception.getMessage());
    }

    @Test
    public void renderingIntentValidationTest() {
        try (PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            ValidationContainer container = new ValidationContainer();
            CustomValidationChecker checker = new CustomValidationChecker();
            container.addChecker(checker);
            doc.getDiContainer().register(ValidationContainer.class, container);
            Assertions.assertNull(checker.intent);
            final PdfPage pdfPage = doc.addNewPage();
            PdfCanvas pdfCanvas = new PdfCanvas(pdfPage);
            final PdfName intent = new PdfName("Test");
            pdfCanvas.setRenderingIntent(intent);
            Assertions.assertSame(intent, checker.intent);
        }
    }

    @Test
    public void bmcValidationTest() {
        try (PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            ValidationContainer container = new ValidationContainer();
            CustomValidationChecker checker = new CustomValidationChecker();
            container.addChecker(checker);
            doc.getDiContainer().register(ValidationContainer.class, container);
            Assertions.assertNull(checker.intent);
            final PdfPage pdfPage = doc.addNewPage();
            PdfCanvas pdfCanvas = new PdfCanvas(pdfPage);
            final PdfName tag = new PdfName("Test");
            pdfCanvas.beginMarkedContent(tag);
            Assertions.assertSame(tag, checker.currentBmc.getFirst());
            Assertions.assertNull(checker.currentBmc.getSecond());
            Assertions.assertEquals(1, checker.tagStructureStack.size());
        }
    }

    @Test
    public void fontGlyphsValidationTest() throws IOException {
        try (PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            ValidationContainer container = new ValidationContainer();
            CustomValidationChecker checker = new CustomValidationChecker();
            container.addChecker(checker);
            doc.getDiContainer().register(ValidationContainer.class, container);
            Assertions.assertNull(checker.intent);
            final PdfPage pdfPage = doc.addNewPage();
            PdfCanvas pdfCanvas = new PdfCanvas(pdfPage);
            pdfCanvas.beginText();
            pdfCanvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.COURIER), 24);
            pdfCanvas.showText("Test");
            pdfCanvas.endText();
            Assertions.assertNotNull(checker.gState);
            Assertions.assertNotNull(checker.contentStream);
        }
    }

    @Test
    public void extendedGStateValidationTest() {
        try (PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            ValidationContainer container = new ValidationContainer();
            CustomValidationChecker checker = new CustomValidationChecker();
            container.addChecker(checker);
            doc.getDiContainer().register(ValidationContainer.class, container);
            Assertions.assertNull(checker.intent);
            final PdfPage pdfPage = doc.addNewPage();
            PdfCanvas pdfCanvas = new PdfCanvas(pdfPage);

            pdfCanvas.setExtGState(new PdfExtGState());
            Assertions.assertNotNull(checker.gState);
            Assertions.assertNotNull(checker.contentStream);
        }
    }

    private static class CustomValidationChecker implements IValidationChecker {
        public PdfName intent;
        public Stack<Tuple2<PdfName, PdfDictionary>> tagStructureStack;
        public Tuple2<PdfName, PdfDictionary> currentBmc;
        public PdfStream contentStream;
        public CanvasGraphicsState gState;

        @Override
        public void validate(IValidationContext validationContext) {
            if (validationContext.getType() == ValidationType.RENDERING_INTENT) {
                intent = ((RenderingIntentValidationContext) validationContext).getIntent();
            }
            if (validationContext.getType() == ValidationType.CANVAS_BEGIN_MARKED_CONTENT) {
                CanvasBmcValidationContext bmcContext = (CanvasBmcValidationContext) validationContext;
                tagStructureStack = bmcContext.getTagStructureStack();
                currentBmc = bmcContext.getCurrentBmc();
            }
            if (validationContext.getType() == ValidationType.EXTENDED_GRAPHICS_STATE) {
                ExtendedGStateValidationContext gContext = (ExtendedGStateValidationContext) validationContext;
                contentStream = gContext.getContentStream();
                gState = gContext.getGraphicsState();
            }
            if (validationContext.getType() == ValidationType.FONT_GLYPHS) {
                FontGlyphsGStateValidationContext glyphsContext = (FontGlyphsGStateValidationContext) validationContext;
                contentStream = glyphsContext.getContentStream();
                gState = glyphsContext.getGraphicsState();
            }
        }
    }
}
