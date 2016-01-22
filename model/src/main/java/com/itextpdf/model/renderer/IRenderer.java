package com.itextpdf.model.renderer;

import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.model.IPropertyContainer;
import com.itextpdf.model.Property;
import com.itextpdf.model.layout.LayoutArea;
import com.itextpdf.model.layout.LayoutContext;
import com.itextpdf.model.layout.LayoutResult;

import java.util.List;

public interface IRenderer extends IPropertyContainer<IRenderer> {

    /**
     * Adds a child to the current renderer
     * @param renderer a child to be added
     */
    void addChild(IRenderer renderer);

    /**
     * This method simulates positioning of the renderer, including all of its children, and returns
     * the {@link LayoutResult}, representing the layout result, including occupied area, status, i.e.
     * if there was enough place to fit the renderer subtree, etc.
     * {@link LayoutResult} can be extended to return custom layout results for custom elements, e.g.
     * {@link TextRenderer} uses {@link com.itextpdf.model.layout.TextLayoutResult} as its result.
     *
     * This method can be called standalone to learn how much area the renderer subtree needs, or can be called
     * before {@link #draw(DrawContext)}, to prepare the renderer to be flushed to the output stream.
     *
     * @param layoutContext the description of layout area and any other additional information
     * @return result of the layout process
     */
    LayoutResult layout(LayoutContext layoutContext);

    /**
     * Flushes the renderer subtree contents, i.e. draws itself on canvas,
     * adds necessary objects to the {@link PdfDocument} etc.
     * @param drawContext contains the {@link PdfDocument} to which the renderer subtree if flushed,
     *                    the {@link PdfCanvas} on which the renderer subtree is drawn and other additional parameters
     *                    needed to perform drawing
     */
    void draw(DrawContext drawContext);

    /**
     * Gets the resultant occupied area after the last call to the {@link #layout(LayoutContext)} method.
     * @return {@link LayoutArea} instance
     */
    LayoutArea getOccupiedArea();

    <T> T getProperty(Property property, T defaultValue);

    IRenderer setParent(IRenderer parent);

    IPropertyContainer getModelElement();

    List<IRenderer> getChildRenderers();

    /**
     * Indicates whether this renderer is flushed or not, i.e. if {@link #draw(DrawContext)} has already
     * been called.
     */
    boolean isFlushed();

    /**
     * Moves the renderer subtree by the specified offset. This method affects occupied area of the renderer.
     * @param dx the x-axis offset in points. Positive value will move the renderer subtree to the right.
     * @param dy the y-axis offset in points. Positive value will move the renderer subtree to the top.
     */
    void move(float dx, float dy);

    /**
     * Gets a new instance of this class to be used as a next renderer, after this renderer is used, if
     * {@link #layout(LayoutContext)} is called more than once.
     * @return new renderer instance
     */
    IRenderer getNextRenderer();
}
