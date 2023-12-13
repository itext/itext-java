/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;

public class TagReference {
    protected TagTreePointer tagPointer;
    protected int insertIndex;
    protected PdfStructElem referencedTag;

    protected PdfName role;
    protected PdfDictionary properties;

    protected TagReference(PdfStructElem referencedTag, TagTreePointer tagPointer, int insertIndex) {
        this.role = referencedTag.getRole();
        this.referencedTag = referencedTag;
        this.tagPointer = tagPointer;
        this.insertIndex = insertIndex;
    }

    public PdfName getRole() {
        return role;
    }

    public int createNextMcid() {
        return tagPointer.createNextMcidForStructElem(referencedTag, insertIndex);
    }

    public TagReference addProperty(PdfName name, PdfObject value) {
        if (properties == null) {
            properties = new PdfDictionary();
        }

        properties.put(name, value);
        return this;
    }

    public TagReference removeProperty(PdfName name) {
        if (properties != null) {
            properties.remove(name);
        }
        return this;
    }

    public PdfObject getProperty(PdfName name) {
        if (properties == null) {
            return null;
        }
        return properties.get(name);
    }

    public PdfDictionary getProperties() {
        return properties;
    }
}
