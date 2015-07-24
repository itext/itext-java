package com.itextpdf.model.element;

import com.itextpdf.core.pdf.xobject.PdfFormXObject;
import com.itextpdf.core.pdf.xobject.PdfImageXObject;
import com.itextpdf.core.pdf.xobject.PdfXObject;
import com.itextpdf.model.Property;
import com.itextpdf.model.layout.LayoutPosition;
import com.itextpdf.model.renderer.IRenderer;
import com.itextpdf.model.renderer.ImageRenderer;

public class Image extends AbstractElement<Image> implements ILeafElement<Image>, IAccessibleElement<Image> {

    protected PdfXObject xObject;

    public Image(PdfImageXObject xObject){
        this.xObject = xObject;
    }

    public Image(PdfFormXObject xObject){
        this.xObject = xObject;
    }

    public Image(PdfImageXObject xObject, float width){
        this.xObject = xObject;
        setProperty(Property.WIDTH, width);
    }

    public Image (PdfImageXObject xObject, float x, float y, float width){
        this.xObject = xObject;
        setProperty(Property.X, x).setProperty(Property.Y, y).setProperty(Property.WIDTH, width).setProperty(Property.POSITION, LayoutPosition.FIXED);
    }

    public Image (PdfImageXObject xObject, float x, float y){
        this.xObject = xObject;
        setProperty(Property.X, x).setProperty(Property.Y, y).setProperty(Property.POSITION, LayoutPosition.FIXED);
    }

    public Image (PdfFormXObject xObject, float x, float y){
        this.xObject = xObject;
        setProperty(Property.X, x).setProperty(Property.Y, y).setProperty(Property.POSITION, LayoutPosition.FIXED);
    }

    @Override
    public IRenderer makeRenderer() {
        if (nextRenderer != null) {
            IRenderer renderer = nextRenderer;
            nextRenderer = null;
            return renderer;
        }
        return new ImageRenderer(this);
    }

    public PdfXObject getXObject() {
        return xObject;
    }

    public Image setRotationAngle(double angle){
        return setProperty(Property.IMAGE_ROTATION_ANGLE, angle);
    }

    public Image setTranslationDistance(float xDistance, float yDistance){
        return setProperty(Property.X_DISTANCE, xDistance).setProperty(Property.Y_DISTANCE, yDistance);
    }

    public Image scale(float horizontalScaling, float verticalScaling){
        return setProperty(Property.HORIZONTAL_SCALING, horizontalScaling).setProperty(Property.VERTICAL_SCALING, verticalScaling);
    }

    public Image scaleToFit(float fitWidth, float fitHeight) {
        float horizontalScaling = fitWidth / xObject.getWidth();
        float verticalScaling = fitHeight / xObject.getHeight();
        return scale(Math.min(horizontalScaling, verticalScaling), Math.min(horizontalScaling, verticalScaling));
    }

    public Image setHorizontalAlignment(Property.HorizontalAlignment horizontalAlignment) {
        return setProperty(Property.HORIZONTAL_ALIGNMENT, horizontalAlignment);
    }

    public Image setAutoScaling(boolean autoScale){
        return setProperty(Property.AUTO_SCALE, autoScale);
    }

    public Image setFixedPosition(float x, float y) {
        return setFixedPosition(x, y, getWidth());
    }

    public Image setFixedPosition(int pageNumber, float x, float y) {
        return setFixedPosition(pageNumber, x, y, getWidth());
    }

    @Override
    public <T> T getDefaultProperty(int propertyKey) {
        switch (propertyKey) {
            case Property.AUTO_SCALE:
                return (T) Boolean.valueOf(false);
            case Property.HORIZONTAL_SCALING:
            case Property.VERTICAL_SCALING:
                return (T) Float.valueOf(1);
            default:
                return super.getDefaultProperty(propertyKey);
        }
    }

    /**
     * Gets width of the image. If a user didn't set Width property before it returns width of image or form XObject
     * @return
     */
    @Override
    public Float getWidth() {
        Float width = super.getWidth();
        if (width == null) {
            width = xObject.getWidth();
        }

        return width;
    }

    /**
     * Gets height of the image. If a user didn't set Height property before it returns height of image or form XObject
     * @return
     */
    @Override
    public Float getHeight() {
        Float height = super.getHeight();
        if (height == null) {
            height = xObject.getHeight();
        }

        return height;
    }
}
