package com.itextpdf.forms.form.renderer.checkboximpl;

import com.itextpdf.forms.fields.properties.CheckBoxType;
import com.itextpdf.forms.form.renderer.CheckBoxRenderer;
import com.itextpdf.forms.logs.FormsLogMessageConstants;
import com.itextpdf.forms.util.DrawingUtil;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.renderer.DrawContext;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.renderer.ParagraphRenderer;

import java.util.HashMap;

/**
 * A factory for creating CheckBoxPdfRenderer objects.
 */
public class CheckBoxPdfRendererFactory extends AbstractCheckBoxRendererFactory {
    private static final HashMap<CheckBoxType, String> CHECKBOX_TYPE_ZAPFDINGBATS_CODE = new HashMap<>();

    static {
        CHECKBOX_TYPE_ZAPFDINGBATS_CODE.put(CheckBoxType.CHECK, "4");
        CHECKBOX_TYPE_ZAPFDINGBATS_CODE.put(CheckBoxType.CIRCLE, "l");
        CHECKBOX_TYPE_ZAPFDINGBATS_CODE.put(CheckBoxType.CROSS, "8");
        CHECKBOX_TYPE_ZAPFDINGBATS_CODE.put(CheckBoxType.DIAMOND, "u");
        CHECKBOX_TYPE_ZAPFDINGBATS_CODE.put(CheckBoxType.SQUARE, "n");
        CHECKBOX_TYPE_ZAPFDINGBATS_CODE.put(CheckBoxType.STAR, "H");
    }

    public CheckBoxPdfRendererFactory(CheckBoxRenderer checkBoxRenderer) {
        super(checkBoxRenderer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IRenderer createFlatRenderer() {
        setupSize();
        Paragraph paragraph = new Paragraph()
                .setWidth(getSize())
                .setHeight(getSize())
                .setMargin(0)
                .setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE)
                .setHorizontalAlignment(HorizontalAlignment.CENTER);
        return new FlatParagraphRenderer(paragraph);
    }

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
            final Rectangle rectangle = getInnerAreaBBox();
            canvas.saveState();
            canvas.setFillColor(getFillColor());
            // matrix transformation to draw the checkbox in the right place
            // because we come here with relative and not absolute coordinates
            canvas.concatMatrix(1, 0, 0, 1, rectangle.getLeft(), rectangle.getBottom());
            final float fontSize = getPropertyAsUnitValue(Property.HEIGHT).getValue();
            CheckBoxType checkBoxType = getCheckBoxType();
            if (checkBoxType == CheckBoxType.CROSS) {
                DrawingUtil.drawCross(canvas, rectangle.getWidth(), rectangle.getHeight(), 1);
                canvas.restoreState();
                return;
            }
            PdfFont fontContainingSymbols;
            try {
                fontContainingSymbols = PdfFontFactory.createFont(StandardFonts.ZAPFDINGBATS);
            } catch (java.io.IOException e) {
                throw new PdfException(e);
            }
            if (fontSize <= 0) {
                throw new PdfException(FormsLogMessageConstants.CHECKBOX_FONT_SIZE_IS_NOT_POSITIVE);
            }
            final String text = CHECKBOX_TYPE_ZAPFDINGBATS_CODE.get(checkBoxType);
            canvas.
                    beginText().
                    setFontAndSize(fontContainingSymbols, fontSize).
                    resetFillColorRgb().
                    setTextMatrix((rectangle.getWidth() - fontContainingSymbols.getWidth(text, fontSize)) / 2,
                            (rectangle.getHeight() - fontContainingSymbols.getAscent(text, fontSize)) / 2).
                    showText(text).
                    endText();

            canvas.restoreState();
        }
    }
}
