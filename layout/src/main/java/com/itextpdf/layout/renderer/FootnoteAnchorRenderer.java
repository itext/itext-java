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
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.layout.IPropertyContainer;
import com.itextpdf.layout.element.IElement;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.margins.Footnote;
import com.itextpdf.layout.properties.margins.FootnoteAnchor;
import com.itextpdf.layout.properties.margins.FootnotesUtil;
import com.itextpdf.layout.tagging.FootnoteTaggingHelper;
import com.itextpdf.layout.tagging.LayoutTaggingHelper;

import java.util.Collections;

/**
 * Renderer for {@link FootnoteAnchor} instance representing an anchor for a footnote.
 */
public class FootnoteAnchorRenderer extends AbstractRenderer {

    private IRenderer footnoteAnchor;

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
            this.footnoteRenderer = (FootnoteRenderer) footnote.createRendererSubTree().setParent(this);
            LayoutTaggingHelper taggingHelper = this.<LayoutTaggingHelper>getProperty(Property.TAGGING_HELPER);
            if (taggingHelper != null) {
                taggingHelper.addKidsHint(this, Collections.<IRenderer>singletonList(footnoteRenderer));
                taggingHelper.addKidsHint(this, Collections.<IRenderer>singletonList(footnoteAnchor));
                LayoutTaggingHelper.addTreeHints(taggingHelper, footnoteAnchor);
            }
        }

        int pageNumber = layoutContext.getArea().getPageNumber();
        Rectangle pageRectangle = this.getPdfDocument().getPage(pageNumber).getPageSize();
        IRenderer parentRenderer = getParent();
        while (parentRenderer != null) {
            if (parentRenderer instanceof DocumentRenderer) {
                DocumentRenderer documentRenderer = (DocumentRenderer) parentRenderer;
                FootnotesUtil.setParentForFootnoteRenderer(this.footnoteRenderer, documentRenderer);
                float leftMargin = (float) documentRenderer.getPropertyAsFloat(Property.MARGIN_LEFT);
                float rightMargin = (float) documentRenderer.getPropertyAsFloat(Property.MARGIN_RIGHT);
                pageRectangle.moveRight(leftMargin).decreaseWidth(leftMargin + rightMargin);
                break;
            }
            parentRenderer = parentRenderer.getParent();
        }

        this.footnoteRenderer.layout(new LayoutContext(new LayoutArea(pageNumber, pageRectangle)));

        // TODO DEVSIX-10023 Process partial result. Take it into account in line renderer
        //  and in case of table header/footer or fixed width.
        LayoutResult layoutResult = footnoteAnchor.layout(layoutContext);
        this.occupiedArea = layoutResult.getOccupiedArea();

        FootnotesCounterHandler.addFootnoteAnchor(this);

        if (LayoutResult.NOTHING == layoutResult.getStatus()) {
            return new LayoutResult(LayoutResult.NOTHING, null, null, layoutResult.getOverflowRenderer(), this);
        }

        return layoutResult;
    }

    @Override
    public void draw(DrawContext drawContext) {
        LayoutTaggingHelper taggingHelper = this.<LayoutTaggingHelper>getProperty(Property.TAGGING_HELPER);
        FootnoteTaggingHelper.repairFootnoteAnchorTagIfNeeded(this, taggingHelper);

        boolean isTagged = drawContext.isTaggingEnabled();
        if (isTagged) {
            taggingHelper = this.<LayoutTaggingHelper>getProperty(Property.TAGGING_HELPER);
            if (taggingHelper == null) {
                isTagged = false;
            } else {
                TagTreePointer tagPointer = taggingHelper.useAutoTaggingPointerAndRememberItsPosition(this);
                taggingHelper.createTag(this, tagPointer);
            }
        }

        footnoteAnchor.draw(drawContext);

        if (isTagged) {
            if (isLastRendererForModelElement) {
                taggingHelper.finishTaggingHint(this);
            }
            taggingHelper.restoreAutoTaggingPointerPosition(this);
        }

        flushed = true;
    }

    @Override
    public IRenderer getNextRenderer() {
        return new FootnoteAnchorRenderer((FootnoteAnchor) modelElement);
    }

    FootnoteAnchorRenderer addSymbolRenderer(IRenderer footnoteNumberingSymbolRenderer) {
        this.footnoteAnchor = footnoteNumberingSymbolRenderer.setParent(this);
        setFootnoteAnchor(((FootnoteAnchor) this.modelElement), footnoteNumberingSymbolRenderer.getModelElement());
        return this;
    }

    private static void setFootnoteAnchor(FootnoteAnchor footnoteAnchor, IPropertyContainer element) {
        if (element instanceof Image) {
            footnoteAnchor.setFootnoteAnchor((Image) element);
        }
        if (element instanceof Text) {
            footnoteAnchor.setFootnoteAnchor((Text) element);
        }
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
}
