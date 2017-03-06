package com.itextpdf.layout.renderer;

public interface ILeafElementRenderer extends IRenderer {

    /**
     * Gets the maximum offset above the base line that this {@link ILeafElementRenderer} extends to.
     *
     * @return the upwards vertical offset of this {@link ILeafElementRenderer}
     */
    float getAscent();

    /**
     * Gets the maximum offset below the base line that this {@link ILeafElementRenderer} extends to.
     *
     * @return the downwards vertical offset of this {@link ILeafElementRenderer}
     */
    float getDescent();
}
