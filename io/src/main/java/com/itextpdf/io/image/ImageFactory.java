package com.itextpdf.io.image;

import com.itextpdf.io.IOException;
import com.itextpdf.io.util.Utilities;
import com.itextpdf.io.codec.CCITTG4Encoder;
import com.itextpdf.io.codec.TIFFFaxDecoder;
import com.itextpdf.io.source.ByteArrayOutputStream;

import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.PixelGrabber;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ImageFactory {

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

    public static Image getImage(byte[] bytes, boolean recoverImage) {
        return getImageInstance(bytes, recoverImage);
    }

    public static Image getImage(byte[] bytes) {
        return getImage(bytes, false);
    }

    public static Image getImage(URL url, boolean recoverImage) {
        return getImageInstance(url, recoverImage);
    }

    public static Image getImage(URL url) {
        return getImage(url, false);
    }

    public static Image getImage(String filename, boolean recoverImage) throws MalformedURLException {
        return getImage(Utilities.toURL(filename), recoverImage);
    }

    public static Image getImage(String filename) throws MalformedURLException {
        return getImage(filename, false);
    }

    public static Image getImage(final int width, final int height, final boolean reverseBits,
                                    final int typeCCITT, final int parameters, final byte[] data,
                                    final int[] transparency) {
        if (transparency != null && transparency.length != 2)
            throw new IOException(IOException.TransparencyLengthMustBeEqualTo2WithCcittImages);
        if (typeCCITT != RawImage.CCITTG4 && typeCCITT != RawImage.CCITTG3_1D && typeCCITT != RawImage.CCITTG3_2D)
            throw new IOException(IOException.CcittCompressionTypeMustBeCcittg4Ccittg3_1dOrCcittg3_2d);
        if (reverseBits)
            TIFFFaxDecoder.reverseBits(data);
        RawImage image = new RawImage(data, ImageType.RAW);
        image.setTypeCcitt(typeCCITT);
        image.height = height;
        image.width = width;
        image.colorSpace = parameters;
        image.transparency = transparency;
        return image;
    }

    public static Image getImage(final int width, final int height, final int components,
                                    final int bpc, final byte[] data, final int[] transparency) {
        if (transparency != null && transparency.length != components * 2)
            throw new IOException(IOException.TransparencyLengthMustBeEqualTo2WithCcittImages);
        if (components == 1 && bpc == 1) {
            byte g4[] = CCITTG4Encoder.compress(data, width, height);
            return ImageFactory.getImage(width, height, false, RawImage.CCITTG4, RawImage.CCITT_BLACKIS1, g4, transparency);
        }
        RawImage image = new RawImage(data, ImageType.RAW);
        image.height = height;
        image.width = width;
        if (components != 1 && components != 3 && components != 4)
            throw new IOException(IOException.ComponentsMustBe1_3Or4);
        if (bpc != 1 && bpc != 2 && bpc != 4 && bpc != 8)
            throw new IOException(IOException.BitsPerComponentMustBe1_2_4or8);
        image.colorSpace = components;
        image.bpc = bpc;
        image.data = data;
        image.transparency = transparency;
        return image;
    }

    /**
     * Gets an instance of an Image from a java.awt.Image
     * @param image the java.awt.Image to convert
     * @param color if different from <CODE>null</CODE> the transparency pixels are replaced by this color
     * @return RawImage
     */
    public static Image getImage(final java.awt.Image image, final java.awt.Color color) throws java.io.IOException {
        return ImageFactory.getImage(image, color, false);
    }

    /**
     * Gets an instance of an Image from a java.awt.Image.
     * @param image the <CODE>java.awt.Image</CODE> to convert
     * @param color if different from <CODE>null</CODE> the transparency pixels are replaced by this color
     * @param forceBW if <CODE>true</CODE> the image is treated as black and white
     * @return RawImage
     */
    public static Image getImage(final java.awt.Image image, final java.awt.Color color, boolean forceBW) throws java.io.IOException {
        if (image instanceof BufferedImage) {
            BufferedImage bi = (BufferedImage) image;
            if (bi.getType() == BufferedImage.TYPE_BYTE_BINARY && bi.getColorModel().getPixelSize() == 1) {
                forceBW = true;
            }
        }

        PixelGrabber pg = new PixelGrabber(image, 0, 0, -1, -1, true);
        try {
            pg.grabPixels();
        } catch (InterruptedException e) {
            throw new java.io.IOException("Java.awt.image was interrupted. Waiting for pixels");
        }
        if ((pg.getStatus() & ImageObserver.ABORT) != 0) {
            throw new java.io.IOException("Java.awt.image fetch aborted or errored");
        }
        int w = pg.getWidth();
        int h = pg.getHeight();
        int[] pixels = (int[]) pg.getPixels();
        if (forceBW) {
            int byteWidth = w / 8 + ((w & 7) != 0 ? 1 : 0);
            byte[] pixelsByte = new byte[byteWidth * h];

            int index = 0;
            int size = h * w;
            int transColor = 1;
            if (color != null) {
                transColor = color.getRed() + color.getGreen()
                        + color.getBlue() < 384 ? 0 : 1;
            }
            int transparency[] = null;
            int cbyte = 0x80;
            int wMarker = 0;
            int currByte = 0;
            if (color != null) {
                for (int j = 0; j < size; j++) {
                    int alpha = pixels[j] >> 24 & 0xff;
                    if (alpha < 250) {
                        if (transColor == 1)
                            currByte |= cbyte;
                    } else {
                        if ((pixels[j] & 0x888) != 0)
                            currByte |= cbyte;
                    }
                    cbyte >>= 1;
                    if (cbyte == 0 || wMarker + 1 >= w) {
                        pixelsByte[index++] = (byte) currByte;
                        cbyte = 0x80;
                        currByte = 0;
                    }
                    ++wMarker;
                    if (wMarker >= w)
                        wMarker = 0;
                }
            } else {
                for (int j = 0; j < size; j++) {
                    if (transparency == null) {
                        int alpha = pixels[j] >> 24 & 0xff;
                        if (alpha == 0) {
                            transparency = new int[2];
							/* bugfix by M.P. Liston, ASC, was: ... ? 1: 0; */
                            transparency[0] = transparency[1] = (pixels[j] & 0x888) != 0 ? 0xff : 0;
                        }
                    }
                    if ((pixels[j] & 0x888) != 0)
                        currByte |= cbyte;
                    cbyte >>= 1;
                    if (cbyte == 0 || wMarker + 1 >= w) {
                        pixelsByte[index++] = (byte) currByte;
                        cbyte = 0x80;
                        currByte = 0;
                    }
                    ++wMarker;
                    if (wMarker >= w)
                        wMarker = 0;
                }
            }
            return ImageFactory.getImage(w, h, 1, 1, pixelsByte, transparency);
        } else {
            byte[] pixelsByte = new byte[w * h * 3];
            byte[] smask = null;

            int index = 0;
            int size = h * w;
            int red = 255;
            int green = 255;
            int blue = 255;
            if (color != null) {
                red = color.getRed();
                green = color.getGreen();
                blue = color.getBlue();
            }
            int transparency[] = null;
            if (color != null) {
                for (int j = 0; j < size; j++) {
                    int alpha = pixels[j] >> 24 & 0xff;
                    if (alpha < 250) {
                        pixelsByte[index++] = (byte) red;
                        pixelsByte[index++] = (byte) green;
                        pixelsByte[index++] = (byte) blue;
                    } else {
                        pixelsByte[index++] = (byte) (pixels[j] >> 16 & 0xff);
                        pixelsByte[index++] = (byte) (pixels[j] >> 8 & 0xff);
                        pixelsByte[index++] = (byte) (pixels[j] & 0xff);
                    }
                }
            } else {
                int transparentPixel = 0;
                smask = new byte[w * h];
                boolean shades = false;
                for (int j = 0; j < size; j++) {
                    byte alpha = smask[j] = (byte) (pixels[j] >> 24 & 0xff);
					/* bugfix by Chris Nokleberg */
                    if (!shades) {
                        if (alpha != 0 && alpha != -1) {
                            shades = true;
                        } else if (transparency == null) {
                            if (alpha == 0) {
                                transparentPixel = pixels[j] & 0xffffff;
                                transparency = new int[6];
                                transparency[0] = transparency[1] = transparentPixel >> 16 & 0xff;
                                transparency[2] = transparency[3] = transparentPixel >> 8 & 0xff;
                                transparency[4] = transparency[5] = transparentPixel & 0xff;
                            }
                        } else if ((pixels[j] & 0xffffff) != transparentPixel) {
                            shades = true;
                        }
                    }
                    pixelsByte[index++] = (byte) (pixels[j] >> 16 & 0xff);
                    pixelsByte[index++] = (byte) (pixels[j] >> 8 & 0xff);
                    pixelsByte[index++] = (byte) (pixels[j] & 0xff);
                }
                if (shades)
                    transparency = null;
                else
                    smask = null;
            }
            Image img = ImageFactory.getImage(w, h, 3, 8, pixelsByte, transparency);
            if (smask != null) {
                Image sm = ImageFactory.getImage(w, h, 1, 8, smask, null);
                sm.makeMask();
                img.setImageMask(sm);
            }
            return img;
        }
    }

    public static Image getBmpImage(URL url, boolean noHeader, int size) {
        byte[] imageType = readImageType(url);
        if (imageTypeIs(imageType, bmp)) {
            Image image = new BmpImage(url, noHeader, size);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            BmpImageHelper.processImage(image, baos);
            return image;
        }
        throw new IllegalArgumentException("BMP image expected.");
    }

    public static Image getBmpImage(byte[] bytes, boolean noHeader, int size) {
        byte[] imageType = readImageType(bytes);
        if (noHeader || imageTypeIs(imageType, bmp)) {
            Image image = new BmpImage(bytes, noHeader, size);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            BmpImageHelper.processImage(image, baos);
            return image;
        }
        throw new IllegalArgumentException("BMP image expected.");
    }

    /**
     * Return a GifImage object. This object cannot be added to a document
     * @param bytes
     * @return
     */
    public static GifImage getGifImage(byte[] bytes) {
        byte[] imageType = readImageType(bytes);
        if (imageTypeIs(imageType, gif)) {
            GifImage image = new GifImage(bytes);
            GifImageHelper.processImage(image);
            return image;
        }
        throw new IllegalArgumentException("GIF image expected.");
    }

    /**
     * Returns a specified frame of the gif image
     * @param url url of gif image
     * @param frame number of frame to be returned
     * @return
     */
    public static Image getGifFrame(URL url, int frame) {
        byte[] imageType = readImageType(url);
        if (imageTypeIs(imageType, gif)) {
            GifImage image = new GifImage(url);
            GifImageHelper.processImage(image, frame - 1);
            return image.getFrames().get(frame - 1);
        }
        throw new IllegalArgumentException("GIF image expected.");
    }

    /**
     * Returns a specified frame of the gif image
     * @param bytes byte array of gif image
     * @param frame number of frame to be returned
     * @return
     */
    public static Image getGifFrame(byte[] bytes, int frame) {
        byte[] imageType = readImageType(bytes);
        if (imageTypeIs(imageType, gif)) {
            GifImage image = new GifImage(bytes);
            GifImageHelper.processImage(image, frame - 1);
            return image.getFrames().get(frame - 1);
        }
        throw new IllegalArgumentException("GIF image expected.");
    }

    /**
     * Returns <CODE>List</CODE> of gif image frames
     * @param bytes byte array of gif image
     * @param frameNumbers array of frame numbers of gif image
     * @return
     */
    public static List<Image> getGifFrames(byte[] bytes, int[] frameNumbers) {
        byte[] imageType = readImageType(bytes);
        if (imageTypeIs(imageType, gif)) {
            GifImage image = new GifImage(bytes);
            Arrays.sort(frameNumbers);
            GifImageHelper.processImage(image, frameNumbers[frameNumbers.length - 1] - 1);
            List<Image> frames = new ArrayList<>();
            for (int frame : frameNumbers) {
                frames.add(image.getFrames().get(frame - 1));
            }
            return frames;
        }
        throw new IllegalArgumentException("GIF image expected.");
    }

    /**
     * Returns <CODE>List</CODE> of gif image frames
     * @param url url of gif image
     * @param frameNumbers array of frame numbers of gif image
     * @return
     */
    public static List<Image> getGifFrames(URL url, int[] frameNumbers) {
        byte[] imageType = readImageType(url);
        if (imageTypeIs(imageType, gif)) {
            GifImage image = new GifImage(url);
            Arrays.sort(frameNumbers);
            GifImageHelper.processImage(image, frameNumbers[frameNumbers.length - 1] - 1);
            List<Image> frames = new ArrayList<>();
            for (int frame : frameNumbers) {
                frames.add(image.getFrames().get(frame - 1));
            }
            return frames;
        }
        throw new IllegalArgumentException("GIF image expected.");
    }

    /**
     * Returns <CODE>List</CODE> of gif image frames
     * @param bytes byte array of gif image
     * @return all frames of gif image
     */
    public static List<Image> getGifFrames(byte[] bytes) {
        byte[] imageType = readImageType(bytes);
        if (imageTypeIs(imageType, gif)) {
            GifImage image = new GifImage(bytes);
            GifImageHelper.processImage(image);
            return image.getFrames();
        }
        throw new IllegalArgumentException("GIF image expected.");
    }

    /**
     * Returns <CODE>List</CODE> of gif image frames
     * @param url url of gif image
     * @return all frames of gif image
     */
    public static List<Image> getGifFrames(URL url) {
        byte[] imageType = readImageType(url);
        if (imageTypeIs(imageType, gif)) {
            GifImage image = new GifImage(url);
            GifImageHelper.processImage(image);
            return image.getFrames();
        }
        throw new IllegalArgumentException("GIF image expected.");
    }

    public static Image getJbig2Image(URL url, int page) {
        if (page < 1)
            throw new IllegalArgumentException("The page number must be greater than 0");
        byte[] imageType = readImageType(url);
        if (imageTypeIs(imageType, jbig2)) {
            Image image = new Jbig2Image(url, page);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Jbig2ImageHelper.processImage(image, baos);
            image.data = baos.toByteArray();
            return image;
        }
        throw new IllegalArgumentException("JBIG2 image expected.");
    }

    public static Image getJbig2Image(byte[] bytes, int page) {
        if (page < 1)
            throw new IllegalArgumentException("The page number must be greater than 0");
        byte[] imageType = readImageType(bytes);
        if (imageTypeIs(imageType, jbig2)) {
            Image image = new Jbig2Image(bytes, page);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Jbig2ImageHelper.processImage(image, baos);
            image.data = baos.toByteArray();
            return image;
        }
        throw new IllegalArgumentException("JBIG2 image expected.");

    }

    public static Image getJpegImage(URL url) {
        byte[] imageType = readImageType(url);
        if (imageTypeIs(imageType, jpeg)) {
            Image image = new JpegImage(url);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            JpegImageHelper.processImage(image, baos);
            image.data = baos.toByteArray();
            return image;
        }
        throw new IllegalArgumentException("JPEG image expected.");
    }

    public static Image getJpegImage(byte[] bytes) {
        byte[] imageType = readImageType(bytes);
        if (imageTypeIs(imageType, jpeg)) {
            Image image = new JpegImage(bytes);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            JpegImageHelper.processImage(image, baos);
            image.data = baos.toByteArray();
            return image;
        }
        throw new IllegalArgumentException("JPEG image expected.");

    }

    public static Image getJpeg2000Image(URL url) {
        byte[] imageType = readImageType(url);
        if (imageTypeIs(imageType, jpeg2000_1) || imageTypeIs(imageType, jpeg2000_2)) {
            Image image = new Jpeg2000Image(url);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Jpeg2000ImageHelper.processImage(image, baos);
            image.data = baos.toByteArray();
            return image;
        }
        throw new IllegalArgumentException("JPEG2000 image expected.");
    }

    public static Image getJpeg2000Image(byte[] bytes) {
        byte[] imageType = readImageType(bytes);
        if (imageTypeIs(imageType, jpeg2000_1) || imageTypeIs(imageType, jpeg2000_2)) {
            Image image = new Jpeg2000Image(bytes);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Jpeg2000ImageHelper.processImage(image, baos);
            image.data = baos.toByteArray();
            return image;
        }
        throw new IllegalArgumentException("JPEG2000 image expected.");

    }

    public static Image getPngImage(URL url) {
        byte[] imageType = readImageType(url);
        if (imageTypeIs(imageType, png)) {
            Image image = new PngImage(url);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PngImageHelper.processImage(image, baos);
            image.data = baos.toByteArray();
            return image;
        }
        throw new IllegalArgumentException("PNG image expected.");
    }

    public static Image getPngImage(byte[] bytes) {
        byte[] imageType = readImageType(bytes);
        if (imageTypeIs(imageType, png)) {
            Image image = new PngImage(bytes);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PngImageHelper.processImage(image, baos);
            image.data = baos.toByteArray();
            return image;
        }
        throw new IllegalArgumentException("PNG image expected.");
    }

    public static Image getTiffImage(URL url, boolean recoverFromImageError, int page, boolean direct) {
        byte[] imageType = readImageType(url);
        if (imageTypeIs(imageType, tiff_1) || imageTypeIs(imageType, tiff_2)) {
            Image image = new TiffImage(url, recoverFromImageError, page, direct);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            TiffImageHelper.processImage(image, baos);
            return image;
        }
        throw new IllegalArgumentException("TIFF image expected.");
    }

    public static Image getTiffImage(byte[] bytes, boolean recoverFromImageError, int page, boolean direct) {
        byte[] imageType = readImageType(bytes);
        if (imageTypeIs(imageType, tiff_1) || imageTypeIs(imageType, tiff_2)) {
            Image image = new TiffImage(bytes, recoverFromImageError, page, direct);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            TiffImageHelper.processImage(image, baos);
            return image;
        }
        throw new IllegalArgumentException("TIFF image expected.");
    }

    public static Image getRawImage(byte[] bytes) {
        return new RawImage(bytes, ImageType.RAW);
    }

    private static Image getImageInstance(URL source, boolean recoverImage) {
        byte[] imageType = readImageType(source);
        if (imageTypeIs(imageType, gif)) {
            GifImage image = new GifImage(source);
            GifImageHelper.processImage(image, 0);
            return image.getFrames().get(0);
        } else if (imageTypeIs(imageType, jpeg)) {
            Image image = new JpegImage(source);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            JpegImageHelper.processImage(image, baos);
            image.data = baos.toByteArray();
            return image;
        } else if (imageTypeIs(imageType, jpeg2000_1) || imageTypeIs(imageType, jpeg2000_2)) {
            Image image = new Jpeg2000Image(source);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Jpeg2000ImageHelper.processImage(image, baos);
            image.data = baos.toByteArray();
            return image;
        } else if (imageTypeIs(imageType, png)) {
            Image image = new PngImage(source);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PngImageHelper.processImage(image, baos);
            image.data = baos.toByteArray();
            return image;
        } else if (imageTypeIs(imageType, bmp)) {
            Image image = new BmpImage(source, false, 0);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            BmpImageHelper.processImage(image, baos);
            return image;
        } else if (imageTypeIs(imageType, tiff_1) || imageTypeIs(imageType, tiff_2)) {
            Image image = new TiffImage(source, recoverImage, 1, false);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            TiffImageHelper.processImage(image, baos);
            return image;
        } else if (imageTypeIs(imageType, jbig2)) {
            Image image = new Jbig2Image(source, 1);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Jbig2ImageHelper.processImage(image, baos);
            image.data = baos.toByteArray();
            return image;
        }
        throw new IOException(IOException.ImageFormatCannotBeRecognized);
    }

    private static Image getImageInstance(byte[] bytes, boolean recoverImage) {
        byte[] imageType = readImageType(bytes);
        if (imageTypeIs(imageType, gif)) {
            GifImage image = new GifImage(bytes);
            GifImageHelper.processImage(image, 0);
            return image.getFrames().get(0);
        } else if (imageTypeIs(imageType, jpeg)) {
            Image image = new JpegImage(bytes);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            JpegImageHelper.processImage(image, baos);
            image.data = baos.toByteArray();
            return image;
        } else if (imageTypeIs(imageType, jpeg2000_1) || imageTypeIs(imageType, jpeg2000_2)) {
            Image image = new Jpeg2000Image(bytes);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Jpeg2000ImageHelper.processImage(image, baos);
            image.data = baos.toByteArray();
            return image;
        } else if (imageTypeIs(imageType, png)) {
            Image image = new PngImage(bytes);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PngImageHelper.processImage(image, baos);
            image.data = baos.toByteArray();
            return image;
        } else if (imageTypeIs(imageType, bmp)) {
            Image image = new BmpImage(bytes, false, 0);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            BmpImageHelper.processImage(image, baos);
            return image;
        } else if (imageTypeIs(imageType, tiff_1) || imageTypeIs(imageType, tiff_2)) {
            Image image = new TiffImage(bytes, recoverImage, 1, false);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            TiffImageHelper.processImage(image, baos);
            return image;
        } else if (imageTypeIs(imageType, jbig2)) {
            Image image = new Jbig2Image(bytes, 1);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Jbig2ImageHelper.processImage(image, baos);
            image.data = baos.toByteArray();
            return image;
        }
        throw new IOException(IOException.ImageFormatCannotBeRecognized);
    }

    private static boolean imageTypeIs(byte[] imageType, byte[] compareWith) {
        for (int i = 0; i < compareWith.length; i++) {
            if (imageType[i] != compareWith[i])
                return false;
        }
        return true;
    }

    private static <T> byte[] readImageType(T source) {
        InputStream stream = null;
        try {
            if (source instanceof URL) {
                stream = ((URL) source).openStream();
            } else {
                stream = new ByteArrayInputStream((byte[])source);
            }
            byte[] bytes = new byte[8];
            stream.read(bytes);
            return bytes;
        } catch (java.io.IOException e) {
            throw new IOException(IOException.IoException, e);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (java.io.IOException ignored) { }
            }
        }
    }
}
