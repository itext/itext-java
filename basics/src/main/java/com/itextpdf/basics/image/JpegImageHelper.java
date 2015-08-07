package com.itextpdf.basics.image;


import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.Utilities;
import com.itextpdf.basics.color.IccProfile;
import com.itextpdf.basics.io.ByteArrayOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class JpegImageHelper {

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

    public static void processImage(Image image, ByteArrayOutputStream stream) {
        if (image.getOriginalType() != Image.JPEG)
            throw new IllegalArgumentException("JPEG image expected");
        InputStream is = null;
        try {
            String errorID;
            if (image.getData() == null) {
                is = image.getUrl().openStream();
                errorID = image.getUrl().toString();
            } else {
                is = new java.io.ByteArrayInputStream(image.getData());
                errorID = "Byte array";
            }
            processParameters(is, errorID, image);
        } catch (IOException e) {
            throw new PdfException(PdfException.JpegImageException, e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ignore) { }
            }
        }

        if (stream != null) {
            updatePdfStream(stream, image);
        }
    }

    private static void updatePdfStream(ByteArrayOutputStream stream, Image image) {
        image.filter = "DCTDecode";
        if (image.getColorTransform() == 0) {
            HashMap<String, Object> decodeParms = new HashMap<>();
            decodeParms.put("ColorTransform", 0);
            image.decodeParms = decodeParms;
        }
        if (image.getColorSpace() != 1 && image.getColorSpace() != 3 && image.isInverted()) {
            image.decode = new float[]{1, 0, 1, 0, 1, 0, 1, 0};
        }

        if (image.getData() != null) {
            byte[] imgBytes = image.getData();
            stream.assignBytes(imgBytes, imgBytes.length);
        } else {
            InputStream is = null;
            try {
                is = image.getUrl().openStream();
                Utilities.transferBytes(is, stream);
            } catch (IOException e) {
                throw new PdfException(PdfException.JpegImageException, e);
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException ignored) { }
                }
            }
        }
    }

    /**
     * This method checks if the image is a valid JPEG and processes some parameters.
     *
     * @throws com.itextpdf.basics.PdfException
     * @throws java.io.IOException
     */
    private static void processParameters(InputStream is, String errorID, Image image) throws IOException {
        byte[][] icc = null;
        if (is.read() != 0xFF || is.read() != 0xD8) {
            throw new PdfException(PdfException._1IsNotAValidJpegFile).setMessageParams(errorID);
        }
        boolean firstPass = true;
        int len;
        while (true) {
            int v = is.read();
            if (v < 0)
                throw new PdfException(PdfException.PrematureEofWhileReadingJpg);
            if (v == 0xFF) {
                int marker = is.read();
                if (firstPass && marker == M_APP0) {
                    firstPass = false;
                    len = getShort(is);
                    if (len < 16) {
                        Utilities.skip(is, len - 2);
                        continue;
                    }
                    byte bcomp[] = new byte[JFIF_ID.length];
                    int r = is.read(bcomp);
                    if (r != bcomp.length)
                        throw new PdfException(PdfException._1CorruptedJfifMarker).setMessageParams(errorID);
                    boolean found = true;
                    for (int k = 0; k < bcomp.length; ++k) {
                        if (bcomp[k] != JFIF_ID[k]) {
                            found = false;
                            break;
                        }
                    }
                    if (!found) {
                        Utilities.skip(is, len - 2 - bcomp.length);
                        continue;
                    }
                    Utilities.skip(is, 2);
                    int units = is.read();
                    int dx = getShort(is);
                    int dy = getShort(is);
                    if (units == 1) {
                        image.setDpi(dx, dy);
                    } else if (units == 2) {
                        image.setDpi((int) (dx * 2.54f + 0.5f), (int) (dy * 2.54f + 0.5f));
                    }
                    Utilities.skip(is, len - 2 - bcomp.length - 7);
                    continue;
                }
                if (marker == M_APPE) {
                    len = getShort(is) - 2;
                    byte[] byteappe = new byte[len];
                    for (int k = 0; k < len; ++k) {
                        byteappe[k] = (byte) is.read();
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
                    len = getShort(is) - 2;
                    byte[] byteapp2 = new byte[len];
                    for (int k = 0; k < len; ++k) {
                        byteapp2[k] = (byte) is.read();
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
                    len = getShort(is) - 2;
                    byte[] byteappd = new byte[len];
                    for (int k = 0; k < len; k++) {
                        byteappd[k] = (byte) is.read();
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
                                logger.debug(String.format("Inconsistent metadata (dpiX: %d vs %d)", image.getDpiX(), dx));
                            } else {
                                image.setDpi(dx, image.getDpiY());
                            }
                        }
                        if (unitsy == 1 || unitsy == 2) {
                            dy = (unitsy == 2 ? (int) (dy * 2.54f + 0.5f) : dy);
                            // make sure this is consistent with JFIF data
                            if (image.getDpiY() != 0 && image.getDpiY() != dy) {
                                Logger logger = LoggerFactory.getLogger(JpegImageHelper.class);
                                logger.debug(String.format("Inconsistent metadata (dpiY: %d vs %d)", image.getDpiY(), dy));
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
                    Utilities.skip(is, 2);
                    if (is.read() != 0x08) {
                        throw new PdfException(PdfException._1MustHave8BitsPerComponent).setMessageParams(errorID);
                    }
                    image.setHeight(getShort(is));
                    image.setWidth(getShort(is));
                    image.setColorSpace(is.read());
                    image.setBpc(8);
                    break;
                } else if (markertype == UNSUPPORTED_MARKER) {
                    throw new PdfException(PdfException._1UnsupportedJpegMarker2).setMessageParams(errorID, String.valueOf(marker));
                } else if (markertype != NOPARAM_MARKER) {
                    Utilities.skip(is, getShort(is) - 2);
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
     * @param is the <CODE>InputStream</CODE>
     * @return an int
     * @throws java.io.IOException
     */
    private static int getShort(InputStream is) throws IOException {
        return (is.read() << 8) + is.read();
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
