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
package com.itextpdf.io.colors;

import com.itextpdf.io.exceptions.IOException;
import com.itextpdf.io.exceptions.IoExceptionMessageConstant;
import com.itextpdf.io.source.RandomAccessFileOrArray;
import com.itextpdf.io.source.RandomAccessSourceFactory;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;


/**
 * Class used to represented the International Color Consortium profile
 */
public class IccProfile {
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
            throw new IOException(IoExceptionMessageConstant.INVALID_ICC_PROFILE);
        IccProfile icc = new IccProfile();
        icc.data = data;
        Integer cs;
        cs = getIccNumberOfComponents(data);
        int nc = cs == null ? 0 : (int) cs;
        icc.numComponents = nc;
        // invalid ICC
        if (nc != numComponents) {
            throw new IOException(IoExceptionMessageConstant.ICC_PROFILE_CONTAINS_COMPONENTS_WHILE_THE_IMAGE_DATA_CONTAINS_COMPONENTS).setMessageParams(nc, numComponents);
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
                    throw new IOException(IoExceptionMessageConstant.INVALID_ICC_PROFILE);
                remain -= n;
                ptr += n;
            }
            if (head[36] != 0x61 || head[37] != 0x63
                    || head[38] != 0x73 || head[39] != 0x70) {
                throw new IOException(IoExceptionMessageConstant.INVALID_ICC_PROFILE);
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
                    throw new IOException(IoExceptionMessageConstant.INVALID_ICC_PROFILE);
                }
                remain -= n;
                ptr += n;
            }
            return getInstance(icc);
        } catch (Exception ex) {
            throw new IOException(IoExceptionMessageConstant.INVALID_ICC_PROFILE, ex);
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
            throw new IOException(IoExceptionMessageConstant.INVALID_ICC_PROFILE, e);
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
            throw new IOException(IoExceptionMessageConstant.INVALID_ICC_PROFILE, e);
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
            throw new IOException(IoExceptionMessageConstant.INVALID_ICC_PROFILE, e);
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
            throw new IOException(IoExceptionMessageConstant.INVALID_ICC_PROFILE, e);
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
