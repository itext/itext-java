/*
    $Id$

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

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
package com.itextpdf.layout.renderer;

import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.Point;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.canvas.CanvasArtifact;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.tagutils.IAccessibleElement;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.kernel.pdf.xobject.PdfXObject;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.property.UnitValue;

public class ImageRenderer extends AbstractRenderer {

    private float height;
    private Float width;
    protected Float fixedXPosition;
    protected Float fixedYPosition;
    protected float pivotY;
    protected float deltaX;
    protected float imageWidth;
    protected float imageHeight;

    float[] matrix = new float[6];

    public ImageRenderer(Image image) {
        super(image);
    }

    @Override
    public LayoutResult layout(LayoutContext layoutContext) {
        LayoutArea area = layoutContext.getArea().clone();
        Rectangle layoutBox = area.getBBox();
        applyMargins(layoutBox, false);
        occupiedArea = new LayoutArea(area.getPageNumber(), new Rectangle(layoutBox.getX(), layoutBox.getY() + layoutBox.getHeight(), 0, 0));

        width = retrieveWidth(layoutBox.getWidth());
        Float angle = getPropertyAsFloat(Property.ROTATION_ANGLE);

        PdfXObject xObject = ((Image) (getModelElement())).getXObject();
        imageWidth = xObject.getWidth();
        imageHeight = xObject.getHeight();

        width = width == null ? imageWidth : width;
        height = width / imageWidth * imageHeight;

        fixedXPosition = getPropertyAsFloat(Property.X);
        fixedYPosition = getPropertyAsFloat(Property.Y);

        Float horizontalScaling = getPropertyAsFloat(Property.HORIZONTAL_SCALING);
        Float verticalScaling = getPropertyAsFloat(Property.VERTICAL_SCALING);

        AffineTransform t = new AffineTransform();

        if (xObject instanceof PdfFormXObject && width != imageWidth) {
            horizontalScaling *= width / imageWidth;
            verticalScaling *= height / imageHeight;
        }

        if (horizontalScaling != 1) {
            if (xObject instanceof PdfFormXObject) {
                t.scale(horizontalScaling, 1);
            }
            width *= horizontalScaling;
        }
        if (verticalScaling != 1) {
            if (xObject instanceof PdfFormXObject) {
                t.scale(1, verticalScaling);
            }
            height *= verticalScaling;
        }

        float imageItselfScaledWidth = width;
        float imageItselfScaledHeight = height;

        // See in adjustPositionAfterRotation why angle = 0 is necessary
        if (null == angle) {
            angle = 0f;
        }
        t.rotate(angle);
        float scaleCoef = adjustPositionAfterRotation(angle, layoutBox.getWidth(), layoutBox.getHeight());

        imageItselfScaledHeight *= scaleCoef;
        imageItselfScaledWidth *= scaleCoef;
        if (xObject instanceof PdfFormXObject) {
            t.scale(scaleCoef, scaleCoef);
        }

        getMatrix(t, imageItselfScaledWidth, imageItselfScaledHeight);

        if (!Boolean.TRUE.equals(getPropertyAsBoolean(Property.FORCED_PLACEMENT)) && (width > layoutBox.getWidth() || height > layoutBox.getHeight())) {
            return new LayoutResult(LayoutResult.NOTHING, occupiedArea, null, this);
        }

        occupiedArea.getBBox().moveDown(height);
        occupiedArea.getBBox().setHeight(height);
        occupiedArea.getBBox().setWidth(width);

        float leftMargin = getPropertyAsFloat(Property.MARGIN_LEFT);
        float topMargin = getPropertyAsFloat(Property.MARGIN_TOP);
        if (leftMargin != 0 || topMargin != 0) {
            translateImage(leftMargin, topMargin, t);
            getMatrix(t, imageItselfScaledWidth, imageItselfScaledHeight);
        }

        applyMargins(occupiedArea.getBBox(), true);
        return new LayoutResult(LayoutResult.FULL, occupiedArea, null, null);
    }

    @Override
    public void draw(DrawContext drawContext) {
        super.draw(drawContext);

        PdfDocument document = drawContext.getDocument();
        boolean isTagged = drawContext.isTaggingEnabled() && getModelElement() instanceof IAccessibleElement;
        boolean isArtifact = false;
        TagTreePointer tagPointer = null;
        if (isTagged) {
            tagPointer = document.getTagStructureContext().getAutoTaggingPointer();
            IAccessibleElement accessibleElement = (IAccessibleElement) getModelElement();
            PdfName role = accessibleElement.getRole();
            if (role != null && !PdfName.Artifact.equals(role)) {
                AccessibleAttributesApplier.applyLayoutAttributes(accessibleElement.getRole(), this, document);
                tagPointer.addTag(accessibleElement);
            } else {
                isTagged = false;
                if (PdfName.Artifact.equals(role)) {
                    isArtifact = true;
                }
            }
        }

        applyMargins(occupiedArea.getBBox(), false);

        boolean isRelativePosition = isRelativePosition();
        if (isRelativePosition) {
            applyAbsolutePositioningTranslation(false);
        }

        if (fixedYPosition == null) {
            fixedYPosition = occupiedArea.getBBox().getY() + pivotY;
        }
        if (fixedXPosition == null) {
            fixedXPosition = occupiedArea.getBBox().getX();
        }

        PdfCanvas canvas = drawContext.getCanvas();
        if (isTagged) {
            canvas.openTag(tagPointer.getTagReference());
        } else if (isArtifact) {
            canvas.openTag(new CanvasArtifact());
        }

        PdfXObject xObject = ((Image) (getModelElement())).getXObject();
        canvas.addXObject(xObject, matrix[0], matrix[1], matrix[2], matrix[3], fixedXPosition + deltaX, fixedYPosition);
        if (Boolean.valueOf(true).equals(getPropertyAsBoolean(Property.FLUSH_ON_DRAW))) {
            xObject.flush();
        }

        if (isTagged || isArtifact) {
            canvas.closeTag();
        }

        if (isRelativePosition) {
            applyAbsolutePositioningTranslation(true);
        }

        applyMargins(occupiedArea.getBBox(), true);

        if (isTagged) {
            tagPointer.moveToParent();
        }
    }

    @Override
    public IRenderer getNextRenderer() {
        return null;
    }

    protected ImageRenderer autoScale(LayoutArea area) {
        if (width > area.getBBox().getWidth()) {
            setProperty(Property.HEIGHT, area.getBBox().getWidth() / width * imageHeight);
            setProperty(Property.WIDTH, UnitValue.createPointValue(area.getBBox().getWidth()));
            // if still image is not scaled properly
            if (getPropertyAsFloat(Property.HEIGHT) > area.getBBox().getHeight()) {
                setProperty(Property.WIDTH, UnitValue.createPointValue(area.getBBox().getHeight() / getPropertyAsFloat(Property.HEIGHT) * ((UnitValue)getProperty(Property.WIDTH)).getValue()));
                setProperty(Property.HEIGHT, UnitValue.createPointValue(area.getBBox().getHeight()));
            }
        }

        return this;
    }

    private void getMatrix(AffineTransform t, float imageItselfScaledWidth, float imageItselfScaledHeight) {
        t.getMatrix(matrix);
        PdfXObject xObject = ((Image) (getModelElement())).getXObject();
        if (xObject instanceof PdfImageXObject) {
            matrix[0] *= imageItselfScaledWidth;
            matrix[1] *= imageItselfScaledWidth;
            matrix[2] *= imageItselfScaledHeight;
            matrix[3] *= imageItselfScaledHeight;
        }
    }

    private float adjustPositionAfterRotation(float angle, float maxWidth, float maxHeight) {
        if (angle != 0) {
            AffineTransform t = AffineTransform.getRotateInstance(angle);
            Point p00 = t.transform(new Point(0, 0), new Point());
            Point p01 = t.transform(new Point(0, height), new Point());
            Point p10 = t.transform(new Point(width, 0), new Point());
            Point p11 = t.transform(new Point(width, height), new Point());

            double[] xValues = {p01.getX(), p10.getX(), p11.getX()};
            double[] yValues = {p01.getY(), p10.getY(), p11.getY()};

            double minX = p00.getX();
            double minY = p00.getY();
            double maxX = minX;
            double maxY = minY;

            for (double x : xValues) {
                minX = Math.min(minX, x);
                maxX = Math.max(maxX, x);
            }
            for (double y : yValues) {
                minY = Math.min(minY, y);
                maxY = Math.max(maxY, y);
            }

            height = (float) (maxY - minY);
            width = (float) (maxX - minX);
            pivotY = (float) (p00.getY() - minY);

            deltaX = -(float) minX;
        }
        // Rotating image can cause fitting into area problems.
        // So let's find scaling coefficient
        // TODO
        float scaleCoeff = 1;
        // hasProperty(Property) checks only properties field, cannot use it
        if (null != getPropertyAsBoolean(Property.AUTO_SCALE) && getPropertyAsBoolean(Property.AUTO_SCALE)) {
            scaleCoeff = Math.min(maxWidth / width, maxHeight / height);
            height *= scaleCoeff;
            width *= scaleCoeff;
        } else if (null != getPropertyAsBoolean(Property.AUTO_SCALE_WIDTH) && getPropertyAsBoolean(Property.AUTO_SCALE_WIDTH)) {
            scaleCoeff = maxWidth / width;
            height *= scaleCoeff;
            width = maxWidth;
        } else if (null != getPropertyAsBoolean(Property.AUTO_SCALE_HEIGHT) && getPropertyAsBoolean(Property.AUTO_SCALE_HEIGHT)) {
            scaleCoeff = maxHeight / height;
            height = maxHeight;
            width *= scaleCoeff;
        }
        pivotY *= scaleCoeff;
        return scaleCoeff;
    }

    private void translateImage(float xDistance, float yDistance, AffineTransform t) {
        t.translate(xDistance, yDistance);
        t.getMatrix(matrix);
        if (fixedXPosition != null) {
            fixedXPosition += (float)t.getTranslateX();
        }
        if (fixedYPosition != null) {
            fixedYPosition += (float)t.getTranslateY();
        }
    }
}
