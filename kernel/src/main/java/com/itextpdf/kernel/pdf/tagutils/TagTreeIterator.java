package com.itextpdf.kernel.pdf.tagutils;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.pdf.tagging.IStructureNode;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * This class is used to traverse the tag tree.
 * <p>
 *
 * There is a possibility to add a handler that will be called for specific events during the traversal.
 */
public class TagTreeIterator {


    private final IStructureNode pointer;

    private final Set<ITagTreeIteratorHandler> handlerList;

    /**
     * Creates a new instance of {@link TagTreeIterator}.
     *
     * @param tagTreePointer the tag tree pointer.
     */
    public TagTreeIterator(IStructureNode tagTreePointer) {
        if (tagTreePointer == null) {
            throw new IllegalArgumentException(
                    MessageFormatUtil.format(KernelExceptionMessageConstant.ARG_SHOULD_NOT_BE_NULL, "tagTreepointer"));
        }
        this.pointer = tagTreePointer;
        handlerList = new HashSet<>();
    }

    /**
     * Adds a handler that will be called for specific events during the traversal.
     *
     * @param handler the handler.
     *
     * @return this {@link TagTreeIterator} instance.
     */
    public TagTreeIterator addHandler(ITagTreeIteratorHandler handler) {
        this.handlerList.add(handler);
        return this;
    }

    /**
     * Traverses the tag tree in the order of the document structure.
     * <p>
     *
     * Make sure the correct handlers are added before calling this method.
     */
    public void traverse() {
        traverse(this.pointer, this.handlerList);
    }

    private static void traverse(IStructureNode elem, Set<ITagTreeIteratorHandler> handlerList) {
        for (ITagTreeIteratorHandler handler : handlerList) {
            handler.nextElement(elem);
        }
        List<IStructureNode> kids = elem.getKids();
        if (kids != null) {
            for (IStructureNode kid : kids) {
                traverse(kid, handlerList);
            }
        }
    }

}
