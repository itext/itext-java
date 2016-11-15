/*

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
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.property.UnitValue;

import java.util.List;

public class ImageRenderer extends AbstractRenderer {

    private Float height;
    private Float width;
    protected Float fixedXPosition;
    protected Float fixedYPosition;
    protected float pivotY;
    protected float deltaX;
    protected float imageWidth;
    protected float imageHeight;
    private float imageItselfScaledWidth;
    private float imageItselfScaledHeight;
    private Rectangle initialOccupiedAreaBBox;

    float[] matrix = new float[6];

    /**
     * Creates an ImageRenderer from its corresponding layout object.
     *
     * @param image the {@link com.itextpdf.layout.element.Image} which this object should manage
     */
    public ImageRenderer(Image image) {
        super(image);
    }

    @Override
    public LayoutResult layout(LayoutContext layoutContext) {
        LayoutArea area = layoutContext.getArea().clone();
        Rectangle layoutBox = area.getBBox();
        applyMargins(layoutBox, false);
        Border[] borders = getBorders();
        applyBorderBox(layoutBox, borders, false);
        occupiedArea = new LayoutArea(area.getPageNumber(), new Rectangle(layoutBox.getX(), layoutBox.getY() + layoutBox.getHeight(), 0, 0));

        width = retrieveWidth(layoutBox.getWidth());
        height = retrieveHeight();
        Float angle = this.getPropertyAsFloat(Property.ROTATION_ANGLE);

        PdfXObject xObject = ((Image) (getModelElement())).getXObject();
        imageWidth = xObject.getWidth();
        imageHeight = xObject.getHeight();

        if (width == null && height == null) {
            width = imageWidth;
            height = (float) width / imageWidth * imageHeight;
        } else if (width == null) {
            width = (float) height / imageHeight * imageWidth;
        } else if (height == null) {
            height = (float) width / imageWidth * imageHeight;
        }

        fixedXPosition = this.getPropertyAsFloat(Property.X);
        fixedYPosition = this.getPropertyAsFloat(Property.Y);

        Float horizontalScaling = this.getPropertyAsFloat(Property.HORIZONTAL_SCALING, 1f);
        Float verticalScaling = this.getPropertyAsFloat(Property.VERTICAL_SCALING, 1f);

        AffineTransform t = new AffineTransform();

        if (xObject instanceof PdfFormXObject && width != imageWidth) {
            horizontalScaling *= width / imageWidth;
            verticalScaling *= height / imageHeight;
        }

        if (horizontalScaling != 1) {
            if (xObject instanceof PdfFormXObject) {
                t.scale((float) horizontalScaling, 1);
            }
            width *= (float) horizontalScaling;
        }
        if (verticalScaling != 1) {
            if (xObject instanceof PdfFormXObject) {
                t.scale(1, (float) verticalScaling);
            }
            height *= (float) verticalScaling;
        }

        if (null != retrieveMinHeight() && height < retrieveMinHeight()) {
            width *= retrieveMinHeight() / height;
            height = retrieveMinHeight();
        } else if (null != retrieveMaxHeight() && height > retrieveMaxHeight()) {
            width *= retrieveMaxHeight() / height;
            height = retrieveMaxHeight();
        } else if (null != retrieveHeight() && height != retrieveHeight()) {
            width *= retrieveHeight() / height;
            height = retrieveHeight();
        }

        imageItselfScaledWidth = (float) width;
        imageItselfScaledHeight = (float) height;

        // See in adjustPositionAfterRotation why angle = 0 is necessary
        if (null == angle) {
            angle = 0f;
        }
        t.rotate((float) angle);
        initialOccupiedAreaBBox = getOccupiedAreaBBox().clone();
        float scaleCoef = adjustPositionAfterRotation((float) angle, layoutBox.getWidth(), layoutBox.getHeight());

        imageItselfScaledHeight *= scaleCoef;
        imageItselfScaledWidth *= scaleCoef;

        initialOccupiedAreaBBox.moveDown(imageItselfScaledHeight);
        initialOccupiedAreaBBox.setHeight(imageItselfScaledHeight);
        initialOccupiedAreaBBox.setWidth(imageItselfScaledWidth);
        if (xObject instanceof PdfFormXObject) {
            t.scale(scaleCoef, scaleCoef);
        }

        getMatrix(t, imageItselfScaledWidth, imageItselfScaledHeight);

        // indicates whether the placement is forced
        boolean isPlacingForced = false;
        if (width > layoutBox.getWidth() || height > layoutBox.getHeight()) {
            if (Boolean.TRUE.equals(getPropertyAsBoolean(Property.FORCED_PLACEMENT))) {
                isPlacingForced = true;
            } else {
                return new LayoutResult(LayoutResult.NOTHING, occupiedArea, null, this, this);
            }
        }

        occupiedArea.getBBox().moveDown((float) height);
        if (borders[3] != null) {
            height += (float) Math.sin(angle) * borders[3].getWidth();
        }
        occupiedArea.getBBox().setHeight((float) height);
        occupiedArea.getBBox().setWidth((float) width);

        float leftMargin = (float) this.getPropertyAsFloat(Property.MARGIN_LEFT);
        float topMargin = (float) this.getPropertyAsFloat(Property.MARGIN_TOP);
        if (leftMargin != 0 || topMargin != 0) {
            translateImage(leftMargin, topMargin, t);
            getMatrix(t, imageItselfScaledWidth, imageItselfScaledHeight);
        }

        applyBorderBox(occupiedArea.getBBox(), borders, true);
        applyMargins(occupiedArea.getBBox(), true);

        return new LayoutResult(LayoutResult.FULL, occupiedArea, null, null,
                isPlacingForced ? this : null);
    }

    @Override
    public void draw(DrawContext drawContext) {
        applyMargins(occupiedArea.getBBox(), false);
        applyBorderBox(occupiedArea.getBBox(), getBorders(), false);

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

        Float angle = this.getPropertyAsFloat(Property.ROTATION_ANGLE);
        if (angle != null) {
            applyConcatMatrix(drawContext, angle);
        }
        super.draw(drawContext);
        if (angle != null) {
            drawContext.getCanvas().restoreState();
        }

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

        PdfCanvas canvas = drawContext.getCanvas();
        if (isTagged) {
            canvas.openTag(tagPointer.getTagReference());
        } else if (isArtifact) {
            canvas.openTag(new CanvasArtifact());
        }

        PdfXObject xObject = ((Image) (getModelElement())).getXObject();
        canvas.addXObject(xObject, matrix[0], matrix[1], matrix[2], matrix[3], (float) fixedXPosition + deltaX, (float) fixedYPosition);
        if (Boolean.TRUE.equals(getPropertyAsBoolean(Property.FLUSH_ON_DRAW))) {
            xObject.flush();
        }

        if (isTagged || isArtifact) {
            canvas.closeTag();
        }

        if (isRelativePosition) {
            applyAbsolutePositioningTranslation(true);
        }
        applyBorderBox(occupiedArea.getBBox(), getBorders(), true);
        applyMargins(occupiedArea.getBBox(), true);

        if (isTagged) {
            tagPointer.moveToParent();
        }
    }

    @Override
    public IRenderer getNextRenderer() {
        return null;
    }

    @Override
    public Rectangle getBorderAreaBBox() {
        applyMargins(initialOccupiedAreaBBox, false);
        applyBorderBox(initialOccupiedAreaBBox, getBorders(), false);

        boolean isRelativePosition = isRelativePosition();
        if (isRelativePosition) {
            applyAbsolutePositioningTranslation(false);
        }
        applyMargins(initialOccupiedAreaBBox, true);
        applyBorderBox(initialOccupiedAreaBBox, true);
        return initialOccupiedAreaBBox;
    }

    protected ImageRenderer autoScale(LayoutArea layoutArea) {
        Rectangle area = layoutArea.getBBox().clone();
        applyMargins(area, false);
        applyBorderBox(area, false);
        // if rotation was applied, width would be equal to the width of rectangle bounding the rotated image
        float angleScaleCoef = imageWidth / (float) width;
        if (width > angleScaleCoef*area.getWidth()) {
            setProperty(Property.HEIGHT, area.getWidth() / width * imageHeight);
            setProperty(Property.WIDTH, UnitValue.createPointValue(angleScaleCoef * area.getWidth()));
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
            Point p01 = t.transform(new Point(0, (float)height), new Point());
            Point p10 = t.transform(new Point((float) width, 0), new Point());
            Point p11 = t.transform(new Point((float) width, (float) height), new Point());

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
        float scaleCoeff = 1;
        if (Boolean.TRUE.equals(getPropertyAsBoolean(Property.AUTO_SCALE))) {
            scaleCoeff = Math.min(maxWidth / (float) width, maxHeight / (float) height);
            height *= scaleCoeff;
            width *= scaleCoeff;
        } else if (null != getPropertyAsBoolean(Property.AUTO_SCALE_WIDTH) && (boolean) getPropertyAsBoolean(Property.AUTO_SCALE_WIDTH)) {
            scaleCoeff = maxWidth / (float) width;
            height *= scaleCoeff;
            width = maxWidth;
        } else if (null != getPropertyAsBoolean(Property.AUTO_SCALE_HEIGHT) && (boolean) getPropertyAsBoolean(Property.AUTO_SCALE_HEIGHT)) {
            scaleCoeff = maxHeight / (float) height;
            height = maxHeight;
            width *= scaleCoeff;
        }
        pivotY *= scaleCoeff;
        deltaX *= scaleCoeff;
        return scaleCoeff;
    }

    private void translateImage(float xDistance, float yDistance, AffineTransform t) {
        t.translate(xDistance, yDistance);
        t.getMatrix(matrix);
        if (fixedXPosition != null) {
            fixedXPosition += (float) t.getTranslateX();
        }
        if (fixedYPosition != null) {
            fixedYPosition += (float) t.getTranslateY();
        }
    }
    private void applyConcatMatrix(DrawContext drawContext, Float angle) {
        drawContext.getCanvas().saveState();
        AffineTransform rotationTransform = AffineTransform.getRotateInstance((float)angle);

        Rectangle rect = getBorderAreaBBox();

        List<Point> rotatedPoints = transformPoints(rectangleToPointsList(rect), rotationTransform);

        float[] shift = calculateShiftToPositionBBoxOfPointsAt(rect.getX(), rect.getY() + rect.getHeight(), rotatedPoints);

        double[] matrix = new double[6];
        rotationTransform.getMatrix(matrix);

        drawContext.getCanvas().concatMatrix(matrix[0], matrix[1], matrix[2], matrix[3], shift[0], shift[1]);
    }
}
