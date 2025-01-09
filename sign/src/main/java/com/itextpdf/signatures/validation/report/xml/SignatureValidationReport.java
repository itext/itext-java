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
package com.itextpdf.signatures.validation.report.xml;

import com.itextpdf.signatures.cms.CMSContainer;

import java.util.Date;
import java.util.Objects;

class SignatureValidationReport extends AbstractIdentifiableObject implements SubValidationReport {
    private final SignatureIdentifier signatureIdentifier;
    private SignatureValidationStatus status;

    public SignatureValidationReport(ValidationObjects validationObjects, CMSContainer signature,
                                     String signatureName, Date signingDate) {
        super("S");
        signatureIdentifier = new SignatureIdentifier(validationObjects, signature, signatureName, signingDate);
    }

    public SignatureIdentifier getSignatureIdentifier() {
        return signatureIdentifier;
    }

    public void setSignatureValidationStatus(SignatureValidationStatus status) {
        this.status = status;
    }

    public SignatureValidationStatus getSignatureValidationStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SignatureValidationReport that = (SignatureValidationReport) o;
        return signatureIdentifier.equals(that.signatureIdentifier)
                && (status == null || status.equals(that.status));
    }

    @Override
    public int hashCode() {
        return Objects.hash((Object)signatureIdentifier, status);
    }

}
