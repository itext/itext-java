/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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

import com.itextpdf.io.font.otf.ActualTextIterator;
import com.itextpdf.io.font.otf.GlyphLine;
import com.itextpdf.io.util.TextUtil;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.layout.TextLayoutResult;
import com.itextpdf.layout.properties.OverflowPropertyValue;
import com.itextpdf.layout.properties.OverflowWrapPropertyValue;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.renderer.LineRenderer.LineAscentDescentState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

final class TextSequenceWordWrapping {
    private static final float OCCUPIED_AREA_RELAYOUT_EPS = 0.0001F;

    private TextSequenceWordWrapping() {
    }

    public static boolean isTextRendererAndRequiresSpecialScriptPreLayoutProcessing(IRenderer childRenderer) {
        return childRenderer instanceof TextRenderer
                && ((TextRenderer) childRenderer).getSpecialScriptsWordBreakPoints() == null
                && ((TextRenderer) childRenderer).textContainsSpecialScriptGlyphs(false)
                && !LineRenderer.isChildFloating(childRenderer);
    }

    /**
     * Preprocess a continuous sequence of TextRenderer containing special scripts
     * prior to layouting the first TextRenderer in the sequence.
     *
     * <p>
     * In this method we preprocess a sequence containing special scripts only,
     * skipping floating renderers as they're not part of a regular layout flow,
     * and breaking the prelayout processing once a non-special script containing renderer occurs.
     * Note! Even though floats are skipped during calculating correct word boundaries,
     * floats themselves are considered as soft-wrap opportunities.
     *
     * <p>
     * Prelayout processing includes the following steps:
     * <ul>
     * <li>{@link #getSpecialScriptsContainingTextRendererSequenceInfo(LineRenderer, int)}: determine boundaries of the
     * sequence
     * and concatenate its TextRenderer#text fields converted to a String representation;
     * <li>get the String analyzed with WordWrapper#getPossibleBreaks and
     * receive a zero-based array of points where the String is allowed to got broken in lines;
     * <li>{@link #distributePossibleBreakPointsOverSequentialTextRenderers(LineRenderer, int, int, List, List)}:
     * distribute the list over the TextRenderer#specialScriptsWordBreakPoints, preliminarily having the points
     * shifted,
     * so that each TextRenderer#specialScriptsWordBreakPoints is based on the first element of TextRenderer#text.
     * </ul>
     *
     * @param lineRenderer line renderer containing text sequence to process
     * @param childPos     index of the childRenderer in LineRenderer#childRenderers
     *                     from which the a continuous sequence of TextRenderer containing special scripts starts
     */
    public static void processSpecialScriptPreLayout(LineRenderer lineRenderer, int childPos) {
        SpecialScriptsContainingTextRendererSequenceInfo info =
                getSpecialScriptsContainingTextRendererSequenceInfo(lineRenderer, childPos);
        int numberOfSequentialTextRenderers = info.numberOfSequentialTextRenderers;
        String sequentialTextContent = info.sequentialTextContent;
        List<Integer> indicesOfFloating = info.indicesOfFloating;
        List<Integer> possibleBreakPointsGlobal = TypographyUtils.getPossibleBreaks(sequentialTextContent);

        distributePossibleBreakPointsOverSequentialTextRenderers(lineRenderer, childPos,
                numberOfSequentialTextRenderers,
                possibleBreakPointsGlobal, indicesOfFloating);
    }

    public static void updateTextSequenceLayoutResults(Map<Integer, LayoutResult> textRendererLayoutResults,
            boolean specialScripts,
            IRenderer childRenderer, int childPos,
            LayoutResult childResult) {

        if (childRenderer instanceof TextRenderer
                && ((TextRenderer) childRenderer).textContainsSpecialScriptGlyphs(true) == specialScripts) {
            textRendererLayoutResults.put(childPos, childResult);
        }
    }

    public static void resetTextSequenceIfItEnded(Map<Integer, LayoutResult> textRendererLayoutResults,
            boolean specialScripts,
            IRenderer childRenderer, int childPos,
            MinMaxWidthOfTextRendererSequenceHelper minMaxWidthOfTextRendererSequenceHelper,
            boolean noSoftWrap, AbstractWidthHandler widthHandler) {

        if (childRenderer instanceof TextRenderer
                && ((TextRenderer) childRenderer).textContainsSpecialScriptGlyphs(true) == specialScripts
                && !LineRenderer.isChildFloating(childRenderer)) {
            return;
        }
        if (!textRendererLayoutResults.isEmpty()) {
            int lastChildInTextSequence = childPos;
            while (lastChildInTextSequence >= 0) {
                if (textRendererLayoutResults.get(lastChildInTextSequence) != null) {
                    break;
                } else {
                    lastChildInTextSequence--;
                }
            }
            LayoutResult childResult = textRendererLayoutResults.get(lastChildInTextSequence);
            updateMinMaxWidthOfLineRendererAfterTextRendererSequenceProcessing(noSoftWrap, lastChildInTextSequence,
                    childResult, widthHandler, minMaxWidthOfTextRendererSequenceHelper, textRendererLayoutResults);
            textRendererLayoutResults.clear();
        }
    }

    public static LineAscentDescentState updateTextRendererSequenceAscentDescent(LineRenderer lineRenderer,
            Map<Integer, float[]> textRendererSequenceAscentDescent,
            int childPos, float[] childAscentDescent,
            LineAscentDescentState preTextSequenceAscentDescent) {

        IRenderer childRenderer = lineRenderer.childRenderers.get(childPos);
        if (childRenderer instanceof TextRenderer
                && !((TextRenderer) childRenderer).textContainsSpecialScriptGlyphs(true)) {
            if (textRendererSequenceAscentDescent.isEmpty()) {
                preTextSequenceAscentDescent = new LineAscentDescentState(lineRenderer.maxAscent,
                        lineRenderer.maxDescent,
                        lineRenderer.maxTextAscent, lineRenderer.maxTextDescent);
            }
            textRendererSequenceAscentDescent.put(childPos, childAscentDescent);
        } else if (!textRendererSequenceAscentDescent.isEmpty()) {
            textRendererSequenceAscentDescent.clear();
            preTextSequenceAscentDescent = null;
        }
        return preTextSequenceAscentDescent;
    }

    public static MinMaxWidthOfTextRendererSequenceHelper updateTextRendererSequenceMinMaxWidth(
            LineRenderer lineRenderer,
            AbstractWidthHandler widthHandler, int childPos,
            MinMaxWidthOfTextRendererSequenceHelper minMaxWidthOfTextRendererSequenceHelper, boolean anythingPlaced,
            Map<Integer, LayoutResult> textRendererLayoutResults,
            Map<Integer, LayoutResult> specialScriptLayoutResults, float textIndent) {

        IRenderer childRenderer = lineRenderer.childRenderers.get(childPos);
        if (childRenderer instanceof TextRenderer) {
            boolean firstTextRendererWithSpecialScripts =
                    ((TextRenderer) childRenderer).textContainsSpecialScriptGlyphs(true)
                            && specialScriptLayoutResults.size() == 1;
            boolean firstTextRendererWithoutSpecialScripts =
                    !((TextRenderer) childRenderer).textContainsSpecialScriptGlyphs(true)
                            && textRendererLayoutResults.size() == 1;
            if (firstTextRendererWithoutSpecialScripts || firstTextRendererWithSpecialScripts) {
                minMaxWidthOfTextRendererSequenceHelper = new MinMaxWidthOfTextRendererSequenceHelper(
                        widthHandler.minMaxWidth.getChildrenMinWidth(), textIndent, anythingPlaced);
            }
            return minMaxWidthOfTextRendererSequenceHelper;
        } else {
            return null;
        }
    }

    public static LastFittingChildRendererData getIndexAndLayoutResultOfTheLastTextRendererWithNoSpecialScripts(
            LineRenderer lineRenderer,
            int childPos, Map<Integer, LayoutResult> textSequenceLayoutResults, boolean wasParentsHeightClipped,
            boolean isOverflowFit, boolean floatsPlaced) {

        LayoutResult lastAnalyzedTextLayoutResult = textSequenceLayoutResults.get(childPos);
        if (lastAnalyzedTextLayoutResult.getStatus() == LayoutResult.PARTIAL
                && !((TextLayoutResult) lastAnalyzedTextLayoutResult).isWordHasBeenSplit()) {
            // line break has already happened based on ISplitCharacters
            return new LastFittingChildRendererData(childPos, textSequenceLayoutResults.get(childPos));
        }

        lastAnalyzedTextLayoutResult = null;
        int lastAnalyzedTextRenderer = childPos;

        for (int i = childPos; i >= 0; i--) {
            if (lineRenderer.childRenderers.get(i) instanceof TextRenderer && !LineRenderer.isChildFloating(
                    lineRenderer.childRenderers.get(i))) {
                TextRenderer textRenderer = (TextRenderer) lineRenderer.childRenderers.get(i);
                if (!textRenderer.textContainsSpecialScriptGlyphs(true)) {
                    TextLayoutResult textLayoutResult = (TextLayoutResult) textSequenceLayoutResults.get(i);
                    TextLayoutResult previousTextLayoutResult =
                            (TextLayoutResult) textSequenceLayoutResults.get(lastAnalyzedTextRenderer);
                    if (i != lastAnalyzedTextRenderer && (textLayoutResult.getStatus() == LayoutResult.FULL
                            && (previousTextLayoutResult.isStartsWithSplitCharacterWhiteSpace()
                            || textLayoutResult.isEndsWithSplitCharacter()))) {
                        lastAnalyzedTextLayoutResult = previousTextLayoutResult.getStatus() == LayoutResult.NOTHING
                                ? previousTextLayoutResult
                                : new TextLayoutResult(LayoutResult.NOTHING, null, null,
                                        lineRenderer.childRenderers.get(lastAnalyzedTextRenderer));
                        break;
                    }
                    if (textLayoutResult.isContainsPossibleBreak()
                            && textLayoutResult.getStatus() != LayoutResult.NOTHING) {
                        textRenderer.setIndexOfFirstCharacterToBeForcedToOverflow(textRenderer.line.getEnd());
                        LayoutArea layoutArea = textRenderer.getOccupiedArea().clone();
                        layoutArea.getBBox()
                                .increaseHeight(OCCUPIED_AREA_RELAYOUT_EPS)
                                .increaseWidth(OCCUPIED_AREA_RELAYOUT_EPS);

                        // Here we relayout the child with the possible break using its own occupied area as
                        // available layout box. It's expected to always work, because since during relayout
                        // we try to achieve partial result of the original layout, the resultant occupied area
                        // will be smaller. More right approach would be to reuse the same layout box which was
                        // used for the original layouting, however it seems to be an overkill to preserve them all.
                        LayoutResult newChildLayoutResult = textRenderer
                                .layout(new LayoutContext(layoutArea, wasParentsHeightClipped));
                        textRenderer.setIndexOfFirstCharacterToBeForcedToOverflow(TextRenderer.UNDEFINED_FIRST_CHAR_TO_FORCE_OVERFLOW);
                        if (newChildLayoutResult.getStatus() == LayoutResult.FULL) {
                            lastAnalyzedTextLayoutResult = new TextLayoutResult(LayoutResult.NOTHING, null,
                                    null, lineRenderer.childRenderers.get(lastAnalyzedTextRenderer));
                        } else {
                            lastAnalyzedTextLayoutResult = newChildLayoutResult;
                            lastAnalyzedTextRenderer = i;
                        }
                        break;
                    }
                    lastAnalyzedTextRenderer = i;
                } else {
                    lastAnalyzedTextLayoutResult = new TextLayoutResult(LayoutResult.NOTHING, null, null,
                            lineRenderer.childRenderers.get(lastAnalyzedTextRenderer));
                    break;
                }

            } else if (LineRenderer.isChildFloating(lineRenderer.childRenderers.get(i))
                    || lineRenderer.childRenderers.get(i) instanceof ImageRenderer
                    || LineRenderer.isInlineBlockChild(lineRenderer.childRenderers.get(i))) {
                lastAnalyzedTextLayoutResult = new TextLayoutResult(LayoutResult.NOTHING, null, null,
                        lineRenderer.childRenderers.get(lastAnalyzedTextRenderer));
                break;
            } else {
                break;
            }
        }

        if (lastAnalyzedTextLayoutResult == null) {
            OverflowWrapPropertyValue overflowWrapValue =
                    lineRenderer.childRenderers.get(childPos).<OverflowWrapPropertyValue>getProperty(
                            Property.OVERFLOW_WRAP);
            boolean overflowWrapNotNormal = overflowWrapValue == OverflowWrapPropertyValue.ANYWHERE
                    || overflowWrapValue == OverflowWrapPropertyValue.BREAK_WORD;
            if (overflowWrapNotNormal
                    && textSequenceLayoutResults.get(lastAnalyzedTextRenderer).getStatus() != LayoutResult.NOTHING
                    || isOverflowFit) {
                lastAnalyzedTextRenderer = childPos;
                lastAnalyzedTextLayoutResult = textSequenceLayoutResults.get(lastAnalyzedTextRenderer);
            } else if (floatsPlaced) {
                lastAnalyzedTextLayoutResult = new TextLayoutResult(LayoutResult.NOTHING, null, null,
                        lineRenderer.childRenderers.get(lastAnalyzedTextRenderer));
            } else {
                return null;
            }
        }

        if (lastAnalyzedTextLayoutResult != null) {
            return new LastFittingChildRendererData(lastAnalyzedTextRenderer,
                    lastAnalyzedTextLayoutResult);
        } else {
            return null;
        }
    }

    public static LastFittingChildRendererData getIndexAndLayoutResultOfTheLastTextRendererContainingSpecialScripts
            (LineRenderer lineRenderer, int childPos, Map<Integer, LayoutResult> specialScriptLayoutResults,
                    boolean wasParentsHeightClipped, boolean isOverflowFit) {
        int indexOfRendererContainingLastFullyFittingWord = childPos;
        int splitPosition = 0;
        boolean needToSplitRendererContainingLastFullyFittingWord = false;
        int fittingLengthWithTrailingRightSideSpaces = 0;
        int amountOfTrailingRightSideSpaces = 0;
        LayoutResult childPosLayoutResult = specialScriptLayoutResults.get(childPos);
        LayoutResult returnLayoutResult = null;
        for (int analyzedTextRendererIndex = childPos; analyzedTextRendererIndex >= 0; analyzedTextRendererIndex--) {
            // get the number of fitting glyphs in the renderer being analyzed
            TextRenderer textRenderer = (TextRenderer) lineRenderer.childRenderers.get(analyzedTextRendererIndex);
            if (analyzedTextRendererIndex != childPos) {
                fittingLengthWithTrailingRightSideSpaces = textRenderer.length();
            } else if (childPosLayoutResult.getSplitRenderer() != null) {
                TextRenderer splitTextRenderer = (TextRenderer) childPosLayoutResult.getSplitRenderer();
                GlyphLine splitText = splitTextRenderer.text;
                if (splitTextRenderer.length() > 0) {
                    fittingLengthWithTrailingRightSideSpaces = splitTextRenderer.length();
                    while (splitText.getEnd() + amountOfTrailingRightSideSpaces < splitText.size()
                            && TextUtil.isWhitespace(splitText.get(splitText.getEnd()
                            + amountOfTrailingRightSideSpaces))) {
                        fittingLengthWithTrailingRightSideSpaces++;
                        amountOfTrailingRightSideSpaces++;
                    }
                }
            }

            // check if line break can happen in this renderer relying on its specialScriptsWordBreakPoints list
            if (fittingLengthWithTrailingRightSideSpaces > 0) {
                List<Integer> breakPoints = textRenderer.getSpecialScriptsWordBreakPoints();
                if (breakPoints != null && breakPoints.size() > 0 && breakPoints.get(0) != -1) {
                    int possibleBreakPointPosition = TextRenderer.findPossibleBreaksSplitPosition(
                            textRenderer.getSpecialScriptsWordBreakPoints(),
                            fittingLengthWithTrailingRightSideSpaces + textRenderer.text.getStart(), false);
                    if (possibleBreakPointPosition > -1) {
                        splitPosition = breakPoints.get(possibleBreakPointPosition) - amountOfTrailingRightSideSpaces;
                        needToSplitRendererContainingLastFullyFittingWord = splitPosition != textRenderer.text.getEnd();
                        if (!needToSplitRendererContainingLastFullyFittingWord) {
                            analyzedTextRendererIndex++;
                        }
                        indexOfRendererContainingLastFullyFittingWord = analyzedTextRendererIndex;
                        break;
                    }
                }
            }

            SpecialScriptsContainingSequenceStatus status =
                    getSpecialScriptsContainingSequenceStatus(lineRenderer, analyzedTextRendererIndex);

            // possible breaks haven't been found, can't move back:
            // forced split on the latter renderer having either Full or Partial result
            // if either OVERFLOW_X is FIT or OVERFLOW_WRAP is either ANYWHERE or BREAK_WORD,
            // otherwise return null as a flag to move forward across this.childRenderers
            // till the end of the unbreakable word
            if (status == SpecialScriptsContainingSequenceStatus.FORCED_SPLIT) {
                OverflowWrapPropertyValue overflowWrapValue =
                        lineRenderer.childRenderers.get(childPos).<OverflowWrapPropertyValue>getProperty(
                                Property.OVERFLOW_WRAP);
                boolean overflowWrapNotNormal = overflowWrapValue == OverflowWrapPropertyValue.ANYWHERE
                        || overflowWrapValue == OverflowWrapPropertyValue.BREAK_WORD;
                if (overflowWrapNotNormal && childPosLayoutResult.getStatus() != LayoutResult.NOTHING
                        || isOverflowFit) {
                    if (childPosLayoutResult.getStatus() != LayoutResult.NOTHING) {
                        returnLayoutResult = childPosLayoutResult;
                    }
                    indexOfRendererContainingLastFullyFittingWord = childPos;
                    break;
                } else {
                    return null;
                }
            }

            // possible breaks haven't been found, can't move back
            // move the entire renderer on the next line
            if (status ==
                    SpecialScriptsContainingSequenceStatus.MOVE_SEQUENCE_CONTAINING_SPECIAL_SCRIPTS_ON_NEXT_LINE) {
                indexOfRendererContainingLastFullyFittingWord = analyzedTextRendererIndex;
                break;
            }
        }

        if (returnLayoutResult == null) {
            returnLayoutResult = childPosLayoutResult;

            TextRenderer childRenderer = (TextRenderer) lineRenderer.childRenderers
                    .get(indexOfRendererContainingLastFullyFittingWord);

            if (needToSplitRendererContainingLastFullyFittingWord) {
                int amountOfFitOnTheFirstLayout =
                        fittingLengthWithTrailingRightSideSpaces - amountOfTrailingRightSideSpaces
                                + childRenderer.text.getStart();
                if (amountOfFitOnTheFirstLayout != splitPosition) {
                    LayoutArea layoutArea = childRenderer.getOccupiedArea().clone();
                    layoutArea.getBBox()
                            .increaseHeight(OCCUPIED_AREA_RELAYOUT_EPS)
                            .increaseWidth(OCCUPIED_AREA_RELAYOUT_EPS);
                    childRenderer.setSpecialScriptFirstNotFittingIndex(splitPosition);

                    // Here we relayout the child with the possible break using its own occupied area as
                    // available layout box. It's expected to always work, because since during relayout
                    // we try to achieve partial result of the original layout, the resultant occupied area
                    // will be smaller. More right approach would be to reuse the same layout box which was
                    // used for the original layouting, however it seems to be an overkill to preserve them all.
                    returnLayoutResult = childRenderer.layout(new LayoutContext(layoutArea, wasParentsHeightClipped));
                    childRenderer.setSpecialScriptFirstNotFittingIndex(-1);
                }
            } else {
                returnLayoutResult = new TextLayoutResult(LayoutResult.NOTHING, null, null, childRenderer);
            }
        }

        return new LastFittingChildRendererData(indexOfRendererContainingLastFullyFittingWord,
                returnLayoutResult);
    }

    /**
     * Performs some settings on {@link LineRenderer} and its child prior to layouting the child
     * to be overflowed beyond the available area.
     *
     * @param lineRenderer                    line renderer containing text sequence to process
     * @param textSequenceOverflowXProcessing true if it is {@link TextRenderer} sequence processing in overflowX mode
     * @param childRenderer                   the {@link LineRenderer}'s child to be preprocessed
     * @param wasXOverflowChanged             true if value of {@link Property#OVERFLOW_X} has been changed during
     *                                        layouting
     * @param oldXOverflow                    the value of {@link Property#OVERFLOW_X} before it's been changed
     *                                        during layouting of {@link LineRenderer}
     *                                        or null if {@link Property#OVERFLOW_X} hasn't been changed
     */
    public static void preprocessTextSequenceOverflowX(LineRenderer lineRenderer,
            boolean textSequenceOverflowXProcessing, IRenderer childRenderer,
            boolean wasXOverflowChanged, OverflowPropertyValue oldXOverflow) {

        boolean specialScripts = childRenderer instanceof TextRenderer && ((TextRenderer) childRenderer)
                .textContainsSpecialScriptGlyphs(true);
        if (textSequenceOverflowXProcessing && specialScripts) {
            int firstPossibleBreakWithinTheRenderer =
                    ((TextRenderer) childRenderer).getSpecialScriptsWordBreakPoints().get(0);
            if (firstPossibleBreakWithinTheRenderer != -1) {
                ((TextRenderer) childRenderer)
                        .setSpecialScriptFirstNotFittingIndex(firstPossibleBreakWithinTheRenderer);
            }
            if (wasXOverflowChanged) {
                lineRenderer.setProperty(Property.OVERFLOW_X, oldXOverflow);
            }
        }

        if (textSequenceOverflowXProcessing && !specialScripts && wasXOverflowChanged) {
            lineRenderer.setProperty(Property.OVERFLOW_X, oldXOverflow);
        }
    }

    /**
     * Checks if the layouting should be stopped on current child and resets configurations set on
     * {@link #preprocessTextSequenceOverflowX(LineRenderer, boolean, IRenderer, boolean, OverflowPropertyValue)}.
     *
     * @param lineRenderer                    line renderer containing text sequence to process
     * @param textSequenceOverflowXProcessing true if it is {@link TextRenderer} sequence processing in overflowX mode
     * @param childRenderer                   the {@link LineRenderer}'s child to be preprocessed
     * @param wasXOverflowChanged             true if value of {@link Property#OVERFLOW_X} has been changed during
     *                                        layouting
     */
    public static boolean postprocessTextSequenceOverflowX(LineRenderer lineRenderer,
            boolean textSequenceOverflowXProcessing, int childPos,
            IRenderer childRenderer, LayoutResult childResult, boolean wasXOverflowChanged) {

        boolean specialScripts = childRenderer instanceof TextRenderer && ((TextRenderer) childRenderer)
                .textContainsSpecialScriptGlyphs(true);
        boolean shouldBreakLayouting = false;
        boolean lastElemOfTextSequence = childPos + 1 == lineRenderer.childRenderers.size()
                || LineRenderer.isChildFloating(lineRenderer.childRenderers.get(childPos + 1))
                || !(lineRenderer.childRenderers.get(childPos + 1) instanceof TextRenderer);

        if (textSequenceOverflowXProcessing && specialScripts) {
            if (((TextRenderer) childRenderer).getSpecialScriptFirstNotFittingIndex() > 0
                    || lastElemOfTextSequence) {
                shouldBreakLayouting = true;
            }
            ((TextRenderer) childRenderer).setSpecialScriptFirstNotFittingIndex(-1);
            if (wasXOverflowChanged) {
                lineRenderer.setProperty(Property.OVERFLOW_X, OverflowPropertyValue.FIT);
            }
        }

        if (textSequenceOverflowXProcessing && !specialScripts) {
            if ((childResult instanceof TextLayoutResult && ((TextLayoutResult) childResult).isContainsPossibleBreak())
                    || lastElemOfTextSequence) {
                shouldBreakLayouting = true;
            }
            if (wasXOverflowChanged) {
                lineRenderer.setProperty(Property.OVERFLOW_X, OverflowPropertyValue.FIT);
            }
        }

        return shouldBreakLayouting;
    }

    static SpecialScriptsContainingTextRendererSequenceInfo getSpecialScriptsContainingTextRendererSequenceInfo(
            LineRenderer lineRenderer, int childPos) {
        StringBuilder sequentialTextContentBuilder = new StringBuilder();
        int numberOfSequentialTextRenderers = 0;
        List<Integer> indicesOfFloating = new ArrayList<>();
        for (int i = childPos; i < lineRenderer.childRenderers.size(); i++) {
            if (LineRenderer.isChildFloating(lineRenderer.childRenderers.get(i))) {
                numberOfSequentialTextRenderers++;
                indicesOfFloating.add(i);
            } else {
                if (lineRenderer.childRenderers.get(i) instanceof TextRenderer
                        && ((TextRenderer) lineRenderer.childRenderers.get(i))
                        .textContainsSpecialScriptGlyphs(false)) {
                    sequentialTextContentBuilder
                            .append(((TextRenderer) lineRenderer.childRenderers.get(i)).text.toString());
                    numberOfSequentialTextRenderers++;
                } else {
                    break;
                }
            }
        }
        return new SpecialScriptsContainingTextRendererSequenceInfo(numberOfSequentialTextRenderers,
                sequentialTextContentBuilder.toString(), indicesOfFloating);
    }

    static void distributePossibleBreakPointsOverSequentialTextRenderers(LineRenderer lineRenderer,
            int childPos, int numberOfSequentialTextRenderers, List<Integer> possibleBreakPointsGlobal,
            List<Integer> indicesOfFloating) {
        int alreadyProcessedNumberOfCharsWithinGlyphLines = 0;
        int indexToBeginWith = 0;
        for (int i = 0; i < numberOfSequentialTextRenderers; i++) {
            if (!indicesOfFloating.contains(i)) {
                TextRenderer childTextRenderer = (TextRenderer) lineRenderer.childRenderers.get(childPos + i);
                List<Integer> amountOfCharsBetweenTextStartAndActualTextChunk = new ArrayList<>();
                List<Integer> glyphLineBasedIndicesOfActualTextChunkEnds = new ArrayList<>();

                fillActualTextChunkRelatedLists(childTextRenderer.getText(),
                        amountOfCharsBetweenTextStartAndActualTextChunk, glyphLineBasedIndicesOfActualTextChunkEnds);

                List<Integer> possibleBreakPoints = new ArrayList<Integer>();
                for (int j = indexToBeginWith; j < possibleBreakPointsGlobal.size(); j++) {
                    int shiftedBreakPoint = possibleBreakPointsGlobal.get(j)
                            - alreadyProcessedNumberOfCharsWithinGlyphLines;
                    int amountOfCharsBetweenTextStartAndTextEnd = amountOfCharsBetweenTextStartAndActualTextChunk
                            .get(amountOfCharsBetweenTextStartAndActualTextChunk.size() - 1);
                    if (shiftedBreakPoint > amountOfCharsBetweenTextStartAndTextEnd) {
                        indexToBeginWith = j;
                        alreadyProcessedNumberOfCharsWithinGlyphLines += amountOfCharsBetweenTextStartAndTextEnd;
                        break;
                    }
                    possibleBreakPoints.add(shiftedBreakPoint);
                }

                List<Integer> glyphLineBasedPossibleBreakPoints = convertPossibleBreakPointsToGlyphLineBased(
                        possibleBreakPoints, amountOfCharsBetweenTextStartAndActualTextChunk,
                        glyphLineBasedIndicesOfActualTextChunkEnds);
                childTextRenderer.setSpecialScriptsWordBreakPoints(glyphLineBasedPossibleBreakPoints);
            }
        }
    }

    /**
     * This method defines how to proceed with a {@link TextRenderer} within which possible breaks haven't been found.
     * Possible scenarios are:
     * - Preceding renderer is also an instance of {@link TextRenderer} and does contain special scripts:
     * {@link #getIndexAndLayoutResultOfTheLastTextRendererContainingSpecialScripts(LineRenderer, int, Map, boolean,
     * boolean)}
     * will proceed to analyze the preceding {@link TextRenderer} on the subject of possible breaks;
     * - Preceding renderer is either an instance of {@link TextRenderer} which does not contain special scripts,
     * or an instance of {@link ImageRenderer} or is an inlineBlock child: in this case the entire subsequence of
     * {@link TextRenderer}-s containing special scripts is to be moved to the next line;
     * - Otherwise a forced split is to happen.
     *
     * @param lineRenderer              line renderer containing text sequence to process
     * @param analyzedTextRendererIndex index of the latter child
     *                                  that has been analyzed on the subject of possible breaks
     * @return {@link SpecialScriptsContainingSequenceStatus} instance standing for the strategy to proceed with.
     */
    static SpecialScriptsContainingSequenceStatus getSpecialScriptsContainingSequenceStatus(LineRenderer lineRenderer,
            int analyzedTextRendererIndex) {
        boolean moveSequenceContainingSpecialScriptsOnNextLine = false;
        boolean moveToPreviousTextRendererContainingSpecialScripts = false;

        if (analyzedTextRendererIndex > 0) {
            IRenderer prevChildRenderer = lineRenderer.childRenderers.get(analyzedTextRendererIndex - 1);
            if (prevChildRenderer instanceof TextRenderer && !LineRenderer.isChildFloating(prevChildRenderer)) {
                if (((TextRenderer) prevChildRenderer).textContainsSpecialScriptGlyphs(true)) {
                    moveToPreviousTextRendererContainingSpecialScripts = true;
                } else {
                    moveSequenceContainingSpecialScriptsOnNextLine = true;
                }
            } else if (LineRenderer.isChildFloating(prevChildRenderer)
                    || prevChildRenderer instanceof ImageRenderer
                    || LineRenderer.isInlineBlockChild(prevChildRenderer)) {
                moveSequenceContainingSpecialScriptsOnNextLine = true;
            }
        }

        boolean forcedSplit = !moveToPreviousTextRendererContainingSpecialScripts
                && !moveSequenceContainingSpecialScriptsOnNextLine;

        if (moveSequenceContainingSpecialScriptsOnNextLine) {
            return SpecialScriptsContainingSequenceStatus.MOVE_SEQUENCE_CONTAINING_SPECIAL_SCRIPTS_ON_NEXT_LINE;
        } else if (forcedSplit) {
            return SpecialScriptsContainingSequenceStatus.FORCED_SPLIT;
        } else {
            return SpecialScriptsContainingSequenceStatus.MOVE_TO_PREVIOUS_TEXT_RENDERER_CONTAINING_SPECIAL_SCRIPTS;
        }
    }

    static float getCurWidthRelayoutedTextSequenceDecrement(int childPos, int newChildPos,
            Map<Integer, LayoutResult> textRendererLayoutResults) {
        float decrement = 0.0f;
        // if childPos == newChildPos, curWidth doesn't include width of the current childRenderer yet,
        // so no decrement is needed
        if (childPos != newChildPos) {
            for (int i = childPos - 1; i >= newChildPos; i--) {
                if (textRendererLayoutResults.get(i) != null) {
                    decrement += textRendererLayoutResults.get(i).getOccupiedArea().getBBox().getWidth();
                }
            }
        }

        return decrement;
    }

    private static void updateMinMaxWidthOfLineRendererAfterTextRendererSequenceProcessing(
            boolean noSoftWrap, int childPos, LayoutResult layoutResult, AbstractWidthHandler widthHandler,
            MinMaxWidthOfTextRendererSequenceHelper minMaxWidthOfTextRendererSequenceHelper,
            Map<Integer, LayoutResult> textRendererLayoutResults) {
        if (noSoftWrap) {
            return;
        }
        TextLayoutResult currLayoutResult = (TextLayoutResult) layoutResult;
        float leftMinWidthCurrRenderer = currLayoutResult.getLeftMinWidth();
        float generalMinWidthCurrRenderer = currLayoutResult.getMinMaxWidth().getMinWidth();
        float widthOfUnbreakableChunkSplitAcrossRenderers = leftMinWidthCurrRenderer;
        float minWidthOfTextRendererSequence = generalMinWidthCurrRenderer;

        for (int prevRendererIndex = childPos - 1; prevRendererIndex >= 0; prevRendererIndex--) {
            if (textRendererLayoutResults.get(prevRendererIndex) != null) {
                TextLayoutResult prevLayoutResult = (TextLayoutResult) textRendererLayoutResults.get(prevRendererIndex);
                float leftMinWidthPrevRenderer = prevLayoutResult.getLeftMinWidth();
                float generalMinWidthPrevRenderer = prevLayoutResult.getMinMaxWidth().getMinWidth();
                float rightMinWidthPrevRenderer = prevLayoutResult.getRightMinWidth();
                minWidthOfTextRendererSequence = Math.max(minWidthOfTextRendererSequence, generalMinWidthPrevRenderer);

                if (!prevLayoutResult.isEndsWithSplitCharacter()
                        && !currLayoutResult.isStartsWithSplitCharacterWhiteSpace()) {
                    if (rightMinWidthPrevRenderer > -1f) {
                        widthOfUnbreakableChunkSplitAcrossRenderers += rightMinWidthPrevRenderer;
                    } else {
                        widthOfUnbreakableChunkSplitAcrossRenderers += leftMinWidthPrevRenderer;
                    }
                    minWidthOfTextRendererSequence = Math.max(minWidthOfTextRendererSequence,
                            widthOfUnbreakableChunkSplitAcrossRenderers);
                    if (rightMinWidthPrevRenderer > -1f) {
                        widthOfUnbreakableChunkSplitAcrossRenderers = leftMinWidthPrevRenderer;
                    }
                } else {
                    widthOfUnbreakableChunkSplitAcrossRenderers = leftMinWidthPrevRenderer;
                }
                currLayoutResult = prevLayoutResult;
            }
        }

        if (!minMaxWidthOfTextRendererSequenceHelper.anythingPlacedBeforeTextRendererSequence) {
            widthOfUnbreakableChunkSplitAcrossRenderers += minMaxWidthOfTextRendererSequenceHelper.textIndent;
            minWidthOfTextRendererSequence = Math.max(minWidthOfTextRendererSequence,
                    widthOfUnbreakableChunkSplitAcrossRenderers);
        }

        float lineMinWidth = Math.max(minWidthOfTextRendererSequence,
                minMaxWidthOfTextRendererSequenceHelper.minWidthPreSequence);
        widthHandler.minMaxWidth.setChildrenMinWidth(lineMinWidth);
    }

    private static List<Integer> convertPossibleBreakPointsToGlyphLineBased(
            List<Integer> possibleBreakPoints, List<Integer> amountOfChars, List<Integer> indices) {
        if (possibleBreakPoints.isEmpty()) {
            possibleBreakPoints.add(-1);
            return possibleBreakPoints;
        } else {
            List<Integer> glyphLineBased = new ArrayList<>();

            for (int j : possibleBreakPoints) {
                int found = TextRenderer.findPossibleBreaksSplitPosition(amountOfChars, j, true);
                if (found >= 0) {
                    glyphLineBased.add(indices.get(found));
                }
            }
            return glyphLineBased;
        }
    }

    private static void fillActualTextChunkRelatedLists(
            GlyphLine glyphLine, List<Integer> amountOfCharsBetweenTextStartAndActualTextChunk,
            List<Integer> glyphLineBasedIndicesOfActualTextChunkEnds) {
        // ActualTextChunk is either an ActualText or a single independent glyph

        ActualTextIterator actualTextIterator = new ActualTextIterator(glyphLine);

        int amountOfCharsBetweenTextStartAndCurrentActualTextStartOrGlyph = 0;
        while (actualTextIterator.hasNext()) {
            GlyphLine.GlyphLinePart part = actualTextIterator.next();
            int amountOfCharsWithinCurrentActualTextOrGlyph = 0;
            if (part.getActualText() != null) {
                amountOfCharsWithinCurrentActualTextOrGlyph = part.getActualText().length();
                int nextAmountOfChars = amountOfCharsWithinCurrentActualTextOrGlyph
                        + amountOfCharsBetweenTextStartAndCurrentActualTextStartOrGlyph;
                amountOfCharsBetweenTextStartAndActualTextChunk.add(nextAmountOfChars);
                glyphLineBasedIndicesOfActualTextChunkEnds.add(part.getEnd());
                amountOfCharsBetweenTextStartAndCurrentActualTextStartOrGlyph = nextAmountOfChars;
            } else {
                for (int j = part.getStart(); j < part.getEnd(); j++) {
                    char[] chars = glyphLine.get(j).getChars();
                    amountOfCharsWithinCurrentActualTextOrGlyph = chars != null ? chars.length : 0;
                    int nextAmountOfChars = amountOfCharsWithinCurrentActualTextOrGlyph
                            + amountOfCharsBetweenTextStartAndCurrentActualTextStartOrGlyph;
                    amountOfCharsBetweenTextStartAndActualTextChunk.add(nextAmountOfChars);
                    glyphLineBasedIndicesOfActualTextChunkEnds.add(j + 1);
                    amountOfCharsBetweenTextStartAndCurrentActualTextStartOrGlyph = nextAmountOfChars;
                }
            }
        }
    }

    static enum SpecialScriptsContainingSequenceStatus {
        MOVE_SEQUENCE_CONTAINING_SPECIAL_SCRIPTS_ON_NEXT_LINE,
        MOVE_TO_PREVIOUS_TEXT_RENDERER_CONTAINING_SPECIAL_SCRIPTS,
        FORCED_SPLIT
    }

    static class MinMaxWidthOfTextRendererSequenceHelper {
        public float minWidthPreSequence;
        public float textIndent;
        public boolean anythingPlacedBeforeTextRendererSequence;

        public MinMaxWidthOfTextRendererSequenceHelper(float minWidthPreSequence, float textIndent,
                boolean anythingPlacedBeforeTextRendererSequence) {
            this.minWidthPreSequence = minWidthPreSequence;
            this.textIndent = textIndent;
            this.anythingPlacedBeforeTextRendererSequence = anythingPlacedBeforeTextRendererSequence;
        }

    }

    static class LastFittingChildRendererData {
        public int childIndex;
        public LayoutResult childLayoutResult;

        public LastFittingChildRendererData(int childIndex, LayoutResult childLayoutResult) {
            this.childIndex = childIndex;
            this.childLayoutResult = childLayoutResult;
        }
    }

    // numberOfSequentialTextRenderers - number of sequential TextRenderers containing special scripts,
    // plus number of ignored floating renderers occurring amidst the sequence;
    // sequentialTextContent - converted to String and concatenated TextRenderer#text-s;
    // indicesOfFloating - indices of ignored floating child renderers of this LineRenderer
    static class SpecialScriptsContainingTextRendererSequenceInfo {
        public int numberOfSequentialTextRenderers;
        public String sequentialTextContent;
        List<Integer> indicesOfFloating;

        public SpecialScriptsContainingTextRendererSequenceInfo
                (int numberOfSequentialTextRenderers, String sequentialTextContent, List<Integer> indicesOfFloating) {
            this.numberOfSequentialTextRenderers = numberOfSequentialTextRenderers;
            this.sequentialTextContent = sequentialTextContent;
            this.indicesOfFloating = indicesOfFloating;
        }
    }
}
