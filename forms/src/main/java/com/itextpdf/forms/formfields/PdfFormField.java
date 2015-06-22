package com.itextpdf.forms.formfields;

import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.core.pdf.*;
import com.itextpdf.core.pdf.action.PdfAction;
import com.itextpdf.core.pdf.annot.PdfAnnotation;
import com.itextpdf.core.pdf.annot.PdfWidgetAnnotation;

import java.util.ArrayList;
import java.util.List;


public class PdfFormField extends PdfObjectWrapper<PdfDictionary> {

    public PdfFormField(PdfDocument pdfDocument) {
        this(new PdfDictionary(), pdfDocument);
        put(PdfName.FT, getFormType());
    }

    public PdfFormField(PdfDocument pdfDocument, PdfWidgetAnnotation widget) {
        this(new PdfDictionary(), pdfDocument);
        addKid(widget);
        put(PdfName.FT, getFormType());
    }

    protected PdfFormField(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    protected PdfFormField(PdfDictionary pdfObject, PdfDocument pdfDocument) {
        super(pdfObject, pdfDocument);
    }

    /**
     * Makes a field flag by bit position. Bit positions are numbered 1 to 32.
     * But position 1 corresponds to flag 1, position 3 corresponds to flag 4 etc.
     *
     * @param bitPosition bit position of a flag in range 1 to 32 from the pdf specification.
     * @return corresponding field flag.
     */
    public static int makeFieldFlag(int bitPosition) {
        return (1 << (bitPosition - 1));
    }

    public static PdfFormField createEmptyField(PdfDocument doc, String name) {
        PdfFormField field = new PdfFormField(doc);
        field.setFieldName(name);
        return field;
    }

    public static PdfButtonFormField createButton(PdfDocument doc, Rectangle rect, int flags) {
        PdfWidgetAnnotation annot = new PdfWidgetAnnotation(doc, rect);
        PdfButtonFormField field = new PdfButtonFormField(doc, annot);
        field.setFieldFlags(flags);
        return field;
    }

    public static PdfButtonFormField createButton(PdfDocument doc, int flags) {
        PdfButtonFormField field = new PdfButtonFormField(doc);
        field.setFieldFlags(flags);
        return field;
    }

    public static PdfTextFormField createText(PdfDocument doc) {
        return new PdfTextFormField(doc);
    }

    public static PdfTextFormField createText(PdfDocument doc, Rectangle rect) {
        PdfTextFormField field;
        PdfWidgetAnnotation annot = new PdfWidgetAnnotation(doc, rect);
        field = new PdfTextFormField(doc, annot);

        return field;
    }

    public static PdfChoiceFormField createChoice(PdfDocument doc, int flags) {
        PdfChoiceFormField field = new PdfChoiceFormField(doc);
        field.setFieldFlags(flags);
        return field;
    }

    public static PdfChoiceFormField createChoice(PdfDocument doc, Rectangle rect, PdfArray options, int flags) {
        PdfWidgetAnnotation annot = new PdfWidgetAnnotation(doc, rect);
        PdfChoiceFormField field = new PdfChoiceFormField(doc, annot);
        field.put(PdfName.Opt, options);
        field.setFieldFlags(flags);
        return field;
    }

    public static PdfSignatureFormField createSignature(PdfDocument doc) {
        return new PdfSignatureFormField(doc);
    }

    public static PdfSignatureFormField createSignature(PdfDocument doc, Rectangle rect) {
        PdfWidgetAnnotation annot = new PdfWidgetAnnotation(doc, rect);
        return new PdfSignatureFormField(doc, annot);
    }

    public static PdfButtonFormField createRadioButton(PdfDocument doc, Rectangle rect) {
        return createButton(doc, rect, PdfButtonFormField.FF_RADIO);
    }

    public static PdfButtonFormField createPushButton(PdfDocument doc, Rectangle rect) {
        return createButton(doc, rect, PdfButtonFormField.FF_PUSH_BUTTON);
    }

    public static PdfButtonFormField createCheckBox(PdfDocument doc, Rectangle rect) {
        return createButton(doc, rect, 0);
    }

    public static PdfChoiceFormField createComboBox(PdfDocument doc, Rectangle rect, String options[][]) {
        return createChoice(doc, rect, processOptions(options), PdfChoiceFormField.FF_COMBO);
    }

    public static PdfChoiceFormField createComboBox(PdfDocument doc, Rectangle rect, String options[]) {
        return createChoice(doc, rect, processOptions(options), PdfChoiceFormField.FF_COMBO);
    }

    public static PdfChoiceFormField createList(PdfDocument doc, Rectangle rect, String options[][]) {
        return createChoice(doc, rect, processOptions(options), 0);
    }

    public static PdfChoiceFormField createList(PdfDocument doc, Rectangle rect, String options[]) {
        return createChoice(doc, rect, processOptions(options), 0);
    }

    public static <T extends PdfFormField> T makeFormField(PdfObject pdfObject, PdfDocument document) {
        T field = null;
        if (pdfObject.isIndirectReference())
            pdfObject = ((PdfIndirectReference) pdfObject).getRefersTo();
        if (pdfObject.isDictionary()) {
            PdfDictionary dictionary = (PdfDictionary) pdfObject;
            PdfName formType = dictionary.getAsName(PdfName.FT);
            if (PdfName.Tx.equals(formType))
                field = (T) new PdfTextFormField(dictionary, document);
            else if (PdfName.Btn.equals(formType))
                field = (T) new PdfButtonFormField(dictionary, document);
            else if (PdfName.Ch.equals(formType))
                field = (T) new PdfChoiceFormField(dictionary, document);
            else if (PdfName.Sig.equals(formType))
                field = (T) new PdfSignatureFormField(dictionary, document);
            else
                field = (T) new PdfFormField(dictionary, document);
        }

        return field;
    }

    public PdfName getFormType() {
        return null;
    }

    ;

    public <T extends PdfFormField> T setValue(PdfObject value) {
        return put(PdfName.V, value);
    }

    ;

    public <T extends PdfFormField> T setParent(PdfFormField parent) {
        return put(PdfName.Parent, parent);
    }

    public PdfDictionary getParent() {
        return getPdfObject().getAsDictionary(PdfName.Parent);
    }

    public PdfArray getKids() {
        return getPdfObject().getAsArray(PdfName.Kids);
    }

    public <T extends PdfFormField> T addKid(PdfFormField kid) {
        kid.setParent(this);
        PdfArray kids = getKids();
        if (kids == null) {
            kids = new PdfArray();
        }
        kids.add(kid.getPdfObject());
        return put(PdfName.Kids, kids);
    }

    public <T extends PdfFormField> T addKid(PdfWidgetAnnotation kid) {
        kid.setParent(getPdfObject());
        PdfArray kids = getKids();
        if (kids == null) {
            kids = new PdfArray();
        }
        kids.add(kid.getPdfObject());
        return put(PdfName.Kids, kids);
    }

    public <T extends PdfFormField> T setFieldName(String name) {
        return put(PdfName.T, new PdfString(name));
    }

    public PdfString getFieldName() {
        return getPdfObject().getAsString(PdfName.T);
    }

    public <T extends PdfFormField> T setAlternativeName(String name) {
        return put(PdfName.TU, new PdfString(name));
    }

    public PdfString getAlternativeName() {
        return getPdfObject().getAsString(PdfName.TU);
    }

    public <T extends PdfFormField> T setMappingName(String name) {
        return put(PdfName.TM, new PdfString(name));
    }

    public PdfString getMappingName() {
        return getPdfObject().getAsString(PdfName.TM);
    }

    public boolean getFieldFlag(int flag) {
        return (getFieldFlags() & flag) != 0;
    }

    public <T extends PdfFormField> T setFieldFlag(int flag) {
        return setFieldFlag(flag, true);
    }

    public <T extends PdfFormField> T setFieldFlag(int flag, boolean value) {
        int flags = getFieldFlags();

        if (value) {
            flags |= flag;
        } else {
            flags &= ~flag;
        }

        return setFieldFlags(flags);
    }

    public <T extends PdfFormField> T setFieldFlags(int flags) {
        return put(PdfName.Ff, new PdfNumber(flags));
    }

    public int getFieldFlags() {
        PdfNumber f = getPdfObject().getAsNumber(PdfName.Ff);
        if (f != null)
            return f.getIntValue();
        else
            return 0;
    }

    public PdfObject getValue() {
        return getPdfObject().get(PdfName.V);
    }

    public <T extends PdfFormField> T setDefaultValue(PdfObject value) {
        return put(PdfName.DV, value);
    }

    public PdfObject getDefaultValue() {
        return getPdfObject().get(PdfName.DV);
    }

    public <T extends PdfFormField> T setAdditionalAction(PdfName key, PdfAction action) {
        PdfAction.setAdditionalAction(this, key, action);
        return (T) this;
    }

    public PdfDictionary getAdditionalAction() {
        return getPdfObject().getAsDictionary(PdfName.AA);
    }

    public <T extends PdfFormField> T setOptions(PdfArray options) {
        return put(PdfName.Opt, options);
    }

    public PdfArray getOptions() {
        return getPdfObject().getAsArray(PdfName.Opt);
    }

    public List<PdfWidgetAnnotation> getWidgets() {
        List<PdfWidgetAnnotation> widgets = new ArrayList<>();

        PdfName subType = getPdfObject().getAsName(PdfName.Subtype);
        if (subType != null && subType.equals(PdfName.Widget)) {
            widgets.add((PdfWidgetAnnotation) PdfAnnotation.makeAnnotation(getPdfObject(), getDocument()));
        }

        PdfArray kids = getKids();
        if (kids != null) {
            for (PdfObject kid : kids) {
                subType = getPdfObject().getAsName(PdfName.Subtype);
                if (subType != null && subType.equals(PdfName.Widget)) {
                    widgets.add((PdfWidgetAnnotation) PdfAnnotation.makeAnnotation(kid, getDocument()));
                }
            }
        }

        return widgets;
    }

    protected static PdfArray processOptions(String options[][]) {
        PdfArray array = new PdfArray();
        for (String option[] : options) {
            String subOption[] = option;
            PdfArray subArray = new PdfArray(new PdfString(subOption[0]));
            subArray.add(new PdfString(subOption[1]));
            array.add(subArray);
        }
        return array;
    }

    protected static PdfArray processOptions(String options[]) {
        PdfArray array = new PdfArray();
        for (String option : options) {
            array.add(new PdfString(option));
        }
        return array;
    }
}
