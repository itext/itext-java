/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV
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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A sample implementation of the {#link IPdfPageExtraCopier} interface which
 * copies only AcroForm fields to a new page.
 *
 * <br/><br/>
 * NOTE: While it's absolutely not necessary to use the same PdfPageFormCopier instance for copying operations,
 * it is still worth to know that PdfPageFormCopier uses some caching logic which can potentially improve performance
 * in case of the reusing of the same instance.
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
                            PdfString parentName = PdfFormField.makeFormField(parent, documentTo).getFieldName();
                            if (parentName == null) {
                                continue;
                            }
                            if (!fieldsTo.containsKey(parentName.toUnicodeString())) {
                                PdfFormField field = getParentField(parent, documentTo);
                                PdfArray kids = field.getKids();
                                field.getPdfObject().remove(PdfName.Kids);
                                formTo.addField(field, toPage);
                                field.put(PdfName.Kids, kids);
                                fieldsTo.put(parentName.toUnicodeString(), field);
                            } else {
                                PdfFormField field = PdfFormField.makeFormField(annot.getPdfObject(), documentTo);
                                PdfString fieldName = field.getFieldName();
                                if (fieldName != null) {
                                    PdfFormField existingField = fieldsTo.get(fieldName.toUnicodeString());
                                    if (existingField != null) {
                                        mergeFieldsWithTheSameName(existingField, field);
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
        String fullFieldName = newField.getFieldName().toUnicodeString();
        PdfString fieldName = newField.getPdfObject().getAsString(PdfName.T);
        existingField.getPdfObject().remove(PdfName.T);
        PdfFormField mergedField = formTo.getField(fullFieldName);
        PdfArray kids = mergedField.getKids();
        if (kids != null && !kids.isEmpty()) {
            mergedField.addKid(existingField);
            return mergedField;
        }
        newField.getPdfObject().remove(PdfName.T);
        mergedField = PdfFormField.createEmptyField(documentTo);
        formTo.getFields().remove(newField.getPdfObject());
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
}
