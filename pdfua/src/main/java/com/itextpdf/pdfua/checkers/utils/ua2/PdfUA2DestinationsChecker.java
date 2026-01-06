/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
import com.itextpdf.kernel.pdf.PdfCatalog;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNameTree;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfOutline;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.kernel.pdf.navigation.PdfDestination;
import com.itextpdf.kernel.pdf.navigation.PdfExplicitDestination;
import com.itextpdf.kernel.pdf.navigation.PdfExplicitRemoteGoToDestination;
import com.itextpdf.kernel.pdf.navigation.PdfNamedDestination;
import com.itextpdf.kernel.pdf.navigation.PdfStringDestination;
import com.itextpdf.kernel.pdf.navigation.PdfStructureDestination;
import com.itextpdf.kernel.validation.context.PdfDestinationAdditionContext;
import com.itextpdf.pdfua.exceptions.PdfUAConformanceException;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class which performs UA-2 checks related to intra-document destinations.
 */
public class PdfUA2DestinationsChecker {

    private final PdfDestinationAdditionContext context;
    private final PdfDocument document;

    /**
     * Creates {@link PdfUA2DestinationsChecker} instance.
     *
     * @param context {@link PdfDestinationAdditionContext} which contains destination which was added
     * @param document {@link PdfDocument} instance to which destination was added
     */
    public PdfUA2DestinationsChecker(PdfDestinationAdditionContext context, PdfDocument document) {
        this.context = context;
        this.document = document;
    }

    /**
     * Creates {@link PdfUA2DestinationsChecker} instance.
     *
     * @param document {@link PdfDocument} instance in which destinations shall be checked
     */
    public PdfUA2DestinationsChecker(PdfDocument document) {
        this.context = null;
        this.document = document;
    }

    /**
     * Checks all the destinations in the document.
     */
    public void checkDestinations() {
        checkDestinationsInLinks();
        if (document.hasOutlines()) {
            checkDestinationsInOutline(document.getOutlines(true));
        }
        checkAllGoToActions();
    }

    /**
     * Checks specific destination which was recently added.
     */
    public void checkDestinationsOnCreation() {
        if (context != null && !isDestinationAllowed(context.getDestination(), document, 0) &&
                !isActionAllowed(context.getAction(), document, 0)) {
            throw new PdfUAConformanceException(PdfUAExceptionMessageConstants.DESTINATION_NOT_STRUCTURE_DESTINATION);
        }
    }

    private void checkDestinationsInLinks() {
        for (int i = 1; i < document.getNumberOfPages() + 1; ++i) {
            PdfPage page = document.getPage(i);
            for (PdfAnnotation annotation : page.getAnnotations()) {
                if (annotation instanceof PdfLinkAnnotation) {
                    PdfLinkAnnotation linkAnnotation = (PdfLinkAnnotation) annotation;
                    if (!isDestinationAllowed(linkAnnotation.getDestinationObject(), document, 0)) {
                        throw new PdfUAConformanceException(
                                PdfUAExceptionMessageConstants.DESTINATION_NOT_STRUCTURE_DESTINATION);
                    }
                }
            }
        }
    }

    private void checkDestinationsInOutline(PdfOutline outline) {
        if (outline != null) {
            if (!isDestinationAllowed(outline.getDestination(), document, 0)) {
                throw new PdfUAConformanceException(
                        PdfUAExceptionMessageConstants.DESTINATION_NOT_STRUCTURE_DESTINATION);
            }
            for (PdfOutline kid : outline.getAllChildren()) {
                checkDestinationsInOutline(kid);
            }
        }
    }

    private void checkAllGoToActions() {
        PdfDictionary catalog = document.getCatalog().getPdfObject();
        checkAllGoToActions(catalog, new ArrayList<>());
    }

    private void checkAllGoToActions(PdfObject object, List<PdfObject> visitedObjects) {
        if (visitedObjects.stream().anyMatch(visitedObject -> visitedObject == object)) {
            return;
        }
        visitedObjects.add(object);
        switch (object.getType()) {
            case PdfObject.ARRAY:
                for (PdfObject kid : (PdfArray) object) {
                    checkAllGoToActions(kid, visitedObjects);
                }
                break;
            case PdfObject.DICTIONARY:
            case PdfObject.STREAM:
                checkGoToAction((PdfDictionary) object);
                for (PdfObject kid : ((PdfDictionary) object).values()) {
                    checkAllGoToActions(kid, visitedObjects);
                }
                break;
        }
    }

    private void checkGoToAction(PdfDictionary dictionary) {
        // If dictionary contains S entry with GoTo value we assume this is GoTo action.
        if (PdfName.GoTo.equals(dictionary.getAsName(PdfName.S))) {
            if (!isDestinationAllowed(dictionary, document, 0)) {
                throw new PdfUAConformanceException(
                        PdfUAExceptionMessageConstants.DESTINATION_NOT_STRUCTURE_DESTINATION);
            }
        }
    }

    private static boolean isDestinationAllowed(PdfObject destinationObject, PdfDocument document, int counter) {
        if (counter > 50) {
            // If we reached this method more than 50 times. Something is definitely wrong and destination isn't valid.
            // This can, for example, happen with named or string destinations pointing towards one another.
            return false;
        }
        counter++;
        return destinationObject == null || isDestinationAllowed(
                PdfDestination.makeDestination(destinationObject, false), document, counter);
    }

    private static boolean isDestinationAllowed(PdfDestination destination, PdfDocument document, int counter) {
        if (destination == null || destination instanceof PdfStructureDestination) {
            return true;
        }
        if (destination instanceof PdfExplicitDestination || destination instanceof PdfExplicitRemoteGoToDestination) {
            return false;
        }
        if (destination instanceof PdfNamedDestination) {
            return isDestinationAllowed((PdfNamedDestination) destination, document, counter);
        }
        if (destination instanceof PdfStringDestination) {
            return isDestinationAllowed((PdfStringDestination) destination, document, counter);
        }
        return true;
    }

    private static boolean isDestinationAllowed(PdfNamedDestination namedDestination, PdfDocument document,
            int counter) {
        PdfCatalog catalog = document.getCatalog();
        PdfDictionary dests = catalog.getPdfObject().getAsDictionary(PdfName.Dests);
        if (dests != null) {
            PdfObject actualDestinationObject = dests.get((PdfName) namedDestination.getPdfObject());
            if (actualDestinationObject instanceof PdfDictionary) {
                return isDestinationAllowed((PdfDictionary) actualDestinationObject, document, counter);
            }
            return isDestinationAllowed(actualDestinationObject, document, counter);
        }
        return true;
    }

    private static boolean isDestinationAllowed(PdfStringDestination stringDestination, PdfDocument document,
            int counter) {
        PdfCatalog catalog = document.getCatalog();
        PdfNameTree dests = catalog.getNameTree(PdfName.Dests);
        PdfObject actualDestinationObject = dests.getEntry((PdfString) stringDestination.getPdfObject());
        if (actualDestinationObject instanceof PdfDictionary) {
            return isDestinationAllowed((PdfDictionary) actualDestinationObject, document, counter);
        }
        return isDestinationAllowed(actualDestinationObject, document, counter);
    }

    private static boolean isDestinationAllowed(PdfDictionary destDictionary, PdfDocument document, int counter) {
        if (destDictionary == null) {
            return true;
        }
        boolean isSdPresent = destDictionary.get(PdfName.SD) != null &&
                PdfDestination.makeDestination(destDictionary.get(PdfName.SD)) != null;
        if (!isDestinationAllowed(destDictionary.get(PdfName.SD), document, counter)) {
            return false;
        }
        // We only check D entry if SD is not present.
        return isSdPresent || isDestinationAllowed(destDictionary.get(PdfName.D), document, counter);
    }

    private static boolean isActionAllowed(PdfAction action, PdfDocument document, int counter) {
        return action == null || isDestinationAllowed(action.getPdfObject(), document, counter);
    }
}
