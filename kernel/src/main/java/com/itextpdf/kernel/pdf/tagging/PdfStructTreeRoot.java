package com.itextpdf.kernel.pdf.tagging;

import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.pdf.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Marker;

/**
 * To be able to be wrapped with this {@link PdfObjectWrapper} the {@link PdfObject}
 * must be indirect.
 */
public class PdfStructTreeRoot extends PdfObjectWrapper<PdfDictionary> implements IPdfStructElem {

    private static final long serialVersionUID = 2168384302241193868L;

    private MarkedContentReferencesManager mcrManager;

    /**
     * Indicates if any structure element was already flushed.
     * It is needed to prevent structure tree rebuilding (i.e. when new pages were copied and
     * inserted between other pages) in case something was already flushed.
     */
    private boolean flushOccurred = false;

    public PdfStructTreeRoot(PdfDocument document) {
        this(new PdfDictionary().makeIndirect(document));
        getPdfObject().put(PdfName.Type, PdfName.StructTreeRoot);
    }

    /**
     * @param pdfObject must be an indirect object.
     */
    public PdfStructTreeRoot(PdfDictionary pdfObject) {
        super(pdfObject);
        ensureObjectIsAddedToDocument(pdfObject);
        setForbidRelease();
        // TODO possible to try to init with parent tree here
        mcrManager = new MarkedContentReferencesManager(this);
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
        List<IPdfStructElem> kids = new ArrayList<>();

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

    // TODO make internal
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

    // TODO make internal
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

    @Override
    public PdfName getRole() {
        return null;
    }

    public MarkedContentReferencesManager getMcrManager() {
        return mcrManager;
    }

    @Override
    public void flush() {
        for (int i = 0; i < getDocument().getNumberOfPages(); ++i) {
            mcrManager.createParentTreeEntryForPage(getDocument().getPage(i + 1));
        }
        put(PdfName.ParentTree, mcrManager.buildParentTree());
        flushAllKids(this);
        super.flush();
    }

    public boolean isStructTreeIsPartialFlushed() {
        return flushOccurred;
    }

    public void flushStructElement(PdfStructElem structElem) {
        // TODO i think i don't really need this, because i can create initial mcrsToPages using parent tree
        mcrManager.registerAllMcrsIfNotRegistered();
        structElem.getPdfObject().flush();
        flushOccurred = true;
    }

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
    public void copyTo(PdfDocument destDocument, Map<PdfPage, PdfPage> page2page) {
        StructureTreeCopier.copyTo(destDocument, page2page, getDocument());
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
    public void copyTo(PdfDocument destDocument, int insertBeforePage, Map<PdfPage, PdfPage> page2page) {
        StructureTreeCopier.copyTo(destDocument, insertBeforePage, page2page, getDocument());
    }

    /**
     * TODO this method is tremendously slow. Consider some improvements.
     *
     * TODO probably remove it at all from here. using getMcr by struct parent index do this logic in TagStructureContext
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
            // TODO improve removing: what if it was the last element in array, what if it is an indRef in array instead of object itself
            if (k.isArray()) {
                ((PdfArray) k).remove(objRef.getPdfObject());
            } else {
                parentElem.getPdfObject().remove(PdfName.K);
            }

            mcrManager.unregisterMcr(objRef);

            // We don't remove the parent tree entry with given struct parent index here,
            // because parent tree is fully rebuilt at document closing.

            return parentElem;
        }

        return null;
    }

    public PdfDocument getDocument() {
        return getPdfObject().getIndirectReference().getDocument();
    }

    void addKidObject(PdfDictionary structElem) {
        addKidObject(-1, structElem);
    }

    void addKidObject(int index, PdfDictionary structElem) {
        if (index == -1)
            getKidsObject().add(structElem);
        else
            getKidsObject().add(index, structElem);
        if (PdfStructElem.isStructElem(structElem))
            structElem.put(PdfName.P, getPdfObject());
    }

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return true;
    }

    // TODO revise and write here why do we do this here manually
    private void flushAllKids(IPdfStructElem elem) {
        for (IPdfStructElem kid : elem.getKids()) {
            if (kid instanceof PdfStructElem) {
                flushAllKids(kid);
                ((PdfStructElem) kid).flush();
            }
        }
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
