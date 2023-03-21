package com.itextpdf.forms.form.renderer.checkboximpl;

import com.itextpdf.forms.exceptions.FormsExceptionMessageConstant;
import com.itextpdf.forms.fields.properties.CheckBoxType;
import com.itextpdf.forms.form.renderer.CheckBoxRenderer;
import com.itextpdf.forms.util.DrawingUtil;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.renderer.DrawContext;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.renderer.ParagraphRenderer;

/**
 * A factory for creating CheckBoxPdfARenderer objects.
 */
public class CheckBoxPdfARendererFactory extends AbstractCheckBoxRendererFactory {
    private static final Color DEFAULT_BORDER_COLOR = ColorConstants.DARK_GRAY;
    private static final Color DEFAULT_BACKGROUND_COLOR = ColorConstants.WHITE;
    // 1px
    private static final float DEFAULT_BORDER_WIDTH = 0.75F;

    public CheckBoxPdfARendererFactory(CheckBoxRenderer checkBoxRenderer) {
        super(checkBoxRenderer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IRenderer createFlatRenderer() {
        setupSize();
        final Paragraph paragraph = new Paragraph().setWidth(getSize()).setHeight(getSize())
                .setBorder(new SolidBorder(DEFAULT_BORDER_COLOR, DEFAULT_BORDER_WIDTH))
                .setBackgroundColor(DEFAULT_BACKGROUND_COLOR).setMargin(0)
                .setHorizontalAlignment(HorizontalAlignment.CENTER);
        return new FlatParagraphRenderer(paragraph);
    }

    /**
     * {@inheritDoc}
     */
    protected class FlatParagraphRenderer extends ParagraphRenderer {
        public FlatParagraphRenderer(Paragraph modelElement) {
            super(modelElement);
        }

        @Override
        public void drawChildren(DrawContext drawContext) {
            if (!shouldDrawChildren()) {
                return;
            }
            final PdfCanvas canvas = drawContext.getCanvas();
            final Rectangle rectangle = getInnerAreaBBox().clone();
            canvas.saveState();
            canvas.setFillColor(getFillColor());
            final CheckBoxType checkBoxType = getCheckBoxType();
            drawIcon(checkBoxType, canvas, rectangle);
            canvas.restoreState();
        }
    }

    private static void drawIcon(CheckBoxType type, PdfCanvas canvas1, Rectangle rectangle) {
        switch (type) {
            case CHECK:
                DrawingUtil.drawPdfACheck(canvas1, rectangle.getWidth(), rectangle.getHeight(), rectangle.getLeft(),
                        rectangle.getBottom());
                break;
            case CIRCLE:
                DrawingUtil.drawPdfACircle(canvas1, rectangle.getWidth(), rectangle.getHeight(), rectangle.getLeft(),
                        rectangle.getBottom());
                break;
            case CROSS:
                DrawingUtil.drawPdfACross(canvas1, rectangle.getWidth(), rectangle.getHeight(), rectangle.getLeft(),
                        rectangle.getBottom());
                break;
            case DIAMOND:
                DrawingUtil.drawPdfADiamond(canvas1, rectangle.getWidth(), rectangle.getHeight(), rectangle.getLeft(),
                        rectangle.getBottom());
                break;
            case SQUARE:
                DrawingUtil.drawPdfASquare(canvas1, rectangle.getWidth(), rectangle.getHeight(), rectangle.getLeft(),
                        rectangle.getBottom());
                break;
            case STAR:
                DrawingUtil.drawPdfAStar(canvas1, rectangle.getWidth(), rectangle.getHeight(), rectangle.getLeft(),
                        rectangle.getBottom());
                break;
            default:
                throw new PdfException(FormsExceptionMessageConstant.CHECKBOX_TYPE_NOT_SUPPORTED);
        }


    }
}

