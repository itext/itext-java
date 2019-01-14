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
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutPosition;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.layout.PositionedLayoutContext;
import com.itextpdf.layout.layout.RootLayoutArea;
import com.itextpdf.layout.margincollapse.MarginsCollapseHandler;
import com.itextpdf.layout.margincollapse.MarginsCollapseInfo;
import com.itextpdf.layout.property.ClearPropertyValue;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.tagging.LayoutTaggingHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public abstract class RootRenderer extends AbstractRenderer {

    protected boolean immediateFlush = true;
    protected RootLayoutArea currentArea;
    protected int currentPageNumber;
    protected List<IRenderer> waitingDrawingElements = new ArrayList<>();
    private IRenderer keepWithNextHangingRenderer;
    private LayoutResult keepWithNextHangingRendererLayoutResult;
    private MarginsCollapseHandler marginsCollapseHandler;
    private LayoutArea initialCurrentArea;
    private List<Rectangle> floatRendererAreas;
    private List<IRenderer> waitingNextPageRenderers = new ArrayList<>();
    private boolean floatOverflowedCompletely = false;

    public void addChild(IRenderer renderer) {
        LayoutTaggingHelper taggingHelper = this.<LayoutTaggingHelper>getProperty(Property.TAGGING_HELPER);
        if (taggingHelper != null) {
            LayoutTaggingHelper.addTreeHints(taggingHelper, renderer);
        }

        // Some positioned renderers might have been fetched from non-positioned child and added to this renderer,
        // so we use this generic mechanism of determining which renderers have been just added.
        int numberOfChildRenderers = childRenderers.size();
        int numberOfPositionedChildRenderers = positionedRenderers.size();
        super.addChild(renderer);
        List<IRenderer> addedRenderers = new ArrayList<>(1);
        List<IRenderer> addedPositionedRenderers = new ArrayList<>(1);
        while (childRenderers.size() > numberOfChildRenderers) {
            addedRenderers.add(childRenderers.get(numberOfChildRenderers));
            childRenderers.remove(numberOfChildRenderers);
        }
        while (positionedRenderers.size() > numberOfPositionedChildRenderers) {
            addedPositionedRenderers.add(positionedRenderers.get(numberOfPositionedChildRenderers));
            positionedRenderers.remove(numberOfPositionedChildRenderers);
        }

        boolean marginsCollapsingEnabled = Boolean.TRUE.equals(getPropertyAsBoolean(Property.COLLAPSING_MARGINS));
        if (currentArea == null) {
            updateCurrentAndInitialArea(null);
            if (marginsCollapsingEnabled) {
                marginsCollapseHandler = new MarginsCollapseHandler(this, null);
            }
        }

        // Static layout
        for (int i = 0; currentArea != null && i < addedRenderers.size(); i++) {
            renderer = addedRenderers.get(i);
            boolean rendererIsFloat = FloatingHelper.isRendererFloating(renderer);
            boolean clearanceOverflowsToNextPage = FloatingHelper.isClearanceApplied(waitingNextPageRenderers, renderer.<ClearPropertyValue>getProperty(Property.CLEAR));
            if (rendererIsFloat && (floatOverflowedCompletely || clearanceOverflowsToNextPage)) {
                waitingNextPageRenderers.add(renderer);
                floatOverflowedCompletely = true;
                continue;
            }

            processWaitingKeepWithNextElement(renderer);

            List<IRenderer> resultRenderers = new ArrayList<>();
            LayoutResult result = null;

            RootLayoutArea storedArea = null;
            RootLayoutArea nextStoredArea = null;
            MarginsCollapseInfo childMarginsInfo = null;
            if (marginsCollapsingEnabled && currentArea != null && renderer != null) {
                childMarginsInfo = marginsCollapseHandler.startChildMarginsHandling(renderer, currentArea.getBBox());
            }
            while (clearanceOverflowsToNextPage || currentArea != null && renderer != null
                        && (result = renderer.setParent(this)
                            .layout(new LayoutContext(currentArea.clone(), childMarginsInfo, floatRendererAreas))).getStatus() != LayoutResult.FULL) {
                boolean currentAreaNeedsToBeUpdated = false;
                if (clearanceOverflowsToNextPage) {
                    result = new LayoutResult(LayoutResult.NOTHING, null, null, renderer);
                    currentAreaNeedsToBeUpdated = true;
                }
                if (result.getStatus() == LayoutResult.PARTIAL) {
                    if (rendererIsFloat) {
                        waitingNextPageRenderers.add(result.getOverflowRenderer());
                        break;
                    } else {
                        processRenderer(result.getSplitRenderer(), resultRenderers);
                        if (nextStoredArea != null) {
                            currentArea = nextStoredArea;
                            currentPageNumber = nextStoredArea.getPageNumber();
                            nextStoredArea = null;
                        } else {
                            currentAreaNeedsToBeUpdated = true;
                        }
                    }
                } else if (result.getStatus() == LayoutResult.NOTHING && !clearanceOverflowsToNextPage) {
                    if (result.getOverflowRenderer() instanceof ImageRenderer) {
                        float imgHeight = ((ImageRenderer) result.getOverflowRenderer()).getOccupiedArea().getBBox().getHeight();
                        if (!floatRendererAreas.isEmpty()
                                || currentArea.getBBox().getHeight() < imgHeight && !currentArea.isEmptyArea()) {
                            if (rendererIsFloat) {
                                waitingNextPageRenderers.add(result.getOverflowRenderer());
                                floatOverflowedCompletely = true;
                                break;
                            }
                            currentAreaNeedsToBeUpdated = true;
                        } else {
                            ((ImageRenderer) result.getOverflowRenderer()).autoScale(currentArea);
                            result.getOverflowRenderer().setProperty(Property.FORCED_PLACEMENT, true);
                            Logger logger = LoggerFactory.getLogger(RootRenderer.class);
                            logger.warn(MessageFormatUtil.format(LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, ""));
                        }
                    } else {
                        if (currentArea.isEmptyArea() && result.getAreaBreak() == null) {
                            if (Boolean.TRUE.equals(result.getOverflowRenderer().getModelElement().<Boolean>getProperty(Property.KEEP_TOGETHER))) {
                                result.getOverflowRenderer().getModelElement().setProperty(Property.KEEP_TOGETHER, false);
                                Logger logger = LoggerFactory.getLogger(RootRenderer.class);
                                logger.warn(MessageFormatUtil.format(LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, "KeepTogether property will be ignored."));
                                if (storedArea != null) {
                                    nextStoredArea = currentArea;
                                    currentArea = storedArea;
                                    currentPageNumber = storedArea.getPageNumber();
                                }
                                storedArea = currentArea;
                            } else if (null != result.getCauseOfNothing() && Boolean.TRUE.equals(result.getCauseOfNothing().<Boolean>getProperty(Property.KEEP_TOGETHER))) {
                                // set KEEP_TOGETHER false on the deepest parent (maybe the element itself) to have KEEP_TOGETHER == true
                                IRenderer theDeepestKeptTogether = result.getCauseOfNothing();
                                IRenderer parent;
                                while (null == theDeepestKeptTogether.getModelElement() || null == theDeepestKeptTogether.getModelElement().<Boolean>getOwnProperty(Property.KEEP_TOGETHER)) {
                                    parent = ((AbstractRenderer) theDeepestKeptTogether).parent;
                                    if (parent == null) {
                                        break;
                                    }
                                    theDeepestKeptTogether = parent;
                                }
                                theDeepestKeptTogether.getModelElement().setProperty(Property.KEEP_TOGETHER, false);
                                Logger logger = LoggerFactory.getLogger(RootRenderer.class);
                                logger.warn(MessageFormatUtil.format(LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, "KeepTogether property of inner element will be ignored."));
                            } else if (!Boolean.TRUE.equals(renderer.<Boolean>getProperty(Property.FORCED_PLACEMENT))) {
                                result.getOverflowRenderer().setProperty(Property.FORCED_PLACEMENT, true);
                                Logger logger = LoggerFactory.getLogger(RootRenderer.class);
                                logger.warn(MessageFormatUtil.format(LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, ""));
                            } else {
                                // FORCED_PLACEMENT was already set to the renderer and
                                // LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA message was logged.
                                // This else-clause should never be hit, otherwise there is a bug in FORCED_PLACEMENT implementation.
                                assert false;

                                // Still handling this case in order to avoid nasty infinite loops.
                                break;
                            }
                        } else {
                            storedArea = currentArea;
                            if (nextStoredArea != null) {
                                currentArea = nextStoredArea;
                                currentPageNumber = nextStoredArea.getPageNumber();
                                nextStoredArea = null;
                            } else {
                                if (rendererIsFloat) {
                                    waitingNextPageRenderers.add(result.getOverflowRenderer());
                                    floatOverflowedCompletely = true;
                                    break;
                                }
                                currentAreaNeedsToBeUpdated = true;
                            }
                        }
                    }
                }

                renderer = result.getOverflowRenderer();

                if (marginsCollapsingEnabled) {
                    marginsCollapseHandler.endChildMarginsHandling(currentArea.getBBox());
                }
                if (currentAreaNeedsToBeUpdated) {
                    updateCurrentAndInitialArea(result);
                }
                if (marginsCollapsingEnabled) {
                    marginsCollapseHandler = new MarginsCollapseHandler(this, null);
                    childMarginsInfo = marginsCollapseHandler.startChildMarginsHandling(renderer, currentArea.getBBox());
                }

                clearanceOverflowsToNextPage = clearanceOverflowsToNextPage
                        && FloatingHelper.isClearanceApplied(waitingNextPageRenderers, renderer.<ClearPropertyValue>getProperty(Property.CLEAR));
            }
            if (marginsCollapsingEnabled) {
                marginsCollapseHandler.endChildMarginsHandling(currentArea.getBBox());
            }

            if (null != result && null != result.getSplitRenderer()) {
                renderer = result.getSplitRenderer();
            }

            // Keep renderer until next element is added for future keep with next adjustments
            if (renderer != null && result != null) {
                if (Boolean.TRUE.equals(renderer.<Boolean>getProperty(Property.KEEP_WITH_NEXT))) {
                    if (Boolean.TRUE.equals(renderer.<Boolean>getProperty(Property.FORCED_PLACEMENT))) {
                        Logger logger = LoggerFactory.getLogger(RootRenderer.class);
                        logger.warn(LogMessageConstant.ELEMENT_WAS_FORCE_PLACED_KEEP_WITH_NEXT_WILL_BE_IGNORED);
                        shrinkCurrentAreaAndProcessRenderer(renderer, resultRenderers, result);
                    } else {
                        keepWithNextHangingRenderer = renderer;
                        keepWithNextHangingRendererLayoutResult = result;
                    }
                } else if (result.getStatus() != LayoutResult.NOTHING) {
                    shrinkCurrentAreaAndProcessRenderer(renderer, resultRenderers, result);
                }
            }
        }

        for (int i = 0; i < addedPositionedRenderers.size(); i++) {
            positionedRenderers.add(addedPositionedRenderers.get(i));
            renderer = positionedRenderers.get(positionedRenderers.size() - 1);
            Integer positionedPageNumber = renderer.<Integer>getProperty(Property.PAGE_NUMBER);
            if (positionedPageNumber == null)
                positionedPageNumber = currentPageNumber;

            LayoutArea layoutArea;
            // For position=absolute, if none of the top, bottom, left, right properties are provided,
            // the content should be displayed in the flow of the current content, not overlapping it.
            // The behavior is just if it would be statically positioned except it does not affect other elements
            if (Integer.valueOf(LayoutPosition.ABSOLUTE).equals(renderer.<Integer>getProperty(Property.POSITION)) && AbstractRenderer.noAbsolutePositionInfo(renderer)) {
                layoutArea = new LayoutArea((int) positionedPageNumber, currentArea.getBBox().clone());
            } else {
                layoutArea = new LayoutArea((int) positionedPageNumber, initialCurrentArea.getBBox().clone());
            }
            Rectangle fullBbox = layoutArea.getBBox().clone();
            preparePositionedRendererAndAreaForLayout(renderer, fullBbox, layoutArea.getBBox());
            renderer.layout(new PositionedLayoutContext(new LayoutArea(layoutArea.getPageNumber(), fullBbox), layoutArea));

            if (immediateFlush) {
                flushSingleRenderer(renderer);
                positionedRenderers.remove(positionedRenderers.size() - 1);
            }
        }
    }

    /**
     * Draws (flushes) the content.
     *
     * @see #draw(com.itextpdf.layout.renderer.DrawContext)
     */
    public void flush() {
        for (IRenderer resultRenderer : childRenderers) {
            flushSingleRenderer(resultRenderer);
        }
        for (IRenderer resultRenderer : positionedRenderers) {
            flushSingleRenderer(resultRenderer);
        }
        childRenderers.clear();
        positionedRenderers.clear();
    }

    /**
     * This method correctly closes the {@link RootRenderer} instance.
     * There might be hanging elements, like in case of {@link Property#KEEP_WITH_NEXT} is set to true
     * and when no consequent element has been added. This method addresses such situations.
     */
    public void close() {
        addAllWaitingNextPageRenderers();
        if (keepWithNextHangingRenderer != null) {
            keepWithNextHangingRenderer.setProperty(Property.KEEP_WITH_NEXT, false);
            IRenderer rendererToBeAdded = keepWithNextHangingRenderer;
            keepWithNextHangingRenderer = null;
            addChild(rendererToBeAdded);
        }
        if (!immediateFlush) {
            flush();
        }
        flushWaitingDrawingElements();
        LayoutTaggingHelper taggingHelper = this.<LayoutTaggingHelper>getProperty(Property.TAGGING_HELPER);
        if (taggingHelper != null) {
            taggingHelper.releaseAllHints();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LayoutResult layout(LayoutContext layoutContext) {
        throw new IllegalStateException("Layout is not supported for root renderers.");
    }

    public LayoutArea getCurrentArea() {
        if (currentArea == null) {
            updateCurrentAndInitialArea(null);
        }
        return currentArea;
    }

    protected abstract void flushSingleRenderer(IRenderer resultRenderer);

    protected abstract LayoutArea updateCurrentArea(LayoutResult overflowResult);

    protected void flushWaitingDrawingElements() {
        for (int i = 0; i < waitingDrawingElements.size(); ++i) {
            IRenderer waitingDrawingElement = waitingDrawingElements.get(i);
            flushSingleRenderer(waitingDrawingElement);
        }
        waitingDrawingElements.clear();
    }

    protected void shrinkCurrentAreaAndProcessRenderer(IRenderer renderer, List<IRenderer> resultRenderers, LayoutResult result) {
        if (currentArea != null) {
            float resultRendererHeight = result.getOccupiedArea().getBBox().getHeight();
            currentArea.getBBox().setHeight(currentArea.getBBox().getHeight() - resultRendererHeight);
            if (currentArea.isEmptyArea() && (resultRendererHeight > 0 || FloatingHelper.isRendererFloating(renderer))) {
                currentArea.setEmptyArea(false);
            }
            processRenderer(renderer, resultRenderers);
        }

        if (!immediateFlush) {
            childRenderers.addAll(resultRenderers);
        }
    }

    private void processRenderer(IRenderer renderer, List<IRenderer> resultRenderers) {
        alignChildHorizontally(renderer, currentArea.getBBox());
        if (immediateFlush) {
            flushSingleRenderer(renderer);
        } else {
            resultRenderers.add(renderer);
        }
    }

    private void processWaitingKeepWithNextElement(IRenderer renderer) {
        if (keepWithNextHangingRenderer != null) {
            LayoutArea rest = currentArea.clone();
            rest.getBBox().setHeight(rest.getBBox().getHeight() - keepWithNextHangingRendererLayoutResult.getOccupiedArea().getBBox().getHeight());
            boolean ableToProcessKeepWithNext = false;
            if (renderer.setParent(this).layout(new LayoutContext(rest)).getStatus() != LayoutResult.NOTHING) {
                // The area break will not be introduced and we are safe to place everything as is
                shrinkCurrentAreaAndProcessRenderer(keepWithNextHangingRenderer, new ArrayList<IRenderer>(), keepWithNextHangingRendererLayoutResult);
                ableToProcessKeepWithNext = true;
            } else {
                float originalElementHeight = keepWithNextHangingRendererLayoutResult.getOccupiedArea().getBBox().getHeight();
                List<Float> trySplitHeightPoints = new ArrayList<>();
                float delta = 35;
                for (int i = 1; i <= 5 && originalElementHeight - delta * i > originalElementHeight / 2; i++) {
                    trySplitHeightPoints.add(originalElementHeight - delta * i);
                }
                for (int i = 0; i < trySplitHeightPoints.size() && !ableToProcessKeepWithNext; i++) {
                    float curElementSplitHeight = trySplitHeightPoints.get(i);
                    RootLayoutArea firstElementSplitLayoutArea = (RootLayoutArea) currentArea.clone();
                    firstElementSplitLayoutArea.getBBox().setHeight(curElementSplitHeight).
                            moveUp(currentArea.getBBox().getHeight() - curElementSplitHeight);
                    LayoutResult firstElementSplitLayoutResult = keepWithNextHangingRenderer.setParent(this).layout(new LayoutContext(firstElementSplitLayoutArea.clone()));
                    if (firstElementSplitLayoutResult.getStatus() == LayoutResult.PARTIAL) {
                        RootLayoutArea storedArea = currentArea;
                        updateCurrentAndInitialArea(firstElementSplitLayoutResult);
                        LayoutResult firstElementOverflowLayoutResult = firstElementSplitLayoutResult.getOverflowRenderer().layout(new LayoutContext(currentArea.clone()));
                        if (firstElementOverflowLayoutResult.getStatus() == LayoutResult.FULL) {
                            LayoutArea secondElementLayoutArea = currentArea.clone();
                            secondElementLayoutArea.getBBox().setHeight(secondElementLayoutArea.getBBox().getHeight() - firstElementOverflowLayoutResult.getOccupiedArea().getBBox().getHeight());
                            LayoutResult secondElementLayoutResult = renderer.setParent(this).layout(new LayoutContext(secondElementLayoutArea));
                            if (secondElementLayoutResult.getStatus() != LayoutResult.NOTHING) {
                                ableToProcessKeepWithNext = true;

                                currentArea = firstElementSplitLayoutArea;
                                currentPageNumber = firstElementSplitLayoutArea.getPageNumber();
                                shrinkCurrentAreaAndProcessRenderer(firstElementSplitLayoutResult.getSplitRenderer(), new ArrayList<IRenderer>(), firstElementSplitLayoutResult);
                                updateCurrentAndInitialArea(firstElementSplitLayoutResult);
                                shrinkCurrentAreaAndProcessRenderer(firstElementSplitLayoutResult.getOverflowRenderer(), new ArrayList<IRenderer>(), firstElementOverflowLayoutResult);
                            }
                        }
                        if (!ableToProcessKeepWithNext) {
                            currentArea = storedArea;
                            currentPageNumber = storedArea.getPageNumber();
                        }
                    }
                }
            }
            if (!ableToProcessKeepWithNext && !currentArea.isEmptyArea()) {
                RootLayoutArea storedArea = currentArea;
                updateCurrentAndInitialArea(null);
                LayoutResult firstElementLayoutResult = keepWithNextHangingRenderer.setParent(this).layout(new LayoutContext(currentArea.clone()));
                if (firstElementLayoutResult.getStatus() == LayoutResult.FULL) {
                    LayoutArea secondElementLayoutArea = currentArea.clone();
                    secondElementLayoutArea.getBBox().setHeight(secondElementLayoutArea.getBBox().getHeight() - firstElementLayoutResult.getOccupiedArea().getBBox().getHeight());
                    LayoutResult secondElementLayoutResult = renderer.setParent(this).layout(new LayoutContext(secondElementLayoutArea));
                    if (secondElementLayoutResult.getStatus() != LayoutResult.NOTHING) {
                        ableToProcessKeepWithNext = true;
                        shrinkCurrentAreaAndProcessRenderer(keepWithNextHangingRenderer, new ArrayList<IRenderer>(), keepWithNextHangingRendererLayoutResult);
                    }
                }
                if (!ableToProcessKeepWithNext) {
                    currentArea = storedArea;
                    currentPageNumber = storedArea.getPageNumber();
                }
            }
            if (!ableToProcessKeepWithNext) {
                Logger logger = LoggerFactory.getLogger(RootRenderer.class);
                logger.warn(LogMessageConstant.RENDERER_WAS_NOT_ABLE_TO_PROCESS_KEEP_WITH_NEXT);
                shrinkCurrentAreaAndProcessRenderer(keepWithNextHangingRenderer, new ArrayList<IRenderer>(), keepWithNextHangingRendererLayoutResult);
            }
            keepWithNextHangingRenderer = null;
            keepWithNextHangingRendererLayoutResult = null;
        }
    }

    private void updateCurrentAndInitialArea(LayoutResult overflowResult) {
        floatRendererAreas = new ArrayList<>();
        updateCurrentArea(overflowResult);
        initialCurrentArea = currentArea == null ? null : currentArea.clone();
        // TODO how bout currentArea == null ?
        addWaitingNextPageRenderers();
    }

    private void addAllWaitingNextPageRenderers() {
        boolean marginsCollapsingEnabled = Boolean.TRUE.equals(getPropertyAsBoolean(Property.COLLAPSING_MARGINS));
        while (!waitingNextPageRenderers.isEmpty()) {
            if (marginsCollapsingEnabled) {
                marginsCollapseHandler = new MarginsCollapseHandler(this, null);
            }
            updateCurrentAndInitialArea(null);
        }
    }

    private void addWaitingNextPageRenderers() {
        floatOverflowedCompletely = false;
        List<IRenderer> waitingFloatRenderers = new ArrayList<>(waitingNextPageRenderers);
        waitingNextPageRenderers.clear();
        for (IRenderer renderer : waitingFloatRenderers) {
            addChild(renderer);
        }
    }
}
