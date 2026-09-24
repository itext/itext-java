/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.element.IElement;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.margins.Footnote;
import com.itextpdf.layout.properties.margins.FootnoteAnchor;
import com.itextpdf.layout.properties.margins.FootnotesUtil;
import com.itextpdf.test.ExtendedITextTest;

import java.io.ByteArrayOutputStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("UnitTest")
public class FootnoteAnchorRendererUnitTest extends ExtendedITextTest {

    @Test
    public void directPropertyAndStyleOnFootnoteAnchorAreCopiedToTextAnchorInFootnoteTest() {
        Footnote footnote = new Footnote("footnote text");
        FootnoteAnchor anchor = new FootnoteAnchor(new Text("anchor text"), footnote);
        anchor.setOpacity(0.25f);
        anchor.addStyle(new Style().setRotationAngle(0.5f));

        IElement injectedText = layoutAnchorAndGetInjectedAnchor(anchor);

        Assertions.assertNotSame(anchor.getFootnoteAnchor(), injectedText);
        Assertions.assertEquals(0.25f, injectedText.<Float>getProperty(Property.OPACITY), 1e-10);
        Assertions.assertEquals(0.5f, injectedText.<Float>getProperty(Property.ROTATION_ANGLE), 1e-10);
    }

    @Test
    public void directPropertyAndStyleOnFootnoteAnchorAreCopiedToImageAnchorInFootnoteTest() {
        Footnote footnote = new Footnote("footnote text");
        FootnoteAnchor anchor = new FootnoteAnchor(
                new Image(ImageDataFactory.createRawImage(new byte[] {50, 20})), footnote);
        anchor.setOpacity(0.7f);
        anchor.addStyle(new Style().setRotationAngle(0.75f));

        IElement injectedImage = layoutAnchorAndGetInjectedAnchor(anchor);

        Assertions.assertNotSame(anchor.getFootnoteAnchor(), injectedImage);
        Assertions.assertEquals(0.7f, injectedImage.<Float>getProperty(Property.OPACITY), 1e-10);
        Assertions.assertEquals(0.75f, injectedImage.<Float>getProperty(Property.ROTATION_ANGLE), 1e-10);
    }

    @Test
    public void directPropertyAndStyleOnFootnoteAnchorAreCopiedToTextAnchorInMainTextTest() {
        Footnote footnote = new Footnote("footnote text");
        FootnoteAnchor anchor = new FootnoteAnchor(new Text("anchor text"), footnote);
        anchor.setOpacity(0.25f);
        anchor.addStyle(new Style().setRotationAngle(0.5f));

        layoutAnchorAndGetInjectedAnchor(anchor);

        IElement mainTextAnchor = anchor.getFootnoteAnchor();
        Assertions.assertEquals(0.25f, mainTextAnchor.<Float>getProperty(Property.OPACITY), 1e-10);
        Assertions.assertEquals(0.5f, mainTextAnchor.<Float>getProperty(Property.ROTATION_ANGLE), 1e-10);
    }

    @Test
    public void directPropertyAndStyleOnFootnoteAnchorAreCopiedToImageAnchorInMainTextTest() {
        Footnote footnote = new Footnote("footnote text");
        FootnoteAnchor anchor = new FootnoteAnchor(
                new Image(ImageDataFactory.createRawImage(new byte[] {50, 20})), footnote);
        anchor.setOpacity(0.7f);
        anchor.addStyle(new Style().setRotationAngle(0.75f));

        layoutAnchorAndGetInjectedAnchor(anchor);

        IElement mainTextAnchor = anchor.getFootnoteAnchor();
        Assertions.assertEquals(0.7f, mainTextAnchor.<Float>getProperty(Property.OPACITY), 1e-10);
        Assertions.assertEquals(0.75f, mainTextAnchor.<Float>getProperty(Property.ROTATION_ANGLE), 1e-10);
    }

    @Test
    public void copyPropertiesAndStylesDoesNotOverrideDirectPropertyOnAnchorSymbolCopyTest() {
        Footnote footnote = new Footnote("footnote text");
        Text anchorSymbol = new Text("anchor text").setOpacity(0.9f);
        FootnoteAnchor anchor = new FootnoteAnchor(anchorSymbol, footnote);
        anchor.setOpacity(0.1f);

        IElement injectedText = layoutAnchorAndGetInjectedAnchor(anchor);

        Assertions.assertEquals(0.9f, injectedText.<Float>getProperty(Property.OPACITY), 1e-10);
    }

    @Test
    public void copyPropertiesAndStylesDoesNotOverrideStylePropertyOnAnchorSymbolCopyTest() {
        Footnote footnote = new Footnote("footnote text");
        Text anchorSymbol = new Text("anchor text");
        anchorSymbol.addStyle(new Style().setRotationAngle(1.2f));
        FootnoteAnchor anchor = new FootnoteAnchor(anchorSymbol, footnote);
        anchor.addStyle(new Style().setRotationAngle(0.4f));

        IElement injectedText = layoutAnchorAndGetInjectedAnchor(anchor);

        Assertions.assertEquals(1.2f, injectedText.<Float>getProperty(Property.ROTATION_ANGLE), 1e-10);
    }

    private static IElement layoutAnchorAndGetInjectedAnchor(FootnoteAnchor anchor) {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        pdfDocument.addNewPage();
        try (Document document = new Document(pdfDocument)) {
            FootnoteAnchorRenderer renderer = new FootnoteAnchorRenderer(anchor);
            renderer.setParent(document.getRenderer());
            renderer.layout(new LayoutContext(new LayoutArea(1, new Rectangle(500, 500))));
            return FootnotesUtil.getInjectedFootnoteAnchor(anchor.getFootnote());
        }
    }
}
