package com.itextpdf.forms;

import com.itextpdf.basics.PdfException;
import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.basics.geom.Rectangle;
import com.itextpdf.core.pdf.*;
import com.itextpdf.core.pdf.annot.PdfAnnotation;
import com.itextpdf.core.pdf.annot.PdfWidgetAnnotation;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.core.pdf.xobject.PdfFormXObject;

import java.util.*;

public class PdfAcroForm extends PdfObjectWrapper<PdfDictionary> {

    /**
     * Signature flags
     */
    static public final int SIGNATURE_EXIST = 1;
    static public final int APPEND_ONLY = 2;

    protected boolean generateAppearance;
    protected LinkedHashMap<String, PdfFormField> fields = new LinkedHashMap<>();
    protected PdfDocument document;

    private static PdfName resourceNames[] = {PdfName.Font, PdfName.XObject, PdfName.ColorSpace, PdfName.Pattern};
    private PdfDictionary defaultResources;
    private LinkedHashSet<PdfFormField> fieldsForFlattening = new LinkedHashSet<>();

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

        if (acroForm != null) {
            acroForm.defaultResources = acroForm.getDefaultResources();
            if (acroForm.defaultResources == null) {
                acroForm.defaultResources = new PdfDictionary();
            }
            acroForm.document = document;
        }

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

        if (field.getFormType() != null && field.getFormType().equals(PdfName.Tx)) {
            List<PdfDictionary> resources = getResources(field.getPdfObject());
            for (PdfDictionary resDict : resources) {
                mergeResources(defaultResources, resDict, field);
            }
            if (defaultResources.size() != 0) {
                put(PdfName.DR, defaultResources);
            }
        }
    }

    /**
     * This method merges field with its annotation and place it on the given page. This method won't work if the field
     * has no or more than one widget annotations.
     * @param field to be placed.
     * @param page where the field will be placed.
     */
    public void addFieldAppearanceToPage(PdfFormField field, PdfPage page) {
        PdfDictionary fieldDict = field.getPdfObject();
        PdfArray kids = field.getKids();
        if (kids == null || kids.size() > 1) {
            return;
        }

        PdfDictionary kidDict = (PdfDictionary) kids.get(0);
        PdfName type = kidDict.getAsName(PdfName.Subtype);
        if (type != null && type.equals(PdfName.Widget)) {
            fieldDict.remove(PdfName.Kids);
            kidDict.remove(PdfName.Parent);
            fieldDict.mergeDifferent(kidDict);
            PdfAnnotation annot = PdfAnnotation.makeAnnotation(fieldDict, document);
            PdfDictionary pageDic = annot.getPdfObject().getAsDictionary(PdfName.P);
            if (pageDic != null) {
                PdfArray array = pageDic.getAsArray(PdfName.Annots);
                if (array == null) {
                    array = new PdfArray();
                    pageDic.put(PdfName.Annots, array);
                }
                array.add(fieldDict);
            } else {
                page.addAnnotation(annot);
            }
        }
    }

    public LinkedHashMap<String, PdfFormField> getFormFields() {
        if (fields.isEmpty()) {
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
        LinkedHashSet<PdfFormField> fields;
        if (fieldsForFlattening.isEmpty()) {
            fields = new LinkedHashSet<>(getFormFields().values());
        } else {
            fields = new LinkedHashSet<>();
            for (PdfFormField field : fieldsForFlattening) {
                fields.addAll(prepareFieldsForFlattening(field));
            }
        }

        PdfPage page;
        for (PdfFormField field : fields) {
            page = getFieldPage(field.getPdfObject());
            if (page == null) {
                continue;
            }

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
                    if (page.isFlushed()) {
                        throw new PdfException(PdfException.PageWasAlreadyFlushedUseAddFieldAppearanceToPageMethodInstead);
                    }
                    PdfCanvas canvas = new PdfCanvas(page);
                    canvas.addXObject(xObject, box.getX(), box.getY());
                    PdfArray fFields = getFields();
                    fFields.remove(field.getPdfObject().getIndirectReference());
                    PdfArray annots = page.getPdfObject().getAsArray(PdfName.Annots);
                    annots.remove(field.getPdfObject().getIndirectReference());
                    if (annots.isEmpty()) {
                        page.getPdfObject().remove(PdfName.Annots);
                    }
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

        getPdfObject().remove(PdfName.NeedAppearances);
        if (fieldsForFlattening.isEmpty()) {
            getFields().clear();
        }
        if (getFields().isEmpty()) {
            document.getCatalog().getPdfObject().remove(PdfName.AcroForm);
        }
    }

    public boolean removeField(String fieldName) {
        PdfFormField field = getField(fieldName);
        if (field == null) {
            return false;
        }

        PdfPage page = getFieldPage(field.getPdfObject());

        if (page != null) {
            PdfArray annots = page.getPdfObject().getAsArray(PdfName.Annots);
            if (annots != null) {
                annots.remove(field.getPdfObject().getIndirectReference());
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

    public void partialFormFlattening(String fieldName) {
        PdfFormField field = getFormFields().get(fieldName);
        if (field != null) {
            fieldsForFlattening.add(field);
        }
    }

    public void renameField(String oldName, String newName) {
        LinkedHashMap<String, PdfFormField> fields = getFormFields();
        if (fields.containsKey(newName)) {
            return;
        }
        PdfFormField field = fields.get(oldName);
        if (field != null) {
            field.setFieldName(newName);
            fields.remove(oldName);
            fields.put(newName, field);
        }
    }

    protected PdfArray getFields() {
        return getPdfObject().getAsArray(PdfName.Fields);
    }

    private LinkedHashMap<String, PdfFormField> iterateFields(PdfArray array) {
        LinkedHashMap<String, PdfFormField> fields = new LinkedHashMap<>();

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
                PdfAnnotation annot = PdfAnnotation.makeAnnotation(parent, document);
                PdfDictionary pageDic = annot.getPdfObject().getAsDictionary(PdfName.P);
                if (pageDic != null) {
                    if (pageDic.isFlushed()) {
                        throw new PdfException(PdfException.PageWasAlreadyFlushedUseAddFieldAppearanceToPageMethodInstead);
                    }
                    PdfArray array = pageDic.getAsArray(PdfName.Annots);
                    if (array == null) {
                        array = new PdfArray();
                        pageDic.put(PdfName.Annots, array);
                    }
                    array.add(parent);
                } else {
                    page.addAnnotation(annot);
                }
            } else {
                PdfArray otherKids = (dict).getAsArray(PdfName.Kids);
                if (otherKids != null) {
                    processKids(otherKids, dict, page);
                }
            }
        } else {
            for (PdfObject kid : kids){
                if (kid.isIndirectReference()) {
                    kid = ((PdfIndirectReference)kid).getRefersTo();
                }
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

    private PdfPage getFieldPage(PdfDictionary annotation) {
        PdfDictionary pageDic = annotation.getAsDictionary(PdfName.P);
        if (pageDic != null) {
            return getPage(pageDic);
        }
        for (int i = 1; i <= document.getNumOfPages(); i++) {
            PdfPage page = document.getPage(i);
            if (!page.isFlushed()) {
                PdfArray annotations = page.getPdfObject().getAsArray(PdfName.Annots);
                if (annotations != null && annotations.contains(annotation.getIndirectReference())){
                    return page;
                }
            }
        }
        return null;
    }

    private LinkedHashSet<PdfFormField> prepareFieldsForFlattening(PdfFormField field) {
        LinkedHashSet<PdfFormField> preparedFields = new LinkedHashSet<>();
        preparedFields.add(field);
        PdfArray kids = field.getKids();
        if (kids != null) {
            for (PdfObject kid : kids) {
                PdfDictionary fieldDict;
                if (kid.isIndirectReference()) {
                    fieldDict = (PdfDictionary) ((PdfIndirectReference)kid).getRefersTo();
                } else {
                    fieldDict = (PdfDictionary) kid;
                }
                PdfFormField kidField = new PdfFormField(fieldDict);
                preparedFields.add(kidField);
                if (kidField.getKids() != null) {
                    preparedFields.addAll(prepareFieldsForFlattening(kidField));
                }
            }
        }
        return preparedFields;
    }
}
