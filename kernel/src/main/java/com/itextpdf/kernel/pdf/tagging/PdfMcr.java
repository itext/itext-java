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
package com.itextpdf.kernel.pdf.tagging;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfIndirectReference;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;

import java.util.List;

/**
 * Represents Marked Content Reference (MCR) object wrapper.
 */
public abstract class PdfMcr extends PdfObjectWrapper<PdfObject> implements IStructureNode {


    protected PdfStructElem parent;

    protected PdfMcr(PdfObject pdfObject, PdfStructElem parent) {
        super(pdfObject);
        this.parent = parent;
    }

    public abstract int getMcid();

    public PdfDictionary getPageObject() {
        PdfObject pageObject = getPageIndirectReference().getRefersTo();
        if (pageObject instanceof PdfDictionary) {
            return (PdfDictionary) pageObject;
        }
        return null;
    }

    public PdfIndirectReference getPageIndirectReference() {
        PdfObject page = null;
        if (getPdfObject() instanceof PdfDictionary) {
            page = ((PdfDictionary) getPdfObject()).get(PdfName.Pg, false);
        }
        if (page == null) {
            page = parent.getPdfObject().get(PdfName.Pg, false);
        }
        if (page instanceof PdfIndirectReference) {
            return (PdfIndirectReference) page;
        } else if (page instanceof PdfDictionary) {
            return page.getIndirectReference();
        }
        return null;
    }

    @Override
    public PdfName getRole() {
        return parent.getRole();
    }

    @Override
    public IStructureNode getParent() {
        return parent;
    }

    @Override
    public List<IStructureNode> getKids() {
        return null;
    }

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return false;
    }

}
