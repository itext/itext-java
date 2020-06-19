/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2020 iText Group NV
    Authors: iText Software.

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
