package com.itextpdf.svg.renderers;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.svg.SvgTagConstants;
import com.itextpdf.svg.renderers.impl.AbstractSvgNodeRenderer;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class TransformationApplicationTest {

    @Test
    public void normalDrawTest() {
        byte[] expected = "1 0 0 1 7.5 0 cm\n0 0 0 rg\nf\nh\n".getBytes(StandardCharsets.UTF_8);

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