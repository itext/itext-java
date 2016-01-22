package com.itextpdf.model.renderer;

import com.itextpdf.basics.font.FontMetrics;
import com.itextpdf.basics.font.FontProgram;
import com.itextpdf.basics.font.TrueTypeFont;
import com.itextpdf.basics.font.otf.Glyph;
import com.itextpdf.basics.font.otf.GlyphLine;
import com.itextpdf.basics.geom.Rectangle;
import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.core.color.Color;
import com.itextpdf.core.font.PdfFont;
import com.itextpdf.core.font.PdfType0Font;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.tagutils.IAccessibleElement;
import com.itextpdf.core.pdf.tagutils.PdfTagStructure;
import com.itextpdf.model.Property;
import com.itextpdf.model.element.Text;
import com.itextpdf.model.hyphenation.Hyphenation;
import com.itextpdf.model.hyphenation.HyphenationConfig;
import com.itextpdf.model.layout.LayoutArea;
import com.itextpdf.model.layout.LayoutContext;
import com.itextpdf.model.layout.LayoutPosition;
import com.itextpdf.model.layout.LayoutResult;
import com.itextpdf.model.layout.TextLayoutResult;
import com.itextpdf.model.splitting.ISplitCharacters;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TextRenderer extends AbstractRenderer {

    protected static final float TEXT_SPACE_COEFF = FontProgram.UNITS_NORMALIZATION;
    private static final float ITALIC_ANGLE = 0.21256f;
    private static final float BOLD_SIMULATION_STROKE_COEFF = 1/30f;
    private static final float TYPO_ASCENDER_SCALE_COEFF = 1.2f;

    private static final Logger logger = LoggerFactory.getLogger(TextRenderer.class);
    private static final String TYPOGRAPHY_PACKAGE = "com.itextpdf.typography.";

    protected float yLineOffset;
    protected byte[] levels;

    protected GlyphLine text;
    protected GlyphLine line;
    protected String strToBeConverted;

    protected boolean otfFeaturesApplied = false;

    protected float tabAnchorCharacterPosition = -1;

    public TextRenderer(Text textElement) {
        this(textElement, textElement.getText());
    }

    public TextRenderer(Text textElement, String text) {
        super(textElement);
        this.strToBeConverted = text;
    }

    @Override
    public TextLayoutResult layout(LayoutContext layoutContext) {
        convertWaitingStringToGlyphLine();

        LayoutArea area = layoutContext.getArea();
        Rectangle layoutBox = applyMargins(area.getBBox().clone(), false);
        applyBorderBox(layoutBox, false);

        occupiedArea = new LayoutArea(area.getPageNumber(), new Rectangle(layoutBox.getX(), layoutBox.getY() + layoutBox.getHeight(), 0, 0));

        boolean anythingPlaced = false;

        int currentTextPos = text.start;
        float fontSize = getPropertyAsFloat(Property.FONT_SIZE);
        float textRise = getPropertyAsFloat(Property.TEXT_RISE);
        Float characterSpacing = getPropertyAsFloat(Property.CHARACTER_SPACING);
        Float wordSpacing = getPropertyAsFloat(Property.WORD_SPACING);
        PdfFont font = getPropertyAsFont(Property.FONT);
        Float hScale = getProperty(Property.HORIZONTAL_SCALING);
        Property.FontKerning fontKerning = getProperty(Property.FONT_KERNING);
        ISplitCharacters splitCharacters = getProperty(Property.SPLIT_CHARACTERS);
        float italicSkewAddition = Boolean.valueOf(true).equals(getPropertyAsBoolean(Property.ITALIC_SIMULATION)) ? ITALIC_ANGLE * fontSize : 0;
        float boldSimulationAddition = Boolean.valueOf(true).equals(getPropertyAsBoolean(Property.BOLD_SIMULATION)) ? BOLD_SIMULATION_STROKE_COEFF * fontSize : 0;
        Character.UnicodeScript script = getProperty(Property.FONT_SCRIPT);
        Property.BaseDirection baseDirection = getProperty(Property.BASE_DIRECTION);

        boolean typographyModuleInitialized = checkTypographyModulePresence();

        if (!otfFeaturesApplied && script != null && isOtfFont(font)) {
            if (!typographyModuleInitialized) {
                logger.warn("Cannot find advanced typography module, which was implicitly required by one of the model properties");
            } else {
                callMethod(TYPOGRAPHY_PACKAGE + "shaping.Shaper", "applyOtfScript", new Class[]{TrueTypeFont.class, GlyphLine.class, Character.UnicodeScript.class},
                        font.getFontProgram(), text, script);
                //Shaper.applyOtfScript((TrueTypeFont)font.getFontProgram(), text, script);
            }
        }

        if (!otfFeaturesApplied && fontKerning == Property.FontKerning.YES) {
            if (!typographyModuleInitialized) {
                logger.warn("Cannot find advanced typography module, which was implicitly required by one of the model properties");
            } else {
                callMethod(TYPOGRAPHY_PACKAGE + "shaping.Shaper", "applyKerning", new Class[]{FontProgram.class, GlyphLine.class},
                        font.getFontProgram(), text);
                //Shaper.applyKerning(font.getFontProgram(), text);
            }
        }

        otfFeaturesApplied = true;

        line = new GlyphLine(text);
        line.start = line.end = -1;

        if (levels == null && baseDirection != Property.BaseDirection.NO_BIDI) {
            if (!typographyModuleInitialized) {
                logger.warn("Cannot find advanced typography module, which was implicitly required by one of the model properties");
            } else {
                byte direction;
                switch (baseDirection) {
                    case LEFT_TO_RIGHT:
                        direction = 0;
                        break;
                    case RIGHT_TO_LEFT:
                        direction = 1;
                        break;
                    case DEFAULT_BIDI:
                    default:
                        direction = 2;
                        break;
                }

                int[] unicodeIds = new int[text.end - text.start];
                for (int i = text.start; i < text.end; i++) {
                    assert text.glyphs.get(i).getChars().length > 0;
                    // we assume all the chars will have the same bidi group
                    // we also assume pairing symbols won't get merged with other ones
                    int unicode = text.glyphs.get(i).getChars()[0];
                    unicodeIds[i - text.start] = unicode;
                }
                byte[] types = (byte[]) callMethod(TYPOGRAPHY_PACKAGE + "bidi.BidiCharacterMap", "getCharacterTypes", new Class[]{int[].class, int.class, int.class},
                        unicodeIds, 0, text.end - text.start);
                //byte[] types = BidiCharacterMap.getCharacterTypes(unicodeIds, 0, text.end - text.start;
                byte[] pairTypes = (byte[]) callMethod(TYPOGRAPHY_PACKAGE + "bidi.BidiBracketMap", "getBracketTypes", new Class[]{int[].class, int.class, int.class},
                        unicodeIds, 0, text.end - text.start);
                //byte[] pairTypes = BidiBracketMap.getBracketTypes(unicodeIds, 0, text.end - text.start);
                int[] pairValues = (int[]) callMethod(TYPOGRAPHY_PACKAGE + "bidi.BidiBracketMap", "getBracketValues", new Class[]{int[].class, int.class, int.class},
                        unicodeIds, 0, text.end - text.start);
                //int[] pairValues = BidiBracketMap.getBracketValues(unicodeIds, 0, text.end - text.start);
                Object bidiReorder = callConstructor(TYPOGRAPHY_PACKAGE + "bidi.BidiAlgorithm", new Class[]{byte[].class, byte[].class, int[].class, byte.class},
                        types, pairTypes, pairValues, direction);
                //BidiAlgorithm bidiReorder = new BidiAlgorithm(types, pairTypes, pairValues, direction);
                levels = (byte[]) callMethod(TYPOGRAPHY_PACKAGE + "bidi.BidiAlgorithm", "getLevels", bidiReorder, new Class[]{int[].class},
                        new int[]{text.end - text.start});
                //levels = bidiReorder.getLevels(new int[]{text.end - text.start});
            }
        }

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

        Character tabAnchorCharacter = getProperty(Property.TAB_ANCHOR);

        TextLayoutResult result = null;

        while (currentTextPos < text.end) {
            if (noPrint(text.glyphs.get(currentTextPos))) {
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
                    firstCharacterWhichExceedsAllowedWidth = ind + 1;
                    break;
                }

                Glyph currentGlyph = text.glyphs.get(ind);
                if (noPrint(currentGlyph))
                    continue;

                if (tabAnchorCharacter != null && tabAnchorCharacter == text.glyphs.get(ind).getUnicode().intValue()) {
                    tabAnchorCharacterPosition = currentLineWidth + nonBreakablePartFullWidth;
                    tabAnchorCharacter = null;
                }

                float glyphWidth = getCharWidth(currentGlyph, fontSize, hScale, characterSpacing, wordSpacing) / TEXT_SPACE_COEFF;
                float xAdvance = previousCharPos != -1 ? scaleXAdvance(text.glyphs.get(previousCharPos).getXAdvance(), fontSize, hScale) / TEXT_SPACE_COEFF : 0;
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
                                (Character.isWhitespace(text.glyphs.get(ind + 1).getUnicode()) || Character.isSpaceChar(text.glyphs.get(ind + 1).getUnicode()))) {
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
                    // the line does not fit because of height - full overflow
                    TextRenderer[] splitResult = split(initialLineTextPos);
                    applyBorderBox(occupiedArea.getBBox(), true);
                    applyMargins(occupiedArea.getBBox(), true);
                    return new TextLayoutResult(LayoutResult.NOTHING, occupiedArea, splitResult[0], splitResult[1]);
                } else {
                    // cannot fit a word as a whole

                    boolean wordSplit = false;
                    boolean hyphenationApplied = false;

                    HyphenationConfig hyphenationConfig = getProperty(Property.HYPHENATION);
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
                                    if (currentHyphenationChoicePreTextWidth + italicSkewAddition + boldSimulationAddition <= layoutBox.getWidth()) {
                                        hyphenationApplied = true;

                                        if (line.start == -1) {
                                            line.start = currentTextPos;
                                        }
                                        line.end = Math.max(line.end, currentTextPos + pre.length());
                                        GlyphLine lineCopy = line.copy(line.start, line.end);
                                        lineCopy.glyphs.add(font.getGlyph(hyphenationConfig.getHyphenSymbol()));
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

                    if (nonBreakablePartFullWidth > layoutBox.getWidth() && !anythingPlaced && !hyphenationApplied) {
                        // if the word is too long for a single line we will have to split it
                        wordSplit = true;
                        if (line.start == - 1) {
                            line.start = currentTextPos;
                        }
                        line.end = Math.max(line.end, firstCharacterWhichExceedsAllowedWidth);
                        currentLineAscender = Math.max(currentLineAscender, nonBreakablePartMaxAscender);
                        currentLineDescender = Math.min(currentLineDescender, nonBreakablePartMaxDescender);
                        currentLineHeight = Math.max(currentLineHeight, nonBreakablePartMaxHeight);
                        currentLineWidth += nonBreakablePartWidthWhichDoesNotExceedAllowedWidth;
                        currentTextPos = firstCharacterWhichExceedsAllowedWidth;
                    }

                    if (line.end <= 0) {
                        result = new TextLayoutResult(LayoutResult.NOTHING, occupiedArea, null, this);
                    } else {
                        result = new TextLayoutResult(LayoutResult.PARTIAL, occupiedArea, null, null).setWordHasBeenSplit(wordSplit);
                    }

                    break;
                }
            }
        }

        if (result != null && result.getStatus() == LayoutResult.NOTHING) {
            return result;
        } else {
            if (currentLineHeight > layoutBox.getHeight()) {
                    applyBorderBox(occupiedArea.getBBox(), true);
                    applyMargins(occupiedArea.getBBox(), true);
                    return new TextLayoutResult(LayoutResult.NOTHING, occupiedArea, null, this);
            }

            yLineOffset = currentLineAscender * fontSize / TEXT_SPACE_COEFF;

            occupiedArea.getBBox().moveDown(currentLineHeight);
            occupiedArea.getBBox().setHeight(occupiedArea.getBBox().getHeight() + currentLineHeight);
            occupiedArea.getBBox().setWidth(Math.max(occupiedArea.getBBox().getWidth(), currentLineWidth));
            layoutBox.setHeight(area.getBBox().getHeight() - currentLineHeight);

            occupiedArea.getBBox().setWidth(occupiedArea.getBBox().getWidth() + italicSkewAddition + boldSimulationAddition);
            applyBorderBox(occupiedArea.getBBox(), true);
            applyMargins(occupiedArea.getBBox(), true);

            if (result != null) {
                TextRenderer[] split = split(currentTextPos);
                if (split[1].length() > 0 && split[1].charAt(0) != null && split[1].charAt(0) == '\n')
                    result.setSplitForcedByNewline(true);
                result.setSplitRenderer(split[0]);
                result.setOverflowRenderer(split[1]);
            } else {
                result = new TextLayoutResult(LayoutResult.FULL, occupiedArea, null, null);
            }
        }

        if (baseDirection != Property.BaseDirection.NO_BIDI) {
            if (!typographyModuleInitialized) {
                logger.warn("Cannot find advanced typography module, which was implicitly required by one of the model properties");
            } else {
                byte[] lineLevels = new byte[line.end - line.start];
                System.arraycopy(levels, line.start, lineLevels, 0, line.end - line.start);
                int[] reorder = (int[]) callMethod(TYPOGRAPHY_PACKAGE + "bidi.BidiAlgorithm", "computeReordering", new Class[]{byte[].class},
                        lineLevels);
                //int[] reorder = BidiAlgorithm.computeReordering(lineLevels);
                List<Glyph> reorderedLine = new ArrayList<>(line.end - line.start);
                for (int i = 0; i < line.end - line.start; i++) {
                    reorderedLine.add(line.glyphs.get(line.start + reorder[i]));

                    // Mirror RTL glyphs
                    if (levels[line.start + reorder[i]] % 2 == 1) {
                        if (reorderedLine.get(i).getUnicode() != null) {
                            int pairedBracket = (int) callMethod(TYPOGRAPHY_PACKAGE + "bidi.BidiBracketMap", "getPairedBracket", new Class[]{int.class},
                                    reorderedLine.get(i).getUnicode());
                            //BidiBracketMap.getPairedBracket(reorderedLine.get(i).getUnicode())
                            reorderedLine.set(i, font.getGlyph(pairedBracket));
                        }
                    }
                }
                line = new GlyphLine(reorderedLine, 0, line.end - line.start);

                // Don't forget to update the line for the split renderer
                if (result.getStatus() == LayoutResult.PARTIAL) {
                    ((TextRenderer) result.getSplitRenderer()).line = line;
                }
            }
        }

        return result;
    }

    @Override
    public void draw(DrawContext drawContext) {
        super.draw(drawContext);

        boolean isTagged = drawContext.getDocument().isTagged() && getModelElement() instanceof IAccessibleElement;
        PdfTagStructure tagStructure = null;
        IAccessibleElement accessibleElement = null;
        if (isTagged) {
            tagStructure = drawContext.getDocument().getTagStructure();
            accessibleElement = (IAccessibleElement) getModelElement();
            if (!drawContext.getDocument().getTagStructure().isConnectedToTag(accessibleElement)) {
                AccessibleAttributesApplier.applyLayoutAttributes(accessibleElement.getRole(), this, drawContext.getDocument());
            }
            tagStructure.addTag(accessibleElement, true);
        }

        int position = getPropertyAsInteger(Property.POSITION);
        if (position == LayoutPosition.RELATIVE) {
            applyAbsolutePositioningTranslation(false);
        }

        float leftBBoxX = occupiedArea.getBBox().getX();

        if (line.end > line.start) {
            PdfFont font = getPropertyAsFont(Property.FONT);
            float fontSize = getPropertyAsFloat(Property.FONT_SIZE);
            Color fontColor = getPropertyAsColor(Property.FONT_COLOR);
            Integer textRenderingMode = getProperty(Property.TEXT_RENDERING_MODE);
            Float textRise = getPropertyAsFloat(Property.TEXT_RISE);
            Float characterSpacing = getPropertyAsFloat(Property.CHARACTER_SPACING);
            Float wordSpacing = getPropertyAsFloat(Property.WORD_SPACING);
            Float horizontalScaling = getProperty(Property.HORIZONTAL_SCALING);
            boolean italicSimulation = Boolean.valueOf(true).equals(getPropertyAsBoolean(Property.ITALIC_SIMULATION));
            boolean boldSimulation = Boolean.valueOf(true).equals(getPropertyAsBoolean(Property.BOLD_SIMULATION));
            Float strokeWidth = null;

            if (boldSimulation) {
                textRenderingMode = Property.TextRenderingMode.TEXT_RENDERING_MODE_FILL_STROKE;
                strokeWidth = fontSize / 30;
            }

            PdfCanvas canvas = drawContext.getCanvas();
            if (isTagged) {
                canvas.openTag(tagStructure.getTagReference());
            }
            canvas.saveState().beginText().setFontAndSize(font, fontSize);

            if (italicSimulation) {
                canvas.setTextMatrix(1, 0, ITALIC_ANGLE, 1, leftBBoxX, getYLine());
            } else {
                canvas.moveText(leftBBoxX, getYLine());
            }

            if (textRenderingMode != Property.TextRenderingMode.TEXT_RENDERING_MODE_FILL) {
                canvas.setTextRenderingMode(textRenderingMode);
            }
            if (textRenderingMode == Property.TextRenderingMode.TEXT_RENDERING_MODE_STROKE || textRenderingMode == Property.TextRenderingMode.TEXT_RENDERING_MODE_FILL_STROKE) {
                if (strokeWidth == null) {
                    strokeWidth = getPropertyAsFloat(Property.STROKE_WIDTH);
                }
                if (strokeWidth != null && strokeWidth != 1f) {
                    canvas.setLineWidth(strokeWidth);
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
                canvas.setTextRise(textRise);
            if (characterSpacing != null && characterSpacing != 0)
                canvas.setCharacterSpacing(characterSpacing);
            if (wordSpacing != null && wordSpacing != 0)
                canvas.setWordSpacing(wordSpacing);
            if (horizontalScaling != null && horizontalScaling != 1)
                canvas.setHorizontalScaling(horizontalScaling * 100);

            GlyphLine output = line.filter(new GlyphLine.GlyphLineFilter() {
                @Override
                public boolean accept(Glyph glyph) {
                    return !noPrint(glyph);
                }
            });

            canvas.showText(output);
            canvas.endText().restoreState();
            if (isTagged) {
                canvas.closeTag();
            }

            Object underlines = getProperty(Property.UNDERLINE);
            if (underlines instanceof List) {
                for (Object underline : (List)underlines) {
                    if (underline instanceof Property.Underline) {
                        drawSingleUnderline((Property.Underline) underline, fontColor, canvas, fontSize, italicSimulation ? ITALIC_ANGLE : 0);
                    }
                }
            } else if (underlines instanceof Property.Underline) {
                drawSingleUnderline((Property.Underline) underlines, fontColor, canvas, fontSize, italicSimulation ? ITALIC_ANGLE : 0);
            }
        }

        if (position == LayoutPosition.RELATIVE) {
            applyAbsolutePositioningTranslation(false);
        }

        if (isTagged) {
            tagStructure.moveToParent();
            if (isLastRendererForModelElement) {
                tagStructure.removeConnectionToTag(accessibleElement);
            }
        }
    }

    @Override
    public void drawBackground(DrawContext drawContext) {
        Property.Background background = getProperty(Property.BACKGROUND);
        Float textRise = getPropertyAsFloat(Property.TEXT_RISE);
        float bottomBBoxY = occupiedArea.getBBox().getY();
        float leftBBoxX = occupiedArea.getBBox().getX();
        if (background != null) {
            drawContext.getCanvas().saveState().setFillColor(background.getColor());
            drawContext.getCanvas().rectangle(leftBBoxX - background.getExtraLeft(), bottomBBoxY + textRise - background.getExtraBottom(),
                    occupiedArea.getBBox().getWidth() + background.getExtraLeft() + background.getExtraRight(),
                    occupiedArea.getBBox().getHeight() - textRise + background.getExtraTop() + background.getExtraBottom());
            drawContext.getCanvas().fill().restoreState();
        }
    }

    @Override
    public <T> T getDefaultProperty(Property property) {
        switch (property) {
            case HORIZONTAL_SCALING:
                return (T) Float.valueOf(1);
            default:
                return super.getDefaultProperty(property);
        }
    }

    public void trimFirst() {
        convertWaitingStringToGlyphLine();

        Glyph glyph;
        while (text.start < text.end && (glyph = text.glyphs.get(text.start)).getUnicode() != null && Character.isWhitespace(glyph.getUnicode())) {
            text.start++;
        }
    }

    /**
     * Returns the amount of space in points which the text was trimmed by.
     */
    public float trimLast() {
        float trimmedSpace = 0;

        if (line.end <= 0)
            return trimmedSpace;

        float fontSize = getPropertyAsFloat(Property.FONT_SIZE);
        Float characterSpacing = getPropertyAsFloat(Property.CHARACTER_SPACING);
        Float wordSpacing = getPropertyAsFloat(Property.WORD_SPACING);
        Float hScale = getProperty(Property.HORIZONTAL_SCALING);

        int firstNonSpaceCharIndex = line.end - 1;
        while (firstNonSpaceCharIndex >= line.start) {
            Glyph currentGlyph = line.glyphs.get(firstNonSpaceCharIndex);
            if (currentGlyph.getUnicode() == null || !Character.isWhitespace(currentGlyph.getUnicode())) {
                break;
            }

            float currentCharWidth = getCharWidth(currentGlyph, fontSize, hScale, characterSpacing, wordSpacing) / TEXT_SPACE_COEFF;
            float xAdvance = firstNonSpaceCharIndex > line.start ? scaleXAdvance(line.glyphs.get(firstNonSpaceCharIndex - 1).getXAdvance(), fontSize, hScale) / TEXT_SPACE_COEFF : 0;
            trimmedSpace += currentCharWidth - xAdvance;
            occupiedArea.getBBox().setWidth(occupiedArea.getBBox().getWidth() - currentCharWidth);

            firstNonSpaceCharIndex--;
        }

        line.end = firstNonSpaceCharIndex + 1;

        return trimmedSpace;
    }

    public float getAscent() {
        return yLineOffset;
    }

    public float getDescent() {
        return -(occupiedArea.getBBox().getHeight() - yLineOffset - getPropertyAsFloat(Property.TEXT_RISE));
    }

    public float getYLine() {
        return occupiedArea.getBBox().getY() + occupiedArea.getBBox().getHeight() - yLineOffset - getPropertyAsFloat(Property.TEXT_RISE);
    }

    public void moveYLineTo(float y) {
        float curYLine = getYLine();
        float delta = y - curYLine;
        occupiedArea.getBBox().setY(occupiedArea.getBBox().getY() + delta);
    }

    public void setText(String text) {
        GlyphLine glyphLine = convertToGlyphLine(text);
        setText(glyphLine, glyphLine.start, glyphLine.end);
    }

    public void setText(GlyphLine text, int leftPos, int rightPos) {
        this.text = new GlyphLine(text);
        this.text.start = leftPos;
        this.text.end = rightPos;
        this.levels = null;
        this.otfFeaturesApplied = false;
    }

    /**
     * The length of the whole text assigned to this renderer.
     */
    public int length() {
        return text == null ? 0 : text.end - text.start;
    }

    @Override
    public String toString() {
        convertWaitingStringToGlyphLine();
        return line != null ? line.toUnicodeString(line.start, line.end) : text.toUnicodeString(text.start, text.end);
    }

    /**
     * Gets char code at given position for the text belonging to this renderer.
     * @param pos the position in range [0; length())
     * @return Unicode char code
     */
    public Integer charAt(int pos) {
        return text.glyphs.get(pos + text.start).getUnicode();
    }

    public float getTabAnchorCharacterPosition(){
        return tabAnchorCharacterPosition;
    }

    @Override
    public TextRenderer getNextRenderer() {
        return new TextRenderer((Text) modelElement, null);
    }

    private boolean checkTypographyModulePresence() {
        boolean moduleFound = false;
        try {
            Class.forName("com.itextpdf.typography.shaping.Shaper");
            moduleFound = true;
        } catch (ClassNotFoundException ignored) {
        }
        return moduleFound;
    }

    private Object callMethod(String className, String methodName, Class[] parameterTypes, Object... args) {
        return callMethod(className, methodName, null, parameterTypes, args);
    }

    private Object callMethod(String className, String methodName, Object target, Class[] parameterTypes, Object... args) {
        try {
            Method method = Class.forName(className).getMethod(methodName, parameterTypes);
            return method.invoke(target, args);
        } catch (NoSuchMethodException e) {
            logger.warn(String.format("Cannot find method %s for class %s", methodName, className));
        } catch (ClassNotFoundException e) {
            logger.warn(String.format("Cannot find class %s", className));
        } catch (Exception ignored) {
        }
        return null;
    }

    private Object callConstructor(String className, Class[] parameterTypes, Object... args) {
        Constructor constructor = null;
        try {
            constructor = Class.forName(className).getConstructor(parameterTypes);
            return constructor.newInstance(args);
        } catch (NoSuchMethodException e) {
            logger.warn(String.format("Cannot find constructor for class %s", className));
        } catch (ClassNotFoundException e) {
            logger.warn(String.format("Cannot find class %s", className));
        } catch (Exception ignored) {
        }
        return null;
    }

    private boolean isNewLine(GlyphLine text, int ind) {
        return text.glyphs.get(ind).getUnicode() != null && text.glyphs.get(ind).getUnicode() == '\n';
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
     * Returns the length of the {@see line} which is the result of the layout call.
     */
    protected int lineLength() {
        return line.end > 0 ? line.end - line.start: 0;
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
            if (currentGlyph.getUnicode() != null && currentGlyph.getUnicode() == ' ') {
                spaces++;
            }
        }
        return spaces;
    }

    protected TextRenderer createSplitRenderer() {
        return getNextRenderer();
    }

    protected TextRenderer createOverflowRenderer() {
        return getNextRenderer();
    }

    protected TextRenderer[] split(int initialOverflowTextPos) {
        TextRenderer splitRenderer = createSplitRenderer();
        splitRenderer.setText(text, text.start, initialOverflowTextPos);
        splitRenderer.line = line;
        splitRenderer.occupiedArea = occupiedArea.clone();
        splitRenderer.parent = parent;
        splitRenderer.yLineOffset = yLineOffset;
        splitRenderer.levels = levels;
        splitRenderer.otfFeaturesApplied = otfFeaturesApplied;
        splitRenderer.isLastRendererForModelElement = false;

        TextRenderer overflowRenderer = createOverflowRenderer();
        overflowRenderer.setText(text, initialOverflowTextPos, text.end);
        overflowRenderer.levels = levels;
        overflowRenderer.otfFeaturesApplied = otfFeaturesApplied;
        overflowRenderer.parent = parent;

        return new TextRenderer[] {splitRenderer, overflowRenderer};
    }

    protected void drawSingleUnderline(Property.Underline underline, Color fontStrokeColor, PdfCanvas canvas, float fontSize, float italicAngleTan) {
        Color underlineColor = underline.getColor() != null ? underline.getColor() : fontStrokeColor;
        canvas.saveState();

        if (underlineColor != null) {
            canvas.setStrokeColor(underlineColor);
        }
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

    private static boolean noPrint(Glyph g) {
        if (g.getUnicode() == null) {
            return false;
        }
        int c = g.getUnicode();
        return c >= 0x200b && c <= 0x200f || c >= 0x202a && c <= 0x202e || c == '\u00AD';
    }

    private float getCharWidth(Glyph g, float fontSize, Float hScale, Float characterSpacing, Float wordSpacing) {
        if (hScale == null)
            hScale = 1f;

        float resultWidth = g.getWidth() * fontSize * hScale;
        if (characterSpacing != null) {
            resultWidth += characterSpacing * hScale * TEXT_SPACE_COEFF;
        }
        if (wordSpacing != null && g.getUnicode() != null && g.getUnicode() == ' ') {
            resultWidth += wordSpacing * hScale * TEXT_SPACE_COEFF;
        }
        return resultWidth;
    }

    private float scaleXAdvance(float xAdvance, float fontSize, Float hScale) {
        return xAdvance * fontSize * hScale;
    }

    private float getGlyphLineWidth(GlyphLine glyphLine, float fontSize, Float hScale, Float characterSpacing, Float wordSpacing) {
        float width = 0;
        for (int i = glyphLine.start; i < glyphLine.end; i++) {
            float charWidth = getCharWidth(glyphLine.glyphs.get(i), fontSize, hScale, characterSpacing, wordSpacing);
            width += charWidth;
            float xAdvance = (i != glyphLine.start) ? scaleXAdvance(glyphLine.glyphs.get(i - 1).getXAdvance(), fontSize, hScale) : 0;
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
        return g.getUnicode() != null && (Character.isLetter(g.getUnicode()) ||
            Character.isDigit(g.getUnicode()) || '\u00ad' == g.getUnicode());
    }

    private boolean isWhitespaceGlyph(Glyph g) {
        return g.getUnicode() != null && g.getUnicode() == ' ';
    }

    private void convertWaitingStringToGlyphLine() {
        if (strToBeConverted != null) {
            GlyphLine glyphLine = convertToGlyphLine(strToBeConverted);
            setText(glyphLine, glyphLine.start, glyphLine.end);
            strToBeConverted = null;
        }
    }
}
