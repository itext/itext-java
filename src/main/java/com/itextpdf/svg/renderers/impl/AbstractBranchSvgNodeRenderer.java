package com.itextpdf.svg.renderers.impl;

import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.pdf.xobject.PdfXObject;
import com.itextpdf.styledxmlparser.css.util.CssUtils;
import com.itextpdf.svg.SvgTagConstants;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.svg.utils.SvgCssUtils;
import com.itextpdf.svg.utils.TransformUtils;

import java.util.List;

/**
 * Abstract class that will be the superclass for any element that can function
 * as a parent.
 */
public abstract class AbstractBranchSvgNodeRenderer extends AbstractSvgNodeRenderer {

    /**
     * Method that will set properties to be inherited by this branch renderer's
     * children and will iterate over all children in order to draw them.
     *
     * @param context the object that knows the place to draw this element and
     *                maintains its state
     */
    @Override
    protected void doDraw(SvgDrawContext context) {
        if (getChildren().size() > 0) { // if branch has no children, don't do anything
            PdfStream stream = new PdfStream();
            stream.put(PdfName.Type, PdfName.XObject);
            stream.put(PdfName.Subtype, PdfName.Form);
            stream.put(PdfName.BBox, new PdfArray(context.getCurrentViewPort()));
            PdfFormXObject xObject = (PdfFormXObject) PdfXObject.makeXObject(stream);

            PdfCanvas newCanvas = new PdfCanvas(xObject, context.getCurrentCanvas().getDocument());
            applyViewBox(context);
            context.pushCanvas(newCanvas);
            applyViewport(context);

            for (ISvgNodeRenderer child : getChildren()) {
                newCanvas.saveState();
                child.draw(context);
                newCanvas.restoreState();
            }

            cleanUp(context);

            AffineTransform transformation = new AffineTransform();

            if (attributesAndStyles != null && attributesAndStyles.containsKey(SvgTagConstants.TRANSFORM)) {
                transformation = TransformUtils.parseTransform(attributesAndStyles.get(SvgTagConstants.TRANSFORM));
            }

            // TODO DEVSIX-1891
            float[] matrixValues = new float[6];
            transformation.getMatrix(matrixValues);

            // TODO DEVSIX-1890
            context.getCurrentCanvas().addXObject(xObject, matrixValues[0], matrixValues[1], matrixValues[2], matrixValues[3], matrixValues[4], matrixValues[5]);

            if (attributesAndStyles != null && attributesAndStyles.containsKey(SvgTagConstants.ID)) {
                context.addNamedObject(attributesAndStyles.get(SvgTagConstants.ID), xObject);
            }
        }
    }

    /**
     * Applies a transformation based on a viewBox for a given branch node.
     *
     * @param context current svg draw context
     */
    private void applyViewBox(SvgDrawContext context) {
        if (this.attributesAndStyles != null ) {
            if (this.attributesAndStyles.containsKey(SvgTagConstants.VIEWBOX)) {
                String viewBoxValues = attributesAndStyles.get(SvgTagConstants.VIEWBOX);
                List<String> valueStrings = SvgCssUtils.splitValueList(viewBoxValues);
                float[] values = new float[valueStrings.size()];

                for (int i = 0; i < values.length; i++) {
                    values[i] = CssUtils.parseAbsoluteLength(valueStrings.get(i));
                }

                Rectangle currentViewPort = context.getCurrentViewPort();

                float scaleWidth = currentViewPort.getWidth() / values[2];
                float scaleHeight = currentViewPort.getHeight() / values[3];

                AffineTransform scale = AffineTransform.getScaleInstance(scaleWidth, scaleHeight);
                context.getCurrentCanvas().concatMatrix(scale);

                AffineTransform transform = processAspectRatio(context, values);
                context.getCurrentCanvas().concatMatrix(transform);
            }
        }
    }

    /**
     * Applies a clipping operation based on the view port.
     *
     * @param context the svg draw context
     */
    private void applyViewport(SvgDrawContext context) {
        if (getParent() != null) {
            PdfCanvas currentCanvas = context.getCurrentCanvas();

            currentCanvas.rectangle(context.getCurrentViewPort());
            currentCanvas.clip();
            currentCanvas.newPath();
        }
    }

    /**
     * If present, process the preserveAspectRatio.
     *
     * @param context the svg draw context
     * @param viewBoxValues the four values depicting the viewbox [min-x min-y width height]
     * @return the transformation based on the preserveAspectRatio value
     */
    private AffineTransform processAspectRatio(SvgDrawContext context, float[] viewBoxValues) {
        AffineTransform transform = new AffineTransform();

        if (this.attributesAndStyles.containsKey(SvgTagConstants.PRESERVE_ASPECT_RATIO)) {
            Rectangle currentViewPort = context.getCurrentViewPort();

            String preserveAspectRatioValue = this.attributesAndStyles.get(SvgTagConstants.PRESERVE_ASPECT_RATIO);

            List<String> values = SvgCssUtils.splitValueList(preserveAspectRatioValue);

            if (SvgTagConstants.DEFER.equalsIgnoreCase(values.get(0))) {
                values.remove(0);
            }

            String align = values.get(0);

            String meetOrSlice = SvgTagConstants.MEET_OR_SLICE_DEFAULT;

            if ( values.size() > 1 ) {
                meetOrSlice = values.get(1);
            }

            float x = 0f;
            float y = 0f;

            float midXBox = viewBoxValues[0] + (viewBoxValues[2] / 2);
            float midYBox = viewBoxValues[1] + (viewBoxValues[3] / 2);

            float midXPort = currentViewPort.getX() + (currentViewPort.getWidth() / 2);
            float midYPort = currentViewPort.getY() + (currentViewPort.getHeight() / 2);

            switch (align.toLowerCase()) {
            case SvgTagConstants.NONE:
                break;

            case SvgTagConstants.XMIN_YMIN:
                x = -viewBoxValues[0];
                y = -viewBoxValues[1];
                break;
            case SvgTagConstants.XMIN_YMID:
                x = -viewBoxValues[0];
                y = midYPort - midYBox;
                break;
            case SvgTagConstants.XMIN_YMAX:
                x = -viewBoxValues[0];
                y = currentViewPort.getHeight() - viewBoxValues[3];
                break;

            case SvgTagConstants.XMID_YMIN:
                x = midXPort - midXBox;
                y = -viewBoxValues[1];
                break;
            case SvgTagConstants.XMID_YMAX:
                x = midXPort - midXBox;
                y = currentViewPort.getHeight() - viewBoxValues[3];
                break;

            case SvgTagConstants.XMAX_YMIN:
                x = currentViewPort.getWidth() - viewBoxValues[2];
                y = -viewBoxValues[1];
                break;
            case SvgTagConstants.XMAX_YMID:
                x = currentViewPort.getWidth() - viewBoxValues[2];
                y = midYPort - midYBox;
                break;
            case SvgTagConstants.XMAX_YMAX:
                x = currentViewPort.getWidth() - viewBoxValues[2];
                y = currentViewPort.getHeight() - viewBoxValues[3];
                break;

            case SvgTagConstants.DEFAULT_ASPECT_RATIO:
                x = midXPort - midXBox;
                y = midYPort - midYBox;
            default:
                break;
            }

            transform.translate(x, y);
        }

        return transform;
    }

    /**
     * Cleans up the SvgDrawContext by removing the current viewport and by popping the current canvas.
     *
     * @param context context to clean
     */
    private void cleanUp(SvgDrawContext context) {
        if (getParent() != null) {
            context.removeCurrentViewPort();
        }

        context.popCanvas();
    }
}
