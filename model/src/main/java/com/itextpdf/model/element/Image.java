package com.itextpdf.model.element;

import com.itextpdf.core.pdf.xobject.PdfImageXObject;
import com.itextpdf.model.Property;
import com.itextpdf.model.renderer.IRenderer;
import com.itextpdf.model.renderer.ImageRenderer;

public class Image extends AbstractElement<Image> implements ILeafElement, IAccessibleElement {

    protected PdfImageXObject xObject;

    public Image(PdfImageXObject xObject){
        this.xObject = xObject;
    }

    public Image(PdfImageXObject xObject, float width){
        this.xObject = xObject;
        setProperty(Property.WIDTH, width);
    }

    public Image (PdfImageXObject xObject, float x, float y, float width){
        this.xObject = xObject;
        setProperty(Property.X, x).setProperty(Property.Y, y).setProperty(Property.WIDTH, width);
    }

    public Image (PdfImageXObject xObject, float x, float y){
        this.xObject = xObject;
        setProperty(Property.X, x).setProperty(Property.Y, y);
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

    public PdfImageXObject getxObject() {
        return xObject;
    }

    public void setxObject(PdfImageXObject xObject) {
        this.xObject = xObject;
    }

    public Image setRotateAngle(double angle){
        return setProperty(Property.ANGLE, angle);
    }

    public Image setTranslationDistance(float xDistance, float yDistance){
        return setProperty(Property.X_DISTANCE, xDistance).setProperty(Property.Y_DISTANCE, yDistance);
    }

    public Image scale(float horizontalScaling, float verticalScaling){
        return setProperty(Property.HORIZONTAL_SCALING, horizontalScaling).setProperty(Property.VERTICAL_SCALING, verticalScaling);
    }
}
