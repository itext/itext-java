package com.itextpdf.core.pdf.canvas.wmf;

/**
 * A meta object.
 */
public class MetaObject {

    public static final int META_NOT_SUPPORTED = 0;
    public static final int META_PEN = 1;
    public static final int META_BRUSH = 2;
    public static final int META_FONT = 3;

    private int type = META_NOT_SUPPORTED;

    /**
     * Creates a new MetaObject. This constructor doesn't set the type.
     */
    public MetaObject() {
        // Empty body
    }

    /**
     * Creates a MetaObject with a type.
     *
     * @param type the type of meta object
     */
    public MetaObject(int type) {
        this.type = type;
    }

    /**
     * Get the type of this MetaObject.
     *
     * @return type of MetaObject
     */
    public int getType() {
        return type;
    }

}
