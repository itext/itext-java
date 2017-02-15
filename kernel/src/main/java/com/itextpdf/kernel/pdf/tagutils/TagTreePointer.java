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
import com.itextpdf.kernel.pdf.tagging.PdfObjRef;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;
import com.itextpdf.kernel.pdf.tagging.PdfStructTreeRoot;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@code TagTreePointer} class is used to modify the document's tag tree. At any given moment, instance of this class
 * 'points' at the specific position in the tree (at the specific tag). For the current tag you can add new tags, modify
 * it's role and properties, etc. Also, using instance of this class, you can change tag position in the tag structure,
 * you can flush current tag or remove it.
 * <br/><br/>
 * <p>
 * There could be any number of the instances of this class, simultaneously pointing to different (or the same) parts of
 * the tag structure. Because of this, you can for example remove the tag at which another instance is currently pointing.
 * In this case, this another instance becomes invalid, and invocation of any method on it will result in exception. To make
 * given instance valid again, use {@link #moveToRoot()} method.
 */
public class TagTreePointer implements Serializable {

    private static final long serialVersionUID = 3774218733446157411L;

    private TagStructureContext tagStructureContext;
    private PdfStructElem currentStructElem;
    private PdfPage currentPage;
    private PdfStream contentStream;

    // '-1' value of this field means that next new kid will be the last element in the kids array
    private int nextNewKidIndex = -1;

    /**
     * Creates {@code TagTreePointer} instance. After creation {@code TagTreePointer} points at the root tag.
     *
     * @param document the document, at which tag structure this instance will point.
     */
    public TagTreePointer(PdfDocument document) {
        tagStructureContext = document.getTagStructureContext();
        setCurrentStructElem(tagStructureContext.getRootTag());
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
    }

    TagTreePointer(PdfStructElem structElem) {
        PdfDocument doc = structElem.getPdfObject().getIndirectReference().getDocument();
        tagStructureContext = doc.getTagStructureContext();
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
     * @return the document, at which tag structure this instance points.
     */
    public PdfDocument getDocument() {
        return tagStructureContext.getDocument();
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
        addTag(index, new DummyAccessibleElement(role, null));
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
        addTag(element, false);
        return this;
    }

    /**
     * Adds a new tag to the tag structure.
     * This method call moves this {@code TagTreePointer} to the added kid.
     * <br/>
     * New tag will have a role and attributes defined by the given IAccessibleElement.
     * <br><br>
     * If <i>keepConnectedToTag</i> is true then a newly created tag will retain the connection with given
     * accessible element. See {@link TagTreePointer#moveToTag} for more explanations about tag connections concept.
     * <br/><br/>
     * If the same accessible element is connected to the tag and is added twice to the same parent -
     * this {@code TagTreePointer} instance would move to connected kid instead of creating tag twice.
     * But if it is added to some other parent, then connection will be removed.
     *
     * @param element            accessible element which represents a new tag.
     * @param keepConnectedToTag defines if to retain the connection between accessible element and the tag.
     * @return this {@link TagTreePointer} instance.
     */
    public TagTreePointer addTag(IAccessibleElement element, boolean keepConnectedToTag) {
        addTag(-1, element, keepConnectedToTag);
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
        addTag(index, element, false);
        return this;
    }

    /**
     * Adds a new tag to the tag structure.
     * This method call moves this {@code TagTreePointer} to the added kid.
     * <br/>
     * New tag will have a role and attributes defined by the given IAccessibleElement.
     * <br><br>
     * If {@param keepConnectedToTag} is true then a newly created tag will retain the connection with given
     * accessible element. See {@link TagTreePointer#moveToTag} for more explanations about tag connections concept.
     * <br/><br/>
     * If the same accessible element is connected to the tag and is added twice to the same parent -
     * this {@code TagTreePointer} instance would move to connected kid instead of creating tag twice.
     * But if it is added to some other parent, then connection will be removed.
     * <p>
     * <br/><br/>
     * This call is equivalent of calling sequentially {@link #setNextNewKidIndex(int)} and {@link #addTag(IAccessibleElement, boolean)}.
     *
     * @param index              zero-based index in kids array of parent tag at which new tag will be added.
     * @param element            accessible element which represents a new tag.
     * @param keepConnectedToTag defines if to retain the connection between accessible element and the tag.
     * @return this {@link TagTreePointer} instance.
     */
    public TagTreePointer addTag(int index, IAccessibleElement element, boolean keepConnectedToTag) {
        tagStructureContext.throwExceptionIfRoleIsInvalid(element.getRole());
        if (!tagStructureContext.isElementConnectedToTag(element)) {
            setNextNewKidIndex(index);
            setCurrentStructElem(addNewKid(element, keepConnectedToTag));
        } else {
            PdfStructElem connectedStruct = tagStructureContext.getStructConnectedToModel(element);
            if (connectedStruct.getParent() != null && getCurrentStructElem().getPdfObject() == ((PdfStructElem) connectedStruct.getParent()).getPdfObject()) {
                setCurrentStructElem(connectedStruct);
            } else {
                tagStructureContext.removeElementConnectionToTag(element);
                setNextNewKidIndex(index);
                setCurrentStructElem(addNewKid(element, keepConnectedToTag));
            }
        }

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

        PdfObjRef kid = new PdfObjRef(annotation, getCurrentStructElem());
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
     * Checks if given {@code IAccessibleElement} is connected to some tag.
     * See {@link TagTreePointer#moveToTag} for more explanations about tag connections concept.
     *
     * @param element element to check if it has a connected tag.
     * @return true, if there is a tag which retains the connection to the given accessible element.
     */
    public boolean isElementConnectedToTag(IAccessibleElement element) {
        return tagStructureContext.isElementConnectedToTag(element);
    }

    /**
     * Destroys the connection between the given accessible element and the tag to which this element is connected to.
     * See {@link TagTreePointer#moveToTag} for more explanations about tag connections concept.
     *
     * @param element {@code IAccessibleElement} which connection to the tag (if there is one) will be removed.
     * @return this {@link TagStructureContext} instance.
     */
    public TagStructureContext removeElementConnectionToTag(IAccessibleElement element) {
        return tagStructureContext.removeElementConnectionToTag(element);
    }

    /**
     * Removes the current tag. If it has kids, they will become kids of the current tag parent.
     * This method call moves this {@code TagTreePointer} to the current tag parent.
     * <br/><br/>
     * You cannot remove root tag, and also you cannot remove any tag if document's tag structure was partially flushed;
     * in this two cases an exception will be thrown.
     *
     * @return this {@link TagStructureContext} instance.
     */
    public TagTreePointer removeTag() {
        IPdfStructElem parentElem = getCurrentStructElem().getParent();
        if (parentElem instanceof PdfStructTreeRoot) {
            throw new PdfException(PdfException.CannotRemoveDocumentRootTag);
        }

        List<IPdfStructElem> kids = getCurrentStructElem().getKids();
        PdfStructElem parent = (PdfStructElem) parentElem;

        if (parent.isFlushed()) {
            throw new PdfException(PdfException.CannotRemoveTagBecauseItsParentIsFlushed);
        }
        int removedKidIndex = parent.removeKid(getCurrentStructElem());
        getCurrentStructElem().getPdfObject().getIndirectReference().setFree();

        for (IPdfStructElem kid : kids) {
            if (kid instanceof PdfStructElem) {
                parent.addKid(removedKidIndex++, (PdfStructElem) kid);
            } else {
                PdfMcr mcr = prepareMcrForMovingToNewParent((PdfMcr) kid, parent);
                parent.addKid(removedKidIndex++, mcr);
            }
        }
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
        if (removedKid instanceof PdfStructElem) {
            pointerToNewParent.addNewKid((PdfStructElem) removedKid);
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
        return new TagReference(getCurrentStructElem(), this, index);
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

        IPdfStructElem parent = getCurrentStructElem().getParent();
        if (parent == null) {
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
     * @return this {@link TagStructureContext} instance.
     */
    public TagTreePointer moveToKid(int kidIndex) {
        IPdfStructElem kid = getCurrentStructElem().getKids().get(kidIndex);
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
     *             which is the n'th if you count kids with such role.
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
     * Moves this {@code TagTreePointer} instance to a tag, which is connected with the given accessible element.
     * <p>
     * <br/><br/>
     * The connection between the tag and the accessible element instance is used as a sign that tag is not yet finished
     * and therefore should not be flushed or removed if page tags are flushed or removed. Also, any {@code TagTreePointer}
     * could be immediately moved to the tag with connection via it's connected element by using this method. If accessible
     * element is connected to the tag, then all changes of the role or properties of the element will affect the connected
     * tag role and properties.
     * <br/>
     * For any existing not connected tag the connection could be created using {@link #getConnectedElement(boolean)}
     * with <i>true</i> as parameter.
     *
     * @param element an element which has a connection with some tag.
     * @return this {@link TagStructureContext} instance.
     */
    public TagTreePointer moveToTag(IAccessibleElement element) {
        tagStructureContext.moveTagPointerToTag(element, this);
        return this;
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
            } else if (kid instanceof PdfStructElem) {
                roles.add(kid.getRole());
            } else {
                roles.add(PdfName.MCR);
            }
        }
        return roles;
    }

    /**
     * Flushes the current tag and all it's descenders.
     * This method call moves this {@code TagTreePointer} to the current tag parent.
     * <p>
     * <br><br>
     * If some of the tags to be flushed are still connected to the accessible elements, then these tags are considered
     * as not yet finished ones, and they won't be flushed immediately, but they will be flushed, when the connection
     * is removed.
     *
     * @return this {@link TagStructureContext} instance.
     */
    public TagTreePointer flushTag() {
        if (getCurrentStructElem().getPdfObject() == tagStructureContext.getRootTag().getPdfObject()) {
            throw new PdfException(PdfException.CannotFlushDocumentRootTagBeforeDocumentIsClosed);
        }
        IPdfStructElem parent = tagStructureContext.flushTag(getCurrentStructElem());
        if (parent != null) { // parent is not flushed
            setCurrentStructElem((PdfStructElem) parent);
        } else {
            setCurrentStructElem(tagStructureContext.getRootTag());
        }
        return this;
    }

    /**
     * Gets connected accessible element for the current tag. If tag is not connected to element, behaviour is defined
     * by the createIfNotExist flag.
     * See {@link TagTreePointer#moveToTag} for more explanations about tag connections concept.
     *
     * @param createIfNotExist if <i>true</i>, creates an {@code IAccessibleElement} and connects it to the tag.
     * @return connected {@code IAccessibleElement} if there is one (or if it is created), otherwise null.
     */
    public IAccessibleElement getConnectedElement(boolean createIfNotExist) {
        IAccessibleElement element;
        element = tagStructureContext.getModelConnectedToStruct(getCurrentStructElem());
        if (element == null && createIfNotExist) {
            element = new DummyAccessibleElement(getRole(), getProperties());
            tagStructureContext.saveConnectionBetweenStructAndModel(element, getCurrentStructElem());
        }

        return element;
    }

    /**
     * Gets accessibility properties of the current tag.
     *
     * @return accessibility properties of the current tag.
     */
    public AccessibilityProperties getProperties() {
        PdfStructElem currElem = getCurrentStructElem();
        IAccessibleElement model = tagStructureContext.getModelConnectedToStruct(currElem);
        if (model != null) {
            return model.getAccessibilityProperties();
        } else {
            return new BackedAccessibleProperties(currElem);
        }
    }

    /**
     * Gets current tag role.
     *
     * @return current tag role.
     */
    public PdfName getRole() {
        IAccessibleElement model = tagStructureContext.getModelConnectedToStruct(getCurrentStructElem());
        if (model != null) {
            return model.getRole();
        } else {
            return getCurrentStructElem().getRole();
        }
    }

    /**
     * Sets new role to the current tag.
     *
     * @param role new role to be set.
     * @return this {@link TagStructureContext} instance.
     */
    public TagTreePointer setRole(PdfName role) {
        IAccessibleElement connectedElement = tagStructureContext.getModelConnectedToStruct(getCurrentStructElem());
        if (connectedElement != null) {
            connectedElement.setRole(role);
        } else {
            getCurrentStructElem().setRole(role);
        }
        return this;
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
        currentStructElem = structElem;
        return this;
    }

    PdfStructElem getCurrentStructElem() {
        if (currentStructElem.isFlushed()) {
            throw new PdfException(PdfException.TagTreePointerIsInInvalidStateItPointsAtFlushedElementUseMoveToRoot);
        }
        if (currentStructElem.getPdfObject().getIndirectReference() == null) { // is removed
            throw new PdfException(PdfException.TagTreePointerIsInInvalidStateItPointsAtRemovedElementUseMoveToRoot);
        }

        return currentStructElem;
    }

    private int getNextNewKidPosition() {
        int nextPos = nextNewKidIndex;
        nextNewKidIndex = -1;
        return nextPos;
    }

    private PdfStructElem addNewKid(IAccessibleElement element, boolean keepConnectedToTag) {
        PdfStructElem kid = new PdfStructElem(getDocument(), element.getRole());
        if (keepConnectedToTag) {
            tagStructureContext.saveConnectionBetweenStructAndModel(element, kid);
        }
        if (!keepConnectedToTag && element.getAccessibilityProperties() != null) {
            element.getAccessibilityProperties().setToStructElem(kid);
        }
        return addNewKid(kid);
    }

    private PdfStructElem addNewKid(PdfStructElem kid) {
        return getCurrentStructElem().addKid(getNextNewKidPosition(), kid);
    }

    private PdfMcr addNewKid(PdfMcr kid) {
        return getCurrentStructElem().addKid(getNextNewKidPosition(), kid);
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

    private void writeObject(ObjectOutputStream out) throws IOException {
        throw new NotSerializableException(getClass().toString());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        throw new NotSerializableException(getClass().toString());
    }
}
