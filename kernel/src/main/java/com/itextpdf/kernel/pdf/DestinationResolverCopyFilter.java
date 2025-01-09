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
package com.itextpdf.kernel.pdf;

import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.navigation.PdfDestination;
import com.itextpdf.kernel.utils.ICopyFilter;

import java.util.Collections;
import java.util.List;

/**
 * A copy filter that will handle goto annotations and actions separately.
 */
public class DestinationResolverCopyFilter implements ICopyFilter {

    private static final List<PdfName> EXCLUDE_KEYS_ACTIONCOPY = Collections.singletonList(PdfName.D);

    private final PdfDocument targetDocument;
    private final PdfDocument fromDocument;

    /**
     * Initilazes a copy filter that will set all needed information aside to handle objects with a page destination
     * after all pages are copied.
     *
     * <p>
     *
     * @param fromDocument   the {@link PdfDocument}  the pages are copied from
     * @param targetDocument the {@link PdfDocument} the pages are copied to
     */
    public DestinationResolverCopyFilter(PdfDocument fromDocument, PdfDocument targetDocument) {
        this.fromDocument = fromDocument;
        this.targetDocument = targetDocument;
    }

    @Override
    public boolean shouldProcess(PdfObject newParent, PdfName name, PdfObject value) {
        final PdfObject workRef = getDirectPdfObject(value);
        if (workRef.getType() == PdfObject.DICTIONARY) {
            final PdfDictionary dict = (PdfDictionary) workRef;
            // a goto action
            if (dict.getAsName(PdfName.S) == PdfName.GoTo) {
                processAction(newParent, name, dict);
                return false;
            }
            // a link annotation with destination
            if (PdfName.Link.equals(dict.getAsName(PdfName.Subtype)) && newParent.isDictionary()) {
                return processLinkAnnotion(newParent, value, dict);
            }
        }
        return true;
    }

    private boolean processLinkAnnotion(PdfObject newParent, PdfObject value, PdfDictionary dict) {
        PdfObject destination = dict.get(PdfName.Dest);
        if (destination != null && !destination.equals(PdfNull.PDF_NULL)) {
            fromDocument.storeDestinationToReaddress(
                    PdfDestination.makeDestination(destination), (PdfDestination nd) -> {
                        final PdfObject newVal = value.copyTo(targetDocument, this);
                        (new PdfPage((PdfDictionary) newParent)).
                                addAnnotation(-1, PdfAnnotation.makeAnnotation(newVal), false);
                    }, (PdfDestination od) -> {
                        //do nothing
                    });
            return false;
        }
        if (dict.getAsDictionary(PdfName.A) != null && dict.getAsDictionary(PdfName.A).get(PdfName.D) != null
                && !PdfNull.PDF_NULL.equals(dict.getAsDictionary(PdfName.A).get(PdfName.D))
                && !PdfName.GoToR.equals(dict.getAsDictionary(PdfName.A).get(PdfName.S))) {
            fromDocument.storeDestinationToReaddress(
                    PdfDestination.makeDestination(dict.getAsDictionary(PdfName.A).get(PdfName.D)),
                    (PdfDestination nd) -> {
                        final PdfObject newAnnot = value.copyTo(targetDocument);
                        ((PdfDictionary) newAnnot).getAsDictionary(PdfName.A).put(PdfName.D, nd.getPdfObject());
                        (new PdfPage((PdfDictionary) newParent)).
                                addAnnotation(-1, PdfAnnotation.makeAnnotation(newAnnot), false);
                    }, (PdfDestination od) -> {
                        //do nothing
                    });
            return false;
        }
        return true;
    }

    private void processAction(PdfObject newParent, PdfName name, PdfDictionary dict) {
        PdfObject destination = dict.get(PdfName.D);
        if (destination == null || PdfNull.PDF_NULL.equals(destination)) {
            return;
        }
        fromDocument.storeDestinationToReaddress(
                PdfDestination.makeDestination(destination), (PdfDestination nd) -> {
                    //Add action with new destination
                    final PdfObject newVal = dict.copyTo(targetDocument, EXCLUDE_KEYS_ACTIONCOPY, false);
                    ((PdfDictionary) newVal).put(PdfName.D, nd.getPdfObject());

                    if (newParent.getType() == PdfObject.DICTIONARY) {
                        ((PdfDictionary) newParent).put(name, newVal);
                    } else {
                        ((PdfArray) newParent).add(newVal);
                    }
                }, (PdfDestination od) -> {
                    //do nothing
                });
    }

    private static PdfObject getDirectPdfObject(PdfObject value) {
        PdfObject workRef = value;
        if (value.isIndirectReference()) {
            workRef = ((PdfIndirectReference) value).getRefersTo();
        }
        return workRef;
    }
}
