package com.itextpdf.kernel.pdf.tagutils;

import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.tagging.IPdfStructElem;
import com.itextpdf.kernel.pdf.tagging.PdfMcr;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;
import com.itextpdf.kernel.pdf.tagging.PdfStructTreeRoot;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TagStructureContext {
    private static final Set<PdfName> allowedRootTagRoles = new HashSet<PdfName>() {{
        add(PdfName.Document);
        add(PdfName.Part);
        add(PdfName.Art);
        add(PdfName.Sect);
    }};

    private PdfDocument document;
    private PdfStructElem rootTagElement;
    protected TagTreePointer autoTaggingPointer;

    // TODO describe thoroughly
    private Map<IAccessibleElement, PdfStructElem> connectedModelToStruct;
    private Map<PdfDictionary, IAccessibleElement> connectedStructToModel;

    // TODO shall be one per document
    public TagStructureContext(PdfDocument document) {
        this.document = document;
        if (!document.isTagged()) {
            throw new PdfException(PdfException.MustBeATaggedDocument);
        }
        connectedModelToStruct = new HashMap<>();
        connectedStructToModel = new HashMap<>();

        normalizeDocumentRootTag();
    }

    public TagTreePointer getAutoTaggingPointer() {
        if (autoTaggingPointer == null) {
            autoTaggingPointer = new TagTreePointer(document);
        }
        return autoTaggingPointer;
    }

    /**
     * @param element element to check if it has a connected tag.
     * @return true, if there is a tag which retains the connection to the given accessible element.
     */
    public boolean isConnectedToTag(IAccessibleElement element) {
        return connectedModelToStruct.containsKey(element);
    }

    /**
     * Destroys the connection between the given accessible element and the tag to which this element is connected.
     * @param element
     * @return current {@link TagStructureContext} instance.
     */
    public TagStructureContext removeConnectionToTag(IAccessibleElement element) {
        PdfStructElem structElem = connectedModelToStruct.remove(element);
        removeStructToModelConnection(structElem);
        return this;
    }

    /**
     * Removes annotation content item from the tag structure.
     * If annotation is not added to the document or is not tagged, nothing will happen.
     * @return {@link TagTreePointer} instance which points at annotation tag parent if annotation was removed,
     * otherwise returns null.
     */
    public TagTreePointer removeAnnotationTag(PdfAnnotation annotation) {
        PdfStructElem structElem = document.getStructTreeRoot().removeAnnotationObjectReference(annotation.getPdfObject());
        if (structElem != null) {
            return new TagTreePointer(document).setCurrentStructElem(structElem);
        }
        return null;
    }

    /**
     * Removes all tags that belong only to this page. For the method which defines if tag belongs to the page see
     * {@link #flushPageTags(PdfPage)}.
     * @param page page that defines which tags are to be removed
     * @return current {@link TagStructureContext} instance.
     */
    public TagStructureContext removePageTags(PdfPage page) {
        if (document.getStructTreeRoot().isStructTreeIsPartialFlushed()) {
            throw new PdfException(PdfException.CannotRemoveTagStructureElementsIfTagStructureWasPartiallyFlushed);
        }

        PdfStructTreeRoot structTreeRoot = document.getStructTreeRoot();
        List<PdfMcr> pageMcrs = structTreeRoot.getPageMarkedContentReferences(page);
        if (pageMcrs != null) {
            int mcrsCount = pageMcrs.size();
            PdfMcr mcr;
            // it's crucial to run this cycle backwards, because at each iteration we remove mcr
            for (int i = mcrsCount - 1; i >= 0; --i) {
                mcr = pageMcrs.get(i);
                removePageTagFromParent(mcr.getPdfObject(), mcr.getParent());
                document.getStructTreeRoot().unregisterMcr(mcr);
            }
        }
        return this;
    }

    /**
     * Sets a tag which is connected with the given accessible element as a current tag for given {@link TagTreePointer}.
     * @param element an element which has a connection with some tag.
     * @param tagPointer {@link TagTreePointer} which will be moved to the tag connected to the given accessible element
     * @return current {@link TagStructureContext} instance.
     */
    public TagStructureContext moveTagPointerToTag(IAccessibleElement element, TagTreePointer tagPointer) {
        PdfStructElem connectedStructElem = connectedModelToStruct.get(element);
        if (connectedStructElem == null) {
            throw new PdfException(PdfException.GivenAccessibleElementIsNotConnectedToAnyTag);
        }
        tagPointer.setCurrentStructElem(connectedStructElem);
        return this;
    }

    /**
     * Destroys all the retained connections.
     * @return current {@link TagStructureContext} instance.
     */
    public TagStructureContext removeAllConnectionsToTags() {
        for (PdfStructElem structElem : connectedModelToStruct.values()) {
            removeStructToModelConnection(structElem);
        }
        connectedModelToStruct.clear();
        return this;
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
     * as not yet finished ones, and they won't be flushed.
     * @param page a page which tags will be flushed.
     */
    public TagStructureContext flushPageTags(PdfPage page) {
        PdfStructTreeRoot structTreeRoot = document.getStructTreeRoot();
        List<PdfMcr> pageMcrs = structTreeRoot.getPageMarkedContentReferences(page);
        if (pageMcrs != null) {
            for (PdfMcr mcr : pageMcrs) {
                PdfStructElem parent = (PdfStructElem) mcr.getParent();
                flushParentIfBelongsToPage(parent, page);
            }
        }

        return this;
    }

    //TODO consider the name and document it
    public TagStructureContext reinitialize() {
        removeAllConnectionsToTags();
        normalizeDocumentRootTag();
        getAutoTaggingPointer().moveToRoot();
        return this;
    }

    PdfStructElem getRootTag() {
        return rootTagElement;
    }
    PdfDocument getDocument() {
        return document;
    }

    PdfStructElem getStructConnectedToModel(IAccessibleElement element) {
        return connectedModelToStruct.get(element);
    }

    void saveConnectionBetweenStructAndModel(IAccessibleElement element, PdfStructElem structElem) {
        connectedModelToStruct.put(element, structElem);
        connectedStructToModel.put(structElem.getPdfObject(), element);
    }

    /**
     * @return parent of the flushed tag
     */
    IPdfStructElem flushTag(PdfStructElem tagStruct) {
        IAccessibleElement modelElement = connectedStructToModel.remove(tagStruct.getPdfObject());
        if (modelElement != null) {
            connectedModelToStruct.remove(modelElement);
        }

        IPdfStructElem parent = tagStruct.getParent();
        flushStructElementAndItKids(tagStruct);
        return parent;
    }

    static void removeKidFromParent(PdfObject kid, PdfDictionary parent) {
        PdfObject parentK = parent.get(PdfName.K);
        if (parentK.isArray()) {
            removeObjectFromArray((PdfArray) parentK, kid);
        }

        if (parentK.isDictionary() || parentK.isArray() && ((PdfArray)parentK).isEmpty()) {
            parent.remove(PdfName.K);
        }
    }

    static boolean removeObjectFromArray(PdfArray array, PdfObject toRemove) {
        boolean removed;
        if (!(removed = array.remove(toRemove))) {
            removed = array.remove(toRemove.getIndirectReference());
        }
        return removed;
    }

    private void removeStructToModelConnection(PdfStructElem structElem) {
        if (structElem != null) {
            IAccessibleElement element = connectedStructToModel.remove(structElem.getPdfObject());
            if (element.getAccessibilityProperties() != null) {
                element.getAccessibilityProperties().setToStructElem(structElem);
            }
            if (structElem.getParent() == null) { // is flushed
                flushStructElementAndItKids(structElem);
            }
        }
    }

    private void removePageTagFromParent(PdfObject pageTagObject, IPdfStructElem parent) {
        if (parent instanceof PdfStructElem) {
            PdfDictionary parentObject = ((PdfStructElem) parent).getPdfObject();
            removeKidFromParent(pageTagObject, parentObject);
            if (!connectedStructToModel.containsKey(parentObject) && parent.getKids().isEmpty()
                    && parentObject != rootTagElement.getPdfObject() // TODO this could not solve the problems if there is several root elements under StructTreeRoot
                    ) {
                removePageTagFromParent(parentObject, parent.getParent());
                parentObject.getIndirectReference().setFree();
            }
        } else { // it is StructTreeRoot
            // TODO don't know what to do here. remove from its kids or there should be some root tag like Document which I don't want to remove
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
                // If kid is structElem and was already flushed then in kids list there will be null for it instead of
                // PdfStructElem. And therefore if we get into this if clause it means that some StructElem wasn't flushed.
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

    private void normalizeDocumentRootTag() {
        List<IPdfStructElem> rootKids = document.getStructTreeRoot().getKids();
        if (rootKids.isEmpty()) {
            rootTagElement = document.getStructTreeRoot().addKid(new PdfStructElem(document, PdfName.Document));
        }

        if (rootTagElement == null) {
            rootTagElement = (PdfStructElem) rootKids.get(0);
        }
    }
}
