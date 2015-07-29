package com.itextpdf.model.element;

public interface ILargeElement<Type extends ILargeElement> extends IElement<Type> {

    boolean isComplete();

    void complete();

    void flush();

   // IRenderer makePartialRenderer();

   // IRenderer createPartialRendererSubTree();
}
