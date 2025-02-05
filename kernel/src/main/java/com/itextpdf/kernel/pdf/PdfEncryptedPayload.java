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

import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.pdf.filespec.PdfFileSpec;

public class PdfEncryptedPayload extends PdfObjectWrapper<PdfDictionary> {

    public PdfEncryptedPayload(String subtype) {
        this(new PdfDictionary());
        getPdfObject().put(PdfName.Type, PdfName.EncryptedPayload);
        setSubtype(subtype);
    }

    private PdfEncryptedPayload(PdfDictionary pdfObject) {
        super(pdfObject);
    }


    public static PdfEncryptedPayload extractFrom(PdfFileSpec fileSpec) {
        if (fileSpec != null && fileSpec.getPdfObject().isDictionary()) {
            return PdfEncryptedPayload.wrap(((PdfDictionary) fileSpec.getPdfObject()).getAsDictionary(PdfName.EP));
        }
        return null;
    }

    public static PdfEncryptedPayload wrap(PdfDictionary dictionary) {
        PdfName type = dictionary.getAsName(PdfName.Type);
        if (type != null && !type.equals(PdfName.EncryptedPayload)) {
            throw new PdfException(
                    KernelExceptionMessageConstant.ENCRYPTED_PAYLOAD_SHALL_HAVE_TYPE_EQUALS_TO_ENCRYPTED_PAYLOAD_IF_PRESENT);
        }
        if (dictionary.getAsName(PdfName.Subtype) == null) {
            throw new PdfException(KernelExceptionMessageConstant.ENCRYPTED_PAYLOAD_SHALL_HAVE_SUBTYPE);
        }
        return new PdfEncryptedPayload(dictionary);
    }

    public PdfName getSubtype() {
        return getPdfObject().getAsName(PdfName.Subtype);
    }

    public PdfEncryptedPayload setSubtype(String subtype) {
        return setSubtype(new PdfName(subtype));
    }

    public PdfEncryptedPayload setSubtype(PdfName subtype) {
        setModified();
        getPdfObject().put(PdfName.Subtype, subtype);
        return this;
    }

    public PdfName getVersion() {
        return getPdfObject().getAsName(PdfName.Version);
    }

    public PdfEncryptedPayload setVersion(String version) {
        return setVersion(new PdfName(version));
    }

    public PdfEncryptedPayload setVersion(PdfName version) {
        setModified();
        getPdfObject().put(PdfName.Version, version);
        return this;
    }

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return false;
    }
}
