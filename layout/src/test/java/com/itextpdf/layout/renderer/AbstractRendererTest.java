package com.itextpdf.layout.renderer;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.gradients.AbstractLinearGradientBuilder;
import com.itextpdf.kernel.colors.gradients.GradientColorStop;
import com.itextpdf.kernel.colors.gradients.GradientColorStop.OffsetType;
import com.itextpdf.kernel.colors.gradients.StrategyBasedLinearGradientBuilder;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.xobject.PdfXObject;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class AbstractRendererTest extends ExtendedITextTest {
    @Test
    public void createXObjectTest() {
        AbstractLinearGradientBuilder gradientBuilder = new StrategyBasedLinearGradientBuilder()
                .setGradientDirectionAsStrategy(StrategyBasedLinearGradientBuilder.GradientStrategy.TO_BOTTOM_LEFT)
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 0.5, OffsetType.RELATIVE));

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfXObject pdfXObject = AbstractRenderer.createXObject(gradientBuilder, new Rectangle(0, 0, 20, 20), pdfDocument);
        Assert.assertNotNull(pdfXObject.getPdfObject().get(PdfName.Resources));
    }

    @Test
    public void createXObjectWithNullLinearGradientTest() {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfXObject pdfXObject = AbstractRenderer.createXObject(null, new Rectangle(0, 0, 20, 20), pdfDocument);
        Assert.assertNull(pdfXObject.getPdfObject().get(PdfName.Resources));
    }

    @Test
    public void createXObjectWithInvalidColorTest() {
        AbstractLinearGradientBuilder gradientBuilder = new StrategyBasedLinearGradientBuilder();

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfXObject pdfXObject = AbstractRenderer.createXObject(gradientBuilder, new Rectangle(0, 0, 20, 20), pdfDocument);
        Assert.assertNull(pdfXObject.getPdfObject().get(PdfName.Resources));
    }
}
