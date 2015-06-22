package com.itextpdf.forms;


import com.itextpdf.core.pdf.*;
import com.itextpdf.core.pdf.annot.PdfAnnotation;
import com.itextpdf.forms.formfields.PdfFormField;

import java.util.ArrayList;
import java.util.List;

public class PdfAcroForm extends PdfObjectWrapper<PdfDictionary> {

    /**
     * Signature flags
     */
    static public final int SIGNATURE_EXIST = 1;
    static public final int APPEND_ONLY = 2;

    public PdfAcroForm(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    public PdfAcroForm(PdfDictionary pdfObject, PdfDocument pdfDocument) {
        super(pdfObject, pdfDocument);
    }

    public PdfAcroForm(PdfDocument document, PdfArray fields) {
        this(new PdfDictionary(), document);
        put(PdfName.Fields, fields);
    }

    /**
     * Retrieves AcroForm from the document. If there is no AcroForm int the document Catalog and createIfNotExist flag is true then AcroForm dictionary will be created
     *
     * @param document
     * @param createIfNotExist
     * @return
     */
    public static PdfAcroForm getAcroForm(PdfDocument document, boolean createIfNotExist) {
        PdfDictionary acroFormDictionary = document.getCatalog().getPdfObject().getAsDictionary(PdfName.AcroForm);
        PdfAcroForm acroForm = null;
        if (acroFormDictionary == null) {
            if (createIfNotExist) {
                acroForm = new PdfAcroForm(document, new PdfArray());
                document.getCatalog().put(PdfName.AcroForm, acroForm);
            }
        } else {
            acroForm = new PdfAcroForm(acroFormDictionary);
        }

        return acroForm;
    }

    /**
     * This method adds the field to the last page in the document. If there's no pages, creates a new one.
     * @param field
     */
    public void addField(PdfFormField field) {
        PdfPage page;
        if (getDocument().getNumOfPages() == 0){
            getDocument().addNewPage();
        }
        page = getDocument().getLastPage();
        addField(field, page);
    }

    /**
     * This method adds the field to the page.
     * @param field
     * @param page
     */
    public void addField(PdfFormField field, PdfPage page){
        PdfArray kids = field.getKids();
        if (kids != null){
            processKids(kids, field.getPdfObject(), page);
        }

        getFields().add(field.getPdfObject());
    }

    public List<PdfFormField> getFormFields() {
        return iterateFields(getFields());
    }

    public PdfAcroForm setNeedAppearances(boolean needAppearances) {
        return put(PdfName.NeedAppearances, new PdfBoolean(needAppearances));
    }

    public PdfBoolean getNeedAppearances() {
        return getPdfObject().getAsBoolean(PdfName.NeedAppearances);
    }

    public PdfAcroForm setSignatureFlags(int sigFlags) {
        return put(PdfName.SigFlags, new PdfNumber(sigFlags));
    }

    public PdfAcroForm setSignatureFlag(int sigFlag) {
        int flags = getSignatureFlags();
        flags = flags | sigFlag;

        return setSignatureFlags(flags);
    }

    public int getSignatureFlags() {
        PdfNumber f = getPdfObject().getAsNumber(PdfName.SigFlags);
        if (f != null)
            return f.getIntValue();
        else
            return 0;
    }

    public PdfAcroForm setCalculationOrder(PdfArray calculationOrder) {
        return put(PdfName.CO, calculationOrder);
    }

    public PdfArray getCalculationOrder() {
        return getPdfObject().getAsArray(PdfName.CO);
    }

    public PdfAcroForm setDefaultResources(PdfDictionary defaultResources) {
        return put(PdfName.DR, defaultResources);
    }

    public PdfDictionary getDefaultResources() {
        return getPdfObject().getAsDictionary(PdfName.DR);
    }

    public PdfAcroForm setDefaultAppearance(String appearance) {
        return put(PdfName.DA, new PdfString(appearance));
    }

    public PdfString getDefaultAppearance() {
        return getPdfObject().getAsString(PdfName.DA);
    }

    public PdfAcroForm setDefaultQuadding(int quadding) {
        return put(PdfName.Q, new PdfNumber(quadding));
    }

    public PdfNumber getDefaultQuadding() {
        return getPdfObject().getAsNumber(PdfName.Q);
    }

    public PdfAcroForm setXFAResource(PdfStream xfaResource) {
        return put(PdfName.XFA, xfaResource);
    }

    public PdfAcroForm setXFAResource(PdfArray xfaResource) {
        return put(PdfName.XFA, xfaResource);
    }

    public PdfObject getXFAResource() {
        return getPdfObject().get(PdfName.XFA);
    }

    public PdfFormField getField(String fieldName) {
        List<PdfFormField> fields = getFormFields();
        for (PdfFormField field : fields) {
            if (field.getFieldName().toUnicodeString().equals(fieldName)) {
                return field;
            }
        }

        return null;
    }

    protected PdfArray getFields() {
        return getPdfObject().getAsArray(PdfName.Fields);
    }

    private ArrayList<PdfFormField> iterateFields(PdfArray array) {
        ArrayList<PdfFormField> fields = new ArrayList<>();

        for (PdfObject field : array) {
            PdfFormField formField = PdfFormField.makeFormField(field, getDocument());
            fields.add(formField);
            if (formField.getKids() != null) {
                fields.addAll(iterateFields(formField.getKids()));
            }
        }

        return fields;
    }

    private PdfDictionary processKids(PdfArray kids, PdfDictionary parent, PdfPage page){
        if (kids.size() == 1){
            PdfDictionary dict = (PdfDictionary) kids.get(0);
            PdfName type = dict.getAsName(PdfName.Subtype);
            if (type != null && type.equals(PdfName.Widget)){
                parent.mergeDifferent(dict);
                parent.remove(PdfName.Kids);
                parent.remove(PdfName.Parent);
                page.addAnnotation(PdfAnnotation.makeAnnotation(parent, getDocument()));
            } else {
                PdfArray otherKids = (dict).getAsArray(PdfName.Kids);
                if (otherKids != null) {
                    dict = processKids(otherKids, dict, page);
                }
                type = dict.getAsName(PdfName.Subtype);
                if (type != null && type.equals(PdfName.Widget)){
                    page.addAnnotation(PdfAnnotation.makeAnnotation(dict, getDocument()));
                }
            }
        } else {
            for (PdfObject kid : kids){
                PdfArray otherKids = ((PdfDictionary)kid).getAsArray(PdfName.Kids);
                if (otherKids != null) {
                    processKids(otherKids, (PdfDictionary) kid, page);
                }
            }
        }

        return parent;
    }
}
