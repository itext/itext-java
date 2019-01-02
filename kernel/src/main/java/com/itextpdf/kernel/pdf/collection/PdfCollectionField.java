/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
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
package com.itextpdf.kernel.pdf.collection;

import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.pdf.*;

public class PdfCollectionField extends PdfObjectWrapper<PdfDictionary> {

    private static final long serialVersionUID = 4766153544105870238L;

    /**
     * A possible type of collection field.
     */
    public static final int TEXT = 0;
    /**
     * A possible type of collection field.
     */
    public static final int DATE = 1;
    /**
     * A possible type of collection field.
     */
    public static final int NUMBER = 2;
    /**
     * A possible type of collection field.
     */
    public static final int FILENAME = 3;
    /**
     * A possible type of collection field.
     */
    public static final int DESC = 4;
    /**
     * A possible type of collection field.
     */
    public static final int MODDATE = 5;
    /**
     * A possible type of collection field.
     */
    public static final int CREATIONDATE = 6;
    /**
     * A possible type of collection field.
     */
    public static final int SIZE = 7;

    protected int subType;

    protected PdfCollectionField(PdfDictionary pdfObject) {
        super(pdfObject);
        String subType = pdfObject.getAsName(PdfName.Subtype).getValue();
        switch (subType) {
            case "D":
                this.subType = DATE;
                break;
            case "N":
                this.subType = NUMBER;
                break;
            case "F":
                this.subType = FILENAME;
                break;
            case "Desc":
                this.subType = DESC;
                break;
            case "ModDate":
                this.subType = MODDATE;
                break;
            case "CreationDate":
                this.subType = CREATIONDATE;
                break;
            case "Size":
                this.subType = SIZE;
                break;
            default:
                this.subType = TEXT;
                break;
        }
    }

    /**
     * Creates a PdfCollectionField.
     *
     * @param name    the field name
     * @param subType the field subtype
     */
    public PdfCollectionField(String name, int subType) {
        super(new PdfDictionary());
        getPdfObject().put(PdfName.N, new PdfString(name));
        this.subType = subType;
        switch (subType) {
            default:
                getPdfObject().put(PdfName.Subtype, PdfName.S);
                break;
            case DATE:
                getPdfObject().put(PdfName.Subtype, PdfName.D);
                break;
            case NUMBER:
                getPdfObject().put(PdfName.Subtype, PdfName.N);
                break;
            case FILENAME:
                getPdfObject().put(PdfName.Subtype, PdfName.F);
                break;
            case DESC:
                getPdfObject().put(PdfName.Subtype, PdfName.Desc);
                break;
            case MODDATE:
                getPdfObject().put(PdfName.Subtype, PdfName.ModDate);
                break;
            case CREATIONDATE:
                getPdfObject().put(PdfName.Subtype, PdfName.CreationDate);
                break;
            case SIZE:
                getPdfObject().put(PdfName.Subtype, PdfName.Size);
                break;
        }
    }

    /**
     * The relative order of the field name. Fields are sorted in ascending order.
     *
     * @param order a number indicating the order of the field
     */
    public PdfCollectionField setOrder(int order) {
        getPdfObject().put(PdfName.O, new PdfNumber(order));
        return this;
    }

    public PdfNumber getOrder() {
        return getPdfObject().getAsNumber(PdfName.O);
    }

    /**
     * Sets the initial visibility of the field.
     *
     * @param visible
     */
    public PdfCollectionField setVisibility(boolean visible) {
        getPdfObject().put(PdfName.V, PdfBoolean.valueOf(visible));
        return this;
    }

    public PdfBoolean getVisibility() {
        return getPdfObject().getAsBoolean(PdfName.V);
    }

    /**
     * Indication if the field value should be editable in the viewer.
     *
     * @param editable
     */
    public PdfCollectionField setEditable(boolean editable) {
        getPdfObject().put(PdfName.E, PdfBoolean.valueOf(editable));
        return this;
    }

    public PdfBoolean getEditable() {
        return getPdfObject().getAsBoolean(PdfName.E);
    }

    public PdfObject getValue(String value) {
        switch (subType) {
            case TEXT:
                return new PdfString(value);
            case DATE:
                return new PdfDate(PdfDate.decode(value)).getPdfObject();
            case NUMBER:
                return new PdfNumber(Double.parseDouble(value.trim()));
        }
        throw new PdfException(PdfException._1IsNotAnAcceptableValueForTheField2).setMessageParams(value, getPdfObject().getAsName(PdfName.N).getValue());
    }

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return false;
    }
}
