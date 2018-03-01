package com.itextpdf.svg.renderers;

import com.itextpdf.kernel.pdf.canvas.PdfCanvas;

import java.util.Stack;

/**
 * The SvgDrawContext keeps a stack of {@link PdfCanvas} instances, which
 * represent all levels of XObjects that are added to the root canvas.
 */
public class SvgDrawContext {

    private final Stack<PdfCanvas> canvases = new Stack<>();

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
     * @return the current size of the stack.
     */
    public int size() {
        return canvases.size();
    }
}
