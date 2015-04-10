package com.itextpdf.model.renderer;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.font.FontConstants;
import com.itextpdf.basics.font.Type1Font;
import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.core.font.PdfFont;
import com.itextpdf.core.font.PdfType1Font;
import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.model.element.Property;
import com.itextpdf.model.element.Text;
import com.itextpdf.model.layout.LayoutArea;
import com.itextpdf.model.layout.LayoutContext;
import com.itextpdf.model.layout.LayoutResult;

import java.io.IOException;


public class TextRenderer extends AbstractRenderer {

    protected String text;

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
        occupiedArea = new LayoutArea(area.getPageNumber(), new Rectangle(area.getBBox().getX(), area.getBBox().getY() + area.getBBox().getHeight(), area.getBBox().getWidth(), 0));
        int textPos = 0;
        boolean anythingPlaced = false;
        while (textPos < text.length()) {
            int initialLineTextPos = textPos;
            float maxHeight = 0;
            float curWidth = 0;
            Rectangle glyphSize;
            while (textPos < text.length() && curWidth + (glyphSize = getGlyphSize(text.charAt(textPos))).getWidth() < area.getBBox().getWidth()) {
                curWidth += glyphSize.getWidth();
                maxHeight = Math.max(maxHeight, glyphSize.getHeight());
                textPos++;
            }

            if (maxHeight > area.getBBox().getHeight()) {
                // the line does not fit because of height - full overflow
                // TODO memory optimization
                TextRenderer splitRenderer = new TextRenderer((Text)modelElement, text.substring(0, initialLineTextPos));
                splitRenderer.occupiedArea = occupiedArea.clone();
                splitRenderer.parent = parent;

                TextRenderer overflowRenderer = new TextRenderer((Text)modelElement, text.substring(initialLineTextPos));
                overflowRenderer.parent = parent;

                return new LayoutResult(anythingPlaced ? LayoutResult.PARTIAL : LayoutResult.NOTHING, occupiedArea, splitRenderer, overflowRenderer);
            } else {
                occupiedArea.getBBox().moveDown(maxHeight);
                occupiedArea.getBBox().setHeight(occupiedArea.getBBox().getHeight() + maxHeight);
                area.getBBox().setHeight(area.getBBox().getHeight() - maxHeight);
                anythingPlaced = true;
            }
        }

        return new LayoutResult(LayoutResult.FULL, occupiedArea, null, null);
    }

    @Override
    public void draw(PdfDocument document, PdfCanvas canvas) {
        super.draw(document, canvas);
        float currentY = occupiedArea.getBBox().getY() + occupiedArea.getBBox().getHeight();
        float startX = occupiedArea.getBBox().getX();
        // TODO Here the work is almost the same as in layout
        int textPos = 0;
        while (textPos < text.length()) {
            float curWidth = 0;
            float maxHeight = 0;
            Rectangle glyphSize;
            while (textPos < text.length() && curWidth + (glyphSize = getGlyphSize(text.charAt(textPos))).getWidth() < occupiedArea.getBBox().getWidth()) {
                try {
                    canvas.rectangle(startX + curWidth, currentY - glyphSize.getHeight(), glyphSize.getWidth(), glyphSize.getHeight()).stroke();
                    // TODO property get default
                    PdfFont font = getPropertyAsFont(Property.FONT);
                    canvas.beginText().setFontAndSize(font == null ? new PdfType1Font(document, new Type1Font(FontConstants.HELVETICA, "")) : font, 12).moveText(startX + curWidth, currentY - glyphSize.getHeight()).showText(text.charAt(textPos) + "").endText();
                } catch (PdfException | IOException e) {
                    e.printStackTrace();
                }
                curWidth += glyphSize.getWidth();
                maxHeight = Math.max(maxHeight, glyphSize.getHeight());

                textPos++;
            }

            currentY -= maxHeight;
        }
    }

    private Rectangle getGlyphSize(char ch) {
        return new Rectangle(20, 20);
    }
}
