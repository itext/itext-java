package com.itextpdf.model.element;

import com.itextpdf.model.renderer.IRenderer;

public interface IElement{

    void setRenderer(IRenderer renderer);
    IRenderer makeRenderer();
    <T> T getProperty(Integer propertyKey);
    <T> T getDefaultProperty(Integer propertyKey);
    boolean isBreakable();

}
