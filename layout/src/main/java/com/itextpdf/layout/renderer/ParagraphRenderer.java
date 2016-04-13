/*
    $Id$

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV
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

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.Property;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.layout.LineLayoutResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ParagraphRenderer extends BlockRenderer {

    protected float previousDescent = 0;
    protected List<LineRenderer> lines = null;

    public ParagraphRenderer(Paragraph modelElement) {
        super(modelElement);
    }

    @Override
    public LayoutResult layout(LayoutContext layoutContext) {
        int pageNumber = layoutContext.getArea().getPageNumber();

        Rectangle parentBBox = layoutContext.getArea().getBBox().clone();
        if (getProperty(Property.ROTATION_ANGLE) != null) {
            parentBBox.moveDown(AbstractRenderer.INF - parentBBox.getHeight()).setHeight(AbstractRenderer.INF);
        }
        applyMargins(parentBBox, false);
        applyBorderBox(parentBBox, false);

        if (isPositioned()) {
            float x = getPropertyAsFloat(Property.X);
            float relativeX = isFixedLayout() ? 0 : parentBBox.getX();
            parentBBox.setX(relativeX + x);
        }

        Float blockWidth = retrieveWidth(parentBBox.getWidth());
        if (blockWidth != null && (blockWidth < parentBBox.getWidth() || isPositioned())) {
            parentBBox.setWidth(blockWidth);
        }
        applyPaddings(parentBBox, false);

        List<Rectangle> areas;
        if (isPositioned()) {
            areas = Collections.singletonList(parentBBox);
        } else {
            areas = initElementAreas(new LayoutArea(pageNumber, parentBBox));
        }

        occupiedArea = new LayoutArea(pageNumber, new Rectangle(parentBBox.getX(), parentBBox.getY() + parentBBox.getHeight(), parentBBox.getWidth(), 0));

        int currentAreaPos = 0;
        Rectangle layoutBox = areas.get(0).clone();

        boolean anythingPlaced = false;
        boolean firstLineInBox = true;

        lines = new ArrayList<>();
        LineRenderer currentRenderer = (LineRenderer) new LineRenderer().setParent(this);
        for (IRenderer child : childRenderers) {
            currentRenderer.addChild(child);
        }

        if (0 == childRenderers.size()) {
            anythingPlaced = true;
            currentRenderer = null;
            setProperty(Property.MARGIN_TOP, 0);
            setProperty(Property.MARGIN_RIGHT, 0);
            setProperty(Property.MARGIN_BOTTOM, 0);
            setProperty(Property.MARGIN_LEFT, 0);
            setProperty(Property.PADDING_TOP, 0);
            setProperty(Property.PADDING_RIGHT, 0);
            setProperty(Property.PADDING_BOTTOM, 0);
            setProperty(Property.PADDING_LEFT, 0);
            setProperty(Property.BORDER, Border.NO_BORDER);
        }

        float lastYLine = layoutBox.getY() + layoutBox.getHeight();
        Property.Leading leading = getProperty(Property.LEADING);
        float leadingValue = 0;

        float lastLineHeight = 0;

        while (currentRenderer != null) {
            currentRenderer.setProperty(Property.TAB_DEFAULT, getPropertyAsFloat(Property.TAB_DEFAULT));
            currentRenderer.setProperty(Property.TAB_STOPS, getProperty(Property.TAB_STOPS));

            float lineIndent = anythingPlaced ? 0 : getPropertyAsFloat(Property.FIRST_LINE_INDENT);
            float availableWidth = layoutBox.getWidth() - lineIndent;
            Rectangle childLayoutBox = new Rectangle(layoutBox.getX() + lineIndent, layoutBox.getY(), availableWidth, layoutBox.getHeight());
            LineLayoutResult result = currentRenderer.layout(new LayoutContext(new LayoutArea(pageNumber, childLayoutBox)));

            LineRenderer processedRenderer = null;
            if (result.getStatus() == LayoutResult.FULL) {
                processedRenderer = currentRenderer;
            } else if (result.getStatus() == LayoutResult.PARTIAL) {
                processedRenderer = (LineRenderer) result.getSplitRenderer();
            }

            Property.TextAlignment textAlignment = getProperty(Property.TEXT_ALIGNMENT);
            if (result.getStatus() == LayoutResult.PARTIAL && textAlignment == Property.TextAlignment.JUSTIFIED && !result.isSplitForcedByNewline() ||
                    textAlignment == Property.TextAlignment.JUSTIFIED_ALL) {
                if (processedRenderer != null) {
                    processedRenderer.justify(layoutBox.getWidth() - lineIndent);
                }
            } else if (textAlignment != null && textAlignment != Property.TextAlignment.LEFT && processedRenderer != null) {
                float deltaX = availableWidth - processedRenderer.getOccupiedArea().getBBox().getWidth();
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
            if (processedRenderer != null && processedRenderer.containsImage()){
                leadingValue -= previousDescent;
            }
            boolean doesNotFit = result.getStatus() == LayoutResult.NOTHING;
            float deltaY = 0;
            if (!doesNotFit) {
                lastLineHeight = processedRenderer.getOccupiedArea().getBBox().getHeight();
                deltaY = lastYLine - leadingValue - processedRenderer.getYLine();
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
                    continue;
                } else {
                    boolean keepTogether = getProperty(Property.KEEP_TOGETHER);
                    if (keepTogether) {
                        return new LayoutResult(LayoutResult.NOTHING, occupiedArea, null, this);
                    } else {
                        applyPaddings(occupiedArea.getBBox(), true);
                        applyBorderBox(occupiedArea.getBBox(), true);
                        applyMargins(occupiedArea.getBBox(), true);

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

                        if (anythingPlaced) {
                            return new LayoutResult(LayoutResult.PARTIAL, occupiedArea, split[0], split[1]);
                        } else {
                            if (getPropertyAsBoolean(Property.FORCED_PLACEMENT)) {
                                parent.setProperty(Property.FULL, true);
                                lines.add(currentRenderer);
                                return new LayoutResult(LayoutResult.FULL, occupiedArea, null, this);
                            } else {
                                return new LayoutResult(LayoutResult.NOTHING, occupiedArea, null, this);
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

        if (!isPositioned()) {
            float moveDown = Math.min((leadingValue - lastLineHeight) / 2, occupiedArea.getBBox().getY() - layoutBox.getY());
            occupiedArea.getBBox().moveDown(moveDown);
            occupiedArea.getBBox().setHeight(occupiedArea.getBBox().getHeight() + moveDown);
        }
        Float blockHeight = getPropertyAsFloat(Property.HEIGHT);
        applyPaddings(occupiedArea.getBBox(), true);
        if (blockHeight != null && blockHeight > occupiedArea.getBBox().getHeight()) {
            occupiedArea.getBBox().moveDown(blockHeight - occupiedArea.getBBox().getHeight()).setHeight(blockHeight);
            applyVerticalAlignment();
        }
        if (isPositioned()) {
            float y = getPropertyAsFloat(Property.Y);
            float relativeY = isFixedLayout() ? 0 : layoutBox.getY();
            move(0, relativeY + y - occupiedArea.getBBox().getY());
        }

        applyBorderBox(occupiedArea.getBBox(), true);
        applyMargins(occupiedArea.getBBox(), true);
        if (getProperty(Property.ROTATION_ANGLE) != null) {
            applyRotationLayout(layoutContext.getArea().getBBox().clone());
            if (isNotFittingHeight(layoutContext.getArea())) {
                if (!layoutContext.getArea().isEmptyArea()) {
                    return new LayoutResult(LayoutResult.NOTHING, occupiedArea, null, this);
                }
            }
        }
        return new LayoutResult(LayoutResult.FULL, occupiedArea, null, null);
    }

    @Override
    public ParagraphRenderer getNextRenderer() {
        return new ParagraphRenderer((Paragraph) modelElement);
    }

    protected ParagraphRenderer createOverflowRenderer() {
        ParagraphRenderer overflowRenderer = getNextRenderer();
        // Reset first line indent in case of overflow.
        float firstLineIndent = getPropertyAsFloat(Property.FIRST_LINE_INDENT);
        if (firstLineIndent != 0) {
            overflowRenderer.setProperty(Property.FIRST_LINE_INDENT, 0);
        }
        return overflowRenderer;
    }

    protected ParagraphRenderer createSplitRenderer() {
        return getNextRenderer();
    }

    protected ParagraphRenderer[] split() {
        ParagraphRenderer splitRenderer = createSplitRenderer();
        splitRenderer.occupiedArea = occupiedArea.clone();
        splitRenderer.parent = parent;
        splitRenderer.isLastRendererForModelElement = false;

        ParagraphRenderer overflowRenderer = createOverflowRenderer();
        overflowRenderer.parent = parent;

        return new ParagraphRenderer[] {splitRenderer, overflowRenderer};
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (lines != null && lines.size() > 0) {
            for (LineRenderer lineRenderer : lines) {
                sb.append(lineRenderer.toString()).append("\n");
            }
        } else {
            for (IRenderer renderer : childRenderers) {
                sb.append(renderer.toString());
            }
        }
        return sb.toString();
    }

    @Override
    public void drawChildren(DrawContext drawContext) {
        if (lines != null) {
            for (LineRenderer line : lines) {
                line.draw(drawContext);
            }
        }
    }

    @Override
    public void move(float dxRight, float dyUp) {
        occupiedArea.getBBox().moveRight(dxRight);
        occupiedArea.getBBox().moveUp(dyUp);
        for (LineRenderer line : lines) {
            line.move(dxRight, dyUp);
        }
    }

    @Override
    protected Float getFirstYLineRecursively() {
        if (lines == null || lines.size() == 0) {
            return null;
        }
        return lines.get(0).getFirstYLineRecursively();
    }
}
