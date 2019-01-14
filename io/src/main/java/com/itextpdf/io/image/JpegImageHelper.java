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
import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.io.colors.IccProfile;

import java.io.InputStream;
import com.itextpdf.io.util.MessageFormatUtil;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class JpegImageHelper {

    /**
     * This is a type of marker.
     */
    private static final int NOT_A_MARKER = -1;

    /**
     * This is a type of marker.
     */
    private static final int VALID_MARKER = 0;

    /**
     * Acceptable Jpeg markers.
     */
    private static final int[] VALID_MARKERS = {0xC0, 0xC1, 0xC2};

    /**
     * This is a type of marker.
     */
    private static final int UNSUPPORTED_MARKER = 1;

    /**
     * Unsupported Jpeg markers.
     */
    private static final int[] UNSUPPORTED_MARKERS = {0xC3, 0xC5, 0xC6, 0xC7, 0xC8, 0xC9, 0xCA, 0xCB, 0xCD, 0xCE, 0xCF};

    /**
     * This is a type of marker.
     */
    private static final int NOPARAM_MARKER = 2;

    /**
     * Jpeg markers without additional parameters.
     */
    private static final int[] NOPARAM_MARKERS = {0xD0, 0xD1, 0xD2, 0xD3, 0xD4, 0xD5, 0xD6, 0xD7, 0xD8, 0x01};

    /**
     * Marker value
     */
    private static final int M_APP0 = 0xE0;
    /**
     * Marker value
     */
    private static final int M_APP2 = 0xE2;
    /**
     * Marker value
     */
    private static final int M_APPE = 0xEE;
    /**
     * Marker value for Photoshop IRB
     */
    private static final int M_APPD = 0xED;

    /**
     * sequence that is used in all Jpeg files
     */
    private static final byte[] JFIF_ID = {0x4A, 0x46, 0x49, 0x46, 0x00};

    /**
     * sequence preceding Photoshop resolution data
     */
    private static final byte[] PS_8BIM_RESO = {0x38, 0x42, 0x49, 0x4d, 0x03, (byte) 0xed};

    /**
     * Process the passed Image data as a JPEG image.
     * Image is loaded and all image attributes are initialized and/or updated.
     *
     * @param image the image to process as a JPEG image
     */
    public static void processImage(ImageData image) {
        if (image.getOriginalType() != ImageType.JPEG)
            throw new IllegalArgumentException("JPEG image expected");
        InputStream jpegStream = null;
        try {
            String errorID;
            if (image.getData() == null) {
                image.loadData();
                errorID = image.getUrl().toString();
            } else {
                errorID = "Byte array";
            }
            jpegStream = new java.io.ByteArrayInputStream(image.getData());
            image.imageSize = image.getData().length;
            processParameters(jpegStream, errorID, image);
        } catch (java.io.IOException e) {
            throw new IOException(IOException.JpegImageException, e);
        } finally {
            if (jpegStream != null) {
                try {
                    jpegStream.close();
                } catch (java.io.IOException ignore) { }
            }
        }
        updateAttributes(image);
    }

    private static void updateAttributes(ImageData image) {
        image.filter = "DCTDecode";
        if (image.getColorTransform() == 0) {
            Map<String, Object> decodeParms = new HashMap<>();
            decodeParms.put("ColorTransform", 0);
            image.decodeParms = decodeParms;
        }
        if (image.getColorSpace() != 1 && image.getColorSpace() != 3 && image.isInverted()) {
            image.decode = new float[]{1, 0, 1, 0, 1, 0, 1, 0};
        }
    }

    /**
     * This method checks if the image is a valid JPEG and processes some parameters.
     *
     * @throws IOException
     * @throws java.io.IOException
     */
    private static void processParameters(InputStream jpegStream, String errorID, ImageData image) throws java.io.IOException {
        byte[][] icc = null;
        if (jpegStream.read() != 0xFF || jpegStream.read() != 0xD8) {
            throw new IOException(IOException._1IsNotAValidJpegFile).setMessageParams(errorID);
        }
        boolean firstPass = true;
        int len;
        while (true) {
            int v = jpegStream.read();
            if (v < 0)
                throw new IOException(IOException.PrematureEofWhileReadingJpeg);
            if (v == 0xFF) {
                int marker = jpegStream.read();
                if (firstPass && marker == M_APP0) {
                    firstPass = false;
                    len = getShort(jpegStream);
                    if (len < 16) {
                        StreamUtil.skip(jpegStream, len - 2);
                        continue;
                    }
                    byte[] bcomp = new byte[JFIF_ID.length];
                    int r = jpegStream.read(bcomp);
                    if (r != bcomp.length)
                        throw new IOException(IOException._1CorruptedJfifMarker).setMessageParams(errorID);
                    boolean found = true;
                    for (int k = 0; k < bcomp.length; ++k) {
                        if (bcomp[k] != JFIF_ID[k]) {
                            found = false;
                            break;
                        }
                    }
                    if (!found) {
                        StreamUtil.skip(jpegStream, len - 2 - bcomp.length);
                        continue;
                    }
                    StreamUtil.skip(jpegStream, 2);
                    int units = jpegStream.read();
                    int dx = getShort(jpegStream);
                    int dy = getShort(jpegStream);
                    if (units == 1) {
                        image.setDpi(dx, dy);
                    } else if (units == 2) {
                        image.setDpi((int) (dx * 2.54f + 0.5f), (int) (dy * 2.54f + 0.5f));
                    }
                    StreamUtil.skip(jpegStream, len - 2 - bcomp.length - 7);
                    continue;
                }
                if (marker == M_APPE) {
                    len = getShort(jpegStream) - 2;
                    byte[] byteappe = new byte[len];
                    for (int k = 0; k < len; ++k) {
                        byteappe[k] = (byte) jpegStream.read();
                    }
                    if (byteappe.length >= 12) {
                        String appe = new String(byteappe, 0, 5, "ISO-8859-1");
                        if (appe.equals("Adobe")) {
                            image.setInverted(true);
                        }
                    }
                    continue;
                }
                if (marker == M_APP2) {
                    len = getShort(jpegStream) - 2;
                    byte[] byteapp2 = new byte[len];
                    for (int k = 0; k < len; ++k) {
                        byteapp2[k] = (byte) jpegStream.read();
                    }
                    if (byteapp2.length >= 14) {
                        String app2 = new String(byteapp2, 0, 11, "ISO-8859-1");
                        if (app2.equals("ICC_PROFILE")) {
                            int order = byteapp2[12] & 0xff;
                            int count = byteapp2[13] & 0xff;
                            // some jpeg producers don't know how to count to 1
                            if (order < 1)
                                order = 1;
                            if (count < 1)
                                count = 1;
                            if (icc == null)
                                icc = new byte[count][];
                            icc[order - 1] = byteapp2;
                        }
                    }
                    continue;
                }
                if (marker == M_APPD) {
                    len = getShort(jpegStream) - 2;
                    byte[] byteappd = new byte[len];
                    for (int k = 0; k < len; k++) {
                        byteappd[k] = (byte) jpegStream.read();
                    }
                    // search for '8BIM Resolution' marker
                    int k;
                    for (k = 0; k < len - PS_8BIM_RESO.length; k++) {
                        boolean found = true;
                        for (int j = 0; j < PS_8BIM_RESO.length; j++) {
                            if (byteappd[k + j] != PS_8BIM_RESO[j]) {
                                found = false;
                                break;
                            }
                        }
                        if (found)
                            break;
                    }

                    k += PS_8BIM_RESO.length;
                    if (k < len - PS_8BIM_RESO.length) {
                        // "PASCAL String" for name, i.e. string prefix with length byte
                        // padded to be even length; 2 null bytes if empty
                        byte namelength = byteappd[k];
                        // add length byte
                        namelength++;
                        // add padding
                        if (namelength % 2 == 1)
                            namelength++;
                        // just skip name
                        k += namelength;
                        // size of the resolution data
                        int resosize = (byteappd[k] << 24) + (byteappd[k + 1] << 16) + (byteappd[k + 2] << 8) + byteappd[k + 3];
                        // should be 16
                        if (resosize != 16) {
                            // fail silently, for now
                            //System.err.println("DEBUG: unsupported resolution IRB size");
                            continue;
                        }
                        k += 4;
                        int dx = (byteappd[k] << 8) + (byteappd[k + 1] & 0xff);
                        k += 2;
                        // skip 2 unknown bytes
                        k += 2;
                        int unitsx = (byteappd[k] << 8) + (byteappd[k + 1] & 0xff);
                        k += 2;
                        // skip 2 unknown bytes
                        k += 2;
                        int dy = (byteappd[k] << 8) + (byteappd[k + 1] & 0xff);
                        k += 2;
                        // skip 2 unknown bytes
                        k += 2;
                        int unitsy = (byteappd[k] << 8) + (byteappd[k + 1] & 0xff);

                        if (unitsx == 1 || unitsx == 2) {
                            dx = (unitsx == 2 ? (int) (dx * 2.54f + 0.5f) : dx);
                            // make sure this is consistent with JFIF data
                            if (image.getDpiX() != 0 && image.getDpiX() != dx) {
                                Logger logger = LoggerFactory.getLogger(JpegImageHelper.class);
                                logger.debug(MessageFormatUtil.format("Inconsistent metadata (dpiX: {0} vs {1})", image.getDpiX(), dx));
                            } else {
                                image.setDpi(dx, image.getDpiY());
                            }
                        }
                        if (unitsy == 1 || unitsy == 2) {
                            dy = (unitsy == 2 ? (int) (dy * 2.54f + 0.5f) : dy);
                            // make sure this is consistent with JFIF data
                            if (image.getDpiY() != 0 && image.getDpiY() != dy) {
                                Logger logger = LoggerFactory.getLogger(JpegImageHelper.class);
                                logger.debug(MessageFormatUtil.format("Inconsistent metadata (dpiY: {0} vs {1})", image.getDpiY(), dy));
                            } else {
                                image.setDpi(image.getDpiX(), dx);
                            }
                        }
                    }
                    continue;
                }
                firstPass = false;
                int markertype = marker(marker);
                if (markertype == VALID_MARKER) {
                    StreamUtil.skip(jpegStream, 2);
                    if (jpegStream.read() != 0x08) {
                        throw new IOException(IOException._1MustHave8BitsPerComponent).setMessageParams(errorID);
                    }
                    image.setHeight(getShort(jpegStream));
                    image.setWidth(getShort(jpegStream));
                    image.setColorSpace(jpegStream.read());
                    image.setBpc(8);
                    break;
                } else if (markertype == UNSUPPORTED_MARKER) {
                    throw new IOException(IOException._1UnsupportedJpegMarker2).setMessageParams(errorID, Integer.toString(marker));
                } else if (markertype != NOPARAM_MARKER) {
                    StreamUtil.skip(jpegStream, getShort(jpegStream) - 2);
                }
            }
        }
        if (icc != null) {
            int total = 0;
            for (int k = 0; k < icc.length; ++k) {
                if (icc[k] == null) {
                    icc = null;
                    return;
                }
                total += icc[k].length - 14;
            }
            byte[] ficc = new byte[total];
            total = 0;
            for (int k = 0; k < icc.length; ++k) {
                System.arraycopy(icc[k], 14, ficc, total, icc[k].length - 14);
                total += icc[k].length - 14;
            }
            try {
                image.setProfile(IccProfile.getInstance(ficc, image.getColorSpace()));
            } catch (IllegalArgumentException e) {
                // ignore ICC profile if it's invalid.
            }
        }
    }

    /**
     * Reads a short from the <CODE>InputStream</CODE>.
     *
     * @param jpegStream the <CODE>InputStream</CODE>
     * @return an int
     * @throws java.io.IOException
     */
    private static int getShort(InputStream jpegStream) throws java.io.IOException {
        return (jpegStream.read() << 8) + jpegStream.read();
    }

    /**
     * Returns a type of marker.
     *
     * @param marker an int
     * @return a type: <VAR>VALID_MARKER</CODE>, <VAR>UNSUPPORTED_MARKER</VAR> or <VAR>NOPARAM_MARKER</VAR>
     */
    private static int marker(int marker) {
        for (int i = 0; i < VALID_MARKERS.length; i++) {
            if (marker == VALID_MARKERS[i]) {
                return VALID_MARKER;
            }
        }
        for (int i = 0; i < NOPARAM_MARKERS.length; i++) {
            if (marker == NOPARAM_MARKERS[i]) {
                return NOPARAM_MARKER;
            }
        }
        for (int i = 0; i < UNSUPPORTED_MARKERS.length; i++) {
            if (marker == UNSUPPORTED_MARKERS[i]) {
                return UNSUPPORTED_MARKER;
            }
        }
        return NOT_A_MARKER;
    }
}
