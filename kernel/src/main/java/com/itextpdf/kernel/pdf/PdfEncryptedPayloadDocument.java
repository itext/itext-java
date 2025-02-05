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
package com.itextpdf.kernel.pdf;

import com.itextpdf.kernel.pdf.filespec.PdfFileSpec;

public class PdfEncryptedPayloadDocument extends PdfObjectWrapper<PdfStream> {

    private PdfFileSpec fileSpec;
    private String name;

    public PdfEncryptedPayloadDocument(PdfStream pdfObject, PdfFileSpec fileSpec, String name) {
        super(pdfObject);
        this.fileSpec = fileSpec;
        this.name = name;
    }

    public byte[] getDocumentBytes() {
        return getPdfObject().getBytes();
    }

    public PdfFileSpec getFileSpec() {
        return fileSpec;
    }

    public String getName() {
        return name;
    }

    public PdfEncryptedPayload getEncryptedPayload() {
        return PdfEncryptedPayload.extractFrom(fileSpec);
    }

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return true;
    }
}
