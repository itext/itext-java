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

import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.layout.element.ListItem;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.properties.BaseDirection;
import com.itextpdf.layout.properties.ListSymbolAlignment;
import com.itextpdf.layout.properties.ListSymbolPosition;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.tagging.TaggingDummyElement;
import com.itextpdf.layout.tagging.LayoutTaggingHelper;
import com.itextpdf.layout.tagging.TaggingHintKey;
import com.itextpdf.commons.utils.MessageFormatUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

public class ListItemRenderer extends DivRenderer {

    protected IRenderer symbolRenderer;
    protected float symbolAreaWidth;
    private boolean symbolAddedInside;

    /**
     * Creates a ListItemRenderer from its corresponding layout object.
     *
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
            logger.error(MessageFormatUtil.format(IoLogMessageConstant.OCCUPIED_AREA_HAS_NOT_BEEN_INITIALIZED,
                    "Drawing won't be performed."));
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
            boolean isRtl = BaseDirection.RIGHT_TO_LEFT == this.<BaseDirection>getProperty(Property.BASE_DIRECTION);
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
                            logger.error(
                                    MessageFormatUtil.format(IoLogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED,
                                            Property.MARGIN_RIGHT));
                        }
                        x -= marginRightUV.getValue();
                    } else {
                        UnitValue marginLeftUV = this.getPropertyAsUnitValue(Property.MARGIN_LEFT);
                        if (!marginLeftUV.isPointValue()) {
                            Logger logger = LoggerFactory.getLogger(ListItemRenderer.class);
                            logger.error(
                                    MessageFormatUtil.format(IoLogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED,
                                            Property.MARGIN_LEFT));
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

            ListSymbolAlignment listSymbolAlignment = (ListSymbolAlignment) parent.<ListSymbolAlignment>getProperty(Property.LIST_SYMBOL_ALIGNMENT,
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

            // consider page area without margins
            RootRenderer root = getRootRenderer();
            Rectangle effectiveArea = root.getCurrentArea().getBBox();

            // symbols are not drawn here, because they are in page margins
            if (!isRtl && symbolRenderer.getOccupiedArea().getBBox().getRight() > effectiveArea.getLeft()
                    || isRtl && symbolRenderer.getOccupiedArea().getBBox().getLeft() < effectiveArea.getRight()) {
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
        splitRenderer.symbolAddedInside = symbolAddedInside;
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
                    // TODO DEVSIX-6876 LIST_SYMBOL_INDENT is not inherited
                    Float symbolIndent = this.getPropertyAsFloat(Property.LIST_SYMBOL_INDENT);
                    if (symbolRenderer instanceof LineRenderer) {
                        if (symbolIndent != null) {
                            symbolRenderer.getChildRenderers().get(1).setProperty(isRtl ? Property.MARGIN_LEFT : Property.MARGIN_RIGHT, UnitValue.createPointValue((float) symbolIndent));
                        }
                        if (!paragraphRenderer.childRenderers.contains(symbolRenderer.getChildRenderers().get(1))) {
                            for (IRenderer childRenderer : symbolRenderer.getChildRenderers()) {
                                paragraphRenderer.childRenderers.add(0, childRenderer);
                            }
                        }
                    } else {
                        if (symbolIndent != null) {
                            symbolRenderer.setProperty(isRtl ? Property.MARGIN_LEFT : Property.MARGIN_RIGHT, UnitValue.createPointValue((float) symbolIndent));
                        }
                        if (!paragraphRenderer.childRenderers.contains(symbolRenderer)) {
                            paragraphRenderer.childRenderers.add(0, symbolRenderer);
                        }
                    }
                    symbolAddedInside = true;
                } else if (childRenderers.size() > 0 && childRenderers.get(0) instanceof ImageRenderer) {
                    IRenderer paragraphRenderer = renderSymbolInNeutralParagraph();
                    paragraphRenderer.addChild(childRenderers.get(0));
                    childRenderers.set(0, paragraphRenderer);
                    symbolAddedInside = true;
                }
                if (!symbolAddedInside) {
                    IRenderer paragraphRenderer = renderSymbolInNeutralParagraph();
                    childRenderers.add(0, paragraphRenderer);
                    symbolAddedInside = true;
                }
            }
        }
    }

    private IRenderer renderSymbolInNeutralParagraph() {
        Paragraph p = new Paragraph().setNeutralRole();
        IRenderer paragraphRenderer = p.setMargin(0).createRendererSubTree();
        Float symbolIndent = (Float) ListRenderer.getListItemOrListProperty(this, parent, Property.LIST_SYMBOL_INDENT);
        if (symbolIndent != null) {
            // cast to float is necessary for autoporting reasons
            symbolRenderer.setProperty(Property.MARGIN_RIGHT, UnitValue.createPointValue((float) symbolIndent));
        }
        paragraphRenderer.addChild(symbolRenderer);
        return paragraphRenderer;
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
                logger.error(MessageFormatUtil.format(IoLogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED,
                        Property.FONT_SIZE));
            }
            float[] ascenderDescender = TextRenderer.calculateAscenderDescender(listItemFont);
            return new float[]{
                    fontSize.getValue() * FontProgram.convertTextSpaceToGlyphSpace(ascenderDescender[0]),
                    fontSize.getValue() * FontProgram.convertTextSpaceToGlyphSpace(ascenderDescender[1])
            };
        }
        return new float[]{0, 0};
    }
}
