package com.itextpdf.layout.element;

import com.itextpdf.layout.renderer.ColumnContainerRenderer;
import com.itextpdf.layout.renderer.IRenderer;

import java.util.Map;

/**
 * represents a container of the column objects.
 */
public class ColumnContainer extends Div {

    /**
     * Creates new {@link ColumnContainer} instance.
     */
    public ColumnContainer() {
        super();
    }

    /**
     * Copies all properties of {@link ColumnContainer} to its child elements.
     */
    public void copyAllPropertiesToChildren() {
        for (final IElement child : this.getChildren()) {
            for (final Map.Entry<Integer, Object> entry : this.properties.entrySet()) {
                child.setProperty(entry.getKey(), entry.getValue());
            }
        }
    }

    @Override
    protected IRenderer makeNewRenderer() {
        return new ColumnContainerRenderer(this);
    }

}
