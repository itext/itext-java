package com.itextpdf.model.renderer;

import com.itextpdf.basics.geom.AffineTransform;
import com.itextpdf.basics.geom.Point2D;
import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.core.pdf.PdfDocument;
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

    float[] matrix = new float[6];

    public ImageRenderer(Image image){
        super(image);
    }

    @Override
    public LayoutResult layout(LayoutContext layoutContext) {

        LayoutArea area = layoutContext.getArea();
        Rectangle layoutBox = area.getBBox();
        occupiedArea = new LayoutArea(area.getPageNumber(), new Rectangle(layoutBox.getX(), layoutBox.getY() + layoutBox.getHeight(), 0, 0));

        width = getPropertyAsFloat(Property.WIDTH);
        Float angle = getPropertyAsFloat(Property.ANGLE);

        width = width == null ? ((Image) (getModelElement())).getXObject().getWidth() : width;
        height = width / ((Image) (getModelElement())).getXObject().getWidth() * ((Image) (getModelElement())).getXObject().getHeight();

        Float horizontalScaling = getPropertyAsFloat(Property.HORIZONTAL_SCALING);
        Float verticalScaling = getPropertyAsFloat(Property.VERTICAL_SCALING);

        AffineTransform t = new AffineTransform(setTransformationMatrix(occupiedArea.getBBox().getX(), occupiedArea.getBBox().getY(), width, height));
        if (horizontalScaling != null)
            scale(horizontalScaling, verticalScaling, t);
        t.getMatrix(matrix);

        if (angle != null){
            rotateImage(angle, t);
        } else{
            occupiedArea.getBBox().moveDown(height);
            occupiedArea.getBBox().setHeight(height);
            occupiedArea.getBBox().setWidth(width);
        }


        Float mx = getProperty(Property.X_DISTANCE);
        Float my = getProperty(Property.Y_DISTANCE);

        fixedXPosition = getPropertyAsFloat(Property.X);
        fixedYPosition = getPropertyAsFloat(Property.Y);

        if (my != null && my != null)
            translateImage(mx, my, t);

        if (fixedXPosition != null && fixedYPosition != null){
            occupiedArea.getBBox().setWidth(0);
            occupiedArea.getBBox().setHeight(0);
        }

        return new LayoutResult(LayoutResult.FULL, occupiedArea, null, null);
    }

    @Override
    public void draw(PdfDocument document, PdfCanvas canvas){
        super.draw(document, canvas);

        int position = getPropertyAsInteger(Property.POSITION);
        if (position == LayoutPosition.RELATIVE) {
            applyAbsolutePositioningTranslation(false);
        }

        if (fixedXPosition != null || fixedYPosition != null){
            canvas.addXObject(((Image)(getModelElement())).getXObject(), matrix[0], matrix[2], matrix[1], matrix[3], fixedXPosition, fixedYPosition);
        } else {
            canvas.addXObject(((Image)(getModelElement())).getXObject(), matrix[0], matrix[2], matrix[1], matrix[3], occupiedArea.getBBox().getX(), occupiedArea.getBBox().getY() + pivotY);
        }

        if (position == LayoutPosition.RELATIVE) {
            applyAbsolutePositioningTranslation(true);
        }
    }

    private float[] setTransformationMatrix(float x, float y, float width, float height){
        float[] matrix = new float[6];

        matrix[0] = width;
        matrix[1] = 0;
        matrix[2] = 0;
        matrix[3] = height;
        matrix[4] = x;
        matrix[5] = y;

        return matrix;
    }

    private void rotateImage(float angle, AffineTransform t){

        if (angle != 0){
            t.rotate(angle);

            Point2D p00 = t.transform(new Point2D.Float(0, 0), new Point2D.Float());
            Point2D p01 = t.transform(new Point2D.Float(0, 1), new Point2D.Float());
            Point2D p10 = t.transform(new Point2D.Float(1, 0), new Point2D.Float());
            Point2D p11 = t.transform(new Point2D.Float(1, 1), new Point2D.Float());

            double[] xValues = {p01.getX(), p10.getX(), p11.getX()};
            double[] yValues = {p01.getY(), p10.getY(), p11.getY()};

            double minX = p00.getX();
            double minY = p00.getY();
            double maxX = minX;
            double maxY = minY;

            for (double x : xValues){
                minX = Math.min(minX, x);
                maxX = Math.max(maxX, x);
            }
            for (double y : yValues){
                minY = Math.min(minY, y);
                maxY = Math.max(maxY, y);
            }

            t.getMatrix(matrix);

            pivotY = (float) (p00.getY() - minY);

            occupiedArea.getBBox().moveDown((float) (maxY - minY));
            occupiedArea.getBBox().setHeight((float) (maxY - minY));
            occupiedArea.getBBox().setWidth((float) (maxX - minX));

            if (occupiedArea.getBBox().getX() > minX)
                occupiedArea.getBBox().moveRight((float) (occupiedArea.getBBox().getX() - minX));
        }
        t.getMatrix(matrix);
    }

    private void translateImage(float xDistance, float yDistance, AffineTransform t){

        float mx = xDistance/width;
        float my = yDistance/height;
        t.translate(mx, my);
        t.getMatrix(matrix);
        fixedXPosition = t.getTranslateX();
        fixedYPosition = t.getTranslateY();
    }

    private void scale(float horizontalScaling, float verticalScaling, AffineTransform t){
        t.scale(horizontalScaling, verticalScaling);

        width = t.getScaleX();
        height = t.getScaleY();
    }
}
