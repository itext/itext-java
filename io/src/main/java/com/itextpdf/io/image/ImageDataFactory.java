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
import com.itextpdf.io.util.UrlUtil;
import com.itextpdf.io.codec.CCITTG4Encoder;
import com.itextpdf.io.codec.TIFFFaxDecoder;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ImageDataFactory {

    private static final byte[] gif = new byte[]{(byte) 'G', (byte) 'I', (byte) 'F'};
    private static final byte[] jpeg = new byte[]{(byte) 0xFF, (byte) 0xD8};
    private static final byte[] jpeg2000_1 = new byte[]{0x00, 0x00, 0x00, 0x0c};
    private static final byte[] jpeg2000_2 = new byte[]{(byte) 0xff, (byte) 0x4f, (byte) 0xff, 0x51};
    private static final byte[] png = new byte[]{(byte) 137, 80, 78, 71};
    private static final byte[] wmf = new byte[]{(byte) 0xD7, (byte) 0xCD};
    private static final byte[] bmp = new byte[]{(byte) 'B', (byte) 'M'};
    private static final byte[] tiff_1 = new byte[]{(byte) 'M', (byte) 'M', 0, 42};
    private static final byte[] tiff_2 = new byte[]{(byte) 'I', (byte) 'I', 42, 0};
    private static final byte[] jbig2 = new byte[]{(byte) 0x97, (byte) 'J', (byte) 'B', (byte) '2', (byte) '\r', (byte) '\n', 0x1a, (byte) '\n'};

    private ImageDataFactory() {
    }

    /**
     * Create an ImageData instance representing the image from the image bytes.
     * @param bytes byte representation of the image.
     * @param recoverImage whether to recover from a image error (for TIFF-images)
     * @return The created ImageData object.
     */
    public static ImageData create(byte[] bytes, boolean recoverImage) {
        return createImageInstance(bytes, recoverImage);
    }

    /**
     * Create an ImageData instance representing the image from the image bytes.
     * @param bytes byte representation of the image.
     * @return The created ImageData object.
     */
    public static ImageData create(byte[] bytes) {
        return create(bytes, false);
    }

    /**
     * Create an ImageData instance representing the image from the file located at the specified url.
     * @param url location of the image
     * @param recoverImage whether to recover from a image error (for TIFF-images)
     * @return The created ImageData object.
     */
    public static ImageData create(URL url, boolean recoverImage) {
        return createImageInstance(url, recoverImage);
    }

    /**
     * Create an ImageData instance representing the image from the file located at the specified url.
     * @param url location of the image
     * @return The created ImageData object.
     */
    public static ImageData create(URL url) {
        return create(url, false);
    }

    /**
     * Create an ImageData instance representing the image from the specified file.
     * @param filename filename of the file containing the image
     * @param recoverImage whether to recover from a image error (for TIFF-images)
     * @return The created ImageData object.
     * @throws MalformedURLException
     */
    public static ImageData create(String filename, boolean recoverImage) throws MalformedURLException {
        return create(UrlUtil.toURL(filename), recoverImage);
    }

    /**
     * Create an ImageData instance representing the image from the specified file.
     * @param filename filename of the file containing the image
     * @return The created ImageData object.
     * @throws MalformedURLException
     */
    public static ImageData create(String filename) throws MalformedURLException {
        return create(filename, false);
    }

    /**
     * Create an ImageData instance from the passed parameters.
     *
     * @param width width of the image in pixels
     * @param height height of the image in pixels
     * @param reverseBits whether to reverse the bits stored in data (TIFF images).
     * @param typeCCITT Type of CCITT encoding
     * @param parameters colour space parameters
     * @param data array containing raw image data
     * @param transparency array containing transparency information
     * @return created ImageData object.
     */
    public static ImageData create(int width, int height, boolean reverseBits,
                                   int typeCCITT, int parameters, byte[] data,
                                   int[] transparency) {
        if (transparency != null && transparency.length != 2)
            throw new IOException(IOException.TransparencyLengthMustBeEqualTo2WithCcittImages);
        if (typeCCITT != RawImageData.CCITTG4 && typeCCITT != RawImageData.CCITTG3_1D && typeCCITT != RawImageData.CCITTG3_2D)
            throw new IOException(IOException.CcittCompressionTypeMustBeCcittg4Ccittg3_1dOrCcittg3_2d);
        if (reverseBits)
            TIFFFaxDecoder.reverseBits(data);
        RawImageData image = new RawImageData(data, ImageType.RAW);
        image.setTypeCcitt(typeCCITT);
        image.height = height;
        image.width = width;
        image.colorSpace = parameters;
        image.transparency = transparency;
        return image;
    }

    /**
     * Create an ImageData instance from the passed parameters.
     *
     * @param width width of the image in pixels
     * @param height height of the image in pixels
     * @param components colour space components
     * @param bpc bits per colour.
     * @param data array containing raw image data
     * @param transparency array containing transparency information
     * @return created ImageData object.
     */
    public static ImageData create(int width, int height, int components,
                                   int bpc, byte[] data, int[] transparency) {
        if (transparency != null && transparency.length != components * 2)
            throw new IOException(IOException.TransparencyLengthMustBeEqualTo2WithCcittImages);
        if (components == 1 && bpc == 1) {
            byte[] g4 = CCITTG4Encoder.compress(data, width, height);
            return ImageDataFactory.create(width, height, false, RawImageData.CCITTG4, RawImageData.CCITT_BLACKIS1, g4, transparency);
        }
        RawImageData image = new RawImageData(data, ImageType.RAW);
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
     *
     * @param image the java.awt.Image to convert
     * @param color if different from <CODE>null</CODE> the transparency pixels are replaced by this color
     * @return RawImage
     */
    public static ImageData create(java.awt.Image image, java.awt.Color color) throws java.io.IOException {
        return ImageDataFactory.create(image, color, false);
    }

    /**
     * Gets an instance of an Image from a java.awt.Image.
     *
     * @param image   the <CODE>java.awt.Image</CODE> to convert
     * @param color   if different from <CODE>null</CODE> the transparency pixels are replaced by this color
     * @param forceBW if <CODE>true</CODE> the image is treated as black and white
     * @return RawImage
     */
    public static ImageData create(java.awt.Image image, java.awt.Color color, boolean forceBW) throws java.io.IOException {
        return AwtImageDataFactory.create(image, color, forceBW);
    }

    /**
     * Get a bitmap ImageData instance from the specified url.
     *
     * @param url location of the image.
     * @param noHeader Whether the image contains a header.
     * @param size size of the image
     * @return created ImageData.
     */
    public static ImageData createBmp(URL url, boolean noHeader, int size) {
        byte[] imageType = readImageType(url);
        if (imageTypeIs(imageType, bmp)) {
            ImageData image = new BmpImageData(url, noHeader, size);
            BmpImageHelper.processImage(image);
            return image;
        }
        throw new IllegalArgumentException("BMP image expected.");
    }

    /**
     * Get a bitmap ImageData instance from the provided bytes.
     *
     * @param bytes array containing the raw image data
     * @param noHeader Whether the image contains a header.
     * @param size size of the image
     * @return created ImageData.
     */
    public static ImageData createBmp(byte[] bytes, boolean noHeader, int size) {
        byte[] imageType = readImageType(bytes);
        if (noHeader || imageTypeIs(imageType, bmp)) {
            ImageData image = new BmpImageData(bytes, noHeader, size);
            BmpImageHelper.processImage(image);
            return image;
        }
        throw new IllegalArgumentException("BMP image expected.");
    }

    /**
     * Return a GifImage object. This object cannot be added to a document
     *
     * @param bytes array containing the raw image data
     * @return GifImageData instance.
     */
    public static GifImageData createGif(byte[] bytes) {
        byte[] imageType = readImageType(bytes);
        if (imageTypeIs(imageType, gif)) {
            GifImageData image = new GifImageData(bytes);
            GifImageHelper.processImage(image);
            return image;
        }
        throw new IllegalArgumentException("GIF image expected.");
    }

    /**
     * Returns a specified frame of the gif image
     *
     * @param url   url of gif image
     * @param frame number of frame to be returned
     * @return GifImageData instance.
     */
    public static ImageData createGifFrame(URL url, int frame) {
        byte[] imageType = readImageType(url);
        if (imageTypeIs(imageType, gif)) {
            GifImageData image = new GifImageData(url);
            GifImageHelper.processImage(image, frame - 1);
            return image.getFrames().get(frame - 1);
        }
        throw new IllegalArgumentException("GIF image expected.");
    }

    /**
     * Returns a specified frame of the gif image
     *
     * @param bytes byte array of gif image
     * @param frame number of frame to be returned
     * @return GifImageData instance
     */
    public static ImageData createGifFrame(byte[] bytes, int frame) {
        byte[] imageType = readImageType(bytes);
        if (imageTypeIs(imageType, gif)) {
            GifImageData image = new GifImageData(bytes);
            GifImageHelper.processImage(image, frame - 1);
            return image.getFrames().get(frame - 1);
        }
        throw new IllegalArgumentException("GIF image expected.");
    }

    /**
     * Returns <CODE>List</CODE> of gif image frames
     *
     * @param bytes        byte array of gif image
     * @param frameNumbers array of frame numbers of gif image
     * @return all frames of gif image
     */
    public static List<ImageData> createGifFrames(byte[] bytes, int[] frameNumbers) {
        byte[] imageType = readImageType(bytes);
        if (imageTypeIs(imageType, gif)) {
            GifImageData image = new GifImageData(bytes);
            Arrays.sort(frameNumbers);
            GifImageHelper.processImage(image, frameNumbers[frameNumbers.length - 1] - 1);
            List<ImageData> frames = new ArrayList<>();
            for (int frame : frameNumbers) {
                frames.add(image.getFrames().get(frame - 1));
            }
            return frames;
        }
        throw new IllegalArgumentException("GIF image expected.");
    }

    /**
     * Returns <CODE>List</CODE> of gif image frames
     *
     * @param url          url of gif image
     * @param frameNumbers array of frame numbers of gif image
     * @return all frames of gif image
     */
    public static List<ImageData> createGifFrames(URL url, int[] frameNumbers) {
        byte[] imageType = readImageType(url);
        if (imageTypeIs(imageType, gif)) {
            GifImageData image = new GifImageData(url);
            Arrays.sort(frameNumbers);
            GifImageHelper.processImage(image, frameNumbers[frameNumbers.length - 1] - 1);
            List<ImageData> frames = new ArrayList<>();
            for (int frame : frameNumbers) {
                frames.add(image.getFrames().get(frame - 1));
            }
            return frames;
        }
        throw new IllegalArgumentException("GIF image expected.");
    }

    /**
     * Returns <CODE>List</CODE> of gif image frames
     *
     * @param bytes byte array of gif image
     * @return all frames of gif image
     */
    public static List<ImageData> createGifFrames(byte[] bytes) {
        byte[] imageType = readImageType(bytes);
        if (imageTypeIs(imageType, gif)) {
            GifImageData image = new GifImageData(bytes);
            GifImageHelper.processImage(image);
            return image.getFrames();
        }
        throw new IllegalArgumentException("GIF image expected.");
    }

    /**
     * Returns <CODE>List</CODE> of gif image frames
     *
     * @param url url of gif image
     * @return all frames of gif image
     */
    public static List<ImageData> createGifFrames(URL url) {
        byte[] imageType = readImageType(url);
        if (imageTypeIs(imageType, gif)) {
            GifImageData image = new GifImageData(url);
            GifImageHelper.processImage(image);
            return image.getFrames();
        }
        throw new IllegalArgumentException("GIF image expected.");
    }

    public static ImageData createJbig2(URL url, int page) {
        if (page < 1)
            throw new IllegalArgumentException("The page number must be greater than 0");
        byte[] imageType = readImageType(url);
        if (imageTypeIs(imageType, jbig2)) {
            ImageData image = new Jbig2ImageData(url, page);
            Jbig2ImageHelper.processImage(image);
            return image;
        }
        throw new IllegalArgumentException("JBIG2 image expected.");
    }

    public static ImageData createJbig2(byte[] bytes, int page) {
        if (page < 1)
            throw new IllegalArgumentException("The page number must be greater than 0");
        byte[] imageType = readImageType(bytes);
        if (imageTypeIs(imageType, jbig2)) {
            ImageData image = new Jbig2ImageData(bytes, page);
            Jbig2ImageHelper.processImage(image);
            return image;
        }
        throw new IllegalArgumentException("JBIG2 image expected.");

    }

    /**
     * Create a ImageData instance from a Jpeg image url
     * @param url
     */
    public static ImageData createJpeg(URL url) {
        byte[] imageType = readImageType(url);
        if (imageTypeIs(imageType, jpeg)) {
            ImageData image = new JpegImageData(url);
            JpegImageHelper.processImage(image);
            return image;
        }
        throw new IllegalArgumentException("JPEG image expected.");
    }

    public static ImageData createJpeg(byte[] bytes) {
        byte[] imageType = readImageType(bytes);
        if (imageTypeIs(imageType, jpeg)) {
            ImageData image = new JpegImageData(bytes);
            JpegImageHelper.processImage(image);
            return image;
        }
        throw new IllegalArgumentException("JPEG image expected.");

    }

    public static ImageData createJpeg2000(URL url) {
        byte[] imageType = readImageType(url);
        if (imageTypeIs(imageType, jpeg2000_1) || imageTypeIs(imageType, jpeg2000_2)) {
            ImageData image = new Jpeg2000ImageData(url);
            Jpeg2000ImageHelper.processImage(image);
            return image;
        }
        throw new IllegalArgumentException("JPEG2000 image expected.");
    }

    public static ImageData createJpeg2000(byte[] bytes) {
        byte[] imageType = readImageType(bytes);
        if (imageTypeIs(imageType, jpeg2000_1) || imageTypeIs(imageType, jpeg2000_2)) {
            ImageData image = new Jpeg2000ImageData(bytes);
            Jpeg2000ImageHelper.processImage(image);
            return image;
        }
        throw new IllegalArgumentException("JPEG2000 image expected.");

    }

    public static ImageData createPng(URL url) {
        byte[] imageType = readImageType(url);
        if (imageTypeIs(imageType, png)) {
            ImageData image = new PngImageData(url);
            PngImageHelper.processImage(image);
            return image;
        }
        throw new IllegalArgumentException("PNG image expected.");
    }

    public static ImageData createPng(byte[] bytes) {
        byte[] imageType = readImageType(bytes);
        if (imageTypeIs(imageType, png)) {
            ImageData image = new PngImageData(bytes);
            PngImageHelper.processImage(image);
            return image;
        }
        throw new IllegalArgumentException("PNG image expected.");
    }

    public static ImageData createTiff(URL url, boolean recoverFromImageError, int page, boolean direct) {
        byte[] imageType = readImageType(url);
        if (imageTypeIs(imageType, tiff_1) || imageTypeIs(imageType, tiff_2)) {
            ImageData image = new TiffImageData(url, recoverFromImageError, page, direct);
            TiffImageHelper.processImage(image);
            return image;
        }
        throw new IllegalArgumentException("TIFF image expected.");
    }

    public static ImageData createTiff(byte[] bytes, boolean recoverFromImageError, int page, boolean direct) {
        byte[] imageType = readImageType(bytes);
        if (imageTypeIs(imageType, tiff_1) || imageTypeIs(imageType, tiff_2)) {
            ImageData image = new TiffImageData(bytes, recoverFromImageError, page, direct);
            TiffImageHelper.processImage(image);
            return image;
        }
        throw new IllegalArgumentException("TIFF image expected.");
    }

    public static ImageData createRawImage(byte[] bytes) {
        return new RawImageData(bytes, ImageType.RAW);
    }

    /**
     * Checks if the type of image (based on first 8 bytes) is supported by factory.
     * <br>
     * <b>Note:</b> if this method returns {@code true} it doesn't means that {@link #create(byte[])} won't throw exception
     *
     * @param source image raw bytes
     * @return {@code true} if first eight bytes are recognised by factory as valid image type and {@code false} otherwise
     */
    public static boolean isSupportedType(byte[] source) {
        if (source == null) {
            return false;
        }
        byte[] imageType = readImageType(source);
        return imageTypeIs(imageType, gif) || imageTypeIs(imageType, jpeg) || imageTypeIs(imageType, jpeg2000_1)
                || imageTypeIs(imageType, jpeg2000_2) || imageTypeIs(imageType, png) || imageTypeIs(imageType, bmp)
                || imageTypeIs(imageType, tiff_1) || imageTypeIs(imageType, tiff_2) || imageTypeIs(imageType, jbig2);
    }

    /**
     * Checks if the type of image (based on first 8 bytes) is supported by factory.
     * <br>
     * <b>Note:</b> if this method returns {@code true} it doesn't means that {@link #create(byte[])} won't throw exception
     *
     * @param source image URL
     * @return {@code true} if first eight bytes are recognised by factory as valid image type and {@code false} otherwise
     */
    public static boolean isSupportedType(URL source) {
        if (source == null) {
            return false;
        }
        byte[] imageType = readImageType(source);
        return imageTypeIs(imageType, gif) || imageTypeIs(imageType, jpeg) || imageTypeIs(imageType, jpeg2000_1)
                || imageTypeIs(imageType, jpeg2000_2) || imageTypeIs(imageType, png) || imageTypeIs(imageType, bmp)
                || imageTypeIs(imageType, tiff_1) || imageTypeIs(imageType, tiff_2) || imageTypeIs(imageType, jbig2);
    }

    private static ImageData createImageInstance(URL source, boolean recoverImage) {
        byte[] imageType = readImageType(source);
        if (imageTypeIs(imageType, gif)) {
            GifImageData image = new GifImageData(source);
            GifImageHelper.processImage(image, 0);
            return image.getFrames().get(0);
        } else if (imageTypeIs(imageType, jpeg)) {
            ImageData image = new JpegImageData(source);
            JpegImageHelper.processImage(image);
            return image;
        } else if (imageTypeIs(imageType, jpeg2000_1) || imageTypeIs(imageType, jpeg2000_2)) {
            ImageData image = new Jpeg2000ImageData(source);
            Jpeg2000ImageHelper.processImage(image);
            return image;
        } else if (imageTypeIs(imageType, png)) {
            ImageData image = new PngImageData(source);
            PngImageHelper.processImage(image);
            return image;
        } else if (imageTypeIs(imageType, bmp)) {
            ImageData image = new BmpImageData(source, false, 0);
            BmpImageHelper.processImage(image);
            return image;
        } else if (imageTypeIs(imageType, tiff_1) || imageTypeIs(imageType, tiff_2)) {
            ImageData image = new TiffImageData(source, recoverImage, 1, false);
            TiffImageHelper.processImage(image);
            return image;
        } else if (imageTypeIs(imageType, jbig2)) {
            ImageData image = new Jbig2ImageData(source, 1);
            Jbig2ImageHelper.processImage(image);
            return image;
        }
        throw new IOException(IOException.ImageFormatCannotBeRecognized);
    }

    private static ImageData createImageInstance(byte[] bytes, boolean recoverImage) {
        byte[] imageType = readImageType(bytes);
        if (imageTypeIs(imageType, gif)) {
            GifImageData image = new GifImageData(bytes);
            GifImageHelper.processImage(image, 0);
            return image.getFrames().get(0);
        } else if (imageTypeIs(imageType, jpeg)) {
            ImageData image = new JpegImageData(bytes);
            JpegImageHelper.processImage(image);
            return image;
        } else if (imageTypeIs(imageType, jpeg2000_1) || imageTypeIs(imageType, jpeg2000_2)) {
            ImageData image = new Jpeg2000ImageData(bytes);
            Jpeg2000ImageHelper.processImage(image);
            return image;
        } else if (imageTypeIs(imageType, png)) {
            ImageData image = new PngImageData(bytes);
            PngImageHelper.processImage(image);
            return image;
        } else if (imageTypeIs(imageType, bmp)) {
            ImageData image = new BmpImageData(bytes, false, 0);
            BmpImageHelper.processImage(image);
            return image;
        } else if (imageTypeIs(imageType, tiff_1) || imageTypeIs(imageType, tiff_2)) {
            ImageData image = new TiffImageData(bytes, recoverImage, 1, false);
            TiffImageHelper.processImage(image);
            return image;
        } else if (imageTypeIs(imageType, jbig2)) {
            ImageData image = new Jbig2ImageData(bytes, 1);
            Jbig2ImageHelper.processImage(image);
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

    private static byte[] readImageType(URL source) {
        InputStream stream = null;
        try {
            stream = UrlUtil.openStream(source);
            byte[] bytes = new byte[8];
            stream.read(bytes);
            return bytes;
        } catch (java.io.IOException e) {
            throw new IOException(IOException.IoException, e);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (java.io.IOException ignored) {
                }
            }
        }
    }

    private static byte[] readImageType(byte[] source) {
        try {
            InputStream stream = new ByteArrayInputStream(source);
            byte[] bytes = new byte[8];
            stream.read(bytes);
            return bytes;
        } catch (java.io.IOException e) {
            return null;
        }
    }
}
