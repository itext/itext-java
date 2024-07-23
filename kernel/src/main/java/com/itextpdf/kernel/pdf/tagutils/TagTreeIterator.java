/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.kernel.pdf.tagutils;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.pdf.tagging.IStructureNode;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class is used to traverse the tag tree.
 * <p>
 *
 * There is a possibility to add a handler that will be called for the elements during the traversal.
 */
public class TagTreeIterator {

    private final IStructureNode pointer;

    private final Set<ITagTreeIteratorHandler> handlerList;

    private final TagTreeIteratorElementApprover approver;

    private final TreeTraversalOrder traversalOrder;

    /**
     * Creates a new instance of {@link TagTreeIterator}. It will use {@link TagTreeIteratorElementApprover} to filter
     * elements and TreeTraversalOrder.PRE_ORDER for tree traversal.
     *
     * @param tagTreePointer the tag tree pointer.
     */
    public TagTreeIterator(IStructureNode tagTreePointer) {
        this(tagTreePointer, new TagTreeIteratorElementApprover(), TreeTraversalOrder.PRE_ORDER);
    }

    /**
     * Creates a new instance of {@link TagTreeIterator}.
     *
     * @param tagTreePointer the tag tree pointer.
     * @param approver a filter that will be called to let iterator know whether some particular element
     *                should be traversed or not.
     * @param traversalOrder an order in which the tree will be traversed.
     */
    public TagTreeIterator(IStructureNode tagTreePointer, TagTreeIteratorElementApprover approver,
                           TreeTraversalOrder traversalOrder) {
        if (tagTreePointer == null) {
            throw new IllegalArgumentException(
                    MessageFormatUtil.format(KernelExceptionMessageConstant.ARG_SHOULD_NOT_BE_NULL, "tagTreepointer"));
        }
        if (approver == null) {
            throw new IllegalArgumentException(
                    MessageFormatUtil.format(KernelExceptionMessageConstant.ARG_SHOULD_NOT_BE_NULL, "approver"));
        }
        if (traversalOrder == null) {
            throw new IllegalArgumentException(
                    MessageFormatUtil.format(KernelExceptionMessageConstant.ARG_SHOULD_NOT_BE_NULL, "traversalOrder"));
        }
        this.pointer = tagTreePointer;
        this.traversalOrder = traversalOrder;
        handlerList = new HashSet<>();
        this.approver = approver;
    }

    /**
     * Adds a handler that will be called for the elements during the traversal.
     *
     * @param handler the handler.
     *
     * @return this {@link TagTreeIterator} instance.
     */
    public TagTreeIterator addHandler(ITagTreeIteratorHandler handler) {
        if (handler == null) {
            throw new IllegalArgumentException(
                    MessageFormatUtil.format(KernelExceptionMessageConstant.ARG_SHOULD_NOT_BE_NULL, "handler"));
        }
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
        traverse(this.pointer);
    }

    private void traverse(IStructureNode elem) {
        if (!approver.approve(elem)) {
            return;
        }

        if (traversalOrder == TreeTraversalOrder.PRE_ORDER) {
            for (ITagTreeIteratorHandler handler : handlerList) {
                handler.nextElement(elem);
            }
        }

        List<IStructureNode> kids = elem.getKids();
        if (kids != null) {
            for (IStructureNode kid : kids) {
                traverse(kid);
            }
        }

        if (traversalOrder == TreeTraversalOrder.POST_ORDER) {
            for (ITagTreeIteratorHandler handler : handlerList) {
                handler.nextElement(elem);
            }
        }
    }

    /**
     * Tree traversal order enum.
     */
    public enum TreeTraversalOrder {
        /**
         * Preorder traversal.
         */
        PRE_ORDER,
        /**
         * Postorder traversal.
         */
        POST_ORDER
    }
}
