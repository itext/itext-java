package com.itextpdf.basics.image;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.io.RandomAccessFileOrArray;
import com.itextpdf.basics.io.RandomAccessSourceFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;

public class Image {

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

    public static Image load(byte[] bytes) throws IOException, PdfException {
        return load(bytes, false);
    }

    public static Image load(byte[] bytes, boolean recoverImage) throws IOException, PdfException {
        return readImage(new ByteArrayInputStream(bytes), recoverImage);
    }

    public static Image load(URL url) throws IOException, PdfException {
        return Image.load(url, false);
    }

    public static Image load(URL url, boolean recoverImage) throws IOException, PdfException {
        return readImage(url, recoverImage);
    }

    public static Image load(String filename) throws IOException, PdfException {
        return load(filename, false);
    }

    public static Image load(String filename, boolean recoverImage) throws IOException, PdfException {
        return load(new URL(filename), recoverImage);
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

}
