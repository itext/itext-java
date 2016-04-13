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

import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.color.DeviceCmyk;
import com.itextpdf.kernel.color.DeviceGray;
import com.itextpdf.kernel.color.DeviceRgb;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfString;

abstract public class PdfMarkupAnnotation extends PdfAnnotation {

    private static final long serialVersionUID = 239280278775576458L;
	
    protected PdfAnnotation inReplyTo = null;
    protected PdfPopupAnnotation popup = null;

    public PdfMarkupAnnotation(Rectangle rect) {
        super(rect);
    }

    public PdfMarkupAnnotation(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    public PdfString getText() {
        return getPdfObject().getAsString(PdfName.T);
    }

    public PdfMarkupAnnotation setText(PdfString text) {
        return (PdfMarkupAnnotation) put(PdfName.T, text);
    }

    public PdfNumber getOpacity() {
        return getPdfObject().getAsNumber(PdfName.CA);
    }

    public PdfMarkupAnnotation setOpacity(PdfNumber ca) {
        return (PdfMarkupAnnotation) put(PdfName.CA, ca);
    }

    public PdfObject getRichText() {
        return getPdfObject().getAsDictionary(PdfName.RC);
    }

    public PdfMarkupAnnotation setRichText(PdfObject richText) {
        return (PdfMarkupAnnotation) put(PdfName.RC, richText);
    }

    public PdfString getCreationDate() {
        return getPdfObject().getAsString(PdfName.CreationDate);
    }

    public PdfMarkupAnnotation setCreationDate(PdfString creationDate) {
        return (PdfMarkupAnnotation) put(PdfName.CreationDate, creationDate);
    }

    public PdfDictionary getInReplyToObject() {
        return getPdfObject().getAsDictionary(PdfName.IRT);
    }

    public PdfAnnotation getInReplyTo() {
        return inReplyTo;
    }

    public PdfMarkupAnnotation setInReplyTo(PdfAnnotation inReplyTo) {
        this.inReplyTo = inReplyTo;
        return (PdfMarkupAnnotation) put(PdfName.IRT, inReplyTo.getPdfObject());
    }

    public PdfMarkupAnnotation setPopup(PdfPopupAnnotation popup) {
        this.popup = popup;
        popup.put(PdfName.Parent, getPdfObject());
        return (PdfMarkupAnnotation) put(PdfName.Popup, popup.getPdfObject());
    }

    public PdfDictionary getPopupObject() {
        return getPdfObject().getAsDictionary(PdfName.Popup);
    }

    public PdfPopupAnnotation getPopup() {
        return popup;
    }

    public PdfString getSubject() {
        return getPdfObject().getAsString(PdfName.Subj);
    }

    public PdfMarkupAnnotation setSubject(PdfString subject) {
        return (PdfMarkupAnnotation) put(PdfName.Subj, subject);
    }

    public PdfName getReplyType() {
        return getPdfObject().getAsName(PdfName.RT);
    }

    public PdfMarkupAnnotation setReplyType(PdfName replyType) {
        return (PdfMarkupAnnotation) put(PdfName.RT, replyType);
    }

    public PdfName getIntent() {
        return getPdfObject().getAsName(PdfName.IT);
    }

    public PdfMarkupAnnotation setIntent(PdfName intent) {
        return (PdfMarkupAnnotation) put(PdfName.IT, intent);
    }

    public PdfDictionary getExternalData() {
        return getPdfObject().getAsDictionary(PdfName.ExData);
    }

    public PdfMarkupAnnotation setExternalData(PdfName exData) {
        return (PdfMarkupAnnotation) put(PdfName.ExData, exData);
    }

    public PdfMarkupAnnotation setRectangleDifferences(PdfArray rect) {
        return (PdfMarkupAnnotation) put(PdfName.RD, rect);
    }

    public PdfArray getRectangleDifferences() {
        return getPdfObject().getAsArray(PdfName.RD);
    }

    public PdfDictionary getBorderEffect() {
        return getPdfObject().getAsDictionary(PdfName.BE);
    }

    public PdfMarkupAnnotation setBorderEffect(PdfDictionary borderEffect) {
        return (PdfMarkupAnnotation) put(PdfName.BE, borderEffect);
    }

    public Color getInteriorColor() {
        PdfArray color = getPdfObject().getAsArray(PdfName.IC);
        if (color == null) {
            return null;
        }
        switch (color.size()) {
            case 1:
                return new DeviceGray(color.getAsFloat(0));
            case 3:
                return new DeviceRgb(color.getAsFloat(0), color.getAsFloat(1), color.getAsFloat(2));
            case 4:
                return new DeviceCmyk(color.getAsFloat(0), color.getAsFloat(1), color.getAsFloat(2), color.getAsFloat(3));
            default:
                return null;
        }
    }

    public PdfMarkupAnnotation setInteriorColor(PdfArray interiorColor) {
        return (PdfMarkupAnnotation) put(PdfName.IC, interiorColor);
    }

    public PdfMarkupAnnotation setInteriorColor(float[] interiorColor) {
        return setInteriorColor(new PdfArray(interiorColor));
    }


    public PdfName getIconName() {
        return getPdfObject().getAsName(PdfName.Name);
    }

    public PdfMarkupAnnotation setIconName(PdfName name) {
        return (PdfMarkupAnnotation) put(PdfName.Name, name);
    }

    public PdfMarkupAnnotation setDefaultAppearance(PdfString appearanceString) {
        return (PdfMarkupAnnotation) put(PdfName.DA, appearanceString);
    }

    public PdfString getDefaultAppearance() {
        return getPdfObject().getAsString(PdfName.DA);
    }

    public int getJustification() {
        PdfNumber q = getPdfObject().getAsNumber(PdfName.Q);
        return q == null ? 0 : q.getIntValue();
    }

    public PdfMarkupAnnotation setJustification(int justification) {
        return (PdfMarkupAnnotation) put(PdfName.Q, new PdfNumber(justification));
    }
}
