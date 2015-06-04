package com.itextpdf.basics.image;

import com.itextpdf.basics.PdfRuntimeException;
import com.itextpdf.basics.color.IccProfile;

import java.net.URL;

public abstract class Image {

    public static final int JPEG = 1;
    public static final int PNG = 2;
    public static final int GIF = 3;
    public static final int BMP = 4;
    public static final int TIFF = 5;
    public static final int WMF = 6;
    public static final int PS = 7;
    public static final int JPEG2000 = 8;
    public static final int JBIG2 = 9;
    public static final int RAW = 10;

    protected URL url;

    protected int[] transparency;

    protected int originalType;

    protected float width;

    protected float height;

    protected byte[] data;

    protected int bpc = 1;

    protected int colorSpace = -1;

    protected boolean inverted = false;

    protected float rotation;

    protected IccProfile profile;

    protected int dpiX = 0;

    protected int dpiY = 0;

    protected int colorTransform = 1;

    protected boolean deflated;

    protected boolean mask = false;

    protected Image imageMask;

    protected boolean interpolation;

    protected float XYRatio = 0;

    protected Long mySerialId = getSerialId();

    protected Image(URL url, int type) {
        this.url = url;
        this.originalType = type;
    }

    protected Image(byte[] bytes, int type) {
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

    public int getOriginalType() {
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

    public Image getImageMask() {
        return imageMask;
    }

    public void setImageMask(Image imageMask) {
        if (this.mask)
            throw new PdfRuntimeException(PdfRuntimeException.ImageMaskCannotContainAnotherImageMask);
        if (!imageMask.mask)
            throw new PdfRuntimeException(PdfRuntimeException.ImageMaskIsNotAMaskDidYouDoMakeMask);
        this.imageMask = imageMask;
    }

    public boolean isSoftMask() {
        return mask && bpc > 0 && bpc <=8;
    }

    public void makeMask() {
        if (!canBeMask())
            throw new PdfRuntimeException(PdfRuntimeException.ImageCanNotBeAnImageMask);
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

    /** a static that is used for attributing a unique id to each image. */
    private static long serialId = 0;

    /** Creates a new serial id.
     * @return the new serialId */
    private static synchronized Long getSerialId() {
        return ++serialId;
    }
}
