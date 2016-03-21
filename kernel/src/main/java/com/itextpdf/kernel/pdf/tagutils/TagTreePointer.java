package com.itextpdf.kernel.pdf.tagutils;

import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
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
import java.util.ArrayList;
import java.util.List;

public class TagTreePointer {
    private TagStructureContext tagStructureContext;
    private PdfStructElem currentStructElem;
    private PdfPage currentPage;
    private PdfStream contentStream;

    public TagTreePointer(PdfDocument document) {
        tagStructureContext = document.getTagStructureContext();
        setCurrentStructElem(tagStructureContext.getRootTag());
    }

    public TagTreePointer(TagTreePointer tagPointer) {
        this.tagStructureContext = tagPointer.tagStructureContext;
        setCurrentStructElem(tagPointer.getCurrentStructElem());
        this.currentPage = tagPointer.currentPage;
        this.contentStream = tagPointer.contentStream;
    }

    /**
     * Sets a page to which tags are referenced to via TagReference.
     * In other words, *TODO rephrase it* all tag references shall be used only on PdfCanvas, which belongs to this page.
     * @param page a page to which tags will be connected to.
     * @return current {@link TagTreePointer} instance.
     */
    public TagTreePointer setPage(PdfPage page) {
        if (page.isFlushed()) {
            throw new PdfException(PdfException.PageWasAlreadyFlushed);
        }
        this.currentPage = page;

        return this;
    }

    /**
     * @return a page to which new tags will be currently referenced to.
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
     * NOTE: It's important to set a {@code null} for the content stream, when tagging of this stream content is finished.
     * @param contentStream the content stream with content which will be connected to the tags.
     */
    public TagTreePointer setContentStream(PdfStream contentStream) {
        this.contentStream = contentStream;
        return this;
    }

    /**
     * Adds a new tag with given role to the tag structure.
     * New tag will be added at the end of the parent's kids array.
     * @param role role of the new tag.
     * @return current {@link TagTreePointer} instance.
     */
    public TagTreePointer addTag(final PdfName role) {
        addTag(-1, role);
        return this;
    }

    /**
     * Adds a new tag with given role to the tag structure.
     * @param index zero-based index in kids array of parent tag at which new tag will be added.
     * @param role role of the new tag.
     * @return current {@link TagTreePointer} instance.
     */
    public TagTreePointer addTag(int index, final PdfName role) {
        addTag(index, new IAccessibleElement() {
            PdfName elementRole = role;
            @Override
            public PdfName getRole() {
                return elementRole;
            }

            @Override
            public void setRole(PdfName role) {
                elementRole = role;
            }

            @Override
            public AccessibleElementProperties getAccessibilityProperties() {
                return null;
            }
        });

        return this;
    }

    /**
     * Adds a new tag to the tag structure.
     * New tag will have a role and attributes defined by the given IAccessibleElement.
     * @param element accessible element which represents a new tag.
     * @return current {@link TagTreePointer} instance.
     */
    public TagTreePointer addTag(IAccessibleElement element) {
        addTag(element, false);
        return this;
    }

    /**
     * Adds a new tag to the tag structure.
     * New tag will have a role and attributes defined by the given IAccessibleElement.
     * <br><br>
     * If {@param keepConnectedToTag} is true a newly created tag will retain the connection with given
     * accessible element. While connection is retained, the tag will not be flushed.
     * Also, if an accessible element is added twice to the same parent and this element is connected with tag -
     * TagStructure would move to connected kid instead of creating tag twice.
     * @param element accessible element which represents a new tag.
     * @param keepConnectedToTag defines if to retain the connection between accessible element and the tag.
     * @return current {@link TagTreePointer} instance.
     */
    public TagTreePointer addTag(IAccessibleElement element, boolean keepConnectedToTag) {
        addTag(-1, element, keepConnectedToTag);
        return this;
    }

    /**
     * Adds a new tag to the tag structure.
     * New tag will have a role and attributes defined by the given IAccessibleElement.
     * @param index zero-based index in kids array of parent tag at which new tag will be added.
     * @param element accessible element which represents a new tag.
     * @return current {@link TagTreePointer} instance.
     */
    public TagTreePointer addTag(int index, IAccessibleElement element) {
        addTag(index, element, false);
        return this;
    }

    /**
     * Adds a new tag to the tag structure.
     * New tag will have a role and attributes defined by the given IAccessibleElement.
     * <br><br>
     * If {@param keepConnectedToTag} is true a newly created tag will retain the connection with given
     * accessible element. While connection is retained, the tag will not be flushed.
     * Also, if an accessible element is added twice to the same parent and this element is connected with tag -
     * TagStructure would move to connected kid instead of creating tag twice.
     * @param index zero-based index in kids array of parent tag at which new tag will be added.
     * @param element accessible element which represents a new tag.
     * @param keepConnectedToTag defines if to retain the connection between accessible element and the tag.
     * @return current {@link TagTreePointer} instance.
     */
    public TagTreePointer addTag(int index, IAccessibleElement element, boolean keepConnectedToTag) {
        throwExceptionIfRoleIsInvalid(element.getRole());
        if (!tagStructureContext.isConnectedToTag(element)) {
            setCurrentStructElem(addNewKid(index, element, keepConnectedToTag));
        } else {
            PdfStructElem connectedStruct = tagStructureContext.getStructConnectedToModel(element);
            if (connectedStruct.getParent() != null && getCurrentStructElem().getPdfObject() == ((PdfStructElem)connectedStruct.getParent()).getPdfObject()) {
                setCurrentStructElem(connectedStruct);
            } else {
                tagStructureContext.removeConnectionToTag(element);
                setCurrentStructElem(addNewKid(index, element, keepConnectedToTag));
            }
        }

        return this;
    }

    public TagTreePointer addAnnotationTag(PdfAnnotation annotation) {
        throwExceptionIfCurrentPageIsNotInited();

        PdfObjRef kid = new PdfObjRef(annotation, getCurrentStructElem());
        if (!ensureElementPageEqualsKidPage(getCurrentStructElem(), currentPage.getPdfObject())) {
            ((PdfDictionary)kid.getPdfObject()).put(PdfName.Pg, currentPage.getPdfObject());
        }
        getCurrentStructElem().addKid(kid);
        return this;
    }

    /**
     * @param element element to check if it has a connected tag.
     * @return true, if there is a tag which retains the connection to the given accessible element.
     */
    public boolean isConnectedToTag(IAccessibleElement element) {
        return tagStructureContext.isConnectedToTag(element);
    }

    /**
     * Destroys the connection between the given accessible element and the tag to which this element is connected.
     * @param element
     * @return current {@link TagStructureContext} instance.
     */
    public TagStructureContext removeConnectionToTag(IAccessibleElement element) {
        return tagStructureContext.removeConnectionToTag(element);
    }

    /**
     * Removes the current tag. If it has kids, they will become kids of the current tag parent.
     * TODO need to test this method thoroughly
     */
    public TagTreePointer removeTag() {
        if (tagStructureContext.getDocument().getStructTreeRoot().isStructTreeIsPartialFlushed()) {
            throw new PdfException(PdfException.CannotRemoveTagStructureElementsIfTagStructureWasPartiallyFlushed);
        }

        List<IPdfStructElem> kids = getCurrentStructElem().getKids();
        PdfDictionary tagPage = getCurrentStructElem().getPdfObject().getAsDictionary(PdfName.Pg);
        IPdfStructElem parentElem = getCurrentStructElem().getParent();
        if (parentElem instanceof PdfStructTreeRoot) {
            throw new PdfException(""); //TODO don't know what to do in this case yet
        }

        PdfStructElem parent = (PdfStructElem) parentElem;

        TagStructureContext.removeKidFromParent(getCurrentStructElem().getPdfObject(), parent.getPdfObject());
        getCurrentStructElem().getPdfObject().getIndirectReference().setFree();

        for (IPdfStructElem kid : kids) {
            if (kid instanceof PdfStructElem) {
                PdfStructElem structElem = (PdfStructElem) kid;
                structElem.getPdfObject().put(PdfName.P, parent.getPdfObject()); // TODO it seems it is not needed, as it is set in addKid
                parent.addKid(structElem);
            } else {
                PdfMcr mcr = (PdfMcr) kid;
                tagStructureContext.getDocument().getStructTreeRoot().unregisterMcr(mcr);
                if (mcr instanceof PdfMcrNumber || !((PdfDictionary)mcr.getPdfObject()).containsKey(PdfName.Pg)) {
                    if (!ensureElementPageEqualsKidPage(parent, tagPage)) {
                        if (mcr instanceof PdfMcrNumber) {
                            PdfDictionary mcrDict = new PdfDictionary();
                            mcrDict.put(PdfName.Type, PdfName.MCR);
                            mcrDict.put(PdfName.Pg, tagPage);
                            mcrDict.put(PdfName.MCID, mcr.getPdfObject());
                            mcr = new PdfMcrDictionary(mcrDict, parent);
                        } else {
                            PdfDictionary mcrDict = (PdfDictionary) mcr.getPdfObject();
                            mcrDict.put(PdfName.Pg, tagPage);
                        }
                    }
                }
                parent.addKid(mcr);
            }
        }
        setCurrentStructElem(parent);
        return this;
    }

    /**
     * Creates a reference to the current tag, which could be use to associate a content on the PdfCanvas with current tag.
     * See {@link PdfCanvas#openTag(PdfTagReference)}
     * @return the reference to the current tag.
     */
    public PdfTagReference getTagReference() {
        return getTagReference(-1);
    }

    /**
     * Creates a reference to the current tag, which could be use to associate a content on the PdfCanvas with current tag.
     * See {@link PdfCanvas#openTag(PdfTagReference)}
     * @param index zero-based index in kids array of tag. These indexes define the order of the content on the page.
     * @return the reference to the current tag.
     */
    public PdfTagReference getTagReference(int index) {
        return new PdfTagReference(getCurrentStructElem(), this, index);
    }

    // TODO should be used for fixing invalid tagpointer in case if element was removed, flushed or tag structure was rebuilt
    public void moveToRoot() {
        setCurrentStructElem(tagStructureContext.getRootTag());
    }

    public TagTreePointer moveToParent() {
        if (getCurrentStructElem().getPdfObject() == tagStructureContext.getRootTag().getPdfObject()) {
            throw new PdfException(PdfException.CannotMoveToParentCurrentElementIsRoot);
        }

        IPdfStructElem parent = getCurrentStructElem().getParent();
        if (parent == null) {
            //TODO log that parent is flushed
            moveToRoot();
        } else {
            setCurrentStructElem((PdfStructElem) parent);
        }
        return this;
    }

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

    public TagTreePointer moveToKid(PdfName role) {
        moveToKid(0, role);
        return this;
    }

    public TagTreePointer moveToKid(int roleIndex, PdfName role) {
        if (PdfName.MCR.equals(role)) {
            throw new PdfException(PdfException.CannotMoveToMarkedContentReference);
        }
        List<IPdfStructElem> kids = getCurrentStructElem().getKids();

        int k = 0;
        for (int i = 0; i < kids.size(); ++i) {
            if (kids.get(i) == null)  continue;
            if (kids.get(i).getRole().equals(role)  && !(kids.get(i) instanceof PdfMcr) && k++ == roleIndex) {
                moveToKid(i);
                return this;
            }
        }

        throw new PdfException(PdfException.NoKidWithSuchRole);
    }

    /**
     * Sets a tag, which is connected with the given accessible element, as a current tag.
     * @param element an element which has a connection with some tag.
     * @return current {@link TagStructureContext} instance.
     */
    public TagTreePointer moveToTag(IAccessibleElement element) {
        tagStructureContext.moveTagPointerToTag(element, this);
        return this;
    }

    /**
     * Gets a list of the roles of current element kids.
     * If certain kid is already flushed, at its position there will be a {@code null}.
     * @return
     */
    public List<PdfName> getListOfKidsRoles() {
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
     *
     * <br><br>
     * If some of the tags to be flushed are still connected to the accessible elements, then these tags are considered
     * as not yet finished ones, and they won't be flushed immediately, but they will be flushed, when the connection
     * is removed.
     */
    public void flushTag() {
        IPdfStructElem parent = tagStructureContext.flushTag(getCurrentStructElem());
        if (parent != null && !(parent instanceof PdfStructTreeRoot)) { // parent is not flushed
            setCurrentStructElem((PdfStructElem) parent);
        } else {
            setCurrentStructElem(tagStructureContext.getRootTag());
        }
    }

    //TODO when method to get an accessible element (optionally connected to the tag) will be implemented, remove this method
    public PdfName getRole() {
        return getCurrentStructElem().getRole();
    }

    public TagTreePointer setProperties(AccessibleElementProperties properties) {
        // TODO set those properties to the connected model probably
        properties.setToStructElem(getCurrentStructElem());
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
                ((PdfDictionary)mcr.getPdfObject()).put(PdfName.Stm, contentStream);
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
            // TODO
        }
        if (currentStructElem.getPdfObject().getIndirectReference() == null) { // is removed // TODO double check that when removing struct element we free it's reference. also check it in copying logic
            // TODO
        }

        return currentStructElem;
    }

    private PdfStructElem addNewKid(int index, IAccessibleElement element, boolean keepConnectedToTag) {
        PdfStructElem kid = new PdfStructElem(tagStructureContext.getDocument(), element.getRole());
        if (keepConnectedToTag) {
            tagStructureContext.saveConnectionBetweenStructAndModel(element, kid);
        }
        if (!keepConnectedToTag && element.getAccessibilityProperties() != null) {
            element.getAccessibilityProperties().setToStructElem(kid);
        }
        return getCurrentStructElem().addKid(index, kid);
    }

    private boolean ensureElementPageEqualsKidPage(PdfStructElem elem, PdfDictionary kidPage) {
        PdfObject pageObject = elem.getPdfObject().get(PdfName.Pg);
        if (pageObject == null) {
            pageObject = kidPage;
            elem.getPdfObject().put(PdfName.Pg, kidPage);
        }

        return kidPage.equals(pageObject);
    }

    private boolean markedContentNotInPageStream() {
        return contentStream != null;
    }

    private void throwExceptionIfRoleIsInvalid(PdfName role) {
        if (PdfStructElem.identifyType(tagStructureContext.getDocument(), role) == PdfStructElem.Unknown) {
            throw new PdfException(PdfException.RoleIsNotMappedWithAnyStandardRole);
        }
    }

    private void throwExceptionIfCurrentPageIsNotInited() {
        if (currentPage == null) {
            throw new PdfException(PdfException.PageIsNotSetForThePdfTagStructure);
        }
    }
}
