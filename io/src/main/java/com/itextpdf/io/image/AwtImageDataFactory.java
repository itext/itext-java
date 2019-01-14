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

import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.PixelGrabber;

class AwtImageDataFactory {
    /**
     * Gets an instance of an Image from a java.awt.Image
     * @param image the java.awt.Image to convert
     * @param color if different from <CODE>null</CODE> the transparency pixels are replaced by this color
     * @return RawImage
     */
    public static ImageData create(java.awt.Image image, java.awt.Color color) throws java.io.IOException {
        return AwtImageDataFactory.create(image, color, false);
    }

    /**
     * Gets an instance of an Image from a java.awt.Image.
     * @param image the <CODE>java.awt.Image</CODE> to convert
     * @param color if different from <CODE>null</CODE> the transparency pixels are replaced by this color
     * @param forceBW if <CODE>true</CODE> the image is treated as black and white
     * @return RawImage
     */
    public static ImageData create(java.awt.Image image, java.awt.Color color, boolean forceBW) throws java.io.IOException {
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
            int[] transparency = null;
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
            return ImageDataFactory.create(w, h, 1, 1, pixelsByte, transparency);
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
            int[] transparency = null;
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
                                // Added by Michael Klink
                                // Check whether this value for transparent pixels
                                // has already been used for a non-transparent one
                                // before this position
                                for (int prevPixel = 0; prevPixel < j; prevPixel++) {
                                    if ((pixels[prevPixel] & 0xffffff) == transparentPixel) {
                                        // found a prior use of the transparentPixel color
                                        // and, therefore, cannot make use of this color
                                        // for transparency; we could still use an image
                                        // mask but for simplicity let's use a soft mask
                                        // which already is implemented here
                                        shades = true;
                                        break;
                                    }
                                }
                            }
                        } else if (((pixels[j] & 0xffffff) != transparentPixel) && (alpha == 0)) {
                            shades = true;
                        } else if (((pixels[j] & 0xffffff) == transparentPixel) && (alpha != 0)) {
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
            ImageData img = ImageDataFactory.create(w, h, 3, 8, pixelsByte, transparency);
            if (smask != null) {
                ImageData sm = ImageDataFactory.create(w, h, 1, 8, smask, null);
                sm.makeMask();
                img.setImageMask(sm);
            }
            return img;
        }
    }

}
