package com.itextpdf.model.element;

import com.itextpdf.model.IPropertyContainer;
import com.itextpdf.model.renderer.IRenderer;

public interface IElement<Type extends IElement> extends IPropertyContainer<Type> {

    void setNextRenderer(IRenderer renderer);
    IRenderer makeRenderer();
    IRenderer createRendererSubTree();
    boolean isBreakable();

}
