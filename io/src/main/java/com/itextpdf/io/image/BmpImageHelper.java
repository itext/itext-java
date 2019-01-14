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
import com.itextpdf.io.font.PdfEncodings;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

final class BmpImageHelper {

    private static class BmpParameters {
        public BmpParameters(BmpImageData image) {
            this.image = image;
        }

        BmpImageData image;
        int width;
        int height;
        Map<String, Object> additional;

        // BMP variables
        InputStream inputStream;
        long bitmapFileSize;
        long bitmapOffset;
        long compression;
        long imageSize;
        byte[] palette;
        int imageType;
        int numBands;
        boolean isBottomUp;
        int bitsPerPixel;
        int redMask, greenMask, blueMask, alphaMask;
        Map<String, Object> properties = new HashMap<>();
        long xPelsPerMeter;
        long yPelsPerMeter;
    }

    // BMP Image types
    private static final int VERSION_2_1_BIT = 0;
    private static final int VERSION_2_4_BIT = 1;
    private static final int VERSION_2_8_BIT = 2;
    private static final int VERSION_2_24_BIT = 3;

    private static final int VERSION_3_1_BIT = 4;
    private static final int VERSION_3_4_BIT = 5;
    private static final int VERSION_3_8_BIT = 6;
    private static final int VERSION_3_24_BIT = 7;

    private static final int VERSION_3_NT_16_BIT = 8;
    private static final int VERSION_3_NT_32_BIT = 9;

    private static final int VERSION_4_1_BIT = 10;
    private static final int VERSION_4_4_BIT = 11;
    private static final int VERSION_4_8_BIT = 12;
    private static final int VERSION_4_16_BIT = 13;
    private static final int VERSION_4_24_BIT = 14;
    private static final int VERSION_4_32_BIT = 15;

    // Color space types
    private static final int LCS_CALIBRATED_RGB = 0;
    private static final int LCS_SRGB = 1;
    private static final int LCS_CMYK = 2;

    // Compression Types
    private static final int BI_RGB = 0;
    private static final int BI_RLE8 = 1;
    private static final int BI_RLE4 = 2;
    private static final int BI_BITFIELDS = 3;

    /**
     * Process the passed Image data as a BMP image.
     * Image is loaded and all image attributes are initialized and/or updated
     * @param image the image to process as a BMP image
     */
    public static void processImage(ImageData image) {
        if (image.getOriginalType() != ImageType.BMP)
            throw new IllegalArgumentException("BMP image expected");
        BmpParameters bmp;
        InputStream bmpStream;
        try {
            if (image.getData() == null) {
                image.loadData();
            }
            bmpStream = new ByteArrayInputStream(image.getData());
            image.imageSize = image.getData().length;
            bmp = new BmpParameters((BmpImageData)image);
            process(bmp, bmpStream);
            if (getImage(bmp)) {
                image.setWidth(bmp.width);
                image.setHeight(bmp.height);
                image.setDpi((int) (bmp.xPelsPerMeter * 0.0254d + 0.5d), (int) (bmp.yPelsPerMeter * 0.0254d + 0.5d));
            }
        } catch (java.io.IOException e){
            throw new IOException(IOException.BmpImageException, e);
        }
        RawImageHelper.updateImageAttributes(bmp.image, bmp.additional);
    }

    private static void process(BmpParameters bmp, InputStream stream) throws java.io.IOException {
        bmp.inputStream = stream;
        if (!bmp.image.isNoHeader()) {
            // Start File Header
            if (!(readUnsignedByte(bmp.inputStream) == 'B' &&
                    readUnsignedByte(bmp.inputStream) == 'M')) {
                throw new IOException(IOException.InvalidMagicValueForBmpFileMustBeBM);
            }

            // Read file size
            bmp.bitmapFileSize = readDWord(bmp.inputStream);

            // Read the two reserved fields
            readWord(bmp.inputStream);
            readWord(bmp.inputStream);

            // Offset to the bitmap from the beginning
            bmp.bitmapOffset = readDWord(bmp.inputStream);

            // End File Header
        }
        // Start BitmapCoreHeader
        long size = readDWord(bmp.inputStream);

        if (size == 12) {
            bmp.width = readWord(bmp.inputStream);
            bmp.height = readWord(bmp.inputStream);
        } else {
            bmp.width = readLong(bmp.inputStream);
            bmp.height = readLong(bmp.inputStream);
        }

        int planes = readWord(bmp.inputStream);
        bmp.bitsPerPixel = readWord(bmp.inputStream);

        bmp.properties.put("color_planes", planes);
        bmp.properties.put("bits_per_pixel", bmp.bitsPerPixel);

        // As BMP always has 3 rgb bands, except for Version 5,
        // which is bgra
        bmp.numBands = 3;
        if (bmp.bitmapOffset == 0)
            bmp.bitmapOffset = size;
        if (size == 12) {
            // Windows 2.x and OS/2 1.x
            bmp.properties.put("bmp_version", "BMP v. 2.x");

            // Classify the image type
            if (bmp.bitsPerPixel == 1) {
                bmp.imageType = VERSION_2_1_BIT;
            } else if (bmp.bitsPerPixel == 4) {
                bmp.imageType = VERSION_2_4_BIT;
            } else if (bmp.bitsPerPixel == 8) {
                bmp.imageType = VERSION_2_8_BIT;
            } else if (bmp.bitsPerPixel == 24) {
                bmp.imageType = VERSION_2_24_BIT;
            }

            // Read in the palette
            int numberOfEntries = (int) ((bmp.bitmapOffset - 14 - size) / 3);
            int sizeOfPalette = numberOfEntries * 3;
            if (bmp.bitmapOffset == size) {
                switch (bmp.imageType) {
                    case VERSION_2_1_BIT:
                        sizeOfPalette = 2 * 3;
                        break;
                    case VERSION_2_4_BIT:
                        sizeOfPalette = 16 * 3;
                        break;
                    case VERSION_2_8_BIT:
                        sizeOfPalette = 256 * 3;
                        break;
                    case VERSION_2_24_BIT:
                        sizeOfPalette = 0;
                        break;
                }
                bmp.bitmapOffset = size + sizeOfPalette;
            }
            readPalette(sizeOfPalette, bmp);
        } else {
            bmp.compression = readDWord(bmp.inputStream);
            bmp.imageSize = readDWord(bmp.inputStream);
            bmp.xPelsPerMeter = readLong(bmp.inputStream);
            bmp.yPelsPerMeter = readLong(bmp.inputStream);
            long colorsUsed = readDWord(bmp.inputStream);
            long colorsImportant = readDWord(bmp.inputStream);

            switch ((int) bmp.compression) {
                case BI_RGB:
                    bmp.properties.put("compression", "BI_RGB");
                    break;

                case BI_RLE8:
                    bmp.properties.put("compression", "BI_RLE8");
                    break;

                case BI_RLE4:
                    bmp.properties.put("compression", "BI_RLE4");
                    break;

                case BI_BITFIELDS:
                    bmp.properties.put("compression", "BI_BITFIELDS");
                    break;
            }

            bmp.properties.put("x_pixels_per_meter", bmp.xPelsPerMeter);
            bmp.properties.put("y_pixels_per_meter", bmp.yPelsPerMeter);
            bmp.properties.put("colors_used", colorsUsed);
            bmp.properties.put("colors_important", colorsImportant);

            if (size == 40 || size == 52 || size == 56) {
                int sizeOfPalette;
                // Windows 3.x and Windows NT
                switch ((int) bmp.compression) {

                    case BI_RGB:  // No compression
                    case BI_RLE8:  // 8-bit RLE compression
                    case BI_RLE4:  // 4-bit RLE compression

                        if (bmp.bitsPerPixel == 1) {
                            bmp.imageType = VERSION_3_1_BIT;
                        } else if (bmp.bitsPerPixel == 4) {
                            bmp.imageType = VERSION_3_4_BIT;
                        } else if (bmp.bitsPerPixel == 8) {
                            bmp.imageType = VERSION_3_8_BIT;
                        } else if (bmp.bitsPerPixel == 24) {
                            bmp.imageType = VERSION_3_24_BIT;
                        } else if (bmp.bitsPerPixel == 16) {
                            bmp.imageType = VERSION_3_NT_16_BIT;
                            bmp.redMask = 0x7C00;
                            bmp.greenMask = 0x3E0;
                            bmp.blueMask = 0x1F;
                            bmp.properties.put("red_mask", bmp.redMask);
                            bmp.properties.put("green_mask", bmp.greenMask);
                            bmp.properties.put("blue_mask", bmp.blueMask);
                        } else if (bmp.bitsPerPixel == 32) {
                            bmp.imageType = VERSION_3_NT_32_BIT;
                            bmp.redMask = 0x00FF0000;
                            bmp.greenMask = 0x0000FF00;
                            bmp.blueMask = 0x000000FF;
                            bmp.properties.put("red_mask", bmp.redMask);
                            bmp.properties.put("green_mask", bmp.greenMask);
                            bmp.properties.put("blue_mask", bmp.blueMask);
                        }

                        // 52 and 56 byte header have mandatory R, G and B masks
                        if (size >= 52) {
                            bmp.redMask = (int) readDWord(bmp.inputStream);
                            bmp.greenMask = (int) readDWord(bmp.inputStream);
                            bmp.blueMask = (int) readDWord(bmp.inputStream);
                            bmp.properties.put("red_mask", bmp.redMask);
                            bmp.properties.put("green_mask", bmp.greenMask);
                            bmp.properties.put("blue_mask", bmp.blueMask);
                        }
                        // 56 byte header has mandatory alpha mask
                        if (size == 56) {
                            bmp.alphaMask = (int) readDWord(bmp.inputStream);
                            bmp.properties.put("alpha_mask", bmp.alphaMask);
                        }

                        // Read in the palette
                        int numberOfEntries = (int) ((bmp.bitmapOffset - 14 - size) / 4);
                        sizeOfPalette = numberOfEntries * 4;
                        if (bmp.bitmapOffset == size) {
                            switch (bmp.imageType) {
                                case VERSION_3_1_BIT:
                                    sizeOfPalette = (int) (colorsUsed == 0 ? 2 : colorsUsed) * 4;
                                    break;
                                case VERSION_3_4_BIT:
                                    sizeOfPalette = (int) (colorsUsed == 0 ? 16 : colorsUsed) * 4;
                                    break;
                                case VERSION_3_8_BIT:
                                    sizeOfPalette = (int) (colorsUsed == 0 ? 256 : colorsUsed) * 4;
                                    break;
                                default:
                                    sizeOfPalette = 0;
                                    break;
                            }
                            bmp.bitmapOffset = size + sizeOfPalette;
                        }
                        readPalette(sizeOfPalette, bmp);

                        bmp.properties.put("bmp_version", "BMP v. 3.x");
                        break;

                    case BI_BITFIELDS:

                        if (bmp.bitsPerPixel == 16) {
                            bmp.imageType = VERSION_3_NT_16_BIT;
                        } else if (bmp.bitsPerPixel == 32) {
                            bmp.imageType = VERSION_3_NT_32_BIT;
                        }

                        // BitsField encoding
                        bmp.redMask = (int) readDWord(bmp.inputStream);
                        bmp.greenMask = (int) readDWord(bmp.inputStream);
                        bmp.blueMask = (int) readDWord(bmp.inputStream);

                        // 56 byte header has mandatory alpha mask
                        if (size == 56) {
                            bmp.alphaMask = (int) readDWord(bmp.inputStream);
                            bmp.properties.put("alpha_mask", bmp.alphaMask);
                        }

                        bmp.properties.put("red_mask", bmp.redMask);
                        bmp.properties.put("green_mask", bmp.greenMask);
                        bmp.properties.put("blue_mask", bmp.blueMask);

                        if (colorsUsed != 0) {
                            // there is a palette
                            sizeOfPalette = (int) colorsUsed * 4;
                            readPalette(sizeOfPalette, bmp);
                        }

                        bmp.properties.put("bmp_version", "BMP v. 3.x NT");
                        break;

                    default:
                        throw new IOException(IOException.InvalidBmpFileCompression);
                }
            } else if (size == 108) {
                // Windows 4.x BMP

                bmp.properties.put("bmp_version", "BMP v. 4.x");

                // rgb masks, valid only if comp is BI_BITFIELDS
                bmp.redMask = (int) readDWord(bmp.inputStream);
                bmp.greenMask = (int) readDWord(bmp.inputStream);
                bmp.blueMask = (int) readDWord(bmp.inputStream);
                // Only supported for 32bpp BI_RGB argb
                bmp.alphaMask = (int) readDWord(bmp.inputStream);
                long csType = readDWord(bmp.inputStream);
                int redX = readLong(bmp.inputStream);
                int redY = readLong(bmp.inputStream);
                int redZ = readLong(bmp.inputStream);
                int greenX = readLong(bmp.inputStream);
                int greenY = readLong(bmp.inputStream);
                int greenZ = readLong(bmp.inputStream);
                int blueX = readLong(bmp.inputStream);
                int blueY = readLong(bmp.inputStream);
                int blueZ = readLong(bmp.inputStream);
                long gammaRed = readDWord(bmp.inputStream);
                long gammaGreen = readDWord(bmp.inputStream);
                long gammaBlue = readDWord(bmp.inputStream);

                if (bmp.bitsPerPixel == 1) {
                    bmp.imageType = VERSION_4_1_BIT;
                } else if (bmp.bitsPerPixel == 4) {
                    bmp.imageType = VERSION_4_4_BIT;
                } else if (bmp.bitsPerPixel == 8) {
                    bmp.imageType = VERSION_4_8_BIT;
                } else if (bmp.bitsPerPixel == 16) {
                    bmp.imageType = VERSION_4_16_BIT;
                    if ((int) bmp.compression == BI_RGB) {
                        bmp.redMask = 0x7C00;
                        bmp.greenMask = 0x3E0;
                        bmp.blueMask = 0x1F;
                    }
                } else if (bmp.bitsPerPixel == 24) {
                    bmp.imageType = VERSION_4_24_BIT;
                } else if (bmp.bitsPerPixel == 32) {
                    bmp.imageType = VERSION_4_32_BIT;
                    if ((int) bmp.compression == BI_RGB) {
                        bmp.redMask = 0x00FF0000;
                        bmp.greenMask = 0x0000FF00;
                        bmp.blueMask = 0x000000FF;
                    }
                }

                bmp.properties.put("red_mask", bmp.redMask);
                bmp.properties.put("green_mask", bmp.greenMask);
                bmp.properties.put("blue_mask", bmp.blueMask);
                bmp.properties.put("alpha_mask", bmp.alphaMask);

                // Read in the palette
                int numberOfEntries = (int) ((bmp.bitmapOffset - 14 - size) / 4);
                int sizeOfPalette = numberOfEntries * 4;
                if (bmp.bitmapOffset == size) {
                    switch (bmp.imageType) {
                        case VERSION_4_1_BIT:
                            sizeOfPalette = (int) (colorsUsed == 0 ? 2 : colorsUsed) * 4;
                            break;
                        case VERSION_4_4_BIT:
                            sizeOfPalette = (int) (colorsUsed == 0 ? 16 : colorsUsed) * 4;
                            break;
                        case VERSION_4_8_BIT:
                            sizeOfPalette = (int) (colorsUsed == 0 ? 256 : colorsUsed) * 4;
                            break;
                        default:
                            sizeOfPalette = 0;
                            break;
                    }
                    bmp.bitmapOffset = size + sizeOfPalette;
                }
                readPalette(sizeOfPalette, bmp);

                switch ((int) csType) {
                    case LCS_CALIBRATED_RGB:
                        // All the new fields are valid only for this case
                        bmp.properties.put("color_space", "LCS_CALIBRATED_RGB");
                        bmp.properties.put("redX", redX);
                        bmp.properties.put("redY", redY);
                        bmp.properties.put("redZ", redZ);
                        bmp.properties.put("greenX", greenX);
                        bmp.properties.put("greenY", greenY);
                        bmp.properties.put("greenZ", greenZ);
                        bmp.properties.put("blueX", blueX);
                        bmp.properties.put("blueY", blueY);
                        bmp.properties.put("blueZ", blueZ);
                        bmp.properties.put("gamma_red", gammaRed);
                        bmp.properties.put("gamma_green", gammaGreen);
                        bmp.properties.put("gamma_blue", gammaBlue);
                        throw new RuntimeException("Not implemented yet.");

                    case LCS_SRGB:
                        // Default Windows color space
                        bmp.properties.put("color_space", "LCS_sRGB");
                        break;

                    case LCS_CMYK:
                        bmp.properties.put("color_space", "LCS_CMYK");
                        //		    break;
                        throw new RuntimeException("Not implemented yet.");
                }
            } else {
                bmp.properties.put("bmp_version", "BMP v. 5.x");
                throw new RuntimeException("Not implemented yet.");
            }
        }

        if (bmp.height > 0) {
            // bottom up image
            bmp.isBottomUp = true;
        } else {
            // top down image
            bmp.isBottomUp = false;
            bmp.height = Math.abs(bmp.height);
        }
        // When number of bitsPerPixel is <= 8, we use IndexColorModel.
        if (bmp.bitsPerPixel == 1 || bmp.bitsPerPixel == 4 || bmp.bitsPerPixel == 8) {
            bmp.numBands = 1;
            // Create IndexColorModel from the palette.
            byte[] r, g, b;
            int sizep;
            if (bmp.imageType == VERSION_2_1_BIT ||
                    bmp.imageType == VERSION_2_4_BIT ||
                    bmp.imageType == VERSION_2_8_BIT) {

                sizep = bmp.palette.length / 3;

                if (sizep > 256) {
                    sizep = 256;
                }

                int off;
                r = new byte[sizep];
                g = new byte[sizep];
                b = new byte[sizep];
                for (int i = 0; i < sizep; i++) {
                    off = 3 * i;
                    b[i] = bmp.palette[off];
                    g[i] = bmp.palette[off + 1];
                    r[i] = bmp.palette[off + 2];
                }
            } else {
                sizep = bmp.palette.length / 4;

                if (sizep > 256) {
                    sizep = 256;
                }

                int off;
                r = new byte[sizep];
                g = new byte[sizep];
                b = new byte[sizep];
                for (int i = 0; i < sizep; i++) {
                    off = 4 * i;
                    b[i] = bmp.palette[off];
                    g[i] = bmp.palette[off + 1];
                    r[i] = bmp.palette[off + 2];
                }
            }

        } else if (bmp.bitsPerPixel == 16) {
            bmp.numBands = 3;
        } else if (bmp.bitsPerPixel == 32) {
            bmp.numBands = bmp.alphaMask == 0 ? 3 : 4;

            // The number of bands in the SampleModel is determined by
            // the length of the mask array passed in.
        } else {
            bmp.numBands = 3;
        }
    }

    private static byte[] getPalette(int group,BmpParameters bmp) {
        if (bmp.palette == null)
            return null;
        byte[] np = new byte[bmp.palette.length / group * 3];
        int e = bmp.palette.length / group;
        for (int k = 0; k < e; ++k) {
            int src = k * group;
            int dest = k * 3;
            np[dest + 2] = bmp.palette[src++];
            np[dest + 1] = bmp.palette[src++];
            np[dest] = bmp.palette[src];
        }
        return np;
    }

    private static boolean getImage(BmpParameters bmp) throws java.io.IOException {
        byte bdata[]; // buffer for byte data
        //	if (sampleModel.getDataType() == DataBuffer.TYPE_BYTE)
        //	    bdata = (byte[])((DataBufferByte)tile.getDataBuffer()).getData();
        //	else if (sampleModel.getDataType() == DataBuffer.TYPE_USHORT)
        //	    sdata = (short[])((DataBufferUShort)tile.getDataBuffer()).getData();
        //	else if (sampleModel.getDataType() == DataBuffer.TYPE_INT)
        //	    idata = (int[])((DataBufferInt)tile.getDataBuffer()).getData();

        // There should only be one tile.
        switch (bmp.imageType) {
            case VERSION_2_1_BIT:
                // no compression
                read1Bit(3, bmp);
                return true;
            case VERSION_2_4_BIT:
                // no compression
                read4Bit(3, bmp);
                return true;
            case VERSION_2_8_BIT:
                // no compression
                read8Bit(3, bmp);
                return true;
            case VERSION_2_24_BIT:
                // no compression
                bdata = new byte[bmp.width * bmp.height * 3];
                read24Bit(bdata, bmp);
                RawImageHelper.updateRawImageParameters(bmp.image, bmp.width, bmp.height, 3, 8, bdata);
                return true;
            case VERSION_3_1_BIT:
                // 1-bit images cannot be compressed.
                read1Bit(4, bmp);
                return true;
            case VERSION_3_4_BIT:
                switch ((int) bmp.compression) {
                    case BI_RGB:
                        read4Bit(4, bmp);
                        break;
                    case BI_RLE4:
                        readRLE4(bmp);
                        break;
                    default:
                        throw new IOException(IOException.InvalidBmpFileCompression);
                }
                return true;
            case VERSION_3_8_BIT:
                switch ((int) bmp.compression) {
                    case BI_RGB:
                        read8Bit(4, bmp);
                        break;
                    case BI_RLE8:
                        readRLE8(bmp);
                        break;
                    default:
                        throw new IOException(IOException.InvalidBmpFileCompression);
                }
                return true;
            case VERSION_3_24_BIT:
                // 24-bit images are not compressed
                bdata = new byte[bmp.width * bmp.height * 3];
                read24Bit(bdata, bmp);
                RawImageHelper.updateRawImageParameters(bmp.image, bmp.width, bmp.height, 3, 8, bdata);
                return true;
            case VERSION_3_NT_16_BIT:
                read1632Bit(false, bmp);
                return true;
            case VERSION_3_NT_32_BIT:
                read1632Bit(true, bmp);
                return true;
            case VERSION_4_1_BIT:
                read1Bit(4, bmp);
                return true;
            case VERSION_4_4_BIT:
                switch ((int) bmp.compression) {
                    case BI_RGB:
                        read4Bit(4, bmp);
                        break;
                    case BI_RLE4:
                        readRLE4(bmp);
                        break;
                    default:
                        throw new IOException(IOException.InvalidBmpFileCompression);
                }
                return true;
            case VERSION_4_8_BIT:
                switch ((int) bmp.compression) {
                    case BI_RGB:
                        read8Bit(4, bmp);
                        break;
                    case BI_RLE8:
                        readRLE8(bmp);
                        break;
                    default:
                        throw new IOException(IOException.InvalidBmpFileCompression);
                }
                return true;
            case VERSION_4_16_BIT:
                read1632Bit(false, bmp);
                return true;
            case VERSION_4_24_BIT:
                bdata = new byte[bmp.width * bmp.height * 3];
                read24Bit(bdata, bmp);
                RawImageHelper.updateRawImageParameters(bmp.image, bmp.width, bmp.height, 3, 8, bdata);
                return true;
            case VERSION_4_32_BIT:
                read1632Bit(true, bmp);
                return true;
        }
        return false;
    }

    private static void indexedModel(byte[] bdata, int bpc, int paletteEntries, BmpParameters bmp) {
        RawImageHelper.updateRawImageParameters(bmp.image, bmp.width, bmp.height, 1, bpc, bdata);
        Object[] colorSpace = new Object[4];
        colorSpace[0] = "/Indexed";
        colorSpace[1] = "/DeviceRGB";
        byte[] np = getPalette(paletteEntries, bmp);
        int len = np.length;
        colorSpace[2] = len / 3 - 1;
        colorSpace[3] = PdfEncodings.convertToString(np, null);
        bmp.additional = new HashMap<>();
        bmp.additional.put("ColorSpace", colorSpace);
    }

    private static void readPalette(int sizeOfPalette, BmpParameters bmp) throws java.io.IOException {
        if (sizeOfPalette == 0) {
            return;
        }

        bmp.palette = new byte[sizeOfPalette];
        int bytesRead = 0;
        while (bytesRead < sizeOfPalette) {
            int r = bmp.inputStream.read(bmp.palette, bytesRead, sizeOfPalette - bytesRead);
            if (r < 0) {
                throw new IOException(IOException.IncompletePalette);
            }
            bytesRead += r;
        }
        bmp.properties.put("palette", bmp.palette);
    }

    // Deal with 1 Bit images using IndexColorModels
    private static void read1Bit(int paletteEntries, BmpParameters bmp) throws java.io.IOException {
        byte[] bdata = new byte[(bmp.width + 7) / 8 * bmp.height];
        int padding = 0;
        int bytesPerScanline = (int) Math.ceil(bmp.width / 8.0d);

        int remainder = bytesPerScanline % 4;
        if (remainder != 0) {
            padding = 4 - remainder;
        }

        int imSize = (bytesPerScanline + padding) * bmp.height;

        // Read till we have the whole image
        byte[] values = new byte[imSize];
        int bytesRead = 0;
        while (bytesRead < imSize) {
            bytesRead += bmp.inputStream.read(values, bytesRead,
                    imSize - bytesRead);
        }

        if (bmp.isBottomUp) {

            // Convert the bottom up image to a top down format by copying
            // one scanline from the bottom to the top at a time.

            for (int i = 0; i < bmp.height; i++) {
                System.arraycopy(values,
                        imSize - (i + 1) * (bytesPerScanline + padding),
                        bdata,
                        i * bytesPerScanline, bytesPerScanline);
            }
        } else {

            for (int i = 0; i < bmp.height; i++) {
                System.arraycopy(values,
                        i * (bytesPerScanline + padding),
                        bdata,
                        i * bytesPerScanline,
                        bytesPerScanline);
            }
        }
        indexedModel(bdata, 1, paletteEntries, bmp);
    }

    // Method to read a 4 bit BMP image data
    private static void read4Bit(int paletteEntries, BmpParameters bmp) throws java.io.IOException {
        byte[] bdata = new byte[(bmp.width + 1) / 2 * bmp.height];

        // Padding bytes at the end of each scanline
        int padding = 0;

        int bytesPerScanline = (int) Math.ceil(bmp.width / 2.0d);
        int remainder = bytesPerScanline % 4;
        if (remainder != 0) {
            padding = 4 - remainder;
        }

        int imSize = (bytesPerScanline + padding) * bmp.height;

        // Read till we have the whole image
        byte[] values = new byte[imSize];
        int bytesRead = 0;
        while (bytesRead < imSize) {
            bytesRead += bmp.inputStream.read(values, bytesRead,
                    imSize - bytesRead);
        }

        if (bmp.isBottomUp) {

            // Convert the bottom up image to a top down format by copying
            // one scanline from the bottom to the top at a time.
            for (int i = 0; i < bmp.height; i++) {
                System.arraycopy(values,
                        imSize - (i + 1) * (bytesPerScanline + padding),
                        bdata,
                        i * bytesPerScanline,
                        bytesPerScanline);
            }
        } else {
            for (int i = 0; i < bmp.height; i++) {
                System.arraycopy(values,
                        i * (bytesPerScanline + padding),
                        bdata,
                        i * bytesPerScanline,
                        bytesPerScanline);
            }
        }
        indexedModel(bdata, 4, paletteEntries, bmp);
    }

    // Method to read 8 bit BMP image data
    private static void read8Bit(int paletteEntries, BmpParameters bmp) throws java.io.IOException {
        byte[] bdata = new byte[bmp.width * bmp.height];
        // Padding bytes at the end of each scanline
        int padding = 0;

        // width * bitsPerPixel should be divisible by 32
        int bitsPerScanline = bmp.width * 8;
        if (bitsPerScanline % 32 != 0) {
            padding = (bitsPerScanline / 32 + 1) * 32 - bitsPerScanline;
            padding = (int) Math.ceil(padding / 8.0);
        }

        int imSize = (bmp.width + padding) * bmp.height;

        // Read till we have the whole image
        byte[] values = new byte[imSize];
        int bytesRead = 0;
        while (bytesRead < imSize) {
            bytesRead += bmp.inputStream.read(values, bytesRead, imSize - bytesRead);
        }

        if (bmp.isBottomUp) {

            // Convert the bottom up image to a top down format by copying
            // one scanline from the bottom to the top at a time.
            for (int i = 0; i < bmp.height; i++) {
                System.arraycopy(values,
                        imSize - (i + 1) * (bmp.width + padding),
                        bdata,
                        i * bmp.width,
                        bmp.width);
            }
        } else {
            for (int i = 0; i < bmp.height; i++) {
                System.arraycopy(values,
                        i * (bmp.width + padding),
                        bdata,
                        i * bmp.width,
                        bmp.width);
            }
        }
        indexedModel(bdata, 8, paletteEntries, bmp);
    }

    // Method to read 24 bit BMP image data
    private static void read24Bit(byte[] bdata, BmpParameters bmp) throws java.io.IOException {
        // Padding bytes at the end of each scanline
        int padding = 0;

        // width * bitsPerPixel should be divisible by 32
        int bitsPerScanline = bmp.width * 24;
        if (bitsPerScanline % 32 != 0) {
            padding = (bitsPerScanline / 32 + 1) * 32 - bitsPerScanline;
            padding = (int) Math.ceil(padding / 8.0);
        }


        int imSize = (bmp.width * 3 + 3) / 4 * 4 * bmp.height;
        // Read till we have the whole image
        byte[] values = new byte[imSize];
        int bytesRead = 0;
        while (bytesRead < imSize) {
            int r = bmp.inputStream.read(values, bytesRead,
                    imSize - bytesRead);
            if (r < 0)
                break;
            bytesRead += r;
        }

        int l = 0, count;

        if (bmp.isBottomUp) {
            int max = bmp.width * bmp.height * 3 - 1;

            count = -padding;
            for (int i = 0; i < bmp.height; i++) {
                l = max - (i + 1) * bmp.width * 3 + 1;
                count += padding;
                for (int j = 0; j < bmp.width; j++) {
                    bdata[l + 2] = values[count++];
                    bdata[l + 1] = values[count++];
                    bdata[l] = values[count++];
                    l += 3;
                }
            }
        } else {
            count = -padding;
            for (int i = 0; i < bmp.height; i++) {
                count += padding;
                for (int j = 0; j < bmp.width; j++) {
                    bdata[l + 2] = values[count++];
                    bdata[l + 1] = values[count++];
                    bdata[l] = values[count++];
                    l += 3;
                }
            }
        }
    }

    private static int findMask(int mask) {
        int k = 0;
        for (; k < 32; ++k) {
            if ((mask & 1) == 1)
                break;
            mask >>>= 1;
        }
        return mask;
    }

    private static int findShift(int mask) {
        int k = 0;
        for (; k < 32; ++k) {
            if ((mask & 1) == 1)
                break;
            mask >>>= 1;
        }
        return k;
    }

    private static void read1632Bit(boolean is32, BmpParameters bmp) throws java.io.IOException {
        int red_mask = findMask(bmp.redMask);
        int red_shift = findShift(bmp.redMask);
        int red_factor = red_mask + 1;
        int green_mask = findMask(bmp.greenMask);
        int green_shift = findShift(bmp.greenMask);
        int green_factor = green_mask + 1;
        int blue_mask = findMask(bmp.blueMask);
        int blue_shift = findShift(bmp.blueMask);
        int blue_factor = blue_mask + 1;
        byte[] bdata = new byte[bmp.width * bmp.height * 3];
        // Padding bytes at the end of each scanline
        int padding = 0;

        if (!is32) {
            // width * bitsPerPixel should be divisible by 32
            int bitsPerScanline = bmp.width * 16;
            if (bitsPerScanline % 32 != 0) {
                padding = (bitsPerScanline / 32 + 1) * 32 - bitsPerScanline;
                padding = (int) Math.ceil(padding / 8.0);
            }
        }

        int imSize = (int) bmp.imageSize;
        if (imSize == 0) {
            imSize = (int) (bmp.bitmapFileSize - bmp.bitmapOffset);
        }

        int l = 0;
        int v;
        if (bmp.isBottomUp) {
            for (int i = bmp.height - 1; i >= 0; --i) {
                l = bmp.width * 3 * i;
                for (int j = 0; j < bmp.width; j++) {
                    if (is32)
                        v = (int) readDWord(bmp.inputStream);
                    else
                        v = readWord(bmp.inputStream);
                    bdata[l++] = (byte) ((v >>> red_shift & red_mask) * 256 / red_factor);
                    bdata[l++] = (byte) ((v >>> green_shift & green_mask) * 256 / green_factor);
                    bdata[l++] = (byte) ((v >>> blue_shift & blue_mask) * 256 / blue_factor);
                }
                for (int m = 0; m < padding; m++) {
                    bmp.inputStream.read();
                }
            }
        } else {
            for (int i = 0; i < bmp.height; i++) {
                for (int j = 0; j < bmp.width; j++) {
                    if (is32)
                        v = (int) readDWord(bmp.inputStream);
                    else
                        v = readWord(bmp.inputStream);
                    bdata[l++] = (byte) ((v >>> red_shift & red_mask) * 256 / red_factor);
                    bdata[l++] = (byte) ((v >>> green_shift & green_mask) * 256 / green_factor);
                    bdata[l++] = (byte) ((v >>> blue_shift & blue_mask) * 256 / blue_factor);
                }
                for (int m = 0; m < padding; m++) {
                    bmp.inputStream.read();
                }
            }
        }
        RawImageHelper.updateRawImageParameters(bmp.image, bmp.width, bmp.height, 3, 8, bdata);
    }

    private static void readRLE8(BmpParameters bmp) throws java.io.IOException {

        // If imageSize field is not provided, calculate it.
        int imSize = (int) bmp.imageSize;
        if (imSize == 0) {
            imSize = (int) (bmp.bitmapFileSize - bmp.bitmapOffset);
        }

        // Read till we have the whole image
        byte[] values = new byte[imSize];
        int bytesRead = 0;
        while (bytesRead < imSize) {
            bytesRead += bmp.inputStream.read(values, bytesRead,
                    imSize - bytesRead);
        }

        // Since data is compressed, decompress it
        byte[] val = decodeRLE(true, values, bmp);

        // Uncompressed data does not have any padding
        imSize = bmp.width * bmp.height;

        if (bmp.isBottomUp) {

            // Convert the bottom up image to a top down format by copying
            // one scanline from the bottom to the top at a time.
            // int bytesPerScanline = (int)Math.ceil((double)width/8.0);
            byte[] temp = new byte[val.length];
            int bytesPerScanline = bmp.width;
            for (int i = 0; i < bmp.height; i++) {
                System.arraycopy(val,
                        imSize - (i + 1) * bytesPerScanline,
                        temp,
                        i * bytesPerScanline, bytesPerScanline);
            }
            val = temp;
        }
        indexedModel(val, 8, 4, bmp);
    }

    private static void readRLE4(BmpParameters bmp) throws java.io.IOException {
        // If imageSize field is not specified, calculate it.
        int imSize = (int) bmp.imageSize;
        if (imSize == 0) {
            imSize = (int) (bmp.bitmapFileSize - bmp.bitmapOffset);
        }

        // Read till we have the whole image
        byte[] values = new byte[imSize];
        int bytesRead = 0;
        while (bytesRead < imSize) {
            bytesRead += bmp.inputStream.read(values, bytesRead,
                    imSize - bytesRead);
        }

        // Decompress the RLE4 compressed data.
        byte[] val = decodeRLE(false, values, bmp);

        // Invert it as it is bottom up format.
        if (bmp.isBottomUp) {

            byte[] inverted = val;
            val = new byte[bmp.width * bmp.height];
            int l = 0, index, lineEnd;

            for (int i = bmp.height - 1; i >= 0; i--) {
                index = i * bmp.width;
                lineEnd = l + bmp.width;
                while (l != lineEnd) {
                    val[l++] = inverted[index++];
                }
            }
        }
        int stride = (bmp.width + 1) / 2;
        byte[] bdata = new byte[stride * bmp.height];
        int ptr = 0;
        int sh = 0;
        for (int h = 0; h < bmp.height; ++h) {
            for (int w = 0; w < bmp.width; ++w) {
                if ((w & 1) == 0)
                    bdata[sh + w / 2] = (byte) (val[ptr++] << 4);
                else
                    bdata[sh + w / 2] |= (byte) (val[ptr++] & 0x0f);
            }
            sh += stride;
        }
        indexedModel(bdata, 4, 4, bmp);
    }

    private static byte[] decodeRLE(boolean is8, byte[] values, BmpParameters bmp) {
        byte[] val = new byte[bmp.width * bmp.height];
        try {
            int ptr = 0;
            int x = 0;
            int q = 0;
            for (int y = 0; y < bmp.height && ptr < values.length; ) {
                int count = values[ptr++] & 0xff;
                if (count != 0) {
                    // encoded mode
                    int bt = values[ptr++] & 0xff;
                    if (is8) {
                        for (int i = count; i != 0; --i) {
                            val[q++] = (byte) bt;
                        }
                    } else {
                        for (int i = 0; i < count; ++i) {
                            val[q++] = (byte) ((i & 1) == 1 ? bt & 0x0f : bt >>> 4 & 0x0f);
                        }
                    }
                    x += count;
                } else {
                    // escape mode
                    count = values[ptr++] & 0xff;
                    if (count == 1)
                        break;
                    switch (count) {
                        case 0:
                            x = 0;
                            ++y;
                            q = y * bmp.width;
                            break;
                        case 2:
                            // delta mode
                            x += values[ptr++] & 0xff;
                            y += values[ptr++] & 0xff;
                            q = y * bmp.width + x;
                            break;
                        default:
                            // absolute mode
                            if (is8) {
                                for (int i = count; i != 0; --i)
                                    val[q++] = (byte) (values[ptr++] & 0xff);
                            } else {
                                int bt = 0;
                                for (int i = 0; i < count; ++i) {
                                    if ((i & 1) == 0)
                                        bt = values[ptr++] & 0xff;
                                    val[q++] = (byte) ((i & 1) == 1 ? bt & 0x0f : bt >>> 4 & 0x0f);
                                }
                            }
                            x += count;
                            // read pad byte
                            if (is8) {
                                if ((count & 1) == 1)
                                    ++ptr;
                            } else {
                                if ((count & 3) == 1 || (count & 3) == 2)
                                    ++ptr;
                            }
                            break;
                    }
                }
            }
        } catch (Exception e) {
            //empty on purpose
        }

        return val;
    }

    // Windows defined data type reading methods - everything is little endian

    // Unsigned 8 bits
    private static int readUnsignedByte(InputStream stream) throws java.io.IOException {
        return stream.read() & 0xff;
    }

    // Unsigned 2 bytes
    private static int readUnsignedShort(InputStream stream) throws java.io.IOException {
        int b1 = readUnsignedByte(stream);
        int b2 = readUnsignedByte(stream);
        return (b2 << 8 | b1) & 0xffff;
    }

    // Signed 16 bits
    private static int readShort(InputStream stream) throws java.io.IOException {
        int b1 = readUnsignedByte(stream);
        int b2 = readUnsignedByte(stream);
        return b2 << 8 | b1;
    }

    // Unsigned 16 bits
    private static int readWord(InputStream stream) throws java.io.IOException {
        return readUnsignedShort(stream);
    }

    // Unsigned 4 bytes
    private static long readUnsignedInt(InputStream stream) throws java.io.IOException {
        int b1 = readUnsignedByte(stream);
        int b2 = readUnsignedByte(stream);
        int b3 = readUnsignedByte(stream);
        int b4 = readUnsignedByte(stream);
        long l = b4 << 24 | b3 << 16 | b2 << 8 | b1;
        return l & 0xffffffff;
    }

    // Signed 4 bytes
    private static int readInt(InputStream stream) throws java.io.IOException {
        int b1 = readUnsignedByte(stream);
        int b2 = readUnsignedByte(stream);
        int b3 = readUnsignedByte(stream);
        int b4 = readUnsignedByte(stream);
        return b4 << 24 | b3 << 16 | b2 << 8 | b1;
    }

    // Unsigned 4 bytes
    private static long readDWord(InputStream stream) throws java.io.IOException {
        return readUnsignedInt(stream);
    }

    // 32 bit signed value
    private static int readLong(InputStream stream) throws java.io.IOException {
        return readInt(stream);
    }
}
