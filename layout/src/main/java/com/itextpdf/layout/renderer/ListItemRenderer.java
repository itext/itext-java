/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
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
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.layout.element.ListItem;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.property.BaseDirection;
import com.itextpdf.layout.property.ListSymbolAlignment;
import com.itextpdf.layout.property.ListSymbolPosition;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.property.UnitValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.layout.tagging.TaggingDummyElement;
import com.itextpdf.layout.tagging.LayoutTaggingHelper;
import com.itextpdf.layout.tagging.TaggingHintKey;
import java.util.Collections;
import java.util.List;

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
        if (drawContext.isTaggingEnabled()) {
            LayoutTaggingHelper taggingHelper = this.<LayoutTaggingHelper>getProperty(Property.TAGGING_HELPER);
            if (taggingHelper != null) {
                if (symbolRenderer != null) {
                    LayoutTaggingHelper.addTreeHints(taggingHelper, symbolRenderer);
                }
                if (taggingHelper.isArtifact(this)) {
                    taggingHelper.markArtifactHint(symbolRenderer);
                } else {
                    TaggingHintKey hintKey = LayoutTaggingHelper.getHintKey(this);
                    TaggingHintKey parentHint = taggingHelper.getAccessibleParentHint(hintKey);
                    if (parentHint != null && !(StandardRoles.LI.equals(parentHint.getAccessibleElement().getAccessibilityProperties().getRole()))) {
                        TaggingDummyElement listItemIntermediate = new TaggingDummyElement(StandardRoles.LI);
                        List<TaggingHintKey> intermediateKid = Collections.<TaggingHintKey>singletonList(LayoutTaggingHelper.getOrCreateHintKey(listItemIntermediate));
                        taggingHelper.replaceKidHint(hintKey, intermediateKid);
                        if (symbolRenderer != null) {
                            taggingHelper.addKidsHint(listItemIntermediate, Collections.<IRenderer>singletonList(symbolRenderer));
                        }
                        taggingHelper.addKidsHint(listItemIntermediate, Collections.<IRenderer>singletonList(this));
                    }
                }
            }
        }

        super.draw(drawContext);

        // It will be null in case of overflow (only the "split" part will contain symbol renderer.
        if (symbolRenderer != null && !symbolAddedInside) {
            boolean isRtl = BaseDirection.RIGHT_TO_LEFT.equals(this.<BaseDirection>getProperty(Property.BASE_DIRECTION));
            symbolRenderer.setParent(this);
            float x = isRtl ? occupiedArea.getBBox().getRight() : occupiedArea.getBBox().getLeft();
            ListSymbolPosition symbolPosition = (ListSymbolPosition) ListRenderer.getListItemOrListProperty(this, parent, Property.LIST_SYMBOL_POSITION);
            if (symbolPosition != ListSymbolPosition.DEFAULT) {
                Float symbolIndent = this.getPropertyAsFloat(Property.LIST_SYMBOL_INDENT);
                if (isRtl) {
                    x += (symbolAreaWidth + (float) (symbolIndent == null ? 0 : symbolIndent));
                } else {
                    x -= (symbolAreaWidth + (float) (symbolIndent == null ? 0 : symbolIndent));
                }
                if (symbolPosition == ListSymbolPosition.OUTSIDE) {
                    if (isRtl) {
                        UnitValue marginRightUV = this.getPropertyAsUnitValue(Property.MARGIN_RIGHT);
                        if (!marginRightUV.isPointValue()) {
                            Logger logger = LoggerFactory.getLogger(ListItemRenderer.class);
                            logger.error(MessageFormatUtil.format(LogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED, Property.MARGIN_RIGHT));
                        }
                        x -= marginRightUV.getValue();
                    } else {
                        UnitValue marginLeftUV = this.getPropertyAsUnitValue(Property.MARGIN_LEFT);
                        if (!marginLeftUV.isPointValue()) {
                            Logger logger = LoggerFactory.getLogger(ListItemRenderer.class);
                            logger.error(MessageFormatUtil.format(LogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED, Property.MARGIN_LEFT));
                        }
                        x += marginLeftUV.getValue();
                    }
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
                    if (symbolRenderer instanceof LineRenderer) {
                        symbolRenderer.move(0, (float) yLine - ((LineRenderer) symbolRenderer).getYLine());
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
                    isRtl ? ListSymbolAlignment.LEFT : ListSymbolAlignment.RIGHT);
            float dxPosition = x - symbolRenderer.getOccupiedArea().getBBox().getX();
            if (listSymbolAlignment == ListSymbolAlignment.RIGHT) {
                if (!isRtl) {
                    dxPosition += symbolAreaWidth - symbolRenderer.getOccupiedArea().getBBox().getWidth();
                }
            } else if (listSymbolAlignment == ListSymbolAlignment.LEFT) {
                if (isRtl) {
                    dxPosition -= (symbolAreaWidth - symbolRenderer.getOccupiedArea().getBBox().getWidth());
                }
            }
            if (symbolRenderer instanceof LineRenderer) {
                if (isRtl) {
                    symbolRenderer.move(dxPosition - symbolRenderer.getOccupiedArea().getBBox().getWidth(), 0);
                } else {
                    symbolRenderer.move(dxPosition, 0);
                }
            } else {
                symbolRenderer.move(dxPosition, 0);
            }

            if (symbolRenderer.getOccupiedArea().getBBox().getRight() > parent.getOccupiedArea().getBBox().getLeft()) {
                beginElementOpacityApplying(drawContext);
                symbolRenderer.draw(drawContext);
                endElementOpacityApplying(drawContext);
            }
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
        splitRenderer.addAllProperties(getOwnProperties());
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
        overflowRenderer.addAllProperties(getOwnProperties());
        return overflowRenderer;
    }

    private void applyListSymbolPosition() {
        if (symbolRenderer != null) {
            ListSymbolPosition symbolPosition = (ListSymbolPosition) ListRenderer.getListItemOrListProperty(this, parent, Property.LIST_SYMBOL_POSITION);
            if (symbolPosition == ListSymbolPosition.INSIDE) {
                boolean isRtl = BaseDirection.RIGHT_TO_LEFT.equals(this.<BaseDirection>getProperty(Property.BASE_DIRECTION));
                if (childRenderers.size() > 0 && childRenderers.get(0) instanceof ParagraphRenderer) {
                    ParagraphRenderer paragraphRenderer = (ParagraphRenderer) childRenderers.get(0);
                    Float symbolIndent = this.getPropertyAsFloat(Property.LIST_SYMBOL_INDENT);

                        if (symbolRenderer instanceof LineRenderer) {
                            if (symbolIndent != null) {
                                symbolRenderer.getChildRenderers().get(1).setProperty(isRtl ? Property.MARGIN_LEFT : Property.MARGIN_RIGHT, UnitValue.createPointValue((float) symbolIndent));
                            }
                            for (IRenderer childRenderer: symbolRenderer.getChildRenderers()) {
                                paragraphRenderer.childRenderers.add(0, childRenderer);
                            }
                        } else {
                            if (symbolIndent != null) {
                                symbolRenderer.setProperty(isRtl ? Property.MARGIN_LEFT : Property.MARGIN_RIGHT, UnitValue.createPointValue((float) symbolIndent));
                            }
                            paragraphRenderer.childRenderers.add(0, symbolRenderer);
                        }
                    symbolAddedInside = true;
                } else if (childRenderers.size() > 0 && childRenderers.get(0) instanceof ImageRenderer) {
                    Paragraph p = new Paragraph();
                    p.getAccessibilityProperties().setRole(null);
                    IRenderer paragraphRenderer = p.setMargin(0).createRendererSubTree();
                    Float symbolIndent = this.getPropertyAsFloat(Property.LIST_SYMBOL_INDENT);
                    if (symbolIndent != null) {
                        symbolRenderer.setProperty(Property.MARGIN_RIGHT, UnitValue.createPointValue((float) symbolIndent));
                    }
                    paragraphRenderer.addChild(symbolRenderer);
                    paragraphRenderer.addChild(childRenderers.get(0));
                    childRenderers.set(0, paragraphRenderer);
                    symbolAddedInside = true;
                }
                if (!symbolAddedInside) {
                    Paragraph p = new Paragraph();
                    p.getAccessibilityProperties().setRole(null);
                    IRenderer paragraphRenderer = p.setMargin(0).createRendererSubTree();
                    Float symbolIndent = this.getPropertyAsFloat(Property.LIST_SYMBOL_INDENT);
                    if (symbolIndent != null) {
                        symbolRenderer.setProperty(Property.MARGIN_RIGHT, UnitValue.createPointValue((float) symbolIndent));
                    }
                    paragraphRenderer.addChild(symbolRenderer);
                    childRenderers.add(0, paragraphRenderer);
                    symbolAddedInside = true;
                }
            }
        }
    }

    private boolean isListSymbolEmpty(IRenderer listSymbolRenderer) {
        if (listSymbolRenderer instanceof TextRenderer) {
            return ((TextRenderer) listSymbolRenderer).getText().toString().length() == 0;
        } else if (listSymbolRenderer instanceof LineRenderer) {
            return ((TextRenderer) listSymbolRenderer.getChildRenderers().get(1)).getText().toString().length() == 0;
        }
        return false;
    }

    private float[] calculateAscenderDescender() {
        PdfFont listItemFont = resolveFirstPdfFont();
        UnitValue fontSize = this.getPropertyAsUnitValue(Property.FONT_SIZE);
        if (listItemFont != null && fontSize != null) {
            if (!fontSize.isPointValue()) {
                Logger logger = LoggerFactory.getLogger(ListItemRenderer.class);
                logger.error(MessageFormatUtil.format(LogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED, Property.FONT_SIZE));
            }
            float[] ascenderDescender = TextRenderer.calculateAscenderDescender(listItemFont);
            return new float[] {fontSize.getValue() * ascenderDescender[0] / TextRenderer.TEXT_SPACE_COEFF, fontSize.getValue() * ascenderDescender[1] / TextRenderer.TEXT_SPACE_COEFF};
        }
        return new float[] {0, 0};
    }

}
