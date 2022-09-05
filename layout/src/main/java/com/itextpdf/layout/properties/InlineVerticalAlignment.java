package com.itextpdf.layout.properties;

import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.layout.exceptions.LayoutExceptionMessageConstant;

/**
* A property corresponding to the css vertical-align property and used to
* set vertical alignment on inline blocks, it specifies the  type of alignment
* and where needed a numerical value to complete it.
*/
public class InlineVerticalAlignment {

    private InlineVerticalAlignmentType type;
    private float value;


    /**
     * Creates a default InlineVerticalAlignment, it gets the type {@link InlineVerticalAlignmentType#BASELINE}.
     */
    public InlineVerticalAlignment () {
        type = InlineVerticalAlignmentType.BASELINE;
    }

    /**
     * Creates an InlineVerticalAlignment with a specified type.
     *
     * @param type {@link InlineVerticalAlignmentType}
     */
    public InlineVerticalAlignment (InlineVerticalAlignmentType type) {
        this.type = type;
    }

    /**
     * Creates an InlineVerticalAlignment with a specified type and a value.
     * This will throw a {@link PdfException} when used with a type that does not require a value.
     *
     * @param type {@link InlineVerticalAlignmentType}
     * @param value In the case of {@link InlineVerticalAlignmentType#FIXED} a lenth in pts,
     *              in case of {@link InlineVerticalAlignmentType#FRACTION} a multiplier value.
     */
    public InlineVerticalAlignment (InlineVerticalAlignmentType type, float value) {
        if (!(type == InlineVerticalAlignmentType.FRACTION || type == InlineVerticalAlignmentType.FIXED)) {
            throw new PdfException(LayoutExceptionMessageConstant.INLINE_VERTICAL_ALIGNMENT_DOESN_T_NEED_A_VALUE)
                    .setMessageParams(type);

        }
        this.type = type;
        this.value = value;
    }

    /**
     * Gets the type of InlineVerticalAlignment.
     *
     * @return the type {@link InlineVerticalAlignmentType}
     */
    public InlineVerticalAlignmentType getType() {
        return type;
    }

    /**
     * Sets the type {@link InlineVerticalAlignmentType}.
     *
     * @param type {@link InlineVerticalAlignmentType}
     */
    public void setType(InlineVerticalAlignmentType type) {
        this.type = type;
    }

    /**
     * Gets the value.
     *
     * @return  value In the case of {@link InlineVerticalAlignmentType#FIXED} a lenth in pts,
     *              in case of {@link InlineVerticalAlignmentType#FRACTION} a multiplier value.
     */

    public float getValue() {
        return value;
    }

    /**
     * Sets the value.
     *
     * @param value In the case of {@link InlineVerticalAlignmentType#FIXED} a lenth in pts,
     *              in case of {@link InlineVerticalAlignmentType#FRACTION} a multiplier value.
     */
    public void setValue(float value) {
        this.value = value;
    }
}
