package com.itextpdf.layout.element;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.canvas.wmf.WmfImage;
import com.itextpdf.kernel.pdf.tagutils.AccessibilityProperties;
import com.itextpdf.kernel.pdf.tagutils.IAccessibleElement;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.kernel.pdf.xobject.PdfXObject;
import com.itextpdf.layout.Property;
import com.itextpdf.layout.layout.LayoutPosition;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.renderer.ImageRenderer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A layout element that represents an image for inclusion in the document model.
 */
public class Image extends AbstractElement<Image> implements ILeafElement<Image>, IElement<Image>, IAccessibleElement {

    protected PdfXObject xObject;
    protected PdfName role = PdfName.Figure;
    protected AccessibilityProperties tagProperties;

    /**
     * Creates an {@link Image} from an image XObject, the representation of an
     * image in PDF syntax.
     * @param xObject an internal {@link PdfImageXObject}
     */
    public Image(PdfImageXObject xObject) {
        this.xObject = xObject;
    }

    /**
     * Creates an {@link Image} from a form XObject, the representation of a
     * form in PDF syntax.
     * @param xObject an internal {@link PdfFormXObject}
     */
    public Image(PdfFormXObject xObject) {
        this.xObject = xObject;
    }

    /**
     * Creates an {@link Image} from an image XObject, the representation of an
     * image in PDF syntax, with a custom width.
     * @param xObject an internal {@link PdfImageXObject}
     * @param width a float value
     */
    public Image(PdfImageXObject xObject, float width) {
        this.xObject = xObject;
        setWidth(width);
    }

    /**
     * Creates an {@link Image} from an image XObject, the representation of an
     * image in PDF syntax, with a custom width and on a fixed position.
     * @param xObject an internal {@link PdfImageXObject}
     * @param x a float value representing the horizontal offset of the lower left corner of the image
     * @param y a float value representing the vertical offset of the lower left corner of the image
     * @param width a float value
     */
    public Image(PdfImageXObject xObject, float x, float y, float width) {
        this.xObject = xObject;
        setProperty(Property.X, x).setProperty(Property.Y, y).setWidth(width).setProperty(Property.POSITION, LayoutPosition.FIXED);
    }

    /**
     * Creates an {@link Image} from an image XObject, the representation of an
     * image in PDF syntax, on a fixed position.
     * @param xObject an internal {@link PdfImageXObject}
     * @param x a float value representing the horizontal offset of the lower left corner of the image
     * @param y a float value representing the vertical offset of the lower left corner of the image
     */
    public Image(PdfImageXObject xObject, float x, float y) {
        this.xObject = xObject;
        setProperty(Property.X, x).setProperty(Property.Y, y).setProperty(Property.POSITION, LayoutPosition.FIXED);
    }

    /**
     * Creates an {@link Image} from a form XObject, the representation of a
     * form in PDF syntax.
     * @param xObject an internal {@link PdfFormXObject}
     * @param x a float value representing the horizontal offset of the lower left corner of the form
     * @param y a float value representing the vertical offset of the lower left corner of the form
     */
    public Image(PdfFormXObject xObject, float x, float y) {
        this.xObject = xObject;
        setProperty(Property.X, x).setProperty(Property.Y, y).setProperty(Property.POSITION, LayoutPosition.FIXED);
    }

    /**
     * Creates an {@link Image} from an image resource, read in from a file
     * with the iText I/O module.
     * @param img an internal representation of the {@link com.itextpdf.io.image.Image image resource}
     */
    public Image(com.itextpdf.io.image.Image img) {
        this(new PdfImageXObject(checkImageType(img)));
        setProperty(Property.FLUSH_ON_DRAW, true);
    }

    /**
     * Creates an {@link Image} from an image resource, read in from a file
     * with the iText I/O module, on a fixed position.
     * @param img an internal representation of the {@link com.itextpdf.io.image.Image image resource}
     * @param x a float value representing the horizontal offset of the lower left corner of the image
     * @param y a float value representing the vertical offset of the lower left corner of the image
     */
    public Image(com.itextpdf.io.image.Image img, float x, float y) {
        this(new PdfImageXObject(checkImageType(img)), x, y);
    }

    /**
     * Creates an {@link Image} from an image resource, read in from a file
     * with the iText I/O module, with a custom width and on a fixed position.
     * @param img an internal representation of the {@link com.itextpdf.io.image.Image image resource}
     * @param x a float value representing the horizontal offset of the lower left corner of the image
     * @param y a float value representing the vertical offset of the lower left corner of the image
     * @param width a float value
     */
    public Image(com.itextpdf.io.image.Image img, float x, float y, float width) {
        this(new PdfImageXObject(checkImageType(img)), x, y, width);
    }

    /**
     * Gets the XObject contained in this image object
     * @return a {@link PdfXObject}
     */
    public PdfXObject getXObject() {
        return xObject;
    }

    /**
     * Sets the rotation angle.
     * @param angle a value in radians
     * @return this element
     */
    public Image setRotationAngle(double angle) {
        return setProperty(Property.ROTATION_ANGLE, angle);
    }

    /**
     * Gets the current left margin width of the element.
     * @return the left margin width, as a <code>float</code>
     */
    public Float getMarginLeft() {
        return getProperty(Property.MARGIN_LEFT);
    }

    /**
     * Sets the left margin width of the element.
     * @param value the new left margin width
     * @return this element
     */
    public Image setMarginLeft(float value) {
        return setProperty(Property.MARGIN_LEFT, value);
    }

    /**
     * Gets the current right margin width of the element.
     * @return the right margin width, as a <code>float</code>
     */
    public Float getMarginRight() {
        return getProperty(Property.MARGIN_RIGHT);
    }

    /**
     * Sets the right margin width of the element.
     * @param value the new right margin width
     * @return this element
     */
    public Image setMarginRight(float value) {
        return setProperty(Property.MARGIN_RIGHT, value);
    }

    /**
     * Gets the current top margin width of the element.
     * @return the top margin width, as a <code>float</code>
     */
    public Float getMarginTop() {
        return getProperty(Property.MARGIN_TOP);
    }
    
    /**
     * Sets the top margin width of the element.
     * @param value the new top margin width
     * @return this element
     */
    public Image setMarginTop(float value) {
        return setProperty(Property.MARGIN_TOP, value);
    }

    /**
     * Gets the current bottom margin width of the element.
     * @return the bottom margin width, as a <code>float</code>
     */
    public Float getMarginBottom() {
        return getProperty(Property.MARGIN_BOTTOM);
    }

    /**
     * Sets the bottom margin width of the element.
     * @param value the new bottom margin width
     * @return this element
     */
    public Image setMarginBottom(float value) {
        return setProperty(Property.MARGIN_BOTTOM, value);
    }

    /**
     * Sets the margins around the element to a series of new widths.
     * 
     * @param marginTop the new margin top width
     * @param marginRight the new margin right width
     * @param marginBottom the new margin bottom width
     * @param marginLeft the new margin left width
     * @return this element
     */
    public Image setMargins(float marginTop, float marginRight, float marginBottom, float marginLeft) {
        return setMarginTop(marginTop).setMarginRight(marginRight).setMarginBottom(marginBottom).setMarginLeft(marginLeft);
    }

    /**
     * Scale the image relative to its default size.
     * 
     * @param horizontalScaling the horizontal scaling coefficient. default value 1 = 100%
     * @param verticalScaling the vertical scaling coefficient. default value 1 = 100%
     * @return this element
     */
    public Image scale(float horizontalScaling, float verticalScaling) {
        return setProperty(Property.HORIZONTAL_SCALING, horizontalScaling).setProperty(Property.VERTICAL_SCALING, verticalScaling);
    }

    /**
     * Scale the image to an absolute size. This method will preserve the
     * width-height ratio of the image.
     * 
     * @param fitWidth the new maximum width of the image
     * @param fitHeight the new maximum height of the image
     * @return this element
     */
    public Image scaleToFit(float fitWidth, float fitHeight) {
        float horizontalScaling = fitWidth / xObject.getWidth();
        float verticalScaling = fitHeight / xObject.getHeight();
        return scale(Math.min(horizontalScaling, verticalScaling), Math.min(horizontalScaling, verticalScaling));
    }

    /**
     * Scale the image to an absolute size. This method will <em>not</em>
     * preserve the width-height ratio of the image.
     * 
     * @param fitWidth the new absolute width of the image
     * @param fitHeight the new absolute height of the image
     * @return this element
     */
    public Image scaleAbsolute(float fitWidth, float fitHeight) {
        float horizontalScaling = fitWidth / xObject.getWidth();
        float verticalScaling = fitHeight / xObject.getHeight();
        return scale(horizontalScaling, verticalScaling);
    }

    /**
     * Sets the autoscale property for both width and height.
     * 
     * @param autoScale whether or not to let the image resize automatically
     * @return this image
     */
    public Image setAutoScale(boolean autoScale) {
        if (hasProperty(Property.AUTO_SCALE_WIDTH) && hasProperty(Property.AUTO_SCALE_HEIGHT) && autoScale &&
                ((Boolean) getProperty(Property.AUTO_SCALE_WIDTH) ||
                        (Boolean) getProperty(Property.AUTO_SCALE_HEIGHT))) {
            Logger logger = LoggerFactory.getLogger(Image.class);
            logger.warn(LogMessageConstant.IMAGE_HAS_AMBIGUOUS_SCALE);
        }
        return setProperty(Property.AUTO_SCALE, autoScale);
    }

    /**
     * Sets the autoscale property for the height of the image.
     * 
     * @param autoScale whether or not to let the image height resize automatically
     * @return this image
     */
    public Image setAutoScaleHeight(boolean autoScale) {
        if (hasProperty(Property.AUTO_SCALE_WIDTH) && autoScale && (Boolean) getProperty(Property.AUTO_SCALE_WIDTH)) {
            setProperty(Property.AUTO_SCALE_WIDTH, false);
            setProperty(Property.AUTO_SCALE_HEIGHT, false);
            return setProperty(Property.AUTO_SCALE, true);
        } else {
            return setProperty(Property.AUTO_SCALE_WIDTH, autoScale);
        }
    }

    /**
     *  Sets the autoscale property for the width of the image.
     * 
     * @param autoScale whether or not to let the image width resize automatically
     * @return this image
     */
    public Image setAutoScaleWidth(boolean autoScale) {
        if (hasProperty(Property.AUTO_SCALE_HEIGHT) && autoScale && (Boolean) getProperty(Property.AUTO_SCALE_HEIGHT)) {
            setProperty(Property.AUTO_SCALE_WIDTH, false);
            setProperty(Property.AUTO_SCALE_HEIGHT, false);
            return setProperty(Property.AUTO_SCALE, true);
        } else {
            return setProperty(Property.AUTO_SCALE_WIDTH, autoScale);
        }
    }

    /**
     * Sets values for a absolute repositioning of the Element. Also has as a
     * side effect that the Element's {@link Property#POSITION} is changed to 
     * {@link LayoutPosition#FIXED fixed}.
     * 
     * @param x horizontal position on the page
     * @param y vertical position on the page
     * @return this image.
     */
    public Image setFixedPosition(float x, float y) {
        return setFixedPosition(x, y, getWidth());
    }

    /**
     * Sets values for a absolute repositioning of the Element, on a specific
     * page. Also has as a side effect that the Element's {@link
     * Property#POSITION} is changed to {@link LayoutPosition#FIXED fixed}.
     * 
     * @param pageNumber the page where the element must be positioned
     * @param x horizontal position on the page
     * @param y vertical position on the page
     * @return this Element.
     */
    public Image setFixedPosition(int pageNumber, float x, float y) {
        return setFixedPosition(pageNumber, x, y, getWidth());
    }

    @Override
    public <T> T getDefaultProperty(Property property) {
        switch (property) {
            case AUTO_SCALE:
                return (T) Boolean.valueOf(false);
            case HORIZONTAL_SCALING:
            case VERTICAL_SCALING:
                return (T) Float.valueOf(1);
            default:
                return super.getDefaultProperty(property);
        }
    }

    /**
     * Gets width of the image. It returns width of image or form XObject,
     * not the width set by one of the #setWidth methods
     * @return the original width of the image
     */
    public float getImageWidth() {
        return xObject.getWidth();
    }

    /**
     * Gets height of the image. It returns height of image or form XObject,
     * not the height set by one of the #setHeight methods
     * @return the original height of the image
     */
    public float getImageHeight() {
        return xObject.getHeight();
    }

    /**
     * Gets scaled width of the image.
     * @return the current scaled width
     */
    public float getImageScaledWidth() {
        return null == getProperty(Property.HORIZONTAL_SCALING) ?
                xObject.getWidth() :
                xObject.getWidth() * (Float) getProperty(Property.HORIZONTAL_SCALING);
    }

    /**
     * Gets scaled height of the image.
     * @return the current scaled height
     */
    public float getImageScaledHeight() {
        return null == getProperty(Property.VERTICAL_SCALING) ?
                xObject.getHeight() :
                xObject.getHeight() * (Float) getProperty(Property.VERTICAL_SCALING);
    }

    @Override
    public PdfName getRole() {
        return role;
    }

    @Override
    public void setRole(PdfName role) {
        this.role = role;
    }

    @Override
    public AccessibilityProperties getAccessibilityProperties() {
        if (tagProperties == null) {
            tagProperties = new AccessibilityProperties();
        }
        return tagProperties;
    }

    @Override
    protected IRenderer makeNewRenderer() {
        return new ImageRenderer(this);
    }

    private static com.itextpdf.io.image.Image checkImageType(com.itextpdf.io.image.Image image) {
        if (image instanceof WmfImage) {
            throw new PdfException(PdfException.CannotCreateLayoutImageByWmfImage);
        }
        return image;
    }
}
