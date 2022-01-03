/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2022 iText Group NV
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
package com.itextpdf.kernel.pdf.tagutils;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;

/**
 * The class is used to provide connection between structure element of
 * Tagged PDF document and marked content sequence in PDF stream.
 *
 * <p>
 * See {@link TagTreePointer#getTagReference(int)} and {@link PdfCanvas#openTag(TagReference)}.
 */
public class TagReference {
    protected TagTreePointer tagPointer;
    protected int insertIndex;
    protected PdfStructElem referencedTag;

    protected PdfName role;
    protected PdfDictionary properties;

    /**
     * Creates a {@link TagReference} instance which represents a reference to {@link PdfStructElem}.
     *
     * @param referencedTag a structure element to which marked content will link (if insertIndex is -1,
     *                     otherwise to MC will link to kid with insertIndex of passed structure element)
     * @param tagPointer the tag pointer to structure element
     * @param insertIndex if insertIndex is -1, the referencedTag will be used as a
     *                   source of reference, otherwise the kid will be used
     */
    protected TagReference(PdfStructElem referencedTag, TagTreePointer tagPointer, int insertIndex) {
        this.role = referencedTag.getRole();
        this.referencedTag = referencedTag;
        this.tagPointer = tagPointer;
        this.insertIndex = insertIndex;
    }

    /**
     * Gets role of structure element.
     *
     * @return the role of structure element
     */
    public PdfName getRole() {
        return role;
    }

    /**
     * Creates next marked content identifier, which will be used to mark content in PDF stream.
     *
     * @return the marked content identifier
     */
    public int createNextMcid() {
        return tagPointer.createNextMcidForStructElem(referencedTag, insertIndex);
    }

    /**
     * Adds property, which will be associated with marked-content sequence.
     *
     * @param name the name of the property
     * @param value the value of the property
     *
     * @return the {@link TagReference} instance
     */
    public TagReference addProperty(PdfName name, PdfObject value) {
        if (properties == null) {
            properties = new PdfDictionary();
        }

        properties.put(name, value);
        return this;
    }

    /**
     * Removes property. The property will not be associated with marked-content sequence.
     *
     * @param name the name of property to be deleted
     *
     * @return the {@link TagReference} instance
     */
    public TagReference removeProperty(PdfName name) {
        if (properties != null) {
            properties.remove(name);
        }
        return this;
    }

    /**
     * Gets property which related to specified name.
     *
     * @param name the name of the property
     *
     * @return the value of the property
     */
    public PdfObject getProperty(PdfName name) {
        if (properties == null) {
            return null;
        }
        return properties.get(name);
    }

    /**
     * Gets properties, which will be associated with marked-content sequence as {@link PdfDictionary}.
     *
     * @return the properties
     */
    public PdfDictionary getProperties() {
        return properties;
    }
}
