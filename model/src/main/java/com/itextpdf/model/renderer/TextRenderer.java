package com.itextpdf.model.renderer;

import com.itextpdf.basics.PdfException;
import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.model.element.Text;
import com.itextpdf.model.layout.LayoutArea;
import com.itextpdf.model.layout.LayoutContext;
import com.itextpdf.model.layout.LayoutResult;


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
            float maxHeight = 0;
            float curWidth = 0;
            Rectangle glyphSize;
            while (textPos < text.length() && curWidth + (glyphSize = getGlyphSize(text.charAt(textPos++))).getWidth() < area.getBBox().getWidth()) {
                curWidth += glyphSize.getWidth();
                maxHeight = Math.max(maxHeight, glyphSize.getHeight());
            }

            if (maxHeight > area.getBBox().getHeight()) {
                // the line does not fit because of height - full overflow
                TextRenderer overflow = split();
                return new LayoutResult(anythingPlaced ? LayoutResult.PARTIAL : LayoutResult.NOTHING, occupiedArea, overflow);
            } else {
                occupiedArea.getBBox().moveDown(maxHeight);
                occupiedArea.getBBox().setHeight(occupiedArea.getBBox().getHeight() + maxHeight);
                anythingPlaced = true;
            }
        }

        return new LayoutResult(LayoutResult.FULL, occupiedArea, null);
    }

    @Override
    public TextRenderer split() {
        throw new RuntimeException();
    }

    @Override
    public void draw(PdfCanvas canvas) {
        super.draw(canvas);
        float currentY = occupiedArea.getBBox().getY();
        // TODO Here the work is almost the same as in layout
        int textPos = 0;
        while (textPos < text.length()) {
            float curWidth = 0;
            float maxHeight = 0;
            Rectangle glyphSize;
            while (textPos < text.length() && curWidth + (glyphSize = getGlyphSize(text.charAt(textPos++))).getWidth() < occupiedArea.getBBox().getWidth()) {
                try {
                    canvas.rectangle(curWidth, currentY, glyphSize.getWidth(), glyphSize.getHeight()).stroke();
                } catch (PdfException e) {
                    e.printStackTrace();
                }
                curWidth += glyphSize.getWidth();
                maxHeight = Math.max(maxHeight, glyphSize.getHeight());

            }
        }
    }

    private Rectangle getGlyphSize(char ch) {
        return new Rectangle(20, 20);
    }
}
