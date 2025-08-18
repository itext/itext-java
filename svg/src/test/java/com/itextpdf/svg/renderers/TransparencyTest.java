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
package com.itextpdf.svg.renderers;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfResources;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.styledxmlparser.resolver.resource.ResourceResolver;
import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.renderers.impl.AbstractSvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.CircleSvgNodeRenderer;
import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class TransparencyTest extends ExtendedITextTest {

    private static final PdfName DEFAULT_RESOURCE_NAME = new PdfName("Gs1");
    private static final PdfName FILL_OPAC = new PdfName("ca");
    private static final PdfName STROKE_OPAC = new PdfName("CA");

    @Test
    public void noOpacitySet() {
        // set compression to none, in case you want to write to disk and inspect the created document
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream(),
                new WriterProperties().setCompressionLevel(0)))) {
            SvgDrawContext sdc = setupDrawContext(pdfDocument);
            AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
            renderer.setAttribute(SvgConstants.Attributes.STROKE, "blue");

            renderer.draw(sdc);

            PdfCanvas cv = sdc.getCurrentCanvas();
            PdfResources resources = cv.getResources();
            Assertions.assertTrue(resources.getResourceNames().isEmpty());
        }
    }

    @Test
    public void strokeOpacitySetWithStroke() {
        // set compression to none, in case you want to write to disk and inspect the created document
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream(),
                new WriterProperties().setCompressionLevel(0)))) {
            SvgDrawContext sdc = setupDrawContext(pdfDocument);
            AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
            renderer.setAttribute(SvgConstants.Attributes.STROKE_OPACITY, "0.75");
            renderer.setAttribute(SvgConstants.Attributes.STROKE, "blue");

            renderer.draw(sdc);
            PdfCanvas cv = sdc.getCurrentCanvas();
            PdfResources resources = cv.getResources();

            Assertions.assertEquals(1, resources.getResourceNames().size());
            PdfDictionary resDic = (PdfDictionary) resources.getResourceObject(PdfName.ExtGState,
                    DEFAULT_RESOURCE_NAME);
            Assertions.assertEquals(1, resDic.size());
            Assertions.assertEquals(resDic.get(STROKE_OPAC), new PdfNumber(0.75));
        }
    }

    @Test
    public void strokeOpacitySetWithoutStroke() {
        // set compression to none, in case you want to write to disk and inspect the created document
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream(),
                new WriterProperties().setCompressionLevel(0)))) {
            SvgDrawContext sdc = setupDrawContext(pdfDocument);
            AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
            renderer.setAttribute(SvgConstants.Attributes.STROKE_OPACITY, "0.75");

            renderer.draw(sdc);
            PdfCanvas cv = sdc.getCurrentCanvas();
            PdfResources resources = cv.getResources();

            Assertions.assertTrue(resources.getResourceNames().isEmpty());
        }
    }

    @Test
    public void strokeOpacitySetWithFill() {
        // set compression to none, in case you want to write to disk and inspect the created document
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream(),
                new WriterProperties().setCompressionLevel(0)))) {
            SvgDrawContext sdc = setupDrawContext(pdfDocument);
            AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
            renderer.setAttribute(SvgConstants.Attributes.STROKE_OPACITY, "0.75");
            renderer.setAttribute(SvgConstants.Attributes.FILL, "blue");

            renderer.draw(sdc);
            PdfCanvas cv = sdc.getCurrentCanvas();
            PdfResources resources = cv.getResources();

            Assertions.assertTrue(resources.getResourceNames().isEmpty());
        }
    }

    @Test
    public void strokeOpacitySetWithNoneStroke() {
        // set compression to none, in case you want to write to disk and inspect the created document
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream(),
                new WriterProperties().setCompressionLevel(0)))) {
            SvgDrawContext sdc = setupDrawContext(pdfDocument);
            AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
            renderer.setAttribute(SvgConstants.Attributes.STROKE_OPACITY, "0.75");
            renderer.setAttribute(SvgConstants.Attributes.STROKE, SvgConstants.Values.NONE);

            renderer.draw(sdc);
            PdfCanvas cv = sdc.getCurrentCanvas();
            PdfResources resources = cv.getResources();

            Assertions.assertTrue(resources.getResourceNames().isEmpty());
        }
    }

    @Test
    public void fillOpacitySetWithFill() {
        // set compression to none, in case you want to write to disk and inspect the created document
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream(),
                new WriterProperties().setCompressionLevel(0)))) {
            SvgDrawContext sdc = setupDrawContext(pdfDocument);
            AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
            renderer.setAttribute(SvgConstants.Attributes.FILL_OPACITY, "0.75");
            renderer.setAttribute(SvgConstants.Attributes.FILL, "blue");

            renderer.draw(sdc);
            PdfCanvas cv = sdc.getCurrentCanvas();
            PdfResources resources = cv.getResources();

            Assertions.assertEquals(1, resources.getResourceNames().size());
            PdfDictionary resDic = (PdfDictionary) resources.getResourceObject(PdfName.ExtGState,
                    DEFAULT_RESOURCE_NAME);
            Assertions.assertEquals(1, resDic.size());
            Assertions.assertEquals(resDic.get(FILL_OPAC), new PdfNumber(0.75));
        }
    }

    @Test
    public void fillOpacitySetWithoutFill() {
        // set compression to none, in case you want to write to disk and inspect the created document
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream(),
                new WriterProperties().setCompressionLevel(0)))) {
            SvgDrawContext sdc = setupDrawContext(pdfDocument);
            AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
            renderer.setAttribute(SvgConstants.Attributes.FILL_OPACITY, "0.75");

            renderer.draw(sdc);
            PdfCanvas cv = sdc.getCurrentCanvas();
            PdfResources resources = cv.getResources();

            Assertions.assertEquals(1, resources.getResourceNames().size());
            PdfDictionary resDic = (PdfDictionary) resources.getResourceObject(PdfName.ExtGState,
                    DEFAULT_RESOURCE_NAME);
            Assertions.assertEquals(1, resDic.size());
            Assertions.assertEquals(resDic.get(FILL_OPAC), new PdfNumber(0.75));
        }
    }

    @Test
    public void fillOpacitySetWithNoneFill() {
        // set compression to none, in case you want to write to disk and inspect the created document
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream(),
                new WriterProperties().setCompressionLevel(0)))) {
            SvgDrawContext sdc = setupDrawContext(pdfDocument);
            AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
            renderer.setAttribute(SvgConstants.Attributes.FILL_OPACITY, "0.75");
            renderer.setAttribute(SvgConstants.Attributes.FILL, SvgConstants.Values.NONE);

            renderer.draw(sdc);
            PdfCanvas cv = sdc.getCurrentCanvas();
            PdfResources resources = cv.getResources();

            Assertions.assertTrue(resources.getResourceNames().isEmpty());
        }
    }

    @Test
    public void fillOpacitySetWithStroke() {
        // set compression to none, in case you want to write to disk and inspect the created document
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream(),
                new WriterProperties().setCompressionLevel(0)))) {
            SvgDrawContext sdc = setupDrawContext(pdfDocument);
            AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
            renderer.setAttribute(SvgConstants.Attributes.FILL_OPACITY, "0.75");
            renderer.setAttribute(SvgConstants.Attributes.STROKE, "blue");

            renderer.draw(sdc);
            PdfCanvas cv = sdc.getCurrentCanvas();
            PdfResources resources = cv.getResources();

            Assertions.assertEquals(1, resources.getResourceNames().size());
            PdfDictionary resDic = (PdfDictionary) resources.getResourceObject(PdfName.ExtGState,
                    DEFAULT_RESOURCE_NAME);
            Assertions.assertEquals(1, resDic.size());
            Assertions.assertEquals(resDic.get(FILL_OPAC), new PdfNumber(0.75));
        }
    }

    @Test
    public void fillAndStrokeOpacitySetWithStrokeAndFill() {
        // set compression to none, in case you want to write to disk and inspect the created document
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream(),
                new WriterProperties().setCompressionLevel(0)))) {
            SvgDrawContext sdc = setupDrawContext(pdfDocument);
            AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
            renderer.setAttribute(SvgConstants.Attributes.FILL_OPACITY, "0.75");
            renderer.setAttribute(SvgConstants.Attributes.STROKE_OPACITY, "0.75");
            renderer.setAttribute(SvgConstants.Attributes.FILL, "blue");
            renderer.setAttribute(SvgConstants.Attributes.STROKE, "green");

            renderer.draw(sdc);
            PdfCanvas cv = sdc.getCurrentCanvas();
            PdfResources resources = cv.getResources();

            Assertions.assertEquals(1, resources.getResourceNames().size());
            PdfDictionary resDic = (PdfDictionary) resources.getResourceObject(PdfName.ExtGState,
                    DEFAULT_RESOURCE_NAME);
            Assertions.assertEquals(2, resDic.size());
            Assertions.assertEquals(resDic.get(FILL_OPAC), new PdfNumber(0.75));
            Assertions.assertEquals(resDic.get(STROKE_OPAC), new PdfNumber(0.75));
        }
    }

    @Test
    public void noOpacitySetRGB() {
        // set compression to none, in case you want to write to disk and inspect the created document
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream(),
                new WriterProperties().setCompressionLevel(0)))) {
            SvgDrawContext sdc = setupDrawContext(pdfDocument);
            AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
            renderer.setAttribute(SvgConstants.Attributes.STROKE, "rgb(100,20,80)");

            renderer.draw(sdc);
            PdfCanvas cv = sdc.getCurrentCanvas();
            PdfResources resources = cv.getResources();
            Assertions.assertTrue(resources.getResourceNames().isEmpty());
        }
    }

    @Test
    public void strokeOpacitySetWithStrokeRGB() {
        // set compression to none, in case you want to write to disk and inspect the created document
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream(),
                new WriterProperties().setCompressionLevel(0)))) {
            SvgDrawContext sdc = setupDrawContext(pdfDocument);
            AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
            renderer.setAttribute(SvgConstants.Attributes.STROKE_OPACITY, "0.75");
            renderer.setAttribute(SvgConstants.Attributes.STROKE, "rgb(100,20,80)");

            renderer.draw(sdc);
            PdfCanvas cv = sdc.getCurrentCanvas();
            PdfResources resources = cv.getResources();

            Assertions.assertEquals(1, resources.getResourceNames().size());
            PdfDictionary resDic = (PdfDictionary) resources.getResourceObject(PdfName.ExtGState,
                    DEFAULT_RESOURCE_NAME);
            Assertions.assertEquals(1, resDic.size());
            Assertions.assertEquals(resDic.get(STROKE_OPAC), new PdfNumber(0.75));
        }
    }

    @Test
    public void strokeOpacitySetWithoutStrokeRGB() {
        // set compression to none, in case you want to write to disk and inspect the created document
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream(),
                new WriterProperties().setCompressionLevel(0)))) {
            SvgDrawContext sdc = setupDrawContext(pdfDocument);
            AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
            renderer.setAttribute(SvgConstants.Attributes.STROKE_OPACITY, "0.75");

            renderer.draw(sdc);
            PdfCanvas cv = sdc.getCurrentCanvas();
            PdfResources resources = cv.getResources();

            Assertions.assertTrue(resources.getResourceNames().isEmpty());
        }
    }

    @Test
    public void strokeOpacitySetWithFillRGB() {
        // set compression to none, in case you want to write to disk and inspect the created document
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream(),
                new WriterProperties().setCompressionLevel(0)))) {
            SvgDrawContext sdc = setupDrawContext(pdfDocument);
            AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
            renderer.setAttribute(SvgConstants.Attributes.STROKE_OPACITY, "0.75");
            renderer.setAttribute(SvgConstants.Attributes.FILL, "rgb(100,20,80)");

            renderer.draw(sdc);
            PdfCanvas cv = sdc.getCurrentCanvas();
            PdfResources resources = cv.getResources();

            Assertions.assertTrue(resources.getResourceNames().isEmpty());
        }
    }

    @Test
    public void strokeOpacitySetWithNoneStrokeRGB() {
        // set compression to none, in case you want to write to disk and inspect the created document
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream(),
                new WriterProperties().setCompressionLevel(0)))) {
            SvgDrawContext sdc = setupDrawContext(pdfDocument);
            AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
            renderer.setAttribute(SvgConstants.Attributes.STROKE_OPACITY, "0.75");
            renderer.setAttribute(SvgConstants.Attributes.STROKE, SvgConstants.Values.NONE);

            renderer.draw(sdc);
            PdfCanvas cv = sdc.getCurrentCanvas();
            PdfResources resources = cv.getResources();

            Assertions.assertTrue(resources.getResourceNames().isEmpty());
        }
    }

    @Test
    public void fillOpacitySetWithFillRGB() {
        // set compression to none, in case you want to write to disk and inspect the created document
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream(),
                new WriterProperties().setCompressionLevel(0)))) {
            SvgDrawContext sdc = setupDrawContext(pdfDocument);
            AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
            renderer.setAttribute(SvgConstants.Attributes.FILL_OPACITY, "0.75");
            renderer.setAttribute(SvgConstants.Attributes.FILL, "rgb(100,20,80)");

            renderer.draw(sdc);
            PdfCanvas cv = sdc.getCurrentCanvas();
            PdfResources resources = cv.getResources();

            Assertions.assertEquals(1, resources.getResourceNames().size());
            PdfDictionary resDic = (PdfDictionary) resources.getResourceObject(PdfName.ExtGState,
                    DEFAULT_RESOURCE_NAME);
            Assertions.assertEquals(1, resDic.size());
            Assertions.assertEquals(resDic.get(FILL_OPAC), new PdfNumber(0.75));
        }
    }

    @Test
    public void fillOpacitySetWithoutFillRGB() {
        // set compression to none, in case you want to write to disk and inspect the created document
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream(),
                new WriterProperties().setCompressionLevel(0)))) {
            SvgDrawContext sdc = setupDrawContext(pdfDocument);
            AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
            renderer.setAttribute(SvgConstants.Attributes.FILL_OPACITY, "0.75");

            renderer.draw(sdc);
            PdfCanvas cv = sdc.getCurrentCanvas();
            PdfResources resources = cv.getResources();

            Assertions.assertEquals(1, resources.getResourceNames().size());
            PdfDictionary resDic = (PdfDictionary) resources.getResourceObject(PdfName.ExtGState,
                    DEFAULT_RESOURCE_NAME);
            Assertions.assertEquals(1, resDic.size());
            Assertions.assertEquals(resDic.get(FILL_OPAC), new PdfNumber(0.75));
        }
    }

    @Test
    public void fillOpacitySetWithNoneFillRGB() {
        // set compression to none, in case you want to write to disk and inspect the created document
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream(),
                new WriterProperties().setCompressionLevel(0)))) {
            SvgDrawContext sdc = setupDrawContext(pdfDocument);
            AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
            renderer.setAttribute(SvgConstants.Attributes.FILL_OPACITY, "0.75");
            renderer.setAttribute(SvgConstants.Attributes.FILL, SvgConstants.Values.NONE);

            renderer.draw(sdc);
            PdfCanvas cv = sdc.getCurrentCanvas();
            PdfResources resources = cv.getResources();

            Assertions.assertTrue(resources.getResourceNames().isEmpty());
        }
    }

    @Test
    public void fillOpacitySetWithStrokeRGB() {
        // set compression to none, in case you want to write to disk and inspect the created document
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream(),
                new WriterProperties().setCompressionLevel(0)))) {
            SvgDrawContext sdc = setupDrawContext(pdfDocument);
            AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
            renderer.setAttribute(SvgConstants.Attributes.FILL_OPACITY, "0.75");
            renderer.setAttribute(SvgConstants.Attributes.STROKE, "rgb(100,20,80)");

            renderer.draw(sdc);
            PdfCanvas cv = sdc.getCurrentCanvas();
            PdfResources resources = cv.getResources();

            Assertions.assertEquals(1, resources.getResourceNames().size());
            PdfDictionary resDic = (PdfDictionary) resources.getResourceObject(PdfName.ExtGState,
                    DEFAULT_RESOURCE_NAME);
            Assertions.assertEquals(1, resDic.size());
            Assertions.assertEquals(resDic.get(FILL_OPAC), new PdfNumber(0.75));
        }
    }

    @Test
    public void fillAndStrokeOpacitySetWithStrokeAndFillRGB() {
        // set compression to none, in case you want to write to disk and inspect the created document
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream(),
                new WriterProperties().setCompressionLevel(0)))) {
            SvgDrawContext sdc = setupDrawContext(pdfDocument);
            AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
            renderer.setAttribute(SvgConstants.Attributes.FILL_OPACITY, "0.75");
            renderer.setAttribute(SvgConstants.Attributes.STROKE_OPACITY, "0.75");
            renderer.setAttribute(SvgConstants.Attributes.FILL, "rgb(100,20,80)");
            renderer.setAttribute(SvgConstants.Attributes.STROKE, "rgb(60,90,180)");

            renderer.draw(sdc);
            PdfCanvas cv = sdc.getCurrentCanvas();
            PdfResources resources = cv.getResources();

            Assertions.assertEquals(1, resources.getResourceNames().size());
            PdfDictionary resDic = (PdfDictionary) resources.getResourceObject(PdfName.ExtGState,
                    DEFAULT_RESOURCE_NAME);
            Assertions.assertEquals(2, resDic.size());
            Assertions.assertEquals(resDic.get(FILL_OPAC), new PdfNumber(0.75));
            Assertions.assertEquals(resDic.get(STROKE_OPAC), new PdfNumber(0.75));
        }
    }

    @Test
    public void noOpacitySetRGBA() {
        // set compression to none, in case you want to write to disk and inspect the created document
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream(),
                new WriterProperties().setCompressionLevel(0)))) {
            SvgDrawContext sdc = setupDrawContext(pdfDocument);
            AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
            renderer.setAttribute(SvgConstants.Attributes.STROKE, "rgba(100,20,80, .75)");

            renderer.draw(sdc);
            PdfCanvas cv = sdc.getCurrentCanvas();
            PdfResources resources = cv.getResources();
            Assertions.assertEquals(1, resources.getResourceNames().size());
            PdfDictionary resDic = (PdfDictionary) resources.getResourceObject(PdfName.ExtGState,
                    DEFAULT_RESOURCE_NAME);
            Assertions.assertEquals(1, resDic.size());
            Assertions.assertEquals(resDic.get(STROKE_OPAC), new PdfNumber(0.75));
        }
    }

    @Test
    public void strokeOpacitySetWithStrokeRGBA() {
        // set compression to none, in case you want to write to disk and inspect the created document
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream(),
                new WriterProperties().setCompressionLevel(0)))) {
            SvgDrawContext sdc = setupDrawContext(pdfDocument);
            AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
            renderer.setAttribute(SvgConstants.Attributes.STROKE_OPACITY, "0.75");
            renderer.setAttribute(SvgConstants.Attributes.STROKE, "rgba(100,20,80,.75)");

            renderer.draw(sdc);
            PdfCanvas cv = sdc.getCurrentCanvas();
            PdfResources resources = cv.getResources();

            Assertions.assertEquals(1, resources.getResourceNames().size());
            PdfDictionary resDic = (PdfDictionary) resources.getResourceObject(PdfName.ExtGState,
                    DEFAULT_RESOURCE_NAME);
            Assertions.assertEquals(1, resDic.size());
            Assertions.assertEquals(resDic.get(STROKE_OPAC), new PdfNumber(0.5625));
        }
    }

    @Test
    public void strokeOpacitySetWithoutStrokeRGBA() {
        // set compression to none, in case you want to write to disk and inspect the created document
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream(),
                new WriterProperties().setCompressionLevel(0)))) {
            SvgDrawContext sdc = setupDrawContext(pdfDocument);
            AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
            renderer.setAttribute(SvgConstants.Attributes.STROKE_OPACITY, "0.75");

            renderer.draw(sdc);
            PdfCanvas cv = sdc.getCurrentCanvas();
            PdfResources resources = cv.getResources();

            Assertions.assertTrue(resources.getResourceNames().isEmpty());
        }
    }

    @Test
    public void strokeOpacitySetWithFillRGBA() {
        // set compression to none, in case you want to write to disk and inspect the created document
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream(),
                new WriterProperties().setCompressionLevel(0)))) {
            SvgDrawContext sdc = setupDrawContext(pdfDocument);
            AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
            renderer.setAttribute(SvgConstants.Attributes.STROKE_OPACITY, "0.75");
            renderer.setAttribute(SvgConstants.Attributes.FILL, "rgba(100,20,80,.75)");

            renderer.draw(sdc);
            PdfCanvas cv = sdc.getCurrentCanvas();
            PdfResources resources = cv.getResources();

            Assertions.assertEquals(1, resources.getResourceNames().size());
            PdfDictionary resDic = (PdfDictionary) resources.getResourceObject(PdfName.ExtGState,
                    DEFAULT_RESOURCE_NAME);
            Assertions.assertEquals(1, resDic.size());
            Assertions.assertEquals(resDic.get(FILL_OPAC), new PdfNumber(0.75));
        }
    }

    @Test
    public void strokeOpacitySetWithNoneStrokeRGBA() {
        // set compression to none, in case you want to write to disk and inspect the created document
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream(),
                new WriterProperties().setCompressionLevel(0)))) {
            SvgDrawContext sdc = setupDrawContext(pdfDocument);
            AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
            renderer.setAttribute(SvgConstants.Attributes.STROKE_OPACITY, "0.75");
            renderer.setAttribute(SvgConstants.Attributes.STROKE, SvgConstants.Values.NONE);

            renderer.draw(sdc);
            PdfCanvas cv = sdc.getCurrentCanvas();
            PdfResources resources = cv.getResources();

            Assertions.assertTrue(resources.getResourceNames().isEmpty());
        }
    }

    @Test
    public void fillOpacitySetWithFillRGBA() {
        // set compression to none, in case you want to write to disk and inspect the created document
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream(),
                new WriterProperties().setCompressionLevel(0)))) {
            SvgDrawContext sdc = setupDrawContext(pdfDocument);
            AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
            renderer.setAttribute(SvgConstants.Attributes.FILL_OPACITY, "0.75");
            renderer.setAttribute(SvgConstants.Attributes.FILL, "rgba(100,20,80,.75)");

            renderer.draw(sdc);
            PdfCanvas cv = sdc.getCurrentCanvas();
            PdfResources resources = cv.getResources();

            Assertions.assertEquals(1, resources.getResourceNames().size());
            PdfDictionary resDic = (PdfDictionary) resources.getResourceObject(PdfName.ExtGState,
                    DEFAULT_RESOURCE_NAME);
            Assertions.assertEquals(1, resDic.size());
            Assertions.assertEquals(resDic.get(FILL_OPAC), new PdfNumber(0.5625));
        }
    }

    @Test
    public void fillOpacitySetWithoutFillRGBA() {
        // set compression to none, in case you want to write to disk and inspect the created document
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream(),
                new WriterProperties().setCompressionLevel(0)))) {
            SvgDrawContext sdc = setupDrawContext(pdfDocument);
            AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
            renderer.setAttribute(SvgConstants.Attributes.FILL_OPACITY, "0.75");

            renderer.draw(sdc);
            PdfCanvas cv = sdc.getCurrentCanvas();
            PdfResources resources = cv.getResources();

            Assertions.assertEquals(1, resources.getResourceNames().size());
            PdfDictionary resDic = (PdfDictionary) resources.getResourceObject(PdfName.ExtGState,
                    DEFAULT_RESOURCE_NAME);
            Assertions.assertEquals(1, resDic.size());
            Assertions.assertEquals(resDic.get(FILL_OPAC), new PdfNumber(0.75));
        }
    }

    @Test
    public void fillOpacitySetWithNoneFillRGBA() {
        // set compression to none, in case you want to write to disk and inspect the created document
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream(),
                new WriterProperties().setCompressionLevel(0)))) {
            SvgDrawContext sdc = setupDrawContext(pdfDocument);
            AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
            renderer.setAttribute(SvgConstants.Attributes.FILL_OPACITY, "0.75");
            renderer.setAttribute(SvgConstants.Attributes.FILL, SvgConstants.Values.NONE);

            renderer.draw(sdc);
            PdfCanvas cv = sdc.getCurrentCanvas();
            PdfResources resources = cv.getResources();

            Assertions.assertTrue(resources.getResourceNames().isEmpty());
        }
    }

    @Test
    public void fillOpacitySetWithStrokeRGBA() {
        // set compression to none, in case you want to write to disk and inspect the created document
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream(),
                new WriterProperties().setCompressionLevel(0)))) {
            SvgDrawContext sdc = setupDrawContext(pdfDocument);
            AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
            renderer.setAttribute(SvgConstants.Attributes.FILL_OPACITY, "0.75");
            renderer.setAttribute(SvgConstants.Attributes.STROKE, "rgba(100,20,80,.75)");

            renderer.draw(sdc);
            PdfCanvas cv = sdc.getCurrentCanvas();
            PdfResources resources = cv.getResources();

            Assertions.assertEquals(1, resources.getResourceNames().size());
            PdfDictionary resDic = (PdfDictionary) resources.getResourceObject(PdfName.ExtGState,
                    DEFAULT_RESOURCE_NAME);
            Assertions.assertEquals(2, resDic.size());
            Assertions.assertEquals(resDic.get(STROKE_OPAC), new PdfNumber(0.75));
            Assertions.assertEquals(resDic.get(FILL_OPAC), new PdfNumber(0.75));
        }
    }

    @Test
    public void fillAndStrokeOpacitySetWithStrokeAndFillRGBA() {
        // set compression to none, in case you want to write to disk and inspect the created document
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream(),
                new WriterProperties().setCompressionLevel(0)))) {
            SvgDrawContext sdc = setupDrawContext(pdfDocument);
            AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
            renderer.setAttribute(SvgConstants.Attributes.FILL_OPACITY, "0.75");
            renderer.setAttribute(SvgConstants.Attributes.STROKE_OPACITY, "0.75");
            renderer.setAttribute(SvgConstants.Attributes.FILL, "rgba(100,20,80,.75)");
            renderer.setAttribute(SvgConstants.Attributes.STROKE, "rgba(60,90,180,.75)");

            renderer.draw(sdc);
            PdfCanvas cv = sdc.getCurrentCanvas();
            PdfResources resources = cv.getResources();

            Assertions.assertEquals(1, resources.getResourceNames().size());
            PdfDictionary resDic = (PdfDictionary) resources.getResourceObject(PdfName.ExtGState,
                    DEFAULT_RESOURCE_NAME);
            Assertions.assertEquals(2, resDic.size());
            Assertions.assertEquals(resDic.get(FILL_OPAC), new PdfNumber(0.5625));
            Assertions.assertEquals(resDic.get(STROKE_OPAC), new PdfNumber(0.5625));
        }
    }

    @Test
    public void noOpacitySetWithStrokeRGBA() {
        // set compression to none, in case you want to write to disk and inspect the created document
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream(),
                new WriterProperties().setCompressionLevel(0)))) {
            SvgDrawContext sdc = setupDrawContext(pdfDocument);
            AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
            renderer.setAttribute(SvgConstants.Attributes.STROKE, "rgba(100,20,80,.75)");

            renderer.draw(sdc);
            PdfCanvas cv = sdc.getCurrentCanvas();
            PdfResources resources = cv.getResources();

            Assertions.assertEquals(1, resources.getResourceNames().size());
            PdfDictionary resDic = (PdfDictionary) resources.getResourceObject(PdfName.ExtGState,
                    DEFAULT_RESOURCE_NAME);
            Assertions.assertEquals(1, resDic.size());
            Assertions.assertEquals(resDic.get(STROKE_OPAC), new PdfNumber(0.75));
        }
    }

    @Test
    public void noOpacitySetWithoutStrokeRGBA() {
        // set compression to none, in case you want to write to disk and inspect the created document
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream(),
                new WriterProperties().setCompressionLevel(0)))) {
            SvgDrawContext sdc = setupDrawContext(pdfDocument);
            AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
            renderer.setAttribute(SvgConstants.Attributes.STROKE_OPACITY, "0.75");

            renderer.draw(sdc);
            PdfCanvas cv = sdc.getCurrentCanvas();
            PdfResources resources = cv.getResources();

            Assertions.assertTrue(resources.getResourceNames().isEmpty());
        }
    }

    @Test
    public void noOpacitySetWithFillRGBA() {
        // set compression to none, in case you want to write to disk and inspect the created document
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream(),
                new WriterProperties().setCompressionLevel(0)))) {
            SvgDrawContext sdc = setupDrawContext(pdfDocument);
            AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
            renderer.setAttribute(SvgConstants.Attributes.FILL, "rgba(100,20,80,.75)");

            renderer.draw(sdc);
            PdfCanvas cv = sdc.getCurrentCanvas();
            PdfResources resources = cv.getResources();

            Assertions.assertEquals(1, resources.getResourceNames().size());
            PdfDictionary resDic = (PdfDictionary) resources.getResourceObject(PdfName.ExtGState,
                    DEFAULT_RESOURCE_NAME);
            Assertions.assertEquals(1, resDic.size());
            Assertions.assertEquals(resDic.get(FILL_OPAC), new PdfNumber(0.75));
        }
    }

    @Test
    public void noOpacitySetWithNoneStrokeRGBA() {
        // set compression to none, in case you want to write to disk and inspect the created document
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream(),
                new WriterProperties().setCompressionLevel(0)))) {
            SvgDrawContext sdc = setupDrawContext(pdfDocument);
            AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
            renderer.setAttribute(SvgConstants.Attributes.STROKE_OPACITY, "0.75");
            renderer.setAttribute(SvgConstants.Attributes.STROKE, SvgConstants.Values.NONE);

            renderer.draw(sdc);
            PdfCanvas cv = sdc.getCurrentCanvas();
            PdfResources resources = cv.getResources();

            Assertions.assertTrue(resources.getResourceNames().isEmpty());
        }
    }

    @Test
    public void noOpacitySetWithNoneFillRGBA() {
        // set compression to none, in case you want to write to disk and inspect the created document
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream(),
                new WriterProperties().setCompressionLevel(0)))) {
            SvgDrawContext sdc = setupDrawContext(pdfDocument);
            AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
            renderer.setAttribute(SvgConstants.Attributes.FILL, SvgConstants.Values.NONE);

            renderer.draw(sdc);
            PdfCanvas cv = sdc.getCurrentCanvas();
            PdfResources resources = cv.getResources();

            Assertions.assertTrue(resources.getResourceNames().isEmpty());
        }
    }

    @Test
    public void noAndStrokeOpacitySetWithStrokeAndFillRGBA() {
        // set compression to none, in case you want to write to disk and inspect the created document
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream(),
                new WriterProperties().setCompressionLevel(0)))) {
            SvgDrawContext sdc = setupDrawContext(pdfDocument);
            AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
            renderer.setAttribute(SvgConstants.Attributes.FILL, "rgba(100,20,80,.75)");
            renderer.setAttribute(SvgConstants.Attributes.STROKE, "rgba(60,90,180,.75)");

            renderer.draw(sdc);
            PdfCanvas cv = sdc.getCurrentCanvas();
            PdfResources resources = cv.getResources();

            Assertions.assertEquals(1, resources.getResourceNames().size());
            PdfDictionary resDic = (PdfDictionary) resources.getResourceObject(PdfName.ExtGState,
                    DEFAULT_RESOURCE_NAME);
            Assertions.assertEquals(2, resDic.size());
            Assertions.assertEquals(resDic.get(FILL_OPAC), new PdfNumber(0.75));
            Assertions.assertEquals(resDic.get(STROKE_OPAC), new PdfNumber(0.75));
        }
    }

    private SvgDrawContext setupDrawContext(PdfDocument pdfDocument) {
        SvgDrawContext sdc = new SvgDrawContext(new ResourceResolver(""), new FontProvider());
        PdfCanvas cv = new PdfCanvas(pdfDocument.addNewPage());
        sdc.pushCanvas(cv);
        return sdc;
    }
}
