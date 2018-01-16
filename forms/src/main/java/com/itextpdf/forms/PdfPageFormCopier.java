/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2018 iText Group NV
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

import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.io.util.MessageFormatUtil;
import java.util.*;

/**
 * A sample implementation of the {#link IPdfPageExtraCopier} interface which
 * copies only AcroForm fields to a new page.
 *
 *
 * NOTE: While it's absolutely not necessary to use the same PdfPageFormCopier instance for copying operations,
 * it is still worth to know that PdfPageFormCopier uses some caching logic which can potentially improve performance
 * in case of the reusing of the same instance.
 */
public class PdfPageFormCopier implements IPdfPageExtraCopier {

    PdfAcroForm formFrom;
    PdfAcroForm formTo;
    PdfDocument documentFrom;
    PdfDocument documentTo;
    Logger logger = LoggerFactory.getLogger(PdfPageFormCopier.class);

    @Override
    public void copy(PdfPage fromPage, PdfPage toPage) {
        if (documentFrom != fromPage.getDocument()) {
            documentFrom = fromPage.getDocument();
            formFrom = PdfAcroForm.getAcroForm(documentFrom, false);
        }
        if (documentTo != toPage.getDocument()) {
            documentTo = toPage.getDocument();
            formTo = PdfAcroForm.getAcroForm(documentTo, true);
        }
        if (formFrom != null) {
            //duplicate AcroForm dictionary
            List<PdfName> excludedKeys = new ArrayList<>();
            excludedKeys.add(PdfName.Fields);
            excludedKeys.add(PdfName.DR);
            PdfDictionary dict = formFrom.getPdfObject().copyTo(documentTo, excludedKeys, false);
            formTo.getPdfObject().mergeDifferent(dict);
        }

        if (formFrom != null) {
            Map<String, PdfFormField> fieldsFrom = formFrom.getFormFields();
            if (fieldsFrom.size() > 0) {
                Map<String, PdfFormField> fieldsTo = formTo.getFormFields();
                List<PdfAnnotation> annots = toPage.getAnnotations();
                for (PdfAnnotation annot : annots) {
                    if (annot.getSubtype().equals(PdfName.Widget)) {
                        PdfDictionary parent = annot.getPdfObject().getAsDictionary(PdfName.Parent);
                        if (parent != null) {
                            PdfFormField parentField = getParentField(parent, documentTo);
                            PdfString parentName = parentField.getFieldName();
                            if (parentName == null) {
                                continue;
                            }
                            if (!fieldsTo.containsKey(parentName.toUnicodeString())) {
                                PdfFormField field = createParentFieldCopy(annot.getPdfObject(), documentTo);
                                PdfArray kids  = field.getKids();
                                field.getPdfObject().remove(PdfName.Kids);
                                formTo.addField(field, toPage);
                                field.getPdfObject().put(PdfName.Kids, kids);
                            } else {
                                PdfFormField field = PdfFormField.makeFormField(annot.getPdfObject(), documentTo);
                                PdfString fieldName = field.getFieldName();
                                if (fieldName != null) {
                                    PdfFormField existingField = fieldsTo.get(fieldName.toUnicodeString());
                                    if (existingField != null) {
                                        PdfFormField clonedField = PdfFormField.makeFormField(field.getPdfObject().clone().makeIndirect(documentTo), documentTo);
                                        toPage.getPdfObject().getAsArray(PdfName.Annots).add(clonedField.getPdfObject());
                                        toPage.removeAnnotation(annot);
                                        mergeFieldsWithTheSameName(clonedField);
                                    } else {
                                        HashSet<String> existingFields = new HashSet<>();
                                        getAllFieldNames(formTo.getFields(), existingFields);
                                        addChildToExistingParent(annot.getPdfObject(), existingFields);
                                    }
                                } else {
                                    if (!parentField.getKids().contains(field.getPdfObject())) {
                                        HashSet<String> existingFields = new HashSet<>();
                                        getAllFieldNames(formTo.getFields(), existingFields);
                                        addChildToExistingParent(annot.getPdfObject(), existingFields);
                                    }
                                }
                            }
                        } else {
                            PdfString annotName = annot.getPdfObject().getAsString(PdfName.T);
                            String annotNameString = null;
                            if (annotName != null) {
                                annotNameString = annotName.toUnicodeString();
                            }
                            if (annotNameString != null && fieldsFrom.containsKey(annotNameString)) {
                                PdfFormField field = fieldsTo.get(annotNameString);
                                if (field != null) {
                                    PdfDictionary clonedAnnot = (PdfDictionary) annot.getPdfObject().clone().makeIndirect(documentTo);
                                    toPage.getPdfObject().getAsArray(PdfName.Annots).add(clonedAnnot);
                                    toPage.removeAnnotation(annot);
                                    field = mergeFieldsWithTheSameName(PdfFormField.makeFormField(clonedAnnot, toPage.getDocument()));

                                    logger.warn(MessageFormatUtil.format(LogMessageConstant.DOCUMENT_ALREADY_HAS_FIELD, annotNameString));
                                    PdfArray kids  = field.getKids();
                                    if (kids != null) {
                                        field.getPdfObject().remove(PdfName.Kids);
                                        formTo.addField(field, toPage);
                                        field.getPdfObject().put(PdfName.Kids, kids);
                                    } else {
                                        formTo.addField(field, toPage);
                                    }
                                } else {
                                    formTo.addField(PdfFormField.makeFormField(annot.getPdfObject(), documentTo), null);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private PdfFormField mergeFieldsWithTheSameName(PdfFormField newField) {
        String fullFieldName = newField.getFieldName().toUnicodeString();
        PdfString fieldName = newField.getPdfObject().getAsString(PdfName.T);

        PdfFormField existingField = formTo.getField(fullFieldName);
        if (existingField.isFlushed()) {
            int index = 0;
            do {
                index++;
                newField.setFieldName(fieldName.toUnicodeString() + "_#" + index);
                fullFieldName = newField.getFieldName().toUnicodeString();
            } while(formTo.getField(fullFieldName) != null);
            return newField;
        }
        newField.getPdfObject().remove(PdfName.T);
        newField.getPdfObject().remove(PdfName.P);
        PdfArray kids = existingField.getKids();
        if (kids != null && !kids.isEmpty()) {
            existingField.addKid(newField);
            return existingField;
        }

        existingField.getPdfObject().remove(PdfName.T);
        existingField.getPdfObject().remove(PdfName.P);
        formTo.getFields().remove(existingField.getPdfObject());
        PdfFormField mergedField = PdfFormField.createEmptyField(documentTo);
        mergedField.
                put(PdfName.FT, existingField.getFormType()).
                put(PdfName.T, fieldName);
        PdfDictionary parent = existingField.getParent();
        if (parent != null) {
            mergedField.put(PdfName.Parent, parent);
            PdfArray parentKids = parent.getAsArray(PdfName.Kids);
            for (int i = 0; i < parentKids.size(); i++) {
                PdfObject obj = parentKids.get(i);
                if (obj == existingField.getPdfObject()) {
                    parentKids.set(i, mergedField.getPdfObject());
                    break;
                }
            }
        }
        kids = existingField.getKids();
        if (kids != null) {
            mergedField.put(PdfName.Kids, kids);
        }

        mergedField.addKid(existingField).addKid(newField);
        PdfObject value = existingField.getValue();
        if (value != null) {
            mergedField.put(PdfName.V, existingField.getPdfObject().get(PdfName.V));
        }

        return mergedField;
    }

    private PdfFormField getParentField(PdfDictionary parent, PdfDocument pdfDoc) {
        PdfFormField parentField = PdfFormField.makeFormField(parent, pdfDoc);

        PdfDictionary parentOfParent = parentField.getParent();
        if (parentOfParent != null) {
            parentField = getParentField(parentOfParent, pdfDoc);
        }

        return parentField;
    }

    private PdfFormField createParentFieldCopy(PdfDictionary fieldDic, PdfDocument pdfDoc) {
        fieldDic.remove(PdfName.Kids);

        PdfDictionary parent = fieldDic.getAsDictionary(PdfName.Parent);
        PdfFormField field = PdfFormField.makeFormField(fieldDic, pdfDoc);

        if (parent != null) {
            field = createParentFieldCopy(parent, pdfDoc);
            parent.put(PdfName.Kids, new PdfArray(fieldDic));
        }

        return field;
    }

    private void addChildToExistingParent(PdfDictionary fieldDic, Set<String> existingFields) {
        PdfDictionary parent = fieldDic.getAsDictionary(PdfName.Parent);
        PdfString parentName = parent.getAsString(PdfName.T);
        if (parentName != null) {
            String name = parentName.toUnicodeString();
            if (existingFields.contains(name)) {
                PdfArray kids = parent.getAsArray(PdfName.Kids);
                kids.add(fieldDic);
            } else {
                parent.put(PdfName.Kids, new PdfArray(fieldDic));
                addChildToExistingParent(parent, existingFields);
            }
        }
    }

    private void getAllFieldNames(PdfArray fields, Set<String> existingFields) {
        for (PdfObject field : fields) {
            if (field.isFlushed()) {
                continue;
            }
            PdfDictionary dic = (PdfDictionary) field;
            PdfString name = dic.getAsString(PdfName.T);
            if (name != null) {
                existingFields.add(name.toUnicodeString());
            }
            PdfArray kids = dic.getAsArray(PdfName.Kids);
            if (kids != null) {
                getAllFieldNames(kids, existingFields);
            }
        }
    }
}
