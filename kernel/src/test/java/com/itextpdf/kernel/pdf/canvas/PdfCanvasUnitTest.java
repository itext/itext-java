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

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfResources;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

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
}
