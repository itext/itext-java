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

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.tagging.IStructureNode;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This class is used to manage waiting tags state.
 * Any tag in the structure tree could be marked as "waiting". This state indicates that
 * tag is not yet finished and therefore should not be flushed or removed if page tags are
 * flushed or removed or if parent tags are flushed.
 * <p>
 * Waiting state of tags is defined by the association with arbitrary objects instances.
 * <p>
 * Waiting state could also be perceived as a temporal association of the object to some particular tag.
 */
public class WaitingTagsManager {

    private Map<Object, PdfStructElem> associatedObjToWaitingTag;
    private Map<PdfDictionary, Object> waitingTagToAssociatedObj;

    WaitingTagsManager() {
        associatedObjToWaitingTag = new HashMap<>();
        waitingTagToAssociatedObj = new HashMap<>();
    }

    /**
     * Assigns waiting state to the tag at which given {@link TagTreePointer} points, associating it with the given
     * {@link Object}. If current tag of the given {@link TagTreePointer} is already waiting, then after this method call
     * it's associated object will change to the one passed as the argument and the old one will not longer be
     * an associated object.
     * @param pointerToTag a {@link TagTreePointer} pointing at a tag which is desired to be marked as waiting.
     * @param associatedObj an object that is to be associated with the waiting tag. A null value is forbidden.
     * @return the previous associated object with the tag if it has already had waiting state,
     * or null if it was not waiting tag.
     */
    public Object assignWaitingState(TagTreePointer pointerToTag, Object associatedObj) {
        if (associatedObj == null) { throw new IllegalArgumentException("Passed associated object can not be null."); }
        return saveAssociatedObjectForWaitingTag(associatedObj, pointerToTag.getCurrentStructElem());
    }

    /**
     * Checks if there is waiting tag which state was assigned using given {@link Object}.
     * @param obj an {@link Object} which is to be checked if it is associated with any waiting tag. A null value is forbidden.
     * @return true if object is currently associated with some waiting tag.
     */
    public boolean isObjectAssociatedWithWaitingTag(Object obj) {
        if (obj == null) { throw new IllegalArgumentException("Passed associated object can not be null."); }
        return associatedObjToWaitingTag.containsKey(obj);
    }

    /**
     * Moves given {@link TagTreePointer} to the waiting tag which is associated with the given object.
     * If the passed object is not associated with any waiting tag, {@link TagTreePointer} position won't change.
     * @param tagPointer a {@link TagTreePointer} which position in the tree is to be changed to the
     *                   waiting tag in case of the successful call.
     * @param associatedObject an object which is associated with the waiting tag to which {@link TagTreePointer} is to be moved.
     * @return true if given object is actually associated with the waiting tag and {@link TagTreePointer} was moved
     * in order to point at it.
     */
    public boolean tryMovePointerToWaitingTag(TagTreePointer tagPointer, Object associatedObject) {
        if (associatedObject == null) return false;

        PdfStructElem waitingStructElem = associatedObjToWaitingTag.get(associatedObject);
        if (waitingStructElem != null) {
            tagPointer.setCurrentStructElem(waitingStructElem);
            return true;
        }
        return false;
    }

//    /**
//     * Gets an object that is associated with the tag (if there is one) at which given {@link TagTreePointer} points.
//     * Essentially, this method could be used as indication that current tag has waiting state.
//     * @param pointer a {@link TagTreePointer} which points at the tag for which associated object is to be retrieved.
//     * @return an object that is associated with the tag at which given {@link TagTreePointer} points, or null if
//     * current tag of the {@link TagTreePointer} is not a waiting tag.
//     */
//    public Object getAssociatedObject(TagTreePointer pointer) {
//        return getObjForStructDict(pointer.getCurrentStructElem().getPdfObject());
//    }

    /**
     * Removes waiting state of the tag which is associated with the given object.
     * <p>
     * NOTE: if parent of the waiting tag is already flushed, the tag and it's children
     * (unless they are waiting tags on their own) will be also immediately flushed right after
     * the waiting state removal.
     *
     * @param associatedObject an object which association with the waiting tag is to be removed.
     * @return true if object was actually associated with some tag and it's association was removed.
     */
    public boolean removeWaitingState(Object associatedObject) {
        if (associatedObject != null) {
            PdfStructElem structElem = associatedObjToWaitingTag.remove(associatedObject);
            removeWaitingStateAndFlushIfParentFlushed(structElem);
            return structElem != null;
        }
        return false;
    }

    /**
     * Removes waiting state of all waiting tags by removing association with objects.
     * <p>
     * NOTE: if parent of the waiting tag is already flushed, the tag and it's children
     * will be also immediately flushed right after the waiting state removal.
     */
    public void removeAllWaitingStates() {
        for (PdfStructElem structElem : associatedObjToWaitingTag.values()) {
            removeWaitingStateAndFlushIfParentFlushed(structElem);
        }
        associatedObjToWaitingTag.clear();
    }

    PdfStructElem getStructForObj(Object associatedObj) {
        return associatedObjToWaitingTag.get(associatedObj);
    }

    Object getObjForStructDict(PdfDictionary structDict) {
        return waitingTagToAssociatedObj.get(structDict);
    }

    Object saveAssociatedObjectForWaitingTag(Object associatedObj, PdfStructElem structElem) {
        associatedObjToWaitingTag.put(associatedObj, structElem);
        return waitingTagToAssociatedObj.put(structElem.getPdfObject(), associatedObj);
    }

    /**
     * @return parent of the flushed tag
     */
    IStructureNode flushTag(PdfStructElem tagStruct) {
        Object associatedObj = waitingTagToAssociatedObj.remove(tagStruct.getPdfObject());
        if (associatedObj != null) {
            associatedObjToWaitingTag.remove(associatedObj);
        }

        IStructureNode parent = tagStruct.getParent();
        flushStructElementAndItKids(tagStruct);
        return parent;
    }

    private void flushStructElementAndItKids(PdfStructElem elem) {
        if (waitingTagToAssociatedObj.containsKey(elem.getPdfObject())) {
            return;
        }

        TagTreeIterator iterator = new TagTreeIterator(elem,
                new WaitingTagsApprover(waitingTagToAssociatedObj.keySet()),
                TagTreeIterator.TreeTraversalOrder.POST_ORDER);
        iterator.addHandler(new TagTreeIteratorFlusher());
        iterator.traverse();
    }

    private void removeWaitingStateAndFlushIfParentFlushed(PdfStructElem structElem) {
        if (structElem != null) {
            waitingTagToAssociatedObj.remove(structElem.getPdfObject());
            IStructureNode parent = structElem.getParent();
            if (parent instanceof PdfStructElem && ((PdfStructElem) parent).isFlushed()) {
                flushStructElementAndItKids(structElem);
            }
        }
    }

    private static class WaitingTagsApprover extends TagTreeIteratorAvoidDuplicatesApprover {
        private final Set<PdfDictionary> waitingTags;
        public WaitingTagsApprover(Set<PdfDictionary> waitingTags) {
            super();
            this.waitingTags = waitingTags;
        }

        @Override
        public boolean approve(IStructureNode elem) {
            return super.approve(elem) && elem instanceof PdfStructElem &&
                    (waitingTags == null || !waitingTags.contains(((PdfStructElem) elem).getPdfObject()));
        }
    }
}
