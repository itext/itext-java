package com.itextpdf.layout.renderer;

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
import com.itextpdf.kernel.pdf.tagutils.PdfTagStructure;
import com.itextpdf.layout.Property;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.hyphenation.Hyphenation;
import com.itextpdf.layout.hyphenation.HyphenationConfig;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutPosition;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.layout.TextLayoutResult;
import com.itextpdf.layout.splitting.ISplitCharacters;

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class represents the {@link IRenderer renderer} object for a {@link Text}
 * object. It will draw the glyphs of the textual content on the {@link DrawingContext}.
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
     * @param textElement the {@link Text} which this object should manage
     */
    public TextRenderer(Text textElement) {
        this(textElement, textElement.getText());
    }

    /**
     * Creates a TextRenderer from its corresponding layout object, with a custom
     * text to replace the contents of the {@link Text}.
     * @param textElement the {@link Text} which this object should manage
     * @param text the replacement text
     */
    public TextRenderer(Text textElement, String text) {
        super(textElement);
        this.strToBeConverted = text;
    }

    protected TextRenderer(TextRenderer other) {
        super(other);
        this.text = other.text;
        this.line = other.line;
        this.strToBeConverted = other.strToBeConverted;
        this.otfFeaturesApplied = other.otfFeaturesApplied;
        this.tabAnchorCharacterPosition = other.tabAnchorCharacterPosition;
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
        ISplitCharacters splitCharacters = getProperty(Property.SPLIT_CHARACTERS);
        float italicSkewAddition = Boolean.valueOf(true).equals(getPropertyAsBoolean(Property.ITALIC_SIMULATION)) ? ITALIC_ANGLE * fontSize : 0;
        float boldSimulationAddition = Boolean.valueOf(true).equals(getPropertyAsBoolean(Property.BOLD_SIMULATION)) ? BOLD_SIMULATION_STROKE_COEFF * fontSize : 0;

        applyOtf();
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
                        if (line.start == -1) {
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

        return result;
    }

    public void applyOtf() {
        convertWaitingStringToGlyphLine();
        Character.UnicodeScript script = getProperty(Property.FONT_SCRIPT);
        Property.FontKerning fontKerning = getProperty(Property.FONT_KERNING);
        PdfFont font = getPropertyAsFont(Property.FONT);
        if (!otfFeaturesApplied) {
            if (script == null && TypographyUtils.isTypographyModuleInitialized()) {
                // Try to autodetect complex script.
                Collection<Character.UnicodeScript> supportedScripts = TypographyUtils.getSupportedScripts();
                if (supportedScripts != null) {
                    Map<Character.UnicodeScript, Integer> scriptFrequency = new EnumMap<>(Character.UnicodeScript.class);
                    for (int i = text.start; i < text.end; i++) {
                        Integer unicode = text.get(i).getUnicode();
                        Character.UnicodeScript glyphScript = unicode != null ? Character.UnicodeScript.of(unicode) : null;
                        if (glyphScript != null && supportedScripts.contains(glyphScript)) {
                            if (scriptFrequency.containsKey(glyphScript)) {
                                scriptFrequency.put(glyphScript, scriptFrequency.get(glyphScript));
                            } else {
                                scriptFrequency.put(glyphScript, 1);
                            }
                        }
                    }
                    int max = 0;
                    Character.UnicodeScript selectScript = null;
                    for (Map.Entry<Character.UnicodeScript, Integer> entry : scriptFrequency.entrySet()) {
                        if (entry.getValue() > max) {
                            max = entry.getValue();
                            selectScript = entry.getKey();
                        }
                    }
                    if (selectScript != null) {
                        script = selectScript;
                    }
                }
            }

            if (isOtfFont(font) && script != null) {
                TypographyUtils.applyOtfScript(font.getFontProgram(), text, script);
            }

            if (fontKerning == Property.FontKerning.YES) {
                TypographyUtils.applyKerning(font.getFontProgram(), text);
            }

            otfFeaturesApplied = true;
        }
    }

    @Override
    public void draw(DrawContext drawContext) {
        super.draw(drawContext);

        PdfDocument document = drawContext.getDocument();
        boolean isTagged = drawContext.isTaggingEnabled() && getModelElement() instanceof IAccessibleElement;
        boolean isArtifact = false;
        PdfTagStructure tagStructure = null;
        IAccessibleElement accessibleElement = null;
        if (isTagged) {
            accessibleElement = (IAccessibleElement) getModelElement();
            PdfName role = accessibleElement.getRole();
            if (role != null && !PdfName.Artifact.equals(role)) {
                tagStructure = document.getTagStructure();
                if (!document.getTagStructure().isConnectedToTag(accessibleElement)) {
                    AccessibleAttributesApplier.applyLayoutAttributes(accessibleElement.getRole(), this, document);
                }
                tagStructure.addTag(accessibleElement, true);
            } else {
                isTagged = false;
                if (PdfName.Artifact.equals(role)) {
                    isArtifact = true;
                }
            }
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
            Float[] skew = getProperty(Property.SKEW);
            boolean italicSimulation = Boolean.valueOf(true).equals(getPropertyAsBoolean(Property.ITALIC_SIMULATION));
            boolean boldSimulation = Boolean.valueOf(true).equals(getPropertyAsBoolean(Property.BOLD_SIMULATION));
            Float strokeWidth = null;

            if (boldSimulation) {
                textRenderingMode = PdfCanvasConstants.TextRenderingMode.FILL_STROKE;
                strokeWidth = fontSize / 30;
            }

            PdfCanvas canvas = drawContext.getCanvas();
            if (isTagged) {
                canvas.openTag(tagStructure.getTagReference());
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
                canvas.setTextRenderingMode(textRenderingMode);
            }
            if (textRenderingMode == PdfCanvasConstants.TextRenderingMode.STROKE || textRenderingMode == PdfCanvasConstants.TextRenderingMode.FILL_STROKE) {
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
            if (isTagged || isArtifact) {
                canvas.closeTag();
            }

            Object underlines = getProperty(Property.UNDERLINE);
            if (underlines instanceof List) {
                for (Object underline : (List) underlines) {
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
            boolean isTagged = drawContext.isTaggingEnabled() && getModelElement() instanceof IAccessibleElement;
            PdfCanvas canvas = drawContext.getCanvas();
            if (isTagged) {
                canvas.openTag(new CanvasArtifact());
            }
            canvas.saveState().setFillColor(background.getColor());
            canvas.rectangle(leftBBoxX - background.getExtraLeft(), bottomBBoxY + textRise - background.getExtraBottom(),
                    occupiedArea.getBBox().getWidth() + background.getExtraLeft() + background.getExtraRight(),
                    occupiedArea.getBBox().getHeight() - textRise + background.getExtraTop() + background.getExtraBottom());
            canvas.fill().restoreState();
            if (isTagged) {
                canvas.closeTag();
            }
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

    /**
     * Trims any whitespace characters from the start of the {@link Glyphline}
     * to be rendered.
     */
    public void trimFirst() {
        convertWaitingStringToGlyphLine();

        if (text != null) {
            Glyph glyph;
            while (text.start < text.end && (glyph = text.glyphs.get(text.start)).getUnicode() != null && Character.isWhitespace(glyph.getUnicode())) {
                text.start++;
            }
        }
    }

    /**
     * Trims any whitespace characters from the end of the {@link Glyphline} to
     * be rendered.
     * @return the amount of space in points which the text was trimmed by
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

    /**
     * Gets the maximum offset above the base line that this Text extends to.
     * @return the upwards vertical offset of this {@link Text}
     */
    public float getAscent() {
        return yLineOffset;
    }
    
    /**
     * Gets the maximum offset below the base line that this Text extends to.
     * @return the downwards vertical offset of this {@link Text}
     */
    public float getDescent() {
        return -(occupiedArea.getBBox().getHeight() - yLineOffset - getPropertyAsFloat(Property.TEXT_RISE));
    }

    /**
     * Gets the position on the canvas of the imaginary horizontal line upon which
     * the {@link Text}'s contents will be written.
     * @return the y position of this text on the {@link DrawingContext}
     */
    public float getYLine() {
        return occupiedArea.getBBox().getY() + occupiedArea.getBBox().getHeight() - yLineOffset - getPropertyAsFloat(Property.TEXT_RISE);
    }

    /**
     * Moves the vertical position to the parameter's value.
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
     * @param text a {@link GlyphLine}
     * @param leftPos the leftmost end of the GlyphLine
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
     * @return the text length
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
     *
     * @param pos the position in range [0; length())
     * @return Unicode char code
     */
    public Integer charAt(int pos) {
        return text.glyphs.get(pos + text.start).getUnicode();
    }

    public float getTabAnchorCharacterPosition() {
        return tabAnchorCharacterPosition;
    }

    @Override
    public TextRenderer getNextRenderer() {
        return new TextRenderer((Text) modelElement, null);
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
        splitRenderer.otfFeaturesApplied = otfFeaturesApplied;
        splitRenderer.isLastRendererForModelElement = false;

        TextRenderer overflowRenderer = createOverflowRenderer();
        overflowRenderer.setText(text, initialOverflowTextPos, text.end);
        overflowRenderer.otfFeaturesApplied = otfFeaturesApplied;
        overflowRenderer.parent = parent;

        return new TextRenderer[]{splitRenderer, overflowRenderer};
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

    protected float calculateLineWidth() {
        return getGlyphLineWidth(line, getPropertyAsFloat(Property.FONT_SIZE), getPropertyAsFloat(Property.HORIZONTAL_SCALING),
                getPropertyAsFloat(Property.CHARACTER_SPACING), getPropertyAsFloat(Property.WORD_SPACING));
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
