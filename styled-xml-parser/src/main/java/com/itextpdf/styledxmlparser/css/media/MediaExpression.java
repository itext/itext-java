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
package com.itextpdf.styledxmlparser.css.media;

import com.itextpdf.styledxmlparser.css.util.CssUtils;
import java.util.Objects;

/**
 * Class that bundles all the media expression properties.
 */
public class MediaExpression {

    /**
     * The default font size.
     */
    private static final float DEFAULT_FONT_SIZE = 12;

    /**
     * Indicates if there's a "min-" prefix.
     */
    private boolean minPrefix;

    /**
     * Indicates if there's a "max-" prefix.
     */
    private boolean maxPrefix;

    /**
     * The feature.
     */
    private String feature;

    /**
     * The value.
     */
    private String value;

    /**
     * Creates a new {@link MediaExpression} instance.
     *
     * @param feature the feature
     * @param value   the value
     */
    MediaExpression(String feature, String value) {
        this.feature = feature.trim().toLowerCase();
        if (value != null) {
            this.value = value.trim().toLowerCase();
        }

        String minPref = MediaRuleConstants.MIN + "-";
        String maxPref = MediaRuleConstants.MAX + "-";
        minPrefix = feature.startsWith(minPref);
        if (minPrefix) {
            this.feature = feature.substring(minPref.length());
        }
        maxPrefix = feature.startsWith(maxPref);
        if (maxPrefix) {
            this.feature = feature.substring(maxPref.length());
        }
    }

    /**
     * Tries to match a {@link MediaDeviceDescription}.
     *
     * @param deviceDescription the device description
     * @return true, if successful
     */
    public boolean matches(MediaDeviceDescription deviceDescription) {
        switch (feature) {
            case MediaFeature.COLOR: {
                Integer val = CssUtils.parseInteger(value);
                if (minPrefix) {
                    return val != null && deviceDescription.getBitsPerComponent() >= val;
                } else if (maxPrefix) {
                    return val != null && deviceDescription.getBitsPerComponent() <= val;
                } else {
                    return val == null ? deviceDescription.getBitsPerComponent() != 0 : val == deviceDescription.getBitsPerComponent();
                }
            }
            case MediaFeature.COLOR_INDEX: {
                Integer val = CssUtils.parseInteger(value);
                if (minPrefix) {
                    return val != null && deviceDescription.getColorIndex() >= val;
                } else if (maxPrefix) {
                    return val != null && deviceDescription.getColorIndex() <= val;
                } else {
                    return val == null ? deviceDescription.getColorIndex() != 0 : val == deviceDescription.getColorIndex();
                }
            }
            case MediaFeature.ASPECT_RATIO: {
                int[] aspectRatio = CssUtils.parseAspectRatio(value);
                if (minPrefix) {
                    return aspectRatio != null && aspectRatio[0] * deviceDescription.getHeight() >= aspectRatio[1] * deviceDescription.getWidth();
                } else if (maxPrefix) {
                    return aspectRatio != null && aspectRatio[0] * deviceDescription.getHeight() <= aspectRatio[1] * deviceDescription.getWidth();
                } else {
                    return aspectRatio != null && CssUtils.compareFloats(aspectRatio[0] * deviceDescription.getHeight(), aspectRatio[1] * deviceDescription.getWidth());
                }
            }
            case MediaFeature.GRID: {
                Integer val = CssUtils.parseInteger(value);
                return val != null && val == 0 && !deviceDescription.isGrid() || deviceDescription.isGrid();
            }
            case MediaFeature.SCAN: {
                return Objects.equals(value, deviceDescription.getScan());
            }
            case MediaFeature.ORIENTATION: {
                return Objects.equals(value, deviceDescription.getOrientation());
            }
            case MediaFeature.MONOCHROME: {
                Integer val = CssUtils.parseInteger(value);
                if (minPrefix) {
                    return val != null && deviceDescription.getMonochrome() >= val;
                } else if (maxPrefix) {
                    return val != null && deviceDescription.getMonochrome() <= val;
                } else {
                    return val == null ? deviceDescription.getMonochrome() > 0 : val == deviceDescription.getMonochrome();
                }
            }
            case MediaFeature.HEIGHT: {
                float val = parseAbsoluteLength(value);
                if (minPrefix) {
                    return deviceDescription.getHeight() >= val;
                } else if (maxPrefix) {
                    return deviceDescription.getHeight() <= val;
                } else {
                    return deviceDescription.getHeight() > 0;
                }
            }
            case MediaFeature.WIDTH: {
                float val = parseAbsoluteLength(value);
                if (minPrefix) {
                    return deviceDescription.getWidth() >= val;
                } else if (maxPrefix) {
                    return deviceDescription.getWidth() <= val;
                } else {
                    return deviceDescription.getWidth() > 0;
                }
            }
            case MediaFeature.RESOLUTION: {
                float val = CssUtils.parseResolution(value);
                if (minPrefix) {
                    return deviceDescription.getResolution() >= val;
                } else if (maxPrefix) {
                    return deviceDescription.getResolution() <= val;
                } else {
                    return deviceDescription.getResolution() > 0;
                }
            }
            default:
                return false;
        }
    }

    /**
     * Parses an absolute length.
     *
     * @param value the absolute length as a {@link String} value
     * @return the absolute length as a {@code float} value
     */
    private static float parseAbsoluteLength(String value) {
        if (CssUtils.isRelativeValue(value)) {
            // TODO here should be used default font size of the browser, it probably should be fetched from the more generic place than private class constant
            return CssUtils.parseRelativeValue(value, DEFAULT_FONT_SIZE);
        } else {
            return CssUtils.parseAbsoluteLength(value);
        }
    }
}
