package com.itextpdf.kernel.pdf.tagging;

import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfIndirectReference;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNull;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;
import com.itextpdf.kernel.pdf.PdfPage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.slf4j.LoggerFactory;

public class PdfStructTreeRoot extends PdfObjectWrapper<PdfDictionary> implements IPdfStructElem {

    protected Map<PdfDictionary, Integer> objRefs = new HashMap<>();

    /**
     * Contains parent tree entries of the pages that were flushed.
     */
    private Map<Integer, PdfObject> parentTreeEntries = new TreeMap<>();

    /**
     * Contains marked content references lists of all pages.
     * <p>
     * When this field is initialized all new mcrs added to the tag structure are also added to this map.
     * The idea that this field is initialized only once, therefore the tree would be traversed only once.
     * </p>
     * <p>
     * On this field initializing the whole tag structure is traversed; this is needed for example for stamping mode.
     * This field is initialized:
     * <ul>
     *      <li> when some structure element is flushed;</li>
     *      <li> when {@code getPageMarkedContentReferences} method is called;</li>
     *      <li> when {@code createParentTreeEntryForPage} method is called to create parentTree entry.</li>
     * </ul>
     * </p>
     */
    private Map<PdfDictionary, List<PdfMcr>> pageToPageMcrs;

    /**
     * Indicates if any structure element was already flushed.
     * It is needed to prevent structure tree rebuilding (i.e. when new pages were copied and
     * inserted between other pages) in case something was already flushed.
     */
    private boolean flushOccurred = false;

    static private List<PdfName> ignoreKeysForCopy = new ArrayList<PdfName>() {{
        add(PdfName.K);
        add(PdfName.P);
        add(PdfName.Pg);
        add(PdfName.Obj);
    }};

    public PdfStructTreeRoot(PdfDocument document) {
        this(new PdfDictionary(), document);
        getPdfObject().put(PdfName.Type, PdfName.StructTreeRoot);
    }

    public PdfStructTreeRoot(PdfDictionary pdfObject, PdfDocument document) {
        super(pdfObject);
        makeIndirect(document);
    }

    public PdfStructElem addKid(PdfStructElem structElem) {
        return addKid(-1, structElem);
    }

    public PdfStructElem addKid(int index, PdfStructElem structElem) {
        addKidObject(index, structElem.getPdfObject());
        return structElem;
    }

    @Override
    public IPdfStructElem getParent() {
        return null;
    }

    /**
     * Gets list of the direct kids of StructTreeRoot.
     * If certain kid is flushed, there will be a {@code null} in the list on it's place.
     * @return list of the direct kids of StructTreeRoot.
     */
    @Override
    public List<IPdfStructElem> getKids() {
        PdfObject k = getPdfObject().get(PdfName.K);
        List<IPdfStructElem> kids = new ArrayList<IPdfStructElem>();

        if (k != null) {
            if (k.isArray()) {
                PdfArray a = (PdfArray) k;
                for (int i = 0; i < a.size(); i++) {
                    ifKidIsStructElementAddToList(a.get(i), kids);
                }
            } else {
                ifKidIsStructElementAddToList(k, kids);
            }
        }
        return kids;
    }

    public PdfArray getKidsObject() {
        PdfArray k = null;
        PdfObject kObj = getPdfObject().get(PdfName.K);
        if (kObj != null && kObj.isArray()) {
            k = (PdfArray) kObj;
        }
        if (k == null) {
            k = new PdfArray();
            getPdfObject().put(PdfName.K, k);
            if (kObj != null) {
                k.add(kObj);
            }
        }
        return k;
    }

    public PdfDictionary getRoleMap() {
        PdfDictionary roleMap = getPdfObject().getAsDictionary(PdfName.RoleMap);
        if (roleMap == null) {
            roleMap = new PdfDictionary();
            getPdfObject().put(PdfName.RoleMap, roleMap);
        }
        return roleMap;
    }

    public PdfDictionary getParentTreeObject() {
        PdfDictionary parentTree = getPdfObject().getAsDictionary(PdfName.ParentTree);
        if (parentTree == null) {
            parentTree = new PdfDictionary();
            if (getDocument().getWriter() != null)
                parentTree.makeIndirect(getDocument());
            getPdfObject().put(PdfName.ParentTree, parentTree);
        }
        return parentTree;
    }

    public int getStructParentIndex() {
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
        if (nums != null) {
            for (int i = 0; i < nums.size(); i++) {
                PdfNumber n = nums.getAsNumber(i);
                if (n != null && n.getIntValue() > maxStructParentIndex)
                    maxStructParentIndex = n.getIntValue();
            }
        }

        return maxStructParentIndex;

    }

    @Override
    public PdfName getRole() {
        return null;
    }

    /**
     * Gets a list of marked content references on page.
     */
    public List<PdfMcr> getPageMarkedContentReferences(PdfPage page) {
        registerAllMcrsIfNotRegistered();
        return pageToPageMcrs.get(page.getPdfObject());
    }

    @Override
    public void flush() {
        for (int i = 0; i < getDocument().getNumberOfPages(); ++i) {
            createParentTreeEntryForPage(getDocument().getPage(i + 1));
        }
        createParentTree();
        getDocument().getTagStructure().removeAllConnectionsToTags();
        flushAllKids(this);
        super.flush();
    }

    public boolean isStructTreeIsPartialFlushed() {
        return flushOccurred;
    }

    public void createParentTreeEntryForPage(PdfPage page) {
        registerAllMcrsIfNotRegistered();

        List<PdfMcr> mcrs = pageToPageMcrs.get(page.getPdfObject());
        if (mcrs == null) {
            return;
        }
        pageToPageMcrs.remove(page.getPdfObject());
        updateStructParentTreeEntries(page.getStructParentIndex(), mcrs);
    }

    public void flushStructElement(PdfStructElem structElem) {
        registerAllMcrsIfNotRegistered();
        structElem.getPdfObject().flush();
        flushOccurred = true;
    }

    /**
     * Copies structure to a {@code toDocument}.
     *
     * @param toDocument document to cpt structure to.
     * @param page2page  association between original page and copied page.
     * @throws PdfException
     */
    public void copyToDocument(PdfDocument toDocument, Map<PdfPage, PdfPage> page2page) {
        copyToDocument(toDocument, page2page, false);
    }

    /**
     * Copies structure to a {@code toDocument}.
     *
     * @param toDocument document to cpt structure to.
     * @param page2page  association between original page and copied page.
     * @param copyToCurrent indicates if <code>page2page</code> keys and values represent pages from single document
     * @throws PdfException
     */
    public void copyToDocument(PdfDocument toDocument, Map<PdfPage, PdfPage> page2page, boolean copyToCurrent) {
        if (!toDocument.isTagged())
            return;
        PdfDocument fromDocument = copyToCurrent ? toDocument : getDocument();
        Set<PdfDictionary> tops = new LinkedHashSet<>();
        Set<PdfDictionary> objectsToCopy = new LinkedHashSet<>();
        Map<PdfDictionary, PdfDictionary> page2pageDictionaries = new LinkedHashMap<>();
        for (Map.Entry<PdfPage, PdfPage> page : page2page.entrySet()) {
            page2pageDictionaries.put(page.getKey().getPdfObject(), page.getValue().getPdfObject());
            List<PdfMcr> mcrs = fromDocument.getStructTreeRoot().getPageMarkedContentReferences(page.getKey());
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
            PdfDictionary copied = copyObject(top, objectsToCopy, toDocument, page2pageDictionaries, copyToCurrent);
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
    public void copyToDocument(PdfDocument toDocument, int insertBeforePage, Map<PdfPage, PdfPage> page2page) {
        if (!toDocument.isTagged())
            return;

        List<PdfObject> kids = new ArrayList<>();
        PdfArray kidsObject = toDocument.getStructTreeRoot().getKidsObject();
        for (int i = 0; i < kidsObject.size(); i++) {
            kids.add(kidsObject.get(i, false));
        }

        Map<PdfPage, PdfPage> page2pageSource = new LinkedHashMap<>();
        for (int i = 1; i < insertBeforePage; i++) {
            PdfPage page = toDocument.getPage(i);
            page2pageSource.put(page, page);
        }
        copyToDocument(toDocument, page2pageSource, true);

        copyToDocument(toDocument, page2page);

        page2pageSource = new LinkedHashMap<>();
        for (int i = insertBeforePage; i <= toDocument.getNumberOfPages(); i++) {
            PdfPage page = toDocument.getPage(i);
            page2pageSource.put(page, page);
        }
        copyToDocument(toDocument, page2pageSource, true);
        for (PdfObject k : kids) {
            toDocument.getStructTreeRoot().getKidsObject().remove(k);
        }

        for (PdfObject kid : kids) {
            if (kid.isIndirectReference()) {
                kid = ((PdfIndirectReference) kid).getRefersTo();
            }
            freeAllReferences(new PdfStructElem((PdfDictionary) kid));
        }

        toDocument.getStructTreeRoot().unregisterAllMcrs();
    }

    /**
     * TODO this method is tremendously slow. Consider some improvements.
     *
     * For the given annotation removes it's object reference dictionary from the document logical structure.
     * Be careful with this method, cause it might be slow. If document is opened in stamping mode and
     * annotation already existed in it, obj ref will be found quickly. Otherwise the whole structure tree will be
     * traversed.
     * If annotation is not added to the document or is not tagged, nothing will happen.
     * @param annotDic dictionary object which represents the annotation to remove from logical structure
     * @return structure element which was the parent of the removed object reference dictionary
     */
    public PdfStructElem removeAnnotationObjectReference(PdfDictionary annotDic) {
        if (isStructTreeIsPartialFlushed()) {
            throw new PdfException(PdfException.CannotRemoveTagStructureElementsIfTagStructureWasPartiallyFlushed);
        }

        PdfNumber structParentIndex = (PdfNumber) annotDic.remove(PdfName.StructParent);
        if (structParentIndex == null) {
            return null;
        }

        PdfStructElem parentElem = null;
        PdfObjRef objRef = null;

        PdfDictionary parentTree = getParentTreeObject();
        PdfObject elem = findAnnotParentElemInParentTree(parentTree, structParentIndex.getIntValue());
        if (elem != null) {
            if (elem.isArray()) {
                throw new PdfException(PdfException.AnnotationHasInvalidStructParentValue);
            }
            parentElem = new PdfStructElem((PdfDictionary) elem);
            for (IPdfStructElem kid : parentElem.getKids()) {
                if (kid instanceof PdfObjRef) {
                    PdfDictionary kidObject = (PdfDictionary) ((PdfObjRef) kid).getPdfObject();
                    if (kidObject.get(PdfName.Obj) == annotDic) {
                        objRef = new PdfObjRef(kidObject, parentElem);
                    }
                }
            }
        }


        // parentTree doesn't contain given structParent index (which means that probably it's
        // a new annotation in the document) or logical structure somehow changed and parentElem doesn't contain
        // objRef to the given annotation anymore. In this case we will traverse the logical structure tree
        // to try to find the obj reference.
        if (parentElem == null || objRef == null) {
            objRef = findAnnotObjRefInStructTree(this, annotDic);
            if (objRef != null) {
                parentElem = (PdfStructElem) objRef.getParent();
            }
        }

        if (parentElem != null && objRef != null) {
            PdfObject k = parentElem.getK();
            if (k.isArray()) {
                ((PdfArray) k).remove(objRef.getPdfObject());
            } else {
                parentElem.getPdfObject().remove(PdfName.K);
            }

            if (pageToPageMcrs != null) {
                unregisterMcr(objRef);
            }

            // We don't remove the parent tree entry with given struct parent index here,
            // because parent tree is fully rebuilt at document closing.

            return parentElem;
        }

        return null;
    }

    public void registerMcr(PdfMcr mcr) {
        if (pageToPageMcrs == null) {
            return;
        }

        List<PdfMcr> pageMcrs = pageToPageMcrs.get(mcr.getPageObject());
        if (pageMcrs == null) {
            pageMcrs = new ArrayList<>();
            pageToPageMcrs.put(mcr.getPageObject(), pageMcrs);
        }
        pageMcrs.add(mcr);
        if (mcr instanceof PdfObjRef) {
            registerObjRef((PdfObjRef) mcr);
        }
    }

    public void unregisterMcr(PdfMcr mcrToUnregister) {
        if (pageToPageMcrs == null) {
            return;
        }

        List<PdfMcr> pageMcrs = pageToPageMcrs.get(mcrToUnregister.getPageObject());
        if (pageMcrs != null) {
            PdfMcr mcrObjectToRemove = null;
            for (PdfMcr mcr : pageMcrs) {
                if (mcr.getPdfObject() == mcrToUnregister.getPdfObject()) {
                    mcrObjectToRemove = mcr;
                    break;
                }
            }
            pageMcrs.remove(mcrObjectToRemove);

            if (mcrToUnregister instanceof PdfObjRef) {
                objRefs.remove(mcrToUnregister.getPdfObject());
            }
        }
    }

    private void freeAllReferences(PdfStructElem elem) {
        for (IPdfStructElem kid : elem.getKids()) {
            if (kid instanceof PdfStructElem) {
                freeAllReferences((PdfStructElem) kid);
            }
        }
        elem.getPdfObject().getIndirectReference().setFree();
    }

    /**
     * It should be called when tag structure of document was rebuilt.
     * This happens when new pages were inserted in between of old pages.
     */
    private void unregisterAllMcrs() {
        if (isStructTreeIsPartialFlushed()) {
            throw new PdfException(PdfException.CannotRebuildTagStructureWhenItWasPartlyFlushed);
        }
        pageToPageMcrs = null;
        parentTreeEntries = new TreeMap<>();
        getDocument().getTagStructure().removeAllConnectionsToTags();
    }

    private void registerAllMcrsIfNotRegistered() {
        if (pageToPageMcrs == null) {
            pageToPageMcrs = new HashMap<>();
            registerAllMcrs(this);

            Comparator<PdfMcr> mcrComparator = new Comparator<PdfMcr>() {
                @Override
                public int compare(PdfMcr o1, PdfMcr o2) {
                    Integer mcid1 = o1.getMcid();
                    Integer mcid2 = o2.getMcid();

                    if (mcid1 == null || mcid2 == null) {
                        return 0;
                    }

                    return mcid1 > mcid2
                            ? 1
                            : mcid1 < mcid2
                            ? -1
                            : 0;
                }
            };
            for (List<PdfMcr> pdfMcrs : pageToPageMcrs.values()) {
                Collections.sort(pdfMcrs, mcrComparator);
            }
        }
    }

    private void registerAllMcrs(IPdfStructElem element) {
        if (element == null)  return;
        if (element instanceof PdfMcr) {
            registerMcr((PdfMcr)element);
        } else {
            for (IPdfStructElem kid : element.getKids()) {
                registerAllMcrs(kid);
            }
        }
    }

    private void registerObjRef(PdfObjRef objRef) {
        if (objRef == null)
            return;
        PdfDictionary o = ((PdfDictionary) objRef.getPdfObject()).getAsDictionary(PdfName.Obj);
        if (o != null) {
            PdfNumber n = o.getAsNumber(PdfName.StructParent);
            if (n != null)
                objRefs.put((PdfDictionary) objRef.getPdfObject(), n.getIntValue());
        }
    }

    private void flushAllKids(IPdfStructElem elem) {
        for (IPdfStructElem kid : elem.getKids()) {
            if (kid instanceof PdfStructElem) {
                flushAllKids(kid);
                ((PdfStructElem) kid).flush();
            }
        }
    }

    private void createParentTree() {
        //nums is an array of pairs <PdfNumber, PdfObject> in which pairs shall be ordered by the PdfNumber values.
        // See "Number trees" in pdf spec
        PdfArray nums = new PdfArray();
        for (Map.Entry<Integer, PdfObject> entry : parentTreeEntries.entrySet()) {
            nums.add(new PdfNumber(entry.getKey()));
            nums.add(entry.getValue());
        }
        getParentTreeObject().remove(PdfName.Kids);
        getParentTreeObject().put(PdfName.Nums, nums);
    }

    /**
     * Number and dictionary references in list shall be order by mcid ascending.
     * Number and dictionary references in list shall belong to the same page.
     * @param pageStructParentIndex structParent index of the page to which mcrs belong.
     * @param mcrs list of the marked content references, that belong to the page with given structParetn index.
     */
    private void updateStructParentTreeEntries(Integer pageStructParentIndex, List<PdfMcr> mcrs) {
        // element indexes in parentsOfPageMcrs shall be the same as mcid of one of their kids.
        // See "Finding Structure Elements from Content Items" in pdf spec.
        PdfArray parentsOfPageMcrs = new PdfArray();
        int currentMcid = 0;
        for (PdfMcr mcr : mcrs) {
            if (mcr instanceof PdfObjRef) {
                Integer structParent = this.objRefs.get(mcr.getPdfObject());
                if (structParent != null) {
                    parentTreeEntries.put(structParent, ((PdfStructElem) mcr.getParent()).getPdfObject());
                }
            } else {
                // if for some reason some mcr where not registered or don't exist, we ensure that the rest
                // of the parent objects were placed at correct index
                while (currentMcid++ < mcr.getMcid()) {
                    parentsOfPageMcrs.add(PdfNull.PdfNull);
                }
                parentsOfPageMcrs.add(((PdfStructElem)mcr.getParent()).getPdfObject());
            }
        }


        if (!parentsOfPageMcrs.isEmpty()) {
            parentsOfPageMcrs.makeIndirect(getDocument());
            parentTreeEntries.put(pageStructParentIndex, parentsOfPageMcrs);
            parentsOfPageMcrs.flush();
        }
    }

    private PdfDictionary getObjectsToCopy(PdfMcr mcr, Set<PdfDictionary> objectsToCopy) {
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

    private PdfDictionary copyObject(PdfDictionary source, Set<PdfDictionary> objectsToCopy, PdfDocument toDocument, Map<PdfDictionary, PdfDictionary> page2page, boolean copyToCurrent) {
        PdfDictionary copied;
        if (copyToCurrent) {
            copied = source.clone(ignoreKeysForCopy);
            if (source.isIndirect()) {
                copied.makeIndirect(toDocument);
            }
        }
        else
            copied = source.copyToDocument(toDocument, ignoreKeysForCopy, true);

        if (source.containsKey(PdfName.Obj)) {
            PdfDictionary obj = source.getAsDictionary(PdfName.Obj);
            if (!copyToCurrent && obj != null) {
                // Link annotations could be not added to the toDocument, so we need to identify this case.
                // When obj.copyToDocument is called, and annotation was already copied, we would get this already created copy.
                // If it was already copied and added, /P key would be set. Otherwise /P won't be set.
                obj = obj.copyToDocument(toDocument, Arrays.asList(PdfName.P), false);
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
                    PdfObject copiedKid = copyObjectKid(kArr.get(i), copied, objectsToCopy, toDocument, page2page, copyToCurrent);
                    if (copiedKid != null) {
                        newArr.add(copiedKid);
                    }
                }
                copied.put(PdfName.K, newArr);
            } else {
                PdfObject copiedKid = copyObjectKid(k, copied, objectsToCopy, toDocument, page2page, copyToCurrent);
                if (copiedKid != null) {
                    copied.put(PdfName.K, copiedKid);
                }
            }
        }
        return copied;
    }

    private PdfObject copyObjectKid(PdfObject kid, PdfObject copiedParent, Set<PdfDictionary> objectsToCopy, PdfDocument toDocument, Map<PdfDictionary, PdfDictionary> page2page, boolean copyToCurrent) {
        if (kid.isNumber()) {
            return kid;
        } else if (kid.isDictionary()) {
            PdfDictionary kidAsDict = (PdfDictionary) kid;
            if (objectsToCopy.contains(kidAsDict)) {
                boolean hasParent = kidAsDict.containsKey(PdfName.P);
                PdfDictionary copiedKid = copyObject(kidAsDict, objectsToCopy, toDocument, page2page, copyToCurrent);
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

    private void addKidObject(PdfDictionary structElem) {
        addKidObject(-1, structElem);
    }

    private void addKidObject(int index, PdfDictionary structElem) {
        if (index == -1)
            getKidsObject().add(structElem);
        else
            getKidsObject().add(index, structElem);
        if (PdfStructElem.isStructElem(structElem))
            structElem.put(PdfName.P, getPdfObject());
    }

    private void ifKidIsStructElementAddToList(PdfObject kid, List<IPdfStructElem> kids) {
        if (kid.isFlushed()) {
            kids.add(null);
        } else if (kid.getType() == PdfObject.Dictionary && PdfStructElem.isStructElem((PdfDictionary) kid)) {
            kids.add(new PdfStructElem((PdfDictionary) kid));
        }
    }

    private PdfObject findAnnotParentElemInParentTree(PdfDictionary parentTreeNode, int structParentIndex) {
        if (parentTreeNode.containsKey(PdfName.Limits)) {
            PdfArray limits = parentTreeNode.getAsArray(PdfName.Limits);
            if (structParentIndex < limits.getAsNumber(0).getIntValue()
                    || structParentIndex > limits.getAsNumber(0).getIntValue()) {
                return null;
            }
        }
        if (parentTreeNode.containsKey(PdfName.Kids)) {
            PdfArray kids = parentTreeNode.getAsArray(PdfName.Kids);
            for (int i = 0; i < kids.size(); ++i) {
                PdfDictionary kid = kids.getAsDictionary(i);
                PdfObject parentElem = findAnnotParentElemInParentTree(kid, structParentIndex);
                if (parentElem != null) {
                    return parentElem;
                }
            }

            return null;
        }

        PdfArray nums = parentTreeNode.getAsArray(PdfName.Nums);
        if (nums != null) {
            return findElemInNumsArray(nums, 0, nums.size(), structParentIndex);
        }

        return null;
    }

    private PdfObject findElemInNumsArray(PdfArray numsArray, int startIndex, int endIndex, int structParentIndex) {
        int length = endIndex - startIndex; // a power of two
        if (length == 2) {
            if (numsArray.getAsNumber(startIndex).getIntValue() == structParentIndex) {
                return numsArray.get(startIndex + 1);
            } else {
                return null;
            }
        }

        int leastInSecondHalf = numsArray.getAsNumber(startIndex + (length / 2)).getIntValue();
        if (leastInSecondHalf > structParentIndex) {
            return findElemInNumsArray(numsArray, startIndex, startIndex + length / 2, structParentIndex);
        } else {
            return findElemInNumsArray(numsArray, startIndex + length / 2, endIndex, structParentIndex);
        }
    }

    private PdfObjRef findAnnotObjRefInStructTree(IPdfStructElem structElem, PdfDictionary annotDic) {
        if (structElem instanceof PdfObjRef) {
            if (((PdfDictionary) ((PdfObjRef) structElem).getPdfObject()).get(PdfName.Obj) == annotDic) {
                return (PdfObjRef) structElem;
            } else {
                return null;
            }
        }

        List<IPdfStructElem> kids = structElem.getKids();
        if (kids != null) {
            for (IPdfStructElem kid : kids) {
                PdfObjRef objRef = findAnnotObjRefInStructTree(kid, annotDic);
                if (objRef != null) {
                    return objRef;
                }
            }
        }

        return null;
    }
}
