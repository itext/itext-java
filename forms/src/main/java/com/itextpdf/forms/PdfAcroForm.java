package com.itextpdf.forms;

import com.itextpdf.basics.PdfException;
import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.core.pdf.*;
import com.itextpdf.core.pdf.annot.PdfAnnotation;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.core.pdf.xobject.PdfFormXObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PdfAcroForm extends PdfObjectWrapper<PdfDictionary> {

    /**
     * Signature flags
     */
    static public final int SIGNATURE_EXIST = 1;
    static public final int APPEND_ONLY = 2;

    protected boolean generateAppearance;
    protected HashMap<String, PdfFormField> fields = new HashMap<>();
    protected PdfDocument document;

    private static PdfName resourceNames[] = {PdfName.Font, PdfName.XObject, PdfName.ColorSpace, PdfName.Pattern};
    private PdfDictionary defaultResources;

    public PdfAcroForm(PdfDictionary pdfObject) {
        super(pdfObject);
        getFormFields();
    }

    public PdfAcroForm(PdfArray fields) {
        super(new PdfDictionary());
        put(PdfName.Fields, fields);
        getFormFields();
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
                acroForm = new PdfAcroForm(new PdfArray()).makeIndirect(document);
                document.getCatalog().put(PdfName.AcroForm, acroForm);
                acroForm.setDefaultAppearance("/Helv 0 Tf 0 g ");
            }
        } else {
            acroForm = new PdfAcroForm(acroFormDictionary);
        }

        acroForm.defaultResources = acroForm.getDefaultResources();
        if (acroForm.defaultResources == null) {
            acroForm.defaultResources = new PdfDictionary();
        }
        acroForm.document = document;

        return acroForm;
    }

    /**
     * This method adds the field to the last page in the document. If there's no pages, creates a new one.
     * @param field
     */
    public void addField(PdfFormField field) {
        PdfPage page;
        if (document.getNumOfPages() == 0){
            document.addNewPage();
        }
        page = document.getLastPage();
        addField(field, page);
    }

    /**
     * This method adds the field to the page.
     * @param field
     * @param page
     */
    public void addField(PdfFormField field, PdfPage page){
        PdfArray kids = field.getKids();

        PdfDictionary fieldDic = field.getPdfObject();
        if (kids != null){
            processKids(kids, fieldDic, page);
        }

        getFields().add(fieldDic);
        fields.put(field.getFieldName().toUnicodeString(), field);

        if (field.getFormType().equals(PdfName.Tx)) {
            List<PdfDictionary> resources = getResources(field.getPdfObject());
            for (PdfDictionary resDict : resources) {
                mergeResources(defaultResources, resDict, field);
            }
            if (defaultResources.size() != 0) {
                put(PdfName.DR, defaultResources);
            }
        }
    }

    public HashMap<String, PdfFormField> getFormFields() {
        if (fields.size() == 0) {
            fields = iterateFields(getFields());
        }
        return fields;
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

    public PdfAcroForm setDefaultJustification(int justification) {
        return put(PdfName.Q, new PdfNumber(justification));
    }

    public PdfNumber getDefaultJustification() {
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
        return fields.get(fieldName);
    }

    public boolean isGenerateAppearance() {
        return generateAppearance;
    }

    public void setGenerateAppearance(boolean generateAppearance) {
        if (generateAppearance) {
            getPdfObject().remove(PdfName.NeedAppearances);
        }
        this.generateAppearance = generateAppearance;
    }

    public void flatFields() {
        if (document.isAppendMode()) {
            throw new PdfException(PdfException.FieldFlatteningIsNotSupportedInAppendMode);
        }
        Map<String, PdfFormField> fields = getFormFields();
        PdfPage page = null;
        for (Map.Entry<String, PdfFormField> entry : fields.entrySet()) {
            PdfFormField field = entry.getValue();

            PdfDictionary pageDic = field.getPdfObject().getAsDictionary(PdfName.P);
            if (pageDic == null) {
                continue;
            }

            page = getPage(pageDic);

            PdfDictionary appDic = field.getPdfObject().getAsDictionary(PdfName.AP);
            PdfObject asNormal = null;
            if (appDic != null) {
                asNormal = appDic.getAsStream(PdfName.N);
                if (asNormal == null) {
                    asNormal = appDic.getAsDictionary(PdfName.N);
                }
            }
            if (generateAppearance) {
                if (appDic == null || asNormal == null) {
                    field.regenerateField();
                    appDic = field.getPdfObject().getAsDictionary(PdfName.AP);
                }
            }
            if (appDic != null) {
                PdfObject normal = appDic.get(PdfName.N);
                PdfFormXObject xObject = null;
                if (normal.isStream()) {
                    xObject = new PdfFormXObject((PdfStream) normal);
                } else if (normal.isDictionary()) {
                    PdfName as = field.getPdfObject().getAsName(PdfName.AS);
                    xObject = new PdfFormXObject(((PdfDictionary)normal).getAsStream(as)).makeIndirect(document);
                }

                if (xObject != null) {
                    Rectangle box = field.getPdfObject().getAsRectangle(PdfName.Rect);
                    PdfCanvas canvas = new PdfCanvas(page);
                    canvas.addXObject(xObject, box.getX(), box.getY());
                    PdfArray fFields = getFields();
                    fFields.remove(field.getPdfObject().getIndirectReference());
                    PdfArray annots = page.getPdfObject().getAsArray(PdfName.Annots);
                    annots.remove(field.getPdfObject().getIndirectReference());
                    PdfDictionary parent = field.getPdfObject().getAsDictionary(PdfName.Parent);
                    if (parent != null) {
                        PdfArray kids = parent.getAsArray(PdfName.Kids);
                        kids.remove(field.getPdfObject().getIndirectReference());
                        if (kids == null || kids.isEmpty()) {
                            fFields.remove(parent.getIndirectReference());
                        }
                    }
                }
            }
        }

        if (page != null && page.getPdfObject().getAsArray(PdfName.Annots).isEmpty()) {
            page.getPdfObject().remove(PdfName.Annots);
        }

        getPdfObject().remove(PdfName.NeedAppearances);
        if (getFields().isEmpty()) {
            document.getCatalog().getPdfObject().remove(PdfName.AcroForm);
        }
    }

    public boolean removeField(String fieldName) {
        PdfFormField field = getField(fieldName);
        if (field == null) {
            return false;
        }

        PdfDictionary pageDic = field.getPdfObject().getAsDictionary(PdfName.P);

        if (pageDic != null) {
            PdfPage page = getPage(pageDic);

            if (page != null) {
                PdfArray annots = page.getPdfObject().getAsArray(PdfName.Annots);
                if (annots != null) {
                    annots.remove(field.getPdfObject().getIndirectReference());
                }
            }
        }

        PdfDictionary parent = field.getParent();
        if (parent != null) {
            parent.getAsArray(PdfName.Kids).remove(field.getPdfObject().getIndirectReference());
            fields.remove(fieldName);
            return true;
        }

        if (getFields().remove(field.getPdfObject().getIndirectReference())) {
            fields.remove(fieldName);
            return true;
        }
        return false;
    }

    protected PdfArray getFields() {
        return getPdfObject().getAsArray(PdfName.Fields);
    }

    private HashMap<String, PdfFormField> iterateFields(PdfArray array) {
        HashMap<String, PdfFormField> fields = new HashMap<>();

        int index = 1;
        for (PdfObject field : array) {
            PdfFormField formField = PdfFormField.makeFormField(field, document);
            PdfString fieldName = formField.getFieldName();
            String name;
            if (fieldName == null) {
                name = formField.getParent().getAsString(PdfName.T).toUnicodeString() + "_" + index;
                index++;
            } else {
                name = fieldName.toUnicodeString();
            }
            fields.put(name, formField);
            if (formField.getKids() != null) {
                fields.putAll(iterateFields(formField.getKids()));
            }
        }

        return fields;
    }

    private PdfDictionary processKids(PdfArray kids, PdfDictionary parent, PdfPage page){
        if (kids.size() == 1){
            PdfDictionary dict = (PdfDictionary) kids.get(0);
            PdfName type = dict.getAsName(PdfName.Subtype);
            if (type != null && type.equals(PdfName.Widget)){
                parent.remove(PdfName.Kids);
                dict.remove(PdfName.Parent);
                parent.mergeDifferent(dict);
                page.addAnnotation(PdfAnnotation.makeAnnotation(parent, document));
            } else {
                PdfArray otherKids = (dict).getAsArray(PdfName.Kids);
                if (otherKids != null) {
                    dict = processKids(otherKids, dict, page);
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

    private List<PdfDictionary> getResources(PdfDictionary field) {
        List<PdfDictionary> resources = new ArrayList<>();

        PdfDictionary ap = field.getAsDictionary(PdfName.AP);
        if (ap != null) {
            PdfObject normal = ap.get(PdfName.N);
            if (normal != null) {
                if (normal.isDictionary()) {
                    for (PdfName key : ((PdfDictionary)normal).keySet()) {
                        PdfStream appearance = ((PdfDictionary)normal).getAsStream(key);
                        PdfDictionary resDict = appearance.getAsDictionary(PdfName.Resources);
                        if (resDict != null) {
                            resources.add(resDict);
                            break;
                        }
                    }
                } else if (normal.isStream()) {
                    PdfDictionary resDict = ((PdfStream)normal).getAsDictionary(PdfName.Resources);
                    if (resDict != null) {
                        resources.add(resDict);
                    }
                }
            }
        }

        PdfArray kids = field.getAsArray(PdfName.Kids);
        if (kids != null) {
            for (PdfObject kid : kids) {
                resources.addAll(getResources((PdfDictionary) kid));
            }
        }

        return resources;
    }

    public void mergeResources(PdfDictionary result, PdfDictionary source, PdfFormField field) {
        for (PdfName name : resourceNames) {
            PdfDictionary dic = source.getAsDictionary(name);
            PdfDictionary res = result.getAsDictionary(name);
            if (res == null) {
                res = new PdfDictionary();
            }
            if (dic != null) {
                res.mergeDifferent(dic);
                result.put(name, res);
            }
        }
    }

    private PdfPage getPage(PdfDictionary pageDic) {
        PdfPage page;
        for (int i = 1; i <= document.getNumOfPages(); i++) {
            page = document.getPage(i);
            if (page.getPdfObject() == pageDic) {
                return page;
            }
        }
        return null;
    }
}
