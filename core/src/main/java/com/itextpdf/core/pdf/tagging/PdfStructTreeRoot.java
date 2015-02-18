package com.itextpdf.core.pdf.tagging;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.pdf.*;
import org.slf4j.LoggerFactory;

import java.util.*;

public class PdfStructTreeRoot extends PdfObjectWrapper<PdfDictionary> implements IPdfStructElem {

    static private List<PdfName> ignoreKeysForCopy = new ArrayList<PdfName>() {{
        add(PdfName.K);
        add(PdfName.P);
        add(PdfName.Pg);
    }};

    public PdfStructTreeRoot(PdfDocument document) throws PdfException {
        this(new PdfDictionary(), document);
        getPdfObject().put(PdfName.Type, PdfName.StructTreeRoot);
    }

    public PdfStructTreeRoot(PdfDictionary pdfObject, PdfDocument document) throws PdfException {
        super(pdfObject, document);
    }

    public PdfStructElem addKid(PdfStructElem structElem) throws PdfException {
        return addKid(-1, structElem);
    }

    public PdfStructElem addKid(int index, PdfStructElem structElem) throws PdfException {
        addKidObject(index, structElem.getPdfObject());
        return structElem;
    }

    @Override
    public IPdfStructElem getParent() throws PdfException {
        return null;
    }

    @Override
    public List<IPdfStructElem> getKids() throws PdfException {
        PdfObject k = getPdfObject().get(PdfName.K);
        List<IPdfStructElem> kids = new ArrayList<IPdfStructElem>();
        switch (k.getType()) {
            case PdfObject.Dictionary:
                PdfDictionary d = (PdfDictionary) k;
                if (PdfStructElem.isStructElem(d))
                    kids.add(new PdfStructElem(d));
                break;
            case PdfObject.Array:
                PdfArray a = (PdfArray) k;
                for (int i = 0; i < a.size(); i++) {
                    PdfObject o = a.get(i);
                    switch (o.getType()) {
                        case PdfObject.Dictionary:
                            d = a.getAsDictionary(i);
                            if (d != null) {
                                if (PdfStructElem.isStructElem(d))
                                    kids.add(new PdfStructElem(d));
                            }
                            break;
                        default:
                            break;
                    }
                }
                break;
            default:
                break;
        }
        return kids;
    }

    public PdfArray getKidsObject() throws PdfException {
        PdfArray k = getPdfObject().getAsArray(PdfName.K);
        if (k == null) {
            k = new PdfArray();
            getPdfObject().put(PdfName.K, k);
        }
        return k;
    }

    public PdfDictionary getRoleMap() throws PdfException {
        PdfDictionary roleMap = getPdfObject().getAsDictionary(PdfName.RoleMap);
        if (roleMap == null) {
            roleMap = new PdfDictionary();
            getPdfObject().put(PdfName.RoleMap, roleMap);
        }
        return roleMap;
    }

    public PdfDictionary getParentTreeObject() throws PdfException {
        PdfDictionary parentTree = getPdfObject().getAsDictionary(PdfName.ParentTree);
        if (parentTree == null) {
            parentTree = new PdfDictionary();
            if (getDocument().getWriter() != null)
                parentTree.makeIndirect(getDocument());
            getPdfObject().put(PdfName.ParentTree, parentTree);
        }
        return parentTree;
    }

    public int getStructParentIndex() throws PdfException {
        PdfArray nums = null;
        PdfArray kids = getParentTreeObject().getAsArray(PdfName.Kids);
        if (kids != null) {
            nums = new PdfArray();
            for (int i = 0; i < kids.size(); i++) {
                PdfObject o = kids.get(i);
                if (o instanceof PdfDictionary) {
                    PdfArray numsLocal = ((PdfDictionary) o).getAsArray(PdfName.Nums);
                    if (numsLocal != null) {
                        nums.addAll(numsLocal);
                    }
                } else {
                    LoggerFactory.getLogger(this.getClass()).warn("Suspicious nums element in StructParentTree", o);
                }
            }
        }

        int maxStructParentIndex = 0;
        if (nums == null)
            nums = getParentTreeObject().getAsArray(PdfName.Nums);
        for (int i = 0; i < nums.size(); i++) {
            PdfNumber n = nums.getAsNumber(i);
            if (n != null && n.getIntValue() > maxStructParentIndex)
                maxStructParentIndex = n.getIntValue();
        }
        return maxStructParentIndex;

    }

    @Override
    public PdfName getRole() throws PdfException {
        return null;
    }

    @Override
    public void flush() throws PdfException {
        PdfArray nums = new PdfArray();
        for (int i = 0; i < getDocument().getNumOfPages(); i++) {
            PdfPage page = getDocument().getPage(i + 1);
            List<IPdfTag> tags = page.getPageTags();
            PdfArray numsBranch = new PdfArray();
            if (getDocument().getWriter() != null)
                numsBranch.makeIndirect(getDocument());
            for (IPdfTag tag : tags) {
                numsBranch.add(((PdfStructElem) tag.getParent()).getPdfObject());
            }
            nums.add(new PdfNumber(i));
            nums.add(numsBranch);
        }
        getParentTreeObject().remove(PdfName.Kids);
        getParentTreeObject().put(PdfName.Nums, nums);
        super.flush();
    }

    /**
     * Copies structure to a {@code toDocument}.
     *
     * @param toDocument document to cpt structure to.
     * @param page2page  association between original page and copied page.
     * @throws PdfException
     */
    public void copyToDocument(PdfDocument toDocument, LinkedHashMap<PdfPage, PdfPage> page2page) throws PdfException {
        if (!toDocument.isTagged())
            return;
        Set<PdfDictionary> tops = new LinkedHashSet<PdfDictionary>();
        Set<PdfDictionary> objectsToCopy = new LinkedHashSet<PdfDictionary>();
        LinkedHashMap<PdfDictionary, PdfDictionary> page2pageDictionaries = new LinkedHashMap<PdfDictionary, PdfDictionary>();
        for (Map.Entry<PdfPage, PdfPage> page : page2page.entrySet()) {
            page2pageDictionaries.put(page.getKey().getPdfObject(), page.getValue().getPdfObject());
            List<IPdfTag> tags = page.getKey().getPageTags();
            for (IPdfTag tag : tags) {
                tops.add(getObjectsToCopy(tag, objectsToCopy));
            }
        }
        for (PdfDictionary top : tops) {
            PdfDictionary copied = copyObject(top, objectsToCopy, toDocument, page2pageDictionaries);
            toDocument.getStructTreeRoot().addKidObject(copied);
        }
    }

    /**
     * Copies structure to a {@code toDocument} and insert it in a specified position in the document..
     *
     * @param toDocument       document to cpt structure to.
     * @param insertBeforePage indicates where the structure to be inserted.
     * @param page2page        association between original page and copied page.
     * @throws PdfException
     */
    public void copyToDocument(PdfDocument toDocument, int insertBeforePage, LinkedHashMap<PdfPage, PdfPage> page2page) throws PdfException {
        if (!toDocument.isTagged())
            return;

        List<PdfObject> kids = new ArrayList<PdfObject>();
        for (int i = 0; i < toDocument.getStructTreeRoot().getKidsObject().size(); i++) {
            kids.add(toDocument.getStructTreeRoot().getKidsObject().get(i, false));
        }

        LinkedHashMap<PdfPage, PdfPage> page2pageSource = new LinkedHashMap<PdfPage, PdfPage>();
        for (int i = 1; i < insertBeforePage; i++) {
            PdfPage page = toDocument.getPage(i);
            page2pageSource.put(page, page);
        }
        copyToDocument(toDocument, page2pageSource);

        copyToDocument(toDocument, page2page);

        page2pageSource = new LinkedHashMap<PdfPage, PdfPage>();
        for (int i = insertBeforePage; i <= toDocument.getNumOfPages(); i++) {
            PdfPage page = toDocument.getPage(i);
            page2pageSource.put(page, page);
        }
        copyToDocument(toDocument, page2pageSource);
        for (PdfObject k : kids) {
            toDocument.getStructTreeRoot().getKidsObject().remove(k);
        }


    }

    private PdfDictionary getObjectsToCopy(IPdfTag tag, Set<PdfDictionary> objectsToCopy) throws PdfException {
        if (tag instanceof PdfMcrDictionary)
            objectsToCopy.add((PdfDictionary) ((PdfMcrDictionary) tag).getPdfObject());
        PdfDictionary elem = ((PdfStructElem) tag.getParent()).getPdfObject();
        objectsToCopy.add(elem);
        for (; ; ) {
            PdfDictionary p = elem.getAsDictionary(PdfName.P);
            if (p == null || PdfName.StructTreeRoot.equals(p.getAsName(PdfName.Type))) {
                break;
            } else {
                elem = p;
                objectsToCopy.add(elem);
            }
        }
        return elem;
    }

    private PdfDictionary copyObject(PdfDictionary source, Set<PdfDictionary> objectsToCopy, PdfDocument toDocument, Map<PdfDictionary, PdfDictionary> page2page) throws PdfException {
        PdfDictionary copied = source.copy(toDocument, ignoreKeysForCopy, true);
        PdfDictionary pg = source.getAsDictionary(PdfName.Pg);
        if (pg != null)
            copied.put(PdfName.Pg, page2page.get(pg));
        PdfObject k = source.get(PdfName.K);
        if (k != null) {
            switch (k.getType()) {
                case PdfObject.Number:
                    copied.put(PdfName.K, k);
                    break;
                case PdfObject.Dictionary:
                    PdfDictionary kDict = (PdfDictionary) k;
                    if (objectsToCopy.contains(kDict)) {
                        boolean hasParent = kDict.containsKey(PdfName.P);
                        PdfDictionary copiedK = copyObject(kDict, objectsToCopy, kDict.getIndirectReference() == null ? null : toDocument, page2page);
                        if (hasParent)
                            copiedK.put(PdfName.P, copied);
                        copied.put(PdfName.K, copiedK);
                    }
                    break;
                case PdfObject.Array:
                    PdfArray kArr = (PdfArray) k;
                    PdfArray newArr = new PdfArray();
                    for (int i = 0; i < kArr.size(); i++) {
                        PdfObject kArrElem = kArr.get(i);
                        switch (kArrElem.getType()) {
                            case PdfObject.Number:
                                newArr.add(kArrElem);
                                break;
                            case PdfObject.Dictionary:
                                PdfDictionary kArrElemDict = (PdfDictionary) kArrElem;
                                if (objectsToCopy.contains(kArrElemDict)) {
                                    boolean hasParent = kArrElemDict.containsKey(PdfName.P);
                                    PdfDictionary copiedK = copyObject(kArrElemDict, objectsToCopy, kArrElemDict.getIndirectReference() == null ? null : toDocument, page2page);
                                    if (hasParent)
                                        copiedK.put(PdfName.P, copied);
                                    newArr.add(copiedK);
                                }
                                break;
                            default:
                                break;
                        }
                    }
                    copied.put(PdfName.K, newArr);
                    break;
                default:
                    break;
            }
        }
        return copied;
    }

    private void addKidObject(PdfDictionary structElem) throws PdfException {
        addKidObject(-1, structElem);
    }

    private void addKidObject(int index, PdfDictionary structElem) throws PdfException {
        if (index == -1)
            getKidsObject().add(structElem);
        else
            getKidsObject().add(index, structElem);
        if (PdfStructElem.isStructElem(structElem))
            structElem.put(PdfName.P, getPdfObject());
    }


}
