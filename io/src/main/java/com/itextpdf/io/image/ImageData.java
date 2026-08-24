/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.io.image;

import com.itextpdf.commons.logs.LazyLogger;
import com.itextpdf.io.colors.IccProfile;
import com.itextpdf.io.exceptions.IOException;
import com.itextpdf.io.exceptions.IoExceptionMessageConstant;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.io.source.RandomAccessFileOrArray;
import com.itextpdf.io.source.RandomAccessSourceFactory;
import com.itextpdf.io.util.StreamUtil;

import java.net.URL;
import java.util.Map;

/**
 * Describes encoded image data and the attributes needed to embed it in a document.
 */
public abstract class ImageData {

    private static final LazyLogger LOGGER = new LazyLogger(ImageData.class);

    /** a static that is used for attributing a unique id to each image. */
    private static long serialId = 0;

    private static final Object staticLock = new Object();

    /** Source URL from which the image bytes can be loaded, or {@code null} when bytes were supplied directly. */
    protected URL url;

    /** Component-value pairs defining transparent ranges, or {@code null} when no range is specified. */
    protected int[] transparency;

    /** Detected or declared format of the source image. */
    protected ImageType originalType;

    /** Image width in pixels. */
    protected float width;

    /** Image height in pixels. */
    protected float height;

    /** Encoded image bytes, or {@code null} until data is loaded from {@link #url}. */
    protected byte[] data;

    /** Size of the image data in bytes when known. */
    protected int imageSize;

    /** Bits per color component. */
    protected int bpc = 1;

    /** Is the number of components used to encode colorspace. */
    protected int colorEncodingComponentsNumber = -1;

    /** Decode array applied to image samples, or {@code null} when no decode array is specified. */
    protected float[] decode;

    /** Parameters associated with the image decoder, or {@code null}. */
    protected Map<String, Object> decodeParms;

    /** Whether the image samples are inverted. */
    protected boolean inverted = false;

    /** Clockwise rotation to apply to the image, in degrees. */
    protected float rotation;

    /** ICC color profile associated with the image, or {@code null}. */
    protected IccProfile profile;

    /** Horizontal resolution in dots per inch, or {@code 0} when unspecified. */
    protected int dpiX = 0;

    /** Vertical resolution in dots per inch, or {@code 0} when unspecified. */
    protected int dpiY = 0;

    /** Color-transform selector used by formats that support it. */
    protected int colorTransform = 1;

    /** Whether {@link #data} is already deflate-compressed. */
    protected boolean deflated;

    /** Whether this image is used as an image mask. */
    protected boolean mask = false;

    /** Mask image associated with this image, or {@code null}. */
    protected ImageData imageMask;

    /** Whether interpolation should be requested when rendering the image. */
    protected boolean interpolation;

    /** Pixel aspect ratio, or {@code 0} when unspecified. */
    protected float XYRatio = 0;

    /** PDF filter name used to decode the image data, or {@code null}. */
    protected String filter;

    /** Additional image attributes, or {@code null}. */
    protected Map<String, Object> imageAttributes;

    @Deprecated
    protected Long mySerialId = getSerialId();

    /**
     * Creates image data whose bytes are available from a URL.
     *
     * @param url  source URL, not {@code null}
     * @param type source image format
     */
    protected ImageData(URL url, ImageType type) {
        this.url = url;
        this.originalType = type;
    }

    /**
     * Creates image data from encoded bytes.
     *
     * @param bytes encoded image bytes; the array is retained
     * @param type  source image format
     */
    protected ImageData(byte[] bytes, ImageType type) {
        this.data = bytes;
        this.originalType = type;
    }

    /**
     * Indicates whether this instance represents raw sample data.
     *
     * @return {@code true} for raw image data; {@code false} otherwise
     */
    public boolean isRawImage(){
        return false;
    }

    /**
     * Gets the source URL.
     *
     * @return source URL, or {@code null} when the image was created from bytes
     */
    public URL getUrl() {
        return url;
    }

    /**
     * Sets the source URL used to load image bytes.
     *
     * @param url source URL, or {@code null}
     */
    public void setUrl(URL url) {
        this.url = url;
    }

    /**
     * Gets the component-value pairs that define transparent ranges.
     *
     * @return retained transparency array, or {@code null}
     */
    public int[] getTransparency() {
        return transparency;
    }

    /**
     * Sets component-value pairs that define transparent ranges.
     *
     * @param transparency transparency array to retain, or {@code null}
     */
    public void setTransparency(int[] transparency) {
        this.transparency = transparency;
    }

    /**
     * Checks whether image samples are inverted.
     *
     * @return {@code true} if samples are inverted
     */
    public boolean isInverted() {
        return inverted;
    }

    /**
     * Sets whether image samples are inverted.
     *
     * @param inverted {@code true} to invert samples
     */
    public void setInverted(boolean inverted) {
        this.inverted = inverted;
    }

    /**
     * Gets the clockwise rotation applied to the image.
     *
     * @return rotation in degrees
     */
    public float getRotation() {
        return rotation;
    }

    /**
     * Sets the clockwise rotation applied to the image.
     *
     * @param rotation rotation in degrees
     */
    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    /**
     * Gets the associated ICC color profile.
     *
     * @return ICC profile, or {@code null}
     */
    public IccProfile getProfile() {
        return profile;
    }

    /**
     * Sets the associated ICC color profile.
     *
     * @param profile ICC profile to associate, or {@code null}
     */
    public void setProfile(IccProfile profile) {
        this.profile = profile;
    }

    /**
     * Gets the horizontal image resolution.
     *
     * @return dots per inch, or {@code 0} when unspecified
     */
    public int getDpiX() {
        return dpiX;
    }

    /**
     * Gets the vertical image resolution.
     *
     * @return dots per inch, or {@code 0} when unspecified
     */
    public int getDpiY() {
        return dpiY;
    }

    /**
     * Sets the image resolution.
     *
     * @param dpiX horizontal resolution in dots per inch
     * @param dpiY vertical resolution in dots per inch
     */
    public void setDpi(int dpiX, int dpiY) {
        this.dpiX = dpiX;
        this.dpiY = dpiY;
    }

    /**
     * Gets the color-transform selector.
     *
     * @return color-transform selector
     */
    public int getColorTransform() {
        return colorTransform;
    }

    /**
     * Sets the color-transform selector.
     *
     * @param colorTransform color-transform selector
     */
    public void setColorTransform(int colorTransform) {
        this.colorTransform = colorTransform;
    }

    /**
     * Checks whether image bytes are already deflate-compressed.
     *
     * @return {@code true} when the image bytes are deflated
     */
    public boolean isDeflated() {
        return deflated;
    }

    /**
     * Sets whether image bytes are already deflate-compressed.
     *
     * @param deflated {@code true} when the image bytes are deflated
     */
    public void setDeflated(boolean deflated) {
        this.deflated = deflated;
    }

    /**
     * Gets the source image format.
     *
     * @return source image format
     */
    public ImageType getOriginalType() {
        return originalType;
    }

    /**
     * Gets the number of components used to encode colorspace.
     *
     * @return the number of components used to encode colorspace
     */
    public int getColorEncodingComponentsNumber() {
        return colorEncodingComponentsNumber;
    }

    /**
     * Sets the number of components used to encode colorspace.
     *
     * @param colorEncodingComponentsNumber the number of components used to encode colorspace
     */
    public void setColorEncodingComponentsNumber(int colorEncodingComponentsNumber) {
        this.colorEncodingComponentsNumber = colorEncodingComponentsNumber;
    }

    /**
     * Gets the encoded image bytes.
     *
     * @return retained encoded bytes, or {@code null} until loaded
     */
    public byte[] getData() {
        return data;
    }

    /**
     * Checks whether this image can be converted to an image mask.
     *
     * @return {@code true} if the color encoding permits masking
     */
    public boolean canBeMask() {
        if (isRawImage()) {
            if (bpc > 0xff)
                return true;
        }
        return colorEncodingComponentsNumber == 1;
    }

    /**
     * Checks whether this image is an image mask.
     *
     * @return {@code true} if this image is a mask
     */
    public boolean isMask() {
        return mask;
    }

    /**
     * Gets the mask image associated with this image.
     *
     * @return mask image, or {@code null}
     */
    public ImageData getImageMask() {
        return imageMask;
    }

    /**
     * Associates a mask image with this image.
     *
     * @param imageMask image that has been made a mask
     *
     * @throws IOException if this image is a mask or {@code imageMask} is not a mask
     */
    public void setImageMask(ImageData imageMask) {
        if (this.mask)
            throw new IOException(IoExceptionMessageConstant.IMAGE_MASK_CANNOT_CONTAIN_ANOTHER_IMAGE_MASK);
        if (!imageMask.mask)
            throw new IOException(IoExceptionMessageConstant.IMAGE_IS_NOT_A_MASK_YOU_MUST_CALL_IMAGE_DATA_MAKE_MASK);
        this.imageMask = imageMask;
    }

    /**
     * Checks whether this image is a soft mask.
     *
     * @return {@code true} if this image is a soft mask
     */
    public boolean isSoftMask() {
        return mask && bpc > 1 && bpc <=8;
    }

    /**
     * Converts this image to an image mask.
     *
     * @throws IOException if this image cannot be used as a mask
     */
    public void makeMask() {
        if (!canBeMask())
            throw new IOException(IoExceptionMessageConstant.THIS_IMAGE_CAN_NOT_BE_AN_IMAGE_MASK);
        mask = true;
    }

    /**
     * Gets the image width.
     *
     * @return width in pixels
     */
    public float getWidth() {
        return width;
    }

    /**
     * Sets the image width.
     *
     * @param width width in pixels
     */
    public void setWidth(float width) {
        this.width = width;
    }

    /**
     * Gets the image height.
     *
     * @return height in pixels
     */
    public float getHeight() {
        return height;
    }

    /**
     * Sets the image height.
     *
     * @param height height in pixels
     */
    public void setHeight(float height) {
        this.height = height;
    }

    /**
     * Gets the number of bits used for each color component.
     *
     * @return bits per component
     */
    public int getBpc() {
        return bpc;
    }

    /**
     * Sets the number of bits used for each color component.
     *
     * @param bpc bits per component
     */
    public void setBpc(int bpc) {
        this.bpc = bpc;
    }

    /**
     * Checks whether interpolation should be requested while rendering.
     *
     * @return {@code true} to request interpolation
     */
    public boolean isInterpolation() {
        return interpolation;
    }

    /**
     * Sets whether interpolation should be requested while rendering.
     *
     * @param interpolation {@code true} to request interpolation
     */
    public void setInterpolation(boolean interpolation) {
        this.interpolation = interpolation;
    }

    /**
     * Gets the pixel aspect ratio.
     *
     * @return pixel aspect ratio, or {@code 0} when unspecified
     */
    public float getXYRatio() {
        return XYRatio;
    }

    /**
     * Sets the pixel aspect ratio.
     *
     * @param XYRatio pixel aspect ratio
     */
    public void setXYRatio(float XYRatio) {
        this.XYRatio = XYRatio;
    }

    /**
     * Gets additional image attributes.
     *
     * @return retained attribute map, or {@code null}
     */
    public Map<String, Object> getImageAttributes() {
        return imageAttributes;
    }

    /**
     * Sets additional image attributes.
     *
     * @param imageAttributes attribute map to retain, or {@code null}
     */
    public void setImageAttributes(Map<String, Object> imageAttributes) {
        this.imageAttributes = imageAttributes;
    }

    /**
     * Gets the PDF filter name used for the image data.
     *
     * @return filter name, or {@code null}
     */
    public String getFilter() {
        return filter;
    }

    /**
     * Sets the PDF filter name used for the image data.
     *
     * @param filter PDF filter name, or {@code null}
     */
    public void setFilter(String filter) {
        this.filter = filter;
    }

    /**
     * Gets image decoder parameters.
     *
     * @return retained decoder-parameter map, or {@code null}
     */
    public Map<String, Object> getDecodeParms() {
        return decodeParms;
    }

    /**
     * Gets the image decode array.
     *
     * @return retained decode array, or {@code null}
     */
    public float[] getDecode() {
        return decode;
    }

    /**
     * Sets the image decode array.
     *
     * @param decode decode array to retain, or {@code null}
     */
    public void setDecode(float[] decode) {
        this.decode = decode;
    }

    /**
     * Checks if image can be inline
     * @return if the image can be inline
     */
    public boolean canImageBeInline() {
        if (imageSize > 4096) {
            LOGGER.warn(() -> IoLogMessageConstant.IMAGE_SIZE_CANNOT_BE_MORE_4KB);
            return false;
        }
        if (imageMask != null) {
            LOGGER.warn(() -> IoLogMessageConstant.IMAGE_HAS_MASK);
            return false;
        }
        return true;
    }

    /**
     * Load data from URL. url must be not null.
     * Note, this method doesn't check if data or url is null.
     *
     * @throws java.io.IOException if an I/O error occurs.
     */
    protected void loadData() throws java.io.IOException {
        RandomAccessFileOrArray raf = new RandomAccessFileOrArray(new RandomAccessSourceFactory().createSource(url));
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        StreamUtil.transferBytes(raf, stream);
        raf.close();
        data = stream.toByteArray();
    }

    /** Creates a new serial id.
     * @return the new serialId */
    private static Long getSerialId() {
        synchronized (staticLock) {
            return ++serialId;
        }
    }
}
