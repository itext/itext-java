package com.itextpdf.kernel.pdf.tagutils;

import com.itextpdf.kernel.pdf.tagging.IStructureNode;

/**
 * Handler for {@link TagTreeIterator}.
 * Is used to handle specific events during the traversal.
 */
public interface ITagTreeIteratorHandler {

    /**
     * Called when the next element is reached during the traversal.
     *
     * @param elem the next element
     */
    void nextElement(IStructureNode elem);
}
