/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2020 iText Group NV
    Authors: iText Software.

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
package com.itextpdf.forms.xfdf;

import com.itextpdf.kernel.pdf.PdfDocument;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * Represents xfdf element, the top level element in an xfdf document.
 * For more details see paragraph 6.2.1 in Xfdf document specification.
 * Content model: ( f? & ids? & fields? & annots? )
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
     */
    public FObject getF() {
        return f;
    }

    /**
     * Sets the f element, child of the xfdf element.
     * Corresponds to the F key in the file dictionary.
     */
    public void setF(FObject f) {
        this.f = f;
    }

    /**
     * Gets the ids element, child of the xfdf element.
     * Corresponds to the ID key in the file dictionary.
     */
    public IdsObject getIds() {
        return ids;
    }

    /**
     * Sets the ids element, child of the xfdf element.
     * Corresponds to the ID key in the file dictionary.
     */
    public void setIds(IdsObject ids) {
        this.ids = ids;
    }

    /**
     * Gets the fields element, a child of the xfdf element and is the container for form field elements.
     */
    public FieldsObject getFields() {
        return fields;
    }

    /**
     * Sets the fields element, a child of the xfdf element and is the container for form field elements.
     */
    public void setFields(FieldsObject fields) {
        this.fields = fields;
    }

    /**
     * Gets the annots element, a child of the xfdf element and is the container for annot elements.
     */
    public AnnotsObject getAnnots() {
        return annots;
    }

    /**
     * Sets the annots element, a child of the xfdf element and is the container for annot elements.
     */
    public void setAnnots(AnnotsObject annots) {
        this.annots = annots;
    }

    /**
     * Gets the list of attributes of xfdf object.
     */
    public List<AttributeObject> getAttributes() {
        return attributes;
    }

    /**
     * Sets the list of attributes of xfdf object.
     */
    public void setAttributes(List<AttributeObject> attributes) {
        this.attributes = attributes;
    }

    /**
     * Merges info from XfdfObject to pdf document.
     * @param pdfDocument the target document for merge.
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
     * @param filename name of the target file.
     * @throws IOException if a problem occured during opening the target file.
     * @throws TransformerException if there is an error while creating xml structure.
     * @throws ParserConfigurationException if there is an error while writing info into xnl format.
     */
    public void writeToFile(String filename) throws IOException, TransformerException, ParserConfigurationException {
        try (OutputStream os = new FileOutputStream(filename)) {
            writeToFile(os);
        }
    }

    /**
     * Writes info from XfdfObject to .xfdf file.
     * @param os target output stream.
     * @throws TransformerException if there is an error while creating xml structure.
     * @throws ParserConfigurationException if there is an error while writing info into xml format.
     */
    public void writeToFile(OutputStream os) throws TransformerException, ParserConfigurationException {
        XfdfWriter writer = new XfdfWriter(os);
        writer.write(this);
    }
}
