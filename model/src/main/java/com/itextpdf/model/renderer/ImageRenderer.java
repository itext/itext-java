package com.itextpdf.model.renderer;

import com.itextpdf.basics.geom.AffineTransform;
import com.itextpdf.basics.geom.Point2D;
import com.itextpdf.basics.geom.Rectangle;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.canvas.CanvasArtifact;
import com.itextpdf.core.pdf.canvas.PdfCanvas;
import com.itextpdf.core.pdf.tagutils.IAccessibleElement;
import com.itextpdf.core.pdf.tagutils.PdfTagStructure;
import com.itextpdf.core.pdf.xobject.PdfFormXObject;
import com.itextpdf.core.pdf.xobject.PdfImageXObject;
import com.itextpdf.core.pdf.xobject.PdfXObject;
import com.itextpdf.model.Property;
import com.itextpdf.model.element.Image;
import com.itextpdf.model.layout.LayoutArea;
import com.itextpdf.model.layout.LayoutContext;
import com.itextpdf.model.layout.LayoutPosition;
import com.itextpdf.model.layout.LayoutResult;

public class ImageRenderer extends AbstractRenderer {

    float height;
    Float width;
    Float fixedXPosition;
    Float fixedYPosition;
    float pivotY;
    float deltaX;
    float imageWidth;
    float imageHeight;

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

        if (!getPropertyAsBoolean(Property.FORCED_PLACEMENT) && (width > layoutBox.getWidth() || height > layoutBox.getHeight())) {
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
        PdfTagStructure tagStructure = null;
        if (isTagged) {
            tagStructure = document.getTagStructure();
            IAccessibleElement accessibleElement = (IAccessibleElement) getModelElement();
            PdfName role = accessibleElement.getRole();
            if (role != null && !PdfName.Artifact.equals(role)) {
                AccessibleAttributesApplier.applyLayoutAttributes(accessibleElement.getRole(), this, document);
                tagStructure.addTag(accessibleElement);
            } else {
                isTagged = false;
                if (PdfName.Artifact.equals(role)) {
                    isArtifact = true;
                }
            }
        }

        applyMargins(occupiedArea.getBBox(), false);

        int position = getPropertyAsInteger(Property.POSITION);
        if (position == LayoutPosition.RELATIVE) {
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
            canvas.openTag(tagStructure.getTagReference());
        } else if (isArtifact) {
            canvas.openTag(new CanvasArtifact());
        }

        canvas.addXObject(((Image) (getModelElement())).getXObject(), matrix[0], matrix[1], matrix[2], matrix[3],
                fixedXPosition + deltaX, fixedYPosition);

        if (isTagged || isArtifact) {
            canvas.closeTag();
        }

        if (position == LayoutPosition.RELATIVE) {
            applyAbsolutePositioningTranslation(true);
        }

        applyMargins(occupiedArea.getBBox(), true);

        if (isTagged) {
            document.getTagStructure().moveToParent();
        }
    }

    @Override
    public ImageRenderer getNextRenderer() {
        return null;
    }

    protected ImageRenderer autoScale(LayoutArea area) {
        if (width > area.getBBox().getWidth()) {
            setProperty(Property.HEIGHT, area.getBBox().getWidth() / width * imageHeight);
            setProperty(Property.WIDTH, Property.UnitValue.createPointValue(area.getBBox().getWidth()));
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
            Point2D p00 = t.transform(new Point2D.Float(0, 0), new Point2D.Float());
            Point2D p01 = t.transform(new Point2D.Float(0, height), new Point2D.Float());
            Point2D p10 = t.transform(new Point2D.Float(width, 0), new Point2D.Float());
            Point2D p11 = t.transform(new Point2D.Float(width, height), new Point2D.Float());

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
            fixedXPosition += t.getTranslateX();
        }
        if (fixedYPosition != null) {
            fixedYPosition += t.getTranslateY();
        }
    }
}
