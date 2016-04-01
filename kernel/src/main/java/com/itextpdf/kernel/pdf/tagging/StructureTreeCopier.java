package com.itextpdf.kernel.pdf.tagging;

import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfIndirectReference;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfPage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

class StructureTreeCopier {

    static private List<PdfName> ignoreKeysForCopy = new ArrayList<PdfName>() {{
        add(PdfName.K);
        add(PdfName.P);
        add(PdfName.Pg);
        add(PdfName.Obj);
    }};

    /**
     * Copies structure to a {@code destDocument}.
     * <br/><br/>
     * NOTE: Works only for {@code PdfStructTreeRoot} that is read from the document opened in reading mode,
     * otherwise an exception is thrown.
     *
     * @param destDocument document to copy structure to. Shall not be current document.
     * @param page2page  association between original page and copied page.
     * @throws PdfException
     */
    public static void copyTo(PdfDocument destDocument, Map<PdfPage, PdfPage> page2page, PdfDocument callingDocument) {
        if (!destDocument.isTagged())
            return;

        copyTo(destDocument, page2page, callingDocument, false);
        destDocument.getStructTreeRoot().getMcrManager().unregisterAllMcrs();
    }

    /**
     * Copies structure to a {@code destDocument} and insert it in a specified position in the document.
     * <br/><br/>
     * NOTE: Works only for {@code PdfStructTreeRoot} that is read from the document opened in reading mode,
     * otherwise an exception is thrown.
     *
     * @param destDocument       document to copy structure to.
     * @param insertBeforePage indicates where the structure to be inserted.
     * @param page2page        association between original page and copied page.
     * @throws PdfException
     */
    public static void copyTo(PdfDocument destDocument, int insertBeforePage, Map<PdfPage, PdfPage> page2page, PdfDocument callingDocument) {
        if (!destDocument.isTagged())
            return;

        List<PdfObject> kids = new ArrayList<>();
        PdfArray kidsObject = destDocument.getStructTreeRoot().getKidsObject();
        for (int i = 0; i < kidsObject.size(); i++) {
            kids.add(kidsObject.get(i, false));
        }

        Map<PdfPage, PdfPage> page2pageSource = new LinkedHashMap<>();
        for (int i = 1; i < insertBeforePage; i++) {
            PdfPage page = destDocument.getPage(i);
            page2pageSource.put(page, page);
        }
        copyTo(destDocument, page2pageSource, callingDocument, true);

        copyTo(destDocument, page2page, callingDocument, false);

        page2pageSource = new LinkedHashMap<>();
        for (int i = insertBeforePage; i <= destDocument.getNumberOfPages(); i++) {
            PdfPage page = destDocument.getPage(i);
            page2pageSource.put(page, page);
        }
        copyTo(destDocument, page2pageSource, callingDocument, true);
        for (PdfObject k : kids) {
            destDocument.getStructTreeRoot().getKidsObject().remove(k);
        }

        for (PdfObject kid : kids) {
            if (kid.isIndirectReference()) {
                kid = ((PdfIndirectReference) kid).getRefersTo();
            }
            freeAllReferences(new PdfStructElem((PdfDictionary) kid));
        }

        destDocument.getStructTreeRoot().getMcrManager().unregisterAllMcrs();
    }

    /**
     * Copies structure to a {@code destDocument}.
     *
     * @param destDocument document to cpt structure to.
     * @param page2page  association between original page and copied page.
     * @param copyFromDestDocument indicates if <code>page2page</code> keys and values represent pages from {@code destDocument}.
     * @throws PdfException
     */
    private static void copyTo(PdfDocument destDocument, Map<PdfPage, PdfPage> page2page, PdfDocument callingDocument, boolean copyFromDestDocument) {
        PdfDocument fromDocument = copyFromDestDocument ? destDocument : callingDocument;
        Set<PdfDictionary> tops = new LinkedHashSet<>();
        Set<PdfDictionary> objectsToCopy = new LinkedHashSet<>();
        Map<PdfDictionary, PdfDictionary> page2pageDictionaries = new LinkedHashMap<>();
        for (Map.Entry<PdfPage, PdfPage> page : page2page.entrySet()) {
            page2pageDictionaries.put(page.getKey().getPdfObject(), page.getValue().getPdfObject());
            List<PdfMcr> mcrs = fromDocument.getStructTreeRoot().getMcrManager().getPageMarkedContentReferences(page.getKey());
            if (mcrs != null) {
                for (PdfMcr mcr : mcrs) {
                    tops.add(getObjectsToCopy(mcr, objectsToCopy));
                }
            }
        }

        List<PdfDictionary> topsInOriginalOrder = new ArrayList<>();
        for (IPdfStructElem kid : fromDocument.getStructTreeRoot().getKids()) {
            if (kid == null)  continue;

            PdfDictionary kidObject = ((PdfStructElem) kid).getPdfObject();
            if (tops.contains(kidObject)) {
                topsInOriginalOrder.add(kidObject);
            }
        }
        for (PdfDictionary top : topsInOriginalOrder) {
            PdfDictionary copied = copyObject(top, objectsToCopy, destDocument, page2pageDictionaries, copyFromDestDocument);
            destDocument.getStructTreeRoot().addKidObject(copied);
        }
    }

    private static PdfDictionary getObjectsToCopy(PdfMcr mcr, Set<PdfDictionary> objectsToCopy) {
        if (mcr instanceof PdfMcrDictionary || mcr instanceof PdfObjRef) {
            objectsToCopy.add((PdfDictionary) mcr.getPdfObject());
        }
        PdfDictionary elem = ((PdfStructElem) mcr.getParent()).getPdfObject();
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

    private static PdfDictionary copyObject(PdfDictionary source, Set<PdfDictionary> objectsToCopy, PdfDocument toDocument, Map<PdfDictionary, PdfDictionary> page2page, boolean copyFromDestDocument) {
        PdfDictionary copied;
        if (copyFromDestDocument) {
            copied = source.clone(ignoreKeysForCopy);
            if (source.isIndirect()) {
                copied.makeIndirect(toDocument);
            }
        }
        else
            copied = source.copyTo(toDocument, ignoreKeysForCopy, true);

        if (source.containsKey(PdfName.Obj)) {
            PdfDictionary obj = source.getAsDictionary(PdfName.Obj);
            if (!copyFromDestDocument && obj != null) {
                // Link annotations could be not added to the toDocument, so we need to identify this case.
                // When obj.copyTo is called, and annotation was already copied, we would get this already created copy.
                // If it was already copied and added, /P key would be set. Otherwise /P won't be set.
                obj = obj.copyTo(toDocument, Arrays.asList(PdfName.P), false);
            }
            copied.put(PdfName.Obj, obj);
        }

        PdfDictionary pg = source.getAsDictionary(PdfName.Pg);
        if (pg != null) {
            //TODO It is possible, that pg will not be present in the page2page map. Consider the situation,
            // that we want to copy structElem because it has marked content dictionary reference, which belongs to the page from page2page,
            // but the structElem itself has /Pg which value could be arbitrary page.
            copied.put(PdfName.Pg, page2page.get(pg));
        }
        PdfObject k = source.get(PdfName.K);
        if (k != null) {
            if (k.isArray()) {
                PdfArray kArr = (PdfArray) k;
                PdfArray newArr = new PdfArray();
                for (int i = 0; i < kArr.size(); i++) {
                    PdfObject copiedKid = copyObjectKid(kArr.get(i), copied, objectsToCopy, toDocument, page2page, copyFromDestDocument);
                    if (copiedKid != null) {
                        newArr.add(copiedKid);
                    }
                }
                copied.put(PdfName.K, newArr);
            } else {
                PdfObject copiedKid = copyObjectKid(k, copied, objectsToCopy, toDocument, page2page, copyFromDestDocument);
                if (copiedKid != null) {
                    copied.put(PdfName.K, copiedKid);
                }
            }
        }
        return copied;
    }

    private static PdfObject copyObjectKid(PdfObject kid, PdfObject copiedParent, Set<PdfDictionary> objectsToCopy, PdfDocument toDocument, Map<PdfDictionary, PdfDictionary> page2page, boolean copyFromDestDocument) {
        if (kid.isNumber()) {
            return kid; // TODO do we always copy numbers?
        } else if (kid.isDictionary()) {
            PdfDictionary kidAsDict = (PdfDictionary) kid;
            if (objectsToCopy.contains(kidAsDict)) {
                boolean hasParent = kidAsDict.containsKey(PdfName.P);
                PdfDictionary copiedKid = copyObject(kidAsDict, objectsToCopy, toDocument, page2page, copyFromDestDocument);
                if (hasParent)
                    copiedKid.put(PdfName.P, copiedParent);

                if (copiedKid.containsKey(PdfName.Obj)) {
                    PdfDictionary contentItemObject = copiedKid.getAsDictionary(PdfName.Obj);
                    if (!PdfName.Form.equals(contentItemObject.getAsName(PdfName.Subtype))
                            && !contentItemObject.containsKey(PdfName.P)) {
                        // Some link annotations could be not added to any page.
                        return null;
                    }
                    contentItemObject.put(PdfName.StructParent, new PdfNumber(toDocument.getNextStructParentIndex()));
                }
                return copiedKid;
            }
        }
        return null;
    }

    private static void freeAllReferences(PdfStructElem elem) {
        for (IPdfStructElem kid : elem.getKids()) {
            if (kid instanceof PdfStructElem) {
                freeAllReferences((PdfStructElem) kid);
            }
        }
        elem.getPdfObject().getIndirectReference().setFree();
    }
}
