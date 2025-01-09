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
package com.itextpdf.forms.xfdf;

import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.kernel.pdf.PdfDocument;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

/**
 * Represents xfdf element, the top level element in an xfdf document.
 * For more details see paragraph 6.2.1 in Xfdf document specification.
 * Content model: ( f? &amp; ids? &amp; fields? &amp; annots? )
 * Attributes: xml:space, xmlns.
 */
public class XfdfObject {

    /**
     * Represents f element, child of the xfdf element.
     * Corresponds to the F key in the file dictionary.
     */
    private FObject f;

    /**
     * Represents ids element, a child of the xfdf element.
     * Corresponds to the ID key in the file dictionary.
     */
    private IdsObject ids;

    /**
     * Represents the fields element, a child of the xfdf element and is the container for form field elements.
     */
    private FieldsObject fields;

    /**
     * Represent annots element, a child of the xfdf element and is the container for annot elements.
     */
    private AnnotsObject annots;

    /**
     * A list of attributes of xfdf object.
     */
    private List<AttributeObject> attributes;

    /**
     * Gets the f element, child of the xfdf element.
     * Corresponds to the F key in the file dictionary.
     *
     * @return the f element
     */
    public FObject getF() {
        return f;
    }

    /**
     * Sets f element, child of the xfdf element.
     * Corresponds to the F key in the file dictionary.
     *
     * @param f element
     */
    public void setF(FObject f) {
        this.f = f;
    }

    /**
     * Gets the ids element, child of the xfdf element.
     * Corresponds to the ID key in the file dictionary.
     *
     * @return the ids element
     */
    public IdsObject getIds() {
        return ids;
    }

    /**
     * Sets ids element, child of the xfdf element.
     * Corresponds to the ID key in the file dictionary.
     *
     * @param ids element
     */
    public void setIds(IdsObject ids) {
        this.ids = ids;
    }

    /**
     * Gets the fields element, a child of the xfdf element and is the container for form field elements.
     *
     * @return the fields element
     */
    public FieldsObject getFields() {
        return fields;
    }

    /**
     * Sets fields element, a child of the xfdf element and is the container for form field elements.
     *
     * @param fields element
     */
    public void setFields(FieldsObject fields) {
        this.fields = fields;
    }

    /**
     * Gets the annots element, a child of the xfdf element and is the container for annot elements.
     *
     * @return the annots element
     */
    public AnnotsObject getAnnots() {
        return annots;
    }

    /**
     * Sets the annots element, a child of the xfdf element and is the container for annot elements.
     *
     * @param annots element
     */
    public void setAnnots(AnnotsObject annots) {
        this.annots = annots;
    }

    /**
     * Gets the list of attributes of xfdf object.
     *
     * @return the list of attributes
     */
    public List<AttributeObject> getAttributes() {
        return attributes;
    }

    /**
     * Sets the list of attributes of xfdf object.
     *
     * @param attributes list of attributes objects
     */
    public void setAttributes(List<AttributeObject> attributes) {
        this.attributes = attributes;
    }

    /**
     * Merges info from XfdfObject to pdf document.
     *
     * @param pdfDocument     the target document for merge.
     * @param pdfDocumentName the name of the target document. Will be checked in the merge process to determined
     *                        if it is the same as href attribute of f element of merged XfdfObject. If the names are
     *                        different, a warning will be thrown.
     */
    public void mergeToPdf(PdfDocument pdfDocument, String pdfDocumentName) {
        XfdfReader reader = new XfdfReader();
        reader.mergeXfdfIntoPdf(this, pdfDocument, pdfDocumentName);
    }

    /**
     * Writes info from XfdfObject to .xfdf file.
     *
     * @param filename name of the target file.
     * @throws IOException                  if a problem occured during opening the target file.
     * @throws TransformerException         if there is an error while creating xml structure.
     * @throws ParserConfigurationException if there is an error while writing info into xnl format.
     */
    public void writeToFile(String filename) throws IOException, TransformerException, ParserConfigurationException {
        try (OutputStream os = FileUtil.getFileOutputStream(filename)) {
            writeToFile(os);
        }
    }

    /**
     * Writes info from XfdfObject to .xfdf file.
     *
     * @param os target output stream.
     * @throws TransformerException         if there is an error while creating xml structure.
     * @throws ParserConfigurationException if there is an error while writing info into xml format.
     */
    public void writeToFile(OutputStream os) throws TransformerException, ParserConfigurationException {
        XfdfWriter writer = new XfdfWriter(os);
        writer.write(this);
    }
}
