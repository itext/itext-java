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

import com.itextpdf.commons.datastructures.Tuple2;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.layout.IPropertyContainer;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.element.AbstractElement;
import com.itextpdf.layout.element.IAbstractElement;
import com.itextpdf.layout.element.IElement;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.exceptions.LayoutExceptionMessageConstant;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.minmaxwidth.MinMaxWidth;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.margins.Footnote;
import com.itextpdf.layout.properties.margins.FootnoteAnchor;
import com.itextpdf.layout.properties.margins.FootnotesProperties;
import com.itextpdf.layout.properties.margins.FootnotesUtil;
import com.itextpdf.layout.tagging.FootnoteTaggingHelper;
import com.itextpdf.layout.tagging.LayoutTaggingHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Renderer for {@link FootnoteAnchor} instance representing an anchor for a footnote.
 */
public class FootnoteAnchorRenderer extends AbstractRenderer {

    IRenderer footnoteAnchor;

    float yPos = Float.NaN;

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

    /**
     * {@inheritDoc}
     */
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

        handleFootnoteAnchorStyles();

        int pageNumber = layoutContext.getArea().getPageNumber();
        Rectangle pageRectangle = this.getPdfDocument().getPage(pageNumber).getPageSize();
        IRenderer parentRenderer = getParent();
        if (parentRenderer instanceof LineRenderer) {
            this.yPos = ((LineRenderer) parentRenderer).occupiedArea.getBBox().getTop();
        }
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

        LayoutResult layoutResult = footnoteAnchor.setParent(this).layout(layoutContext);
        this.occupiedArea = layoutResult.getOccupiedArea();

        if (LayoutResult.NOTHING == layoutResult.getStatus()) {
            layoutResult.setOverflowRenderer(this);
            layoutResult.setCauseOfNothing(this);
        } else {
            if (Float.isNaN(this.yPos)) {
                this.yPos = this.occupiedArea.getBBox().getTop();
            }
        }
        if (layoutResult.getSplitRenderer() != null) {
            FootnoteAnchorRenderer splitRenderer = createSplitRenderer(layoutResult);
            layoutResult.setSplitRenderer(splitRenderer);
        }

        return layoutResult;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void move(float dxRight, float dyUp) {
        footnoteAnchor.move(dxRight, dyUp);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void draw(DrawContext drawContext) {
        LayoutTaggingHelper taggingHelper = this.<LayoutTaggingHelper>getProperty(Property.TAGGING_HELPER);
        FootnoteTaggingHelper.repairFootnoteAnchorTagIfNeeded(this, taggingHelper);

        boolean isTagged = drawContext.isTaggingEnabled();
        boolean tagCreated = false;
        if (isTagged) {
            taggingHelper = this.<LayoutTaggingHelper>getProperty(Property.TAGGING_HELPER);
            if (taggingHelper == null) {
                isTagged = false;
            } else {
                TagTreePointer tagPointer = taggingHelper.useAutoTaggingPointerAndRememberItsPosition(this);
                tagCreated = taggingHelper.createTag(this, tagPointer);
            }
        }

        if (tagCreated || !isTagged) {
            // We only don't set up links if tagging is enabled, but tag was not created,
            // meaning this content is in fact an artifact. This happens because links contain annotations,
            // and annotations need to be tagged. But since this content is an artifact, we can't properly tag it.
            setUpLinks(drawContext);
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

    /**
     * {@inheritDoc}
     */
    @Override
    public MinMaxWidth getMinMaxWidth() {
        return getMinMaxWidth(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MinMaxWidth getMinMaxWidth(Float parentBoxWidth) {
        childRenderers.clear();
        childRenderers.add(footnoteAnchor);
        MinMaxWidth res = super.getMinMaxWidth(parentBoxWidth);
        childRenderers.clear();
        return res;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IRenderer getNextRenderer() {
        return new FootnoteAnchorRenderer((FootnoteAnchor) modelElement);
    }

    /**
     * Resolve {@link Property#FONT} String[] value.
     *
     * @param newChildRenderers all processed renderers are added to this list.
     */
    void resolveFonts(Collection<IRenderer> newChildRenderers) {
        if (footnoteAnchor != null) {
            List<IRenderer> addedRenderers = new ArrayList<IRenderer>();
            if (footnoteAnchor instanceof TextRenderer) {
                ((TextRenderer) footnoteAnchor).resolveFonts(addedRenderers);
                if (addedRenderers.size() > 1) {
                    throw new PdfException(LayoutExceptionMessageConstant.FOOTNOTE_ANCHOR_LAYOUT_CONSISTENCY);
                }
                newChildRenderers.add( this);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Float getFirstYLineRecursively() {
        childRenderers.clear();
        childRenderers.add(footnoteAnchor);
        Float res = super.getFirstYLineRecursively();
        childRenderers.clear();
        return res;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Float getLastYLineRecursively() {
        childRenderers.clear();
        childRenderers.add(footnoteAnchor);
        Float res = super.getLastYLineRecursively();
        childRenderers.clear();
        return res;
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

    private static void setUpLinks(IPropertyContainer from, IPropertyContainer to, String name,
                                   String altDescription, PdfDocument document) {
        int amountOfNamedDestinations = 0;
        if (document.getCatalog().getNameTree(PdfName.Dests).getNames() != null) {
            amountOfNamedDestinations = document.getCatalog().getNameTree(PdfName.Dests).getNames().size();
        }
        PdfLinkAnnotation footnoteAnnotation = (PdfLinkAnnotation) new PdfLinkAnnotation(new Rectangle(0, 0))
                .setAction(PdfAction.createGoTo(name + amountOfNamedDestinations))
                .setFlags(PdfAnnotation.PRINT);
        footnoteAnnotation.setBorder(new PdfArray(new float[]{0, 0, 0}));
        footnoteAnnotation.setContents(altDescription);

        from.setProperty(Property.LINK_ANNOTATION, footnoteAnnotation);

        Set<Object> footnoteDestinations = to.<Set<Object>>getProperty(Property.DESTINATION);
        if (footnoteDestinations == null) {
            footnoteDestinations = new HashSet<>();
        }
        footnoteDestinations.add(
                new Tuple2<String, PdfDictionary>(name + amountOfNamedDestinations, footnoteAnnotation.getAction()));
        to.setProperty(Property.DESTINATION, footnoteDestinations);
    }

    private void setUpLinks(DrawContext drawContext) {
        IPropertyContainer footnoteLabel =
                FootnotesUtil.getInjectedFootnoteAnchor((Footnote)footnoteRenderer.getModelElement());
        if (footnoteLabel == null) {
            // Footnote label is not supposed to be null. If it is, something is broken, and we don't add links.
            return;
        }
        // We don't want to override existing link annotations, if any.
        if (footnoteAnchor.<PdfLinkAnnotation>getProperty(Property.LINK_ANNOTATION) == null &&
                footnoteLabel.<PdfLinkAnnotation>getProperty(Property.LINK_ANNOTATION) == null) {
            setUpLinks(footnoteAnchor, footnoteLabel, "footnoteAnchor", "Go to footnote.",
                    drawContext.getDocument());
            setUpLinks(footnoteLabel, footnoteAnchor, "footnoteContent", "Go to footnote anchor.",
                    drawContext.getDocument());
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

    private FootnoteAnchorRenderer createSplitRenderer(LayoutResult layoutResult) {
        FootnoteAnchorRenderer splitRenderer = (FootnoteAnchorRenderer) getNextRenderer();
        splitRenderer.occupiedArea = occupiedArea.clone();
        splitRenderer.parent = parent;
        splitRenderer.footnoteRenderer = footnoteRenderer;
        splitRenderer.addAllProperties(getOwnProperties());
        splitRenderer.footnoteAnchor = layoutResult.getSplitRenderer().setParent(splitRenderer);

        return splitRenderer;
    }

    private void handleFootnoteAnchorStyles() {
        if (!(footnoteAnchor.getModelElement() instanceof IAbstractElement)) {
            return;
        }

        IPropertyContainer footnoteAnchorModelElement = footnoteAnchor.getModelElement();
        FootnoteAnchor modelElement = ((FootnoteAnchor) this.getModelElement());
        FootnotesProperties footnotesProperties = this.<FootnotesProperties>getProperty(Property.FOOTNOTES_PROPERTIES);
        Style customStyle = footnotesProperties.getFootnoteAnchorStyle();

        if (footnoteAnchorModelElement instanceof Text) {
            handleFootnoteAnchorStyles(modelElement, (Text) footnoteAnchorModelElement, customStyle);
        } else if (footnoteAnchorModelElement instanceof Image) {
            handleFootnoteAnchorStyles(modelElement, (Image) footnoteAnchorModelElement, customStyle);
        }
    }

    private <T extends IElement> void handleFootnoteAnchorStyles(FootnoteAnchor modelElement,
            AbstractElement<T> footnoteAnchorModelElement, Style customStyle) {
        copyPropertiesAndStyles(modelElement, footnoteAnchorModelElement);

        if (customStyle != null) {
            footnoteAnchorModelElement.addStyleIfAbsent(customStyle);
        }

        if (FootnotesUtil.isDefaultStyleNeeded(modelElement)) {
            UnitValue parentFontSize = getParent().<UnitValue>getProperty(Property.FONT_SIZE);
            Style defaultStyle = FootnotesUtil.createDefaultFootnoteAnchorStyle(parentFontSize);
            if (!footnoteAnchorModelElement.getOwnProperties().containsKey(Property.FONT_SIZE)
                    && !hasStyleWithOwnProperty(footnoteAnchorModelElement, Property.FONT_SIZE)) {
                footnoteAnchor.setProperty(Property.FONT_SIZE, defaultStyle.<UnitValue>getProperty(Property.FONT_SIZE));
            }
            if (!footnoteAnchorModelElement.getOwnProperties().containsKey(Property.TEXT_RISE)
                    && !hasStyleWithOwnProperty(footnoteAnchorModelElement, Property.TEXT_RISE)) {
                footnoteAnchor.setProperty(Property.TEXT_RISE, defaultStyle.<Float>getProperty(Property.TEXT_RISE));
            }
        }

        setFootnoteAnchor(((FootnoteAnchor) this.modelElement), footnoteAnchorModelElement);
    }

    private static <T extends IElement> void copyPropertiesAndStyles(FootnoteAnchor sourceElement,
            AbstractElement<T> targetElement) {
        for (Map.Entry<Integer, Object> property : sourceElement.getOwnProperties().entrySet()) {
            if (!targetElement.hasProperty(property.getKey())) {
                targetElement.setProperty(property.getKey(), property.getValue());
            }
        }
        for (Style style : sourceElement.getStyles()) {
            targetElement.addStyleIfAbsent(style);
        }
    }

    private static <T extends IElement> boolean hasStyleWithOwnProperty(AbstractElement<T> element, int property) {
        for (Style style : element.getStyles()) {
            if (style.hasOwnProperty(property)) {
                return true;
            }
        }
        return false;
    }
}
