package com.itextpdf.model.element;

import com.itextpdf.model.Document;

public interface ILargeElement<Type extends ILargeElement> extends IElement<Type> {

    boolean isComplete();

    void complete();

    void flush();

    void flushContent();

    void setDocument(Document document);
}
