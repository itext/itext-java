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

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.AccessibilityProperties;
import com.itextpdf.kernel.pdf.tagutils.DefaultAccessibilityProperties;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Footnote;
import com.itextpdf.layout.element.FootnoteAnchor;
import com.itextpdf.layout.element.IElement;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.properties.BaseDirection;
import com.itextpdf.layout.properties.Property;

/**
 * Renderer for {@link FootnoteAnchor} instance representing an anchor for a footnote.
 */
public class FootnoteAnchorRenderer extends AbstractRenderer {

    private final IRenderer footnoteAnchor;

    // Create and store footnote renderer once to save its layout result.
    FootnoteRenderer footnoteRenderer = null;

    /**
     * Creates a {@link FootnoteAnchorRenderer} from its corresponding layout object.
     *
     * @param modelElement the {@link FootnoteAnchor} which this object should manage
     */
    public FootnoteAnchorRenderer(FootnoteAnchor modelElement) {
        super(modelElement);
        footnoteAnchor = createFootnoteAnchorRenderer();
        if (footnoteAnchor != null) {
            footnoteAnchor.setParent(this);
        }
    }

    @Override
    public LayoutResult layout(LayoutContext layoutContext) {
        if (this.footnoteRenderer == null) {
            Footnote footnote = ((FootnoteAnchor) this.modelElement).getFootnote();
            applyFootnoteAnchor(footnote);
            this.footnoteRenderer = (FootnoteRenderer) footnote.createRendererSubTree().setParent(this);
        }

        int pageNumber = layoutContext.getArea().getPageNumber();
        Rectangle pageRectangle = this.getPdfDocument().getPage(pageNumber).getPageSize();
        IRenderer parentRenderer = getParent();
        while (parentRenderer != null) {
            if (parentRenderer instanceof DocumentRenderer) {
                DocumentRenderer documentRenderer = (DocumentRenderer) parentRenderer;
                float leftMargin = (float) documentRenderer.getPropertyAsFloat(Property.MARGIN_BOTTOM);
                float rightMargin = (float) documentRenderer.getPropertyAsFloat(Property.MARGIN_TOP);
                pageRectangle.moveRight(leftMargin).decreaseWidth(leftMargin + rightMargin);
                break;
            }
            parentRenderer = parentRenderer.getParent();
        }

        this.footnoteRenderer.layout(new LayoutContext(new LayoutArea(pageNumber, pageRectangle)));

        LayoutResult layoutResult = footnoteAnchor.layout(layoutContext);
        this.occupiedArea = layoutResult.getOccupiedArea();

        FootnotesCounterHandler.addFootnoteAnchor(this);

        return layoutResult;
    }

    @Override
    public void draw(DrawContext drawContext) {
        footnoteAnchor.draw(drawContext);
    }

    @Override
    public IRenderer getNextRenderer() {
        return new FootnoteAnchorRenderer((FootnoteAnchor) modelElement);
    }

    private IRenderer createFootnoteAnchorRenderer() {
        IElement footnoteAnchorSymbol = ((FootnoteAnchor) this.modelElement).getFootnoteAnchor();
        if (footnoteAnchorSymbol instanceof Text) {
            return new TextRenderer((Text) footnoteAnchorSymbol);
        } else if (footnoteAnchorSymbol instanceof Image) {
            return footnoteAnchorSymbol.getRenderer();
        } else if (footnoteAnchorSymbol == null) {
            return null;
        } else {
            throw new IllegalStateException();
        }
    }

    private void applyFootnoteAnchor(Footnote footnote) {
        if (!footnote.getChildren().isEmpty() && footnote.getChildren().get(0) instanceof Paragraph) {
            Paragraph paragraph = (Paragraph) footnote.getChildren().get(0);
            injectFootnoteAnchorIntoParagraph(paragraph);
        }
    }

    private void injectFootnoteAnchorIntoParagraph(Paragraph paragraph) {
        // TODO DEVSIX-9981 Introduce anchor indent property to make it configurable.
        Div anchorIndent = new Div() {
            @Override
            public AccessibilityProperties getAccessibilityProperties() {
                if (tagProperties == null) {
                    tagProperties = new DefaultAccessibilityProperties(StandardRoles.ARTIFACT);
                }
                return tagProperties;
            }
        }.setWidth(5F);
        IElement footnoteAnchorSymbol = ((FootnoteAnchor) this.modelElement).getFootnoteAnchor();
        if (!paragraph.getChildren().contains(footnoteAnchorSymbol)) {
            boolean isRtl = BaseDirection.RIGHT_TO_LEFT == this.<BaseDirection>getProperty(Property.BASE_DIRECTION);
            if (!isRtl) {
                paragraph.getChildren().add(0, anchorIndent);
            }
            paragraph.getChildren().add(0, footnoteAnchorSymbol);
            if (isRtl) {
                paragraph.getChildren().add(0, anchorIndent);
            }
        }
    }
}
