/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2022 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
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
        if (dict.get(PdfName.Dest) != null) {
            fromDocument.storeDestinationToReaddress(
                    PdfDestination.makeDestination(dict.get(PdfName.Dest)), (PdfDestination nd) -> {
                        final PdfObject newVal = value.copyTo(targetDocument, this);
                        (new PdfPage((PdfDictionary) newParent)).
                                addAnnotation(-1, PdfAnnotation.makeAnnotation(newVal), false);
                    }, (PdfDestination od) -> {
                        //do nothing
                    });
            return false;
        }
        if (dict.getAsDictionary(PdfName.A) != null && dict.getAsDictionary(PdfName.A).get(PdfName.D) != null) {
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
        fromDocument.storeDestinationToReaddress(
                PdfDestination.makeDestination(dict.get(PdfName.D)), (PdfDestination nd) -> {
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
