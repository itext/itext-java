package com.itextpdf.layout.property;

import com.itextpdf.layout.IPropertyContainer;

/**
 * A specialized enum holding the possible values for a {@link
 * com.itextpdf.layout.element.List List}'s entry prefix. This class is meant
 * to be used as the value for the {@link Property#LIST_SYMBOL} key in an
 * {@link IPropertyContainer}.
 */
public enum TabAlignment {
    LEFT,
    RIGHT,
    CENTER,
    ANCHOR
}
