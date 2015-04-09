package com.itextpdf.model.element;

import com.itextpdf.model.renderer.IRenderer;

public interface IElement{

    void setRenderer(IRenderer renderer);
    IRenderer makeRenderer();
}
