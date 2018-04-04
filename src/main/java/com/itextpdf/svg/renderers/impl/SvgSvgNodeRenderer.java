package com.itextpdf.svg.renderers.impl;

import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.styledxmlparser.css.util.CssUtils;
import com.itextpdf.svg.SvgTagConstants;
import com.itextpdf.svg.exceptions.SvgLogMessageConstant;
import com.itextpdf.svg.exceptions.SvgProcessingException;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.svg.utils.SvgCssUtils;
import com.itextpdf.svg.utils.TransformUtils;

/**
 * {@link ISvgNodeRenderer} implementation for the &lt;svg&gt; tag.
 */
public class SvgSvgNodeRenderer extends AbstractBranchSvgNodeRenderer {

    private boolean outermost = true;

    @Override
    protected void doDraw(SvgDrawContext context) {
        // we need to know if we are processing the outermost svg element
        // if this renderer's parent is null than we know we're in the outermost svg tag
        // this is important to set portX, portY, portWidth, and portHeight values
        this.outermost = this.getParent() == null;
        // TODO RND-877

        context.addViewPort(this.calculateViewPort(context));

        PdfCanvas currentCanvas = context.getCurrentCanvas();
        currentCanvas.concatMatrix(this.calculateTransformation(context));

        super.doDraw(context);
    }

    /**
     * Calculate the transformation based on the context. If this renderer is the outermost renderer,
     * we need to flip on the vertical axis and translate the height of the viewport
     *
     * @param context the SVG draw context
     * @return the transformation that needs to be applied to this renderer
     */
    AffineTransform calculateTransformation(SvgDrawContext context) {
        Rectangle viewPort = context.getCurrentViewPort();
        AffineTransform transform;

        if ( outermost) {
            float vertical = viewPort.getY() + viewPort.getHeight();
            // flip coordinate space vertically and translate on the y axis with the viewport height
            transform = TransformUtils.parseTransform("matrix(1 0 0 -1 " + SvgCssUtils.convertFloatToString(viewPort.getX()) + " " + SvgCssUtils.convertFloatToString(vertical));
        } else {
            transform = AffineTransform.getTranslateInstance(viewPort.getX(), viewPort.getY());

        }

        return transform;
    }

    /**
     * Calculate the viewport based on the context.
     *
     * @param context the SVG draw context
     * @return the viewport that applies to this renderer
     */
    Rectangle calculateViewPort(SvgDrawContext context) {
        Rectangle currentViewPort = outermost ? null : context.getCurrentViewPort();

        float portX = 0f;
        float portY = 0f;
        float portWidth = 0f;
        float portHeight = 0f;

        if (outermost) {
            PdfStream contentStream = context.getCurrentCanvas().getContentStream();

            if ( ! contentStream.containsKey(PdfName.BBox) ) {
                throw new SvgProcessingException(SvgLogMessageConstant.ROOT_SVG_NO_BBOX);
            }

            PdfArray bboxArray = contentStream.getAsArray(PdfName.BBox);

            portX = bboxArray.getAsNumber(0).floatValue();
            portY = bboxArray.getAsNumber(1).floatValue();
            portWidth = bboxArray.getAsNumber(2).floatValue() - portX;
            portHeight = bboxArray.getAsNumber(3).floatValue() - portY;
        } else {
            // set default values to parent viewport in the case of a nested svg tag
            portX = currentViewPort.getX();
            portY = currentViewPort.getY();
            portWidth = currentViewPort.getWidth(); // default should be parent portWidth if not outermost
            portHeight = currentViewPort.getHeight(); // default should be parent heigth if not outermost
        }

        if (attributesAndStyles != null) {
            if (!outermost) {
                if (attributesAndStyles.containsKey(SvgTagConstants.X)) {
                    portX = CssUtils.parseAbsoluteLength(attributesAndStyles.get(SvgTagConstants.X));
                }

                if (attributesAndStyles.containsKey(SvgTagConstants.Y)) {
                    portY = CssUtils.parseAbsoluteLength(attributesAndStyles.get(SvgTagConstants.Y));
                }

                if (attributesAndStyles.containsKey(SvgTagConstants.WIDTH)) {
                    portWidth = CssUtils.parseAbsoluteLength(attributesAndStyles.get(SvgTagConstants.WIDTH));
                }

                if (attributesAndStyles.containsKey(SvgTagConstants.HEIGHT)) {
                    portHeight = CssUtils.parseAbsoluteLength(attributesAndStyles.get(SvgTagConstants.HEIGHT));
                }
            }
        }

        return new Rectangle(portX, portY, portWidth, portHeight);
    }
}
