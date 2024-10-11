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
package com.itextpdf.kernel.pdf;

import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.logs.KernelLogMessageConstant;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.layer.PdfOCProperties;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class OcgPropertiesCopier {
    private static final Logger LOGGER = LoggerFactory.getLogger(OcgPropertiesCopier.class);

    private OcgPropertiesCopier() {
        // Empty constructor
    }

    /**
     * Copy unique page OCGs stored inside annotations/xobjects/resources from source pages to destination pages.
     *
     * @param sourceDocument document from which OCGs should be copied
     * @param destinationDocument document to which OCGs should be copied
     * @param sourceToDestPageMapping page mapping, linking source pages to destination ones
     */
    public static void copyOCGProperties(PdfDocument sourceDocument, PdfDocument destinationDocument,
                                         Map<PdfPage, PdfPage> sourceToDestPageMapping) {
        try {
            // Configs are not copied
            PdfDictionary toOcProperties = destinationDocument.getCatalog().getPdfObject().getAsDictionary(PdfName.OCProperties);
            final Set<PdfIndirectReference> ocgsToCopy = OcgPropertiesCopier
                    .getAllUsedNonFlushedOCGs(sourceToDestPageMapping, toOcProperties);
            if (ocgsToCopy.isEmpty()) {
                return;
            }

            // Reset ocProperties field in order to create it a new at the
            // method end using the new (merged) OCProperties dictionary
            toOcProperties = destinationDocument.getCatalog().fillAndGetOcPropertiesDictionary();
            final PdfDictionary fromOcProperties = sourceDocument.getCatalog().getPdfObject()
                    .getAsDictionary(PdfName.OCProperties);

            OcgPropertiesCopier.copyOCGs(ocgsToCopy, toOcProperties, destinationDocument);

            OcgPropertiesCopier.copyDDictionary(ocgsToCopy, fromOcProperties.getAsDictionary(PdfName.D),
                    toOcProperties, destinationDocument);
        } catch (Exception e) {
            LOGGER.error(MessageFormatUtil.format(IoLogMessageConstant.OCG_COPYING_ERROR, e.toString()));
        }
    }

    /**
     * Get all OCGs from a given page annotations/xobjects/resources, including ones already stored in catalog
     *
     * @param page where to search for OCGs.
     * @return set of indirect references pointing to found OCGs.
     */
    static Set<PdfIndirectReference> getOCGsFromPage(PdfPage page) {
        //Using linked hash set for elements order consistency (e.g. in tests)
        final Set<PdfIndirectReference> ocgs = new LinkedHashSet<>();
        final List<PdfAnnotation> annotations = page.getAnnotations();
        for (PdfAnnotation annotation : annotations) {
            //Pass null instead of catalog OCProperties value, to include ocg clashing with catalog
            getUsedNonFlushedOCGsFromAnnotation(annotation, annotation, ocgs, null);
        }
        final PdfDictionary resources = page.getPdfObject().getAsDictionary(PdfName.Resources);
        OcgPropertiesCopier.getUsedNonFlushedOCGsFromResources(resources, resources, ocgs,
                null, new HashSet<>());
        return ocgs;
    }

    private static Set<PdfIndirectReference> getAllUsedNonFlushedOCGs(Map<PdfPage, PdfPage> page2page, PdfDictionary toOcProperties) {
        // NOTE: the PDF is considered to be valid and therefore the presence of OСG in OCProperties.OCGs is not checked
        final Set<PdfIndirectReference> fromUsedOcgs = new LinkedHashSet<>();
        // Visit the pages in parallel to find non-flush OSGs
        final PdfPage[] fromPages = page2page.keySet().toArray(new PdfPage[0]);
        final PdfPage[] toPages = page2page.values().toArray(new PdfPage[0]);
        for (int i = 0; i < toPages.length; i++) {
            final PdfPage fromPage = fromPages[i];
            final PdfPage toPage = toPages[i];

            // Copy OCGs from annotations
            final List<PdfAnnotation> toAnnotations = toPage.getAnnotations();
            final List<PdfAnnotation> fromAnnotations = fromPage.getAnnotations();
            for (int j = 0; j < toAnnotations.size(); j++) {
                if (!toAnnotations.get(j).isFlushed()) {
                    final PdfAnnotation toAnnot = toAnnotations.get(j);
                    final PdfAnnotation fromAnnot = fromAnnotations.get(j);
                    if (!toAnnot.getPdfObject().isFlushed()) {
                        getUsedNonFlushedOCGsFromAnnotation(toAnnot, fromAnnot, fromUsedOcgs, toOcProperties);
                    }
                }
            }

            final PdfDictionary toResources = toPage.getPdfObject().getAsDictionary(PdfName.Resources);
            final PdfDictionary fromResources = fromPage.getPdfObject().getAsDictionary(PdfName.Resources);
            OcgPropertiesCopier.getUsedNonFlushedOCGsFromResources(toResources, fromResources, fromUsedOcgs,
                    toOcProperties, new HashSet<>());
        }
        return fromUsedOcgs;
    }

    private static void getUsedNonFlushedOCGsFromAnnotation(PdfAnnotation toAnnot, PdfAnnotation fromAnnot, Set<PdfIndirectReference> fromUsedOcgs, PdfDictionary toOcProperties) {
        OcgPropertiesCopier.getUsedNonFlushedOCGsFromOcDict(toAnnot.getPdfObject().getAsDictionary(PdfName.OC),
                fromAnnot.getPdfObject().getAsDictionary(PdfName.OC), fromUsedOcgs, toOcProperties);
        OcgPropertiesCopier.getUsedNonFlushedOCGsFromXObject(toAnnot.getNormalAppearanceObject(),
                fromAnnot.getNormalAppearanceObject(), fromUsedOcgs, toOcProperties, new HashSet<>());
        OcgPropertiesCopier.getUsedNonFlushedOCGsFromXObject(toAnnot.getRolloverAppearanceObject(),
                fromAnnot.getRolloverAppearanceObject(), fromUsedOcgs, toOcProperties, new HashSet<>());
        OcgPropertiesCopier.getUsedNonFlushedOCGsFromXObject(toAnnot.getDownAppearanceObject(),
                fromAnnot.getDownAppearanceObject(), fromUsedOcgs, toOcProperties, new HashSet<>());
    }

    private static void getUsedNonFlushedOCGsFromResources(PdfDictionary toResources, PdfDictionary fromResources,
            Set<PdfIndirectReference> fromUsedOcgs, PdfDictionary toOcProperties, Set<PdfObject> visitedObjects) {
        if (toResources != null && !toResources.isFlushed()) {
            // Copy OCGs from properties
            final PdfDictionary toProperties = toResources.getAsDictionary(PdfName.Properties);
            final PdfDictionary fromProperties = fromResources.getAsDictionary(PdfName.Properties);
            if (toProperties != null && !toProperties.isFlushed()) {
                for (final PdfName name : toProperties.keySet()) {
                    final PdfObject toCurrObj = toProperties.get(name);
                    final PdfObject fromCurrObj = fromProperties.get(name);
                    OcgPropertiesCopier.getUsedNonFlushedOCGsFromOcDict(toCurrObj, fromCurrObj, fromUsedOcgs, toOcProperties);
                }
            }

            // Copy OCGs from xObject
            final PdfDictionary toXObject = toResources.getAsDictionary(PdfName.XObject);
            final PdfDictionary fromXObject = fromResources.getAsDictionary(PdfName.XObject);
            OcgPropertiesCopier.getUsedNonFlushedOCGsFromXObject(toXObject, fromXObject, fromUsedOcgs, toOcProperties,
                    visitedObjects);
        }
    }

    private static void getUsedNonFlushedOCGsFromXObject(PdfDictionary toXObject, PdfDictionary fromXObject,
            Set<PdfIndirectReference> fromUsedOcgs, PdfDictionary toOcProperties, Set<PdfObject> visitedObjects) {
        //Resolving cycled properties, by memorizing the visited objects
        if (visitedObjects.contains(fromXObject)) {
            return;
        }
        visitedObjects.add(fromXObject);

        if (toXObject != null && !toXObject.isFlushed()) {
            if (toXObject.isStream() && !toXObject.isFlushed()) {
                final PdfStream toStream = (PdfStream) toXObject;
                final PdfStream fromStream = (PdfStream) fromXObject;
                OcgPropertiesCopier.getUsedNonFlushedOCGsFromOcDict(toStream.getAsDictionary(PdfName.OC),
                        fromStream.getAsDictionary(PdfName.OC), fromUsedOcgs, toOcProperties);
                OcgPropertiesCopier.getUsedNonFlushedOCGsFromResources(toStream.getAsDictionary(PdfName.Resources),
                        fromStream.getAsDictionary(PdfName.Resources), fromUsedOcgs, toOcProperties, visitedObjects);
            } else {
                for (final PdfName name : toXObject.keySet()) {
                    final PdfObject toCurrObj = toXObject.get(name);
                    final PdfObject fromCurrObj = fromXObject.get(name);
                    if (toCurrObj.isStream() && !toCurrObj.isFlushed()) {
                        final PdfStream toStream = (PdfStream) toCurrObj;
                        final PdfStream fromStream = (PdfStream) fromCurrObj;
                        OcgPropertiesCopier.getUsedNonFlushedOCGsFromXObject(toStream, fromStream, fromUsedOcgs,
                                toOcProperties, visitedObjects);
                    }
                }
            }
        }
    }

    private static void getUsedNonFlushedOCGsFromOcDict(PdfObject toObj, PdfObject fromObj,
            Set<PdfIndirectReference> fromUsedOcgs, PdfDictionary toOcProperties) {
        if (toObj != null && toObj.isDictionary() && !toObj.isFlushed()) {
            final PdfDictionary toCurrDict = (PdfDictionary) toObj;
            final PdfDictionary fromCurrDict = (PdfDictionary) fromObj;
            final PdfName typeName = toCurrDict.getAsName(PdfName.Type);
            if (PdfName.OCG.equals(typeName) && !OcgPropertiesCopier.ocgAlreadyInOCGs(toCurrDict.getIndirectReference(), toOcProperties)) {
                fromUsedOcgs.add(fromCurrDict.getIndirectReference());
            } else if (PdfName.OCMD.equals(typeName)) {
                PdfArray toOcgs = null;
                PdfArray fromOcgs = null;
                if (toCurrDict.getAsDictionary(PdfName.OCGs) != null) {
                    toOcgs = new PdfArray();
                    toOcgs.add(toCurrDict.getAsDictionary(PdfName.OCGs));

                    fromOcgs = new PdfArray();
                    fromOcgs.add(fromCurrDict.getAsDictionary(PdfName.OCGs));
                } else if (toCurrDict.getAsArray(PdfName.OCGs) != null) {
                    toOcgs = toCurrDict.getAsArray(PdfName.OCGs);
                    fromOcgs = fromCurrDict.getAsArray(PdfName.OCGs);
                }

                if (toOcgs != null && !toOcgs.isFlushed()) {
                    for (int i = 0; i < toOcgs.size(); i++) {
                        OcgPropertiesCopier.getUsedNonFlushedOCGsFromOcDict(toOcgs.get(i), fromOcgs.get(i), fromUsedOcgs, toOcProperties);
                    }
                }
            }
        }
    }

    private static void copyOCGs(Set<PdfIndirectReference> fromOcgsToCopy, PdfDictionary toOcProperties, PdfDocument toDocument) {
        final Set<String> layerNames = new HashSet<>();
        if (toOcProperties.getAsArray(PdfName.OCGs) != null) {
            final PdfArray toOcgs = toOcProperties.getAsArray(PdfName.OCGs);
            for (final PdfObject toOcgObj : toOcgs) {
                if (toOcgObj.isDictionary()) {
                    layerNames.add(((PdfDictionary) toOcgObj).getAsString(PdfName.Name).toUnicodeString());
                }
            }
        }

        boolean hasConflictingNames = false;
        for (final PdfIndirectReference fromOcgRef : fromOcgsToCopy) {
            final PdfDictionary toOcg = (PdfDictionary) fromOcgRef.getRefersTo().copyTo(toDocument, false);

            String currentLayerName = toOcg.getAsString(PdfName.Name).toUnicodeString();

            // Here we check on existed layer names only in destination document but not in source document.
            // That is why there is no something like layerNames.add(currentLayerName); after this if statement
            if (layerNames.contains(currentLayerName)) {
                hasConflictingNames = true;
                int i = 0;
                while (layerNames.contains(currentLayerName + "_" + i)) {
                    i++;
                }
                currentLayerName += "_" + i;
                toOcg.put(PdfName.Name, new PdfString(currentLayerName, PdfEncodings.UNICODE_BIG));
            }

            if (toOcProperties.getAsArray(PdfName.OCGs) == null) {
                toOcProperties.put(PdfName.OCGs, new PdfArray());
            }
            toOcProperties.getAsArray(PdfName.OCGs).add(toOcg);
        }

        if (hasConflictingNames) {
            LOGGER.warn(IoLogMessageConstant.DOCUMENT_HAS_CONFLICTING_OCG_NAMES);
        }
    }

    private static boolean ocgAlreadyInOCGs(PdfIndirectReference toOcgRef, PdfDictionary toOcProperties) {
        if (toOcProperties == null) {
            return false;
        }
        final PdfArray toOcgs = toOcProperties.getAsArray(PdfName.OCGs);
        if (toOcgs != null) {
            for (final PdfObject toOcg : toOcgs) {
                if (toOcgRef.equals(toOcg.getIndirectReference())) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void copyDDictionary(Set<PdfIndirectReference> fromOcgsToCopy, PdfDictionary fromDDict,
            PdfDictionary toOcProperties, PdfDocument toDocument) {
        if (toOcProperties.getAsDictionary(PdfName.D) == null) {
            toOcProperties.put(PdfName.D, new PdfDictionary());
        }

        final PdfDictionary toDDict = toOcProperties.getAsDictionary(PdfName.D);

        OcgPropertiesCopier.copyDStringField(PdfName.Name, fromDDict, toDDict);
        // Delete the Creator field because the D dictionary are changing
        toDDict.remove(PdfName.Creator);
        OcgPropertiesCopier.copyDNameField(PdfName.BaseState, fromDDict, toDDict);
        OcgPropertiesCopier.copyDArrayField(PdfName.ON, fromOcgsToCopy, fromDDict, toDDict, toDocument);
        OcgPropertiesCopier.copyDArrayField(PdfName.OFF, fromOcgsToCopy, fromDDict, toDDict, toDocument);
        OcgPropertiesCopier.copyDNameField(PdfName.Intent, fromDDict, toDDict);
        // The AS field is not copied because it will be given when flushing the PdfOCProperties
        OcgPropertiesCopier.copyDArrayField(PdfName.Order, fromOcgsToCopy, fromDDict, toDDict, toDocument);
        // The ListMode field is copied, but it only affects the visual presentation of the layers
        OcgPropertiesCopier.copyDNameField(PdfName.ListMode, fromDDict, toDDict);
        OcgPropertiesCopier.copyDArrayField(PdfName.RBGroups, fromOcgsToCopy, fromDDict, toDDict, toDocument);
        OcgPropertiesCopier.copyDArrayField(PdfName.Locked, fromOcgsToCopy, fromDDict, toDDict, toDocument);
    }

    private static void attemptToAddObjectToArray(Set<PdfIndirectReference> fromOcgsToCopy, PdfObject fromObj,
            PdfArray toArray, PdfDocument toDocument) {
        final PdfIndirectReference fromObjRef = fromObj.getIndirectReference();
        if (fromObjRef != null && fromOcgsToCopy.contains(fromObjRef)) {
            toArray.add(fromObj.copyTo(toDocument, false));
        }
    }

    private static void copyDNameField(PdfName fieldToCopy, PdfDictionary fromDict, PdfDictionary toDict) {
        final PdfName fromName = fromDict.getAsName(fieldToCopy);
        if (fromName == null || toDict.getAsName(fieldToCopy) != null) {
            return;
        }

        if (PdfOCProperties.checkDDictonaryFieldValue(fieldToCopy, fromName)) {
            toDict.put(fieldToCopy, fromName);
        } else {
            Logger logger = LoggerFactory.getLogger(OcgPropertiesCopier.class);
            String warnText = MessageFormatUtil.format(KernelLogMessageConstant.INVALID_DDICTIONARY_FIELD_VALUE,
                    fieldToCopy, fromName);
            logger.warn(warnText);
        }
    }

    private static void copyDStringField(PdfName fieldToCopy,PdfDictionary fromDict, PdfDictionary toDict){
        PdfString fromString = fromDict.getAsString(fieldToCopy);
        if (fromString == null || toDict.getAsString(fieldToCopy) != null) {
            return;
        }

        if (PdfOCProperties.checkDDictonaryFieldValue(fieldToCopy, fromString)) {
            toDict.put(fieldToCopy,fromString);
        } else {
            Logger logger = LoggerFactory.getLogger(OcgPropertiesCopier.class);
            String warnText = MessageFormatUtil.format(KernelLogMessageConstant.INVALID_DDICTIONARY_FIELD_VALUE,
                    fieldToCopy, fromString);
            logger.warn(warnText);
        }
    }

    private static void copyDArrayField(PdfName fieldToCopy, Set<PdfIndirectReference> fromOcgsToCopy,
            PdfDictionary fromDict, PdfDictionary toDict, PdfDocument toDocument) {
        if (fromDict.getAsArray(fieldToCopy) == null) {
            return;
        }
        final PdfArray fromArray = fromDict.getAsArray(fieldToCopy);

        if (toDict.getAsArray(fieldToCopy) == null) {
            toDict.put(fieldToCopy, new PdfArray());
        }
        final PdfArray toArray = toDict.getAsArray(fieldToCopy);

        final Set<PdfIndirectReference> toOcgsToCopy = new HashSet<>();
        for (final PdfIndirectReference fromRef : fromOcgsToCopy) {
            toOcgsToCopy.add(fromRef.getRefersTo().copyTo(toDocument, false).getIndirectReference());
        }
        if (PdfName.Order.equals(fieldToCopy)) {
            // Stage 1: delete all Order the entire branches from the output document in which the copied OCGs were
            final List<Integer> removeIndex = new ArrayList<>();
            for (int i = 0; i < toArray.size(); i++) {
                PdfObject toOrderItem = toArray.get(i);
                if (OcgPropertiesCopier.orderBranchContainsSetElements(toOrderItem, toArray, i, toOcgsToCopy, null, null)) {
                    removeIndex.add(i);
                }
            }
            for (int i = removeIndex.size() - 1; i > -1; i--) {
                toArray.remove(removeIndex.get(i));
            }

            final PdfArray toOcgs = toDocument.getCatalog().getPdfObject().getAsDictionary(PdfName.OCProperties).getAsArray(PdfName.OCGs);
            // Stage 2: copy all the Order the entire branches in which the copied OСGs were
            for (int i = 0; i < fromArray.size(); i++) {
                final PdfObject fromOrderItem = fromArray.get(i);
                if (OcgPropertiesCopier.orderBranchContainsSetElements(fromOrderItem, fromArray, i, fromOcgsToCopy, toOcgs, toDocument)) {
                    toArray.add(fromOrderItem.copyTo(toDocument, false));
                }
            }

            // Stage 3: remove from Order OCGs not presented in the output document. When forming
            // the Order dictionary in the PdfOcProperties constructor, only those OCGs that are
            // in the OCProperties/OCGs array will be taken into account
        } else if (PdfName.RBGroups.equals(fieldToCopy)) {
            // Stage 1: delete all RBGroups from the output document in which the copied OCGs were
            for (int i = toArray.size() - 1; i > -1; i--) {
                final PdfArray toRbGroup = (PdfArray) toArray.get(i);
                for (final PdfObject toRbGroupItemObj : toRbGroup) {
                    if (toOcgsToCopy.contains(toRbGroupItemObj.getIndirectReference())) {
                        toArray.remove(i);
                        break;
                    }
                }
            }

            // Stage 2: copy all the RBGroups in which the copied OCGs were
            for (final PdfObject fromRbGroupObj : fromArray) {
                final PdfArray fromRbGroup = (PdfArray) fromRbGroupObj;
                for (final PdfObject fromRbGroupItemObj : fromRbGroup) {
                    if (fromOcgsToCopy.contains(fromRbGroupItemObj.getIndirectReference())) {
                        toArray.add(fromRbGroup.copyTo(toDocument, false));
                        break;
                    }
                }
            }

            // Stage 3: remove from RBGroups OCGs not presented in the output
            // document (is in the PdfOcProperties#fillDictionary method)
        } else {
            for (final PdfObject fromObj : fromArray) {
                OcgPropertiesCopier.attemptToAddObjectToArray(fromOcgsToCopy, fromObj, toArray, toDocument);
            }
        }

        if (toArray.isEmpty()) {
            toDict.remove(fieldToCopy);
        }
    }

    private static boolean orderBranchContainsSetElements(PdfObject arrayObj, PdfArray array, int currentIndex,
            Set<PdfIndirectReference> ocgs, PdfArray toOcgs, PdfDocument toDocument) {
        if (arrayObj.isDictionary()) {
            if (ocgs.contains(arrayObj.getIndirectReference())) {
                return true;
            } else {
                if (currentIndex < (array.size() - 1) && array.get(currentIndex + 1).isArray()) {
                    final PdfArray nextArray = array.getAsArray(currentIndex + 1);
                    if (!nextArray.get(0).isString()) {
                        final boolean result = OcgPropertiesCopier.orderBranchContainsSetElements(nextArray, array,
                                currentIndex + 1, ocgs, toOcgs, toDocument);
                        if (result && toOcgs != null && !ocgs.contains(arrayObj.getIndirectReference())) {
                            // Add the OCG to the OCGs array to register the OCG in document, since it is not used
                            // directly in the document, but is used as a parent for the order group. If it is not added
                            // to the OCGs array, then the OCG will be deleted at the 3rd stage of the /Order entry coping.
                            toOcgs.add(arrayObj.copyTo(toDocument, false));
                        }

                        return result;
                    }
                }
            }
        } else if (arrayObj.isArray()){
            final PdfArray arrayItem = (PdfArray) arrayObj;
            for (int i = 0; i < arrayItem.size(); i++) {
                final PdfObject obj = arrayItem.get(i);
                if (OcgPropertiesCopier.orderBranchContainsSetElements(obj, arrayItem, i, ocgs, toOcgs, toDocument)) {
                    return true;
                }
            }

            if (!arrayItem.isEmpty() && !arrayItem.get(0).isString()) {
                if (currentIndex > 0 && array.get(currentIndex - 1).isDictionary()) {
                    final PdfDictionary previousDict = (PdfDictionary) array.get(currentIndex - 1);
                    return ocgs.contains(previousDict.getIndirectReference());
                }
            }
        }
        return false;
    }
}
