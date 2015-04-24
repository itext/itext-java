package com.itextpdf.model.renderer;

import com.itextpdf.basics.PdfException;
import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.canvas.color.Color;
import com.itextpdf.core.font.PdfFont;
import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.model.Property;
import com.itextpdf.model.element.Text;
import com.itextpdf.model.layout.LayoutArea;
import com.itextpdf.model.layout.LayoutContext;
import com.itextpdf.model.layout.LayoutResult;

import java.util.ArrayList;
import java.util.List;


public class TextRenderer extends AbstractRenderer {

    protected String text;
    protected static final float TEXT_SPACE_COEFF = 1000;
    protected List<String> lines;
    protected List<Float> yOffsets;

    public TextRenderer(Text textElement, String text) {
        super(textElement);
        this.text = text;
    }

    public static void showTextAligned(final PdfCanvas canvas, final int alignment, final String text, final float x, final float y, final float rotation) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public LayoutResult layout(LayoutContext layoutContext) {
        LayoutArea area = layoutContext.getArea();
        Rectangle layoutBox = area.getBBox();
        occupiedArea = new LayoutArea(area.getPageNumber(), new Rectangle(layoutBox.getX(), layoutBox.getY() + layoutBox.getHeight(), 0, 0));

        boolean anythingPlaced = false;

        int currentTextPos = 0;
        float fontSize = getPropertyAsFloat(Property.FONT_SIZE);
        float textRise = getPropertyAsFloat(Property.TEXT_RISE);
        Float characterSpacing = getProperty(Property.CHARACTER_SPACING);
        PdfFont font = getPropertyAsFont(Property.FONT);
        Float hScale = getProperty(Property.HORIZONTAL_SCALING);

        lines = new ArrayList<>();
        yOffsets = new ArrayList<>();
        StringBuilder currentLine = new StringBuilder();

        while (currentTextPos < text.length()) {
            float currentLineHeight = 0;
            int initialLineTextPos = currentTextPos;
            float currentLineWidth = 0;

            while (currentTextPos < text.length()) {
                int currentCharCode = getCharCode(currentTextPos);
                if (noPrint(currentCharCode)) {
                    currentTextPos++;
                    continue;
                }

                int nonBreakablePartEnd = getNonBreakablePartEnd(currentTextPos);
                float nonBreakablePartFullWidth = 0;
                float nonBreakablePartWidthWhichDoesNotExceedAllowedWidth = 0;
                float nonBreakablePartMaxHeight = 0;
                int firstCharacterWhichExceedsAllowedWidth = -1;
                for (int ind = currentTextPos; ind <= nonBreakablePartEnd; ind++) {
                    int charCode = getCharCode(ind);
                    if (noPrint(charCode))
                        continue;
                    float glyphWidth = getCharWidth(charCode, font, fontSize, hScale, characterSpacing) / TEXT_SPACE_COEFF;
                    if ((nonBreakablePartFullWidth + glyphWidth) > layoutBox.getWidth() - currentLineWidth && firstCharacterWhichExceedsAllowedWidth == -1) {
                        firstCharacterWhichExceedsAllowedWidth = ind;
                    }
                    if (firstCharacterWhichExceedsAllowedWidth == -1)
                        nonBreakablePartWidthWhichDoesNotExceedAllowedWidth += glyphWidth;

                    nonBreakablePartFullWidth += glyphWidth;
                    nonBreakablePartMaxHeight = fontSize + textRise;
                }

                if (firstCharacterWhichExceedsAllowedWidth == -1) {
                    // can fit the whole word in a line
                    currentLine.append(text.substring(currentTextPos, nonBreakablePartEnd + 1));
                    currentLineHeight = Math.max(currentLineHeight, nonBreakablePartMaxHeight);
                    currentTextPos = nonBreakablePartEnd + 1;
                    currentLineWidth += nonBreakablePartFullWidth;
                } else {
                    // must split the word

                    // check if line height exceeds the allowed height
                    if (Math.max(currentLineHeight, nonBreakablePartMaxHeight) > layoutBox.getHeight()) {
                        // the line does not fit because of height - full overflow
                        TextRenderer[] splitResult = split(initialLineTextPos);
                        return new LayoutResult(anythingPlaced ? LayoutResult.PARTIAL : LayoutResult.NOTHING, occupiedArea, splitResult[0], splitResult[1]);
                    } else {
                        if (nonBreakablePartFullWidth > layoutBox.getWidth()) {
                            // if the word is too long for a single line we will have to split it
                            currentLine.append(text.substring(currentTextPos, firstCharacterWhichExceedsAllowedWidth));
                            currentLineHeight = Math.max(currentLineHeight, nonBreakablePartMaxHeight);
                            currentLineWidth += nonBreakablePartWidthWhichDoesNotExceedAllowedWidth;
                            currentTextPos = firstCharacterWhichExceedsAllowedWidth;
                        }

                        lines.add(currentLine.toString());
                        yOffsets.add(currentLineHeight);
                        anythingPlaced = true;

                        occupiedArea.getBBox().moveDown(currentLineHeight);
                        occupiedArea.getBBox().setHeight(occupiedArea.getBBox().getHeight() + currentLineHeight);
                        occupiedArea.getBBox().setWidth(Math.max(occupiedArea.getBBox().getWidth(), currentLineWidth));
                        layoutBox.setHeight(area.getBBox().getHeight() - currentLineHeight);

                        currentLine.setLength(0);
                        currentLineWidth = 0;

                        break;
                    }
                }
            }

            if (currentLine.length() != 0) {
                lines.add(currentLine.toString());
                yOffsets.add(currentLineHeight);
                anythingPlaced = true;
                currentLine.setLength(0);

                occupiedArea.getBBox().moveDown(currentLineHeight);
                occupiedArea.getBBox().setHeight(occupiedArea.getBBox().getHeight() + currentLineHeight);
                occupiedArea.getBBox().setWidth(Math.max(occupiedArea.getBBox().getWidth(), currentLineWidth));
                layoutBox.setHeight(area.getBBox().getHeight() - currentLineHeight);
            }
        }

        return new LayoutResult(LayoutResult.FULL, occupiedArea, null, null);
    }

    @Override
    public void draw(PdfDocument document, PdfCanvas canvas) {
        super.draw(document, canvas);

        float topBBoxY = occupiedArea.getBBox().getY() + occupiedArea.getBBox().getHeight();
        float leftBBoxX = occupiedArea.getBBox().getX();

        if (lines != null && lines.size() != 0) {
            PdfFont font = getPropertyAsFont(Property.FONT);
            float fontSize = getPropertyAsFloat(Property.FONT_SIZE);
            Color textColor = getPropertyAsColor(Property.FONT_COLOR);
            int textRenderingMode = (int) getProperty(Property.TEXT_RENDERING_MODE) & 3;
            Float textRise = getPropertyAsFloat(Property.TEXT_RISE);

            try {
                canvas.saveState().beginText().setFontAndSize(font, fontSize).moveText(leftBBoxX, topBBoxY);
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
                if (textRise != null)
                    canvas.setTextRise(textRise);
                for (int i = 0; i < lines.size(); i++) {
                    canvas.moveText(0, -yOffsets.get(i));
                    canvas.showText(lines.get(i));
                }
                canvas.endText().restoreState();
            } catch (PdfException exc) {
                throw new RuntimeException(exc);
            }
        }
    }

    @Override
    protected void drawBackground(PdfCanvas canvas) {
        try {
            Property.Background background = getProperty(Property.BACKGROUND);
            Float textRise = getPropertyAsFloat(Property.TEXT_RISE);
            float topBBoxY = occupiedArea.getBBox().getY() + occupiedArea.getBBox().getHeight();
            float leftBBoxX = occupiedArea.getBBox().getX();
            if (background != null) {
                // TODO ASCENDER, DESCENDER
                canvas.saveState().setFillColor(background.getColor());
                float currentY = topBBoxY;
                for (int i = 0; i < lines.size(); i++) {
                    currentY -= yOffsets.get(i);
                    canvas.rectangle(leftBBoxX - background.getExtraLeft(), currentY + textRise - background.getExtraBottom(),
                            occupiedArea.getBBox().getWidth() + background.getExtraLeft() + background.getExtraRight(),
                            yOffsets.get(i) - textRise + background.getExtraTop() + background.getExtraBottom());
                }
                canvas.fill().restoreState();
            }
        } catch (Exception exc) {
            throw new RuntimeException(exc);
        }
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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
        splitRenderer.lines = new ArrayList<>(lines);
        splitRenderer.yOffsets = new ArrayList<>(yOffsets);

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

    private int getNonBreakablePartEnd(int initialLineTextPos) {
        int current = initialLineTextPos;
        if (Character.isWhitespace(text.charAt(current)))
            return current;
        while (current + 1 < text.length() && (!Character.isWhitespace(text.charAt(current + 1))))
            current++;
        return current;
    }

    private float getCharWidth(int c, PdfFont font, float fontSize, Float hScale, Float characterSpacing) {
        if (hScale == null)
            hScale = 1f;
        if (characterSpacing != null) {
            return font.getWidth(c) * fontSize + characterSpacing * hScale;
        }
        return font.getWidth(c) * fontSize;
    }
}
