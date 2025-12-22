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
package com.itextpdf.pdfua.checkers.utils.ua2;

import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNameTree;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.kernel.pdf.tagging.IStructureNode;
import com.itextpdf.kernel.pdf.tagging.PdfObjRef;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;
import com.itextpdf.pdfua.checkers.utils.ContextAwareTagTreeIteratorHandler;
import com.itextpdf.pdfua.checkers.utils.PdfUAValidationContext;
import com.itextpdf.pdfua.exceptions.PdfUAConformanceException;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Class that provides methods for checking PDF/UA-2 compliance of link annotations.
 */
public final class PdfUA2LinkChecker {
    private final PdfDocument pdfDoc;
    private final PdfUAValidationContext context;

    private final Map<PdfObject, Set<IStructureNode>> destinationToStructParentsMap = new HashMap<>();

    private PdfUA2LinkChecker(PdfUAValidationContext context, PdfDocument pdfDoc) {
        this.context = context;
        this.pdfDoc = pdfDoc;
    }

    /**
     * Verifies that each link annotation present in the document is tagged.
     *
     * @param document the {@link PdfDocument} to check links for
     */
    public static void checkLinkAnnotations(PdfDocument document) {
        int amountOfPages = document.getNumberOfPages();
        for (int i = 1; i <= amountOfPages; ++i) {
            PdfPage page = document.getPage(i);
            for (final PdfAnnotation annot : page.getAnnotations()) {
                if (!(annot instanceof PdfLinkAnnotation)) {
                    continue;
                }
                if (annot.getStructParentIndex() == -1) {
                    throw new PdfUAConformanceException(
                            PdfUAExceptionMessageConstants.LINK_ANNOT_IS_NOT_NESTED_WITHIN_LINK_OR_REFERENCE);
                }
            }
        }
    }

    /**
     * Checks that link annotation is enclosed in either a Link or Reference structure element.
     *
     * <p>
     * Also checks that link annotations that target different locations are in separate Link or Reference structure
     * elements, and multiple link annotations targeting the same location are included in a single Link or Reference
     * structure element.
     *
     * @param elem link annotation object reference in the structure tree
     */
    private void checkLinkAnnotationStructureParent(IStructureNode elem) {
        if (!(elem instanceof PdfObjRef) || ((PdfObjRef) elem).getReferencedObject() == null) {
            return;
        }
        PdfName subtype = ((PdfObjRef) elem).getReferencedObject().getAsName(PdfName.Subtype);
        if (!PdfName.Link.equals(subtype)) {
            return;
        }

        IStructureNode linkParent = elem.getParent();
        PdfStructElem parentLink = context.getElementIfRoleMatches(PdfName.Link, linkParent);
        if (parentLink == null) {
            PdfStructElem parentRef = context.getElementIfRoleMatches(PdfName.Reference, linkParent);
            if (parentRef == null) {
                throw new PdfUAConformanceException(
                        PdfUAExceptionMessageConstants.LINK_ANNOT_IS_NOT_NESTED_WITHIN_LINK_OR_REFERENCE);
            }
        }
        checkStructDestinationsInLinkAndReference((PdfObjRef) elem);
    }

    /**
     * Checks that link annotations that target different locations (destinations) are in separate Link or Reference
     * structure elements, and multiple link annotations targeting the same location are included in a single Link
     * or Reference structure element.
     *
     * @param objRef link annotation object reference in the structure tree
     */
    private void checkStructDestinationsInLinkAndReference(PdfObjRef objRef) {
        IStructureNode parent = objRef.getParent();
        if (parent == null) {
            return;
        }
        PdfObject structDestination = getStructureDestinationObject(objRef.getReferencedObject());
        if (structDestination == null) {
            return;
        }

        // Go through all other already checked destinations. They shall have separate Link or Reference structure
        // elements, so no other parent should be equal to the current one. Otherwise, exception will be thrown.
        for (Map.Entry<PdfObject, Set<IStructureNode>> entry : destinationToStructParentsMap.entrySet()) {
            if (structDestination.equals(entry.getKey())) {
                // Skip current destination.
                continue;
            }
            for (IStructureNode parentNode : entry.getValue()) {
                if (parent.equals(parentNode)) {
                    throw new PdfUAConformanceException(
                            PdfUAExceptionMessageConstants.DIFFERENT_LINKS_IN_SINGLE_STRUCT_ELEM);
                }
            }
        }

        // In the map, key is a destination object from current link annotation, value is a set of Link or Reference
        // structure elements enclosing already checked links annotation with that same destination (actually, value
        // always contains either 0 or 1 parent, it's just more convenient to use set during checks).
        Set<IStructureNode> destinationStructParents = destinationToStructParentsMap.computeIfAbsent(structDestination,
                k -> new HashSet<>());

        // Add current parent to the map.
        destinationStructParents.add(parent);
    }

    private PdfObject getStructureDestinationObject(PdfDictionary annotObj) {
        PdfLinkAnnotation linkAnnotation = (PdfLinkAnnotation) PdfAnnotation.makeAnnotation(annotObj);
        PdfObject destination = null;
        PdfDictionary action = linkAnnotation.getAction();
        if (action != null) {
            if (PdfName.GoTo.equals(action.getAsName(PdfName.S))) {
                destination = action.get(PdfName.SD);
                if (destination == null) {
                    destination = action.get(PdfName.D);
                }
            }
        } else {
            destination = linkAnnotation.getDestinationObject();
        }
        if (destination == null) {
            return null;
        }
        PdfArray dest = getDestination(destination);
        if (dest == null || dest.isEmpty()) {
            return null;
        } else {
            return dest.get(0);
        }
    }

    private PdfArray getDestination(PdfObject destination) {
        return getDestination(destination, new HashSet<>());
    }

    private PdfArray getDestination(PdfObject destination, Set<PdfObject> checkedDestinations) {
        if (destination == null || checkedDestinations.contains(destination)) {
            return null;
        }
        checkedDestinations.add(destination);
        switch (destination.getType()) {
            case PdfObject.STRING:
                PdfNameTree destinations = pdfDoc.getCatalog().getNameTree(PdfName.Dests);
                destination = getDestination(destinations.getEntry((PdfString) destination), checkedDestinations);
                break;
            case PdfObject.NAME:
                PdfDictionary dests = pdfDoc.getCatalog().getPdfObject().getAsDictionary(PdfName.Dests);
                if (dests != null) {
                    destination = getDestination(dests.get((PdfName) destination), checkedDestinations);
                }
                break;
            case PdfObject.ARRAY:
                break;
            case PdfObject.DICTIONARY:
                PdfObject actualDestinationObject = getDestination(((PdfDictionary) destination).get(PdfName.SD),
                        checkedDestinations);
                if (actualDestinationObject == null) {
                    destination = getDestination(((PdfDictionary) destination).get(PdfName.D), checkedDestinations);
                } else {
                    destination = actualDestinationObject;
                }
                break;
            default:
                return null;
        }
        if (destination instanceof PdfArray) {
            return (PdfArray) destination;
        }
        return null;
    }

    /**
     * Helper class that checks the conformance of link annotations while iterating the tag tree structure.
     */
    public static class PdfUA2LinkAnnotationHandler extends ContextAwareTagTreeIteratorHandler {
        private final PdfUA2LinkChecker checker;

        /**
         * Creates a new instance of the {@link PdfUA2LinkAnnotationHandler}.
         *
         * @param context  the validation context
         * @param document the {@link PdfDocument} to check link annotations for
         */
        public PdfUA2LinkAnnotationHandler(PdfUAValidationContext context, PdfDocument document) {
            super(context);
            this.checker = new PdfUA2LinkChecker(context, document);
        }

        @Override
        public boolean accept(IStructureNode node) {
            return node != null;
        }

        @Override
        public void processElement(IStructureNode elem) {
            this.checker.checkLinkAnnotationStructureParent(elem);
        }
    }
}
