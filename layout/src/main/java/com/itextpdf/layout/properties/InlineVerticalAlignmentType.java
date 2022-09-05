package com.itextpdf.layout.properties;

/**
 * The possible values for {@link InlineVerticalAlignment#getType()}.
 */
public enum InlineVerticalAlignmentType {
    // Strut oriented alignments
    BASELINE,
    TEXT_TOP,
    TEXT_BOTTOM,
    SUB,
    SUPER,
    /**
     * Fixed is used when a length value is given in css.
     * It needs a companion value in {@link InlineVerticalAlignment#setValue(float)}
     */
    FIXED,
    /**
     * Fixed is used when a percentage value is given in css.
     * It needs a companion value in {@link InlineVerticalAlignment#setValue(float)}
     */
    FRACTION,
    // middle of x height above baseline
    MIDDLE,
    // From here alignments are box oriented, the others are strut (text line) oriented
    TOP,
    BOTTOM
}
