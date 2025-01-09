/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfPage;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.itextpdf.kernel.pdf.tagutils.TagTreeIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Internal helper class which is used to copy, clone or move tag structure across documents.
 */
class StructureTreeCopier {

    private static List<PdfName> ignoreKeysForCopy = new ArrayList<PdfName>();

    private static List<PdfName> ignoreKeysForClone = new ArrayList<PdfName>();

    static {
        ignoreKeysForCopy.add(PdfName.K);
        ignoreKeysForCopy.add(PdfName.P);
        ignoreKeysForCopy.add(PdfName.Pg);
        ignoreKeysForCopy.add(PdfName.Obj);
        ignoreKeysForCopy.add(PdfName.NS);

        ignoreKeysForClone.add(PdfName.K);
        ignoreKeysForClone.add(PdfName.P);
    }

    /**
     * Copies structure to a {@code destDocument}.
     * <br/><br/>
     * NOTE: Works only for {@code PdfStructTreeRoot} that is read from the document opened in reading mode,
     * otherwise an exception is thrown.
     *
     * @param destDocument document to copy structure to. Shall not be current document.
     * @param page2page  association between original page and copied page.
     */
    public static void copyTo(PdfDocument destDocument, Map<PdfPage, PdfPage> page2page, PdfDocument callingDocument) {
        if (!destDocument.isTagged())
            return;

        copyTo(destDocument, page2page, callingDocument, false);
    }

    /**
     * Copies structure to a {@code destDocument} and insert it in a specified position in the document.
     * <br/><br/>
     * NOTE: Works only for {@code PdfStructTreeRoot} that is read from the document opened in reading mode,
     * otherwise an exception is thrown.
     * <br/>
     * Also, to insert a tagged page into existing tag structure, existing tag structure shouldn't be flushed, otherwise
     * an exception may be raised.
     *
     * @param destDocument       document to copy structure to.
     * @param insertBeforePage indicates where the structure to be inserted.
     * @param page2page        association between original page and copied page.
     */
    public static void copyTo(PdfDocument destDocument, int insertBeforePage, Map<PdfPage, PdfPage> page2page, PdfDocument callingDocument) {
        if (!destDocument.isTagged())
            return;

        copyTo(destDocument, insertBeforePage, page2page, callingDocument, false);
    }

    /**
     * Move tag structure of page to other place in the same document
     *
     * @param document document in which modifications will take place (should be opened in read-write mode)
     * @param from page, which tag structure will be moved
     * @param insertBefore indicates before what page number structure will be inserted to
     */
    public static void move(PdfDocument document, PdfPage from, int insertBefore) {
        if (!document.isTagged() || insertBefore < 1 || insertBefore > document.getNumberOfPages() + 1 )
            return;

        int fromNum = document.getPageNumber(from);
        if (fromNum == 0 || fromNum == insertBefore || fromNum + 1 == insertBefore)
            return;

        int destStruct;
        int currStruct = 0;
        if (fromNum > insertBefore) {
            destStruct = currStruct = separateStructure(document, 1, insertBefore, 0);
            currStruct = separateStructure(document, insertBefore, fromNum, currStruct);
            currStruct = separateStructure(document, fromNum, fromNum + 1, currStruct);
        } else {
            currStruct = separateStructure(document, 1, fromNum, 0);
            currStruct = separateStructure(document, fromNum, fromNum + 1, currStruct);
            destStruct = currStruct = separateStructure(document, fromNum + 1, insertBefore, currStruct);
        }

        Set<PdfDictionary> topsToMove = new HashSet<>();
        Collection<PdfMcr> mcrs = document.getStructTreeRoot().getPageMarkedContentReferences(from);
        if (mcrs != null) {
            for (PdfMcr mcr : mcrs) {
                PdfDictionary top = getTopmostParent(mcr);
                if (top != null) {
                    if (top.isFlushed()) {
                        throw new PdfException(KernelExceptionMessageConstant.CANNOT_MOVE_FLUSHED_TAG);
                    }
                    topsToMove.add(top);
                }
            }
        }

        List<PdfDictionary> orderedTopsToMove = new ArrayList<>();
        PdfArray tops = document.getStructTreeRoot().getKidsObject();
        for (int i = 0; i < tops.size(); ++i) {
            PdfDictionary top = tops.getAsDictionary(i);
            if (topsToMove.contains(top)) {
                orderedTopsToMove.add(top);
                tops.remove(i);
                if (i < destStruct) {
                    --destStruct;
                }
            }
        }
        for (PdfDictionary top : orderedTopsToMove) {
            document.getStructTreeRoot().addKidObject(destStruct++, top);
        }
    }

    /**
     * @return structure tree index of first separated (cloned) top
     */
    private static int separateStructure(PdfDocument document, int beforePage) {
        return separateStructure(document, 1, beforePage, 0);
    }

    private static int separateStructure(PdfDocument document, int startPage, int beforePage, int startPageStructTopIndex) {
        if (!document.isTagged() || 1 > startPage || startPage > beforePage || beforePage > document.getNumberOfPages() + 1) {
            return -1;
        } else if (beforePage == startPage) {
            return startPageStructTopIndex;
        } else if(beforePage == document.getNumberOfPages() + 1) {
            return document.getStructTreeRoot().getKidsObject().size();
        }
        // Here we separate the structure tree in two parts: struct elems that belong to the pages which indexes are
        // less then separateBeforePage and those struct elems that belong to other pages. Some elems might belong
        // to both parts and actually these are the ones that we are looking for.
        Set<PdfObject> firstPartElems = new HashSet<>();
        for (int i = startPage; i < beforePage; ++i) {
            PdfPage pageOfFirstHalf = document.getPage(i);
            Collection<PdfMcr> pageMcrs = document.getStructTreeRoot().getPageMarkedContentReferences(pageOfFirstHalf);
            if (pageMcrs != null) {
                for (PdfMcr mcr : pageMcrs) {
                    firstPartElems.add(mcr.getPdfObject());
                    PdfDictionary top = addAllParentsToSet(mcr, firstPartElems);
                    if (top != null && top.isFlushed()) {
                        throw new PdfException(
                                KernelExceptionMessageConstant.TAG_FROM_THE_EXISTING_TAG_STRUCTURE_IS_FLUSHED_CANNOT_ADD_COPIED_PAGE_TAGS);
                    }
                }
            }
        }

        List<PdfDictionary> clonedTops = new ArrayList<>();
        PdfArray tops = document.getStructTreeRoot().getKidsObject();

        // Now we "walk" through all the elems which belong to the first part, and look for the ones that contain both
        // kids from first and second part. We clone found elements and move kids from the second part to cloned elems.
        int lastTopBefore = startPageStructTopIndex - 1;
        for (int i = 0; i < tops.size(); ++i) {
            PdfDictionary top = tops.getAsDictionary(i);
            if (firstPartElems.contains(top)) {
                lastTopBefore = i;

                LastClonedAncestor lastCloned = new LastClonedAncestor();
                lastCloned.ancestor = top;
                PdfDictionary topClone = top.clone(ignoreKeysForClone);
                topClone.put(PdfName.P, document.getStructTreeRoot().getPdfObject());
                lastCloned.clone = topClone;

                separateKids(top, firstPartElems, lastCloned, document);

                if (topClone.containsKey(PdfName.K)) {
                    topClone.makeIndirect(document);
                    clonedTops.add(topClone);
                }
            }
        }

        for (int i = 0; i < clonedTops.size(); ++i) {
            document.getStructTreeRoot().addKidObject(lastTopBefore + 1 + i, clonedTops.get(i));
        }
        return lastTopBefore + 1;
    }

    private static void copyTo(PdfDocument destDocument, int insertBeforePage, Map<PdfPage, PdfPage> page2page, PdfDocument callingDocument, boolean copyFromDestDocument) {
        if (!destDocument.isTagged())
            return;

        int insertIndex = separateStructure(destDocument, insertBeforePage);
        //Opposite should never happened.
        if (insertIndex > 0) {
            copyTo(destDocument, page2page, callingDocument, copyFromDestDocument, insertIndex);
        }
    }

    /**
     * Copies structure to a {@code destDocument}.
     *
     * @param destDocument document to cpt structure to.
     * @param page2page  association between original page and copied page.
     * @param copyFromDestDocument indicates if <code>page2page</code> keys and values represent pages from {@code destDocument}.
     */
    private static void copyTo(PdfDocument destDocument, Map<PdfPage, PdfPage> page2page, PdfDocument callingDocument
            , boolean copyFromDestDocument) {
        copyTo(destDocument, page2page, callingDocument, copyFromDestDocument, -1);
    }

    private static void copyTo(PdfDocument destDocument, Map<PdfPage, PdfPage> page2page, PdfDocument callingDocument
            , boolean copyFromDestDocument, int insertIndex) {
        CopyStructureResult copiedStructure = copyStructure(destDocument, page2page, callingDocument, copyFromDestDocument);
        PdfStructTreeRoot destStructTreeRoot = destDocument.getStructTreeRoot();
        destStructTreeRoot.makeIndirect(destDocument);
        for (PdfDictionary copied : copiedStructure.getTopsList()) {
            destStructTreeRoot.addKidObject(insertIndex, copied);
            if (insertIndex > -1) {
                ++insertIndex;
            }
        }

        if (!copyFromDestDocument) {
            if (!copiedStructure.getCopiedNamespaces().isEmpty()) {
                destStructTreeRoot.getNamespacesObject().addAll(copiedStructure.getCopiedNamespaces());
            }

            PdfDictionary srcRoleMap = callingDocument.getStructTreeRoot().getRoleMap();
            PdfDictionary destRoleMap =  destStructTreeRoot.getRoleMap();
            for (Map.Entry<PdfName, PdfObject> mappingEntry: srcRoleMap.entrySet()) {
                if (!destRoleMap.containsKey(mappingEntry.getKey())) {
                    destRoleMap.put(mappingEntry.getKey(), mappingEntry.getValue());

                } else if (!mappingEntry.getValue().equals(destRoleMap.get(mappingEntry.getKey()))) {
                    String srcMapping = mappingEntry.getKey() + " -> " + mappingEntry.getValue();
                    String destMapping = mappingEntry.getKey() + " -> " + destRoleMap.get(mappingEntry.getKey());

                    Logger logger = LoggerFactory.getLogger(StructureTreeCopier.class);
                    logger.warn(MessageFormat.format(
                            IoLogMessageConstant.ROLE_MAPPING_FROM_SOURCE_IS_NOT_COPIED_ALREADY_EXIST, srcMapping,
                            destMapping));
                }
            }
        }
    }

    private static CopyStructureResult copyStructure(PdfDocument destDocument, Map<PdfPage, PdfPage> page2page
            , PdfDocument callingDocument, boolean copyFromDestDocument) {
        PdfDocument fromDocument = copyFromDestDocument ? destDocument : callingDocument;
        Map<PdfDictionary, PdfDictionary> topsToFirstDestPage = new HashMap<>();
        Set<PdfObject> objectsToCopy = new HashSet<>();
        Map<PdfDictionary, PdfDictionary> page2pageDictionaries = new HashMap<>();
        for (Map.Entry<PdfPage, PdfPage> page : page2page.entrySet()) {
            page2pageDictionaries.put(page.getKey().getPdfObject(), page.getValue().getPdfObject());
            Collection<PdfMcr> mcrs = fromDocument.getStructTreeRoot().getPageMarkedContentReferences(page.getKey());
            if (mcrs != null) {
                for (PdfMcr mcr : mcrs) {
                    if (mcr instanceof PdfMcrDictionary || mcr instanceof PdfObjRef) {
                        objectsToCopy.add(mcr.getPdfObject());
                    }
                    PdfDictionary top = addAllParentsToSet(mcr, objectsToCopy);
                    if (top != null) {
                        if (top.isFlushed()) {
                            throw new PdfException(KernelExceptionMessageConstant.CANNOT_COPY_FLUSHED_TAG);
                        }
                        if (!topsToFirstDestPage.containsKey(top)) {
                            topsToFirstDestPage.put(top, page.getValue().getPdfObject());
                        }
                    }
                }
            }
        }

        List<PdfDictionary> topsInOriginalOrder = new ArrayList<>();
        for (IStructureNode kid : fromDocument.getStructTreeRoot().getKids()) {
            if (kid == null)  continue;

            PdfDictionary kidObject = ((PdfStructElem) kid).getPdfObject();
            if (topsToFirstDestPage.containsKey(kidObject)) {
                topsInOriginalOrder.add(kidObject);
            }
        }
        StructElemCopyingParams structElemCopyingParams = new StructElemCopyingParams(objectsToCopy, destDocument, page2pageDictionaries, copyFromDestDocument);
        PdfStructTreeRoot destStructTreeRoot = destDocument.getStructTreeRoot();
        destStructTreeRoot.makeIndirect(destDocument);
        List<PdfDictionary> copiedTops = new ArrayList<>();
        for (PdfDictionary top : topsInOriginalOrder) {
            PdfDictionary copied = copyObject(top, topsToFirstDestPage.get(top), false, structElemCopyingParams);
            copiedTops.add(copied);
        }
        return new CopyStructureResult(copiedTops, structElemCopyingParams.getCopiedNamespaces());
    }

    private static PdfDictionary copyObject(PdfDictionary source, PdfDictionary destPage, boolean parentChangePg, StructElemCopyingParams copyingParams) {
        PdfDictionary copied;
        if (copyingParams.isCopyFromDestDocument()) {
            copied = source.clone(ignoreKeysForClone);
            if (source.isIndirect()) {
                copied.makeIndirect(copyingParams.getToDocument());
            }

            PdfDictionary pg = source.getAsDictionary(PdfName.Pg);
            if (pg != null) {
                if (copyingParams.isCopyFromDestDocument()) {
                    if (pg != destPage) {
                        copied.put(PdfName.Pg, destPage);
                        parentChangePg = true;
                    } else {
                        parentChangePg = false;
                    }
                }
            }
        } else {
            copied = source.copyTo(copyingParams.getToDocument(), ignoreKeysForCopy, true);

            PdfObject obj = source.get(PdfName.Obj);
            if (obj instanceof PdfDictionary) {
                PdfDictionary objDic = (PdfDictionary) obj;
                // Link annotations could be not added to the toDocument, so we need to identify this case.
                // When obj.copyTo is called, and annotation was already copied, we would get this already created copy.
                // If it was already copied and added, /P key would be set. Otherwise /P won't be set.
                objDic = objDic.copyTo(copyingParams.getToDocument(), Arrays.asList(PdfName.P), false);
                copied.put(PdfName.Obj, objDic);
            }

            PdfDictionary nsDict = source.getAsDictionary(PdfName.NS);
            if (nsDict != null) {
                PdfDictionary copiedNsDict = copyNamespaceDict(nsDict, copyingParams);
                copied.put(PdfName.NS, copiedNsDict);
            }

            PdfDictionary pg = source.getAsDictionary(PdfName.Pg);
            if (pg != null) {
                PdfDictionary pageAnalog = copyingParams.getPage2page().get(pg);
                if (pageAnalog == null) {
                    pageAnalog = destPage;
                    parentChangePg = true;
                } else {
                    parentChangePg = false;
                }
                copied.put(PdfName.Pg, pageAnalog);
            }
        }

        PdfObject k = source.get(PdfName.K);
        PdfDictionary lastCopiedTrPage = null;
        if (k != null) {
            if (k.isArray()) {
                PdfArray kArr = (PdfArray) k;
                PdfArray newArr = new PdfArray();
                for (int i = 0; i < kArr.size(); i++) {
                    PdfObject copiedKid = copyObjectKid(kArr.get(i), copied, destPage, parentChangePg, copyingParams
                            , lastCopiedTrPage);
                    if (copiedKid != null) {
                        newArr.add(copiedKid);
                        if (copiedKid instanceof PdfDictionary
                                && PdfName.TR.equals(((PdfDictionary) copiedKid).getAsName(PdfName.S))) {
                            lastCopiedTrPage = destPage;
                        }
                    }
                }

                if (!newArr.isEmpty()) {
                    if (newArr.size() == 1) {
                        copied.put(PdfName.K, newArr.get(0));
                    } else {
                        copied.put(PdfName.K, newArr);
                    }
                }
            } else {
                PdfObject copiedKid = copyObjectKid(k, copied, destPage, parentChangePg, copyingParams
                        , lastCopiedTrPage);
                if (copiedKid != null) {
                    copied.put(PdfName.K, copiedKid);
                }
            }
        }
        return copied;
    }

    private static PdfObject copyObjectKid(PdfObject kid, PdfDictionary copiedParent, PdfDictionary destPage,
                                           boolean parentChangePg, StructElemCopyingParams copyingParams,
                                           PdfDictionary lastCopiedTrPage) {
        if (kid.isNumber()) {
            if (!parentChangePg) {
                copyingParams.getToDocument().getStructTreeRoot().getParentTreeHandler()
                        .registerMcr(new PdfMcrNumber((PdfNumber) kid, new PdfStructElem(copiedParent)));
                return kid;
            }
        } else if (kid.isDictionary()) {
            PdfDictionary kidAsDict = (PdfDictionary) kid;
            //if element is TD and its parent is TR which was copied, then we copy it in any case
            if (copyingParams.getObjectsToCopy().contains(kidAsDict) ||
                    shouldTableElementBeCopied(kidAsDict, copiedParent)) {
                //if TR element is not connected to any page,
                //it should be copied to the same page as the last copied TR which connected to page
                PdfDictionary destination = destPage;
                if (PdfName.TR.equals(kidAsDict.getAsName(PdfName.S))
                        && !copyingParams.getObjectsToCopy().contains(kidAsDict)) {
                    if (McrCheckUtil.isTrContainsMcr(kidAsDict)){
                        return null;
                    }

                    if (lastCopiedTrPage == null) {
                        return null;
                    } else {
                        destination = lastCopiedTrPage;
                    }
                }
                boolean hasParent = kidAsDict.containsKey(PdfName.P);
                PdfDictionary copiedKid = copyObject(kidAsDict, destination, parentChangePg, copyingParams);

                if (hasParent) {
                    copiedKid.put(PdfName.P, copiedParent);
                } else {
                    PdfMcr mcr;
                    if (copiedKid.containsKey(PdfName.Obj)) {
                        mcr = new PdfObjRef(copiedKid, new PdfStructElem(copiedParent));
                        PdfDictionary contentItemObject = (PdfDictionary) copiedKid.get(PdfName.Obj);
                        if (PdfName.Link.equals(contentItemObject.getAsName(PdfName.Subtype))
                                && !contentItemObject.containsKey(PdfName.P)) {
                            // Some link annotations may be not copied, because their destination page is not copied.
                            return null;
                        }
                        contentItemObject.put(PdfName.StructParent, new PdfNumber((int) copyingParams.getToDocument().getNextStructParentIndex()));
                    } else {
                        mcr = new PdfMcrDictionary(copiedKid, new PdfStructElem(copiedParent));
                    }
                    copyingParams.getToDocument().getStructTreeRoot().getParentTreeHandler().registerMcr(mcr);
                }
                return copiedKid;
            }
        }
        return null;
    }

    static boolean shouldTableElementBeCopied(PdfDictionary obj, PdfDictionary parent) {
        PdfName role = obj.getAsName(PdfName.S);
        return ((PdfName.TD.equals(role) || PdfName.TH.equals(role)) && PdfName.TR.equals(parent.get(PdfName.S)))
                || PdfName.TR.equals(role);
    }

    private static PdfDictionary copyNamespaceDict(PdfDictionary srcNsDict, StructElemCopyingParams copyingParams) {
        List<PdfName> excludeKeys = Collections.<PdfName>singletonList(PdfName.RoleMapNS);
        PdfDocument toDocument = copyingParams.getToDocument();
        PdfDictionary copiedNsDict = srcNsDict.copyTo(toDocument, excludeKeys, false);
        copyingParams.addCopiedNamespace(copiedNsDict);

        PdfDictionary srcRoleMapNs = srcNsDict.getAsDictionary(PdfName.RoleMapNS);
        // if this src namespace was already copied (or in the process of copying) it will contain role map already
        PdfDictionary copiedRoleMap = copiedNsDict.getAsDictionary(PdfName.RoleMapNS);
        if (srcRoleMapNs != null && copiedRoleMap == null) {
            copiedRoleMap = new PdfDictionary();
            copiedNsDict.put(PdfName.RoleMapNS, copiedRoleMap);

            for (Map.Entry<PdfName, PdfObject> entry : srcRoleMapNs.entrySet()) {
                PdfObject copiedMapping;
                if (entry.getValue().isArray()) {
                    PdfArray srcMappingArray = (PdfArray) entry.getValue();
                    if (srcMappingArray.size() > 1 && srcMappingArray.get(1).isDictionary()) {
                        PdfArray copiedMappingArray = new PdfArray();
                        copiedMappingArray.add(srcMappingArray.get(0).copyTo(toDocument));
                        PdfDictionary copiedNamespace = copyNamespaceDict(srcMappingArray.getAsDictionary(1), copyingParams);
                        copiedMappingArray.add(copiedNamespace);
                        copiedMapping = copiedMappingArray;
                    } else {
                        Logger logger = LoggerFactory.getLogger(StructureTreeCopier.class);
                        logger.warn(MessageFormat.format(
                                IoLogMessageConstant.ROLE_MAPPING_FROM_SOURCE_IS_NOT_COPIED_INVALID,
                                entry.getKey().toString()));
                        continue;
                    }
                } else {
                    copiedMapping = entry.getValue().copyTo(toDocument);
                }
                PdfName copiedRoleFrom = (PdfName) entry.getKey().copyTo(toDocument);
                copiedRoleMap.put(copiedRoleFrom, copiedMapping);
            }
        }

        return copiedNsDict;
    }

    private static void separateKids(PdfDictionary structElem, Set<PdfObject> firstPartElems, LastClonedAncestor lastCloned, PdfDocument document) {
        PdfObject k = structElem.get(PdfName.K);

        // If /K entry is not a PdfArray - it would be a kid which we won't clone at the moment, because it won't contain
        // kids from both parts at the same time. It would either be cloned as an ancestor later, or not cloned at all.
        // If it's kid is struct elem - it would definitely be structElem from the first part, so we simply call separateKids for it.
        if (!k.isArray()) {
            if (k.isDictionary() && PdfStructElem.isStructElem((PdfDictionary) k)) {
                separateKids((PdfDictionary) k, firstPartElems, lastCloned, document);
            }
        } else {
            PdfArray kids = (PdfArray) k;

            for (int i = 0; i < kids.size(); ++i) {
                PdfObject kid = kids.get(i);
                PdfDictionary dictKid = null;
                if (kid.isDictionary()) {
                    dictKid = (PdfDictionary) kid;
                }

                if (dictKid != null && PdfStructElem.isStructElem(dictKid)) {
                    if (firstPartElems.contains(kid)) {
                        separateKids((PdfDictionary) kid, firstPartElems, lastCloned, document);
                    } else {
                        if (dictKid.isFlushed()) {
                            throw new PdfException(
                                    KernelExceptionMessageConstant.TAG_FROM_THE_EXISTING_TAG_STRUCTURE_IS_FLUSHED_CANNOT_ADD_COPIED_PAGE_TAGS);
                        }

                        // elems with no kids will not be marked as from the first part,
                        // but nonetheless we don't want to move all of them to the second part; we just leave them as is
                        if (dictKid.containsKey(PdfName.K)) {
                            cloneParents(structElem, lastCloned, document);

                            kids.remove(i--);
                            PdfStructElem.addKidObject(lastCloned.clone, -1, kid);
                        }
                    }
                } else {
                    if (!firstPartElems.contains(kid)) {
                        cloneParents(structElem, lastCloned, document);

                        PdfMcr mcr;
                        if (dictKid != null) {
                            if (dictKid.get(PdfName.Type).equals(PdfName.MCR)) {
                                mcr = new PdfMcrDictionary(dictKid, new PdfStructElem(lastCloned.clone));
                            } else {
                                mcr = new PdfObjRef(dictKid, new PdfStructElem(lastCloned.clone));
                            }
                        } else {
                            mcr = new PdfMcrNumber((PdfNumber) kid, new PdfStructElem(lastCloned.clone));
                        }

                        kids.remove(i--);
                        PdfStructElem.addKidObject(lastCloned.clone, -1, kid);
                        // re-register mcr
                        document.getStructTreeRoot().getParentTreeHandler().registerMcr(mcr);
                    }
                }
            }
        }

        if (lastCloned.ancestor == structElem) {
            lastCloned.ancestor = lastCloned.ancestor.getAsDictionary(PdfName.P);
            lastCloned.clone = lastCloned.clone.getAsDictionary(PdfName.P);
        }
    }

    private static void cloneParents(PdfDictionary structElem, LastClonedAncestor lastCloned, PdfDocument document) {
        if (lastCloned.ancestor != structElem) {
            PdfDictionary structElemClone = (PdfDictionary) structElem.clone(ignoreKeysForClone).makeIndirect(document);
            PdfDictionary currClone = structElemClone;
            PdfDictionary currElem = structElem;
            while (currElem.get(PdfName.P) != lastCloned.ancestor) {
                PdfDictionary parent = currElem.getAsDictionary(PdfName.P);
                PdfDictionary parentClone = (PdfDictionary) parent.clone(ignoreKeysForClone).makeIndirect(document);
                currClone.put(PdfName.P, parentClone);
                parentClone.put(PdfName.K, currClone);
                currClone = parentClone;
                currElem = parent;
            }
            PdfStructElem.addKidObject(lastCloned.clone, -1, currClone);
            lastCloned.clone = structElemClone;
            lastCloned.ancestor = structElem;
        }
    }

    /**
     * @return the topmost parent added to set. If encountered flushed element - stops and returns this flushed element.
     */
    private static PdfDictionary addAllParentsToSet(PdfMcr mcr, Set<PdfObject> set) {
        List<PdfDictionary> allParents = retrieveParents(mcr, true);
        set.addAll(allParents);
        return allParents.isEmpty() ? null : allParents.get(allParents.size() - 1);
    }

    /**
     * Gets the topmost non-root structure element parent. May be flushed.
     *
     * @param mcr starting element
     * @return topmost non-root structure element parent, or {@code null} if it doesn't have any
     */
    private static PdfDictionary getTopmostParent(PdfMcr mcr) {
        return retrieveParents(mcr, false).get(0);
    }

    private static List<PdfDictionary> retrieveParents(PdfMcr mcr, boolean all) {
        final Set<PdfDictionary> parents = new LinkedHashSet<>();
        final IStructureNode firstParent = mcr.getParent();
        PdfDictionary previous = null;
        PdfDictionary current = firstParent instanceof PdfStructElem ? ((PdfStructElem) firstParent).getPdfObject() : null;

        while (current != null
               && !PdfName.StructTreeRoot.equals(current.getAsName(PdfName.Type))
               && !parents.contains(current)) {
            if (all) {
                parents.add(current);
            }
            previous = current;
            current = previous.isFlushed() ? null : previous.getAsDictionary(PdfName.P);
        }
        if (!all) {
            parents.add(previous);
        }
        return new ArrayList<>(parents);
    }

    static class LastClonedAncestor {
        PdfDictionary ancestor;
        PdfDictionary clone;
    }

    private static class StructElemCopyingParams {
        private final Set<PdfObject> objectsToCopy;
        private final PdfDocument toDocument;
        private final Map<PdfDictionary, PdfDictionary> page2page;
        private final boolean copyFromDestDocument;

        private final Set<PdfObject> copiedNamespaces;

        public StructElemCopyingParams(Set<PdfObject> objectsToCopy, PdfDocument toDocument, Map<PdfDictionary, PdfDictionary> page2page, boolean copyFromDestDocument) {
            this.objectsToCopy = objectsToCopy;
            this.toDocument = toDocument;
            this.page2page = page2page;
            this.copyFromDestDocument = copyFromDestDocument;
            this.copiedNamespaces = new LinkedHashSet<>();
        }

        public Set<PdfObject> getObjectsToCopy() {
            return objectsToCopy;
        }

        public PdfDocument getToDocument() {
            return toDocument;
        }

        public Map<PdfDictionary, PdfDictionary> getPage2page() {
            return page2page;
        }

        public boolean isCopyFromDestDocument() {
            return copyFromDestDocument;
        }

        public void addCopiedNamespace(PdfDictionary copiedNs) {
            copiedNamespaces.add(copiedNs);
        }

        public Set<PdfObject> getCopiedNamespaces() {
            return copiedNamespaces;
        }
    }

    private static class CopyStructureResult {
        private final List<PdfDictionary> topsList;
        private final Set<PdfObject> copiedNamespaces;

        public CopyStructureResult(List<PdfDictionary> topsList, Set<PdfObject> copiedNamespaces) {
            this.topsList = topsList;
            this.copiedNamespaces = copiedNamespaces;
        }

        public Set<PdfObject> getCopiedNamespaces() {
            return copiedNamespaces;
        }

        public List<PdfDictionary> getTopsList() {
            return topsList;
        }
    }
}
