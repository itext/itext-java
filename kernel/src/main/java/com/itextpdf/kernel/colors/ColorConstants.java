/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

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
package com.itextpdf.kernel.colors;

/**
 * Class containing predefined {@link DeviceRgb} colors.
 * Color space specific classes should be used for the advanced handling of colors.
 * The most common ones are {@link DeviceGray}, {@link DeviceCmyk} and {@link DeviceRgb}.
 */
public class ColorConstants {
    /**
     * Predefined black DeviceRgb color
     */
    public static final Color BLACK = DeviceRgb.BLACK;
    /**
     * Predefined blue  DeviceRgb color
     */
    public static final Color BLUE = DeviceRgb.BLUE;
    /**
     * Predefined cyan DeviceRgb color
     */
    public static final Color CYAN = new DeviceRgb(0, 255, 255);
    /**
     * Predefined dark gray DeviceRgb color
     */
    public static final Color DARK_GRAY = new DeviceRgb(64, 64, 64);
    /**
     * Predefined gray DeviceRgb color
     */
    public static final Color GRAY = new DeviceRgb(128, 128, 128);
    /**
     * Predefined green DeviceRgb color
     */
    public static final Color GREEN = DeviceRgb.GREEN;
    /**
     * Predefined light gray DeviceRgb color
     */
    public static final Color LIGHT_GRAY = new DeviceRgb(192, 192, 192);
    /**
     * Predefined magenta DeviceRgb color
     */
    public static final Color MAGENTA = new DeviceRgb(255, 0, 255);
    /**
     * Predefined orange DeviceRgb color
     */
    public static final Color ORANGE = new DeviceRgb(255, 200, 0);
    /**
     * Predefined pink DeviceRgb color
     */
    public static final Color PINK = new DeviceRgb(255, 175, 175);
    /**
     * Predefined red DeviceRgb color
     */
    public static final Color RED = DeviceRgb.RED;
    /**
     * Predefined white DeviceRgb color
     */
    public static final Color WHITE = DeviceRgb.WHITE;
    /**
     * Predefined yellow DeviceRgb color
     */
    public static final Color YELLOW = new DeviceRgb(255, 255, 0);
}
