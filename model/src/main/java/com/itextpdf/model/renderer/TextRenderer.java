package com.itextpdf.model.renderer;

import com.itextpdf.basics.Utilities;
import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.canvas.color.Color;
import com.itextpdf.core.font.PdfFont;
import com.itextpdf.basics.geom.Rectangle;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.model.Property;
import com.itextpdf.model.element.Text;
import com.itextpdf.model.layout.*;


public class TextRenderer extends AbstractRenderer {

    // TODO More accurate ascender, descender computation

    protected static final float TEXT_SPACE_COEFF = 1000;
    protected String text;
    // Left pos of the part of the {@see text} which belongs to this renderer, inclusive
    protected int leftPos;
    // Right pos of the part of the {@see text} which belongs to this renderer, exclusive
    protected int rightPos;
    // Processed line which is the result of the layout function. It is then used on {@see draw()}
    protected String line;
    protected float yLineOffset;

    protected float tabAnchorCharacterPosition = -1;

    public TextRenderer(Text textElement) {
        this (textElement, textElement.getText());
    }

    public TextRenderer(Text textElement, String text) {
        super(textElement);
        this.text = text;
        leftPos = 0;
        rightPos = text != null ? text.length() : 0;
    }

    @Override
    public TextLayoutResult layout(LayoutContext layoutContext) {
        LayoutArea area = layoutContext.getArea();
        Rectangle layoutBox = applyMargins(area.getBBox().clone(), false);
        applyBorderBox(layoutBox, false);

        occupiedArea = new LayoutArea(area.getPageNumber(), new Rectangle(layoutBox.getX(), layoutBox.getY() + layoutBox.getHeight(), 0, 0));

        boolean anythingPlaced = false;

        int currentTextPos = leftPos;
        float fontSize = getPropertyAsFloat(Property.FONT_SIZE);
        float textRise = getPropertyAsFloat(Property.TEXT_RISE);
        Float characterSpacing = getPropertyAsFloat(Property.CHARACTER_SPACING);
        Float wordSpacing = getPropertyAsFloat(Property.WORD_SPACING);
        PdfFont font = getPropertyAsFont(Property.FONT);
        Float hScale = getProperty(Property.HORIZONTAL_SCALING);
        Property.FontKerning fontKerning = getProperty(Property.FONT_KERNING);
        float ascender = 800;
        float descender = -200;

        StringBuilder currentLine = new StringBuilder();

        float currentLineAscender = 0;
        float currentLineDescender = 0;
        float currentLineHeight = 0;
        int initialLineTextPos = currentTextPos;
        float currentLineWidth = 0;
        int previousCharPos = -1;

        Character tabAnchorCharacter = getProperty(Property.TAB_ANCHOR);

        while (currentTextPos < rightPos) {
            int currentCharCode = getCharCode(text, currentTextPos);
            if (noPrint(currentCharCode)) {
                currentTextPos++;
                continue;
            }

            int nonBreakablePartEnd = rightPos - 1;
            float nonBreakablePartFullWidth = 0;
            float nonBreakablePartWidthWhichDoesNotExceedAllowedWidth = 0;
            float nonBreakablePartMaxAscender = 0;
            float nonBreakablePartMaxDescender = 0;
            float nonBreakablePartMaxHeight = 0;
            int firstCharacterWhichExceedsAllowedWidth = -1;

            for (int ind = currentTextPos; ind < rightPos; ind += (Utilities.isSurrogatePair(text, currentTextPos) ? 2 : 1)) {
                if (text.charAt(ind) == '\n') {
                    firstCharacterWhichExceedsAllowedWidth = ind + 1;
                    break;
                }

                int charCode = getCharCode(text, ind);
                if (noPrint(charCode))
                    continue;

                if (tabAnchorCharacter != null && tabAnchorCharacter == text.charAt(ind)) {
                    tabAnchorCharacterPosition = currentLineWidth + nonBreakablePartFullWidth;
                    tabAnchorCharacter = null;
                }

                float glyphWidth = getCharWidth(charCode, font, fontSize, hScale, characterSpacing, wordSpacing) / TEXT_SPACE_COEFF;
                float kerning = fontKerning == Property.FontKerning.YES && previousCharPos != -1 ?
                        getKerning(getCharCode(text, previousCharPos), charCode, font, fontSize, hScale) / TEXT_SPACE_COEFF : 0;
                if ((nonBreakablePartFullWidth + glyphWidth + kerning) > layoutBox.getWidth() - currentLineWidth && firstCharacterWhichExceedsAllowedWidth == -1) {
                    firstCharacterWhichExceedsAllowedWidth = ind;
                }
                if (firstCharacterWhichExceedsAllowedWidth == -1) {
                    nonBreakablePartWidthWhichDoesNotExceedAllowedWidth += glyphWidth + kerning;
                }

                nonBreakablePartFullWidth += glyphWidth + kerning;
                nonBreakablePartMaxAscender = ascender;
                nonBreakablePartMaxDescender = descender;
                nonBreakablePartMaxHeight = (nonBreakablePartMaxAscender - nonBreakablePartMaxDescender) * fontSize / TEXT_SPACE_COEFF + textRise;

                previousCharPos = ind;

                if (nonBreakablePartFullWidth > layoutBox.getWidth()) {
                    // we have extracted all the information we wanted and we do not want to continue.
                    // we will have to split the word anyway.
                    break;
                }

                if (Character.isWhitespace(charCode) || isLastChar(text, rightPos, ind) || Character.isWhitespace(getNextChar(text, ind))) {
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
                    applyBorderBox(occupiedArea.getBBox(), true);
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
                    applyBorderBox(occupiedArea.getBBox(), true);
                    applyMargins(occupiedArea.getBBox(), true);
                    TextLayoutResult result = new TextLayoutResult(LayoutResult.PARTIAL, occupiedArea, split[0], split[1]).setWordHasBeenSplit(wordSplit);
                    if (line.isEmpty()) {
                        result = new TextLayoutResult(LayoutResult.NOTHING, occupiedArea, split[0], split[1]);
                    }
                    if (split[1].length() > 0 && split[1].charAt(0) == '\n')
                        result.setSplitForcedByNewline(true);
                    return result;
                }
            }
        }

        if (currentLine.length() != 0) {
            if (currentLineHeight > layoutBox.getHeight()) {
                applyBorderBox(occupiedArea.getBBox(), true);
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

        applyBorderBox(occupiedArea.getBBox(), true);
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
            Property.FontKerning fontKerning = getProperty(Property.FONT_KERNING);
            Float horizontalScaling = getProperty(Property.HORIZONTAL_SCALING);

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
            if (horizontalScaling != null && horizontalScaling != 1)
                canvas.setHorizontalScaling(horizontalScaling * 100);
            if (fontKerning == Property.FontKerning.YES) {
                canvas.showTextKerned(line);
            } else {
                canvas.showText(line);
            }
            canvas.endText().restoreState();
        }

        if (position == LayoutPosition.RELATIVE) {
            applyAbsolutePositioningTranslation(false);
        }
    }

    @Override
    public void drawBackground(PdfDocument document, PdfCanvas canvas) {
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
        while (leftPos < rightPos && Character.isWhitespace(text.charAt(leftPos))) {
            leftPos++;
        }
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

        int firstNonSpaceCharIndex = getLastCharIndex(line, 0, line.length());
        while (firstNonSpaceCharIndex >= 0) {
            if (!Character.isWhitespace(line.charAt(firstNonSpaceCharIndex))) {
                break;
            }

            float currentCharWidth = getCharWidth(getCharCode(line, firstNonSpaceCharIndex), font, fontSize, hScale, characterSpacing, wordSpacing) / TEXT_SPACE_COEFF;
            float kerning = (!isFirstChar(line, firstNonSpaceCharIndex) ? getKerning(getPrevChar(line, firstNonSpaceCharIndex), getCharCode(line, firstNonSpaceCharIndex), font, fontSize, hScale) : 0) / TEXT_SPACE_COEFF;
            trimmedSpace += currentCharWidth - kerning;
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
        setText(text, 0, text.length());
    }

    public void setText(String text, int leftPos, int rightPos) {
        this.text = text;
        this.leftPos = leftPos;
        this.rightPos = rightPos;
    }

    /**
     * The length of the whole text assigned to this renderer.
     */
    public int length() {
        // TODO surrogate pairs
        return text == null ? 0 : rightPos - leftPos;
    }

    @Override
    public String toString() {
        return text.substring(leftPos, rightPos);
    }

    /**
     * Gets char code at given position for the text belonging to this renderer.
     * @param pos the position in range [0; length())
     * @return Unicode char code
     */
    public int charAt(int pos) {
        return getCharCode(text, pos + leftPos);
    }

    public float getTabAnchorCharacterPosition(){
        return tabAnchorCharacterPosition;
    }

    @Override
    protected Float getFirstYLineRecursively() {
        return getYLine();
    }

    /**
     * The length of the {@see line} which is the result of the layout call.
     * @return
     */
    protected int lineLength() {
        return line != null ? line.length() : 0;
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

    protected TextRenderer createSplitRenderer() {
        return new TextRenderer((Text)modelElement, null);
    }

    protected TextRenderer createOverflowRenderer() {
        return new TextRenderer((Text)modelElement, null);
    }

    protected TextRenderer[] split(int initialOverflowTextPos) {
        TextRenderer splitRenderer = createSplitRenderer();
        splitRenderer.setText(text, leftPos, initialOverflowTextPos);
        splitRenderer.occupiedArea = occupiedArea.clone();
        splitRenderer.parent = parent;
        splitRenderer.line = line;
        splitRenderer.yLineOffset = yLineOffset;

        TextRenderer overflowRenderer = createOverflowRenderer();
        overflowRenderer.setText(text, initialOverflowTextPos, rightPos);
        overflowRenderer.parent = parent;

        return new TextRenderer[] {splitRenderer, overflowRenderer};
    }

    private static boolean noPrint(int c) {
        return c >= 0x200b && c <= 0x200f || c >= 0x202a && c <= 0x202e;
    }

    private int getCharCode(String text, int strPos) {
        if (Utilities.isSurrogatePair(text, strPos)) {
            return Utilities.convertToUtf32(text, strPos);
        } else {
            return text.charAt(strPos);
        }
    }

    private boolean isFirstChar(String text, int strPos) {
        return strPos == 0;
    }

    private boolean isLastChar(String text, int rightTextPos, int strPos) {
        int charLen = Utilities.isSurrogatePair(text, strPos) ? 2 : 1;
        return strPos + charLen == rightTextPos;
    }

    private int getNextChar(String text, int strPos) {
        int charLen = Utilities.isSurrogatePair(text, strPos) ? 2 : 1;
        return getCharCode(text, strPos + charLen);
    }

    private int getPrevChar(String text, int strPos) {
        if (strPos > 1 && Utilities.isSurrogatePair(text, strPos - 2)) {
            return getCharCode(text, strPos - 2);
        } else {
            return getCharCode(text, strPos - 1);
        }
    }

    private int getLastCharIndex(String text, int leftTextPos, int rightTextPos) {
        return rightTextPos - leftTextPos >= 2 && Utilities.isSurrogatePair(text, rightTextPos - 2) ? rightTextPos - 2 : rightTextPos - 1;
    }

    private float getCharWidth(int c, PdfFont font, float fontSize, Float hScale, Float characterSpacing, Float wordSpacing) {
        if (hScale == null)
            hScale = 1f;

        float resultWidth = font.getWidth(c) * fontSize * hScale;
        if (characterSpacing != null) {
            resultWidth += characterSpacing * hScale * TEXT_SPACE_COEFF;
        }
        if (wordSpacing != null && c == ' ') {
            resultWidth += wordSpacing * hScale * TEXT_SPACE_COEFF;
        }
        return resultWidth;
    }

    private float getKerning(int char1, int char2, PdfFont font, float fontSize, Float hScale) {
        if (hScale == null)
            hScale = 1f;
        if (font.hasKernPairs()) {
            return font.getKerning(char1, char2) * fontSize * hScale;
        } else {
            return 0;
        }
    }
}
