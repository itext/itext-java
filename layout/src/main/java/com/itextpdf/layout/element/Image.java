/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
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
import com.itextpdf.kernel.pdf.canvas.wmf.WmfImageData;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.DefaultAccessibilityProperties;
import com.itextpdf.kernel.pdf.tagutils.AccessibilityProperties;
import com.itextpdf.layout.tagging.IAccessibleElement;
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
    protected DefaultAccessibilityProperties tagProperties;

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
     * @return the left margin width, as a {@link UnitValue} object
     */
    public UnitValue getMarginLeft() {
        return this.<UnitValue>getProperty(Property.MARGIN_LEFT);
    }

    /**
     * Sets the left margin width of the element.
     *
     * @param value the new left margin width
     * @return this element
     */
    public Image setMarginLeft(float value) {
        UnitValue marginUV = UnitValue.createPointValue(value);
        setProperty(Property.MARGIN_LEFT, marginUV);
        return this;
    }

    /**
     * Gets the current right margin width of the image.
     *
     * @return the right margin width, as a {@link UnitValue} object
     */
    public UnitValue getMarginRight() {
        return this.<UnitValue>getProperty(Property.MARGIN_RIGHT);
    }

    /**
     * Sets the right margin width of the image.
     *
     * @param value the new right margin width
     * @return this image
     */
    public Image setMarginRight(float value) {
        UnitValue marginUV = UnitValue.createPointValue(value);
        setProperty(Property.MARGIN_RIGHT, marginUV);
        return this;
    }

    /**
     * Gets the current top margin width of the image.
     *
     * @return the top margin width, as a {@link UnitValue} object
     */
    public UnitValue getMarginTop() {
        return this.<UnitValue>getProperty(Property.MARGIN_TOP);
    }

    /**
     * Sets the top margin width of the image.
     *
     * @param value the new top margin width
     * @return this image
     */
    public Image setMarginTop(float value) {
        UnitValue marginUV = UnitValue.createPointValue(value);
        setProperty(Property.MARGIN_TOP, marginUV);
        return this;
    }

    /**
     * Gets the current bottom margin width of the image.
     *
     * @return the bottom margin width, as a {@link UnitValue} object
     */
    public UnitValue getMarginBottom() {
        return this.<UnitValue>getProperty(Property.MARGIN_BOTTOM);
    }

    /**
     * Sets the bottom margin width of the image.
     *
     * @param value the new bottom margin width
     * @return this image
     */
    public Image setMarginBottom(float value) {
        UnitValue marginUV = UnitValue.createPointValue(value);
        setProperty(Property.MARGIN_BOTTOM, marginUV);
        return this;
    }

    /**
     * Sets the margins around the image to a series of new widths.
     *
     * @param marginTop    the new margin top width
     * @param marginRight  the new margin right width
     * @param marginBottom the new margin bottom width
     * @param marginLeft   the new margin left width
     * @return this image
     */
    public Image setMargins(float marginTop, float marginRight, float marginBottom, float marginLeft) {
        return setMarginTop(marginTop).setMarginRight(marginRight).setMarginBottom(marginBottom).setMarginLeft(marginLeft);
    }

    /**
     * Gets the current left padding width of the image.
     *
     * @return the left padding width, as a {@link UnitValue} object
     */
    public UnitValue getPaddingLeft() {
        return this.<UnitValue>getProperty(Property.PADDING_LEFT);
    }

    /**
     * Sets the left padding width of the image.
     *
     * @param value the new left padding width
     * @return this image
     */
    public Image setPaddingLeft(float value) {
        UnitValue paddingUV = UnitValue.createPointValue(value);
        setProperty(Property.PADDING_LEFT, paddingUV);
        return (Image) (Object) this;
    }

    /**
     * Gets the current right padding width of the image.
     *
     * @return the right padding width, as a {@link UnitValue} object
     */
    public UnitValue getPaddingRight() {
        return this.<UnitValue>getProperty(Property.PADDING_RIGHT);
    }

    /**
     * Sets the right padding width of the image.
     *
     * @param value the new right padding width
     * @return this image
     */
    public Image setPaddingRight(float value) {
        UnitValue paddingUV = UnitValue.createPointValue(value);
        setProperty(Property.PADDING_RIGHT, paddingUV);
        return (Image) (Object) this;
    }

    /**
     * Gets the current top padding width of the image.
     *
     * @return the top padding width, as a {@link UnitValue} object
     */
    public UnitValue getPaddingTop() {
        return this.<UnitValue>getProperty(Property.PADDING_TOP);
    }

    /**
     * Sets the top padding width of the image.
     *
     * @param value the new top padding width
     * @return this image
     */
    public Image setPaddingTop(float value) {
        UnitValue paddingUV = UnitValue.createPointValue(value);
        setProperty(Property.PADDING_TOP, paddingUV);
        return (Image) (Object) this;
    }

    /**
     * Gets the current bottom padding width of the image.
     *
     * @return the bottom padding width, as a {@link UnitValue} object
     */
    public UnitValue getPaddingBottom() {
        return this.<UnitValue>getProperty(Property.PADDING_BOTTOM);
    }

    /**
     * Sets the bottom padding width of the image.
     *
     * @param value the new bottom padding width
     * @return this image
     */
    public Image setPaddingBottom(float value) {
        UnitValue paddingUV = UnitValue.createPointValue(value);
        setProperty(Property.PADDING_BOTTOM, paddingUV);
        return (Image) (Object) this;
    }

    /**
     * Sets all paddings around the image to the same width.
     *
     * @param commonPadding the new padding width
     * @return this image
     */
    public Image setPadding(float commonPadding) {
        return setPaddings(commonPadding, commonPadding, commonPadding, commonPadding);
    }

    /**
     * Sets the paddings around the image to a series of new widths.
     *
     * @param paddingTop    the new padding top width
     * @param paddingRight  the new padding right width
     * @param paddingBottom the new padding bottom width
     * @param paddingLeft   the new padding left width
     * @return this image
     */
    public Image setPaddings(float paddingTop, float paddingRight, float paddingBottom, float paddingLeft) {
        setPaddingTop(paddingTop);
        setPaddingRight(paddingRight);
        setPaddingBottom(paddingBottom);
        setPaddingLeft(paddingLeft);
        return this;
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

    /**
     * Sets the height property of the image, measured in points.
     *
     * @param height a value measured in points.
     * @return this image.
     */
    public Image setHeight(float height) {
        UnitValue heightAsUV = UnitValue.createPointValue(height);
        setProperty(Property.HEIGHT, heightAsUV);
        return (Image) (Object) this;
    }

    /**
     * Sets the height property of the image with a {@link UnitValue}.
     *
     * @param height a value measured in points.
     * @return this image.
     */
    public Image setHeight(UnitValue height) {
        setProperty(Property.HEIGHT, height);
        return (Image) (Object) this;
    }

    /**
     * Sets the max-height property of the image, measured in points.
     *
     * @param maxHeight a value measured in points.
     * @return this image.
     */
    public Image setMaxHeight(float maxHeight) {
        UnitValue maxHeightAsUv = UnitValue.createPointValue(maxHeight);
        setProperty(Property.MAX_HEIGHT, maxHeightAsUv);
        return (Image) (Object) this;
    }

    /**
     * Sets the max-height property of the image with a {@link UnitValue}.
     *
     * @param maxHeight a value measured in points.
     * @return this image.
     */
    public Image setMaxHeight(UnitValue maxHeight) {
        setProperty(Property.MAX_HEIGHT, maxHeight);
        return (Image) (Object) this;
    }

    /**
     * Sets the min-height property of the image, measured in points.
     *
     * @param minHeight a value measured in points.
     * @return this image.
     */
    public Image setMinHeight(float minHeight) {
        UnitValue minHeightAsUv = UnitValue.createPointValue(minHeight);
        setProperty(Property.MIN_HEIGHT, minHeightAsUv);
        return (Image) (Object) this;
    }

    /**
     * Sets the min-height property of the image with a {@link UnitValue}.
     *
     * @param minHeight a value measured in points.
     * @return this image.
     */
    public Image setMinHeight(UnitValue minHeight) {
        setProperty(Property.MIN_HEIGHT, minHeight);
        return (Image) (Object) this;
    }

    /**
     * Sets the max-width property of the image, measured in points.
     *
     * @param maxWidth a value measured in points.
     * @return this image.
     */
    public Image setMaxWidth(float maxWidth) {
        UnitValue minHeightAsUv = UnitValue.createPointValue(maxWidth);
        setProperty(Property.MAX_WIDTH, minHeightAsUv);
        return (Image) (Object) this;
    }

    /**
     * Sets the max-width property of the image with a {@link UnitValue}.
     *
     * @param maxWidth a value measured in points.
     * @return this image.
     */
    public Image setMaxWidth(UnitValue maxWidth) {
        setProperty(Property.MAX_WIDTH, maxWidth);
        return (Image) (Object) this;
    }

    /**
     * Sets the min-width property of the image, measured in points.
     *
     * @param minWidth a value measured in points.
     * @return this image.
     */
    public Image setMinWidth(float minWidth) {
        UnitValue minHeightAsUv = UnitValue.createPointValue(minWidth);
        setProperty(Property.MIN_WIDTH, minHeightAsUv);
        return (Image) (Object) this;
    }

    /**
     * Sets the min-width property of the image with a {@link UnitValue}.
     *
     * @param minWidth a value measured in points.
     * @return this image.
     */
    public Image setMinWidth(UnitValue minWidth) {
        setProperty(Property.MIN_WIDTH, minWidth);
        return (Image) (Object) this;
    }

    /**
     * Sets the width property of the image, measured in points.
     *
     * @param width a value measured in points.
     * @return this image.
     */
    public Image setWidth(float width) {
        setProperty(Property.WIDTH, UnitValue.createPointValue(width));
        return (Image) (Object) this;
    }

    /**
     * Sets the width property of the image with a {@link UnitValue}.
     *
     * @param width a {@link UnitValue} object
     * @return this image.
     */
    public Image setWidth(UnitValue width) {
        setProperty(Property.WIDTH, width);
        return (Image) (Object) this;
    }

    /**
     * Gets the width property of the image.
     *
     * @return the width of the element, with a value and a measurement unit.
     * @see UnitValue
     */
    public UnitValue getWidth() {
        return (UnitValue) this.<UnitValue>getProperty(Property.WIDTH);
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
    public AccessibilityProperties getAccessibilityProperties() {
        if (tagProperties == null) {
            tagProperties = new DefaultAccessibilityProperties(StandardRoles.FIGURE);
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
