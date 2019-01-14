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
package com.itextpdf.signatures;

import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;

import java.util.ArrayList;
import java.util.List;

/**
 * A helper class that tells you more about the type of signature
 * (certification or approval) and the signature's DMP settings.
 */
public class SignaturePermissions {

    /**
     * Class that contains a field lock action and
     * an array of the fields that are involved.
     */
    public class FieldLock {
        /** Can be /All, /Exclude or /Include */
        PdfName action;
        /** An array of PdfString values with fieldnames */
        PdfArray fields;
        /** Creates a FieldLock instance */
        public FieldLock(PdfName action, PdfArray fields) {
            this.action = action;
            this.fields = fields;
        }
        /** Getter for the field lock action. */
        public PdfName getAction() { return action; }
        /** Getter for the fields involved in the lock action. */
        public PdfArray getFields() { return fields; }
        /** toString method */
        public String toString() {
            return action.toString() + (fields == null ? "" : fields.toString());
        }
    }

    /** Is the signature a cerification signature (true) or an approval signature (false)? */
    boolean certification = false;
    /** Is form filling allowed by this signature? */
    boolean fillInAllowed = true;
    /** Is adding annotations allowed by this signature? */
    boolean annotationsAllowed = true;
    /** Does this signature lock specific fields? */
    List<FieldLock> fieldLocks = new ArrayList<>();

    /**
     * Creates an object that can inform you about the type of signature
     * in a signature dictionary as well as some of the permissions
     * defined by the signature.
     */
    public SignaturePermissions(PdfDictionary sigDict, SignaturePermissions previous) {
        if (previous != null) {
            annotationsAllowed &= previous.isAnnotationsAllowed();
            fillInAllowed &= previous.isFillInAllowed();
            fieldLocks.addAll(previous.getFieldLocks());
        }
        PdfArray ref = sigDict.getAsArray(PdfName.Reference);
        if (ref != null) {
            for (int i = 0; i < ref.size(); i++) {
                PdfDictionary dict = ref.getAsDictionary(i);
                PdfDictionary params = dict.getAsDictionary(PdfName.TransformParams);
                if (PdfName.DocMDP.equals(dict.getAsName(PdfName.TransformMethod))) {
                    certification = true;
                }
                PdfName action = params.getAsName(PdfName.Action);
                if (action != null) {
                    fieldLocks.add(new FieldLock(action, params.getAsArray(PdfName.Fields)));
                }
                PdfNumber p = params.getAsNumber(PdfName.P);
                if (p == null)
                    continue;
                switch (p.intValue()) {
                    default:
                        break;
                    case 1:
                        fillInAllowed &= false;
                    case 2:
                        annotationsAllowed &= false;
                }
            }
        }
    }

    /**
     * Getter to find out if the signature is a certification signature.
     * @return true if the signature is a certification signature, false for an approval signature.
     */
    public boolean isCertification() {
        return certification;
    }
    /**
     * Getter to find out if filling out fields is allowed after signing.
     * @return true if filling out fields is allowed
     */
    public boolean isFillInAllowed() {
        return fillInAllowed;
    }
    /**
     * Getter to find out if adding annotations is allowed after signing.
     * @return true if adding annotations is allowed
     */
    public boolean isAnnotationsAllowed() {
        return annotationsAllowed;
    }
    /**
     * Getter for the field lock actions, and fields that are impacted by the action
     * @return an Array with field names
     */
    public List<FieldLock> getFieldLocks() {
        return fieldLocks;
    }
}
