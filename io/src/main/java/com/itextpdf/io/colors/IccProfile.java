/*
 *
 * This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
 * Authors: Bruno Lowagie, Paulo Soares, et al.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License version 3
 * as published by the Free Software Foundation with the addition of the
 * following permission added to Section 15 as permitted in Section 7(a):
 * FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
 * ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
 * OF THIRD PARTY RIGHTS
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses or write to
 * the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA, 02110-1301 USA, or download the license from the following URL:
 * http://itextpdf.com/terms-of-use/
 *
 * The interactive user interfaces in modified source and object code versions
 * of this program must display Appropriate Legal Notices, as required under
 * Section 5 of the GNU Affero General Public License.
 *
 * In accordance with Section 7(b) of the GNU Affero General Public License,
 * a covered work must retain the producer line in every PDF that is created
 * or manipulated using iText.
 *
 * You can be released from the requirements of the license by purchasing
 * a commercial license. Buying such a license is mandatory as soon as you
 * develop commercial activities involving the iText software without
 * disclosing the source code of your own applications.
 * These activities include: offering paid services to customers as an ASP,
 * serving PDFs on the fly in a web application, shipping iText with a closed
 * source product.
 *
 * For more information, please contact iText Software Corp. at this
 * address: sales@itextpdf.com
 */
package com.itextpdf.io.colors;

import com.itextpdf.io.IOException;
import com.itextpdf.io.source.RandomAccessFileOrArray;
import com.itextpdf.io.source.RandomAccessSourceFactory;

import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;


/**
 * Class used to represented the International Color Consortium profile
 */
public class IccProfile implements Serializable {
    private static final long serialVersionUID = -7466035855770591929L;
    protected byte[] data;
    protected int numComponents;
    private static Map<String, Integer> cstags = new HashMap<>();

    protected IccProfile() {
    }

    /**
     * Construct an icc profile from the passed byte[], using the passed number of components.
     *
     * @param data byte[] containing the raw icc profile data
     * @param numComponents number of components the profile contains
     * @return IccProfile constructed from the data
     *
     * @throws IOException when the specified number of components and the number of components in the created profile do not match.
     */
    public static IccProfile getInstance(byte[] data, int numComponents) {
        if (data.length < 128 || data[36] != 0x61 || data[37] != 0x63
                || data[38] != 0x73 || data[39] != 0x70)
            throw new IOException(IOException.InvalidIccProfile);
        IccProfile icc = new IccProfile();
        icc.data = data;
        Integer cs;
        cs = getIccNumberOfComponents(data);
        int nc = cs == null ? 0 : (int) cs;
        icc.numComponents = nc;
        // invalid ICC
        if (nc != numComponents) {
            throw new com.itextpdf.io.IOException(IOException.IccProfileContains0ComponentsWhileImageDataContains1Components).setMessageParams(nc, numComponents);
        }
        return icc;
    }

    /**
     * Construct an icc profile from the passed byte[], using the passed number of components.
     *
     * @param data byte[] containing the raw icc profile data
     * @return IccProfile constructed from the data
     */
    public static IccProfile getInstance(byte[] data) {
        Integer cs;
        cs = getIccNumberOfComponents(data);
        int numComponents = cs == null ? 0 : (int) cs;
        return getInstance(data, numComponents);
    }

    /**
     * Construct an icc profile from the passed random-access file or array.
     *
     * @param file random-access file or array containing the profile
     * @return IccProfile constructed from the data
     *
     * @throws IOException if the source does not contain a valid icc profile
     */
    public static IccProfile getInstance(RandomAccessFileOrArray file) {
        try {
            byte[] head = new byte[128];
            int remain = head.length;
            int ptr = 0;
            while (remain > 0) {
                int n = file.read(head, ptr, remain);
                if (n < 0)
                    throw new com.itextpdf.io.IOException(IOException.InvalidIccProfile);
                remain -= n;
                ptr += n;
            }
            if (head[36] != 0x61 || head[37] != 0x63
                    || head[38] != 0x73 || head[39] != 0x70) {
                throw new com.itextpdf.io.IOException(IOException.InvalidIccProfile);
            }
            remain = (head[0] & 0xff) << 24 | (head[1] & 0xff) << 16
                    | (head[2] & 0xff) << 8 | head[3] & 0xff;
            byte[] icc = new byte[remain];
            System.arraycopy(head, 0, icc, 0, head.length);
            remain -= head.length;
            ptr = head.length;
            while (remain > 0) {
                int n = file.read(icc, ptr, remain);
                if (n < 0) {
                    throw new com.itextpdf.io.IOException(IOException.InvalidIccProfile);
                }
                remain -= n;
                ptr += n;
            }
            return getInstance(icc);
        } catch (Exception ex) {
            throw new com.itextpdf.io.IOException(IOException.InvalidIccProfile, ex);
        }
    }

    /**
     * Construct an icc profile from the passed InputStream.
     *
     * @param stream inputstream containing the profile
     * @return IccProfile constructed from the data
     *
     * @throws IOException if the source does not contain a valid icc profile
     */
    public static IccProfile getInstance(InputStream stream) {
        RandomAccessFileOrArray raf;
        try {
            raf = new RandomAccessFileOrArray(
                    new RandomAccessSourceFactory().createSource(stream));
        } catch (java.io.IOException e) {
            throw new com.itextpdf.io.IOException(IOException.InvalidIccProfile, e);
        }
        return getInstance(raf);
    }

    /**
     * Construct an icc profile from the file found at the passed path
     *
     * @param filename path to the file contaning the profile
     * @return IccProfile constructed from the data
     * @throws IOException if the source does not contain a valid icc profile
     */
    public static IccProfile getInstance(String filename) {
        RandomAccessFileOrArray raf;
        try {
            raf = new RandomAccessFileOrArray(
                    new RandomAccessSourceFactory().createBestSource(filename));
        } catch (java.io.IOException e) {
            throw new com.itextpdf.io.IOException(IOException.InvalidIccProfile, e);
        }
        return getInstance(raf);
    }

    /**
     * Get the Color space name of the icc profile found in the data.
     *
     * @param data byte[] containing the icc profile
     * @return String containing the color space of the profile
     * @throws IOException if the source does not contain a valid icc profile
     */
    public static String getIccColorSpaceName(byte[] data) {
        String colorSpace;
        try {
            colorSpace = new String(data, 16, 4, "US-ASCII");
        } catch (UnsupportedEncodingException e) {
            throw new com.itextpdf.io.IOException(IOException.InvalidIccProfile, e);
        }
        return colorSpace;
    }

    /**
     * Get the device class of the icc profile found in the data.
     *
     * @param data byte[] containing the icc profile
     * @return String containing the device class of the profile
     * @throws IOException if the source does not contain a valid icc profile
     */
    public static String getIccDeviceClass(byte[] data) {
        String deviceClass;
        try {
            deviceClass = new String(data, 12, 4, "US-ASCII");
        } catch (UnsupportedEncodingException e) {
            throw new com.itextpdf.io.IOException(IOException.InvalidIccProfile, e);
        }
        return deviceClass;
    }

    /**
     * Get the number of color components of the icc profile found in the data.
     *
     * @param data byte[] containing the icc profile
     * @return Number of color components
     */
    public static Integer getIccNumberOfComponents(byte[] data) {
        return cstags.get(getIccColorSpaceName(data));
    }

    /**
     * Get the icc color profile data.
     *
     * @return byte[] containing the data
     */
    public byte[] getData() {
        return data;
    }

    /**
     * Get the number of color components in the profile.
     *
     * @return number of components
     */
    public int getNumComponents() {
        return numComponents;
    }

    static {
        cstags.put("XYZ ", 3);
        cstags.put("Lab ", 3);
        cstags.put("Luv ", 3);
        cstags.put("YCbr", 3);
        cstags.put("Yxy ", 3);
        cstags.put("RGB ", 3);
        cstags.put("GRAY", 1);
        cstags.put("HSV ", 3);
        cstags.put("HLS ", 3);
        cstags.put("CMYK", 4);
        cstags.put("CMY ", 3);
        cstags.put("2CLR", 2);
        cstags.put("3CLR", 3);
        cstags.put("4CLR", 4);
        cstags.put("5CLR", 5);
        cstags.put("6CLR", 6);
        cstags.put("7CLR", 7);
        cstags.put("8CLR", 8);
        cstags.put("9CLR", 9);
        cstags.put("ACLR", 10);
        cstags.put("BCLR", 11);
        cstags.put("CCLR", 12);
        cstags.put("DCLR", 13);
        cstags.put("ECLR", 14);
        cstags.put("FCLR", 15);
    }
}
