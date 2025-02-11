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
package com.itextpdf.styledxmlparser.css.media;

import com.itextpdf.styledxmlparser.css.util.CssTypesValidationUtils;
import com.itextpdf.styledxmlparser.css.util.CssDimensionParsingUtils;
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
                Integer val = CssDimensionParsingUtils.parseInteger(value);
                if (minPrefix) {
                    return val != null && deviceDescription.getBitsPerComponent() >= val;
                } else if (maxPrefix) {
                    return val != null && deviceDescription.getBitsPerComponent() <= val;
                } else {
                    return val == null ? deviceDescription.getBitsPerComponent() != 0 : val == deviceDescription.getBitsPerComponent();
                }
            }
            case MediaFeature.COLOR_INDEX: {
                Integer val = CssDimensionParsingUtils.parseInteger(value);
                if (minPrefix) {
                    return val != null && deviceDescription.getColorIndex() >= val;
                } else if (maxPrefix) {
                    return val != null && deviceDescription.getColorIndex() <= val;
                } else {
                    return val == null ? deviceDescription.getColorIndex() != 0 : val == deviceDescription.getColorIndex();
                }
            }
            case MediaFeature.ASPECT_RATIO: {
                int[] aspectRatio = CssDimensionParsingUtils.parseAspectRatio(value);
                if (minPrefix) {
                    return aspectRatio != null && aspectRatio[0] * deviceDescription.getHeight() >= aspectRatio[1] * deviceDescription.getWidth();
                } else if (maxPrefix) {
                    return aspectRatio != null && aspectRatio[0] * deviceDescription.getHeight() <= aspectRatio[1] * deviceDescription.getWidth();
                } else {
                    return aspectRatio != null && CssUtils.compareFloats(aspectRatio[0] * deviceDescription.getHeight(), aspectRatio[1] * deviceDescription.getWidth());
                }
            }
            case MediaFeature.GRID: {
                Integer val = CssDimensionParsingUtils.parseInteger(value);
                return val != null && val == 0 && !deviceDescription.isGrid() || deviceDescription.isGrid();
            }
            case MediaFeature.SCAN: {
                return Objects.equals(value, deviceDescription.getScan());
            }
            case MediaFeature.ORIENTATION: {
                return Objects.equals(value, deviceDescription.getOrientation());
            }
            case MediaFeature.MONOCHROME: {
                Integer val = CssDimensionParsingUtils.parseInteger(value);
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
                float val = CssDimensionParsingUtils.parseResolution(value);
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
        if (CssTypesValidationUtils.isRelativeValue(value)) {
            // TODO DEVSIX-6365 Use some shared default value (from default.css or CssDefaults)
            //      rather than a constant of this class
            return CssDimensionParsingUtils.parseRelativeValue(value, DEFAULT_FONT_SIZE);
        } else {
            return CssDimensionParsingUtils.parseAbsoluteLength(value);
        }
    }
}
