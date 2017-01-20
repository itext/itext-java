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
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.layout.LineLayoutResult;
import com.itextpdf.layout.layout.MinMaxWidthLayoutResult;
import com.itextpdf.layout.margincollapse.MarginsCollapseHandler;
import com.itextpdf.layout.property.Leading;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.FloatPropertyValue;
import com.itextpdf.layout.minmaxwidth.MinMaxWidth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * This class represents the {@link IRenderer renderer} object for a {@link Paragraph}
 * object. It will draw the glyphs of the textual content on the {@link DrawContext}.
 */
public class ParagraphRenderer extends BlockRenderer {

    protected float previousDescent = 0;
    protected List<LineRenderer> lines = null;

    /**
     * Creates a ParagraphRenderer from its corresponding layout object.
     *
     * @param modelElement the {@link com.itextpdf.layout.element.Paragraph} which this object should manage
     */
    public ParagraphRenderer(Paragraph modelElement) {
        super(modelElement);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LayoutResult layout(LayoutContext layoutContext) {
        overrideHeightProperties();
        boolean wasHeightClipped = false;
        int pageNumber = layoutContext.getArea().getPageNumber();
        boolean anythingPlaced = false;
        boolean firstLineInBox = true;
        LineRenderer currentRenderer = (LineRenderer) new LineRenderer().setParent(this);
        Rectangle parentBBox = layoutContext.getArea().getBBox().clone();
        Map<Rectangle, Float> floatRenderers = layoutContext.getFloatedRenderers();

        Float blockWidth = retrieveWidth(parentBBox.getWidth());
        FloatPropertyValue floatPropertyValue = getProperty(Property.FLOAT);
        if (floatPropertyValue != null) {
            if (floatPropertyValue.equals(FloatPropertyValue.LEFT)) {
                setProperty(Property.HORIZONTAL_ALIGNMENT, HorizontalAlignment.LEFT);
            } else if (floatPropertyValue.equals(FloatPropertyValue.RIGHT)) {
                setProperty(Property.HORIZONTAL_ALIGNMENT, HorizontalAlignment.RIGHT);
            }
        }

        if (0 == childRenderers.size()) {
            anythingPlaced = true;
            currentRenderer = null;
        }

        boolean isPositioned = isPositioned();

        if (this.<Float>getProperty(Property.ROTATION_ANGLE) != null) {
            parentBBox.moveDown(AbstractRenderer.INF - parentBBox.getHeight()).setHeight(AbstractRenderer.INF);
        }
        MarginsCollapseHandler marginsCollapseHandler = null;
        boolean marginsCollapsingEnabled = Boolean.TRUE.equals(getPropertyAsBoolean(Property.COLLAPSING_MARGINS));
        if (marginsCollapsingEnabled) {
            marginsCollapseHandler = new MarginsCollapseHandler(this, layoutContext.getMarginsCollapseInfo());
            marginsCollapseHandler.startMarginsCollapse(parentBBox);
        }
        Border[] borders = getBorders();
        float[] paddings = getPaddings();
        float additionalWidth = applyBordersPaddingsMargins(parentBBox, borders, paddings);
        if (blockWidth != null && (blockWidth < parentBBox.getWidth() || isPositioned)) {
            parentBBox.setWidth((float) blockWidth);
        }

        MinMaxWidth minMaxWidth = new MinMaxWidth(additionalWidth, layoutContext.getArea().getBBox().getWidth());
        AbstractWidthHandler widthHandler = new MaxMaxWidthHandler(minMaxWidth);

        if (floatRenderers != null) {
            adjustLineRendererAccordingToFloatRenderers(floatRenderers, parentBBox, layoutContext.getArea().getBBox().getWidth() - additionalWidth);
        }

        Float blockMaxHeight = retrieveMaxHeight();
        if (null != blockMaxHeight && parentBBox.getHeight() > blockMaxHeight) {
            float heightDelta = parentBBox.getHeight() - (float) blockMaxHeight;
            if (marginsCollapsingEnabled) {
                marginsCollapseHandler.processFixedHeightAdjustment(heightDelta);
            }
            parentBBox.moveUp(heightDelta).setHeight((float) blockMaxHeight);
            wasHeightClipped = true;
        }

        List<Rectangle> areas;
        if (isPositioned) {
            areas = Collections.singletonList(parentBBox);
        } else {
            areas = initElementAreas(new LayoutArea(pageNumber, parentBBox));
        }

        occupiedArea = new LayoutArea(pageNumber, new Rectangle(parentBBox.getX(), parentBBox.getY() + parentBBox.getHeight(), parentBBox.getWidth(), 0));
        shrinkOccupiedAreaForAbsolutePosition();

        int currentAreaPos = 0;
        Rectangle layoutBox = areas.get(0).clone();
        lines = new ArrayList<>();
        for (IRenderer child : childRenderers) {
            currentRenderer.addChild(child);
        }

        float lastYLine = layoutBox.getY() + layoutBox.getHeight();
        Leading leading = this.<Leading>getProperty(Property.LEADING);
        float leadingValue = 0;

        float lastLineHeight = 0;

        if (marginsCollapsingEnabled && childRenderers.size() > 0) {
            // passing null is sufficient to notify that there is a kid, however we don't care about it and it's margins
            marginsCollapseHandler.startChildMarginsHandling(null, layoutBox);
        }
        while (currentRenderer != null) {
            currentRenderer.setProperty(Property.TAB_DEFAULT, this.getPropertyAsFloat(Property.TAB_DEFAULT));
            currentRenderer.setProperty(Property.TAB_STOPS, this.<Object>getProperty(Property.TAB_STOPS));

            float lineIndent = anythingPlaced ? 0 : (float) this.getPropertyAsFloat(Property.FIRST_LINE_INDENT);
            float childBBoxWidth = layoutBox.getWidth() - lineIndent;
            Rectangle childLayoutBox = new Rectangle(layoutBox.getX() + lineIndent, layoutBox.getY(), childBBoxWidth, layoutBox.getHeight());
            
            LineLayoutResult result = ((LineRenderer) currentRenderer.setParent(this)).layout(new LayoutContext(
                    new LayoutArea(pageNumber, childLayoutBox), layoutContext.getMarginsCollapseInfo(),
                    floatRenderers));

            float minChildWidth = 0;
            float maxChildWidth = 0;
            if (result instanceof MinMaxWidthLayoutResult) {
                minChildWidth = ((MinMaxWidthLayoutResult)result).getNotNullMinMaxWidth(childBBoxWidth).getMinWidth();
                maxChildWidth = ((MinMaxWidthLayoutResult)result).getNotNullMinMaxWidth(childBBoxWidth).getMaxWidth();
            }

            widthHandler.updateMinChildWidth(minChildWidth + lineIndent);
            widthHandler.updateMaxChildWidth(maxChildWidth + lineIndent);

            LineRenderer processedRenderer = null;
            floatRenderers = result.getFloatRenderers();
            if (result.getStatus() == LayoutResult.FULL) {
                processedRenderer = currentRenderer;
            } else if (result.getStatus() == LayoutResult.PARTIAL) {
                processedRenderer = (LineRenderer) result.getSplitRenderer();
            }

            TextAlignment textAlignment = (TextAlignment) this.<TextAlignment>getProperty(Property.TEXT_ALIGNMENT, TextAlignment.LEFT);
            if (result.getStatus() == LayoutResult.PARTIAL && textAlignment == TextAlignment.JUSTIFIED && !result.isSplitForcedByNewline() ||
                    textAlignment == TextAlignment.JUSTIFIED_ALL) {
                if (processedRenderer != null) {
                    processedRenderer.justify(layoutBox.getWidth() - lineIndent);
                }
            } else if (textAlignment != TextAlignment.LEFT && processedRenderer != null) {
                float maxFloatWidth = 0;
                for (Rectangle floatRenderer : floatRenderers.keySet()) {
                    if (floatRenderer.getWidth() > maxFloatWidth) {
                        maxFloatWidth = floatRenderer.getWidth();
                    }
                }
                float deltaX = childBBoxWidth - maxFloatWidth - processedRenderer.getOccupiedArea().getBBox().getWidth();
                switch (textAlignment) {
                    case RIGHT:
                        processedRenderer.move(deltaX, 0);
                        break;
                    case CENTER:
                        processedRenderer.move(deltaX / 2, 0);
                        break;
                }
            }

            leadingValue = processedRenderer != null && leading != null ? processedRenderer.getLeadingValue(leading) : 0;
            if (processedRenderer != null && processedRenderer.containsImage()) {
                leadingValue -= previousDescent;
            }
            boolean doesNotFit = result.getStatus() == LayoutResult.NOTHING;
            float deltaY = 0;
            if (!doesNotFit) {
                lastLineHeight = processedRenderer.getOccupiedArea().getBBox().getHeight();
                if (result.getCurrentLineFloatRenderers().size() == 0) {
                    deltaY = lastYLine - leadingValue - processedRenderer.getYLine();
                }

                // for the first and last line in a paragraph, leading is smaller
                if (firstLineInBox)
                    deltaY = -(leadingValue - lastLineHeight) / 2;
                doesNotFit = leading != null && processedRenderer.getOccupiedArea().getBBox().getY() + deltaY < layoutBox.getY();
            }

            if (doesNotFit) {
                if (currentAreaPos + 1 < areas.size()) {
                    layoutBox = areas.get(++currentAreaPos).clone();
                    lastYLine = layoutBox.getY() + layoutBox.getHeight();
                    firstLineInBox = true;
                } else {
                    boolean keepTogether = isKeepTogether();
                    if (keepTogether) {
                        return new MinMaxWidthLayoutResult(LayoutResult.NOTHING, null, null, this, null == result.getCauseOfNothing() ? this : result.getCauseOfNothing(), floatRenderers);
                    } else {
                        if (marginsCollapsingEnabled) {
                            if (anythingPlaced) {
                                marginsCollapseHandler.endChildMarginsHandling(layoutBox);
                            }
                            marginsCollapseHandler.endMarginsCollapse(layoutBox);
                        }
                        ParagraphRenderer[] split = split();
                        split[0].lines = lines;
                        for (LineRenderer line : lines) {
                            split[0].childRenderers.addAll(line.getChildRenderers());
                        }
                        if (processedRenderer != null) {
                            split[1].childRenderers.addAll(processedRenderer.getChildRenderers());
                        }
                        if (result.getOverflowRenderer() != null) {
                            split[1].childRenderers.addAll(result.getOverflowRenderer().getChildRenderers());
                        }
                        if (hasProperty(Property.MAX_HEIGHT)) {
                            if (isPositioned) {
                                correctPositionedLayout(layoutBox);
                            }
                            split[1].setProperty(Property.MAX_HEIGHT, retrieveMaxHeight() - occupiedArea.getBBox().getHeight());
                        }
                        if (hasProperty(Property.MIN_HEIGHT)) {
                            split[1].setProperty(Property.MIN_HEIGHT, retrieveMinHeight() - occupiedArea.getBBox().getHeight());
                        }
                        if (hasProperty(Property.HEIGHT)) {
                            split[1].setProperty(Property.HEIGHT, retrieveHeight() - occupiedArea.getBBox().getHeight());
                        }
                        if (wasHeightClipped) {
                            split[0].getOccupiedArea().getBBox()
                                    .moveDown((float) blockMaxHeight - occupiedArea.getBBox().getHeight())
                                    .setHeight((float) blockMaxHeight);
                            Logger logger = LoggerFactory.getLogger(ParagraphRenderer.class);
                            logger.warn(LogMessageConstant.CLIP_ELEMENT);
                        }
                        applyPaddings(occupiedArea.getBBox(), paddings, true);
                        applyBorderBox(occupiedArea.getBBox(), borders, true);
                        applyMargins(occupiedArea.getBBox(), true);

                        if (wasHeightClipped) {
                            return new MinMaxWidthLayoutResult(LayoutResult.FULL, occupiedArea, split[0], null).setMinMaxWidth(minMaxWidth);
                        } else if (anythingPlaced) {
                            return new MinMaxWidthLayoutResult(LayoutResult.PARTIAL, occupiedArea, split[0], split[1]).setMinMaxWidth(minMaxWidth);
                        } else {
                            if (Boolean.TRUE.equals(getPropertyAsBoolean(Property.FORCED_PLACEMENT))) {
                                occupiedArea.setBBox(Rectangle.getCommonRectangle(occupiedArea.getBBox(), currentRenderer.getOccupiedArea().getBBox()));
                                parent.setProperty(Property.FULL, true);
                                lines.add(currentRenderer);
                                // Force placement of children we have and do not force placement of the others
                                if (LayoutResult.PARTIAL == result.getStatus()) {
                                    IRenderer childNotRendered = result.getCauseOfNothing();
                                    int firstNotRendered = currentRenderer.childRenderers.indexOf(childNotRendered);
                                    currentRenderer.childRenderers.retainAll(currentRenderer.childRenderers.subList(0, firstNotRendered));
                                    split[1].childRenderers.removeAll(split[1].childRenderers.subList(0, firstNotRendered));
                                    return new MinMaxWidthLayoutResult(LayoutResult.PARTIAL, occupiedArea, this, split[1], null, floatRenderers).setMinMaxWidth(minMaxWidth);
                                } else {
                                    return new MinMaxWidthLayoutResult(LayoutResult.FULL, occupiedArea, null, null, this, floatRenderers).setMinMaxWidth(minMaxWidth);
                                }
                            } else {
                                return new MinMaxWidthLayoutResult(LayoutResult.NOTHING, null, null, this, null == result.getCauseOfNothing() ? this : result.getCauseOfNothing(), floatRenderers);
                            }
                        }
                    }
                }
            } else {
                if (leading != null) {
                    processedRenderer.move(0, deltaY);
                    lastYLine = processedRenderer.getYLine();
                }
                occupiedArea.setBBox(Rectangle.getCommonRectangle(occupiedArea.getBBox(), processedRenderer.getOccupiedArea().getBBox()));
                layoutBox.setHeight(processedRenderer.getOccupiedArea().getBBox().getY() - layoutBox.getY());
                lines.add(processedRenderer);

                anythingPlaced = true;
                firstLineInBox = false;

                currentRenderer = (LineRenderer) result.getOverflowRenderer();
                previousDescent = processedRenderer.getMaxDescent();
            }
        }
        
        if (marginsCollapsingEnabled) {
            if (childRenderers.size() > 0) {
                marginsCollapseHandler.endChildMarginsHandling(layoutBox);
            }
            marginsCollapseHandler.endMarginsCollapse(layoutBox);
        }

        float moveDown = Math.min((leadingValue - lastLineHeight) / 2, occupiedArea.getBBox().getY() - layoutBox.getY());
        occupiedArea.getBBox().moveDown(moveDown);
        occupiedArea.getBBox().setHeight(occupiedArea.getBBox().getHeight() + moveDown);

        IRenderer overflowRenderer = null;
        Float blockMinHeight = retrieveMinHeight();
        if (null != blockMinHeight && blockMinHeight > occupiedArea.getBBox().getHeight()) {
            float blockBottom = occupiedArea.getBBox().getBottom() - ((float) blockMinHeight - occupiedArea.getBBox().getHeight());
            if (blockBottom >= layoutContext.getArea().getBBox().getBottom()) {
                occupiedArea.getBBox().setY(blockBottom).setHeight((float) blockMinHeight);
            } else {
                occupiedArea.getBBox()
                        .increaseHeight(occupiedArea.getBBox().getBottom() - layoutContext.getArea().getBBox().getBottom())
                        .setY(layoutContext.getArea().getBBox().getBottom());
                overflowRenderer = createOverflowRenderer(parent);
                overflowRenderer.setProperty(Property.MIN_HEIGHT, (float) blockMinHeight - occupiedArea.getBBox().getHeight());
                if (hasProperty(Property.HEIGHT)) {
                    overflowRenderer.setProperty(Property.HEIGHT, retrieveHeight() - occupiedArea.getBBox().getHeight());
                }
            }
            applyVerticalAlignment();
        }
        if (isPositioned) {
            correctPositionedLayout(layoutBox);
        }

        applyPaddings(occupiedArea.getBBox(), paddings, true);
        applyBorderBox(occupiedArea.getBBox(), borders, true);
        applyMargins(occupiedArea.getBBox(), true);
        if (this.<Float>getProperty(Property.ROTATION_ANGLE) != null) {
            applyRotationLayout(layoutContext.getArea().getBBox().clone());
            if (isNotFittingLayoutArea(layoutContext.getArea())) {
                if (!Boolean.TRUE.equals(getPropertyAsBoolean(Property.FORCED_PLACEMENT))) {
                    return new MinMaxWidthLayoutResult(LayoutResult.NOTHING, null, null, this, this);
                }
            }
        }

        reduceFloatRenderersOccupiedArea(floatRenderers);

        LayoutArea editedArea = applyFloatPropertyOnCurrentArea(floatRenderers,layoutContext.getArea().getBBox().getWidth());

        if (editedArea == null) {
            editedArea = occupiedArea;
        }
        if (null == overflowRenderer) {
            return new MinMaxWidthLayoutResult(LayoutResult.FULL, editedArea, null, null, null, floatRenderers).setMinMaxWidth(minMaxWidth);
        } else {
            return new MinMaxWidthLayoutResult(LayoutResult.PARTIAL, editedArea, this, overflowRenderer, null, floatRenderers).setMinMaxWidth(minMaxWidth);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IRenderer getNextRenderer() {
        return new ParagraphRenderer((Paragraph) modelElement);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T1> T1 getDefaultProperty(int property) {
        if ((property == Property.MARGIN_TOP || property == Property.MARGIN_BOTTOM) && parent instanceof CellRenderer) {
            return (T1) (Object) 0f;
        }
        return super.<T1>getDefaultProperty(property);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (lines != null && lines.size() > 0) {
            for (int i = 0; i < lines.size(); i++) {
                if (i > 0) {
                    sb.append("\n");
                }
                sb.append(lines.get(i).toString());
            }
        } else {
            for (IRenderer renderer : childRenderers) {
                sb.append(renderer.toString());
            }
        }
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void drawChildren(DrawContext drawContext) {
        if (lines != null) {
            for (LineRenderer line : lines) {
                line.draw(drawContext);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void move(float dxRight, float dyUp) {
        occupiedArea.getBBox().moveRight(dxRight);
        occupiedArea.getBBox().moveUp(dyUp);
        if (null != lines) {
            for (LineRenderer line : lines) {
                line.move(dxRight, dyUp);
            }
        }
    }

    /**
     * Gets the lines which are the result of the {@link #layout(LayoutContext)}.
     * @return paragraph lines, or <code>null</code> if layout hasn't been called yet
     */
    public List<LineRenderer> getLines() {
        return lines;
    }

    @Override
    protected Float getFirstYLineRecursively() {
        if (lines == null || lines.size() == 0) {
            return null;
        }
        return lines.get(0).getFirstYLineRecursively();
    }

    @Deprecated
    protected ParagraphRenderer createOverflowRenderer() {
        return (ParagraphRenderer) getNextRenderer();
    }

    @Deprecated
    protected ParagraphRenderer createSplitRenderer() {
        return (ParagraphRenderer) getNextRenderer();
    }

    protected ParagraphRenderer createOverflowRenderer(IRenderer parent) {
        ParagraphRenderer overflowRenderer = createOverflowRenderer();
        overflowRenderer.parent = parent;
        fixOverflowRenderer(overflowRenderer);
        return overflowRenderer;
    }

    protected ParagraphRenderer createSplitRenderer(IRenderer parent) {
        ParagraphRenderer splitRenderer = createSplitRenderer();
        splitRenderer.parent = parent;
        splitRenderer.properties = new HashMap<>(properties);
        return splitRenderer;
    }


    @Override
    protected MinMaxWidth getMinMaxWidth(float availableWidth) {
        MinMaxWidthLayoutResult result = (MinMaxWidthLayoutResult)layout(new LayoutContext(new LayoutArea(1, new Rectangle(availableWidth, AbstractRenderer.INF))));
        return countRotationMinMaxWidth(correctMinMaxWidth(result.getNotNullMinMaxWidth(availableWidth)));
    }

    protected ParagraphRenderer[] split() {
        ParagraphRenderer splitRenderer = createSplitRenderer(parent);
        splitRenderer.occupiedArea = occupiedArea;
        splitRenderer.isLastRendererForModelElement = false;

        ParagraphRenderer overflowRenderer = createOverflowRenderer(parent);

        return new ParagraphRenderer[]{splitRenderer, overflowRenderer};
    }

    private void fixOverflowRenderer(ParagraphRenderer overflowRenderer) {
        // Reset first line indent in case of overflow.
        float firstLineIndent = (float) overflowRenderer.getPropertyAsFloat(Property.FIRST_LINE_INDENT);
        if (firstLineIndent != 0) {
            overflowRenderer.setProperty(Property.FIRST_LINE_INDENT, 0);
        }
    }
}
