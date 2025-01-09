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
import com.itextpdf.commons.datastructures.Tuple2;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.font.FontMetrics;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.TrueTypeFont;
import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.io.font.otf.GlyphLine;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.io.util.EnumUtil;
import com.itextpdf.io.util.TextUtil;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfType0Font;
import com.itextpdf.kernel.font.PdfType1Font;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.canvas.CanvasArtifact;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.exceptions.LayoutExceptionMessageConstant;
import com.itextpdf.layout.font.FontCharacteristics;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.layout.font.FontSet;
import com.itextpdf.layout.font.selectorstrategy.IFontSelectorStrategy;
import com.itextpdf.layout.hyphenation.Hyphenation;
import com.itextpdf.layout.hyphenation.HyphenationConfig;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.layout.TextLayoutResult;
import com.itextpdf.layout.minmaxwidth.MinMaxWidth;
import com.itextpdf.layout.minmaxwidth.MinMaxWidthUtils;
import com.itextpdf.layout.properties.BaseDirection;
import com.itextpdf.layout.properties.FloatPropertyValue;
import com.itextpdf.layout.properties.FontKerning;
import com.itextpdf.layout.properties.OverflowPropertyValue;
import com.itextpdf.layout.properties.OverflowWrapPropertyValue;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.RenderingMode;
import com.itextpdf.layout.properties.TransparentColor;
import com.itextpdf.layout.properties.Underline;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.splitting.BreakAllSplitCharacters;
import com.itextpdf.layout.splitting.ISplitCharacters;
import com.itextpdf.layout.tagging.LayoutTaggingHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents the {@link IRenderer renderer} object for a {@link Text}
 * object. It will draw the glyphs of the textual content on the {@link DrawContext}.
 */
public class TextRenderer extends AbstractRenderer implements ILeafElementRenderer {

    protected static final float TEXT_SPACE_COEFF = FontProgram.UNITS_NORMALIZATION;
    static final float TYPO_ASCENDER_SCALE_COEFF = 1.2f;
    static final int UNDEFINED_FIRST_CHAR_TO_FORCE_OVERFLOW = Integer.MAX_VALUE;

    private static final float ITALIC_ANGLE = 0.21256f;
    private static final float BOLD_SIMULATION_STROKE_COEFF = 1 / 30f;

    protected float yLineOffset;

    private PdfFont font;
    protected GlyphLine text;
    protected GlyphLine line;
    protected String strToBeConverted;

    protected boolean otfFeaturesApplied = false;

    protected float tabAnchorCharacterPosition = -1;

    protected List<int[]> reversedRanges;

    protected GlyphLine savedWordBreakAtLineEnding;

    // if list is null, presence of special scripts in the TextRenderer#text hasn't been checked yet
    // if list is empty, TextRenderer#text has been analyzed and no special scripts have been detected
    // if list contains -1, TextRenderer#text contains special scripts, but no word break is possible within it
    // Must remain ArrayList: once an instance is formed and filled prior to layouting on split of this TextRenderer,
    // it's used to get element by index or passed to List.subList()
    private List<Integer> specialScriptsWordBreakPoints;
    private int specialScriptFirstNotFittingIndex = -1;
    private int indexOfFirstCharacterToBeForcedToOverflow = UNDEFINED_FIRST_CHAR_TO_FORCE_OVERFLOW;

    /**
     * Creates a TextRenderer from its corresponding layout object.
     *
     * @param textElement the {@link Text} which this object should manage
     */
    public TextRenderer(Text textElement) {
        this(textElement, textElement.getText());
    }

    /**
     * Creates a TextRenderer from its corresponding layout object, with a custom
     * text to replace the contents of the {@link Text}.
     *
     * @param textElement the {@link Text} which this object should manage
     * @param text        the replacement text
     */
    public TextRenderer(Text textElement, String text) {
        super(textElement);
        this.strToBeConverted = text;
    }

    protected TextRenderer(TextRenderer other) {
        super(other);
        this.text = other.text;
        this.line = other.line;
        this.font = other.font;
        this.yLineOffset = other.yLineOffset;
        this.strToBeConverted = other.strToBeConverted;
        this.otfFeaturesApplied = other.otfFeaturesApplied;
        this.tabAnchorCharacterPosition = other.tabAnchorCharacterPosition;
        this.reversedRanges = other.reversedRanges;
        this.specialScriptsWordBreakPoints = other.specialScriptsWordBreakPoints;
    }

    @Override
    public LayoutResult layout(LayoutContext layoutContext) {
        updateFontAndText();

        LayoutArea area = layoutContext.getArea();
        Rectangle layoutBox = area.getBBox().clone();

        boolean noSoftWrap = Boolean.TRUE.equals(this.parent.<Boolean>getOwnProperty(Property.NO_SOFT_WRAP_INLINE));

        OverflowPropertyValue overflowX = this.parent.<OverflowPropertyValue>getProperty(Property.OVERFLOW_X);

        OverflowWrapPropertyValue overflowWrap = this.<OverflowWrapPropertyValue>getProperty(Property.OVERFLOW_WRAP);
        boolean overflowWrapNotNormal = overflowWrap == OverflowWrapPropertyValue.ANYWHERE
                || overflowWrap == OverflowWrapPropertyValue.BREAK_WORD;
        if (overflowWrapNotNormal) {
            overflowX = OverflowPropertyValue.FIT;
        }

        List<Rectangle> floatRendererAreas = layoutContext.getFloatRendererAreas();
        FloatPropertyValue floatPropertyValue = this.<FloatPropertyValue>getProperty(Property.FLOAT);

        if (FloatingHelper.isRendererFloating(this, floatPropertyValue)) {
            FloatingHelper.adjustFloatedBlockLayoutBox(this, layoutBox, null, floatRendererAreas, floatPropertyValue, overflowX);
        }

        float preMarginBorderPaddingWidth = layoutBox.getWidth();
        UnitValue[] margins = getMargins();
        applyMargins(layoutBox, margins, false);
        Border[] borders = getBorders();
        applyBorderBox(layoutBox, borders, false);

        UnitValue[] paddings = getPaddings();
        applyPaddings(layoutBox, paddings, false);

        MinMaxWidth countedMinMaxWidth = new MinMaxWidth(preMarginBorderPaddingWidth - layoutBox.getWidth());
        AbstractWidthHandler widthHandler;
        if (noSoftWrap) {
            widthHandler = new SumSumWidthHandler(countedMinMaxWidth);
        } else {
            widthHandler = new MaxSumWidthHandler(countedMinMaxWidth);
        }

        float leftMinWidth = -1f;
        float[] leftMarginBorderPadding = {margins[LEFT_SIDE].getValue(),
                borders[LEFT_SIDE] == null ? 0.0f : borders[LEFT_SIDE].getWidth(),
                paddings[LEFT_SIDE].getValue()};
        float rightMinWidth = -1f;
        float[] rightMarginBorderPadding = {margins[RIGHT_SIDE].getValue(),
                borders[RIGHT_SIDE] == null ? 0.0f : borders[RIGHT_SIDE].getWidth(),
                paddings[RIGHT_SIDE].getValue()};

        occupiedArea = new LayoutArea(area.getPageNumber(), new Rectangle(layoutBox.getX(), layoutBox.getY() + layoutBox.getHeight(), 0, 0));

        TargetCounterHandler.addPageByID(this);

        boolean anythingPlaced = false;

        int currentTextPos = text.getStart();
        UnitValue fontSize = (UnitValue) this.getPropertyAsUnitValue(Property.FONT_SIZE);
        if (!fontSize.isPointValue()) {
            Logger logger = LoggerFactory.getLogger(TextRenderer.class);
            logger.error(MessageFormatUtil.format(IoLogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED,
                    Property.FONT_SIZE));
        }
        float textRise = (float) this.getPropertyAsFloat(Property.TEXT_RISE);
        Float characterSpacing = this.getPropertyAsFloat(Property.CHARACTER_SPACING);
        Float wordSpacing = this.getPropertyAsFloat(Property.WORD_SPACING);
        float hScale = (float) this.getProperty(Property.HORIZONTAL_SCALING, (Float) 1f);
        ISplitCharacters splitCharacters = this.<ISplitCharacters>getProperty(Property.SPLIT_CHARACTERS);
        float italicSkewAddition = Boolean.TRUE.equals(getPropertyAsBoolean(Property.ITALIC_SIMULATION)) ? ITALIC_ANGLE * fontSize.getValue() : 0;
        float boldSimulationAddition = Boolean.TRUE.equals(getPropertyAsBoolean(Property.BOLD_SIMULATION)) ? BOLD_SIMULATION_STROKE_COEFF * fontSize.getValue() : 0;

        line = new GlyphLine(text);
        line.setStart(-1);
        line.setEnd(-1);

        float ascender = 0;
        float descender = 0;

        float currentLineAscender = 0;
        float currentLineDescender = 0;
        float currentLineHeight = 0;
        int initialLineTextPos = currentTextPos;
        float currentLineWidth = 0;
        int previousCharPos = -1;

        RenderingMode mode = this.<RenderingMode>getProperty(Property.RENDERING_MODE);
        float[] ascenderDescender = calculateAscenderDescender(font, mode);
        ascender = ascenderDescender[0];
        descender = ascenderDescender[1];
        if (RenderingMode.HTML_MODE.equals(mode)) {
            currentLineAscender = ascenderDescender[0];
            currentLineDescender = ascenderDescender[1];
            currentLineHeight = (currentLineAscender - currentLineDescender) * FontProgram.convertTextSpaceToGlyphSpace(
                    fontSize.getValue()) + textRise;
        }

        savedWordBreakAtLineEnding = null;
        Glyph wordBreakGlyphAtLineEnding = null;

        Character tabAnchorCharacter = this.<Character>getProperty(Property.TAB_ANCHOR);

        TextLayoutResult result = null;

        OverflowPropertyValue overflowY = !layoutContext.isClippedHeight()
                ? OverflowPropertyValue.FIT
                : this.parent.<OverflowPropertyValue>getProperty(Property.OVERFLOW_Y);

        // true in situations like "\nHello World" or "Hello\nWorld"
        boolean isSplitForcedByNewLine = false;
        // needed in situation like "\nHello World" or " Hello World", when split occurs on first character, but we want to leave it on previous line
        boolean forcePartialSplitOnFirstChar = false;
        // true in situations like "Hello\nWorld"
        boolean ignoreNewLineSymbol = false;
        // true when \r\n are found
        boolean crlf = false;

        boolean containsPossibleBreak = false;

        HyphenationConfig hyphenationConfig = this.<HyphenationConfig>getProperty(Property.HYPHENATION);

        // For example, if a first character is a RTL mark (U+200F), and the second is a newline, we need to break anyway
        int firstPrintPos = currentTextPos;
        while (firstPrintPos < text.getEnd() && noPrint(text.get(firstPrintPos))) {
            firstPrintPos++;
        }

        while (currentTextPos < text.getEnd()) {
            if (noPrint(text.get(currentTextPos))) {
                if (line.getStart() == -1) {
                    line.setStart(currentTextPos);
                }
                line.setEnd(Math.max(line.getEnd(), currentTextPos + 1));
                currentTextPos++;
                continue;
            }

            int nonBreakablePartEnd = text.getEnd() - 1;
            float nonBreakablePartFullWidth = 0;
            float nonBreakablePartWidthWhichDoesNotExceedAllowedWidth = 0;
            float nonBreakablePartMaxAscender = 0;
            float nonBreakablePartMaxDescender = 0;
            float nonBreakablePartMaxHeight = 0;
            int firstCharacterWhichExceedsAllowedWidth = -1;
            float nonBreakingHyphenRelatedChunkWidth = 0;
            int nonBreakingHyphenRelatedChunkStart = -1;
            float beforeNonBreakingHyphenRelatedChunkMaxAscender = 0;
            float beforeNonBreakingHyphenRelatedChunkMaxDescender = 0;

            for (int ind = currentTextPos; ind < text.getEnd(); ind++) {
                if (TextUtil.isNewLine(text.get(ind))) {
                    containsPossibleBreak = true;
                    wordBreakGlyphAtLineEnding = text.get(ind);
                    isSplitForcedByNewLine = true;
                    firstCharacterWhichExceedsAllowedWidth = ind + 1;
                    if (ind != firstPrintPos) {
                        ignoreNewLineSymbol = true;
                    } else {
                        // Notice that in that case we do not need to ignore the new line symbol ('\n')
                        forcePartialSplitOnFirstChar = true;
                    }

                    if (line.getStart() == -1) {
                        line.setStart(currentTextPos);
                    }

                    crlf = TextUtil.isCarriageReturnFollowedByLineFeed(text, currentTextPos);

                    if (crlf) {
                        currentTextPos++;
                    }

                    line.setEnd(Math.max(line.getEnd(), firstCharacterWhichExceedsAllowedWidth - 1));
                    break;
                }

                Glyph currentGlyph = text.get(ind);
                if (noPrint(currentGlyph)) {
                    boolean nextGlyphIsSpaceOrWhiteSpace = ind + 1 < text.getEnd()
                            && (splitCharacters.isSplitCharacter(text, ind + 1)
                            && TextUtil.isSpaceOrWhitespace(text.get(ind + 1)));
                    if (nextGlyphIsSpaceOrWhiteSpace && firstCharacterWhichExceedsAllowedWidth == -1) {
                        containsPossibleBreak = true;
                    }
                    if (ind + 1 == text.getEnd() || nextGlyphIsSpaceOrWhiteSpace
                            || (ind + 1 >= indexOfFirstCharacterToBeForcedToOverflow)) {
                        if (ind + 1 >= indexOfFirstCharacterToBeForcedToOverflow) {
                            firstCharacterWhichExceedsAllowedWidth = currentTextPos;
                            break;
                        } else {
                            nonBreakablePartEnd = ind;
                            break;
                        }
                    }
                    continue;
                }
                if (tabAnchorCharacter != null && tabAnchorCharacter == text.get(ind).getUnicode()) {
                    tabAnchorCharacterPosition = currentLineWidth + nonBreakablePartFullWidth;
                    tabAnchorCharacter = null;
                }

                final float glyphWidth = FontProgram.convertTextSpaceToGlyphSpace(
                        getCharWidth(currentGlyph, fontSize.getValue(), hScale, characterSpacing, wordSpacing));
                float xAdvance = previousCharPos != -1 ? text.get(previousCharPos).getXAdvance() : 0;
                if (xAdvance != 0) {
                    xAdvance = FontProgram.convertTextSpaceToGlyphSpace(
                            scaleXAdvance(xAdvance, fontSize.getValue(), hScale));
                }

                final float potentialWidth =
                        nonBreakablePartFullWidth + glyphWidth + xAdvance + italicSkewAddition + boldSimulationAddition;
                final boolean symbolNotFitOnLine = potentialWidth > layoutBox.getWidth() - currentLineWidth + EPS;
                if ((!noSoftWrap && symbolNotFitOnLine && firstCharacterWhichExceedsAllowedWidth == -1)
                        || ind == specialScriptFirstNotFittingIndex) {
                    firstCharacterWhichExceedsAllowedWidth = ind;
                    boolean spaceOrWhitespace = TextUtil.isSpaceOrWhitespace(text.get(ind));
                    OverflowPropertyValue parentOverflowX = parent.<OverflowPropertyValue>getProperty(Property.OVERFLOW_X);
                    if (spaceOrWhitespace || overflowWrapNotNormal && !isOverflowFit(parentOverflowX)) {
                        if (spaceOrWhitespace) {
                            wordBreakGlyphAtLineEnding = currentGlyph;
                        }
                        if (ind == firstPrintPos) {
                            containsPossibleBreak = true;
                            forcePartialSplitOnFirstChar = true;
                            firstCharacterWhichExceedsAllowedWidth = ind + 1;
                            break;
                        }
                    }
                }

                if (null != hyphenationConfig) {
                    if (glyphBelongsToNonBreakingHyphenRelatedChunk(text, ind)) {
                        if (-1 == nonBreakingHyphenRelatedChunkStart) {
                            beforeNonBreakingHyphenRelatedChunkMaxAscender = nonBreakablePartMaxAscender;
                            beforeNonBreakingHyphenRelatedChunkMaxDescender = nonBreakablePartMaxDescender;
                            nonBreakingHyphenRelatedChunkStart = ind;
                        }
                        nonBreakingHyphenRelatedChunkWidth += glyphWidth + xAdvance;
                    } else {
                        nonBreakingHyphenRelatedChunkStart = -1;
                        nonBreakingHyphenRelatedChunkWidth = 0;
                    }
                }
                if (firstCharacterWhichExceedsAllowedWidth == -1 || !isOverflowFit(overflowX)) {
                    nonBreakablePartWidthWhichDoesNotExceedAllowedWidth += glyphWidth + xAdvance;
                }
                nonBreakablePartFullWidth += glyphWidth + xAdvance;

                nonBreakablePartMaxAscender = Math.max(nonBreakablePartMaxAscender, ascender);
                nonBreakablePartMaxDescender = Math.min(nonBreakablePartMaxDescender, descender);
                nonBreakablePartMaxHeight = FontProgram.convertTextSpaceToGlyphSpace(
                        (nonBreakablePartMaxAscender - nonBreakablePartMaxDescender) * fontSize.getValue()) + textRise;

                previousCharPos = ind;

                if (!noSoftWrap && symbolNotFitOnLine
                        && (0 == nonBreakingHyphenRelatedChunkWidth || ind + 1 == text.getEnd() ||
                        !glyphBelongsToNonBreakingHyphenRelatedChunk(text, ind + 1))) {
                    if (isOverflowFit(overflowX)) {
                        // we have extracted all the information we wanted and we do not want to continue.
                        // we will have to split the word anyway.
                        break;
                    }
                }

                if (OverflowWrapPropertyValue.ANYWHERE == overflowWrap) {
                    float childMinWidth = (float) ((double) glyphWidth + (double) xAdvance + (double) italicSkewAddition
                            + (double) boldSimulationAddition);
                    if (leftMinWidth == -1f) {
                        leftMinWidth = childMinWidth;
                    } else {
                        rightMinWidth = childMinWidth;
                    }
                    widthHandler.updateMinChildWidth(childMinWidth);
                    widthHandler.updateMaxChildWidth((float) ((double) glyphWidth + (double) xAdvance));
                }

                boolean endOfWordBelongingToSpecialScripts = textContainsSpecialScriptGlyphs(true)
                        && findPossibleBreaksSplitPosition(specialScriptsWordBreakPoints,
                        ind + 1, true) >= 0;
                boolean endOfNonBreakablePartCausedBySplitCharacter = splitCharacters.isSplitCharacter(text, ind)
                        || (ind + 1 < text.getEnd()
                        && (splitCharacters.isSplitCharacter(text, ind + 1)
                        && TextUtil.isSpaceOrWhitespace(text.get(ind + 1))));
                if (endOfNonBreakablePartCausedBySplitCharacter && firstCharacterWhichExceedsAllowedWidth == -1) {
                    containsPossibleBreak = true;
                }
                if (ind + 1 == text.getEnd()
                        || endOfNonBreakablePartCausedBySplitCharacter
                        || endOfWordBelongingToSpecialScripts
                        || (ind + 1 >= indexOfFirstCharacterToBeForcedToOverflow)) {
                    if (ind + 1 >= indexOfFirstCharacterToBeForcedToOverflow
                            && !endOfNonBreakablePartCausedBySplitCharacter) {
                        firstCharacterWhichExceedsAllowedWidth = currentTextPos;
                    }
                    nonBreakablePartEnd = ind;
                    break;
                }
            }

            if (firstCharacterWhichExceedsAllowedWidth == -1) {
                // can fit the whole word in a line
                if (line.getStart() == -1) {
                    line.setStart(currentTextPos);
                }
                line.setEnd(Math.max(line.getEnd(), nonBreakablePartEnd + 1));
                currentLineAscender = Math.max(currentLineAscender, nonBreakablePartMaxAscender);
                currentLineDescender = Math.min(currentLineDescender, nonBreakablePartMaxDescender);
                currentLineHeight = Math.max(currentLineHeight, nonBreakablePartMaxHeight);
                currentTextPos = nonBreakablePartEnd + 1;
                currentLineWidth += nonBreakablePartFullWidth;
                if (OverflowWrapPropertyValue.ANYWHERE == overflowWrap) {
                    widthHandler.updateMaxChildWidth((float) ((double) italicSkewAddition
                            + (double) boldSimulationAddition));
                } else {
                    float childMinWidth = (float) ((double) nonBreakablePartWidthWhichDoesNotExceedAllowedWidth
                            + (double) italicSkewAddition + (double) boldSimulationAddition);
                    if (leftMinWidth == -1f) {
                        leftMinWidth = childMinWidth;
                    } else {
                        rightMinWidth = childMinWidth;
                    }
                    widthHandler.updateMinChildWidth(childMinWidth);
                    widthHandler.updateMaxChildWidth(childMinWidth);
                }
                anythingPlaced = true;
            } else {
                // check if line height exceeds the allowed height
                if (Math.max(currentLineHeight, nonBreakablePartMaxHeight) > layoutBox.getHeight() && isOverflowFit(overflowY)) {
                    applyPaddings(occupiedArea.getBBox(), paddings, true);
                    applyBorderBox(occupiedArea.getBBox(), borders, true);
                    applyMargins(occupiedArea.getBBox(), margins, true);
                    // Force to place what we can
                    if (line.getStart() == -1) {
                        line.setStart(currentTextPos);
                    }
                    line.setEnd(Math.max(line.getEnd(), firstCharacterWhichExceedsAllowedWidth));
                    // the line does not fit because of height - full overflow
                    TextRenderer[] splitResult = split(initialLineTextPos);

                    boolean[] startsEnds = isStartsWithSplitCharWhiteSpaceAndEndsWithSplitChar(splitCharacters);
                    return new TextLayoutResult(
                            LayoutResult.NOTHING, occupiedArea, splitResult[0], splitResult[1], this)
                            .setContainsPossibleBreak(containsPossibleBreak)
                            .setStartsWithSplitCharacterWhiteSpace(startsEnds[0])
                            .setEndsWithSplitCharacter(startsEnds[1]);
                } else {
                    // cannot fit a word as a whole

                    boolean wordSplit = false;
                    boolean hyphenationApplied = false;

                    if (hyphenationConfig != null && indexOfFirstCharacterToBeForcedToOverflow == UNDEFINED_FIRST_CHAR_TO_FORCE_OVERFLOW) {
                        if (-1 == nonBreakingHyphenRelatedChunkStart) {
                            int[] wordBounds = getWordBoundsForHyphenation(text, currentTextPos, text.getEnd(),
                                    Math.max(currentTextPos, firstCharacterWhichExceedsAllowedWidth - 1));
                            if (wordBounds != null) {
                                String word = text.toUnicodeString(wordBounds[0], wordBounds[1]);
                                Hyphenation hyph = hyphenationConfig.hyphenate(word);
                                if (hyph != null) {
                                    for (int i = hyph.length() - 1; i >= 0; i--) {
                                        String pre = hyph.getPreHyphenText(i);
                                        String pos = hyph.getPostHyphenText(i);
                                        char hyphen = hyphenationConfig.getHyphenSymbol();
                                        String glyphLine = text.toUnicodeString(currentTextPos, wordBounds[0]) + pre;
                                        if (font.containsGlyph(hyphen)) {
                                            glyphLine += hyphen;
                                        }
                                        float currentHyphenationChoicePreTextWidth =
                                                getGlyphLineWidth(convertToGlyphLine(glyphLine),
                                                        fontSize.getValue(), hScale, characterSpacing, wordSpacing);
                                        if (currentLineWidth + currentHyphenationChoicePreTextWidth + italicSkewAddition + boldSimulationAddition <= layoutBox.getWidth()) {
                                            hyphenationApplied = true;

                                            if (line.getStart() == -1) {
                                                line.setStart(currentTextPos);
                                            }
                                            line.setEnd(Math.max(line.getEnd(), wordBounds[0] + pre.length()));
                                            if (font.containsGlyph(hyphen)) {
                                                GlyphLine lineCopy = line.copy(line.getStart(), line.getEnd());
                                                lineCopy.add(font.getGlyph(hyphen));
                                                lineCopy.setEnd(lineCopy.getEnd() + 1);
                                                line = lineCopy;
                                            }

                                            // TODO DEVSIX-7010 recalculate line properties in case of word hyphenation.
                                            // These values are based on whole word. Recalculate properly based on hyphenated part.
                                            currentLineAscender = Math.max(currentLineAscender, nonBreakablePartMaxAscender);
                                            currentLineDescender = Math.min(currentLineDescender, nonBreakablePartMaxDescender);
                                            currentLineHeight = Math.max(currentLineHeight, nonBreakablePartMaxHeight);

                                            currentLineWidth += currentHyphenationChoicePreTextWidth;
                                            if (OverflowWrapPropertyValue.ANYWHERE == overflowWrap) {
                                                widthHandler.updateMaxChildWidth((float) ((double) italicSkewAddition
                                                        + (double) boldSimulationAddition));
                                            } else {
                                                widthHandler.updateMinChildWidth(
                                                        (float) ((double) currentHyphenationChoicePreTextWidth
                                                                + (double) italicSkewAddition
                                                                + (double) boldSimulationAddition));
                                                widthHandler.updateMaxChildWidth(
                                                        (float) ((double) currentHyphenationChoicePreTextWidth
                                                                + (double) italicSkewAddition
                                                                + (double) boldSimulationAddition));
                                            }
                                            currentTextPos = wordBounds[0] + pre.length();
                                            break;
                                        }
                                    }
                                }
                            }
                        } else {
                            if (text.getStart() == nonBreakingHyphenRelatedChunkStart) {
                                nonBreakingHyphenRelatedChunkWidth = 0;
                                firstCharacterWhichExceedsAllowedWidth = previousCharPos + 1;
                            } else {
                                firstCharacterWhichExceedsAllowedWidth = nonBreakingHyphenRelatedChunkStart;
                                nonBreakablePartFullWidth -= nonBreakingHyphenRelatedChunkWidth;
                                nonBreakablePartMaxAscender = beforeNonBreakingHyphenRelatedChunkMaxAscender;
                                nonBreakablePartMaxDescender = beforeNonBreakingHyphenRelatedChunkMaxDescender;
                            }
                        }
                    }

                    boolean specialScriptWordSplit = textContainsSpecialScriptGlyphs(true)
                            && !isSplitForcedByNewLine && isOverflowFit(overflowX);
                    if ((nonBreakablePartFullWidth > layoutBox.getWidth() && !anythingPlaced && !hyphenationApplied)
                            || forcePartialSplitOnFirstChar
                            || -1 != nonBreakingHyphenRelatedChunkStart
                            || specialScriptWordSplit) {
                        // if the word is too long for a single line we will have to split it
                        // we also need to split the word here if text contains glyphs from scripts
                        // which require word wrapping for further processing in LineRenderer
                        if (line.getStart() == -1) {
                            line.setStart(currentTextPos);
                        }
                        if (!crlf) {
                            currentTextPos = (forcePartialSplitOnFirstChar || isOverflowFit(overflowX) || specialScriptWordSplit) ? firstCharacterWhichExceedsAllowedWidth : nonBreakablePartEnd + 1;
                        }
                        line.setEnd(Math.max(line.getEnd(), currentTextPos));
                        wordSplit = !forcePartialSplitOnFirstChar && (text.getEnd() != currentTextPos);
                        if (wordSplit || !(forcePartialSplitOnFirstChar || isOverflowFit(overflowX))) {
                            currentLineAscender = Math.max(currentLineAscender, nonBreakablePartMaxAscender);
                            currentLineDescender = Math.min(currentLineDescender, nonBreakablePartMaxDescender);
                            currentLineHeight = Math.max(currentLineHeight, nonBreakablePartMaxHeight);
                            currentLineWidth += nonBreakablePartWidthWhichDoesNotExceedAllowedWidth;
                            if (OverflowWrapPropertyValue.ANYWHERE == overflowWrap) {
                                widthHandler.updateMaxChildWidth((float) ((double) italicSkewAddition
                                        + (double) boldSimulationAddition));
                            } else {
                                float childMinWidth =
                                        (float) ((double) nonBreakablePartWidthWhichDoesNotExceedAllowedWidth
                                                + (double) italicSkewAddition + (double) boldSimulationAddition);
                                if (leftMinWidth == -1f) {
                                    leftMinWidth = childMinWidth;
                                } else {
                                    rightMinWidth = childMinWidth;
                                }
                                widthHandler.updateMinChildWidth(childMinWidth);
                                widthHandler.updateMaxChildWidth(childMinWidth);
                            }
                        } else {
                            // process empty line (e.g. '\n')
                            currentLineAscender = ascender;
                            currentLineDescender = descender;
                            currentLineHeight = FontProgram.convertTextSpaceToGlyphSpace(
                                    (currentLineAscender - currentLineDescender) * fontSize.getValue()) + textRise;
                            currentLineWidth += FontProgram.convertTextSpaceToGlyphSpace(
                                    getCharWidth(line.get(line.getStart()), fontSize.getValue(), hScale,
                                            characterSpacing, wordSpacing));
                        }
                    }
                    if (line.getEnd() <= line.getStart()) {
                        boolean[] startsEnds = isStartsWithSplitCharWhiteSpaceAndEndsWithSplitChar(splitCharacters);
                        return new TextLayoutResult(
                                LayoutResult.NOTHING, occupiedArea, null, this, this)
                                .setContainsPossibleBreak(containsPossibleBreak)
                                .setStartsWithSplitCharacterWhiteSpace(startsEnds[0])
                                .setEndsWithSplitCharacter(startsEnds[1]);
                    } else {
                        result = new TextLayoutResult(
                                LayoutResult.PARTIAL, occupiedArea, null, null)
                                .setWordHasBeenSplit(wordSplit)
                                .setContainsPossibleBreak(containsPossibleBreak);
                    }

                    break;
                }
            }
        }
        // indicates whether the placing is forced while the layout result is LayoutResult.NOTHING
        boolean isPlacingForcedWhileNothing = false;
        if (currentLineHeight > layoutBox.getHeight()) {
            if (!Boolean.TRUE.equals(getPropertyAsBoolean(Property.FORCED_PLACEMENT)) && isOverflowFit(overflowY)) {
                applyPaddings(occupiedArea.getBBox(), paddings, true);
                applyBorderBox(occupiedArea.getBBox(), borders, true);
                applyMargins(occupiedArea.getBBox(), margins, true);
                boolean[] startsEnds = isStartsWithSplitCharWhiteSpaceAndEndsWithSplitChar(splitCharacters);
                return new TextLayoutResult(
                        LayoutResult.NOTHING, occupiedArea, null, this, this)
                        .setContainsPossibleBreak(containsPossibleBreak)
                        .setStartsWithSplitCharacterWhiteSpace(startsEnds[0])
                        .setEndsWithSplitCharacter(startsEnds[1]);
            } else {
                isPlacingForcedWhileNothing = true;
            }
        }

        yLineOffset = RenderingMode.SVG_MODE == mode ? 0 :
                FontProgram.convertTextSpaceToGlyphSpace(currentLineAscender * fontSize.getValue());

        occupiedArea.getBBox().moveDown(currentLineHeight);
        occupiedArea.getBBox().setHeight(occupiedArea.getBBox().getHeight() + currentLineHeight);

        occupiedArea.getBBox().setWidth(Math.max(occupiedArea.getBBox().getWidth(), currentLineWidth));
        layoutBox.setHeight(area.getBBox().getHeight() - currentLineHeight);

        occupiedArea.getBBox().setWidth(occupiedArea.getBBox().getWidth() + italicSkewAddition + boldSimulationAddition);

        applyPaddings(occupiedArea.getBBox(), paddings, true);
        applyBorderBox(occupiedArea.getBBox(), borders, true);
        applyMargins(occupiedArea.getBBox(), margins, true);

        increaseYLineOffset(paddings, borders, margins);

        if (result == null) {
            result = new TextLayoutResult(LayoutResult.FULL, occupiedArea, null, null,
                    isPlacingForcedWhileNothing ? this : null)
                    .setContainsPossibleBreak(containsPossibleBreak);
        } else {
            TextRenderer[] split;
            if (ignoreNewLineSymbol || crlf) {
                // ignore '\n'
                split = splitIgnoreFirstNewLine(currentTextPos);
            } else {
                split = split(currentTextPos);
            }
            result.setSplitForcedByNewline(isSplitForcedByNewLine);
            result.setSplitRenderer(split[0]);
            if (wordBreakGlyphAtLineEnding != null) {
                split[0].saveWordBreakIfNotYetSaved(wordBreakGlyphAtLineEnding);
            }

            // no sense to process empty renderer
            if (split[1].text.getStart() != split[1].text.getEnd()) {
                result.setOverflowRenderer(split[1]);
            } else {
                // LayoutResult with partial status should have non-null overflow renderer
                result.setStatus(LayoutResult.FULL);
            }
        }

        if (FloatingHelper.isRendererFloating(this, floatPropertyValue)) {
            if (result.getStatus() == LayoutResult.FULL) {
                if (occupiedArea.getBBox().getWidth() > 0) {
                    floatRendererAreas.add(occupiedArea.getBBox());
                }
            } else if (result.getStatus() == LayoutResult.PARTIAL) {
                floatRendererAreas.add(result.getSplitRenderer().getOccupiedArea().getBBox());
            }
        }

        result.setMinMaxWidth(countedMinMaxWidth);
        if (!noSoftWrap) {
            for (float dimension : leftMarginBorderPadding) {
                leftMinWidth += dimension;
            }
            for (float dimension : rightMarginBorderPadding) {
                if (rightMinWidth < 0) {
                    leftMinWidth += dimension;
                } else {
                    rightMinWidth += dimension;
                }
            }
            result.setLeftMinWidth(leftMinWidth);
            result.setRightMinWidth(rightMinWidth);
        } else {
            result.setLeftMinWidth(countedMinMaxWidth.getMinWidth());
            result.setRightMinWidth(-1f);
        }
        boolean[] startsEnds = isStartsWithSplitCharWhiteSpaceAndEndsWithSplitChar(splitCharacters);
        result.setStartsWithSplitCharacterWhiteSpace(startsEnds[0])
              .setEndsWithSplitCharacter(startsEnds[1]);
        return result;
    }

    private void increaseYLineOffset(UnitValue[] paddings, Border[] borders, UnitValue[] margins) {
        yLineOffset += paddings[0] != null ? paddings[0].getValue() : 0;
        yLineOffset += borders[0] != null ?borders[0].getWidth() : 0;
        yLineOffset +=  margins[0] != null ? margins[0].getValue() : 0;
    }

    public void applyOtf() {
        updateFontAndText();
        Character.UnicodeScript script = this.<Character.UnicodeScript>getProperty(Property.FONT_SCRIPT);
        if (!otfFeaturesApplied && TypographyUtils.isPdfCalligraphAvailable() && text.getStart() < text.getEnd()) {
            final PdfDocument pdfDocument = getPdfDocument();
            final SequenceId sequenceId = pdfDocument == null ? null : pdfDocument.getDocumentIdWrapper();
            final MetaInfoContainer metaInfoContainer = this.<MetaInfoContainer>getProperty(Property.META_INFO);
            final IMetaInfo metaInfo = metaInfoContainer == null ? null : metaInfoContainer.getMetaInfo();
            if (hasOtfFont()) {
                Object typographyConfig = this.<Object>getProperty(Property.TYPOGRAPHY_CONFIG);
                Collection<Character.UnicodeScript> supportedScripts = null;
        	    if (typographyConfig != null) {
    	            supportedScripts = TypographyUtils.getSupportedScripts(typographyConfig);
	            }
	            if (supportedScripts == null) {
	                supportedScripts = TypographyUtils.getSupportedScripts();
	            }
                List<ScriptRange> scriptsRanges = new ArrayList<>();
                if (script != null) {
                    scriptsRanges.add(new ScriptRange(script, text.getEnd()));
                } else {
                    // Try to autodetect script.
                    ScriptRange currRange = new ScriptRange(null, text.getEnd());
                    scriptsRanges.add(currRange);
                    for (int i = text.getStart(); i < text.getEnd(); i++) {
                        int unicode = text.get(i).getUnicode();
                        if (unicode > -1) {
                            Character.UnicodeScript glyphScript = Character.UnicodeScript.of(unicode);
                            if (Character.UnicodeScript.COMMON.equals(glyphScript) || Character.UnicodeScript.UNKNOWN.equals(glyphScript)
                                    || Character.UnicodeScript.INHERITED.equals(glyphScript)) {
                                continue;
                            }
                            if (glyphScript != currRange.script) {
                                if (currRange.script == null) {
                                    currRange.script = glyphScript;
                                } else {
                                    currRange.rangeEnd = i;
                                    currRange = new ScriptRange(glyphScript, text.getEnd());
                                    scriptsRanges.add(currRange);
                                }
                            }
                        }
                    }
                }

                int delta = 0;
                int origTextStart = text.getStart();
                int origTextEnd = text.getEnd();
                int shapingRangeStart = text.getStart();
                for (ScriptRange scriptsRange : scriptsRanges) {
                    if (scriptsRange.script == null || !supportedScripts.contains(EnumUtil.throwIfNull(scriptsRange.script))) {
                        continue;
                    }
                    scriptsRange.rangeEnd += delta;
                    text.setStart(shapingRangeStart);
                    text.setEnd(scriptsRange.rangeEnd);

                    if ((scriptsRange.script == Character.UnicodeScript.ARABIC || scriptsRange.script == Character.UnicodeScript.HEBREW) && parent instanceof LineRenderer) {
                        // It's safe to set here BASE_DIRECTION to TextRenderer without additional checks, because
                        // by convention this property makes sense only if it's applied to LineRenderer or it's
                        // parents (Paragraph or above).
                        // Only if it's not found there first, LineRenderer tries to fetch autodetected BaseDirection
                        // from text renderers (see LineRenderer#applyOtf).
                        setProperty(Property.BASE_DIRECTION, BaseDirection.DEFAULT_BIDI);
                    }
                    TypographyUtils.applyOtfScript(
                            font.getFontProgram(), text, scriptsRange.script, typographyConfig, sequenceId, metaInfo);

                    delta += text.getEnd() - scriptsRange.rangeEnd;
                    scriptsRange.rangeEnd = shapingRangeStart = text.getEnd();
                }
                text.setStart(origTextStart);
                text.setEnd(origTextEnd + delta);
            }

            FontKerning fontKerning = (FontKerning) this.<FontKerning>getProperty(Property.FONT_KERNING, FontKerning.NO);
            if (fontKerning == FontKerning.YES) {
                TypographyUtils.applyKerning(font.getFontProgram(), text, sequenceId, metaInfo);
            }

            otfFeaturesApplied = true;
        }
    }

    @Override
    public void draw(DrawContext drawContext) {
        if (occupiedArea == null) {
            Logger logger = LoggerFactory.getLogger(TextRenderer.class);
            logger.error(MessageFormatUtil.format(IoLogMessageConstant.OCCUPIED_AREA_HAS_NOT_BEEN_INITIALIZED,
                    "Drawing won't be performed."));
            return;
        }

        // Set up marked content before super.draw so that annotations are placed within marked content
        boolean isTagged = drawContext.isTaggingEnabled();
        LayoutTaggingHelper taggingHelper = null;
        boolean isArtifact = false;
        TagTreePointer tagPointer = null;
        if (isTagged) {
            taggingHelper = this.<LayoutTaggingHelper>getProperty(Property.TAGGING_HELPER);
            if (taggingHelper == null) {
                isArtifact = true;
            } else {
                isArtifact = taggingHelper.isArtifact(this);
                if (!isArtifact) {
                    tagPointer = taggingHelper.useAutoTaggingPointerAndRememberItsPosition(this);
                    if (taggingHelper.createTag(this, tagPointer)) {
                        tagPointer.getProperties().addAttributes(0, AccessibleAttributesApplier.getLayoutAttributes(this, tagPointer));
                    }
                }
            }
        }

        super.draw(drawContext);

        boolean isRelativePosition = isRelativePosition();
        if (isRelativePosition) {
            applyRelativePositioningTranslation(false);
        }

        float leftBBoxX = getInnerAreaBBox().getX();

        if (line.getEnd() > line.getStart() || savedWordBreakAtLineEnding != null) {
            UnitValue fontSize = this.getPropertyAsUnitValue(Property.FONT_SIZE);
            if (!fontSize.isPointValue()) {
                Logger logger = LoggerFactory.getLogger(TextRenderer.class);
                logger.error(MessageFormatUtil.format(IoLogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED,
                        Property.FONT_SIZE));
            }
            TransparentColor fontColor = getPropertyAsTransparentColor(Property.FONT_COLOR);
            Integer textRenderingMode = this.<Integer>getProperty(Property.TEXT_RENDERING_MODE);
            Float textRise = this.getPropertyAsFloat(Property.TEXT_RISE);
            Float characterSpacing = this.getPropertyAsFloat(Property.CHARACTER_SPACING);
            Float wordSpacing = this.getPropertyAsFloat(Property.WORD_SPACING);
            Float horizontalScaling = this.<Float>getProperty(Property.HORIZONTAL_SCALING);
            float[] skew = this.<float[]>getProperty(Property.SKEW);
            boolean italicSimulation = Boolean.TRUE.equals(getPropertyAsBoolean(Property.ITALIC_SIMULATION));
            boolean boldSimulation = Boolean.TRUE.equals(getPropertyAsBoolean(Property.BOLD_SIMULATION));
            Float strokeWidth = null;

            if (boldSimulation) {
                textRenderingMode = PdfCanvasConstants.TextRenderingMode.FILL_STROKE;
                strokeWidth = fontSize.getValue() / 30;
            }

            PdfCanvas canvas = drawContext.getCanvas();
            if (isTagged) {
                if (isArtifact) {
                    canvas.openTag(new CanvasArtifact());
                } else {
                    canvas.openTag(tagPointer.getTagReference());
                }
            }
            beginElementOpacityApplying(drawContext);
            canvas.saveState().beginText().setFontAndSize(font, fontSize.getValue());

            float verticalScale = (float) this.getPropertyAsFloat(Property.VERTICAL_SCALING, 1f);
            if (skew != null && skew.length == 2) {
                canvas.setTextMatrix(1, skew[0], skew[1], verticalScale, leftBBoxX, getYLine());
            } else if (italicSimulation) {
                canvas.setTextMatrix(1, 0, ITALIC_ANGLE, verticalScale, leftBBoxX, getYLine());
            } else if (Math.abs(verticalScale - 1) < EPS) {
                canvas.moveText(leftBBoxX, getYLine());
            } else {
                canvas.setTextMatrix(1, 0, 0, verticalScale, leftBBoxX, getYLine());
            }

            if (textRenderingMode != PdfCanvasConstants.TextRenderingMode.FILL) {
                canvas.setTextRenderingMode((int) textRenderingMode);
            }
            if (textRenderingMode == PdfCanvasConstants.TextRenderingMode.STROKE || textRenderingMode == PdfCanvasConstants.TextRenderingMode.FILL_STROKE) {
                if (strokeWidth == null) {
                    strokeWidth = this.getPropertyAsFloat(Property.STROKE_WIDTH);
                }
                if (strokeWidth != null && strokeWidth != 1f) {
                    canvas.setLineWidth((float) strokeWidth);
                }
                Color strokeColor = getPropertyAsColor(Property.STROKE_COLOR);
                if (strokeColor == null && fontColor != null) {
                    strokeColor = fontColor.getColor();
                }
                if (strokeColor != null) {
                    canvas.setStrokeColor(strokeColor);
                }
            }
            if (fontColor != null) {
                canvas.setFillColor(fontColor.getColor());
                fontColor.applyFillTransparency(canvas);
            }
            if (textRise != null && textRise != 0) {
                canvas.setTextRise((float) textRise);
            }
            if (characterSpacing != null && characterSpacing != 0) {
                canvas.setCharacterSpacing((float) characterSpacing);
            }
            if (wordSpacing != null && wordSpacing != 0) {
                if (font instanceof PdfType0Font) {
                    // From the spec: Word spacing is applied to every occurrence of the single-byte character code 32 in
                    // a string when using a simple font or a composite font that defines code 32 as a single-byte code.
                    // It does not apply to occurrences of the byte value 32 in multiple-byte codes.
                    //
                    // For PdfType0Font we must add word manually with glyph offsets
                    for (int gInd = line.getStart(); gInd < line.getEnd(); gInd++) {
                        if (TextUtil.isUni0020(line.get(gInd))) {
                            final short advance = (short) (FontProgram.convertGlyphSpaceToTextSpace((float) wordSpacing)
                                    / fontSize.getValue());
                            Glyph copy = new Glyph(line.get(gInd));
                            copy.setXAdvance(advance);
                            line.set(gInd, copy);
                        }
                    }
                } else {
                    canvas.setWordSpacing((float) wordSpacing);
                }
            }
            if (horizontalScaling != null && horizontalScaling != 1) {
                canvas.setHorizontalScaling((float) horizontalScaling * 100);
            }

            GlyphLine.IGlyphLineFilter filter = new CustomGlyphLineFilter();

            boolean appearanceStreamLayout = Boolean.TRUE.equals(getPropertyAsBoolean(Property.APPEARANCE_STREAM_LAYOUT));

            if (getReversedRanges() != null) {
                boolean writeReversedChars = !appearanceStreamLayout;
                ArrayList<Integer> removedIds = new ArrayList<>();
                for (int i = line.getStart(); i < line.getEnd(); i++) {
                    if (!filter.accept(line.get(i))) {
                        removedIds.add(i);
                    }
                }
                for (int[] range : getReversedRanges()) {
                    updateRangeBasedOnRemovedCharacters(removedIds, range);
                }
                line = line.filter(filter);
                if (writeReversedChars) {
                    canvas.showText(line, new ReversedCharsIterator(reversedRanges, line).
                            setUseReversed(true));
                } else {
                    canvas.showText(line);
                }
            } else {
                if (appearanceStreamLayout) {
                    line.setActualText(line.getStart(), line.getEnd(), null);
                }
                canvas.showText(line.filter(filter));
            }
            if (savedWordBreakAtLineEnding != null) {
                canvas.showText(savedWordBreakAtLineEnding);
            }

            canvas.endText().restoreState();
            endElementOpacityApplying(drawContext);

            if (isTagged) {
                canvas.closeTag();
            }

            Object underlines = this.<Object>getProperty(Property.UNDERLINE);
            if (underlines instanceof List) {
                for (Object underline : (List) underlines) {
                    if (underline instanceof Underline) {
                        drawAndTagSingleUnderline(drawContext.isTaggingEnabled(), (Underline) underline, fontColor, canvas, fontSize.getValue(), italicSimulation ? ITALIC_ANGLE : 0);
                    }
                }
            } else if (underlines instanceof Underline) {
                drawAndTagSingleUnderline(drawContext.isTaggingEnabled(), (Underline) underlines, fontColor, canvas, fontSize.getValue(), italicSimulation ? ITALIC_ANGLE : 0);
            }
        }

        if (isRelativePosition) {
            applyRelativePositioningTranslation(false);
        }

        if (isTagged && !isArtifact) {
            if (isLastRendererForModelElement) {
                taggingHelper.finishTaggingHint(this);
            }
            taggingHelper.restoreAutoTaggingPointerPosition(this);
        }
    }

    /**
     * Trims any whitespace characters from the start of the {@link GlyphLine}
     * to be rendered.
     */
    public void trimFirst() {
        updateFontAndText();

        if (text != null) {
            Glyph glyph;
            while (text.getStart() < text.getEnd()
                    && TextUtil.isWhitespace(glyph = text.get(text.getStart())) && !TextUtil.isNewLine(glyph)) {
                text.setStart(text.getStart() +1);
            }
        }

        /*  Between two sentences separated by one or more whitespaces,
            icu allows to break right after the last whitespace.
            Therefore we need to carefully edit specialScriptsWordBreakPoints list after trimming:
            if a break is allowed to happen right before the first glyph of an already trimmed text,
            we need to remove this point from the list
            (or replace it with -1 thus marking that text contains special scripts,
             in case if the removed break point was the only possible break point).
         */
        if (textContainsSpecialScriptGlyphs(true)
                && specialScriptsWordBreakPoints.get(0) == text.getStart()) {
            if (specialScriptsWordBreakPoints.size() == 1) {
                specialScriptsWordBreakPoints.set(0, -1);
            } else {
                specialScriptsWordBreakPoints.remove(0);
            }
        }
    }

    float trimLast() {
        float trimmedSpace = 0;

        if (line.getEnd() <= 0)
            return trimmedSpace;

        UnitValue fontSize = (UnitValue) this.getPropertyAsUnitValue(Property.FONT_SIZE);
        if (!fontSize.isPointValue()) {
            Logger logger = LoggerFactory.getLogger(TextRenderer.class);
            logger.error(MessageFormatUtil.format(IoLogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED,
                    Property.FONT_SIZE));
        }
        Float characterSpacing = this.getPropertyAsFloat(Property.CHARACTER_SPACING);
        Float wordSpacing = this.getPropertyAsFloat(Property.WORD_SPACING);
        float hScale = (float) this.getPropertyAsFloat(Property.HORIZONTAL_SCALING, 1f);

        int firstNonSpaceCharIndex = line.getEnd() - 1;
        while (firstNonSpaceCharIndex >= line.getStart()) {
            Glyph currentGlyph = line.get(firstNonSpaceCharIndex);
            if (!TextUtil.isWhitespace(currentGlyph)) {
                break;
            }
            saveWordBreakIfNotYetSaved(currentGlyph);

            final float currentCharWidth = FontProgram.convertTextSpaceToGlyphSpace(
                    getCharWidth(currentGlyph, fontSize.getValue(), hScale, characterSpacing, wordSpacing));
            final float xAdvance = firstNonSpaceCharIndex > line.getStart() ? FontProgram.convertTextSpaceToGlyphSpace(
                    scaleXAdvance(line.get(firstNonSpaceCharIndex - 1).getXAdvance(), fontSize.getValue(), hScale)) : 0;
            trimmedSpace += currentCharWidth - xAdvance;
            occupiedArea.getBBox().setWidth(occupiedArea.getBBox().getWidth() - currentCharWidth);

            firstNonSpaceCharIndex--;
        }

        line.setEnd(firstNonSpaceCharIndex + 1);

        return trimmedSpace;
    }

    /**
     * Gets the maximum offset above the base line that this Text extends to.
     *
     * @return the upwards vertical offset of this {@link Text}
     */
    @Override
    public float getAscent() {
        return yLineOffset;
    }

    /**
     * Gets the maximum offset below the base line that this Text extends to.
     *
     * @return the downwards vertical offset of this {@link Text}
     */
    @Override
    public float getDescent() {
        return -(getOccupiedAreaBBox().getHeight() - yLineOffset - (float) this.getPropertyAsFloat(Property.TEXT_RISE));
    }

    /**
     * Gets the position on the canvas of the imaginary horizontal line upon which
     * the {@link Text}'s contents will be written.
     *
     * @return the y position of this text on the {@link DrawContext}
     */
    public float getYLine() {
        return occupiedArea.getBBox().getY() + occupiedArea.getBBox().getHeight() - yLineOffset - (float) this.getPropertyAsFloat(Property.TEXT_RISE);
    }

    /**
     * Moves the vertical position to the parameter's value.
     *
     * @param y the new vertical position of the Text
     */
    public void moveYLineTo(float y) {
        float curYLine = getYLine();
        float delta = y - curYLine;
        occupiedArea.getBBox().setY(occupiedArea.getBBox().getY() + delta);
    }

    /**
     * Manually sets the contents of the Text's representation on the canvas,
     * regardless of the Text's own contents.
     *
     * @param text the replacement text
     */
    public void setText(String text) {
        strToBeConverted = text;
        //strToBeConverted will be null after next method.
        updateFontAndText();
    }

    /**
     * Manually set a GlyphLine and PdfFont for rendering.
     *
     * @param text the {@link GlyphLine}
     * @param font the font
     */
    public void setText(GlyphLine text, PdfFont font) {
        GlyphLine newText = new GlyphLine(text);
        newText = TextPreprocessingUtil.replaceSpecialWhitespaceGlyphs(newText, font);
        setProcessedGlyphLineAndFont(newText, font);
    }

    public GlyphLine getText() {
        updateFontAndText();
        return text;
    }

    /**
     * The length of the whole text assigned to this renderer.
     *
     * @return the text length
     */
    public int length() {
        return text == null ? 0 : text.getEnd() - text.getStart();
    }

    @Override
    public String toString() {
        return line != null ? line.toString() : null;
    }

    /**
     * Gets char code at given position for the text belonging to this renderer.
     *
     * @param pos the position in range [0; length())
     * @return Unicode char code
     */
    public int charAt(int pos) {
        return text.get(pos + text.getStart()).getUnicode();
    }

    public float getTabAnchorCharacterPosition() {
        return tabAnchorCharacterPosition;
    }

    /**
     * Gets a new instance of this class to be used as a next renderer, after this renderer is used, if
     * {@link #layout(LayoutContext)} is called more than once.
     *
     * <p>
     * If {@link TextRenderer} overflows to the next line, iText uses this method to create a renderer
     * for the overflow part. So if one wants to extend {@link TextRenderer}, one should override
     * this method: otherwise the default method will be used and thus the default rather than the custom
     * renderer will be created. Another method that should be overridden in case of
     * {@link TextRenderer}'s extension is {@link #createCopy(GlyphLine, PdfFont)}. This method is responsible
     * for creation of {@link TextRenderer}'s copies, which represent its parts of specific font.
     * @return new renderer instance
     */
    @Override
    public IRenderer getNextRenderer() {
        logWarningIfGetNextRendererNotOverridden(TextRenderer.class, this.getClass());
        return new TextRenderer((Text) modelElement);
    }

    /**
     * Get ascender and descender from font metrics.
     * If these values are obtained from typo metrics they are normalized with a scale coefficient.
     *
     * @param font from which metrics will be extracted
     * @return array in which the first element is an ascender and the second is a descender
     */
    public static float[] calculateAscenderDescender(PdfFont font) {
        return calculateAscenderDescender(font, RenderingMode.DEFAULT_LAYOUT_MODE);
    }

    /**
     * Get ascender and descender from font metrics.
     * In RenderingMode.DEFAULT_LAYOUT_MODE if these values are obtained from typo metrics they are normalized with a scale coefficient.
     *
     * @param font from which metrics will be extracted
     * @param mode mode in which metrics will be obtained. Impact on the use of scale coefficient
     * @return array in which the first element is an ascender and the second is a descender
     */
    public static float[] calculateAscenderDescender(PdfFont font, RenderingMode mode) {
        FontMetrics fontMetrics = font.getFontProgram().getFontMetrics();
        float ascender;
        float descender;
        float usedTypoAscenderScaleCoeff = TYPO_ASCENDER_SCALE_COEFF;
        if (RenderingMode.HTML_MODE.equals(mode) && !(font instanceof PdfType1Font)) {
            usedTypoAscenderScaleCoeff = 1;
        }
        if (fontMetrics.getWinAscender() == 0 || fontMetrics.getWinDescender() == 0 ||
                fontMetrics.getTypoAscender() == fontMetrics.getWinAscender()
                        && fontMetrics.getTypoDescender() == fontMetrics.getWinDescender()) {
            ascender = fontMetrics.getTypoAscender() * usedTypoAscenderScaleCoeff;
            descender = fontMetrics.getTypoDescender() * usedTypoAscenderScaleCoeff;
        } else {
            ascender = fontMetrics.getWinAscender();
            descender = fontMetrics.getWinDescender();
        }
        return new float[] {ascender, descender};
    }

    List<int[]> getReversedRanges() {
        return reversedRanges;
    }

    List<int[]> initReversedRanges() {
        if (reversedRanges == null) {
            reversedRanges = new ArrayList<>();
        }
        return reversedRanges;
    }

    TextRenderer removeReversedRanges() {
        reversedRanges = null;
        return this;
    }

    private TextRenderer[] splitIgnoreFirstNewLine(int currentTextPos) {
        if (TextUtil.isCarriageReturnFollowedByLineFeed(text, currentTextPos)) {
            return split(currentTextPos + 2);
        } else {
            return split(currentTextPos + 1);
        }
    }

    private GlyphLine convertToGlyphLine(String text) {
        return font.createGlyphLine(text);
    }

    private boolean hasOtfFont() {
        return font instanceof PdfType0Font && font.getFontProgram() instanceof TrueTypeFont;
    }

    /**
     * Analyzes/checks whether {@link TextRenderer#text}, bounded by start and end,
     * contains glyphs belonging to special script.
     *
     * Mind that the behavior of this method depends on the analyzeSpecialScriptsWordBreakPointsOnly parameter:
     * - pass {@code false} if you need to analyze the {@link TextRenderer#text} by checking each of its glyphs
     * AND to fill {@link TextRenderer#specialScriptsWordBreakPoints} list afterwards,
     * i.e. when analyzing a sequence of TextRenderers prior to layouting;
     * - pass {@code true} if you want to check if text contains glyphs belonging to special scripts,
     * according to the already filled {@link TextRenderer#specialScriptsWordBreakPoints} list.
     *
     * @param analyzeSpecialScriptsWordBreakPointsOnly false if analysis of each glyph is required,
     *                                                 true if analysis has already been performed earlier
     *                                                 and the results are stored in {@link TextRenderer#specialScriptsWordBreakPoints}
     * @return true if {@link TextRenderer#text}, bounded by start and end, contains glyphs belonging to special script, otherwise false
     * @see TextRenderer#specialScriptsWordBreakPoints
     */
    boolean textContainsSpecialScriptGlyphs(boolean analyzeSpecialScriptsWordBreakPointsOnly) {
        if (specialScriptsWordBreakPoints != null) {
            return !specialScriptsWordBreakPoints.isEmpty();
        }

        if (analyzeSpecialScriptsWordBreakPointsOnly) {
            return false;
        }

        ISplitCharacters splitCharacters = this.<ISplitCharacters>getProperty(Property.SPLIT_CHARACTERS);

        if (splitCharacters instanceof BreakAllSplitCharacters) {
            specialScriptsWordBreakPoints = new ArrayList<>();
        }

        for (int i = text.getStart(); i < text.getEnd(); i++) {
            int unicode = text.get(i).getUnicode();
            if (unicode > -1) {
                if (codePointIsOfSpecialScript(unicode)) {
                    return true;
                }
            } else {
                char[] chars = text.get(i).getChars();
                if (chars != null) {
                    for (char ch : chars) {
                        if (codePointIsOfSpecialScript(ch)) {
                            return true;
                        }
                    }
                }
            }
        }
        // if we've reached this point, it means we've analyzed the entire TextRenderer#text
        // and haven't found special scripts, therefore we define specialScriptsWordBreakPoints
        // as an empty list to mark, it's already been analyzed
        specialScriptsWordBreakPoints = new ArrayList<>();

        return false;
    }

    void setSpecialScriptsWordBreakPoints(List<Integer> specialScriptsWordBreakPoints) {
        this.specialScriptsWordBreakPoints = specialScriptsWordBreakPoints;
    }

    List<Integer> getSpecialScriptsWordBreakPoints() {
        return this.specialScriptsWordBreakPoints;
    }

    void setSpecialScriptFirstNotFittingIndex(int lastFittingIndex) {
        this.specialScriptFirstNotFittingIndex = lastFittingIndex;
    }

    int getSpecialScriptFirstNotFittingIndex() {
        return specialScriptFirstNotFittingIndex;
    }

    void setIndexOfFirstCharacterToBeForcedToOverflow(int indexOfFirstCharacterToBeForcedToOverflow) {
        this.indexOfFirstCharacterToBeForcedToOverflow = indexOfFirstCharacterToBeForcedToOverflow;
    }

    @Override
    protected Rectangle getBackgroundArea(Rectangle occupiedAreaWithMargins) {
        float textRise = (float) this.getPropertyAsFloat(Property.TEXT_RISE);
        return occupiedAreaWithMargins.moveUp(textRise).decreaseHeight(textRise);
    }

    @Override
    protected Float getFirstYLineRecursively() {
        return getYLine();
    }

    @Override
    protected Float getLastYLineRecursively() {
        return getYLine();
    }

    /**
     * Returns the length of the {@link com.itextpdf.layout.renderer.TextRenderer#line line} which is the result of the layout call.
     *
     * @return the length of the line
     */
    protected int lineLength() {
        return line.getEnd() > 0 ? line.getEnd() - line.getStart() : 0;
    }

    protected int baseCharactersCount() {
        int count = 0;
        for (int i = line.getStart(); i < line.getEnd(); i++) {
            Glyph glyph = line.get(i);
            if (!glyph.hasPlacement()) {
                count++;
            }
        }
        return count;
    }

    @Override
    public MinMaxWidth getMinMaxWidth() {
        TextLayoutResult result = (TextLayoutResult) layout(new LayoutContext(new LayoutArea(1, new Rectangle(MinMaxWidthUtils.getInfWidth(), AbstractRenderer.INF))));
        return result.getMinMaxWidth();
    }

    protected int getNumberOfSpaces() {
        if (line.getEnd() <= 0)
            return 0;
        int spaces = 0;
        for (int i = line.getStart(); i < line.getEnd(); i++) {
            Glyph currentGlyph = line.get(i);
            if (currentGlyph.getUnicode() == ' ') {
                spaces++;
            }
        }
        return spaces;
    }

    protected TextRenderer createSplitRenderer() {
        return (TextRenderer) getNextRenderer();
    }

    protected TextRenderer createOverflowRenderer() {
        return (TextRenderer) getNextRenderer();
    }

    protected TextRenderer[] split(int initialOverflowTextPos) {
        TextRenderer splitRenderer = createSplitRenderer();
        GlyphLine newText = new GlyphLine(text);
        newText.setStart(text.getStart());
        newText.setEnd(initialOverflowTextPos);
        splitRenderer.setProcessedGlyphLineAndFont(newText, font);
        splitRenderer.line = line;
        splitRenderer.occupiedArea = occupiedArea.clone();
        splitRenderer.parent = parent;
        splitRenderer.yLineOffset = yLineOffset;
        splitRenderer.otfFeaturesApplied = otfFeaturesApplied;
        splitRenderer.isLastRendererForModelElement = false;
        splitRenderer.addAllProperties(getOwnProperties());

        TextRenderer overflowRenderer = createOverflowRenderer();
        newText = new GlyphLine(text);
        newText.setStart(initialOverflowTextPos);
        newText.setEnd(text.getEnd());
        overflowRenderer.setProcessedGlyphLineAndFont(newText, font);
        overflowRenderer.otfFeaturesApplied = otfFeaturesApplied;
        overflowRenderer.parent = parent;
        overflowRenderer.addAllProperties(getOwnProperties());

        if (specialScriptsWordBreakPoints != null) {
            if (specialScriptsWordBreakPoints.isEmpty()) {
                splitRenderer.setSpecialScriptsWordBreakPoints(new ArrayList<Integer>());
                overflowRenderer.setSpecialScriptsWordBreakPoints(new ArrayList<Integer>());
            } else if (specialScriptsWordBreakPoints.get(0) == -1) {
                List<Integer> split = new ArrayList<Integer>(1);
                split.add(-1);
                splitRenderer.setSpecialScriptsWordBreakPoints(split);

                List<Integer> overflow = new ArrayList<Integer>(1);
                overflow.add(-1);
                overflowRenderer.setSpecialScriptsWordBreakPoints(overflow);
            } else {
                int splitIndex = findPossibleBreaksSplitPosition(specialScriptsWordBreakPoints, initialOverflowTextPos,
                        false);

                if (splitIndex > -1) {
                    splitRenderer.setSpecialScriptsWordBreakPoints(specialScriptsWordBreakPoints
                            .subList(0, splitIndex + 1));
                } else {
                    List<Integer> split = new ArrayList<Integer>(1);
                    split.add(-1);
                    splitRenderer.setSpecialScriptsWordBreakPoints(split);
                }

                if (splitIndex + 1 < specialScriptsWordBreakPoints.size()) {
                    overflowRenderer.setSpecialScriptsWordBreakPoints(specialScriptsWordBreakPoints
                            .subList(splitIndex + 1, specialScriptsWordBreakPoints.size()));
                } else {
                    List<Integer> split = new ArrayList<Integer>(1);
                    split.add(-1);
                    overflowRenderer.setSpecialScriptsWordBreakPoints(split);
                }
            }
        }

        return new TextRenderer[]{splitRenderer, overflowRenderer};
    }

    protected void drawSingleUnderline(Underline underline, TransparentColor fontStrokeColor, PdfCanvas canvas, float fontSize, float italicAngleTan) {
        TransparentColor underlineColor = underline.getColor() != null ? new TransparentColor(underline.getColor(), underline.getOpacity()) : fontStrokeColor;
        canvas.saveState();

        if (underlineColor != null) {
            canvas.setStrokeColor(underlineColor.getColor());
            underlineColor.applyStrokeTransparency(canvas);
        }
        canvas.setLineCapStyle(underline.getLineCapStyle());
        float underlineThickness = underline.getThickness(fontSize);
        if (underlineThickness != 0) {
            canvas.setLineWidth(underlineThickness);
            float yLine = getYLine();
            float underlineYPosition = underline.getYPosition(fontSize) + yLine;
            float italicWidthSubstraction = .5f * fontSize * italicAngleTan;
            Rectangle innerAreaBbox = getInnerAreaBBox();
            canvas.moveTo(innerAreaBbox.getX(), underlineYPosition).
                    lineTo(innerAreaBbox.getX() + innerAreaBbox.getWidth() - italicWidthSubstraction, underlineYPosition).
                    stroke();
        }

        canvas.restoreState();
    }

    protected float calculateLineWidth() {
        UnitValue fontSize = this.getPropertyAsUnitValue(Property.FONT_SIZE);
        if (!fontSize.isPointValue()) {
            Logger logger = LoggerFactory.getLogger(TextRenderer.class);
            logger.error(MessageFormatUtil.format(IoLogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED, Property.FONT_SIZE));
        }
        return getGlyphLineWidth(line, fontSize.getValue(),
                (float) this.getPropertyAsFloat(Property.HORIZONTAL_SCALING, 1f),
                this.getPropertyAsFloat(Property.CHARACTER_SPACING), this.getPropertyAsFloat(Property.WORD_SPACING));
    }

    /**
     * Resolve {@link Property#FONT} String[] value.
     *
     * @param addTo add all processed renderers to.
     * @return true, if new {@link TextRenderer} has been created.
     */
    protected boolean resolveFonts(List<IRenderer> addTo) {
        Object font = this.<Object>getProperty(Property.FONT);
        if (font instanceof PdfFont) {
            addTo.add(this);
            return false;
        } else if (font instanceof String[]) {
            FontProvider provider = this.<FontProvider>getProperty(Property.FONT_PROVIDER);
            FontSet fontSet = this.<FontSet>getProperty(Property.FONT_SET);
            if (provider.getFontSet().isEmpty() && (fontSet == null || fontSet.isEmpty())) {
                throw new IllegalStateException(
                        LayoutExceptionMessageConstant.FONT_PROVIDER_NOT_SET_FONT_FAMILY_NOT_RESOLVED);
            }
            // process empty renderers because they can have borders or paddings with background to be drawn
            if (null == strToBeConverted || strToBeConverted.isEmpty()) {
                addTo.add(this);
            } else {
                FontCharacteristics fc = createFontCharacteristics();
                IFontSelectorStrategy strategy = provider.createFontSelectorStrategy(Arrays.asList((String[])font), fc, fontSet);
                List<Tuple2<GlyphLine, PdfFont>> subTextWithFont = strategy.getGlyphLines(strToBeConverted);
                for (Tuple2<GlyphLine, PdfFont> subText : subTextWithFont) {
                    TextRenderer textRenderer = createCopy(subText.getFirst(), subText.getSecond());
                    addTo.add(textRenderer);
                }
            }
            return true;
        } else {
            throw new IllegalStateException(LayoutExceptionMessageConstant.INVALID_FONT_PROPERTY_VALUE);
        }
    }

    protected void setProcessedGlyphLineAndFont(GlyphLine gl, PdfFont font) {
        this.text = gl;
        this.font = font;
        this.otfFeaturesApplied = false;
        this.strToBeConverted = null;
        this.specialScriptsWordBreakPoints = null;
        setProperty(Property.FONT, font);
    }

    /**
     * Creates a copy of this {@link TextRenderer}, which corresponds to the passed {@link GlyphLine}
     * with {@link PdfFont}.
     * <p>
     * While processing {@link TextRenderer}, iText uses this method to create {@link GlyphLine glyph lines}
     * of specific {@link PdfFont fonts}, which represent the {@link TextRenderer}'s parts. If one extends
     * {@link TextRenderer}, one should override this method, otherwise if {@link com.itextpdf.layout.font.FontSelector}
     * related logic is triggered, copies of this {@link TextRenderer} will have the default behavior rather than
     * the custom one.
     * @param gl a {@link GlyphLine} which represents some of this {@link TextRenderer}'s content
     * @param font a {@link PdfFont} for this part of the {@link TextRenderer}'s content
     * @return copy of this {@link TextRenderer}, which correspond to the passed {@link GlyphLine} with {@link PdfFont}
     */
    protected TextRenderer createCopy(GlyphLine gl, PdfFont font) {
        if (TextRenderer.class != this.getClass()) {
            Logger logger = LoggerFactory.getLogger(TextRenderer.class);
            logger.error(MessageFormatUtil.format(IoLogMessageConstant.CREATE_COPY_SHOULD_BE_OVERRIDDEN));
        }
        TextRenderer copy = new TextRenderer(this);
        copy.setProcessedGlyphLineAndFont(gl, font);
        return copy;
    }

    static void updateRangeBasedOnRemovedCharacters(ArrayList<Integer> removedIds, int[] range) {
        int shift = numberOfElementsLessThan(removedIds, range[0]);
        range[0] -= shift;
        shift = numberOfElementsLessThanOrEqual(removedIds, range[1]);
        range[1] -= shift;
    }
    // if amongPresentOnly is true,
    // returns the index of lists's element which equals textStartBasedInitialOverflowTextPos
    // or -1 if textStartBasedInitialOverflowTextPos wasn't found in the list.
    // if amongPresentOnly is false, returns the index of list's element
    // that is not greater than textStartBasedInitialOverflowTextPos
    // if there's no such element in the list, -1 is returned
    static int findPossibleBreaksSplitPosition(List<Integer> list, int textStartBasedInitialOverflowTextPos,
            boolean amongPresentOnly) {
        int low = 0;
        int high = list.size() - 1;

        while (low <= high) {
            int middle = (low + high) >>> 1;
            if (list.get(middle).compareTo(textStartBasedInitialOverflowTextPos) < 0) {
                low = middle + 1;
            } else if (list.get(middle).compareTo(textStartBasedInitialOverflowTextPos) > 0) {
                high = middle - 1;
            } else {
                return middle;
            }
        }
        if (!amongPresentOnly && low > 0) {
            return low - 1;
        }
        return -1;
    }

    static boolean codePointIsOfSpecialScript(int codePoint) {
        Character.UnicodeScript glyphScript = Character.UnicodeScript.of(codePoint);
        return Character.UnicodeScript.THAI == glyphScript
                || Character.UnicodeScript.KHMER == glyphScript
                || Character.UnicodeScript.LAO == glyphScript
                || Character.UnicodeScript.MYANMAR == glyphScript;
    }

    @Override
    PdfFont resolveFirstPdfFont(String[] font, FontProvider provider, FontCharacteristics fc, FontSet additionalFonts) {
        IFontSelectorStrategy strategy = provider.createFontSelectorStrategy(Arrays.asList(font), fc, additionalFonts);
        // Try to find first font that can render at least one glyph.
        final List<Tuple2<GlyphLine, PdfFont>> glyphLines = strategy.getGlyphLines(strToBeConverted);
        if (!glyphLines.isEmpty()) {
            return glyphLines.get(0).getSecond();
        }
        return super.resolveFirstPdfFont(font, provider, fc, additionalFonts);
    }

    /**
     * Identifies two properties for the layouted text renderer text: start and end break possibilities.
     * First - if it ends with split character, second - if it starts with the split character
     * which is at the same time is a whitespace character. These properties will later be used for identifying
     * if we can consider this and previous/next text renderers chunks to be a part of a single word spanning across
     * the text renderers boundaries. In the start of the text renderer we only care about split characters, which are
     * white spaces, because only such will allow soft-breaks before them: normally split characters allow breaks only
     * after them.
     *
     * @param splitCharacters current renderer {@link ISplitCharacters} property value
     * @return a boolean array of two elements, where first element identifies start break possibility, and second - end
     *         break possibility.
     */
    boolean[] isStartsWithSplitCharWhiteSpaceAndEndsWithSplitChar(ISplitCharacters splitCharacters) {
        boolean startsWithBreak = line.getStart() < line.getEnd()
                && splitCharacters.isSplitCharacter(text, line.getStart())
                && TextUtil.isSpaceOrWhitespace(text.get(line.getStart()));
        boolean endsWithBreak = line.getStart() < line.getEnd()
                && splitCharacters.isSplitCharacter(text, line.getEnd() - 1);
        if (specialScriptsWordBreakPoints == null || specialScriptsWordBreakPoints.isEmpty()) {
            return new boolean[]{startsWithBreak, endsWithBreak};
        } else {
            if (!endsWithBreak) {
                endsWithBreak = specialScriptsWordBreakPoints.contains(line.getEnd());
            }
            return new boolean[]{startsWithBreak, endsWithBreak};
        }
    }

    private void drawAndTagSingleUnderline(boolean isTagged, Underline underline,
                                           TransparentColor fontStrokeColor, PdfCanvas canvas,
                                           float fontSize, float italicAngleTan) {
        if (isTagged) {
            canvas.openTag(new CanvasArtifact());
        }
        drawSingleUnderline(underline, fontStrokeColor, canvas, fontSize, italicAngleTan);
        if (isTagged) {
            canvas.closeTag();
        }
    }

    private float getCharWidth(Glyph g, float fontSize, Float hScale, Float characterSpacing, Float wordSpacing) {
        if (hScale == null)
            hScale = 1f;

        float resultWidth = g.getWidth() * fontSize * (float) hScale;
        if (characterSpacing != null) {
            resultWidth += FontProgram.convertGlyphSpaceToTextSpace((float) characterSpacing * (float) hScale);
        }
        if (wordSpacing != null && g.getUnicode() == ' ') {
            resultWidth += FontProgram.convertGlyphSpaceToTextSpace((float) wordSpacing * (float) hScale);
        }
        return resultWidth;
    }

    private float scaleXAdvance(float xAdvance, float fontSize, Float hScale) {
        return xAdvance * fontSize * (float) hScale;
    }

    private float getGlyphLineWidth(GlyphLine glyphLine, float fontSize, float hScale, Float characterSpacing, Float wordSpacing) {
        float width = 0;
        for (int i = glyphLine.getStart(); i < glyphLine.getEnd(); i++) {
            if (!noPrint(glyphLine.get(i))) {
                float charWidth = getCharWidth(glyphLine.get(i), fontSize, hScale, characterSpacing, wordSpacing);
                width += charWidth;
                float xAdvance = (i != glyphLine.getStart()) ? scaleXAdvance(glyphLine.get(i - 1).getXAdvance(),
                        fontSize, hScale) : 0;
                width += xAdvance;
            }
        }
        return FontProgram.convertTextSpaceToGlyphSpace(width);
    }

    private int[] getWordBoundsForHyphenation(GlyphLine text, int leftTextPos, int rightTextPos, int wordMiddleCharPos) {
        while (wordMiddleCharPos >= leftTextPos && !isGlyphPartOfWordForHyphenation(text.get(wordMiddleCharPos))
                && !TextUtil.isUni0020(text.get(wordMiddleCharPos))) {
            wordMiddleCharPos--;
        }
        if (wordMiddleCharPos >= leftTextPos) {
            int left = wordMiddleCharPos;
            while (left >= leftTextPos && isGlyphPartOfWordForHyphenation(text.get(left))) {
                left--;
            }
            int right = wordMiddleCharPos;
            while (right < rightTextPos && isGlyphPartOfWordForHyphenation(text.get(right))) {
                right++;
            }
            return new int[]{left + 1, right};
        } else {
            return null;
        }
    }

    private boolean isGlyphPartOfWordForHyphenation(Glyph g) {
        return Character.isLetter((char) g.getUnicode())

                // soft hyphen
                || '\u00ad' == g.getUnicode();
    }

    private void updateFontAndText() {
        if (strToBeConverted != null) {
            PdfFont newFont;
            try {
                newFont = getPropertyAsFont(Property.FONT);
            } catch (ClassCastException cce) {
                newFont = resolveFirstPdfFont();
                if (!strToBeConverted.isEmpty()) {
                    Logger logger = LoggerFactory.getLogger(TextRenderer.class);
                    logger.error(IoLogMessageConstant.FONT_PROPERTY_MUST_BE_PDF_FONT_OBJECT);
                }
            }
            GlyphLine newText = newFont.createGlyphLine(strToBeConverted);
            newText = TextPreprocessingUtil.replaceSpecialWhitespaceGlyphs(newText, newFont);
            setProcessedGlyphLineAndFont(newText, newFont);
        }
    }

    private void saveWordBreakIfNotYetSaved(Glyph wordBreak) {
        if (savedWordBreakAtLineEnding == null) {
            if (TextUtil.isNewLine(wordBreak)) {

                // we don't want to print '\n' in content stream
                wordBreak = font.getGlyph('\u0020');
            }
            // it's word-break character at the end of the line, which we want to save after trimming
            savedWordBreakAtLineEnding = new GlyphLine(Collections.<Glyph>singletonList(wordBreak));
        }
    }

    private static int numberOfElementsLessThan(ArrayList<Integer> numbers, int n) {
        int x = Collections.binarySearch(numbers, n);
        if (x >= 0) {
            return x;
        } else {
            return -x - 1;
        }
    }

    private static int numberOfElementsLessThanOrEqual(ArrayList<Integer> numbers, int n) {
        int x = Collections.binarySearch(numbers, n);
        if (x >= 0) {
            return x + 1;
        } else {
            return -x - 1;
        }
    }

    private static boolean noPrint(Glyph g) {
        if (!g.hasValidUnicode()) {
            return false;
        }
        int c = g.getUnicode();
        return TextUtil.isNonPrintable(c);
    }

    private static boolean glyphBelongsToNonBreakingHyphenRelatedChunk(GlyphLine text, int ind) {
        return TextUtil.isNonBreakingHyphen(text.get(ind)) || (ind + 1 < text.getEnd() &&
                TextUtil.isNonBreakingHyphen(text.get(ind + 1))) || ind - 1 >= text.getStart() &&
                TextUtil.isNonBreakingHyphen(text.get(ind - 1));
    }

    private static class ReversedCharsIterator implements Iterator<GlyphLine.GlyphLinePart> {
        private List<Integer> outStart;
        private List<Integer> outEnd;
        private List<Boolean> reversed;
        private int currentInd = 0;
        private boolean useReversed;

        public ReversedCharsIterator(List<int[]> reversedRange, GlyphLine line) {
            outStart = new ArrayList<>();
            outEnd = new ArrayList<>();
            reversed = new ArrayList<>();
            if (reversedRange != null) {
                if (reversedRange.get(0)[0] > 0) {
                    outStart.add(0);
                    outEnd.add(reversedRange.get(0)[0]);
                    reversed.add(false);
                }
                for (int i = 0; i < reversedRange.size(); i++) {
                    int[] range = reversedRange.get(i);
                    outStart.add(range[0]);
                    outEnd.add(range[1] + 1);
                    reversed.add(true);
                    if (i != reversedRange.size() - 1) {
                        outStart.add(range[1] + 1);
                        outEnd.add(reversedRange.get(i + 1)[0]);
                        reversed.add(false);
                    }
                }
                int lastIndex = reversedRange.get(reversedRange.size() - 1)[1];
                if (lastIndex < line.size() - 1) {
                    outStart.add(lastIndex + 1);
                    outEnd.add(line.size());
                    reversed.add(false);
                }
            } else {
                outStart.add(line.getStart());
                outEnd.add(line.getEnd());
                reversed.add(false);
            }
        }

        public ReversedCharsIterator setUseReversed(boolean useReversed) {
            this.useReversed = useReversed;
            return this;
        }

        @Override
        public boolean hasNext() {
            return currentInd < outStart.size();
        }

        @Override
        public GlyphLine.GlyphLinePart next() {
            GlyphLine.GlyphLinePart part = new GlyphLine.GlyphLinePart(outStart.get(currentInd), outEnd.get(currentInd)).
                    setReversed(useReversed && reversed.get(currentInd));
            currentInd++;
            return part;
        }

        @Override
        public void remove() {
            throw new IllegalStateException("Operation not supported");
        }

    }

    private static class ScriptRange {
        Character.UnicodeScript script;
        int rangeEnd;

        ScriptRange(Character.UnicodeScript script, int rangeEnd) {
            this.script = script;
            this.rangeEnd = rangeEnd;
        }
    }

    private static final class CustomGlyphLineFilter implements GlyphLine.IGlyphLineFilter {
        @Override
        public boolean accept(Glyph glyph) {
            return !noPrint(glyph);
        }
    }
}
