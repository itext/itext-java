package com.itextpdf.styledxmlparser.css.util;

import com.itextpdf.styledxmlparser.css.CommonCssConstants;

public final class CssBackgroundUtils {

    /**
     * Creates a new {@link CssBackgroundUtils} instance.
     */
    private CssBackgroundUtils() {
    }

    /**
     * Gets background property name corresponding to its type.
     *
     * @param propertyType background property type
     * @return background property name
     */
    public static String getBackgroundPropertyNameFromType(BackgroundPropertyType propertyType) {
        switch (propertyType) {
            case BACKGROUND_COLOR:
                return CommonCssConstants.BACKGROUND_COLOR;
            case BACKGROUND_IMAGE:
                return CommonCssConstants.BACKGROUND_IMAGE;
            case BACKGROUND_POSITION:
                return CommonCssConstants.BACKGROUND_POSITION;
            case BACKGROUND_SIZE:
                return CommonCssConstants.BACKGROUND_SIZE;
            case BACKGROUND_REPEAT:
                return CommonCssConstants.BACKGROUND_REPEAT;
            case BACKGROUND_ORIGIN:
                return CommonCssConstants.BACKGROUND_ORIGIN;
            case BACKGROUND_CLIP:
                return CommonCssConstants.BACKGROUND_CLIP;
            case BACKGROUND_ATTACHMENT:
                return CommonCssConstants.BACKGROUND_ATTACHMENT;
            default:
                return CommonCssConstants.UNDEFINED_NAME;
        }
    }

    /**
     * Resolves the background property type using it's value.
     *
     * @param value the value
     * @return the background property type value
     */
    public static BackgroundPropertyType resolveBackgroundPropertyType(final String value) {
        final String url = "url(";
        if (value.startsWith(url) && value.indexOf('(', url.length()) == -1
                && value.indexOf(')') == value.length() - 1) {
            return BackgroundPropertyType.BACKGROUND_IMAGE;
        }
        if (CssGradientUtil.isCssLinearGradientValue(value) || CommonCssConstants.NONE.equals(value)) {
            return BackgroundPropertyType.BACKGROUND_IMAGE;
        }
        if (CommonCssConstants.BACKGROUND_REPEAT_VALUES.contains(value)) {
            return BackgroundPropertyType.BACKGROUND_REPEAT;
        }
        if (CommonCssConstants.BACKGROUND_ATTACHMENT_VALUES.contains(value)) {
            return BackgroundPropertyType.BACKGROUND_ATTACHMENT;
        }
        if (CommonCssConstants.BACKGROUND_POSITION_VALUES.contains(value)) {
            return BackgroundPropertyType.BACKGROUND_POSITION;
        }
        if ("0".equals(value) || CssUtils.isMetricValue(value) || CssUtils.isRelativeValue(value)) {
            return BackgroundPropertyType.BACKGROUND_POSITION_OR_SIZE;
        }
        if (CommonCssConstants.BACKGROUND_SIZE_VALUES.contains(value)) {
            return BackgroundPropertyType.BACKGROUND_SIZE;
        }
        if (CssUtils.isColorProperty(value)) {
            return BackgroundPropertyType.BACKGROUND_COLOR;
        }
        if (CommonCssConstants.BACKGROUND_ORIGIN_OR_CLIP_VALUES.contains(value)) {
            return BackgroundPropertyType.BACKGROUND_ORIGIN_OR_CLIP;
        }
        return BackgroundPropertyType.UNDEFINED;
    }

    public enum BackgroundPropertyType {
        BACKGROUND_COLOR,
        BACKGROUND_IMAGE,
        BACKGROUND_POSITION,
        BACKGROUND_SIZE,
        BACKGROUND_REPEAT,
        BACKGROUND_ORIGIN,
        BACKGROUND_CLIP,
        BACKGROUND_ATTACHMENT,
        BACKGROUND_POSITION_OR_SIZE,
        BACKGROUND_ORIGIN_OR_CLIP,
        UNDEFINED
    }
}
