/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
package com.itextpdf.kernel.validation.context;

import com.itextpdf.kernel.pdf.PdfXrefTable;
import com.itextpdf.kernel.validation.IValidationContext;
import com.itextpdf.kernel.validation.ValidationType;

/**
 * Class for {@link PdfXrefTable} validation context.
 */
public class XrefTableValidationContext implements IValidationContext {
    private final PdfXrefTable xrefTable;

    /**
     * Instantiates a new {@link XrefTableValidationContext} based on pdf xref table.
     *
     * @param xrefTable the pdf xref table
     */
    public XrefTableValidationContext(PdfXrefTable xrefTable) {
        this.xrefTable = xrefTable;
    }

    /**
     * Gets the pdf xref table.
     *
     * @return the pdf xref table
     */
    public PdfXrefTable getXrefTable() {
        return xrefTable;
    }

    @Override
    public ValidationType getType() {
        return ValidationType.XREF_TABLE;
    }
}
