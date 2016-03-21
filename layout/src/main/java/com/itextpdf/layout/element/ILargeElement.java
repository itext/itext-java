package com.itextpdf.layout.element;

import com.itextpdf.layout.Document;

/**
 * A {@link ILargeElement} is a layout element which may get added to
 * indefinitely, making the object prohibitively large.
 * In order to avoid consuming and holding on to undesirable amounts of
 * resources, the contents of a {@link ILargeElement} can be flushed regularly
 * by client code, e.g. at page boundaries or after a certain amount of additions.
 * 
 * @param <Type> the type of the implementation
 */
public interface ILargeElement<Type extends ILargeElement> extends IElement<Type> {

    /**
     * Checks whether an element has already been marked as complete.
     * @return the completion marker boolean
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
     * @param document the document
     */
    void setDocument(Document document);
}
