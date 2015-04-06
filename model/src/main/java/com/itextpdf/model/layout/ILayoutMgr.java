package com.itextpdf.model.layout;

import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.basics.PdfException;
import com.itextpdf.model.elements.IElement;
import com.itextpdf.model.layout.shapes.ILayoutShape;

import java.util.List;

public interface ILayoutMgr {

    public static final int Flowing = 0;
    public static final int Fixed = 1;

    /**
     * Sets the canvas where to write to.
     *
     * @param canvas
     */
    public void setCanvas(PdfCanvas canvas);

    /**
     * Places the element to a document.
     * Layout manager decides if to write element to canvas immediately or cache it or whatever...
     *
     * @param element
     * @return
     */
    public IPlaceElementResult placeElement(IElement element);

    /**
     * Invoked in case if element cannot be placed by placeElement method because of insufficient space or any other reason.
     *
     * @param element
     * @return
     */
    public IPlaceElementResult overflow(IElement element) throws PdfException;

    /**
     * Sets a list of shapes for layout manager.
     * For example, if includes 2 boxes then elements first will be placed in first box, then in second one.
     *
     * @param shapes
     */
    public void setShapes(List<ILayoutShape> shapes);

    /**
     * Returns a list of shapes.
     *
     * @return
     */
    public List<ILayoutShape> getShapes();

}
