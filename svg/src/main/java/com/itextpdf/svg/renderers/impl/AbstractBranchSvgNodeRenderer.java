/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
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
import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.renderers.IBranchSvgNodeRenderer;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.svg.utils.SvgCssUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract class that will be the superclass for any element that can function
 * as a parent.
 */
public abstract class AbstractBranchSvgNodeRenderer extends AbstractSvgNodeRenderer implements IBranchSvgNodeRenderer {

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
        if (getChildren().size() > 0) { // if branch has no children, don't do anything
            PdfStream stream = new PdfStream();
            stream.put(PdfName.Type, PdfName.XObject);
            stream.put(PdfName.Subtype, PdfName.Form);

            PdfFormXObject xObject = (PdfFormXObject) PdfXObject.makeXObject(stream);

            PdfCanvas newCanvas = new PdfCanvas(xObject, context.getCurrentCanvas().getDocument());
            applyViewBox(context);
            //Bounding box needs to be written after viewbox calculations to account for pdf syntax interaction
            stream.put(PdfName.BBox, new PdfArray(context.getCurrentViewPort()));

            context.pushCanvas(newCanvas);
            applyViewport(context);

            for (ISvgNodeRenderer child : getChildren()) {
                newCanvas.saveState();
                child.draw(context);
                newCanvas.restoreState();
            }

            cleanUp(context);

            context.getCurrentCanvas().addXObject(xObject, 0, 0); // transformation already happened in AbstractSvgNodeRenderer, so no need to do a transformation here
        }
    }

    /**
     * Applies a transformation based on a viewBox for a given branch node.
     *
     * @param context current svg draw context
     */
    private void applyViewBox(SvgDrawContext context) {
        if (this.attributesAndStyles != null) {
            if (this.attributesAndStyles.containsKey(SvgConstants.Attributes.VIEWBOX)) {
                //Parse aspect ratio related stuff
                String viewBoxValues = attributesAndStyles.get(SvgConstants.Attributes.VIEWBOX);
                List<String> valueStrings = SvgCssUtils.splitValueList(viewBoxValues);
                float[] values = new float[valueStrings.size()];

                for (int i = 0; i < values.length; i++) {
                    values[i] = CssUtils.parseAbsoluteLength(valueStrings.get(i));
                }

                Rectangle currentViewPort = context.getCurrentViewPort();

                float scaleWidth = currentViewPort.getWidth() / values[2];
                float scaleHeight = currentViewPort.getHeight() / values[3];

                AffineTransform scale = AffineTransform.getScaleInstance(scaleWidth, scaleHeight);
                if (!scale.isIdentity()) {
                    context.getCurrentCanvas().concatMatrix(scale);

                    //Inverse scaling needs to be applied to viewport dimensions
                    context.getCurrentViewPort().setWidth(currentViewPort.getWidth() / scaleWidth);
                    context.getCurrentViewPort().setX(currentViewPort.getX() / scaleWidth);
                    context.getCurrentViewPort().setHeight(currentViewPort.getHeight() / scaleHeight);
                    context.getCurrentViewPort().setY(currentViewPort.getY() / scaleHeight);
                }

                AffineTransform transform = processAspectRatio(context, values);
                if (!transform.isIdentity()) {
                    //TODO (RND-876)
                    context.getCurrentCanvas().writeLiteral("% applying viewbox aspect ratio correction (not correct) \n");
                    //context.getCurrentCanvas().concatMatrix(transform);
                }
            }
        }
    }

    /**
     * Applies a clipping operation based on the view port.
     *
     * @param context the svg draw context
     */
    private void applyViewport(SvgDrawContext context) {
        if (getParent() != null && getParent() instanceof AbstractSvgNodeRenderer) {
            AbstractSvgNodeRenderer parent = (AbstractSvgNodeRenderer) getParent();
            PdfCanvas currentCanvas = context.getCurrentCanvas();

            currentCanvas.rectangle(context.getCurrentViewPort());
            currentCanvas.clip();
            currentCanvas.newPath();

            if (parent.canConstructViewPort()) {
                currentCanvas.concatMatrix(parent.calculateViewPortTranslation(context));
            }
        }
    }

    /**
     * If present, process the preserveAspectRatio.
     *
     * @param context       the svg draw context
     * @param viewBoxValues the four values depicting the viewbox [min-x min-y width height]
     * @return the transformation based on the preserveAspectRatio value
     */
    private AffineTransform processAspectRatio(SvgDrawContext context, float[] viewBoxValues) {
        AffineTransform transform = new AffineTransform();

        if (this.attributesAndStyles.containsKey(SvgConstants.Attributes.PRESERVE_ASPECT_RATIO)) {
            Rectangle currentViewPort = context.getCurrentViewPort();
            String preserveAspectRatioValue = this.attributesAndStyles.get(SvgConstants.Attributes.PRESERVE_ASPECT_RATIO);
            List<String> values = SvgCssUtils.splitValueList(preserveAspectRatioValue);

            if (SvgConstants.Values.DEFER.equalsIgnoreCase(values.get(0))) {
                values.remove(0);
            }

            String align = values.get(0);

            float x = 0f;
            float y = 0f;

            float midXBox = viewBoxValues[0] + (viewBoxValues[2] / 2);
            float midYBox = viewBoxValues[1] + (viewBoxValues[3] / 2);

            float midXPort = currentViewPort.getX() + (currentViewPort.getWidth() / 2);
            float midYPort = currentViewPort.getY() + (currentViewPort.getHeight() / 2);

            switch (align.toLowerCase()) {
            case SvgConstants.Values.NONE:
                break;

            case SvgConstants.Values.XMIN_YMIN:
                x = -viewBoxValues[0];
                y = -viewBoxValues[1];
                break;
            case SvgConstants.Values.XMIN_YMID:
                x = -viewBoxValues[0];
                y = midYPort - midYBox;
                break;
            case SvgConstants.Values.XMIN_YMAX:
                x = -viewBoxValues[0];
                y = currentViewPort.getHeight() - viewBoxValues[3];
                break;

            case SvgConstants.Values.XMID_YMIN:
                x = midXPort - midXBox;
                y = -viewBoxValues[1];
                break;
            case SvgConstants.Values.XMID_YMAX:
                x = midXPort - midXBox;
                y = currentViewPort.getHeight() - viewBoxValues[3];
                break;

            case SvgConstants.Values.XMAX_YMIN:
                x = currentViewPort.getWidth() - viewBoxValues[2];
                y = -viewBoxValues[1];
                break;
            case SvgConstants.Values.XMAX_YMID:
                x = currentViewPort.getWidth() - viewBoxValues[2];
                y = midYPort - midYBox;
                break;
            case SvgConstants.Values.XMAX_YMAX:
                x = currentViewPort.getWidth() - viewBoxValues[2];
                y = currentViewPort.getHeight() - viewBoxValues[3];
                break;

            case SvgConstants.Values.DEFAULT_ASPECT_RATIO:
            default:
                x = midXPort - midXBox;
                y = midYPort - midYBox;
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
     * @param deepCopy renderer to add copies of children to
     */
    protected final void deepCopyChildren(AbstractBranchSvgNodeRenderer deepCopy) {
        for (ISvgNodeRenderer child:children) {
            ISvgNodeRenderer newChild = child.createDeepCopy();
            child.setParent(deepCopy);
            deepCopy.addChild(newChild);
        }
    }

    @Override void postDraw(SvgDrawContext context) {}

    @Override
    public abstract ISvgNodeRenderer createDeepCopy();

    @Override
    public boolean equals(Object other){
        if (other == null || this.getClass() != other.getClass()) {
            return false;
        }
        AbstractBranchSvgNodeRenderer oabr = (AbstractBranchSvgNodeRenderer) other;
        boolean result = super.equals(oabr);
        if(result){
            result &= children.equals(oabr.getChildren());
        }
        return result;
    }

    @Override
    public int hashCode() {
        return super.hashCode()*7 + 255 + children.hashCode();
    }

    @Override
    void setPartOfClipPath(boolean isPart) {
        super.setPartOfClipPath(isPart);
        for (ISvgNodeRenderer child : children) {
            if (child instanceof AbstractSvgNodeRenderer) {
                ((AbstractSvgNodeRenderer) child).setPartOfClipPath(isPart);
            }
        }
    }
}
