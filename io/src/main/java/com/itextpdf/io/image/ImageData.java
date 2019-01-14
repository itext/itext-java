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
package com.itextpdf.io.image;

import com.itextpdf.io.IOException;
import com.itextpdf.io.LogMessageConstant;
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

    protected int colorSpace = -1;

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

    public int getColorSpace() {
        return colorSpace;
    }

    public void setColorSpace(int colorSpace) {
        this.colorSpace = colorSpace;
    }

    public byte[] getData() {
        return data;
    }

    public boolean canBeMask() {
        if (isRawImage()) {
            if (bpc > 0xff)
                return true;
        }
        return colorSpace == 1;
    }

    public boolean isMask() {
        return mask;
    }

    public ImageData getImageMask() {
        return imageMask;
    }

    public void setImageMask(ImageData imageMask) {
        if (this.mask)
            throw new IOException(IOException.ImageMaskCannotContainAnotherImageMask);
        if (!imageMask.mask)
            throw new IOException(IOException.ImageIsNotMaskYouMustCallImageDataMakeMask);
        this.imageMask = imageMask;
    }

    public boolean isSoftMask() {
        return mask && bpc > 1 && bpc <=8;
    }

    public void makeMask() {
        if (!canBeMask())
            throw new IOException(IOException.ThisImageCanNotBeAnImageMask);
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
            logger.warn(LogMessageConstant.IMAGE_SIZE_CANNOT_BE_MORE_4KB);
            return false;
        }
        if (imageMask != null) {
            logger.warn(LogMessageConstant.IMAGE_HAS_MASK);
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
