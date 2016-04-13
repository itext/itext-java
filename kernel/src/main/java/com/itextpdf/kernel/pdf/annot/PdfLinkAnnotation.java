/*
    $Id$

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV
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
package com.itextpdf.kernel.pdf.annot;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.navigation.PdfDestination;

public class PdfLinkAnnotation extends PdfAnnotation {

    private static final long serialVersionUID = 5795613340575331536L;
	
    /**
     * Highlight modes.
     */
    static public final PdfName None = PdfName.N;
    static public final PdfName Invert = PdfName.I;
    static public final PdfName Outline = PdfName.O;
    static public final PdfName Push = PdfName.P;

    public PdfLinkAnnotation(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    public PdfLinkAnnotation(Rectangle rect) {
        super(rect);
    }

    public PdfName getSubtype() {
        return PdfName.Link;
    }

    public PdfObject getDestinationObject() {
        return getPdfObject().get(PdfName.Dest);
    }

    public PdfLinkAnnotation setDestination(PdfObject destination) {
        return (PdfLinkAnnotation) put(PdfName.Dest, destination);
    }

    public PdfLinkAnnotation setDestination(PdfDestination destination) {
        return (PdfLinkAnnotation) put(PdfName.Dest, destination.getPdfObject());
    }

    public PdfLinkAnnotation setAction(PdfDictionary action) {
        return (PdfLinkAnnotation) put(PdfName.A, action);
    }

    @Override
    public PdfLinkAnnotation setAction(PdfAction action) {
        return (PdfLinkAnnotation) put(PdfName.A, action.getPdfObject());
    }

    public PdfName getHighlightMode() {
        return getPdfObject().getAsName(PdfName.H);
    }

    public PdfLinkAnnotation setHighlightMode(PdfName hlMode) {
        return (PdfLinkAnnotation) put(PdfName.H, hlMode);
    }

    public PdfDictionary getUriActionObject() {
        return getPdfObject().getAsDictionary(PdfName.PA);
    }

    public PdfLinkAnnotation setUriAction(PdfDictionary action) {
        return (PdfLinkAnnotation) put(PdfName.PA, action);
    }

    public PdfLinkAnnotation setUriAction(PdfAction action) {
        return (PdfLinkAnnotation) put(PdfName.PA, action.getPdfObject());
    }
}
