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

import com.itextpdf.commons.actions.EventManager;
import com.itextpdf.commons.actions.sequence.AbstractIdentifiableElement;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.actions.events.LinkDocumentIdEvent;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.IPropertyContainer;
import com.itextpdf.layout.exceptions.LayoutExceptionMessageConstant;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutPosition;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.layout.PositionedLayoutContext;
import com.itextpdf.layout.layout.RootLayoutArea;
import com.itextpdf.layout.logs.LayoutLogMessageConstant;
import com.itextpdf.layout.margincollapse.MarginsCollapseHandler;
import com.itextpdf.layout.margincollapse.MarginsCollapseInfo;
import com.itextpdf.layout.properties.ClearPropertyValue;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.margins.FootnoteNumberingConfig;
import com.itextpdf.layout.properties.margins.FootnotesProperties;
import com.itextpdf.layout.properties.margins.FootnotesUtil;
import com.itextpdf.layout.properties.margins.PageMarginBoxes;
import com.itextpdf.layout.properties.margins.PageMarginContent;
import com.itextpdf.layout.tagging.LayoutTaggingHelper;
import com.itextpdf.layout.utils.LayoutInfiniteLoopResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class RootRenderer extends AbstractRenderer {

    /**
     * The Logger instance.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RootRenderer.class);

    private static final int MAX_AMOUNT_OF_ELEMENT_LAYOUTS = 1_000_000;

    protected boolean immediateFlush = true;
    protected RootLayoutArea currentArea;
    protected List<IRenderer> waitingDrawingElements = new ArrayList<>();
    List<Rectangle> floatRendererAreas;
    Map<Integer, Integer> latestFootnoteNumber = new HashMap<>();
    private final List<IRenderer> waitingNextPageRenderers = new ArrayList<>();
    private IRenderer keepWithNextHangingRenderer;
    private LayoutResult keepWithNextHangingRendererLayoutResult;
    private MarginsCollapseHandler marginsCollapseHandler;
    private LayoutArea initialCurrentArea;
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
            RootRendererAreaStateHandler rootRendererStateHandler = new RootRendererAreaStateHandler();

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

            MarginsCollapseInfo childMarginsInfo = null;
            if (marginsCollapsingEnabled && currentArea != null) {
                childMarginsInfo = marginsCollapseHandler.startChildMarginsHandling(renderer, currentArea.getBBox());
            }
            int rendererLayoutCounter = 0;
            while (clearanceOverflowsToNextPage || (currentArea != null && renderer != null
                    && (result = layoutChild(renderer, childMarginsInfo)).getStatus() != LayoutResult.FULL)) {
                rendererLayoutCounter = getRendererLayoutCounter(rendererLayoutCounter);
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
                        if (!rootRendererStateHandler.attemptGoForwardToStoredNextState(this)) {
                            currentAreaNeedsToBeUpdated = true;
                        }
                    }
                    addedPositionedRenderers =
                            layoutPositionedRenderersInStaticLoop(addedPositionedRenderers, result.getSplitRenderer());
                } else if (result.getStatus() == LayoutResult.NOTHING && !clearanceOverflowsToNextPage) {
                    if (result.getOverflowRenderer() instanceof ImageRenderer) {
                        float imgHeight = result.getOverflowRenderer().getOccupiedArea().getBBox().getHeight();
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
                            LOGGER.warn(MessageFormatUtil.format(LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA,
                                    ""));
                        }
                    } else {
                        if (currentArea.isEmptyArea() && result.getAreaBreak() == null &&
                                result.getSectionBreak() == null) {
                            boolean keepTogetherChanged = tryDisableKeepTogether(result,
                                    rendererIsFloat, rootRendererStateHandler);

                            boolean areKeepTogetherAndForcedPlacementBothNotChanged = !keepTogetherChanged;
                            if (areKeepTogetherAndForcedPlacementBothNotChanged) {
                                areKeepTogetherAndForcedPlacementBothNotChanged =
                                        !updateForcedPlacement(renderer, result.getOverflowRenderer());
                            }

                            if (areKeepTogetherAndForcedPlacementBothNotChanged) {
                                // FORCED_PLACEMENT was already set to the renderer and
                                // LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA message was logged.
                                // This else-clause should never be hit, otherwise there is a bug in FORCED_PLACEMENT implementation.
                                assert false;

                                // Still handling this case in order to avoid nasty infinite loops.
                                break;
                            }
                        } else {
                            rootRendererStateHandler.storePreviousState(this);
                            if (!rootRendererStateHandler.attemptGoForwardToStoredNextState(this)) {
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
                        LOGGER.warn(IoLogMessageConstant.ELEMENT_WAS_FORCE_PLACED_KEEP_WITH_NEXT_WILL_BE_IGNORED);
                        shrinkCurrentAreaAndProcessRenderer(renderer, resultRenderers, result);
                    } else {
                        keepWithNextHangingRenderer = renderer;
                        keepWithNextHangingRendererLayoutResult = result;
                        this.addAllChildRenderers(resultRenderers);
                    }
                } else if (result.getStatus() != LayoutResult.NOTHING) {
                    shrinkCurrentAreaAndProcessRenderer(renderer, resultRenderers, result);
                }
            }
        }

        for (IRenderer positionedRenderer : addedPositionedRenderers) {
            layoutPositionedRenderer(positionedRenderer);
        }
    }

    private List<IRenderer> layoutPositionedRenderersInStaticLoop(List<IRenderer> addedPositionedRenderers,
                                                                  IRenderer splitRenderer) {
        List<IRenderer> remainingAddedPositionedRenderers = new ArrayList<>();
        for (IRenderer positionedRenderer : addedPositionedRenderers) {
            if (positionedRenderer.hasProperty(Property.POSITIONED_ELEMENT_WRAPPED)
                    && isRendererInSplitRendererTree(positionedRenderer, splitRenderer)) {
                // Positioned renderer wrapper, if exists, was already layouted.
                // It means we need to layout positioned renderer on the same page.
                layoutPositionedRenderer(positionedRenderer);
            } else {
                remainingAddedPositionedRenderers.add(positionedRenderer);
            }
        }
        return remainingAddedPositionedRenderers;
    }

    private void layoutPositionedRenderer(IRenderer positionedRenderer) {
        positionedRenderers.add(positionedRenderer);
        Integer positionedPageNumber = positionedRenderer.<Integer>getProperty(Property.PAGE_NUMBER);
        if (positionedPageNumber == null) {
            positionedPageNumber = currentArea.getPageNumber();
        }

        LayoutArea layoutArea;
        // For position=absolute, if none of the top, bottom, left, right properties are provided,
        // the content should be displayed in the flow of the current content, not overlapping it.
        // The behavior is just if it would be statically positioned except it does not affect other elements
        if (Integer.valueOf(LayoutPosition.ABSOLUTE).equals(positionedRenderer.<Integer>getProperty(Property.POSITION))
                && AbstractRenderer.horizontalCoordinateMissingForAbsolutePosition(positionedRenderer)
                && AbstractRenderer.verticalCoordinateMissingForAbsolutePosition(positionedRenderer)) {
            layoutArea = new LayoutArea((int) positionedPageNumber, currentArea.getBBox().clone());
        } else {
            layoutArea = new LayoutArea((int) positionedPageNumber, initialCurrentArea.getBBox().clone());
        }
        Rectangle fullBbox = layoutArea.getBBox().clone();
        preparePositionedRendererAndAreaForLayout(positionedRenderer, fullBbox, layoutArea.getBBox());
        positionedRenderer.layout(
                new PositionedLayoutContext(new LayoutArea(layoutArea.getPageNumber(), fullBbox), layoutArea));
        if (immediateFlush) {
            flushSingleRenderer(positionedRenderer);
            positionedRenderers.remove(positionedRenderers.size() - 1);
        }
    }

    private LayoutResult layoutChild(IRenderer renderer, MarginsCollapseInfo childMarginsInfo) {
        FootnotesCounterHandler footnotesCounterHandler = FootnotesCounterHandler.getFootnotesCounterHandler(this);
        if (footnotesCounterHandler != null) {
            footnotesCounterHandler.reset();
        }

        boolean isForcedPlacement = Boolean.TRUE.equals(renderer.<Boolean>getProperty(Property.FORCED_PLACEMENT));
        LayoutResult layoutResult = renderer.setParent(this)
                .layout(new LayoutContext(currentArea.clone(), childMarginsInfo, floatRendererAreas));

        if (footnotesCounterHandler == null) {
            return layoutResult;
        }

        // Process footnotes that were collected during renderer layout.
        Map<FootnoteRenderer, Float> footnotes = footnotesCounterHandler.collectFootnotes(
                layoutResult.getOccupiedArea() == null ? currentArea : layoutResult.getOccupiedArea());
        int footnoteAnchorsNum = footnotes.size();
        if (footnoteAnchorsNum == 0) {
            return layoutResult;
        }

        int pageNum = currentArea.getPageNumber();
        PageMarginBoxes pageMarginBoxes = null;
        Document document = new Document(this.getPdfDocument());
        if (this instanceof DocumentRenderer) {
            document = (Document) this.getModelElement();
            pageMarginBoxes = document.getPageMargins(currentArea.getPageNumber());
        }

        FootnotesProperties footnotesProperties = document.getFootnotesProperties();
        FootnoteNumberingConfig footnoteNumberingConfig = footnotesProperties.getFootnoteNumberingConfig();
        if (FootnoteNumberingConfig.PER_PAGE != footnoteNumberingConfig &&
                !latestFootnoteNumber.containsKey(pageNum) && latestFootnoteNumber.containsKey(pageNum - 1)) {
            latestFootnoteNumber.put(pageNum, latestFootnoteNumber.get(pageNum - 1));
        }

        int rendererAdditionalLayoutCounter = 0;

        boolean footnotesPlaced = false;
        float decreasedHeight = 0;
        boolean footnotesNumDefined = false;
        int footnotesNum = 0;
        while (!footnotesPlaced) {
            if (footnotesNumDefined) {
                decreasedHeight = 0;
            } else {
                // Restore initial current area.
                currentArea.getBBox().moveDown(decreasedHeight).increaseHeight(decreasedHeight);
                // Decrease current area from the bottom to the height of footnotes.
                footnotesNum = footnoteAnchorsNum;
                decreasedHeight = 0;
                for (Float footnoteHeight : footnotes.values()) {
                    currentArea.getBBox().moveUp((float) footnoteHeight).decreaseHeight((float) footnoteHeight);
                    decreasedHeight += (float) footnoteHeight;
                }
            }

            footnotesCounterHandler.updateFootnoteNumberingAndStyles(footnotesProperties,
                    (int) latestFootnoteNumber.getOrDefault(pageNum, 0));

            footnotesCounterHandler.reset();
            if (isForcedPlacement) {
                renderer.setProperty(Property.FORCED_PLACEMENT, true);
            }
            layoutResult = renderer.setParent(this)
                    .layout(new LayoutContext(currentArea.clone(), childMarginsInfo, floatRendererAreas));
            if (layoutResult.getStatus() == LayoutResult.NOTHING) {
                footnotes.clear();
                footnotesCounterHandler.reset();
            } else {
                footnotes = footnotesCounterHandler.collectFootnotes(
                        layoutResult.getOccupiedArea() == null ? currentArea : layoutResult.getOccupiedArea());
            }
            footnoteAnchorsNum = footnotes.size();

            // Number of the placed anchors == number of footnotes we reserved the space for before the layout
            footnotesPlaced = footnoteAnchorsNum == footnotesNum;
            if (footnoteAnchorsNum > footnotesNum) {
                footnotesNumDefined = true;
                // Decrease current area from the bottom until extra anchor will be moved to the next page.
                // This logic can be improved in the future.
                currentArea.getBBox().moveUp(1).decreaseHeight(1);
            }
            rendererAdditionalLayoutCounter = getRendererLayoutCounter(rendererAdditionalLayoutCounter);
        }

        if (pageMarginBoxes == null) {
            pageMarginBoxes = new PageMarginBoxes(Collections.<PageMarginContent>emptyList());
            document.setPageMargins(currentArea.getPageNumber(), pageMarginBoxes);
        }
        FootnotesUtil.addFootnotesToPage(pageNum,
                new ArrayList<>(footnotes.keySet()), pageMarginBoxes, footnotesProperties);
        latestFootnoteNumber.put(pageNum, latestFootnoteNumber.containsKey(pageNum) ?
                (latestFootnoteNumber.get(pageNum) + footnotes.size()) : footnotes.size());

        return layoutResult;
    }

    /**
     * Draws (flushes) the content.
     *
     * @see #draw(DrawContext)
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
        flushOnClose();
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


    @Deprecated
    protected void flushWaitingDrawingElements() {
        flushWaitingDrawingElements(true);
    }

    /**
     * Draws (flushes) the content, of this element and all its children that were not yet processed.
     *
     * @see #draw(DrawContext)
     */
    protected void flushOnClose() {
        if (!immediateFlush) {
            flush();
        }
        flushWaitingDrawingElements(true);
    }

    void flushWaitingDrawingElements(boolean force) {
        Set<IRenderer> flushedElements = new HashSet<>();
        for (int i = 0; i < waitingDrawingElements.size(); ++i) {
            IRenderer waitingDrawingElement = waitingDrawingElements.get(i);
            // TODO Remove checking occupied area to be not null when DEVSIX-1655 is resolved.
            if (force || (null != waitingDrawingElement.getOccupiedArea() &&
                    waitingDrawingElement.getOccupiedArea().getPageNumber() < currentArea.getPageNumber())) {
                flushSingleRenderer(waitingDrawingElement);
                flushedElements.add(waitingDrawingElement);
            } else if (null == waitingDrawingElement.getOccupiedArea()) {
                flushedElements.add(waitingDrawingElement);
            }
        }
        waitingDrawingElements.removeAll(flushedElements);
    }

    final void linkRenderToDocument(IRenderer renderer, PdfDocument pdfDocument) {
        if (renderer == null) {
            return;
        }
        final IPropertyContainer container = renderer.getModelElement();
        if (container instanceof AbstractIdentifiableElement) {
            EventManager.getInstance().onEvent(
                    new LinkDocumentIdEvent(pdfDocument, (AbstractIdentifiableElement) container)
            );
        }
        final List<IRenderer> children = renderer.getChildRenderers();
        if (children != null) {
            for (IRenderer child : children) {
                linkRenderToDocument(child, pdfDocument);
            }
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
                // The area break will not be introduced, and we are safe to place everything as is
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
                                shrinkCurrentAreaAndProcessRenderer(firstElementSplitLayoutResult.getSplitRenderer(), new ArrayList<IRenderer>(), firstElementSplitLayoutResult);
                                updateCurrentAndInitialArea(firstElementSplitLayoutResult);
                                shrinkCurrentAreaAndProcessRenderer(firstElementSplitLayoutResult.getOverflowRenderer(), new ArrayList<IRenderer>(), firstElementOverflowLayoutResult);
                            }
                        }
                        if (!ableToProcessKeepWithNext) {
                            currentArea = storedArea;
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
                }
            }
            if (!ableToProcessKeepWithNext) {
                LOGGER.warn(IoLogMessageConstant.RENDERER_WAS_NOT_ABLE_TO_PROCESS_KEEP_WITH_NEXT);
                keepWithNextHangingRendererLayoutResult = keepWithNextHangingRenderer.layout(new LayoutContext(currentArea.clone()));
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

    private int getRendererLayoutCounter(int rendererLayoutCounter) {
        rendererLayoutCounter++;
        LayoutInfiniteLoopResolver loopResolver =
                getPdfDocument().getDiContainer().getInstance(LayoutInfiniteLoopResolver.class);
        int limit = loopResolver == null ?
                MAX_AMOUNT_OF_ELEMENT_LAYOUTS : loopResolver.getMaxPagesCountForSingleElement();
        if (rendererLayoutCounter > limit) {
            throw new PdfException(
                    MessageFormatUtil.format(LayoutExceptionMessageConstant.INFINITE_LOOP_DETECTED, limit / 3));
        }
        return rendererLayoutCounter;
    }

    private boolean updateForcedPlacement(IRenderer currentRenderer, IRenderer overflowRenderer) {
        if (Boolean.TRUE.equals(currentRenderer.<Boolean>getProperty(Property.FORCED_PLACEMENT))) {
            return false;
        } else {
            overflowRenderer.setProperty(Property.FORCED_PLACEMENT, true);
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn(MessageFormatUtil.format(LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, ""));
            }
            return true;
        }
    }

    private boolean tryDisableKeepTogether(LayoutResult result,
                                           boolean rendererIsFloat, RootRendererAreaStateHandler rootRendererStateHandler) {
        IRenderer toDisableKeepTogether = null;

        // looking for the most outer keep together element
        IRenderer current = result.getCauseOfNothing();
        while (current != null) {
            if (Boolean.TRUE.equals(current.<Boolean>getProperty(Property.KEEP_TOGETHER))) {
                toDisableKeepTogether = current;
            }
            current = current.getParent();
        }

        if (toDisableKeepTogether == null) {
            return false;
        }
        if (result.getOverflowRenderer() != null && !isItemInSubtree(result.getOverflowRenderer(), toDisableKeepTogether)) {
            return false;
        }

        toDisableKeepTogether.setProperty(Property.KEEP_TOGETHER, false);
        if (LOGGER.isWarnEnabled()) {
            LOGGER.warn(MessageFormatUtil.format(
                    LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA,
                    "KeepTogether property will be ignored."));
        }
        if (!rendererIsFloat) {
            rootRendererStateHandler.attemptGoBackToStoredPreviousStateAndStoreNextState(this);
        }
        return true;
    }

    private static boolean isItemInSubtree(IRenderer ancestor, IRenderer item) {
        if (ancestor == null) {
            return false;
        }
        if (ancestor == item) {
            return true;
        }
        for (IRenderer renderer : ancestor.getChildRenderers()) {
            if (isItemInSubtree(renderer, item)) {
                return true;
            }
        }
        if (ancestor instanceof TableRenderer) {
            TableRenderer tableRenderer = (TableRenderer) ancestor;
            for (CellRenderer[] row : tableRenderer.rows) {
                for (CellRenderer cellRenderer : row) {
                    if (isItemInSubtree(cellRenderer, item)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
