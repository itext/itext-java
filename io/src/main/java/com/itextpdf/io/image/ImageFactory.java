/*
    $Id$

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV
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
        return getImage(UrlUtil.toURL(filename), recoverImage);
    }

    public static Image getImage(String filename) throws MalformedURLException {
        return getImage(filename, false);
    }

    public static Image getImage(int width, int height, boolean reverseBits,
                                 int typeCCITT, int parameters, byte[] data,
                                 int[] transparency) {
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

    public static Image getImage(int width, int height, int components,
                                 int bpc, byte[] data, int[] transparency) {
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
     *
     * @param image the java.awt.Image to convert
     * @param color if different from <CODE>null</CODE> the transparency pixels are replaced by this color
     * @return RawImage
     */
    public static Image getImage(java.awt.Image image, java.awt.Color color) throws java.io.IOException {
        return ImageFactory.getImage(image, color, false);
    }

    /**
     * Gets an instance of an Image from a java.awt.Image.
     *
     * @param image   the <CODE>java.awt.Image</CODE> to convert
     * @param color   if different from <CODE>null</CODE> the transparency pixels are replaced by this color
     * @param forceBW if <CODE>true</CODE> the image is treated as black and white
     * @return RawImage
     */
    public static Image getImage(java.awt.Image image, java.awt.Color color, boolean forceBW) throws java.io.IOException {
        return AwtImageFactory.getImage(image, color, forceBW);
    }

    public static Image getBmpImage(URL url, boolean noHeader, int size) {
        byte[] imageType = readImageType(url);
        if (imageTypeIs(imageType, bmp)) {
            Image image = new BmpImage(url, noHeader, size);
            BmpImageHelper.processImage(image);
            return image;
        }
        throw new IllegalArgumentException("BMP image expected.");
    }

    public static Image getBmpImage(byte[] bytes, boolean noHeader, int size) {
        byte[] imageType = readImageType(bytes);
        if (noHeader || imageTypeIs(imageType, bmp)) {
            Image image = new BmpImage(bytes, noHeader, size);
            BmpImageHelper.processImage(image);
            return image;
        }
        throw new IllegalArgumentException("BMP image expected.");
    }

    /**
     * Return a GifImage object. This object cannot be added to a document
     *
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
     *
     * @param url   url of gif image
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
     *
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
     *
     * @param bytes        byte array of gif image
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
     *
     * @param url          url of gif image
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
     *
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
     *
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
            Jbig2ImageHelper.processImage(image);
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
            Jbig2ImageHelper.processImage(image);
            return image;
        }
        throw new IllegalArgumentException("JBIG2 image expected.");

    }

    public static Image getJpegImage(URL url) {
        byte[] imageType = readImageType(url);
        if (imageTypeIs(imageType, jpeg)) {
            Image image = new JpegImage(url);
            JpegImageHelper.processImage(image);
            return image;
        }
        throw new IllegalArgumentException("JPEG image expected.");
    }

    public static Image getJpegImage(byte[] bytes) {
        byte[] imageType = readImageType(bytes);
        if (imageTypeIs(imageType, jpeg)) {
            Image image = new JpegImage(bytes);
            JpegImageHelper.processImage(image);
            return image;
        }
        throw new IllegalArgumentException("JPEG image expected.");

    }

    public static Image getJpeg2000Image(URL url) {
        byte[] imageType = readImageType(url);
        if (imageTypeIs(imageType, jpeg2000_1) || imageTypeIs(imageType, jpeg2000_2)) {
            Image image = new Jpeg2000Image(url);
            Jpeg2000ImageHelper.processImage(image);
            return image;
        }
        throw new IllegalArgumentException("JPEG2000 image expected.");
    }

    public static Image getJpeg2000Image(byte[] bytes) {
        byte[] imageType = readImageType(bytes);
        if (imageTypeIs(imageType, jpeg2000_1) || imageTypeIs(imageType, jpeg2000_2)) {
            Image image = new Jpeg2000Image(bytes);
            Jpeg2000ImageHelper.processImage(image);
            return image;
        }
        throw new IllegalArgumentException("JPEG2000 image expected.");

    }

    public static Image getPngImage(URL url) {
        byte[] imageType = readImageType(url);
        if (imageTypeIs(imageType, png)) {
            Image image = new PngImage(url);
            PngImageHelper.processImage(image);
            return image;
        }
        throw new IllegalArgumentException("PNG image expected.");
    }

    public static Image getPngImage(byte[] bytes) {
        byte[] imageType = readImageType(bytes);
        if (imageTypeIs(imageType, png)) {
            Image image = new PngImage(bytes);
            PngImageHelper.processImage(image);
            return image;
        }
        throw new IllegalArgumentException("PNG image expected.");
    }

    public static Image getTiffImage(URL url, boolean recoverFromImageError, int page, boolean direct) {
        byte[] imageType = readImageType(url);
        if (imageTypeIs(imageType, tiff_1) || imageTypeIs(imageType, tiff_2)) {
            Image image = new TiffImage(url, recoverFromImageError, page, direct);
            TiffImageHelper.processImage(image);
            return image;
        }
        throw new IllegalArgumentException("TIFF image expected.");
    }

    public static Image getTiffImage(byte[] bytes, boolean recoverFromImageError, int page, boolean direct) {
        byte[] imageType = readImageType(bytes);
        if (imageTypeIs(imageType, tiff_1) || imageTypeIs(imageType, tiff_2)) {
            Image image = new TiffImage(bytes, recoverFromImageError, page, direct);
            TiffImageHelper.processImage(image);
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
            JpegImageHelper.processImage(image);
            return image;
        } else if (imageTypeIs(imageType, jpeg2000_1) || imageTypeIs(imageType, jpeg2000_2)) {
            Image image = new Jpeg2000Image(source);
            Jpeg2000ImageHelper.processImage(image);
            return image;
        } else if (imageTypeIs(imageType, png)) {
            Image image = new PngImage(source);
            PngImageHelper.processImage(image);
            return image;
        } else if (imageTypeIs(imageType, bmp)) {
            Image image = new BmpImage(source, false, 0);
            BmpImageHelper.processImage(image);
            return image;
        } else if (imageTypeIs(imageType, tiff_1) || imageTypeIs(imageType, tiff_2)) {
            Image image = new TiffImage(source, recoverImage, 1, false);
            TiffImageHelper.processImage(image);
            return image;
        } else if (imageTypeIs(imageType, jbig2)) {
            Image image = new Jbig2Image(source, 1);
            Jbig2ImageHelper.processImage(image);
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
            JpegImageHelper.processImage(image);
            return image;
        } else if (imageTypeIs(imageType, jpeg2000_1) || imageTypeIs(imageType, jpeg2000_2)) {
            Image image = new Jpeg2000Image(bytes);
            Jpeg2000ImageHelper.processImage(image);
            return image;
        } else if (imageTypeIs(imageType, png)) {
            Image image = new PngImage(bytes);
            PngImageHelper.processImage(image);
            return image;
        } else if (imageTypeIs(imageType, bmp)) {
            Image image = new BmpImage(bytes, false, 0);
            BmpImageHelper.processImage(image);
            return image;
        } else if (imageTypeIs(imageType, tiff_1) || imageTypeIs(imageType, tiff_2)) {
            Image image = new TiffImage(bytes, recoverImage, 1, false);
            TiffImageHelper.processImage(image);
            return image;
        } else if (imageTypeIs(imageType, jbig2)) {
            Image image = new Jbig2Image(bytes, 1);
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
