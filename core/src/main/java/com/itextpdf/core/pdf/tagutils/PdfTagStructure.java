package com.itextpdf.core.pdf.tagutils;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.pdf.PdfArray;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfNumber;
import com.itextpdf.core.pdf.PdfObject;
import com.itextpdf.core.pdf.PdfPage;
import com.itextpdf.core.pdf.PdfStream;
import com.itextpdf.core.pdf.PdfString;
import com.itextpdf.core.pdf.annot.PdfAnnotation;
import com.itextpdf.core.pdf.canvas.PdfCanvas;
import com.itextpdf.core.pdf.tagging.IPdfStructElem;
import com.itextpdf.core.pdf.tagging.PdfMcr;
import com.itextpdf.core.pdf.tagging.PdfMcrDictionary;
import com.itextpdf.core.pdf.tagging.PdfMcrNumber;
import com.itextpdf.core.pdf.tagging.PdfObjRef;
import com.itextpdf.core.pdf.tagging.PdfStructElem;
import com.itextpdf.core.pdf.tagging.PdfStructTreeRoot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PdfTagStructure {
    protected PdfDocument document;
    protected PdfStructElem documentElem;
    protected PdfStructElem currentStructElem;
    protected PdfPage currentPage;
    protected Map<IAccessibleElement, PdfStructElem> connectedModelToStruct;
    protected Map<PdfDictionary, IAccessibleElement> connectedStructToModel;
    protected PdfStream contentStream;

    public PdfTagStructure(PdfDocument document) {
        this.document = document;
        if (!document.isTagged()) {
            throw new PdfException(PdfException.MustBeATaggedDocument);
        }
        connectedModelToStruct = new HashMap<>();
        connectedStructToModel = new HashMap<>();

        ensureDocumentTagIsOpen();
    }

    /**
     * Sets a page to which tags are referenced to via TagReference.
     * In other words, all tag references shall be used only on PdfCanvas, which belongs to this page.
     * @param page a page to which tags will be connected to.
     * @return current tag structure instance.
     */
    public PdfTagStructure setPage(PdfPage page) {
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
     * Adds a new tag with given role to the tag structure.
     * New tag will be added at the end of the parent's kids array.
     * @param role role of the new tag.
     * @return current tag structure instance.
     */
    public PdfTagStructure addTag(final PdfName role) {
        addTag(-1, role);
        return this;
    }

    /**
     * Adds a new tag with given role to the tag structure.
     * @param index zero-based index in kids array of parent tag at which new tag will be added.
     * @param role role of the new tag.
     * @return current tag structure instance.
     */
    public PdfTagStructure addTag(int index, final PdfName role) {
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
     * @return current tag structure instance.
     */
    public PdfTagStructure addTag(IAccessibleElement element) {
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
     * @return current tag structure instance.
     */
    public PdfTagStructure addTag(IAccessibleElement element, boolean keepConnectedToTag) {
        addTag(-1, element, keepConnectedToTag);
        return this;
    }

    /**
     * Adds a new tag to the tag structure.
     * New tag will have a role and attributes defined by the given IAccessibleElement.
     * @param index zero-based index in kids array of parent tag at which new tag will be added.
     * @param element accessible element which represents a new tag.
     * @return current tag structure instance.
     */
    public PdfTagStructure addTag(int index, IAccessibleElement element) {
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
     * @return current tag structure instance.
     */
    public PdfTagStructure addTag(int index, IAccessibleElement element, boolean keepConnectedToTag) {
        throwExceptionIfRoleIsInvalid(element.getRole());
        if (!connectedModelToStruct.containsKey(element)) {
            currentStructElem = addNewKid(index, element, keepConnectedToTag);
        } else {
            IPdfStructElem parent = connectedModelToStruct.get(element).getParent();
            if (parent != null && currentStructElem.getPdfObject() == ((PdfStructElem)parent).getPdfObject()) {
                currentStructElem = connectedModelToStruct.get(element);
            } else {
                removeConnectionToTag(element);
                currentStructElem = addNewKid(index, element, keepConnectedToTag);
            }
        }

        return this;
    }

    public PdfTagStructure setProperties(AccessibleElementProperties properties) {
        updateProperties(currentStructElem, properties);
        return this;
    }

    public PdfTagStructure addAnnotationTag(PdfAnnotation annotation) {
        PdfObjRef kid = new PdfObjRef(annotation, currentStructElem);
        if (!ensureElementPageEqualsCurrentOne(currentStructElem)) {
            ((PdfDictionary)kid.getPdfObject()).put(PdfName.Pg, currentPage.getPdfObject());
        }
        currentStructElem.addKid(kid);
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
        return new PdfTagReference(currentStructElem, this, index);
    }

    public void moveToRoot() {
        currentStructElem = documentElem;
    }

    public PdfTagStructure moveToParent() {
        if (currentStructElem.getPdfObject() == documentElem.getPdfObject()) {
            throw new PdfException(PdfException.CannotMoveToParentCurrentElementIsRoot);
        }

        IPdfStructElem parent = currentStructElem.getParent();
        if (parent == null) {
            //TODO log that parent is flushed
            moveToRoot();
        } else {
            currentStructElem = (PdfStructElem) parent;
        }
        return this;
    }

    public PdfTagStructure moveToKid(int kidIndex) {
        IPdfStructElem kid = currentStructElem.getKids().get(kidIndex);
        if (kid instanceof PdfStructElem) {
            currentStructElem = (PdfStructElem) kid;
        } else if (kid instanceof PdfMcr) {
            throw new PdfException(PdfException.CannotMoveToMarkedContentReference);
        } else {
            throw new PdfException(PdfException.CannotMoveToFlushedKid);
        }
        return this;
    }

    public PdfTagStructure moveToKid(PdfName role) {
        moveToKid(0, role);
        return this;
    }

    public PdfTagStructure moveToKid(int roleIndex, PdfName role) {
        if (PdfName.MCR.equals(role)) {
            throw new PdfException(PdfException.CannotMoveToMarkedContentReference);
        }
        List<IPdfStructElem> kids = currentStructElem.getKids();

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
     * Gets a list of the roles of current element kids.
     * If certain kid is already flushed, at its position there will be a {@code null}.
     * @return
     */
    public List<PdfName> getListOfKidsRoles() {
        List<PdfName> roles = new ArrayList<>();
        List<IPdfStructElem> kids = currentStructElem.getKids();
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
     * @param element element to check if it has a connected tag.
     * @return true, if there is a tag which retains the connection to the given accessible element.
     */
    public boolean isConnectedToTag(IAccessibleElement element) {
        return connectedModelToStruct.containsKey(element);
    }

    /**
     * Sets a tag, which is connected with the given accessible element, as a current tag.
     * @param element an element which has a connection with some tag.
     * @return current tag structure instance.
     */
    public PdfTagStructure moveToTag(IAccessibleElement element) {
        if (!connectedModelToStruct.containsKey(element)) {
            throw new PdfException(PdfException.GivenAccessibleElementIsNotConnectedToAnyTag);
        }
        currentStructElem = connectedModelToStruct.get(element);
        return this;
    }

    /**
     * Destroys the connection between the given accessible element and the tag to which this element is connected.
     * @param element
     * @return current tag structure instance.
     */
    public PdfTagStructure removeConnectionToTag(IAccessibleElement element) {
        PdfStructElem structElem = connectedModelToStruct.remove(element);
        removeStructToModelConnection(structElem);
        return this;
    }

    /**
     * Destroys all the retained connections.
     * @return current tag structure instance.
     */
    public PdfTagStructure removeAllConnectionsToTags() {
        for (PdfStructElem structElem : connectedModelToStruct.values()) {
            removeStructToModelConnection(structElem);
        }
        connectedModelToStruct.clear();
        return this;
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
    public void setContentStream(PdfStream contentStream) {
        this.contentStream = contentStream;
    }

    /**
     * Flushes the tags which are considered to belong to the given page.
     * The logic that defines if the given tag (structure element) belongs to the page is the following:
     * if all the marked content references (dictionary or number references), that are the
     * descenders of the given structure element, belong to the current page - the tag is considered
     * to belong to the page. If tag has descenders from several pages - it is flushed, if all other pages except the
     * current one are flushed.
     *
     * <br><br>
     * If some of the page's tags are still connected to the accessible elements, in this case these tags are considered
     * as not yet finished ones, and they won't be flushed immediately, but they will be flushed, when the connection
     * is removed.
     * @param page a page which tags will be flushed.
     */
    public void flushPageTags(PdfPage page) {
        PdfStructTreeRoot structTreeRoot = document.getStructTreeRoot();
        List<PdfMcr> pageMcrs = structTreeRoot.getPageMarkedContentReferences(page);
        if (pageMcrs != null) {
            for (PdfMcr mcr : pageMcrs) {
                PdfStructElem parent = (PdfStructElem) mcr.getParent();
                flushParentIfBelongsToPage(parent, page);
            }
        }
    }

    /**
     * Flushes the current tag and all it's descenders.
     *
     * <br><br>
     * If some of the tags to be flushed are still connected to the accessible elements, in this case these tags are considered
     * as not yet finished ones, and they won't be flushed immediately, but they will be flushed, when the connection
     * is removed.
     */
    public void flushTag() {
        IAccessibleElement modelElement = connectedStructToModel.remove(currentStructElem.getPdfObject());
        if (modelElement != null) {
            connectedModelToStruct.remove(modelElement);
        }

        IPdfStructElem parent = currentStructElem.getParent();
        flushStructElementAndItKids(currentStructElem);
        if (parent != null && !(parent instanceof PdfStructTreeRoot)) { // parent is not flushed
            currentStructElem = (PdfStructElem) parent;
        } else {
            currentStructElem = documentElem;
        }
    }

    protected int createNextMcidForStructElem(PdfStructElem elem, int index) {
        PdfMcr mcr;
        if (!markedContentNotInPageStream() && ensureElementPageEqualsCurrentOne(elem)) {
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

    private void removeStructToModelConnection(PdfStructElem structElem) {
        if (structElem != null) {
            IAccessibleElement element = connectedStructToModel.remove(structElem.getPdfObject());
            updateProperties(structElem, element.getAccessibilityProperties());
            if (structElem.getParent() == null) { // is flushed
                flushStructElementAndItKids(structElem);
            }
        }
    }

    private void flushParentIfBelongsToPage(PdfStructElem parent, PdfPage currentPage) {
        if (parent.isFlushed() || connectedStructToModel.containsKey(parent.getPdfObject())) {
            return;
        }

        List<IPdfStructElem> kids = parent.getKids();
        boolean allKidsBelongToPage = true;
        for (IPdfStructElem kid : kids) {
            if (kid instanceof PdfMcr) {
                PdfDictionary kidPage = ((PdfMcr) kid).getPageObject();
                if (!kidPage.isFlushed() && !kidPage.equals(currentPage.getPdfObject())) {
                    allKidsBelongToPage = false;
                    break;
                }
            } else if (kid instanceof PdfStructElem) {
                allKidsBelongToPage = false;
                break;
            }
        }

        if (allKidsBelongToPage) {
            PdfStructTreeRoot structTreeRoot = document.getStructTreeRoot();
            IPdfStructElem parentsParent = parent.getParent();
            structTreeRoot.flushStructElement(parent);
            if (parentsParent instanceof PdfStructElem) {
                flushParentIfBelongsToPage((PdfStructElem)parentsParent, currentPage);
            }
        }

        return;
    }

    private void flushStructElementAndItKids(PdfStructElem elem) {
        if (connectedStructToModel.containsKey(elem.getPdfObject())) {
            return;
        }

        for (IPdfStructElem kid : elem.getKids()) {
            if (kid instanceof PdfStructElem) {
                flushStructElementAndItKids((PdfStructElem) kid);
            }
        }
        document.getStructTreeRoot().flushStructElement(elem);
    }

    private boolean markedContentNotInPageStream() {
        return contentStream != null;
    }

    private boolean ensureElementPageEqualsCurrentOne(PdfStructElem elem) {
        if (currentPage == null) {
            throw new PdfException(PdfException.PageIsNotSetForThePdfTagStructure);
        }
        PdfObject pageObject = elem.getPdfObject().get(PdfName.Pg);
        if (pageObject == null) {
            pageObject = currentPage.getPdfObject();
            elem.getPdfObject().put(PdfName.Pg, pageObject);
        }

        return currentPage.getPdfObject().equals(pageObject);
    }

    private void ensureDocumentTagIsOpen() {
        List<IPdfStructElem> rootKids = document.getStructTreeRoot().getKids();
        if (rootKids.isEmpty()) {
            documentElem = document.getStructTreeRoot().addKid(new PdfStructElem(document, com.itextpdf.core.pdf.PdfName.Document));
        }

        if (documentElem == null) {
            documentElem = (PdfStructElem) rootKids.get(0);
        }
        if (currentStructElem == null) {
            currentStructElem = documentElem;
        }
    }

    private PdfStructElem addNewKid(int index, IAccessibleElement element, boolean keepConnectedToTag) {
        PdfStructElem kid = new PdfStructElem(document, element.getRole());
        if (keepConnectedToTag) {
            connectedModelToStruct.put(element, kid);
            connectedStructToModel.put(kid.getPdfObject(), element);
        }
        if (!keepConnectedToTag) {
            updateProperties(kid, element.getAccessibilityProperties());
        }
        return  currentStructElem.addKid(index, kid);
    }

    private void updateProperties(PdfStructElem elem, AccessibleElementProperties properties) {
        if (properties == null) {
            return;
        }

        if (properties.getActualText() != null) {
            elem.setActualText(new PdfString(properties.getActualText()));
        }
        if (properties.getAlternateDescription() != null) {
            elem.setAlt(new PdfString(properties.getAlternateDescription()));
        }
        if (properties.getExpansion() != null) {
            elem.setE(new PdfString(properties.getExpansion()));
        }
        if (properties.getLanguage() != null) {
            elem.setLang(new PdfString(properties.getLanguage()));
        }

        List<PdfDictionary> newAttributesList = properties.getAttributesList();
        if (!newAttributesList.isEmpty()) {
            PdfObject attributesObject = elem.getAttributes(false);

            PdfObject combinedAttributes = combineAttributesList(attributesObject, newAttributesList, elem.getPdfObject().getAsNumber(PdfName.R));
            elem.setAttributes(combinedAttributes);
        }
    }

    private PdfObject combineAttributesList(PdfObject attributesObject, List<PdfDictionary> newAttributesList, PdfNumber revision) {
        PdfObject combinedAttributes;

        if (attributesObject instanceof PdfDictionary) {
            PdfArray combinedAttributesArray = new PdfArray();
            combinedAttributesArray.add(attributesObject);
            addNewAttributesToAttributesArray(newAttributesList, revision, combinedAttributesArray);
            combinedAttributes = combinedAttributesArray;
        } else if (attributesObject instanceof PdfArray) {
            PdfArray combinedAttributesArray = (PdfArray) attributesObject;
            addNewAttributesToAttributesArray(newAttributesList, revision, combinedAttributesArray);
            combinedAttributes = combinedAttributesArray;
        } else {
            if (newAttributesList.size() == 1) {
                combinedAttributes = newAttributesList.get(0);
            } else {
                combinedAttributes = new PdfArray();
                addNewAttributesToAttributesArray(newAttributesList, revision, (PdfArray) combinedAttributes);
            }
        }

        return combinedAttributes;
    }

    private void addNewAttributesToAttributesArray(List<PdfDictionary> newAttributesList, PdfNumber revision, PdfArray attributesArray) {
        if (revision != null) {
            for (PdfDictionary attributes : newAttributesList) {
                attributesArray.add(attributes);
                attributesArray.add(revision);
            }
        } else {
            attributesArray.addAll(newAttributesList);
        }
    }

    private void throwExceptionIfRoleIsInvalid(PdfName role) {
        if (PdfStructElem.identifyType(document, role) == PdfStructElem.Unknown) {
            throw new PdfException(PdfException.RoleIsNotMappedWithAnyStandardRole);
        }
    }
}
