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
package com.itextpdf.signatures.validation;

import com.itextpdf.commons.datastructures.Tuple2;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfIndirectReference;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfXrefTable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Utility class for comparing PDF objects, used in {@link DocumentRevisionsValidator}.
 */
final class DocumentRevisionPdfObjectComparator {

    private DocumentRevisionPdfObjectComparator() {
        //Empty constructor used to avoid instantiation of utility class
    }


    public static boolean isSameReference(PdfIndirectReference indirectReference1,
            PdfIndirectReference indirectReference2) {
        if (indirectReference1 == null || indirectReference2 == null) {
            return false;
        }
        return indirectReference1.getObjNumber() == indirectReference2.getObjNumber()
                && indirectReference1.getGenNumber() == indirectReference2.getGenNumber();
    }

    public static boolean comparePdfObjects(PdfObject pdfObject1, PdfObject pdfObject2,
            Tuple2<Set<PdfIndirectReference>, Set<PdfIndirectReference>> usuallyModifiedObjects) {
        return comparePdfObjects(pdfObject1, pdfObject2, new ArrayList<>(), usuallyModifiedObjects);
    }

    public static boolean isMaxGenerationObject(PdfIndirectReference indirectReference) {
        return indirectReference.getObjNumber() == 0 && indirectReference.getGenNumber() == PdfXrefTable.MAX_GENERATION;
    }

    public static boolean comparePdfDictionaries(PdfDictionary dictionary1, PdfDictionary dictionary2,
            List<Tuple2<PdfObject, PdfObject>> visitedObjects,
            Tuple2<Set<PdfIndirectReference>, Set<PdfIndirectReference>> usuallyModifiedObjects) {
        Set<Map.Entry<PdfName, PdfObject>> entrySet1 = dictionary1.entrySet();
        Set<Map.Entry<PdfName, PdfObject>> entrySet2 = dictionary2.entrySet();
        if (entrySet1.size() != entrySet2.size()) {
            return false;
        }
        for (Map.Entry<PdfName, PdfObject> entry1 : entrySet1) {
            if (!entrySet2.stream().anyMatch(
                    entry2 -> entry2.getKey().equals(entry1.getKey()) && comparePdfObjects(entry2.getValue(),
                            entry1.getValue(), visitedObjects, usuallyModifiedObjects))) {
                return false;
            }
        }
        return true;
    }

    private static boolean comparePdfObjects(PdfObject pdfObject1, PdfObject pdfObject2,
            List<Tuple2<PdfObject, PdfObject>> visitedObjects,
            Tuple2<Set<PdfIndirectReference>, Set<PdfIndirectReference>> usuallyModifiedObjects) {
        for (Tuple2<PdfObject, PdfObject> pair : visitedObjects) {
            if (pair.getFirst() == pdfObject1) {
                return pair.getSecond() == pdfObject2;
            }
        }
        visitedObjects.add(new Tuple2<>(pdfObject1, pdfObject2));
        if (Objects.equals(pdfObject1, pdfObject2)) {
            return true;
        }
        if (pdfObject1 == null || pdfObject2 == null) {
            return false;
        }
        if (pdfObject1.getClass() != pdfObject2.getClass()) {
            return false;
        }
        if (pdfObject1.getIndirectReference() != null && usuallyModifiedObjects.getFirst().stream()
                .anyMatch(reference -> isSameReference(reference, pdfObject1.getIndirectReference()))
                && pdfObject2.getIndirectReference() != null && usuallyModifiedObjects.getSecond().stream()
                .anyMatch(reference -> isSameReference(reference, pdfObject2.getIndirectReference()))) {
            // These two objects are expected to not be completely equal, we check them independently.
            // However, we still need to make sure those are same instances.
            return isSameReference(pdfObject1.getIndirectReference(), pdfObject2.getIndirectReference());
        }
        // We don't allow objects to change from being direct to indirect and vice versa.
        // Acrobat allows it, but such change can invalidate the document.
        if (pdfObject1.getIndirectReference() == null ^ pdfObject2.getIndirectReference() == null) {
            return false;
        }
        switch (pdfObject1.getType()) {
            case PdfObject.BOOLEAN:
            case PdfObject.NAME:
            case PdfObject.NULL:
            case PdfObject.LITERAL:
            case PdfObject.NUMBER:
            case PdfObject.STRING:
                return pdfObject1.equals(pdfObject2);
            case PdfObject.INDIRECT_REFERENCE:
                return comparePdfObjects(((PdfIndirectReference) pdfObject1).getRefersTo(),
                        ((PdfIndirectReference) pdfObject2).getRefersTo(), visitedObjects, usuallyModifiedObjects);
            case PdfObject.ARRAY:
                return comparePdfArrays((PdfArray) pdfObject1, (PdfArray) pdfObject2, visitedObjects,
                        usuallyModifiedObjects);
            case PdfObject.DICTIONARY:
                return comparePdfDictionaries((PdfDictionary) pdfObject1, (PdfDictionary) pdfObject2, visitedObjects,
                        usuallyModifiedObjects);
            case PdfObject.STREAM:
                return comparePdfStreams((PdfStream) pdfObject1, (PdfStream) pdfObject2, visitedObjects,
                        usuallyModifiedObjects);
            default:
                return false;
        }
    }

    private static boolean comparePdfArrays(PdfArray array1, PdfArray array2,
            List<Tuple2<PdfObject, PdfObject>> visitedObjects,
            Tuple2<Set<PdfIndirectReference>, Set<PdfIndirectReference>> usuallyModifiedObjects) {
        if (array1.size() != array2.size()) {
            return false;
        }
        for (int i = 0; i < array1.size(); i++) {
            if (!comparePdfObjects(array1.get(i), array2.get(i), visitedObjects, usuallyModifiedObjects)) {
                return false;
            }
        }
        return true;
    }

    private static boolean comparePdfStreams(PdfStream stream1, PdfStream stream2,
            List<Tuple2<PdfObject, PdfObject>> visitedObjects,
            Tuple2<Set<PdfIndirectReference>, Set<PdfIndirectReference>> usuallyModifiedObjects) {
        return Arrays.equals(stream1.getBytes(false), stream2.getBytes(false)) && comparePdfDictionaries(stream1,
                stream2, visitedObjects, usuallyModifiedObjects);
    }
}
