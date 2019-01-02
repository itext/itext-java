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
     * @param dict The dictionary whose entries should be added to
     *             the signature field lock dictionary.
     */
    public PdfSigFieldLock(PdfDictionary dict) {
        super(dict);
        getPdfObject().put(PdfName.Type, PdfName.SigFieldLock);
    }

    /**
     * Sets the permissions granted for the document when the corresponding signature
     * field is signed. See {@link PdfSigFieldLock.LockPermissions}
     * for getting more info.
     * @param permissions The permissions granted for the document.
     * @return This {@link PdfSigFieldLock} object.
     */
    public PdfSigFieldLock setDocumentPermissions(LockPermissions permissions) {
        getPdfObject().put(PdfName.P, getLockPermission(permissions));
        return this;
    }

    /**
     * Sets signature lock for specific fields in the document.
     * @param action Indicates the set of fields that should be locked after the actual
     *               signing of the corresponding signature takes place.
     * @param fields Names indicating the fields.
     * @return This {@link PdfSigFieldLock} object.
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
     *     <li>all the fields in the document,</li>
     *     <li>all the fields specified in the /Fields array,</li>
     *     <li>all the fields except those specified in the /Fields array.</li>
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
     *     permitted; any change to the document invalidates the signature,</li>
     *     <li>{@link #FORM_FILLING} - permitted changes are filling in forms,
     *     instantiating page templates, and signing; other changes invalidate
     *     the signature,</li>
     *     <li>{@link #FORM_FILLING_AND_ANNOTATION} - permitted changes are the
     *     same as for the previous, as well as annotation creation, deletion,
     *     and modification; other changes invalidate the signature.</li>
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
