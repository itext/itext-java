/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

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

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfIndirectReference;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.tagging.IStructureNode;
import com.itextpdf.kernel.pdf.tagging.PdfMcr;
import com.itextpdf.kernel.pdf.tagging.PdfMcrDictionary;
import com.itextpdf.kernel.pdf.tagging.PdfMcrNumber;
import com.itextpdf.kernel.pdf.tagging.PdfNamespace;
import com.itextpdf.kernel.pdf.tagging.PdfObjRef;
import com.itextpdf.kernel.pdf.tagging.PdfStructTreeRoot;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link TagTreePointer} class is used to modify the document's tag tree. At any given moment, instance of this class
 * 'points' at the specific position in the tree (at the specific tag), however every instance can be freely moved around
 * the tree primarily using {@link #moveToKid} and {@link #moveToParent()} methods. For the current tag you can add new tags,
 * modify it's role and properties, etc. Also, using instance of this class, you can change tag position in the tag structure,
 * you can flush current tag or remove it.
 * <br><br>
 * <p>
 * There could be any number of the instances of this class, simultaneously pointing to different (or the same) parts of
 * the tag structure. Because of this, you can for example remove the tag at which another instance is currently pointing.
 * In this case, this another instance becomes invalid, and invocation of any method on it will result in exception. To make
 * given instance valid again, use {@link #moveToRoot()} method.
 */
public class TagTreePointer {

    private static final String MCR_MARKER = "MCR";

    private TagStructureContext tagStructureContext;
    private PdfStructElem currentStructElem;
    private PdfPage currentPage;
    private PdfStream contentStream;

    private PdfNamespace currentNamespace;

    // '-1' value of this field means that next new kid will be the last element in the kids array
    private int nextNewKidIndex = -1;

    /**
     * Creates {@code TagTreePointer} instance. After creation {@code TagTreePointer} points at the root tag.
     * <p>
     * The {@link PdfNamespace} for the new tags, which don't explicitly define namespace by the means of
     * {@link DefaultAccessibilityProperties#setNamespace(PdfNamespace)}, is set to the value returned by
     * {@link TagStructureContext#getDocumentDefaultNamespace()} on {@link TagTreePointer} creation.
     * See also {@link TagTreePointer#setNamespaceForNewTags(PdfNamespace)}.
     * </p>
     * @param document the document, at which tag structure this instance will point.
     */
    public TagTreePointer(PdfDocument document) {
        tagStructureContext = document.getTagStructureContext();
        setCurrentStructElem(tagStructureContext.getRootTag());
        setNamespaceForNewTags(tagStructureContext.getDocumentDefaultNamespace());
    }

    /**
     * A copy constructor.
     *
     * @param tagPointer the {@code TagTreePointer} from which current position and page are copied.
     */
    public TagTreePointer(TagTreePointer tagPointer) {
        this.tagStructureContext = tagPointer.tagStructureContext;
        setCurrentStructElem(tagPointer.getCurrentStructElem());
        this.currentPage = tagPointer.currentPage;
        this.contentStream = tagPointer.contentStream;
        this.currentNamespace = tagPointer.currentNamespace;
    }

    TagTreePointer(PdfStructElem structElem, PdfDocument document) {
        tagStructureContext = document.getTagStructureContext();
        setCurrentStructElem(structElem);
    }

    /**
     * Sets a page which content will be tagged with this instance of {@code TagTreePointer}.
     * To tag page content:
     * <ol>
     * <li>Set pointer position to the tag which will be the parent of the page content item;</li>
     * <li>Call {@link #getTagReference()} to obtain the reference to the current tag;</li>
     * <li>Pass {@link TagReference} to the {@link PdfCanvas#openTag(TagReference)} method of the page's {@link PdfCanvas} to start marked content item;</li>
     * <li>Draw content on {@code PdfCanvas};</li>
     * <li>Use {@link PdfCanvas#closeTag()} to finish marked content item.</li>
     * </ol>
     *
     * @param page the page which content will be tagged with this instance of {@code TagTreePointer}.
     * @return this {@link TagTreePointer} instance.
     */
    public TagTreePointer setPageForTagging(PdfPage page) {
        if (page.isFlushed()) {
            throw new PdfException(PdfException.PageAlreadyFlushed);
        }
        this.currentPage = page;

        return this;
    }

    /**
     * @return a page which content will be tagged with this instance of {@code TagTreePointer}.
     */
    public PdfPage getCurrentPage() {
        return currentPage;
    }

    /**
     * Sometimes, tags are desired to be connected with the content that resides not in the page's content stream,
     * but rather in the some appearance stream or in the form xObject stream. In that case, to have a valid tag structure,
     * one shall set not only the page, on which the content will be rendered, but also the content stream in which
     * the tagged content will reside.
     * <br><br>
     * NOTE: It's important to set a {@code null} for this value, when tagging of this stream content is finished.
     *
     * @param contentStream the content stream which content will be tagged with this instance of {@code TagTreePointer}
     *                      or {@code null} if content stream tagging is finished.
     */
    public TagTreePointer setContentStreamForTagging(PdfStream contentStream) {
        this.contentStream = contentStream;
        return this;
    }

    /**
     * @return the content stream which content will be tagged with this instance of {@code TagTreePointer}.
     */
    public PdfStream getCurrentContentStream() {
        return contentStream;
    }

    /**
     * @return the {@link TagStructureContext} associated with the document to which this pointer belongs.
     */
    public TagStructureContext getContext() {
        return tagStructureContext;
    }

    /**
     * @return the document, at which tag structure this instance points.
     */
    public PdfDocument getDocument() {
        return tagStructureContext.getDocument();
    }

    /**
     * Sets a {@link PdfNamespace} which will be set to every new tag created by this {@link TagTreePointer} instance
     * if this tag doesn't explicitly define namespace by the means of {@link DefaultAccessibilityProperties#setNamespace(PdfNamespace)}.
     * <p>This value has meaning only for the PDF documents of version <b>2.0 and higher</b>.</p>
     * <p>It's highly recommended to acquire {@link PdfNamespace} class instances via {@link TagStructureContext#fetchNamespace(String)}.</p>
     * @param namespace a {@link PdfNamespace} to be set for the new tags created. If set to null - new tags will have
     *                  a namespace set only if it is defined in the corresponding {@link AccessibilityProperties}.
     * @return this {@link TagTreePointer} instance.
     * @see TagStructureContext#fetchNamespace(String)
     */
    public TagTreePointer setNamespaceForNewTags(PdfNamespace namespace) {
        this.currentNamespace = namespace;
        return this;
    }

    /**
     * Gets a {@link PdfNamespace} which will be set to every new tag created by this {@link TagTreePointer} instance.
     * @return a {@link PdfNamespace} which is to be set for the new tags created, or null if one is not defined.
     * @see TagTreePointer#setNamespaceForNewTags(PdfNamespace)
     */
    public PdfNamespace getNamespaceForNewTags() {
        return this.currentNamespace;
    }

    /**
     * Adds a new tag with given role to the tag structure.
     * This method call moves this {@code TagTreePointer} to the added kid.
     *
     * @param role role of the new tag.
     * @return this {@link TagTreePointer} instance.
     */
    public TagTreePointer addTag(String role) {
        addTag(-1, role);
        return this;
    }

    /**
     * Adds a new tag with given role to the tag structure.
     * This method call moves this {@code TagTreePointer} to the added kid.
     * <br>
     * This call is equivalent of calling sequentially {@link #setNextNewKidIndex(int)} and {@link #addTag(PdfName)}.
     *
     * @param index zero-based index in kids array of parent tag at which new tag will be added.
     * @param role  role of the new tag.
     * @return this {@link TagTreePointer} instance.
     */
    public TagTreePointer addTag(int index, String role) {
        tagStructureContext.throwExceptionIfRoleIsInvalid(role, currentNamespace);
        setNextNewKidIndex(index);
        setCurrentStructElem(addNewKid(role));
        return this;
    }

    /**
     * Adds a new tag to the tag structure.
     * This method call moves this {@link TagTreePointer} to the added kid.
     * <br>
     * New tag will have a role and attributes defined by the given {@link AccessibilityProperties}.
     *
     * @param properties accessibility properties which define a new tag role and other properties.
     * @return this {@link TagTreePointer} instance.
     */
    public TagTreePointer addTag(AccessibilityProperties properties) {
        addTag(-1, properties);
        return this;
    }

    /**
     * Adds a new tag to the tag structure.
     * This method call moves this {@code TagTreePointer} to the added kid.
     * <br>
     * New tag will have a role and attributes defined by the given {@link AccessibilityProperties}.
     * This call is equivalent of calling sequentially {@link #setNextNewKidIndex(int)} and {@link #addTag(AccessibilityProperties)}.
     *
     * @param index   zero-based index in kids array of parent tag at which new tag will be added.
     * @param properties accessibility properties which define a new tag role and other properties.
     * @return this {@link TagTreePointer} instance.
     */
    public TagTreePointer addTag(int index, AccessibilityProperties properties) {
        tagStructureContext.throwExceptionIfRoleIsInvalid(properties, currentNamespace);
        setNextNewKidIndex(index);
        setCurrentStructElem(addNewKid(properties));
        return this;
    }

    /**
     * Adds a new content item for the given {@code PdfAnnotation} under the current tag.
     * <br><br>
     * By default, when annotation is added to the page it is automatically tagged with auto tagging pointer
     * (see {@link TagStructureContext#getAutoTaggingPointer()}). If you want to add annotation tag manually, be sure to use
     * {@link PdfPage#addAnnotation(int, PdfAnnotation, boolean)} method with <i>false</i> for boolean flag.
     *
     * @param annotation {@code PdfAnnotation} to be tagged.
     * @return this {@link TagTreePointer} instance.
     */
    public TagTreePointer addAnnotationTag(PdfAnnotation annotation) {
        throwExceptionIfCurrentPageIsNotInited();

        PdfObjRef kid = new PdfObjRef(annotation, getCurrentStructElem(), getDocument().getNextStructParentIndex());
        if (!ensureElementPageEqualsKidPage(getCurrentStructElem(), currentPage.getPdfObject())) {
            ((PdfDictionary) kid.getPdfObject()).put(PdfName.Pg, currentPage.getPdfObject());
        }
        addNewKid(kid);
        return this;
    }

    /**
     * Sets index of the next added to the current tag kid, which could be another tag or content item.
     * By default, new tag is added at the end of the parent kids array. This property affects only the next added tag,
     * all tags added after will be added with the default behaviour.
     * <br><br>
     * This method could be used with any overload of {@link #addTag(PdfName)} method,
     * with {@link #relocateKid(int, TagTreePointer)} and {@link #addAnnotationTag(PdfAnnotation)}.
     * <br>
     * Keep in mind, that this method set property to the {@code TagTreePointer} and not to the tag itself, which means
     * that if you would move the pointer, this property would be applied to the new current tag.
     *
     * @param nextNewKidIndex index of the next added kid.
     * @return this {@link TagTreePointer} instance.
     */
    public TagTreePointer setNextNewKidIndex(int nextNewKidIndex) {
        if (nextNewKidIndex > -1) {
            this.nextNewKidIndex = nextNewKidIndex;
        }
        return this;
    }

    /**
     * Removes the current tag. If it has kids, they will become kids of the current tag parent.
     * This method call moves this {@code TagTreePointer} to the current tag parent.
     * <br><br>
     * You cannot remove root tag, and also you cannot remove the tag if it's parent is already flushed;
     * in this two cases an exception will be thrown.
     *
     * @return this {@link TagStructureContext} instance.
     */
    public TagTreePointer removeTag() {
        PdfStructElem currentStructElem = getCurrentStructElem();
        IStructureNode parentElem = currentStructElem.getParent();
        if (parentElem instanceof PdfStructTreeRoot) {
            throw new PdfException(PdfException.CannotRemoveDocumentRootTag);
        }

        List<IStructureNode> kids = currentStructElem.getKids();
        PdfStructElem parent = (PdfStructElem) parentElem;

        if (parent.isFlushed()) {
            throw new PdfException(PdfException.CannotRemoveTagBecauseItsParentIsFlushed);
        }

        // remove waiting tag state if tag is removed
        Object objForStructDict = tagStructureContext.getWaitingTagsManager().getObjForStructDict(currentStructElem.getPdfObject());
        tagStructureContext.getWaitingTagsManager().removeWaitingState(objForStructDict);

        int removedKidIndex = parent.removeKid(currentStructElem);

        PdfIndirectReference indRef = currentStructElem.getPdfObject().getIndirectReference();
        if (indRef != null) {
            // TODO how about possible references to structure element from refs or structure destination for instance?
            indRef.setFree();
        }

        for (IStructureNode kid : kids) {
            if (kid instanceof PdfStructElem) {
                parent.addKid(removedKidIndex++, (PdfStructElem) kid);
            } else {
                PdfMcr mcr = prepareMcrForMovingToNewParent((PdfMcr) kid, parent);
                parent.addKid(removedKidIndex++, mcr);
            }
        }
        currentStructElem.getPdfObject().clear();
        setCurrentStructElem(parent);
        return this;
    }

    /**
     * Moves kid of the current tag to the tag at which given {@code TagTreePointer} points.
     * This method doesn't change neither this instance nor pointerToNewParent position.
     *
     * @param kidIndex           zero-based index of the current tag's kid to be relocated.
     * @param pointerToNewParent the {@code TagTreePointer} which is positioned at the tag which will become kid's new parent.
     * @return this {@link TagTreePointer} instance.
     */
    public TagTreePointer relocateKid(int kidIndex, TagTreePointer pointerToNewParent) {
        if (getDocument() != pointerToNewParent.getDocument()) {
            throw new PdfException(PdfException.TagCannotBeMovedToTheAnotherDocumentsTagStructure);
        }
        if (getCurrentStructElem().isFlushed()) {
            throw new PdfException(PdfException.CannotRelocateTagWhichParentIsAlreadyFlushed);
        }

        if (isPointingToSameTag(pointerToNewParent)){
            if (kidIndex == pointerToNewParent.nextNewKidIndex) {
                return this;
            } else if (kidIndex < pointerToNewParent.nextNewKidIndex) {
                pointerToNewParent.setNextNewKidIndex(pointerToNewParent.nextNewKidIndex - 1);
            }
        }
        if (getCurrentStructElem().getKids().get(kidIndex) == null) {
            throw new PdfException(PdfException.CannotRelocateTagWhichIsAlreadyFlushed);
        }
        IStructureNode removedKid = getCurrentStructElem().removeKid(kidIndex, true);
        if (removedKid instanceof PdfStructElem) {
            pointerToNewParent.addNewKid((PdfStructElem) removedKid);
        } else if (removedKid instanceof PdfMcr) {
            PdfMcr mcrKid = prepareMcrForMovingToNewParent((PdfMcr) removedKid, pointerToNewParent.getCurrentStructElem());
            pointerToNewParent.addNewKid(mcrKid);
        }

        return this;
    }

    /**
     * Moves current tag to the tag at which given {@code TagTreePointer} points.
     * This method doesn't change either this instance or pointerToNewParent position.
     *
     * @param pointerToNewParent the {@code TagTreePointer} which is positioned at the tag
     *                           which will become current tag new parent.
     * @return this {@link TagTreePointer} instance.
     */
    public TagTreePointer relocate(TagTreePointer pointerToNewParent) {
        if (getCurrentStructElem().getPdfObject() == tagStructureContext.getRootTag().getPdfObject()) {
            throw new PdfException(PdfException.CannotRelocateRootTag);
        }
        if (getCurrentStructElem().isFlushed()) {
            throw new PdfException(PdfException.CannotRelocateTagWhichIsAlreadyFlushed);
        }
        int i = getIndexInParentKidsList();
        if (i < 0) {
            throw new PdfException(PdfException.CannotRelocateTagWhichParentIsAlreadyFlushed);
        }
        new TagTreePointer(this).moveToParent().relocateKid(i, pointerToNewParent);
        return this;
    }

    /**
     * Creates a reference to the current tag, which could be used to associate a content on the PdfCanvas with current tag.
     * See {@link PdfCanvas#openTag(TagReference)} and {@link #setPageForTagging(PdfPage)}.
     *
     * @return the reference to the current tag.
     */
    public TagReference getTagReference() {
        return getTagReference(-1);
    }

    /**
     * Creates a reference to the current tag, which could be used to associate a content on the PdfCanvas with current tag.
     * See {@link PdfCanvas#openTag(TagReference)} and {@link #setPageForTagging(PdfPage)}.
     *
     * @param index zero-based index in kids array of tag. These indexes define the logical order of the content on the page.
     * @return the reference to the current tag.
     */
    public TagReference getTagReference(int index) {
        return new TagReference(getCurrentElemEnsureIndirect(), this, index);
    }

    /**
     * Moves this {@code TagTreePointer} instance to the document root tag.
     *
     * @return this {@link TagTreePointer} instance.
     */
    public TagTreePointer moveToRoot() {
        setCurrentStructElem(tagStructureContext.getRootTag());
        return this;
    }

    /**
     * Moves this {@link TagTreePointer} instance to the parent of the current tag.
     *
     * @return this {@link TagTreePointer} instance.
     */
    public TagTreePointer moveToParent() {
        if (getCurrentStructElem().getPdfObject() == tagStructureContext.getRootTag().getPdfObject()) {
            throw new PdfException(PdfException.CannotMoveToParentCurrentElementIsRoot);
        }

        PdfStructElem parent = (PdfStructElem) getCurrentStructElem().getParent();
        if (parent.isFlushed()) {
            Logger logger = LoggerFactory.getLogger(TagTreePointer.class);
            logger.warn(LogMessageConstant.ATTEMPT_TO_MOVE_TO_FLUSHED_PARENT);

            moveToRoot();
        } else {
            setCurrentStructElem((PdfStructElem) parent);
        }
        return this;
    }

    /**
     * Moves this {@code TagTreePointer} instance to the kid of the current tag.
     *
     * @param kidIndex zero-based index of the current tag kid to which pointer will be moved.
     * @return this {@link TagTreePointer} instance.
     */
    public TagTreePointer moveToKid(int kidIndex) {
        IStructureNode kid = getCurrentStructElem().getKids().get(kidIndex);
        if (kid instanceof PdfStructElem) {
            setCurrentStructElem((PdfStructElem) kid);
        } else if (kid instanceof PdfMcr) {
            throw new PdfException(PdfException.CannotMoveToMarkedContentReference);
        } else {
            throw new PdfException(PdfException.CannotMoveToFlushedKid);
        }
        return this;
    }

    /**
     * Moves this {@link TagTreePointer} instance to the first descendant of the current tag which has the given role.
     * If there are no direct kids of the tag with such role, further descendants are checked in BFS order.
     *
     * @param role role of the current tag descendant to which pointer will be moved.
     *             If there are several descendants with this role, pointer will be moved
     *             to the first kid with such role in BFS order.
     * @return this {@link TagTreePointer} instance.
     */
    public TagTreePointer moveToKid(String role) {
        moveToKid(0, role);
        return this;
    }

    /**
     * Moves this {@link TagTreePointer} instance to the first descendant of the current tag which has the given role.
     * If there are no direct kids of the tag with such role, further descendants are checked in BFS order.
     *
     * @param n    if there are several descendants with the given role, pointer will be moved to the descendant
     *             which has zero-based index <em>n</em> if you count only the descendants with the given role in BFS order.
     * @param role role of the current tag descendant to which pointer will be moved.
     * @return this {@link TagTreePointer} instance.
     */
    public TagTreePointer moveToKid(int n, String role) {
        if (MCR_MARKER.equals(role)) { // MCR literal could be returned in a list of kid names (see #getKidsRoles())
            throw new PdfException(PdfException.CannotMoveToMarkedContentReference);
        }
        List<IStructureNode> descendants = new ArrayList<>(getCurrentStructElem().getKids());
        int k = 0;
        for (int i = 0; i < descendants.size(); ++i) {
            if (descendants.get(i) == null || descendants.get(i) instanceof PdfMcr) {
                continue;
            }
            String descendantRole = descendants.get(i).getRole().getValue();
            if (descendantRole.equals(role) && k++ == n) {
                setCurrentStructElem((PdfStructElem) descendants.get(i));
                return this;
            } else {
                descendants.addAll(descendants.get(i).getKids());
            }
        }

        throw new PdfException(PdfException.NoKidWithSuchRole);
    }

    /**
     * Gets current tag kids roles.
     * If certain kid is already flushed, at its position there will be a {@code null}.
     * If kid is a content item, at it's position there will be "MCR" string literal (stands for Marked Content Reference).
     *
     * @return current tag kids roles
     */
    public List<String> getKidsRoles() {
        List<String> roles = new ArrayList<>();
        List<IStructureNode> kids = getCurrentStructElem().getKids();
        for (IStructureNode kid : kids) {
            if (kid == null) {
                roles.add(null);
            } else if (kid instanceof PdfStructElem) {
                roles.add(kid.getRole().getValue());
            } else {
                roles.add(MCR_MARKER);
            }
        }
        return roles;
    }

    /**
     * Flushes current tag and all it's descendants.
     * This method call moves this {@code TagTreePointer} to the current tag parent.
     * <p>
     * If some of the descendant tags of the current tag have waiting state (see {@link WaitingTagsManager}),
     * then these tags are considered as not yet finished ones, and they won't be flushed immediately,
     * but they will be flushed, when waiting state is removed.
     * </p>
     *
     * @return this {@link TagTreePointer} instance.
     */
    public TagTreePointer flushTag() {
        if (getCurrentStructElem().getPdfObject() == tagStructureContext.getRootTag().getPdfObject()) {
            throw new PdfException(PdfException.CannotFlushDocumentRootTagBeforeDocumentIsClosed);
        }
        IStructureNode parent = tagStructureContext.getWaitingTagsManager().flushTag(getCurrentStructElem());
        if (parent != null) { // parent is not flushed
            setCurrentStructElem((PdfStructElem) parent);
        } else {
            setCurrentStructElem(tagStructureContext.getRootTag());
        }
        return this;
    }

    /**
     * For current tag and all of it's parents consequentially checks if the following constraints apply,
     * and flushes the tag if they do or stops if they don't:
     * <ul>
     *     <li>tag is not already flushed;</li>
     *     <li>tag is not in waiting state (see {@link WaitingTagsManager});</li>
     *     <li>tag is not the root tag;</li>
     *     <li>tag has no kids or all of the kids are either flushed themselves or
     *         (if they are a marked content reference) belong to the flushed page.</li>
     * </ul>
     * It makes sense to use this method in conjunction with {@link TagStructureContext#flushPageTags(PdfPage)}
     * for the tags which have just lost their waiting state and might be not flushed only because they had one.
     * This helps to eliminate hanging (not flushed) tags when they don't have waiting state anymore.
     * @return this {@link TagTreePointer} instance.
     */
    public TagTreePointer flushParentsIfAllKidsFlushed() {
        getContext().flushParentIfBelongsToPage(getCurrentStructElem(), null);
        return this;
    }

    /**
     * Gets accessibility properties of the current tag.
     *
     * @return accessibility properties of the current tag.
     */
    public AccessibilityProperties getProperties() {
        return new BackedAccessibilityProperties(this);
    }

    /**
     * Gets current tag role.
     *
     * @return current tag role.
     */
    public String getRole() {
        return getCurrentStructElem().getRole().getValue();
    }

    /**
     * Sets new role to the current tag.
     *
     * @param role new role to be set.
     * @return this {@link TagTreePointer} instance.
     */
    public TagTreePointer setRole(String role) {
        getCurrentStructElem().setRole(PdfStructTreeRoot.convertRoleToPdfName(role));
        return this;
    }

    /**
     * Defines index of the current tag in the parent's kids list.
     * @return returns index of the current tag in the parent's kids list, or -1
     * if either current tag is a root tag, parent is flushed or it wasn't possible to define index.
     */
    public int getIndexInParentKidsList() {
        if (getCurrentStructElem().getPdfObject() == tagStructureContext.getRootTag().getPdfObject()) {
            return -1;
        }

        PdfStructElem parent = (PdfStructElem) getCurrentStructElem().getParent();
        if (parent.isFlushed()) {
            return -1;
        }
        PdfObject k = parent.getK();
        if (k == getCurrentStructElem().getPdfObject()) {
            return 0;
        }
        if (k.isArray()) {
            PdfArray kidsArr = (PdfArray) k;
            return kidsArr.indexOf(getCurrentStructElem().getPdfObject());
        }
        return -1;
    }

    /**
     * Moves this {@link TagTreePointer} instance to the tag at which given {@link TagTreePointer} instance is pointing.
     *
     * @param tagTreePointer a {@link TagTreePointer} that points at the tag which will become the current tag
     *                       of this instance.
     * @return this {@link TagTreePointer} instance.
     */
    public TagTreePointer moveToPointer(TagTreePointer tagTreePointer) {
        this.currentStructElem = tagTreePointer.currentStructElem;
        return this;
    }

    /**
     * Checks if this {@link TagTreePointer} is pointing at the same tag as the giving {@link TagTreePointer}.
     * @param otherPointer a {@link TagTreePointer} which is checked against this instance on whether they point
     *                     at the same tag.
     * @return true if both {@link TagTreePointer} instances point at the same tag, false otherwise.
     */
    public boolean isPointingToSameTag(TagTreePointer otherPointer) {
        return getCurrentStructElem().getPdfObject().equals(otherPointer.getCurrentStructElem().getPdfObject());
    }

    int createNextMcidForStructElem(PdfStructElem elem, int index) {
        throwExceptionIfCurrentPageIsNotInited();

        PdfMcr mcr;
        if (!markedContentNotInPageStream() && ensureElementPageEqualsKidPage(elem, currentPage.getPdfObject())) {
            mcr = new PdfMcrNumber(currentPage, elem);
        } else {
            mcr = new PdfMcrDictionary(currentPage, elem);
            if (markedContentNotInPageStream()) {
                ((PdfDictionary) mcr.getPdfObject()).put(PdfName.Stm, contentStream);
            }
        }
        elem.addKid(index, mcr);
        return mcr.getMcid();
    }

    TagTreePointer setCurrentStructElem(PdfStructElem structElem) {
        if (structElem.getParent() == null) {
            throw new PdfException(PdfException.StructureElementShallContainParentObject);
        }

        currentStructElem = structElem;
        return this;
    }

    PdfStructElem getCurrentStructElem() {
        if (currentStructElem.isFlushed()) {
            throw new PdfException(PdfException.TagTreePointerIsInInvalidStateItPointsAtFlushedElementUseMoveToRoot);
        }

        PdfIndirectReference indRef = currentStructElem.getPdfObject().getIndirectReference();
        if (indRef != null && indRef.isFree()) { // is removed
            throw new PdfException(PdfException.TagTreePointerIsInInvalidStateItPointsAtRemovedElementUseMoveToRoot);
        }

        return currentStructElem;
    }

    private int getNextNewKidPosition() {
        int nextPos = nextNewKidIndex;
        nextNewKidIndex = -1;
        return nextPos;
    }

    private PdfStructElem addNewKid(String role) {
        PdfStructElem kid = new PdfStructElem(getDocument(), PdfStructTreeRoot.convertRoleToPdfName(role));
        processKidNamespace(kid);
        return addNewKid(kid);
    }

    private PdfStructElem addNewKid(AccessibilityProperties properties) {
        PdfStructElem kid = new PdfStructElem(getDocument(), PdfStructTreeRoot.convertRoleToPdfName(properties.getRole()));
        AccessibilityPropertiesToStructElem.apply(properties, kid);
        processKidNamespace(kid);
        return addNewKid(kid);
    }

    private void processKidNamespace(PdfStructElem kid) {
        PdfNamespace kidNamespace = kid.getNamespace();
        if (currentNamespace != null && kidNamespace == null) {
            kid.setNamespace(currentNamespace);
            kidNamespace = currentNamespace;
        }
        tagStructureContext.ensureNamespaceRegistered(kidNamespace);
    }

    private PdfStructElem addNewKid(PdfStructElem kid) {
        return getCurrentElemEnsureIndirect().addKid(getNextNewKidPosition(), kid);
    }

    private PdfMcr addNewKid(PdfMcr kid) {
        return getCurrentElemEnsureIndirect().addKid(getNextNewKidPosition(), kid);
    }

    private PdfStructElem getCurrentElemEnsureIndirect() {
        PdfStructElem currentStructElem = getCurrentStructElem();
        if (currentStructElem.getPdfObject().getIndirectReference() == null) {
            currentStructElem.makeIndirect(getDocument());
        }
        return currentStructElem;
    }

    private PdfMcr prepareMcrForMovingToNewParent(PdfMcr mcrKid, PdfStructElem newParent) {
        PdfObject mcrObject = mcrKid.getPdfObject();
        PdfDictionary mcrPage = mcrKid.getPageObject();

        PdfDictionary mcrDict = null;
        if (!mcrObject.isNumber()) {
            mcrDict = (PdfDictionary) mcrObject;
        }
        if (mcrDict == null || !mcrDict.containsKey(PdfName.Pg)) {
            if (!ensureElementPageEqualsKidPage(newParent, mcrPage)) {
                if (mcrDict == null) {
                    mcrDict = new PdfDictionary();
                    mcrDict.put(PdfName.Type, PdfName.MCR);
                    mcrDict.put(PdfName.MCID, mcrKid.getPdfObject());
                }
                mcrDict.put(PdfName.Pg, mcrPage);
            }
        }

        if (mcrDict != null) {
            if (PdfName.MCR.equals(mcrDict.get(PdfName.Type))) {
                mcrKid = new PdfMcrDictionary(mcrDict, newParent);
            } else if (PdfName.OBJR.equals(mcrDict.get(PdfName.Type))) {
                mcrKid = new PdfObjRef(mcrDict, newParent);
            }
        } else {
            mcrKid = new PdfMcrNumber((PdfNumber) mcrObject, newParent);
        }

        return mcrKid;
    }

    private boolean ensureElementPageEqualsKidPage(PdfStructElem elem, PdfDictionary kidPage) {
        PdfObject pageObject = elem.getPdfObject().get(PdfName.Pg);
        if (pageObject == null) {
            pageObject = kidPage;
            elem.getPdfObject().put(PdfName.Pg, kidPage);
            elem.setModified();
        }

        return kidPage.equals(pageObject);
    }

    private boolean markedContentNotInPageStream() {
        return contentStream != null;
    }

    private void throwExceptionIfCurrentPageIsNotInited() {
        if (currentPage == null) {
            throw new PdfException(PdfException.PageIsNotSetForThePdfTagStructure);
        }
    }
}
