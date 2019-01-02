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
package com.itextpdf.kernel.colors;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.kernel.pdf.colorspace.PdfDeviceCs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Color space to specify shades of gray color.
 */
public class DeviceGray extends Color {

    /**
     * Predefined white DeviceGray color.
     */
    public static final DeviceGray WHITE = new DeviceGray(1f);
    /**
     * Predefined gray DeviceGray color.
     */
    public static final DeviceGray GRAY = new DeviceGray(.5f);
    /**
     * Predefined black DeviceGray color.
     */
    public static final DeviceGray BLACK = new DeviceGray();

    private static final long serialVersionUID = 8307729543359242834L;

    /**
     * Creates DeviceGray color by given grayscale.
     * The grayscale is considered to be in [0, 1] interval, if not,
     * the grayscale will be considered as 1 (when grayscale's value is bigger than 1)
     * or 0 (when grayscale's value is less than 0).
     *
     * @param value the grayscale value
     */
    public DeviceGray(float value) {
        super(new PdfDeviceCs.Gray(), new float[] {value > 1 ? 1 : (value > 0 ? value : 0)});
        if (value > 1 || value < 0) {
            Logger LOGGER = LoggerFactory.getLogger(DeviceGray.class);
            LOGGER.warn(LogMessageConstant.COLORANT_INTENSITIES_INVALID);
        }
    }

    /**
     * Creates DeviceGray color with grayscale value initialised as zero.
     */
    public DeviceGray() {
        this(0f);
    }

    /**
     * Returns {@link DeviceGray DeviceGray} color which is lighter than given one
     * @param grayColor the DeviceGray color to be made lighter
     *
     * @return lighter color
     */
    public static DeviceGray makeLighter(DeviceGray grayColor) {
        float v = grayColor.getColorValue()[0];

        if (v == 0f)
            return new DeviceGray(0.3f);

        float multiplier = Math.min(1f, v + 0.33f) / v;

        return new DeviceGray(v * multiplier);
    }

    /**
     * Returns {@link DeviceGray DeviceGray} color which is darker than given one
     * @param grayColor the DeviceGray color to be made darker
     *
     * @return darker color
     */
    public static DeviceGray makeDarker(DeviceGray grayColor) {
        float v = grayColor.getColorValue()[0];
        float multiplier = Math.max(0f, (v - 0.33f) / v);

        return new DeviceGray(v * multiplier);
    }
}
