package com.itextpdf.model.element;

import com.itextpdf.model.IPropertyContainer;
import com.itextpdf.model.renderer.IRenderer;

public interface IElement extends IPropertyContainer{

    void setRenderer(IRenderer renderer);
    IRenderer makeRenderer();
    boolean isBreakable();

}
