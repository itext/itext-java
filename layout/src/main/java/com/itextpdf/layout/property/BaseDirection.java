package com.itextpdf.layout.property;

import com.itextpdf.layout.IPropertyContainer;

/**
 * A specialized enum holding the possible values for a text {@link
 * com.itextpdf.layout.element.IElement}'s base direction. This class is meant to
 * be used as the value for the {@link Property#BASE_DIRECTION} key in an
 * {@link IPropertyContainer}.
 */
public enum BaseDirection {
    NO_BIDI,
    DEFAULT_BIDI,
    LEFT_TO_RIGHT,
    RIGHT_TO_LEFT
}
