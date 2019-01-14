/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.kernel.pdf.tagutils;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.tagging.IStructureNode;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class is used to manage waiting tags state.
 * Any tag in the structure tree could be marked as "waiting". This state indicates that
 * tag is not yet finished and therefore should not be flushed or removed if page tags are
 * flushed or removed or if parent tags are flushed.
 * </p>
 * <p>
 * Waiting state of tags is defined by the association with arbitrary objects instances.
 * </p>
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
     * <p>NOTE: if parent of the waiting tag is already flushed, the tag and it's children
     * (unless they are waiting tags on their own) will be also immediately flushed right after
     * the waiting state removal.</p>
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
     *
     * <p>NOTE: if parent of the waiting tag is already flushed, the tag and it's children
     * will be also immediately flushed right after the waiting state removal.</p>
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

        for (IStructureNode kid : elem.getKids()) {
            if (kid instanceof PdfStructElem) {
                flushStructElementAndItKids((PdfStructElem) kid);
            }
        }
        elem.flush();
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
}
