package com.itextpdf.core.pdf.tagutils;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfObject;
import com.itextpdf.core.pdf.PdfPage;
import com.itextpdf.core.pdf.PdfString;
import com.itextpdf.core.pdf.tagging.IPdfStructElem;
import com.itextpdf.core.pdf.tagging.IPdfTag;
import com.itextpdf.core.pdf.tagging.PdfMcr;
import com.itextpdf.core.pdf.tagging.PdfMcrDictionary;
import com.itextpdf.core.pdf.tagging.PdfMcrNumber;
import com.itextpdf.core.pdf.tagging.PdfStructElem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/* TODO
    1. add possibility to specify role classes through this class
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
            public AccessibleAttributes getAccessibleAttributes() {
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
        updateAttributes(currentStructElem, element.getAccessibleAttributes());

        return this;
    }

    public PdfTagStructure setAttributes(AccessibleAttributes attributes) {
        updateAttributes(currentStructElem, attributes);
        return this;
    }

    public IPdfTag getTagReference() {
        return getTagReference(-1);
    }

    public IPdfTag getTagReference(int index) {
        if (currentPage == null) {
            throw new PdfException(""); //TODO exception
        }

        int type = PdfStructElem.identifyType(document, currentStructElem.getRole());
        if (type != PdfStructElem.InlineLevel && type != PdfStructElem.Illustration) {
            throw new PdfException(""); //TODO exception
        }

        //TODO check this logic on created document
        PdfMcr tag;
        PdfObject pageObject = currentStructElem.getPdfObject().get(PdfName.Pg);
        if (pageObject == null) {
            pageObject = currentPage.getPdfObject();
            currentStructElem.getPdfObject().put(PdfName.Pg, pageObject);
        }
        if (currentPage.getPdfObject().equals(pageObject)) {
            tag = new PdfMcrNumber(currentPage, currentStructElem);
        } else {
            tag = new PdfMcrDictionary(currentPage, currentStructElem);
        }
        currentStructElem.addKid(index, tag);
        return tag;
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

    private void updateAttributes(PdfStructElem kid, AccessibleAttributes attributes) {
        if (attributes == null) {
            return;
        }

        if (attributes.getActualText() != null) {
            kid.setActualText(new PdfString(attributes.getActualText()));
        }
        if (attributes.getAlternateDescription() != null) {
            kid.setAlt(new PdfString(attributes.getAlternateDescription()));
        }
        if (attributes.getExpansion() != null) {
            kid.setE(new PdfString(attributes.getExpansion()));
        }
        if (attributes.getLanguage() != null) {
            kid.setLang(new PdfString(attributes.getLanguage()));
        }
    }

    private void throwExceptionIfRoleIsInvalid(PdfName role) {
        if (PdfStructElem.identifyType(document, role) == PdfStructElem.Unknown) {
            throw new PdfException(""); //TODO exception
        }
    }
}
