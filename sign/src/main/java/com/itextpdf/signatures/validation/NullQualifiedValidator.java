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
package com.itextpdf.signatures.validation;

import com.itextpdf.signatures.validation.context.ValidationContext;
import com.itextpdf.signatures.validation.lotl.CountryServiceContext;
import com.itextpdf.signatures.validation.lotl.QualifiedValidator;

import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class NullQualifiedValidator extends QualifiedValidator {
    NullQualifiedValidator() {
        // Empty constructor.
    }

    @Override
    public QualificationValidationData obtainQualificationValidationResultForSignature(String signatureName) {
        return null;
    }

    @Override
    public Map<String, QualificationValidationData> obtainAllSignaturesValidationResults() {
        return new HashMap<>();
    }

    @Override
    public void startSignatureValidation(String signatureName) {
        // Do nothing.
    }

    @Override
    public void ensureValidatorIsEmpty() {
        // Do nothing.
    }

    @Override
    protected void checkSignatureQualification(List<X509Certificate> previousCertificates,
                                               CountryServiceContext currentContext, X509Certificate trustedCertificate,
                                               Date validationDate, ValidationContext context) {
        // Do nothing.
    }
}
