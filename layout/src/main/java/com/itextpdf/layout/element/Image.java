/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2017 iText Group NV
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
package com.itextpdf.layout.element;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.canvas.wmf.WmfImageData;
import com.itextpdf.kernel.pdf.tagutils.AccessibilityProperties;
import com.itextpdf.kernel.pdf.tagutils.IAccessibleElement;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.kernel.pdf.xobject.PdfXObject;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.layout.LayoutPosition;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.renderer.ImageRenderer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A layout element that represents an image for inclusion in the document model.
 */
public class Image extends AbstractElement<Image> implements ILeafElement, IAccessibleElement {

    protected PdfXObject xObject;
    protected PdfName role = PdfName.Figure;
    protected AccessibilityProperties tagProperties;

    /**
     * Creates an {@link Image} from an image XObject, the representation of an
     * image in PDF syntax.
     *
     * @param xObject an internal {@link PdfImageXObject}
     */
    public Image(PdfImageXObject xObject) {
        this.xObject = xObject;
    }

    /**
     * Creates an {@link Image} from a form XObject, the representation of a
     * form in PDF syntax.
     *
     * @param xObject an internal {@link PdfFormXObject}
     */
    public Image(PdfFormXObject xObject) {
        this.xObject = xObject;
    }

    /**
     * Creates an {@link Image} from an image XObject, the representation of an
     * image in PDF syntax, with a custom width.
     *
     * @param xObject an internal {@link PdfImageXObject}
     * @param width   a float value
     */
    public Image(PdfImageXObject xObject, float width) {
        this.xObject = xObject;
        setWidth(width);
    }

    /**
     * Creates an {@link Image} from an image XObject, the representation of an
     * image in PDF syntax, with a custom width and on a fixed position.
     *
     * @param xObject an internal {@link PdfImageXObject}
     * @param left    a float value representing the horizontal offset of the lower left corner of the image
     * @param bottom  a float value representing the vertical offset of the lower left corner of the image
     * @param width   a float value
     */
    public Image(PdfImageXObject xObject, float left, float bottom, float width) {
        this.xObject = xObject;
        setProperty(Property.LEFT, left);
        setProperty(Property.BOTTOM, bottom);
        setWidth(width);
        setProperty(Property.POSITION, LayoutPosition.FIXED);
    }

    /**
     * Creates an {@link Image} from an image XObject, the representation of an
     * image in PDF syntax, on a fixed position.
     *
     * @param xObject an internal {@link PdfImageXObject}
     * @param left    a float value representing the horizontal offset of the lower left corner of the image
     * @param bottom  a float value representing the vertical offset of the lower left corner of the image
     */
    public Image(PdfImageXObject xObject, float left, float bottom) {
        this.xObject = xObject;
        setProperty(Property.LEFT, left);
        setProperty(Property.BOTTOM, bottom);
        setProperty(Property.POSITION, LayoutPosition.FIXED);
    }

    /**
     * Creates an {@link Image} from a form XObject, the representation of a
     * form in PDF syntax.
     *
     * @param xObject an internal {@link PdfFormXObject}
     * @param left    a float value representing the horizontal offset of the lower left corner of the form
     * @param bottom  a float value representing the vertical offset of the lower left corner of the form
     */
    public Image(PdfFormXObject xObject, float left, float bottom) {
        this.xObject = xObject;
        setProperty(Property.LEFT, left);
        setProperty(Property.BOTTOM, bottom);
        setProperty(Property.POSITION, LayoutPosition.FIXED);
    }

    /**
     * Creates an {@link Image} from an image resource, read in from a file
     * with the iText I/O module.
     *
     * @param img an internal representation of the {@link com.itextpdf.io.image.ImageData image resource}
     */
    public Image(ImageData img) {
        this(new PdfImageXObject(checkImageType(img)));
        setProperty(Property.FLUSH_ON_DRAW, true);
    }

    /**
     * Creates an {@link Image} from an image resource, read in from a file
     * with the iText I/O module, on a fixed position.
     *
     * @param img    an internal representation of the {@link com.itextpdf.io.image.ImageData image resource}
     * @param left   a float value representing the horizontal offset of the lower left corner of the image
     * @param bottom a float value representing the vertical offset of the lower left corner of the image
     */
    public Image(ImageData img, float left, float bottom) {
        this(new PdfImageXObject(checkImageType(img)), left, bottom);
        setProperty(Property.FLUSH_ON_DRAW, true);
    }

    /**
     * Creates an {@link Image} from an image resource, read in from a file
     * with the iText I/O module, with a custom width and on a fixed position.
     *
     * @param img    an internal representation of the {@link com.itextpdf.io.image.ImageData image resource}
     * @param left   a float value representing the horizontal offset of the lower left corner of the image
     * @param bottom a float value representing the vertical offset of the lower left corner of the image
     * @param width  a float value
     */
    public Image(ImageData img, float left, float bottom, float width) {
        this(new PdfImageXObject(checkImageType(img)), left, bottom, width);
        setProperty(Property.FLUSH_ON_DRAW, true);
    }

    /**
     * Gets the XObject contained in this image object
     *
     * @return a {@link PdfXObject}
     */
    public PdfXObject getXObject() {
        return xObject;
    }

    /**
     * Sets the rotation radAngle.
     *
     * @param radAngle a value in radians
     * @return this element
     */
    public Image setRotationAngle(double radAngle) {
        setProperty(Property.ROTATION_ANGLE, radAngle);
        return this;
    }

    /**
     * Gets the current left margin width of the element.
     *
     * @return the left margin width, as a <code>float</code>
     */
    public Float getMarginLeft() {
        return this.<Float>getProperty(Property.MARGIN_LEFT);
    }

    /**
     * Sets the left margin width of the element.
     *
     * @param value the new left margin width
     * @return this element
     */
    public Image setMarginLeft(float value) {
        setProperty(Property.MARGIN_LEFT, value);
        return this;
    }

    /**
     * Gets the current right margin width of the element.
     *
     * @return the right margin width, as a <code>float</code>
     */
    public Float getMarginRight() {
        return this.<Float>getProperty(Property.MARGIN_RIGHT);
    }

    /**
     * Sets the right margin width of the element.
     *
     * @param value the new right margin width
     * @return this element
     */
    public Image setMarginRight(float value) {
        setProperty(Property.MARGIN_RIGHT, value);
        return this;
    }

    /**
     * Gets the current top margin width of the element.
     *
     * @return the top margin width, as a <code>float</code>
     */
    public Float getMarginTop() {
        return this.<Float>getProperty(Property.MARGIN_TOP);
    }

    /**
     * Sets the top margin width of the element.
     *
     * @param value the new top margin width
     * @return this element
     */
    public Image setMarginTop(float value) {
        setProperty(Property.MARGIN_TOP, value);
        return this;
    }

    /**
     * Gets the current bottom margin width of the element.
     *
     * @return the bottom margin width, as a <code>float</code>
     */
    public Float getMarginBottom() {
        return this.<Float>getProperty(Property.MARGIN_BOTTOM);
    }

    /**
     * Sets the bottom margin width of the element.
     *
     * @param value the new bottom margin width
     * @return this element
     */
    public Image setMarginBottom(float value) {
        setProperty(Property.MARGIN_BOTTOM, value);
        return this;
    }

    /**
     * Sets the margins around the element to a series of new widths.
     *
     * @param marginTop    the new margin top width
     * @param marginRight  the new margin right width
     * @param marginBottom the new margin bottom width
     * @param marginLeft   the new margin left width
     * @return this element
     */
    public Image setMargins(float marginTop, float marginRight, float marginBottom, float marginLeft) {
        return setMarginTop(marginTop).setMarginRight(marginRight).setMarginBottom(marginBottom).setMarginLeft(marginLeft);
    }

    /**
     * Scale the image relative to its default size.
     *
     * @param horizontalScaling the horizontal scaling coefficient. default value 1 = 100%
     * @param verticalScaling   the vertical scaling coefficient. default value 1 = 100%
     * @return this element
     */
    public Image scale(float horizontalScaling, float verticalScaling) {
        setProperty(Property.HORIZONTAL_SCALING, horizontalScaling);
        setProperty(Property.VERTICAL_SCALING, verticalScaling);
        return this;
    }

    /**
     * Scale the image to an absolute size. This method will preserve the
     * width-height ratio of the image.
     *
     * @param fitWidth  the new maximum width of the image
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
     * @param fitWidth  the new absolute width of the image
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
                ((boolean) this.<Boolean>getProperty(Property.AUTO_SCALE_WIDTH) ||
                        (boolean) this.<Boolean>getProperty(Property.AUTO_SCALE_HEIGHT))) {
            Logger logger = LoggerFactory.getLogger(Image.class);
            logger.warn(LogMessageConstant.IMAGE_HAS_AMBIGUOUS_SCALE);
        }
        setProperty(Property.AUTO_SCALE, autoScale);
        return this;
    }

    //TODO(DEVSIX-1658):Remove bugged mention
    /**
     * Sets the autoscale property for the height of the image.
     * Is currently bugged and will not work as expected.
     * @param autoScale whether or not to let the image height resize automatically
     * @return this image
     */
    public Image setAutoScaleHeight(boolean autoScale) {
        if (hasProperty(Property.AUTO_SCALE_WIDTH) && autoScale && (boolean) this.<Boolean>getProperty(Property.AUTO_SCALE_WIDTH)) {
            setProperty(Property.AUTO_SCALE_WIDTH, false);
            setProperty(Property.AUTO_SCALE_HEIGHT, false);
            setProperty(Property.AUTO_SCALE, true);
        } else {
            setProperty(Property.AUTO_SCALE_WIDTH, autoScale);
        }
        return this;
    }

    /**
     * Sets the autoscale property for the width of the image.
     *
     * @param autoScale whether or not to let the image width resize automatically
     * @return this image
     */
    public Image setAutoScaleWidth(boolean autoScale) {
        if (hasProperty(Property.AUTO_SCALE_HEIGHT) && autoScale && (boolean) this.<Boolean>getProperty(Property.AUTO_SCALE_HEIGHT)) {
            setProperty(Property.AUTO_SCALE_WIDTH, false);
            setProperty(Property.AUTO_SCALE_HEIGHT, false);
            setProperty(Property.AUTO_SCALE, true);
        } else {
            setProperty(Property.AUTO_SCALE_WIDTH, autoScale);
        }
        return this;
    }

    /**
     * Sets values for a absolute repositioning of the Element. Also has as a
     * side effect that the Element's {@link Property#POSITION} is changed to
     * {@link LayoutPosition#FIXED fixed}.
     *
     * @param left   horizontal position on the page
     * @param bottom vertical position on the page
     * @return this image.
     */
    public Image setFixedPosition(float left, float bottom) {
        setFixedPosition(left, bottom, getWidth());
        return this;
    }

    /**
     * Sets values for a absolute repositioning of the Element, on a specific
     * page. Also has as a side effect that the Element's {@link
     * Property#POSITION} is changed to {@link LayoutPosition#FIXED fixed}.
     *
     * @param pageNumber the page where the element must be positioned
     * @param left       horizontal position on the page
     * @param bottom     vertical position on the page
     * @return this Element.
     */
    public Image setFixedPosition(int pageNumber, float left, float bottom) {
        setFixedPosition(pageNumber, left, bottom, getWidth());
        return this;
    }

    /**
     * Gets width of the image. It returns width of image or form XObject,
     * not the width set by one of the #setWidth methods
     *
     * @return the original width of the image
     */
    public float getImageWidth() {
        return xObject.getWidth();
    }

    /**
     * Gets height of the image. It returns height of image or form XObject,
     * not the height set by one of the #setHeight methods
     *
     * @return the original height of the image
     */
    public float getImageHeight() {
        return xObject.getHeight();
    }

    public Image setMaxHeight(float maxHeight) {
        UnitValue maxHeightAsUv = UnitValue.createPointValue(maxHeight);
        setProperty(Property.MAX_HEIGHT, maxHeightAsUv);
        return (Image) (Object) this;
    }

    public Image setMinHeight(float minHeight) {
        UnitValue minHeightAsUv = UnitValue.createPointValue(minHeight);
        setProperty(Property.MIN_HEIGHT, minHeightAsUv);
        return (Image) (Object) this;
    }

    public Image setMaxWidth(float maxWidth) {
        setProperty(Property.MAX_WIDTH, maxWidth);
        return (Image) (Object) this;
    }

    public Image setMinWidth(float minWidth) {
        setProperty(Property.MIN_WIDTH, minWidth);
        return (Image) (Object) this;
    }

    /**
     * Gets scaled width of the image.
     *
     * @return the current scaled width
     */
    public float getImageScaledWidth() {
        return null == this.<Float>getProperty(Property.HORIZONTAL_SCALING) ?
                xObject.getWidth() :
                xObject.getWidth() * (float) this.<Float>getProperty(Property.HORIZONTAL_SCALING);
    }

    /**
     * Gets scaled height of the image.
     *
     * @return the current scaled height
     */
    public float getImageScaledHeight() {
        return null == this.<Float>getProperty(Property.VERTICAL_SCALING) ?
                xObject.getHeight() :
                xObject.getHeight() * (float) this.<Float>getProperty(Property.VERTICAL_SCALING);
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

    private static ImageData checkImageType(ImageData image) {
        if (image instanceof WmfImageData) {
            throw new PdfException(PdfException.CannotCreateLayoutImageByWmfImage);
        }
        return image;
    }
}
