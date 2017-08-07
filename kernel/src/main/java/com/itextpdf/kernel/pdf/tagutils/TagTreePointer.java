/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2017 iText Group NV
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
import com.itextpdf.kernel.pdf.tagging.IPdfStructElem;
import com.itextpdf.kernel.pdf.tagging.PdfMcr;
import com.itextpdf.kernel.pdf.tagging.PdfMcrDictionary;
import com.itextpdf.kernel.pdf.tagging.PdfMcrNumber;
import com.itextpdf.kernel.pdf.tagging.PdfNamespace;
import com.itextpdf.kernel.pdf.tagging.PdfObjRef;
import com.itextpdf.kernel.pdf.tagging.PdfStructTreeRoot;
import com.itextpdf.kernel.pdf.tagging.PdfStructElement;
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
 * <br/><br/>
 * <p>
 * There could be any number of the instances of this class, simultaneously pointing to different (or the same) parts of
 * the tag structure. Because of this, you can for example remove the tag at which another instance is currently pointing.
 * In this case, this another instance becomes invalid, and invocation of any method on it will result in exception. To make
 * given instance valid again, use {@link #moveToRoot()} method.
 */
public class TagTreePointer {

    private TagStructureContext tagStructureContext;
    private PdfStructElement currentStructElem;
    private PdfPage currentPage;
    private PdfStream contentStream;

    private PdfNamespace currentNamespace;

    // '-1' value of this field means that next new kid will be the last element in the kids array
    private int nextNewKidIndex = -1;

    /**
     * Creates {@code TagTreePointer} instance. After creation {@code TagTreePointer} points at the root tag.
     * <p>
     * The {@link PdfNamespace} for the new tags, which don't explicitly define namespace by the means of
     * {@link AccessibilityProperties#setNamespace(PdfNamespace)}, is set to the value returned by
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

    TagTreePointer(PdfStructElement structElem, PdfDocument document) {
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
     * if this tag doesn't explicitly define namespace by the means of {@link AccessibilityProperties#setNamespace(PdfNamespace)}.
     * <p>This value has meaning only for the PDF documents of version <b>2.0 and higher</b>.</p>
     * <p>It's highly recommended to acquire {@link PdfNamespace} class instances via {@link TagStructureContext#fetchNamespace(String)}.</p>
     * @param namespace a {@link PdfNamespace} to be set for the new tags created. If set to null - new tags will have
     *                  a namespace set only if it is defined in the corresponding {@link IAccessibleElement}.
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
    public TagTreePointer addTag(PdfName role) {
        addTag(-1, role);
        return this;
    }

    /**
     * Adds a new tag with given role to the tag structure.
     * This method call moves this {@code TagTreePointer} to the added kid.
     * <br/>
     * This call is equivalent of calling sequentially {@link #setNextNewKidIndex(int)} and {@link #addTag(PdfName)}.
     *
     * @param index zero-based index in kids array of parent tag at which new tag will be added.
     * @param role  role of the new tag.
     * @return this {@link TagTreePointer} instance.
     */
    public TagTreePointer addTag(int index, PdfName role) {
        tagStructureContext.throwExceptionIfRoleIsInvalid(role, currentNamespace);
        setNextNewKidIndex(index);
        setCurrentStructElem(addNewKid(role));
        return this;
    }

    /**
     * Adds a new tag to the tag structure.
     * This method call moves this {@code TagTreePointer} to the added kid.
     * <br/>
     * New tag will have a role and attributes defined by the given IAccessibleElement.
     *
     * @param element accessible element which represents a new tag.
     * @return this {@link TagTreePointer} instance.
     */
    public TagTreePointer addTag(IAccessibleElement element) {
        addTag(-1, element);
        return this;
    }

    /**
     * Adds a new tag to the tag structure.
     * This method call moves this {@code TagTreePointer} to the added kid.
     * <br/>
     * New tag will have a role and attributes defined by the given IAccessibleElement.
     * This call is equivalent of calling sequentially {@link #setNextNewKidIndex(int)} and {@link #addTag(IAccessibleElement)}.
     *
     * @param index   zero-based index in kids array of parent tag at which new tag will be added.
     * @param element accessible element which represents a new tag.
     * @return this {@link TagTreePointer} instance.
     */
    public TagTreePointer addTag(int index, IAccessibleElement element) {
        tagStructureContext.throwExceptionIfRoleIsInvalid(element, currentNamespace);
        setNextNewKidIndex(index);
        setCurrentStructElem(addNewKid(element));
        return this;
    }

    /**
     * Adds a new content item for the given {@code PdfAnnotation} under the current tag.
     * <br/><br/>
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
     * <br/><br/>
     * This method could be used with any overload of {@link #addTag(PdfName)} method,
     * with {@link #relocateKid(int, TagTreePointer)} and {@link #addAnnotationTag(PdfAnnotation)}.
     * <br/>
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
     * <br/><br/>
     * You cannot remove root tag, and also you cannot remove the tag if it's parent is already flushed;
     * in this two cases an exception will be thrown.
     *
     * @return this {@link TagStructureContext} instance.
     */
    public TagTreePointer removeTag() {
        PdfStructElement currentStructElem = getCurrentStructElem();
        IPdfStructElem parentElem = currentStructElem.getParent();
        if (parentElem instanceof PdfStructTreeRoot) {
            throw new PdfException(PdfException.CannotRemoveDocumentRootTag);
        }

        List<IPdfStructElem> kids = currentStructElem.getKids();
        PdfStructElement parent = (PdfStructElement) parentElem;

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

        for (IPdfStructElem kid : kids) {
            if (kid instanceof PdfStructElement) {
                parent.addKid(removedKidIndex++, (PdfStructElement) kid);
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
     * This method doesn't change pointerToNewParent position.
     *
     * @param kidIndex           zero-based index of the current tag's kid to be relocated.
     * @param pointerToNewParent the {@code TagTreePointer} which is positioned at the tag which will become kid's new parent.
     * @return this {@link TagStructureContext} instance.
     */
    public TagTreePointer relocateKid(int kidIndex, TagTreePointer pointerToNewParent) {
        if (getDocument() != pointerToNewParent.getDocument()) {
            throw new PdfException(PdfException.TagCannotBeMovedToTheAnotherDocumentsTagStructure);
        }

        IPdfStructElem removedKid = getCurrentStructElem().removeKid(kidIndex);
        if (removedKid instanceof PdfStructElement) {
            pointerToNewParent.addNewKid((PdfStructElement) removedKid);
        } else if (removedKid instanceof PdfMcr) {
            PdfMcr mcrKid = prepareMcrForMovingToNewParent((PdfMcr) removedKid, pointerToNewParent.getCurrentStructElem());
            pointerToNewParent.addNewKid(mcrKid);
        }

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
     * @return this {@link TagStructureContext} instance.
     */
    public TagTreePointer moveToRoot() {
        setCurrentStructElem(tagStructureContext.getRootTag());
        return this;
    }

    /**
     * Moves this {@code TagTreePointer} instance to the parent of the current tag.
     *
     * @return this {@link TagStructureContext} instance.
     */
    public TagTreePointer moveToParent() {
        if (getCurrentStructElem().getPdfObject() == tagStructureContext.getRootTag().getPdfObject()) {
            throw new PdfException(PdfException.CannotMoveToParentCurrentElementIsRoot);
        }

        PdfStructElement parent = (PdfStructElement) getCurrentStructElem().getParent();
        if (parent.isFlushed()) {
            Logger logger = LoggerFactory.getLogger(TagTreePointer.class);
            logger.warn(LogMessageConstant.ATTEMPT_TO_MOVE_TO_FLUSHED_PARENT);

            moveToRoot();
        } else {
            setCurrentStructElem((PdfStructElement) parent);
        }
        return this;
    }

    /**
     * Moves this {@code TagTreePointer} instance to the kid of the current tag.
     *
     * @param kidIndex zero-based index of the current tag kid to which pointer will be moved.
     * @return this {@link TagStructureContext} instance.
     */
    public TagTreePointer moveToKid(int kidIndex) {
        IPdfStructElem kid = getCurrentStructElem().getKids().get(kidIndex);
        if (kid instanceof PdfStructElement) {
            setCurrentStructElem((PdfStructElement) kid);
        } else if (kid instanceof PdfMcr) {
            throw new PdfException(PdfException.CannotMoveToMarkedContentReference);
        } else {
            throw new PdfException(PdfException.CannotMoveToFlushedKid);
        }
        return this;
    }

    /**
     * Moves this {@code TagTreePointer} instance to the kid of the current tag.
     *
     * @param role role of the current tag kid to which pointer will be moved.
     *             If there is several kids with this role, pointer will be moved to the first kid with such role.
     * @return this {@link TagStructureContext} instance.
     */
    public TagTreePointer moveToKid(PdfName role) {
        moveToKid(0, role);
        return this;
    }

    /**
     * Moves this {@code TagTreePointer} instance to the kid of the current tag.
     *
     * @param n    if there is several kids with the given role, pointer will be moved to the kid
     *             which has zero-based index n if you count only the kids with given role.
     * @param role role of the current tag kid to which pointer will be moved.
     * @return this {@link TagStructureContext} instance.
     */
    public TagTreePointer moveToKid(int n, PdfName role) {
        if (PdfName.MCR.equals(role)) {
            throw new PdfException(PdfException.CannotMoveToMarkedContentReference);
        }
        List<IPdfStructElem> kids = getCurrentStructElem().getKids();

        int k = 0;
        for (int i = 0; i < kids.size(); ++i) {
            if (kids.get(i) == null) continue;
            if (kids.get(i).getRole().equals(role) && !(kids.get(i) instanceof PdfMcr) && k++ == n) {
                moveToKid(i);
                return this;
            }
        }

        throw new PdfException(PdfException.NoKidWithSuchRole);
    }

    /**
     * Gets current element kids roles.
     * If certain kid is already flushed, at its position there will be a {@code null}.
     * If kid is content item, at its position there will be "MCR" (Marked Content Reference).
     *
     * @return current element kids roles
     */
    public List<PdfName> getKidsRoles() {
        List<PdfName> roles = new ArrayList<>();
        List<IPdfStructElem> kids = getCurrentStructElem().getKids();
        for (IPdfStructElem kid : kids) {
            if (kid == null) {
                roles.add(null);
            } else if (kid instanceof PdfStructElement) {
                roles.add(kid.getRole());
            } else {
                roles.add(PdfName.MCR);
            }
        }
        return roles;
    }

    /**
     * Flushes current tag and all it's descenders.
     * This method call moves this {@code TagTreePointer} to the current tag parent.
     * <p>
     * If some of the descender tags of the current tag have waiting state (see {@link WaitingTagsManager}),
     * then these tags are considered as not yet finished ones, and they won't be flushed immediately,
     * but they will be flushed, when waiting state is removed.
     * </p>
     *
     * @return this {@link TagStructureContext} instance.
     */
    public TagTreePointer flushTag() {
        if (getCurrentStructElem().getPdfObject() == tagStructureContext.getRootTag().getPdfObject()) {
            throw new PdfException(PdfException.CannotFlushDocumentRootTagBeforeDocumentIsClosed);
        }
        IPdfStructElem parent = tagStructureContext.getWaitingTagsManager().flushTag(getCurrentStructElem());
        if (parent != null) { // parent is not flushed
            setCurrentStructElem((PdfStructElement) parent);
        } else {
            setCurrentStructElem(tagStructureContext.getRootTag());
        }
        return this;
    }

    /**
     * Gets accessibility properties of the current tag.
     *
     * @return accessibility properties of the current tag.
     */
    public AccessibilityProperties getProperties() {
        return new BackedAccessibleProperties(this);
    }

    /**
     * Gets current tag role.
     *
     * @return current tag role.
     */
    public PdfName getRole() {
        return getCurrentStructElem().getRole();
    }

    /**
     * Sets new role to the current tag.
     *
     * @param role new role to be set.
     * @return this {@link TagStructureContext} instance.
     */
    public TagTreePointer setRole(PdfName role) {
        getCurrentStructElem().setRole(role);
        return this;
    }

    int createNextMcidForStructElem(PdfStructElement elem, int index) {
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

    TagTreePointer setCurrentStructElem(PdfStructElement structElem) {
        if (structElem.getParent() == null) {
            throw new PdfException(PdfException.StructureElementShallContainParentObject);
        }

        currentStructElem = structElem;
        return this;
    }

    PdfStructElement getCurrentStructElem() {
        if (currentStructElem.isFlushed()) {
            throw new PdfException(PdfException.TagTreePointerIsInInvalidStateItPointsAtFlushedElementUseMoveToRoot);
        }

        if (currentStructElem.getParent() == null) { // is removed
            throw new PdfException(PdfException.TagTreePointerIsInInvalidStateItPointsAtRemovedElementUseMoveToRoot);
        }

        return currentStructElem;
    }

    private int getNextNewKidPosition() {
        int nextPos = nextNewKidIndex;
        nextNewKidIndex = -1;
        return nextPos;
    }

    private PdfStructElement addNewKid(PdfName role) {
        PdfStructElement kid = new PdfStructElement(getDocument(), role);
        processKidNamespace(kid);
        return addNewKid(kid);
    }

    private PdfStructElement addNewKid(IAccessibleElement element) {
        PdfStructElement kid = new PdfStructElement(getDocument(), element.getRole());
        if (element.getAccessibilityProperties() != null) {
            element.getAccessibilityProperties().setToStructElem(kid);
        }
        processKidNamespace(kid);
        return addNewKid(kid);
    }

    private void processKidNamespace(PdfStructElement kid) {
        PdfNamespace kidNamespace = kid.getNamespace();
        if (currentNamespace != null && kidNamespace == null) {
            kid.setNamespace(currentNamespace);
            kidNamespace = currentNamespace;
        }
        tagStructureContext.ensureNamespaceRegistered(kidNamespace);
    }

    private PdfStructElement addNewKid(PdfStructElement kid) {
        return getCurrentElemEnsureIndirect().addKid(getNextNewKidPosition(), kid);
    }

    private PdfMcr addNewKid(PdfMcr kid) {
        return getCurrentElemEnsureIndirect().addKid(getNextNewKidPosition(), kid);
    }

    private PdfStructElement getCurrentElemEnsureIndirect() {
        PdfStructElement currentStructElem = getCurrentStructElem();
        if (currentStructElem.getPdfObject().getIndirectReference() == null) {
            currentStructElem.makeIndirect(getDocument());
        }
        return currentStructElem;
    }

    private PdfMcr prepareMcrForMovingToNewParent(PdfMcr mcrKid, PdfStructElement newParent) {
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

    private boolean ensureElementPageEqualsKidPage(PdfStructElement elem, PdfDictionary kidPage) {
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
