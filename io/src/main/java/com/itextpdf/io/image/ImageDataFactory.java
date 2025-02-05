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
import com.itextpdf.io.codec.CCITTG4Encoder;
import com.itextpdf.io.codec.TIFFFaxDecoder;
import com.itextpdf.io.exceptions.IoExceptionMessageConstant;
import com.itextpdf.io.util.UrlUtil;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ImageDataFactory {

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
     * @throws MalformedURLException if an error occurred generating the URL.
     */
    public static ImageData create(String filename, boolean recoverImage) throws MalformedURLException {
        return create(UrlUtil.toURL(filename), recoverImage);
    }

    /**
     * Create an ImageData instance representing the image from the specified file.
     * @param filename filename of the file containing the image
     * @return The created ImageData object.
     * @throws MalformedURLException if an error occurred generating the URL.
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
            throw new IOException(IoExceptionMessageConstant.TRANSPARENCY_LENGTH_MUST_BE_EQUAL_TO_2_WITH_CCITT_IMAGES);
        if (typeCCITT != RawImageData.CCITTG4 && typeCCITT != RawImageData.CCITTG3_1D && typeCCITT != RawImageData.CCITTG3_2D)
            throw new IOException(IoExceptionMessageConstant.CCITT_COMPRESSION_TYPE_MUST_BE_CCITTG4_CCITTG3_1D_OR_CCITTG3_2D);
        if (reverseBits)
            TIFFFaxDecoder.reverseBits(data);
        RawImageData image = new RawImageData(data, ImageType.RAW);
        image.setTypeCcitt(typeCCITT);
        image.height = height;
        image.width = width;
        image.colorEncodingComponentsNumber = parameters;
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
            throw new IOException(IoExceptionMessageConstant.TRANSPARENCY_LENGTH_MUST_BE_EQUAL_TO_2_WITH_CCITT_IMAGES);
        if (components == 1 && bpc == 1) {
            byte[] g4 = CCITTG4Encoder.compress(data, width, height);
            return ImageDataFactory.create(width, height, false, RawImageData.CCITTG4, RawImageData.CCITT_BLACKIS1, g4, transparency);
        }
        RawImageData image = new RawImageData(data, ImageType.RAW);
        image.height = height;
        image.width = width;
        if (components != 1 && components != 3 && components != 4)
            throw new IOException(IoExceptionMessageConstant.COMPONENTS_MUST_BE_1_3_OR_4);
        if (bpc != 1 && bpc != 2 && bpc != 4 && bpc != 8)
            throw new IOException(IoExceptionMessageConstant.BITS_PER_COMPONENT_MUST_BE_1_2_4_OR_8);
        image.colorEncodingComponentsNumber = components;
        image.bpc = bpc;
        image.data = data;
        image.transparency = transparency;
        return image;
    }

    // Android-Conversion-Skip-Block-Start (java.awt library isn't available on Android)
    /**
     * Gets an instance of an Image from a java.awt.Image
     *
     * @param image the java.awt.Image to convert
     * @param color if different from <CODE>null</CODE> the transparency pixels are replaced by this color
     * @return RawImage
     * @throws java.io.IOException if an I/O error occurs.
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
     * @throws java.io.IOException if an I/O error occurs.
     */
    public static ImageData create(java.awt.Image image, java.awt.Color color, boolean forceBW) throws java.io.IOException {
        return AwtImageDataFactory.create(image, color, forceBW);
    }
    // Android-Conversion-Skip-Block-End

    /**
     * Get a bitmap ImageData instance from the specified url.
     *
     * @param url location of the image.
     * @param noHeader Whether the image contains a header.
     * @return created ImageData
     */
    public static ImageData createBmp(URL url, boolean noHeader) {
        validateImageType(url, ImageType.BMP);
        final ImageData image = new BmpImageData(url, noHeader);
        BmpImageHelper.processImage(image);
        return image;
    }

    /**
     * Get a bitmap ImageData instance from the provided bytes.
     *
     * @param bytes array containing the raw image data
     * @param noHeader Whether the image contains a header.
     * @return created ImageData
     */
    public static ImageData createBmp(byte[] bytes, boolean noHeader) {
        if (noHeader || ImageTypeDetector.detectImageType(bytes) == ImageType.BMP) {
            ImageData image = new BmpImageData(bytes, noHeader);
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
        validateImageType(bytes, ImageType.GIF);
        GifImageData image = new GifImageData(bytes);
        GifImageHelper.processImage(image);
        return image;
    }

    /**
     * Returns a specified frame of the gif image
     *
     * @param url   url of gif image
     * @param frame number of frame to be returned, 1-based
     * @return GifImageData instance.
     */
    public static ImageData createGifFrame(URL url, int frame) {
        return createGifFrames(url, new int[] {frame}).get(0);
    }

    /**
     * Returns a specified frame of the gif image
     *
     * @param bytes byte array of gif image
     * @param frame number of frame to be returned, 1-based
     * @return GifImageData instance
     */
    public static ImageData createGifFrame(byte[] bytes, int frame) {
        return createGifFrames(bytes, new int[] {frame}).get(0);
    }

    /**
     * Returns <CODE>List</CODE> of gif image frames
     *
     * @param bytes        byte array of gif image
     * @param frameNumbers array of frame numbers of gif image, 1-based
     * @return all frames of gif image
     */
    public static List<ImageData> createGifFrames(byte[] bytes, int[] frameNumbers) {
        validateImageType(bytes, ImageType.GIF);
        GifImageData image = new GifImageData(bytes);
        return processGifImageAndExtractFrames(frameNumbers, image);
    }

    /**
     * Returns <CODE>List</CODE> of gif image frames
     *
     * @param url          url of gif image
     * @param frameNumbers array of frame numbers of gif image, 1-based
     * @return all frames of gif image
     */
    public static List<ImageData> createGifFrames(URL url, int[] frameNumbers) {
        validateImageType(url, ImageType.GIF);
        GifImageData image = new GifImageData(url);
        return processGifImageAndExtractFrames(frameNumbers, image);
    }

    /**
     * Returns <CODE>List</CODE> of gif image frames
     *
     * @param bytes byte array of gif image
     * @return all frames of gif image
     */
    public static List<ImageData> createGifFrames(byte[] bytes) {
        validateImageType(bytes, ImageType.GIF);
        GifImageData image = new GifImageData(bytes);
        GifImageHelper.processImage(image);
        return image.getFrames();
    }

    /**
     * Returns <CODE>List</CODE> of gif image frames
     *
     * @param url url of gif image
     * @return all frames of gif image
     */
    public static List<ImageData> createGifFrames(URL url) {
        validateImageType(url, ImageType.GIF);
        GifImageData image = new GifImageData(url);
        GifImageHelper.processImage(image);
        return image.getFrames();
    }

    public static ImageData createJbig2(URL url, int page) {
        if (page < 1)
            throw new IllegalArgumentException("The page number must be greater than 0");
        validateImageType(url, ImageType.JBIG2);
        ImageData image = new Jbig2ImageData(url, page);
        Jbig2ImageHelper.processImage(image);
        return image;
    }

    public static ImageData createJbig2(byte[] bytes, int page) {
        if (page < 1)
            throw new IllegalArgumentException("The page number must be greater than 0");
        validateImageType(bytes, ImageType.JBIG2);
        ImageData image = new Jbig2ImageData(bytes, page);
        Jbig2ImageHelper.processImage(image);
        return image;

    }

    /**
     * Create an {@link ImageData} instance from a Jpeg image url
     * @param url URL
     * @return the created JPEG image
     */
    public static ImageData createJpeg(URL url) {
        validateImageType(url, ImageType.JPEG);
        ImageData image = new JpegImageData(url);
        JpegImageHelper.processImage(image);
        return image;
    }

    public static ImageData createJpeg(byte[] bytes) {
        validateImageType(bytes, ImageType.JPEG);
        ImageData image = new JpegImageData(bytes);
        JpegImageHelper.processImage(image);
        return image;

    }

    public static ImageData createJpeg2000(URL url) {
        validateImageType(url, ImageType.JPEG2000);
        ImageData image = new Jpeg2000ImageData(url);
        Jpeg2000ImageHelper.processImage(image);
        return image;
    }

    public static ImageData createJpeg2000(byte[] bytes) {
        validateImageType(bytes, ImageType.JPEG2000);
        ImageData image = new Jpeg2000ImageData(bytes);
        Jpeg2000ImageHelper.processImage(image);
        return image;
    }

    public static ImageData createPng(URL url) {
        validateImageType(url, ImageType.PNG);
        ImageData image = new PngImageData(url);
        PngImageHelper.processImage(image);
        return image;
    }

    public static ImageData createPng(byte[] bytes) {
        validateImageType(bytes, ImageType.PNG);
        ImageData image = new PngImageData(bytes);
        PngImageHelper.processImage(image);
        return image;
    }

    public static ImageData createTiff(URL url, boolean recoverFromImageError, int page, boolean direct) {
        validateImageType(url, ImageType.TIFF);
        ImageData image = new TiffImageData(url, recoverFromImageError, page, direct);
        TiffImageHelper.processImage(image);
        return image;
    }

    public static ImageData createTiff(byte[] bytes, boolean recoverFromImageError, int page, boolean direct) {
        validateImageType(bytes, ImageType.TIFF);
        ImageData image = new TiffImageData(bytes, recoverFromImageError, page, direct);
        TiffImageHelper.processImage(image);
        return image;
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
        ImageType imageType = ImageTypeDetector.detectImageType(source);
        return isSupportedType(imageType);
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
        ImageType imageType = ImageTypeDetector.detectImageType(source);
        return isSupportedType(imageType);
    }

    /**
     * Checks if the type of image is supported by factory.
     * <br>
     * <b>Note:</b> if this method returns {@code true} it doesn't means that {@link #create(byte[])} won't throw exception
     *
     * @param imageType image type
     * @return {@code true} if image type is supported and {@code false} otherwise
     */
    public static boolean isSupportedType(ImageType imageType) {
        return imageType == ImageType.GIF || imageType == ImageType.JPEG || imageType == ImageType.JPEG2000
                || imageType == ImageType.PNG || imageType == ImageType.BMP || imageType == ImageType.TIFF
                || imageType == ImageType.JBIG2;
    }

    private static ImageData createImageInstance(URL source, boolean recoverImage) {
        ImageType imageType = ImageTypeDetector.detectImageType(source);
        switch (imageType) {
            case GIF: {
                GifImageData image = new GifImageData(source);
                GifImageHelper.processImage(image, 0);
                return image.getFrames().get(0);
            }
            case JPEG: {
                ImageData image = new JpegImageData(source);
                JpegImageHelper.processImage(image);
                return image;
            }
            case JPEG2000: {
                ImageData image = new Jpeg2000ImageData(source);
                Jpeg2000ImageHelper.processImage(image);
                return image;
            }
            case PNG: {
                ImageData image = new PngImageData(source);
                PngImageHelper.processImage(image);
                return image;
            }
            case BMP: {
                ImageData image = new BmpImageData(source, false);
                BmpImageHelper.processImage(image);
                return image;
            }
            case TIFF: {
                ImageData image = new TiffImageData(source, recoverImage, 1, false);
                TiffImageHelper.processImage(image);
                return image;
            }
            case JBIG2: {
                ImageData image = new Jbig2ImageData(source, 1);
                Jbig2ImageHelper.processImage(image);
                return image;
            }
            default:
                throw new IOException(IoExceptionMessageConstant.IMAGE_FORMAT_CANNOT_BE_RECOGNIZED);
        }
    }

    private static ImageData createImageInstance(byte[] bytes, boolean recoverImage) {
        ImageType imageType = ImageTypeDetector.detectImageType(bytes);
        switch (imageType) {
            case GIF: {
                GifImageData image = new GifImageData(bytes);
                GifImageHelper.processImage(image, 0);
                return image.getFrames().get(0);
            }
            case JPEG: {
                ImageData image = new JpegImageData(bytes);
                JpegImageHelper.processImage(image);
                return image;
            }
            case JPEG2000: {
                ImageData image = new Jpeg2000ImageData(bytes);
                Jpeg2000ImageHelper.processImage(image);
                return image;
            }
            case PNG: {
                ImageData image = new PngImageData(bytes);
                PngImageHelper.processImage(image);
                return image;
            }
            case BMP: {
                ImageData image = new BmpImageData(bytes, false);
                BmpImageHelper.processImage(image);
                return image;
            }
            case TIFF: {
                ImageData image = new TiffImageData(bytes, recoverImage, 1, false);
                TiffImageHelper.processImage(image);
                return image;
            }
            case JBIG2: {
                ImageData image = new Jbig2ImageData(bytes, 1);
                Jbig2ImageHelper.processImage(image);
                return image;
            }
            default:
                throw new IOException(IoExceptionMessageConstant.IMAGE_FORMAT_CANNOT_BE_RECOGNIZED);
        }
    }

    private static List<ImageData> processGifImageAndExtractFrames(int[] frameNumbers, GifImageData image) {
        Arrays.sort(frameNumbers);
        GifImageHelper.processImage(image, frameNumbers[frameNumbers.length - 1] - 1);
        List<ImageData> frames = new ArrayList<>();
        for (int frame : frameNumbers) {
            frames.add(image.getFrames().get(frame - 1));
        }
        return frames;
    }

    private static void validateImageType(byte[] image, ImageType expectedType) {
        ImageType detectedType = ImageTypeDetector.detectImageType(image);
        if (detectedType != expectedType) {
            throw new IllegalArgumentException(expectedType.name() +
                    " image expected. Detected image type: " + detectedType.name());
        }
    }

    private static void validateImageType(URL imageUrl, ImageType expectedType) {
        ImageType detectedType = ImageTypeDetector.detectImageType(imageUrl);
        if (detectedType != expectedType) {
            throw new IllegalArgumentException(expectedType.name() +
                    " image expected. Detected image type: " + detectedType.name());
        }
    }
}
