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
                switch (p.getIntValue()) {
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