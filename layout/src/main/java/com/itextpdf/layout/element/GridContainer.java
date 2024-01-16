package com.itextpdf.layout.element;

import com.itextpdf.layout.renderer.GridContainerRenderer;
import com.itextpdf.layout.renderer.IRenderer;

/**
 * A {@link GridContainer} represents a container of the css grid object.
 */
public class GridContainer extends Div {

    /**
     * Creates a new {@link GridContainer} instance.
     */
    public GridContainer() {
        super();
    }

    @Override
    protected IRenderer makeNewRenderer() {
        return new GridContainerRenderer(this);
    }
}
