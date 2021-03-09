package com.itextpdf.layout.renderer;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.property.OverflowPropertyValue;
import com.itextpdf.layout.property.Property;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class BlockRendererUnitTest extends ExtendedITextTest {

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.PAGE_WAS_FLUSHED_ACTION_WILL_NOT_BE_PERFORMED))
    public void clippedAreaFlushedPageTest() {
        BlockRenderer blockRenderer = new DivRenderer(new Div());
        blockRenderer.setProperty(Property.OVERFLOW_X, OverflowPropertyValue.HIDDEN);
        blockRenderer.occupiedArea = new LayoutArea(1, new Rectangle(100, 100));

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        pdfDocument.addNewPage();
        PdfCanvas pdfCanvas = new PdfCanvas(pdfDocument.addNewPage());
        DrawContext context = new DrawContext(pdfDocument, pdfCanvas);

        pdfDocument.getPage(1).flush();

        blockRenderer.draw(context);

        // This test checks that there is log message and there is no NPE so assertions are not required
        Assert.assertTrue(true);
    }
}
