/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.svg.renderers.impl;

import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.Matrix;
import com.itextpdf.kernel.geom.NoninvertibleTransformException;
import com.itextpdf.kernel.geom.Point;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.pdf.xobject.PdfXObject;
import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.SvgConstants.Values;
import com.itextpdf.svg.logs.SvgLogMessageConstant;
import com.itextpdf.svg.renderers.IBranchSvgNodeRenderer;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.svg.utils.SvgCoordinateUtils;
import com.itextpdf.svg.utils.SvgCssUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract class that will be the superclass for any element that can function
 * as a parent.
 */
public abstract class AbstractBranchSvgNodeRenderer extends AbstractSvgNodeRenderer implements IBranchSvgNodeRenderer {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractBranchSvgNodeRenderer.class);

    /**
     * The number of viewBox values.
     * Deprecate in favour of {@code SvgConstants.Values.VIEWBOX_VALUES_NUMBER}
     */
    @Deprecated
    protected final static int VIEWBOX_VALUES_NUMBER = 4;

    private static final float EPS = 1e-6f;

    private final List<ISvgNodeRenderer> children = new ArrayList<>();

    /**
     * Method that will set properties to be inherited by this branch renderer's
     * children and will iterate over all children in order to draw them.
     *
     * @param context the object that knows the place to draw this element and
     *                maintains its state
     */
    @Override
    protected void doDraw(SvgDrawContext context) {
        // If branch has no children, don't do anything
        if (!getChildren().isEmpty()) {
            PdfStream stream = new PdfStream();
            stream.put(PdfName.Type, PdfName.XObject);
            stream.put(PdfName.Subtype, PdfName.Form);

            PdfFormXObject xObject = (PdfFormXObject) PdfXObject.makeXObject(stream);

            PdfCanvas newCanvas = new PdfCanvas(xObject, context.getCurrentCanvas().getDocument());
            applyViewBox(context);

            boolean overflowVisible = isOverflowVisible(this);
            Rectangle bbBox;
            // TODO (DEVSIX-3482) Currently overflow logic works only for markers.  Update this code after the ticket will be finished.
            if (this instanceof MarkerSvgNodeRenderer && overflowVisible) {
                bbBox = getBBoxAccordingToVisibleOverflow(context);
            } else {
                bbBox = context.getCurrentViewPort().clone();
            }
            stream.put(PdfName.BBox, new PdfArray(bbBox));

            context.pushCanvas(newCanvas);

            // TODO (DEVSIX-3482) Currently overflow logic works only for markers. Update this code after the ticket will be finished.
            if (!(this instanceof MarkerSvgNodeRenderer) || !overflowVisible) {
                applyViewportClip(context);
            }

            for (ISvgNodeRenderer child : getChildren()) {
                if (!(child instanceof MarkerSvgNodeRenderer)) {
                    newCanvas.saveState();
                    child.draw(context);
                    newCanvas.restoreState();
                }
            }

            cleanUp(context);

            // Transformation already happened in AbstractSvgNodeRenderer, so no need to do a transformation here
            context.getCurrentCanvas().addXObjectAt(xObject, bbBox.getX(), bbBox.getY());
        }
    }

    /**
     * Applies a transformation based on a viewBox for a given branch node.
     *
     * @param context current svg draw context
     */
    void applyViewBox(SvgDrawContext context) {
        Rectangle currentViewPort = context.getCurrentViewPort();
        float[] viewBoxValues = SvgCssUtils.parseViewBox(this);
        if (viewBoxValues == null || viewBoxValues.length < SvgConstants.Values.VIEWBOX_VALUES_NUMBER) {
            viewBoxValues = new float[]{0, 0, currentViewPort.getWidth(), currentViewPort.getHeight()};
        }
        calculateAndApplyViewBox(context, viewBoxValues, currentViewPort);
    }

    String[] retrieveAlignAndMeet() {
        String meetOrSlice = SvgConstants.Values.MEET;
        String align = SvgConstants.Values.DEFAULT_ASPECT_RATIO;

        String preserveAspectRatioValue = this.attributesAndStyles.get(SvgConstants.Attributes.PRESERVE_ASPECT_RATIO);
        // TODO: DEVSIX-3923 remove normalization (.toLowerCase)
        if (preserveAspectRatioValue == null) {
            preserveAspectRatioValue =
                    this.attributesAndStyles.get(SvgConstants.Attributes.PRESERVE_ASPECT_RATIO.toLowerCase());
        }

        if (this.attributesAndStyles.containsKey(SvgConstants.Attributes.PRESERVE_ASPECT_RATIO) ||
                this.attributesAndStyles.containsKey(SvgConstants.Attributes.PRESERVE_ASPECT_RATIO.toLowerCase())) {
            List<String> aspectRatioValuesSplitValues = SvgCssUtils.splitValueList(preserveAspectRatioValue);

            align = aspectRatioValuesSplitValues.get(0).toLowerCase();
            if (aspectRatioValuesSplitValues.size() > 1) {
                meetOrSlice = aspectRatioValuesSplitValues.get(1).toLowerCase();
            }
        }

        if (this instanceof MarkerSvgNodeRenderer && !SvgConstants.Values.NONE.equals(align)
                && SvgConstants.Values.MEET.equals(meetOrSlice)) {
            // Browsers do not correctly display markers with 'meet' option in the preserveAspectRatio attribute.
            // The Chrome, IE, and Firefox browsers set the align value to 'xMinYMin' regardless of the actual align.
            align = Values.XMIN_YMIN;
        }

        return new String[] {align, meetOrSlice};
    }

    /**
     * Applies a clipping operation based on the view port.
     *
     * @param context the svg draw context
     */
    private void applyViewportClip(SvgDrawContext context) {
        PdfCanvas currentCanvas = context.getCurrentCanvas();
        currentCanvas.rectangle(context.getCurrentViewPort());
        currentCanvas.clip();
        currentCanvas.endPath();
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

    @Override
    public final void addChild(ISvgNodeRenderer child) {
        // final method, in order to disallow adding null
        if (child != null) {
            children.add(child);
        }
    }

    @Override
    public final List<ISvgNodeRenderer> getChildren() {
        // final method, in order to disallow modifying the List
        return Collections.unmodifiableList(children);
    }

    /**
     * Create a deep copy of every child renderer and add them to the passed {@link AbstractBranchSvgNodeRenderer}
     *
     * @param deepCopy renderer to add copies of children to
     */
    protected final void deepCopyChildren(AbstractBranchSvgNodeRenderer deepCopy) {
        for (ISvgNodeRenderer child : children) {
            ISvgNodeRenderer newChild = child.createDeepCopy();
            child.setParent(deepCopy);
            deepCopy.addChild(newChild);
        }
    }

    @Override
    void postDraw(SvgDrawContext context) {
    }

    @Override
    public abstract ISvgNodeRenderer createDeepCopy();

    @Override
    void setPartOfClipPath(boolean isPart) {
        super.setPartOfClipPath(isPart);
        for (ISvgNodeRenderer child : children) {
            if (child instanceof AbstractSvgNodeRenderer) {
                ((AbstractSvgNodeRenderer) child).setPartOfClipPath(isPart);
            }
        }
    }

    void calculateAndApplyViewBox(SvgDrawContext context, float[] values, Rectangle currentViewPort) {
        // If viewBox width or height is zero we should disable rendering of the element.
        if (Math.abs(values[2]) < EPS || Math.abs(values[3]) < EPS) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info(SvgLogMessageConstant.VIEWBOX_WIDTH_OR_HEIGHT_IS_ZERO);
            }
            context.getCurrentCanvas().concatMatrix(AffineTransform.getScaleInstance(0, 0));
            return;
        }
        String[] alignAndMeet = retrieveAlignAndMeet();
        String align = alignAndMeet[0];
        String meetOrSlice = alignAndMeet[1];

        Rectangle viewBox = new Rectangle(values[0], values[1], values[2], values[3]);
        Rectangle appliedViewBox = SvgCoordinateUtils.applyViewBox(viewBox, currentViewPort, align, meetOrSlice);

        float scaleWidth = appliedViewBox.getWidth() / viewBox.getWidth();
        float scaleHeight = appliedViewBox.getHeight() / viewBox.getHeight();

        AffineTransform scale = AffineTransform.getScaleInstance(scaleWidth, scaleHeight);

        float xOffset = appliedViewBox.getX() / scaleWidth - viewBox.getX();
        float yOffset = appliedViewBox.getY() / scaleHeight - viewBox.getY();

        AffineTransform transform = new AffineTransform();
        transform.translate(xOffset, yOffset);

        if (!transform.isIdentity()) {
            context.getCurrentCanvas().concatMatrix(transform);
            // Apply inverse translation to viewport to make it line up nicely
            context.getCurrentViewPort()
                    .setX(currentViewPort.getX() - (float) transform.getTranslateX())
                    .setY(currentViewPort.getY() - (float) transform.getTranslateY());
        }

        if (this instanceof MarkerSvgNodeRenderer) {
            ((MarkerSvgNodeRenderer) this).applyMarkerAttributes(context);
        }

        if (!scale.isIdentity()) {
            context.getCurrentCanvas().concatMatrix(scale);
            // Inverse scaling needs to be applied to viewport dimensions
            context.getCurrentViewPort()
                    .setWidth(currentViewPort.getWidth() / scaleWidth)
                    .setX(currentViewPort.getX() / scaleWidth)
                    .setHeight(currentViewPort.getHeight() / scaleHeight)
                    .setY(currentViewPort.getY() / scaleHeight);
        }
    }

    private static boolean isOverflowVisible(AbstractSvgNodeRenderer currentElement) {
        return (CommonCssConstants.VISIBLE.equals(currentElement.attributesAndStyles.get(CommonCssConstants.OVERFLOW))
                || CommonCssConstants.AUTO.equals(currentElement.attributesAndStyles.get(CommonCssConstants.OVERFLOW)));
    }

    /**
     * When in the svg element {@code overflow} is {@code visible} the corresponding formXObject should have a BBox
     * (form XObject’s bounding box; see PDF 32000-1:2008 - 8.10.2 Form Dictionaries) that should cover the entire svg
     * space (page in pdf) in order to be able to show parts of the element which are outside the current element
     * viewPort. To do this, we get the inverse matrix of all the current transformation matrix changes and apply it
     * to the root viewPort. This allows you to get the root rectangle in the final coordinate system.
     *
     * @param context current context to get canvases and view ports
     *
     * @return the set to {@code PdfStream} bbox
     */
    private static Rectangle getBBoxAccordingToVisibleOverflow(SvgDrawContext context) {
        List<PdfCanvas> canvases = new ArrayList<>();
        int canvasesSize = context.size();
        for (int i = 0; i < canvasesSize; i++) {
            canvases.add(context.popCanvas());
        }
        AffineTransform transform = new AffineTransform();
        for (int i = canvases.size() - 1; i >= 0; i--) {
            PdfCanvas canvas = canvases.get(i);
            Matrix matrix = canvas.getGraphicsState().getCtm();
            transform.concatenate(new AffineTransform(matrix.get(0), matrix.get(1), matrix.get(3),
                    matrix.get(4), matrix.get(6), matrix.get(7)));
            context.pushCanvas(canvas);
        }
        try {
            transform = transform.createInverse();
        } catch (NoninvertibleTransformException e) {
            Logger logger = LoggerFactory.getLogger(AbstractBranchSvgNodeRenderer.class);
            logger.warn(SvgLogMessageConstant.UNABLE_TO_GET_INVERSE_MATRIX_DUE_TO_ZERO_DETERMINANT);
            // Case with zero determiner (see PDF 32000-1:2008 - 8.3.4 Transformation Matrices - NOTE 3)
            // for example with a, b, c, d in cm equal to 0
            return new Rectangle(0, 0, 0, 0);
        }
        Point[] points = context.getRootViewPort().toPointsArray();
        transform.transform(points, 0, points, 0, points.length);
        return Rectangle.calculateBBox(Arrays.asList(points));
    }
}
