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
package com.itextpdf.forms;

import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;
import com.itextpdf.kernel.pdf.PdfString;

/**
 * A signature field lock dictionary. Specifies a set of form
 * fields that shall be locked when this signature field is
 * signed.
 */
public class PdfSigFieldLock extends PdfObjectWrapper<PdfDictionary> {

    /**
     * Creates an instance of {@link PdfSigFieldLock}.
     */
    public PdfSigFieldLock() {
        this(new PdfDictionary());
    }

    /**
     * Creates an instance of {@link PdfSigFieldLock}.
     *
     * @param dict the dictionary whose entries should be added to
     *             the signature field lock dictionary
     */
    public PdfSigFieldLock(PdfDictionary dict) {
        super(dict);
        getPdfObject().put(PdfName.Type, PdfName.SigFieldLock);
    }

    /**
     * Sets the permissions granted for the document when the corresponding signature
     * field is signed. See {@link PdfSigFieldLock.LockPermissions}
     * for getting more info.
     *
     * @param permissions the permissions granted for the document
     *
     * @return this {@link PdfSigFieldLock} object.
     */
    public PdfSigFieldLock setDocumentPermissions(LockPermissions permissions) {
        getPdfObject().put(PdfName.P, getLockPermission(permissions));
        return this;
    }

    /**
     * Sets signature lock for specific fields in the document.
     *
     * @param action indicates the set of fields that should be locked after the actual
     *               signing of the corresponding signature takes place
     * @param fields names indicating the fields
     *
     * @return this {@link PdfSigFieldLock} object.
     */
    public PdfSigFieldLock setFieldLock(LockAction action, String... fields) {
        PdfArray fieldsArray = new PdfArray();
        for (String field : fields) {
            fieldsArray.add(new PdfString(field));
        }
        getPdfObject().put(PdfName.Action, getLockActionValue(action));
        getPdfObject().put(PdfName.Fields, fieldsArray);
        return this;
    }

    /**
     * Returns the specified action of a signature field lock as {@link PdfName} value.
     *
     * @param action the action as {@link LockAction}
     *
     * @return the specified action of a signature field lock as {@link PdfName}.
     */
    public static PdfName getLockActionValue(LockAction action) {
        switch (action) {
            case ALL:
                return PdfName.All;
            case INCLUDE:
                return PdfName.Include;
            case EXCLUDE:
                return PdfName.Exclude;
            default:
                return PdfName.All;
        }
    }

    /**
     * Returns the specified level of access permissions granted for the document as {@link PdfNumber} value.
     *
     * @param permissions the level of access permissions as {@link LockPermissions}
     * @return the specified level of access permissions as {@link PdfNumber}.
     */
    public static PdfNumber getLockPermission(LockPermissions permissions) {
        switch (permissions) {
            case NO_CHANGES_ALLOWED:
                return new PdfNumber(1);
            case FORM_FILLING:
                return new PdfNumber(2);
            case FORM_FILLING_AND_ANNOTATION:
                return new PdfNumber(3);
            default:
                return new PdfNumber(0);
        }
    }

    /**
     * Enumerates the different actions of a signature field lock.
     * Indicates the set of fields that should be locked when the
     * corresponding signature field is signed:
     * <ul>
     *     <li>all the fields in the document,
     *     <li>all the fields specified in the /Fields array,
     *     <li>all the fields except those specified in the /Fields array.
     * </ul>
     */
    public enum LockAction {
        ALL, INCLUDE, EXCLUDE;
    }

    /**
     * Enumerates the different levels of access permissions granted for
     * the document when the corresponding signature field is signed:
     * <ul>
     *     <li>{@link #NO_CHANGES_ALLOWED} - no changes to the document are
     *     permitted; any change to the document invalidates the signature,
     *     <li>{@link #FORM_FILLING} - permitted changes are filling in forms,
     *     instantiating page templates, and signing; other changes invalidate
     *     the signature,
     *     <li>{@link #FORM_FILLING_AND_ANNOTATION} - permitted changes are the
     *     same as for the previous, as well as annotation creation, deletion,
     *     and modification; other changes invalidate the signature.
     * </ul>
     */
    public enum LockPermissions {
        NO_CHANGES_ALLOWED, FORM_FILLING, FORM_FILLING_AND_ANNOTATION;
    }

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return true;
    }
}
