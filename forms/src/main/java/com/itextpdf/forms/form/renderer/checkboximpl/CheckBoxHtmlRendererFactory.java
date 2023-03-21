package com.itextpdf.forms.form.renderer.checkboximpl;

import com.itextpdf.commons.utils.ExperimentalFeatures;
import com.itextpdf.forms.fields.properties.CheckBoxType;
import com.itextpdf.forms.form.renderer.CheckBoxRenderer;
import com.itextpdf.forms.util.DrawingUtil;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.Background;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.itextpdf.layout.renderer.DrawContext;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.renderer.ParagraphRenderer;

/**
 * The factory class for creating a flat renderer for the checkbox in HTML rendering mode.
 */
public class CheckBoxHtmlRendererFactory extends AbstractCheckBoxRendererFactory {

    private static final Color DEFAULT_BORDER_COLOR = ColorConstants.DARK_GRAY;
    private static final Color DEFAULT_BACKGROUND_COLOR = ColorConstants.WHITE;
    // 1px
    private static final float DEFAULT_BORDER_WIDTH = 0.75F;
    // 11px
    private static final float DEFAULT_SIZE = 8.25F;

    public CheckBoxHtmlRendererFactory(CheckBoxRenderer checkBoxRenderer) {
        super(checkBoxRenderer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    //TODO DEVSIX-7426 make this method in parent class and overwrite if needed
    public IRenderer createFlatRenderer() {
        //TODO DEVSIX-7426 remove flag
        if (ExperimentalFeatures.ENABLE_EXPERIMENTAL_CHECKBOX_RENDERING) {
            setupSize();
            final Paragraph paragraph = new Paragraph()
                    .setWidth(getSize())
                    .setHeight(getSize())
                    .setMargin(0)
                    .setBorder(new SolidBorder(DEFAULT_BORDER_COLOR, DEFAULT_BORDER_WIDTH))
                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setHorizontalAlignment(HorizontalAlignment.CENTER);
            return new FlatParagraphRenderer(paragraph);
        }

        //TODO DEVSIX-7426 remove this
        final Paragraph paragraph = new Paragraph()
                .setWidth(DEFAULT_SIZE)
                .setHeight(DEFAULT_SIZE)
                .setBorder(new SolidBorder(DEFAULT_BORDER_COLOR, DEFAULT_BORDER_WIDTH))
                .setBackgroundColor(DEFAULT_BACKGROUND_COLOR)
                .setHorizontalAlignment(HorizontalAlignment.CENTER);
        return new FlatParagraphRenderer(paragraph);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected float getDefaultSize() {
        return DEFAULT_SIZE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Background getDefaultColor() {
        return new Background(DEFAULT_BACKGROUND_COLOR, 1F);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected CheckBoxType getDefaultCheckBoxType() {
        return CheckBoxType.CHECK;
    }

    class FlatParagraphRenderer extends ParagraphRenderer {
        public FlatParagraphRenderer(Paragraph modelElement) {
            super(modelElement);
        }

        @Override
        public void drawChildren(DrawContext drawContext) {
            if (!shouldDrawChildren()) {
                return;
            }
            final PdfCanvas canvas = drawContext.getCanvas();
            final Rectangle rectangle = getInnerAreaBBox();
            canvas.saveState();
            canvas.setFillColor(ColorConstants.BLACK);
            DrawingUtil.drawPdfACheck(canvas, rectangle.getWidth(), rectangle.getHeight(),
                    rectangle.getLeft(), rectangle.getBottom());
            canvas.restoreState();
        }
    }
}

