/*
    $Id$

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV
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

import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.tagging.IPdfStructElem;
import com.itextpdf.kernel.pdf.tagging.PdfMcr;
import com.itextpdf.kernel.pdf.tagging.PdfObjRef;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;
import com.itextpdf.kernel.pdf.tagging.PdfStructTreeRoot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
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
public class TagStructureContext implements Serializable {

    private static final long serialVersionUID = -7870069015800895036L;

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
        PdfStructElem structElem = null;
        PdfDictionary annotDic = annotation.getPdfObject();

        PdfNumber structParentIndex = (PdfNumber) annotDic.get(PdfName.StructParent);
        if (structParentIndex != null) {
            PdfObjRef objRef = document.getStructTreeRoot().findObjRefByStructParentIndex(annotDic.getAsDictionary(PdfName.P), structParentIndex.getIntValue());

            if (objRef != null) {
                PdfStructElem parent = (PdfStructElem) objRef.getParent();
                parent.removeKid(objRef);
                structElem = parent;
            }
        }
        annotDic.remove(PdfName.StructParent);

        if (structElem != null) {
            return new TagTreePointer(document).setCurrentStructElem(structElem);
        }
        return null;
    }

    /**
     * Removes content item from the tag structure.
     * <br/>
     * Nothing happens if there is no such mcid on given page.
     * @param page page, which contains this content item
     * @param mcid marked content id of this content item
     * @return {@code TagTreePointer} which points at the parent of the removed content item, or null if there is no
     * such mcid on given page.
     */
    public TagTreePointer removeContentItem(PdfPage page, int mcid) {
        PdfMcr mcr = document.getStructTreeRoot().findMcrByMcid(page.getPdfObject(), mcid);
        if (mcr == null) {
            return null;
        }

        PdfStructElem parent = (PdfStructElem) mcr.getParent();
        parent.removeKid(mcr);
        return new TagTreePointer(document).setCurrentStructElem(parent);
    }

    /**
     * Removes all tags that belong only to this page. The logic which defines if tag belongs to the page is described
     * at {@link #flushPageTags(PdfPage)}.
     * @param page page that defines which tags are to be removed
     * @return current {@link TagStructureContext} instance.
     */
    public TagStructureContext removePageTags(PdfPage page) {
        PdfStructTreeRoot structTreeRoot = document.getStructTreeRoot();
        Collection<PdfMcr> pageMcrs = structTreeRoot.getPageMarkedContentReferences(page);
        if (pageMcrs != null) {
            // We create a copy here, because pageMcrs is backed by the internal collection which is changed when mcrs are removed.
            List<PdfMcr> mcrsList = new ArrayList<>(pageMcrs);
            for (PdfMcr mcr : mcrsList) {
                removePageTagFromParent(mcr, mcr.getParent());
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
        Collection<PdfMcr> pageMcrs = structTreeRoot.getPageMarkedContentReferences(page);
        if (pageMcrs != null) {
            for (PdfMcr mcr : pageMcrs) {
                PdfStructElem parent = (PdfStructElem) mcr.getParent();
                flushParentIfBelongsToPage(parent, page);
            }
        }

        return this;
    }

    /**
     * Transforms root tags in a way that complies with the PDF References.
     *
     * <br/><br/>
     * PDF Reference
     * 10.7.3 Grouping Elements:
     *
     * <br/><br/>
     * For most content extraction formats, the document must be a tree with a single top-level element;
     * the structure tree root (identified by the StructTreeRoot entry in the document catalog) must have
     * only one child in its K (kids) array. If the PDF file contains a complete document, the structure
     * type Document is recommended for this top-level element in the logical structure hierarchy. If the
     * file contains a well-formed document fragment, one of the structure types Part, Art, Sect, or Div
     * may be used instead.
     */
    public void normalizeDocumentRootTag() {
        List<IPdfStructElem> rootKids = document.getStructTreeRoot().getKids();

        if (rootKids.size() == 1 && allowedRootTagRoles.contains(rootKids.get(0).getRole())) {
            rootTagElement = (PdfStructElem) rootKids.get(0);
        } else {
            PdfStructElem prevRootTag = rootTagElement;
            document.getStructTreeRoot().getPdfObject().remove(PdfName.K);
            if (prevRootTag == null) {
                rootTagElement = document.getStructTreeRoot().addKid(new PdfStructElem(document, PdfName.Document));
            } else {
                document.getStructTreeRoot().addKid(rootTagElement);
                if (!PdfName.Document.equals(rootTagElement.getRole())) {
                    wrapAllKidsInTag(rootTagElement, rootTagElement.getRole());
                    rootTagElement.setRole(PdfName.Document);
                }
            }

            int originalRootKidsIndex = 0;
            boolean isBeforeOriginalRoot = true;
            for (IPdfStructElem elem : rootKids) {
                // StructTreeRoot kids are always PdfStructElem, so we are save here to cast it
                PdfStructElem kid = (PdfStructElem) elem;
                if (kid.getPdfObject() == rootTagElement.getPdfObject()) {
                    isBeforeOriginalRoot = false;
                    continue;
                }

                boolean kidIsDocument = PdfName.Document.equals(kid.getRole());
                if (isBeforeOriginalRoot) {
                    rootTagElement.addKid(originalRootKidsIndex, kid);
                    originalRootKidsIndex += kidIsDocument ? kid.getKids().size() : 1;
                } else {
                    rootTagElement.addKid(kid);
                }
                if (kidIsDocument) {
                    removeOldRoot(kid);
                }
            }
        }
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

    private void removePageTagFromParent(IPdfStructElem pageTag, IPdfStructElem parent) {
        if (parent instanceof PdfStructElem) {
            PdfStructElem structParent = (PdfStructElem) parent;
            if (!structParent.isFlushed()) {
                structParent.removeKid(pageTag);
                PdfDictionary parentObject = structParent.getPdfObject();
                if (!connectedStructToModel.containsKey(parentObject) && parent.getKids().isEmpty()
                        && parentObject != rootTagElement.getPdfObject()) {
                    removePageTagFromParent(structParent, parent.getParent());
                    parentObject.getIndirectReference().setFree();
                }
            } else {
                if (pageTag instanceof PdfMcr) {
                    throw new PdfException(PdfException.CannotRemoveTagBecauseItsParentIsFlushed);
                }
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
            IPdfStructElem parentsParent = parent.getParent();
            parent.flush();
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
        elem.flush();
    }

    private void wrapAllKidsInTag(PdfStructElem parent, PdfName wrapTagRole) {
        int kidsNum = parent.getKids().size();
        TagTreePointer tagPointer = new TagTreePointer(document);
        tagPointer
                .setCurrentStructElem(parent)
                .addTag(0, wrapTagRole);
        TagTreePointer newParentOfKids = new TagTreePointer(tagPointer);
        tagPointer.moveToParent();
        for (int i = 0; i < kidsNum; ++i) {
            tagPointer.relocateKid(1, newParentOfKids);
        }
    }

    private void removeOldRoot(PdfStructElem oldRoot) {
        TagTreePointer tagPointer = new TagTreePointer(document);
        tagPointer
                .setCurrentStructElem(oldRoot)
                .removeTag();
    }
}
