package com.itextpdf.model.renderer;

import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.canvas.color.Color;
import com.itextpdf.core.font.PdfFont;
import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.model.Property;
import com.itextpdf.model.element.Text;
import com.itextpdf.model.layout.*;


public class TextRenderer extends AbstractRenderer {

    // TODO Kerning

    protected static final float TEXT_SPACE_COEFF = 1000;
    protected String text;
    protected String line;
    protected float yLineOffset;

    public TextRenderer(Text textElement) {
        this (textElement, textElement.getText());
    }

    public TextRenderer(Text textElement, String text) {
        super(textElement);
        this.text = text;
    }

    public static void showTextAligned(final PdfCanvas canvas, final int alignment, final String text, final float x, final float y, final float rotation) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public TextLayoutResult layout(LayoutContext layoutContext) {
        LayoutArea area = layoutContext.getArea();
        Rectangle layoutBox = applyMargins(area.getBBox().clone(), false);

        occupiedArea = new LayoutArea(area.getPageNumber(), new Rectangle(layoutBox.getX(), layoutBox.getY() + layoutBox.getHeight(), 0, 0));

        boolean anythingPlaced = false;

        int currentTextPos = 0;
        float fontSize = getPropertyAsFloat(Property.FONT_SIZE);
        float textRise = getPropertyAsFloat(Property.TEXT_RISE);
        Float characterSpacing = getPropertyAsFloat(Property.CHARACTER_SPACING);
        Float wordSpacing = getPropertyAsFloat(Property.WORD_SPACING);
        PdfFont font = getPropertyAsFont(Property.FONT);
        Float hScale = getProperty(Property.HORIZONTAL_SCALING);
        float ascender = 800;
        float descender = -200;

        StringBuilder currentLine = new StringBuilder();

        float currentLineAscender = 0;
        float currentLineDescender = 0;
        float currentLineHeight = 0;
        int initialLineTextPos = currentTextPos;
        float currentLineWidth = 0;

        while (currentTextPos < text.length()) {
            int currentCharCode = getCharCode(currentTextPos);
            if (noPrint(currentCharCode)) {
                currentTextPos++;
                continue;
            }

            int nonBreakablePartEnd = text.length() - 1;
            float nonBreakablePartFullWidth = 0;
            float nonBreakablePartWidthWhichDoesNotExceedAllowedWidth = 0;
            float nonBreakablePartMaxAscender = 0;
            float nonBreakablePartMaxDescender = 0;
            float nonBreakablePartMaxHeight = 0;
            int firstCharacterWhichExceedsAllowedWidth = -1;
            for (int ind = currentTextPos; ind < text.length(); ind++) {
                if (text.charAt(ind) == '\n') {
                    firstCharacterWhichExceedsAllowedWidth = ind + 1;
                    break;
                }

                int charCode = getCharCode(ind);
                if (noPrint(charCode))
                    continue;
                float glyphWidth = getCharWidth(charCode, font, fontSize, hScale, characterSpacing, wordSpacing) / TEXT_SPACE_COEFF;
                if ((nonBreakablePartFullWidth + glyphWidth) > layoutBox.getWidth() - currentLineWidth && firstCharacterWhichExceedsAllowedWidth == -1) {
                    firstCharacterWhichExceedsAllowedWidth = ind;
                }
                if (firstCharacterWhichExceedsAllowedWidth == -1)
                    nonBreakablePartWidthWhichDoesNotExceedAllowedWidth += glyphWidth;

                nonBreakablePartFullWidth += glyphWidth;
                nonBreakablePartMaxAscender = ascender;
                nonBreakablePartMaxDescender = descender;
                nonBreakablePartMaxHeight = (nonBreakablePartMaxAscender - nonBreakablePartMaxDescender) * fontSize / TEXT_SPACE_COEFF + textRise;

                if (nonBreakablePartFullWidth > layoutBox.getWidth()) {
                    // we have extracted all the information we wanted and we do not want to continue.
                    // we will have to split the word anyway.
                    break;
                }

                if (Character.isWhitespace(text.charAt(ind)) || ind + 1 == text.length() || Character.isWhitespace(text.charAt(ind + 1))) {
                    nonBreakablePartEnd = ind;
                    break;
                }
            }

            if (firstCharacterWhichExceedsAllowedWidth == -1) {
                // can fit the whole word in a line
                currentLine.append(text.substring(currentTextPos, nonBreakablePartEnd + 1));
                currentLineAscender = Math.max(currentLineAscender, nonBreakablePartMaxAscender);
                currentLineDescender = Math.min(currentLineDescender, nonBreakablePartMaxDescender);
                currentLineHeight = Math.max(currentLineHeight, nonBreakablePartMaxHeight);
                currentTextPos = nonBreakablePartEnd + 1;
                currentLineWidth += nonBreakablePartFullWidth;
                anythingPlaced = true;
            } else {
                // must split the word

                // check if line height exceeds the allowed height
                if (Math.max(currentLineHeight, nonBreakablePartMaxHeight) > layoutBox.getHeight()) {
                    // the line does not fit because of height - full overflow
                    TextRenderer[] splitResult = split(initialLineTextPos);
                    applyMargins(occupiedArea.getBBox(), true);
                    return new TextLayoutResult(LayoutResult.NOTHING, occupiedArea, splitResult[0], splitResult[1]);
                } else {
                    boolean wordSplit = false;
                    if (nonBreakablePartFullWidth > layoutBox.getWidth() && !anythingPlaced) {
                        // if the word is too long for a single line we will have to split it
                        wordSplit = true;
                        currentLine.append(text.substring(currentTextPos, firstCharacterWhichExceedsAllowedWidth));
                        currentLineAscender = Math.max(currentLineAscender, nonBreakablePartMaxAscender);
                        currentLineDescender = Math.min(currentLineDescender, nonBreakablePartMaxDescender);
                        currentLineHeight = Math.max(currentLineHeight, nonBreakablePartMaxHeight);
                        currentLineWidth += nonBreakablePartWidthWhichDoesNotExceedAllowedWidth;
                        currentTextPos = firstCharacterWhichExceedsAllowedWidth;
                    }

                    line = currentLine.toString();
                    yLineOffset = currentLineAscender * fontSize / TEXT_SPACE_COEFF;

                    occupiedArea.getBBox().moveDown(currentLineHeight);
                    occupiedArea.getBBox().setHeight(occupiedArea.getBBox().getHeight() + currentLineHeight);
                    occupiedArea.getBBox().setWidth(Math.max(occupiedArea.getBBox().getWidth(), currentLineWidth));
                    layoutBox.setHeight(area.getBBox().getHeight() - currentLineHeight);

                    currentLine.setLength(0);

                    TextRenderer[] split = split(currentTextPos);
                    applyMargins(occupiedArea.getBBox(), true);
                    TextLayoutResult result = new TextLayoutResult(LayoutResult.PARTIAL, occupiedArea, split[0], split[1]).setWordHasBeenSplit(wordSplit);
                    if (split[1].text.length() > 0 && split[1].text.charAt(0) == '\n')
                        result.setSplitForcedByNewline(true);
                    return result;
                }
            }
        }

        if (currentLine.length() != 0) {
            if (currentLineHeight > layoutBox.getHeight()) {
                applyMargins(occupiedArea.getBBox(), true);
                return new TextLayoutResult(LayoutResult.NOTHING, occupiedArea, null, this);
            }

            line = currentLine.toString();
            yLineOffset = currentLineAscender * fontSize / TEXT_SPACE_COEFF;
            currentLine.setLength(0);

            occupiedArea.getBBox().moveDown(currentLineHeight);
            occupiedArea.getBBox().setHeight(occupiedArea.getBBox().getHeight() + currentLineHeight);
            occupiedArea.getBBox().setWidth(Math.max(occupiedArea.getBBox().getWidth(), currentLineWidth));
            layoutBox.setHeight(area.getBBox().getHeight() - currentLineHeight);
        }

        applyMargins(occupiedArea.getBBox(), true);
        return new TextLayoutResult(LayoutResult.FULL, occupiedArea, null, null);
    }

    @Override
    public void draw(PdfDocument document, PdfCanvas canvas) {
        super.draw(document, canvas);

        int position = getPropertyAsInteger(Property.POSITION);
        if (position == LayoutPosition.RELATIVE) {
            applyAbsolutePositioningTranslation(false);
        }

        float leftBBoxX = occupiedArea.getBBox().getX();

        if (line != null && line.length() != 0) {
            PdfFont font = getPropertyAsFont(Property.FONT);
            float fontSize = getPropertyAsFloat(Property.FONT_SIZE);
            Color textColor = getPropertyAsColor(Property.FONT_COLOR);
            int textRenderingMode = (int) getProperty(Property.TEXT_RENDERING_MODE) & 3;
            Float textRise = getPropertyAsFloat(Property.TEXT_RISE);
            Float characterSpacing = getPropertyAsFloat(Property.CHARACTER_SPACING);
            Float wordSpacing = getPropertyAsFloat(Property.WORD_SPACING);

            canvas.saveState().beginText().setFontAndSize(font, fontSize).moveText(leftBBoxX, getYLine());
            if (textRenderingMode != Property.TextRenderingMode.TEXT_RENDERING_MODE_FILL) {
                canvas.setTextRenderingMode(textRenderingMode);
            }
            if (textRenderingMode == Property.TextRenderingMode.TEXT_RENDERING_MODE_STROKE || textRenderingMode == Property.TextRenderingMode.TEXT_RENDERING_MODE_FILL_STROKE) {
                Float strokeWidth = getPropertyAsFloat(Property.STROKE_WIDTH);
                if (strokeWidth != null && strokeWidth != 1f) {
                    canvas.setLineWidth(strokeWidth);
                }
                Color strokeColor = getPropertyAsColor(Property.STROKE_COLOR);
                if (strokeColor == null)
                    strokeColor = textColor;
                if (strokeColor != null)
                    canvas.setStrokeColor(strokeColor);
            }
            if (textColor != null)
                canvas.setFillColor(textColor);
            if (textRise != null && textRise != 0)
                canvas.setTextRise(textRise);
            if (characterSpacing != null && characterSpacing != 0)
                canvas.setCharacterSpacing(characterSpacing);
            if (wordSpacing != null && wordSpacing != 0)
                canvas.setWordSpacing(wordSpacing);
            canvas.showText(line);
            canvas.endText().restoreState();
        }

        if (position == LayoutPosition.RELATIVE) {
            applyAbsolutePositioningTranslation(false);
        }
    }

    @Override
    public void drawBackground(PdfDocument document, PdfCanvas canvas) {
        try {
            Property.Background background = getProperty(Property.BACKGROUND);
            Float textRise = getPropertyAsFloat(Property.TEXT_RISE);
            float bottomBBoxY = occupiedArea.getBBox().getY();
            float leftBBoxX = occupiedArea.getBBox().getX();
            if (background != null) {
                canvas.saveState().setFillColor(background.getColor());
                canvas.rectangle(leftBBoxX - background.getExtraLeft(), bottomBBoxY + textRise - background.getExtraBottom(),
                        occupiedArea.getBBox().getWidth() + background.getExtraLeft() + background.getExtraRight(),
                        occupiedArea.getBBox().getHeight() - textRise + background.getExtraTop() + background.getExtraBottom());
                canvas.fill().restoreState();
            }
        } catch (Exception exc) {
            throw new RuntimeException(exc);
        }
    }

    @Override
    public <T> T getDefaultProperty(int propertyKey) {
        switch (propertyKey) {
            case Property.HORIZONTAL_SCALING:
                return (T) Float.valueOf(1);
            default:
                return super.getDefaultProperty(propertyKey);
        }
    }

    public void trimFirst() {
        text = text.replaceAll("^\\s+", "");
    }

    /**
     * Returns the amount of space in points which the text was trimmed by.
     */
    public float trimLast() {
        float trimmedSpace = 0;

        if (line == null)
            return trimmedSpace;

        float fontSize = getPropertyAsFloat(Property.FONT_SIZE);
        Float characterSpacing = getPropertyAsFloat(Property.CHARACTER_SPACING);
        Float wordSpacing = getPropertyAsFloat(Property.WORD_SPACING);
        PdfFont font = getPropertyAsFont(Property.FONT);
        Float hScale = getProperty(Property.HORIZONTAL_SCALING);

        int firstNonSpaceCharIndex = line.length() - 1;
        while (firstNonSpaceCharIndex >= 0) {
            if (!Character.isWhitespace(line.charAt(firstNonSpaceCharIndex))) {
                break;
            }

            float currentCharWidth = getCharWidth(line.charAt(firstNonSpaceCharIndex), font, fontSize, hScale, characterSpacing, wordSpacing) / TEXT_SPACE_COEFF;
            trimmedSpace += currentCharWidth;
            occupiedArea.getBBox().setWidth(occupiedArea.getBBox().getWidth() - currentCharWidth);

            firstNonSpaceCharIndex--;
        }
        line = line.substring(0, firstNonSpaceCharIndex + 1);

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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    protected int getNumberOfSpaces() {
        if (line == null)
            return 0;
        int spaces = 0;
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == ' ')
                spaces++;
        }
        return spaces;
    }

    protected int length() {
        return line == null ? 0 : line.length();
    }

    @Override
    protected TextRenderer createSplitRenderer() {
        return new TextRenderer((Text)modelElement, null);
    }

    @Override
    protected TextRenderer createOverflowRenderer() {
        return new TextRenderer((Text)modelElement, null);
    }

    protected TextRenderer[] split(int initialOverflowTextPos) {
        // TODO memory optimization

        TextRenderer splitRenderer = createSplitRenderer();
        splitRenderer.setText(text.substring(0, initialOverflowTextPos));
        splitRenderer.occupiedArea = occupiedArea.clone();
        splitRenderer.parent = parent;
        splitRenderer.line = line;
        splitRenderer.yLineOffset = yLineOffset;

        TextRenderer overflowRenderer = createOverflowRenderer();
        overflowRenderer.setText(text.substring(initialOverflowTextPos));
        overflowRenderer.parent = parent;

        return new TextRenderer[] {splitRenderer, overflowRenderer};
    }

    private static boolean noPrint(int c) {
        return c >= 0x200b && c <= 0x200f || c >= 0x202a && c <= 0x202e;
    }

    private int getCharCode(int pos) {
        // TODO surrogate pair
        return text.charAt(pos);
    }

    private float getCharWidth(int c, PdfFont font, float fontSize, Float hScale, Float characterSpacing, Float wordSpacing) {
        if (hScale == null)
            hScale = 1f;
        float resultWidth = font.getWidth(c) * fontSize;
        if (characterSpacing != null) {
            resultWidth += characterSpacing * hScale * TEXT_SPACE_COEFF;
        }
        if (wordSpacing != null && c == ' ') {
            resultWidth += wordSpacing * hScale * TEXT_SPACE_COEFF;
        }
        return resultWidth;
    }
}
