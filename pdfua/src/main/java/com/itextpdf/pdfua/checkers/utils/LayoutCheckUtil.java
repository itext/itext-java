package com.itextpdf.pdfua.checkers.utils;

import com.itextpdf.layout.element.Image;

/**
 * Utility class for delegating the layout checks to the correct checking logic.
 */
public final class LayoutCheckUtil {

    /**
     * Creates a new {@link LayoutCheckUtil} instance.
     */
    private LayoutCheckUtil() {
        // Empty constructor
    }

    /**
     * Checks if a layout element is valid against the PDF/UA specification.
     *
     * @param layoutElement layout element to check
     */
    public static void checkLayoutElements(Object layoutElement) {
        if (layoutElement instanceof Image) {
            GraphicsCheckUtil.checkLayoutImage((Image) layoutElement);
            return;
        }
    }
}
