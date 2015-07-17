package com.itextpdf.model.renderer;

import com.itextpdf.basics.geom.AffineTransform;
import com.itextpdf.basics.geom.Point2D;
import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfName;
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
    float imageWidth;
    float imageHeight;

    float[] matrix = new float[6];

    public ImageRenderer(Image image) {
        super(image);
    }

    @Override
    public LayoutResult layout(LayoutContext layoutContext) {
        LayoutArea area = layoutContext.getArea();
        Rectangle layoutBox = area.getBBox();
        occupiedArea = new LayoutArea(area.getPageNumber(), new Rectangle(layoutBox.getX(), layoutBox.getY() + layoutBox.getHeight(), 0, 0));

        width = getPropertyAsFloat(Property.WIDTH);
        Float angle = getPropertyAsFloat(Property.IMAGE_ROTATION_ANGLE);

        PdfXObject xObject = ((Image) (getModelElement())).getXObject();
        if (xObject instanceof PdfImageXObject) {
            imageWidth = ((PdfImageXObject)xObject).getWidth();
            imageHeight = ((PdfImageXObject)xObject).getHeight();
        } else {
            imageWidth = xObject.getPdfObject().getAsArray(PdfName.BBox).getAsFloat(2);
            imageHeight = xObject.getPdfObject().getAsArray(PdfName.BBox).getAsFloat(3);
        }

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

        if (angle != null) {
            t.rotate(angle);
            adjustPositionAfterRotation(angle);
        }

        getMatrix(t, imageItselfScaledWidth, imageItselfScaledHeight);

        if (width > layoutBox.getWidth()){
            return new LayoutResult(LayoutResult.NOTHING, occupiedArea, null, this);
        }
        if (height > layoutBox.getHeight()){
            return new LayoutResult(LayoutResult.NOTHING, occupiedArea, null, this);
        }

        occupiedArea.getBBox().moveDown(height);
        occupiedArea.getBBox().setHeight(height);
        occupiedArea.getBBox().setWidth(width);

        Float mx = getProperty(Property.X_DISTANCE);
        Float my = getProperty(Property.Y_DISTANCE);
        if (mx != null && my != null) {
            translateImage(mx, my, t);
            getMatrix(t, imageItselfScaledWidth, imageItselfScaledHeight);
        }

        if (fixedXPosition != null && fixedYPosition != null) {
            occupiedArea.getBBox().setWidth(0);
            occupiedArea.getBBox().setHeight(0);
        }

        return new LayoutResult(LayoutResult.FULL, occupiedArea, null, null);
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

    @Override
    public void draw(PdfDocument document, PdfCanvas canvas) {
        super.draw(document, canvas);

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

        canvas.addXObject(((Image) (getModelElement())).getXObject(), matrix[0], matrix[1], matrix[2], matrix[3],
                fixedXPosition, fixedYPosition);

        if (position == LayoutPosition.RELATIVE) {
            applyAbsolutePositioningTranslation(true);
        }
    }

    protected ImageRenderer autoScale(LayoutArea area) {
        if (width > area.getBBox().getWidth()) {
            setProperty(Property.HEIGHT, area.getBBox().getWidth() / width * imageHeight);
            setProperty(Property.WIDTH, area.getBBox().getWidth());
        }

        return this;
    }

    private void adjustPositionAfterRotation(float angle) {
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

        pivotY = (float) (p00.getY() - minY);

        height = (float) (maxY - minY);
        width = (float) (maxX - minX);

        if (occupiedArea.getBBox().getX() > minX) {
            occupiedArea.getBBox().moveRight((float) -minX);
            if (fixedXPosition != null) {
                fixedXPosition -= (float)minX;
            }
        }
    }

    private void translateImage(float xDistance, float yDistance, AffineTransform t) {
        t.translate(xDistance, yDistance);
        t.getMatrix(matrix);
        if (fixedXPosition == null) {
            fixedXPosition = occupiedArea.getBBox().getX();
        }
        if (fixedYPosition == null) {
            fixedYPosition = occupiedArea.getBBox().getY() + height;
        }
        fixedXPosition += t.getTranslateX();
        fixedYPosition += t.getTranslateY();
    }
}
