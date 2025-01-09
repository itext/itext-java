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
package com.itextpdf.forms.fields;

import com.itextpdf.commons.actions.contexts.IMetaInfo;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfResources;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.renderer.MetaInfoContainer;
import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class PdfFormFieldUnitTest extends ExtendedITextTest {

    @Test
    public void cannotGetRectangleIfKidsIsNullTest() {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfFormField pdfFormField = PdfFormCreator.createFormField(pdfDocument);
        Assertions.assertNull(pdfFormField.getFirstFormAnnotation());
    }

    @Test
    public void setMetaInfoToCanvasMetaInfoUsedTest() {
        Canvas canvas = createCanvas();
        MetaInfoContainer metaInfoContainer = new MetaInfoContainer(new IMetaInfo() {
        });
        FormsMetaInfoStaticContainer.useMetaInfoDuringTheAction(metaInfoContainer,
                () -> PdfFormAnnotation.setMetaInfoToCanvas(canvas));

        Assertions.assertSame(metaInfoContainer, canvas.<MetaInfoContainer>getProperty(Property.META_INFO));
    }

    @Test
    public void setMetaInfoToCanvasMetaInfoNotUsedTest() {
        Canvas canvas = createCanvas();
        PdfFormAnnotation.setMetaInfoToCanvas(canvas);

        Assertions.assertNull(canvas.<MetaInfoContainer>getProperty(Property.META_INFO));
    }

    private static Canvas createCanvas() {
        try (PdfDocument document = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            PdfStream stream = (PdfStream) new PdfStream().makeIndirect(document);
            PdfResources resources = new PdfResources();
            PdfCanvas pdfCanvas = new PdfCanvas(stream, resources, document);
            return new Canvas(pdfCanvas, new Rectangle(100, 100));
        }
    }
}
