package com.itextpdf.svg.renderers.impl;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.styledxmlparser.css.CssConstants;
import com.itextpdf.styledxmlparser.css.util.CssUtils;
import com.itextpdf.svg.SvgTagConstants;
import com.itextpdf.svg.exceptions.SvgLogMessageConstant;
import com.itextpdf.svg.exceptions.SvgProcessingException;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.svg.utils.SvgCssUtils;
import com.itextpdf.svg.utils.TransformUtils;

import java.io.IOException;
import java.util.List;

/**
 * Draws text to a PdfCanvas.
 * Currently supported:
 *  - only the default font of PDF
 *  - x, y
 */
public class TextSvgNodeRenderer extends AbstractSvgNodeRenderer {

    @Override
    protected void doDraw(SvgDrawContext context) {
        if ( this.attributesAndStyles != null && this.attributesAndStyles.containsKey(SvgTagConstants.TEXT_CONTENT) ) {
            PdfCanvas currentCanvas = context.getCurrentCanvas();
            Rectangle currentViewPort = context.getCurrentViewPort();
            currentCanvas.concatMatrix(TransformUtils.parseTransform("matrix(1 0 0 -1 0 " + currentViewPort.getHeight() + ")"));

            String xRawValue = this.attributesAndStyles.get(SvgTagConstants.X);
            String yRawValue = this.attributesAndStyles.get(SvgTagConstants.Y);
            String fontSizeRawValue = this.attributesAndStyles.get(SvgTagConstants.FONT_SIZE);

            List<String> xValuesList = SvgCssUtils.splitValueList(xRawValue);
            List<String> yValuesList = SvgCssUtils.splitValueList(yRawValue);

            float x = 0f;
            float y = 0f;
            float fontSize = 0f;

            if ( fontSizeRawValue != null && !fontSizeRawValue.isEmpty()) {
                fontSize = CssUtils.parseAbsoluteLength(fontSizeRawValue, CssConstants.PT);
            }

            if ( !xValuesList.isEmpty() ) {
                x = Float.parseFloat(xValuesList.get(0));
            }

            if ( !yValuesList.isEmpty() ) {
                y = Float.parseFloat(yValuesList.get(0));
            }

            currentCanvas.beginText();

            try {
                // TODO font resolution RND-883
                currentCanvas.setFontAndSize(PdfFontFactory.createFont(), fontSize);
            } catch (IOException e) {
                throw new SvgProcessingException(SvgLogMessageConstant.FONT_NOT_FOUND, e);
            }

            currentCanvas.moveText(x, y);
            currentCanvas.setColor(ColorConstants.BLACK, true);
            currentCanvas.showText(this.attributesAndStyles.get(SvgTagConstants.TEXT_CONTENT));

            currentCanvas.endText();
        }
    }
}