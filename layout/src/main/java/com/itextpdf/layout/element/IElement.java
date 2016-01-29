package com.itextpdf.layout.element;

import com.itextpdf.layout.IPropertyContainer;
import com.itextpdf.layout.renderer.IRenderer;

public interface IElement<Type extends IElement> extends IPropertyContainer<Type> {

    /**
     * Overrides the {@see IRenderer} instance which will be returned by the next call to the {@link #getRenderer()}.
     * @param renderer the renderer instance
     */
    void setNextRenderer(IRenderer renderer);

    /**
     * Gets a renderer for this element. Note that this method can be called more than once.
     * By default each element should define its own renderer, but the renderer can be overridden by
     * {@link #setNextRenderer(IRenderer)} method call.
     * @return a renderer for this element
     */
    IRenderer getRenderer();

    /**
     * Creates a renderer subtree with root in the current element.
     * Compared to {@link #getRenderer()}, the renderer returned by this method should contain all the child
     * renderers for children of the current element.
     * @return a renderer subtree for this element
     */
    IRenderer createRendererSubTree();
}
