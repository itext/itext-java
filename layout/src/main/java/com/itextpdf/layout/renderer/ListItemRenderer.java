/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2017 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

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
package com.itextpdf.layout.renderer;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.tagutils.IAccessibleElement;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.kernel.pdf.tagutils.WaitingTagsManager;
import com.itextpdf.layout.element.ListItem;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.property.ListSymbolAlignment;
import com.itextpdf.layout.property.ListSymbolPosition;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.property.UnitValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.io.util.MessageFormatUtil;

public class ListItemRenderer extends DivRenderer {

    protected IRenderer symbolRenderer;
    protected float symbolAreaWidth;
    private boolean symbolAddedInside;

    /**
     * Creates a ListItemRenderer from its corresponding layout object.
     * @param modelElement the {@link com.itextpdf.layout.element.ListItem} which this object should manage
     */
    public ListItemRenderer(ListItem modelElement) {
        super(modelElement);
    }

    public void addSymbolRenderer(IRenderer symbolRenderer, float symbolAreaWidth) {
        this.symbolRenderer = symbolRenderer;
        this.symbolAreaWidth = symbolAreaWidth;
    }

    @Override
    public LayoutResult layout(LayoutContext layoutContext) {
        if (symbolRenderer != null && this.<Object>getProperty(Property.HEIGHT) == null && !isListSymbolEmpty(symbolRenderer)) {
            float[] ascenderDescender = calculateAscenderDescender();
            float minHeight = Math.max(symbolRenderer.getOccupiedArea().getBBox().getHeight(), ascenderDescender[0] - ascenderDescender[1]);
            updateMinHeight(UnitValue.createPointValue(minHeight));
        }
        applyListSymbolPosition();
        LayoutResult result = super.layout(layoutContext);
        if (LayoutResult.PARTIAL == result.getStatus()) {
            result.getOverflowRenderer().deleteOwnProperty(Property.MIN_HEIGHT);
        }
        return result;
    }

    @Override
    public void draw(DrawContext drawContext) {
        if (occupiedArea == null) {
            Logger logger = LoggerFactory.getLogger(ListItemRenderer.class);
            logger.error(MessageFormatUtil.format(LogMessageConstant.OCCUPIED_AREA_HAS_NOT_BEEN_INITIALIZED, "Drawing won't be performed."));
            return;
        }
        boolean isTagged = drawContext.isTaggingEnabled() && getModelElement() instanceof IAccessibleElement;
        TagTreePointer tagPointer = null;
        if (isTagged) {
            IAccessibleElement modelElement = (IAccessibleElement) getModelElement();
            PdfName role = modelElement.getRole();
            if (role != null && !PdfName.Artifact.equals(role)) {
                tagPointer = drawContext.getDocument().getTagStructureContext().getAutoTaggingPointer();
                WaitingTagsManager waitingTagsManager = drawContext.getDocument().getTagStructureContext().getWaitingTagsManager();
                boolean lBodyTagIsCreated = waitingTagsManager.tryMovePointerToWaitingTag(tagPointer, modelElement);
                if (lBodyTagIsCreated) {
                    tagPointer.moveToParent();
                } else {
                    tagPointer.addTag(isPossibleBadTagging(PdfName.LI) ? PdfName.Div : PdfName.LI);
                }
            } else {
                isTagged = false;
            }
        }

        super.draw(drawContext);

        // It will be null in case of overflow (only the "split" part will contain symbol renderer.
        if (symbolRenderer != null && !symbolAddedInside) {
            symbolRenderer.setParent(this);
            float x = occupiedArea.getBBox().getX();
            ListSymbolPosition symbolPosition = (ListSymbolPosition) ListRenderer.getListItemOrListProperty(this, parent, Property.LIST_SYMBOL_POSITION);
            if (symbolPosition != ListSymbolPosition.DEFAULT) {
                Float symbolIndent = this.getPropertyAsFloat(Property.LIST_SYMBOL_INDENT);
                x -= symbolAreaWidth + (float) (symbolIndent == null ? 0 : symbolIndent);
                if (symbolPosition == ListSymbolPosition.OUTSIDE) {
                    x += (float) this.getPropertyAsFloat(Property.MARGIN_LEFT);
                }
            }
            applyMargins(occupiedArea.getBBox(), false);
            applyBorderBox(occupiedArea.getBBox(), false);
            if (childRenderers.size() > 0) {
                Float yLine = null;
                for (int i = 0; i < childRenderers.size(); i++) {
                    if (childRenderers.get(i).getOccupiedArea().getBBox().getHeight() > 0) {
                        yLine = ((AbstractRenderer) childRenderers.get(i)).getFirstYLineRecursively();
                        if (yLine != null) {
                            break;
                        }
                    }
                }
                if (yLine != null) {
                    if (symbolRenderer instanceof TextRenderer) {
                        ((TextRenderer) symbolRenderer).moveYLineTo((float) yLine);
                    } else {
                        symbolRenderer.move(0, (float) yLine - symbolRenderer.getOccupiedArea().getBBox().getY());
                    }
                } else {
                    symbolRenderer.move(0, occupiedArea.getBBox().getY() + occupiedArea.getBBox().getHeight() -
                            (symbolRenderer.getOccupiedArea().getBBox().getY() + symbolRenderer.getOccupiedArea().getBBox().getHeight()));
                }
            } else {
                if (symbolRenderer instanceof TextRenderer) {
                    ((TextRenderer) symbolRenderer).moveYLineTo(occupiedArea.getBBox().getY() + occupiedArea.getBBox().getHeight() - calculateAscenderDescender()[0]);
                } else {
                    symbolRenderer.move(0, occupiedArea.getBBox().getY() + occupiedArea.getBBox().getHeight() -
                            symbolRenderer.getOccupiedArea().getBBox().getHeight() - symbolRenderer.getOccupiedArea().getBBox().getY());
                }
            }
            applyBorderBox(occupiedArea.getBBox(), true);
            applyMargins(occupiedArea.getBBox(), true);

            ListSymbolAlignment listSymbolAlignment = (ListSymbolAlignment)parent.<ListSymbolAlignment>getProperty(Property.LIST_SYMBOL_ALIGNMENT,
                    ListSymbolAlignment.RIGHT);
            float xPosition = x - symbolRenderer.getOccupiedArea().getBBox().getX();
            if (listSymbolAlignment == ListSymbolAlignment.RIGHT) {
                xPosition += symbolAreaWidth - symbolRenderer.getOccupiedArea().getBBox().getWidth();
            }
            symbolRenderer.move(xPosition, 0);

            if (symbolRenderer.getOccupiedArea().getBBox().getRight() > parent.getOccupiedArea().getBBox().getLeft()) {
                if (isTagged) {
                    tagPointer.addTag(0, isPossibleBadTagging(PdfName.Lbl) ? PdfName.P : PdfName.Lbl);
                }
                beginElementOpacityApplying(drawContext);
                symbolRenderer.draw(drawContext);
                endElementOpacityApplying(drawContext);
                if (isTagged) {
                    tagPointer.moveToParent();
                }
            }
        }

        if (isTagged) {
            tagPointer.moveToParent();
        }
    }

    @Override
    public IRenderer getNextRenderer() {
        return new ListItemRenderer((ListItem) modelElement);
    }

    @Override
    protected AbstractRenderer createSplitRenderer(int layoutResult) {
        ListItemRenderer splitRenderer = (ListItemRenderer) getNextRenderer();
        splitRenderer.parent = parent;
        splitRenderer.modelElement = modelElement;
        splitRenderer.occupiedArea = occupiedArea;
        splitRenderer.isLastRendererForModelElement = false;
        if (layoutResult == LayoutResult.PARTIAL) {
            splitRenderer.symbolRenderer = symbolRenderer;
            splitRenderer.symbolAreaWidth = symbolAreaWidth;
        }
        // TODO retain all the properties ?
        splitRenderer.setProperty(Property.MARGIN_LEFT, this.<Object>getProperty(Property.MARGIN_LEFT));
        return splitRenderer;
    }

    @Override
    protected AbstractRenderer createOverflowRenderer(int layoutResult) {
        ListItemRenderer overflowRenderer = (ListItemRenderer) getNextRenderer();
        overflowRenderer.parent = parent;
        overflowRenderer.modelElement = modelElement;
        if (layoutResult == LayoutResult.NOTHING) {
            overflowRenderer.symbolRenderer = symbolRenderer;
            overflowRenderer.symbolAreaWidth = symbolAreaWidth;
        }
        // TODO retain all the properties ?
        overflowRenderer.setProperty(Property.MARGIN_LEFT, this.<Object>getProperty(Property.MARGIN_LEFT));
        return overflowRenderer;
    }

    private void applyListSymbolPosition() {
        if (symbolRenderer != null) {
            ListSymbolPosition symbolPosition = (ListSymbolPosition) ListRenderer.getListItemOrListProperty(this, parent, Property.LIST_SYMBOL_POSITION);
            if (symbolPosition == ListSymbolPosition.INSIDE) {
                if (childRenderers.size() > 0 && childRenderers.get(0) instanceof ParagraphRenderer) {
                    ParagraphRenderer paragraphRenderer = (ParagraphRenderer) childRenderers.get(0);
                    Float symbolIndent = this.getPropertyAsFloat(Property.LIST_SYMBOL_INDENT);
                    if (symbolIndent != null) {
                        symbolRenderer.setProperty(Property.MARGIN_RIGHT, symbolIndent);
                    }
                    paragraphRenderer.childRenderers.add(0, symbolRenderer);
                    symbolAddedInside = true;
                } else if (childRenderers.size() > 0 && childRenderers.get(0) instanceof ImageRenderer) {
                    IRenderer paragraphRenderer = new Paragraph().setMargin(0).createRendererSubTree();
                    Float symbolIndent = this.getPropertyAsFloat(Property.LIST_SYMBOL_INDENT);
                    if (symbolIndent != null) {
                        symbolRenderer.setProperty(Property.MARGIN_RIGHT, symbolIndent);
                    }
                    paragraphRenderer.addChild(symbolRenderer);
                    paragraphRenderer.addChild(childRenderers.get(0));
                    childRenderers.set(0, paragraphRenderer);
                    symbolAddedInside = true;
                }
                if (!symbolAddedInside) {
                    IRenderer paragraphRenderer = new Paragraph().setMargin(0).createRendererSubTree();
                    Float symbolIndent = this.getPropertyAsFloat(Property.LIST_SYMBOL_INDENT);
                    if (symbolIndent != null) {
                        symbolRenderer.setProperty(Property.MARGIN_RIGHT, symbolIndent);
                    }
                    paragraphRenderer.addChild(symbolRenderer);
                    childRenderers.add(0, paragraphRenderer);
                    symbolAddedInside = true;
                }
            }
        }
    }

    private boolean isListSymbolEmpty(IRenderer listSymbolRenderer) {
        return listSymbolRenderer instanceof TextRenderer && ((TextRenderer) listSymbolRenderer).getText().toString().length() == 0;
    }

    private float[] calculateAscenderDescender() {
        PdfFont listItemFont = resolveFirstPdfFont();
        Float fontSize = this.getPropertyAsFloat(Property.FONT_SIZE);
        if (listItemFont != null && fontSize != null) {
            float[] ascenderDescender = TextRenderer.calculateAscenderDescender(listItemFont);
            return new float[] {(float)fontSize * ascenderDescender[0] / TextRenderer.TEXT_SPACE_COEFF, (float)fontSize * ascenderDescender[1] / TextRenderer.TEXT_SPACE_COEFF};
        }
        return new float[] {0, 0};
    }

}
