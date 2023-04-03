package com.itextpdf.forms.form.renderer.checkboximpl;

import com.itextpdf.forms.exceptions.FormsExceptionMessageConstant;
import com.itextpdf.forms.fields.properties.CheckBoxType;
import com.itextpdf.forms.form.FormProperty;
import com.itextpdf.forms.form.renderer.CheckBoxRenderer;
import com.itextpdf.forms.util.DrawingUtil;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.renderer.DrawContext;

/**
 * This class is used to draw a checkBox icon in PDF/A mode.
 */
public final class PdfACheckBoxRenderingStrategy implements ICheckBoxRenderingStrategy {


    /**
     * Creates a new {@link PdfACheckBoxRenderingStrategy} instance.
     */
    public PdfACheckBoxRenderingStrategy() {
        // empty constructor
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void drawCheckBoxContent(DrawContext drawContext, CheckBoxRenderer checkBoxRenderer, Rectangle rectangle) {
        if (!checkBoxRenderer.isBoxChecked()) {
            return;
        }
        final PdfCanvas canvas = drawContext.getCanvas();
        canvas.saveState();
        canvas.setFillColor(ColorConstants.RED);
        final CheckBoxType checkBoxType = (CheckBoxType) checkBoxRenderer.<CheckBoxType>getProperty(
                FormProperty.FORM_CHECKBOX_TYPE,
                CheckBoxType.CROSS);
        drawIcon(checkBoxType, canvas, rectangle);
        canvas.restoreState();
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
