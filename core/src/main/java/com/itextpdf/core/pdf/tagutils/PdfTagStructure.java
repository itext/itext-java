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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    protected HashMap<IAccessibleElement, PdfStructElem> incompleteElements;

    public PdfTagStructure(PdfDocument document) {
        this.document = document;
        if (!document.isTagged()) {
            throw new PdfException(""); //TODO exception
        }
        incompleteElements = new HashMap<>();

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
        if (!incompleteElements.containsKey(element)) {
            currentStructElem = addNewKid(index, element, keepConnectedToTag);
        } else {
            IPdfStructElem parent = incompleteElements.get(element).getParent();
            //TODO thinking in this place that only one element could be child of root. check.
            if (currentStructElem.getPdfObject() == ((PdfStructElem)parent).getPdfObject()) {
                currentStructElem = incompleteElements.get(element);
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

    public PdfTagStructure moveToParent() {
        if (currentStructElem.getParent() == documentElem) {
            //TODO don't do this
        }
        currentStructElem = (PdfStructElem) currentStructElem.getParent();
        return this;
    }

    public PdfTagStructure moveToKid(int kidIndex) {
        IPdfStructElem kid = currentStructElem.getKids().get(kidIndex);
        if (kid instanceof PdfStructElem) {
            currentStructElem = (PdfStructElem) kid;
        } else {
            throw new PdfException(""); //TODO exception: could not move to marked content references
        }
        return this;
    }

    public PdfTagStructure moveToKid(PdfName role) {
        moveToKid(0, role);
        return this;
    }

    public PdfTagStructure moveToKid(int /*TODO*/roleIndex, PdfName role) {
        List<IPdfStructElem> kids = currentStructElem.getKids();

        int k = 0;
        for (int i = 0; i < kids.size(); ++i) {
            //TODO what if kid - is mcr, but there is some other kid that is structElement. is it possible?
            if (kids.get(i).getRole().equals(role) && k++ == roleIndex) {
                moveToKid(i);
                return this;
            }
        }

        throw new PdfException(""); //TODO exception: no kid with such role
    }

    public List<PdfName> getListOfKidsRoles() {
        List<PdfName> roles = new ArrayList<>();
        List<IPdfStructElem> kids = currentStructElem.getKids();
        for (IPdfStructElem kid : kids) {
            if (kid instanceof PdfStructElem) {
                roles.add(kid.getRole());
            } else {
                roles.add(PdfName.MCR);
            }
        }
        return roles;
    }

    public boolean isConnectedToTag(IAccessibleElement element) {
        return incompleteElements.containsKey(element);
    }

    public PdfTagStructure moveToTag(IAccessibleElement element) {
        if (!incompleteElements.containsKey(element)) {
            throw new PdfException(""); //TODO exception: not connected
        }
        currentStructElem = incompleteElements.get(element);
        return this;
    }

    public PdfTagStructure removeConnectionToTag(IAccessibleElement element) {
        PdfStructElem structElem = incompleteElements.remove(element);
        if (structElem != null) {
            //TODO remove mark that struct element couldn't be flushed
        }
        return this;
    }

    public PdfTagStructure removeAllConnectionsToTags() {
        for (IAccessibleElement e: incompleteElements.keySet()) {
            removeConnectionToTag(e);
        }
        incompleteElements.clear();
        return this;
    }

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
            //TODO review. may be log something. check this in spec
            throw new PdfException("according to spec, only one element shall be structTreeRoot child");
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
            incompleteElements.put(element, kid);
            //TODO mark currentStructElem as not finished
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
