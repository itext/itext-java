package com.itextpdf.svg.renderers;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.svg.SvgTagConstants;
import com.itextpdf.svg.renderers.impl.AbstractSvgNodeRenderer;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class TransformationApplicationTest {

    @Test
    public void normalDrawTest() {
        // translates to "1 0 0 1 10 0 cm" followed by a newline
        byte[] expected = new byte[] {
                49, 32, 48, 32, 48, 32, 49, 32, 49, 48, 32, 48, 32, 99, 109, 10
        };

        ISvgNodeRenderer nodeRenderer = new AbstractSvgNodeRenderer() {

            @Override
            protected void doDraw(SvgDrawContext context) {
                // do nothing
            }
        };

        Map<String, String> attributeMap = new HashMap<>();
        attributeMap.put(SvgTagConstants.TRANSFORM, "translate(10)");
        nodeRenderer.setAttributesAndStyles(attributeMap);

        SvgDrawContext context = new SvgDrawContext();

        PdfDocument document = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfCanvas canvas = new PdfCanvas(document.addNewPage());
        context.pushCanvas(canvas);

        nodeRenderer.draw(context);

        byte[] actual = canvas.getContentStream().getBytes(true);

        Assert.assertArrayEquals(expected, actual);
    }
}