package com.itextpdf.core.pdf.tagutils;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.pdf.PdfArray;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfNumber;
import com.itextpdf.core.pdf.PdfObject;
import com.itextpdf.core.pdf.PdfPage;
import com.itextpdf.core.pdf.PdfString;
import com.itextpdf.core.pdf.tagging.IPdfStructElem;
import com.itextpdf.core.pdf.tagging.PdfMcr;
import com.itextpdf.core.pdf.tagging.PdfMcrDictionary;
import com.itextpdf.core.pdf.tagging.PdfMcrNumber;
import com.itextpdf.core.pdf.tagging.PdfStructElem;
import com.itextpdf.core.pdf.tagging.PdfStructTreeRoot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* TODO
    1. add possibility to specify role mapping through this class
    2. add flushing logic? or add it to page flushing? need to build parentsTree when flushing.
    3. IllegalStateExceptions
    4. add convenient way to see current tags tree (toString method?)
 */
public class PdfTagStructure {
    protected PdfDocument document;
    protected PdfStructElem documentElem; //?
    protected PdfStructElem currentStructElem;
    protected PdfPage currentPage;
    protected Map<IAccessibleElement, PdfStructElem> connectedModelToStruct;
    protected Map<PdfDictionary, IAccessibleElement> connectedStructToModel;

    //TODO do not modify tag structure in constructor
    public PdfTagStructure(PdfDocument document) {
        this.document = document;
        if (!document.isTagged()) {
            throw new PdfException(""); //TODO exception
        }
        connectedModelToStruct = new HashMap<>();
        connectedStructToModel = new HashMap<>();

        //TODO remove this
        ensureDocumentTagIsOpen();
    }

    public PdfTagStructure setPage(PdfPage page) {
        if (page.isFlushed()) {
            throw new PdfException(""); //TODO exception
        }
        this.currentPage = page;

        return this;
    }

    public PdfTagStructure addTag(final PdfName role) {
        addTag(-1, role);
        return this;
    }

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

    public PdfTagStructure addTag(IAccessibleElement element) {
        addTag(element, false);
        return this;
    }

    public PdfTagStructure addTag(IAccessibleElement element, boolean keepConnectedToTag) {
        addTag(-1, element, keepConnectedToTag);
        return this;
    }

    public PdfTagStructure addTag(int index, IAccessibleElement element) {
        addTag(index, element, false);
        return this;
    }

    public PdfTagStructure addTag(int index, IAccessibleElement element, boolean keepConnectedToTag) {
        throwExceptionIfRoleIsInvalid(element.getRole());
        if (!connectedModelToStruct.containsKey(element)) {
            currentStructElem = addNewKid(index, element, keepConnectedToTag);
        } else {
            IPdfStructElem parent = connectedModelToStruct.get(element).getParent();
            //TODO thinking in this place that only one element could be child of root. check.
            if (parent != null && currentStructElem.getPdfObject() == ((PdfStructElem)parent).getPdfObject()) {
                currentStructElem = connectedModelToStruct.get(element);
            } else {
                removeConnectionToTag(element);
                currentStructElem = addNewKid(index, element, keepConnectedToTag);
            }
        }
        updateProperties(currentStructElem, element.getAccessibilityProperties());

        return this;
    }

    public PdfTagStructure setProperties(AccessibleElementProperties properties) {
        updateProperties(currentStructElem, properties);
        return this;
    }

    public PdfTagReference getTagReference() {
        return getTagReference(-1);
    }

    public PdfTagReference getTagReference(int index) {
        return new PdfTagReference(currentStructElem, this, index);
    }

    //TODO review this
    public void moveToRoot() {
        currentStructElem = documentElem;
    }

    public PdfTagStructure moveToParent() {
        if (currentStructElem.getParent() == documentElem) {
            //TODO don't do this
        }
        // TODO if parent == null, move to the root; log it.

        currentStructElem = (PdfStructElem) currentStructElem.getParent();
        return this;
    }

    public PdfTagStructure moveToKid(int kidIndex) {
        IPdfStructElem kid = currentStructElem.getKids().get(kidIndex);
        if (kid instanceof PdfStructElem) {
            currentStructElem = (PdfStructElem) kid;
        } else if (kid instanceof PdfMcr) {
            throw new PdfException(""); //TODO exception: could not move to marked content references
        } else {
            throw new PdfException(""); //TODO exception: could not move to the flushed kid
        }
        return this;
    }

    public PdfTagStructure moveToKid(PdfName role) {
        moveToKid(0, role);
        return this;
    }

    public PdfTagStructure moveToKid(int /*TODO*/roleIndex, PdfName role) {
        if (PdfName.MCR.equals(role)) {
            throw new PdfException(""); //TODO exception: could not move to marked content references
        }
        List<IPdfStructElem> kids = currentStructElem.getKids();

        int k = 0;
        for (int i = 0; i < kids.size(); ++i) {
            if (kids.get(i) == null)  continue;
            //TODO what if kid - is mcr, but there is some other kid that is structElement. is it possible?
            if (kids.get(i).getRole().equals(role) && k++ == roleIndex) {
                moveToKid(i);
                return this;
            }
        }

        throw new PdfException(""); //TODO exception: no kid with such role
    }

    //returns null for kids that are flushed
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

    public boolean isConnectedToTag(IAccessibleElement element) {
        return connectedModelToStruct.containsKey(element);
    }

    public PdfTagStructure moveToTag(IAccessibleElement element) {
        if (!connectedModelToStruct.containsKey(element)) {
            throw new PdfException(""); //TODO exception: not connected
        }
        currentStructElem = connectedModelToStruct.get(element);
        return this;
    }

    public PdfTagStructure removeConnectionToTag(IAccessibleElement element) {
        PdfStructElem structElem = connectedModelToStruct.remove(element);
        removeStructToModelConnection(structElem);
        return this;
    }

    //TODO don't forget to call it on document close
    public PdfTagStructure removeAllConnectionsToTags() {
        for (PdfStructElem structElem : connectedModelToStruct.values()) {
            removeStructToModelConnection(structElem);
        }
        connectedModelToStruct.clear();
        return this;
    }

    private void removeStructToModelConnection(PdfStructElem structElem) {
        if (structElem != null) {
            if (structElem.getParent() == null) { // is flushed
                flushStructElementAndItKids(structElem);
            }
            connectedStructToModel.remove(structElem.getPdfObject());
        }
    }

    //-------------------------------------------------------------------------------------------------


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

    public void flushTag() {
        IAccessibleElement modelElement = connectedStructToModel.remove(currentStructElem.getPdfObject());
        if (modelElement != null) {
            connectedModelToStruct.remove(modelElement);
        }

        //TODO if flushing document level tag?
        IPdfStructElem parent = currentStructElem.getParent();
        flushStructElementAndItKids(currentStructElem);
        if (parent != null && !(parent instanceof PdfStructTreeRoot)) { // parent is not flushed
            currentStructElem = (PdfStructElem) parent;
        } else {
            currentStructElem = documentElem;
        }
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

    //-------------------------------------------------------------------------------------------------

    //TODO if strucElement contains properties that have to or better to write to canvas (like E) - add them to the tag here (and may be remove them from the structure element?)
    protected int getNextMcidForStructElem(PdfStructElem elem, int index) {
        if (currentPage == null) {
            throw new PdfException(""); //TODO exception
        }

        int type = PdfStructElem.identifyType(document, elem.getRole());
        if (type != PdfStructElem.InlineLevel && type != PdfStructElem.Illustration) {
            throw new PdfException(""); //TODO exception
        }

        //TODO check this logic on created document
        PdfMcr mcr;
        PdfObject pageObject = elem.getPdfObject().get(PdfName.Pg);
        if (pageObject == null) {
            pageObject = currentPage.getPdfObject();
            elem.getPdfObject().put(PdfName.Pg, pageObject);
        }
        if (currentPage.getPdfObject().equals(pageObject)) {
            mcr = new PdfMcrNumber(currentPage, elem);
        } else {
            mcr = new PdfMcrDictionary(currentPage, elem);
        }
        elem.addKid(index, mcr);
        return mcr.getMcid();
    }

    private void ensureDocumentTagIsOpen() {
        List<IPdfStructElem> rootKids = document.getStructTreeRoot().getKids();
        if (rootKids.isEmpty()) {
            documentElem = document.getStructTreeRoot().addKid(new PdfStructElem(document, com.itextpdf.core.pdf.PdfName.Document));
        } else if (rootKids.size() > 1) {
            //TODO not true
//            throw new PdfException("according to spec, only one element shall be structTreeRoot child");
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
        return currentStructElem.addKid(index, kid);
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
            throw new PdfException(""); //TODO exception
        }
    }
}
