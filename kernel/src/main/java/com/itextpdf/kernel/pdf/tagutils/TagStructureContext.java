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

/**
 * {@code TagStructureContext} class is used to track necessary information of document's tag structure.
 * It is also used to make some global modifications of the tag tree like removing or flushing page tags, however
 * these two methods and also others are called automatically and are for the most part for internal usage.
 * <br/><br/>
 * There shall be only one instance of this class per {@code PdfDocument}. To obtain instance of this class use
 * {@link PdfDocument#getTagStructureContext()}.
 */
public class TagStructureContext {
    private static final Set<PdfName> allowedRootTagRoles = new HashSet<PdfName>() {{
        add(PdfName.Book);
        add(PdfName.Document);
        add(PdfName.Part);
        add(PdfName.Art);
        add(PdfName.Sect);
        add(PdfName.Div);
    }};

    private PdfDocument document;
    private PdfStructElem rootTagElement;
    protected TagTreePointer autoTaggingPointer;

    /**
     * These two fields define the connections between tags ({@code PdfStructElem}) and
     * layout model elements ({@code IAccessibleElement}). This connection is used as
     * a sign that tag is not yet finished and therefore should not be flushed or removed
     * if page tags are flushed or removed. Also, any {@code TagTreePointer} could be
     * immediately moved to the tag with connection via it's connected element {@link TagTreePointer#moveToTag}.
     *
     * When connection is removed, accessible element role and properties are set to the structure element.
     */
    private Map<IAccessibleElement, PdfStructElem> connectedModelToStruct;
    private Map<PdfDictionary, IAccessibleElement> connectedStructToModel;

    /**
     * Do not use this constructor, instead use {@link PdfDocument#getTagStructureContext()}
     * method.
     * <br/><br/>
     * Creates {@code TagStructureContext} for document. There shall be only one instance of this
     * class per {@code PdfDocument}.
     * @param document the document which tag structure will be manipulated with this class.
     */
    public TagStructureContext(PdfDocument document) {
        this.document = document;
        if (!document.isTagged()) {
            throw new PdfException(PdfException.MustBeATaggedDocument);
        }
        connectedModelToStruct = new HashMap<>();
        connectedStructToModel = new HashMap<>();

        normalizeDocumentRootTag();
    }

    /**
     * All document auto tagging logic uses {@link TagTreePointer} returned by this method to manipulate tag structure.
     * Typically it points at the root tag. This pointer also could be used to tweak auto tagging process
     * (e.g. move this pointer to the Sect tag, which would result in placing all automatically tagged content
     * under Sect tag).
     * @return the {@code TagTreePointer} which is used for all auto tagging of the document.
     */
    public TagTreePointer getAutoTaggingPointer() {
        if (autoTaggingPointer == null) {
            autoTaggingPointer = new TagTreePointer(document);
        }
        return autoTaggingPointer;
    }

    /**
     * Checks if given {@code IAccessibleElement} is connected to some tag.
     * @param element element to check if it has a connected tag.
     * @return true, if there is a tag which retains the connection to the given accessible element.
     */
    public boolean isElementConnectedToTag(IAccessibleElement element) {
        return connectedModelToStruct.containsKey(element);
    }

    /**
     * Destroys the connection between the given accessible element and the tag to which this element is connected to.
     * @param element {@code IAccessibleElement} which connection to the tag (if there is one) will be removed.
     * @return current {@link TagStructureContext} instance.
     */
    public TagStructureContext removeElementConnectionToTag(IAccessibleElement element) {
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
     * Removes all tags that belong only to this page. The logic which defines if tag belongs to the page is described
     * at {@link #flushPageTags(PdfPage)}.
     * @param page page that defines which tags are to be removed
     * @return current {@link TagStructureContext} instance.
     */
    public TagStructureContext removePageTags(PdfPage page) {
        if (document.getStructTreeRoot().isStructTreeIsPartialFlushed()) {
            // some page tags could already be flushed in this case, and we won't be able even to find parents of these
            // flushed tags to remove them from tag structure
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
     * Sets the tag, which is connected with the given accessible element, as a current tag for the given
     * {@link TagTreePointer}. An exception will be thrown, if given accessible element is not connected to any tag.
     * @param element an element which has a connection with some tag.
     * @param tagPointer {@link TagTreePointer} which will be moved to the tag connected to the given accessible element.
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

    /**
     * If document tag structure was modified on the low (PdfObjects) level, the {@code TagStructureContext} should be
     * reinitialized. If iText does some of these low level modifications, this method is called automatically
     * (e.g. when tagged page is copied to the current document).
     * This method essentially does three things:
     *  <ul>
     *      <li>removes all connections between model elements and tags;</li>
     *      <li>normalizes document root tag (if there is more than one root tags, combines them under the single root tag);</li>
     *      <li>moves auto tagging pointer to the root tag.</li>
     *  </ul>
     * @return current {@link TagStructureContext} instance.
     */
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

    IAccessibleElement getModelConnectedToStruct(PdfStructElem struct) {
        return connectedStructToModel.get(struct);
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

    // returns index of the removed kid
    static int removeKidFromParent(PdfObject kid, PdfDictionary parent) {
        PdfObject parentK = parent.get(PdfName.K);
        int removedKidIndex = -1;
        if (parentK.isArray()) {
            removedKidIndex = removeObjectFromArray((PdfArray) parentK, kid);
        }

        if (parentK.isDictionary() || parentK.isArray() && ((PdfArray)parentK).isEmpty()) {
            parent.remove(PdfName.K);
            removedKidIndex = 0;
        }

        return removedKidIndex;
    }

    static int removeObjectFromArray(PdfArray array, PdfObject toRemove) {
        int i;
        for (i = 0; i < array.size(); ++i) {
            PdfObject obj = array.get(i);
            if (obj == toRemove || obj == toRemove.getIndirectReference()) {
                array.remove(i);
                break;
            }
        }
        return i;
    }

    private void removeStructToModelConnection(PdfStructElem structElem) {
        if (structElem != null) {
            IAccessibleElement element = connectedStructToModel.remove(structElem.getPdfObject());
            structElem.setRole(element.getRole());
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
                    && parentObject != rootTagElement.getPdfObject()) {
                removePageTagFromParent(parentObject, parent.getParent());
                parentObject.getIndirectReference().setFree();
            }
        } else {
            // it is StructTreeRoot
            // should never happen as we always should have only one root tag and we don't remove it
        }
    }

    private void flushParentIfBelongsToPage(PdfStructElem parent, PdfPage currentPage) {
        if (parent.isFlushed() || connectedStructToModel.containsKey(parent.getPdfObject())
                || parent.getPdfObject() == rootTagElement.getPdfObject()) {
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

    /**
     * PDF Reference
     * 10.7.3 Grouping Elements:
     *
     * For most content extraction formats, the document must be a tree with a single top-level element;
     * the structure tree root (identified by the StructTreeRoot entry in the document catalog) must have
     * only one child in its K (kids) array. If the PDF file contains a complete document, the structure
     * type Document is recommended for this top-level element in the logical structure hierarchy. If the
     * file contains a well-formed document fragment, one of the structure types Part, Art, Sect, or Div
     * may be used instead.
     */
    private void normalizeDocumentRootTag() {
        List<IPdfStructElem> rootKids = document.getStructTreeRoot().getKids();

        if (rootKids.size() == 1 && allowedRootTagRoles.contains(rootKids.get(0).getRole())) {
            rootTagElement = (PdfStructElem) rootKids.get(0);
        } else {
            document.getStructTreeRoot().remove(PdfName.K);
            rootTagElement = document.getStructTreeRoot().addKid(new PdfStructElem(document, PdfName.Document));

            for (IPdfStructElem elem : rootKids) {
                // StructTreeRoot kids are always PdfStructElem, so we are save here to cast it
                PdfStructElem kid = (PdfStructElem) elem;
                rootTagElement.addKid(kid);
                if (PdfName.Document.equals(kid.getRole())) {
                    removeOldRoot(kid);
                }
            }
        }
    }

    private void removeOldRoot(PdfStructElem oldRoot) {
        TagTreePointer tagPointer = new TagTreePointer(document);
        tagPointer
                .setCurrentStructElem(oldRoot)
                .removeTag();
    }
}
