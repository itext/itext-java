package com.itextpdf.svg.renderers;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.svg.exceptions.SvgLogMessageConstant;
import com.itextpdf.svg.exceptions.SvgProcessingException;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * The SvgDrawContext keeps a stack of {@link PdfCanvas} instances, which
 * represent all levels of XObjects that are added to the root canvas.
 */
public class SvgDrawContext {

    private final Map<String, Object> namedObjects = new HashMap<>();

    private final Stack<PdfCanvas> canvases = new Stack<>();
    private final Stack<Rectangle> viewports = new Stack<>();

    /**
     * Retrieves the current top of the stack, without modifying the stack.
     *
     * @return the current canvas that can be used for drawing operations.
     */
    public PdfCanvas getCurrentCanvas() {
        return canvases.peek();
    }

    /**
     * Retrieves the current top of the stack, thereby taking the current item
     * off the stack.
     *
     * @return the current canvas that can be used for drawing operations.
     */
    public PdfCanvas popCanvas() {
        return canvases.pop();
    }

    /**
     * Adds a {@link PdfCanvas} to the stack (by definition its top), for use in
     * drawing operations.
     *
     * @param canvas the new top of the stack
     */
    public void pushCanvas(PdfCanvas canvas) {
        canvases.push(canvas);
    }

    /**
     * Get the current size of the stack, signifying the nesting level of the
     * XObjects.
     *
     * @return the current size of the stack.
     */
    public int size() {
        return canvases.size();
    }

    /**
     * Adds a viewbox to the context.
     *
     * @param viewPort rectangle representing the current viewbox
     */
    public void addViewPort(Rectangle viewPort) {
        this.viewports.push(viewPort);
    }

    /**
     * Get the current viewbox.
     *
     * @return the viewbox as it is currently set
     */
    public Rectangle getCurrentViewPort() {
        return this.viewports.peek();
    }

    /**
     * Remove the currently set view box.
     */
    public void removeCurrentViewPort() {
        if (this.viewports.size() > 0) {
            this.viewports.pop();
        }
    }

    /**
     * Adds a named object to the draw context. These objects can then be referenced from a different tag.
     *
     * @param name name of the object
     * @param namedObject object to be referenced
     */
    public void addNamedObject(String name, Object namedObject) {
        if ( namedObject == null ) {
            throw new SvgProcessingException(SvgLogMessageConstant.NAMED_OBJECT_NULL);
        }

        if ( name == null || name.isEmpty() ) {
            throw new SvgProcessingException(SvgLogMessageConstant.NAMED_OBJECT_NAME_NULL_OR_EMPTY);
        }

        if (!this.namedObjects.containsKey(name) || namedObject instanceof PdfFormXObject) {
            this.namedObjects.put(name, namedObject);
        }
    }

    /**
     * Get a named object based on its name. If the name isn't listed, this method will return null.
     *
     * @param name name of the object you want to reference
     * @return the referenced object
     */
    public Object getNamedObject(String name) {
        return this.namedObjects.get(name);
    }
}
