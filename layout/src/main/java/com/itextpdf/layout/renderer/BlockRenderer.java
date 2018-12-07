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
import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.Point;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.IElement;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.layout.MinMaxWidthLayoutResult;
import com.itextpdf.layout.layout.PositionedLayoutContext;
import com.itextpdf.layout.margincollapse.MarginsCollapseHandler;
import com.itextpdf.layout.margincollapse.MarginsCollapseInfo;
import com.itextpdf.layout.minmaxwidth.MinMaxWidth;
import com.itextpdf.layout.minmaxwidth.MinMaxWidthUtils;
import com.itextpdf.layout.property.AreaBreakType;
import com.itextpdf.layout.property.FloatPropertyValue;
import com.itextpdf.layout.property.OverflowPropertyValue;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.property.VerticalAlignment;
import com.itextpdf.layout.property.ClearPropertyValue;
import com.itextpdf.layout.tagging.LayoutTaggingHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class BlockRenderer extends AbstractRenderer {

    protected BlockRenderer(IElement modelElement) {
        super(modelElement);
    }

    @Override
    public LayoutResult layout(LayoutContext layoutContext) {
        this.isLastRendererForModelElement = true;

        Map<Integer, IRenderer> waitingFloatsSplitRenderers = new LinkedHashMap<>();
        List<IRenderer> waitingOverflowFloatRenderers = new ArrayList<>();
        boolean floatOverflowedCompletely = false;
        boolean wasHeightClipped = false;
        boolean wasParentsHeightClipped = layoutContext.isClippedHeight();
        int pageNumber = layoutContext.getArea().getPageNumber();

        boolean isPositioned = isPositioned();

        Rectangle parentBBox = layoutContext.getArea().getBBox().clone();

        List<Rectangle> floatRendererAreas = layoutContext.getFloatRendererAreas();
        FloatPropertyValue floatPropertyValue = this.<FloatPropertyValue>getProperty(Property.FLOAT);
        Float rotation = this.getPropertyAsFloat(Property.ROTATION_ANGLE);

        OverflowPropertyValue overflowX = this.<OverflowPropertyValue>getProperty(Property.OVERFLOW_X);

        MarginsCollapseHandler marginsCollapseHandler = null;
        boolean marginsCollapsingEnabled = Boolean.TRUE.equals(getPropertyAsBoolean(Property.COLLAPSING_MARGINS));
        if (marginsCollapsingEnabled) {
            marginsCollapseHandler = new MarginsCollapseHandler(this, layoutContext.getMarginsCollapseInfo());
        }
        Float blockWidth = retrieveWidth(parentBBox.getWidth());
        if (rotation != null || isFixedLayout()) {
            parentBBox.moveDown(AbstractRenderer.INF - parentBBox.getHeight()).setHeight(AbstractRenderer.INF);
        }
        if (rotation != null && !FloatingHelper.isRendererFloating(this, floatPropertyValue)) {
            blockWidth = RotationUtils.retrieveRotatedLayoutWidth(parentBBox.getWidth(), this);
        }
        boolean includeFloatsInOccupiedArea = BlockFormattingContextUtil.isRendererCreateBfc(this);
        float clearHeightCorrection = FloatingHelper.calculateClearHeightCorrection(this, floatRendererAreas, parentBBox);
        FloatingHelper.applyClearance(parentBBox, marginsCollapseHandler, clearHeightCorrection, FloatingHelper.isRendererFloating(this));
        if (FloatingHelper.isRendererFloating(this, floatPropertyValue)) {
            blockWidth = FloatingHelper.adjustFloatedBlockLayoutBox(this, parentBBox, blockWidth, floatRendererAreas, floatPropertyValue, overflowX);
            floatRendererAreas = new ArrayList<>();
        }

        boolean isCellRenderer = this instanceof CellRenderer;
        if (marginsCollapsingEnabled) {
            marginsCollapseHandler.startMarginsCollapse(parentBBox);
        }

        Border[] borders = getBorders();
        UnitValue[] paddings = getPaddings();

        applyBordersPaddingsMargins(parentBBox, borders, paddings);
        Float blockMaxHeight = retrieveMaxHeight();
        OverflowPropertyValue overflowY = (null == blockMaxHeight || blockMaxHeight > parentBBox.getHeight())
                && !wasParentsHeightClipped
                ? OverflowPropertyValue.FIT
                : this.<OverflowPropertyValue>getProperty(Property.OVERFLOW_Y);
        applyWidth(parentBBox, blockWidth, overflowX);
        wasHeightClipped = applyMaxHeight(parentBBox, blockMaxHeight, marginsCollapseHandler, isCellRenderer, wasParentsHeightClipped, overflowY);

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

        Set<Rectangle> nonChildFloatingRendererAreas = new HashSet<>(floatRendererAreas); // rectangles are compared by instances

        // the first renderer (one of childRenderers or their children) to produce LayoutResult.NOTHING
        IRenderer causeOfNothing = null;
        boolean anythingPlaced = false;
        for (int childPos = 0; childPos < childRenderers.size(); childPos++) {
            IRenderer childRenderer = childRenderers.get(childPos);
            LayoutResult result;
            childRenderer.setParent(this);
            MarginsCollapseInfo childMarginsInfo = null;

            if (floatOverflowedCompletely && FloatingHelper.isRendererFloating(childRenderer)) {
                waitingFloatsSplitRenderers.put(childPos, null);
                waitingOverflowFloatRenderers.add(childRenderer);
                continue;
            }

            if (!waitingOverflowFloatRenderers.isEmpty() && FloatingHelper.isClearanceApplied(waitingOverflowFloatRenderers, childRenderer.<ClearPropertyValue>getProperty(Property.CLEAR))) {
                if (FloatingHelper.isRendererFloating(childRenderer)) {
                    waitingFloatsSplitRenderers.put(childPos, null);
                    waitingOverflowFloatRenderers.add(childRenderer);
                    floatOverflowedCompletely = true;
                    continue;
                }
                if (marginsCollapsingEnabled && !isCellRenderer) {
                    marginsCollapseHandler.endMarginsCollapse(layoutBox);
                }

                FloatingHelper.includeChildFloatsInOccupiedArea(floatRendererAreas, this, nonChildFloatingRendererAreas);
                fixOccupiedAreaIfOverflowedX(overflowX, layoutBox);

                result = new LayoutResult(LayoutResult.NOTHING, null, null, childRenderer);
                int layoutResult = anythingPlaced ? LayoutResult.PARTIAL : LayoutResult.NOTHING;
                AbstractRenderer[] splitAndOverflowRenderers = createSplitAndOverflowRenderers(childPos, layoutResult, result, waitingFloatsSplitRenderers, waitingOverflowFloatRenderers);

                AbstractRenderer splitRenderer = splitAndOverflowRenderers[0];
                AbstractRenderer overflowRenderer = splitAndOverflowRenderers[1];

                updateHeightsOnSplit(wasHeightClipped, splitRenderer, overflowRenderer);
                applyPaddings(occupiedArea.getBBox(), paddings, true);
                applyBorderBox(occupiedArea.getBBox(), borders, true);
                applyMargins(occupiedArea.getBBox(), true);

                if (Boolean.TRUE.equals(getPropertyAsBoolean(Property.FORCED_PLACEMENT)) || wasHeightClipped) {
                    LayoutArea editedArea = FloatingHelper.adjustResultOccupiedAreaForFloatAndClear(this, layoutContext.getFloatRendererAreas(), layoutContext.getArea().getBBox(), clearHeightCorrection, marginsCollapsingEnabled);
                    return new LayoutResult(LayoutResult.FULL, editedArea, splitRenderer, null, null);
                } else {
                    if (layoutResult != LayoutResult.NOTHING) {
                        LayoutArea editedArea = FloatingHelper.adjustResultOccupiedAreaForFloatAndClear(this, layoutContext.getFloatRendererAreas(), layoutContext.getArea().getBBox(), clearHeightCorrection, marginsCollapsingEnabled);
                        return new LayoutResult(layoutResult, editedArea, splitRenderer, overflowRenderer, null).setAreaBreak(result.getAreaBreak());
                    } else {
                        return new LayoutResult(layoutResult, null, null, overflowRenderer, result.getCauseOfNothing()).setAreaBreak(result.getAreaBreak());
                    }
                }
            }

            if (marginsCollapsingEnabled) {
                childMarginsInfo = marginsCollapseHandler.startChildMarginsHandling(childRenderer, layoutBox);
            }
            while ((result = childRenderer.setParent(this).layout(new LayoutContext(new LayoutArea(pageNumber, layoutBox), childMarginsInfo, floatRendererAreas, wasHeightClipped || wasParentsHeightClipped)))
                    .getStatus() != LayoutResult.FULL) {

                if (Boolean.TRUE.equals(getPropertyAsBoolean(Property.FILL_AVAILABLE_AREA_ON_SPLIT))
                        || Boolean.TRUE.equals(getPropertyAsBoolean(Property.FILL_AVAILABLE_AREA))) {
                    occupiedArea.setBBox(Rectangle.getCommonRectangle(occupiedArea.getBBox(), layoutBox));
                } else if (result.getOccupiedArea() != null && result.getStatus() != LayoutResult.NOTHING) {
                    occupiedArea.setBBox(Rectangle.getCommonRectangle(occupiedArea.getBBox(), result.getOccupiedArea().getBBox()));
                    fixOccupiedAreaIfOverflowedX(overflowX, layoutBox);
                }

                if (marginsCollapsingEnabled && result.getStatus() != LayoutResult.NOTHING) {
                    marginsCollapseHandler.endChildMarginsHandling(layoutBox);
                }

                if (FloatingHelper.isRendererFloating(childRenderer)) {
                    // Check if current block is empty, kid returns nothing and neither floats nor content
                    // were met on root area (e.g. page area) - return NOTHING, don't layout other kids,
                    // expect FORCED_PLACEMENT to be set.
                    boolean immediatelyReturnNothing = result.getStatus() == LayoutResult.NOTHING
                            && !anythingPlaced
                            && floatRendererAreas.isEmpty()
                            && isFirstOnRootArea();
                    if (!immediatelyReturnNothing) {
                        waitingFloatsSplitRenderers.put(childPos, result.getStatus() == LayoutResult.PARTIAL ? result.getSplitRenderer() : null);
                        waitingOverflowFloatRenderers.add(result.getOverflowRenderer());
                        floatOverflowedCompletely = result.getStatus() == LayoutResult.NOTHING;
                        break;
                    }
                }

                if (marginsCollapsingEnabled) {
                    marginsCollapseHandler.endMarginsCollapse(layoutBox);
                }

                // On page split, content will be drawn on next page, i.e. under all floats on this page
                FloatingHelper.includeChildFloatsInOccupiedArea(floatRendererAreas, this, nonChildFloatingRendererAreas);
                fixOccupiedAreaIfOverflowedX(overflowX, layoutBox);

                if (result.getSplitRenderer() != null) {
                    // Use occupied area's bbox width so that for absolutely positioned renderers we do not align using full width
                    // in case when parent box should wrap around child boxes.
                    // TODO in the latter case, all elements should be layouted first so that we know maximum width needed to place all children and then apply horizontal alignment
                    alignChildHorizontally(result.getSplitRenderer(), occupiedArea.getBBox());
                }

                // Save the first renderer to produce LayoutResult.NOTHING
                if (null == causeOfNothing && null != result.getCauseOfNothing()) {
                    causeOfNothing = result.getCauseOfNothing();
                }

                // have more areas
                if (currentAreaPos + 1 < areas.size() && !(result.getAreaBreak() != null && result.getAreaBreak().getType() == AreaBreakType.NEXT_PAGE)) {
                    if (result.getStatus() == LayoutResult.PARTIAL) {
                        childRenderers.set(childPos, result.getSplitRenderer());
                        // TODO linkedList would make it faster
                        childRenderers.add(childPos + 1, result.getOverflowRenderer());
                    } else {
                        if (result.getOverflowRenderer() != null) {
                            childRenderers.set(childPos, result.getOverflowRenderer());
                        } else {
                            childRenderers.remove(childPos);
                        }
                        childPos--;
                    }
                    layoutBox = areas.get(++currentAreaPos).clone();
                    break;
                } else {
                    if (result.getStatus() == LayoutResult.PARTIAL) {
                        if (currentAreaPos + 1 == areas.size()) {

                            AbstractRenderer[] splitAndOverflowRenderers = createSplitAndOverflowRenderers(childPos,
                                    LayoutResult.PARTIAL, result, waitingFloatsSplitRenderers, waitingOverflowFloatRenderers);

                            AbstractRenderer splitRenderer = splitAndOverflowRenderers[0];
                            AbstractRenderer overflowRenderer = splitAndOverflowRenderers[1];
                            overflowRenderer.deleteOwnProperty(Property.FORCED_PLACEMENT);

                            updateHeightsOnSplit(wasHeightClipped, splitRenderer, overflowRenderer);
                            applyPaddings(occupiedArea.getBBox(), paddings, true);
                            applyBorderBox(occupiedArea.getBBox(), borders, true);
                            applyMargins(occupiedArea.getBBox(), true);

                            correctFixedLayout(layoutBox);

                            LayoutArea editedArea = FloatingHelper.adjustResultOccupiedAreaForFloatAndClear(this, layoutContext.getFloatRendererAreas(), layoutContext.getArea().getBBox(), clearHeightCorrection, marginsCollapsingEnabled);
                            if (wasHeightClipped) {
                                return new LayoutResult(LayoutResult.FULL, editedArea, splitRenderer, null);
                            } else {
                                return new LayoutResult(LayoutResult.PARTIAL, editedArea, splitRenderer, overflowRenderer, causeOfNothing);
                            }
                        } else {
                            childRenderers.set(childPos, result.getSplitRenderer());
                            childRenderers.add(childPos + 1, result.getOverflowRenderer());
                            layoutBox = areas.get(++currentAreaPos).clone();
                            break;
                        }
                    } else if (result.getStatus() == LayoutResult.NOTHING) {
                        boolean keepTogether = isKeepTogether();
                        int layoutResult = anythingPlaced && !keepTogether ? LayoutResult.PARTIAL : LayoutResult.NOTHING;

                        AbstractRenderer[] splitAndOverflowRenderers = createSplitAndOverflowRenderers(childPos, layoutResult,
                                result, waitingFloatsSplitRenderers, waitingOverflowFloatRenderers);

                        AbstractRenderer splitRenderer = splitAndOverflowRenderers[0];
                        AbstractRenderer overflowRenderer = splitAndOverflowRenderers[1];

                        if (isRelativePosition() && positionedRenderers.size() > 0) {
                            overflowRenderer.positionedRenderers = new ArrayList<>(positionedRenderers);
                        }

                        updateHeightsOnSplit(wasHeightClipped, splitRenderer, overflowRenderer);

                        if (keepTogether) {
                            splitRenderer = null;
                            overflowRenderer.childRenderers.clear();
                            overflowRenderer.childRenderers = new ArrayList<>(childRenderers);
                        }

                        correctFixedLayout(layoutBox);

                        applyPaddings(occupiedArea.getBBox(), paddings, true);
                        applyBorderBox(occupiedArea.getBBox(), borders, true);
                        applyMargins(occupiedArea.getBBox(), true);

                        applyAbsolutePositionIfNeeded(layoutContext);

                        if (Boolean.TRUE.equals(getPropertyAsBoolean(Property.FORCED_PLACEMENT)) || wasHeightClipped) {
                            LayoutArea editedArea = FloatingHelper.adjustResultOccupiedAreaForFloatAndClear(this, layoutContext.getFloatRendererAreas(), layoutContext.getArea().getBBox(), clearHeightCorrection, marginsCollapsingEnabled);
                            return new LayoutResult(LayoutResult.FULL, editedArea, splitRenderer, null, null);
                        } else {
                            if (layoutResult != LayoutResult.NOTHING) {
                                LayoutArea editedArea = FloatingHelper.adjustResultOccupiedAreaForFloatAndClear(this, layoutContext.getFloatRendererAreas(), layoutContext.getArea().getBBox(), clearHeightCorrection, marginsCollapsingEnabled);
                                return new LayoutResult(layoutResult, editedArea, splitRenderer, overflowRenderer, null).setAreaBreak(result.getAreaBreak());
                            } else {
                                return new LayoutResult(layoutResult, null, null, overflowRenderer, result.getCauseOfNothing()).setAreaBreak(result.getAreaBreak());
                            }
                        }
                    }
                }
            }
            anythingPlaced = anythingPlaced || result.getStatus() != LayoutResult.NOTHING;

            if (result.getOccupiedArea() != null) {
                if (!FloatingHelper.isRendererFloating(childRenderer) || includeFloatsInOccupiedArea) { // this check is needed only if margins collapsing is enabled
                    occupiedArea.setBBox(Rectangle.getCommonRectangle(occupiedArea.getBBox(), result.getOccupiedArea().getBBox()));
                    fixOccupiedAreaIfOverflowedX(overflowX, layoutBox);
                }
            }
            if (marginsCollapsingEnabled) {
                marginsCollapseHandler.endChildMarginsHandling(layoutBox);
            }
            if (result.getStatus() == LayoutResult.FULL) {
                layoutBox.setHeight(result.getOccupiedArea().getBBox().getY() - layoutBox.getY());
                if (childRenderer.getOccupiedArea() != null) {
                    // Use occupied area's bbox width so that for absolutely positioned renderers we do not align using full width
                    // in case when parent box should wrap around child boxes.
                    // TODO in the latter case, all elements should be layouted first so that we know maximum width needed to place all children and then apply horizontal alignment
                    alignChildHorizontally(childRenderer, occupiedArea.getBBox());
                }
            }

            // Save the first renderer to produce LayoutResult.NOTHING
            if (null == causeOfNothing && null != result.getCauseOfNothing()) {
                causeOfNothing = result.getCauseOfNothing();
            }
        }

        if (includeFloatsInOccupiedArea) {
            FloatingHelper.includeChildFloatsInOccupiedArea(floatRendererAreas, this, nonChildFloatingRendererAreas);
            fixOccupiedAreaIfOverflowedX(overflowX, layoutBox);
        }
        if (wasHeightClipped) {
            fixOccupiedAreaIfOverflowedY(overflowY, layoutBox);
        }
        if (marginsCollapsingEnabled) {
            marginsCollapseHandler.endMarginsCollapse(layoutBox);
        }

        if (Boolean.TRUE.equals(getPropertyAsBoolean(Property.FILL_AVAILABLE_AREA))) {
            occupiedArea.setBBox(Rectangle.getCommonRectangle(occupiedArea.getBBox(), layoutBox));
        }

        int layoutResult = LayoutResult.FULL;
        boolean processOverflowedFloats = !waitingOverflowFloatRenderers.isEmpty() && !wasHeightClipped && !Boolean.TRUE.equals(getPropertyAsBoolean(Property.FORCED_PLACEMENT));

        AbstractRenderer overflowRenderer = null;
        if (!includeFloatsInOccupiedArea || !processOverflowedFloats) {
            overflowRenderer = applyMinHeight(overflowY, layoutBox);
        }

        boolean minHeightOverflow = overflowRenderer != null;
        if (minHeightOverflow && isKeepTogether()) {
            return new LayoutResult(LayoutResult.NOTHING, null, null, this, this);
        }

        if (overflowRenderer != null || processOverflowedFloats) { // in this case layout result need to be changed
            layoutResult = !anythingPlaced && !waitingOverflowFloatRenderers.isEmpty()
                    // nothing was placed and there are some overflowed floats
                    ? LayoutResult.NOTHING
                    // either something was placed or (since there are no overflowed floats) there is overflow renderer
                    // that indicates overflowed min_height
                    : LayoutResult.PARTIAL;
        }
        if (processOverflowedFloats) {
            if (overflowRenderer == null || layoutResult == LayoutResult.NOTHING) {
                // if layout result is NOTHING - avoid possible usage of the overflowRenderer created
                // for overflow of min_height with adjusted height properties
                overflowRenderer = createOverflowRenderer(layoutResult);
            }
            overflowRenderer.getChildRenderers().addAll(waitingOverflowFloatRenderers);
            if (layoutResult == LayoutResult.PARTIAL && !minHeightOverflow && !includeFloatsInOccupiedArea) {
                FloatingHelper.removeParentArtifactsOnPageSplitIfOnlyFloatsOverflow(overflowRenderer);
            }
        }
        AbstractRenderer splitRenderer = this;
        if (waitingFloatsSplitRenderers.size() > 0 && layoutResult != LayoutResult.NOTHING) {
            splitRenderer = createSplitRenderer(layoutResult);
            splitRenderer.childRenderers = new ArrayList<>(childRenderers);
            replaceSplitRendererKidFloats(waitingFloatsSplitRenderers, splitRenderer);

            float usedHeight = occupiedArea.getBBox().getHeight();
            if (!includeFloatsInOccupiedArea) {
                Rectangle commonRectangle = Rectangle.getCommonRectangle(layoutBox, occupiedArea.getBBox());
                usedHeight = commonRectangle.getHeight();
            }
            // this must be processed before margin/border/padding
            updateHeightsOnSplit(usedHeight, wasHeightClipped, splitRenderer, overflowRenderer, includeFloatsInOccupiedArea);
        }

        if (positionedRenderers.size() > 0) {
            for (IRenderer childPositionedRenderer : positionedRenderers) {
                Rectangle fullBbox = occupiedArea.getBBox().clone();

                // Use that value so that layout is independent of whether we are in the bottom of the page or in the top of the page
                float layoutMinHeight = 1000;
                fullBbox.moveDown(layoutMinHeight).setHeight(layoutMinHeight + fullBbox.getHeight());
                LayoutArea parentArea = new LayoutArea(occupiedArea.getPageNumber(), occupiedArea.getBBox().clone());
                applyPaddings(parentArea.getBBox(), paddings, true);

                preparePositionedRendererAndAreaForLayout(childPositionedRenderer, fullBbox, parentArea.getBBox());
                childPositionedRenderer.layout(new PositionedLayoutContext(new LayoutArea(occupiedArea.getPageNumber(), fullBbox), parentArea));
            }
        }

        if (isPositioned) {
            correctFixedLayout(layoutBox);
        }

        applyPaddings(occupiedArea.getBBox(), paddings, true);
        applyBorderBox(occupiedArea.getBBox(), borders, true);
        applyMargins(occupiedArea.getBBox(), true);

        applyAbsolutePositionIfNeeded(layoutContext);

        if (rotation != null) {
            applyRotationLayout(layoutContext.getArea().getBBox().clone());
            if (isNotFittingLayoutArea(layoutContext.getArea())) {
                if (isNotFittingWidth(layoutContext.getArea()) && !isNotFittingHeight(layoutContext.getArea())) {
                    LoggerFactory.getLogger(getClass()).warn(MessageFormatUtil.format(LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, "It fits by height so it will be forced placed"));
                } else if (!Boolean.TRUE.equals(getPropertyAsBoolean(Property.FORCED_PLACEMENT))) {
                    return new MinMaxWidthLayoutResult(LayoutResult.NOTHING, null, null, this, this);
                }
            }
        }
        applyVerticalAlignment();

        FloatingHelper.removeFloatsAboveRendererBottom(floatRendererAreas, this);

        if (layoutResult != LayoutResult.NOTHING) {
            LayoutArea editedArea = FloatingHelper.adjustResultOccupiedAreaForFloatAndClear(this, layoutContext.getFloatRendererAreas(), layoutContext.getArea().getBBox(), clearHeightCorrection, marginsCollapsingEnabled);
            return new LayoutResult(layoutResult, editedArea, splitRenderer, overflowRenderer, causeOfNothing);
        } else {
            if (positionedRenderers.size() > 0) {
                overflowRenderer.positionedRenderers = new ArrayList<>(positionedRenderers);
            }
            return new LayoutResult(LayoutResult.NOTHING, null, null, overflowRenderer, causeOfNothing);
        }
    }

    protected AbstractRenderer createSplitRenderer(int layoutResult) {
        AbstractRenderer splitRenderer = (AbstractRenderer) getNextRenderer();
        splitRenderer.parent = parent;
        splitRenderer.modelElement = modelElement;
        splitRenderer.occupiedArea = occupiedArea;
        splitRenderer.isLastRendererForModelElement = false;
        splitRenderer.addAllProperties(getOwnProperties());
        return splitRenderer;
    }

    protected AbstractRenderer createOverflowRenderer(int layoutResult) {
        AbstractRenderer overflowRenderer = (AbstractRenderer) getNextRenderer();
        overflowRenderer.parent = parent;
        overflowRenderer.modelElement = modelElement;
        overflowRenderer.addAllProperties(getOwnProperties());
        return overflowRenderer;
    }

    @Override
    public void draw(DrawContext drawContext) {
        if (occupiedArea == null) {
            Logger logger = LoggerFactory.getLogger(BlockRenderer.class);
            logger.error(MessageFormatUtil.format(LogMessageConstant.OCCUPIED_AREA_HAS_NOT_BEEN_INITIALIZED, "Drawing won't be performed."));
            return;
        }

        boolean isTagged = drawContext.isTaggingEnabled();
        LayoutTaggingHelper taggingHelper = null;
        if (isTagged) {
            taggingHelper = this.<LayoutTaggingHelper>getProperty(Property.TAGGING_HELPER);
            if (taggingHelper == null) {
                isTagged = false;
            } else {
                TagTreePointer tagPointer = taggingHelper.useAutoTaggingPointerAndRememberItsPosition(this);
                if (taggingHelper.createTag(this, tagPointer)) {
                    tagPointer.getProperties()
                            .addAttributes(0, AccessibleAttributesApplier.getListAttributes(this, tagPointer))
                            .addAttributes(0, AccessibleAttributesApplier.getTableAttributes(this, tagPointer))
                            .addAttributes(0, AccessibleAttributesApplier.getLayoutAttributes(this, tagPointer));
                }
            }
        }

        beginTransformationIfApplied(drawContext.getCanvas());
        applyDestinationsAndAnnotation(drawContext);

        boolean isRelativePosition = isRelativePosition();
        if (isRelativePosition) {
            applyRelativePositioningTranslation(false);
        }

        beginElementOpacityApplying(drawContext);
        beginRotationIfApplied(drawContext.getCanvas());

        boolean overflowXHidden = isOverflowProperty(OverflowPropertyValue.HIDDEN, Property.OVERFLOW_X);
        boolean overflowYHidden = isOverflowProperty(OverflowPropertyValue.HIDDEN, Property.OVERFLOW_Y);
        boolean processOverflow = overflowXHidden || overflowYHidden;

        drawBackground(drawContext);
        drawBorder(drawContext);

        if (processOverflow) {
            drawContext.getCanvas().saveState();
            int pageNumber = occupiedArea.getPageNumber();
            Rectangle clippedArea;
            if (pageNumber < 1 || pageNumber > drawContext.getDocument().getNumberOfPages()) {
                clippedArea = new Rectangle(-INF / 2 , -INF / 2, INF, INF);
            } else {
                clippedArea = drawContext.getDocument().getPage(pageNumber).getPageSize();
            }
            Rectangle area = getBorderAreaBBox();
            if (overflowXHidden) {
                clippedArea.setX(area.getX()).setWidth(area.getWidth());
            }
            if (overflowYHidden) {
                clippedArea.setY(area.getY()).setHeight(area.getHeight());
            }
            drawContext.getCanvas().rectangle(clippedArea).clip().newPath();
        }

        drawChildren(drawContext);
        drawPositionedChildren(drawContext);

        if (processOverflow) {
            drawContext.getCanvas().restoreState();
        }

        endRotationIfApplied(drawContext.getCanvas());
        endElementOpacityApplying(drawContext);

        if (isRelativePosition) {
            applyRelativePositioningTranslation(true);
        }

        if (isTagged) {
            if (isLastRendererForModelElement) {
                taggingHelper.finishTaggingHint(this);
            }
            taggingHelper.restoreAutoTaggingPointerPosition(this);
        }

        flushed = true;
        endTransformationIfApplied(drawContext.getCanvas());
    }

    @Override
    public Rectangle getOccupiedAreaBBox() {
        Rectangle bBox = occupiedArea.getBBox().clone();
        Float rotationAngle = this.<Float>getProperty(Property.ROTATION_ANGLE);
        if (rotationAngle != null) {
            if (!hasOwnProperty(Property.ROTATION_INITIAL_WIDTH) || !hasOwnProperty(Property.ROTATION_INITIAL_HEIGHT)) {
                Logger logger = LoggerFactory.getLogger(BlockRenderer.class);
                logger.error(MessageFormatUtil.format(LogMessageConstant.ROTATION_WAS_NOT_CORRECTLY_PROCESSED_FOR_RENDERER, getClass().getSimpleName()));
            } else {
                bBox.setWidth((float) this.getPropertyAsFloat(Property.ROTATION_INITIAL_WIDTH));
                bBox.setHeight((float) this.getPropertyAsFloat(Property.ROTATION_INITIAL_HEIGHT));
            }
        }
        return bBox;
    }

    protected void applyVerticalAlignment() {
        VerticalAlignment verticalAlignment = this.<VerticalAlignment>getProperty(Property.VERTICAL_ALIGNMENT);
        if (verticalAlignment == null || verticalAlignment == VerticalAlignment.TOP || childRenderers.isEmpty()) {
            return;
        }

        float lowestChildBottom = Float.MAX_VALUE;
        if (FloatingHelper.isRendererFloating(this) || this instanceof CellRenderer) {
            // include floats in vertical alignment
            for (IRenderer child : childRenderers) {
                if (child.getOccupiedArea().getBBox().getBottom() < lowestChildBottom) {
                    lowestChildBottom = child.getOccupiedArea().getBBox().getBottom();
                }
            }
        } else {
            int lastChildIndex = childRenderers.size() - 1;
            while (lastChildIndex >= 0) {
                IRenderer child = childRenderers.get(lastChildIndex--);
                if (!FloatingHelper.isRendererFloating(child)) {
                    lowestChildBottom = child.getOccupiedArea().getBBox().getBottom();
                    break;
                }
            }
        }

        if (lowestChildBottom == Float.MAX_VALUE) {
            return;
        }

        float deltaY = lowestChildBottom - getInnerAreaBBox().getY();
        if (deltaY < 0) {
            return;
        }
        switch (verticalAlignment) {
            case BOTTOM:
                for (IRenderer child : childRenderers) {
                    child.move(0, -deltaY);
                }
                break;
            case MIDDLE:
                for (IRenderer child : childRenderers) {
                    child.move(0, -deltaY / 2);
                }
                break;
        }
    }

    protected void applyRotationLayout(Rectangle layoutBox) {
        float angle = (float) this.getPropertyAsFloat(Property.ROTATION_ANGLE);

        float x = occupiedArea.getBBox().getX();
        float y = occupiedArea.getBBox().getY();
        float height = occupiedArea.getBBox().getHeight();
        float width = occupiedArea.getBBox().getWidth();

        setProperty(Property.ROTATION_INITIAL_WIDTH, width);
        setProperty(Property.ROTATION_INITIAL_HEIGHT, height);

        AffineTransform rotationTransform = new AffineTransform();

        // here we calculate and set the actual occupied area of the rotated content
        if (isPositioned()) {
            Float rotationPointX = this.getPropertyAsFloat(Property.ROTATION_POINT_X);
            Float rotationPointY = this.getPropertyAsFloat(Property.ROTATION_POINT_Y);

            if (rotationPointX == null || rotationPointY == null) {
                // if rotation point was not specified, the most bottom-left point is used
                rotationPointX = x;
                rotationPointY = y;
            }

            // transforms apply from bottom to top
            rotationTransform.translate((float) rotationPointX, (float) rotationPointY); // move point back at place
            rotationTransform.rotate(angle); // rotate
            rotationTransform.translate((float) -rotationPointX, (float) -rotationPointY); // move rotation point to origin

            List<Point> rotatedPoints = transformPoints(rectangleToPointsList(occupiedArea.getBBox()), rotationTransform);
            Rectangle newBBox = calculateBBox(rotatedPoints);

            // make occupied area be of size and position of actual content
            occupiedArea.getBBox().setWidth(newBBox.getWidth());
            occupiedArea.getBBox().setHeight(newBBox.getHeight());
            float occupiedAreaShiftX = newBBox.getX() - x;
            float occupiedAreaShiftY = newBBox.getY() - y;
            move(occupiedAreaShiftX, occupiedAreaShiftY);
        } else {
            rotationTransform = AffineTransform.getRotateInstance(angle);
            List<Point> rotatedPoints = transformPoints(rectangleToPointsList(occupiedArea.getBBox()), rotationTransform);
            float[] shift = calculateShiftToPositionBBoxOfPointsAt(x, y + height, rotatedPoints);

            for (Point point : rotatedPoints) {
                point.setLocation(point.getX() + shift[0], point.getY() + shift[1]);
            }

            Rectangle newBBox = calculateBBox(rotatedPoints);

            occupiedArea.getBBox().setWidth(newBBox.getWidth());
            occupiedArea.getBBox().setHeight(newBBox.getHeight());

            float heightDiff = height - newBBox.getHeight();
            move(0, heightDiff);
        }
    }

    /**
     * This method creates {@link AffineTransform} instance that could be used
     * to rotate content inside the occupied area. Be aware that it should be used only after
     * layout rendering is finished and correct occupied area for the rotated element is calculated.
     *
     * @return {@link AffineTransform} that rotates the content and places it inside occupied area.
     */
    protected AffineTransform createRotationTransformInsideOccupiedArea() {
        Float angle = this.<Float>getProperty(Property.ROTATION_ANGLE);
        AffineTransform rotationTransform = AffineTransform.getRotateInstance((float) angle);

        Rectangle contentBox = this.getOccupiedAreaBBox();
        List<Point> rotatedContentBoxPoints = transformPoints(rectangleToPointsList(contentBox), rotationTransform);
        // Occupied area for rotated elements is already calculated on layout in such way to enclose rotated content;
        // therefore we can simply rotate content as is and then shift it to the occupied area.
        float[] shift = calculateShiftToPositionBBoxOfPointsAt(occupiedArea.getBBox().getLeft(), occupiedArea.getBBox().getTop(), rotatedContentBoxPoints);
        rotationTransform.preConcatenate(AffineTransform.getTranslateInstance(shift[0], shift[1]));

        return rotationTransform;
    }

    protected void beginRotationIfApplied(PdfCanvas canvas) {
        Float angle = this.getPropertyAsFloat(Property.ROTATION_ANGLE);
        if (angle != null) {
            if (!hasOwnProperty(Property.ROTATION_INITIAL_HEIGHT)) {
                Logger logger = LoggerFactory.getLogger(BlockRenderer.class);
                logger.error(MessageFormatUtil.format(LogMessageConstant.ROTATION_WAS_NOT_CORRECTLY_PROCESSED_FOR_RENDERER, getClass().getSimpleName()));
            } else {
                AffineTransform transform = createRotationTransformInsideOccupiedArea();
                canvas.saveState().concatMatrix(transform);
            }
        }
    }

    protected void endRotationIfApplied(PdfCanvas canvas) {
        Float angle = this.getPropertyAsFloat(Property.ROTATION_ANGLE);
        if (angle != null && hasOwnProperty(Property.ROTATION_INITIAL_HEIGHT)) {
            canvas.restoreState();
        }
    }

    void correctFixedLayout(Rectangle layoutBox) {
        if (isFixedLayout()) {
            float y = (float) this.getPropertyAsFloat(Property.BOTTOM);
            move(0, y - occupiedArea.getBBox().getY());
        }
    }

    void applyWidth(Rectangle parentBBox, Float blockWidth, OverflowPropertyValue overflowX) {
        // maxWidth has already taken in attention in blockWidth,
        // therefore only `parentBBox > minWidth` needs to be checked.
        Float rotation = this.getPropertyAsFloat(Property.ROTATION_ANGLE);

        if (blockWidth != null && (
                        blockWidth < parentBBox.getWidth() ||
                        isPositioned() ||
                        rotation != null ||
                        (!isOverflowFit(overflowX)))) {
            parentBBox.setWidth((float) blockWidth);
        } else {
            Float minWidth = retrieveMinWidth(parentBBox.getWidth());
            //Shall we check overflow-x here?
            if (minWidth != null && minWidth > parentBBox.getWidth()) {
                parentBBox.setWidth((float) minWidth);
            }
        }
    }

    boolean applyMaxHeight(Rectangle parentBBox, Float blockMaxHeight, MarginsCollapseHandler marginsCollapseHandler,
                           boolean isCellRenderer, boolean wasParentsHeightClipped, OverflowPropertyValue overflowY) {
        if (null == blockMaxHeight || (blockMaxHeight >= parentBBox.getHeight() && (isOverflowFit(overflowY)))) {
            return false;
        }
        boolean wasHeightClipped = false;
        if (blockMaxHeight <= parentBBox.getHeight()) {
            wasHeightClipped = true;
        }
        float heightDelta = parentBBox.getHeight() - (float) blockMaxHeight;
        if (marginsCollapseHandler != null && !isCellRenderer) {
            marginsCollapseHandler.processFixedHeightAdjustment(heightDelta);
        }
        parentBBox.moveUp(heightDelta).setHeight((float) blockMaxHeight);
        return wasHeightClipped;
    }

    AbstractRenderer applyMinHeight(OverflowPropertyValue overflowY, Rectangle layoutBox) {
        AbstractRenderer overflowRenderer = null;
        Float blockMinHeight = retrieveMinHeight();
        if (!Boolean.TRUE.equals(getPropertyAsBoolean(Property.FORCED_PLACEMENT)) && null != blockMinHeight && blockMinHeight > occupiedArea.getBBox().getHeight()) {
            float blockBottom = occupiedArea.getBBox().getBottom() - ((float) blockMinHeight - occupiedArea.getBBox().getHeight());
            if (isFixedLayout()) {
                occupiedArea.getBBox().setY(blockBottom).setHeight((float) blockMinHeight);
            } else {
                if (isOverflowFit(overflowY) && blockBottom < layoutBox.getBottom()) {
                    float hDelta = occupiedArea.getBBox().getBottom() - layoutBox.getBottom();
                    occupiedArea.getBBox()
                            .increaseHeight(hDelta)
                            .setY(layoutBox.getBottom());

                    if (occupiedArea.getBBox().getHeight() < 0) {
                        occupiedArea.getBBox().setHeight(0);
                    }

                    this.isLastRendererForModelElement = false;
                    overflowRenderer = createOverflowRenderer(LayoutResult.PARTIAL);
                    overflowRenderer.updateMinHeight(UnitValue.createPointValue((float) blockMinHeight - occupiedArea.getBBox().getHeight()));
                    if (hasProperty(Property.HEIGHT)) {
                        overflowRenderer.updateHeight(UnitValue.createPointValue((float) retrieveHeight() - occupiedArea.getBBox().getHeight()));
                    }
                } else {
                    occupiedArea.getBBox().setY(blockBottom).setHeight((float) blockMinHeight);
                }
            }
        }
        return overflowRenderer;
    }

    void fixOccupiedAreaIfOverflowedX(OverflowPropertyValue overflowX, Rectangle layoutBox) {
        if (isOverflowFit(overflowX)) {
            return;
        }

        if ((occupiedArea.getBBox().getWidth() > layoutBox.getWidth() || occupiedArea.getBBox().getLeft() < layoutBox.getLeft())) {
            occupiedArea.getBBox().setX(layoutBox.getX()).setWidth(layoutBox.getWidth());
        }
    }

    void fixOccupiedAreaIfOverflowedY(OverflowPropertyValue overflowY, Rectangle layoutBox) {
        if (isOverflowFit(overflowY)) {
            return;
        }
        if (occupiedArea.getBBox().getBottom() < layoutBox.getBottom()) {
            float difference = layoutBox.getBottom() - occupiedArea.getBBox().getBottom();
            occupiedArea.getBBox().moveUp(difference).decreaseHeight(difference);
        }
    }

    protected float applyBordersPaddingsMargins(Rectangle parentBBox, Border[] borders, UnitValue[] paddings) {
        float parentWidth = parentBBox.getWidth();

        applyMargins(parentBBox, false);
        applyBorderBox(parentBBox, borders, false);
        if (isFixedLayout()) {
            parentBBox.setX((float) this.getPropertyAsFloat(Property.LEFT));
        }
        applyPaddings(parentBBox, paddings, false);
        return parentWidth - parentBBox.getWidth();
    }

    @Override
    public MinMaxWidth getMinMaxWidth() {
        MinMaxWidth minMaxWidth = new MinMaxWidth(calculateAdditionalWidth(this));
        if (!setMinMaxWidthBasedOnFixedWidth(minMaxWidth)) {
            Float minWidth = hasAbsoluteUnitValue(Property.MIN_WIDTH) ? retrieveMinWidth(0) : null;
            Float maxWidth = hasAbsoluteUnitValue(Property.MAX_WIDTH) ? retrieveMaxWidth(0) : null;
            if (minWidth == null || maxWidth == null) {
                AbstractWidthHandler handler = new MaxMaxWidthHandler(minMaxWidth);
                int epsilonNum = 0;
                int curEpsNum = 0;
                float previousFloatingChildWidth = 0;
                for (IRenderer childRenderer : childRenderers) {
                    MinMaxWidth childMinMaxWidth;
                    childRenderer.setParent(this);
                    if (childRenderer instanceof AbstractRenderer) {
                        childMinMaxWidth = ((AbstractRenderer) childRenderer).getMinMaxWidth();
                    } else {
                        childMinMaxWidth = MinMaxWidthUtils.countDefaultMinMaxWidth(childRenderer);
                    }
                    handler.updateMaxChildWidth(childMinMaxWidth.getMaxWidth() + (FloatingHelper.isRendererFloating(childRenderer) ? previousFloatingChildWidth : 0));
                    handler.updateMinChildWidth(childMinMaxWidth.getMinWidth());
                    previousFloatingChildWidth = FloatingHelper.isRendererFloating(childRenderer) ? previousFloatingChildWidth + childMinMaxWidth.getMaxWidth() : 0;
                    if (FloatingHelper.isRendererFloating(childRenderer)) {
                        curEpsNum++;
                    } else {
                        epsilonNum = Math.max(epsilonNum, curEpsNum);
                        curEpsNum = 0;
                    }
                }
                epsilonNum = Math.max(epsilonNum, curEpsNum);
                handler.minMaxWidth.setChildrenMaxWidth(handler.minMaxWidth.getChildrenMaxWidth() + epsilonNum * AbstractRenderer.EPS);
                handler.minMaxWidth.setChildrenMinWidth(handler.minMaxWidth.getChildrenMinWidth() + epsilonNum * AbstractRenderer.EPS);
            }
            if (minWidth != null) {
                minMaxWidth.setChildrenMinWidth((float) minWidth);

            }
            // if max-width was defined explicitly, it shouldn't be overwritten
            if (maxWidth != null) {
                minMaxWidth.setChildrenMaxWidth((float) maxWidth);
            } else {
                if (minMaxWidth.getChildrenMinWidth() > minMaxWidth.getChildrenMaxWidth()) {
                    minMaxWidth.setChildrenMaxWidth(minMaxWidth.getChildrenMinWidth());
                }
            }
        }

        if (this.getPropertyAsFloat(Property.ROTATION_ANGLE) != null) {
            return RotationUtils.countRotationMinMaxWidth(minMaxWidth, this);
        }

        return minMaxWidth;
    }

    private AbstractRenderer[] createSplitAndOverflowRenderers(int childPos, int layoutStatus, LayoutResult childResult, Map<Integer, IRenderer> waitingFloatsSplitRenderers,
                                                               List<IRenderer> waitingOverflowFloatRenderers) {
        AbstractRenderer splitRenderer = createSplitRenderer(layoutStatus);
        splitRenderer.childRenderers = new ArrayList<>(childRenderers.subList(0, childPos));
        if (childResult.getStatus() == LayoutResult.PARTIAL && childResult.getSplitRenderer() != null) {
            splitRenderer.childRenderers.add(childResult.getSplitRenderer());
        }


        replaceSplitRendererKidFloats(waitingFloatsSplitRenderers, splitRenderer);
        for (IRenderer renderer : splitRenderer.childRenderers) {
            renderer.setParent(splitRenderer);
        }

        AbstractRenderer overflowRenderer = createOverflowRenderer(layoutStatus);
        overflowRenderer.childRenderers.addAll(waitingOverflowFloatRenderers);
        if (childResult.getOverflowRenderer() != null) {
            overflowRenderer.childRenderers.add(childResult.getOverflowRenderer());
        }
        overflowRenderer.childRenderers.addAll(childRenderers.subList(childPos + 1, childRenderers.size()));

        if (childResult.getStatus() == LayoutResult.PARTIAL) {
            // Apply forced placement only on split renderer
            overflowRenderer.deleteOwnProperty(Property.FORCED_PLACEMENT);
        }

        return new AbstractRenderer[]{splitRenderer, overflowRenderer};
    }

    private void replaceSplitRendererKidFloats(Map<Integer, IRenderer> waitingFloatsSplitRenderers, IRenderer splitRenderer) {
        for (Map.Entry<Integer, IRenderer> waitingSplitRenderer : waitingFloatsSplitRenderers.entrySet()) {
            if (waitingSplitRenderer.getValue() != null) {
                splitRenderer.getChildRenderers().set(waitingSplitRenderer.getKey(), waitingSplitRenderer.getValue());
            } else {
                splitRenderer.getChildRenderers().set((int) waitingSplitRenderer.getKey(), null);
            }
        }
        for (int i = splitRenderer.getChildRenderers().size() - 1; i >= 0; --i) {
            if (splitRenderer.getChildRenderers().get(i) == null) {
                splitRenderer.getChildRenderers().remove(i);
            }
        }
    }

    private List<Point> clipPolygon(List<Point> points, Point clipLineBeg, Point clipLineEnd) {
        List<Point> filteredPoints = new ArrayList<>();

        boolean prevOnRightSide = false;
        Point filteringPoint = points.get(0);
        if (checkPointSide(filteringPoint, clipLineBeg, clipLineEnd) >= 0) {
            filteredPoints.add(filteringPoint);
            prevOnRightSide = true;
        }

        Point prevPoint = filteringPoint;
        for (int i = 1; i < points.size() + 1; ++i) {
            filteringPoint = points.get(i % points.size());
            if (checkPointSide(filteringPoint, clipLineBeg, clipLineEnd) >= 0) {
                if (!prevOnRightSide) {
                    filteredPoints.add(getIntersectionPoint(prevPoint, filteringPoint, clipLineBeg, clipLineEnd));
                }
                filteredPoints.add(filteringPoint);
                prevOnRightSide = true;
            } else if (prevOnRightSide) {
                filteredPoints.add(getIntersectionPoint(prevPoint, filteringPoint, clipLineBeg, clipLineEnd));
            }

            prevPoint = filteringPoint;
        }

        return filteredPoints;
    }

    private int checkPointSide(Point filteredPoint, Point clipLineBeg, Point clipLineEnd) {
        double x1, x2, y1, y2;
        x1 = filteredPoint.getX() - clipLineBeg.getX();
        y2 = clipLineEnd.getY() - clipLineBeg.getY();

        x2 = clipLineEnd.getX() - clipLineBeg.getX();
        y1 = filteredPoint.getY() - clipLineBeg.getY();

        double sgn = x1 * y2 - x2 * y1;

        if (Math.abs(sgn) < 0.001) return 0;
        if (sgn > 0) return 1;
        if (sgn < 0) return -1;

        return 0;
    }

    private Point getIntersectionPoint(Point lineBeg, Point lineEnd, Point clipLineBeg, Point clipLineEnd) {
        double A1 = lineBeg.getY() - lineEnd.getY(), A2 = clipLineBeg.getY() - clipLineEnd.getY();
        double B1 = lineEnd.getX() - lineBeg.getX(), B2 = clipLineEnd.getX() - clipLineBeg.getX();
        double C1 = lineBeg.getX() * lineEnd.getY() - lineBeg.getY() * lineEnd.getX();
        double C2 = clipLineBeg.getX() * clipLineEnd.getY() - clipLineBeg.getY() * clipLineEnd.getX();

        double M = B1 * A2 - B2 * A1;

        return new Point((B2 * C1 - B1 * C2) / M, (C2 * A1 - C1 * A2) / M);
    }
}
