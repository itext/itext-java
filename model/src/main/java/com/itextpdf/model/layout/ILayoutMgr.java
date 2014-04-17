package com.itextpdf.model.layout;

import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.model.elements.IElement;
import com.itextpdf.model.events.IEventDispatcher;
import com.itextpdf.model.layout.shapes.ILayoutShape;

import java.util.List;

public interface ILayoutMgr {

    public static final int Flowing = 0;
    public static final int Fixed = 1;

    public void setCanvas(PdfCanvas canvas);
    public IPlaceElementResult placeElement(IElement element);
    public IPlaceElementResult overflow(IElement element);

    public void setShapes(List<ILayoutShape> shapes);
    public List<ILayoutShape> getShapes();

}
