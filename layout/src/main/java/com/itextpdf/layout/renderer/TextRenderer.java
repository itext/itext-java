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
import com.itextpdf.io.font.FontMetrics;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.TrueTypeFont;
import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.io.font.otf.GlyphLine;
import com.itextpdf.io.util.EnumUtil;
import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.io.util.TextUtil;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfType0Font;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.CanvasArtifact;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.font.FontCharacteristics;
import com.itextpdf.layout.font.FontFamilySplitter;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.layout.font.FontSelectorStrategy;
import com.itextpdf.layout.font.FontSet;
import com.itextpdf.layout.hyphenation.Hyphenation;
import com.itextpdf.layout.hyphenation.HyphenationConfig;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.layout.TextLayoutResult;
import com.itextpdf.layout.minmaxwidth.MinMaxWidth;
import com.itextpdf.layout.minmaxwidth.MinMaxWidthUtils;
import com.itextpdf.layout.property.Background;
import com.itextpdf.layout.property.BaseDirection;
import com.itextpdf.layout.property.FloatPropertyValue;
import com.itextpdf.layout.property.FontKerning;
import com.itextpdf.layout.property.OverflowPropertyValue;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.property.TransparentColor;
import com.itextpdf.layout.property.Underline;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.splitting.ISplitCharacters;
import com.itextpdf.layout.tagging.LayoutTaggingHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * This class represents the {@link IRenderer renderer} object for a {@link Text}
 * object. It will draw the glyphs of the textual content on the {@link DrawContext}.
 */
public class TextRenderer extends AbstractRenderer implements ILeafElementRenderer {

    protected static final float TEXT_SPACE_COEFF = FontProgram.UNITS_NORMALIZATION;
    private static final float ITALIC_ANGLE = 0.21256f;
    private static final float BOLD_SIMULATION_STROKE_COEFF = 1 / 30f;
    private static final float TYPO_ASCENDER_SCALE_COEFF = 1.2f;

    protected float yLineOffset;

    // font should be stored only during converting original string to GlyphLine, however now it's not true
    private PdfFont font;
    protected GlyphLine text;
    protected GlyphLine line;
    protected String strToBeConverted;

    protected boolean otfFeaturesApplied = false;

    protected float tabAnchorCharacterPosition = -1;

    protected List<int[]> reversedRanges;

    protected GlyphLine savedWordBreakAtLineEnding;

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
    }

    @Override
    public LayoutResult layout(LayoutContext layoutContext) {
        updateFontAndText();
        if (null != text) {
            // if text != null => font != null
            text = replaceSpecialWhitespaceGlyphs(text, font);
        }

        LayoutArea area = layoutContext.getArea();
        Rectangle layoutBox = area.getBBox().clone();

        boolean noSoftWrap = Boolean.TRUE.equals(this.parent.<Boolean>getOwnProperty(Property.NO_SOFT_WRAP_INLINE));

        OverflowPropertyValue overflowX = this.parent.<OverflowPropertyValue>getProperty(Property.OVERFLOW_X);

        List<Rectangle> floatRendererAreas = layoutContext.getFloatRendererAreas();
        FloatPropertyValue floatPropertyValue = this.<FloatPropertyValue>getProperty(Property.FLOAT);

        if (FloatingHelper.isRendererFloating(this, floatPropertyValue)) {
            FloatingHelper.adjustFloatedBlockLayoutBox(this, layoutBox, null, floatRendererAreas, floatPropertyValue, overflowX);
        }

        UnitValue[] margins = getMargins();
        applyMargins(layoutBox, margins, false);
        Border[] borders = getBorders();
        applyBorderBox(layoutBox, borders, false);

        UnitValue[] paddings = getPaddings();
        applyPaddings(layoutBox, paddings, false);

        MinMaxWidth countedMinMaxWidth = new MinMaxWidth(area.getBBox().getWidth() - layoutBox.getWidth());
        AbstractWidthHandler widthHandler;
        if (noSoftWrap) {
            widthHandler = new SumSumWidthHandler(countedMinMaxWidth);
        } else {
            widthHandler = new MaxSumWidthHandler(countedMinMaxWidth);
        }

        occupiedArea = new LayoutArea(area.getPageNumber(), new Rectangle(layoutBox.getX(), layoutBox.getY() + layoutBox.getHeight(), 0, 0));

        boolean anythingPlaced = false;

        int currentTextPos = text.start;
        UnitValue fontSize = (UnitValue) this.getPropertyAsUnitValue(Property.FONT_SIZE);
        if (!fontSize.isPointValue()) {
            Logger logger = LoggerFactory.getLogger(TextRenderer.class);
            logger.error(MessageFormatUtil.format(LogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED, Property.FONT_SIZE));
        }
        float textRise = (float) this.getPropertyAsFloat(Property.TEXT_RISE);
        Float characterSpacing = this.getPropertyAsFloat(Property.CHARACTER_SPACING);
        Float wordSpacing = this.getPropertyAsFloat(Property.WORD_SPACING);
        float hScale = (float) this.getProperty(Property.HORIZONTAL_SCALING, (Float) 1f);
        ISplitCharacters splitCharacters = this.<ISplitCharacters>getProperty(Property.SPLIT_CHARACTERS);
        float italicSkewAddition = Boolean.TRUE.equals(getPropertyAsBoolean(Property.ITALIC_SIMULATION)) ? ITALIC_ANGLE * fontSize.getValue() : 0;
        float boldSimulationAddition = Boolean.TRUE.equals(getPropertyAsBoolean(Property.BOLD_SIMULATION)) ? BOLD_SIMULATION_STROKE_COEFF * fontSize.getValue() : 0;

        line = new GlyphLine(text);
        line.start = line.end = -1;

        float[] ascenderDescender = calculateAscenderDescender(font);
        float ascender = ascenderDescender[0];
        float descender = ascenderDescender[1];

        float currentLineAscender = 0;
        float currentLineDescender = 0;
        float currentLineHeight = 0;
        int initialLineTextPos = currentTextPos;
        float currentLineWidth = 0;
        int previousCharPos = -1;

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

        HyphenationConfig hyphenationConfig = this.<HyphenationConfig>getProperty(Property.HYPHENATION);

        // For example, if a first character is a RTL mark (U+200F), and the second is a newline, we need to break anyway
        int firstPrintPos = currentTextPos;
        while (firstPrintPos < text.end && noPrint(text.get(firstPrintPos))) {
            firstPrintPos++;
        }

        while (currentTextPos < text.end) {
            if (noPrint(text.get(currentTextPos))) {
                if (line.start == -1) {
                    line.start = currentTextPos;
                }
                line.end = Math.max(line.end, currentTextPos + 1);
                currentTextPos++;
                continue;
            }

            int nonBreakablePartEnd = text.end - 1;
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

            for (int ind = currentTextPos; ind < text.end; ind++) {
                if (TextUtil.isNewLine(text.get(ind))) {
                    wordBreakGlyphAtLineEnding = text.get(ind);
                    isSplitForcedByNewLine = true;
                    firstCharacterWhichExceedsAllowedWidth = ind + 1;
                    if (ind != firstPrintPos) {
                        ignoreNewLineSymbol = true;
                    } else {
                        // Notice that in that case we do not need to ignore the new line symbol ('\n')
                        forcePartialSplitOnFirstChar = true;
                    }

                    if (line.start == -1) {
                        line.start = currentTextPos;
                    }

                    crlf = TextUtil.isCarriageReturnFollowedByLineFeed(text, currentTextPos);

                    if (crlf) {
                        currentTextPos++;
                    }

                    line.end = Math.max(line.end, firstCharacterWhichExceedsAllowedWidth - 1);
                    break;
                }

                Glyph currentGlyph = text.get(ind);
                if (noPrint(currentGlyph)) {
                    if (ind + 1 == text.end ||
                            splitCharacters.isSplitCharacter(text, ind + 1) &&
                                    TextUtil.isSpaceOrWhitespace(text.get(ind + 1))) {
                        nonBreakablePartEnd = ind;
                        break;
                    }
                    continue;
                }
                if (tabAnchorCharacter != null && tabAnchorCharacter == text.get(ind).getUnicode()) {
                    tabAnchorCharacterPosition = currentLineWidth + nonBreakablePartFullWidth;
                    tabAnchorCharacter = null;
                }

                float glyphWidth = getCharWidth(currentGlyph, fontSize.getValue(), hScale, characterSpacing, wordSpacing) / TEXT_SPACE_COEFF;
                float xAdvance = previousCharPos != -1 ? text.get(previousCharPos).getXAdvance() : 0;
                if (xAdvance != 0) {
                    xAdvance = scaleXAdvance(xAdvance, fontSize.getValue(), hScale) / TEXT_SPACE_COEFF;
                }
                if (!noSoftWrap
                        && (nonBreakablePartFullWidth + glyphWidth + xAdvance + italicSkewAddition + boldSimulationAddition) > layoutBox.getWidth() - currentLineWidth
                        && firstCharacterWhichExceedsAllowedWidth == -1) {
                    firstCharacterWhichExceedsAllowedWidth = ind;
                    if (TextUtil.isSpaceOrWhitespace(text.get(ind))) {
                        wordBreakGlyphAtLineEnding = currentGlyph;
                        if (ind == firstPrintPos) {
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
                if (firstCharacterWhichExceedsAllowedWidth == -1) {
                    nonBreakablePartWidthWhichDoesNotExceedAllowedWidth += glyphWidth + xAdvance;
                }
                nonBreakablePartFullWidth += glyphWidth + xAdvance;

                nonBreakablePartMaxAscender = Math.max(nonBreakablePartMaxAscender, ascender);
                nonBreakablePartMaxDescender = Math.min(nonBreakablePartMaxDescender, descender);
                nonBreakablePartMaxHeight = (nonBreakablePartMaxAscender - nonBreakablePartMaxDescender) * fontSize.getValue() / TEXT_SPACE_COEFF + textRise;

                previousCharPos = ind;

                if (!noSoftWrap
                        && nonBreakablePartFullWidth + italicSkewAddition + boldSimulationAddition > layoutBox.getWidth()
                        && (0 == nonBreakingHyphenRelatedChunkWidth || ind + 1 == text.end || !glyphBelongsToNonBreakingHyphenRelatedChunk(text, ind + 1))) {
                    if (isOverflowFit(overflowX)) {
                        // we have extracted all the information we wanted and we do not want to continue.
                        // we will have to split the word anyway.
                        break;
                    }
                }

                if (splitCharacters.isSplitCharacter(text, ind) || ind + 1 == text.end ||
                        splitCharacters.isSplitCharacter(text, ind + 1) &&
                                TextUtil.isSpaceOrWhitespace(text.get(ind + 1))) {
                    nonBreakablePartEnd = ind;
                    break;
                }
            }

            if (firstCharacterWhichExceedsAllowedWidth == -1) {
                // can fit the whole word in a line
                if (line.start == -1) {
                    line.start = currentTextPos;
                }
                line.end = Math.max(line.end, nonBreakablePartEnd + 1);
                currentLineAscender = Math.max(currentLineAscender, nonBreakablePartMaxAscender);
                currentLineDescender = Math.min(currentLineDescender, nonBreakablePartMaxDescender);
                currentLineHeight = Math.max(currentLineHeight, nonBreakablePartMaxHeight);
                currentTextPos = nonBreakablePartEnd + 1;
                currentLineWidth += nonBreakablePartFullWidth;
                widthHandler.updateMinChildWidth(nonBreakablePartWidthWhichDoesNotExceedAllowedWidth + italicSkewAddition + boldSimulationAddition);
                widthHandler.updateMaxChildWidth(nonBreakablePartWidthWhichDoesNotExceedAllowedWidth + italicSkewAddition + boldSimulationAddition);
                anythingPlaced = true;
            } else {
                // check if line height exceeds the allowed height
                if (Math.max(currentLineHeight, nonBreakablePartMaxHeight) > layoutBox.getHeight() && isOverflowFit(overflowY)) {
                    applyPaddings(occupiedArea.getBBox(), paddings, true);
                    applyBorderBox(occupiedArea.getBBox(), borders, true);
                    applyMargins(occupiedArea.getBBox(), margins, true);
                    // Force to place what we can
                    if (line.start == -1) {
                        line.start = currentTextPos;
                    }
                    line.end = Math.max(line.end, firstCharacterWhichExceedsAllowedWidth - 1);
                    // the line does not fit because of height - full overflow
                    TextRenderer[] splitResult = split(initialLineTextPos);
                    return new TextLayoutResult(LayoutResult.NOTHING, occupiedArea, splitResult[0], splitResult[1], this);
                } else {
                    // cannot fit a word as a whole

                    boolean wordSplit = false;
                    boolean hyphenationApplied = false;

                    if (hyphenationConfig != null) {
                        if (-1 == nonBreakingHyphenRelatedChunkStart) {
                            int[] wordBounds = getWordBoundsForHyphenation(text, currentTextPos, text.end, Math.max(currentTextPos, firstCharacterWhichExceedsAllowedWidth - 1));
                            if (wordBounds != null) {
                                String word = text.toUnicodeString(wordBounds[0], wordBounds[1]);
                                Hyphenation hyph = hyphenationConfig.hyphenate(word);
                                if (hyph != null) {
                                    for (int i = hyph.length() - 1; i >= 0; i--) {
                                        String pre = hyph.getPreHyphenText(i);
                                        String pos = hyph.getPostHyphenText(i);
                                        float currentHyphenationChoicePreTextWidth =
                                                getGlyphLineWidth(convertToGlyphLine(text.toUnicodeString(currentTextPos, wordBounds[0]) + pre + hyphenationConfig.getHyphenSymbol()), fontSize.getValue(), hScale, characterSpacing, wordSpacing);
                                        if (currentLineWidth + currentHyphenationChoicePreTextWidth + italicSkewAddition + boldSimulationAddition <= layoutBox.getWidth()) {
                                            hyphenationApplied = true;

                                            if (line.start == -1) {
                                                line.start = currentTextPos;
                                            }
                                            line.end = Math.max(line.end, wordBounds[0] + pre.length());
                                            GlyphLine lineCopy = line.copy(line.start, line.end);
                                            lineCopy.add(font.getGlyph(hyphenationConfig.getHyphenSymbol()));
                                            lineCopy.end++;
                                            line = lineCopy;

                                            // TODO these values are based on whole word. recalculate properly based on hyphenated part
                                            currentLineAscender = Math.max(currentLineAscender, nonBreakablePartMaxAscender);
                                            currentLineDescender = Math.min(currentLineDescender, nonBreakablePartMaxDescender);
                                            currentLineHeight = Math.max(currentLineHeight, nonBreakablePartMaxHeight);

                                            currentLineWidth += currentHyphenationChoicePreTextWidth;
                                            widthHandler.updateMinChildWidth(currentHyphenationChoicePreTextWidth + italicSkewAddition + boldSimulationAddition);
                                            widthHandler.updateMaxChildWidth(currentHyphenationChoicePreTextWidth + italicSkewAddition + boldSimulationAddition);

                                            currentTextPos = wordBounds[0] + pre.length();
                                            break;
                                        }
                                    }
                                }
                            }
                        } else {
                            if (text.start == nonBreakingHyphenRelatedChunkStart) {
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

                    if ((nonBreakablePartFullWidth > layoutBox.getWidth() && !anythingPlaced && !hyphenationApplied) || forcePartialSplitOnFirstChar || -1 != nonBreakingHyphenRelatedChunkStart) {
                        // if the word is too long for a single line we will have to split it
                        if (line.start == -1) {
                            line.start = currentTextPos;
                        }
                        if (!crlf) {
                            currentTextPos = (forcePartialSplitOnFirstChar || isOverflowFit(overflowX)) ? firstCharacterWhichExceedsAllowedWidth : nonBreakablePartEnd + 1;
                        }
                        line.end = Math.max(line.end, currentTextPos);
                        wordSplit = !forcePartialSplitOnFirstChar && (text.end != currentTextPos);
                        if (wordSplit || !(forcePartialSplitOnFirstChar || isOverflowFit(overflowX))) {
                            currentLineAscender = Math.max(currentLineAscender, nonBreakablePartMaxAscender);
                            currentLineDescender = Math.min(currentLineDescender, nonBreakablePartMaxDescender);
                            currentLineHeight = Math.max(currentLineHeight, nonBreakablePartMaxHeight);
                            currentLineWidth += nonBreakablePartWidthWhichDoesNotExceedAllowedWidth;
                            widthHandler.updateMinChildWidth(nonBreakablePartWidthWhichDoesNotExceedAllowedWidth + italicSkewAddition + boldSimulationAddition);
                            widthHandler.updateMaxChildWidth(nonBreakablePartWidthWhichDoesNotExceedAllowedWidth + italicSkewAddition + boldSimulationAddition);
                        } else {
                            // process empty line (e.g. '\n')
                            currentLineAscender = ascender;
                            currentLineDescender = descender;
                            currentLineHeight = (currentLineAscender - currentLineDescender) * fontSize.getValue() / TEXT_SPACE_COEFF + textRise;
                            currentLineWidth += getCharWidth(line.get(line.start), fontSize.getValue(), hScale, characterSpacing, wordSpacing) / TEXT_SPACE_COEFF;
                        }
                    }
                    if (line.end <= line.start) {
                        return new TextLayoutResult(LayoutResult.NOTHING, occupiedArea, null, this, this);
                    } else {
                        result = new TextLayoutResult(LayoutResult.PARTIAL, occupiedArea, null, null).setWordHasBeenSplit(wordSplit);
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
                return new TextLayoutResult(LayoutResult.NOTHING, occupiedArea, null, this, this);
            } else {
                isPlacingForcedWhileNothing = true;
            }
        }

        yLineOffset = currentLineAscender * fontSize.getValue() / TEXT_SPACE_COEFF;

        occupiedArea.getBBox().moveDown(currentLineHeight);
        occupiedArea.getBBox().setHeight(occupiedArea.getBBox().getHeight() + currentLineHeight);

        occupiedArea.getBBox().setWidth(Math.max(occupiedArea.getBBox().getWidth(), currentLineWidth));
        layoutBox.setHeight(area.getBBox().getHeight() - currentLineHeight);

        occupiedArea.getBBox().setWidth(occupiedArea.getBBox().getWidth() + italicSkewAddition + boldSimulationAddition);
        applyPaddings(occupiedArea.getBBox(), paddings, true);
        applyBorderBox(occupiedArea.getBBox(), borders, true);
        applyMargins(occupiedArea.getBBox(), margins, true);

        if (result == null) {
            result = new TextLayoutResult(LayoutResult.FULL, occupiedArea, null, null, isPlacingForcedWhileNothing ? this : null);
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
            if (split[1].text.start != split[1].text.end) {
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
        return result;
    }

    public void applyOtf() {
        updateFontAndText();
        Character.UnicodeScript script = this.<Character.UnicodeScript>getProperty(Property.FONT_SCRIPT);
        if (!otfFeaturesApplied && TypographyUtils.isTypographyModuleInitialized() && text.start < text.end) {
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
                    scriptsRanges.add(new ScriptRange(script, text.end));
                } else {
                    // Try to autodetect script.
                    ScriptRange currRange = new ScriptRange(null, text.end);
                    scriptsRanges.add(currRange);
                    for (int i = text.start; i < text.end; i++) {
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
                                    currRange = new ScriptRange(glyphScript, text.end);
                                    scriptsRanges.add(currRange);
                                }
                            }
                        }
                    }
                }

                int delta = 0;
                int origTextStart = text.start;
                int origTextEnd = text.end;
                int shapingRangeStart = text.start;
                for (ScriptRange scriptsRange : scriptsRanges) {
                    if (scriptsRange.script == null || !supportedScripts.contains(EnumUtil.throwIfNull(scriptsRange.script))) {
                        continue;
                    }
                    scriptsRange.rangeEnd += delta;
                    text.start = shapingRangeStart;
                    text.end = scriptsRange.rangeEnd;

                    if ((scriptsRange.script == Character.UnicodeScript.ARABIC || scriptsRange.script == Character.UnicodeScript.HEBREW) && parent instanceof LineRenderer) {
                        // It's safe to set here BASE_DIRECTION to TextRenderer without additional checks, because
                        // by convention this property makes sense only if it's applied to LineRenderer or it's
                        // parents (Paragraph or above).
                        // Only if it's not found there first, LineRenderer tries to fetch autodetected BaseDirection
                        // from text renderers (see LineRenderer#applyOtf).
                        setProperty(Property.BASE_DIRECTION, BaseDirection.DEFAULT_BIDI);
                    }
                    TypographyUtils.applyOtfScript(font.getFontProgram(), text, scriptsRange.script, typographyConfig);

                    delta += text.end - scriptsRange.rangeEnd;
                    scriptsRange.rangeEnd = shapingRangeStart = text.end;
                }
                text.start = origTextStart;
                text.end = origTextEnd + delta;
            }

            FontKerning fontKerning = (FontKerning) this.<FontKerning>getProperty(Property.FONT_KERNING, FontKerning.NO);
            if (fontKerning == FontKerning.YES) {
                TypographyUtils.applyKerning(font.getFontProgram(), text);
            }

            otfFeaturesApplied = true;
        }
    }

    @Override
    public void draw(DrawContext drawContext) {
        if (occupiedArea == null) {
            Logger logger = LoggerFactory.getLogger(TextRenderer.class);
            logger.error(MessageFormatUtil.format(LogMessageConstant.OCCUPIED_AREA_HAS_NOT_BEEN_INITIALIZED, "Drawing won't be performed."));
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

        applyMargins(occupiedArea.getBBox(), getMargins(), false);
        applyBorderBox(occupiedArea.getBBox(), false);
        applyPaddings(occupiedArea.getBBox(), getPaddings(), false);

        boolean isRelativePosition = isRelativePosition();
        if (isRelativePosition) {
            applyRelativePositioningTranslation(false);
        }

        float leftBBoxX = occupiedArea.getBBox().getX();

        if (line.end > line.start || savedWordBreakAtLineEnding != null) {
            UnitValue fontSize = this.getPropertyAsUnitValue(Property.FONT_SIZE);
            if (!fontSize.isPointValue()) {
                Logger logger = LoggerFactory.getLogger(TextRenderer.class);
                logger.error(MessageFormatUtil.format(LogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED, Property.FONT_SIZE));
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

            if (skew != null && skew.length == 2) {
                canvas.setTextMatrix(1, skew[0], skew[1], 1, leftBBoxX, getYLine());
            } else if (italicSimulation) {
                canvas.setTextMatrix(1, 0, ITALIC_ANGLE, 1, leftBBoxX, getYLine());
            } else {
                canvas.moveText(leftBBoxX, getYLine());
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
                    for (int gInd = line.start; gInd < line.end; gInd++) {
                        if (TextUtil.isUni0020(line.get(gInd))) {
                            short advance = (short) (TextRenderer.TEXT_SPACE_COEFF * (float) wordSpacing / fontSize.getValue());
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

            GlyphLine.IGlyphLineFilter filter = new GlyphLine.IGlyphLineFilter() {
                @Override
                public boolean accept(Glyph glyph) {
                    return !noPrint(glyph);
                }
            };

            boolean appearanceStreamLayout = Boolean.TRUE.equals(getPropertyAsBoolean(Property.APPEARANCE_STREAM_LAYOUT));

            if (getReversedRanges() != null) {
                boolean writeReversedChars = !appearanceStreamLayout;
                ArrayList<Integer> removedIds = new ArrayList<>();
                for (int i = line.start; i < line.end; i++) {
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
                    line.setActualText(line.start, line.end, null);
                }
                canvas.showText(line.filter(filter));
            }
            if (savedWordBreakAtLineEnding != null) {
                canvas.showText(savedWordBreakAtLineEnding);
            }

            canvas.endText().restoreState();
            endElementOpacityApplying(drawContext);

            Object underlines = this.<Object>getProperty(Property.UNDERLINE);
            if (underlines instanceof List) {
                for (Object underline : (List) underlines) {
                    if (underline instanceof Underline) {
                        drawSingleUnderline((Underline) underline, fontColor, canvas, fontSize.getValue(), italicSimulation ? ITALIC_ANGLE : 0);
                    }
                }
            } else if (underlines instanceof Underline) {
                drawSingleUnderline((Underline) underlines, fontColor, canvas, fontSize.getValue(), italicSimulation ? ITALIC_ANGLE : 0);
            }

            if (isTagged) {
                canvas.closeTag();
            }
        }

        if (isRelativePosition) {
            applyRelativePositioningTranslation(false);
        }

        applyPaddings(occupiedArea.getBBox(), true);
        applyBorderBox(occupiedArea.getBBox(), true);
        applyMargins(occupiedArea.getBBox(), getMargins(), true);

        if (isTagged && !isArtifact) {
            if (isLastRendererForModelElement) {
                taggingHelper.finishTaggingHint(this);
            }
            taggingHelper.restoreAutoTaggingPointerPosition(this);
        }
    }

    @Override
    public void drawBackground(DrawContext drawContext) {
        Background background = this.<Background>getProperty(Property.BACKGROUND);
        Float textRise = this.getPropertyAsFloat(Property.TEXT_RISE);
        Rectangle bBox = getOccupiedAreaBBox();
        Rectangle backgroundArea = applyMargins(bBox, false);
        float bottomBBoxY = backgroundArea.getY();
        float leftBBoxX = backgroundArea.getX();
        if (background != null) {
            boolean isTagged = drawContext.isTaggingEnabled();
            PdfCanvas canvas = drawContext.getCanvas();
            if (isTagged) {
                canvas.openTag(new CanvasArtifact());
            }
            boolean backgroundAreaIsClipped = clipBackgroundArea(drawContext, backgroundArea);
            canvas.saveState().setFillColor(background.getColor());
            canvas.rectangle(leftBBoxX - background.getExtraLeft(), bottomBBoxY + (float) textRise - background.getExtraBottom(),
                    backgroundArea.getWidth() + background.getExtraLeft() + background.getExtraRight(),
                    backgroundArea.getHeight() - (float) textRise + background.getExtraTop() + background.getExtraBottom());
            canvas.fill().restoreState();
            if (backgroundAreaIsClipped) {
                drawContext.getCanvas().restoreState();
            }
            if (isTagged) {
                canvas.closeTag();
            }
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
            while (text.start < text.end
                    && TextUtil.isWhitespace(glyph = text.get(text.start)) && !TextUtil.isNewLine(glyph)) {
                text.start++;
            }
        }
    }

    float trimLast() {
        float trimmedSpace = 0;

        if (line.end <= 0)
            return trimmedSpace;

        UnitValue fontSize = (UnitValue) this.getPropertyAsUnitValue(Property.FONT_SIZE);
        if (!fontSize.isPointValue()) {
            Logger logger = LoggerFactory.getLogger(TextRenderer.class);
            logger.error(MessageFormatUtil.format(LogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED, Property.FONT_SIZE));
        }
        Float characterSpacing = this.getPropertyAsFloat(Property.CHARACTER_SPACING);
        Float wordSpacing = this.getPropertyAsFloat(Property.WORD_SPACING);
        float hScale = (float) this.getPropertyAsFloat(Property.HORIZONTAL_SCALING, 1f);

        int firstNonSpaceCharIndex = line.end - 1;
        while (firstNonSpaceCharIndex >= line.start) {
            Glyph currentGlyph = line.get(firstNonSpaceCharIndex);
            if (!TextUtil.isWhitespace(currentGlyph)) {
                break;
            }
            saveWordBreakIfNotYetSaved(currentGlyph);

            float currentCharWidth = getCharWidth(currentGlyph, fontSize.getValue(), hScale, characterSpacing, wordSpacing) / TEXT_SPACE_COEFF;
            float xAdvance = firstNonSpaceCharIndex > line.start ? scaleXAdvance(line.get(firstNonSpaceCharIndex - 1).getXAdvance(), fontSize.getValue(), hScale) / TEXT_SPACE_COEFF : 0;
            trimmedSpace += currentCharWidth - xAdvance;
            occupiedArea.getBBox().setWidth(occupiedArea.getBBox().getWidth() - currentCharWidth);

            firstNonSpaceCharIndex--;
        }

        line.end = firstNonSpaceCharIndex + 1;

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
        return -(occupiedArea.getBBox().getHeight() - yLineOffset - (float) this.getPropertyAsFloat(Property.TEXT_RISE));
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
     * Manually sets a GlyphLine to be rendered with a specific start and end
     * point.
     *
     * @param text     a {@link GlyphLine}
     * @param leftPos  the leftmost end of the GlyphLine
     * @param rightPos the rightmost end of the GlyphLine
     */
    public void setText(GlyphLine text, int leftPos, int rightPos) {
        this.strToBeConverted = null;
        this.text = new GlyphLine(text);
        this.text.start = leftPos;
        this.text.end = rightPos;
        this.otfFeaturesApplied = false;
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
        return text == null ? 0 : text.end - text.start;
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
        return text.get(pos + text.start).getUnicode();
    }

    public float getTabAnchorCharacterPosition() {
        return tabAnchorCharacterPosition;
    }

    @Override
    public IRenderer getNextRenderer() {
        return new TextRenderer((Text) modelElement);
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

    static float[] calculateAscenderDescender(PdfFont font) {
        FontMetrics fontMetrics = font.getFontProgram().getFontMetrics();
        float ascender;
        float descender;
        if (fontMetrics.getWinAscender() == 0 || fontMetrics.getWinDescender() == 0 ||
                fontMetrics.getTypoAscender() == fontMetrics.getWinAscender() && fontMetrics.getTypoDescender() == fontMetrics.getWinDescender()) {
            ascender = fontMetrics.getTypoAscender() * TYPO_ASCENDER_SCALE_COEFF;
            descender = fontMetrics.getTypoDescender() * TYPO_ASCENDER_SCALE_COEFF;
        } else {
            ascender = fontMetrics.getWinAscender();
            descender = fontMetrics.getWinDescender();
        }
        return new float[]{ascender, descender};
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
        return line.end > 0 ? line.end - line.start : 0;
    }

    protected int baseCharactersCount() {
        int count = 0;
        for (int i = line.start; i < line.end; i++) {
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
        if (line.end <= 0)
            return 0;
        int spaces = 0;
        for (int i = line.start; i < line.end; i++) {
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
        splitRenderer.setText(text, text.start, initialOverflowTextPos);
        splitRenderer.font = font;
        splitRenderer.line = line;
        splitRenderer.occupiedArea = occupiedArea.clone();
        splitRenderer.parent = parent;
        splitRenderer.yLineOffset = yLineOffset;
        splitRenderer.otfFeaturesApplied = otfFeaturesApplied;
        splitRenderer.isLastRendererForModelElement = false;
        splitRenderer.addAllProperties(getOwnProperties());

        TextRenderer overflowRenderer = createOverflowRenderer();
        overflowRenderer.setText(text, initialOverflowTextPos, text.end);
        overflowRenderer.font = font;
        overflowRenderer.otfFeaturesApplied = otfFeaturesApplied;
        overflowRenderer.parent = parent;
        overflowRenderer.addAllProperties(getOwnProperties());

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
            canvas.moveTo(occupiedArea.getBBox().getX(), underlineYPosition).
                    lineTo(occupiedArea.getBBox().getX() + occupiedArea.getBBox().getWidth() - italicWidthSubstraction, underlineYPosition).
                    stroke();
        }

        canvas.restoreState();
    }

    protected float calculateLineWidth() {
        UnitValue fontSize = this.getPropertyAsUnitValue(Property.FONT_SIZE);
        if (!fontSize.isPointValue()) {
            Logger logger = LoggerFactory.getLogger(TextRenderer.class);
            logger.error(MessageFormatUtil.format(LogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED, Property.FONT_SIZE));
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
        } else if (font instanceof String || font instanceof String[]) {
            if (font instanceof String) {
                // TODO remove this if-clause before 7.2
                Logger logger = LoggerFactory.getLogger(AbstractRenderer.class);
                logger.warn(LogMessageConstant.FONT_PROPERTY_OF_STRING_TYPE_IS_DEPRECATED_USE_STRINGS_ARRAY_INSTEAD);
                List<String> splitFontFamily = FontFamilySplitter.splitFontFamily((String) font);
                font = splitFontFamily.toArray(new String[splitFontFamily.size()]);
            }
            FontProvider provider = this.<FontProvider>getProperty(Property.FONT_PROVIDER);
            FontSet fontSet = this.<FontSet>getProperty(Property.FONT_SET);
            if (provider.getFontSet().isEmpty() && (fontSet == null || fontSet.isEmpty())) {
                throw new IllegalStateException(PdfException.FontProviderNotSetFontFamilyNotResolved);
            }
            FontCharacteristics fc = createFontCharacteristics();
            FontSelectorStrategy strategy = provider.getStrategy(strToBeConverted, Arrays.asList((String[])font), fc, fontSet);
            // process empty renderers because they can have borders or paddings with background to be drawn
            if (null == strToBeConverted || strToBeConverted.isEmpty()) {
                addTo.add(this);
            } else {
                while (!strategy.endOfText()) {
                    GlyphLine nextGlyphs = new GlyphLine(strategy.nextGlyphs());
                    PdfFont currentFont = strategy.getCurrentFont();
                    TextRenderer textRenderer = createCopy(replaceSpecialWhitespaceGlyphs(nextGlyphs, currentFont), currentFont);
                    addTo.add(textRenderer);
                }
            }
            return true;
        } else {
            throw new IllegalStateException("Invalid FONT property value type.");
        }
    }

    protected void setGlyphLineAndFont(GlyphLine gl, PdfFont font) {
        this.text = gl;
        this.font = font;
        this.otfFeaturesApplied = false;
        this.strToBeConverted = null;
        setProperty(Property.FONT, font);
    }

    protected TextRenderer createCopy(GlyphLine gl, PdfFont font) {
        TextRenderer copy = new TextRenderer(this);
        copy.setGlyphLineAndFont(gl, font);
        return copy;
    }

    static void updateRangeBasedOnRemovedCharacters(ArrayList<Integer> removedIds, int[] range) {
        int shift = numberOfElementsLessThan(removedIds, range[0]);
        range[0] -= shift;
        shift = numberOfElementsLessThanOrEqual(removedIds, range[1]);
        range[1] -= shift;
    }

    @Override
    PdfFont resolveFirstPdfFont(String[] font, FontProvider provider, FontCharacteristics fc) {
        FontSelectorStrategy strategy = provider.getStrategy(strToBeConverted, Arrays.asList(font), fc);
        List<Glyph> resolvedGlyphs;
        PdfFont currentFont;
        //try to find first font that can render at least one glyph.
        while (!strategy.endOfText()) {
            resolvedGlyphs = strategy.nextGlyphs();
            currentFont = strategy.getCurrentFont();
            for (Glyph glyph : resolvedGlyphs) {
                if (currentFont.containsGlyph(glyph.getUnicode())) {
                    return currentFont;
                }
            }
        }
        return super.resolveFirstPdfFont(font, provider, fc);
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
        return TextUtil.isNonBreakingHyphen(text.get(ind)) || (ind + 1 < text.end && TextUtil.isNonBreakingHyphen(text.get(ind + 1))) || ind - 1 >= text.start && TextUtil.isNonBreakingHyphen(text.get(ind - 1));
    }

    private float getCharWidth(Glyph g, float fontSize, Float hScale, Float characterSpacing, Float wordSpacing) {
        if (hScale == null)
            hScale = 1f;

        float resultWidth = g.getWidth() * fontSize * (float) hScale;
        if (characterSpacing != null) {
            resultWidth += (float) characterSpacing * (float) hScale * TEXT_SPACE_COEFF;
        }
        if (wordSpacing != null && g.getUnicode() == ' ') {
            resultWidth += (float) wordSpacing * (float) hScale * TEXT_SPACE_COEFF;
        }
        return resultWidth;
    }

    private float scaleXAdvance(float xAdvance, float fontSize, Float hScale) {
        return xAdvance * fontSize * (float) hScale;
    }

    private float getGlyphLineWidth(GlyphLine glyphLine, float fontSize, float hScale, Float characterSpacing, Float wordSpacing) {
        float width = 0;
        for (int i = glyphLine.start; i < glyphLine.end; i++) {
            if (!noPrint(glyphLine.get(i))) {
                float charWidth = getCharWidth(glyphLine.get(i), fontSize, hScale, characterSpacing, wordSpacing);
                width += charWidth;
                float xAdvance = (i != glyphLine.start) ? scaleXAdvance(glyphLine.get(i - 1).getXAdvance(), fontSize, hScale) : 0;
                width += xAdvance;
            }
        }
        return width / TEXT_SPACE_COEFF;
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
                || '\u00ad' == g.getUnicode(); // soft hyphen
    }

    private void updateFontAndText() {
        if (strToBeConverted != null) {
            try {
                font = getPropertyAsFont(Property.FONT);
            } catch (ClassCastException cce) {
                font = resolveFirstPdfFont();
                if (!strToBeConverted.isEmpty()) {
                    Logger logger = LoggerFactory.getLogger(TextRenderer.class);
                    logger.error(LogMessageConstant.FONT_PROPERTY_MUST_BE_PDF_FONT_OBJECT);
                }
            }
            text = convertToGlyphLine(strToBeConverted);
            otfFeaturesApplied = false;
            strToBeConverted = null;
        }
    }

    private void saveWordBreakIfNotYetSaved(Glyph wordBreak) {
        if (savedWordBreakAtLineEnding == null) {
            if (TextUtil.isNewLine(wordBreak)) {
                wordBreak = font.getGlyph('\u0020'); // we don't want to print '\n' in content stream
            }
            // it's word-break character at the end of the line, which we want to save after trimming
            savedWordBreakAtLineEnding = new GlyphLine(Collections.<Glyph>singletonList(wordBreak));
        }
    }

    private static GlyphLine replaceSpecialWhitespaceGlyphs(GlyphLine line, PdfFont font) {
        if (null != line) {
            Glyph space = font.getGlyph('\u0020');
            Glyph glyph;
            for (int i = 0; i < line.size(); i++) {
                glyph = line.get(i);
                Integer xAdvance = getSpecialWhitespaceXAdvance(glyph, space, font.getFontProgram().getFontMetrics().isFixedPitch());
                if (xAdvance != null) {
                    Glyph newGlyph = new Glyph(space, glyph.getUnicode());
                    assert xAdvance <= Short.MAX_VALUE && xAdvance >= Short.MIN_VALUE;
                    newGlyph.setXAdvance((short) (int) xAdvance);
                    line.set(i, newGlyph);
                }
            }
        }
        return line;
    }

    private static Integer getSpecialWhitespaceXAdvance(Glyph glyph, Glyph spaceGlyph, boolean isMonospaceFont) {
        if (glyph.getCode() > 0) {
            return null;
        }
        switch (glyph.getUnicode()) {
            case '\u2002': // ensp
                return isMonospaceFont ? 0 : 500 - spaceGlyph.getWidth();
            case '\u2003': // emsp
                return isMonospaceFont ? 0 : 1000 - spaceGlyph.getWidth();
            case '\u2009': // thinsp
                return isMonospaceFont ? 0 : 200 - spaceGlyph.getWidth();
            case '\t':
                return 3 * spaceGlyph.getWidth();
        }

        return null;
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
                outStart.add(line.start);
                outEnd.add(line.end);
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
}
