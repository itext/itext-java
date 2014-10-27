package com.itextpdf.basics.image;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.codec.CCITTG4Encoder;
import com.itextpdf.basics.color.IccProfile;
import com.itextpdf.basics.io.RandomAccessFileOrArray;
import com.itextpdf.basics.io.RandomAccessSourceFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;

public class Image {

    public static final int NONE = 0;
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

    private static final byte[] gif = new byte[]{'G', 'I', 'F'};
    private static final byte[] jpeg = new byte[]{(byte) 0xFF, (byte) 0xD8};
    private static final byte[] jpeg2000_1 = new byte[]{0x00, 0x00, 0x00, 0x0c};
    private static final byte[] jpeg2000_2 = new byte[]{(byte) 0xff, (byte) 0x4f, (byte) 0xff, 0x51};
    private static final byte[] png = new byte[]{(byte) 137, 80, 78, 71};
    private static final byte[] wmf = new byte[]{(byte) 0xD7, (byte) 0xCD};
    private static final byte[] bmp = new byte[]{'B', 'M'};
    private static final byte[] tiff_1 = new byte[]{'M', 'M', 0, 42};
    private static final byte[] tiff_2 = new byte[]{'I', 'I', 42, 0};
    private static final byte[] jbig2 = new byte[]{(byte) 0x97, 'J', 'B', '2', '\r', '\n', 0x1a, '\n'};

    /**
     * Holds value of property originalData.
     */
    protected byte[] originalData;

    /**
     * The URL of the image.
     */
    protected URL url;

    /**
     * this is the transparency information of the raw image
     */
    protected int transparency[];

    /**
     * The image type.
     */
    protected int type;

    protected float width;

    protected float height;

    /**
     * The raw data of the image.
     */
    protected byte rawData[];

    /**
     * The bits per component of the raw image. It also flags a CCITT image.
     */
    protected int bpc = 1;

    protected int colorSpace = -1;

    /**
     * Holds value of property originalType.
     */
    protected int originalType = NONE;

    /**
     * Image color inversion
     */
    protected boolean inverted = false;

    /**
     * Some image formats, like TIFF may present the images rotated.
     * This is the rotation of the image in radians.
     */
    protected float rotation;

    protected IccProfile profile;

    /**
     * Holds value of property dpiX.
     */
    protected int dpiX = 0;

    /**
     * Holds value of property dpiY.
     */
    protected int dpiY = 0;

    protected int colorTransform = 1;

    protected boolean deflated;

    /**
     * Is this image a mask?
     */
    protected boolean mask = false;

    /**
     * The image that serves as a mask for this image.
     */
    protected Image imageMask;

    /**
     * Holds value of property smask.
     */
    protected boolean smask;

    protected IAdditional additional;

    protected Image(URL url) {
        this.url = url;
    }

    protected Image() {

    }

    public static Image getInstance(byte[] bytes) throws IOException, PdfException {
        return getInstance(bytes, false);
    }

    public static Image getInstance(byte[] bytes, boolean recoverImage) throws IOException, PdfException {
        return readImage(new ByteArrayInputStream(bytes), recoverImage);
    }

    public static Image getInstance(URL url) throws IOException, PdfException {
        return Image.getInstance(url, false);
    }

    public static Image getInstance(URL url, boolean recoverImage) throws IOException, PdfException {
        return readImage(url, recoverImage);
    }

    public static Image getInstance(String filename) throws IOException, PdfException {
        return getInstance(filename, false);
    }

    public static Image getInstance(String filename, boolean recoverImage) throws IOException, PdfException {
        return getInstance(new URL(filename), recoverImage);
    }

    /**
     * Creates an Image with CCITT G3 or G4 compression. It assumes that the
     * data bytes are already compressed.
     *
     * @param width       the exact width of the image
     * @param height      the exact height of the image
     * @param reverseBits reverses the bits in <code>data</code>. Bit 0 is swapped
     *                    with bit 7 and so on
     * @param typeCCITT   the type of compression in <code>data</code>. It can be
     *                    CCITTG4, CCITTG31D, CCITTG32D
     * @param parameters  parameters associated with this stream. Possible values are
     *                    CCITT_BLACKIS1, CCITT_ENCODEDBYTEALIGN, CCITT_ENDOFLINE and
     *                    CCITT_ENDOFBLOCK or a combination of them
     * @param data        the image data
     * @return an Image object
     * @throws com.itextpdf.basics.PdfException on error
     */
    public static Image getInstance(final int width, final int height, final boolean reverseBits,
                                    final int typeCCITT, final int parameters, final byte[] data)
            throws PdfException {
        return Image.getInstance(width, height, reverseBits, typeCCITT,
                parameters, data, null);
    }

    /**
     * Creates an Image with CCITT G3 or G4 compression. It assumes that the
     * data bytes are already compressed.
     *
     * @param width        the exact width of the image
     * @param height       the exact height of the image
     * @param reverseBits  reverses the bits in <code>data</code>. Bit 0 is swapped
     *                     with bit 7 and so on
     * @param typeCCITT    the type of compression in <code>data</code>. It can be
     *                     CCITTG4, CCITTG31D, CCITTG32D
     * @param parameters   parameters associated with this stream. Possible values are
     *                     CCITT_BLACKIS1, CCITT_ENCODEDBYTEALIGN, CCITT_ENDOFLINE and
     *                     CCITT_ENDOFBLOCK or a combination of them
     * @param data         the image data
     * @param transparency transparency information in the Mask format of the image
     *                     dictionary
     * @return an Image object
     * @throws com.itextpdf.basics.PdfException on error
     */
    public static Image getInstance(final int width, final int height, final boolean reverseBits,
                                    final int typeCCITT, final int parameters, final byte[] data, final int transparency[])
            throws PdfException {
        if (transparency != null && transparency.length != 2)
            throw new PdfException(PdfException.TransparencyLengthMustBeEqualTo2WithCcittImages);
        Image img = new CcittImage(width, height, reverseBits, typeCCITT,
                parameters, data);
        img.transparency = transparency;
        return img;
    }

    /**
     * Gets an instance of an Image in raw mode.
     *
     * @param width      the width of the image in pixels
     * @param height     the height of the image in pixels
     * @param components 1,3 or 4 for GrayScale, RGB and CMYK
     * @param data       the image data
     * @param bpc        bits per component
     * @return an object of type <CODE>ImgRaw</CODE>
     * @throws com.itextpdf.basics.PdfException on error
     */
    public static Image getInstance(final int width, final int height, final int components,
                                    final int bpc, final byte data[]) throws PdfException {
        return Image.getInstance(width, height, components, bpc, data, null);
    }

    /**
     * Gets an instance of an Image in raw mode.
     *
     * @param width        the width of the image in pixels
     * @param height       the height of the image in pixels
     * @param components   1,3 or 4 for GrayScale, RGB and CMYK
     * @param data         the image data
     * @param bpc          bits per component
     * @param transparency transparency information in the Mask format of the image
     *                     dictionary
     * @return an object of type <CODE>ImgRaw</CODE>
     * @throws com.itextpdf.basics.PdfException on error
     */
    public static Image getInstance(final int width, final int height, final int components,
                                    final int bpc, final byte data[], final int transparency[])
            throws PdfException {
        if (transparency != null && transparency.length != components * 2)
            throw new PdfException(PdfException.TransparencyLengthMustBeEqualTo2WithCcittImages);
        if (components == 1 && bpc == 1) {
            byte g4[] = CCITTG4Encoder.compress(data, width, height);
            return Image.getInstance(width, height, false, CcittImage.CCITTG4,
                    CcittImage.CCITT_BLACKIS1, g4, transparency);
        }
        Image img = new RawImage(width, height, components, bpc, data);
        img.transparency = transparency;
        return img;
    }

    public byte[] getOriginalData() {
        return originalData;
    }

    public void setOriginalData(byte[] originalData) {
        this.originalData = originalData;
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

    public int getOriginalType() {
        return originalType;
    }

    public void setOriginalType(int originalType) {
        this.originalType = originalType;
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

    /**
     * Returns <CODE>true</CODE> if this <CODE>Image</CODE> has the
     * requisites to be a mask.
     *
     * @return <CODE>true</CODE> if this <CODE>Image</CODE> can be a mask
     */
    public boolean canBeMask() {
        if (type == RAW) {
            if (bpc > 0xff)
                return true;
        }
        return colorSpace == 1;
    }

    /**
     * Returns <CODE>true</CODE> if this <CODE>Image</CODE> is a mask.
     *
     * @return <CODE>true</CODE> if this <CODE>Image</CODE> is a mask
     */
    public boolean isMask() {
        return mask;
    }

    public Image getImageMask() {
        return imageMask;
    }

    public void setImageMask(Image imageMask) throws PdfException {
        if (this.mask)
            throw new PdfException(PdfException.ImageMaskCannotContainAnotherImageMask);
        if (!imageMask.mask)
            throw new PdfException(PdfException.ImageMaskIsNotAMaskDidYouDoMakeMask);
        this.imageMask = imageMask;
        smask = imageMask.bpc > 1 && imageMask.bpc <= 8;
    }

    public boolean isSmask() {
        return smask;
    }

    public void setSmask(boolean smask) {
        this.smask = smask;
    }

    /**
     * Make this <CODE>Image</CODE> a mask.
     *
     * @throws com.itextpdf.basics.PdfException if this <CODE>Image</CODE> can not be a mask
     */
    public void makeMask() throws PdfException {
        if (!canBeMask())
            throw new PdfException(PdfException.ImageCanNotBeAnImageMask);
        mask = true;
    }

    public IAdditional getAdditional() {
        return additional;
    }

    public void setAdditional(IAdditional additional) {
        this.additional = additional;
    }

    private static byte[] readImageType(InputStream inputStream) throws IOException {
        byte[] bytes = new byte[8];
        inputStream.read(bytes);
        return bytes;
    }

    private static RandomAccessFileOrArray createRandomAccessSource(URL url) throws IOException {
        RandomAccessSourceFactory rasf = new RandomAccessSourceFactory();
        if ("file".equals(url.getProtocol())) {
            String file = url.getFile();
            file = URLDecoder.decode(file, "UTF-8");
            return new RandomAccessFileOrArray(rasf.createBestSource(file));
        } else
            return new RandomAccessFileOrArray(rasf.createSource(url));
    }

    private static RandomAccessFileOrArray createRandomAccessSource(byte[] bytes) {
        RandomAccessSourceFactory rasf = new RandomAccessSourceFactory();
        return new RandomAccessFileOrArray(rasf.createSource(bytes));
    }

    private static <T> RandomAccessFileOrArray createRandomAccessSource(T input) throws IOException {
        if (input instanceof URL)
            return createRandomAccessSource((URL) input);
        else
            return createRandomAccessSource((byte[]) input);

    }

    private static <T> Image readImage(T source, boolean recoverImage) throws IOException, PdfException {
        InputStream is = source instanceof URL ? ((URL) source).openStream() : new ByteArrayInputStream((byte[]) source);
        byte[] imageType = readImageType(is);
        is.close();
//        if (imageTypeIs(imageType, gif)) {
//            GifImage gif = new GifImage(source);
//            return gif.getImage(1);
//        } else if (imageTypeIs(imageType, jpeg)) {
//            return new Jpeg(source);
//        } else if (imageTypeIs(imageType, jpeg2000_1) || imageTypeIs(imageType, jpeg2000_2)) {
//            return new Jpeg2000(source);
//        } else if (imageTypeIs(imageType, png)) {
//            return PngImage.getImage(source);
//        } else if (imageTypeIs(imageType, wmf)) {
//            return new ImgWMF(bytes);
//        } else if (imageTypeIs(imageType, bmp)) {
//            return BmpImage.getImage(source);
//        } else if (imageTypeIs(imageType, tiff_1) || imageTypeIs(imageType, tiff_2)) {
//            RandomAccessFileOrArray ra = null;
//            try {
//                ra = createRandomAccessSource(source);
//                Image img = TiffImage.getTiffImage(ra, 1);
//                setOriginalSource(img, source);
//                return img;
//            } catch (RuntimeException e) {
//                if (recoverImage) {
//                    // reruns the getTiffImage() with several error recovering workarounds in place
//                    // not guaranteed to work with every TIFF
//                    Image img = TiffImage.getTiffImage(ra, recoverImage, 1);
//                    setOriginalSource(img, source);
//                    return img;
//                }
//                throw e;
//            } finally {
//                if (ra != null)
//                    ra.close();
//            }
//
//        } else if (imageTypeIs(imageType, jbig2)) {
//            // a jbig2 file with a file header.  the header is the only way we know here.
//            // embedded jbig2s don't have a header, have to create them by explicit use of Jbig2Image?
//            // nkerr, 2008-12-05  see also the getInstance(URL)
//            RandomAccessFileOrArray ra = null;
//            try {
//                ra = createRandomAccessSource(source);
//                Image img = JBIG2Image.getJbig2Image(ra, 1);
//                setOriginalSource(img, source);
//                return img;
//            } finally {
//                if (ra != null)
//                    ra.close();
//            }
//        }
        throw new PdfException(PdfException.ImageFormatCannotBeRecognized);
    }

    private static <T> void setOriginalSource(Image img, T data) {
        if (data instanceof URL)
            img.setUrl((URL) data);
        else if (img.getOriginalData() == null)
            img.setOriginalData((byte[]) data);
    }

    private static boolean imageTypeIs(byte[] imageType, byte[] compareWith) {
        for (int i = 0; i < compareWith.length; i++) {
            if (imageType[i] != compareWith[i])
                return false;
        }
        return true;
    }

    public static interface IAdditional {

    }

}
