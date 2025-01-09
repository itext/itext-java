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
package com.itextpdf.layout.renderer;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.test.ExtendedITextTest;

public abstract class RendererUnitTest extends ExtendedITextTest {

    // This also can be converted to a @Rule to have it all at hand in the future
    protected static Document createDummyDocument() {
        Document document = new Document(new PdfDocument(new PdfWriter(new ByteArrayOutputStream())));

        // setting margins to 0, because it's a dummy parent and this way it would less likely
        // interfere with other calculations
        document.setMargins(0, 0, 0, 0);
        return document;
    }

    protected static TextRenderer createLayoutedTextRenderer(String text, Document document) {
        TextRenderer renderer = (TextRenderer) new TextRenderer(new Text(text)).setParent(document.getRenderer());
        renderer.layout(new LayoutContext(new LayoutArea(1, new Rectangle(1000, 1000))));
        return renderer;
    }

    protected static ImageRenderer createLayoutedImageRenderer(float width, float height, Document document) {
        PdfFormXObject xObject = new PdfFormXObject(new Rectangle(width, height));
        Image img = new Image(xObject);
        ImageRenderer renderer = (ImageRenderer) new ImageRenderer(img).setParent(document.getRenderer());
        renderer.layout(new LayoutContext(new LayoutArea(1, new Rectangle(1000, 1000))));
        return renderer;
    }

    protected static LayoutArea createLayoutArea(float width, float height) {
        return new LayoutArea(1, new Rectangle(width, height));
    }

    protected static LayoutContext createLayoutContext(float width, float height) {
        return new LayoutContext(createLayoutArea(width, height));
    }
}
