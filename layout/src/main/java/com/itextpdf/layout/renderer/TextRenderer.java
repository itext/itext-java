/*

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

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.io.font.FontMetrics;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.TrueTypeFont;
import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.io.font.otf.GlyphLine;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfType0Font;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.canvas.CanvasArtifact;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants;
import com.itextpdf.kernel.pdf.tagutils.IAccessibleElement;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.hyphenation.Hyphenation;
import com.itextpdf.layout.hyphenation.HyphenationConfig;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.layout.TextLayoutResult;
import com.itextpdf.layout.property.Background;
import com.itextpdf.layout.property.BaseDirection;
import com.itextpdf.layout.property.FontKerning;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.property.Underline;
import com.itextpdf.layout.splitting.ISplitCharacters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This class represents the {@link IRenderer renderer} object for a {@link Text}
 * object. It will draw the glyphs of the textual content on the {@link DrawContext}.
 */
public class TextRenderer extends AbstractRenderer {

    protected static final float TEXT_SPACE_COEFF = FontProgram.UNITS_NORMALIZATION;
    private static final float ITALIC_ANGLE = 0.21256f;
    private static final float BOLD_SIMULATION_STROKE_COEFF = 1 / 30f;
    private static final float TYPO_ASCENDER_SCALE_COEFF = 1.2f;

    protected float yLineOffset;

    protected GlyphLine text;
    protected GlyphLine line;
    protected String strToBeConverted;

    protected boolean otfFeaturesApplied = false;

    protected float tabAnchorCharacterPosition = -1;

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
        this.yLineOffset = other.yLineOffset;
        this.strToBeConverted = other.strToBeConverted;
        this.otfFeaturesApplied = other.otfFeaturesApplied;
        this.tabAnchorCharacterPosition = other.tabAnchorCharacterPosition;
    }

    @Override
    public LayoutResult layout(LayoutContext layoutContext) {
        convertWaitingStringToGlyphLine();

        LayoutArea area = layoutContext.getArea();
        float[] margins = getMargins();
        Rectangle layoutBox = applyMargins(area.getBBox().clone(), margins, false);
        Border[] borders = getBorders();
        applyBorderBox(layoutBox, borders, false);

        occupiedArea = new LayoutArea(area.getPageNumber(), new Rectangle(layoutBox.getX(), layoutBox.getY() + layoutBox.getHeight(), 0, 0));

        boolean anythingPlaced = false;

        int currentTextPos = text.start;
        float fontSize = (float) this.getPropertyAsFloat(Property.FONT_SIZE);
        float textRise = (float) this.getPropertyAsFloat(Property.TEXT_RISE);
        Float characterSpacing = this.getPropertyAsFloat(Property.CHARACTER_SPACING);
        Float wordSpacing = this.getPropertyAsFloat(Property.WORD_SPACING);
        PdfFont font = this.getPropertyAsFont(Property.FONT);
        Float hScale = this.getProperty(Property.HORIZONTAL_SCALING, (Float) 1f);
        ISplitCharacters splitCharacters = this.<ISplitCharacters>getProperty(Property.SPLIT_CHARACTERS);
        float italicSkewAddition = Boolean.TRUE.equals(getPropertyAsBoolean(Property.ITALIC_SIMULATION)) ? ITALIC_ANGLE * fontSize : 0;
        float boldSimulationAddition = Boolean.TRUE.equals(getPropertyAsBoolean(Property.BOLD_SIMULATION)) ? BOLD_SIMULATION_STROKE_COEFF * fontSize : 0;

        line = new GlyphLine(text);
        line.start = line.end = -1;

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

        float currentLineAscender = 0;
        float currentLineDescender = 0;
        float currentLineHeight = 0;
        int initialLineTextPos = currentTextPos;
        float currentLineWidth = 0;
        int previousCharPos = -1;

        Character tabAnchorCharacter = this.<Character>getProperty(Property.TAB_ANCHOR);

        TextLayoutResult result = null;

        // true in situations like "\nHello World"
        boolean isSplitForcedByImmediateNewLine = false;
        // true in situations like "Hello\nWorld"
        boolean isSplitForcedByNewLineAndWeNeedToIgnoreNewLineSymbol = false;

        while (currentTextPos < text.end) {
            if (noPrint(text.get(currentTextPos))) {
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

            for (int ind = currentTextPos; ind < text.end; ind++) {
                if (isNewLine(text, ind)) {
                    isSplitForcedByNewLineAndWeNeedToIgnoreNewLineSymbol = true;
                    firstCharacterWhichExceedsAllowedWidth = ind + 1;
                    if (text.start == currentTextPos) {
                        isSplitForcedByImmediateNewLine = true;
                        // Notice that in that case we do not need to ignore the new line symbol ('\n')
                        isSplitForcedByNewLineAndWeNeedToIgnoreNewLineSymbol = false;
                    }
                    break;
                }

                Glyph currentGlyph = text.get(ind);
                if (noPrint(currentGlyph))
                    continue;

                if (tabAnchorCharacter != null && tabAnchorCharacter == text.get(ind).getUnicode()) {
                    tabAnchorCharacterPosition = currentLineWidth + nonBreakablePartFullWidth;
                    tabAnchorCharacter = null;
                }

                float glyphWidth = getCharWidth(currentGlyph, fontSize, hScale, characterSpacing, wordSpacing) / TEXT_SPACE_COEFF;
                float xAdvance = previousCharPos != -1 ? text.get(previousCharPos).getXAdvance() : 0;
                if (xAdvance != 0) {
                    xAdvance = scaleXAdvance(xAdvance, fontSize, hScale) / TEXT_SPACE_COEFF;
                }
                if ((nonBreakablePartFullWidth + glyphWidth + xAdvance + italicSkewAddition + boldSimulationAddition) > layoutBox.getWidth() - currentLineWidth && firstCharacterWhichExceedsAllowedWidth == -1) {
                    firstCharacterWhichExceedsAllowedWidth = ind;
                }
                if (firstCharacterWhichExceedsAllowedWidth == -1) {
                    nonBreakablePartWidthWhichDoesNotExceedAllowedWidth += glyphWidth + xAdvance;
                }

                nonBreakablePartFullWidth += glyphWidth + xAdvance;
                nonBreakablePartMaxAscender = Math.max(nonBreakablePartMaxAscender, ascender);
                nonBreakablePartMaxDescender = Math.min(nonBreakablePartMaxDescender, descender);
                nonBreakablePartMaxHeight = (nonBreakablePartMaxAscender - nonBreakablePartMaxDescender) * fontSize / TEXT_SPACE_COEFF + textRise;

                previousCharPos = ind;

                if (nonBreakablePartFullWidth + italicSkewAddition + boldSimulationAddition > layoutBox.getWidth()) {
                    // we have extracted all the information we wanted and we do not want to continue.
                    // we will have to split the word anyway.
                    break;
                }

                if (splitCharacters.isSplitCharacter(text, ind) || ind + 1 == text.end ||
                        splitCharacters.isSplitCharacter(text, ind + 1) &&
                                isSpaceGlyph(text.get(ind + 1))) {
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
                anythingPlaced = true;
            } else {
                // check if line height exceeds the allowed height
                if (Math.max(currentLineHeight, nonBreakablePartMaxHeight) > layoutBox.getHeight()) {
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

                    HyphenationConfig hyphenationConfig = this.<HyphenationConfig>getProperty(Property.HYPHENATION);
                    if (hyphenationConfig != null) {
                        int[] wordBounds = getWordBoundsForHyphenation(text, currentTextPos, text.end, Math.max(currentTextPos, firstCharacterWhichExceedsAllowedWidth - 1));
                        if (wordBounds != null) {
                            String word = text.toUnicodeString(wordBounds[0], wordBounds[1]);
                            Hyphenation hyph = hyphenationConfig.hyphenate(word);
                            if (hyph != null) {
                                for (int i = hyph.length() - 1; i >= 0; i--) {
                                    String pre = hyph.getPreHyphenText(i);
                                    String pos = hyph.getPostHyphenText(i);
                                    float currentHyphenationChoicePreTextWidth =
                                            getGlyphLineWidth(convertToGlyphLine(pre + hyphenationConfig.getHyphenSymbol()), fontSize, hScale, characterSpacing, wordSpacing);
                                    if (currentLineWidth + currentHyphenationChoicePreTextWidth + italicSkewAddition + boldSimulationAddition <= layoutBox.getWidth()) {
                                        hyphenationApplied = true;

                                        if (line.start == -1) {
                                            line.start = currentTextPos;
                                        }
                                        line.end = Math.max(line.end, currentTextPos + pre.length());
                                        GlyphLine lineCopy = line.copy(line.start, line.end);
                                        lineCopy.add(font.getGlyph(hyphenationConfig.getHyphenSymbol()));
                                        lineCopy.end++;
                                        line = lineCopy;

                                        // TODO these values are based on whole word. recalculate properly based on hyphenated part
                                        currentLineAscender = Math.max(currentLineAscender, nonBreakablePartMaxAscender);
                                        currentLineDescender = Math.min(currentLineDescender, nonBreakablePartMaxDescender);
                                        currentLineHeight = Math.max(currentLineHeight, nonBreakablePartMaxHeight);

                                        currentLineWidth += currentHyphenationChoicePreTextWidth;
                                        currentTextPos += pre.length();

                                        break;
                                    }
                                }
                            }
                        }
                    }

                    if ((nonBreakablePartFullWidth > layoutBox.getWidth() && !anythingPlaced && !hyphenationApplied) || (isSplitForcedByImmediateNewLine)) {
                        // if the word is too long for a single line we will have to split it
                        wordSplit = true;
                        if (line.start == -1) {
                            line.start = currentTextPos;
                        }
                        currentTextPos = firstCharacterWhichExceedsAllowedWidth;
                        line.end = Math.max(line.end, firstCharacterWhichExceedsAllowedWidth);
                        if (nonBreakablePartFullWidth > layoutBox.getWidth() && !anythingPlaced && !hyphenationApplied) {
                            currentLineAscender = Math.max(currentLineAscender, nonBreakablePartMaxAscender);
                            currentLineDescender = Math.min(currentLineDescender, nonBreakablePartMaxDescender);
                            currentLineHeight = Math.max(currentLineHeight, nonBreakablePartMaxHeight);
                            currentLineWidth += nonBreakablePartWidthWhichDoesNotExceedAllowedWidth;

                        } else {
                            // process empty line (e.g. '\n')
                            currentLineAscender = ascender;
                            currentLineDescender = descender;
                            currentLineHeight = (currentLineAscender - currentLineDescender) * fontSize / TEXT_SPACE_COEFF + textRise;
                            currentLineWidth += getCharWidth(line.get(0), fontSize, hScale, characterSpacing, wordSpacing) / TEXT_SPACE_COEFF;
                        }
                    }
                    if (line.end <= line.start) {
                        return new TextLayoutResult(LayoutResult.NOTHING,
                                occupiedArea, null, this, this);
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
            if (!Boolean.TRUE.equals(getPropertyAsBoolean(Property.FORCED_PLACEMENT))) {
                applyBorderBox(occupiedArea.getBBox(), borders, true);
                applyMargins(occupiedArea.getBBox(), margins, true);
                return new TextLayoutResult(LayoutResult.NOTHING, occupiedArea, null, this, this);
            } else {
                isPlacingForcedWhileNothing = true;
            }
        }

        yLineOffset = currentLineAscender * fontSize / TEXT_SPACE_COEFF;

        occupiedArea.getBBox().moveDown(currentLineHeight);
        occupiedArea.getBBox().setHeight(occupiedArea.getBBox().getHeight() + currentLineHeight);

        occupiedArea.getBBox().setWidth(Math.max(occupiedArea.getBBox().getWidth(), currentLineWidth));
        layoutBox.setHeight(area.getBBox().getHeight() - currentLineHeight);

        occupiedArea.getBBox().setWidth(occupiedArea.getBBox().getWidth() + italicSkewAddition + boldSimulationAddition);
        applyBorderBox(occupiedArea.getBBox(), borders, true);
        applyMargins(occupiedArea.getBBox(), margins, true);

        if (result != null) {
            TextRenderer[] split;
            if (isSplitForcedByNewLineAndWeNeedToIgnoreNewLineSymbol) {
                // ignore '\n'
                split = splitIgnoreFirstNewLine(currentTextPos);
            } else {
                split = split(currentTextPos);
            }
            // if (split[1].length() > 0 && split[1].charAt(0) != null && split[1].charAt(0) == '\n') {
            if (isSplitForcedByNewLineAndWeNeedToIgnoreNewLineSymbol) {
                result.setSplitForcedByNewline(true);
            }
            result.setSplitRenderer(split[0]);
            // no sense to process empty renderer
            if (split[1].text.start != split[1].text.end) {
                result.setOverflowRenderer(split[1]);
            }
        } else {
            result = new TextLayoutResult(LayoutResult.FULL, occupiedArea, null, null,
                    isPlacingForcedWhileNothing ? this : null);
        }

        return result;
    }

    public void applyOtf() {
        convertWaitingStringToGlyphLine();
        Character.UnicodeScript script = this.<Character.UnicodeScript>getProperty(Property.FONT_SCRIPT);
        if (!otfFeaturesApplied) {
            if (script == null && TypographyUtils.isTypographyModuleInitialized()) {
                // Try to autodetect complex script.
                Collection<Character.UnicodeScript> supportedScripts = TypographyUtils.getSupportedScripts();
                Map<Character.UnicodeScript, Integer> scriptFrequency = new EnumMap<Character.UnicodeScript, Integer>(Character.UnicodeScript.class);
                for (int i = text.start; i < text.end; i++) {
                    int unicode = text.get(i).getUnicode();
                    if (unicode > -1) {
                        Character.UnicodeScript glyphScript = Character.UnicodeScript.of(unicode);
                        if (scriptFrequency.containsKey(glyphScript)) {
                            scriptFrequency.put(glyphScript, scriptFrequency.get(glyphScript) + 1);
                        } else {
                            scriptFrequency.put(glyphScript, 1);
                        }
                    }
                }
                Integer max = 0;
                Map.Entry<Character.UnicodeScript, Integer> selectedEntry = null;
                for (Map.Entry<Character.UnicodeScript, Integer> entry : scriptFrequency.entrySet()) {
                    Character.UnicodeScript entryScript = entry.getKey();
                    if (entry.getValue() > max && !Character.UnicodeScript.COMMON.equals(entryScript) && !Character.UnicodeScript.UNKNOWN.equals(entryScript)
                            && !Character.UnicodeScript.INHERITED.equals(entryScript)) {
                        max = entry.getValue();
                        selectedEntry = entry;
                    }
                }
                if (selectedEntry != null) {
                    Character.UnicodeScript selectScript = ((Map.Entry<Character.UnicodeScript, Integer>)selectedEntry).getKey();
                    if ((selectScript == Character.UnicodeScript.ARABIC || selectScript == Character.UnicodeScript.HEBREW) && parent instanceof LineRenderer) {
                        setProperty(Property.BASE_DIRECTION, BaseDirection.DEFAULT_BIDI);
                    }
                    if (supportedScripts != null && supportedScripts.contains(selectScript)) {
                        script = selectScript;
                    }
                }
            }

            PdfFont font = getPropertyAsFont(Property.FONT);
            if (isOtfFont(font) && script != null) {
                TypographyUtils.applyOtfScript(font.getFontProgram(), text, script);
            }

            FontKerning fontKerning = (FontKerning)this.<FontKerning>getProperty(Property.FONT_KERNING, FontKerning.NO);
            if (fontKerning == FontKerning.YES) {
                TypographyUtils.applyKerning(font.getFontProgram(), text);
            }

            otfFeaturesApplied = true;
        }
    }

    @Override
    public void draw(DrawContext drawContext) {
        if (occupiedArea == null) {
            Logger logger = LoggerFactory.getLogger(RootRenderer.class);
            logger.error(LogMessageConstant.OCCUPIED_AREA_HAS_NOT_BEEN_INITIALIZED);
            return;
        }
        super.draw(drawContext);

        PdfDocument document = drawContext.getDocument();
        boolean isTagged = drawContext.isTaggingEnabled() && getModelElement() instanceof IAccessibleElement;
        boolean isArtifact = false;
        TagTreePointer tagPointer = null;
        IAccessibleElement accessibleElement = null;
        if (isTagged) {
            accessibleElement = (IAccessibleElement) getModelElement();
            PdfName role = accessibleElement.getRole();
            if (role != null && !PdfName.Artifact.equals(role)) {
                tagPointer = document.getTagStructureContext().getAutoTaggingPointer();
                if (!tagPointer.isElementConnectedToTag(accessibleElement)) {
                    AccessibleAttributesApplier.applyLayoutAttributes(accessibleElement.getRole(), this, document);
                }
                tagPointer.addTag(accessibleElement, true);
            } else {
                isTagged = false;
                if (PdfName.Artifact.equals(role)) {
                    isArtifact = true;
                }
            }
        }

        applyBorderBox(occupiedArea.getBBox(), false);

        boolean isRelativePosition = isRelativePosition();
        if (isRelativePosition) {
            applyAbsolutePositioningTranslation(false);
        }

        float leftBBoxX = occupiedArea.getBBox().getX();

        if (line.end > line.start) {
            PdfFont font = getPropertyAsFont(Property.FONT);
            float fontSize = (float) this.getPropertyAsFloat(Property.FONT_SIZE);
            Color fontColor = getPropertyAsColor(Property.FONT_COLOR);
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
                strokeWidth = fontSize / 30;
            }

            PdfCanvas canvas = drawContext.getCanvas();
            if (isTagged) {
                canvas.openTag(tagPointer.getTagReference());
            } else if (isArtifact) {
                canvas.openTag(new CanvasArtifact());
            }
            canvas.saveState().beginText().setFontAndSize(font, fontSize);

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
                if (strokeColor == null)
                    strokeColor = fontColor;
                if (strokeColor != null)
                    canvas.setStrokeColor(strokeColor);
            }
            if (fontColor != null)
                canvas.setFillColor(fontColor);
            if (textRise != null && textRise != 0)
                canvas.setTextRise((float) textRise);
            if (characterSpacing != null && characterSpacing != 0)
                canvas.setCharacterSpacing((float) characterSpacing);
            if (wordSpacing != null && wordSpacing != 0)
                canvas.setWordSpacing((float) wordSpacing);
            if (horizontalScaling != null && horizontalScaling != 1)
                canvas.setHorizontalScaling((float) horizontalScaling * 100);

            GlyphLine.IGlyphLineFilter filter = new GlyphLine.IGlyphLineFilter() {
                @Override
                public boolean accept(Glyph glyph) {
                    return !noPrint(glyph);
                }
            };

            boolean appearanceStreamLayout = Boolean.TRUE.equals (getPropertyAsBoolean(Property.APPEARANCE_STREAM_LAYOUT));

            if (hasOwnProperty(Property.REVERSED)) {
                boolean writeReversedChars = !appearanceStreamLayout;
                List<int[]> reversedRanges = this.<List<int[]>>getOwnProperty(Property.REVERSED);
                ArrayList<Integer> removedIds = new ArrayList<>();
                for (int i = line.start; i < line.end; i++) {
                    if (!filter.accept(line.get(i))) {
                        removedIds.add(i);
                    }
                }
                if (reversedRanges != null) {
                    for (int[] range : reversedRanges) {
                        int shift = Collections.binarySearch(removedIds, range[0]);
                        if (shift < 0) {
                            shift = -shift - 1;
                        }
                        range[0] -= shift;
                        shift = Collections.binarySearch(removedIds, range[1] - 1);
                        if (shift < 0) {
                            shift = -shift - 1;
                        }
                        range[1] -= shift;
                    }
                }
                line = line.filter(filter);
                if (writeReversedChars) {
                    canvas.showText(line, new ReversedCharsIterator(reversedRanges, line).
                            setUseReversed(writeReversedChars));
                } else {
                    canvas.showText(line);
                }
            } else {
                if (appearanceStreamLayout) {
                    line.setActualText(line.start, line.end, null);
                }
                canvas.showText(line.filter(filter));
            }

            canvas.endText().restoreState();
            if (isTagged || isArtifact) {
                canvas.closeTag();
            }

            Object underlines = this.<Object>getProperty(Property.UNDERLINE);
            if (underlines instanceof List) {
                for (Object underline : (List) underlines) {
                    if (underline instanceof Underline) {
                        drawSingleUnderline((Underline) underline, fontColor, canvas, fontSize, italicSimulation ? ITALIC_ANGLE : 0);
                    }
                }
            } else if (underlines instanceof Underline) {
                drawSingleUnderline((Underline) underlines, fontColor, canvas, fontSize, italicSimulation ? ITALIC_ANGLE : 0);
            }
        }

        if (isRelativePosition) {
            applyAbsolutePositioningTranslation(false);
        }

        applyBorderBox(occupiedArea.getBBox(), true);

        if (isTagged) {
            tagPointer.moveToParent();
            if (isLastRendererForModelElement) {
                tagPointer.removeElementConnectionToTag(accessibleElement);
            }
        }
    }

    @Override
    public void drawBackground(DrawContext drawContext) {
        Background background = this.<Background>getProperty(Property.BACKGROUND);
        Float textRise = this.getPropertyAsFloat(Property.TEXT_RISE);
        float bottomBBoxY = occupiedArea.getBBox().getY();
        float leftBBoxX = occupiedArea.getBBox().getX();
        if (background != null) {
            boolean isTagged = drawContext.isTaggingEnabled() && getModelElement() instanceof IAccessibleElement;
            PdfCanvas canvas = drawContext.getCanvas();
            if (isTagged) {
                canvas.openTag(new CanvasArtifact());
            }
            canvas.saveState().setFillColor(background.getColor());
            canvas.rectangle(leftBBoxX - background.getExtraLeft(), bottomBBoxY + (float) textRise - background.getExtraBottom(),
                    occupiedArea.getBBox().getWidth() + background.getExtraLeft() + background.getExtraRight(),
                    occupiedArea.getBBox().getHeight() - (float) textRise + background.getExtraTop() + background.getExtraBottom());
            canvas.fill().restoreState();
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
        convertWaitingStringToGlyphLine();

        if (text != null) {
            Glyph glyph;
            while (text.start < text.end && (glyph = text.get(text.start)).hasValidUnicode() && isSpaceGlyph(glyph) && !isNewLine(text, text.start)) {
                text.start++;
            }
        }
    }

    /**
     * Trims any whitespace characters from the end of the {@link GlyphLine} to
     * be rendered.
     *
     * @return the amount of space in points which the text was trimmed by
     */
    public float trimLast() {
        float trimmedSpace = 0;

        if (line.end <= 0)
            return trimmedSpace;

        float fontSize = (float) this.getPropertyAsFloat(Property.FONT_SIZE);
        Float characterSpacing = this.getPropertyAsFloat(Property.CHARACTER_SPACING);
        Float wordSpacing = this.getPropertyAsFloat(Property.WORD_SPACING);
        Float hScale = this.getPropertyAsFloat(Property.HORIZONTAL_SCALING, 1f);

        int firstNonSpaceCharIndex = line.end - 1;
        while (firstNonSpaceCharIndex >= line.start) {
            Glyph currentGlyph = line.get(firstNonSpaceCharIndex);
            if (!currentGlyph.hasValidUnicode() || !isSpaceGlyph(currentGlyph)) {
                break;
            }

            float currentCharWidth = getCharWidth(currentGlyph, fontSize, hScale, characterSpacing, wordSpacing) / TEXT_SPACE_COEFF;
            float xAdvance = firstNonSpaceCharIndex > line.start ? scaleXAdvance(line.get(firstNonSpaceCharIndex - 1).getXAdvance(), fontSize, hScale) / TEXT_SPACE_COEFF : 0;
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
    public float getAscent() {
        return yLineOffset;
    }

    /**
     * Gets the maximum offset below the base line that this Text extends to.
     *
     * @return the downwards vertical offset of this {@link Text}
     */
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
        GlyphLine glyphLine = convertToGlyphLine(text);
        setText(glyphLine, glyphLine.start, glyphLine.end);
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
        this.text = new GlyphLine(text);
        this.text.start = leftPos;
        this.text.end = rightPos;
        this.otfFeaturesApplied = false;
    }

    public GlyphLine getText() {
        convertWaitingStringToGlyphLine();
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
        return line != null ? line.toUnicodeString(line.start, line.end) : strToBeConverted;
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
        return new TextRenderer((Text) modelElement, null);
    }

    protected static boolean isNewLine(GlyphLine text, int ind) {
        int unicode = text.get(ind).getUnicode();
        return unicode == '\n' || unicode == '\r';
    }

    static boolean isSpaceGlyph(Glyph glyph) {
        return Character.isWhitespace((char) glyph.getUnicode()) || Character.isSpaceChar((char) glyph.getUnicode());
    }

    private TextRenderer[] splitIgnoreFirstNewLine(int currentTextPos) {
        if (text.get(currentTextPos).hasValidUnicode() && text.get(currentTextPos).getUnicode() == '\r') {
            int next = currentTextPos + 1 < text.end ? text.get(currentTextPos + 1).getUnicode() : -1;
            if (next == '\n') {
                return split(currentTextPos + 2);
            } else {
                return split(currentTextPos + 1);
            }
        } else {
            return split(currentTextPos + 1);
        }
    }

    private GlyphLine convertToGlyphLine(String text) {
        PdfFont font = getPropertyAsFont(Property.FONT);
        return font.createGlyphLine(text);
    }

    private boolean isOtfFont(PdfFont font) {
        return font instanceof PdfType0Font && font.getFontProgram() instanceof TrueTypeFont;
    }

    @Override
    protected Float getFirstYLineRecursively() {
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

    protected int getNumberOfSpaces() {
        if (line.end <= 0)
            return 0;
        int spaces = 0;
        for (int i = line.start; i < line.end; i++) {
            Glyph currentGlyph = line.get(i);
            if (currentGlyph.hasValidUnicode() && currentGlyph.getUnicode() == ' ') {
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
        splitRenderer.line = line;
        splitRenderer.occupiedArea = occupiedArea.clone();
        splitRenderer.parent = parent;
        splitRenderer.yLineOffset = yLineOffset;
        splitRenderer.otfFeaturesApplied = otfFeaturesApplied;
        splitRenderer.isLastRendererForModelElement = false;
        splitRenderer.addAllProperties(getOwnProperties());

        TextRenderer overflowRenderer = createOverflowRenderer();
        overflowRenderer.setText(text, initialOverflowTextPos, text.end);
        overflowRenderer.otfFeaturesApplied = otfFeaturesApplied;
        overflowRenderer.parent = parent;
        overflowRenderer.addAllProperties(getOwnProperties());

        return new TextRenderer[]{splitRenderer, overflowRenderer};
    }

    protected void drawSingleUnderline(Underline underline, Color fontStrokeColor, PdfCanvas canvas, float fontSize, float italicAngleTan) {
        Color underlineColor = underline.getColor() != null ? underline.getColor() : fontStrokeColor;
        canvas.saveState();

        if (underlineColor != null) {
            canvas.setStrokeColor(underlineColor);
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
        return getGlyphLineWidth(line, (float) this.getPropertyAsFloat(Property.FONT_SIZE), this.getPropertyAsFloat(Property.HORIZONTAL_SCALING, 1f),
                this.getPropertyAsFloat(Property.CHARACTER_SPACING), this.getPropertyAsFloat(Property.WORD_SPACING));
    }

    private static boolean noPrint(Glyph g) {
        if (!g.hasValidUnicode()) {
            return false;
        }
        int c = g.getUnicode();
        return c >= 0x200b && c <= 0x200f || c >= 0x202a && c <= 0x202e || c == '\u00AD';
    }

    private float getCharWidth(Glyph g, float fontSize, Float hScale, Float characterSpacing, Float wordSpacing) {
        if (hScale == null)
            hScale = 1f;

        float resultWidth = g.getWidth() * fontSize * (float) hScale;
        if (characterSpacing != null) {
            resultWidth += (float) characterSpacing * (float) hScale * TEXT_SPACE_COEFF;
        }
        if (wordSpacing != null && g.hasValidUnicode() && g.getUnicode() == ' ') {
            resultWidth += (float) wordSpacing * (float) hScale * TEXT_SPACE_COEFF;
        }
        return resultWidth;
    }

    private float scaleXAdvance(float xAdvance, float fontSize, Float hScale) {
        return xAdvance * fontSize * (float) hScale;
    }

    private float getGlyphLineWidth(GlyphLine glyphLine, float fontSize, Float hScale, Float characterSpacing, Float wordSpacing) {
        float width = 0;
        for (int i = glyphLine.start; i < glyphLine.end; i++) {
            float charWidth = getCharWidth(glyphLine.get(i), fontSize, hScale, characterSpacing, wordSpacing);
            width += charWidth;
            float xAdvance = (i != glyphLine.start) ? scaleXAdvance(glyphLine.get(i - 1).getXAdvance(), fontSize, hScale) : 0;
            width += xAdvance;
        }
        return width / TEXT_SPACE_COEFF;
    }

    private int[] getWordBoundsForHyphenation(GlyphLine text, int leftTextPos, int rightTextPos, int wordMiddleCharPos) {
        while (wordMiddleCharPos >= leftTextPos && !isGlyphPartOfWordForHyphenation(text.get(wordMiddleCharPos)) && !isWhitespaceGlyph(text.get(wordMiddleCharPos))) {
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
        return g.hasValidUnicode() && (Character.isLetter((char) g.getUnicode()) ||
                Character.isDigit((char) g.getUnicode()) || '\u00ad' == g.getUnicode());
    }

    private boolean isWhitespaceGlyph(Glyph g) {
        return g.hasValidUnicode() && g.getUnicode() == ' ';
    }

    private void convertWaitingStringToGlyphLine() {
        if (strToBeConverted != null) {
            GlyphLine glyphLine = convertToGlyphLine(strToBeConverted);
            setText(glyphLine, glyphLine.start, glyphLine.end);
            strToBeConverted = null;
        }
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

}
