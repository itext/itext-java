package com.itextpdf.model.element;

import com.itextpdf.model.Document;

public interface ILargeElement<Type extends ILargeElement> extends IElement<Type> {

    /**
     * Checks whether an element has already been marked as complete.
     */
    boolean isComplete();

    /**
     * Indicates that all the desired content has been added to this large element.
     */
    void complete();

    /**
     * Writes the newly added content to the document.
     */
    void flush();

    /**
     * Flushes the content which has just been added to the document.
     * This is a method for internal usage and is called automatically by the document.
     */
    void flushContent();

    /**
     * Sets the document this element is bound to.
     * We cannot write a large element into several documents simultaneously because we would need
     * more bulky interfaces for this feature. For now we went for simplicity.
     */
    void setDocument(Document document);
}
