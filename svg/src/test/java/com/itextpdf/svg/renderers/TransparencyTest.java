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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class TransparencyTest extends ExtendedITextTest {

    private static final PdfName DEFAULT_RESOURCE_NAME = new PdfName("Gs1");
    private static final PdfName FILL_OPAC = new PdfName("ca");
    private static final PdfName STROKE_OPAC = new PdfName("CA");

    private PdfCanvas cv;
    private SvgDrawContext sdc;

    @BeforeEach
    public void setupDrawContextAndCanvas() {
        sdc = new SvgDrawContext(new ResourceResolver(""), new FontProvider());

        // set compression to none, in case you want to write to disk and inspect the created document
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream(), new WriterProperties().setCompressionLevel(0));
        PdfDocument doc = new PdfDocument(writer);

        cv = new PdfCanvas(doc.addNewPage());
        sdc.pushCanvas(cv);
    }

    @AfterEach
    public void close() {
        cv.getDocument().close();
    }

    @Test
    public void noOpacitySet() {
        AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
        renderer.setAttribute(SvgConstants.Attributes.STROKE, "blue");

        renderer.draw(sdc);
        PdfResources resources = cv.getResources();
        Assertions.assertTrue(resources.getResourceNames().isEmpty());
    }

    @Test
    public void strokeOpacitySetWithStroke() {
        AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
        renderer.setAttribute(SvgConstants.Attributes.STROKE_OPACITY, "0.75");
        renderer.setAttribute(SvgConstants.Attributes.STROKE, "blue");

        renderer.draw(sdc);
        PdfResources resources = cv.getResources();

        Assertions.assertEquals(1, resources.getResourceNames().size());
        PdfDictionary resDic = (PdfDictionary) resources.getResourceObject(PdfName.ExtGState, DEFAULT_RESOURCE_NAME);
        Assertions.assertEquals(1, resDic.size());
        Assertions.assertEquals(resDic.get(STROKE_OPAC), new PdfNumber(0.75));
    }

    @Test
    public void strokeOpacitySetWithoutStroke() {
        AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
        renderer.setAttribute(SvgConstants.Attributes.STROKE_OPACITY, "0.75");

        renderer.draw(sdc);
        PdfResources resources = cv.getResources();

        Assertions.assertTrue(resources.getResourceNames().isEmpty());
    }

    @Test
    public void strokeOpacitySetWithFill() {
        AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
        renderer.setAttribute(SvgConstants.Attributes.STROKE_OPACITY, "0.75");
        renderer.setAttribute(SvgConstants.Attributes.FILL, "blue");

        renderer.draw(sdc);
        PdfResources resources = cv.getResources();

        Assertions.assertTrue(resources.getResourceNames().isEmpty());
    }

    @Test
    public void strokeOpacitySetWithNoneStroke() {
        AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
        renderer.setAttribute(SvgConstants.Attributes.STROKE_OPACITY, "0.75");
        renderer.setAttribute(SvgConstants.Attributes.STROKE, SvgConstants.Values.NONE);

        renderer.draw(sdc);
        PdfResources resources = cv.getResources();

        Assertions.assertTrue(resources.getResourceNames().isEmpty());
    }


    @Test
    public void fillOpacitySetWithFill() {
        AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
        renderer.setAttribute(SvgConstants.Attributes.FILL_OPACITY, "0.75");
        renderer.setAttribute(SvgConstants.Attributes.FILL, "blue");

        renderer.draw(sdc);
        PdfResources resources = cv.getResources();

        Assertions.assertEquals(1, resources.getResourceNames().size());
        PdfDictionary resDic = (PdfDictionary) resources.getResourceObject(PdfName.ExtGState, DEFAULT_RESOURCE_NAME);
        Assertions.assertEquals(1, resDic.size());
        Assertions.assertEquals(resDic.get(FILL_OPAC), new PdfNumber(0.75));
    }

    @Test
    public void fillOpacitySetWithoutFill() {
        AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
        renderer.setAttribute(SvgConstants.Attributes.FILL_OPACITY, "0.75");

        renderer.draw(sdc);
        PdfResources resources = cv.getResources();

        Assertions.assertEquals(1, resources.getResourceNames().size());
        PdfDictionary resDic = (PdfDictionary) resources.getResourceObject(PdfName.ExtGState, DEFAULT_RESOURCE_NAME);
        Assertions.assertEquals(1, resDic.size());
        Assertions.assertEquals(resDic.get(FILL_OPAC), new PdfNumber(0.75));
    }

    @Test
    public void fillOpacitySetWithNoneFill() {
        AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
        renderer.setAttribute(SvgConstants.Attributes.FILL_OPACITY, "0.75");
        renderer.setAttribute(SvgConstants.Attributes.FILL, SvgConstants.Values.NONE);

        renderer.draw(sdc);
        PdfResources resources = cv.getResources();

        Assertions.assertTrue(resources.getResourceNames().isEmpty());
    }

    @Test
    public void fillOpacitySetWithStroke() {
        AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
        renderer.setAttribute(SvgConstants.Attributes.FILL_OPACITY, "0.75");
        renderer.setAttribute(SvgConstants.Attributes.STROKE, "blue");

        renderer.draw(sdc);
        PdfResources resources = cv.getResources();

        Assertions.assertEquals(1, resources.getResourceNames().size());
        PdfDictionary resDic = (PdfDictionary) resources.getResourceObject(PdfName.ExtGState, DEFAULT_RESOURCE_NAME);
        Assertions.assertEquals(1, resDic.size());
        Assertions.assertEquals(resDic.get(FILL_OPAC), new PdfNumber(0.75));
    }

    @Test
    public void fillAndStrokeOpacitySetWithStrokeAndFill() {
        AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
        renderer.setAttribute(SvgConstants.Attributes.FILL_OPACITY, "0.75");
        renderer.setAttribute(SvgConstants.Attributes.STROKE_OPACITY, "0.75");
        renderer.setAttribute(SvgConstants.Attributes.FILL, "blue");
        renderer.setAttribute(SvgConstants.Attributes.STROKE, "green");

        renderer.draw(sdc);
        PdfResources resources = cv.getResources();

        Assertions.assertEquals(1, resources.getResourceNames().size());
        PdfDictionary resDic = (PdfDictionary) resources.getResourceObject(PdfName.ExtGState, DEFAULT_RESOURCE_NAME);
        Assertions.assertEquals(2, resDic.size());
        Assertions.assertEquals(resDic.get(FILL_OPAC), new PdfNumber(0.75));
        Assertions.assertEquals(resDic.get(STROKE_OPAC), new PdfNumber(0.75));
    }


    @Test
    public void noOpacitySetRGB() {
        AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
        renderer.setAttribute(SvgConstants.Attributes.STROKE, "rgb(100,20,80)");

        renderer.draw(sdc);
        PdfResources resources = cv.getResources();
        Assertions.assertTrue(resources.getResourceNames().isEmpty());
    }

    @Test
    public void strokeOpacitySetWithStrokeRGB() {
        AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
        renderer.setAttribute(SvgConstants.Attributes.STROKE_OPACITY, "0.75");
        renderer.setAttribute(SvgConstants.Attributes.STROKE, "rgb(100,20,80)");

        renderer.draw(sdc);
        PdfResources resources = cv.getResources();

        Assertions.assertEquals(1, resources.getResourceNames().size());
        PdfDictionary resDic = (PdfDictionary) resources.getResourceObject(PdfName.ExtGState, DEFAULT_RESOURCE_NAME);
        Assertions.assertEquals(1, resDic.size());
        Assertions.assertEquals(resDic.get(STROKE_OPAC), new PdfNumber(0.75));
    }

    @Test
    public void strokeOpacitySetWithoutStrokeRGB() {
        AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
        renderer.setAttribute(SvgConstants.Attributes.STROKE_OPACITY, "0.75");

        renderer.draw(sdc);
        PdfResources resources = cv.getResources();

        Assertions.assertTrue(resources.getResourceNames().isEmpty());
    }

    @Test
    public void strokeOpacitySetWithFillRGB() {
        AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
        renderer.setAttribute(SvgConstants.Attributes.STROKE_OPACITY, "0.75");
        renderer.setAttribute(SvgConstants.Attributes.FILL, "rgb(100,20,80)");

        renderer.draw(sdc);
        PdfResources resources = cv.getResources();

        Assertions.assertTrue(resources.getResourceNames().isEmpty());
    }

    @Test
    public void strokeOpacitySetWithNoneStrokeRGB() {
        AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
        renderer.setAttribute(SvgConstants.Attributes.STROKE_OPACITY, "0.75");
        renderer.setAttribute(SvgConstants.Attributes.STROKE, SvgConstants.Values.NONE);

        renderer.draw(sdc);
        PdfResources resources = cv.getResources();

        Assertions.assertTrue(resources.getResourceNames().isEmpty());
    }


    @Test
    public void fillOpacitySetWithFillRGB() {
        AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
        renderer.setAttribute(SvgConstants.Attributes.FILL_OPACITY, "0.75");
        renderer.setAttribute(SvgConstants.Attributes.FILL, "rgb(100,20,80)");

        renderer.draw(sdc);
        PdfResources resources = cv.getResources();

        Assertions.assertEquals(1, resources.getResourceNames().size());
        PdfDictionary resDic = (PdfDictionary) resources.getResourceObject(PdfName.ExtGState, DEFAULT_RESOURCE_NAME);
        Assertions.assertEquals(1, resDic.size());
        Assertions.assertEquals(resDic.get(FILL_OPAC), new PdfNumber(0.75));
    }

    @Test
    public void fillOpacitySetWithoutFillRGB() {
        AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
        renderer.setAttribute(SvgConstants.Attributes.FILL_OPACITY, "0.75");

        renderer.draw(sdc);
        PdfResources resources = cv.getResources();

        Assertions.assertEquals(1, resources.getResourceNames().size());
        PdfDictionary resDic = (PdfDictionary) resources.getResourceObject(PdfName.ExtGState, DEFAULT_RESOURCE_NAME);
        Assertions.assertEquals(1, resDic.size());
        Assertions.assertEquals(resDic.get(FILL_OPAC), new PdfNumber(0.75));
    }

    @Test
    public void fillOpacitySetWithNoneFillRGB() {
        AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
        renderer.setAttribute(SvgConstants.Attributes.FILL_OPACITY, "0.75");
        renderer.setAttribute(SvgConstants.Attributes.FILL, SvgConstants.Values.NONE);

        renderer.draw(sdc);
        PdfResources resources = cv.getResources();

        Assertions.assertTrue(resources.getResourceNames().isEmpty());
    }

    @Test
    public void fillOpacitySetWithStrokeRGB() {
        AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
        renderer.setAttribute(SvgConstants.Attributes.FILL_OPACITY, "0.75");
        renderer.setAttribute(SvgConstants.Attributes.STROKE, "rgb(100,20,80)");

        renderer.draw(sdc);
        PdfResources resources = cv.getResources();

        Assertions.assertEquals(1, resources.getResourceNames().size());
        PdfDictionary resDic = (PdfDictionary) resources.getResourceObject(PdfName.ExtGState, DEFAULT_RESOURCE_NAME);
        Assertions.assertEquals(1, resDic.size());
        Assertions.assertEquals(resDic.get(FILL_OPAC), new PdfNumber(0.75));
    }

    @Test
    public void fillAndStrokeOpacitySetWithStrokeAndFillRGB() {
        AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
        renderer.setAttribute(SvgConstants.Attributes.FILL_OPACITY, "0.75");
        renderer.setAttribute(SvgConstants.Attributes.STROKE_OPACITY, "0.75");
        renderer.setAttribute(SvgConstants.Attributes.FILL, "rgb(100,20,80)");
        renderer.setAttribute(SvgConstants.Attributes.STROKE, "rgb(60,90,180)");

        renderer.draw(sdc);
        PdfResources resources = cv.getResources();

        Assertions.assertEquals(1, resources.getResourceNames().size());
        PdfDictionary resDic = (PdfDictionary) resources.getResourceObject(PdfName.ExtGState, DEFAULT_RESOURCE_NAME);
        Assertions.assertEquals(2, resDic.size());
        Assertions.assertEquals(resDic.get(FILL_OPAC), new PdfNumber(0.75));
        Assertions.assertEquals(resDic.get(STROKE_OPAC), new PdfNumber(0.75));
    }

    @Test
    public void noOpacitySetRGBA() {
        AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
        renderer.setAttribute(SvgConstants.Attributes.STROKE, "rgba(100,20,80, .75)");

        renderer.draw(sdc);
        PdfResources resources = cv.getResources();
        Assertions.assertEquals(1, resources.getResourceNames().size());
        PdfDictionary resDic = (PdfDictionary) resources.getResourceObject(PdfName.ExtGState, DEFAULT_RESOURCE_NAME);
        Assertions.assertEquals(1, resDic.size());
        Assertions.assertEquals(resDic.get(STROKE_OPAC), new PdfNumber(0.75));
    }

    @Test
    public void strokeOpacitySetWithStrokeRGBA() {
        AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
        renderer.setAttribute(SvgConstants.Attributes.STROKE_OPACITY, "0.75");
        renderer.setAttribute(SvgConstants.Attributes.STROKE, "rgba(100,20,80,.75)");

        renderer.draw(sdc);
        PdfResources resources = cv.getResources();

        Assertions.assertEquals(1, resources.getResourceNames().size());
        PdfDictionary resDic = (PdfDictionary) resources.getResourceObject(PdfName.ExtGState, DEFAULT_RESOURCE_NAME);
        Assertions.assertEquals(1, resDic.size());
        Assertions.assertEquals(resDic.get(STROKE_OPAC), new PdfNumber(0.5625));
    }

    @Test
    public void strokeOpacitySetWithoutStrokeRGBA() {
        AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
        renderer.setAttribute(SvgConstants.Attributes.STROKE_OPACITY, "0.75");

        renderer.draw(sdc);
        PdfResources resources = cv.getResources();

        Assertions.assertTrue(resources.getResourceNames().isEmpty());
    }

    @Test
    public void strokeOpacitySetWithFillRGBA() {
        AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
        renderer.setAttribute(SvgConstants.Attributes.STROKE_OPACITY, "0.75");
        renderer.setAttribute(SvgConstants.Attributes.FILL, "rgba(100,20,80,.75)");

        renderer.draw(sdc);
        PdfResources resources = cv.getResources();

        Assertions.assertEquals(1, resources.getResourceNames().size());
        PdfDictionary resDic = (PdfDictionary) resources.getResourceObject(PdfName.ExtGState, DEFAULT_RESOURCE_NAME);
        Assertions.assertEquals(1, resDic.size());
        Assertions.assertEquals(resDic.get(FILL_OPAC), new PdfNumber(0.75));
    }

    @Test
    public void strokeOpacitySetWithNoneStrokeRGBA() {
        AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
        renderer.setAttribute(SvgConstants.Attributes.STROKE_OPACITY, "0.75");
        renderer.setAttribute(SvgConstants.Attributes.STROKE, SvgConstants.Values.NONE);

        renderer.draw(sdc);
        PdfResources resources = cv.getResources();

        Assertions.assertTrue(resources.getResourceNames().isEmpty());
    }


    @Test
    public void fillOpacitySetWithFillRGBA() {
        AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
        renderer.setAttribute(SvgConstants.Attributes.FILL_OPACITY, "0.75");
        renderer.setAttribute(SvgConstants.Attributes.FILL, "rgba(100,20,80,.75)");

        renderer.draw(sdc);
        PdfResources resources = cv.getResources();

        Assertions.assertEquals(1, resources.getResourceNames().size());
        PdfDictionary resDic = (PdfDictionary) resources.getResourceObject(PdfName.ExtGState, DEFAULT_RESOURCE_NAME);
        Assertions.assertEquals(1, resDic.size());
        Assertions.assertEquals(resDic.get(FILL_OPAC), new PdfNumber(0.5625));
    }

    @Test
    public void fillOpacitySetWithoutFillRGBA() {
        AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
        renderer.setAttribute(SvgConstants.Attributes.FILL_OPACITY, "0.75");

        renderer.draw(sdc);
        PdfResources resources = cv.getResources();

        Assertions.assertEquals(1, resources.getResourceNames().size());
        PdfDictionary resDic = (PdfDictionary) resources.getResourceObject(PdfName.ExtGState, DEFAULT_RESOURCE_NAME);
        Assertions.assertEquals(1, resDic.size());
        Assertions.assertEquals(resDic.get(FILL_OPAC), new PdfNumber(0.75));
    }

    @Test
    public void fillOpacitySetWithNoneFillRGBA() {
        AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
        renderer.setAttribute(SvgConstants.Attributes.FILL_OPACITY, "0.75");
        renderer.setAttribute(SvgConstants.Attributes.FILL, SvgConstants.Values.NONE);

        renderer.draw(sdc);
        PdfResources resources = cv.getResources();

        Assertions.assertTrue(resources.getResourceNames().isEmpty());
    }

    @Test
    public void fillOpacitySetWithStrokeRGBA() {
        AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
        renderer.setAttribute(SvgConstants.Attributes.FILL_OPACITY, "0.75");
        renderer.setAttribute(SvgConstants.Attributes.STROKE, "rgba(100,20,80,.75)");

        renderer.draw(sdc);
        PdfResources resources = cv.getResources();

        Assertions.assertEquals(1, resources.getResourceNames().size());
        PdfDictionary resDic = (PdfDictionary) resources.getResourceObject(PdfName.ExtGState, DEFAULT_RESOURCE_NAME);
        Assertions.assertEquals(2, resDic.size());
        Assertions.assertEquals(resDic.get(STROKE_OPAC), new PdfNumber(0.75));
        Assertions.assertEquals(resDic.get(FILL_OPAC), new PdfNumber(0.75));
    }

    @Test
    public void fillAndStrokeOpacitySetWithStrokeAndFillRGBA() {
        AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
        renderer.setAttribute(SvgConstants.Attributes.FILL_OPACITY, "0.75");
        renderer.setAttribute(SvgConstants.Attributes.STROKE_OPACITY, "0.75");
        renderer.setAttribute(SvgConstants.Attributes.FILL, "rgba(100,20,80,.75)");
        renderer.setAttribute(SvgConstants.Attributes.STROKE, "rgba(60,90,180,.75)");

        renderer.draw(sdc);
        PdfResources resources = cv.getResources();

        Assertions.assertEquals(1, resources.getResourceNames().size());
        PdfDictionary resDic = (PdfDictionary) resources.getResourceObject(PdfName.ExtGState, DEFAULT_RESOURCE_NAME);
        Assertions.assertEquals(2, resDic.size());
        Assertions.assertEquals(resDic.get(FILL_OPAC), new PdfNumber(0.5625));
        Assertions.assertEquals(resDic.get(STROKE_OPAC), new PdfNumber(0.5625));
    }

    @Test
    public void noOpacitySetWithStrokeRGBA() {
        AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
        renderer.setAttribute(SvgConstants.Attributes.STROKE, "rgba(100,20,80,.75)");

        renderer.draw(sdc);
        PdfResources resources = cv.getResources();

        Assertions.assertEquals(1, resources.getResourceNames().size());
        PdfDictionary resDic = (PdfDictionary) resources.getResourceObject(PdfName.ExtGState, DEFAULT_RESOURCE_NAME);
        Assertions.assertEquals(1, resDic.size());
        Assertions.assertEquals(resDic.get(STROKE_OPAC), new PdfNumber(0.75));
    }

    @Test
    public void noOpacitySetWithoutStrokeRGBA() {
        AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
        renderer.setAttribute(SvgConstants.Attributes.STROKE_OPACITY, "0.75");

        renderer.draw(sdc);
        PdfResources resources = cv.getResources();

        Assertions.assertTrue(resources.getResourceNames().isEmpty());
    }

    @Test
    public void noOpacitySetWithFillRGBA() {
        AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
        renderer.setAttribute(SvgConstants.Attributes.FILL, "rgba(100,20,80,.75)");

        renderer.draw(sdc);
        PdfResources resources = cv.getResources();

        Assertions.assertEquals(1, resources.getResourceNames().size());
        PdfDictionary resDic = (PdfDictionary) resources.getResourceObject(PdfName.ExtGState, DEFAULT_RESOURCE_NAME);
        Assertions.assertEquals(1, resDic.size());
        Assertions.assertEquals(resDic.get(FILL_OPAC), new PdfNumber(0.75));
    }

    @Test
    public void noOpacitySetWithNoneStrokeRGBA() {
        AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
        renderer.setAttribute(SvgConstants.Attributes.STROKE_OPACITY, "0.75");
        renderer.setAttribute(SvgConstants.Attributes.STROKE, SvgConstants.Values.NONE);

        renderer.draw(sdc);
        PdfResources resources = cv.getResources();

        Assertions.assertTrue(resources.getResourceNames().isEmpty());
    }

    @Test
    public void noOpacitySetWithNoneFillRGBA() {
        AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
        renderer.setAttribute(SvgConstants.Attributes.FILL, SvgConstants.Values.NONE);

        renderer.draw(sdc);
        PdfResources resources = cv.getResources();

        Assertions.assertTrue(resources.getResourceNames().isEmpty());
    }

    @Test
    public void noAndStrokeOpacitySetWithStrokeAndFillRGBA() {
        AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
        renderer.setAttribute(SvgConstants.Attributes.FILL, "rgba(100,20,80,.75)");
        renderer.setAttribute(SvgConstants.Attributes.STROKE, "rgba(60,90,180,.75)");

        renderer.draw(sdc);
        PdfResources resources = cv.getResources();

        Assertions.assertEquals(1, resources.getResourceNames().size());
        PdfDictionary resDic = (PdfDictionary) resources.getResourceObject(PdfName.ExtGState, DEFAULT_RESOURCE_NAME);
        Assertions.assertEquals(2, resDic.size());
        Assertions.assertEquals(resDic.get(FILL_OPAC), new PdfNumber(0.75));
        Assertions.assertEquals(resDic.get(STROKE_OPAC), new PdfNumber(0.75));
    }
}
