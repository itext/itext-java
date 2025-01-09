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
