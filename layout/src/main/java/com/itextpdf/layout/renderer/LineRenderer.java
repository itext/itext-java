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

import com.itextpdf.commons.actions.contexts.IMetaInfo;
import com.itextpdf.commons.actions.sequence.SequenceId;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.io.font.otf.GlyphLine;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.io.util.ArrayUtil;
import com.itextpdf.io.util.TextUtil;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.element.TabStop;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.layout.LineLayoutContext;
import com.itextpdf.layout.layout.LineLayoutResult;
import com.itextpdf.layout.layout.MinMaxWidthLayoutResult;
import com.itextpdf.layout.layout.TextLayoutResult;
import com.itextpdf.layout.minmaxwidth.MinMaxWidth;
import com.itextpdf.layout.minmaxwidth.MinMaxWidthUtils;
import com.itextpdf.layout.properties.BaseDirection;
import com.itextpdf.layout.properties.FloatPropertyValue;
import com.itextpdf.layout.properties.InlineVerticalAlignment;
import com.itextpdf.layout.properties.InlineVerticalAlignmentType;
import com.itextpdf.layout.properties.Leading;
import com.itextpdf.layout.properties.OverflowPropertyValue;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.RenderingMode;
import com.itextpdf.layout.properties.TabAlignment;
import com.itextpdf.layout.properties.TextAnchor;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.renderer.TextSequenceWordWrapping.LastFittingChildRendererData;
import com.itextpdf.layout.renderer.TextSequenceWordWrapping.MinMaxWidthOfTextRendererSequenceHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LineRenderer extends AbstractRenderer {

    // AbstractRenderer.EPS is not enough here
    private static final float MIN_MAX_WIDTH_CORRECTION_EPS = 0.001f;

    private static final Logger logger = LoggerFactory.getLogger(LineRenderer.class);

    protected float maxAscent;
    protected float maxDescent;

    // bidi levels
    protected byte[] levels;

    float maxTextAscent;
    float maxTextDescent;
    private float maxBlockAscent;
    private float maxBlockDescent;

    @Override
    public LayoutResult layout(LayoutContext layoutContext) {
        boolean textSequenceOverflowXProcessing = false;
        int firstChildToRelayout = -1;

        Rectangle layoutBox = layoutContext.getArea().getBBox().clone();
        boolean wasParentsHeightClipped = layoutContext.isClippedHeight();
        List<Rectangle> floatRendererAreas = layoutContext.getFloatRendererAreas();

        OverflowPropertyValue oldXOverflow = null;
        boolean wasXOverflowChanged = false;
        boolean floatsPlacedBeforeLine = false;

        if (floatRendererAreas != null) {
            float layoutWidth = layoutBox.getWidth();
            float layoutHeight = layoutBox.getHeight();
            // consider returning some value to check if layoutBox has been changed due to floats,
            // than reuse on non-float layout: kind of not first piece of content on the line
            FloatingHelper.adjustLineAreaAccordingToFloats(floatRendererAreas, layoutBox);
            if (layoutWidth > layoutBox.getWidth() || layoutHeight > layoutBox.getHeight()) {
                floatsPlacedBeforeLine = true;
                oldXOverflow = this.<OverflowPropertyValue>getProperty(Property.OVERFLOW_X);
                wasXOverflowChanged = true;
                setProperty(Property.OVERFLOW_X, OverflowPropertyValue.FIT);
            }
        }

        boolean noSoftWrap = Boolean.TRUE.equals(this.<Boolean>getOwnProperty(Property.NO_SOFT_WRAP_INLINE));

        LineLayoutContext lineLayoutContext =
                layoutContext instanceof LineLayoutContext ? (LineLayoutContext) layoutContext
                        : new LineLayoutContext(layoutContext);
        if (lineLayoutContext.getTextIndent() != 0) {
            layoutBox
                    .moveRight(lineLayoutContext.getTextIndent())
                    .setWidth(layoutBox.getWidth() - lineLayoutContext.getTextIndent());
        }

        occupiedArea = new LayoutArea(layoutContext.getArea().getPageNumber(),
                layoutBox.clone().moveUp(layoutBox.getHeight()).setHeight(0).setWidth(0));

        updateChildrenParent();

        TargetCounterHandler.addPageByID(this);

        float curWidth = 0;
        if (RenderingMode.HTML_MODE.equals(this.<RenderingMode>getProperty(Property.RENDERING_MODE))
                && hasChildRendererInHtmlMode()) {
            float[] ascenderDescender = LineHeightHelper.getActualAscenderDescender(this);
            maxAscent = ascenderDescender[0];
            maxDescent = ascenderDescender[1];
        } else {
            maxAscent = 0;
            maxDescent = 0;
        }
        maxTextAscent = 0;
        maxTextDescent = 0;
        maxBlockAscent = -1e20f;
        maxBlockDescent = 1e20f;

        int childPos = 0;

        MinMaxWidth minMaxWidth = new MinMaxWidth();
        AbstractWidthHandler widthHandler;
        if (noSoftWrap) {
            widthHandler = new SumSumWidthHandler(minMaxWidth);
        } else {
            widthHandler = new MaxSumWidthHandler(minMaxWidth);
        }

        resolveChildrenFonts();

        int totalNumberOfTrimmedGlyphs = trimFirst();

        BaseDirection baseDirection = applyOtf();

        updateBidiLevels(totalNumberOfTrimmedGlyphs, baseDirection);

        boolean anythingPlaced = false;
        TabStop hangingTabStop = null;
        LineLayoutResult result = null;

        boolean floatsPlacedInLine = false;
        Map<Integer, IRenderer> floatsToNextPageSplitRenderers = new LinkedHashMap<>();
        List<IRenderer> floatsToNextPageOverflowRenderers = new ArrayList<>();
        List<IRenderer> floatsOverflowedToNextLine = new ArrayList<>();
        int lastTabIndex = 0;

        Map<Integer, LayoutResult> specialScriptLayoutResults = new HashMap<>();
        Map<Integer, LayoutResult> textRendererLayoutResults = new HashMap<>();

        Map<Integer, float[]> textRendererSequenceAscentDescent = new HashMap<>();
        LineAscentDescentState lineAscentDescentStateBeforeTextRendererSequence = null;

        MinMaxWidthOfTextRendererSequenceHelper minMaxWidthOfTextRendererSequenceHelper = null;

        while (childPos < getChildRenderers().size()) {
            IRenderer childRenderer = getChildRenderers().get(childPos);
            LayoutResult childResult = null;
            Rectangle bbox = new Rectangle(layoutBox.getX() + curWidth, layoutBox.getY(),
                    layoutBox.getWidth() - curWidth, layoutBox.getHeight());

            RenderingMode childRenderingMode = childRenderer.<RenderingMode>getProperty(Property.RENDERING_MODE);

            if (TextSequenceWordWrapping.isTextRendererAndRequiresSpecialScriptPreLayoutProcessing(childRenderer)
                    && TypographyUtils.isPdfCalligraphAvailable()) {
                TextSequenceWordWrapping.processSpecialScriptPreLayout(this, childPos);
            }
            TextSequenceWordWrapping.resetTextSequenceIfItEnded(
                    specialScriptLayoutResults, true, childRenderer, childPos,
                    minMaxWidthOfTextRendererSequenceHelper, noSoftWrap, widthHandler);
            TextSequenceWordWrapping.resetTextSequenceIfItEnded(
                    textRendererLayoutResults, false, childRenderer, childPos,
                    minMaxWidthOfTextRendererSequenceHelper, noSoftWrap, widthHandler);

            if (childRenderer instanceof TextRenderer) {
                // Delete these properties in case of relayout. We might have applied them during justify().
                childRenderer.deleteOwnProperty(Property.CHARACTER_SPACING);
                childRenderer.deleteOwnProperty(Property.WORD_SPACING);
            } else if (childRenderer instanceof TabRenderer) {
                if (hangingTabStop != null) {
                    IRenderer tabRenderer = getChildRenderers().get(childPos - 1);
                    tabRenderer.layout(new LayoutContext(new LayoutArea(layoutContext.getArea().getPageNumber(), bbox),
                            wasParentsHeightClipped));
                    curWidth += tabRenderer.getOccupiedArea().getBBox().getWidth();
                    widthHandler.updateMaxChildWidth(tabRenderer.getOccupiedArea().getBBox().getWidth());
                }
                hangingTabStop = calculateTab(childRenderer, curWidth, layoutBox.getWidth());
                if (childPos == getChildRenderers().size() - 1) {
                    hangingTabStop = null;
                }
                if (hangingTabStop != null) {
                    lastTabIndex = childPos;
                    ++childPos;
                    continue;
                }
            }

            if (hangingTabStop != null && hangingTabStop.getTabAlignment() == TabAlignment.ANCHOR
                    && childRenderer instanceof TextRenderer) {
                childRenderer.setProperty(Property.TAB_ANCHOR, hangingTabStop.getTabAnchor());
            }

            // Normalize child width
            Object childWidth = childRenderer.<Object>getProperty(Property.WIDTH);
            boolean childWidthWasReplaced = false;
            boolean childRendererHasOwnWidthProperty = childRenderer.hasOwnProperty(Property.WIDTH);
            if (childWidth instanceof UnitValue && ((UnitValue) childWidth).isPercentValue()) {
                float normalizedChildWidth =
                        ((UnitValue) childWidth).getValue() / 100 * layoutContext.getArea().getBBox().getWidth();
                normalizedChildWidth = decreaseRelativeWidthByChildAdditionalWidth(childRenderer, normalizedChildWidth);

                if (normalizedChildWidth > 0) {
                    childRenderer.setProperty(Property.WIDTH, UnitValue.createPointValue(normalizedChildWidth));
                    childWidthWasReplaced = true;
                }
            }

            FloatPropertyValue kidFloatPropertyVal = childRenderer.<FloatPropertyValue>getProperty(Property.FLOAT);
            boolean isChildFloating =
                    childRenderer instanceof AbstractRenderer && FloatingHelper.isRendererFloating(childRenderer,
                            kidFloatPropertyVal);
            if (isChildFloating) {
                childResult = null;
                MinMaxWidth kidMinMaxWidth = FloatingHelper.calculateMinMaxWidthForFloat(
                        (AbstractRenderer) childRenderer, kidFloatPropertyVal);
                float floatingBoxFullWidth = kidMinMaxWidth.getMaxWidth();
                // Width will be recalculated on float layout;
                // also not taking it into account (i.e. not setting it on child renderer) results in differences with html
                // when floating span is split on other line;
                // TODO DEVSIX-1730: may be process floating spans as inline blocks always?

                if (!wasXOverflowChanged && childPos > 0) {
                    oldXOverflow = this.<OverflowPropertyValue>getProperty(Property.OVERFLOW_X);
                    wasXOverflowChanged = true;
                    setProperty(Property.OVERFLOW_X, OverflowPropertyValue.FIT);
                }
                if (!lineLayoutContext.isFloatOverflowedToNextPageWithNothing() && floatsOverflowedToNextLine.isEmpty()
                        && (!anythingPlaced || floatingBoxFullWidth <= bbox.getWidth())) {
                    childResult = childRenderer.layout(new LayoutContext(
                            new LayoutArea(layoutContext.getArea().getPageNumber(),
                                    layoutContext.getArea().getBBox().clone()), null, floatRendererAreas,
                            wasParentsHeightClipped));
                }
                // Get back child width so that it's not lost
                if (childWidthWasReplaced) {
                    if (childRendererHasOwnWidthProperty) {
                        childRenderer.setProperty(Property.WIDTH, childWidth);
                    } else {
                        childRenderer.deleteOwnProperty(Property.WIDTH);
                    }
                }

                float minChildWidth = 0;
                float maxChildWidth = 0;
                if (childResult instanceof MinMaxWidthLayoutResult) {
                    if (!childWidthWasReplaced) {
                        minChildWidth = ((MinMaxWidthLayoutResult) childResult).getMinMaxWidth().getMinWidth();
                    }
                    maxChildWidth = ((MinMaxWidthLayoutResult) childResult).getMinMaxWidth().getMaxWidth();
                    widthHandler.updateMinChildWidth(minChildWidth + AbstractRenderer.EPS);
                    widthHandler.updateMaxChildWidth(maxChildWidth + AbstractRenderer.EPS);
                } else {
                    widthHandler.updateMinChildWidth(kidMinMaxWidth.getMinWidth() + AbstractRenderer.EPS);
                    widthHandler.updateMaxChildWidth(kidMinMaxWidth.getMaxWidth() + AbstractRenderer.EPS);
                }

                if (childResult == null && !lineLayoutContext.isFloatOverflowedToNextPageWithNothing()) {
                    floatsOverflowedToNextLine.add(childRenderer);
                } else if (lineLayoutContext.isFloatOverflowedToNextPageWithNothing()
                        || childResult.getStatus() == LayoutResult.NOTHING) {
                    floatsToNextPageSplitRenderers.put(childPos, null);
                    floatsToNextPageOverflowRenderers.add(childRenderer);
                    lineLayoutContext.setFloatOverflowedToNextPageWithNothing(true);
                } else if (childResult.getStatus() == LayoutResult.PARTIAL) {
                    floatsPlacedInLine = true;

                    if (childRenderer instanceof TextRenderer) {
                        // This code is specifically for floating inline text elements:
                        // inline elements cannot have fixed width, also they progress horizontally, which means
                        // that if they don't fit in one line, they will definitely be moved onto the new line (and also
                        // under all floats). Specifying the whole width of layout area is required to avoid possible normal
                        // content wrapping around floating text in case floating text gets wrapped onto the next line
                        // not evenly.
                        LineRenderer[] split = splitNotFittingFloat(childPos, childResult);
                        IRenderer splitRenderer = childResult.getSplitRenderer();
                        if (splitRenderer instanceof TextRenderer) {
                            ((TextRenderer) splitRenderer).trimFirst();
                            ((TextRenderer) splitRenderer).trimLast();
                        }
                        // ensure no other thing (like text wrapping the float) will occupy the line
                        splitRenderer.getOccupiedArea().getBBox()
                                .setWidth(layoutContext.getArea().getBBox().getWidth());
                        result = new LineLayoutResult(LayoutResult.PARTIAL, occupiedArea, split[0], split[1], null);
                        break;
                    } else {
                        floatsToNextPageSplitRenderers.put(childPos, childResult.getSplitRenderer());
                        floatsToNextPageOverflowRenderers.add(childResult.getOverflowRenderer());
                        adjustLineOnFloatPlaced(layoutBox, childPos, kidFloatPropertyVal,
                                childResult.getSplitRenderer().getOccupiedArea().getBBox());
                    }
                } else {
                    floatsPlacedInLine = true;

                    if (childRenderer instanceof TextRenderer) {
                        ((TextRenderer) childRenderer).trimFirst();
                        ((TextRenderer) childRenderer).trimLast();
                    }

                    adjustLineOnFloatPlaced(layoutBox, childPos, kidFloatPropertyVal,
                            childRenderer.getOccupiedArea().getBBox());
                }

                childPos++;
                if (!anythingPlaced && childResult != null && childResult.getStatus() == LayoutResult.NOTHING
                        && floatRendererAreas.isEmpty()) {
                    if (isFirstOnRootArea()) {
                        // Current line is empty, kid returns nothing and neither floats nor content
                        // were met on root area (e.g. page area) - return NOTHING, don't layout other line content,
                        // expect FORCED_PLACEMENT to be set.
                        break;
                    }
                }
                continue;
            }

            MinMaxWidth childBlockMinMaxWidth = null;
            boolean isInlineBlockChild = isInlineBlockChild(childRenderer);
            if (isInlineBlockChild && childRenderer instanceof AbstractRenderer) {
                final MinMaxWidth childBlockMinMaxWidthLocal = ((AbstractRenderer) childRenderer).getMinMaxWidth();
                // Don't calculate childBlockMinMaxWidth in case of relative width here
                // and further (childBlockMinMaxWidth != null)
                if (!childWidthWasReplaced) {
                    childBlockMinMaxWidth = childBlockMinMaxWidthLocal;
                }

                float childMaxWidth = childBlockMinMaxWidthLocal.getMaxWidth();
                float lineFullAvailableWidth = layoutContext.getArea().getBBox().getWidth() - lineLayoutContext.getTextIndent();
                if (!noSoftWrap && childMaxWidth > bbox.getWidth() + MIN_MAX_WIDTH_CORRECTION_EPS && bbox.getWidth() != lineFullAvailableWidth) {
                    childResult = new LineLayoutResult(LayoutResult.NOTHING, null, null, childRenderer, childRenderer);
                } else {
                    if (childBlockMinMaxWidth != null) {
                        childMaxWidth += MIN_MAX_WIDTH_CORRECTION_EPS;
                        float inlineBlockWidth = Math.min(childMaxWidth, lineFullAvailableWidth);

                        if (!isOverflowFit(this.<OverflowPropertyValue>getProperty(Property.OVERFLOW_X))) {
                            float childMinWidth = childBlockMinMaxWidth.getMinWidth() + MIN_MAX_WIDTH_CORRECTION_EPS;
                            inlineBlockWidth = Math.max(childMinWidth, inlineBlockWidth);
                        }
                        bbox.setWidth(inlineBlockWidth);

                        if (childBlockMinMaxWidth.getMinWidth() > bbox.getWidth()) {
                            if (logger.isWarnEnabled()) {
                                logger.warn(IoLogMessageConstant.INLINE_BLOCK_ELEMENT_WILL_BE_CLIPPED);
                            }
                            childRenderer.setProperty(Property.FORCED_PLACEMENT, true);
                        }
                    }
                }

                if (childBlockMinMaxWidth != null) {
                    childBlockMinMaxWidth.setChildrenMaxWidth(
                            childBlockMinMaxWidth.getChildrenMaxWidth() + MIN_MAX_WIDTH_CORRECTION_EPS);
                    childBlockMinMaxWidth.setChildrenMinWidth(
                            childBlockMinMaxWidth.getChildrenMinWidth() + MIN_MAX_WIDTH_CORRECTION_EPS);
                }
            }

            boolean shouldBreakLayouting = false;

            if (childResult == null) {
                boolean setOverflowFitCausedBySpecialScripts = childRenderer instanceof TextRenderer
                        && ((TextRenderer) childRenderer).textContainsSpecialScriptGlyphs(true);

                boolean setOverflowFitCausedByTextRendererInHtmlMode = RenderingMode.HTML_MODE == childRenderingMode
                        && childRenderer instanceof TextRenderer
                        && !((TextRenderer) childRenderer).textContainsSpecialScriptGlyphs(true);

                if (!wasXOverflowChanged
                        && (childPos > 0 || setOverflowFitCausedBySpecialScripts
                        || setOverflowFitCausedByTextRendererInHtmlMode)
                        && !textSequenceOverflowXProcessing) {
                    oldXOverflow = this.<OverflowPropertyValue>getProperty(Property.OVERFLOW_X);
                    wasXOverflowChanged = true;
                    setProperty(Property.OVERFLOW_X, OverflowPropertyValue.FIT);
                }

                TextSequenceWordWrapping.preprocessTextSequenceOverflowX(this, textSequenceOverflowXProcessing,
                        childRenderer, wasXOverflowChanged, oldXOverflow);

                childResult = childRenderer.layout(
                        new LayoutContext(new LayoutArea(layoutContext.getArea().getPageNumber(), bbox),
                                wasParentsHeightClipped));

                shouldBreakLayouting = TextSequenceWordWrapping.postprocessTextSequenceOverflowX(
                        this, textSequenceOverflowXProcessing,
                        childPos, childRenderer, childResult, wasXOverflowChanged);

                TextSequenceWordWrapping.updateTextSequenceLayoutResults(
                        textRendererLayoutResults, false, childRenderer, childPos, childResult);
                TextSequenceWordWrapping.updateTextSequenceLayoutResults(
                        specialScriptLayoutResults, true, childRenderer, childPos, childResult);

                // it means that we've already increased layout area by MIN_MAX_WIDTH_CORRECTION_EPS
                if (childResult instanceof MinMaxWidthLayoutResult && null != childBlockMinMaxWidth) {
                    MinMaxWidth childResultMinMaxWidth = ((MinMaxWidthLayoutResult) childResult).getMinMaxWidth();
                    childResultMinMaxWidth.setChildrenMaxWidth(
                            childResultMinMaxWidth.getChildrenMaxWidth() + MIN_MAX_WIDTH_CORRECTION_EPS);
                    childResultMinMaxWidth.setChildrenMinWidth(
                            childResultMinMaxWidth.getChildrenMinWidth() + MIN_MAX_WIDTH_CORRECTION_EPS);
                }
            }

            // Get back child width so that it's not lost
            if (childWidthWasReplaced) {
                if (childRendererHasOwnWidthProperty) {
                    childRenderer.setProperty(Property.WIDTH, childWidth);
                } else {
                    childRenderer.deleteOwnProperty(Property.WIDTH);
                }
            }

            float minChildWidth = 0;
            float maxChildWidth = 0;
            if (childResult instanceof MinMaxWidthLayoutResult) {
                if (!childWidthWasReplaced) {
                    minChildWidth = ((MinMaxWidthLayoutResult) childResult).getMinMaxWidth().getMinWidth();
                }
                maxChildWidth = ((MinMaxWidthLayoutResult) childResult).getMinMaxWidth().getMaxWidth();
            } else if (childBlockMinMaxWidth != null) {
                minChildWidth = childBlockMinMaxWidth.getMinWidth();
                maxChildWidth = childBlockMinMaxWidth.getMaxWidth();
            }

            float[] childAscentDescent = getAscentDescentOfLayoutedChildRenderer(childRenderer, childResult,
                    childRenderingMode, isInlineBlockChild);

            lineAscentDescentStateBeforeTextRendererSequence =
                    TextSequenceWordWrapping.updateTextRendererSequenceAscentDescent(
                            this, textRendererSequenceAscentDescent, childPos, childAscentDescent,
                            lineAscentDescentStateBeforeTextRendererSequence);

            minMaxWidthOfTextRendererSequenceHelper =
                    TextSequenceWordWrapping.updateTextRendererSequenceMinMaxWidth(
                            this, widthHandler, childPos,
                            minMaxWidthOfTextRendererSequenceHelper, anythingPlaced, textRendererLayoutResults,
                            specialScriptLayoutResults, lineLayoutContext.getTextIndent());

            boolean newLineOccurred = (childResult instanceof TextLayoutResult
                    && ((TextLayoutResult) childResult).isSplitForcedByNewline());
            if (!shouldBreakLayouting) {
                shouldBreakLayouting = childResult.getStatus() != LayoutResult.FULL || newLineOccurred;
            }

            boolean shouldBreakLayoutingOnTextRenderer = shouldBreakLayouting
                    && childResult instanceof TextLayoutResult;
            boolean forceOverflowForTextRendererPartialResult = false;

            if (shouldBreakLayoutingOnTextRenderer) {
                boolean isWordHasBeenSplitLayoutRenderingMode = ((TextLayoutResult) childResult).isWordHasBeenSplit()
                        && RenderingMode.HTML_MODE != childRenderingMode
                        && !((TextRenderer) childRenderer).textContainsSpecialScriptGlyphs(true);
                boolean enableSpecialScriptsWrapping = ((TextRenderer) getChildRenderers().get(childPos))
                        .textContainsSpecialScriptGlyphs(true)
                        && !textSequenceOverflowXProcessing && !newLineOccurred;
                boolean enableTextSequenceWrapping = RenderingMode.HTML_MODE == childRenderingMode && !newLineOccurred
                        && !textSequenceOverflowXProcessing;

                if (isWordHasBeenSplitLayoutRenderingMode) {
                    forceOverflowForTextRendererPartialResult = isForceOverflowForTextRendererPartialResult(
                            childRenderer, wasXOverflowChanged, oldXOverflow, layoutContext, layoutBox,
                            wasParentsHeightClipped);
                } else if (enableSpecialScriptsWrapping) {
                    boolean isOverflowFit = wasXOverflowChanged
                            ? (oldXOverflow == OverflowPropertyValue.FIT)
                            : isOverflowFit(this.<OverflowPropertyValue>getProperty(Property.OVERFLOW_X));
                    LastFittingChildRendererData lastFittingChildRendererData =
                            TextSequenceWordWrapping.getIndexAndLayoutResultOfTheLastTextRendererContainingSpecialScripts(
                                    this, childPos,
                                    specialScriptLayoutResults, wasParentsHeightClipped,
                                    isOverflowFit);

                    if (lastFittingChildRendererData == null) {
                        textSequenceOverflowXProcessing = true;
                        shouldBreakLayouting = false;
                        firstChildToRelayout = childPos;
                    } else {
                        curWidth -= TextSequenceWordWrapping.getCurWidthRelayoutedTextSequenceDecrement(childPos,
                                lastFittingChildRendererData.childIndex, specialScriptLayoutResults);
                        childPos = lastFittingChildRendererData.childIndex;
                        childResult = lastFittingChildRendererData.childLayoutResult;
                        specialScriptLayoutResults.put(childPos, childResult);

                        MinMaxWidth textSequenceElemminMaxWidth = ((MinMaxWidthLayoutResult) childResult).getMinMaxWidth();
                        minChildWidth = textSequenceElemminMaxWidth.getMinWidth();
                        maxChildWidth = textSequenceElemminMaxWidth.getMaxWidth();
                    }
                } else if (enableTextSequenceWrapping) {
                    boolean isOverflowFit = wasXOverflowChanged
                            ? (oldXOverflow == OverflowPropertyValue.FIT)
                            : isOverflowFit(this.<OverflowPropertyValue>getProperty(Property.OVERFLOW_X));
                    LastFittingChildRendererData lastFittingChildRendererData =
                            TextSequenceWordWrapping.getIndexAndLayoutResultOfTheLastTextRendererWithNoSpecialScripts(
                                    this, childPos,
                                    textRendererLayoutResults, wasParentsHeightClipped,
                                    isOverflowFit, floatsPlacedInLine || floatsPlacedBeforeLine);
                    if (lastFittingChildRendererData == null) {
                        textSequenceOverflowXProcessing = true;
                        shouldBreakLayouting = false;
                        firstChildToRelayout = childPos;
                    } else {
                        curWidth -= TextSequenceWordWrapping.getCurWidthRelayoutedTextSequenceDecrement(childPos,
                                lastFittingChildRendererData.childIndex, textRendererLayoutResults);
                        childAscentDescent =
                                updateAscentDescentAfterTextRendererSequenceProcessing(
                                        (lastFittingChildRendererData.childLayoutResult.getStatus()
                                                == LayoutResult.NOTHING)
                                                ? (lastFittingChildRendererData.childIndex - 1)
                                                : lastFittingChildRendererData.childIndex,
                                        lineAscentDescentStateBeforeTextRendererSequence,
                                        textRendererSequenceAscentDescent);

                        childPos = lastFittingChildRendererData.childIndex;
                        childResult = lastFittingChildRendererData.childLayoutResult;
                        if (0 == childPos && LayoutResult.NOTHING == childResult.getStatus()) {
                            anythingPlaced = false;
                        }
                        textRendererLayoutResults.put(childPos, childResult);

                        MinMaxWidth textSequenceElemminMaxWidth = ((MinMaxWidthLayoutResult) childResult).getMinMaxWidth();
                        minChildWidth = textSequenceElemminMaxWidth.getMinWidth();
                        maxChildWidth = textSequenceElemminMaxWidth.getMaxWidth();
                    }
                }
            }

            if (childPos != firstChildToRelayout) {
                if (!forceOverflowForTextRendererPartialResult) {
                    updateAscentDescentAfterChildLayout(childAscentDescent, childRenderer, isChildFloating);
                }
                float maxHeight = maxAscent - maxDescent;

                float currChildTextIndent = anythingPlaced ? 0 : lineLayoutContext.getTextIndent();
                if (hangingTabStop != null && (
                        TabAlignment.LEFT == hangingTabStop.getTabAlignment()
                                || shouldBreakLayouting
                                || getChildRenderers().size() - 1 == childPos
                                || getChildRenderers().get(childPos + 1) instanceof TabRenderer)) {
                    IRenderer tabRenderer = getChildRenderers().get(lastTabIndex);
                    List<IRenderer> affectedRenderers = new ArrayList<>();
                    affectedRenderers.addAll(getChildRenderers().subList(lastTabIndex + 1, childPos + 1));
                    float tabWidth = calculateTab(layoutBox, curWidth, hangingTabStop, affectedRenderers, tabRenderer);

                    tabRenderer.layout(new LayoutContext(new LayoutArea(layoutContext.getArea().getPageNumber(), bbox),
                            wasParentsHeightClipped));
                    float sumOfAffectedRendererWidths = 0;
                    for (IRenderer renderer : affectedRenderers) {
                        renderer.move(tabWidth + sumOfAffectedRendererWidths, 0);
                        sumOfAffectedRendererWidths += renderer.getOccupiedArea().getBBox().getWidth();
                    }
                    if (childResult.getSplitRenderer() != null) {
                        childResult.getSplitRenderer()
                                .move(tabWidth + sumOfAffectedRendererWidths - childResult.getSplitRenderer()
                                        .getOccupiedArea().getBBox().getWidth(), 0);
                    }
                    float tabAndNextElemWidth = tabWidth + childResult.getOccupiedArea().getBBox().getWidth();
                    if (hangingTabStop.getTabAlignment() == TabAlignment.RIGHT
                            && curWidth + tabAndNextElemWidth < hangingTabStop.getTabPosition()) {
                        curWidth = hangingTabStop.getTabPosition();
                    } else {
                        curWidth += tabAndNextElemWidth;
                    }
                    widthHandler.updateMinChildWidth(minChildWidth + currChildTextIndent);
                    widthHandler.updateMaxChildWidth(tabWidth + maxChildWidth + currChildTextIndent);
                    hangingTabStop = null;
                } else if (null == hangingTabStop) {
                    if (childResult.getOccupiedArea() != null && childResult.getOccupiedArea().getBBox() != null) {
                        curWidth += childResult.getOccupiedArea().getBBox().getWidth();
                    }
                    widthHandler.updateMinChildWidth(minChildWidth + currChildTextIndent);
                    widthHandler.updateMaxChildWidth(maxChildWidth + currChildTextIndent);
                }
                if (!forceOverflowForTextRendererPartialResult) {
                    occupiedArea.setBBox(
                            new Rectangle(layoutBox.getX(), layoutBox.getY() + layoutBox.getHeight() - maxHeight,
                                    curWidth, maxHeight));
                }
            }

            if (shouldBreakLayouting) {
                LineRenderer[] split = split();
                split[0].setChildRenderers(getChildRenderers().subList(0, childPos));

                if (forceOverflowForTextRendererPartialResult) {
                    split[1].addChildRenderer(childRenderer);
                } else {
                    boolean forcePlacement = Boolean.TRUE.equals(getPropertyAsBoolean(Property.FORCED_PLACEMENT));
                    boolean isInlineBlockAndFirstOnRootArea = isInlineBlockChild && isFirstOnRootArea();
                    if ((childResult.getStatus() == LayoutResult.PARTIAL
                            && (!isInlineBlockChild || forcePlacement || isInlineBlockAndFirstOnRootArea))
                            || childResult.getStatus() == LayoutResult.FULL) {
                        final IRenderer splitRenderer = childResult.getSplitRenderer();
                        split[0].addChild(splitRenderer);
                        // TODO: DEVSIX-4717 this code should be removed if/when the AbstractRenderer
                        //  would start using the newly added methods
                        if (splitRenderer.getParent() != split[0] && split[0].childRenderers.contains(splitRenderer)) {
                            splitRenderer.setParent(split[0]);
                        }
                        anythingPlaced = true;
                    }

                    if (null != childResult.getOverflowRenderer()) {
                        if (isInlineBlockChild && !forcePlacement && !isInlineBlockAndFirstOnRootArea) {
                            split[1].addChildRenderer(childRenderer);
                        } else if (isInlineBlockChild
                                && childResult.getOverflowRenderer().getChildRenderers().isEmpty()
                                && childResult.getStatus() == LayoutResult.PARTIAL) {
                            if (logger.isWarnEnabled()) {
                                logger.warn(IoLogMessageConstant.INLINE_BLOCK_ELEMENT_WILL_BE_CLIPPED);
                            }
                        } else {
                            split[1].addChildRenderer(childResult.getOverflowRenderer());
                        }
                    }
                }
                split[1].addAllChildRenderers(getChildRenderers().subList(childPos + 1, getChildRenderers().size()));

                replaceSplitRendererKidFloats(floatsToNextPageSplitRenderers, split[0]);
                split[0].removeAllChildRenderers(floatsOverflowedToNextLine);
                split[1].addAllChildRenderers(0, floatsOverflowedToNextLine);

                // no sense to process empty renderer
                if (split[1].getChildRenderers().isEmpty() && floatsToNextPageOverflowRenderers.isEmpty()) {
                    split[1] = null;
                }

                final IRenderer causeOfNothing = childResult.getStatus() == LayoutResult.NOTHING
                        ? childResult.getCauseOfNothing() : getChildRenderers().get(childPos);
                if (split[1] == null) {
                    result = new LineLayoutResult(LayoutResult.FULL, occupiedArea, split[0], split[1], causeOfNothing);
                } else if (anythingPlaced || floatsPlacedInLine) {
                    result = new LineLayoutResult(LayoutResult.PARTIAL, occupiedArea, split[0],
                            split[1], causeOfNothing);
                } else {
                    result = new LineLayoutResult(LayoutResult.NOTHING, null, null, split[1], null);
                }
                result.setFloatsOverflowedToNextPage(floatsToNextPageOverflowRenderers);
                if (newLineOccurred) {
                    result.setSplitForcedByNewline(true);
                }

                break;
            } else {
                if (childPos == firstChildToRelayout) {
                    firstChildToRelayout = -1;
                } else {
                    anythingPlaced = true;
                    childPos++;
                }
            }
        }

        TextSequenceWordWrapping.resetTextSequenceIfItEnded(specialScriptLayoutResults, true, null, childPos,
                minMaxWidthOfTextRendererSequenceHelper, noSoftWrap, widthHandler);
        TextSequenceWordWrapping.resetTextSequenceIfItEnded(textRendererLayoutResults, false, null, childPos,
                minMaxWidthOfTextRendererSequenceHelper, noSoftWrap, widthHandler);

        if (result == null) {
            boolean noOverflowedFloats =
                    floatsOverflowedToNextLine.isEmpty() && floatsToNextPageOverflowRenderers.isEmpty();
            if (((anythingPlaced || floatsPlacedInLine) && noOverflowedFloats) || getChildRenderers().isEmpty()) {
                result = new LineLayoutResult(LayoutResult.FULL, occupiedArea, null, null);
            } else {
                if (noOverflowedFloats) {
                    // all kids were some non-image and non-text kids (tab-stops?),
                    // but in this case, it should be okay to return FULL, as there is nothing to be placed
                    result = new LineLayoutResult(LayoutResult.FULL, occupiedArea, null, null);
                } else if (anythingPlaced || floatsPlacedInLine) {
                    LineRenderer[] split = split();
                    split[0].addAllChildRenderers(getChildRenderers().subList(0, childPos));
                    replaceSplitRendererKidFloats(floatsToNextPageSplitRenderers, split[0]);
                    split[0].removeAllChildRenderers(floatsOverflowedToNextLine);

                    // If `result` variable is null up until now but not everything was placed - there is no
                    // content overflow, only floats are overflowing.
                    // The floatsOverflowedToNextLine might be empty, while the only overflowing floats are
                    // in floatsToNextPageOverflowRenderers. This situation is handled in ParagraphRenderer separately.
                    split[1].addAllChildRenderers(floatsOverflowedToNextLine);
                    result = new LineLayoutResult(LayoutResult.PARTIAL, occupiedArea, split[0], split[1], null);
                    result.setFloatsOverflowedToNextPage(floatsToNextPageOverflowRenderers);
                } else {
                    IRenderer causeOfNothing =
                            floatsOverflowedToNextLine.isEmpty() ? floatsToNextPageOverflowRenderers.get(0)
                                    : floatsOverflowedToNextLine.get(0);
                    result = new LineLayoutResult(LayoutResult.NOTHING, null, null, this, causeOfNothing);
                }
            }
        }

        LineRenderer toProcess = (LineRenderer) result.getSplitRenderer();
        if (toProcess == null && result.getStatus() == LayoutResult.FULL) {
            toProcess = this;
        }
        if (baseDirection != null && baseDirection != BaseDirection.NO_BIDI && toProcess != null) {
            final LineSplitIntoGlyphsData splitIntoGlyphsData = splitLineIntoGlyphs(toProcess);
            final byte[] lineLevels = new byte[splitIntoGlyphsData.getLineGlyphs().size()];
            if (levels != null) {
                System.arraycopy(levels, 0, lineLevels, 0, splitIntoGlyphsData.getLineGlyphs().size());
            }

            final int[] newOrder = TypographyUtils.reorderLine(splitIntoGlyphsData.getLineGlyphs(), lineLevels, levels);
            if (newOrder != null) {
                reorder(toProcess, splitIntoGlyphsData, newOrder);
                adjustChildPositionsAfterReordering(toProcess.getChildRenderers(), occupiedArea.getBBox().getLeft());
            }

            if (result.getStatus() == LayoutResult.PARTIAL && levels != null) {
                LineRenderer overflow = (LineRenderer) result.getOverflowRenderer();
                overflow.levels = new byte[levels.length - lineLevels.length];
                System.arraycopy(levels, lineLevels.length, overflow.levels, 0, overflow.levels.length);
                if (overflow.levels.length == 0) {
                    overflow.levels = null;
                }
            }
        }

        if (anythingPlaced || floatsPlacedInLine) {
            toProcess.adjustChildrenYLine().trimLast();
            toProcess.adjustChildrenXLine();
            result.setMinMaxWidth(minMaxWidth);
        }

        if (wasXOverflowChanged) {
            setProperty(Property.OVERFLOW_X, oldXOverflow);
            if (null != result.getSplitRenderer()) {
                result.getSplitRenderer().setProperty(Property.OVERFLOW_X, oldXOverflow);
            }
            if (null != result.getOverflowRenderer()) {
                result.getOverflowRenderer().setProperty(Property.OVERFLOW_X, oldXOverflow);
            }
        }
        return result;
    }

    public float getMaxAscent() {
        return maxAscent;
    }

    public float getMaxDescent() {
        return maxDescent;
    }

    public float getYLine() {
        return occupiedArea.getBBox().getY() - maxDescent;
    }

    public float getLeadingValue(Leading leading) {
        switch (leading.getType()) {
            case Leading.FIXED:
                return Math.max(leading.getValue(), maxBlockAscent - maxBlockDescent);
            case Leading.MULTIPLIED:
                return getTopLeadingIndent(leading) + getBottomLeadingIndent(leading);
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public IRenderer getNextRenderer() {
        return new LineRenderer();
    }

    @Override
    protected Float getFirstYLineRecursively() {
        return getYLine();
    }

    @Override
    protected Float getLastYLineRecursively() {
        return getYLine();
    }

    public void justify(float width) {
        float ratio = (float) this.getPropertyAsFloat(Property.SPACING_RATIO);
        IRenderer lastChildRenderer = getLastNonFloatChildRenderer();
        if (lastChildRenderer == null) {
            return;
        }
        float freeWidth = occupiedArea.getBBox().getX() + width - lastChildRenderer.getOccupiedArea().getBBox().getX() -
                lastChildRenderer.getOccupiedArea().getBBox().getWidth();
        int numberOfSpaces = getNumberOfSpaces();
        int baseCharsCount = baseCharactersCount();
        float baseFactor = freeWidth / (ratio * numberOfSpaces + (1 - ratio) * (baseCharsCount - 1));

        //Prevent a NaN when trying to justify a single word with spacing_ratio == 1.0
        if (Float.isInfinite(baseFactor) || Float.isNaN(baseFactor)) {
            baseFactor = 0;
        }
        float wordSpacing = ratio * baseFactor;
        float characterSpacing = (1 - ratio) * baseFactor;

        float lastRightPos = occupiedArea.getBBox().getX();
        for (final IRenderer child : getChildRenderers()) {
            if (FloatingHelper.isRendererFloating(child)) {
                continue;
            }
            float childX = child.getOccupiedArea().getBBox().getX();
            child.move(lastRightPos - childX, 0);
            childX = lastRightPos;
            if (child instanceof TextRenderer) {
                float childHSCale = (float) ((TextRenderer) child).getPropertyAsFloat(Property.HORIZONTAL_SCALING, 1f);
                Float oldCharacterSpacing = ((TextRenderer) child).getPropertyAsFloat(Property.CHARACTER_SPACING);
                Float oldWordSpacing = ((TextRenderer) child).getPropertyAsFloat(Property.WORD_SPACING);
                child.setProperty(Property.CHARACTER_SPACING,
                        (null == oldCharacterSpacing ? 0 : (float) oldCharacterSpacing)
                                + characterSpacing / childHSCale);
                child.setProperty(Property.WORD_SPACING,
                        (null == oldWordSpacing ? 0 : (float) oldWordSpacing) + wordSpacing / childHSCale);
                boolean isLastTextRenderer = child == lastChildRenderer;
                float widthAddition = (isLastTextRenderer ? (((TextRenderer) child).lineLength() - 1)
                        : ((TextRenderer) child).lineLength()) * characterSpacing +
                        wordSpacing * ((TextRenderer) child).getNumberOfSpaces();
                child.getOccupiedArea().getBBox()
                        .setWidth(child.getOccupiedArea().getBBox().getWidth() + widthAddition);
            }
            lastRightPos = childX + child.getOccupiedArea().getBBox().getWidth();
        }

        getOccupiedArea().getBBox().setWidth(width);
    }

    protected int getNumberOfSpaces() {
        int spaces = 0;
        for (final IRenderer child : getChildRenderers()) {
            if (child instanceof TextRenderer && !FloatingHelper.isRendererFloating(child)) {
                spaces += ((TextRenderer) child).getNumberOfSpaces();
            }
        }
        return spaces;
    }

    /**
     * Gets the total lengths of characters in this line. Other elements (images, tables) are not taken
     * into account.
     *
     * @return the total lengths of characters in this line.
     */
    protected int length() {
        int length = 0;
        for (final IRenderer child : getChildRenderers()) {
            if (child instanceof TextRenderer && !FloatingHelper.isRendererFloating(child)) {
                length += ((TextRenderer) child).lineLength();
            }
        }
        return length;
    }

    /**
     * Returns the number of base characters, i.e. non-mark characters
     *
     * @return the number of base non-mark characters
     */
    protected int baseCharactersCount() {
        int count = 0;
        for (final IRenderer child : getChildRenderers()) {
            if (child instanceof TextRenderer && !FloatingHelper.isRendererFloating(child)) {
                count += ((TextRenderer) child).baseCharactersCount();
            }
        }
        return count;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (final IRenderer renderer : getChildRenderers()) {
            sb.append(renderer.toString());
        }
        return sb.toString();
    }

    protected LineRenderer createSplitRenderer() {
        return (LineRenderer) getNextRenderer();
    }

    protected LineRenderer createOverflowRenderer() {
        return (LineRenderer) getNextRenderer();
    }

    protected LineRenderer[] split() {
        LineRenderer splitRenderer = createSplitRenderer();
        splitRenderer.occupiedArea = occupiedArea.clone();
        splitRenderer.parent = parent;
        splitRenderer.maxAscent = maxAscent;
        splitRenderer.maxDescent = maxDescent;
        splitRenderer.maxTextAscent = maxTextAscent;
        splitRenderer.maxTextDescent = maxTextDescent;
        splitRenderer.maxBlockAscent = maxBlockAscent;
        splitRenderer.maxBlockDescent = maxBlockDescent;
        splitRenderer.levels = levels;
        splitRenderer.addAllProperties(getOwnProperties());

        LineRenderer overflowRenderer = createOverflowRenderer();
        overflowRenderer.parent = parent;
        overflowRenderer.addAllProperties(getOwnProperties());

        return new LineRenderer[] {splitRenderer, overflowRenderer};
    }

    protected LineRenderer adjustChildrenYLine() {
        if (RenderingMode.HTML_MODE == this.<RenderingMode>getProperty(Property.RENDERING_MODE) &&
                hasInlineBlocksWithVerticalAlignment()) {
            InlineVerticalAlignmentHelper.adjustChildrenYLineHtmlMode(this);
        } else {
            adjustChildrenYLineDefaultMode();
        }

        return this;
    }

    protected void applyLeading(float deltaY) {
        occupiedArea.getBBox().moveUp(deltaY);
        occupiedArea.getBBox().decreaseHeight(deltaY);
        for (final IRenderer child : getChildRenderers()) {
            if (!FloatingHelper.isRendererFloating(child)) {
                child.move(0, deltaY);
            }
        }
    }

    protected LineRenderer trimLast() {
        int lastIndex = getChildRenderers().size();
        IRenderer lastRenderer = null;
        while (--lastIndex >= 0) {
            lastRenderer = getChildRenderers().get(lastIndex);
            if (!FloatingHelper.isRendererFloating(lastRenderer)) {
                break;
            }
        }
        if (lastRenderer instanceof TextRenderer && lastIndex >= 0) {
            float trimmedSpace = ((TextRenderer) lastRenderer).trimLast();
            occupiedArea.getBBox().setWidth(occupiedArea.getBBox().getWidth() - trimmedSpace);
        }
        return this;
    }

    public boolean containsImage() {
        for (final IRenderer renderer : getChildRenderers()) {
            if (renderer instanceof ImageRenderer) {
                return true;
            }
        }
        return false;
    }

    @Override
    public MinMaxWidth getMinMaxWidth() {
        LineLayoutResult result = (LineLayoutResult) layout(new LayoutContext(
                new LayoutArea(1, new Rectangle(MinMaxWidthUtils.getInfWidth(), AbstractRenderer.INF))));
        return result.getMinMaxWidth();
    }

    boolean hasChildRendererInHtmlMode() {
        for (final IRenderer childRenderer : getChildRenderers()) {
            if (RenderingMode.HTML_MODE.equals(childRenderer.<RenderingMode>getProperty(Property.RENDERING_MODE))) {
                return true;
            }
        }
        return false;
    }

    float getTopLeadingIndent(Leading leading) {
        switch (leading.getType()) {
            case Leading.FIXED:
                return (Math.max(leading.getValue(), maxBlockAscent - maxBlockDescent) -
                        occupiedArea.getBBox().getHeight()) / 2;
            case Leading.MULTIPLIED:
                UnitValue fontSize = this.<UnitValue>getProperty(Property.FONT_SIZE, UnitValue.createPointValue(0f));
                if (!fontSize.isPointValue()) {
                    logger.error(MessageFormatUtil.format(IoLogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED,
                            Property.FONT_SIZE));
                }
                // In HTML, depending on whether <!DOCTYPE html> is present or not, and if present then depending
                // on the version, the behavior is different. In one case, bottom leading indent is added for images,
                // in the other it is not added.
                // This is why !containsImage() is present below. Depending on the presence of
                // this !containsImage() condition, the behavior changes between the two possible scenarios in HTML.
                float textAscent = maxTextAscent == 0 && maxTextDescent == 0 && Math.abs(maxAscent)
                        + Math.abs(maxDescent) != 0 && !containsImage() ? fontSize.getValue() * 0.8f : maxTextAscent;
                float textDescent = maxTextAscent == 0 && maxTextDescent == 0 && Math.abs(maxAscent)
                        + Math.abs(maxDescent) != 0 && !containsImage() ? -fontSize.getValue() * 0.2f : maxTextDescent;
                return Math.max(textAscent + ((textAscent - textDescent) * (leading.getValue() - 1)) / 2,
                        maxBlockAscent) - maxAscent;
            default:
                throw new IllegalStateException();
        }
    }

    float getBottomLeadingIndent(Leading leading) {
        switch (leading.getType()) {
            case Leading.FIXED:
                return (Math.max(leading.getValue(), maxBlockAscent - maxBlockDescent) -
                        occupiedArea.getBBox().getHeight()) / 2;
            case Leading.MULTIPLIED:
                UnitValue fontSize = this.<UnitValue>getProperty(Property.FONT_SIZE, UnitValue.createPointValue(0f));
                if (!fontSize.isPointValue()) {
                    logger.error(MessageFormatUtil.format(IoLogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED,
                            Property.FONT_SIZE));
                }
                // In HTML, depending on whether <!DOCTYPE html> is present or not, and if present then depending
                // on the version, the behavior is different. In one case, bottom leading indent is added for images,
                // in the other it is not added.
                // This is why !containsImage() is present below. Depending on the presence of
                // this !containsImage() condition, the behavior changes between the two possible scenarios in HTML.
                float textAscent = maxTextAscent == 0 && maxTextDescent == 0 && !containsImage() ?
                        fontSize.getValue() * 0.8f : maxTextAscent;
                float textDescent = maxTextAscent == 0 && maxTextDescent == 0 && !containsImage() ?
                        -fontSize.getValue() * 0.2f : maxTextDescent;
                return Math.max(-textDescent + ((textAscent - textDescent) * (leading.getValue() - 1)) / 2,
                        -maxBlockDescent) + maxDescent;
            default:
                throw new IllegalStateException();
        }
    }

    static LineSplitIntoGlyphsData splitLineIntoGlyphs(LineRenderer toSplit) {
        final LineSplitIntoGlyphsData result = new LineSplitIntoGlyphsData();

        boolean newLineFound = false;
        TextRenderer lastTextRenderer = null;
        for (final IRenderer child : toSplit.getChildRenderers()) {
            if (newLineFound) {
                break;
            }
            if (child instanceof TextRenderer) {
                GlyphLine childLine = ((TextRenderer) child).line;
                for (int i = childLine.getStart(); i < childLine.getEnd(); i++) {
                    if (TextUtil.isNewLine(childLine.get(i))) {
                        newLineFound = true;
                        break;
                    }
                    result.addLineGlyph(new RendererGlyph(childLine.get(i), (TextRenderer) child));
                }
                lastTextRenderer = (TextRenderer) child;
            } else {
                result.addInsertAfter(lastTextRenderer, child);
            }
        }
        return result;
    }

    static void reorder(LineRenderer toProcess, LineSplitIntoGlyphsData splitLineIntoGlyphsResult, int[] newOrder) {
        // Insert non-text renderers
        toProcess.setChildRenderers(splitLineIntoGlyphsResult.getStarterNonTextRenderers());

        final List<RendererGlyph> lineGlyphs = splitLineIntoGlyphsResult.getLineGlyphs();
        int initialPos = 0;
        for (int offset = initialPos; offset < lineGlyphs.size(); offset = initialPos) {
            final TextRenderer renderer = lineGlyphs.get(offset).getRenderer();
            final TextRenderer newRenderer = new TextRenderer(renderer).removeReversedRanges();
            toProcess.addChildRenderer(newRenderer);

            // Insert non-text renderers
            toProcess.addAllChildRenderers(splitLineIntoGlyphsResult.getInsertAfterAndRemove(renderer));

            newRenderer.line = new GlyphLine(newRenderer.line);
            List<Glyph> replacementGlyphs = new ArrayList<>();
            boolean reversed = false;
            for (int pos = offset; pos < lineGlyphs.size() && lineGlyphs.get(pos).getRenderer() == renderer; ++pos) {
                replacementGlyphs.add(lineGlyphs.get(pos).getGlyph());
                if (pos + 1 < lineGlyphs.size()
                        && lineGlyphs.get(pos + 1).getRenderer() == renderer
                        && newOrder[pos] == newOrder[pos + 1] + 1
                        && !TextUtil.isSpaceOrWhitespace(lineGlyphs.get(pos + 1).getGlyph())
                        && !TextUtil.isSpaceOrWhitespace(lineGlyphs.get(pos).getGlyph())) {
                    reversed = true;
                    continue;
                }
                if (reversed) {
                    newRenderer.initReversedRanges().add(new int[] {initialPos - offset, pos - offset});
                    reversed = false;
                }
                initialPos = pos + 1;
            }

            newRenderer.line.setGlyphs(replacementGlyphs);
        }
    }

    static void adjustChildPositionsAfterReordering(List<IRenderer> children, float initialXPos) {
        float currentXPos = initialXPos;
        for (IRenderer child : children) {
            if (!FloatingHelper.isRendererFloating(child)) {
                float currentWidth;
                if (child instanceof TextRenderer) {
                    currentWidth = ((TextRenderer) child).calculateLineWidth();
                    UnitValue[] margins = ((TextRenderer) child).getMargins();
                    if (!margins[1].isPointValue() && logger.isErrorEnabled()) {
                        logger.error(MessageFormatUtil.format(IoLogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED,
                                "right margin"));
                    }
                    if (!margins[3].isPointValue() && logger.isErrorEnabled()) {
                        logger.error(MessageFormatUtil.format(IoLogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED,
                                "left margin"));
                    }
                    UnitValue[] paddings = ((TextRenderer) child).getPaddings();
                    if (!paddings[1].isPointValue() && logger.isErrorEnabled()) {
                        logger.error(MessageFormatUtil.format(IoLogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED,
                                "right padding"));
                    }
                    if (!paddings[3].isPointValue() && logger.isErrorEnabled()) {
                        logger.error(MessageFormatUtil.format(IoLogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED,
                                "left padding"));
                    }
                    currentWidth += margins[1].getValue() + margins[3].getValue() +
                            paddings[1].getValue() + paddings[3].getValue();
                    ((TextRenderer) child).occupiedArea.getBBox().setX(currentXPos).setWidth(currentWidth);
                } else {
                    currentWidth = child.getOccupiedArea().getBBox().getWidth();
                    child.move(currentXPos - child.getOccupiedArea().getBBox().getX(), 0);
                }
                currentXPos += currentWidth;
            }
        }
    }

    private LineRenderer[] splitNotFittingFloat(int childPos, LayoutResult childResult) {
        LineRenderer[] split = split();
        split[0].addAllChildRenderers(getChildRenderers().subList(0, childPos));
        split[0].addChildRenderer(childResult.getSplitRenderer());
        split[1].addChildRenderer(childResult.getOverflowRenderer());
        split[1].addAllChildRenderers(getChildRenderers().subList(childPos + 1, getChildRenderers().size()));

        return split;
    }

    private void adjustLineOnFloatPlaced(Rectangle layoutBox, int childPos, FloatPropertyValue kidFloatPropertyVal,
            Rectangle justPlacedFloatBox) {
        if (justPlacedFloatBox.getBottom() >= layoutBox.getTop() || justPlacedFloatBox.getTop() < layoutBox.getTop()) {
            return;
        }

        float floatWidth = justPlacedFloatBox.getWidth();
        if (kidFloatPropertyVal.equals(FloatPropertyValue.LEFT)) {
            layoutBox.setWidth(layoutBox.getWidth() - floatWidth).moveRight(floatWidth);
            occupiedArea.getBBox().moveRight(floatWidth);
            for (int i = 0; i < childPos; ++i) {
                final IRenderer prevChild = getChildRenderers().get(i);
                if (!FloatingHelper.isRendererFloating(prevChild)) {
                    prevChild.move(floatWidth, 0);
                }
            }

        } else {
            layoutBox.setWidth(layoutBox.getWidth() - floatWidth);
        }
    }

    private void replaceSplitRendererKidFloats(Map<Integer, IRenderer> floatsToNextPageSplitRenderers,
            LineRenderer splitRenderer) {
        for (Map.Entry<Integer, IRenderer> splitFloat : floatsToNextPageSplitRenderers.entrySet()) {
            if (splitFloat.getValue() != null) {
                splitRenderer.setChildRenderer(splitFloat.getKey(), splitFloat.getValue());
            } else {
                splitRenderer.setChildRenderer(splitFloat.getKey(), null);
            }
        }
        for (int i = splitRenderer.getChildRenderers().size() - 1; i >= 0; --i) {
            if (splitRenderer.getChildRenderers().get(i) == null) {
                splitRenderer.removeChildRenderer(i);
            }
        }
    }

    private IRenderer getLastNonFloatChildRenderer() {
        IRenderer result = null;
        for (int i = getChildRenderers().size() - 1; i >= 0; --i) {
            IRenderer current = getChildRenderers().get(i);
            if (!FloatingHelper.isRendererFloating(current)) {
                result = current;
                break;
            }
        }
        return result;
    }

    private TabStop getNextTabStop(float curWidth) {
        NavigableMap<Float, TabStop> tabStops = this.<NavigableMap<Float, TabStop>>getProperty(Property.TAB_STOPS);

        Map.Entry<Float, TabStop> nextTabStopEntry = null;
        TabStop nextTabStop = null;

        if (tabStops != null) {
            nextTabStopEntry = tabStops.higherEntry(curWidth);
        }
        if (nextTabStopEntry != null) {
            nextTabStop = ((Map.Entry<Float, TabStop>) nextTabStopEntry).getValue();
        }

        return nextTabStop;
    }

    /**
     * Calculates and sets encountered tab size.
     * Returns null, if processing is finished and layout can be performed for the tab renderer;
     * otherwise, in case when the tab should be processed after the next element in the line,
     * this method returns corresponding tab stop.
     */
    private TabStop calculateTab(IRenderer childRenderer, float curWidth, float lineWidth) {
        TabStop nextTabStop = getNextTabStop(curWidth);

        if (nextTabStop == null) {
            processDefaultTab(childRenderer, curWidth, lineWidth);
            return null;
        }

        childRenderer.setProperty(Property.TAB_LEADER, nextTabStop.getTabLeader());
        childRenderer.setProperty(Property.WIDTH, UnitValue.createPointValue(nextTabStop.getTabPosition() - curWidth));
        childRenderer.setProperty(Property.MIN_HEIGHT, UnitValue.createPointValue(maxAscent - maxDescent));

        if (nextTabStop.getTabAlignment() == TabAlignment.LEFT) {
            return null;
        }

        return nextTabStop;
    }

    /**
     * Calculates and sets tab size with the account of the element that is next in the line after the tab.
     * Returns resulting width of the tab.
     */
    private float calculateTab(Rectangle layoutBox, float curWidth, TabStop tabStop, List<IRenderer> affectedRenderers,
            IRenderer tabRenderer) {
        float sumOfAffectedRendererWidths = 0;
        for (IRenderer renderer : affectedRenderers) {
            sumOfAffectedRendererWidths += renderer.getOccupiedArea().getBBox().getWidth();
        }
        float tabWidth = 0;
        switch (tabStop.getTabAlignment()) {
            case RIGHT:
                tabWidth = tabStop.getTabPosition() - curWidth - sumOfAffectedRendererWidths;
                break;
            case CENTER:
                tabWidth = tabStop.getTabPosition() - curWidth - sumOfAffectedRendererWidths / 2;
                break;
            case ANCHOR:
                float anchorPosition = -1;
                float processedRenderersWidth = 0;
                for (IRenderer renderer : affectedRenderers) {
                    anchorPosition = ((TextRenderer) renderer).getTabAnchorCharacterPosition();
                    if (-1 != anchorPosition) {
                        break;
                    } else {
                        processedRenderersWidth += renderer.getOccupiedArea().getBBox().getWidth();
                    }
                }
                if (anchorPosition == -1) {
                    anchorPosition = 0;
                }
                tabWidth = tabStop.getTabPosition() - curWidth - anchorPosition - processedRenderersWidth;
                break;
        }
        if (tabWidth < 0) {
            tabWidth = 0;
        }
        if (curWidth + tabWidth + sumOfAffectedRendererWidths > layoutBox.getWidth()) {
            tabWidth -= (curWidth + sumOfAffectedRendererWidths + tabWidth) - layoutBox.getWidth();
        }

        tabRenderer.setProperty(Property.WIDTH, UnitValue.createPointValue(tabWidth));
        tabRenderer.setProperty(Property.MIN_HEIGHT, UnitValue.createPointValue(maxAscent - maxDescent));

        return tabWidth;
    }

    private void processDefaultTab(IRenderer tabRenderer, float curWidth, float lineWidth) {
        Float tabDefault = this.getPropertyAsFloat(Property.TAB_DEFAULT);
        Float tabWidth = tabDefault - curWidth % tabDefault;
        if (curWidth + tabWidth > lineWidth) {
            tabWidth = lineWidth - curWidth;
        }
        tabRenderer.setProperty(Property.WIDTH, UnitValue.createPointValue((float) tabWidth));
        tabRenderer.setProperty(Property.MIN_HEIGHT, UnitValue.createPointValue(maxAscent - maxDescent));
    }

    private void updateChildrenParent() {
        for (final IRenderer renderer : getChildRenderers()) {
            renderer.setParent(this);
        }
    }

    /**
     * Trim first child text renderers.
     *
     * @return total number of trimmed glyphs.
     */
    int trimFirst() {
        int totalNumberOfTrimmedGlyphs = 0;
        for (final IRenderer renderer : getChildRenderers()) {
            if (FloatingHelper.isRendererFloating(renderer)) {
                continue;
            }
            boolean trimFinished;
            if (renderer instanceof TextRenderer) {
                TextRenderer textRenderer = (TextRenderer) renderer;
                GlyphLine currentText = textRenderer.getText();
                if (currentText != null) {
                    int prevTextStart = currentText.getStart();
                    textRenderer.trimFirst();
                    int numOfTrimmedGlyphs = textRenderer.getText().getStart() - prevTextStart;
                    totalNumberOfTrimmedGlyphs += numOfTrimmedGlyphs;
                }
                trimFinished = textRenderer.length() > 0;
            } else {
                trimFinished = true;
            }
            if (trimFinished) {
                break;
            }
        }
        return totalNumberOfTrimmedGlyphs;
    }

    /**
     * Apply OTF features and return the last(!) base direction of child renderer
     *
     * @return the last(!) base direction of child renderer.
     */
    private BaseDirection applyOtf() {
        BaseDirection baseDirection = this.<BaseDirection>getProperty(Property.BASE_DIRECTION);
        for (final IRenderer renderer : getChildRenderers()) {
            if (renderer instanceof TextRenderer) {
                ((TextRenderer) renderer).applyOtf();
                if (baseDirection == null || baseDirection == BaseDirection.NO_BIDI) {
                    baseDirection = renderer.<BaseDirection>getOwnProperty(Property.BASE_DIRECTION);
                }
            }
        }
        return baseDirection;
    }

    static boolean isChildFloating(IRenderer childRenderer) {
        FloatPropertyValue kidFloatPropertyVal = childRenderer.<FloatPropertyValue>getProperty(Property.FLOAT);
        return childRenderer instanceof AbstractRenderer
                && FloatingHelper.isRendererFloating(childRenderer, kidFloatPropertyVal);
    }

    static boolean isInlineBlockChild(IRenderer child) {
        return child instanceof BlockRenderer || child instanceof TableRenderer;
    }

    /**
     * Checks if the word that's been split when has been layouted on this line can fit the next line without splitting.
     *
     * @param childRenderer the childRenderer containing the split word
     * @param wasXOverflowChanged true if {@link Property#OVERFLOW_X} has been changed
     *                            during layouting of {@link LineRenderer}
     * @param oldXOverflow the value of {@link Property#OVERFLOW_X} before it's been changed
     *                     during layouting of {@link LineRenderer}
     *                     or null if {@link Property#OVERFLOW_X} hasn't been changed
     * @param layoutContext {@link LayoutContext}
     * @param layoutBox current layoutBox
     * @param wasParentsHeightClipped true if layoutBox's height has been clipped
     * @return true if the split word can fit the next line without splitting
     */
    boolean isForceOverflowForTextRendererPartialResult(IRenderer childRenderer, boolean wasXOverflowChanged,
            OverflowPropertyValue oldXOverflow, LayoutContext layoutContext,
            Rectangle layoutBox, boolean wasParentsHeightClipped) {
        if (wasXOverflowChanged) {
            setProperty(Property.OVERFLOW_X, oldXOverflow);
        }
        LayoutResult newLayoutResult = childRenderer.layout(
                new LayoutContext(new LayoutArea(layoutContext.getArea().getPageNumber(), layoutBox),
                        wasParentsHeightClipped));
        if (wasXOverflowChanged) {
            setProperty(Property.OVERFLOW_X, OverflowPropertyValue.FIT);
        }
        return newLayoutResult instanceof TextLayoutResult
                && !((TextLayoutResult) newLayoutResult).isWordHasBeenSplit();
    }

    /**
     * Extracts ascender and descender of an already layouted {@link IRenderer childRenderer}.
     *
     * @param childRenderer an already layouted child who's ascender and descender are to be extracted
     * @param childResult {@link LayoutResult} of the childRenderer based on which ascender and descender are defined
     * @param childRenderingMode {@link RenderingMode rendering mode}
     * @param isInlineBlockChild true if childRenderer {@link #isInlineBlockChild(IRenderer)}
     * @return a two-element float array where first element is ascender value and second element is descender value
     */
    float[] getAscentDescentOfLayoutedChildRenderer(IRenderer childRenderer, LayoutResult childResult,
            RenderingMode childRenderingMode, boolean isInlineBlockChild) {
        float childAscent = 0;
        float childDescent = 0;
        if (childRenderer instanceof ILeafElementRenderer
                && childResult.getStatus() != LayoutResult.NOTHING) {
            if (RenderingMode.HTML_MODE == childRenderingMode && childRenderer instanceof TextRenderer) {
                return LineHeightHelper.getActualAscenderDescender((TextRenderer) childRenderer);
            } else {
                childAscent = ((ILeafElementRenderer) childRenderer).getAscent();
                childDescent = ((ILeafElementRenderer) childRenderer).getDescent();
            }
        } else if (isInlineBlockChild && childResult.getStatus() != LayoutResult.NOTHING) {
            if (childRenderer instanceof AbstractRenderer) {
                Float yLine = ((AbstractRenderer) childRenderer).getLastYLineRecursively();
                if (yLine == null) {
                    childAscent = childRenderer.getOccupiedArea().getBBox().getHeight();
                } else {
                    childAscent = childRenderer.getOccupiedArea().getBBox().getTop() - (float) yLine;
                    childDescent = -((float) yLine - childRenderer.getOccupiedArea().getBBox().getBottom());
                }
            } else {
                childAscent = childRenderer.getOccupiedArea().getBBox().getHeight();
            }
        }

        return new float[] {childAscent, childDescent};
    }

    /**
     * Updates {@link LineRenderer#maxAscent}, {@link LineRenderer#maxDescent}, {@link LineRenderer#maxTextAscent} and
     * {@link LineRenderer#maxTextDescent} after a {@link TextRenderer} sequence has been fully processed.
     *
     * @param newChildPos                                      position of the last {@link TextRenderer} child of the
     *                                                         sequence to remain on the line
     * @param lineAscentDescentStateBeforeTextRendererSequence a {@link LineAscentDescentState} containing
     *                                                         {@link LineRenderer}'s maxAscent, maxDescent,
     *                                                         maxTextAscent, maxTextDescent before
     *                                                         {@link TextRenderer} sequence start
     * @param textRendererSequenceAscentDescent                a {@link Map} with {@link TextRenderer} children's
     *                                                         positions as keys
     *                                                         and float arrays consisting of maxAscent, maxDescent,
     *                                                         maxTextAscent,
     *                                                         maxTextDescent of the corresponding {@link TextRenderer}
     *                                                         children.
     * @return a two-element float array where first element is a new {@link LineRenderer}'s ascender
     *         and second element is a new {@link LineRenderer}'s descender
     */
    float[] updateAscentDescentAfterTextRendererSequenceProcessing(
            int newChildPos, LineAscentDescentState lineAscentDescentStateBeforeTextRendererSequence,
            Map<Integer, float[]> textRendererSequenceAscentDescent) {
        float maxAscentUpdated = lineAscentDescentStateBeforeTextRendererSequence.maxAscent;
        float maxDescentUpdated = lineAscentDescentStateBeforeTextRendererSequence.maxDescent;
        float maxTextAscentUpdated = lineAscentDescentStateBeforeTextRendererSequence.maxTextAscent;
        float maxTextDescentUpdated = lineAscentDescentStateBeforeTextRendererSequence.maxTextDescent;
        for (Map.Entry<Integer, float[]> childAscentDescent : textRendererSequenceAscentDescent.entrySet()) {
            if (childAscentDescent.getKey() <= newChildPos) {
                maxAscentUpdated = Math.max(maxAscentUpdated, childAscentDescent.getValue()[0]);
                maxDescentUpdated = Math.min(maxDescentUpdated, childAscentDescent.getValue()[1]);
                maxTextAscentUpdated = Math.max(maxTextAscentUpdated, childAscentDescent.getValue()[0]);
                maxTextDescentUpdated = Math.min(maxTextDescentUpdated, childAscentDescent.getValue()[1]);
            }
        }

        this.maxAscent = maxAscentUpdated;
        this.maxDescent = maxDescentUpdated;
        this.maxTextAscent = maxTextAscentUpdated;
        this.maxTextDescent = maxTextDescentUpdated;

        return new float[] {this.maxAscent, this.maxDescent};
    }

    /**
     * Update {@link LineRenderer#maxAscent}, {@link LineRenderer#maxDescent}, {@link LineRenderer#maxTextAscent},
     * {@link LineRenderer#maxTextDescent}, {@link LineRenderer#maxBlockAscent} and {@link LineRenderer#maxBlockDescent}
     * after child's layout.
     *
     * @param childAscentDescent a two-element float array where first element is ascender of a layouted child
     *                           and second element is descender of a layouted child
     * @param childRenderer the layouted {@link IRenderer childRenderer} of current {@link LineRenderer}
     * @param isChildFloating true if {@link #isChildFloating(IRenderer)}
     */
    void updateAscentDescentAfterChildLayout(float[] childAscentDescent, IRenderer childRenderer,
            boolean isChildFloating) {
        float childAscent = childAscentDescent[0];
        float childDescent = childAscentDescent[1];
        this.maxAscent = Math.max(this.maxAscent, childAscent);
        if (childRenderer instanceof TextRenderer) {
            this.maxTextAscent = Math.max(this.maxTextAscent, childAscent);
        } else if (!isChildFloating) {
            this.maxBlockAscent = Math.max(this.maxBlockAscent, childAscent);
        }
        this.maxDescent = Math.min(this.maxDescent, childDescent);
        if (childRenderer instanceof TextRenderer) {
            this.maxTextDescent = Math.min(this.maxTextDescent, childDescent);
        } else if (!isChildFloating) {
            this.maxBlockDescent = Math.min(this.maxBlockDescent, childDescent);
        }
    }

    private void updateBidiLevels(int totalNumberOfTrimmedGlyphs, BaseDirection baseDirection) {
        if (totalNumberOfTrimmedGlyphs != 0 && levels != null) {
            levels = Arrays.copyOfRange(levels, totalNumberOfTrimmedGlyphs, levels.length);
        }

        List<Integer> unicodeIdsReorderingList = null;
        if (levels == null && baseDirection != null && baseDirection != BaseDirection.NO_BIDI) {
            unicodeIdsReorderingList = new ArrayList<>();
            boolean newLineFound = false;
            for (final IRenderer child : getChildRenderers()) {
                if (newLineFound) {
                    break;
                }
                if (child instanceof TextRenderer) {
                    GlyphLine text = ((TextRenderer) child).getText();
                    for (int i = text.getStart(); i < text.getEnd(); i++) {
                        Glyph glyph = text.get(i);
                        if (TextUtil.isNewLine(glyph)) {
                            newLineFound = true;
                            break;
                        }
                        // we assume all the chars will have the same bidi group
                        // we also assume pairing symbols won't get merged with other ones
                        int unicode = glyph.hasValidUnicode() ? glyph.getUnicode() : glyph.getUnicodeChars()[0];
                        unicodeIdsReorderingList.add(unicode);
                    }
                }
            }
            if (unicodeIdsReorderingList.size() > 0) {
                final PdfDocument pdfDocument = getPdfDocument();
                final SequenceId sequenceId = pdfDocument == null ? null : pdfDocument.getDocumentIdWrapper();
                final MetaInfoContainer metaInfoContainer = this.<MetaInfoContainer>getProperty(Property.META_INFO);
                final IMetaInfo metaInfo = metaInfoContainer == null ? null : metaInfoContainer.getMetaInfo();
                levels = TypographyUtils.getBidiLevels(baseDirection, ArrayUtil.toIntArray(unicodeIdsReorderingList),
                        sequenceId, metaInfo);
            } else {
                levels = null;
            }
        }
    }

    /**
     * While resolving TextRenderer may split into several ones with different fonts.
     */
    private void resolveChildrenFonts() {
        final List<IRenderer> newChildRenderers = new ArrayList<>(getChildRenderers().size());
        boolean updateChildRenderers = false;
        for (final IRenderer child : getChildRenderers()) {
            if (child instanceof TextRenderer) {
                if (((TextRenderer) child).resolveFonts(newChildRenderers)) {
                    updateChildRenderers = true;
                }
            } else {
                newChildRenderers.add(child);
            }
        }

        // this mean, that some TextRenderer has been replaced.
        if (updateChildRenderers) {
            setChildRenderers(newChildRenderers);
        }
    }

    private float decreaseRelativeWidthByChildAdditionalWidth(IRenderer childRenderer, float normalizedChildWidth) {
        // Decrease the calculated width by margins, paddings and borders so that
        // even for 100% width the content definitely fits.
        if (childRenderer instanceof AbstractRenderer) {
            Rectangle dummyRect = new Rectangle(normalizedChildWidth, 0);
            ((AbstractRenderer) childRenderer).applyMargins(dummyRect, false);
            if (!isBorderBoxSizing(childRenderer)) {
                ((AbstractRenderer) childRenderer).applyBorderBox(dummyRect, false);
                ((AbstractRenderer) childRenderer).applyPaddings(dummyRect, false);
            }
            normalizedChildWidth = dummyRect.getWidth();
        }
        return normalizedChildWidth;
    }

    private void adjustChildrenYLineDefaultMode() {
        float actualYLine = occupiedArea.getBBox().getY() + occupiedArea.getBBox().getHeight() - maxAscent;

        for (final IRenderer renderer : getChildRenderers()) {
            if (FloatingHelper.isRendererFloating(renderer)) {
                continue;
            }
            if (renderer instanceof ILeafElementRenderer) {
                float descent = ((ILeafElementRenderer) renderer).getDescent();
                renderer.move(0, actualYLine - renderer.getOccupiedArea().getBBox().getBottom() + descent);
            } else {
                Float yLine = isInlineBlockChild(renderer) && renderer instanceof AbstractRenderer ?
                        ((AbstractRenderer) renderer).getLastYLineRecursively() : null;
                renderer.move(0, actualYLine - (yLine == null ?
                        renderer.getOccupiedArea().getBBox().getBottom() : (float) yLine));
            }
        }
    }

    private boolean hasInlineBlocksWithVerticalAlignment() {
        for (IRenderer child : getChildRenderers()) {
            if (child.hasProperty(Property.INLINE_VERTICAL_ALIGNMENT) &&
                    InlineVerticalAlignmentType.BASELINE != ((InlineVerticalAlignment)child.
                            <InlineVerticalAlignment>getProperty(Property.INLINE_VERTICAL_ALIGNMENT)).getType()) {
                return true;
            }
        }
        return false;
    }

    private void adjustChildrenXLine() {
        RenderingMode mode = this.<RenderingMode>getProperty(Property.RENDERING_MODE);
        if (RenderingMode.SVG_MODE != mode) {
            return;
        }
        // LineRenderer first child contains initial x of the occupied area, in order to identify originX we need to
        // also add relative x position of the first text chunk in the svg which is ParagraphRenderer first child.
        float originX = (float) getChildRenderers().get(0).getOccupiedArea().getBBox().getLeft() +
                (float) ((TextRenderer) getParent().getChildRenderers().get(0)).getPropertyAsFloat(Property.LEFT, 0f);
        float[] minMaxX = getMinMaxX();
        float leftmostX = minMaxX[0];
        float xShift = originX - leftmostX;
        float textAnchorCorrection = applyTextAnchor(minMaxX[1] - minMaxX[0]);
        xShift += textAnchorCorrection;

        for (final IRenderer renderer : getChildRenderers()) {
            if (renderer instanceof TextRenderer) {
                renderer.move(xShift, 0);
            }
        }
    }

    private float[] getMinMaxX() {
        float leftmostX = Float.MAX_VALUE;
        float rightmostX = Float.MIN_VALUE;
        for (int i = 0; i < getChildRenderers().size(); i++) {
            IRenderer renderer = getChildRenderers().get(i);
            if (renderer instanceof TextRenderer) {
                final TextRenderer textRenderer = (TextRenderer) renderer;
                float x = textRenderer.getOccupiedArea().getBBox().getX();
                if (textRenderer.isRelativePosition()) {
                    x += (float) textRenderer.getPropertyAsFloat(Property.LEFT, 0f);
                }
                if (x < leftmostX) {
                    leftmostX = x;
                }
                float width = textRenderer.getOccupiedArea().getBBox().getWidth();
                if (x + width > rightmostX) {
                    rightmostX = x + width;
                }
            }
        }
        return new float[]{leftmostX, rightmostX};
    }

    private float applyTextAnchor(float textWidth) {
        TextAnchor textAnchor = (TextAnchor) this.<TextAnchor>getProperty(Property.TEXT_ANCHOR, TextAnchor.START);
        switch (textAnchor) {
            case END:
                return -textWidth;
            case MIDDLE:
                return -textWidth / 2;
            default:
                return 0;
        }
    }

    public static class RendererGlyph {
        private Glyph glyph;
        private TextRenderer renderer;

        public RendererGlyph(Glyph glyph, TextRenderer textRenderer) {
            this.glyph = glyph;
            this.renderer = textRenderer;
        }

        /**
         * Sets the glyph of the object.
         *
         * @param glyph glyph
         */
        public void setGlyph(Glyph glyph) {
            this.glyph = glyph;
        }

        /**
         * Retrieves the glyph of the object.
         *
         * @return glyph
         */
        public Glyph getGlyph() {
            return glyph;
        }

        /**
         * Sets the renderer of the object.
         *
         * @param renderer renderer
         */
        public void setRenderer(TextRenderer renderer) {
            this.renderer = renderer;
        }

        /**
         * Retrieves the renderer of the object.
         *
         * @return renderer
         */
        public TextRenderer getRenderer() {
            return renderer;
        }
    }

    static class LineAscentDescentState {
        float maxAscent;
        float maxDescent;
        float maxTextAscent;
        float maxTextDescent;

        LineAscentDescentState(float maxAscent, float maxDescent, float maxTextAscent, float maxTextDescent) {
            this.maxAscent = maxAscent;
            this.maxDescent = maxDescent;
            this.maxTextAscent = maxTextAscent;
            this.maxTextDescent = maxTextDescent;
        }
    }

    static class LineSplitIntoGlyphsData {
        private final List<RendererGlyph> lineGlyphs;
        private final Map<TextRenderer, List<IRenderer>> insertAfter;
        private final List<IRenderer> starterNonTextRenderers;

        public LineSplitIntoGlyphsData() {
            lineGlyphs = new ArrayList<>();
            insertAfter = new HashMap<>();
            starterNonTextRenderers = new ArrayList<>();
        }

        public List<RendererGlyph> getLineGlyphs() {
            return lineGlyphs;
        }

        public List<IRenderer> getInsertAfterAndRemove(TextRenderer afterRenderer) {
            return insertAfter.remove(afterRenderer);
        }

        public List<IRenderer> getStarterNonTextRenderers() {
            return starterNonTextRenderers;
        }

        public void addLineGlyph(RendererGlyph glyph) {
            lineGlyphs.add(glyph);
        }

        public void addInsertAfter(TextRenderer afterRenderer, IRenderer toInsert) {
            if (afterRenderer == null) {
                // null indicates that there were no previous renderers
                starterNonTextRenderers.add(toInsert);
            } else {
                if (!insertAfter.containsKey(afterRenderer)) {
                    insertAfter.put(afterRenderer, new ArrayList<IRenderer>());
                }
                insertAfter.get(afterRenderer).add(toInsert);
            }
        }
    }
}
