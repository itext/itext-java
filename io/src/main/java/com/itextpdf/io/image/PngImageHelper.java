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
import com.itextpdf.io.util.FilterUtil;
import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.io.colors.IccProfile;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.io.source.ByteBuffer;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import com.itextpdf.io.util.MessageFormatUtil;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

class PngImageHelper {

    private static class PngParameters {
        PngParameters(PngImageData image) {
            this.image = image;
        }

        PngImageData image;

        InputStream dataStream;
        int width;
        int height;
        int bitDepth;
        int colorType;
        int compressionMethod;
        int filterMethod;
        int interlaceMethod;
        Map<String, Object> additional = new HashMap<>();
        byte[] imageData;
        byte[] smask;
        byte[] trans;
        ByteArrayOutputStream idat = new ByteArrayOutputStream();
        int dpiX;
        int dpiY;
        float XYRatio;
        boolean genBWMask;
        boolean palShades;
        int transRedGray = -1;
        int transGreen = -1;
        int transBlue = -1;
        int inputBands;
        int bytesPerPixel; // number of bytes per input pixel
        byte[] colorTable;
        float gamma = 1f;
        boolean hasCHRM = false;
        float xW, yW, xR, yR, xG, yG, xB, yB;
        String intent;
        IccProfile iccProfile;
    }

    /**
     * Some PNG specific values.
     */
    public static final int[] PNGID = {137, 80, 78, 71, 13, 10, 26, 10};

    /**
     * A PNG marker.
     */
    public static final String IHDR = "IHDR";

    /**
     * A PNG marker.
     */
    public static final String PLTE = "PLTE";

    /**
     * A PNG marker.
     */
    public static final String IDAT = "IDAT";

    /**
     * A PNG marker.
     */
    public static final String IEND = "IEND";

    /**
     * A PNG marker.
     */
    public static final String tRNS = "tRNS";

    /**
     * A PNG marker.
     */
    public static final String pHYs = "pHYs";

    /**
     * A PNG marker.
     */
    public static final String gAMA = "gAMA";

    /**
     * A PNG marker.
     */
    public static final String cHRM = "cHRM";

    /**
     * A PNG marker.
     */
    public static final String sRGB = "sRGB";

    /**
     * A PNG marker.
     */
    public static final String iCCP = "iCCP";

    private static final int TRANSFERSIZE = 4096;
    private static final int PNG_FILTER_NONE = 0;
    private static final int PNG_FILTER_SUB = 1;
    private static final int PNG_FILTER_UP = 2;
    private static final int PNG_FILTER_AVERAGE = 3;
    private static final int PNG_FILTER_PAETH = 4;
    private static final String[] intents = {"/Perceptual",
            "/RelativeColorimetric", "/Saturation", "/AbsoluteColorimetric"};

    public static void processImage(ImageData image) {
        if (image.getOriginalType() != ImageType.PNG)
            throw new IllegalArgumentException("PNG image expected");
        PngParameters png;
        InputStream pngStream = null;
        try {
            if (image.getData() == null) {
                image.loadData();
            }
            pngStream = new ByteArrayInputStream(image.getData());
            image.imageSize = image.getData().length;
            png = new PngParameters((PngImageData) image);
            processPng(pngStream, png);
        } catch (java.io.IOException e) {
            throw new IOException(IOException.PngImageException, e);
        } finally {
            if (pngStream != null) {
                try {
                    pngStream.close();
                } catch (java.io.IOException ignored) {
                }
            }
        }
        RawImageHelper.updateImageAttributes(png.image, png.additional);
    }

    private static void processPng(InputStream pngStream, PngParameters png) throws java.io.IOException {
        readPng(pngStream, png);
        if (png.iccProfile != null && png.iccProfile.getNumComponents() != getExpectedNumberOfColorComponents(png)) {
            LoggerFactory.getLogger(PngImageHelper.class).warn(LogMessageConstant.PNG_IMAGE_HAS_ICC_PROFILE_WITH_INCOMPATIBLE_NUMBER_OF_COLOR_COMPONENTS);
        }
        try {
            int pal0 = 0;
            int palIdx = 0;
            png.palShades = false;
            if (png.trans != null) {
                for (int k = 0; k < png.trans.length; ++k) {
                    int n = png.trans[k] & 0xff;
                    if (n == 0) {
                        ++pal0;
                        palIdx = k;
                    }
                    if (n != 0 && n != 255) {
                        png.palShades = true;
                        break;
                    }
                }
            }
            if ((png.colorType & 4) != 0)
                png.palShades = true;
            png.genBWMask = (!png.palShades && (pal0 > 1 || png.transRedGray >= 0));
            if (!png.palShades && !png.genBWMask && pal0 == 1) {
                png.additional.put("Mask", new int[]{palIdx,palIdx});
            }
            boolean needDecode = (png.interlaceMethod == 1) || (png.bitDepth == 16) || ((png.colorType & 4) != 0) || png.palShades || png.genBWMask;
            switch (png.colorType) {
                case 0:
                    png.inputBands = 1;
                    break;
                case 2:
                    png.inputBands = 3;
                    break;
                case 3:
                    png.inputBands = 1;
                    break;
                case 4:
                    png.inputBands = 2;
                    break;
                case 6:
                    png.inputBands = 4;
                    break;
            }
            if (needDecode)
                decodeIdat(png);
            int components = png.inputBands;
            if ((png.colorType & 4) != 0)
                --components;
            int bpc = png.bitDepth;
            if (bpc == 16)
                bpc = 8;
            if (png.imageData != null) {
                if (png.colorType == 3) {
                    RawImageHelper.updateRawImageParameters(png.image, png.width, png.height, components, bpc, png.imageData);
                } else {
                    RawImageHelper.updateRawImageParameters(png.image, png.width, png.height, components, bpc, png.imageData, null);
                }
            } else {
                RawImageHelper.updateRawImageParameters(png.image, png.width, png.height, components, bpc, png.idat.toByteArray());
                png.image.setDeflated(true);
                Map<String, Object> decodeparms = new HashMap<>();
                decodeparms.put("BitsPerComponent", png.bitDepth);
                decodeparms.put("Predictor", 15);
                decodeparms.put("Columns", png.width);
                decodeparms.put("Colors", (png.colorType == 3 || (png.colorType & 2) == 0) ? 1 : 3);
                png.image.decodeParms = decodeparms;
            }
            if (png.additional.get("ColorSpace") == null)
                png.additional.put("ColorSpace", getColorspace(png));
            if (png.intent != null)
                png.additional.put("Intent", png.intent);
            if (png.iccProfile != null)
                png.image.setProfile(png.iccProfile);
            if (png.palShades) {
                RawImageData im2 = (RawImageData) ImageDataFactory.createRawImage(null);
                RawImageHelper.updateRawImageParameters(im2, png.width, png.height, 1, 8, png.smask);
                im2.makeMask();
                png.image.setImageMask(im2);
            }
            if (png.genBWMask) {
                RawImageData im2 = (RawImageData) ImageDataFactory.createRawImage(null);
                RawImageHelper.updateRawImageParameters(im2, png.width, png.height, 1, 1, png.smask);
                im2.makeMask();
                png.image.setImageMask(im2);
            }
            png.image.setDpi(png.dpiX, png.dpiY);
            png.image.setXYRatio(png.XYRatio);
        } catch (Exception e) {
            throw new IOException(IOException.PngImageException, e);
        }
    }

    private static Object getColorspace(PngParameters png) {
        if (png.iccProfile != null) {
            if ((png.colorType & 2) == 0)
                return "/DeviceGray";
            else
                return "/DeviceRGB";
        }
        if (png.gamma == 1f && !png.hasCHRM) {
            if ((png.colorType & 2) == 0)
                return "/DeviceGray";
            else
                return "/DeviceRGB";
        } else {
            Object[] array = new Object[2];
            Map<String, Object> map = new HashMap<>();
            if ((png.colorType & 2) == 0) {
                if (png.gamma == 1f)
                    return "/DeviceGray";
                array[0] = "/CalGray";
                map.put("Gamma", png.gamma);
                map.put("WhitePoint", new int[]{1, 1, 1});
                array[1] = map;
            } else {
                float[] wp = new float[]{1, 1, 1};
                array[0] = "/CalRGB";
                if (png.gamma != 1f) {
                    float[] gm = new float[3];
                    gm[0] = png.gamma;
                    gm[1] = png.gamma;
                    gm[2] = png.gamma;
                    map.put("Gamma", gm);
                }
                if (png.hasCHRM) {
                    float z = png.yW * ((png.xG - png.xB) * png.yR - (png.xR - png.xB) * png.yG + (png.xR - png.xG) * png.yB);
                    float YA = png.yR * ((png.xG - png.xB) * png.yW - (png.xW - png.xB) * png.yG + (png.xW - png.xG) * png.yB) / z;
                    float XA = YA * png.xR / png.yR;
                    float ZA = YA * ((1 - png.xR) / png.yR - 1);
                    float YB = -png.yG * ((png.xR - png.xB) * png.yW - (png.xW - png.xB) * png.yR + (png.xW - png.xR) * png.yB) / z;
                    float XB = YB * png.xG / png.yG;
                    float ZB = YB * ((1 - png.xG) / png.yG - 1);
                    float YC = png.yB * ((png.xR - png.xG) * png.yW - (png.xW - png.xG) * png.yW + (png.xW - png.xR) * png.yG) / z;
                    float XC = YC * png.xB / png.yB;
                    float ZC = YC * ((1 - png.xB) / png.yB - 1);
                    float XW = XA + XB + XC;
                    float YW = 1;//YA+YB+YC;
                    float ZW = ZA + ZB + ZC;
                    float[] wpa = new float[3];
                    wpa[0] = XW;
                    wpa[1] = YW;
                    wpa[2] = ZW;
                    wp = wpa;
                    float[] matrix = new float[9];
                    matrix[0] = XA;
                    matrix[1] = YA;
                    matrix[2] = ZA;
                    matrix[3] = XB;
                    matrix[4] = YB;
                    matrix[5] = ZB;
                    matrix[6] = XC;
                    matrix[7] = YC;
                    matrix[8] = ZC;
                    map.put("Matrix", matrix);
                }
                map.put("WhitePoint", wp);
                array[1] = map;
            }
            return array;
        }
    }

    private static int getExpectedNumberOfColorComponents(PngParameters png) {
        return (png.colorType & 2) == 0 ? 1 : 3;
    }

    private static void readPng(InputStream pngStream, PngParameters png) throws java.io.IOException {
        for (int i = 0; i < PNGID.length; i++) {
            if (PNGID[i] != pngStream.read()) {
                throw new java.io.IOException("file.is.not.a.valid.png");
            }
        }
        byte[] buffer = new byte[TRANSFERSIZE];
        while (true) {
            int len = getInt(pngStream);
            String marker = getString(pngStream);
            if (len < 0 || !checkMarker(marker))
                throw new java.io.IOException("corrupted.png.file");
            if (IDAT.equals(marker)) {
                int size;
                while (len != 0) {
                    size = pngStream.read(buffer, 0, Math.min(len, TRANSFERSIZE));
                    if (size < 0)
                        return;
                    png.idat.write(buffer, 0, size);
                    len -= size;
                }
            } else if (tRNS.equals(marker)) {
                switch (png.colorType) {
                    case 0:
                        if (len >= 2) {
                            len -= 2;
                            int gray = getWord(pngStream);
                            if (png.bitDepth == 16)
                                png.transRedGray = gray;
                            else
                                png.additional.put("Mask", MessageFormatUtil.format("[{0} {1}]", gray, gray));
                        }
                        break;
                    case 2:
                        if (len >= 6) {
                            len -= 6;
                            int red = getWord(pngStream);
                            int green = getWord(pngStream);
                            int blue = getWord(pngStream);
                            if (png.bitDepth == 16) {
                                png.transRedGray = red;
                                png.transGreen = green;
                                png.transBlue = blue;
                            } else
                                png.additional.put("Mask", MessageFormatUtil.format("[{0} {1} {2} {3} {4} {5}]", red, red, green, green, blue, blue));
                        }
                        break;
                    case 3:
                        if (len > 0) {
                            png.trans = new byte[len];
                            for (int k = 0; k < len; ++k)
                                png.trans[k] = (byte) pngStream.read();
                            len = 0;
                        }
                        break;
                }
                StreamUtil.skip(pngStream, len);
            } else if (IHDR.equals(marker)) {
                png.width = getInt(pngStream);
                png.height = getInt(pngStream);

                png.bitDepth = pngStream.read();
                png.colorType = pngStream.read();
                png.compressionMethod = pngStream.read();
                png.filterMethod = pngStream.read();
                png.interlaceMethod = pngStream.read();
            } else if (PLTE.equals(marker)) {
                if (png.colorType == 3) {
                    Object[] colorspace = new Object[4];
                    colorspace[0] = "/Indexed";
                    colorspace[1] = getColorspace(png);
                    colorspace[2] = len / 3 - 1;
                    ByteBuffer colorTableBuf = new ByteBuffer();
                    while ((len--) > 0) {
                        colorTableBuf.append(pngStream.read());
                    }
                    png.colorTable = colorTableBuf.toByteArray();
                    colorspace[3] = PdfEncodings.convertToString(png.colorTable, null);
                    png.additional.put("ColorSpace", colorspace);
                } else {
                    StreamUtil.skip(pngStream, len);
                }
            } else if (pHYs.equals(marker)) {
                int dx = getInt(pngStream);
                int dy = getInt(pngStream);
                int unit = pngStream.read();
                if (unit == 1) {
                    png.dpiX = (int) (dx * 0.0254f + 0.5f);
                    png.dpiY = (int) (dy * 0.0254f + 0.5f);
                } else {
                    if (dy != 0)
                        png.XYRatio = (float) dx / (float) dy;
                }
            } else if (cHRM.equals(marker)) {
                png.xW = getInt(pngStream) / 100000f;
                png.yW = getInt(pngStream) / 100000f;
                png.xR = getInt(pngStream) / 100000f;
                png.yR = getInt(pngStream) / 100000f;
                png.xG = getInt(pngStream) / 100000f;
                png.yG = getInt(pngStream) / 100000f;
                png.xB = getInt(pngStream) / 100000f;
                png.yB = getInt(pngStream) / 100000f;
                png.hasCHRM = !(Math.abs(png.xW) < 0.0001f || Math.abs(png.yW) < 0.0001f || Math.abs(png.xR) < 0.0001f || Math.abs(png.yR) < 0.0001f || Math.abs(png.xG) < 0.0001f || Math.abs(png.yG) < 0.0001f || Math.abs(png.xB) < 0.0001f || Math.abs(png.yB) < 0.0001f);
            } else if (sRGB.equals(marker)) {
                int ri = pngStream.read();
                png.intent = intents[ri];
                png.gamma = 2.2f;
                png.xW = 0.3127f;
                png.yW = 0.329f;
                png.xR = 0.64f;
                png.yR = 0.33f;
                png.xG = 0.3f;
                png.yG = 0.6f;
                png.xB = 0.15f;
                png.yB = 0.06f;
                png.hasCHRM = true;
            } else if (gAMA.equals(marker)) {
                int gm = getInt(pngStream);
                if (gm != 0) {
                    png.gamma = 100000f / gm;
                    if (!png.hasCHRM) {
                        png.xW = 0.3127f;
                        png.yW = 0.329f;
                        png.xR = 0.64f;
                        png.yR = 0.33f;
                        png.xG = 0.3f;
                        png.yG = 0.6f;
                        png.xB = 0.15f;
                        png.yB = 0.06f;
                        png.hasCHRM = true;
                    }
                }
            } else if (iCCP.equals(marker)) {
                do {
                    --len;
                } while (pngStream.read() != 0);
                pngStream.read();
                --len;
                byte[] icccom = new byte[len];
                int p = 0;
                while (len > 0) {
                    int r = pngStream.read(icccom, p, len);
                    if (r < 0)
                        throw new java.io.IOException("premature.end.of.file");
                    p += r;
                    len -= r;
                }
                byte[] iccp = FilterUtil.flateDecode(icccom, true);
                icccom = null;
                try {
                    png.iccProfile = IccProfile.getInstance(iccp);
                } catch (RuntimeException e) {
                    png.iccProfile = null;
                }
            } else if (IEND.equals(marker)) {
                break;
            } else {
                StreamUtil.skip(pngStream, len);
            }
            StreamUtil.skip(pngStream, 4);
        }
    }

    private static boolean checkMarker(String s) {
        if (s.length() != 4)
            return false;
        for (int k = 0; k < 4; ++k) {
            char c = s.charAt(k);
            if ((c < 'a' || c > 'z') && (c < 'A' || c > 'Z'))
                return false;
        }
        return true;
    }

    private static void decodeIdat(PngParameters png) {
        int nbitDepth = png.bitDepth;
        if (nbitDepth == 16)
            nbitDepth = 8;
        int size = -1;
        png.bytesPerPixel = (png.bitDepth == 16) ? 2 : 1;
        switch (png.colorType) {
            case 0:
                size = (nbitDepth * png.width + 7) / 8 * png.height;
                break;
            case 2:
                size = png.width * 3 * png.height;
                png.bytesPerPixel *= 3;
                break;
            case 3:
                if (png.interlaceMethod == 1)
                    size = (nbitDepth * png.width + 7) / 8 * png.height;
                png.bytesPerPixel = 1;
                break;
            case 4:
                size = png.width * png.height;
                png.bytesPerPixel *= 2;
                break;
            case 6:
                size = png.width * 3 * png.height;
                png.bytesPerPixel *= 4;
                break;
        }
        if (size >= 0)
            png.imageData = new byte[size];
        if (png.palShades)
            png.smask = new byte[png.width * png.height];
        else if (png.genBWMask)
            png.smask = new byte[(png.width + 7) / 8 * png.height];
        ByteArrayInputStream bai = new ByteArrayInputStream(png.idat.toByteArray());
        png.dataStream = FilterUtil.getInflaterInputStream(bai);

        if (png.interlaceMethod != 1) {
            decodePass(0, 0, 1, 1, png.width, png.height, png);
        } else {
            decodePass(0, 0, 8, 8, (png.width + 7) / 8, (png.height + 7) / 8, png);
            decodePass(4, 0, 8, 8, (png.width + 3) / 8, (png.height + 7) / 8, png);
            decodePass(0, 4, 4, 8, (png.width + 3) / 4, (png.height + 3) / 8, png);
            decodePass(2, 0, 4, 4, (png.width + 1) / 4, (png.height + 3) / 4, png);
            decodePass(0, 2, 2, 4, (png.width + 1) / 2, (png.height + 1) / 4, png);
            decodePass(1, 0, 2, 2, png.width / 2, (png.height + 1) / 2, png);
            decodePass(0, 1, 1, 2, png.width, png.height / 2, png);
        }

    }

    private static void decodePass(int xOffset, int yOffset, int xStep, int yStep,
                                   int passWidth, int passHeight, PngParameters png) {
        if ((passWidth == 0) || (passHeight == 0)) {
            return;
        }

        int bytesPerRow = (png.inputBands * passWidth * png.bitDepth + 7) / 8;
        byte[] curr = new byte[bytesPerRow];
        byte[] prior = new byte[bytesPerRow];

        // Decode the (sub)image row-by-row
        int srcY, dstY;
        for (srcY = 0, dstY = yOffset;
             srcY < passHeight;
             srcY++, dstY += yStep) {
            // Read the filter type byte and a row of data
            int filter = 0;
            try {
                filter = png.dataStream.read();
                StreamUtil.readFully(png.dataStream, curr, 0, bytesPerRow);
            } catch (Exception e) {
                // empty on purpose
            }

            switch (filter) {
                case PNG_FILTER_NONE:
                    break;
                case PNG_FILTER_SUB:
                    decodeSubFilter(curr, bytesPerRow, png.bytesPerPixel);
                    break;
                case PNG_FILTER_UP:
                    decodeUpFilter(curr, prior, bytesPerRow);
                    break;
                case PNG_FILTER_AVERAGE:
                    decodeAverageFilter(curr, prior, bytesPerRow, png.bytesPerPixel);
                    break;
                case PNG_FILTER_PAETH:
                    decodePaethFilter(curr, prior, bytesPerRow, png.bytesPerPixel);
                    break;
                default:
                    // Error -- uknown filter type
                    throw new IOException(IOException.UnknownPngFilter);
            }

            processPixels(curr, xOffset, xStep, dstY, passWidth, png);

            // Swap curr and prior
            byte[] tmp = prior;
            prior = curr;
            curr = tmp;
        }
    }

    private static void processPixels(byte[] curr, int xOffset, int step, int y, int width, PngParameters png) {
        int srcX, dstX;

        int[] outPixel = getPixel(curr, png);
        int sizes = 0;
        switch (png.colorType) {
            case 0:
            case 3:
            case 4:
                sizes = 1;
                break;
            case 2:
            case 6:
                sizes = 3;
                break;
        }
        if (png.imageData != null) {
            dstX = xOffset;
            int yStride = (sizes * png.width * (png.bitDepth == 16 ? 8 : png.bitDepth) + 7) / 8;
            for (srcX = 0; srcX < width; srcX++) {
                setPixel(png.imageData, outPixel, png.inputBands * srcX, sizes, dstX, y, png.bitDepth, yStride);
                dstX += step;
            }
        }
        if (png.palShades) {
            if ((png.colorType & 4) != 0) {
                if (png.bitDepth == 16) {
                    for (int k = 0; k < width; ++k)
                        outPixel[k * png.inputBands + sizes] >>>= 8;
                }
                int yStride = png.width;
                dstX = xOffset;
                for (srcX = 0; srcX < width; srcX++) {
                    setPixel(png.smask, outPixel, png.inputBands * srcX + sizes, 1, dstX, y, 8, yStride);
                    dstX += step;
                }
            } else { //colorType 3
                int yStride = png.width;
                int[] v = new int[1];
                dstX = xOffset;
                for (srcX = 0; srcX < width; srcX++) {
                    int idx = outPixel[srcX];
                    if (idx < png.trans.length)
                        v[0] = png.trans[idx];
                    else
                        v[0] = 255; // Patrick Valsecchi
                    setPixel(png.smask, v, 0, 1, dstX, y, 8, yStride);
                    dstX += step;
                }
            }
        } else if (png.genBWMask) {
            switch (png.colorType) {
                case 3: {
                    int yStride = (png.width + 7) / 8;
                    int[] v = new int[1];
                    dstX = xOffset;
                    for (srcX = 0; srcX < width; srcX++) {
                        int idx = outPixel[srcX];
                        v[0] = ((idx < png.trans.length && png.trans[idx] == 0) ? 1 : 0);
                        setPixel(png.smask, v, 0, 1, dstX, y, 1, yStride);
                        dstX += step;
                    }
                    break;
                }
                case 0: {
                    int yStride = (png.width + 7) / 8;
                    int[] v = new int[1];
                    dstX = xOffset;
                    for (srcX = 0; srcX < width; srcX++) {
                        int g = outPixel[srcX];
                        v[0] = (g == png.transRedGray ? 1 : 0);
                        setPixel(png.smask, v, 0, 1, dstX, y, 1, yStride);
                        dstX += step;
                    }
                    break;
                }
                case 2: {
                    int yStride = (png.width + 7) / 8;
                    int[] v = new int[1];
                    dstX = xOffset;
                    for (srcX = 0; srcX < width; srcX++) {
                        int markRed = png.inputBands * srcX;
                        v[0] = (outPixel[markRed] == png.transRedGray && outPixel[markRed + 1] == png.transGreen
                                && outPixel[markRed + 2] == png.transBlue ? 1 : 0);
                        setPixel(png.smask, v, 0, 1, dstX, y, 1, yStride);
                        dstX += step;
                    }
                    break;
                }
            }
        }
    }

    private static int getPixel(byte[] image, int x, int y, int bitDepth, int bytesPerRow) {
        if (bitDepth == 8) {
            int pos = bytesPerRow * y + x;
            return image[pos] & 0xff;
        } else {
            int pos = bytesPerRow * y + x / (8 / bitDepth);
            int v = image[pos] >> (8 - bitDepth * (x % (8 / bitDepth)) - bitDepth);
            return v & ((1 << bitDepth) - 1);
        }
    }

    static void setPixel(byte[] image, int[] data, int offset, int size, int x, int y, int bitDepth, int bytesPerRow) {
        if (bitDepth == 8) {
            int pos = bytesPerRow * y + size * x;
            for (int k = 0; k < size; ++k)
                image[pos + k] = (byte) data[k + offset];
        } else if (bitDepth == 16) {
            int pos = bytesPerRow * y + size * x;
            for (int k = 0; k < size; ++k)
                image[pos + k] = (byte) (data[k + offset] >>> 8);
        } else {
            int pos = bytesPerRow * y + x / (8 / bitDepth);
            int v = data[offset] << (8 - bitDepth * (x % (8 / bitDepth)) - bitDepth);
            image[pos] |= (byte) v;
        }
    }

    private static int[] getPixel(byte[] curr, PngParameters png) {
        switch (png.bitDepth) {
            case 8: {
                int[] res = new int[curr.length];
                for (int k = 0; k < res.length; ++k)
                    res[k] = curr[k] & 0xff;
                return res;
            }
            case 16: {
                int[] res = new int[curr.length / 2];
                for (int k = 0; k < res.length; ++k)
                    res[k] = ((curr[k * 2] & 0xff) << 8) + (curr[k * 2 + 1] & 0xff);
                return res;
            }
            default: {
                int[] res = new int[curr.length * 8 / png.bitDepth];
                int idx = 0;
                int passes = 8 / png.bitDepth;
                int mask = (1 << png.bitDepth) - 1;
                for (int k = 0; k < curr.length; ++k) {
                    for (int j = passes - 1; j >= 0; --j) {
                        res[idx++] = (curr[k] >>> (png.bitDepth * j)) & mask;
                    }
                }
                return res;
            }
        }
    }

    private static void decodeSubFilter(byte[] curr, int count, int bpp) {
        for (int i = bpp; i < count; i++) {
            int val = curr[i] & 0xff;
            val += curr[i - bpp] & 0xff;
            curr[i] = (byte) val;
        }
    }

    private static void decodeUpFilter(byte[] curr, byte[] prev, int count) {
        for (int i = 0; i < count; i++) {
            int raw = curr[i] & 0xff;
            int prior = prev[i] & 0xff;
            curr[i] = (byte) (raw + prior);
        }
    }

    private static void decodeAverageFilter(byte[] curr, byte[] prev, int count, int bpp) {
        int raw, priorPixel, priorRow;

        for (int i = 0; i < bpp; i++) {
            raw = curr[i] & 0xff;
            priorRow = prev[i] & 0xff;
            curr[i] = (byte) (raw + priorRow / 2);
        }

        for (int i = bpp; i < count; i++) {
            raw = curr[i] & 0xff;
            priorPixel = curr[i - bpp] & 0xff;
            priorRow = prev[i] & 0xff;
            curr[i] = (byte) (raw + (priorPixel + priorRow) / 2);
        }
    }

    private static int paethPredictor(int a, int b, int c) {
        int p = a + b - c;
        int pa = Math.abs(p - a);
        int pb = Math.abs(p - b);
        int pc = Math.abs(p - c);

        if ((pa <= pb) && (pa <= pc)) {
            return a;
        } else if (pb <= pc) {
            return b;
        } else {
            return c;
        }
    }

    private static void decodePaethFilter(byte[] curr, byte[] prev, int count, int bpp) {
        int raw, priorPixel, priorRow, priorRowPixel;

        for (int i = 0; i < bpp; i++) {
            raw = curr[i] & 0xff;
            priorRow = prev[i] & 0xff;
            curr[i] = (byte) (raw + priorRow);
        }

        for (int i = bpp; i < count; i++) {
            raw = curr[i] & 0xff;
            priorPixel = curr[i - bpp] & 0xff;
            priorRow = prev[i] & 0xff;
            priorRowPixel = prev[i - bpp] & 0xff;
            curr[i] = (byte) (raw + paethPredictor(priorPixel,
                    priorRow,
                    priorRowPixel));
        }
    }

    /**
     * Gets an <CODE>int</CODE> from an <CODE>InputStream</CODE>.
     *
     * @param pngStream an <CODE>InputStream</CODE>
     * @return the value of an <CODE>int</CODE>
     */
    public static int getInt(InputStream pngStream) throws java.io.IOException {
        return (pngStream.read() << 24) + (pngStream.read() << 16)
                + (pngStream.read() << 8) + pngStream.read();
    }

    /**
     * Gets a <CODE>word</CODE> from an <CODE>InputStream</CODE>.
     *
     * @param pngStream an <CODE>InputStream</CODE>
     * @return the value of an <CODE>int</CODE>
     */
    public static int getWord(InputStream pngStream) throws java.io.IOException {
        return (pngStream.read() << 8) + pngStream.read();
    }

    /**
     * Gets a <CODE>String</CODE> from an <CODE>InputStream</CODE>.
     *
     * @param pngStream an <CODE>InputStream</CODE>
     * @return the value of an <CODE>int</CODE>
     */
    public static String getString(InputStream pngStream) throws java.io.IOException {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            buf.append((char) pngStream.read());
        }
        return buf.toString();
    }
}
