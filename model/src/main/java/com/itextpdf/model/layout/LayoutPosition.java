package com.itextpdf.model.layout;

/**
 * We use a simplified version of CSS positioning.
 * See https://www.webkit.org/blog/117/webcore-rendering-iv-absolutefixed-and-relative-positioning
 */
public class LayoutPosition {
    /**
     * Default positioning by normal rules of block and line layout.
     */
    public static final int STATIC = 1;

    /**
     * Relative positioning is exactly like static positioning except that the left, top, right and bottom properties
     * can be used to apply a translation to the object. Relative positioning is literally nothing more than a paint-time translation.
     * As far as layout is concerned, the object is at its original position.
     */
    public static final int RELATIVE = 2;

    // TODO
    public static final int ABSOLUTE = 3;

    /**
     * Fixed positioned objects are positioned relative to the viewport, i.e., the page area of the current page.
     */
    public static final int FIXED = 4;

}
