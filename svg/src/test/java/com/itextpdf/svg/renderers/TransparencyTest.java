/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
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
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;

public class TransparencyTest {

    private static final PdfName DEFAULT_RESOURCE_NAME = new PdfName("Gs1");
    private static final PdfName FILL_OPAC = new PdfName("ca");
    private static final PdfName STROKE_OPAC = new PdfName("CA");

    private PdfCanvas cv;
    private SvgDrawContext sdc;

    @Before
    public void setupDrawContextAndCanvas() {
        sdc = new SvgDrawContext(new ResourceResolver(""), new FontProvider());

        // set compression to none, in case you want to write to disk and inspect the created document
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream(), new WriterProperties().setCompressionLevel(0));
        PdfDocument doc = new PdfDocument(writer);

        cv = new PdfCanvas(doc.addNewPage());
        sdc.pushCanvas(cv);
    }

    @After
    public void close() {
        cv.getDocument().close();
    }

    @Test
    public void noOpacitySet() {
        AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
        renderer.setAttribute(SvgConstants.Attributes.STROKE, "blue");

        renderer.draw(sdc);
        PdfResources resources = cv.getResources();
        Assert.assertTrue(resources.getResourceNames().isEmpty());
    }

    @Test
    public void strokeOpacitySetWithStroke() {
        AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
        renderer.setAttribute(SvgConstants.Attributes.STROKE_OPACITY, "0.75");
        renderer.setAttribute(SvgConstants.Attributes.STROKE, "blue");

        renderer.draw(sdc);
        PdfResources resources = cv.getResources();

        Assert.assertEquals(1, resources.getResourceNames().size());
        PdfDictionary resDic = (PdfDictionary) resources.getResourceObject(PdfName.ExtGState, DEFAULT_RESOURCE_NAME);
        Assert.assertEquals(1, resDic.size());
        Assert.assertEquals(resDic.get(STROKE_OPAC), new PdfNumber(0.75));
    }

    @Test
    public void strokeOpacitySetWithoutStroke() {
        AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
        renderer.setAttribute(SvgConstants.Attributes.STROKE_OPACITY, "0.75");

        renderer.draw(sdc);
        PdfResources resources = cv.getResources();

        Assert.assertTrue(resources.getResourceNames().isEmpty());
    }

    @Test
    public void strokeOpacitySetWithFill() {
        AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
        renderer.setAttribute(SvgConstants.Attributes.STROKE_OPACITY, "0.75");
        renderer.setAttribute(SvgConstants.Attributes.FILL, "blue");

        renderer.draw(sdc);
        PdfResources resources = cv.getResources();

        Assert.assertTrue(resources.getResourceNames().isEmpty());
    }

    @Test
    public void strokeOpacitySetWithNoneStroke() {
        AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
        renderer.setAttribute(SvgConstants.Attributes.STROKE_OPACITY, "0.75");
        renderer.setAttribute(SvgConstants.Attributes.STROKE, SvgConstants.Values.NONE);

        renderer.draw(sdc);
        PdfResources resources = cv.getResources();

        Assert.assertTrue(resources.getResourceNames().isEmpty());
    }


    @Test
    public void fillOpacitySetWithFill() {
        AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
        renderer.setAttribute(SvgConstants.Attributes.FILL_OPACITY, "0.75");
        renderer.setAttribute(SvgConstants.Attributes.FILL, "blue");

        renderer.draw(sdc);
        PdfResources resources = cv.getResources();

        Assert.assertEquals(1, resources.getResourceNames().size());
        PdfDictionary resDic = (PdfDictionary) resources.getResourceObject(PdfName.ExtGState, DEFAULT_RESOURCE_NAME);
        Assert.assertEquals(1, resDic.size());
        Assert.assertEquals(resDic.get(FILL_OPAC), new PdfNumber(0.75));
    }

    @Test
    public void fillOpacitySetWithoutFill() {
        AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
        renderer.setAttribute(SvgConstants.Attributes.FILL_OPACITY, "0.75");

        renderer.draw(sdc);
        PdfResources resources = cv.getResources();

        Assert.assertEquals(1, resources.getResourceNames().size());
        PdfDictionary resDic = (PdfDictionary) resources.getResourceObject(PdfName.ExtGState, DEFAULT_RESOURCE_NAME);
        Assert.assertEquals(1, resDic.size());
        Assert.assertEquals(resDic.get(FILL_OPAC), new PdfNumber(0.75));
    }

    @Test
    public void fillOpacitySetWithNoneFill() {
        AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
        renderer.setAttribute(SvgConstants.Attributes.FILL_OPACITY, "0.75");
        renderer.setAttribute(SvgConstants.Attributes.FILL, SvgConstants.Values.NONE);

        renderer.draw(sdc);
        PdfResources resources = cv.getResources();

        Assert.assertTrue(resources.getResourceNames().isEmpty());
    }

    @Test
    public void fillOpacitySetWithStroke() {
        AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
        renderer.setAttribute(SvgConstants.Attributes.FILL_OPACITY, "0.75");
        renderer.setAttribute(SvgConstants.Attributes.STROKE, "blue");

        renderer.draw(sdc);
        PdfResources resources = cv.getResources();

        Assert.assertEquals(1, resources.getResourceNames().size());
        PdfDictionary resDic = (PdfDictionary) resources.getResourceObject(PdfName.ExtGState, DEFAULT_RESOURCE_NAME);
        Assert.assertEquals(1, resDic.size());
        Assert.assertEquals(resDic.get(FILL_OPAC), new PdfNumber(0.75));
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

        Assert.assertEquals(1, resources.getResourceNames().size());
        PdfDictionary resDic = (PdfDictionary) resources.getResourceObject(PdfName.ExtGState, DEFAULT_RESOURCE_NAME);
        Assert.assertEquals(2, resDic.size());
        Assert.assertEquals(resDic.get(FILL_OPAC), new PdfNumber(0.75));
        Assert.assertEquals(resDic.get(STROKE_OPAC), new PdfNumber(0.75));
    }


    @Test
    public void noOpacitySetRGB() {
        AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
        renderer.setAttribute(SvgConstants.Attributes.STROKE, "rgb(100,20,80)");

        renderer.draw(sdc);
        PdfResources resources = cv.getResources();
        Assert.assertTrue(resources.getResourceNames().isEmpty());
    }

    @Test
    public void strokeOpacitySetWithStrokeRGB() {
        AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
        renderer.setAttribute(SvgConstants.Attributes.STROKE_OPACITY, "0.75");
        renderer.setAttribute(SvgConstants.Attributes.STROKE, "rgb(100,20,80)");

        renderer.draw(sdc);
        PdfResources resources = cv.getResources();

        Assert.assertEquals(1, resources.getResourceNames().size());
        PdfDictionary resDic = (PdfDictionary) resources.getResourceObject(PdfName.ExtGState, DEFAULT_RESOURCE_NAME);
        Assert.assertEquals(1, resDic.size());
        Assert.assertEquals(resDic.get(STROKE_OPAC), new PdfNumber(0.75));
    }

    @Test
    public void strokeOpacitySetWithoutStrokeRGB() {
        AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
        renderer.setAttribute(SvgConstants.Attributes.STROKE_OPACITY, "0.75");

        renderer.draw(sdc);
        PdfResources resources = cv.getResources();

        Assert.assertTrue(resources.getResourceNames().isEmpty());
    }

    @Test
    public void strokeOpacitySetWithFillRGB() {
        AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
        renderer.setAttribute(SvgConstants.Attributes.STROKE_OPACITY, "0.75");
        renderer.setAttribute(SvgConstants.Attributes.FILL, "rgb(100,20,80)");

        renderer.draw(sdc);
        PdfResources resources = cv.getResources();

        Assert.assertTrue(resources.getResourceNames().isEmpty());
    }

    @Test
    public void strokeOpacitySetWithNoneStrokeRGB() {
        AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
        renderer.setAttribute(SvgConstants.Attributes.STROKE_OPACITY, "0.75");
        renderer.setAttribute(SvgConstants.Attributes.STROKE, SvgConstants.Values.NONE);

        renderer.draw(sdc);
        PdfResources resources = cv.getResources();

        Assert.assertTrue(resources.getResourceNames().isEmpty());
    }


    @Test
    public void fillOpacitySetWithFillRGB() {
        AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
        renderer.setAttribute(SvgConstants.Attributes.FILL_OPACITY, "0.75");
        renderer.setAttribute(SvgConstants.Attributes.FILL, "rgb(100,20,80)");

        renderer.draw(sdc);
        PdfResources resources = cv.getResources();

        Assert.assertEquals(1, resources.getResourceNames().size());
        PdfDictionary resDic = (PdfDictionary) resources.getResourceObject(PdfName.ExtGState, DEFAULT_RESOURCE_NAME);
        Assert.assertEquals(1, resDic.size());
        Assert.assertEquals(resDic.get(FILL_OPAC), new PdfNumber(0.75));
    }

    @Test
    public void fillOpacitySetWithoutFillRGB() {
        AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
        renderer.setAttribute(SvgConstants.Attributes.FILL_OPACITY, "0.75");

        renderer.draw(sdc);
        PdfResources resources = cv.getResources();

        Assert.assertEquals(1, resources.getResourceNames().size());
        PdfDictionary resDic = (PdfDictionary) resources.getResourceObject(PdfName.ExtGState, DEFAULT_RESOURCE_NAME);
        Assert.assertEquals(1, resDic.size());
        Assert.assertEquals(resDic.get(FILL_OPAC), new PdfNumber(0.75));
    }

    @Test
    public void fillOpacitySetWithNoneFillRGB() {
        AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
        renderer.setAttribute(SvgConstants.Attributes.FILL_OPACITY, "0.75");
        renderer.setAttribute(SvgConstants.Attributes.FILL, SvgConstants.Values.NONE);

        renderer.draw(sdc);
        PdfResources resources = cv.getResources();

        Assert.assertTrue(resources.getResourceNames().isEmpty());
    }

    @Test
    public void fillOpacitySetWithStrokeRGB() {
        AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
        renderer.setAttribute(SvgConstants.Attributes.FILL_OPACITY, "0.75");
        renderer.setAttribute(SvgConstants.Attributes.STROKE, "rgb(100,20,80)");

        renderer.draw(sdc);
        PdfResources resources = cv.getResources();

        Assert.assertEquals(1, resources.getResourceNames().size());
        PdfDictionary resDic = (PdfDictionary) resources.getResourceObject(PdfName.ExtGState, DEFAULT_RESOURCE_NAME);
        Assert.assertEquals(1, resDic.size());
        Assert.assertEquals(resDic.get(FILL_OPAC), new PdfNumber(0.75));
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

        Assert.assertEquals(1, resources.getResourceNames().size());
        PdfDictionary resDic = (PdfDictionary) resources.getResourceObject(PdfName.ExtGState, DEFAULT_RESOURCE_NAME);
        Assert.assertEquals(2, resDic.size());
        Assert.assertEquals(resDic.get(FILL_OPAC), new PdfNumber(0.75));
        Assert.assertEquals(resDic.get(STROKE_OPAC), new PdfNumber(0.75));
    }

    @Test
    public void noOpacitySetRGBA() {
        AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
        renderer.setAttribute(SvgConstants.Attributes.STROKE, "rgba(100,20,80, .75)");

        renderer.draw(sdc);
        PdfResources resources = cv.getResources();
        Assert.assertEquals(1, resources.getResourceNames().size());
        PdfDictionary resDic = (PdfDictionary) resources.getResourceObject(PdfName.ExtGState, DEFAULT_RESOURCE_NAME);
        Assert.assertEquals(1, resDic.size());
        Assert.assertEquals(resDic.get(STROKE_OPAC), new PdfNumber(0.75));
    }

    @Test
    public void strokeOpacitySetWithStrokeRGBA() {
        AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
        renderer.setAttribute(SvgConstants.Attributes.STROKE_OPACITY, "0.75");
        renderer.setAttribute(SvgConstants.Attributes.STROKE, "rgba(100,20,80,.75)");

        renderer.draw(sdc);
        PdfResources resources = cv.getResources();

        Assert.assertEquals(1, resources.getResourceNames().size());
        PdfDictionary resDic = (PdfDictionary) resources.getResourceObject(PdfName.ExtGState, DEFAULT_RESOURCE_NAME);
        Assert.assertEquals(1, resDic.size());
        Assert.assertEquals(resDic.get(STROKE_OPAC), new PdfNumber(0.5625));
    }

    @Test
    public void strokeOpacitySetWithoutStrokeRGBA() {
        AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
        renderer.setAttribute(SvgConstants.Attributes.STROKE_OPACITY, "0.75");

        renderer.draw(sdc);
        PdfResources resources = cv.getResources();

        Assert.assertTrue(resources.getResourceNames().isEmpty());
    }

    @Test
    public void strokeOpacitySetWithFillRGBA() {
        AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
        renderer.setAttribute(SvgConstants.Attributes.STROKE_OPACITY, "0.75");
        renderer.setAttribute(SvgConstants.Attributes.FILL, "rgba(100,20,80,.75)");

        renderer.draw(sdc);
        PdfResources resources = cv.getResources();

        Assert.assertEquals(1, resources.getResourceNames().size());
        PdfDictionary resDic = (PdfDictionary) resources.getResourceObject(PdfName.ExtGState, DEFAULT_RESOURCE_NAME);
        Assert.assertEquals(1, resDic.size());
        Assert.assertEquals(resDic.get(FILL_OPAC), new PdfNumber(0.75));
    }

    @Test
    public void strokeOpacitySetWithNoneStrokeRGBA() {
        AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
        renderer.setAttribute(SvgConstants.Attributes.STROKE_OPACITY, "0.75");
        renderer.setAttribute(SvgConstants.Attributes.STROKE, SvgConstants.Values.NONE);

        renderer.draw(sdc);
        PdfResources resources = cv.getResources();

        Assert.assertTrue(resources.getResourceNames().isEmpty());
    }


    @Test
    public void fillOpacitySetWithFillRGBA() {
        AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
        renderer.setAttribute(SvgConstants.Attributes.FILL_OPACITY, "0.75");
        renderer.setAttribute(SvgConstants.Attributes.FILL, "rgba(100,20,80,.75)");

        renderer.draw(sdc);
        PdfResources resources = cv.getResources();

        Assert.assertEquals(1, resources.getResourceNames().size());
        PdfDictionary resDic = (PdfDictionary) resources.getResourceObject(PdfName.ExtGState, DEFAULT_RESOURCE_NAME);
        Assert.assertEquals(1, resDic.size());
        Assert.assertEquals(resDic.get(FILL_OPAC), new PdfNumber(0.5625));
    }

    @Test
    public void fillOpacitySetWithoutFillRGBA() {
        AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
        renderer.setAttribute(SvgConstants.Attributes.FILL_OPACITY, "0.75");

        renderer.draw(sdc);
        PdfResources resources = cv.getResources();

        Assert.assertEquals(1, resources.getResourceNames().size());
        PdfDictionary resDic = (PdfDictionary) resources.getResourceObject(PdfName.ExtGState, DEFAULT_RESOURCE_NAME);
        Assert.assertEquals(1, resDic.size());
        Assert.assertEquals(resDic.get(FILL_OPAC), new PdfNumber(0.75));
    }

    @Test
    public void fillOpacitySetWithNoneFillRGBA() {
        AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
        renderer.setAttribute(SvgConstants.Attributes.FILL_OPACITY, "0.75");
        renderer.setAttribute(SvgConstants.Attributes.FILL, SvgConstants.Values.NONE);

        renderer.draw(sdc);
        PdfResources resources = cv.getResources();

        Assert.assertTrue(resources.getResourceNames().isEmpty());
    }

    @Test
    public void fillOpacitySetWithStrokeRGBA() {
        AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
        renderer.setAttribute(SvgConstants.Attributes.FILL_OPACITY, "0.75");
        renderer.setAttribute(SvgConstants.Attributes.STROKE, "rgba(100,20,80,.75)");

        renderer.draw(sdc);
        PdfResources resources = cv.getResources();

        Assert.assertEquals(1, resources.getResourceNames().size());
        PdfDictionary resDic = (PdfDictionary) resources.getResourceObject(PdfName.ExtGState, DEFAULT_RESOURCE_NAME);
        Assert.assertEquals(2, resDic.size());
        Assert.assertEquals(resDic.get(STROKE_OPAC), new PdfNumber(0.75));
        Assert.assertEquals(resDic.get(FILL_OPAC), new PdfNumber(0.75));
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

        Assert.assertEquals(1, resources.getResourceNames().size());
        PdfDictionary resDic = (PdfDictionary) resources.getResourceObject(PdfName.ExtGState, DEFAULT_RESOURCE_NAME);
        Assert.assertEquals(2, resDic.size());
        Assert.assertEquals(resDic.get(FILL_OPAC), new PdfNumber(0.5625));
        Assert.assertEquals(resDic.get(STROKE_OPAC), new PdfNumber(0.5625));
    }

    @Test
    public void noOpacitySetWithStrokeRGBA() {
        AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
        renderer.setAttribute(SvgConstants.Attributes.STROKE, "rgba(100,20,80,.75)");

        renderer.draw(sdc);
        PdfResources resources = cv.getResources();

        Assert.assertEquals(1, resources.getResourceNames().size());
        PdfDictionary resDic = (PdfDictionary) resources.getResourceObject(PdfName.ExtGState, DEFAULT_RESOURCE_NAME);
        Assert.assertEquals(1, resDic.size());
        Assert.assertEquals(resDic.get(STROKE_OPAC), new PdfNumber(0.75));
    }

    @Test
    public void noOpacitySetWithoutStrokeRGBA() {
        AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
        renderer.setAttribute(SvgConstants.Attributes.STROKE_OPACITY, "0.75");

        renderer.draw(sdc);
        PdfResources resources = cv.getResources();

        Assert.assertTrue(resources.getResourceNames().isEmpty());
    }

    @Test
    public void noOpacitySetWithFillRGBA() {
        AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
        renderer.setAttribute(SvgConstants.Attributes.FILL, "rgba(100,20,80,.75)");

        renderer.draw(sdc);
        PdfResources resources = cv.getResources();

        Assert.assertEquals(1, resources.getResourceNames().size());
        PdfDictionary resDic = (PdfDictionary) resources.getResourceObject(PdfName.ExtGState, DEFAULT_RESOURCE_NAME);
        Assert.assertEquals(1, resDic.size());
        Assert.assertEquals(resDic.get(FILL_OPAC), new PdfNumber(0.75));
    }

    @Test
    public void noOpacitySetWithNoneStrokeRGBA() {
        AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
        renderer.setAttribute(SvgConstants.Attributes.STROKE_OPACITY, "0.75");
        renderer.setAttribute(SvgConstants.Attributes.STROKE, SvgConstants.Values.NONE);

        renderer.draw(sdc);
        PdfResources resources = cv.getResources();

        Assert.assertTrue(resources.getResourceNames().isEmpty());
    }

    @Test
    public void noOpacitySetWithNoneFillRGBA() {
        AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
        renderer.setAttribute(SvgConstants.Attributes.FILL, SvgConstants.Values.NONE);

        renderer.draw(sdc);
        PdfResources resources = cv.getResources();

        Assert.assertTrue(resources.getResourceNames().isEmpty());
    }

    @Test
    public void noAndStrokeOpacitySetWithStrokeAndFillRGBA() {
        AbstractSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
        renderer.setAttribute(SvgConstants.Attributes.FILL, "rgba(100,20,80,.75)");
        renderer.setAttribute(SvgConstants.Attributes.STROKE, "rgba(60,90,180,.75)");

        renderer.draw(sdc);
        PdfResources resources = cv.getResources();

        Assert.assertEquals(1, resources.getResourceNames().size());
        PdfDictionary resDic = (PdfDictionary) resources.getResourceObject(PdfName.ExtGState, DEFAULT_RESOURCE_NAME);
        Assert.assertEquals(2, resDic.size());
        Assert.assertEquals(resDic.get(FILL_OPAC), new PdfNumber(0.75));
        Assert.assertEquals(resDic.get(STROKE_OPAC), new PdfNumber(0.75));
    }
}
