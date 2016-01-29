package com.itextpdf.forms;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.core.pdf.IPdfPageExtraCopier;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfPage;
import com.itextpdf.core.pdf.PdfString;
import com.itextpdf.core.pdf.annot.PdfAnnotation;
import com.itextpdf.core.pdf.annot.PdfWidgetAnnotation;
import com.itextpdf.forms.fields.PdfFormField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A sample implementation of the {#link IPdfPageExtraCopier} interface which
 * copies only AcroForm fields to a new page.
 */
public class PdfPageFormCopier implements IPdfPageExtraCopier {

    PdfAcroForm formFrom;
    PdfAcroForm formTo;
    PdfDocument documentFrom;
    PdfDocument documentTo;

    @Override
    public void copy(PdfPage fromPage, PdfPage toPage) {
        if (documentFrom != fromPage.getDocument()) {
            documentFrom = fromPage.getDocument();
            formFrom = PdfAcroForm.getAcroForm(documentFrom, false);
        }
        if (documentTo != toPage.getDocument()) {
            documentTo = toPage.getDocument();
            formTo = PdfAcroForm.getAcroForm(documentTo, true);
            if (formFrom != null) {
                //duplicate AcroForm dictionary
                List<PdfName> excludedKeys = new ArrayList<>();
                excludedKeys.add(PdfName.Fields);
                excludedKeys.add(PdfName.DR);
                PdfDictionary dict = formFrom.getPdfObject().copyToDocument(documentTo, excludedKeys, false);
                formTo.getPdfObject().mergeDifferent(dict);
            }
        }

        List<PdfDictionary> usedParents = new ArrayList<>();
        if (formFrom != null) {
            Map<String, PdfFormField> fieldsFrom = formFrom.getFormFields();
            if (!fieldsFrom.isEmpty()) {
                Map<String, PdfFormField> fieldsTo = formTo.getFormFields();
                List<PdfAnnotation> annots = toPage.getAnnotations();
                for (PdfAnnotation annot : annots) {
                    if (annot.getSubtype().equals(PdfName.Widget)) {
                        PdfDictionary parent = annot.getPdfObject().getAsDictionary(PdfName.Parent);
                        if (parent != null) {
                            PdfString parentName = parent.getAsString(PdfName.T);
                            if (parentName == null) {
                                continue;
                            }
                            if (!usedParents.contains(parent)) {
                                PdfFormField field = PdfFormField.makeFormField(parent, toPage.getDocument());
                                field.getKids().clear();
                                formTo.addField(field, toPage);
                                usedParents.add(parent);
                                field.addKid((PdfWidgetAnnotation) annot);
                            } else {
                                parent.getAsArray(PdfName.Kids).add(annot.getPdfObject());
                            }
                        } else {
                            PdfString annotName = annot.getPdfObject().getAsString(PdfName.T);
                            String annotNameString = null;
                            if (annotName != null) {
                                annotNameString = annotName.toUnicodeString();
                            }
                            if (annotNameString != null && fieldsFrom.containsKey(annotNameString)) {
                                PdfFormField field = PdfFormField.makeFormField(annot.getPdfObject(), toPage.getDocument());
                                if (fieldsTo.containsKey(annotNameString)) {
                                    field = mergeFieldsWithTheSameName(field, fieldsTo.get(annotNameString));
                                    Logger logger = LoggerFactory.getLogger(PdfPageFormCopier.class);
                                    logger.warn(MessageFormat.format(LogMessageConstant.DOCUMENT_ALREADY_HAS_FIELD, annotNameString));
                                }
                                formTo.addField(field, null);
                            }
                        }
                    }
                }
            }
        }
    }

    private PdfFormField mergeFieldsWithTheSameName(PdfFormField existingField, PdfFormField newField) {
        PdfFormField mergedField = PdfFormField.createEmptyField(documentTo);
        formTo.removeField(existingField.getFieldName().toUnicodeString());
        mergedField.
                put(PdfName.FT, existingField.getFormType()).
                put(PdfName.T, existingField.getFieldName()).
                put(PdfName.Parent, existingField.getParent()).
                put(PdfName.Kids, existingField.getKids());

        existingField.remove(PdfName.T);
        newField.remove(PdfName.T);
        mergedField.addKid(existingField).addKid(newField);

        return mergedField;
    }

}
