package com.itextpdf.forms;

import com.itextpdf.core.pdf.*;

/**
 * A signature field lock dictionary. Specifies a set of form
 * fields that shall be locked when this signature field is
 * signed.
 */
public class PdfSigFieldLockDictionary extends PdfObjectWrapper<PdfDictionary> {

    /**
     * Creates an instance of {@link PdfSigFieldLockDictionary}.
     */
    public PdfSigFieldLockDictionary() {
        this(new PdfDictionary());
    }

    /**
     * Creates an instance of {@link PdfSigFieldLockDictionary}.
     * @param dict The dictionary whose entries should be added to
     *             the signature field lock dictionary.
     */
    public PdfSigFieldLockDictionary(PdfDictionary dict) {
        super(dict);
        put(PdfName.Type, PdfName.SigFieldLock);
    }

    /**
     * Sets the permissions granted for the document when the corresponding signature
     * field is signed. See {@link com.itextpdf.forms.PdfSigFieldLockDictionary.LockPermissions}
     * for getting more info.
     * @param permissions The permissions granted for the document.
     * @return This {@link PdfSigFieldLockDictionary} object.
     */
    public PdfSigFieldLockDictionary setDocumentPermissions(LockPermissions permissions) {
        put(PdfName.P, permissions.getValue());
        return this;
    }

    /**
     * Sets signature lock for specific fields in the document.
     * @param action Indicates the set of fields that should be locked after the actual
     *               signing of the corresponding signature takes place.
     * @param fields Names indicating the fields.
     * @return This {@link PdfSigFieldLockDictionary} object.
     */
    public PdfSigFieldLockDictionary setFieldLock(LockAction action, String... fields) {
        PdfArray fieldsArray = new PdfArray();

        for (String field : fields) {
            fieldsArray.add(new PdfString(field));
        }

        put(PdfName.Action, action.getValue());
        put(PdfName.Fields, fieldsArray);

        return this;
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
        ALL(PdfName.All), INCLUDE(PdfName.Include), EXCLUDE(PdfName.Exclude);

        private PdfName name;

        LockAction(PdfName name) {
            this.name = name;
        }

        public PdfName getValue() {
            return name;
        }
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
        NO_CHANGES_ALLOWED(1), FORM_FILLING(2), FORM_FILLING_AND_ANNOTATION(3);

        private PdfNumber number;

        LockPermissions(int p) {
            number = new PdfNumber(p);
        }

        public PdfNumber getValue() {
            return number;
        }
    }
}
