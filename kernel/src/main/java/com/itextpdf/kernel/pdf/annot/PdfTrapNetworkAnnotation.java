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

import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDate;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;

import java.util.List;

public class PdfTrapNetworkAnnotation extends PdfAnnotation {

    private static final long serialVersionUID = 5118904991630303608L;

	public PdfTrapNetworkAnnotation(Rectangle rect, PdfFormXObject appearanceStream) {
        super(rect);
        if (appearanceStream.getProcessColorModel() == null) {
            throw new PdfException("Process color model must be set in appearance stream for Trap Network annotation!");
        }
        setNormalAppearance(appearanceStream.getPdfObject());
        setFlags(PdfAnnotation.Print | PdfAnnotation.ReadOnly);
    }

    public PdfTrapNetworkAnnotation(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    public PdfName getSubtype() {
        return PdfName.TrapNet;
    }

    public PdfTrapNetworkAnnotation setLastModified(PdfDate lastModified) {
        return (PdfTrapNetworkAnnotation) put(PdfName.LastModified, lastModified.getPdfObject());
    }

    public PdfString getLastModified() {
        return getPdfObject().getAsString(PdfName.LastModified);
    }

    public PdfTrapNetworkAnnotation setVersion(PdfArray version) {
        return (PdfTrapNetworkAnnotation) put(PdfName.Version, version);
    }

    public PdfArray getVersion() {
        return getPdfObject().getAsArray(PdfName.Version);
    }

    public PdfTrapNetworkAnnotation setAnnotStates(PdfArray annotStates) {
        return (PdfTrapNetworkAnnotation) put(PdfName.AnnotStates, annotStates);
    }

    public PdfArray getAnnotStates() {
        return getPdfObject().getAsArray(PdfName.AnnotStates);
    }

    public PdfTrapNetworkAnnotation setFauxedFonts(PdfArray fauxedFonts) {
        return (PdfTrapNetworkAnnotation) put(PdfName.FontFauxing, fauxedFonts);
    }

    public PdfTrapNetworkAnnotation setFauxedFonts(List<PdfFont> fauxedFonts) {
        PdfArray arr = new PdfArray();
        for (PdfFont f : fauxedFonts)
            arr.add(f.getPdfObject());
        return setFauxedFonts(arr);
    }

    public PdfArray getFauxedFonts() {
        return getPdfObject().getAsArray(PdfName.FontFauxing);
    }
}
