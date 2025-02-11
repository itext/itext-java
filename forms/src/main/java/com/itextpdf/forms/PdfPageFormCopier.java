/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.forms;

import com.itextpdf.forms.fields.PdfFormCreator;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.forms.fields.AbstractPdfFormField;
import com.itextpdf.forms.logs.FormsLogMessageConstants;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.IPdfPageFormCopier;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;

import java.util.LinkedHashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A sample implementation of the {#link IPdfPageExtraCopier} interface which
 * copies only AcroForm fields to a new page.
 *
 * <p>
 * NOTE: While it's absolutely not necessary to use the same PdfPageFormCopier instance for copying operations,
 * it is still worth to know that PdfPageFormCopier uses some caching logic which can potentially improve performance
 * in case of the reusing of the same instance.
 */
public class PdfPageFormCopier implements IPdfPageFormCopier {

    private PdfAcroForm formFrom;
    private PdfAcroForm formTo;
    private PdfDocument documentFrom;
    private PdfDocument documentTo;

    private final Set<PdfObject> collectedFieldObjects = new LinkedHashSet<PdfObject>();

    private static Logger logger = LoggerFactory.getLogger(PdfPageFormCopier.class);

    @Override
    public void copy(PdfPage fromPage, PdfPage toPage) {
        if (documentFrom != fromPage.getDocument()) {
            documentFrom = fromPage.getDocument();
            formFrom = PdfFormCreator.getAcroForm(documentFrom, false);
        }
        if (documentTo != toPage.getDocument()) {
            documentTo = toPage.getDocument();
        }
        // We should always regenerate the acroform if we expect the same result when the old or new
        // PdfPageFormCopier instance is used because getAcroForm changes the fields structure,
        // e.g. removes wrong field keys from the pure widget annotations dictionaries.
        formTo = PdfFormCreator.getAcroForm(documentTo, true);

        if (formFrom == null) {
            return;
        }

        //duplicate AcroForm dictionary
        List<PdfName> excludedKeys = new ArrayList<>();
        excludedKeys.add(PdfName.Fields);
        excludedKeys.add(PdfName.DR);

        PdfDictionary dict = formFrom.getPdfObject().copyTo(documentTo, excludedKeys, false);
        formTo.getPdfObject().mergeDifferent(dict);

        Map<String, PdfFormField> fieldsFrom = formFrom.getAllFormFields();
        if (fieldsFrom.size() <= 0) {
            return;
        }
        Map<String, PdfFormField> fieldsTo = formTo.getRootFormFields();

        List<PdfAnnotation> annots = toPage.getAnnotations();

        try {
            for (PdfAnnotation annot : annots) {
                if (!annot.getSubtype().equals(PdfName.Widget)) {
                    continue;
                }
                copyField(fieldsFrom, fieldsTo, annot);
            }
            // Add collected field objects to the acroform PDF object.
            PdfArray fieldsArray = formTo.getFields();
            fieldsArray.addAll(collectedFieldObjects);
            fieldsArray.setModified();
            if (!documentFrom.isTagged() || !documentTo.isTagged()) {
                // It makes sense to create the Acroform only after copying the tag structure,
                // so fields with the same names will be merged ang tag structure will be correct,
                // but when the document is not tagged we can re-create Acroform with added fields right away.
                PdfFormCreator.getAcroForm(documentTo, true);
            }
        } finally {
            collectedFieldObjects.clear();
        }
    }

    @Override
    public void recreateAcroformToProcessCopiedFields(PdfDocument documentTo) {
        PdfFormCreator.getAcroForm(documentTo, true);
    }

    private AbstractPdfFormField makeFormField(PdfObject fieldDict) {
        AbstractPdfFormField field = PdfFormField.makeFormFieldOrAnnotation(fieldDict, documentTo);
        if (field == null) {
            logger.warn(MessageFormatUtil.format(FormsLogMessageConstants.CANNOT_CREATE_FORMFIELD,
                    fieldDict.getIndirectReference()));
        }
        return field;
    }

    private void copyField(Map<String, PdfFormField> fieldsFrom,
                           Map<String, PdfFormField> fieldsTo, PdfAnnotation currentAnnot) {
        PdfDictionary parent = currentAnnot.getPdfObject().getAsDictionary(PdfName.Parent);
        if (parent != null) {
            PdfFormField parentField = getParentField(parent, documentTo);
            if (parentField == null) {
                return;
            }
            PdfString parentName = parentField.getFieldName();
            if (parentName == null) {
                return;
            }
            copyParentFormField(fieldsTo, currentAnnot, parentField);
        } else {
            PdfString annotName = currentAnnot.getPdfObject().getAsString(PdfName.T);
            String annotNameString = null;
            if (annotName != null) {
                annotNameString = annotName.toUnicodeString();
            }
            if (annotNameString != null && fieldsFrom.containsKey(annotNameString)) {
                // In this piece on code we expect annotation with T field
                // It could mean only merged form field and annotation
                // This cast must be ok
                PdfFormField field = (PdfFormField)makeFormField(currentAnnot.getPdfObject());
                if (field == null) {
                    return;
                }

                if (!collectedFieldObjects.contains(field.getPdfObject())) {
                    if (fieldsTo.get(annotNameString) != null) {
                        logger.warn(MessageFormatUtil.format(IoLogMessageConstant.DOCUMENT_ALREADY_HAS_FIELD,
                                annotNameString));
                    }

                    collectedFieldObjects.add(field.getPdfObject());
                }

                field.updateDefaultAppearance();
            }
        }
    }

    private void copyParentFormField(Map<String, PdfFormField> fieldsTo,
                                     PdfAnnotation annot, PdfFormField parentField) {
        String parentName = parentField.getFieldName().toUnicodeString();
        PdfFormField existingField = fieldsTo.get(parentName);
        PdfFormField field = createParentFieldCopy(annot.getPdfObject(), documentTo);

        if (!collectedFieldObjects.contains(field.getPdfObject())) {
            if (existingField != null) {
                logger.warn(MessageFormatUtil.format(IoLogMessageConstant.DOCUMENT_ALREADY_HAS_FIELD, parentName));
            }

            collectedFieldObjects.add(field.getPdfObject());
        }
    }

    private static PdfFormField getParentField(PdfDictionary parent, PdfDocument pdfDoc) {
        PdfDictionary parentOfParent = parent.getAsDictionary(PdfName.Parent);
        if (parentOfParent != null) {
            return getParentField(parentOfParent, pdfDoc);
        }

        return PdfFormField.makeFormField(parent, pdfDoc);
    }

    private PdfFormField createParentFieldCopy(PdfDictionary fieldDict, PdfDocument pdfDoc) {
        PdfDictionary parent = fieldDict.getAsDictionary(PdfName.Parent);
        PdfFormField field;

        if (parent != null) {
            // Here we operate with Kids array to do not run split/merge logic before PdfAcroForm.addField
            PdfArray kids = (PdfArray) parent.get(PdfName.Kids);
            if (kids == null) {
                parent.put(PdfName.Kids, new PdfArray(fieldDict));
            } else {
                if (!kids.contains(fieldDict)) {
                    kids.add(fieldDict);
                }
            }
            field = createParentFieldCopy(parent, pdfDoc);
        } else {
            field = PdfFormField.makeFormField(fieldDict, pdfDoc);
        }

        return field;
    }
}
