/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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

import com.itextpdf.io.exceptions.IOException;
import com.itextpdf.io.exceptions.IoExceptionMessageConstant;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.io.colors.IccProfile;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.io.source.RandomAccessFileOrArray;
import com.itextpdf.io.source.RandomAccessSourceFactory;
import com.itextpdf.io.util.StreamUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Map;

public abstract class ImageData {

    /** a static that is used for attributing a unique id to each image. */
    private static long serialId = 0;

    private static final Object staticLock = new Object();

    protected URL url;

    protected int[] transparency;

    protected ImageType originalType;

    protected float width;

    protected float height;

    protected byte[] data;

    protected int imageSize;

    protected int bpc = 1;

    /** Is the number of components used to encode colorspace. */
    protected int colorEncodingComponentsNumber = -1;

    protected float[] decode;

    protected Map<String, Object> decodeParms;

    protected boolean inverted = false;

    protected float rotation;

    protected IccProfile profile;

    protected int dpiX = 0;

    protected int dpiY = 0;

    protected int colorTransform = 1;

    protected boolean deflated;

    protected boolean mask = false;

    protected ImageData imageMask;

    protected boolean interpolation;

    protected float XYRatio = 0;

    protected String filter;

    protected Map<String, Object> imageAttributes;

    protected Long mySerialId = getSerialId();

    protected ImageData(URL url, ImageType type) {
        this.url = url;
        this.originalType = type;
    }

    protected ImageData(byte[] bytes, ImageType type) {
        this.data = bytes;
        this.originalType = type;
    }

    public boolean isRawImage(){
        return false;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public int[] getTransparency() {
        return transparency;
    }

    public void setTransparency(int[] transparency) {
        this.transparency = transparency;
    }

    public boolean isInverted() {
        return inverted;
    }

    public void setInverted(boolean inverted) {
        this.inverted = inverted;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public IccProfile getProfile() {
        return profile;
    }

    public void setProfile(IccProfile profile) {
        this.profile = profile;
    }

    public int getDpiX() {
        return dpiX;
    }

    public int getDpiY() {
        return dpiY;
    }

    public void setDpi(int dpiX, int dpiY) {
        this.dpiX = dpiX;
        this.dpiY = dpiY;
    }

    public int getColorTransform() {
        return colorTransform;
    }

    public void setColorTransform(int colorTransform) {
        this.colorTransform = colorTransform;
    }

    public boolean isDeflated() {
        return deflated;
    }

    public void setDeflated(boolean deflated) {
        this.deflated = deflated;
    }

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

    public byte[] getData() {
        return data;
    }

    public boolean canBeMask() {
        if (isRawImage()) {
            if (bpc > 0xff)
                return true;
        }
        return colorEncodingComponentsNumber == 1;
    }

    public boolean isMask() {
        return mask;
    }

    public ImageData getImageMask() {
        return imageMask;
    }

    public void setImageMask(ImageData imageMask) {
        if (this.mask)
            throw new IOException(IoExceptionMessageConstant.IMAGE_MASK_CANNOT_CONTAIN_ANOTHER_IMAGE_MASK);
        if (!imageMask.mask)
            throw new IOException(IoExceptionMessageConstant.IMAGE_IS_NOT_A_MASK_YOU_MUST_CALL_IMAGE_DATA_MAKE_MASK);
        this.imageMask = imageMask;
    }

    public boolean isSoftMask() {
        return mask && bpc > 1 && bpc <=8;
    }

    public void makeMask() {
        if (!canBeMask())
            throw new IOException(IoExceptionMessageConstant.THIS_IMAGE_CAN_NOT_BE_AN_IMAGE_MASK);
        mask = true;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public int getBpc() {
        return bpc;
    }

    public void setBpc(int bpc) {
        this.bpc = bpc;
    }

    public boolean isInterpolation() {
        return interpolation;
    }

    public void setInterpolation(boolean interpolation) {
        this.interpolation = interpolation;
    }

    public float getXYRatio() {
        return XYRatio;
    }

    public void setXYRatio(float XYRatio) {
        this.XYRatio = XYRatio;
    }

    public Map<String, Object> getImageAttributes() {
        return imageAttributes;
    }

    public void setImageAttributes(Map<String, Object> imageAttributes) {
        this.imageAttributes = imageAttributes;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public Map<String, Object> getDecodeParms() {
        return decodeParms;
    }

    public float[] getDecode() {
        return decode;
    }

    public void setDecode(float[] decode) {
        this.decode = decode;
    }

    /**
     * Checks if image can be inline
     * @return if the image can be inline
     */
    public boolean canImageBeInline() {
        Logger logger = LoggerFactory.getLogger(ImageData.class);
        if (imageSize > 4096) {
            logger.warn(IoLogMessageConstant.IMAGE_SIZE_CANNOT_BE_MORE_4KB);
            return false;
        }
        if (imageMask != null) {
            logger.warn(IoLogMessageConstant.IMAGE_HAS_MASK);
            return false;
        }
        return true;
    }

    /**
     * Load data from URL. url must be not null.
     * Note, this method doesn't check if data or url is null.
     * @throws java.io.IOException
     */
    void loadData() throws java.io.IOException {
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
