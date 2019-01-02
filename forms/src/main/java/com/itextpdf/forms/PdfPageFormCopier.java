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

import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.kernel.pdf.IPdfPageExtraCopier;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A sample implementation of the {#link IPdfPageExtraCopier} interface which
 * copies only AcroForm fields to a new page.
 * <p>
 * <p>
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

        if (formFrom == null) {
            return;
        }

        //duplicate AcroForm dictionary
        List<PdfName> excludedKeys = new ArrayList<>();
        excludedKeys.add(PdfName.Fields);
        excludedKeys.add(PdfName.DR);

        PdfDictionary dict = formFrom.getPdfObject().copyTo(documentTo, excludedKeys, false);
        formTo.getPdfObject().mergeDifferent(dict);

        Map<String, PdfFormField> fieldsFrom = formFrom.getFormFields();
        if (fieldsFrom.size() <= 0) {
            return;
        }
        Map<String, PdfFormField> fieldsTo = formTo.getFormFields();

        List<PdfAnnotation> annots = toPage.getAnnotations();

        for (PdfAnnotation annot : annots) {
            if (!annot.getSubtype().equals(PdfName.Widget)) {
                continue;
            }
            copyField(toPage, fieldsFrom, fieldsTo, annot);
        }
    }

    private void copyField(PdfPage toPage, Map<String, PdfFormField> fieldsFrom,
                           Map<String, PdfFormField> fieldsTo, PdfAnnotation currentAnnot) {
        PdfDictionary parent = currentAnnot.getPdfObject().getAsDictionary(PdfName.Parent);
        if (parent != null) {
            PdfFormField parentField = getParentField(parent, documentTo);
            PdfString parentName = parentField.getFieldName();
            if (parentName == null) {
                return;
            }
            copyParentFormField(toPage, fieldsTo, currentAnnot, parentField);
        } else {
            PdfString annotName = currentAnnot.getPdfObject().getAsString(PdfName.T);
            String annotNameString = null;
            if (annotName != null) {
                annotNameString = annotName.toUnicodeString();
            }
            if (annotNameString != null && fieldsFrom.containsKey(annotNameString)) {
                PdfFormField field = fieldsTo.get(annotNameString);
                if (field == null) {
                    formTo.addField(PdfFormField.makeFormField(currentAnnot.getPdfObject(), documentTo), null);
                } else {
                    copyExistingField(toPage, currentAnnot);
                }
            }
        }
    }

    private void copyExistingField(PdfPage toPage, PdfAnnotation currentAnnot) {
        PdfFormField field = mergeFieldsWithTheSameName(PdfFormField.makeFormField(currentAnnot.getPdfObject(), toPage.getDocument()));

        PdfArray kids = field.getKids();
        if (kids != null) {
            field.getPdfObject().remove(PdfName.Kids);
            formTo.addField(field, toPage);
            field.getPdfObject().put(PdfName.Kids, kids);
        } else {
            formTo.addField(field, toPage);
        }
    }

    private void copyParentFormField(PdfPage toPage, Map<String, PdfFormField> fieldsTo,
                                     PdfAnnotation annot, PdfFormField parentField) {
        PdfString parentName = parentField.getFieldName();
        if (!fieldsTo.containsKey(parentName.toUnicodeString())) {
            PdfFormField field = createParentFieldCopy(annot.getPdfObject(), documentTo);
            PdfArray kids = field.getKids();
            field.getPdfObject().remove(PdfName.Kids);
            formTo.addField(field, toPage);
            field.getPdfObject().put(PdfName.Kids, kids);
        } else {
            PdfFormField field = PdfFormField.makeFormField(annot.getPdfObject(), documentTo);
            PdfString fieldName = field.getFieldName();
            if (fieldName != null) {
                PdfFormField existingField = fieldsTo.get(fieldName.toUnicodeString());
                if (existingField != null) {
                    PdfFormField mergedField = mergeFieldsWithTheSameName(field);
                    formTo.getFormFields().put(mergedField.getFieldName().toUnicodeString(), mergedField);
                } else {
                    HashSet<String> existingFields = new HashSet<>();
                    getAllFieldNames(formTo.getFields(), existingFields);
                    addChildToExistingParent(annot.getPdfObject(), existingFields,
                            fieldsTo, toPage, annot);
                }
            } else {
                if (!parentField.getKids().contains(field.getPdfObject())) {
                    HashSet<String> existingFields = new HashSet<>();
                    getAllFieldNames(formTo.getFields(), existingFields);
                    addChildToExistingParent(annot.getPdfObject(), existingFields);
                }
            }
        }
    }

    private PdfFormField mergeFieldsWithTheSameName(PdfFormField newField) {
        String fullFieldName = newField.getFieldName().toUnicodeString();
        PdfString fieldName = newField.getPdfObject().getAsString(PdfName.T);

        logger.warn(MessageFormatUtil.format(LogMessageConstant.DOCUMENT_ALREADY_HAS_FIELD, fullFieldName));

        PdfFormField existingField = formTo.getField(fullFieldName);
        if (existingField.isFlushed()) {
            int index = 0;
            do {
                index++;
                newField.setFieldName(fieldName.toUnicodeString() + "_#" + index);
                fullFieldName = newField.getFieldName().toUnicodeString();
            } while (formTo.getField(fullFieldName) != null);
            return newField;
        }
        newField.getPdfObject().remove(PdfName.T);
        newField.getPdfObject().remove(PdfName.P);

        formTo.getFields().remove(existingField.getPdfObject());

        PdfArray kids = existingField.getKids();
        if (kids != null && !kids.isEmpty()) {
            existingField.addKid(newField);
            return existingField;
        }

        existingField.getPdfObject().remove(PdfName.T);
        existingField.getPdfObject().remove(PdfName.P);
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
        PdfDictionary parent = fieldDic.getAsDictionary(PdfName.Parent);
        PdfFormField field = PdfFormField.makeFormField(fieldDic, pdfDoc);

        if (parent != null) {
            field = createParentFieldCopy(parent, pdfDoc);
            PdfArray kids = (PdfArray) parent.get(PdfName.Kids);
            if (kids == null) {
                parent.put(PdfName.Kids, new PdfArray(fieldDic));
            } else {
                kids.add(fieldDic);
            }
        }

        return field;
    }

    private void addChildToExistingParent(PdfDictionary fieldDic, Set<String> existingFields) {
        PdfDictionary parent = fieldDic.getAsDictionary(PdfName.Parent);
        if (parent == null) {
            return;
        }

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

    private void addChildToExistingParent(PdfDictionary fieldDic, Set<String> existingFields,
                                          Map<String, PdfFormField> fieldsTo,
                                          PdfPage toPage, PdfAnnotation annot) {
        PdfDictionary parent = fieldDic.getAsDictionary(PdfName.Parent);
        if (parent == null) {
            return;
        }

        PdfString parentName = parent.getAsString(PdfName.T);
        if (parentName != null) {
            String name = parentName.toUnicodeString();
            if (existingFields.contains(name)) {
                PdfArray kids = parent.getAsArray(PdfName.Kids);
                for (PdfObject kid : kids) {
                    if (((PdfDictionary) kid).get(PdfName.T).equals(fieldDic.get(PdfName.T))) {
                        PdfFormField kidField = PdfFormField.makeFormField(kid, documentTo);
                        fieldsTo.put(kidField.getFieldName().toUnicodeString(), kidField);

                        PdfFormField mergedField = mergeFieldsWithTheSameName(PdfFormField.makeFormField(fieldDic, documentTo));
                        formTo.getFormFields().put(mergedField.getFieldName().toUnicodeString(), mergedField);
                        return;
                    }
                }
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
