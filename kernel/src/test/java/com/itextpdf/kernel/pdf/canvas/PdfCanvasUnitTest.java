package com.itextpdf.kernel.pdf.canvas;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfResources;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class PdfCanvasUnitTest extends ExtendedITextTest {
    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void unbalancedSaveRestoreStateOperatorsUnexpectedRestoreTest() {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException.expectMessage(KernelExceptionMessageConstant.UNBALANCED_SAVE_RESTORE_STATE_OPERATORS);

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfStream pdfStream = new PdfStream();
        PdfResources pdfResources = new PdfResources();
        PdfCanvas pdfCanvas = new PdfCanvas(pdfStream, pdfResources, pdfDocument);
        Assert.assertTrue(pdfCanvas.gsStack.isEmpty());
        pdfCanvas.restoreState();
    }

    @Test
    public void unbalancedLayerOperatorUnexpectedEndTest() {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException.expectMessage(KernelExceptionMessageConstant.UNBALANCED_LAYER_OPERATORS);

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfStream pdfStream = new PdfStream();
        PdfResources pdfResources = new PdfResources();
        PdfCanvas pdfCanvas = new PdfCanvas(pdfStream, pdfResources, pdfDocument);
        pdfCanvas.endLayer();
    }

    @Test
    public void unbalancedBeginAndMarkedOperatorsUnexpectedEndTest() {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException
                .expectMessage(KernelExceptionMessageConstant.UNBALANCED_BEGIN_END_MARKED_CONTENT_OPERATORS);

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfStream pdfStream = new PdfStream();
        PdfResources pdfResources = new PdfResources();
        PdfCanvas pdfCanvas = new PdfCanvas(pdfStream, pdfResources, pdfDocument);
        pdfCanvas.endMarkedContent();
    }

    @Test
    public void fontAndSizeShouldBeSetBeforeShowTextTest01() {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException
                .expectMessage(KernelExceptionMessageConstant.FONT_AND_SIZE_MUST_BE_SET_BEFORE_WRITING_ANY_TEXT);

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfPage pdfPage = pdfDocument.addNewPage();
        PdfCanvas pdfCanvas = new PdfCanvas(pdfPage);
        pdfCanvas.showText("text");
    }

    @Test
    public void fontAndSizeShouldBeSetBeforeShowTextTest02() {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException
                .expectMessage(KernelExceptionMessageConstant.FONT_AND_SIZE_MUST_BE_SET_BEFORE_WRITING_ANY_TEXT);

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfPage pdfPage = pdfDocument.addNewPage();
        PdfCanvas pdfCanvas = new PdfCanvas(pdfPage);
        PdfArray pdfArray = new PdfArray();
        pdfCanvas.showText(pdfArray);
    }
}
