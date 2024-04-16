/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.kernel.pdf.tagging;

import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.logs.KernelLogMessageConstant;
import com.itextpdf.kernel.pdf.IsoKey;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfIndirectReference;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNull;
import com.itextpdf.kernel.pdf.PdfNumTree;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfStream;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Internal helper class which is used to effectively build parent tree and also find marked content references:
 * for specified page, by MCID or by struct parent index.
 */
class ParentTreeHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ParentTreeHandler.class);


    private PdfStructTreeRoot structTreeRoot;

    /**
     * Represents parentTree in structTreeRoot. It contains only those entries that belong to the already flushed pages.
     */
    private PdfNumTree parentTree;

    private Map<PdfIndirectReference, PageMcrsContainer> pageToPageMcrs;

    private Map<PdfIndirectReference, Integer> pageToStructParentsInd;

    private Map<PdfIndirectReference, Integer> xObjectToStructParentsInd;

    private int maxStructParentIndex = -1;

    /**
     * Init ParentTreeHandler. On init the parent tree is read and stored in this instance.
     */
    ParentTreeHandler(PdfStructTreeRoot structTreeRoot) {
        this.structTreeRoot = structTreeRoot;
        parentTree = new PdfNumTree(structTreeRoot.getDocument().getCatalog(), PdfName.ParentTree);
        xObjectToStructParentsInd = new HashMap<>();
        registerAllMcrs();
        pageToStructParentsInd = new HashMap<>();
    }

    /**
     * Gets a list of all marked content references on the page.
     */
    public PageMcrsContainer getPageMarkedContentReferences(PdfPage page) {
        return pageToPageMcrs.get(page.getPdfObject().getIndirectReference());
    }

    // Mind that this method searches among items contained in page's content stream  only
    public PdfMcr findMcrByMcid(PdfDictionary pageDict, int mcid) {
        PageMcrsContainer pageMcrs = pageToPageMcrs.get(pageDict.getIndirectReference());
        return pageMcrs != null ? pageMcrs.getPageContentStreamsMcrs().get(mcid) : null;
    }

    public PdfObjRef findObjRefByStructParentIndex(PdfDictionary pageDict, int structParentIndex) {
        PageMcrsContainer pageMcrs = pageToPageMcrs.get(pageDict.getIndirectReference());
        return pageMcrs != null ? (PdfObjRef) pageMcrs.getObjRefs().get(structParentIndex) : null;
    }

    public int getNextMcidForPage(PdfPage page) {
        PageMcrsContainer pageMcrs = getPageMarkedContentReferences(page);
        if (pageMcrs == null || pageMcrs.getPageContentStreamsMcrs().size() == 0) {
            return 0;
        } else {
            return (int) pageMcrs.getPageContentStreamsMcrs().lastEntry().getKey() + 1;
        }
    }

    /**
     * Creates and flushes parent tree entry for the page.
     * Effectively this means that new content mustn't be added to the page.
     *
     * @param page {@link PdfPage} for which to create parent tree entry. Typically this page is flushed after this
     *             call.
     */
    public void createParentTreeEntryForPage(PdfPage page) {
        PageMcrsContainer mcrs = getPageMarkedContentReferences(page);
        if (mcrs == null) {
            return;
        }
        pageToPageMcrs.remove(page.getPdfObject().getIndirectReference());

        if (updateStructParentTreeEntries(page, mcrs)) {
            structTreeRoot.setModified();
        }
    }

    public void savePageStructParentIndexIfNeeded(PdfPage page) {
        PdfIndirectReference indRef = page.getPdfObject().getIndirectReference();
        if (page.isFlushed() || pageToPageMcrs.get(indRef) == null) {
            return;
        }
        boolean hasNonObjRefMcr = pageToPageMcrs.get(indRef).getPageContentStreamsMcrs().size() > 0 ||
                pageToPageMcrs.get(indRef).getPageResourceXObjects().size() > 0;

        if (hasNonObjRefMcr) {
            pageToStructParentsInd.put(indRef, (Integer) getOrCreatePageStructParentIndex(page));
        }
    }

    public PdfDictionary buildParentTree() {
        return (PdfDictionary) parentTree.buildTree().makeIndirect(structTreeRoot.getDocument());
    }

    public void registerMcr(PdfMcr mcr) {
        registerMcr(mcr, false);
    }

    private void registerMcr(PdfMcr mcr, boolean registeringOnInit) {
        PdfIndirectReference mcrPageIndRef = mcr.getPageIndirectReference();
        if (mcrPageIndRef == null || (!(mcr instanceof PdfObjRef) && mcr.getMcid() < 0)) {
            LOGGER.error(IoLogMessageConstant.ENCOUNTERED_INVALID_MCR);
            return;
        }
        PageMcrsContainer pageMcrs = pageToPageMcrs.get(mcrPageIndRef);
        if (pageMcrs == null) {
            pageMcrs = new PageMcrsContainer();
            pageToPageMcrs.put(mcrPageIndRef, pageMcrs);
        }

        PdfObject stm;
        if ((stm = getStm(mcr)) != null) {
            PdfIndirectReference stmIndRef;
            PdfStream xObjectStream;
            if (stm instanceof PdfIndirectReference) {
                stmIndRef = (PdfIndirectReference) stm;
                xObjectStream = (PdfStream) stmIndRef.getRefersTo();
            } else {
                if (stm.getIndirectReference() == null) {
                    stm.makeIndirect(structTreeRoot.getDocument());
                }
                stmIndRef = stm.getIndirectReference();
                xObjectStream = (PdfStream) stm;
            }

            Integer structParent = xObjectStream.getAsInt(PdfName.StructParents);
            if (structParent != null) {
                xObjectToStructParentsInd.put(stmIndRef, structParent);
                if (registeringOnInit) {
                    xObjectStream.release();
                }
            } else if (isModificationAllowed()) {
                maxStructParentIndex++;
                xObjectToStructParentsInd.put(stmIndRef, maxStructParentIndex);
                xObjectStream.put(PdfName.StructParents, new PdfNumber(maxStructParentIndex));
                structTreeRoot.getPdfObject().put(PdfName.ParentTreeNextKey, new PdfNumber(maxStructParentIndex + 1));
                LOGGER.warn(KernelLogMessageConstant.XOBJECT_STRUCT_PARENT_INDEX_MISSED_AND_RECREATED);
            } else {
                throw new PdfException(KernelExceptionMessageConstant.XOBJECT_STRUCT_PARENT_INDEX_MISSED);
            }
            pageMcrs.putXObjectMcr(stmIndRef, mcr);
        } else if (mcr instanceof PdfObjRef) {
            PdfDictionary obj = ((PdfDictionary) mcr.getPdfObject()).getAsDictionary(PdfName.Obj);
            if (obj == null || obj.isFlushed()) {
                throw new PdfException(
                        KernelExceptionMessageConstant.WHEN_ADDING_OBJECT_REFERENCE_TO_THE_TAG_TREE_IT_MUST_BE_CONNECTED_TO_NOT_FLUSHED_OBJECT);
            }
            PdfNumber n = obj.getAsNumber(PdfName.StructParent);
            if (n != null) {
                pageMcrs.putObjectReferenceMcr(n.intValue(), mcr);
            } else if (isModificationAllowed()) {
                maxStructParentIndex++;
                pageMcrs.putObjectReferenceMcr(maxStructParentIndex, mcr);
                obj.put(PdfName.StructParent, new PdfNumber(maxStructParentIndex));
                structTreeRoot.getPdfObject().put(PdfName.ParentTreeNextKey, new PdfNumber(maxStructParentIndex + 1));
                LOGGER.warn(KernelLogMessageConstant.STRUCT_PARENT_INDEX_MISSED_AND_RECREATED);
            } else {
                throw new PdfException(KernelExceptionMessageConstant.STRUCT_PARENT_INDEX_NOT_FOUND_IN_TAGGED_OBJECT);
            }
        } else {
            pageMcrs.putPageContentStreamMcr(mcr.getMcid(), mcr);
        }

        if (!registeringOnInit) {
            structTreeRoot.setModified();
        }
    }

    public void unregisterMcr(PdfMcr mcrToUnregister) {
        PdfDictionary pageDict = mcrToUnregister.getPageObject();
        if (pageDict == null) {
            // invalid mcr, ignore

            return;
        }
        if (pageDict.isFlushed()) {
            throw new PdfException(
                    KernelExceptionMessageConstant.CANNOT_REMOVE_MARKED_CONTENT_REFERENCE_BECAUSE_ITS_PAGE_WAS_ALREADY_FLUSHED);
        }
        PageMcrsContainer pageMcrs = pageToPageMcrs.get(pageDict.getIndirectReference());
        if (pageMcrs != null) {
            PdfObject stm;
            if ((stm = getStm(mcrToUnregister)) != null) {
                PdfIndirectReference xObjectReference =
                        stm instanceof PdfIndirectReference ? (PdfIndirectReference) stm : stm.getIndirectReference();
                pageMcrs.getPageResourceXObjects().get(xObjectReference).remove(mcrToUnregister.getMcid());
                if (pageMcrs.getPageResourceXObjects().get(xObjectReference).isEmpty()) {
                    pageMcrs.getPageResourceXObjects().remove(xObjectReference);
                    xObjectToStructParentsInd.remove(xObjectReference);
                }
                structTreeRoot.setModified();
            } else if (mcrToUnregister instanceof PdfObjRef) {
                PdfDictionary obj = ((PdfDictionary) mcrToUnregister.getPdfObject()).getAsDictionary(PdfName.Obj);
                if (obj != null && !obj.isFlushed()) {
                    PdfNumber n = obj.getAsNumber(PdfName.StructParent);
                    if (n != null) {
                        pageMcrs.getObjRefs().remove(n.intValue());
                        structTreeRoot.setModified();
                        return;
                    }
                }
                for (Map.Entry<Integer, PdfMcr> entry : pageMcrs.getObjRefs().entrySet()) {
                    if (entry.getValue().getPdfObject() == mcrToUnregister.getPdfObject()) {
                        pageMcrs.getObjRefs().remove(entry.getKey());
                        structTreeRoot.setModified();
                        break;
                    }
                }
            } else {
                pageMcrs.getPageContentStreamsMcrs().remove(mcrToUnregister.getMcid());
                structTreeRoot.setModified();
            }
        }
    }

    private boolean isModificationAllowed() {
        PdfReader reader = this.structTreeRoot.getDocument().getReader();
        if (reader != null){
            return PdfReader.StrictnessLevel.CONSERVATIVE.isStricter(reader.getStrictnessLevel());
        } else {
            return true;
        }
    }

    private void registerAllMcrs() {
        pageToPageMcrs = new HashMap<>();
        // we create new number tree and not using parentTree, because we want parentTree to be empty
        Map<Integer, PdfObject> parentTreeEntries = new PdfNumTree(structTreeRoot.getDocument().getCatalog(),
                PdfName.ParentTree).getNumbers();
        Set<PdfDictionary> mcrParents = new LinkedHashSet<>();
        for (Map.Entry<Integer, PdfObject> entry : parentTreeEntries.entrySet()) {
            if (entry.getKey() > maxStructParentIndex) {
                maxStructParentIndex = (int) entry.getKey();
            }

            PdfObject entryValue = entry.getValue();
            if (entryValue.isDictionary()) {
                mcrParents.add((PdfDictionary) entryValue);
            } else if (entryValue.isArray()) {
                PdfArray parentsArray = (PdfArray) entryValue;
                for (int i = 0; i < parentsArray.size(); ++i) {
                    PdfDictionary parent = parentsArray.getAsDictionary(i);
                    if (parent != null) {
                        mcrParents.add(parent);
                    }
                }
            }
        }
        structTreeRoot.getPdfObject().put(PdfName.ParentTreeNextKey, new PdfNumber(maxStructParentIndex + 1));

        for (PdfObject mcrParent : mcrParents) {
            PdfStructElem mcrParentStructElem = new PdfStructElem((PdfDictionary) mcrParent);
            for (IStructureNode kid : mcrParentStructElem.getKids()) {
                if (kid instanceof PdfMcr) {
                    registerMcr((PdfMcr) kid, true);
                }
            }
        }
    }

    private boolean updateStructParentTreeEntries(PdfPage page, PageMcrsContainer mcrs) {
        boolean res = false;

        for (Map.Entry<Integer, PdfMcr> entry : mcrs.getObjRefs().entrySet()) {
            PdfMcr mcr = entry.getValue();
            PdfDictionary parentObj = ((PdfStructElem) mcr.getParent()).getPdfObject();
            if (!parentObj.isIndirect()) {
                continue;
            }
            int structParent = entry.getKey();
            parentTree.addEntry(structParent, parentObj);
            res = true;
        }

        int pageStructParentIndex;
        for (Map.Entry<PdfIndirectReference, TreeMap<Integer, PdfMcr>> entry : mcrs.getPageResourceXObjects()
                .entrySet()) {
            PdfIndirectReference xObjectRef = entry.getKey();
            if (xObjectToStructParentsInd.containsKey(xObjectRef)) {
                pageStructParentIndex = (int) xObjectToStructParentsInd.remove(xObjectRef);
                if (updateStructParentTreeForContentStreamEntries(entry.getValue(), pageStructParentIndex)) {
                    res = true;
                }
            }
        }
        if (page.isFlushed()) {
            PdfIndirectReference pageRef = page.getPdfObject().getIndirectReference();
            if (!pageToStructParentsInd.containsKey(pageRef)) {
                return res;
            }
            pageStructParentIndex = (int) pageToStructParentsInd.remove(pageRef);
        } else {
            pageStructParentIndex = getOrCreatePageStructParentIndex(page);
        }
        if (updateStructParentTreeForContentStreamEntries(mcrs.getPageContentStreamsMcrs(), pageStructParentIndex)) {
            res = true;
        }

        return res;
    }

    private boolean updateStructParentTreeForContentStreamEntries(Map<Integer, PdfMcr> mcrsOfContentStream,
            int pageStructParentIndex) {
        // element indices in parentsOfMcrs shall be the same as mcid of one of their kids.
        // See "Finding Structure Elements from Content Items" in pdf spec.
        PdfArray parentsOfMcrs = new PdfArray();
        int currentMcid = 0;
        for (Map.Entry<Integer, PdfMcr> entry : mcrsOfContentStream.entrySet()) {
            PdfMcr mcr = entry.getValue();
            PdfDictionary parentObj = ((PdfStructElem) mcr.getParent()).getPdfObject();
            if (!parentObj.isIndirect()) {
                continue;
            }
            // if for some reason some mcrs were not registered or don't exist, we ensure that the rest
            // of the parent objects were placed at correct index
            while (currentMcid++ < mcr.getMcid()) {
                parentsOfMcrs.add(PdfNull.PDF_NULL);
            }
            parentsOfMcrs.add(parentObj);
        }

        if (!parentsOfMcrs.isEmpty()) {
            parentsOfMcrs.makeIndirect(structTreeRoot.getDocument());
            parentTree.addEntry(pageStructParentIndex, parentsOfMcrs);
            structTreeRoot.getDocument().checkIsoConformance(parentsOfMcrs, IsoKey.TAG_STRUCTURE_ELEMENT);
            parentsOfMcrs.flush();
            return true;
        }
        return false;
    }

    private int getOrCreatePageStructParentIndex(PdfPage page) {
        int structParentIndex = page.getStructParentIndex();
        if (structParentIndex < 0) {
            structParentIndex = page.getDocument().getNextStructParentIndex();
            page.getPdfObject().put(PdfName.StructParents, new PdfNumber(structParentIndex));
        }
        return structParentIndex;
    }

    private static PdfObject getStm(PdfMcr mcr) {
        /*
         * Presence of Stm guarantees that the mcr belongs to XObject, absence of Stm guarantees that the mcr belongs to page content stream.
         * See 14.7.4.2 Marked-Content Sequences as Content Items, Table 324 – Entries in a marked-content reference dictionary.
         */
        if (mcr instanceof PdfMcrDictionary) {
            return ((PdfDictionary) mcr.getPdfObject()).get(PdfName.Stm, false);
        }
        return null;
    }

    static class PageMcrsContainer {


        Map<Integer, PdfMcr> objRefs;
        NavigableMap<Integer, PdfMcr> pageContentStreams;
        /*
         * Keys of this map are indirect references to XObjects contained in page's resources,
         * values are the mcrs contained in the corresponding XObject streams, stored as mappings "MCID-number to PdfMcr".
         */
        Map<PdfIndirectReference, TreeMap<Integer, PdfMcr>> pageResourceXObjects;

        PageMcrsContainer() {
            objRefs = new LinkedHashMap<Integer, PdfMcr>();
            pageContentStreams = new TreeMap<Integer, PdfMcr>();
            pageResourceXObjects = new LinkedHashMap<PdfIndirectReference, TreeMap<Integer, PdfMcr>>();
        }

        void putObjectReferenceMcr(int structParentIndex, PdfMcr mcr) {
            objRefs.put(structParentIndex, mcr);
        }

        void putPageContentStreamMcr(int mcid, PdfMcr mcr) {
            pageContentStreams.put(mcid, mcr);
        }

        void putXObjectMcr(PdfIndirectReference xObjectIndRef, PdfMcr mcr) {
            TreeMap<Integer, PdfMcr> xObjectMcrs = pageResourceXObjects.get(xObjectIndRef);
            if (xObjectMcrs == null) {
                xObjectMcrs = new TreeMap<Integer, PdfMcr>();
                pageResourceXObjects.put(xObjectIndRef, xObjectMcrs);
            }
            pageResourceXObjects.get(xObjectIndRef).put(mcr.getMcid(), mcr);
        }

        NavigableMap<Integer, PdfMcr> getPageContentStreamsMcrs() {
            return pageContentStreams;
        }

        Map<Integer, PdfMcr> getObjRefs() {
            return objRefs;
        }

        Map<PdfIndirectReference, TreeMap<Integer, PdfMcr>> getPageResourceXObjects() {
            return pageResourceXObjects;
        }

        Collection<PdfMcr> getAllMcrsAsCollection() {
            Collection<PdfMcr> collection = new ArrayList<PdfMcr>();
            collection.addAll(objRefs.values());
            collection.addAll(pageContentStreams.values());
            for (Map.Entry<PdfIndirectReference, TreeMap<Integer, PdfMcr>> entry : pageResourceXObjects.entrySet()) {
                collection.addAll(entry.getValue().values());
            }
            return collection;
        }
    }

}
